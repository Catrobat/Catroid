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
package org.catrobat.catroid.test.devices.phiro

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.base.Stopwatch
import junit.framework.Assert
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.catrobat.catroid.common.firmata.FirmataUtils
import org.catrobat.catroid.devices.arduino.phiro.Phiro
import org.catrobat.catroid.devices.arduino.phiro.PhiroImpl
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PhiroImplTest {
    private var phiro: Phiro? = null
    private var logger: ConnectionDataLogger? = null
    private var firmataUtils: FirmataUtils? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        phiro = PhiroImpl()
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        firmataUtils = FirmataUtils(logger)
        (phiro as PhiroImpl).setConnection(logger?.connectionProxy)
        (phiro as PhiroImpl).initialise()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        phiro!!.disconnect()
        logger!!.disconnectAndDestroy()
    }

    @Test
    fun testMoveLeftMotorForward() {
        doTestFirmataInitialization()
        phiro!!.moveLeftMotorForward(SPEED_IN_PERCENT)
        testSpeed(SPEED_IN_PERCENT, PIN_LEFT_MOTOR_SPEED)
    }

    @Test
    fun testMoveLeftMotorBackward() {
        doTestFirmataInitialization()
        phiro!!.moveLeftMotorBackward(SPEED_IN_PERCENT)
        testSpeed(SPEED_IN_PERCENT, PIN_LEFT_MOTOR_SPEED)
    }

    @Test
    fun testMoveRightMotorForward() {
        doTestFirmataInitialization()
        phiro!!.moveRightMotorForward(SPEED_IN_PERCENT)
        testSpeed(SPEED_IN_PERCENT, PIN_RIGHT_MOTOR_SPEED)
    }

    @Test
    fun testMoveRightMotorBackward() {
        doTestFirmataInitialization()
        phiro!!.moveRightMotorBackward(SPEED_IN_PERCENT)
        testSpeed(SPEED_IN_PERCENT, PIN_RIGHT_MOTOR_SPEED)
    }

    @Test
    fun testStopLeftMotor() {
        doTestFirmataInitialization()
        phiro!!.stopLeftMotor()
        testSpeed(0, PIN_LEFT_MOTOR_SPEED)
        testSpeed(0, PIN_LEFT_MOTOR_FORWARD_BACKWARD)
    }

    @Test
    fun testStopRightMotor() {
        doTestFirmataInitialization()
        phiro!!.stopRightMotor()
        testSpeed(0, PIN_RIGHT_MOTOR_SPEED)
    }

    @Test
    fun testStopAllMovements() {
        doTestFirmataInitialization()
        phiro!!.stopAllMovements()
        testSpeed(0, PIN_LEFT_MOTOR_SPEED)
        testSpeed(0, PIN_LEFT_MOTOR_FORWARD_BACKWARD)
        testSpeed(0, PIN_RIGHT_MOTOR_SPEED)
    }

    @Test
    fun testSetLeftRGBLightColor() {
        doTestFirmataInitialization()
        val red = 242
        val green = 0
        val blue = 3
        phiro!!.setLeftRGBLightColor(red, green, blue)
        testLight(red, PIN_RGB_RED_LEFT)
        testLight(green, PIN_RGB_GREEN_LEFT)
        testLight(blue, PIN_RGB_BLUE_LEFT)
    }

    @Test
    fun testSetRightRGBLightColor() {
        doTestFirmataInitialization()
        val red = 242
        val green = 1
        val blue = 3
        phiro!!.setRightRGBLightColor(red, green, blue)
        testLight(red, PIN_RGB_RED_RIGHT)
        testLight(green, PIN_RGB_GREEN_RIGHT)
        testLight(blue, PIN_RGB_BLUE_RIGHT)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testPlayTone() {
        doTestFirmataInitialization()
        val tone = 294
        val durationInSeconds = 1
        phiro!!.playTone(tone, durationInSeconds)
        var m = firmataUtils!!.analogMessageData
        Assert.assertEquals(ANALOG_MESSAGE_COMMAND, m.command)
        Assert.assertEquals(PIN_SPEAKER_OUT, m.pin)
        Assert.assertEquals(tone, m.data)
        val stopwatch = Stopwatch.createStarted()
        while (stopwatch.elapsed(TimeUnit.SECONDS) < durationInSeconds) {
            Assert.assertEquals(0, logger!!.getSentMessages(0).size)
            Thread.sleep((durationInSeconds * 100).toLong())
        }
        m = firmataUtils!!.analogMessageData
        Assert.assertEquals(ANALOG_MESSAGE_COMMAND, m.command)
        Assert.assertEquals(PIN_SPEAKER_OUT, m.pin)
        Assert.assertEquals(0, m.data)
    }

    private fun doTestFirmataInitialization() {
        for (i in MIN_PWM_PIN..MAX_PWM_PIN) {
            val m = firmataUtils!!.setPinModeMessage
            Assert.assertEquals(SET_PIN_MODE_COMMAND, m.command)
            Assert.assertEquals(i, m.pin)
            Assert.assertEquals(PWM_MODE, m.data)
        }
        testReportAnalogPin(true)
    }

    private fun testReportAnalogPin(enable: Boolean) {
        for (i in MIN_SENSOR_PIN..MAX_SENSOR_PIN) {
            val m = firmataUtils!!.reportAnalogPinMessage
            Assert.assertEquals(REPORT_ANALOG_PIN_COMMAND, m.command)
            Assert.assertEquals(i, m.pin)
            Assert.assertEquals(if (enable) 1 else 0, m.data)
        }
    }

    private fun testSpeed(speedInPercent: Int, pin: Int) {
        val speed = percentToSpeed(speedInPercent)
        val m = firmataUtils!!.analogMessageData
        Assert.assertEquals(ANALOG_MESSAGE_COMMAND, m.command)
        Assert.assertEquals(pin, m.pin)
        Assert.assertEquals(speed, m.data)
    }

    private fun testLight(color: Int, pin: Int) {
        val m = firmataUtils!!.analogMessageData
        Assert.assertEquals(ANALOG_MESSAGE_COMMAND, m.command)
        Assert.assertEquals(pin, m.pin)
        Assert.assertEquals(color, m.data)
    }

    private fun percentToSpeed(percent: Int): Int {
        if (percent <= 0) {
            return 0
        }
        return if (percent >= 100) {
            255
        } else (percent * 2.55).toInt()
    }

    companion object {
        private const val PIN_SPEAKER_OUT = 3
        private const val PIN_RGB_RED_RIGHT = 4
        private const val PIN_RGB_GREEN_RIGHT = 5
        private const val PIN_RGB_BLUE_RIGHT = 6
        private const val PIN_RGB_RED_LEFT = 7
        private const val PIN_RGB_GREEN_LEFT = 8
        private const val PIN_RGB_BLUE_LEFT = 9
        private const val PIN_LEFT_MOTOR_SPEED = 10
        private const val PIN_LEFT_MOTOR_FORWARD_BACKWARD = 11
        private const val PIN_RIGHT_MOTOR_SPEED = 12
        private const val MIN_PWM_PIN = 2
        private const val MAX_PWM_PIN = 13
        private const val MIN_SENSOR_PIN = 0
        private const val MAX_SENSOR_PIN = 5
        private const val PWM_MODE = 3
        private const val SET_PIN_MODE_COMMAND = 0xF4
        private const val REPORT_ANALOG_PIN_COMMAND = 0xC0
        private const val ANALOG_MESSAGE_COMMAND = 0xE0
        private const val SPEED_IN_PERCENT = 42
    }
}