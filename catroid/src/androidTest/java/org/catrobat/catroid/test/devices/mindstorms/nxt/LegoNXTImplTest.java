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
package org.catrobat.catroid.test.devices.mindstorms.nxt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXTImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.NXT_SENSORS;

@RunWith(AndroidJUnit4.class)
public class LegoNXTImplTest {

	private Context applicationContext;

	private LegoNXT nxt;
	private ConnectionDataLogger logger;

	private NXTSensor.Sensor[] sensorMappingBuffer;
	private boolean nxtSettingBuffer;

	private NXTSensor.Sensor[] defaultSensorMapping = {
			NXTSensor.Sensor.TOUCH,
			NXTSensor.Sensor.SOUND,
			NXTSensor.Sensor.LIGHT_INACTIVE,
			NXTSensor.Sensor.ULTRASONIC
	};

	private static final int PREFERENCES_SAVE_BROADCAST_DELAY = 50;

	@Before
	public void setUp() throws Exception {
		applicationContext = ApplicationProvider.getApplicationContext().getApplicationContext();

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		nxtSettingBuffer = sharedPreferences
				.getBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE, false);

		sharedPreferences.edit()
				.putBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE, true)
				.commit();

		sensorMappingBuffer = SettingsFragment.getLegoNXTSensorMapping(ApplicationProvider.getApplicationContext());

		setSensorMapping(new NXTSensor.Sensor[] {
				NXTSensor.Sensor.NO_SENSOR,
				NXTSensor.Sensor.NO_SENSOR,
				NXTSensor.Sensor.NO_SENSOR,
				NXTSensor.Sensor.NO_SENSOR});

		nxt = new LegoNXTImpl(this.applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		nxt.setConnection(logger.getConnectionProxy());
	}

	@After
	public void tearDown() throws Exception {
		nxt.disconnect();
		logger.disconnectAndDestroy();

		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
				.putBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE, nxtSettingBuffer)
				.commit();
		setSensorMapping(sensorMappingBuffer);
	}

	@Test
	public void testSensorAssignment() throws MindstormsException {
		NXTSensor.Sensor[] sensorMapping = {
				NXTSensor.Sensor.LIGHT_INACTIVE,
				NXTSensor.Sensor.SOUND,
				NXTSensor.Sensor.TOUCH,
				NXTSensor.Sensor.ULTRASONIC
		};

		setSensorMapping(sensorMapping);

		nxt.initialise();

		assertNotNull(nxt.getMotorA());
		assertNotNull(nxt.getMotorB());
		assertNotNull(nxt.getMotorC());

		assertNotNull(nxt.getSensor1());
		assertTrue(nxt.getSensor1() instanceof NXTLightSensor);

		assertNotNull(nxt.getSensor2());
		assertTrue(nxt.getSensor2() instanceof NXTSoundSensor);

		assertNotNull(nxt.getSensor3());
		assertTrue(nxt.getSensor3() instanceof NXTTouchSensor);

		assertNotNull(nxt.getSensor4());
		assertTrue(nxt.getSensor4() instanceof NXTI2CUltraSonicSensor);
	}

	@Test
	public void testSensorAssignmentChange() throws InterruptedException, MindstormsException {
		setSensorMapping(defaultSensorMapping);
		nxt.initialise();

		SettingsFragment.setLegoMindstormsNXTSensorMapping(applicationContext,
				NXTSensor.Sensor.LIGHT_INACTIVE, NXT_SENSORS[0]);

		Thread.sleep(PREFERENCES_SAVE_BROADCAST_DELAY);

		assertNotNull(nxt.getSensor1());
		assertTrue(nxt.getSensor1() instanceof NXTLightSensor);

		SettingsFragment.setLegoMindstormsNXTSensorMapping(applicationContext,
				NXTSensor.Sensor.TOUCH, NXT_SENSORS[0]);

		Thread.sleep(PREFERENCES_SAVE_BROADCAST_DELAY);

		assertNotNull(nxt.getSensor1());
		assertTrue(nxt.getSensor1() instanceof NXTTouchSensor);
	}

	@Test
	public void testSimplePlayToneTest() throws MindstormsException {
		int inputHz = 100;
		int expectedHz = 10000;
		int durationInMs = 3000;

		nxt.initialise();
		nxt.playTone(inputHz * 100, durationInMs);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedHz, setOutputState[2]);
		assertEquals((byte) (expectedHz >> 8), setOutputState[3]);
	}

	@Test
	public void testPlayToneHzOverMaxValue() throws MindstormsException {
		int inputHz = 160;
		int expectedHz = 14000;
		int durationInMs = 5000;

		nxt.initialise();
		nxt.playTone(inputHz * 100, durationInMs);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedHz, setOutputState[2]);
		assertEquals((byte) (expectedHz >> 8), setOutputState[3]);
	}

	@Test
	public void testCheckDurationOfTone() throws MindstormsException {
		int inputHz = 130;
		float inputDurationInS = 5.5f;
		int inputDurationInMs = (int) (inputDurationInS * 1000);

		int expectedDurationInMs = 5500;

		nxt.initialise();
		nxt.playTone(inputHz * 100, inputDurationInMs);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedDurationInMs, setOutputState[4]);
		assertEquals((byte) (expectedDurationInMs >> 8), setOutputState[5]);
	}

	@Test
	public void testWithZeroDuration() throws MindstormsException {
		int inputHz = 13000;
		int inputDurationInMs = 0;

		nxt.initialise();
		nxt.playTone(inputHz, inputDurationInMs);

		ArrayList<byte[]> commands = logger.getSentMessages(2, 0);

		assertEquals(0, commands.size());
	}

	private void setSensorMapping(NXTSensor.Sensor[] sensorMapping) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit();
		for (int i = 0; i < NXT_SENSORS.length; i++) {
			editor.putString(NXT_SENSORS[i], sensorMapping[i].getSensorCode());
		}
		editor.commit();
	}
}
