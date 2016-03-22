/*
* Catroid: An on-device visual programming system for Android devices
* Copyright (C) 2010-2016 The Catrobat Team
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
import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl;

public class LegoEV3ImplTest extends AndroidTestCase {

	private Context applicationContext;

	private LegoEV3 ev3;
	ConnectionDataLogger logger;

	private static final int BASIC_MESSAGE_BYTE_OFFSET = 6;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		applicationContext = this.getContext().getApplicationContext();

		ev3 = new LegoEV3Impl(this.applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		ev3.setConnection(logger.getConnectionProxy());
	}

	public void testSimplePlayToneTest() {

		int inputHz = 9000;
		int expectedHz = 9000;
		int durationInMs = 3000;
		int volume = 100;

		ev3.initialise();
		ev3.playTone(inputHz, durationInMs, volume);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 4; // 1 byte command, 2 bytes volume, 1 byte datatype

		assertEquals("Expected Hz not same as input Hz", (byte) expectedHz, setOutputState[offset]);
		assertEquals("Expected Hz not same as input Hz", (byte) (expectedHz >> 8), setOutputState[offset + 1]);
	}

	public void testPlayToneHzOverMaxValue() {

		// MaxHz = 10000;
		int inputHz = 16000;
		int expectedHz = 10000;
		int durationInMs = 5000;
		int volume = 100;

		ev3.initialise();
		ev3.playTone(inputHz, durationInMs, volume);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 4; // 1 byte command, 2 bytes volume, 1 byte datatype

		assertEquals("Expected Hz not same as input Hz", (byte) expectedHz, setOutputState[offset]);
		assertEquals("Expected Hz not same as input Hz", (byte) (expectedHz >> 8), setOutputState[offset + 1]);
	}

	public void testCheckDurationOfTone() {

		int inputHz = 9000;
		int durationInMs = 2000;
		int volume = 100;
		int expectedDurationInMs = 2000;

		ev3.initialise();
		ev3.playTone(inputHz, durationInMs, volume);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 7; // 1 byte command, 2 bytes volume, 3 bytes freq, 1 byte datatype

		assertEquals("Expected duration not same as input", (byte) expectedDurationInMs, setOutputState[offset]);
		assertEquals("Expected duration not same as input", (byte) (expectedDurationInMs >> 8), setOutputState[offset
				+ 1]);
	}

	public void testWithZeroDuration() {

		int inputHz = 13000;
		int inputDurationInMs = 0;
		int volume = 100;

		ev3.initialise();
		ev3.playTone(inputHz, inputDurationInMs, volume);

		byte[] command = logger.getNextSentMessage(0, 2);

		assertEquals("LastSentCommand Should be NULL", null, command);
	}

//	public void testWithZeroVolume() {
//
//		int inputHz = 13000;
//		int inputDurationInMs = 0;
//		int volume = 0;
//
//		ev3.initialise();
//		ev3.playTone(inputHz, inputDurationInMs, volume);
//
//		byte[] command = logger.getNextSentMessage(0, 2);
//
//		assertEquals("LastSentCommand Should be NULL", null, command);
//	}

	public void testSimpleLED() {

		int ledStatus = 0x04;
		int expectedLedStatus = 0x04;

		ev3.initialise();
		ev3.setLed(ledStatus);

		byte[] setOutputState = logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 2; // 1 byte command, 1 byte datatype

		assertEquals("Sent LED-Status doesn't match expected Status", (byte) expectedLedStatus, setOutputState[offset]);
	}
}
