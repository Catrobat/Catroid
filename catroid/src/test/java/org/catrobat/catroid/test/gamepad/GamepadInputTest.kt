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
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.catrobat.catroid.gamepad.GamepadButton.DOWN
import org.catrobat.catroid.gamepad.GamepadButton.L2
import org.catrobat.catroid.gamepad.GamepadButton.LEFT
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.gamepad.GamepadInputSource.HAT_AXIS
import org.catrobat.catroid.gamepad.GamepadInputSource.KEY
import org.catrobat.catroid.gamepad.GamepadInputSource.SIMULATED
import org.catrobat.catroid.gamepad.GamepadInputSource.TRIGGER_AXIS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class GamepadInputTest {

    private val input = GamepadInput()
    private val firedButtons = mutableListOf<GamepadButton>()

    @Test
    fun `all buttons are released initially`() {
        assertPressedExactly()
    }

    @Test
    fun `applying input without pending input fires nothing`() {
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `press is neither visible nor fired before pending input is applied`() {
        press(A)

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `press fires exactly one event when pending input is applied`() {
        press(A)

        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `button is already pressed when its event fires`() {
        val pressedStateDuringEvent = mutableListOf<Boolean>()
        press(A)

        input.applyPendingInput { button -> pressedStateDuringEvent.add(input.isPressed(button)) }

        assertEquals(listOf(true), pressedStateDuringEvent)
    }

    @Test
    fun `holding a button fires no further events`() {
        press(A)
        applyPendingInput()

        applyPendingInput()
        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `repeated press from the same source fires no further events`() {
        press(A)
        applyPendingInput()

        press(A)
        applyPendingInput()
        press(A)
        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `repeated press from the same source within one batch fires once`() {
        press(A)
        press(A)
        press(A)

        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `release fires no event`() {
        press(A)
        applyPendingInput()
        firedButtons.clear()

        release(A)
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `release is not visible before pending input is applied`() {
        press(A)
        applyPendingInput()

        release(A)

        assertPressedExactly(A)
    }

    @Test
    fun `release of a released button fires nothing and keeps it released`() {
        release(A)

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `press after release fires again`() {
        press(A)
        applyPendingInput()
        release(A)
        applyPendingInput()

        press(A)
        applyPendingInput()

        assertFired(A, A)
        assertPressedExactly(A)
    }

    @Test
    fun `buttons are pressed and released independently`() {
        press(LEFT)
        press(DOWN)
        applyPendingInput()
        assertFired(LEFT, DOWN)
        assertPressedExactly(LEFT, DOWN)

        release(LEFT)
        applyPendingInput()
        assertPressedExactly(DOWN)

        press(LEFT)
        applyPendingInput()
        assertFired(LEFT, DOWN, LEFT)
        assertPressedExactly(LEFT, DOWN)
    }

    @Test
    fun `events fire in the order the presses were enqueued`() {
        press(B)
        press(A)
        press(X)

        applyPendingInput()

        assertFired(B, A, X)
    }

    @Test
    fun `every button can be pressed and released`() {
        GamepadButton.entries.forEach { press(it) }
        applyPendingInput()
        assertFired(*GamepadButton.entries.toTypedArray())
        assertPressedExactly(*GamepadButton.entries.toTypedArray())

        GamepadButton.entries.forEach { release(it) }
        applyPendingInput()
        assertPressedExactly()
    }

    @Test
    fun `every input source can press and release a button on its own`() {
        GamepadInputSource.entries.forEach { source ->
            firedButtons.clear()

            press(A, source)
            applyPendingInput()
            assertFired(A)
            assertPressedExactly(A)

            release(A, source)
            applyPendingInput()
            assertPressedExactly()
        }
    }

    @Test
    fun `press from two sources in one batch counts as one press`() {
        press(A, KEY)
        press(A, HAT_AXIS)

        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `press from a second source while held fires nothing`() {
        press(A, KEY)
        applyPendingInput()

        press(A, HAT_AXIS)
        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `button stays pressed until all of its sources are released`() {
        press(L2, KEY)
        press(L2, TRIGGER_AXIS)
        press(L2, SIMULATED)
        applyPendingInput()

        release(L2, KEY)
        applyPendingInput()
        assertPressedExactly(L2)

        release(L2, SIMULATED)
        applyPendingInput()
        assertPressedExactly(L2)

        release(L2, TRIGGER_AXIS)
        applyPendingInput()
        assertPressedExactly()
        assertFired(L2)
    }

    @Test
    fun `releasing a source that does not hold the button keeps it pressed`() {
        press(A, KEY)
        applyPendingInput()

        release(A, HAT_AXIS)
        applyPendingInput()

        assertPressedExactly(A)
    }

    @Test
    fun `re-pressing one source while another source holds the button fires nothing`() {
        press(A, KEY)
        press(A, HAT_AXIS)
        applyPendingInput()

        release(A, KEY)
        applyPendingInput()
        press(A, KEY)
        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `press after all sources were released fires again`() {
        press(A, KEY)
        press(A, HAT_AXIS)
        applyPendingInput()
        release(A, KEY)
        release(A, HAT_AXIS)
        applyPendingInput()

        press(A, HAT_AXIS)
        applyPendingInput()

        assertFired(A, A)
        assertPressedExactly(A)
    }

    @Test
    fun `press and release within one batch fires once and ends released`() {
        press(A)
        release(A)

        applyPendingInput()

        assertFired(A)
        assertPressedExactly()
    }

    @Test
    fun `button is pressed during its event even when released later in the same batch`() {
        val pressedStateDuringEvent = mutableListOf<Boolean>()
        press(A)
        release(A)

        input.applyPendingInput { button -> pressedStateDuringEvent.add(input.isPressed(button)) }

        assertEquals(listOf(true), pressedStateDuringEvent)
        assertPressedExactly()
    }

    @Test
    fun `release and press within one batch of a held button fires again and ends pressed`() {
        press(A)
        applyPendingInput()

        release(A)
        press(A)
        applyPendingInput()

        assertFired(A, A)
        assertPressedExactly(A)
    }

    @Test
    fun `release and press within one batch of a released button fires once and ends pressed`() {
        release(A)
        press(A)

        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `press release press within one batch fires for each edge in order`() {
        press(A)
        release(A)
        press(A)

        applyPendingInput()

        assertFired(A, A)
        assertPressedExactly(A)
    }

    @Test
    fun `input enqueued while events fire is applied with the next frame`() {
        press(A)

        input.applyPendingInput { button ->
            firedButtons.add(button)
            if (button == A) {
                press(B)
            }
        }

        assertFired(A)
        assertPressedExactly(A)

        applyPendingInput()

        assertFired(A, B)
        assertPressedExactly(A, B)
    }

    @Test
    fun `releaseAll releases all held buttons without firing`() {
        press(A, KEY)
        press(DOWN, HAT_AXIS)
        press(L2, TRIGGER_AXIS)
        press(X, SIMULATED)
        applyPendingInput()
        firedButtons.clear()

        input.releaseAll()
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `releaseAll is not visible before pending input is applied`() {
        press(A)
        applyPendingInput()

        input.releaseAll()

        assertPressedExactly(A)
    }

    @Test
    fun `releaseAll releases every source of a button`() {
        press(A, KEY)
        press(A, HAT_AXIS)
        applyPendingInput()

        input.releaseAll()
        applyPendingInput()
        press(A, KEY)
        applyPendingInput()

        assertFired(A, A)
        assertPressedExactly(A)
    }

    @Test
    fun `button needs a fresh press after releaseAll`() {
        press(A)
        applyPendingInput()
        input.releaseAll()
        applyPendingInput()

        applyPendingInput()
        assertFired(A)
        assertPressedExactly()

        press(A)
        applyPendingInput()
        assertFired(A, A)
        assertPressedExactly(A)
    }

    @Test
    fun `releaseAll is applied in order with other input of the same batch`() {
        press(A)
        input.releaseAll()
        press(B)

        applyPendingInput()

        assertFired(A, B)
        assertPressedExactly(B)
    }

    @Test
    fun `reset discards pending input`() {
        press(A)

        input.reset()
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `reset releases held buttons immediately without firing`() {
        press(A)
        press(B, HAT_AXIS)
        applyPendingInput()

        input.reset()

        assertPressedExactly()
        applyPendingInput()
        assertFired(A, B)
    }

    @Test
    fun `button needs a fresh press after reset`() {
        press(A)
        applyPendingInput()
        input.reset()

        press(A)
        applyPendingInput()

        assertFired(A, A)
        assertPressedExactly(A)
    }

    @Test
    fun `reset marks every issued sequence as applied`() {
        val pressSequence = press(A)
        val releaseAllSequence = input.releaseAll()

        input.reset()

        assertTrue(input.isApplied(pressSequence))
        assertTrue(input.isApplied(releaseAllSequence))
    }

    @Test
    fun `input enqueued after reset is applied normally`() {
        press(A)
        input.reset()

        val sequence = press(B)
        assertFalse(input.isApplied(sequence))
        applyPendingInput()

        assertTrue(input.isApplied(sequence))
        assertFired(B)
        assertPressedExactly(B)
    }

    @Test
    fun `issued sequences are strictly increasing`() {
        val sequences = listOf(press(A), release(A), input.releaseAll(), press(B, SIMULATED))

        assertEquals(sequences.sorted().distinct(), sequences)
    }

    @Test
    fun `sequence is not applied before and applied after pending input is applied`() {
        val sequence = press(A)
        assertFalse(input.isApplied(sequence))

        applyPendingInput()

        assertTrue(input.isApplied(sequence))
    }

    @Test
    fun `all sequences of one batch are applied together`() {
        val sequences = listOf(press(A), release(A), input.releaseAll(), press(B))

        applyPendingInput()

        sequences.forEach { assertTrue("Sequence $it not applied", input.isApplied(it)) }
    }

    @Test
    fun `sequence issued after applying input is not applied yet`() {
        val appliedSequence = press(A)
        applyPendingInput()

        val pendingSequence = release(A)

        assertTrue(input.isApplied(appliedSequence))
        assertFalse(input.isApplied(pendingSequence))
    }

    @Test
    fun `sequence of input without visible effect is applied too`() {
        val sequence = release(A)

        applyPendingInput()

        assertTrue(input.isApplied(sequence))
    }

    @Test
    fun `sequence enqueued while events fire is applied with the next frame`() {
        var sequenceFromEvent = 0L
        press(A)

        input.applyPendingInput { sequenceFromEvent = press(B) }

        assertFalse(input.isApplied(sequenceFromEvent))
        applyPendingInput()
        assertTrue(input.isApplied(sequenceFromEvent))
    }

    @Test
    fun `pressed snapshot is visible to other threads after pending input is applied`() {
        press(A)
        press(L2, TRIGGER_AXIS)
        applyPendingInput()

        val pressedOnOtherThread = readPressedButtonsOnOtherThread()

        assertEquals(setOf(A, L2), pressedOnOtherThread)
    }

    @Test
    fun `pending press is not visible to other threads`() {
        press(A)

        val pressedOnOtherThread = readPressedButtonsOnOtherThread()

        assertEquals(emptySet<GamepadButton>(), pressedOnOtherThread)
    }

    private fun press(button: GamepadButton, source: GamepadInputSource = KEY): Long =
        input.setButtonState(button, source, true)

    private fun release(button: GamepadButton, source: GamepadInputSource = KEY): Long =
        input.setButtonState(button, source, false)

    private fun applyPendingInput() {
        input.applyPendingInput { button -> firedButtons.add(button) }
    }

    private fun assertFired(vararg expectedButtons: GamepadButton) {
        assertEquals(expectedButtons.toList(), firedButtons)
    }

    private fun assertPressedExactly(vararg expectedButtons: GamepadButton) {
        GamepadButton.entries.forEach { button ->
            assertEquals("Pressed state of $button", button in expectedButtons, input.isPressed(button))
        }
    }

    private fun readPressedButtonsOnOtherThread(): Set<GamepadButton> {
        val executor = Executors.newSingleThreadExecutor()
        try {
            return executor.submit<Set<GamepadButton>> {
                GamepadButton.entries.filter { input.isPressed(it) }.toSet()
            }.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } finally {
            executor.shutdownNow()
        }
    }

    private companion object {
        const val TIMEOUT_SECONDS = 10L
    }
}
