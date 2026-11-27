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

import android.view.InputDevice
import android.view.InputDevice.SOURCE_DPAD
import android.view.InputDevice.SOURCE_GAMEPAD
import android.view.InputDevice.SOURCE_JOYSTICK
import android.view.InputDevice.SOURCE_KEYBOARD
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.MotionEvent.AXIS_BRAKE
import android.view.MotionEvent.AXIS_GAS
import android.view.MotionEvent.AXIS_HAT_X
import android.view.MotionEvent.AXIS_LTRIGGER
import android.view.MotionEvent.AXIS_RTRIGGER
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
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
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.stage.gamepad.AndroidGamepadInputAdapter
import org.catrobat.catroid.stage.gamepad.StageGamepadInputAdapterFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.InputDeviceBuilder

/**
 * Shared setup and helpers of the [AndroidGamepadInputAdapter] tests.
 *
 * Runs on API 35 because Robolectric's [InputDeviceBuilder] needs API 34 or newer to create input
 * devices with sources and motion ranges.
 *
 * Robolectric's shadow of `InputEvent.getDevice()` does not look up registered input devices but
 * returns the device set on the shadow of the event, so every test event gets its device that way.
 */
open class AndroidGamepadInputAdapterTestBase {

    protected val input = GamepadInput()
    protected var availableInput: GamepadInput? = input
    protected val unhandledButtons = mutableSetOf<GamepadButton>()
    protected val firedButtons = mutableListOf<GamepadButton>()
    protected lateinit var devices: Map<Int, InputDevice>

    protected val adapter = AndroidGamepadInputAdapter(
        gamepadInput = { availableInput },
        isButtonHandled = { button -> button !in unhandledButtons }
    )
    protected var backPresses = 0
    protected val swappingAdapter = AndroidGamepadInputAdapter(
        gamepadInput = { availableInput },
        isButtonHandled = { button -> button !in unhandledButtons },
        swapsFaceButtons = true,
        pressBack = { backPresses++ }
    )

    @Before
    fun setUpDevices() {
        devices = listOf(
            inputDevice(GAMEPAD_DEVICE_ID, GAMEPAD_SOURCES),
            rangedGamepadDevice(),
            inputDevice(JOYSTICK_DEVICE_ID, SOURCE_JOYSTICK),
            inputDevice(KEYBOARD_DEVICE_ID, SOURCE_KEYBOARD),
            inputDevice(REMOTE_CONTROL_DEVICE_ID, SOURCE_KEYBOARD or SOURCE_DPAD)
        ).associateBy { it.id }
    }

    @After
    fun resetStage() {
        StageActivity.stageListener = null
    }

    /** Lets the adapters of [StageGamepadInputAdapterFactory] feed [input] as gamepad input of the stage. */
    protected fun useStageGamepadInput() {
        val stageListener = mock(StageListener::class.java)
        doReturn(input).`when`(stageListener).gamepadInput
        StageActivity.stageListener = stageListener
    }

    protected fun projectWith(vararg scripts: Script): Project {
        val project = Project()
        val scene = Scene("Scene", project)
        scene.addSprite(Sprite("Sprite").apply { scripts.forEach { addScript(it) } })
        project.addScene(scene)
        return project
    }

    protected fun holdButtonsFromAllSources() {
        keyDown(KeyEvent.KEYCODE_BUTTON_A)
        moveAxes(AXIS_HAT_X to -1f, AXIS_RTRIGGER to 1f)
        applyPendingInput()
        assertPressedExactly(A, LEFT, R2)
        firedButtons.clear()
    }

    protected fun holdWithKeySource(button: GamepadButton) {
        input.setButtonState(button, GamepadInputSource.KEY, true)
    }

    protected fun assertHatPressesExactly(axis: Int, value: Float, vararg pressed: GamepadButton) {
        assertTrue(moveAxes(axis to value))
        applyPendingInput()
        assertPressedExactly(*pressed)
    }

    protected fun assertTriggerPressesExactly(
        axisValues: Array<Pair<Int, Float>>,
        vararg pressed: GamepadButton
    ) {
        assertTrue(moveAxes(*axisValues))
        applyPendingInput()
        assertPressedExactly(*pressed)
    }

