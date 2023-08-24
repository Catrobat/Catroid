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

package org.catrobat.catroid.formulaeditor.sensor.testutils

import android.app.Activity
import android.content.Context
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService

class BluetoothDeviceServiceTestable : BluetoothDeviceService {

    var serviceConnectDeviceReturnValue = BluetoothDeviceService.ConnectDeviceResult.ALREADY_CONNECTED

    private val connectedDevices: Map<Class<out BluetoothDevice>, BluetoothDevice> = HashMap()

    fun setConnectDeviceReturnValue(newValue: BluetoothDeviceService.ConnectDeviceResult) {
        serviceConnectDeviceReturnValue = newValue
    }

    override fun connectDevice(
        deviceType: Class<out BluetoothDevice>?,
        activity: Activity?,
        requestCode: Int
    ): BluetoothDeviceService.ConnectDeviceResult {
        return serviceConnectDeviceReturnValue
    }

    override fun connectDevice(
        deviceToConnect: Class<out BluetoothDevice>?,
        context: Context?
    ): BluetoothDeviceService.ConnectDeviceResult {
        return serviceConnectDeviceReturnValue
    }

    override fun initialise() {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun deviceConnected(device: BluetoothDevice?) {}

    override fun disconnectDevices() {}
    override fun <T : BluetoothDevice?> getDevice(btDevice: Class<T>?): T {
        TODO("Not yet implemented")
    }
}