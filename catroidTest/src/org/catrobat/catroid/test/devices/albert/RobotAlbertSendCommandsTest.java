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

package org.catrobat.catroid.test.devices.albert;


import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.albert.Albert;
import org.catrobat.catroid.devices.albert.AlbertImpl;

public class RobotAlbertSendCommandsTest extends AndroidTestCase {

	public void testFrontLed() {
		Albert albert = new AlbertImpl();
		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLogger();
		albert.setConnection(logger.getConnectionProxy());
		albert.setFrontLed(1);
		byte[] send = logger.getNextSentMessage();
		checkSendCommand(1, send, 17);
	}

	public void testBodyLed() {
		Albert albert = new AlbertImpl();
		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLogger();
		albert.setConnection(logger.getConnectionProxy());
		albert.setBodyLed(150);
		byte[] send = logger.getNextSentMessage();
		checkSendCommand(150, send, 19);
	}

	public void testBuzzer() {
		Albert albert = new AlbertImpl();
		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLogger();
		albert.setConnection(logger.getConnectionProxy());
		albert.setBuzzer(60);
		byte[] send = logger.getNextSentMessage();
		checkSendCommand(60, send, 10);
	}

	public void testMotor() {
		Albert albert = new AlbertImpl();
		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLogger();
		albert.setConnection(logger.getConnectionProxy());

		albert.move(AlbertImpl.MOTOR_LEFT, 50);
		byte[] send = logger.getNextSentMessage();
		checkSendCommand(50, send, 9);

		albert.move(AlbertImpl.MOTOR_RIGHT, 40);
		send = logger.getNextSentMessage();
		checkSendCommand(40, send, 8);

		albert.move(AlbertImpl.MOTOR_BOTH, 60);
		send = logger.getNextSentMessage();
		checkSendCommand(60, send, 8);
		checkSendCommand(60, send, 9);
	}

	public void testRgbEyeLed() {
		Albert albert = new AlbertImpl();
		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLogger();
		albert.setConnection(logger.getConnectionProxy());

		albert.setRgbLedEye(AlbertImpl.EYE_LEFT, 100, 100, 100);
		byte[] send = logger.getNextSentMessage();
		checkSendCommand(100, send, 11, 12, 13);

		albert.setRgbLedEye(AlbertImpl.EYE_RIGHT, 150, 150, 150);
		send = logger.getNextSentMessage();
		checkSendCommand(150, send, 14, 15, 16);

		albert.setRgbLedEye(AlbertImpl.EYE_BOTH, 200, 200, 200);
		send = logger.getNextSentMessage();
		checkSendCommand(200, send, 11, 12, 13, 14, 15, 16);
	}

	private void checkSendCommand(int target, byte[] send, int... items) {
		assertEquals("Error: Albert test HEADER1 not found!", (byte) 0xAA, send[0]);
		assertEquals("Error: Albert test HEADER2 not found!", (byte) 0x55, send[1]);
		assertEquals("Error: Albert test send command length false!", 22, send.length);
		assertEquals("Error: Albert test TAIL1 not found!", (byte) 0x0D, send[20]);
		assertEquals("Error: Albert test TAIL1 not found!", (byte) 0x0A, send[21]);
		for (int item : items) {
			assertEquals("Error: Albert test wrong value send!", (byte) target, send[item]);
		}
	}


}