    protected fun assertRangedTriggerPressesExactly(
        axisValue: Pair<Int, Float>,
        pressed: GamepadButton? = null
    ) {
        val axisValues = RESTING_RANGED_TRIGGER_AXES + axisValue
        assertTrue(moveAxes(*axisValues.toList().toTypedArray(), deviceId = RANGED_GAMEPAD_DEVICE_ID))
        applyPendingInput()
        assertPressedExactly(*listOfNotNull(pressed).toTypedArray())
    }

    protected fun keyDown(
        keyCode: Int,
        source: Int = SOURCE_GAMEPAD,
        deviceId: Int = GAMEPAD_DEVICE_ID,
        repeatCount: Int = 0
    ): Boolean = dispatchKey(KeyEvent.ACTION_DOWN, keyCode, source, deviceId, repeatCount)

    protected fun keyUp(
        keyCode: Int,
        source: Int = SOURCE_GAMEPAD,
        deviceId: Int = GAMEPAD_DEVICE_ID
    ): Boolean = dispatchKey(KeyEvent.ACTION_UP, keyCode, source, deviceId, repeatCount = 0)

    protected fun dispatchKey(action: Int, keyCode: Int, source: Int, deviceId: Int, repeatCount: Int): Boolean =
        adapter.dispatchKeyEvent(keyEvent(action, keyCode, source, deviceId, repeatCount))

    protected fun keyEvent(
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

    protected fun projectUsingSensor(sensor: Sensors): Project =
        projectWith(
            StartScript().apply {
                addBrick(ChangeXByNBrick(Formula(FormulaElement(FormulaElement.ElementType.SENSOR, sensor.name, null))))
            }
        )

    protected fun moveAxes(
        vararg axisValues: Pair<Int, Float>,
        source: Int = SOURCE_JOYSTICK,
        deviceId: Int = GAMEPAD_DEVICE_ID,
        action: Int = MotionEvent.ACTION_MOVE,
        target: AndroidGamepadInputAdapter = adapter
    ): Boolean = dispatchMotion(listOf(axisValues.toMap()), source, deviceId, action, target)

    /**
     * Moves the axes in one joystick move that carries several [samples], as Android batches them:
     * all but the last sample are historical, the last one is the current sample.
     */
    protected fun moveAxesInSamples(vararg samples: Map<Int, Float>): Boolean =
        dispatchMotion(samples.toList(), SOURCE_JOYSTICK, GAMEPAD_DEVICE_ID, MotionEvent.ACTION_MOVE, adapter)

    private fun dispatchMotion(
        samples: List<Map<Int, Float>>,
        source: Int,
        deviceId: Int,
        action: Int,
        target: AndroidGamepadInputAdapter
    ): Boolean {
        val properties = MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_UNKNOWN
        }
        val coordinates = samples.map { sample ->
            MotionEvent.PointerCoords().apply {
                sample.forEach { (axis, value) -> setAxisValue(axis, value) }
            }
        }
        val event = MotionEvent.obtain(
            0L, 0L, action, 1, arrayOf(properties), arrayOf(coordinates.first()),
            0, 0, 1f, 1f, deviceId, 0, source, 0
        )
        coordinates.drop(1).forEachIndexed { index, sample ->
            event.addBatch(index + 1L, arrayOf(sample), 0)
        }
        shadowOf(event).setDevice(devices[deviceId])
        return try {
            target.dispatchGenericMotionEvent(event)
        } finally {
            event.recycle()
        }
    }

    protected fun applyPendingInput() {
        input.applyPendingInput { button -> firedButtons.add(button) }
    }

    protected fun assertFired(vararg expectedButtons: GamepadButton) {
        assertEquals(expectedButtons.toList(), firedButtons)
    }

    protected fun assertPressedExactly(vararg expectedButtons: GamepadButton) {
        GamepadButton.entries.forEach { button ->
            assertEquals("Pressed state of $button", button in expectedButtons, input.isPressed(button))
        }
    }

    protected fun inputDevice(id: Int, sources: Int): InputDevice =
        InputDeviceBuilder.newBuilder()
            .setId(id)
            .setName("Test device $id")
            .setSources(sources)
            .build()

    protected fun rangedGamepadDevice(): InputDevice =
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

    companion object {
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
