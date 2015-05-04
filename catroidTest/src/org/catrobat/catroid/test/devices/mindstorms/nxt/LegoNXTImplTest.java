/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.test.AndroidTestCase;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXTImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.ArrayList;

public class LegoNXTImplTest extends AndroidTestCase {

	private Context applicationContext;
	private SharedPreferences preferences;

	private LegoNXT nxt;
	ConnectionDataLogger logger;

	private static final int PREFERENCES_SAVE_DELAY = 50;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		applicationContext = this.getContext().getApplicationContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);

		nxt = new LegoNXTImpl(this.applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		nxt.setConnection(logger.getConnectionProxy());
	}

	@Override
	protected void tearDown() throws Exception {
		nxt.disconnect();
		logger.disconnectAndDestroy();
		super.tearDown();
	}

	private void setSensor(SharedPreferences.Editor editor, String sensor, int sensorType) {
		editor.putString(sensor, applicationContext.getString(sensorType));
	}

	public void testSensorAssignment() throws InterruptedException {
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();

		setSensor(editor, SettingsActivity.NXT_SENSOR_1, R.string.nxt_sensor_light);
		setSensor(editor, SettingsActivity.NXT_SENSOR_2, R.string.nxt_sensor_sound);
		setSensor(editor, SettingsActivity.NXT_SENSOR_3, R.string.nxt_sensor_touch);
		setSensor(editor, SettingsActivity.NXT_SENSOR_4, R.string.nxt_sensor_ultrasonic);

		editor.apply();
		Thread.sleep(PREFERENCES_SAVE_DELAY); // Preferences need some time to get saved

		nxt.initialise();

		assertNotNull("Motor A not initialized correctly", nxt.getMotorA());
		assertNotNull("Motor B not initialized correctly", nxt.getMotorB());
		assertNotNull("Motor C not initialized correctly", nxt.getMotorC());

		assertNotNull("Sensor 1 not initialized correctly", nxt.getSensor1());
		assertTrue("Sensor 1 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor1() instanceof NXTLightSensor);

		assertNotNull("Sensor 2 not initialized correctly", nxt.getSensor2());
		assertTrue("Sensor 2 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor2() instanceof NXTSoundSensor);

		assertNotNull("Sensor 3 not initialized correctly", nxt.getSensor3());
		assertTrue("Sensor 3 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor3() instanceof NXTTouchSensor);

		assertNotNull("Sensor 4 not initialized correctly", nxt.getSensor4());
		assertTrue("Sensor 4 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor4() instanceof NXTI2CUltraSonicSensor);
	}

	public void testSensorAssignmentChange() throws InterruptedException {
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.apply();
		Thread.sleep(PREFERENCES_SAVE_DELAY); // Preferences need some time to get saved

		nxt.initialise();

		assertNull("Sensor 1 should not be initialized", nxt.getSensor1());

		editor = preferences.edit();
		setSensor(editor, SettingsActivity.NXT_SENSOR_1, R.string.nxt_sensor_light);
		editor.apply();
		Thread.sleep(PREFERENCES_SAVE_DELAY); // Preferences need some time to get saved

		assertNotNull("Sensor 1 not initialized correctly", nxt.getSensor1());
		assertTrue("Sensor 1 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor1() instanceof NXTLightSensor);

		editor = preferences.edit();
		setSensor(editor, SettingsActivity.NXT_SENSOR_1, R.string.nxt_sensor_touch);
		editor.apply();
		Thread.sleep(PREFERENCES_SAVE_DELAY); // Preferences need some time to get saved

		assertNotNull("Sensor 1 not reinitialized correctly", nxt.getSensor1());
		assertTrue("Sensor 1 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor1() instanceof NXTTouchSensor);
	}

	public void testSimplePlayToneTest() {

		int inputHz = 100;
		int expectedHz = 10000;
		int durationInMs = 3000;

		nxt.initialise();
		nxt.playTone(inputHz * 100, durationInMs);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals("Expected Hz not same as input Hz", (byte)expectedHz, setOutputState[2]);
		assertEquals("Expected Hz not same as input Hz", (byte)(expectedHz >> 8), setOutputState[3]);
	}

	public void testPlayToneHzOverMaxValue() {

		// MaxHz = 14000;
		int inputHz = 160;
		int expectedHz = 14000;
		int durationInMs = 5000;

		nxt.initialise();
		nxt.playTone(inputHz * 100, durationInMs);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals("Expected Hz over maximum Value (max. Value = 14000)", (byte)expectedHz, setOutputState[2]);
		assertEquals("Expected Hz over maximum Value (max. Value = 14000)", (byte)(expectedHz >> 8), setOutputState[3]);
	}

	public void testCheckDurationOfTone() {

		int inputHz = 130;
		float inputDurationInS = 5.5f;
		int expectedDurationInMs = 5500;

		nxt.initialise();
		nxt.playTone(inputHz * 100, (int) inputDurationInS * 1000);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals("Expected Duration not same as Input Duration", (byte) expectedDurationInMs, setOutputState[4]);
		assertEquals("Expected Duration not same as Input Duration", (byte)(expectedDurationInMs >> 8), setOutputState[5]);
	}

	public void testWithZeroDuration() {

		int inputHz = 13000;
		int inputDurationInMs = 0;

		nxt.initialise();
		nxt.playTone(inputHz, inputDurationInMs);

		ArrayList<byte[]> commands = logger.getSentMessages(2, 0);

		assertEquals("No commands should be sent", 0, commands.size());
	}
}
