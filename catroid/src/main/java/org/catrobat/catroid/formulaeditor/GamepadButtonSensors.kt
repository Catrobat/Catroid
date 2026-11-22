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
@file:JvmName("GamepadButtonSensors")

package org.catrobat.catroid.formulaeditor

import androidx.annotation.StringRes
import org.catrobat.catroid.R
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.stage.StageActivity

private const val PRESSED_VALUE = 1.0
private const val RELEASED_VALUE = 0.0

/** The Boolean formula editor sensor that tells whether this physical gamepad button is pressed. */
val GamepadButton.sensor: Sensors
    get() = when (this) {
        GamepadButton.A -> Sensors.GAMEPAD_BUTTON_A_PRESSED
        GamepadButton.B -> Sensors.GAMEPAD_BUTTON_B_PRESSED
        GamepadButton.X -> Sensors.GAMEPAD_BUTTON_X_PRESSED
        GamepadButton.Y -> Sensors.GAMEPAD_BUTTON_Y_PRESSED
        GamepadButton.UP -> Sensors.GAMEPAD_BUTTON_UP_PRESSED
        GamepadButton.DOWN -> Sensors.GAMEPAD_BUTTON_DOWN_PRESSED
        GamepadButton.LEFT -> Sensors.GAMEPAD_BUTTON_LEFT_PRESSED
        GamepadButton.RIGHT -> Sensors.GAMEPAD_BUTTON_RIGHT_PRESSED
        GamepadButton.START -> Sensors.GAMEPAD_BUTTON_START_PRESSED
        GamepadButton.SELECT -> Sensors.GAMEPAD_BUTTON_SELECT_PRESSED
        GamepadButton.L1 -> Sensors.GAMEPAD_BUTTON_L1_PRESSED
        GamepadButton.L2 -> Sensors.GAMEPAD_BUTTON_L2_PRESSED
        GamepadButton.R1 -> Sensors.GAMEPAD_BUTTON_R1_PRESSED
        GamepadButton.R2 -> Sensors.GAMEPAD_BUTTON_R2_PRESSED
    }

/**
 * The string resource of the name under which the formula editor shows the [sensor] of this button,
 * for example "gamepad button A pressed".
 */
@get:StringRes
val GamepadButton.sensorNameResId: Int
    get() = when (this) {
        GamepadButton.A -> R.string.formula_editor_sensor_gamepad_button_a_pressed
        GamepadButton.B -> R.string.formula_editor_sensor_gamepad_button_b_pressed
        GamepadButton.X -> R.string.formula_editor_sensor_gamepad_button_x_pressed
        GamepadButton.Y -> R.string.formula_editor_sensor_gamepad_button_y_pressed
        GamepadButton.UP -> R.string.formula_editor_sensor_gamepad_button_up_pressed
        GamepadButton.DOWN -> R.string.formula_editor_sensor_gamepad_button_down_pressed
        GamepadButton.LEFT -> R.string.formula_editor_sensor_gamepad_button_left_pressed
        GamepadButton.RIGHT -> R.string.formula_editor_sensor_gamepad_button_right_pressed
        GamepadButton.START -> R.string.formula_editor_sensor_gamepad_button_start_pressed
        GamepadButton.SELECT -> R.string.formula_editor_sensor_gamepad_button_select_pressed
        GamepadButton.L1 -> R.string.formula_editor_sensor_gamepad_button_l1_pressed
        GamepadButton.L2 -> R.string.formula_editor_sensor_gamepad_button_l2_pressed
        GamepadButton.R1 -> R.string.formula_editor_sensor_gamepad_button_r1_pressed
        GamepadButton.R2 -> R.string.formula_editor_sensor_gamepad_button_r2_pressed
    }

private val buttonsBySensor: Map<Sensors, GamepadButton> =
    GamepadButton.entries.associateBy { it.sensor }

private val sensorsBySensorNameResId: Map<Int, Sensors> =
    GamepadButton.entries.associate { it.sensorNameResId to it.sensor }

/**
 * Returns the physical gamepad button whose pressed state this sensor reports, or `null` for any
 * other sensor, including the legacy Chromecast gamepad sensors such as [Sensors.GAMEPAD_A_PRESSED].
 */
fun Sensors.toGamepadButton(): GamepadButton? = buttonsBySensor[this]

/**
 * Returns the gamepad button sensor that the formula editor shows under the string resource
 * [sensorNameResId] (see [sensorNameResId]), or `null` if the resource names no such sensor.
 */
fun findGamepadButtonSensor(@StringRes sensorNameResId: Int): Sensors? =
    sensorsBySensorNameResId[sensorNameResId]

/**
 * The gamepad input that the formula editor's Compute dialog receives while it is open, or `null`.
 * While it is set, the gamepad sensors read it instead of the input of the running stage.
 */
@Volatile
var formulaEditorGamepadInput: GamepadInput? = null

/**
 * The value of the [sensor] of this button: 1.0 while the button is pressed in the formula
 * editor's Compute dialog or else on the running stage, otherwise 0.0, also when neither is open.
 */
fun GamepadButton.sensorValue(): Double {
    val gamepadInput: GamepadInput? =
        formulaEditorGamepadInput ?: StageActivity.stageListener?.gamepadInput
    return if (gamepadInput?.isPressed(this) == true) {
        PRESSED_VALUE
    } else {
        RELEASED_VALUE
    }
}
