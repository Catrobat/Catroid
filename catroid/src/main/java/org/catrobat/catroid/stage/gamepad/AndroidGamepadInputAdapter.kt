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
import kotlin.math.roundToInt
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.gamepad.GamepadStick
import org.catrobat.catroid.gamepad.GamepadStickPosition
import java.util.EnumSet
import kotlin.math.abs
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
 * The analog sticks are forwarded only while [isStickHandled] accepts them: the left stick from the
 * X and Y axes, the right stick from the Z and RZ axes, or from RX and RY on a device without Z.
 *
 * @param swapsFaceButtons whether A and B, and X and Y, change places
 * @param pressBack presses Android Back for an unhandled B when [swapsFaceButtons] is set
 * @param isStickHandled returns whether the running project reads the given analog stick
 */
class AndroidGamepadInputAdapter @JvmOverloads constructor(
    private val gamepadInput: () -> GamepadInput?,
    private val isButtonHandled: (GamepadButton) -> Boolean,
    private val swapsFaceButtons: Boolean = false,
    private val pressBack: () -> Unit = {},
    private val isStickHandled: (GamepadStick) -> Boolean = { false }
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
        val reportedButton = reportedGamepadButton(event) ?: return false
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
     * Forwards the hat (D-pad), the analog triggers and the handled analog sticks of a joystick
     * move. Each event reports the current state of all these buttons and sticks, and [GamepadInput]
     * ignores button states that did not change. Android may batch older samples into the event, so
     * the buttons are forwarded for each sample in order, and a short press within the batch still
     * counts. The sticks only take their latest position. Consuming the event also keeps Android from
     * turning the hat or a stick into D-pad keys.
     *
     * @return true if the event is consumed because the project handles at least one of its
     * buttons or sticks, false if Android should handle it
     */
    fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (!event.isFromSource(InputDevice.SOURCE_JOYSTICK) ||
            event.actionMasked != MotionEvent.ACTION_MOVE
        ) {
            return false
        }
        val input = gamepadInput() ?: return false
        val handledStates = (0..event.historySize)
            .flatMap { sample -> axisButtonStates(event, sample) }
            .filter { isButtonHandled(it.button) }
        handledStates.forEach { input.setButtonState(it.button, it.source, it.pressed) }
        val handledSticks = GamepadStick.entries.filter(isStickHandled)
        handledSticks.forEach { stick ->
            val position = stickPosition(event, stick)
            input.setStickPosition(stick, position.x, position.y)
        }
        return handledStates.isNotEmpty() || handledSticks.isNotEmpty()
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

    /**
     * Returns the states of the hat and trigger buttons in [sample] of [event], where the samples
     * before [MotionEvent.getHistorySize] are historical and the last one is the current sample.
     */
    private fun axisButtonStates(event: MotionEvent, sample: Int): List<AxisButtonState> {
        val axisValue = { axis: Int ->
            if (sample < event.historySize) {
                event.getHistoricalAxisValue(axis, sample)
            } else {
                event.getAxisValue(axis)
            }
        }
        val travel = { axis: Int -> axisTravel(axisValue(axis), event, axis) }
        val hatX = axisValue(MotionEvent.AXIS_HAT_X)
        val hatY = axisValue(MotionEvent.AXIS_HAT_Y)
        val leftTriggerTravel = max(travel(MotionEvent.AXIS_LTRIGGER), travel(MotionEvent.AXIS_BRAKE))
        val rightTriggerTravel = max(travel(MotionEvent.AXIS_RTRIGGER), travel(MotionEvent.AXIS_GAS))
        return listOf(
            hatState(GamepadButton.LEFT, hatX <= -HAT_PRESS_THRESHOLD),
            hatState(GamepadButton.RIGHT, hatX >= HAT_PRESS_THRESHOLD),
            hatState(GamepadButton.UP, hatY <= -HAT_PRESS_THRESHOLD),
            hatState(GamepadButton.DOWN, hatY >= HAT_PRESS_THRESHOLD),
            triggerState(GamepadButton.L2, leftTriggerTravel),
            triggerState(GamepadButton.R2, rightTriggerTravel)
        )
    }

    /**
     * Returns how far [axis] at [value] is pulled, from [NO_TRAVEL] to [FULL_TRAVEL], relative to
     * its motion range on the device of [event]. Without a known range the raw value is taken as
     * travel. A trigger takes the further pulled of the two axes it may report.
     */
    private fun axisTravel(value: Float, event: MotionEvent, axis: Int): Float {
        val range = event.device?.getMotionRange(axis, event.source)
        val travel = if (range != null && range.max > range.min) {
            (value - range.min) / (range.max - range.min)
        } else {
            value
        }
        return travel.coerceIn(NO_TRAVEL, FULL_TRAVEL)
    }

    /** Returns the position of [stick], with right and up positive like the stage. */
    private fun stickPosition(event: MotionEvent, stick: GamepadStick): GamepadStickPosition {
        val device = event.device
        val (xAxis, yAxis) = when (stick) {
            GamepadStick.LEFT -> MotionEvent.AXIS_X to MotionEvent.AXIS_Y
            GamepadStick.RIGHT -> if (usesRxRyForRightStick(device, event.source)) {
                MotionEvent.AXIS_RX to MotionEvent.AXIS_RY
            } else {
                MotionEvent.AXIS_Z to MotionEvent.AXIS_RZ
            }
        }
        return GamepadStickPosition.limited(
            stickDeflection(event, device, xAxis),
            -stickDeflection(event, device, yAxis)
        )
    }

    private fun usesRxRyForRightStick(device: InputDevice?, source: Int): Boolean =
        device?.getMotionRange(MotionEvent.AXIS_Z, source) == null &&
            device?.getMotionRange(MotionEvent.AXIS_RX, source) != null

    /**
     * Returns how far [axis] is pushed from the center, from -100 to 100 in whole steps, relative to
     * its motion range on the [device]. Deflections within [STICK_DEAD_ZONE] count as 0, so a resting
     * stick reads as centered.
     */
    private fun stickDeflection(event: MotionEvent, device: InputDevice?, axis: Int): Double {
        val value = event.getAxisValue(axis)
        val range = device?.getMotionRange(axis, event.source)
        val deflection = if (range != null && range.max > range.min) {
            2f * (value - range.min) / (range.max - range.min) - 1f
        } else {
            value
        }.coerceIn(-1f, 1f)
        return if (abs(deflection) < STICK_DEAD_ZONE) {
            0.0
        } else {
            (deflection * GamepadStickPosition.MAX_DEFLECTION).roundToInt().toDouble()
        }
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

        /** A stick deflection below 10% of the way to the edge counts as centered. */
        const val STICK_DEAD_ZONE = 0.1f

        const val NO_TRAVEL = 0f
        const val FULL_TRAVEL = 1f
    }
}
