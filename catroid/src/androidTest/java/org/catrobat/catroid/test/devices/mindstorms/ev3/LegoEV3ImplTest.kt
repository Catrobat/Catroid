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
import org.junit.runner.RunWith
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.junit.Before
import kotlin.Throws
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.test.devices.mindstorms.ev3.LegoEV3ImplTest
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class LegoEV3ImplTest {
    private var applicationContext: Context? = null
    private var ev3: LegoEV3? = null
    var logger: ConnectionDataLogger? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext<Context>().applicationContext
        ev3 = LegoEV3Impl(applicationContext)
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        logger?.setTimeoutMilliSeconds(100)
        (ev3 as LegoEV3Impl).setConnection(logger?.getConnectionProxy())
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSimplePlayToneTest() {
        val inputHz = 9000
        val expectedHz = 9000
        val durationInMs = 3000
        val volume = 100
        ev3!!.initialise()
        ev3!!.playTone(inputHz, durationInMs, volume)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        val offset =
            BASIC_MESSAGE_BYTE_OFFSET + 3 // 1 byte command, 1 bytes volume, 1 byte datatype
        Assert.assertEquals(expectedHz.toByte(), setOutputState[offset])
        Assert.assertEquals((expectedHz shr 8).toByte(), setOutputState[offset + 1])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testPlayToneHzOverMaxValue() {

        // MaxHz = 10000;
        val inputHz = 16000
        val expectedHz = 10000
        val durationInMs = 5000
        val volume = 100
        ev3!!.initialise()
        ev3!!.playTone(inputHz, durationInMs, volume)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        val offset =
            BASIC_MESSAGE_BYTE_OFFSET + 3 // 1 byte command, 1 bytes volume, 1 byte datatype
        Assert.assertEquals(expectedHz.toByte(), setOutputState[offset])
        Assert.assertEquals((expectedHz shr 8).toByte(), setOutputState[offset + 1])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testPlayToneCheckDuration() {
        val inputHz = 9000
        val durationInMs = 2000
        val volume = 100
        val expectedDurationInMs = 2000
        ev3!!.initialise()
        ev3!!.playTone(inputHz, durationInMs, volume)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        val offset =
            BASIC_MESSAGE_BYTE_OFFSET + 6 // 1 byte command, 1 bytes volume, 3 bytes freq, 1 byte datatype
        Assert.assertEquals(expectedDurationInMs.toByte(), setOutputState[offset])
        Assert.assertEquals((expectedDurationInMs shr 8).toByte(), setOutputState[offset + 1])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testPlayToneCheckVolume() {
        val inputHz = 9000
        val durationInMs = 2000
        val volume1 = 100
        val expectedVolumeLevel1 = 13
        ev3!!.initialise()
        ev3!!.playTone(inputHz, durationInMs, volume1)
        var setOutputState = logger!!.getNextSentMessage(0, 2)
        val offset = BASIC_MESSAGE_BYTE_OFFSET + 1 // 1 byte command
        Assert.assertEquals(expectedVolumeLevel1.toByte(), setOutputState[offset])
        val volume2 = 25
        val expectedVolumeLevel2 = 4
        ev3!!.playTone(inputHz, durationInMs, volume2)
        setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedVolumeLevel2.toByte(), setOutputState[offset])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testPlayToneWithZeroDuration() {
        val inputHz = 13000
        val inputDurationInMs = 0
        val volume = 100
        ev3!!.initialise()
        ev3!!.playTone(inputHz, inputDurationInMs, volume)
        val command = logger!!.getNextSentMessage(0, 2)
        Assert.assertNull(command)
    }

    @Test
    @Throws(Exception::class)
    fun testPlayToneWithZeroVolume() {
        val inputHz = 13000
        val inputDurationInMs = 0
        val volume = 0
        ev3!!.initialise()
        ev3!!.playTone(inputHz, inputDurationInMs, volume)
        val command = logger!!.getNextSentMessage(0, 2)
        Assert.assertNull(command)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSimpleLED() {
        val ledStatus = 0x04
        val expectedLedStatus = 0x04
        ev3!!.initialise()
        ev3!!.setLed(ledStatus)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        val offset = BASIC_MESSAGE_BYTE_OFFSET + 2 // 1 byte command, 1 byte datatype
        Assert.assertEquals(expectedLedStatus.toByte(), setOutputState[offset])
    }

    companion object {
        private const val BASIC_MESSAGE_BYTE_OFFSET = 6
    }
}