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
class SensorHeadPoseSensorsTest(
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
            arrayOf("noseX1", Sensors.NOSE_X, 7),
            arrayOf("noseX2", Sensors.NOSE_X, -7),
            arrayOf("noseY1", Sensors.NOSE_Y, 7),
            arrayOf("noseY2", Sensors.NOSE_Y, -7),
            arrayOf("leftEyeInnerX1", Sensors.LEFT_EYE_INNER_X, 7),
            arrayOf("leftEyeInnerX2", Sensors.LEFT_EYE_INNER_X, -7),
            arrayOf("leftEyeInnerY1", Sensors.LEFT_EYE_INNER_Y, 7),
            arrayOf("leftEyeInnerY2", Sensors.LEFT_EYE_INNER_Y, -7),
            arrayOf("leftEyeCenterX1", Sensors.LEFT_EYE_CENTER_X, 7),
            arrayOf("leftEyeCenterX2", Sensors.LEFT_EYE_CENTER_X, -7),
            arrayOf("leftEyeCenterY1", Sensors.LEFT_EYE_CENTER_Y, 7),
            arrayOf("leftEyeCenterY2", Sensors.LEFT_EYE_CENTER_Y, -7),
            arrayOf("leftEyeOuterX1", Sensors.LEFT_EYE_OUTER_X, 7),
            arrayOf("leftEyeOuterX2", Sensors.LEFT_EYE_OUTER_X, -7),
            arrayOf("leftEyeOuterY1", Sensors.LEFT_EYE_OUTER_Y, 7),
            arrayOf("leftEyeOuterY2", Sensors.LEFT_EYE_OUTER_Y, -7),
            arrayOf("rightEyeInnerX1", Sensors.RIGHT_EYE_INNER_X, 7),
            arrayOf("rightEyeInnerX2", Sensors.RIGHT_EYE_INNER_X, -7),
            arrayOf("rightEyeInnerY1", Sensors.RIGHT_EYE_INNER_Y, 7),
            arrayOf("rightEyeInnerY2", Sensors.RIGHT_EYE_INNER_Y, -7),
            arrayOf("rightEyeCenterX1", Sensors.RIGHT_EYE_CENTER_X, 7),
            arrayOf("rightEyeCenterX2", Sensors.RIGHT_EYE_CENTER_X, -7),
            arrayOf("rightEyeCenterY1", Sensors.RIGHT_EYE_CENTER_Y, 7),
            arrayOf("rightEyeCenterY2", Sensors.RIGHT_EYE_CENTER_Y, -7),
            arrayOf("rightEyeOuterX1", Sensors.RIGHT_EYE_OUTER_X, 7),
            arrayOf("rightEyeOuterX2", Sensors.RIGHT_EYE_OUTER_X, -7),
            arrayOf("rightEyeOuterY1", Sensors.RIGHT_EYE_OUTER_Y, 7),
            arrayOf("rightEyeOuterY2", Sensors.RIGHT_EYE_OUTER_Y, -7),
            arrayOf("leftEarX1", Sensors.LEFT_EAR_X, 7),
            arrayOf("leftEarX2", Sensors.LEFT_EAR_X, -7),
            arrayOf("leftEarY1", Sensors.LEFT_EAR_Y, 7),
            arrayOf("leftEarY2", Sensors.LEFT_EAR_Y, -7),
            arrayOf("rightEarX1", Sensors.RIGHT_EAR_X, 7),
            arrayOf("rightEarX2", Sensors.RIGHT_EAR_X, -7),
            arrayOf("rightEarY1", Sensors.RIGHT_EAR_Y, 7),
            arrayOf("rightEarY2", Sensors.RIGHT_EAR_Y, -7),
            arrayOf("leftMouthCornerX1", Sensors.MOUTH_LEFT_CORNER_X, 7),
            arrayOf("leftMouthCornerX2", Sensors.MOUTH_LEFT_CORNER_X, -7),
            arrayOf("leftMouthCornerY1", Sensors.MOUTH_LEFT_CORNER_Y, 7),
            arrayOf("leftMouthCornerY2", Sensors.MOUTH_LEFT_CORNER_Y, -7),
            arrayOf("rightMouthCornerX1", Sensors.MOUTH_RIGHT_CORNER_X, 7),
            arrayOf("rightMouthCornerX2", Sensors.MOUTH_RIGHT_CORNER_X, -7),
            arrayOf("rightMouthCornerY1", Sensors.MOUTH_RIGHT_CORNER_Y, 7),
            arrayOf("rightMouthCornerY2", Sensors.MOUTH_RIGHT_CORNER_Y, -7),
            arrayOf("headTopX1", Sensors.HEAD_TOP_X, 7),
            arrayOf("headTopX1", Sensors.HEAD_TOP_X, -7),
            arrayOf("headTopY1", Sensors.HEAD_TOP_Y, 7),
            arrayOf("headTopY1", Sensors.HEAD_TOP_Y, -7),
        )
    }
}
