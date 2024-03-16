/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.formulaeditor.sensor

import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SensorUpperBodyPoseSensorsTest(
    private val name: String,
    private val sensor: Sensors,
    private val expectedValue: Int
) {

    @Test
    fun faceSensorTest() {
        sensor.getSensor().updateSensorValue(expectedValue.toDouble())
        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("leftShoulderX1", Sensors.LEFT_SHOULDER_X, 7),
            arrayOf("leftShoulderX2", Sensors.LEFT_SHOULDER_X, -7),
            arrayOf("leftShoulderY1", Sensors.LEFT_SHOULDER_Y, 7),
            arrayOf("leftShoulderY2", Sensors.LEFT_SHOULDER_Y, -7),
            arrayOf("rightShoulderX1", Sensors.RIGHT_SHOULDER_X, 7),
            arrayOf("rightShoulderX2", Sensors.RIGHT_SHOULDER_X, -7),
            arrayOf("rightShoulderY1", Sensors.RIGHT_SHOULDER_Y, 7),
            arrayOf("rightShoulderY2", Sensors.RIGHT_SHOULDER_Y, -7),
            arrayOf("leftWristX1", Sensors.LEFT_WRIST_X, 7),
            arrayOf("leftWristX2", Sensors.LEFT_WRIST_X, -7),
            arrayOf("leftWristY1", Sensors.LEFT_WRIST_Y, 7),
            arrayOf("leftWristX2", Sensors.LEFT_WRIST_Y, -7),
            arrayOf("rightWristX1", Sensors.RIGHT_WRIST_X, 7),
            arrayOf("rightWristX2", Sensors.RIGHT_WRIST_X, -7),
            arrayOf("rightWristY1", Sensors.RIGHT_WRIST_Y, 7),
            arrayOf("rightWristX2", Sensors.RIGHT_WRIST_Y, -7),
            arrayOf("leftPinkyX1", Sensors.LEFT_PINKY_X, 7),
            arrayOf("leftPinkyX2", Sensors.LEFT_PINKY_X, -7),
            arrayOf("leftPinkyY1", Sensors.LEFT_PINKY_Y, 7),
            arrayOf("leftPinkyY2", Sensors.LEFT_PINKY_Y, -7),
            arrayOf("rightPinkyX1", Sensors.RIGHT_PINKY_X, 7),
            arrayOf("rightPinkyX2", Sensors.RIGHT_PINKY_X, -7),
            arrayOf("rightPinkyY1", Sensors.RIGHT_PINKY_Y, 7),
            arrayOf("rightPinkyY2", Sensors.RIGHT_PINKY_Y, -7),
            arrayOf("leftIndexX1", Sensors.LEFT_INDEX_X, 7),
            arrayOf("leftIndexX2", Sensors.LEFT_INDEX_X, -7),
            arrayOf("leftIndexY1", Sensors.LEFT_INDEX_Y, 7),
            arrayOf("leftIndexY2", Sensors.LEFT_INDEX_Y, -7),
            arrayOf("rightIndexX1", Sensors.RIGHT_INDEX_X, 7),
            arrayOf("rightIndexX2", Sensors.RIGHT_INDEX_X, -7),
            arrayOf("rightIndexY1", Sensors.RIGHT_INDEX_Y, 7),
            arrayOf("rightIndexY2", Sensors.RIGHT_INDEX_Y, -7),
            arrayOf("leftThumbX1", Sensors.LEFT_THUMB_X, 7),
            arrayOf("leftThumbX2", Sensors.LEFT_THUMB_X, -7),
            arrayOf("leftThumbY1", Sensors.LEFT_THUMB_Y, 7),
            arrayOf("leftThumbY2", Sensors.LEFT_THUMB_Y, -7),
            arrayOf("rightThumbX1", Sensors.RIGHT_THUMB_X, 7),
            arrayOf("rightThumbX2", Sensors.RIGHT_THUMB_X, -7),
            arrayOf("rightThumbY1", Sensors.RIGHT_THUMB_Y, 7),
            arrayOf("rightThumbY2", Sensors.RIGHT_THUMB_Y, -7),
            arrayOf("neckX1", Sensors.NECK_X, 7),
            arrayOf("neckX2", Sensors.NECK_X, -7),
            arrayOf("neckY1", Sensors.NECK_Y, 7),
            arrayOf("neckY2", Sensors.NECK_Y, -7),
            arrayOf("leftElbowX1", Sensors.LEFT_ELBOW_X, 7),
            arrayOf("leftElbowX2", Sensors.LEFT_ELBOW_X, -7),
            arrayOf("leftElbowY1", Sensors.LEFT_ELBOW_Y, 7),
            arrayOf("leftElbowY2", Sensors.LEFT_ELBOW_Y, -7),
            arrayOf("rightElbowX1", Sensors.RIGHT_ELBOW_X, 7),
            arrayOf("rightElbowX2", Sensors.RIGHT_ELBOW_X, -7),
            arrayOf("rightElbowY1", Sensors.RIGHT_ELBOW_Y, 7),
            arrayOf("rightElbowY2", Sensors.RIGHT_ELBOW_Y, -7),

            )
    }
}
