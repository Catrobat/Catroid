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
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorType
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorMode
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class LegoEV3SensorTest {
    private var applicationContext: Context? = null
    private var ev3: LegoEV3? = null
    private val expectedPort = 0
    var logger: ConnectionDataLogger? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext<Context>().applicationContext
        ev3 = LegoEV3Impl(applicationContext)
        logger = ConnectionDataLogger.createLocalConnectionLogger()
        (ev3 as LegoEV3Impl).setConnection(logger?.connectionProxy)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testTouchSensor() {
        val expectedType = EV3SensorType.EV3_TOUCH
        val expectedMode = EV3SensorMode.MODE0
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.TOUCH,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkPercentValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testNxtLightActiveSensor() {
        val expectedType = EV3SensorType.NXT_LIGHT
        val expectedMode = EV3SensorMode.MODE0
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.NXT_LIGHT_ACTIVE,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkPercentValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testNxtLightSensor() {
        val expectedType = EV3SensorType.NXT_LIGHT
        val expectedMode = EV3SensorMode.MODE1
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.NXT_LIGHT,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkPercentValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testColorSensor() {
        val expectedType = EV3SensorType.EV3_COLOR
        val expectedMode = EV3SensorMode.MODE2
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.COLOR,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkRawValueCommand()
    }

    @Test
    @Throws(MindstormsException::class)
    fun testEV3ColorReflectSensor() {
        val expectedType = EV3SensorType.EV3_COLOR
        val expectedMode = EV3SensorMode.MODE1
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.COLOR_REFLECT,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkPercentValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testEV3ColorAmbientSensor() {
        val expectedType = EV3SensorType.EV3_COLOR
        val expectedMode = EV3SensorMode.MODE0
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.COLOR_AMBIENT,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkPercentValueCommand(expectedType, expectedMode)
    }

    @Throws(MindstormsException::class)
    fun testHiTechnicColorSensor() {
        val expectedType = EV3SensorType.IIC
        val expectedMode = EV3SensorMode.MODE1
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.HT_NXT_COLOR,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkRawValueCommand()
    }

    @Test
    @Throws(MindstormsException::class)
    fun testNxtTemperatureFSensor() {
        val expectedType = EV3SensorType.NXT_TEMPERATURE
        val expectedMode = EV3SensorMode.MODE1
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.NXT_TEMPERATURE_F,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkSiValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testTemperatureCSensor() {
        val expectedType = EV3SensorType.NXT_TEMPERATURE
        val expectedMode = EV3SensorMode.MODE0
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.NXT_TEMPERATURE_C,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkSiValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testNXTSoundSensor() {
        val expectedType = EV3SensorType.NXT_SOUND
        val expectedMode = EV3SensorMode.MODE1
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.NXT_SOUND,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkPercentValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testInfraredSensor() {
        val expectedType = EV3SensorType.EV3_INFRARED
        val expectedMode = EV3SensorMode.MODE0
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.INFRARED,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkPercentValueCommand(expectedType, expectedMode)
    }

    @Test
    @Throws(MindstormsException::class)
    fun testEV3UltrasonicSensorNXT() {
        val expectedType = EV3SensorType.NXT_ULTRASONIC
        val expectedMode = EV3SensorMode.MODE0
        SettingsFragment.setLegoMindstormsEV3SensorMapping(
            applicationContext,
            EV3Sensor.Sensor.NXT_ULTRASONIC,
            SettingsFragment.EV3_SENSORS[0]
        )
        initSensor()
        ev3!!.sensor1.updateLastSensorValue()
        checkInitializationCommand(expectedType, expectedMode)
        checkRawValueCommand()
    }

    private fun checkInitializationCommand(
        expectedType: EV3SensorType,
        expectedMode: EV3SensorMode
    ) {
        val expectedCommandCounter = 1
        val commandBytes = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedCommandCounter.toByte(), commandBytes[0])
        Assert.assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_DEVICE.byte, commandBytes[5])
        Assert.assertEquals(
            EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_READY_RAW.byte,
            commandBytes[6]
        )
        Assert.assertEquals(expectedPort.toByte(), commandBytes[8])
        Assert.assertEquals(expectedType.byte, commandBytes[10])
        Assert.assertEquals(expectedMode.byte, commandBytes[11])
    }

    private fun checkPercentValueCommand(expectedType: EV3SensorType, expectedMode: EV3SensorMode) {
        val expectedCommandCounter = 2
        val commandBytes = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedCommandCounter.toByte(), commandBytes[0])
        Assert.assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_READ.byte, commandBytes[5])
        Assert.assertEquals(expectedPort.toByte(), commandBytes[7])
        Assert.assertEquals(expectedType.byte, commandBytes[9])
        Assert.assertEquals(expectedMode.byte, commandBytes[10])
    }

    private fun checkSiValueCommand(expectedType: EV3SensorType, expectedMode: EV3SensorMode) {
        val expectedCommandCounter = 2
        val commandBytes = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedCommandCounter.toByte(), commandBytes[0])
        Assert.assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_READ_SI.byte, commandBytes[5])
        Assert.assertEquals(expectedPort.toByte(), commandBytes[7])
        Assert.assertEquals(expectedType.byte, commandBytes[9])
        Assert.assertEquals(expectedMode.byte, commandBytes[10])
    }

    private fun checkRawValueCommand() {
        val expectedCommandCounter = 2
        val commandBytes = logger!!.getNextSentMessage(0, 2)
        Assert.assertEquals(expectedCommandCounter.toByte(), commandBytes[0])
        Assert.assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_DEVICE.byte, commandBytes[5])
        Assert.assertEquals(
            EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_GET_RAW.byte,
            commandBytes[6]
        )
        Assert.assertEquals(expectedPort.toByte(), commandBytes[8])
    }

    @Throws(MindstormsException::class)
    private fun initSensor() {
        ev3!!.initialise()
        ev3!!.sensor1.updateLastSensorValue() // First time the Sensor gets initialized
    }
}