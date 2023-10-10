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
package org.catrobat.catroid.common.bluetooth.models

import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType
import org.catrobat.catroid.devices.mindstorms.nxt.NXTError
import org.catrobat.catroid.devices.mindstorms.nxt.NXTReply
import java.io.DataInputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Random
import kotlin.experimental.and
import kotlin.experimental.or

class MindstormsNXTTestModel : DeviceModel {
    private var isRunning = true
    private val random = Random(System.currentTimeMillis())
    private val batteryValue = byteArrayOf(getRandomByte(256), getRandomByte(256))
    private val keepAliveTime =
        byteArrayOf(getRandomByte(256), getRandomByte(256), getRandomByte(256), getRandomByte(256))
    private val portSensorType = byteArrayOf(0, 0, 0, 0)
    private val portSensorMode = byteArrayOf(0, 0, 0, 0)
    private val sensorValue = byteArrayOf(getRandomByte(256), getRandomByte(256))
    private var ultrasonicSensorBytesReady: Byte = 0
    protected fun createResponseFromClientRequest(message: ByteArray): ByteArray? {
        val commandType = message[0]
        val commandByte = message[1]
        return when (CommandByte.getTypeByValue(commandByte)) {
            CommandByte.SET_INPUT_MODE -> handleSetInputModeMessage(message, commandType)
            CommandByte.GET_INPUT_VALUES -> handleGetInputValuesMessage(message, commandType)
            CommandByte.RESET_INPUT_SCALED_VALUE -> handleResetInputScaledValueMessage(
                message,
                commandType
            )
            CommandByte.LS_WRITE -> handleLsWriteMessage(message, commandType)
            CommandByte.LS_GET_STATUS -> handleLsGetStatusMessage(message, commandType)
            CommandByte.LS_READ -> handleLsReadMessage(message, commandType)
            CommandByte.KEEP_ALIVE -> handleKeepAlive(message, commandType)
            CommandByte.GET_BATTERY_LEVEL -> handleGetBatteryLevel(message, commandType)
            else -> handleUnknownMessage(commandType, commandByte)
        }
    }

