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
import org.catrobat.catroid.devices.arduino.phiro.Phiro
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
class SensorPhiroSensorsTest(
    private val name: String,
    private val sensor: Sensors,
    private val returnPhiro: Int,
    private val returnValue: Int,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        PowerMockito.mockStatic(ServiceProvider::class.java)
        PowerMockito.`when`(ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE))
            .thenReturn(mockBlueToothService)
        if (returnPhiro == PHIRO) {
            mockPhiro = mock(Phiro::class.java)
            PowerMockito.`when`(mockPhiro?.getSensorValue(sensor)).thenReturn(returnValue)
        } else {
            mockPhiro = null
        }
        PowerMockito.`when`(mockBlueToothService.getDevice(BluetoothDevice.PHIRO))
            .thenReturn(mockPhiro)
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
        var mockPhiro: Phiro? = null

        private const val DELTA = 0.01

        const val PHIRO = 1
        private const val NULL = 0

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("phiroBottomLeftNotNull", Sensors.PHIRO_BOTTOM_LEFT, PHIRO, 64, 64),
            arrayOf("phiroBottomLeftNull", Sensors.PHIRO_BOTTOM_LEFT, NULL, 8, 0),
            arrayOf("phiroBottomRightNotNull", Sensors.PHIRO_BOTTOM_RIGHT, PHIRO, 64, 64),
            arrayOf("phiroBottomRightNull", Sensors.PHIRO_BOTTOM_RIGHT, NULL, 8, 0),
            arrayOf("phiroSideLeftNotNull", Sensors.PHIRO_SIDE_LEFT, PHIRO, 64, 64),
            arrayOf("phiroSideLeftNull", Sensors.PHIRO_SIDE_LEFT, NULL, 8, 0),
            arrayOf("phiroSideRightNotNull", Sensors.PHIRO_SIDE_RIGHT, PHIRO, 64, 64),
            arrayOf("phiroSideRightNull", Sensors.PHIRO_SIDE_RIGHT, NULL, 8, 0),
            arrayOf("phiroFrontLeftNotNull", Sensors.PHIRO_FRONT_LEFT, PHIRO, 64, 64),
            arrayOf("phiroFrontLeftNull", Sensors.PHIRO_FRONT_LEFT, NULL, 8, 0),
            arrayOf("phiroFrontRightNotNull", Sensors.PHIRO_FRONT_RIGHT, PHIRO, 64, 64),
            arrayOf("phiroFrontRightNull", Sensors.PHIRO_FRONT_RIGHT, NULL, 8, 0),
        )
    }
}
