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
package org.catrobat.catroid.test.devices.mindstorms.nxt

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl
import org.catrobat.catroid.devices.mindstorms.nxt.Command
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream

@RunWith(AndroidJUnit4::class)
class MindstormsConnectionTest {
    @Test
    @Throws(Exception::class)
    fun testSend() {
        val outStream = ByteArrayOutputStream()
        val connection = MindstormsConnectionImpl(null)
        Reflection.setPrivateField(connection, "legoOutputStream", outStream)
        val command = Command(CommandType.DIRECT_COMMAND, CommandByte.SET_OUTPUT_STATE, false)
        command.append(0x1.toByte())
        command.append(0x2.toByte())
        command.append(0x3.toByte())
        connection.send(command)
        val sentBytes = outStream.toByteArray()
        val expectedMessage = command.rawCommand
        Assert.assertEquals(expectedMessage.size + HEADER_SIZE, sentBytes.size)
        Assert.assertEquals(expectedMessage.size.toByte(), sentBytes[0])
        Assert.assertEquals((expectedMessage.size shr 8).toByte(), sentBytes[1])
        for (i in expectedMessage.indices) {
            Assert.assertEquals(expectedMessage[i], sentBytes[i + HEADER_SIZE])
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSendAndReceive() {
        val inputBuffer = byteArrayOf(4, 0, 3, 4, 5, 7)
        val inStream = ByteArrayInputStream(inputBuffer)
        val outStream = ByteArrayOutputStream()
        val connection = MindstormsConnectionImpl(null)
        Reflection.setPrivateField(connection, "legoOutputStream", outStream)
        Reflection.setPrivateField(connection, "legoInputStream", DataInputStream(inStream))
        val command = Command(CommandType.DIRECT_COMMAND, CommandByte.SET_OUTPUT_STATE, false)
        command.append(0x1.toByte())
        command.append(0x2.toByte())
        command.append(0x3.toByte())
        val receivedBytes = connection.sendAndReceive(command)
        val sentBytes = outStream.toByteArray()
        val expectedMessage = command.rawCommand
        Assert.assertEquals(expectedMessage.size + HEADER_SIZE, sentBytes.size)
        Assert.assertEquals(expectedMessage.size, sentBytes[0].toInt())
        Assert.assertEquals((expectedMessage.size shr 8).toByte(), sentBytes[1])
        for (i in expectedMessage.indices) {
            Assert.assertEquals(expectedMessage[i], sentBytes[i + HEADER_SIZE])
        }
        Assert.assertEquals(inputBuffer.size - HEADER_SIZE, receivedBytes.size)
        for (i in receivedBytes.indices) {
            Assert.assertEquals(inputBuffer[i + HEADER_SIZE], receivedBytes[i])
        }
    }

    companion object {
        const val HEADER_SIZE = 2
    }
}