    private fun handleSetInputModeMessage(message: ByteArray, commandType: Byte): ByteArray? {
        var status: Byte
        var reply: ByteArray? = null
        status = checkMessageLength(message, 5)
        if (status == NXTReply.NO_ERROR) {
            val port = message[2]
            val sensorType = message[3]
            val sensorMode = message[4]
            status = setSensorTypeAndMode(sensorType, sensorMode, port)
        }
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(3)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.SET_INPUT_MODE.byte
            reply[2] = status
        }
        return reply
    }

    private fun handleGetInputValuesMessage(message: ByteArray, commandType: Byte): ByteArray? {
        var reply: ByteArray? = null
        var status: Byte
        status = checkMessageLength(message, 3)
        val port = message[2]
        if (status == NXTReply.NO_ERROR) {
            status = checkMessagePort(port)
        }
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(16)
            val isValid: Byte = 1
            val isCalibrated: Byte = 0
            val notUsed: Byte = 0
            val scaledValue0 = sensorValue[0]
            val scaledValue1 = sensorValue[1]
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.GET_INPUT_VALUES.byte
            reply[2] = status
            reply[3] = port
            reply[4] = isValid
            reply[5] = isCalibrated
            reply[6] = portSensorType[port.toInt()]
            reply[7] = portSensorMode[port.toInt()]
            reply[8] = notUsed
            reply[9] = notUsed
            reply[10] = notUsed
            reply[11] = notUsed
            reply[12] = scaledValue0
            reply[13] = scaledValue1
            reply[14] = notUsed
            reply[15] = notUsed
        }
        return reply
    }

    private fun handleResetInputScaledValueMessage(
        message: ByteArray,
        commandType: Byte
    ): ByteArray? {
        var reply: ByteArray? = null
        var status = checkMessageLength(message, 3)
        if (status == NO_ERROR) {
            status = checkMessagePort(message[2])
        }
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(3)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.RESET_INPUT_SCALED_VALUE.byte
            reply[2] = status
        }
        return reply
    }

    private fun handleLsWriteMessage(message: ByteArray, commandType: Byte): ByteArray? {
        var reply: ByteArray? = null
        var status = checkMessageLength(message, 7)
        if (status == NO_ERROR) {
            status = checkMessagePort(message[2])
        }
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(3)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.LS_WRITE.byte
            reply[2] = status
        }
        return reply
    }

    private fun handleLsGetStatusMessage(message: ByteArray, commandType: Byte): ByteArray? {
        var reply: ByteArray? = null
        var status = checkMessageLength(message, 3)
        if (status == NO_ERROR) {
            status = checkMessagePort(message[2])
        }
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(4)
            ultrasonicSensorBytesReady = getRandomByte(2)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.LS_GET_STATUS.byte
            reply[2] = status
            reply[3] = ultrasonicSensorBytesReady
        }
        return reply
    }

    private fun handleLsReadMessage(message: ByteArray, commandType: Byte): ByteArray? {
        var reply: ByteArray? = null
        var status = checkMessageLength(message, 3)
        if (status == NO_ERROR) {
            status = checkMessagePort(message[2])
        }
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(20)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.LS_READ.byte
            reply[2] = status
            reply[3] = ultrasonicSensorBytesReady
            reply[4] = sensorValue[0]
            ultrasonicSensorBytesReady = 0
        }
        return reply
    }

    private fun handleKeepAlive(message: ByteArray, commandType: Byte): ByteArray? {
        var reply: ByteArray? = null
        val status = checkMessageLength(message, 2)
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(7)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.KEEP_ALIVE.byte
            reply[2] = status
            reply[3] = keepAliveTime[0]
            reply[4] = keepAliveTime[1]
            reply[5] = keepAliveTime[2]
            reply[6] = keepAliveTime[3]
        }
        return reply
    }

    private fun handleGetBatteryLevel(message: ByteArray, commandType: Byte): ByteArray? {
        var reply: ByteArray? = null
        val status = checkMessageLength(message, 2)
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(5)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = CommandByte.GET_BATTERY_LEVEL.byte
            reply[2] = status
            reply[3] = batteryValue[0]
            reply[4] = batteryValue[1]
        }
        return reply
    }

    private fun handleUnknownMessage(commandType: Byte, commandByte: Byte): ByteArray? {
        var reply: ByteArray? = null
        if (commandType == SHOULD_REPLY) {
            reply = ByteArray(3)
            reply[0] = CommandType.REPLY_COMMAND.byte
            reply[1] = commandByte
            reply[2] = NXTError.ErrorCode.UnknownCommand.byte
        }
        return reply
    }

    private fun checkMessagePort(port: Byte): Byte {
        return if (port < 0 || port > 3) {
            NXTError.ErrorCode.BadArguments.byte
        } else NXTReply.NO_ERROR
    }

    private fun checkMessageLength(message: ByteArray, expectedMessageLength: Int): Byte {
        return if (message.size != expectedMessageLength) {
            NXTError.ErrorCode.WrongNumberOfBytes.byte
        } else NXTReply.NO_ERROR
    }

    private fun setSensorType(sensorType: Byte, port: Byte): Byte {
        if (sensorType < 0x0 || sensorType > 0x0C) {
            return NXTError.ErrorCode.BadArguments.byte
        }
        portSensorType[port.toInt()] = sensorType
        return NXTReply.NO_ERROR
    }

    private fun setSensorTypeAndMode(sensorType: Byte, sensorMode: Byte, port: Byte): Byte {
        var status = checkMessagePort(port)
        if (status != NO_ERROR) {
            return status
        }
        status = setSensorType(sensorType, port)
        return if (status != NO_ERROR) {
            status
        } else setSensorMode(sensorMode, port)
    }

    private fun setSensorMode(sensorMode: Byte, port: Byte): Byte {
        return when (sensorMode) {
            0x00.toByte(), 0x20.toByte(), 0x40.toByte(), 0x60.toByte(), 0x80.toByte(), 0xA0.toByte(),
            0xC0.toByte(), 0xE0.toByte(),
            0x1F.toByte() -> {
                portSensorMode[port.toInt()] = sensorMode
                NO_ERROR
            }
            else -> NXTError.ErrorCode.BadArguments.byte
        }
    }

    @Throws(IOException::class)
    override fun start(inStream: DataInputStream?, outStream: OutputStream?) {
        val messageLengthBuffer = ByteArray(2)

        while (isRunning) {
            if (inStream != null) {
                inStream.readFully(messageLengthBuffer, 0, 2)
            }
            val expectedMessageLength = (messageLengthBuffer[0].toInt() and 0xFF) or
                ((messageLengthBuffer[1].toInt() and 0xFF) shl 8)
            handleClientMessage(expectedMessageLength, inStream, outStream)
        }
    }

    override fun stop() {
        isRunning = false
    }

    @Throws(IOException::class)
    private fun handleClientMessage(
        expectedMessageLength: Int,
        inStream: DataInputStream?,
        outStream: OutputStream?
    ) {
        val requestMessage = ByteArray(expectedMessageLength)
        inStream!!.readFully(requestMessage, 0, expectedMessageLength)
        val responseMessage = createResponseFromClientRequest(requestMessage) ?: return
        outStream!!.write(getMessageLength(responseMessage))
        outStream.write(responseMessage)
        outStream.flush()
    }

    private fun getMessageLength(message: ByteArray): ByteArray {
        return byteArrayOf(
            (message.size and 0x00FF).toByte(),
            (message.size and 0xFF00 shr 8).toByte()
        )
    }

    fun setSensorValue(value: Int) {
        sensorValue[0] = (value and 0xff).toByte()
        sensorValue[1] = (value shr 8 and 0xff).toByte()
    }

    fun setBatteryValue(batteryValue: Int) {
        this.batteryValue[0] = (batteryValue and 0xff).toByte()
        this.batteryValue[1] = (batteryValue shr 8 and 0xff).toByte()
    }

    fun setKeepAliveTime(keepAliveTimeValue: Int) {
        keepAliveTime[0] = (keepAliveTimeValue and 0xff).toByte()
        keepAliveTime[1] = (keepAliveTimeValue shr 8 and 0xff).toByte()
        keepAliveTime[2] = (keepAliveTimeValue shr 16 and 0xff).toByte()
        keepAliveTime[3] = (keepAliveTimeValue shr 24 and 0xff).toByte()
    }

    fun getRandomByte(maxExclusive: Int): Byte {
        return random.nextInt(maxExclusive).toByte()
    }

    companion object {
        private const val SHOULD_REPLY: Byte = 0x0
        private const val NO_ERROR: Byte = 0x0
    }
}