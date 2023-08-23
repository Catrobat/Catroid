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
package org.catrobat.catroid.test.devices.mindstorms.ev3

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.experimental.and
import kotlin.experimental.or

@RunWith(AndroidJUnit4::class)
class LegoEV3MotorTest {
    private var applicationContext: Context? = null
    private var ev3: LegoEV3? = null
    var logger: ConnectionDataLogger? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext<Context>().applicationContext
        ev3 = LegoEV3Impl(applicationContext)
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        (ev3 as LegoEV3Impl).setConnection(logger?.getConnectionProxy())
    }

    @Test
    @Throws(MindstormsException::class)
    fun testMotorMoveTest() {
        val inputSpeed = -70
        val outputField = 0x01.toByte()
        val expectedSpeed: Byte =
            (EV3CommandByte.EV3CommandParamByteCode.PARAM_SHORT_MAX.byte and inputSpeed.toByte()
                or EV3CommandByte.EV3CommandParamByteCode.PARAM_SHORT_SIGN_NEGATIVE.byte)
        val expectedOutputField = 0x01.toByte()
        val startCmdCode = 0xA6.toByte()
        ev3!!.initialise()
        ev3!!.moveMotorSpeed(outputField, 0, inputSpeed)
        var setOutputState = logger!!.getNextSentMessage(0, 2)
        var offset = BASIC_MESSAGE_BYTE_OFFSET + 1
        Assert.assertEquals(expectedOutputField, setOutputState[offset])
        offset += 1
        Assert.assertEquals(expectedSpeed.toByte(), setOutputState[offset])
        setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(startCmdCode, setOutputState[5])
        offset = BASIC_MESSAGE_BYTE_OFFSET + 1
        Assert.assertEquals(expectedOutputField, setOutputState[offset])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testStopMotorTest() {
        val outputField = 0x01.toByte()
        val expectedOutputField = 0x01.toByte()
        ev3!!.initialise()
        ev3!!.stopMotor(outputField, 0, true)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        val offset = BASIC_MESSAGE_BYTE_OFFSET + 1
        Assert.assertEquals(expectedOutputField, setOutputState[offset])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testMotorTurnAngle360DegreeTest() {
        val step2Degrees = 360 - POWER_DOWN_RAMP_DEGREES
        val step3Degrees = POWER_DOWN_RAMP_DEGREES
        val inputSpeed = -70
        val outputField = 0x01.toByte()
        val expectedStep1Degrees = 0
        val expectedStep2Degrees = 360 - POWER_DOWN_RAMP_DEGREES
        val expectedStep3Degrees = POWER_DOWN_RAMP_DEGREES
        val expectedSpeed = -70
        val expectedOutputField = 0x01.toByte()
        ev3!!.initialise()
        ev3!!.moveMotorStepsSpeed(outputField, 0, inputSpeed, 0, step2Degrees, step3Degrees, true)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        var offset = BASIC_MESSAGE_BYTE_OFFSET + 1
        Assert.assertEquals(expectedOutputField, setOutputState[offset])
        offset += 2
        Assert.assertEquals(expectedSpeed.toByte(), setOutputState[offset])
        offset += 1
        Assert.assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset])
        offset += 1
        Assert.assertEquals(expectedStep1Degrees.toByte(), setOutputState[offset])
        offset += 1
        Assert.assertEquals(LONG_PARAMETER_BYTE_TWO_FOLLOW, setOutputState[offset])
        offset += 1
        Assert.assertEquals(expectedStep2Degrees.toByte(), setOutputState[offset])
        Assert.assertEquals((expectedStep2Degrees shr 8).toByte(), setOutputState[offset + 1])
        offset += 2
        Assert.assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset])
        offset += 1
        Assert.assertEquals(expectedStep3Degrees.toByte(), setOutputState[offset])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testMotorTurnAngleMinus15DegreeTest() {
        val step2Degrees = 15
        val step3Degrees = 0
        val inputSpeed = -70
        val outputField = 0x01.toByte()
        val expectedStep1Degrees = 0
        val expectedStep2Degrees = 15
        val expectedStep3Degrees = 0
        val expectedSpeed = -70
        val expectedOutputField = 0x01.toByte()
        ev3!!.initialise()
        ev3!!.moveMotorStepsSpeed(outputField, 0, inputSpeed, 0, step2Degrees, step3Degrees, true)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        var offset = BASIC_MESSAGE_BYTE_OFFSET + 1
        Assert.assertEquals(expectedOutputField, setOutputState[offset])
        offset += 2
        Assert.assertEquals(expectedSpeed.toByte(), setOutputState[offset])
        offset += 1
        Assert.assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset])
        offset += 1
        Assert.assertEquals(expectedStep1Degrees.toByte(), setOutputState[offset])
        offset += 1
        Assert.assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset])
        offset += 1
        Assert.assertEquals(expectedStep2Degrees.toByte(), setOutputState[offset])
        offset += 1
        Assert.assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset])
        offset += 1
        Assert.assertEquals(expectedStep3Degrees.toByte(), setOutputState[offset])
    }

    companion object {
        private const val BASIC_MESSAGE_BYTE_OFFSET = 6
        private const val POWER_DOWN_RAMP_DEGREES = 20
        private const val LONG_PARAMETER_BYTE_ONE_FOLLOW = 0x81.toByte()
        private const val LONG_PARAMETER_BYTE_TWO_FOLLOW = 0x82.toByte()
    }
}