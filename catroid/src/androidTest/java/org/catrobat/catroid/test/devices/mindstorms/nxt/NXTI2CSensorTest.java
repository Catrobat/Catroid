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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NXTI2CSensorTest {

	private static final byte DIRECT_COMMAND_WITHOUT_REPLY = (byte) 0x80;
	private static final byte DIRECT_COMMAND_WITH_REPLY = (byte) 0x00;
	private static final byte PORT_NR = 3;

	private static final byte ULTRASONIC_ADDRESS = 0x02;
	private static final byte SENSOR_REGISTER_RESULT = 0x42;
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
		byte[] message = logger.getNextSentMessage(0, 2);
		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.SET_INPUT_MODE.getByte(), PORT_NR,
						NXTSensorType.LOW_SPEED_9V.getByte(), NXTSensorMode.RAW.getByte() },
				message);
	}

	@Test
	public void testResetInputScaledValueMessage() {
		nxtSensor.getValue();
		byte[] message = logger.getNextSentMessage(1, 2);
		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), PORT_NR}, message);
	}

	@Test
	public void testSetInputModeMessageEndInitialisation() {
		nxtSensor.getValue();
		byte[] message = logger.getNextSentMessage(2, 2);

		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.SET_INPUT_MODE.getByte(), PORT_NR,
						NXTSensorType.LOW_SPEED_9V.getByte(), NXTSensorMode.RAW.getByte()},
				message);
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
		testLsWriteMessage(SENSOR_REGISTER_RESULT);
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
		byte[] message = logger.getNextSentMessage(0, 2);
		assertArrayEquals(
				new byte[] {DIRECT_COMMAND_WITHOUT_REPLY, CommandByte.LS_WRITE.getByte(), PORT_NR, 2, 1, ULTRASONIC_ADDRESS, register},
				message);
	}
}
