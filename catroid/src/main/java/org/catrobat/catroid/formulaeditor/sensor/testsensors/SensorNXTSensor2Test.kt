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

package org.catrobat.catroid.formulaeditor.sensor.testsensors

import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService
import org.catrobat.catroid.formulaeditor.sensor.SensorNXTSensor2

class SensorNXTSensor2Test : SensorNXTSensor2() {

    private lateinit var btSvervice: BluetoothDeviceService

    override fun getSensorValue(): Double = super.getNXTSensor2Value(btSvervice)

    fun setService(service: BluetoothDeviceService) {
        btSvervice = service
    }

    companion object {
        @Volatile
        private var instance: SensorNXTSensor2Test? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorNXTSensor2Test().also { instance = it }
            }
    }
}
