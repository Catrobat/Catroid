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
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.gamepad.GamepadInputSource.KEY
import org.catrobat.catroid.gamepad.GamepadInputSource.SIMULATED
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** The rules of [GamepadInput.applyPendingFrameInput], which applies the input of a stage frame. */
@RunWith(JUnit4::class)
class GamepadInputFrameTest {

    private val input = GamepadInput()
    private val firedButtons = mutableListOf<GamepadButton>()

    @Test
    fun `press and release of a button in one frame take two frames`() {
        press(A)
        val release = release(A)

        applyFrame()

        assertPressedExactly(A)
        assertFalse(input.isApplied(release))

        applyFrame()

        assertPressedExactly()
        assertTrue(input.isApplied(release))
        assertFired(A)
    }

    @Test
    fun `second press of a button in one frame fires it again two frames later`() {
        press(A)
        release(A)
        val secondPress = press(A)

        applyFrame()
        assertFired(A)

        applyFrame()
        assertPressedExactly()
        assertFired(A)

        applyFrame()
        assertPressedExactly(A)
        assertTrue(input.isApplied(secondPress))
        assertFired(A, A)
    }

    @Test
    fun `changes after a waiting change wait too, so the order stays the same`() {
        press(A)
        release(A)
        val pressOfX = press(X)

        applyFrame()

        assertPressedExactly(A)
        assertFalse(input.isApplied(pressOfX))

        applyFrame()

        assertPressedExactly(X)
        assertFired(A, X)
    }

    @Test
    fun `presses of different buttons in one frame are applied together`() {
        press(A)
        press(X)

        applyFrame()

        assertPressedExactly(A, X)
        assertFired(A, X)
    }

    @Test
    fun `presses that start the scripts for any button are applied in frames of their own`() {
        press(A)
        val pressOfX = press(X)

        applyFrame(startsAnyButtonScripts = { true })

        assertPressedExactly(A)
        assertFalse(input.isApplied(pressOfX))

        applyFrame(startsAnyButtonScripts = { true })

        assertPressedExactly(A, X)
        assertFired(A, X)
    }

    @Test
    fun `press that starts no scripts for any button does not wait for one that does`() {
        press(A)
        press(B)

        applyFrame(startsAnyButtonScripts = { it != B })

        assertPressedExactly(A, B)
        assertFired(A, B)
    }

    @Test
    fun `press of a held button from a further source does not count as a change`() {
        press(A, KEY)
        val furtherPress = press(A, SIMULATED)
        val release = release(A, KEY)

        applyFrame()

        assertTrue(input.isApplied(furtherPress))
        assertTrue(input.isApplied(release))
        assertPressedExactly(A)
        assertFired(A)
    }

    @Test
    fun `release of the last source counts as a change of the button`() {
        press(A, KEY)
        press(A, SIMULATED)
        applyFrame()

        release(A, KEY)
        release(A, SIMULATED)
        val pressAgain = press(A, KEY)
        applyFrame()

        assertPressedExactly()
        assertFalse(input.isApplied(pressAgain))

        applyFrame()

        assertPressedExactly(A)
        assertFired(A, A)
    }

    @Test
    fun `releasing all buttons waits if it would release a button pressed in the same frame`() {
        press(A)
        val releaseAll = input.releaseAll()

        applyFrame()

        assertPressedExactly(A)
        assertFalse(input.isApplied(releaseAll))

        applyFrame()

        assertPressedExactly()
        assertTrue(input.isApplied(releaseAll))
    }

    @Test
    fun `releasing all buttons pressed in earlier frames is applied at once`() {
        press(A)
        applyFrame()

        val releaseAll = input.releaseAll()
        applyFrame()

        assertPressedExactly()
        assertTrue(input.isApplied(releaseAll))
    }

    @Test
    fun `applying all pending input at once ignores the frame rules`() {
        press(A)
        release(A)
        val secondPress = press(A)

        input.applyPendingInput { firedButtons.add(it) }

        assertTrue(input.isApplied(secondPress))
        assertPressedExactly(A)
        assertFired(A, A)
    }

    private fun press(button: GamepadButton, source: GamepadInputSource = KEY): Long =
        input.setButtonState(button, source, true)

    private fun release(button: GamepadButton, source: GamepadInputSource = KEY): Long =
        input.setButtonState(button, source, false)

    private fun applyFrame(startsAnyButtonScripts: (GamepadButton) -> Boolean = { false }) {
        input.applyPendingFrameInput(startsAnyButtonScripts) { firedButtons.add(it) }
    }

    private fun assertFired(vararg expectedButtons: GamepadButton) {
        assertEquals(expectedButtons.toList(), firedButtons)
    }

    private fun assertPressedExactly(vararg expectedButtons: GamepadButton) {
        assertEquals(expectedButtons.toSet(), GamepadButton.entries.filter(input::isPressed).toSet())
    }
}
