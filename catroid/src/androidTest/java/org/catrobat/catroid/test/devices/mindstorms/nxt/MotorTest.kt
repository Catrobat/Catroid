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
import org.junit.runner.RunWith
import org.catrobat.catroid.devices.mindstorms.nxt.NXTMotor
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl
import org.catrobat.catroid.test.devices.mindstorms.nxt.MotorTest
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType
import org.junit.After
import org.junit.Test
import java.lang.Exception
import kotlin.experimental.or

@RunWith(AndroidJUnit4::class)
class MotorTest {
    private var motor: NXTMotor? = null
    private var logger: ConnectionDataLogger? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        val mindstormsConnection: MindstormsConnection =
            MindstormsConnectionImpl(logger?.getConnectionProxy())
        mindstormsConnection.init()
        motor = NXTMotor(USED_PORT, mindstormsConnection)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        logger!!.disconnectAndDestroy()
    }

    @Test
    fun testSimpleMotorTest() {
        val inputSpeed = 70
        val degrees = 360
        val motorRegulationSpeed: Byte = 0x01
        val expectedTurnRatio: Byte = 100
        motor!!.move(inputSpeed, degrees)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(DIRECT_COMMAND_HEADER, setOutputState[0])
        Assert.assertEquals(CommandByte.SET_OUTPUT_STATE.byte, setOutputState[1])
        Assert.assertEquals(USED_PORT, setOutputState[2].toInt())
        Assert.assertEquals(inputSpeed, setOutputState[3].toInt())
        Assert.assertEquals(
            NXTMotor.MotorMode.BREAK or NXTMotor.MotorMode.ON or NXTMotor.MotorMode.REGULATED,
            setOutputState[4]
        )
        Assert.assertEquals(motorRegulationSpeed, setOutputState[5])
        Assert.assertEquals(expectedTurnRatio, setOutputState[6])
        Assert.assertEquals(NXTMotor.MotorRunState.RUNNING.byte, setOutputState[7])
        checkDegrees(degrees, setOutputState)
    }

    @Test
    fun testMotorSpeedOverHundred() {
        val inputSpeed = 120
        val expectedSpeed = 100
        val degrees = 360
        val motorRegulationSpeed: Byte = 0x01
        val expectedTurnRatio: Byte = 100
        motor!!.move(inputSpeed, degrees)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(DIRECT_COMMAND_HEADER, setOutputState[0])
        Assert.assertEquals(CommandByte.SET_OUTPUT_STATE.byte, setOutputState[1])
        Assert.assertEquals(USED_PORT, setOutputState[2].toInt())
        Assert.assertEquals(expectedSpeed, setOutputState[3].toInt())
        Assert.assertEquals(
            NXTMotor.MotorMode.BREAK or NXTMotor.MotorMode.ON or NXTMotor.MotorMode.REGULATED,
            setOutputState[4]
        )
        Assert.assertEquals(motorRegulationSpeed, setOutputState[5])
        Assert.assertEquals(expectedTurnRatio, setOutputState[6])
        Assert.assertEquals(NXTMotor.MotorRunState.RUNNING.byte, setOutputState[7])
        checkDegrees(degrees, setOutputState)
    }

    @Test
    fun testMotorWithZeroValues() {
        val inputSpeed = 0
        val degrees = 0
        val motorRegulationSpeed: Byte = 0x01
        val expectedTurnRatio: Byte = 100
        motor!!.move(inputSpeed, degrees)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(DIRECT_COMMAND_HEADER, setOutputState[0])
        Assert.assertEquals(CommandByte.SET_OUTPUT_STATE.byte, setOutputState[1])
        Assert.assertEquals(USED_PORT, setOutputState[2].toInt())
        Assert.assertEquals(inputSpeed, setOutputState[3].toInt())
        Assert.assertEquals(
            NXTMotor.MotorMode.BREAK or NXTMotor.MotorMode.ON or NXTMotor.MotorMode.REGULATED,
            setOutputState[4]
        )
        Assert.assertEquals(motorRegulationSpeed, setOutputState[5])
        Assert.assertEquals(expectedTurnRatio, setOutputState[6])
        Assert.assertEquals(NXTMotor.MotorRunState.RUNNING.byte, setOutputState[7])
        checkDegrees(degrees, setOutputState)
    }

    @Test
    fun testMotorWithNegativeSpeedOverHundred() {
        val inputSpeed = -120
        val expectedSpeed = -100
        val degrees = 360
        val motorRegulationSpeed: Byte = 0x01
        val expectedTurnRatio: Byte = 100
        motor!!.move(inputSpeed, degrees)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(DIRECT_COMMAND_HEADER, setOutputState[0])
        Assert.assertEquals(CommandByte.SET_OUTPUT_STATE.byte, setOutputState[1])
        Assert.assertEquals(USED_PORT, setOutputState[2].toInt())
        Assert.assertEquals(expectedSpeed, setOutputState[3].toInt())
        Assert.assertEquals(
            NXTMotor.MotorMode.BREAK or NXTMotor.MotorMode.ON or NXTMotor.MotorMode.REGULATED,
            setOutputState[4]
        )
        Assert.assertEquals(motorRegulationSpeed, setOutputState[5])
        Assert.assertEquals(expectedTurnRatio, setOutputState[6])
        Assert.assertEquals(NXTMotor.MotorRunState.RUNNING.byte, setOutputState[7])
        checkDegrees(degrees, setOutputState)
    }

    fun checkDegrees(degrees: Int, setOutputState: ByteArray) {
        Assert.assertEquals(degrees.toByte(), setOutputState[8])
        Assert.assertEquals((degrees shr 8).toByte(), setOutputState[9])
    }

    companion object {
        private val DIRECT_COMMAND_HEADER: Byte = (CommandType.DIRECT_COMMAND.byte or 0x80.toByte())
        private const val USED_PORT = 0
    }
}