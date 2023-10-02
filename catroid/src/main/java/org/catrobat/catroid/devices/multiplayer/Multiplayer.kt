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
package org.catrobat.catroid.devices.multiplayer

import android.bluetooth.BluetoothSocket
import android.util.Log
import org.apache.commons.lang3.SerializationException
import org.apache.commons.lang3.SerializationUtils
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity.AcceptThread
import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.formulaeditor.UserVariable
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.util.UUID

class Multiplayer : MultiplayerInterface {
    private var connectedThread: Thread? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var bluetoothConnection: BluetoothConnection? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var acceptThread: AcceptThread? = null

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    override fun getName(): String = "MULTIPLAYER"

    override fun setConnection(connection: BluetoothConnection) {
        bluetoothConnection = connection
    }

    fun setBluetoothSocket(bluetoothSocket: BluetoothSocket?) {
        this.bluetoothSocket = bluetoothSocket
    }

    fun setAcceptThread(acceptThread: AcceptThread?) {
        this.acceptThread = acceptThread
    }

    override fun getDeviceType(): Class<out BluetoothDevice?> = BluetoothDevice.MULTIPLAYER

    override fun disconnect() {
        bluetoothConnection?.let {
            bluetoothConnection?.disconnect()
            bluetoothConnection = null
        }

        bluetoothSocket?.let {
            try {
                bluetoothSocket?.close()
            } catch (exception: IOException) {
                Log.d(TAG, exception.message, exception)
            }

            bluetoothSocket = null
        }

        closeStreams()

        if (acceptThread?.isAlive == true) {
            acceptThread?.cancel()
            acceptThread?.interrupt()
        }
    }

    fun closeStreams() {
        try {
            inputStream?.close()
            inputStream = null
        } catch (exception: IOException) {
            Log.d(TAG, exception.message, exception)
        }

        try {
            outputStream?.close()
            outputStream = null
        } catch (exception: IOException) {
            Log.d(TAG, exception.message, exception)
        }
    }

    override fun isAlive(): Boolean = !(inputStream == null || outputStream == null)

    override fun getBluetoothDeviceUUID(): UUID = MULTIPLAYER_UUID

    override fun initialise() = Unit

    override fun start() {
        connectedThread = object : Thread() {
            override fun run() {
                val buffer = ByteArray(READ_BLOCKSIZE)
                var numberOfBytes: Int
                while (!this.isInterrupted) {
                    try {
                        numberOfBytes = inputStream?.read(buffer) ?: 0
                        if (numberOfBytes > 0) {
                            getChangedMultiplayerVariables(buffer)
                        }
                    } catch (exception: IOException) {
                        Log.d(TAG, "Input stream was disconnected", exception)
                        disconnect()
                        break
                    }
                }
            }
        }
        connectedThread?.start()
    }

    override fun pause() {
        connectedThread?.interrupt()
    }

    override fun destroy() = Unit

    override fun sendChangedMultiplayerVariables(multiplayerVariable: UserVariable?) {
        val message = multiplayerVariable?.let {
            MultiplayerVariableMessage(it.name, it.value)
        }

        try {
            outputStream?.write(SerializationUtils.serialize(message))
        } catch (exception: IOException) {
            Log.d(TAG, "Error occurred when sending data", exception)
            disconnect()
        }
    }

    override fun getChangedMultiplayerVariables(receivedData: ByteArray?) {
        try {
            val message = SerializationUtils.deserialize<MultiplayerVariableMessage>(receivedData)
            val multiplayerVariable = projectManager.currentProject.getMultiplayerVariable(message.name)
            multiplayerVariable?.value = message.value
        } catch (_: SerializationException) {
            return
        }
    }

    override fun setStreams(inputStream: InputStream?, outputStream: OutputStream?) {
        this.inputStream = inputStream
        this.outputStream = outputStream
    }

    companion object {
        val MULTIPLAYER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private val TAG = Multiplayer::class.simpleName
        private const val READ_BLOCKSIZE = 1024
    }
}

class MultiplayerVariableMessage internal constructor(val name: String, val value: Any) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
