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

import org.catrobat.catroid.common.bluetooth.BluetoothLogger
import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import org.catrobat.catroid.common.bluetooth.ObservedInputStream
import org.catrobat.catroid.common.bluetooth.ObservedOutputStream
import org.catrobat.catroid.bluetooth.BluetoothConnectionImpl
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.Throws

internal class BluetoothConnectionProxy(macAddress: String?, uuid: UUID?, logger: BluetoothLogger) :
    BluetoothConnection {
    private val logger: BluetoothLogger
    var btConnection: BluetoothConnection
    var observedInputStream: ObservedInputStream? = null
    var observedOutputStream: ObservedOutputStream? = null

    init {
        btConnection = BluetoothConnectionImpl(macAddress, uuid)
        this.logger = logger
        logger.loggerAttached(this)
    }

    override fun connect(): BluetoothConnection.State {
        return btConnection.connect()
    }

    override fun connectSocket(socket: BluetoothSocket): BluetoothConnection.State {
        return btConnection.connectSocket(socket)
    }

    override fun disconnect() {
        btConnection.disconnect()
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        if (observedInputStream == null) {
            observedInputStream = ObservedInputStream(btConnection.inputStream, logger)
        }
        return observedInputStream as ObservedInputStream
    }

    @Throws(IOException::class)
    override fun getOutputStream(): OutputStream {
        if (observedOutputStream == null) {
            observedOutputStream = ObservedOutputStream(btConnection.outputStream, logger)
        }
        return observedOutputStream as ObservedOutputStream
    }

    override fun getState(): BluetoothConnection.State {
        return btConnection.state
    }
}