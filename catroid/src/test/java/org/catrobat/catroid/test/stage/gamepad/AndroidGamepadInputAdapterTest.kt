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

import android.hardware.input.InputManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.InputDevice
import android.view.InputDevice.SOURCE_DPAD
import android.view.InputDevice.SOURCE_GAMEPAD
import android.view.InputDevice.SOURCE_JOYSTICK
import android.view.InputDevice.SOURCE_KEYBOARD
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
import android.view.MotionEvent.AXIS_RZ
import android.view.MotionEvent.AXIS_X
import android.view.MotionEvent.AXIS_Y
import android.view.MotionEvent.AXIS_Z
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.catrobat.catroid.gamepad.GamepadButton.DOWN
import org.catrobat.catroid.gamepad.GamepadButton.L1
import org.catrobat.catroid.gamepad.GamepadButton.L2
import org.catrobat.catroid.gamepad.GamepadButton.LEFT
import org.catrobat.catroid.gamepad.GamepadButton.R1
import org.catrobat.catroid.gamepad.GamepadButton.R2
import org.catrobat.catroid.gamepad.GamepadButton.RIGHT
import org.catrobat.catroid.gamepad.GamepadButton.SELECT
import org.catrobat.catroid.gamepad.GamepadButton.START
import org.catrobat.catroid.gamepad.GamepadButton.UP
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadButton.Y
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.gamepad.GamepadStick
import org.catrobat.catroid.gamepad.GamepadStickPosition
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.stage.gamepad.AndroidGamepadInputAdapter
import org.catrobat.catroid.stage.gamepad.StageGamepadInputAdapterFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.InputDeviceBuilder

