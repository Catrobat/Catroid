/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.formulaeditor.common

import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Sensors

object FormulaElementResources {
    @JvmStatic
    fun addSensorsResources(resources: MutableSet<Int?>, sensor: Sensors?) {
        when (sensor) {
            Sensors.X_ACCELERATION,
            Sensors.Y_ACCELERATION,
            Sensors.Z_ACCELERATION -> Brick.SENSOR_ACCELERATION

            Sensors.X_INCLINATION,
            Sensors.Y_INCLINATION -> Brick.SENSOR_INCLINATION

            Sensors.COMPASS_DIRECTION -> Brick.SENSOR_COMPASS

            Sensors.LATITUDE,
            Sensors.LONGITUDE,
            Sensors.LOCATION_ACCURACY,
            Sensors.ALTITUDE -> Brick.SENSOR_GPS

            Sensors.FACE_DETECTED,
            Sensors.FACE_SIZE,
            Sensors.FACE_X_POSITION,
            Sensors.FACE_Y_POSITION,
            Sensors.SECOND_FACE_DETECTED,
            Sensors.SECOND_FACE_SIZE,
            Sensors.SECOND_FACE_X_POSITION,
            Sensors.SECOND_FACE_Y_POSITION -> Brick.FACE_DETECTION

            Sensors.NXT_SENSOR_1,
            Sensors.NXT_SENSOR_2,
            Sensors.NXT_SENSOR_3,
            Sensors.NXT_SENSOR_4 -> Brick.BLUETOOTH_LEGO_NXT

            Sensors.EV3_SENSOR_1,
            Sensors.EV3_SENSOR_2,
            Sensors.EV3_SENSOR_3,
            Sensors.EV3_SENSOR_4 -> Brick.BLUETOOTH_LEGO_EV3

            Sensors.PHIRO_FRONT_LEFT,
            Sensors.PHIRO_FRONT_RIGHT,
            Sensors.PHIRO_SIDE_LEFT,
            Sensors.PHIRO_SIDE_RIGHT,
            Sensors.PHIRO_BOTTOM_LEFT,
            Sensors.PHIRO_BOTTOM_RIGHT -> Brick.BLUETOOTH_PHIRO

            Sensors.DRONE_BATTERY_STATUS,
            Sensors.DRONE_CAMERA_READY,
            Sensors.DRONE_EMERGENCY_STATE,
            Sensors.DRONE_FLYING,
            Sensors.DRONE_INITIALIZED,
            Sensors.DRONE_NUM_FRAMES,
            Sensors.DRONE_RECORD_READY,
            Sensors.DRONE_RECORDING,
            Sensors.DRONE_USB_ACTIVE,
            Sensors.DRONE_USB_REMAINING_TIME -> Brick.ARDRONE_SUPPORT

            Sensors.NFC_TAG_MESSAGE,
            Sensors.NFC_TAG_ID -> Brick.NFC_ADAPTER

            Sensors.COLLIDES_WITH_EDGE,
            Sensors.COLLIDES_WITH_FINGER -> Brick.COLLISION

            Sensors.GAMEPAD_A_PRESSED,
            Sensors.GAMEPAD_B_PRESSED,
            Sensors.GAMEPAD_DOWN_PRESSED,
            Sensors.GAMEPAD_UP_PRESSED,
            Sensors.GAMEPAD_LEFT_PRESSED,
            Sensors.GAMEPAD_RIGHT_PRESSED -> Brick.CAST_REQUIRED

            Sensors.LOUDNESS -> Brick.MICROPHONE

            Sensors.TEXT_FROM_CAMERA,
            Sensors.TEXT_BLOCKS_NUMBER -> Brick.TEXT_DETECTION

            else -> return
        }.let { resources.add(it) }
    }

    @JvmStatic
    fun addFunctionResources(resources: MutableSet<Int?>, functions: Functions?) {
        when (functions) {
            Functions.ARDUINOANALOG, Functions.ARDUINODIGITAL -> Brick.BLUETOOTH_SENSORS_ARDUINO
            Functions.RASPIDIGITAL -> Brick.SOCKET_RASPI
            else -> return
        }.let { resources.add(it) }
    }
}
