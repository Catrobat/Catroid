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

import android.content.Context
import org.junit.runner.RunWith
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.junit.Before
import kotlin.Throws
import androidx.test.core.app.ApplicationProvider
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXTImpl
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor
import org.catrobat.catroid.test.devices.mindstorms.nxt.LegoNXTImplTest
import org.junit.After
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class LegoNXTImplTest {
    private var applicationContext: Context? = null
    private var nxt: LegoNXT? = null
    private var logger: ConnectionDataLogger? = null
    private lateinit var sensorMappingBuffer: Array<NXTSensor.Sensor>
    private var nxtSettingBuffer = false
    private val defaultSensorMapping = arrayOf(
        NXTSensor.Sensor.TOUCH,
        NXTSensor.Sensor.SOUND,
        NXTSensor.Sensor.LIGHT_INACTIVE,
        NXTSensor.Sensor.ULTRASONIC
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext<Context>().applicationContext
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        nxtSettingBuffer = sharedPreferences
            .getBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true)
            .commit()
        sensorMappingBuffer =
            SettingsFragment.getLegoNXTSensorMapping(ApplicationProvider.getApplicationContext())
        setSensorMapping(
            arrayOf(
                NXTSensor.Sensor.NO_SENSOR,
                NXTSensor.Sensor.NO_SENSOR,
                NXTSensor.Sensor.NO_SENSOR,
                NXTSensor.Sensor.NO_SENSOR
            )
        )
        nxt = LegoNXTImpl(applicationContext)
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        (nxt as LegoNXTImpl).setConnection(logger?.connectionProxy)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        nxt!!.disconnect()
        logger!!.disconnectAndDestroy()
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
            .edit()
            .putBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, nxtSettingBuffer)
            .commit()
        setSensorMapping(sensorMappingBuffer)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSensorAssignment() {
        val sensorMapping = arrayOf(
            NXTSensor.Sensor.LIGHT_INACTIVE,
            NXTSensor.Sensor.SOUND,
            NXTSensor.Sensor.TOUCH,
            NXTSensor.Sensor.ULTRASONIC
        )
        setSensorMapping(sensorMapping)
        nxt!!.initialise()
        Assert.assertNotNull(nxt!!.motorA)
        Assert.assertNotNull(nxt!!.motorB)
        Assert.assertNotNull(nxt!!.motorC)
        Assert.assertNotNull(nxt!!.sensor1)
        Assert.assertTrue(nxt!!.sensor1 is NXTLightSensor)
        Assert.assertNotNull(nxt!!.sensor2)
        Assert.assertTrue(nxt!!.sensor2 is NXTSoundSensor)
        Assert.assertNotNull(nxt!!.sensor3)
        Assert.assertTrue(nxt!!.sensor3 is NXTTouchSensor)
        Assert.assertNotNull(nxt!!.sensor4)
        Assert.assertTrue(nxt!!.sensor4 is NXTI2CUltraSonicSensor)
    }

    @Test
    @Throws(InterruptedException::class, MindstormsException::class)
    fun testSensorAssignmentChange() {
        setSensorMapping(defaultSensorMapping)
        nxt!!.initialise()
        SettingsFragment.setLegoMindstormsNXTSensorMapping(
            applicationContext,
            NXTSensor.Sensor.LIGHT_INACTIVE, SettingsFragment.NXT_SENSORS[0]
        )
        Thread.sleep(PREFERENCES_SAVE_BROADCAST_DELAY.toLong())
        Assert.assertNotNull(nxt!!.sensor1)
        Assert.assertTrue(nxt!!.sensor1 is NXTLightSensor)
        SettingsFragment.setLegoMindstormsNXTSensorMapping(
            applicationContext,
            NXTSensor.Sensor.TOUCH, SettingsFragment.NXT_SENSORS[0]
        )
        Thread.sleep(PREFERENCES_SAVE_BROADCAST_DELAY.toLong())
        Assert.assertNotNull(nxt!!.sensor1)
        Assert.assertTrue(nxt!!.sensor1 is NXTTouchSensor)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testSimplePlayToneTest() {
        val inputHz = 100
        val expectedHz = 10000
        val durationInMs = 3000
        nxt!!.initialise()
        nxt!!.playTone(inputHz * 100, durationInMs)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedHz.toByte(), setOutputState[2])
        Assert.assertEquals((expectedHz shr 8).toByte(), setOutputState[3])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testPlayToneHzOverMaxValue() {
        val inputHz = 160
        val expectedHz = 14000
        val durationInMs = 5000
        nxt!!.initialise()
        nxt!!.playTone(inputHz * 100, durationInMs)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedHz.toByte(), setOutputState[2])
        Assert.assertEquals((expectedHz shr 8).toByte(), setOutputState[3])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testCheckDurationOfTone() {
        val inputHz = 130
        val inputDurationInS = 5.5f
        val inputDurationInMs = (inputDurationInS * 1000).toInt()
        val expectedDurationInMs = 5500
        nxt!!.initialise()
        nxt!!.playTone(inputHz * 100, inputDurationInMs)
        val setOutputState = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedDurationInMs.toByte(), setOutputState[4])
        Assert.assertEquals((expectedDurationInMs shr 8).toByte(), setOutputState[5])
    }

    @Test
    @Throws(MindstormsException::class)
    fun testWithZeroDuration() {
        val inputHz = 13000
        val inputDurationInMs = 0
        nxt!!.initialise()
        nxt!!.playTone(inputHz, inputDurationInMs)
        val commands = logger!!.getSentMessages(2, 0)
        Assert.assertEquals(0, commands.size)
    }

    private fun setSensorMapping(sensorMapping: Array<NXTSensor.Sensor>) {
        val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
        for (i in SettingsFragment.NXT_SENSORS.indices) {
            editor.putString(SettingsFragment.NXT_SENSORS[i], sensorMapping[i].sensorCode)
        }
        editor.commit()
    }

    companion object {
        private const val PREFERENCES_SAVE_BROADCAST_DELAY = 50
    }
}