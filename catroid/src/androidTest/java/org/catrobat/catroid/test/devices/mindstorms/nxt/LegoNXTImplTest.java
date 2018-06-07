/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXTImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.util.ArrayList;

public class LegoNXTImplTest extends AndroidTestCase {

	private Context applicationContext;

	private LegoNXT nxt;
	ConnectionDataLogger logger;

	private static final int PREFERENCES_SAVE_BROADCAST_DELAY = 50;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		applicationContext = this.getContext().getApplicationContext();

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

	public void testSensorAssignment() throws InterruptedException {

		SettingsFragment.setLegoMindstormsNXTSensorMapping(applicationContext,
				new NXTSensor.Sensor[] {NXTSensor.Sensor.LIGHT_INACTIVE, NXTSensor.Sensor.SOUND,
						NXTSensor.Sensor.TOUCH, NXTSensor.Sensor.ULTRASONIC});

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

	private void resetSensorMappingToDefault() throws InterruptedException {
		SettingsFragment.setLegoMindstormsNXTSensorMapping(this.getContext(),
				new NXTSensor.Sensor[] {NXTSensor.Sensor.TOUCH, NXTSensor.Sensor.SOUND,
						NXTSensor.Sensor.LIGHT_INACTIVE, NXTSensor.Sensor.ULTRASONIC});
	}

	public void testSensorAssignmentChange() throws InterruptedException {
		resetSensorMappingToDefault();
		nxt.initialise();

		SettingsFragment.setLegoMindstormsNXTSensorMapping(applicationContext,
				NXTSensor.Sensor.LIGHT_INACTIVE, SettingsFragment.NXT_SENSOR_1);

		Thread.sleep(PREFERENCES_SAVE_BROADCAST_DELAY);

		assertNotNull(nxt.getSensor1());
		assertTrue(nxt.getSensor1() instanceof NXTLightSensor);

		SettingsFragment.setLegoMindstormsNXTSensorMapping(applicationContext,
				NXTSensor.Sensor.TOUCH, SettingsFragment.NXT_SENSOR_1);

		Thread.sleep(PREFERENCES_SAVE_BROADCAST_DELAY);

		assertNotNull(nxt.getSensor1());
		assertTrue(nxt.getSensor1() instanceof NXTTouchSensor);
	}

	public void testSimplePlayToneTest() {

		int inputHz = 100;
		int expectedHz = 10000;
		int durationInMs = 3000;

		nxt.initialise();
		nxt.playTone(inputHz * 100, durationInMs);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedHz, setOutputState[2]);
		assertEquals((byte) (expectedHz >> 8), setOutputState[3]);
	}

	public void testPlayToneHzOverMaxValue() {

		// MaxHz = 14000;
		int inputHz = 160;
		int expectedHz = 14000;
		int durationInMs = 5000;

		nxt.initialise();
		nxt.playTone(inputHz * 100, durationInMs);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		assertEquals((byte) expectedHz, setOutputState[2]);
		assertEquals((byte) (expectedHz >> 8), setOutputState[3]);
	}

	public void testCheckDurationOfTone() {

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

	public void testWithZeroDuration() {

		int inputHz = 13000;
		int inputDurationInMs = 0;

		nxt.initialise();
		nxt.playTone(inputHz, inputDurationInMs);

		ArrayList<byte[]> commands = logger.getSentMessages(2, 0);

		assertEquals(0, commands.size());
	}
}
