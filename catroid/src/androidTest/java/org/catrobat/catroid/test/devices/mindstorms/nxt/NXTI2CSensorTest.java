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

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.MindstormsNXTTestModel;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NXTI2CSensorTest {

	private static final byte DIRECT_COMMAND_WITHOUT_REPLY = (byte) 0x80;
	private static final byte DIRECT_COMMAND_WITH_REPLY = (byte) 0x00;
	private static final byte PORT_NR = 3;

	private static final byte ULTRASONIC_ADDRESS = 0x02;
	private static final byte SENSOR_REGISTER_RESULT1 = 0x42;
	private static final int EXPECTED_SENSOR_VALUE = 142;

	private ConnectionDataLogger logger;
	private MindstormsConnection mindstormsConnection;
	private NXTI2CUltraSonicSensor nxtSensor;

	@Before
	public void setUp() throws Exception {
		MindstormsNXTTestModel nxtModel = new MindstormsNXTTestModel();
		logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(nxtModel);
		mindstormsConnection = new MindstormsConnectionImpl(logger.getConnectionProxy());
		mindstormsConnection.init();
		nxtSensor = new NXTI2CUltraSonicSensor(mindstormsConnection);
		nxtModel.setSensorValue(EXPECTED_SENSOR_VALUE);
	}

	@After
	public void tearDown() throws Exception {
		mindstormsConnection.disconnect();
		logger.disconnectAndDestroy();
	}

	@Test
	public void testSensorValue() {
		int sensorValue = (int) nxtSensor.getValue();
		assertEquals(EXPECTED_SENSOR_VALUE, sensorValue);
	}

	@Test
	public void testSetInputModeMessageBeginInitialisation() {
		nxtSensor.getValue();
		byte[] setInputModeMsg = logger.getNextSentMessage(0, 2);
		assertNotNull(setInputModeMsg);
		assertEquals(5, setInputModeMsg.length);
		assertEquals(DIRECT_COMMAND_WITH_REPLY, setInputModeMsg[0]);
		assertEquals(CommandByte.SET_INPUT_MODE.getByte(), setInputModeMsg[1]);
		assertEquals(PORT_NR, setInputModeMsg[2]);
		assertEquals(NXTSensorType.LOW_SPEED_9V.getByte(), setInputModeMsg[3]);
		assertEquals(NXTSensorMode.RAW.getByte(), setInputModeMsg[4]);
	}

	@Test
	public void testResetInputScaledValueMessage() {
		nxtSensor.getValue();
		byte[] resetScaledValueMsg = logger.getNextSentMessage(1, 2);

		assertNotNull(resetScaledValueMsg);
		assertEquals(3, resetScaledValueMsg.length);

		assertEquals(DIRECT_COMMAND_WITHOUT_REPLY, resetScaledValueMsg[0]);
		assertEquals(CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), resetScaledValueMsg[1]);
		assertEquals(PORT_NR, resetScaledValueMsg[2]);
	}

	@Test
	public void testSetInputModeMessageEndInitialisation() {
		nxtSensor.getValue();
		byte[] setInputModeMsg = logger.getNextSentMessage(2, 2);
		assertNotNull(setInputModeMsg);
		assertEquals(5, setInputModeMsg.length);
		assertEquals(DIRECT_COMMAND_WITH_REPLY, setInputModeMsg[0]);
		assertEquals(CommandByte.SET_INPUT_MODE.getByte(), setInputModeMsg[1]);
		assertEquals(PORT_NR, setInputModeMsg[2]);
		assertEquals(NXTSensorType.LOW_SPEED_9V.getByte(), setInputModeMsg[3]);
		assertEquals(NXTSensorMode.RAW.getByte(), setInputModeMsg[4]);
	}

	@Test
	public void testLsWriteMessage() {
		nxtSensor.getValue();
		logger.getNextSentMessage(2, 2);
		testLsWriteMessage((byte) 0x0);
	}


	@Test
	public void testReadAndWriteAfterInit() {
		nxtSensor.getValue();
		logger.getNextSentMessage(3, 2);
		testLsReadMessage();
		testLsWriteMessage(SENSOR_REGISTER_RESULT1);
		testLsReadMessage();
	}

	private void testLsReadMessage() {
		byte[] message = logger.getNextSentMessage(0, 2);
		do {
			assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.LS_GET_STATUS.getByte(), PORT_NR}, message);
			message = logger.getNextSentMessage(0, 2);
		} while (message[1] == CommandByte.LS_GET_STATUS.getByte());

		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.LS_READ.getByte(), PORT_NR}, message);
	}

	private void testLsWriteMessage(byte register) {
		byte[] lsWriteMsg = logger.getNextSentMessage(0, 2);

		assertNotNull(lsWriteMsg);
		assertEquals(7, lsWriteMsg.length);

		assertEquals(DIRECT_COMMAND_WITHOUT_REPLY, lsWriteMsg[0]);
		assertEquals(CommandByte.LS_WRITE.getByte(), lsWriteMsg[1]);
		assertEquals(PORT_NR, lsWriteMsg[2]);
		assertEquals(2, lsWriteMsg[3]);
		assertEquals(1, lsWriteMsg[4]);
		assertEquals(ULTRASONIC_ADDRESS, lsWriteMsg[5]);
		assertEquals(register, lsWriteMsg[6]);
	}
}
