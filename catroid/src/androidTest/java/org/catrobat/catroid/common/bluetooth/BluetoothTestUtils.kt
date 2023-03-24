/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.common.bluetooth

import android.content.Context
import junit.framework.Assert
import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity
import org.catrobat.catroid.bluetooth.base.BluetoothConnectionFactory
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.common.bluetooth.BluetoothLogger
import org.catrobat.catroid.common.bluetooth.BluetoothConnectionProxy
import java.nio.ByteBuffer
import java.util.Arrays
import java.util.UUID

object BluetoothTestUtils {
    @JvmStatic
	fun intToByteArray(i: Int): ByteArray {
        return ByteBuffer.allocate(4).putInt(i).array()
    }

    fun getSubArray(buffer: ByteArray?, offset: Int): ByteArray? {
        return if (buffer == null) {
            null
        } else Arrays.copyOfRange(buffer, offset, buffer.size)
    }

    @JvmStatic
	fun getSubArray(buffer: ByteArray?, offset: Int, count: Int): ByteArray? {
        if (buffer == null) {
            return null
        }
        Assert.assertTrue("count can't be negative", count >= 0)
        Assert.assertTrue("wrong offset or count", buffer.size - offset >= count)
        return Arrays.copyOfRange(buffer, offset, offset + count)
    }

    @JvmStatic
	fun hookInConnection(connectionProxy: BluetoothConnection) {
        ConnectBluetoothDeviceActivity.setConnectionFactory(object : BluetoothConnectionFactory {
            override fun <T : BluetoothDevice?> createBTConnectionForDevice(
                bluetoothDeviceType: Class<T>,
                address: String,
                deviceUUID: UUID,
                applicationContext: Context
            ): BluetoothConnection {
                return connectionProxy
            }
        })
    }

    @JvmStatic
	fun hookInConnectionFactoryWithBluetoothConnectionProxy(logger: BluetoothLogger?) {
        ConnectBluetoothDeviceActivity.setConnectionFactory(object : BluetoothConnectionFactory {
            override fun <T : BluetoothDevice?> createBTConnectionForDevice(
                device: Class<T>,
                address: String,
                deviceUUID: UUID,
                applicationContext: Context
            ): BluetoothConnection {
                return BluetoothConnectionProxy(address, deviceUUID, logger!!)
            }
        })
    }

    @JvmStatic
	fun resetConnectionHooks() {
        ConnectBluetoothDeviceActivity.setConnectionFactory(null)
        ConnectBluetoothDeviceActivity.setDeviceFactory(null)
    }
}