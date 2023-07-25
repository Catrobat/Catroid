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
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT
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
class SensorNXTSensorsTest(
    private val name: String,
    private val sensor: Sensors,
    private val returnNXT: Int,
    private val returnValue: Int?,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        PowerMockito.mockStatic(ServiceProvider::class.java)
        PowerMockito.`when`(ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE))
            .thenReturn(mockBlueToothService)
        if (returnNXT == NXT) {
            mockNXT = mock(LegoNXT::class.java)
            prepareSensor(sensor, returnValue)
        } else {
            mockNXT = null
        }
        PowerMockito.`when`(mockBlueToothService.getDevice(BluetoothDevice.LEGO_NXT))
            .thenReturn(mockNXT)
    }

    private fun prepareSensor(testSensor: Sensors, returnValue: Int?) {

        var sensor: LegoSensor? = null
        if (returnValue != null) {
            PowerMockito.`when`(mockLegoSensor.lastSensorValue).thenReturn(returnValue.toFloat())
            sensor = mockLegoSensor
        }
        when (testSensor) {
            Sensors.NXT_SENSOR_1 -> PowerMockito.`when`(mockNXT?.sensor1).thenReturn(sensor)
            Sensors.NXT_SENSOR_2 -> PowerMockito.`when`(mockNXT?.sensor2).thenReturn(sensor)
            Sensors.NXT_SENSOR_3 -> PowerMockito.`when`(mockNXT?.sensor3).thenReturn(sensor)
            Sensors.NXT_SENSOR_4 -> PowerMockito.`when`(mockNXT?.sensor4).thenReturn(sensor)
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
        var mockNXT: LegoNXT? = null
        var mockLegoSensor: LegoSensor = mock(LegoSensor::class.java)

        private const val DELTA = 0.01

        const val NXT = 1
        private const val NULL = 0

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("NXTSensor1NXTNull", Sensors.NXT_SENSOR_1, NULL, 64, 0),
            arrayOf("NXTSensor1SensorNull", Sensors.NXT_SENSOR_1, NXT, null, 0),
            arrayOf("NXTSensor1", Sensors.NXT_SENSOR_1, NXT, 64, 64),
            arrayOf("NXTSensor2NXTNull", Sensors.NXT_SENSOR_2, NULL, 64, 0),
            arrayOf("NXTSensor2SensorNull", Sensors.NXT_SENSOR_2, NXT, null, 0),
            arrayOf("NXTSensor2", Sensors.NXT_SENSOR_2, NXT, 64, 64),
            arrayOf("NXTSensor3NXTNull", Sensors.NXT_SENSOR_3, NULL, 64, 0),
            arrayOf("NXTSensor3SensorNull", Sensors.NXT_SENSOR_3, NXT, null, 0),
            arrayOf("NXTSensor3", Sensors.NXT_SENSOR_3, NXT, 64, 64),
            arrayOf("NXTSensor4NXTNull", Sensors.NXT_SENSOR_4, NULL, 64, 0),
            arrayOf("NXTSensor4SensorNull", Sensors.NXT_SENSOR_4, NXT, null, 0),
            arrayOf("NXTSensor4", Sensors.NXT_SENSOR_4, NXT, 64, 64),
        )
    }
}
