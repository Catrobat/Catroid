/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
        addDeviceSensorsResources(resources, sensor)
        addExtensionSensorsResources(resources, sensor)
        addAIExtensionSensorsResources(resources, sensor)
        addCollisionSensorsResources(resources, sensor)
    }

    @JvmStatic
    private fun addAIExtensionSensorsResources(resources: MutableSet<Int?>, sensor: Sensors?) {
        addPoseDetectionSensorsResources(resources, sensor)
        when (sensor) {
            Sensors.FACE_DETECTED,
            Sensors.FACE_SIZE,
            Sensors.FACE_X,
            Sensors.FACE_Y,
            Sensors.SECOND_FACE_DETECTED,
            Sensors.SECOND_FACE_SIZE,
            Sensors.SECOND_FACE_X,
            Sensors.SECOND_FACE_Y -> Brick.FACE_DETECTION

            Sensors.TEXT_FROM_CAMERA,
            Sensors.TEXT_BLOCKS_NUMBER,
            Sensors.TEXT_BLOCK_X,
            Sensors.TEXT_BLOCK_Y,
            Sensors.TEXT_BLOCK_SIZE,
            Sensors.TEXT_BLOCK_FROM_CAMERA,
            Sensors.TEXT_BLOCK_LANGUAGE_FROM_CAMERA -> Brick.TEXT_DETECTION

            Sensors.SPEECH_RECOGNITION_LANGUAGE -> Brick.SPEECH_RECOGNITION

            else -> return
        }.let { resources.add(it) }
    }

    @JvmStatic
    private fun addAIExtensionFunctionsResources(resources: MutableSet<Int?>, function: Functions?) {
        when (function) {
            Functions.ID_OF_DETECTED_OBJECT,
            Functions.OBJECT_WITH_ID_VISIBLE -> Brick.OBJECT_DETECTION
            else -> return
        }.let { resources.add(it) }
    }

    @JvmStatic
    private fun addPoseDetectionSensorsResources(resources: MutableSet<Int?>, sensor: Sensors?) {
        when (sensor) {
            Sensors.HEAD_TOP_X,
            Sensors.HEAD_TOP_Y,
            Sensors.NECK_X,
            Sensors.NECK_Y,
            Sensors.NOSE_X,
            Sensors.NOSE_Y,
            Sensors.LEFT_EYE_INNER_X,
            Sensors.LEFT_EYE_INNER_Y,
            Sensors.LEFT_EYE_CENTER_X,
            Sensors.LEFT_EYE_CENTER_Y,
            Sensors.LEFT_EYE_OUTER_X,
            Sensors.LEFT_EYE_OUTER_Y,
            Sensors.RIGHT_EYE_INNER_X,
            Sensors.RIGHT_EYE_INNER_Y,
            Sensors.RIGHT_EYE_CENTER_X,
            Sensors.RIGHT_EYE_CENTER_Y,
            Sensors.RIGHT_EYE_OUTER_X,
            Sensors.RIGHT_EYE_OUTER_Y,
            Sensors.LEFT_EAR_X,
            Sensors.LEFT_EAR_Y,
            Sensors.RIGHT_EAR_X,
            Sensors.RIGHT_EAR_Y,
            Sensors.MOUTH_LEFT_CORNER_X,
            Sensors.MOUTH_LEFT_CORNER_Y,
            Sensors.MOUTH_RIGHT_CORNER_X,
            Sensors.MOUTH_RIGHT_CORNER_Y,
            Sensors.LEFT_SHOULDER_X,
            Sensors.LEFT_SHOULDER_Y,
            Sensors.RIGHT_SHOULDER_X,
            Sensors.RIGHT_SHOULDER_Y,
            Sensors.LEFT_ELBOW_X,
            Sensors.LEFT_ELBOW_Y,
            Sensors.RIGHT_ELBOW_X,
            Sensors.RIGHT_ELBOW_Y,
            Sensors.LEFT_WRIST_X,
            Sensors.LEFT_WRIST_Y,
            Sensors.RIGHT_WRIST_X,
            Sensors.RIGHT_WRIST_Y,
            Sensors.LEFT_PINKY_X,
            Sensors.LEFT_PINKY_Y,
            Sensors.RIGHT_PINKY_X,
            Sensors.RIGHT_PINKY_Y,
            Sensors.LEFT_INDEX_X,
            Sensors.LEFT_INDEX_Y,
            Sensors.RIGHT_INDEX_X,
            Sensors.RIGHT_INDEX_Y,
            Sensors.LEFT_THUMB_X,
            Sensors.LEFT_THUMB_Y,
            Sensors.RIGHT_THUMB_X,
            Sensors.RIGHT_THUMB_Y,
            Sensors.LEFT_HIP_X,
            Sensors.LEFT_HIP_Y,
            Sensors.RIGHT_HIP_X,
            Sensors.RIGHT_HIP_Y,
            Sensors.LEFT_KNEE_X,
            Sensors.LEFT_KNEE_Y,
            Sensors.RIGHT_KNEE_X,
            Sensors.RIGHT_KNEE_Y,
            Sensors.LEFT_ANKLE_X,
            Sensors.LEFT_ANKLE_Y,
            Sensors.RIGHT_ANKLE_X,
            Sensors.RIGHT_ANKLE_Y,
            Sensors.LEFT_HEEL_X,
            Sensors.LEFT_HEEL_Y,
            Sensors.RIGHT_HEEL_X,
            Sensors.RIGHT_HEEL_Y,
            Sensors.LEFT_FOOT_INDEX_X,
            Sensors.LEFT_FOOT_INDEX_Y,
            Sensors.RIGHT_FOOT_INDEX_X,
            Sensors.RIGHT_FOOT_INDEX_Y -> Brick.POSE_DETECTION

            else -> return
        }.let { resources.add(it) }
    }

    @JvmStatic
    private fun addDeviceSensorsResources(resources: MutableSet<Int?>, sensor: Sensors?) {
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

            Sensors.NFC_TAG_MESSAGE,
            Sensors.NFC_TAG_ID -> Brick.NFC_ADAPTER

            Sensors.LOUDNESS -> Brick.MICROPHONE
            else -> return
        }.let { resources.add(it) }
    }

    @JvmStatic
    private fun addExtensionSensorsResources(resources: MutableSet<Int?>, sensor: Sensors?) {
        when (sensor) {
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

            Sensors.GAMEPAD_A_PRESSED,
            Sensors.GAMEPAD_B_PRESSED,
            Sensors.GAMEPAD_DOWN_PRESSED,
            Sensors.GAMEPAD_UP_PRESSED,
            Sensors.GAMEPAD_LEFT_PRESSED,
            Sensors.GAMEPAD_RIGHT_PRESSED -> Brick.CAST_REQUIRED
            else -> return
        }.let { resources.add(it) }
    }

    @JvmStatic
    private fun addCollisionSensorsResources(resources: MutableSet<Int?>, sensor: Sensors?) {
        when (sensor) {
            Sensors.COLLIDES_WITH_EDGE,
            Sensors.COLLIDES_WITH_FINGER -> Brick.COLLISION

            else -> return
        }.let { resources.add(it) }
    }

    @JvmStatic
    fun addFunctionResources(resources: MutableSet<Int?>, functions: Functions?) {
        addAIExtensionFunctionsResources(resources, functions)
        when (functions) {
            Functions.ARDUINOANALOG, Functions.ARDUINODIGITAL -> Brick.BLUETOOTH_SENSORS_ARDUINO
            Functions.RASPIDIGITAL -> Brick.SOCKET_RASPI
            else -> return
        }.let { resources.add(it) }
    }
}
