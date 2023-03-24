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
package org.catrobat.catroid.common.firmata

import junit.framework.Assert
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.catrobat.catroid.common.firmata.FirmataMessage
import org.catrobat.catroid.common.firmata.FirmataUtils
import name.antonsmirnov.firmata.BytesHelper
import name.antonsmirnov.firmata.writer.DigitalMessageWriter
import name.antonsmirnov.firmata.writer.AnalogMessageWriter
import name.antonsmirnov.firmata.writer.ReportDigitalPortMessageWriter
import name.antonsmirnov.firmata.writer.ReportAnalogPinMessageWriter
import name.antonsmirnov.firmata.writer.SetPinModeMessageWriter
import java.nio.ByteBuffer

class FirmataUtils(private val logger: ConnectionDataLogger) {
    val digitalMessageData: FirmataMessage
        get() {
            val portAndCommand = nextMessage
            val port = getPinFromHeader(portAndCommand)
            val command = getCommandFromHeader(portAndCommand)
            Assert.assertEquals(
                "This is no digital message command",
                DIGITAL_MESSAGE_COMMAND,
                command
            )
            val lsb = nextMessage
            val msb = nextMessage
            val data = BytesHelper.DECODE_BYTE(lsb, msb)
            return FirmataMessage(command, port, data)
        }
    val analogMessageData: FirmataMessage
        get() {
            val pinAndCommand = nextMessage
            val pin = getPinFromHeader(pinAndCommand)
            val command = getCommandFromHeader(pinAndCommand)
            Assert.assertEquals(
                "This is no analog message command",
                ANALOG_MESSAGE_COMMAND,
                command
            )
            val lsb = nextMessage
            val msb = nextMessage
            val data = BytesHelper.DECODE_BYTE(lsb, msb)
            return FirmataMessage(command, pin, data)
        }
    val setPinModeMessage: FirmataMessage
        get() {
            val command = nextMessage
            val pin = nextMessage
            val mode = nextMessage
            Assert.assertEquals("No set pin mode message", SET_PIN_MODE_COMMAND, command)
            return FirmataMessage(command, pin, mode)
        }
    val reportDigitalPortMessage: FirmataMessage
        get() {
            val portAndCommand = nextMessage
            val port = getPinFromHeader(portAndCommand)
            val command = getCommandFromHeader(portAndCommand)
            Assert.assertEquals(
                "No report digital port message",
                REPORT_DIGITAL_PORT_COMMAND,
                command
            )
            val data = nextMessage
            return FirmataMessage(command, port, data)
        }
    val reportAnalogPinMessage: FirmataMessage
        get() {
            val pinAndCommand = nextMessage
            val pin = getPinFromHeader(pinAndCommand)
            val command = getCommandFromHeader(pinAndCommand)
            Assert.assertEquals("No report analog pin message", REPORT_ANALOG_PIN_COMMAND, command)
            val data = nextMessage
            return FirmataMessage(command, pin, data)
        }
    val nextMessage: Int
        get() {
            val message = logger.nextSentMessage
            Assert.assertNotNull("There is no message", message)
            val bb = ByteBuffer.wrap(message)
            return bb.int
        }

    private fun getPinFromHeader(header: Int): Int {
        return header and BITMASK_PIN
    }

    private fun getCommandFromHeader(header: Int): Int {
        return header and BITMASK_COMMAND
    }

    companion object {
        private const val DIGITAL_MESSAGE_COMMAND = DigitalMessageWriter.COMMAND // 0x90
        private const val ANALOG_MESSAGE_COMMAND = AnalogMessageWriter.COMMAND // 0xE0
        private const val REPORT_DIGITAL_PORT_COMMAND =
            ReportDigitalPortMessageWriter.COMMAND // 0xD0
        private const val REPORT_ANALOG_PIN_COMMAND = ReportAnalogPinMessageWriter.COMMAND // 0xC0
        private const val SET_PIN_MODE_COMMAND = SetPinModeMessageWriter.COMMAND // 0xF4
        private const val BITMASK_COMMAND = 0xF0
        private const val BITMASK_PIN = 0x0F
    }
}