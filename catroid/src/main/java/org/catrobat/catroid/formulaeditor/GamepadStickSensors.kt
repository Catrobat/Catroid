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

@file:JvmName("GamepadStickSensors")

package org.catrobat.catroid.formulaeditor

import androidx.annotation.StringRes
import org.catrobat.catroid.R
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadStick
import org.catrobat.catroid.gamepad.GamepadStickPosition
import org.catrobat.catroid.stage.StageActivity

/** An axis of an analog stick that a sensor reports. */
private data class StickAxis(val stick: GamepadStick, val isHorizontal: Boolean, @StringRes val nameResId: Int)

private val stickAxesBySensor: Map<Sensors, StickAxis> = linkedMapOf(
    Sensors.GAMEPAD_LEFT_STICK_X to
        StickAxis(GamepadStick.LEFT, true, R.string.formula_editor_sensor_gamepad_left_stick_x),
    Sensors.GAMEPAD_LEFT_STICK_Y to
        StickAxis(GamepadStick.LEFT, false, R.string.formula_editor_sensor_gamepad_left_stick_y),
    Sensors.GAMEPAD_RIGHT_STICK_X to
        StickAxis(GamepadStick.RIGHT, true, R.string.formula_editor_sensor_gamepad_right_stick_x),
    Sensors.GAMEPAD_RIGHT_STICK_Y to
        StickAxis(GamepadStick.RIGHT, false, R.string.formula_editor_sensor_gamepad_right_stick_y)
)

private val stickSensorsByNameResId: Map<Int, Sensors> =
    stickAxesBySensor.entries.associate { (sensor, axis) -> axis.nameResId to sensor }

/** The four analog stick sensors, in the order in which the formula editor offers them. */
val gamepadStickSensors: List<Sensors> = stickAxesBySensor.keys.toList()

/** The string resource of the name of a stick sensor, or `null` for other sensors. */
@StringRes
fun gamepadStickSensorNameResId(sensor: Sensors): Int? = stickAxesBySensor[sensor]?.nameResId

/** Returns the stick sensor whose name is the string resource [sensorNameResId], or `null`. */
fun findGamepadStickSensor(@StringRes sensorNameResId: Int): Sensors? =
    stickSensorsByNameResId[sensorNameResId]

/** Returns the stick that [this] sensor reports, or `null` if it is no stick sensor. */
fun Sensors.toGamepadStick(): GamepadStick? = stickAxesBySensor[this]?.stick

/**
 * Returns the deflection of the stick axis that [sensor] reports, from -100 to 100 with right and
 * up positive, or `null` if [sensor] is no stick sensor. Like the button sensors, it reads the input
 * of the open Compute dialog, otherwise the input of the running stage, and 0 without either.
 */
fun gamepadStickSensorValue(sensor: Sensors): Double? {
    val axis = stickAxesBySensor[sensor] ?: return null
    val gamepadInput: GamepadInput? =
        formulaEditorGamepadInput ?: StageActivity.stageListener?.gamepadInput
    val position = gamepadInput?.stickPosition(axis.stick) ?: GamepadStickPosition.CENTER
    return if (axis.isHorizontal) position.x else position.y
}
