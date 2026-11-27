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

package org.catrobat.catroid.test.stage.gamepad

import android.os.Build
import android.view.InputDevice.SOURCE_DPAD
import android.view.InputDevice.SOURCE_GAMEPAD
import android.view.InputDevice.SOURCE_JOYSTICK
import android.view.InputDevice.SOURCE_KEYBOARD
import android.view.KeyEvent
import android.view.MotionEvent.AXIS_HAT_X
import android.view.MotionEvent.AXIS_LTRIGGER
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.catrobat.catroid.gamepad.GamepadButton.DOWN
import org.catrobat.catroid.gamepad.GamepadButton.L1
import org.catrobat.catroid.gamepad.GamepadButton.LEFT
import org.catrobat.catroid.gamepad.GamepadButton.RIGHT
import org.catrobat.catroid.gamepad.GamepadButton.UP
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadButton.Y
import org.catrobat.catroid.stage.gamepad.AndroidGamepadInputAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Tests how [AndroidGamepadInputAdapter] forwards controller keys, including the swap of A/B and X/Y. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
class AndroidGamepadInputAdapterKeyTest : AndroidGamepadInputAdapterTestBase() {

    @Test
    fun `key mapping covers every gamepad button`() {
        assertEquals(GamepadButton.entries.toSet(), KEY_CODE_BUTTONS.values.toSet())
    }

    @Test
    fun `each mapped controller key is consumed and presses and releases its button`() {
        KEY_CODE_BUTTONS.forEach { (keyCode, button) ->
            assertTrue("Key down of $button consumed", keyDown(keyCode))
            applyPendingInput()
            assertPressedExactly(button)

            assertTrue("Key up of $button consumed", keyUp(keyCode))
            applyPendingInput()
            assertPressedExactly()
        }

        assertFired(*KEY_CODE_BUTTONS.values.toTypedArray())
    }

