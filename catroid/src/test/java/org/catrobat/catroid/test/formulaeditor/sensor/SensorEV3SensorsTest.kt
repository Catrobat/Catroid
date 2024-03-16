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

import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService
import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorEV3Sensor1Test
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorEV3Sensor2Test
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorEV3Sensor3Test
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorEV3Sensor4Test
import org.catrobat.catroid.formulaeditor.sensor.testutils.BluetoothDeviceServiceTestable
import org.catrobat.catroid.formulaeditor.sensor.testutils.LegoEV3Testable
import org.catrobat.catroid.formulaeditor.sensor.testutils.LegoSensorTestable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SensorEV3SensorsTest(
    private val name: String,
    private val sensor: Sensor,
    private val returnEV3: Int,
    private val returnValue: Int?,
    private val expectedValue: Int
) {

    private var btService: BluetoothDeviceService = BluetoothDeviceServiceTestable()

    @Before
    fun setup() {
        btService.disconnectDevices()
        if (returnEV3 == EV3) {
            prepareSensor(sensor, returnValue)
        }
        when (sensor) {
            is SensorEV3Sensor1Test -> sensor.setService(btService)
            is SensorEV3Sensor2Test -> sensor.setService(btService)
            is SensorEV3Sensor3Test -> sensor.setService(btService)
            is SensorEV3Sensor4Test -> sensor.setService(btService)
        }
    }

    private fun prepareSensor(testSensor: Sensor, returnValue: Int?) {

        val ev3 = LegoEV3Testable()

        var sensorToTest: LegoSensorTestable? = null
        if (returnValue != null) {
            sensorToTest = LegoSensorTestable()
            sensorToTest.sensorLastSensorValue = returnValue.toFloat()
        }
        when (testSensor) {
            is SensorEV3Sensor1Test -> ev3.setSensor(sensorToTest, 1)
            is SensorEV3Sensor2Test -> ev3.setSensor(sensorToTest, 2)
            is SensorEV3Sensor3Test -> ev3.setSensor(sensorToTest, 3)
            is SensorEV3Sensor4Test -> ev3.setSensor(sensorToTest, 4)
        }
        btService.deviceConnected(ev3)
    }

    @Test
    fun objectSensorsTest() {
        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01

        const val EV3 = 1
        private const val NULL = 0

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("EV3Sensor1EV3Null", SensorEV3Sensor1Test.getInstance(), NULL, 64, 0),
            arrayOf("EV3Sensor1SensorNull", SensorEV3Sensor1Test.getInstance(), EV3, null, 0),
            arrayOf("EV3Sensor1", SensorEV3Sensor1Test.getInstance(), EV3, 64, 64),
            arrayOf("EV3Sensor2EV3Null", SensorEV3Sensor2Test.getInstance(), NULL, 64, 0),
            arrayOf("EV3Sensor2SensorNull", SensorEV3Sensor2Test.getInstance(), EV3, null, 0),
            arrayOf("EV3Sensor2", SensorEV3Sensor2Test.getInstance(), EV3, 64, 64),
            arrayOf("EV3Sensor3EV3Null", SensorEV3Sensor3Test.getInstance(), NULL, 64, 0),
            arrayOf("EV3Sensor3SensorNull", SensorEV3Sensor3Test.getInstance(), EV3, null, 0),
            arrayOf("EV3Sensor3", SensorEV3Sensor3Test.getInstance(), EV3, 64, 64),
            arrayOf("EV3Sensor4EV3Null", SensorEV3Sensor4Test.getInstance(), NULL, 64, 0),
            arrayOf("EV3Sensor4SensorNull", SensorEV3Sensor4Test.getInstance(), EV3, null, 0),
            arrayOf("EV3Sensor4", SensorEV3Sensor4Test.getInstance(), EV3, 64, 64),
        )
    }
}