/**
 * Runs on API 35 because Robolectric's [InputDeviceBuilder] needs API 34 or newer to create input
 * devices with sources and motion ranges.
 *
 * Robolectric's shadow of `InputEvent.getDevice()` does not look up registered input devices but
 * returns the device set on the shadow of the event, so every test event gets its device that way.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
class AndroidGamepadInputAdapterTest {

    private val input = GamepadInput()
    private var availableInput: GamepadInput? = input
    private val unhandledButtons = mutableSetOf<GamepadButton>()
    private val firedButtons = mutableListOf<GamepadButton>()
    private lateinit var devices: Map<Int, InputDevice>

    private val adapter = AndroidGamepadInputAdapter(
        gamepadInput = { availableInput },
        isButtonHandled = { button -> button !in unhandledButtons }
    )
    private var backPresses = 0
    private val swappingAdapter = AndroidGamepadInputAdapter(
        gamepadInput = { availableInput },
        isButtonHandled = { button -> button !in unhandledButtons },
        swapsFaceButtons = true,
        pressBack = { backPresses++ }
    )

    @Before
    fun setUp() {
        devices = listOf(
            inputDevice(GAMEPAD_DEVICE_ID, GAMEPAD_SOURCES),
            rangedGamepadDevice(),
            inputDevice(JOYSTICK_DEVICE_ID, SOURCE_JOYSTICK),
            inputDevice(KEYBOARD_DEVICE_ID, SOURCE_KEYBOARD),
            inputDevice(REMOTE_CONTROL_DEVICE_ID, SOURCE_KEYBOARD or SOURCE_DPAD)
        ).associateBy { it.id }
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

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
    fun `D-pad keys with D-pad source from a device that is no game controller are ignored`() {
        DPAD_KEY_CODES.forEach { keyCode ->
            assertFalse(keyDown(keyCode, source = SOURCE_DPAD, deviceId = REMOTE_CONTROL_DEVICE_ID))
        }

        applyPendingInput()

        assertFired()
        assertPressedExactly()
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
    fun `releaseAll releases every held button without firing`() {
        holdButtonsFromAllSources()

        adapter.releaseAll()
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `removing an input device releases every held button without firing`() {
        holdButtonsFromAllSources()

        adapter.onInputDeviceRemoved(GAMEPAD_DEVICE_ID)
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `adding or changing an input device keeps held buttons`() {
        holdButtonsFromAllSources()

        adapter.onInputDeviceAdded(KEYBOARD_DEVICE_ID)
        adapter.onInputDeviceChanged(GAMEPAD_DEVICE_ID)
        applyPendingInput()

        assertFired()
        assertPressedExactly(A, LEFT, R2)
    }

    @Test
    fun `register adds and unregister removes the input device listener`() {
        val inputManager = mock(InputManager::class.java)
        val handler = Handler(Looper.getMainLooper())

        adapter.register(inputManager, handler)
        verify(inputManager).registerInputDeviceListener(adapter, handler)

        adapter.unregister(inputManager)
        verify(inputManager).unregisterInputDeviceListener(adapter)
    }

    @Test
    fun `register without handler uses the calling thread`() {
        val inputManager = mock(InputManager::class.java)

        adapter.register(inputManager)

        verify(inputManager).registerInputDeviceListener(adapter, null)
    }

    @Test
    fun `register and unregister without input manager do nothing`() {
        adapter.register(null)
        adapter.unregister(null)
    }

    @Test
    fun `stage adapter consumes and forwards controller B in every project`() {
        useStageGamepadInput()
        val projects = listOf(
            null,
            Project(),
            projectWith(StartScript()),
            projectWith(WhenGamepadButtonPressedScript(A.name)),
            projectWith(WhenGamepadButtonPressedScript(B.name))
        )

        projects.forEachIndexed { index, project ->
            val stageAdapter = StageGamepadInputAdapterFactory.create(project)

            val downConsumed = stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B))
            assertTrue("B down consumed in project $index", downConsumed)
            applyPendingInput()
            assertPressedExactly(B)
            val upConsumed = stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B))
            assertTrue("B up consumed in project $index", upConsumed)
            applyPendingInput()
            assertPressedExactly()
        }
        assertFired(B, B, B, B, B)
    }

    @Test
    fun `stage adapter consumes other buttons only in a project with gamepad elements`() {
        useStageGamepadInput()
        val adapterWithoutGamepad = StageGamepadInputAdapterFactory.create(projectWith(StartScript()))
        val adapterWithGamepad =
            StageGamepadInputAdapterFactory.create(projectWith(WhenGamepadButtonPressedScript(X.name)))

        KEY_CODE_BUTTONS.filterValues { it != B }.keys.forEach { keyCode ->
            assertFalse(adapterWithoutGamepad.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, keyCode)))
            assertTrue(adapterWithGamepad.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, keyCode)))
        }
    }

    @Test
    fun `stage adapter does not consume B from a keyboard`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(Project())

        assertFalse(
            stageAdapter.dispatchKeyEvent(
                keyEvent(
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B,
                    source = SOURCE_KEYBOARD, deviceId = KEYBOARD_DEVICE_ID
                )
            )
        )
    }

    @Test
    fun `stage adapter does not consume B without a running stage`() {
        val stageAdapter = StageGamepadInputAdapterFactory.create(Project())

        assertFalse(stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
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
    fun `reported B of an unhandled swapped A is consumed so that Android does not treat it as Back`() {
        unhandledButtons += A

        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        assertTrue(swappingAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B)))
        applyPendingInput()

        assertPressedExactly()
        assertFired()
        assertEquals(0, backPresses)
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

    @Test
    fun `stage adapter with swapped face buttons takes a controller A as B`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(Project(), swapsFaceButtons = true)

        assertTrue(stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)))
        assertTrue(stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        input.applyPendingInput { button -> firedButtons.add(button) }

        assertPressedExactly(B)
        assertFired(B)
    }

    @Test
    fun `stage adapter forwards the left stick of a project that uses a left stick sensor`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_LEFT_STICK_X))

        assertTrue(moveAxes(AXIS_X to 0.5f, AXIS_Y to -0.25f, target = stageAdapter))

        assertEquals(GamepadStickPosition(50.0, 25.0), input.stickPosition(GamepadStick.LEFT))
        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.RIGHT))
    }

    @Test
    fun `stage adapter reads the right stick from the Z and RZ axes`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_RIGHT_STICK_Y))

        assertTrue(moveAxes(AXIS_Z to 1f, AXIS_RZ to 1f, AXIS_X to 1f, target = stageAdapter))

        assertEquals(GamepadStickPosition(100.0, -100.0), input.stickPosition(GamepadStick.RIGHT))
        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.LEFT))
    }

    @Test
    fun `stick deflection in the dead zone around the center reads as centered`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_LEFT_STICK_Y))

        assertTrue(moveAxes(AXIS_X to 0.05f, AXIS_Y to -0.09f, target = stageAdapter))

        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.LEFT))
    }

    @Test
    fun `stage adapter does not forward a stick that the project does not use`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_BUTTON_A_PRESSED))

        moveAxes(AXIS_X to 1f, AXIS_Z to 1f, target = stageAdapter)

        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.LEFT))
        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.RIGHT))
    }

    /** Lets the adapters of [StageGamepadInputAdapterFactory] feed [input] as gamepad input of the stage. */
    private fun useStageGamepadInput() {
        val stageListener = mock(StageListener::class.java)
        doReturn(input).`when`(stageListener).gamepadInput
        StageActivity.stageListener = stageListener
    }

    private fun projectWith(vararg scripts: Script): Project {
        val project = Project()
        val scene = Scene("Scene", project)
        scene.addSprite(Sprite("Sprite").apply { scripts.forEach { addScript(it) } })
        project.addScene(scene)
        return project
    }

    private fun holdButtonsFromAllSources() {
        keyDown(KeyEvent.KEYCODE_BUTTON_A)
        moveAxes(AXIS_HAT_X to -1f, AXIS_RTRIGGER to 1f)
        applyPendingInput()
        assertPressedExactly(A, LEFT, R2)
        firedButtons.clear()
    }

    private fun holdWithKeySource(button: GamepadButton) {
        input.setButtonState(button, GamepadInputSource.KEY, true)
    }

    private fun assertHatPressesExactly(axis: Int, value: Float, vararg pressed: GamepadButton) {
        assertTrue(moveAxes(axis to value))
        applyPendingInput()
        assertPressedExactly(*pressed)
    }

    private fun assertTriggerPressesExactly(
        axisValues: Array<Pair<Int, Float>>,
        vararg pressed: GamepadButton
    ) {
        assertTrue(moveAxes(*axisValues))
        applyPendingInput()
        assertPressedExactly(*pressed)
    }

    private fun assertRangedTriggerPressesExactly(
        axisValue: Pair<Int, Float>,
        pressed: GamepadButton? = null
    ) {
        val axisValues = RESTING_RANGED_TRIGGER_AXES + axisValue
        assertTrue(moveAxes(*axisValues.toList().toTypedArray(), deviceId = RANGED_GAMEPAD_DEVICE_ID))
        applyPendingInput()
        assertPressedExactly(*listOfNotNull(pressed).toTypedArray())
    }

    private fun keyDown(
        keyCode: Int,
        source: Int = SOURCE_GAMEPAD,
        deviceId: Int = GAMEPAD_DEVICE_ID,
        repeatCount: Int = 0
    ): Boolean = dispatchKey(KeyEvent.ACTION_DOWN, keyCode, source, deviceId, repeatCount)

    private fun keyUp(
        keyCode: Int,
        source: Int = SOURCE_GAMEPAD,
        deviceId: Int = GAMEPAD_DEVICE_ID
    ): Boolean = dispatchKey(KeyEvent.ACTION_UP, keyCode, source, deviceId, repeatCount = 0)

    private fun dispatchKey(action: Int, keyCode: Int, source: Int, deviceId: Int, repeatCount: Int): Boolean =
        adapter.dispatchKeyEvent(keyEvent(action, keyCode, source, deviceId, repeatCount))

    private fun keyEvent(
        action: Int,
        keyCode: Int,
        source: Int = SOURCE_GAMEPAD,
        deviceId: Int = GAMEPAD_DEVICE_ID,
        repeatCount: Int = 0
    ): KeyEvent {
        val event = KeyEvent(0L, 0L, action, keyCode, repeatCount, 0, deviceId, 0, 0, source)
        shadowOf(event).setDevice(devices[deviceId])
        return event
    }

    private fun projectUsingSensor(sensor: Sensors): Project =
        projectWith(
            StartScript().apply {
                addBrick(ChangeXByNBrick(Formula(FormulaElement(FormulaElement.ElementType.SENSOR, sensor.name, null))))
            }
        )

    private fun moveAxes(
        vararg axisValues: Pair<Int, Float>,
        source: Int = SOURCE_JOYSTICK,
        deviceId: Int = GAMEPAD_DEVICE_ID,
        action: Int = MotionEvent.ACTION_MOVE,
        target: AndroidGamepadInputAdapter = adapter
    ): Boolean {
        val properties = MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_UNKNOWN
        }
        val coordinates = MotionEvent.PointerCoords().apply {
            axisValues.forEach { (axis, value) -> setAxisValue(axis, value) }
        }
        val event = MotionEvent.obtain(
            0L, 0L, action, 1, arrayOf(properties), arrayOf(coordinates),
            0, 0, 1f, 1f, deviceId, 0, source, 0
        )
        shadowOf(event).setDevice(devices[deviceId])
        return try {
            target.dispatchGenericMotionEvent(event)
        } finally {
            event.recycle()
        }
    }

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

    private fun inputDevice(id: Int, sources: Int): InputDevice =
        InputDeviceBuilder.newBuilder()
            .setId(id)
            .setName("Test device $id")
            .setSources(sources)
            .build()

    private fun rangedGamepadDevice(): InputDevice =
        RESTING_RANGED_TRIGGER_AXES.keys
            .fold(
                InputDeviceBuilder.newBuilder()
                    .setId(RANGED_GAMEPAD_DEVICE_ID)
                    .setName("Test device with trigger ranges")
                    .setSources(GAMEPAD_SOURCES)
            ) { builder, axis ->
                builder.addMotionRange(axis, SOURCE_JOYSTICK, RANGE_MIN, RANGE_MAX, 0f, 0f, 0f)
            }
            .build()

    private companion object {
        const val GAMEPAD_DEVICE_ID = 11
        const val RANGED_GAMEPAD_DEVICE_ID = 12
        const val JOYSTICK_DEVICE_ID = 13
        const val KEYBOARD_DEVICE_ID = 14
        const val REMOTE_CONTROL_DEVICE_ID = 15
        const val UNKNOWN_DEVICE_ID = 99

        const val GAMEPAD_SOURCES = SOURCE_GAMEPAD or SOURCE_JOYSTICK or SOURCE_DPAD or SOURCE_KEYBOARD

        const val RANGE_MIN = -1f
        const val RANGE_MAX = 1f

        val RESTING_RANGED_TRIGGER_AXES = mapOf(
            AXIS_LTRIGGER to RANGE_MIN,
            AXIS_BRAKE to RANGE_MIN,
            AXIS_RTRIGGER to RANGE_MIN,
            AXIS_GAS to RANGE_MIN
        )

        val KEY_CODE_BUTTONS = linkedMapOf(
            KeyEvent.KEYCODE_BUTTON_A to A,
            KeyEvent.KEYCODE_BUTTON_B to B,
            KeyEvent.KEYCODE_BUTTON_X to X,
            KeyEvent.KEYCODE_BUTTON_Y to Y,
            KeyEvent.KEYCODE_DPAD_UP to UP,
            KeyEvent.KEYCODE_DPAD_DOWN to DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT to LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT to RIGHT,
            KeyEvent.KEYCODE_BUTTON_START to START,
            KeyEvent.KEYCODE_BUTTON_SELECT to SELECT,
            KeyEvent.KEYCODE_BUTTON_L1 to L1,
            KeyEvent.KEYCODE_BUTTON_L2 to L2,
            KeyEvent.KEYCODE_BUTTON_R1 to R1,
            KeyEvent.KEYCODE_BUTTON_R2 to R2
        )

        val DPAD_KEY_CODES = listOf(
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT
        )

        val UNMAPPED_KEY_CODES = listOf(
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_MUTE,
            KeyEvent.KEYCODE_BUTTON_MODE,
            KeyEvent.KEYCODE_BUTTON_THUMBL,
            KeyEvent.KEYCODE_BUTTON_THUMBR,
            KeyEvent.KEYCODE_BUTTON_C,
            KeyEvent.KEYCODE_BUTTON_Z,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_SPACE
        )

        val MOTION_BUTTONS = setOf(LEFT, RIGHT, UP, DOWN, L2, R2)
    }
}
