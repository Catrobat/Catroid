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

import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.devices.mindstorms.LegoSensor
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(ServiceProvider::class, BluetoothDeviceService::class)
class SensorEV3SensorsTest(
    private val name: String,
    private val sensor: Sensors,
    private val returnEV3: Int,
    private val returnValue: Int?,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        PowerMockito.mockStatic(ServiceProvider::class.java)
        PowerMockito.`when`(ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE))
            .thenReturn(mockBlueToothService)
        if (returnEV3 == EV3) {
            mockEV3 = mock(LegoEV3::class.java)
            prepareSensor(sensor, returnValue)
        } else {
            mockEV3 = null
        }
        PowerMockito.`when`(mockBlueToothService.getDevice(BluetoothDevice.LEGO_EV3))
            .thenReturn(mockEV3)
    }

    private fun prepareSensor(testSensor: Sensors, returnValue: Int?) {

        var sensor: LegoSensor? = null
        if (returnValue != null) {
            PowerMockito.`when`(mockLegoSensor.lastSensorValue).thenReturn(returnValue.toFloat())
            sensor = mockLegoSensor
        }
        when (testSensor) {
            Sensors.EV3_SENSOR_1 -> PowerMockito.`when`(mockEV3?.sensor1).thenReturn(sensor)
            Sensors.EV3_SENSOR_2 -> PowerMockito.`when`(mockEV3?.sensor2).thenReturn(sensor)
            Sensors.EV3_SENSOR_3 -> PowerMockito.`when`(mockEV3?.sensor3).thenReturn(sensor)
            Sensors.EV3_SENSOR_4 -> PowerMockito.`when`(mockEV3?.sensor4).thenReturn(sensor)
        }
    }

    @Test
    fun objectSensorsTest() {
        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {

        private var mockBlueToothService: BluetoothDeviceService =
            mock(BluetoothDeviceService::class.java)
        var mockEV3: LegoEV3? = null
        var mockLegoSensor: LegoSensor = mock(LegoSensor::class.java)

        private const val DELTA = 0.01

        const val EV3 = 1
        private const val NULL = 0

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("EV3Sensor1EV3Null", Sensors.EV3_SENSOR_1, NULL, 64, 0),
            arrayOf("EV3Sensor1SensorNull", Sensors.EV3_SENSOR_1, EV3, null, 0),
            arrayOf("EV3Sensor1", Sensors.EV3_SENSOR_1, EV3, 64, 64),
            arrayOf("EV3Sensor2EV3Null", Sensors.EV3_SENSOR_2, NULL, 64, 0),
            arrayOf("EV3Sensor2SensorNull", Sensors.EV3_SENSOR_2, EV3, null, 0),
            arrayOf("EV3Sensor2", Sensors.EV3_SENSOR_2, EV3, 64, 64),
            arrayOf("EV3Sensor3EV3Null", Sensors.EV3_SENSOR_3, NULL, 64, 0),
            arrayOf("EV3Sensor3SensorNull", Sensors.EV3_SENSOR_3, EV3, null, 0),
            arrayOf("EV3Sensor3", Sensors.EV3_SENSOR_3, EV3, 64, 64),
            arrayOf("EV3Sensor4EV3Null", Sensors.EV3_SENSOR_4, NULL, 64, 0),
            arrayOf("EV3Sensor4SensorNull", Sensors.EV3_SENSOR_4, EV3, null, 0),
            arrayOf("EV3Sensor4", Sensors.EV3_SENSOR_4, EV3, 64, 64),
        )
    }
}
