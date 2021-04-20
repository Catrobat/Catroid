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

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException
import java.util.UUID

class BluetoothConnectionImpl(private val macAddress: String, private val uuid: UUID) :
    BluetoothConnection {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private var state: BluetoothConnection.State
    override fun connect(): BluetoothConnection.State {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            state = BluetoothConnection.State.ERROR_BLUETOOTH_NOT_SUPPORTED
            return  state
        }
        if (bluetoothAdapter!!.state != BluetoothAdapter.STATE_ON) {
            state = BluetoothConnection.State.ERROR_ADAPTER
            return state
        }
        Log.d(TAG, "Got Adapter and Adapter was on")
        bluetoothDevice = bluetoothAdapter!!.getRemoteDevice(macAddress)
        Log.d(TAG, "Got remote device")
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
        } catch (ioException: IOException) {
             if (bluetoothDevice.bondState == BluetoothDevice.BOND_NONE) {
                state = BluetoothConnection.State.ERROR_NOT_BONDED
                 return state
            } else if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDING) {
                 state = BluetoothConnection.State.ERROR_STILL_BONDING
                 return state
            } else {
                 state = BluetoothConnection.State.ERROR_SOCKET
                 return state
            }
        }
        Log.d(TAG, "Socket was created")
        return when (connectSocket(bluetoothSocket)) {
            BluetoothConnection.State.CONNECTED -> {
                Log.d(TAG, "connected")
                BluetoothConnection.State.CONNECTED
            }
            BluetoothConnection.State.ERROR_SOCKET -> {
                Log.d(TAG, "error connecting")
                BluetoothConnection.State.ERROR_SOCKET
            }
            else -> {
                Log.wtf(TAG, "This should never happen!")
                BluetoothConnection.State.NOT_CONNECTED
            }
        }
    }

    override fun connectSocket(socket: BluetoothSocket): BluetoothConnection.State {
        Log.d(TAG, "Connecting")
        bluetoothSocket = socket
        try {
            bluetoothSocket.connect()
        } catch (ioException: IOException) {
            try {
                Log.e(TAG, Log.getStackTraceString(ioException))
                Log.d(TAG, "Try connecting again")
                // try another method for connection, this should work on the HTC desire, credits to Michael Biermann
                val mMethod = bluetoothDevice.javaClass
                    .getMethod(
                        REFLECTION_METHOD_NAME, *arrayOf<Class<*>?>(
                            Int::class.javaPrimitiveType
                        )
                    )
                bluetoothSocket =
                    mMethod.invoke(bluetoothDevice, Integer.valueOf(1)) as BluetoothSocket
                bluetoothSocket.connect()
                state = BluetoothConnection.State.CONNECTED
                return state
            } catch (noSuchMethodException: NoSuchMethodException) {
                Log.e(TAG, Log.getStackTraceString(noSuchMethodException))
            } catch (invocationTargetException: InvocationTargetException) {
                Log.e(TAG, Log.getStackTraceString(invocationTargetException))
            } catch (illegalAccessException: IllegalAccessException) {
                Log.e(TAG, Log.getStackTraceString(illegalAccessException))
            } catch (secondIOException: IOException) {
                Log.e(TAG, Log.getStackTraceString(secondIOException))
            }
            state = BluetoothConnection.State.ERROR_SOCKET
            return state
        }
        state = BluetoothConnection.State.CONNECTED
        return state
    }

    override fun disconnect() {
        Log.d(TAG, "disconnecting")
        try {
            state = BluetoothConnection.State.NOT_CONNECTED
            bluetoothSocket.close()
        } catch (ioException: IOException) {
            Log.e(TAG, Log.getStackTraceString(ioException))
        }
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return bluetoothSocket.inputStream
    }

    @Throws(IOException::class)
    override fun getOutputStream(): OutputStream {
        return bluetoothSocket.outputStream
    }

    override fun getState(): BluetoothConnection.State {
        return state
    }

    companion object {
        private const val REFLECTION_METHOD_NAME = "createRfcommSocket"
        private val TAG = BluetoothConnectionImpl::class.java.simpleName
    }

    init {
        state = BluetoothConnection.State.NOT_CONNECTED
    }
}