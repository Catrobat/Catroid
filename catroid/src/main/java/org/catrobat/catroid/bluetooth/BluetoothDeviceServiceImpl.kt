/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.bluetooth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService.ConnectDeviceResult
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import java.util.HashMap

class BluetoothDeviceServiceImpl : BluetoothDeviceService {
    private val connectedDevices: MutableMap<Class<out BluetoothDevice>, BluetoothDevice> =
        HashMap()

    override fun connectDevice(
        deviceToConnect: Class<out BluetoothDevice>,
        activity: Activity, requestCode: Int
    ): ConnectDeviceResult {
        if (isDeviceConnectedAndAlive(deviceToConnect)) {
            return ConnectDeviceResult.ALREADY_CONNECTED
        }
        val intent = createStartIntent(deviceToConnect, activity)
        activity.startActivityForResult(intent, requestCode)
        return ConnectDeviceResult.CONNECTION_REQUESTED
    }

    override fun connectDevice(
        deviceToConnect: Class<out BluetoothDevice>,
        context: Context
    ): ConnectDeviceResult {
        if (isDeviceConnectedAndAlive(deviceToConnect)) {
            return ConnectDeviceResult.ALREADY_CONNECTED
        }
        val intent = createStartIntent(deviceToConnect, context)
        context.startActivity(intent)
        return ConnectDeviceResult.CONNECTION_REQUESTED
    }

    @Synchronized
    private fun isDeviceConnectedAndAlive(deviceToConnect: Class<out BluetoothDevice>): Boolean {
        val device = connectedDevices[deviceToConnect]
        if (device != null) {
            try {
                if (device.isAlive) {
                    device.start()
                    return true
                }
            } catch (e: MindstormsException) {
                Log.e(TAG, e.message)
            }
            device.disconnect()
            connectedDevices.remove(device.deviceType)
        }
        return false
    }

    @Synchronized
    @Throws(MindstormsException::class)
    override fun deviceConnected(device: BluetoothDevice) {
        connectedDevices[device.deviceType] = device
        device.start()
    }

    @Synchronized
    override fun disconnectDevices() {
        for (device in connectedDevices.values) {
            device.disconnect()
        }
        connectedDevices.clear()
    }

    @Synchronized
    override fun <T : BluetoothDevice?> getDevice(btDevice: Class<T>): T? {
        val device = connectedDevices[btDevice]
        return if (device != null) {
            device as T
        } else null
    }

    protected fun createStartIntent(
        deviceToConnect: Class<out BluetoothDevice>?,
        context: Context?
    ): Intent {
        val intent = Intent(context, ConnectBluetoothDeviceActivity::class.java)
        intent.putExtra(ConnectBluetoothDeviceActivity.DEVICE_TO_CONNECT, deviceToConnect)
        return intent
    }

    @Synchronized
    @Throws(MindstormsException::class)
    override fun initialise() {
        for (device in connectedDevices.values) {
            device.initialise()
        }
    }

    @Synchronized
    @Throws(MindstormsException::class)
    override fun start() {
        for (device in connectedDevices.values) {
            device.start()
        }
    }

    @Synchronized
    override fun pause() {
        for (device in connectedDevices.values) {
            device.pause()
        }
    }

    @Synchronized
    override fun destroy() {
        for (device in connectedDevices.values) {
            device.destroy()
        }
    }

    companion object {
        private val TAG = BluetoothDeviceServiceImpl::class.java.simpleName
    }
}