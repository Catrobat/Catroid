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
package org.catrobat.catroid.test.devices.arduino

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.junit.runner.RunWith
import org.catrobat.catroid.devices.arduino.Arduino
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.catrobat.catroid.common.firmata.FirmataUtils
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.devices.arduino.ArduinoImpl
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import name.antonsmirnov.firmata.message.SetPinModeMessage
import name.antonsmirnov.firmata.writer.DigitalMessageWriter
import name.antonsmirnov.firmata.writer.AnalogMessageWriter
import name.antonsmirnov.firmata.writer.ReportAnalogPinMessageWriter
import name.antonsmirnov.firmata.writer.SetPinModeMessageWriter
import org.junit.After
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class ArduinoImplTest {
    private var arduino: Arduino? = null
    private var logger: ConnectionDataLogger? = null
    private var firmataUtils: FirmataUtils? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        arduino = ArduinoImpl()
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        firmataUtils = FirmataUtils(logger)
        (arduino as ArduinoImpl).setConnection(logger?.getConnectionProxy())
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        arduino!!.disconnect()
        logger!!.disconnectAndDestroy()
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSetDigitalArduinoPinIndividually() {
        arduino!!.initialise()
        doTestFirmataInitialization()

        // test set and clear of each individual pin
        for (i in 0 until ArduinoImpl.NUMBER_OF_DIGITAL_PINS) {
            arduino!!.setDigitalArduinoPin(i, 1)
            testDigital(1 shl ArduinoImpl.getIndexOfPinOnPort(i), i)
            arduino!!.setDigitalArduinoPin(i, 0)
            testDigital(0x00, i)
        }
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSetDigitalArduinoPinInterleavedOnPort() {
        arduino!!.initialise()
        doTestFirmataInitialization()

        // test interleaved set and clear of pins of a port
        var pin: Int
        for (i in 0 until ArduinoImpl.NUMBER_OF_DIGITAL_PORTS) {
            val offset = i * ArduinoImpl.PINS_IN_A_PORT
            pin = offset + 0
            arduino!!.setDigitalArduinoPin(pin, 1)
            testDigital(0x01, pin)
            arduino!!.setDigitalArduinoPin(pin, 0)
            testDigital(0x00, pin)
            pin = offset + 2
            arduino!!.setDigitalArduinoPin(pin, 1)
            testDigital(0x04, pin)
            pin = offset + 1
            arduino!!.setDigitalArduinoPin(pin, 1)
            testDigital(0x06, pin)
            pin = offset + 5
            arduino!!.setDigitalArduinoPin(pin, 1)
            testDigital(0x26, pin)
            pin = offset + 4
            arduino!!.setDigitalArduinoPin(pin, 1)
            testDigital(0x36, pin)
            pin = offset + 2
            arduino!!.setDigitalArduinoPin(pin, 0)
            testDigital(0x32, pin)
            pin = offset + 3
            arduino!!.setDigitalArduinoPin(pin, 1)
            testDigital(0x3A, pin)
            if (ArduinoImpl.NUMBER_OF_DIGITAL_PINS > offset + 7) {
                pin = offset + 6
                arduino!!.setDigitalArduinoPin(pin, 1)
                testDigital(0x7A, pin)
                pin = offset + 7
                arduino!!.setDigitalArduinoPin(pin, 1)
                testDigital(0xFA, pin)
                pin = offset + 6
                arduino!!.setDigitalArduinoPin(pin, 0)
                testDigital(0xBA, pin)
                pin = offset + 4
                arduino!!.setDigitalArduinoPin(pin, 0)
                testDigital(0xAA, pin)
                pin = offset + 5
                arduino!!.setDigitalArduinoPin(pin, 0)
                testDigital(0x8A, pin)
            }
        }
        for (i in 0 until ArduinoImpl.NUMBER_OF_DIGITAL_PORTS) {
            val offset = i * ArduinoImpl.PINS_IN_A_PORT
            arduino!!.setDigitalArduinoPin(offset + 0, 1)
            if (ArduinoImpl.NUMBER_OF_DIGITAL_PINS > offset + 7) {
                testDigital(0x8B, offset)
            } else {
                testDigital(0x3B, offset)
            }
        }
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSetDigitalArduinoPinInterleavedBetweenPorts() {
        arduino!!.initialise()
        doTestFirmataInitialization()

        // test interleaved set and clear of pins of different ports
        arduino!!.setDigitalArduinoPin(7, 1)
        testDigital(0x80, 7)
        arduino!!.setDigitalArduinoPin(13, 1)
        testDigital(0x20, 13)
        arduino!!.setDigitalArduinoPin(7, 0)
        testDigital(0x00, 7)
        arduino!!.setDigitalArduinoPin(8, 1)
        testDigital(0x21, 8)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSetAnalogArduinoPin() {
        arduino!!.initialise()
        doTestFirmataInitialization()
        var pin = 0
        for (i in 0 until ArduinoImpl.NUMBER_OF_ANALOG_PINS) {
            pin = i
            testAnalog(pin, 0)
            testAnalog(pin, 1)
            testAnalog(pin, 99)
            testAnalog(pin, 100)
            testAnalog(pin, 101)
            testAnalog(pin, 255)
            testAnalog(pin, 256)
            testAnalog(pin, 1024)
            testAnalog(pin, MAX_ANALOG_VALUE_FIRMATA)
        }
        testAnalogOutOfRange(pin, MAX_ANALOG_VALUE_FIRMATA + 1, 0)
        testAnalogOutOfRange(pin, -1, MAX_ANALOG_VALUE_FIRMATA)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testGetDigitalArduinoPin() {
        arduino!!.initialise()
        doTestFirmataInitialization()

        //TODO
    }

    @Test
    @Throws(MindstormsException::class)
    fun testGetAnalogArduinoPin() {
        arduino!!.initialise()
        doTestFirmataInitialization()

        //TODO
    }

    private fun doTestFirmataInitialization() {
        for (pin in ArduinoImpl.PWM_PINS) {
            val m = firmataUtils!!.setPinModeMessage
            Assert.assertEquals(SET_PIN_MODE_COMMAND, m.command)
            Assert.assertEquals(pin, m.pin)
            Assert.assertEquals(PWM_MODE, m.data)
        }
        testReportAnalogPin(true)
    }

    private fun testReportAnalogPin(enable: Boolean) {
        for (i in MIN_ANALOG_SENSOR_PIN..MAX_ANALOG_SENSOR_PIN) {
            val m = firmataUtils!!.reportAnalogPinMessage
            Assert.assertEquals(REPORT_ANALOG_PIN_COMMAND, m.command)
            Assert.assertEquals(i, m.pin)
            Assert.assertEquals(if (enable) 1 else 0, m.data)
        }
    }

    private fun testDigital(portValue: Int, pin: Int) {
        var m = firmataUtils!!.setPinModeMessage
        Assert.assertEquals(SET_PIN_MODE_COMMAND, m.command)
        Assert.assertEquals(pin, m.pin)
        Assert.assertEquals(OUTPUT_MODE, m.data)
        m = firmataUtils!!.digitalMessageData
        Assert.assertEquals(DIGITAL_MESSAGE_COMMAND, m.command)
        Assert.assertEquals(ArduinoImpl.getPortFromPin(pin), m.pin)
        Assert.assertEquals(portValue, m.data)
    }

    private fun checkAnalog(pin: Int, expectedValue: Int) {
        var m = firmataUtils!!.setPinModeMessage
        Assert.assertEquals(SET_PIN_MODE_COMMAND, m.command)
        Assert.assertEquals(pin, m.pin)
        Assert.assertEquals(PWM_MODE, m.data)
        m = firmataUtils!!.analogMessageData
        Assert.assertEquals(ANALOG_MESSAGE_COMMAND, m.command)
        Assert.assertEquals(pin, m.pin)
        Assert.assertEquals(expectedValue, m.data)
    }

    private fun testAnalog(pin: Int, value: Int) {
        arduino!!.setAnalogArduinoPin(pin, value)
        checkAnalog(pin, value)
    }

    private fun testAnalogOutOfRange(pin: Int, value: Int, expectedValue: Int) {
        arduino!!.setAnalogArduinoPin(pin, value)
        checkAnalog(pin, expectedValue)
    }

    companion object {
        private const val MIN_ANALOG_SENSOR_PIN = 0
        private const val MAX_ANALOG_SENSOR_PIN = 5
        private val OUTPUT_MODE = SetPinModeMessage.PIN_MODE.OUTPUT.mode
        private val PWM_MODE = SetPinModeMessage.PIN_MODE.PWM.mode
        private const val DIGITAL_MESSAGE_COMMAND = DigitalMessageWriter.COMMAND
        private const val ANALOG_MESSAGE_COMMAND = AnalogMessageWriter.COMMAND
        private const val REPORT_ANALOG_PIN_COMMAND = ReportAnalogPinMessageWriter.COMMAND
        private const val SET_PIN_MODE_COMMAND = SetPinModeMessageWriter.COMMAND
        private const val MAX_ANALOG_VALUE_FIRMATA = (1 shl 14) - 1
    }
}