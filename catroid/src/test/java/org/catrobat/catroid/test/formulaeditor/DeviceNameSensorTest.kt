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

package org.catrobat.catroid.test.formulaeditor

import android.os.Build
import org.catrobat.catroid.formulaeditor.SensorHandlerDevice
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Test
import org.robolectric.util.ReflectionHelpers

class DeviceNameSensorTest {

    @Test
    fun testDeviceNameFull() {
        val manufacturer = "Company"
        val product = "smartphone 1000"
        val expectedResult = "$manufacturer $product"
        ReflectionHelpers.setStaticField(Build::class.java, "MANUFACTURER", manufacturer)
        ReflectionHelpers.setStaticField(Build::class.java, "PRODUCT", product)
        compareToSensor(expectedResult, Sensors.DEVICE_MODEL)
    }

    @Test
    fun testDeviceNameMissingManufacturer() {
        val manufacturer = "Company"
        val product = null
        val expectedResult = "$manufacturer Unknown Product"
        ReflectionHelpers.setStaticField(Build::class.java, "MANUFACTURER", manufacturer)
        ReflectionHelpers.setStaticField(Build::class.java, "PRODUCT", product)
        compareToSensor(expectedResult, Sensors.DEVICE_MODEL)
    }

    @Test
    fun testDeviceNameMissingProductName() {
        val manufacturer = null
        val product = "smartphone 1000"
        val expectedResult = "Unknown Manufacturer $product"
        ReflectionHelpers.setStaticField(Build::class.java, "MANUFACTURER", manufacturer)
        ReflectionHelpers.setStaticField(Build::class.java, "PRODUCT", product)
        compareToSensor(expectedResult, Sensors.DEVICE_MODEL)
    }

    @Test
    fun testDeviceNameMissingBoth() {
        val manufacturer = null
        val product = null
        val expectedResult = "Unknown Manufacturer Unknown Product"
        ReflectionHelpers.setStaticField(Build::class.java, "MANUFACTURER", manufacturer)
        ReflectionHelpers.setStaticField(Build::class.java, "PRODUCT", product)
        compareToSensor(expectedResult, Sensors.DEVICE_MODEL)
    }

    private fun compareToSensor(value: String, sensor: Sensors) {
        org.junit.Assert.assertEquals(
            value,
            SensorHandlerDevice.getInstance().getSensorValue(sensor)
        )
    }
}
