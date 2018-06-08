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

import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.MindstormsNXTTestModel;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensorActive;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SensorTests extends AndroidTestCase {

	private static final byte DIRECT_COMMAND_WITHOUT_REPLY = (byte) 0x80;
	private static final byte DIRECT_COMMAND_WITH_REPLY = (byte) 0x00;
	private static final byte PORT_NR_0 = 0;
	private static final byte PORT_NR_1 = 1;
	private static final byte PORT_NR_2 = 2;
	private static final byte PORT_NR_3 = 3;

	private static final byte ULTRASONIC_ADDRESS = 0x02;
	private static final byte SENSOR_REGISTER_RESULT1 = 0x42;

	private ConnectionDataLogger logger;
	private MindstormsConnection mindstormsConnection;
	private MindstormsNXTTestModel nxtModel;

	@Before
	public void setUp() throws Exception {

		nxtModel = new MindstormsNXTTestModel();
		this.logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(nxtModel);

		this.mindstormsConnection = new MindstormsConnectionImpl(logger.getConnectionProxy());
		mindstormsConnection.init();
	}

	@After
	public void tearDown() throws Exception {
		mindstormsConnection.disconnect();
		logger.disconnectAndDestroy();
	}

	@Test
	public void testTouchSensor() {

		final int expectedSensorValue = 1;

		nxtModel.setSensorValue(expectedSensorValue);
		NXTSensor sensor = new NXTTouchSensor(PORT_NR_0, mindstormsConnection);
		int sensorValue = (int) sensor.getValue();

		testInitializationOfSensor(PORT_NR_0, NXTSensorType.TOUCH, NXTSensorMode.BOOL);
		testGetInputValuesMessage(PORT_NR_0);

		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testSoundSensor() {
		final int expectedSensorValue = 42;
		nxtModel.setSensorValue(expectedSensorValue);

		NXTSensor sensor = new NXTSoundSensor(PORT_NR_1, mindstormsConnection);
		int sensorValue = (int) sensor.getValue();

		testInitializationOfSensor(PORT_NR_1, NXTSensorType.SOUND_DBA, NXTSensorMode.Percent);
		testGetInputValuesMessage(PORT_NR_1);

		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testLightSensor() {
		final int expectedSensorValue = 24;

		nxtModel.setSensorValue(expectedSensorValue);
		NXTSensor sensor = new NXTLightSensor(PORT_NR_2, mindstormsConnection);
		int sensorValue = (int) sensor.getValue();

		testInitializationOfSensor(PORT_NR_2, NXTSensorType.LIGHT_INACTIVE, NXTSensorMode.Percent);
		testGetInputValuesMessage(PORT_NR_2);

		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testLightSensorActive() {
		final int expectedSensorValue = 33;

		nxtModel.setSensorValue(expectedSensorValue);
		NXTSensor sensor = new NXTLightSensorActive(PORT_NR_2, mindstormsConnection);
		int sensorValue = (int) sensor.getValue();

		testInitializationOfSensor(PORT_NR_2, NXTSensorType.LIGHT_ACTIVE, NXTSensorMode.Percent);
		testGetInputValuesMessage(PORT_NR_2);

		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testI2CUltrasonicSensor() {
		final int expectedSensorValue = 142;
		nxtModel.setSensorValue(expectedSensorValue);

		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(mindstormsConnection);

		int sensorValue = (int) sensor.getValue();

		testInitializationOfI2CSensor(PORT_NR_3, NXTSensorType.LOW_SPEED_9V, NXTSensorMode.RAW);

		testLsWriteMessage(SENSOR_REGISTER_RESULT1, PORT_NR_3);
		testLsReadMessage(PORT_NR_3);

		assertEquals(expectedSensorValue, sensorValue);
	}

	private void testInitializationOfSensor(int port, NXTSensorType sensorType, NXTSensorMode sensorMode) {
		testSetInputModeMessage(port, sensorType, sensorMode);
		testResetInputScaledValueMessage(port);
		testSetInputModeMessage(port, sensorType, sensorMode);
	}

	private void testInitializationOfI2CSensor(byte port, NXTSensorType sensorType, NXTSensorMode sensorMode) {
		testInitializationOfSensor(port, sensorType, sensorMode);
		testLsWriteMessage((byte) 0x0, port);
		testLsReadMessage(port);
	}

	private void testSetInputModeMessage(int port, NXTSensorType sensorType, NXTSensorMode sensorMode) {
		byte[] setInputModeMsg = logger.getNextSentMessage(0, 2);

		assertNotNull(setInputModeMsg);
		assertEquals(5, setInputModeMsg.length);

		assertEquals(DIRECT_COMMAND_WITH_REPLY, setInputModeMsg[0]);
		assertEquals(CommandByte.SET_INPUT_MODE.getByte(), setInputModeMsg[1]);
		assertEquals(port, setInputModeMsg[2]);
		assertEquals(sensorType.getByte(), setInputModeMsg[3]);
		assertEquals(sensorMode.getByte(), setInputModeMsg[4]);
	}

	private void testResetInputScaledValueMessage(int port) {
		byte[] resetScaledValueMsg = logger.getNextSentMessage(0, 2);

		assertNotNull(resetScaledValueMsg);
		assertEquals(3, resetScaledValueMsg.length);

		assertEquals(DIRECT_COMMAND_WITHOUT_REPLY, resetScaledValueMsg[0]);
		assertEquals(CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), resetScaledValueMsg[1]);
		assertEquals(port, resetScaledValueMsg[2]);
	}

	private void testGetInputValuesMessage(int port) {
		byte[] getInputValuesMsg = logger.getNextSentMessage(0, 2);

		assertNotNull(getInputValuesMsg);
		assertEquals(3, getInputValuesMsg.length);

		assertEquals(DIRECT_COMMAND_WITH_REPLY, getInputValuesMsg[0]);
		assertEquals(CommandByte.GET_INPUT_VALUES.getByte(), getInputValuesMsg[1]);
		assertEquals(port, getInputValuesMsg[2]);
	}

	private void testLsReadMessage(byte port) {

		byte[] currentMessage = logger.getNextSentMessage(0, 2);

		assertNotNull(currentMessage);

		do {
			byte[] lsGetStatusMsg = currentMessage;

			assertNotNull(currentMessage);
			assertEquals(3, lsGetStatusMsg.length);

			assertEquals(DIRECT_COMMAND_WITH_REPLY, lsGetStatusMsg[0]);
			assertEquals(CommandByte.LS_GET_STATUS.getByte(), lsGetStatusMsg[1]);
			assertEquals(port, lsGetStatusMsg[2]);

			currentMessage = logger.getNextSentMessage(0, 2);
		} while (currentMessage[1] == CommandByte.LS_GET_STATUS.getByte());

		byte[] lsReadMsg = currentMessage;

		assertNotNull(lsReadMsg);
		assertEquals(3, lsReadMsg.length);

		assertEquals(DIRECT_COMMAND_WITH_REPLY, lsReadMsg[0]);
		assertEquals(CommandByte.LS_READ.getByte(), lsReadMsg[1]);
		assertEquals(port, lsReadMsg[2]);
	}

	private void testLsWriteMessage(byte register, byte port) {
		byte[] lsWriteMsg = logger.getNextSentMessage(0, 2);

		assertNotNull(lsWriteMsg);
		assertEquals(7, lsWriteMsg.length);

		assertEquals(DIRECT_COMMAND_WITHOUT_REPLY, lsWriteMsg[0]);
		assertEquals(CommandByte.LS_WRITE.getByte(), lsWriteMsg[1]);
		assertEquals(port, lsWriteMsg[2]);
		assertEquals(2, lsWriteMsg[3]);
		assertEquals(1, lsWriteMsg[4]);
		assertEquals(ULTRASONIC_ADDRESS, lsWriteMsg[5]);
		assertEquals(register, lsWriteMsg[6]);
	}
}
