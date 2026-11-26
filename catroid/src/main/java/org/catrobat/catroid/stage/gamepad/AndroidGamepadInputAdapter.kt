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

package org.catrobat.catroid.stage.gamepad

import android.hardware.input.InputManager
import android.os.Handler
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import java.util.EnumSet
import kotlin.math.max

/**
 * Translates the key and motion events of a physical game controller into [GamepadInput] changes.
 *
 * Only events from a game controller count: the event or its input device has the gamepad or the
 * joystick source. Keyboard keys, system keys such as Back, Home or volume, and controller keys
 * without a [GamepadButton] are never consumed, so Android handles them as usual. A mapped button
 * is forwarded and consumed only while a [GamepadInput] is available and [isButtonHandled] accepts
 * it. Otherwise Android handles it as usual too.
 *
 * The adapter only remembers which analog triggers count as pressed, so that a pressed trigger is
 * released at a lower travel than it is pressed at, and supports a single controller. Use it on
 * one thread only. To release every button when a controller is removed, [register] it as
 * [InputManager.InputDeviceListener].
 *
 * With [swapsFaceButtons], the reported A and B, and X and Y, change places, for controllers that
 * report them the other way round. Android still sees the reported key, so a reported B that the
 * project does not handle is consumed instead of acting as Back, and a reported A that counts as an
 * unhandled B calls [pressBack] when it is released.
 *
 * @param gamepadInput returns the gamepad input of the running stage, or `null` if there is none
 * @param isButtonHandled returns whether the running project handles the given button
 * @param swapsFaceButtons whether A and B, and X and Y, change places
 * @param pressBack presses Android Back for an unhandled B when [swapsFaceButtons] is set
 */
