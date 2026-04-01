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

package org.catrobat.catroid.test.devices.mindstorms.ev3;

import android.content.Context;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorMode;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorType;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LegoEV3SensorTest {

	private Context applicationContext;

	private LegoEV3 ev3;
	private final int expectedPort = 0;
	ConnectionDataLogger logger;

	@Before
	public void setUp() throws Exception {
		applicationContext = ApplicationProvider.getApplicationContext().getApplicationContext();
		ev3 = new LegoEV3Impl(applicationContext);

		logger = ConnectionDataLogger.createLocalConnectionLogger();
		ev3.setConnection(logger.getConnectionProxy());
	}

	@Test
	public void testTouchSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.EV3_TOUCH;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE0;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.TOUCH, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkPercentValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testNxtLightActiveSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.NXT_LIGHT;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE0;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.NXT_LIGHT_ACTIVE, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkPercentValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testNxtLightSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.NXT_LIGHT;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE1;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.NXT_LIGHT, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkPercentValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testColorSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.EV3_COLOR;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE2;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.COLOR, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkRawValueCommand();
	}

	@Test
	public void testEV3ColorReflectSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.EV3_COLOR;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE1;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.COLOR_REFLECT, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkPercentValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testEV3ColorAmbientSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.EV3_COLOR;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE0;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.COLOR_AMBIENT, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkPercentValueCommand(expectedType, expectedMode);
	}

	public void testHiTechnicColorSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.IIC;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE1;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.HT_NXT_COLOR, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkRawValueCommand();
	}

	@Test
	public void testNxtTemperatureFSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.NXT_TEMPERATURE;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE1;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.NXT_TEMPERATURE_F, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkSiValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testTemperatureCSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.NXT_TEMPERATURE;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE0;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.NXT_TEMPERATURE_C, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkSiValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testNXTSoundSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.NXT_SOUND;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE1;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.NXT_SOUND, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkPercentValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testInfraredSensor() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.EV3_INFRARED;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE0;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.INFRARED, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkPercentValueCommand(expectedType, expectedMode);
	}

	@Test
	public void testEV3UltrasonicSensorNXT() throws MindstormsException {
		final EV3SensorType expectedType = EV3SensorType.NXT_ULTRASONIC;
		final EV3SensorMode expectedMode = EV3SensorMode.MODE0;

		SettingsFragment.setLegoMindstormsEV3SensorMapping(applicationContext, EV3Sensor.Sensor.NXT_ULTRASONIC, SettingsFragment.EV3_SENSORS[0]);

		initSensor();
		ev3.getSensor1().updateLastSensorValue();

		checkInitializationCommand(expectedType, expectedMode);
		checkRawValueCommand();
	}

	private void checkInitializationCommand(EV3SensorType expectedType, EV3SensorMode expectedMode) {
		final int expectedCommandCounter = 1;

		byte[] commandBytes = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedCommandCounter, commandBytes[0]);
		assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_DEVICE.getByte(), commandBytes[5]);
		assertEquals(EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_READY_RAW.getByte(), commandBytes[6]);
		assertEquals((byte) expectedPort, commandBytes[8]);
		assertEquals(expectedType.getByte(), commandBytes[10]);
		assertEquals(expectedMode.getByte(), commandBytes[11]);
	}

	private void checkPercentValueCommand(EV3SensorType expectedType, EV3SensorMode expectedMode) {
		final int expectedCommandCounter = 2;

		byte[] commandBytes = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedCommandCounter, commandBytes[0]);
		assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_READ.getByte(), commandBytes[5]);
		assertEquals((byte) expectedPort, commandBytes[7]);
		assertEquals(expectedType.getByte(), commandBytes[9]);
		assertEquals(expectedMode.getByte(), commandBytes[10]);
	}

	private void checkSiValueCommand(EV3SensorType expectedType, EV3SensorMode expectedMode) {
		final int expectedCommandCounter = 2;

		byte[] commandBytes = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedCommandCounter, commandBytes[0]);
		assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_READ_SI.getByte(), commandBytes[5]);
		assertEquals((byte) expectedPort, commandBytes[7]);
		assertEquals(expectedType.getByte(), commandBytes[9]);
		assertEquals(expectedMode.getByte(), commandBytes[10]);
	}

	private void checkRawValueCommand() {
		final int expectedCommandCounter = 2;

		byte[] commandBytes = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedCommandCounter, commandBytes[0]);
		assertEquals(EV3CommandByte.EV3CommandOpCode.OP_INPUT_DEVICE.getByte(), commandBytes[5]);
		assertEquals(EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_GET_RAW.getByte(), commandBytes[6]);
		assertEquals((byte) expectedPort, commandBytes[8]);
	}

	private void initSensor() throws MindstormsException {
		ev3.initialise();

		ev3.getSensor1().updateLastSensorValue(); // First time the Sensor gets initialized
	}
}
