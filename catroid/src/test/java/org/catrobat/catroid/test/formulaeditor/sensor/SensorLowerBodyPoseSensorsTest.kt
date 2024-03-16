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
class SensorLowerBodyPoseSensorsTest(
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
            arrayOf("leftHipX1", Sensors.LEFT_HIP_X, 7),
            arrayOf("leftHipX2", Sensors.LEFT_HIP_X, -7),
            arrayOf("leftHipY1", Sensors.LEFT_HIP_Y, 7),
            arrayOf("leftHipY2", Sensors.LEFT_HIP_Y, -7),
            arrayOf("rightHipX1", Sensors.RIGHT_HIP_X, 7),
            arrayOf("rightHipX2", Sensors.RIGHT_HIP_X, -7),
            arrayOf("rightHipY1", Sensors.RIGHT_HIP_Y, 7),
            arrayOf("rightHipY2", Sensors.RIGHT_HIP_Y, -7),
            arrayOf("leftKneeX1", Sensors.LEFT_KNEE_X, 7),
            arrayOf("leftKneeX2", Sensors.LEFT_KNEE_X, -7),
            arrayOf("leftKneeY1", Sensors.LEFT_KNEE_Y, 7),
            arrayOf("leftKneeY2", Sensors.LEFT_KNEE_Y, -7),
            arrayOf("rightKneeX1", Sensors.RIGHT_KNEE_X, 7),
            arrayOf("rightKneeX2", Sensors.RIGHT_KNEE_X, -7),
            arrayOf("rightKneeY1", Sensors.RIGHT_KNEE_Y, 7),
            arrayOf("rightKneeY2", Sensors.RIGHT_KNEE_Y, -7),
            arrayOf("leftAnkleX1", Sensors.LEFT_ANKLE_X, 7),
            arrayOf("leftAnkleX2", Sensors.LEFT_ANKLE_X, -7),
            arrayOf("leftAnkleY1", Sensors.LEFT_ANKLE_Y, 7),
            arrayOf("leftAnkleY2", Sensors.LEFT_ANKLE_Y, -7),
            arrayOf("rightAnkleX1", Sensors.RIGHT_ANKLE_X, 7),
            arrayOf("rightAnkleX2", Sensors.RIGHT_ANKLE_X, -7),
            arrayOf("rightAnkleY1", Sensors.RIGHT_ANKLE_Y, 7),
            arrayOf("rightAnkleY2", Sensors.RIGHT_ANKLE_Y, -7),
            arrayOf("leftHeelX1", Sensors.LEFT_HEEL_X, 7),
            arrayOf("leftHeelX2", Sensors.LEFT_HEEL_X, -7),
            arrayOf("leftHeelY1", Sensors.LEFT_HEEL_Y, 7),
            arrayOf("leftHeelY2", Sensors.LEFT_HEEL_Y, -7),
            arrayOf("rightHeelX1", Sensors.RIGHT_HEEL_X, 7),
            arrayOf("rightHeelX2", Sensors.RIGHT_HEEL_X, -7),
            arrayOf("rightHeelY1", Sensors.RIGHT_HEEL_Y, 7),
            arrayOf("rightHeelY2", Sensors.RIGHT_HEEL_Y, -7),
            arrayOf("leftFootIndexX1", Sensors.LEFT_FOOT_INDEX_X, 7),
            arrayOf("leftFootIndexX2", Sensors.LEFT_FOOT_INDEX_X, -7),
            arrayOf("leftFootIndexY1", Sensors.LEFT_FOOT_INDEX_Y, 7),
            arrayOf("leftFootIndexY2", Sensors.LEFT_FOOT_INDEX_Y, -7),
            arrayOf("rightFootIndexX1", Sensors.RIGHT_FOOT_INDEX_X, 7),
            arrayOf("rightFootIndexX2", Sensors.RIGHT_FOOT_INDEX_X, -7),
            arrayOf("rightFootIndexY1", Sensors.RIGHT_FOOT_INDEX_Y, 7),
            arrayOf("rightFootIndexY2", Sensors.RIGHT_FOOT_INDEX_Y, -7),
        )
    }
}
