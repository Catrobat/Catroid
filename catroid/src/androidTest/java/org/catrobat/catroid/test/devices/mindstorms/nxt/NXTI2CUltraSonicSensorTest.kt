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
import org.junit.runner.RunWith
import org.junit.rules.ExpectedException
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.devices.mindstorms.MindstormsCommand
import org.catrobat.catroid.devices.mindstorms.nxt.Command
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte
import org.catrobat.catroid.devices.mindstorms.nxt.NXTReply
import org.catrobat.catroid.devices.mindstorms.nxt.NXTException
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class NXTI2CUltraSonicSensorTest {
    @get:Rule
    val exception = ExpectedException.none()
    private var mindstormsConnection: MindstormsConnection? = null
    private var nxtSensor: NXTI2CUltraSonicSensor? = null
    private val sensorType = NXTSensorType.LOW_SPEED_9V
    private val sensorMode = NXTSensorMode.RAW
    private val port = 3
    @Before
    @Throws(Exception::class)
    fun setUp() {
        mindstormsConnection = Mockito.mock(MindstormsConnection::class.java)
        Mockito.`when`(mindstormsConnection?.isConnected).thenReturn(true)
        nxtSensor = NXTI2CUltraSonicSensor(mindstormsConnection)
        nxtSensor!!.hasInit = true
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSensorGetValueInvalidFirstByteException() {
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any())).thenReturn(
            byteArrayOf(
                0,
                CommandByte.LS_READ.byte,
                NXTReply.NO_ERROR,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
            )
        )
        exception.expect(NXTException::class.java)
        exception.expectMessage(NXTReply.INVALID_FIRST_BYTE_EXCEPTION_MESSAGE)
        nxtSensor!!.value
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSensorGetValueInvalidCommandByteException() {
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any())).thenReturn(
            byteArrayOf(
                CommandType.REPLY_COMMAND.byte,
                0,
                NXTReply.NO_ERROR,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
            )
        )
        exception.expect(NXTException::class.java)
        exception.expectMessage(NXTReply.INVALID_COMMAND_BYTE_EXCEPTION_MESSAGE)
        nxtSensor!!.value
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSensorGetValueInvalidReplyLengthException() {
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any()))
            .thenReturn(byteArrayOf())
        exception.expect(NXTException::class.java)
        exception.expectMessage(NXTReply.INSUFFICIENT_REPLY_LENGTH_EXCEPTION_MESSAGE)
        nxtSensor!!.value
    }

    @Test
    @Throws(MindstormsException::class)
    fun testGetValueLeastSignificantByte() {
        val expectedNormalizedValue: Byte = 1
        val expectedRawValue: Byte = 2
        val expectedScaledValue: Byte = 3
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any())).thenReturn(
            byteArrayOf(
                CommandType.REPLY_COMMAND.byte,
                CommandByte.LS_READ.byte,
                NXTReply.NO_ERROR,
                0,
                0,
                0,
                0,
                0,
                expectedRawValue,
                0,
                expectedNormalizedValue,
                0,
                expectedScaledValue,
                0,
                0,
                0
            )
        )
        Assert.assertEquals(
            expectedNormalizedValue.toLong(),
            nxtSensor!!.sensorReadings.normalized.toLong()
        )
        Assert.assertEquals(expectedRawValue.toLong(), nxtSensor!!.sensorReadings.raw.toLong())
        Assert.assertEquals(
            expectedScaledValue.toLong(),
            nxtSensor!!.sensorReadings.scaled.toLong()
        )
    }

    @Test
    @Throws(MindstormsException::class)
    fun testGetValueMostSignificantByte() {
        val expectedNormalizedValue: Byte = 1
        val expectedRawValue: Byte = 2
        val expectedScaledValue: Byte = 3
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any())).thenReturn(
            byteArrayOf(
                CommandType.REPLY_COMMAND.byte,
                CommandByte.LS_READ.byte,
                NXTReply.NO_ERROR,
                0,
                0,
                0,
                0,
                0,
                0,
                expectedRawValue,
                0,
                expectedNormalizedValue,
                0,
                expectedScaledValue,
                0,
                0
            )
        )
        Assert.assertEquals(
            (256 * expectedNormalizedValue).toLong(),
            nxtSensor!!.sensorReadings.normalized.toLong()
        )
        Assert.assertEquals(
            (256 * expectedRawValue).toLong(),
            nxtSensor!!.sensorReadings.raw.toLong()
        )
        Assert.assertEquals(
            (256 * expectedScaledValue).toLong(),
            nxtSensor!!.sensorReadings.scaled.toLong()
        )
    }

    @Test
    @Throws(MindstormsException::class)
    fun testResetScaledValue() {
        val command =
            Command(CommandType.DIRECT_COMMAND, CommandByte.RESET_INPUT_SCALED_VALUE, false)
        command.append(port.toByte())
        nxtSensor!!.resetScaledValue()
        Mockito.verify(mindstormsConnection, VerificationModeFactory.times(1))
            ?.send(ArgumentMatchers.eq(command))
    }

    @Test
    @Throws(MindstormsException::class)
    fun testUpdateTypeAndMode() {
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any()))
            .thenReturn(
                byteArrayOf(
                    CommandType.REPLY_COMMAND.byte,
                    CommandByte.LS_READ.byte,
                    NXTReply.NO_ERROR
                )
            )
        val command = Command(CommandType.DIRECT_COMMAND, CommandByte.SET_INPUT_MODE, true)
        command.append(port.toByte())
        command.append(sensorType.byte)
        command.append(sensorMode.byte)
        nxtSensor!!.updateTypeAndMode()
        Mockito.verify(mindstormsConnection, VerificationModeFactory.times(1))?.sendAndReceive(
            ArgumentMatchers.eq(command)
        )
    }

    @Test
    @Throws(MindstormsException::class)
    fun testGetNumberOfBytesAreReadyToRead() {
        val expectedReadBytes: Byte = 15
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any()))
            .thenReturn(
                byteArrayOf(
                    CommandType.REPLY_COMMAND.byte,
                    CommandByte.LS_READ.byte,
                    NXTReply.NO_ERROR,
                    expectedReadBytes
                )
            )
        Assert.assertEquals(
            expectedReadBytes.toLong(),
            nxtSensor!!.numberOfBytesAreReadyToRead.toLong()
        )
        val command = Command(CommandType.DIRECT_COMMAND, CommandByte.LS_GET_STATUS, true)
        command.append(port.toByte())
        Mockito.verify(mindstormsConnection, VerificationModeFactory.times(1))?.sendAndReceive(
            ArgumentMatchers.eq(command)
        )
    }

    @Test
    @Throws(MindstormsException::class)
    fun testRead() {
        val expectedReadMessage = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        val receivingMessageHead = byteArrayOf(
            CommandType.REPLY_COMMAND.byte,
            CommandByte.LS_READ.byte,
            NXTReply.NO_ERROR,
            expectedReadMessage.size.toByte()
        )
        val receivingMessage = ByteArray(receivingMessageHead.size + expectedReadMessage.size)
        System.arraycopy(receivingMessageHead, 0, receivingMessage, 0, receivingMessageHead.size)
        System.arraycopy(
            expectedReadMessage,
            0,
            receivingMessage,
            receivingMessageHead.size,
            expectedReadMessage.size
        )
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any()))
            .thenReturn(receivingMessage)
        Assert.assertArrayEquals(expectedReadMessage, nxtSensor!!.read())
        val command = Command(CommandType.DIRECT_COMMAND, CommandByte.LS_READ, true)
        command.append(port.toByte())
        Mockito.verify(mindstormsConnection, VerificationModeFactory.times(1))?.sendAndReceive(
            ArgumentMatchers.eq(command)
        )
    }

    @Test
    @Throws(MindstormsException::class)
    fun testWriteWithoutReply() {
        val expectedWriteMessage = byteArrayOf(1, 2, 3, 4)
        val expectedRxLength: Byte = 0x0
        nxtSensor!!.write(expectedWriteMessage, expectedRxLength, false)
        val command = Command(CommandType.DIRECT_COMMAND, CommandByte.LS_WRITE, false)
        command.append(port.toByte())
        command.append(expectedWriteMessage.size.toByte())
        command.append(expectedRxLength)
        command.append(expectedWriteMessage)
        Mockito.verify(mindstormsConnection, VerificationModeFactory.times(1))
            ?.send(ArgumentMatchers.eq(command))
    }
}