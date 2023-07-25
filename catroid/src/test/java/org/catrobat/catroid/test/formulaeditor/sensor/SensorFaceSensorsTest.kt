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
class SensorFaceSensorsTest(
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
            arrayOf("faceDetected", Sensors.FACE_DETECTED, 0),
            arrayOf("faceNotDetected", Sensors.FACE_DETECTED, 1),
            arrayOf("faceX1", Sensors.FACE_X, 7),
            arrayOf("faceX2", Sensors.FACE_X, -7),
            arrayOf("faceY1", Sensors.FACE_Y, 7),
            arrayOf("faceY2", Sensors.FACE_Y, -7),
            arrayOf("faceSize", Sensors.FACE_SIZE, 67),
            arrayOf("secondFaceDetected", Sensors.SECOND_FACE_DETECTED, 0),
            arrayOf("secondFaceNotDetected", Sensors.SECOND_FACE_DETECTED, 1),
            arrayOf("secondFaceX1", Sensors.SECOND_FACE_X, 7),
            arrayOf("secondFaceX2", Sensors.SECOND_FACE_X, -7),
            arrayOf("secondFaceY1", Sensors.SECOND_FACE_Y, 7),
            arrayOf("secondFaceY2", Sensors.SECOND_FACE_Y, -7),
            arrayOf("secondFaceSize", Sensors.SECOND_FACE_SIZE, 67),
        )
    }
}
