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
import android.view.InputDevice.SOURCE_MOUSE
import android.view.InputDevice.SOURCE_TOUCHSCREEN
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.MotionEvent.AXIS_BRAKE
import android.view.MotionEvent.AXIS_GAS
import android.view.MotionEvent.AXIS_HAT_X
import android.view.MotionEvent.AXIS_HAT_Y
import android.view.MotionEvent.AXIS_LTRIGGER
import android.view.MotionEvent.AXIS_RTRIGGER
import android.view.MotionEvent.AXIS_X
import android.view.MotionEvent.AXIS_Y
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.DOWN
import org.catrobat.catroid.gamepad.GamepadButton.L2
import org.catrobat.catroid.gamepad.GamepadButton.LEFT
import org.catrobat.catroid.gamepad.GamepadButton.R2
import org.catrobat.catroid.gamepad.GamepadButton.RIGHT
import org.catrobat.catroid.gamepad.GamepadButton.UP
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.stage.gamepad.AndroidGamepadInputAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Tests how [AndroidGamepadInputAdapter] forwards the hat and the analog triggers of joystick moves. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
class AndroidGamepadInputAdapterMotionTest : AndroidGamepadInputAdapterTestBase() {

    @Test
    fun `D-pad keys with D-pad source from a device that is no game controller are ignored`() {
        DPAD_KEY_CODES.forEach { keyCode ->
            assertFalse(keyDown(keyCode, source = SOURCE_DPAD, deviceId = REMOTE_CONTROL_DEVICE_ID))
        }

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `hat x presses left and right and centering releases them`() {
        assertTrue(moveAxes(AXIS_HAT_X to -1f))
        applyPendingInput()
        assertPressedExactly(LEFT)

        assertTrue(moveAxes(AXIS_HAT_X to 0f))
        applyPendingInput()
        assertPressedExactly()

        assertTrue(moveAxes(AXIS_HAT_X to 1f))
        applyPendingInput()
        assertPressedExactly(RIGHT)

        assertTrue(moveAxes(AXIS_HAT_X to 0f))
        applyPendingInput()
        assertPressedExactly()

        assertFired(LEFT, RIGHT)
    }

    @Test
    fun `hat y presses up and down and centering releases them`() {
        assertTrue(moveAxes(AXIS_HAT_Y to -1f))
        applyPendingInput()
        assertPressedExactly(UP)

        assertTrue(moveAxes(AXIS_HAT_Y to 0f))
        applyPendingInput()
        assertPressedExactly()

        assertTrue(moveAxes(AXIS_HAT_Y to 1f))
        applyPendingInput()
        assertPressedExactly(DOWN)

        assertTrue(moveAxes(AXIS_HAT_Y to 0f))
        applyPendingInput()
        assertPressedExactly()

        assertFired(UP, DOWN)
    }

    @Test
    fun `hat direction is pressed from half deflection`() {
        assertHatPressesExactly(AXIS_HAT_X, -0.49f)
        assertHatPressesExactly(AXIS_HAT_X, -0.5f, LEFT)
        assertHatPressesExactly(AXIS_HAT_X, 0.49f)
        assertHatPressesExactly(AXIS_HAT_X, 0.5f, RIGHT)
        assertHatPressesExactly(AXIS_HAT_Y, -0.49f)
        assertHatPressesExactly(AXIS_HAT_Y, -0.5f, UP)
        assertHatPressesExactly(AXIS_HAT_Y, 0.49f)
        assertHatPressesExactly(AXIS_HAT_Y, 0.5f, DOWN)
    }

    @Test
    fun `diagonal hat presses both directions`() {
        moveAxes(AXIS_HAT_X to -1f, AXIS_HAT_Y to 1f)
        applyPendingInput()

        assertPressedExactly(LEFT, DOWN)
        assertEquals(setOf(LEFT, DOWN), firedButtons.toSet())
        assertEquals(2, firedButtons.size)

        moveAxes(AXIS_HAT_X to 0f, AXIS_HAT_Y to 1f)
        applyPendingInput()

        assertPressedExactly(DOWN)
        assertEquals(2, firedButtons.size)
    }

    @Test
    fun `hat and D-pad key of the same button count as one press`() {
        keyDown(KeyEvent.KEYCODE_DPAD_UP, source = SOURCE_DPAD)
        moveAxes(AXIS_HAT_Y to -1f)
        applyPendingInput()
        assertPressedExactly(UP)

        moveAxes(AXIS_HAT_Y to 0f)
        applyPendingInput()
        assertPressedExactly(UP)

        keyUp(KeyEvent.KEYCODE_DPAD_UP, source = SOURCE_DPAD)
        applyPendingInput()
        assertPressedExactly()

        assertFired(UP)
    }

    @Test
    fun `hat keeps the button pressed after the D-pad key is released`() {
        moveAxes(AXIS_HAT_X to 1f)
        keyDown(KeyEvent.KEYCODE_DPAD_RIGHT, source = SOURCE_DPAD)
        applyPendingInput()

        keyUp(KeyEvent.KEYCODE_DPAD_RIGHT, source = SOURCE_DPAD)
        applyPendingInput()
        assertPressedExactly(RIGHT)

        moveAxes(AXIS_HAT_X to 0f)
        applyPendingInput()
        assertPressedExactly()

        assertFired(RIGHT)
    }

    @Test
    fun `trigger without motion range is pressed from half of its raw value`() {
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.49f))
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.5f), L2)
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.49f))
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.5f), R2)
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 1f, AXIS_RTRIGGER to 1f), L2, R2)
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0f, AXIS_RTRIGGER to 0f))
    }

    @Test
    fun `brake and gas axes press L2 and R2`() {
        assertTriggerPressesExactly(arrayOf(AXIS_BRAKE to 0.49f, AXIS_GAS to 0.49f))
        assertTriggerPressesExactly(arrayOf(AXIS_BRAKE to 0.5f), L2)
        assertTriggerPressesExactly(arrayOf(AXIS_GAS to 0.5f), R2)
    }

    @Test
    fun `the further pulled of trigger and brake or gas axis decides`() {
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.2f, AXIS_BRAKE to 0.6f), L2)
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.6f, AXIS_BRAKE to 0.2f), L2)
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.3f, AXIS_BRAKE to 0.3f))
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.2f, AXIS_GAS to 0.6f), R2)
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.6f, AXIS_GAS to 0.2f), R2)
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.3f, AXIS_GAS to 0.3f))

        assertFired(L2, R2)
    }

    @Test
    fun `pressed trigger stays pressed until its travel falls below 40 percent`() {
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.5f), L2)
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.45f), L2)
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.4f), L2)
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.39f))

        assertFired(L2)
    }

    @Test
    fun `released trigger is pressed again only from half of its travel`() {
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 1f), R2)
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.3f))
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.45f))
        assertTriggerPressesExactly(arrayOf(AXIS_RTRIGGER to 0.5f), R2)

        assertFired(R2, R2)
    }

    @Test
    fun `trigger jitter around half of its travel counts as one press`() {
        listOf(0.5f, 0.49f, 0.51f, 0.48f, 0.5f, 0.47f).forEach { travel ->
            assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to travel), L2)
        }

        assertFired(L2)
    }

    @Test
    fun `releasing all buttons also forgets a held trigger`() {
        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.6f), L2)

        adapter.releaseAll()
        applyPendingInput()
        assertPressedExactly()

        assertTriggerPressesExactly(arrayOf(AXIS_LTRIGGER to 0.45f))
        assertFired(L2)
    }

    @Test
    fun `trigger travel is normalized by the motion range of the device`() {
        assertRangedTriggerPressesExactly(AXIS_LTRIGGER to -1f)
        assertRangedTriggerPressesExactly(AXIS_LTRIGGER to -0.02f)
        assertRangedTriggerPressesExactly(AXIS_LTRIGGER to 0f, pressed = L2)
        assertRangedTriggerPressesExactly(AXIS_RTRIGGER to -0.02f)
        assertRangedTriggerPressesExactly(AXIS_RTRIGGER to 0f, pressed = R2)
    }

    @Test
    fun `brake and gas travel is normalized by the motion range of the device`() {
        assertRangedTriggerPressesExactly(AXIS_BRAKE to -0.02f)
        assertRangedTriggerPressesExactly(AXIS_BRAKE to 0f, pressed = L2)
        assertRangedTriggerPressesExactly(AXIS_GAS to -0.02f)
        assertRangedTriggerPressesExactly(AXIS_GAS to 0f, pressed = R2)
    }

    @Test
    fun `digital L2 key and analog L2 axis count as one press`() {
        keyDown(KeyEvent.KEYCODE_BUTTON_L2)
        moveAxes(AXIS_LTRIGGER to 1f)
        applyPendingInput()
        assertPressedExactly(L2)

        moveAxes(AXIS_LTRIGGER to 0f)
        applyPendingInput()
        assertPressedExactly(L2)

        keyUp(KeyEvent.KEYCODE_BUTTON_L2)
        applyPendingInput()
        assertPressedExactly()

        assertFired(L2)
    }

    @Test
    fun `analog R2 axis keeps the button pressed after the digital R2 key is released`() {
        moveAxes(AXIS_RTRIGGER to 1f)
        keyDown(KeyEvent.KEYCODE_BUTTON_R2)
        applyPendingInput()

        keyUp(KeyEvent.KEYCODE_BUTTON_R2)
        applyPendingInput()
        assertPressedExactly(R2)

        moveAxes(AXIS_RTRIGGER to 0f)
        applyPendingInput()
        assertPressedExactly()

        assertFired(R2)
    }

    @Test
    fun `motion that is not from a joystick is ignored`() {
        listOf(SOURCE_TOUCHSCREEN, SOURCE_MOUSE, SOURCE_GAMEPAD).forEach { source ->
            assertFalse(
                "Motion from source $source consumed",
                moveAxes(AXIS_HAT_X to -1f, AXIS_LTRIGGER to 1f, source = source, deviceId = UNKNOWN_DEVICE_ID)
            )
        }

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `joystick motion that is no move is ignored`() {
        assertFalse(moveAxes(AXIS_HAT_X to -1f, action = MotionEvent.ACTION_DOWN))

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `stick motion presses nothing`() {
        moveAxes(AXIS_X to -1f, AXIS_Y to 1f)

        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `motion is not consumed when none of its buttons is handled`() {
        unhandledButtons += MOTION_BUTTONS

        assertFalse(moveAxes(AXIS_HAT_X to -1f, AXIS_HAT_Y to -1f, AXIS_LTRIGGER to 1f, AXIS_RTRIGGER to 1f))
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `motion with one handled button is consumed but forwards only handled buttons`() {
        unhandledButtons += MOTION_BUTTONS - L2

        assertTrue(moveAxes(AXIS_HAT_X to -1f, AXIS_HAT_Y to -1f, AXIS_LTRIGGER to 1f, AXIS_RTRIGGER to 1f))
        applyPendingInput()

        assertFired(L2)
        assertPressedExactly(L2)
    }

    @Test
    fun `motion does not release unhandled buttons`() {
        unhandledButtons += LEFT
        input.setButtonState(LEFT, GamepadInputSource.HAT_AXIS, true)
        applyPendingInput()

        moveAxes(AXIS_HAT_X to 0f)
        applyPendingInput()

        assertPressedExactly(LEFT)
    }

    @Test
    fun `reported B of an unhandled swapped A is consumed so that Android does not treat it as Back`() {
        unhandledButtons += A

        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B)))
        applyPendingInput()

        assertPressedExactly()
        assertFired()
        assertEquals(0, backPresses)
    }
}