    @Test
    fun `mapped keys from a keyboard are neither consumed nor forwarded`() {
        KEY_CODE_BUTTONS.keys.forEach { keyCode ->
            assertFalse(
                "Keyboard key ${KeyEvent.keyCodeToString(keyCode)} consumed",
                keyDown(keyCode, source = SOURCE_KEYBOARD, deviceId = KEYBOARD_DEVICE_ID)
            )
        }

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `key releases from a keyboard are not forwarded`() {
        GamepadButton.entries.forEach { holdWithKeySource(it) }
        applyPendingInput()

        KEY_CODE_BUTTONS.keys.forEach { keyCode ->
            assertFalse(keyUp(keyCode, source = SOURCE_KEYBOARD, deviceId = KEYBOARD_DEVICE_ID))
        }
        applyPendingInput()

        assertPressedExactly(*GamepadButton.entries.toTypedArray())
    }

    @Test
    fun `keyboard arrow keys of an unknown device are ignored`() {
        DPAD_KEY_CODES.forEach { keyCode ->
            assertFalse(keyDown(keyCode, source = SOURCE_KEYBOARD, deviceId = UNKNOWN_DEVICE_ID))
        }

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `D-pad keys with D-pad source from a gamepad device are accepted`() {
        DPAD_KEY_CODES.forEach { keyCode ->
            assertTrue(keyDown(keyCode, source = SOURCE_DPAD, deviceId = GAMEPAD_DEVICE_ID))
        }

        applyPendingInput()

        assertFired(UP, DOWN, LEFT, RIGHT)
        assertPressedExactly(UP, DOWN, LEFT, RIGHT)
    }

    @Test
    fun `keys from a device with joystick source are accepted`() {
        assertTrue(keyDown(KeyEvent.KEYCODE_DPAD_UP, source = SOURCE_DPAD, deviceId = JOYSTICK_DEVICE_ID))

        applyPendingInput()

        assertPressedExactly(UP)
    }

    @Test
    fun `controller source of the key event is enough without a known device`() {
        assertTrue(keyDown(KeyEvent.KEYCODE_BUTTON_A, source = SOURCE_GAMEPAD, deviceId = UNKNOWN_DEVICE_ID))
        assertTrue(keyDown(KeyEvent.KEYCODE_BUTTON_X, source = SOURCE_JOYSTICK, deviceId = UNKNOWN_DEVICE_ID))

        applyPendingInput()

        assertFired(A, X)
        assertPressedExactly(A, X)
    }

    @Test
    fun `system and unmapped controller keys are never consumed`() {
        UNMAPPED_KEY_CODES.forEach { keyCode ->
            val keyName = KeyEvent.keyCodeToString(keyCode)
            assertFalse("Key down of $keyName consumed", keyDown(keyCode))
            assertFalse("Key up of $keyName consumed", keyUp(keyCode))
        }

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `unhandled button is neither consumed nor forwarded while handled buttons are`() {
        unhandledButtons += B

        assertFalse(keyDown(KeyEvent.KEYCODE_BUTTON_B))
        assertTrue(keyDown(KeyEvent.KEYCODE_BUTTON_A))
        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `release of an unhandled button is not forwarded`() {
        unhandledButtons += B
        holdWithKeySource(B)
        applyPendingInput()

        assertFalse(keyUp(KeyEvent.KEYCODE_BUTTON_B))
        applyPendingInput()

        assertPressedExactly(B)
    }

    @Test
    fun `repeated key down is consumed but not forwarded`() {
        keyDown(KeyEvent.KEYCODE_BUTTON_A)
        applyPendingInput()
        keyUp(KeyEvent.KEYCODE_BUTTON_A)
        applyPendingInput()

        assertTrue(keyDown(KeyEvent.KEYCODE_BUTTON_A, repeatCount = 1))
        applyPendingInput()

        assertFired(A)
        assertPressedExactly()
    }

    @Test
    fun `holding a key with repeated key downs fires once`() {
        keyDown(KeyEvent.KEYCODE_BUTTON_A)
        keyDown(KeyEvent.KEYCODE_BUTTON_A, repeatCount = 1)
        keyDown(KeyEvent.KEYCODE_BUTTON_A, repeatCount = 2)

        applyPendingInput()

        assertFired(A)
        assertPressedExactly(A)
    }

    @Test
    fun `repeated key down of an unhandled button is not consumed`() {
        unhandledButtons += B

        assertFalse(keyDown(KeyEvent.KEYCODE_BUTTON_B, repeatCount = 1))
    }

    @Test
    fun `controller input is not consumed without gamepad input`() {
        availableInput = null

        assertFalse(keyDown(KeyEvent.KEYCODE_BUTTON_A))
        assertFalse(keyUp(KeyEvent.KEYCODE_BUTTON_A))
        assertFalse(moveAxes(AXIS_HAT_X to -1f, AXIS_LTRIGGER to 1f))
        adapter.releaseAll()
        adapter.onInputDeviceRemoved(GAMEPAD_DEVICE_ID)
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `gamepad input is looked up for every event`() {
        availableInput = null
        keyDown(KeyEvent.KEYCODE_BUTTON_A)

        availableInput = input
        assertTrue(keyDown(KeyEvent.KEYCODE_BUTTON_B))
        applyPendingInput()

        assertFired(B)
        assertPressedExactly(B)
    }

    @Test
    fun `swapped face buttons turn A into B and X into Y and the other way round`() {
        listOf(
            KeyEvent.KEYCODE_BUTTON_A to B,
            KeyEvent.KEYCODE_BUTTON_B to A,
            KeyEvent.KEYCODE_BUTTON_X to Y,
            KeyEvent.KEYCODE_BUTTON_Y to X
        ).forEach { (keyCode, button) ->
            assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, keyCode)))
            applyPendingInput()
            assertPressedExactly(button)

            assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_UP, keyCode)))
            applyPendingInput()
            assertPressedExactly()
        }

        assertFired(B, A, Y, X)
    }

    @Test
    fun `swapped face buttons leave the other buttons unchanged`() {
        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_L1)))
        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)))
        applyPendingInput()

        assertPressedExactly(L1, UP)
    }

    @Test
    fun `reported A of an unhandled swapped B presses Back once on release`() {
        unhandledButtons += B

        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)))
        assertEquals(0, backPresses)
        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)))
        applyPendingInput()

        assertPressedExactly()
        assertEquals(1, backPresses)
    }

    @Test
    fun `swapped face buttons are left to Android without a running stage`() {
        availableInput = null

        assertFalse(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        assertFalse(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)))
        assertEquals(0, backPresses)
    }
}
