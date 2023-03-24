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

import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import org.catrobat.catroid.common.bluetooth.ModelRunner
import org.catrobat.catroid.common.bluetooth.BluetoothLogger
import org.catrobat.catroid.common.bluetooth.ObservedInputStream
import kotlin.Throws
import org.catrobat.catroid.common.bluetooth.ObservedOutputStream
import org.catrobat.catroid.common.bluetooth.models.DeviceModel
import android.bluetooth.BluetoothSocket
import android.util.Log
import junit.framework.Assert
import org.catrobat.catroid.common.bluetooth.LocalConnectionProxy
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

internal class LocalConnectionProxy : BluetoothConnection {
    private var observedInputStream: InputStream? = null
    private var observedOutputStream: OutputStream? = null
    private var connectionState = BluetoothConnection.State.NOT_CONNECTED
    private var modelRunner: ModelRunner? = null

    constructor(logger: BluetoothLogger) {
        observedInputStream = ObservedInputStream(object : InputStream() {
            @Throws(IOException::class)
            override fun read(): Int {
                return 0
            }
        }, logger)
        observedOutputStream = ObservedOutputStream(object : OutputStream() {
            @Throws(IOException::class)
            override fun write(i: Int) {
            }
        }, logger)
        logger.loggerAttached(this)
    }

    constructor(logger: BluetoothLogger?, deviceModel: DeviceModel?) {
        val serverInputStreamFromClientsOutputStream = PipedInputStream()
        val serverOutputStreamToClientsInputStream = PipedOutputStream()
        val pipedInputStreamForClient = PipedInputStream()
        val pipedOutputStreamForClient = PipedOutputStream()
        try {
            serverInputStreamFromClientsOutputStream.connect(pipedOutputStreamForClient)
            serverOutputStreamToClientsInputStream.connect(pipedInputStreamForClient)
            observedInputStream = ObservedInputStream(pipedInputStreamForClient, logger)
            observedOutputStream = ObservedOutputStream(pipedOutputStreamForClient, logger)
            modelRunner = ModelRunner(
                deviceModel,
                serverInputStreamFromClientsOutputStream,
                serverOutputStreamToClientsInputStream
            )
            modelRunner!!.start()
        } catch (e: IOException) {
            Assert.fail("Error with ConnectionProxy Stream pipes.")
        }
    }

    override fun connect(): BluetoothConnection.State {
        connectionState = BluetoothConnection.State.CONNECTED
        return connectionState
    }

    override fun connectSocket(socket: BluetoothSocket): BluetoothConnection.State {
        connectionState = BluetoothConnection.State.CONNECTED
        return connectionState
    }

    override fun disconnect() {
        try {
            observedOutputStream!!.close()
            observedInputStream!!.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error on disconnect while closing streams")
        }
        if (modelRunner != null) {
            modelRunner!!.stopModelRunner()
        }
        connectionState = BluetoothConnection.State.NOT_CONNECTED
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return observedInputStream!!
    }

    @Throws(IOException::class)
    override fun getOutputStream(): OutputStream {
        return observedOutputStream!!
    }

    override fun getState(): BluetoothConnection.State {
        return connectionState
    }

    companion object {
        val TAG = LocalConnectionProxy::class.java.simpleName
    }
}