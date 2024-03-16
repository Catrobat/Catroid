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

import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.SensorAccelerationZ
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorAccelerationXTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorAccelerationYTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SensorAccelerationSensorsTest(
    private val name: String,
    private val sensor: Sensor,
    private val rotation: Int,
    private val accelerationValue: Int,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        SensorHandler.linearAcceleration.accelerationX = accelerationValue.toDouble()
        SensorHandler.linearAcceleration.accelerationY = accelerationValue.toDouble()
        SensorHandler.linearAcceleration.accelerationZ = accelerationValue.toDouble()
        if (sensor is SensorAccelerationXTest) {
            sensor.setRotation(rotation)
        } else if (sensor is SensorAccelerationYTest) {
            sensor.setRotation(rotation)
        }
    }

    @Test
    fun accelerationSensorText() {
        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {

        private const val DELTA = 0.01

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("accelerationZPos", SensorAccelerationZ.getInstance(), 0, 64, 64),
            arrayOf("accelerationZNeg", SensorAccelerationZ.getInstance(), 0, -63, -63),
            arrayOf("accelerationXPos", SensorAccelerationXTest.getInstance(), 0, 62, 62),
            arrayOf("accelerationXNeg", SensorAccelerationXTest.getInstance(), 0, -61, -61),
            arrayOf("accelerationXPosRotPos", SensorAccelerationXTest.getInstance(), 1, 60, -60),
            arrayOf("accelerationXNegRotPos", SensorAccelerationXTest.getInstance(), 1, -59, 59),
            arrayOf("accelerationXNegRotNeg", SensorAccelerationXTest.getInstance(), -1, 58, 58),
            arrayOf("accelerationXNegRotNeg", SensorAccelerationXTest.getInstance(), -1, -57, -57),
            arrayOf("accelerationYPos", SensorAccelerationYTest.getInstance(), 0, 56, 56),
            arrayOf("accelerationYNeg", SensorAccelerationYTest.getInstance(), 0, -57, -57),
            arrayOf("accelerationYPosRotPos", SensorAccelerationYTest.getInstance(), 1, 56, 56),
            arrayOf("accelerationYNegRotPos", SensorAccelerationYTest.getInstance(), 1, -55, -55),
            arrayOf("accelerationYNegRotNeg", SensorAccelerationYTest.getInstance(), -1, 54, -54),
            arrayOf("accelerationYNegRotNeg", SensorAccelerationYTest.getInstance(), -1, -53, 53),
        )
    }
}
