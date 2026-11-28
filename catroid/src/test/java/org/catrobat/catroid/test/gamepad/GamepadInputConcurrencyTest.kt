/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.gamepad

import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.gamepad.GamepadInputSource.KEY
import org.catrobat.catroid.gamepad.GamepadInputSource.SIMULATED
import org.catrobat.catroid.gamepad.GamepadInputSource.TRIGGER_AXIS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.EnumMap
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class GamepadInputConcurrencyTest {

    private val input = GamepadInput()
    private val issuedSequences = ConcurrentLinkedQueue<Long>()

    @Test
    fun `concurrent producers on distinct buttons neither lose nor duplicate events`() {
        val firedCounts = EnumMap<GamepadButton, Int>(GamepadButton::class.java)
        val producers: List<() -> Unit> = GamepadButton.entries.map { button ->
            {
                repeat(PRESS_CYCLES) {
                    issue(button, KEY, pressed = true)
                    issue(button, KEY, pressed = false)
                }
                if (endsPressed(button)) {
                    issue(button, KEY, pressed = true)
                }
            }
        }

        runProducersWhileDraining(producers) { button ->
            firedCounts[button] = (firedCounts[button] ?: 0) + 1
        }

        GamepadButton.entries.forEach { button ->
            val expectedPresses = if (endsPressed(button)) PRESS_CYCLES + 1 else PRESS_CYCLES
            assertEquals("Fired events of $button", expectedPresses, firedCounts[button])
            assertEquals("Pressed state of $button", endsPressed(button), input.isPressed(button))
        }
        assertAllIssuedSequencesUniqueAndApplied()
    }

    @Test
    fun `concurrent producers on one button with different sources end in a consistent state`() {
        var firedCount = 0
        val sourcesEndingPressed = setOf(KEY, TRIGGER_AXIS)
        val issuedPresses = GamepadInputSource.entries.size * PRESS_CYCLES + sourcesEndingPressed.size
        val producers: List<() -> Unit> = GamepadInputSource.entries.map { source ->
            {
                repeat(PRESS_CYCLES) {
                    issue(A, source, pressed = true)
                    issue(A, source, pressed = false)
                }
                if (source in sourcesEndingPressed) {
                    issue(A, source, pressed = true)
                }
            }
        }

        runProducersWhileDraining(producers) { firedCount++ }

        assertTrue("Fired events: $firedCount", firedCount in 1..issuedPresses)
        assertTrue(input.isPressed(A))
        assertAllIssuedSequencesUniqueAndApplied()

        issue(A, KEY, pressed = false)
        input.applyPendingInput { firedCount++ }
        assertTrue("Button released although TRIGGER_AXIS still holds it", input.isPressed(A))

        issue(A, TRIGGER_AXIS, pressed = false)
        input.applyPendingInput { firedCount++ }
        assertFalse(input.isPressed(A))
    }

    @Test
    fun `applied sequence guarantees that its state is visible to the producer thread`() {
        val buttons = GamepadButton.entries.take(PRODUCER_THREADS)
        val producers: List<() -> Unit> = buttons.map { button ->
            {
                repeat(PRESS_CYCLES_AWAITING_DRAIN) {
                    awaitApplied(issue(button, SIMULATED, pressed = true))
                    check(input.isPressed(button)) { "$button not pressed after its press was applied" }
                    awaitApplied(issue(button, SIMULATED, pressed = false))
                    check(!input.isPressed(button)) { "$button still pressed after its release was applied" }
                }
            }
        }

        runProducersWhileDraining(producers) { }

        assertAllIssuedSequencesUniqueAndApplied()
    }

    private fun issue(button: GamepadButton, source: GamepadInputSource, pressed: Boolean): Long =
        input.setButtonState(button, source, pressed).also { issuedSequences.add(it) }

    private fun endsPressed(button: GamepadButton): Boolean = button.ordinal % 2 == 0

    private fun awaitApplied(sequence: Long) {
        val deadline = deadlineFromNow()
        while (!input.isApplied(sequence)) {
            check(System.nanoTime() < deadline) { "Sequence $sequence was not applied in time" }
            Thread.yield()
        }
    }

    private fun runProducersWhileDraining(
        producers: List<() -> Unit>,
        onPressed: (GamepadButton) -> Unit
    ) {
        val executor = Executors.newFixedThreadPool(producers.size)
        val startSignal = CountDownLatch(1)
        try {
            val results = producers.map { producer ->
                executor.submit(Callable {
                    startSignal.await()
                    producer()
                })
            }
            startSignal.countDown()

            val deadline = deadlineFromNow()
            while (results.any { !it.isDone }) {
                assertTrue("Producers did not finish in time", System.nanoTime() < deadline)
                input.applyPendingInput { button -> onPressed(button) }
            }
            results.forEach { it.get() }
            input.applyPendingInput { button -> onPressed(button) }
        } finally {
            executor.shutdownNow()
        }
    }

    private fun assertAllIssuedSequencesUniqueAndApplied() {
        val sequences = issuedSequences.toList()
        assertEquals("Duplicate sequences issued", sequences.size, sequences.toSet().size)
        sequences.forEach { assertTrue("Sequence $it not applied", input.isApplied(it)) }
    }

    private fun deadlineFromNow(): Long = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS)

    private companion object {
        const val PRESS_CYCLES = 500
        const val PRESS_CYCLES_AWAITING_DRAIN = 100
        const val PRODUCER_THREADS = 4
        const val TIMEOUT_SECONDS = 30L
    }
}
