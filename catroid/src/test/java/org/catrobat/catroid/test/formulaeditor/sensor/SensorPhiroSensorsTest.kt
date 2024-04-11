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
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorPhiroBottomLeftTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorPhiroBottomRightTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorPhiroFrontLeftTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorPhiroFrontRightTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorPhiroSideLeftTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorPhiroSideRightTest
import org.catrobat.catroid.formulaeditor.sensor.testutils.BluetoothDeviceServiceTestable
import org.catrobat.catroid.formulaeditor.sensor.testutils.PhiroTestable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SensorPhiroSensorsTest(
    private val name: String,
    private val sensor: Sensor,
    private val returnPhiro: Int,
    private val returnValue: Int,
    private val expectedValue: Int
) {

    private var btService: BluetoothDeviceService = BluetoothDeviceServiceTestable()

    @Before
    fun setup() {
        btService.disconnectDevices()
        if (returnPhiro == PHIRO) {
            prepareSensor(sensor, returnValue)
        }
        when (sensor) {
            is SensorPhiroBottomLeftTest -> sensor.setService(btService)
            is SensorPhiroBottomRightTest -> sensor.setService(btService)
            is SensorPhiroSideLeftTest -> sensor.setService(btService)
            is SensorPhiroSideRightTest -> sensor.setService(btService)
            is SensorPhiroFrontLeftTest -> sensor.setService(btService)
            is SensorPhiroFrontRightTest -> sensor.setService(btService)
        }
    }

    private fun prepareSensor(sensor: Sensor, returnValue: Int) {
        val phiro = PhiroTestable()
        when (sensor) {
            is SensorPhiroBottomLeftTest -> setValue(Sensors.PHIRO_BOTTOM_LEFT, returnValue, phiro)
            is SensorPhiroBottomRightTest -> setValue(Sensors.PHIRO_BOTTOM_RIGHT, returnValue, phiro)
            is SensorPhiroSideLeftTest -> setValue(Sensors.PHIRO_SIDE_LEFT, returnValue, phiro)
            is SensorPhiroSideRightTest -> setValue(Sensors.PHIRO_SIDE_RIGHT, returnValue, phiro)
            is SensorPhiroFrontLeftTest -> setValue(Sensors.PHIRO_FRONT_LEFT, returnValue, phiro)
            is SensorPhiroFrontRightTest -> setValue(Sensors.PHIRO_FRONT_RIGHT, returnValue, phiro)
        }
        btService.deviceConnected(phiro)
    }

    private fun setValue(sensor: Sensors, value: Int, device: PhiroTestable) {
        device.setSensorValue(sensor, value)
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

        const val PHIRO = 1
        private const val NULL = 0

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("phiroBottomLeftNotNull", SensorPhiroBottomLeftTest.getInstance(), PHIRO, 64, 64),
            arrayOf("phiroBottomLeftNull", SensorPhiroBottomLeftTest.getInstance(), NULL, 8, 0),
            arrayOf("phiroBottomRightNotNull", SensorPhiroBottomRightTest.getInstance(), PHIRO, 64, 64),
            arrayOf("phiroBottomRightNull", SensorPhiroBottomRightTest.getInstance(), NULL, 8, 0),
            arrayOf("phiroSideLeftNotNull", SensorPhiroSideLeftTest.getInstance(), PHIRO, 64, 64),
            arrayOf("phiroSideLeftNull", SensorPhiroSideLeftTest.getInstance(), NULL, 8, 0),
            arrayOf("phiroSideRightNotNull", SensorPhiroSideRightTest.getInstance(), PHIRO, 64, 64),
            arrayOf("phiroSideRightNull", SensorPhiroSideRightTest.getInstance(), NULL, 8, 0),
            arrayOf("phiroFrontLeftNotNull", SensorPhiroFrontLeftTest.getInstance(), PHIRO, 64, 64),
            arrayOf("phiroFrontLeftNull", SensorPhiroFrontLeftTest.getInstance(), NULL, 8, 0),
            arrayOf("phiroFrontRightNotNull", SensorPhiroFrontRightTest.getInstance(), PHIRO, 64, 64),
            arrayOf("phiroFrontRightNull", SensorPhiroFrontRightTest.getInstance(), NULL, 8, 0),
        )
    }
}
