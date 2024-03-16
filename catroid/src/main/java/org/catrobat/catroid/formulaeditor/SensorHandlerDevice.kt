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

package org.catrobat.catroid.formulaeditor

import android.os.Build
import org.catrobat.catroid.formulaeditor.common.Conversions

class SensorHandlerDevice {

    companion object {
        private val instance: SensorHandlerDevice = SensorHandlerDevice()
        fun getInstance(): SensorHandlerDevice = instance
    }

    fun getSensorValue(sensor: Sensors): Any {
        return when (sensor) {
            Sensors.DEVICE_MODEL -> getDeviceName()
            else -> Conversions.FALSE
        }
    }

    private fun getDeviceName(): String {
        var manufacturer = Build.MANUFACTURER
        var product = Build.PRODUCT
        if (manufacturer == null) {
            manufacturer = "Unknown Manufacturer"
        }
        if (product == null) {
            product = "Unknown Product"
        }
        return "$manufacturer $product"
    }
}
