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
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorNXTSensor1Test
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorNXTSensor2Test
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorNXTSensor3Test
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorNXTSensor4Test
import org.catrobat.catroid.formulaeditor.sensor.testutils.BluetoothDeviceServiceTestable
import org.catrobat.catroid.formulaeditor.sensor.testutils.LegoNXTTestable
import org.catrobat.catroid.formulaeditor.sensor.testutils.LegoSensorTestable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SensorNXTSensorsTest(
    private val name: String,
    private val sensor: Sensor,
    private val returnNXT: Int,
    private val returnValue: Int?,
    private val expectedValue: Int
) {

    private var btService: BluetoothDeviceService = BluetoothDeviceServiceTestable()

    @Before
    fun setup() {
        btService.disconnectDevices()
        if (returnNXT == NXT) {
            prepareSensor(sensor, returnValue)
        }
        when (sensor) {
            is SensorNXTSensor1Test -> sensor.setService(btService)
            is SensorNXTSensor2Test -> sensor.setService(btService)
            is SensorNXTSensor3Test -> sensor.setService(btService)
            is SensorNXTSensor4Test -> sensor.setService(btService)
        }
    }

    private fun prepareSensor(testSensor: Sensor, returnValue: Int?) {
        val nxt = LegoNXTTestable()

        var sensorToTest: LegoSensorTestable? = null
        if (returnValue != null) {
            sensorToTest = LegoSensorTestable()
            sensorToTest.sensorLastSensorValue = returnValue.toFloat()
        }
        when (testSensor) {
            is SensorNXTSensor1Test -> nxt.setSensor(sensorToTest, 1)
            is SensorNXTSensor2Test -> nxt.setSensor(sensorToTest, 2)
            is SensorNXTSensor3Test -> nxt.setSensor(sensorToTest, 3)
            is SensorNXTSensor4Test -> nxt.setSensor(sensorToTest, 4)
        }
        btService.deviceConnected(nxt)
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

        const val NXT = 1
        private const val NULL = 0

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("NXTSensor1NXTNull", SensorNXTSensor1Test.getInstance(), NULL, 64, 0),
            arrayOf("NXTSensor1SensorNull", SensorNXTSensor1Test.getInstance(), NXT, null, 0),
            arrayOf("NXTSensor1", SensorNXTSensor1Test.getInstance(), NXT, 64, 64),
            arrayOf("NXTSensor2NXTNull", SensorNXTSensor2Test.getInstance(), NULL, 64, 0),
            arrayOf("NXTSensor2SensorNull", SensorNXTSensor2Test.getInstance(), NXT, null, 0),
            arrayOf("NXTSensor2", SensorNXTSensor2Test.getInstance(), NXT, 64, 64),
            arrayOf("NXTSensor3NXTNull", SensorNXTSensor3Test.getInstance(), NULL, 64, 0),
            arrayOf("NXTSensor3SensorNull", SensorNXTSensor3Test.getInstance(), NXT, null, 0),
            arrayOf("NXTSensor3", SensorNXTSensor3Test.getInstance(), NXT, 64, 64),
            arrayOf("NXTSensor4NXTNull", SensorNXTSensor4Test.getInstance(), NULL, 64, 0),
            arrayOf("NXTSensor4SensorNull", SensorNXTSensor4Test.getInstance(), NXT, null, 0),
            arrayOf("NXTSensor4", SensorNXTSensor4Test.getInstance(), NXT, 64, 64),
        )
    }
}
