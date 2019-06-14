/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType;

import static junit.framework.Assert.assertNotNull;

import static org.junit.Assert.assertEquals;

public class NXTI2CSensorTest {

	//
//	@Test
//	public void testI2CUltrasonicSensor() {
//		final int expectedSensorValue = 142;
//		nxtModel.setSensorValue(expectedSensorValue);
//
//		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(mindstormsConnection);
//
//		int sensorValue = (int) sensor.getValue();
//
//		testInitializationOfI2CSensor(PORT_NR_3, NXTSensorType.LOW_SPEED_9V, NXTSensorMode.RAW);
//
//		testLsWriteMessage(SENSOR_REGISTER_RESULT1, PORT_NR_3);
//		testLsReadMessage(PORT_NR_3);
//
//		assertEquals(expectedSensorValue, sensorValue);
//	}

//	private void testInitializationOfI2CSensor(byte port, NXTSensorType sensorType, NXTSensorMode sensorMode) {
//		testInitializationOfSensor(port, sensorType, sensorMode);
//		testLsWriteMessage((byte) 0x0, port);
//		testLsReadMessage(port);
//	}
//private void testLsReadMessage(byte port) {
//
//	byte[] currentMessage = logger.getNextSentMessage(0, 2);
//
//	assertNotNull(currentMessage);
//
//	do {
//		byte[] lsGetStatusMsg = currentMessage;
//
//		assertNotNull(currentMessage);
//		assertEquals(3, lsGetStatusMsg.length);
//
//		assertEquals(DIRECT_COMMAND_WITH_REPLY, lsGetStatusMsg[0]);
//		assertEquals(CommandByte.LS_GET_STATUS.getByte(), lsGetStatusMsg[1]);
//		assertEquals(port, lsGetStatusMsg[2]);
//
//		currentMessage = logger.getNextSentMessage(0, 2);
//	} while (currentMessage[1] == CommandByte.LS_GET_STATUS.getByte());
//
//	byte[] lsReadMsg = currentMessage;
//
//	assertNotNull(lsReadMsg);
//	assertEquals(3, lsReadMsg.length);
//
//	assertEquals(DIRECT_COMMAND_WITH_REPLY, lsReadMsg[0]);
//	assertEquals(CommandByte.LS_READ.getByte(), lsReadMsg[1]);
//	assertEquals(port, lsReadMsg[2]);
//}
//
//	private void testLsWriteMessage(byte register, byte port) {
//		byte[] lsWriteMsg = logger.getNextSentMessage(0, 2);
//
//		assertNotNull(lsWriteMsg);
//		assertEquals(7, lsWriteMsg.length);
//
//		assertEquals(DIRECT_COMMAND_WITHOUT_REPLY, lsWriteMsg[0]);
//		assertEquals(CommandByte.LS_WRITE.getByte(), lsWriteMsg[1]);
//		assertEquals(port, lsWriteMsg[2]);
//		assertEquals(2, lsWriteMsg[3]);
//		assertEquals(1, lsWriteMsg[4]);
//		assertEquals(ULTRASONIC_ADDRESS, lsWriteMsg[5]);
//		assertEquals(register, lsWriteMsg[6]);
//	}
}
