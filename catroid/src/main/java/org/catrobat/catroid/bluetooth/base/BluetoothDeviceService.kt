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
package org.catrobat.catroid.bluetooth.base

import android.app.Activity
import android.content.Context
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.stage.StageResourceInterface

interface BluetoothDeviceService : CatroidService, StageResourceInterface {
    enum class ConnectDeviceResult {
        ALREADY_CONNECTED, CONNECTION_REQUESTED
    }

    fun connectDevice(
        deviceType: Class<out BluetoothDevice?>?,
        activity: Activity?, requestCode: Int
    ): ConnectDeviceResult?

    fun connectDevice(
        deviceToConnect: Class<out BluetoothDevice?>?,
        context: Context?
    ): ConnectDeviceResult?

    @Throws(MindstormsException::class)
    fun deviceConnected(device: BluetoothDevice?)
    fun disconnectDevices()
    fun <T : BluetoothDevice?> getDevice(btDevice: Class<T>?): T
}