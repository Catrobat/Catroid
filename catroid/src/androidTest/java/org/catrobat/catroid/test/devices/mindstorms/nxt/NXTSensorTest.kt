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

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.rules.ExpectedException
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.devices.mindstorms.MindstormsCommand
import org.catrobat.catroid.devices.mindstorms.nxt.Command
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte
import org.catrobat.catroid.devices.mindstorms.nxt.NXTReply
import org.catrobat.catroid.devices.mindstorms.nxt.NXTException
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor
import org.catrobat.catroid.test.devices.mindstorms.nxt.NXTSensorTest
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensorActive
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory
import java.lang.Exception
import java.util.Arrays

@RunWith(Parameterized::class)
class NXTSensorTest {
    @get:Rule
    val exception = ExpectedException.none()
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var nxtSensorClass: Class<NXTSensor>? = null
    @JvmField
    @Parameterized.Parameter(2)
    var sensorType: NXTSensorType? = null
    @JvmField
    @Parameterized.Parameter(3)
    var sensorMode: NXTSensorMode? = null
    @JvmField
    @Parameterized.Parameter(4)
    var port: Byte = 0
    @JvmField
    @Parameterized.Parameter(5)
    var expectedSensorValue = 0
    private var mindstormsConnection: MindstormsConnection? = null
    private var nxtSensor: NXTSensor? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        mindstormsConnection = Mockito.mock(MindstormsConnection::class.java)
        Mockito.`when`(mindstormsConnection?.isConnected).thenReturn(true)
        nxtSensor = nxtSensorClass!!.getConstructor(
            Int::class.javaPrimitiveType,
            MindstormsConnection::class.java
        ).newInstance(port, mindstormsConnection)
        nxtSensor?.hasInit = true
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
        Assert.assertEquals(expectedScaledValue.toFloat(), nxtSensor!!.value)
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
        Assert.assertEquals((256f * expectedScaledValue), nxtSensor!!.value)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testResetScaledValue() {
        val command =
            Command(CommandType.DIRECT_COMMAND, CommandByte.RESET_INPUT_SCALED_VALUE, false)
        command.append(port)
        nxtSensor!!.resetScaledValue()
        Mockito.verify(mindstormsConnection, VerificationModeFactory.times(1))
            ?.send(ArgumentMatchers.eq(command))
    }

    @Test
    @Throws(MindstormsException::class)
    fun testUpdateTypeAndMode() {
        Mockito.`when`(mindstormsConnection!!.sendAndReceive(ArgumentMatchers.any())).thenReturn(
            byteArrayOf(
                CommandType.REPLY_COMMAND.byte,
                CommandByte.LS_READ.byte,
                NXTReply.NO_ERROR
            )
        )
        val command = Command(CommandType.DIRECT_COMMAND, CommandByte.SET_INPUT_MODE, true)
        command.append(port)
        command.append(sensorType!!.byte)
        command.append(sensorMode!!.byte)
        nxtSensor!!.updateTypeAndMode()
        Mockito.verify(mindstormsConnection, VerificationModeFactory.times(1))?.sendAndReceive(
            ArgumentMatchers.eq(command)
        )
    }

    companion object {
        private const val PORT_NR_0: Byte = 0
        private const val PORT_NR_1: Byte = 1
        private const val PORT_NR_2: Byte = 2
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "NXTTouchSensor",
                        NXTTouchSensor::class.java,
                        NXTSensorType.TOUCH,
                        NXTSensorMode.BOOL,
                        PORT_NR_0,
                        1
                    ),
                    arrayOf(
                        "NXTSoundSensor",
                        NXTSoundSensor::class.java,
                        NXTSensorType.SOUND_DBA,
                        NXTSensorMode.Percent,
                        PORT_NR_1,
                        42
                    ),
                    arrayOf(
                        "NXTLightSensor",
                        NXTLightSensor::class.java,
                        NXTSensorType.LIGHT_INACTIVE,
                        NXTSensorMode.Percent,
                        PORT_NR_2,
                        24
                    ),
                    arrayOf(
                        "NXTLightSensorActive",
                        NXTLightSensorActive::class.java,
                        NXTSensorType.LIGHT_ACTIVE,
                        NXTSensorMode.Percent,
                        PORT_NR_2,
                        33
                    )
                )
            )
        }
    }
}