class AndroidGamepadInputAdapter @JvmOverloads constructor(
    private val gamepadInput: () -> GamepadInput?,
    private val isButtonHandled: (GamepadButton) -> Boolean,
    private val swapsFaceButtons: Boolean = false,
    private val pressBack: () -> Unit = {}
) : InputManager.InputDeviceListener {

    /** The triggers whose travel counted as pressed in the last joystick move. */
    private val pressedTriggers: MutableSet<GamepadButton> = EnumSet.noneOf(GamepadButton::class.java)

    /**
     * Forwards a mapped controller key as a [GamepadInputSource.KEY] change. Repeated key downs of
     * a held key are consumed but not forwarded.
     *
     * @return true if the event is consumed, false if Android should handle it
     */
    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val reportedButton = KEY_CODE_BUTTONS[event.keyCode] ?: return false
        if (!isFromGameController(event)) {
            return false
        }
        val input = gamepadInput() ?: return false
        val button = if (swapsFaceButtons) swappedFaceButton(reportedButton) else reportedButton
        if (!isButtonHandled(button)) {
            return consumesUnhandledSwappedButton(event, reportedButton, button)
        }
        if (event.repeatCount == 0) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> input.setButtonState(button, GamepadInputSource.KEY, true)
                KeyEvent.ACTION_UP -> input.setButtonState(button, GamepadInputSource.KEY, false)
            }
        }
        return true
    }

    /**
     * Forwards the hat (D-pad) and the analog triggers of a joystick move. Each event reports the
     * current state of all these buttons, and [GamepadInput] ignores states that did not change.
     * Consuming the event also keeps Android from turning the hat into D-pad keys.
     *
     * @return true if the event is consumed because the project handles at least one of its
     * buttons, false if Android should handle it
     */
    fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (!event.isFromSource(InputDevice.SOURCE_JOYSTICK) ||
            event.actionMasked != MotionEvent.ACTION_MOVE
        ) {
            return false
        }
        val input = gamepadInput() ?: return false
        val handledStates = axisButtonStates(event).filter { isButtonHandled(it.button) }
        handledStates.forEach { input.setButtonState(it.button, it.source, it.pressed) }
        return handledStates.isNotEmpty()
    }

    /** Releases every button without firing, for example when the stage pauses or loses focus. */
    fun releaseAll() {
        pressedTriggers.clear()
        gamepadInput()?.releaseAll()
    }

    /**
     * Starts listening for removed controllers. Does nothing without an [inputManager].
     *
     * @param handler the handler whose thread receives the callbacks, or `null` for the calling
     * thread's looper
     */
    @JvmOverloads
    fun register(inputManager: InputManager?, handler: Handler? = null) {
        inputManager?.registerInputDeviceListener(this, handler)
    }

    /** Stops listening for removed controllers. Does nothing without an [inputManager]. */
    fun unregister(inputManager: InputManager?) {
        inputManager?.unregisterInputDeviceListener(this)
    }

    override fun onInputDeviceAdded(deviceId: Int) = Unit

    override fun onInputDeviceChanged(deviceId: Int) = Unit

    /**
     * Releases every button. The removed device can no longer be identified as a controller, and
     * with a single supported controller any removal may be the controller.
     */
    override fun onInputDeviceRemoved(deviceId: Int) = releaseAll()

    private fun isFromGameController(event: KeyEvent): Boolean {
        if (GAME_CONTROLLER_SOURCES.any(event::isFromSource)) {
            return true
        }
        val device = event.device ?: return false
        return GAME_CONTROLLER_SOURCES.any(device::supportsSource)
    }

    /**
     * Keeps Android from seeing the Back of a reported B that counts as A, and presses Back for a
     * reported A that counts as B, because Android only knows the reported key.
     */
    private fun consumesUnhandledSwappedButton(
        event: KeyEvent,
        reportedButton: GamepadButton,
        button: GamepadButton
    ): Boolean {
        if (!swapsFaceButtons || reportedButton == button) {
            return false
        }
        return when {
            reportedButton == GamepadButton.B -> true
            button == GamepadButton.B -> {
                if (event.action == KeyEvent.ACTION_UP && event.repeatCount == 0) {
                    pressBack()
                }
                true
            }
            else -> false
        }
    }

    private fun swappedFaceButton(button: GamepadButton): GamepadButton =
        when (button) {
            GamepadButton.A -> GamepadButton.B
            GamepadButton.B -> GamepadButton.A
            GamepadButton.X -> GamepadButton.Y
            GamepadButton.Y -> GamepadButton.X
            else -> button
        }

    private fun axisButtonStates(event: MotionEvent): List<AxisButtonState> {
        val hatX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
        val hatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)
        val device = event.device
        val leftTriggerTravel =
            triggerTravel(event, device, MotionEvent.AXIS_LTRIGGER, MotionEvent.AXIS_BRAKE)
        val rightTriggerTravel =
            triggerTravel(event, device, MotionEvent.AXIS_RTRIGGER, MotionEvent.AXIS_GAS)
        return listOf(
            hatState(GamepadButton.LEFT, hatX <= -HAT_PRESS_THRESHOLD),
            hatState(GamepadButton.RIGHT, hatX >= HAT_PRESS_THRESHOLD),
            hatState(GamepadButton.UP, hatY <= -HAT_PRESS_THRESHOLD),
            hatState(GamepadButton.DOWN, hatY >= HAT_PRESS_THRESHOLD),
            triggerState(GamepadButton.L2, leftTriggerTravel),
            triggerState(GamepadButton.R2, rightTriggerTravel)
        )
    }

    /** Returns the travel of the further pulled of the two axes that a trigger may report. */
    private fun triggerTravel(
        event: MotionEvent,
        device: InputDevice?,
        triggerAxis: Int,
        alternativeAxis: Int
    ): Float = max(axisTravel(event, device, triggerAxis), axisTravel(event, device, alternativeAxis))

    /**
     * Returns how far [axis] is pulled, from [NO_TRAVEL] to [FULL_TRAVEL], relative to its motion
     * range on the [device]. Without a known range the raw value is taken as travel.
     */
    private fun axisTravel(event: MotionEvent, device: InputDevice?, axis: Int): Float {
        val value = event.getAxisValue(axis)
        val range = device?.getMotionRange(axis, event.source)
        val travel = if (range != null && range.max > range.min) {
            (value - range.min) / (range.max - range.min)
        } else {
            value
        }
        return travel.coerceIn(NO_TRAVEL, FULL_TRAVEL)
    }

    private fun hatState(button: GamepadButton, pressed: Boolean) =
        AxisButtonState(button, GamepadInputSource.HAT_AXIS, pressed)

    /**
     * Returns whether the trigger [button] counts as pressed at [travel] and remembers it. A
     * released trigger is pressed from [TRIGGER_PRESS_THRESHOLD], a pressed trigger is released
     * below [TRIGGER_RELEASE_THRESHOLD], so that a trigger that jitters around the press threshold
     * does not repeat its press.
     */
    private fun triggerState(button: GamepadButton, travel: Float): AxisButtonState {
        val wasPressed = button in pressedTriggers
        val pressed = travel >= if (wasPressed) TRIGGER_RELEASE_THRESHOLD else TRIGGER_PRESS_THRESHOLD
        if (pressed) {
            pressedTriggers.add(button)
        } else {
            pressedTriggers.remove(button)
        }
        return AxisButtonState(button, GamepadInputSource.TRIGGER_AXIS, pressed)
    }

    private data class AxisButtonState(
        val button: GamepadButton,
        val source: GamepadInputSource,
        val pressed: Boolean
    )

    private companion object {
        /** The hat reports -1, 0 or 1 per axis. A direction counts as pressed from half way. */
        const val HAT_PRESS_THRESHOLD = 0.5f

        /** A released trigger counts as pressed from half of its travel. */
        const val TRIGGER_PRESS_THRESHOLD = 0.5f

        /** A pressed trigger counts as released below 40% of its travel. */
        const val TRIGGER_RELEASE_THRESHOLD = 0.4f

        const val NO_TRAVEL = 0f
        const val FULL_TRAVEL = 1f

        val GAME_CONTROLLER_SOURCES = intArrayOf(InputDevice.SOURCE_GAMEPAD, InputDevice.SOURCE_JOYSTICK)

        val KEY_CODE_BUTTONS = mapOf(
            KeyEvent.KEYCODE_BUTTON_A to GamepadButton.A,
            KeyEvent.KEYCODE_BUTTON_B to GamepadButton.B,
            KeyEvent.KEYCODE_BUTTON_X to GamepadButton.X,
            KeyEvent.KEYCODE_BUTTON_Y to GamepadButton.Y,
            KeyEvent.KEYCODE_DPAD_UP to GamepadButton.UP,
            KeyEvent.KEYCODE_DPAD_DOWN to GamepadButton.DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT to GamepadButton.LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT to GamepadButton.RIGHT,
            KeyEvent.KEYCODE_BUTTON_START to GamepadButton.START,
            KeyEvent.KEYCODE_BUTTON_SELECT to GamepadButton.SELECT,
            KeyEvent.KEYCODE_BUTTON_L1 to GamepadButton.L1,
            KeyEvent.KEYCODE_BUTTON_L2 to GamepadButton.L2,
            KeyEvent.KEYCODE_BUTTON_R1 to GamepadButton.R1,
            KeyEvent.KEYCODE_BUTTON_R2 to GamepadButton.R2
        )
    }
}
