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

package org.catrobat.catroid.test.devices.mindstorms.ev3;

import android.content.Context;
import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl;

public class LegoEV3MotorTest extends AndroidTestCase {

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

	public void testSingleMotorMoveTest() {
		int inputPower = -70;
		int timeInMs = 1500;
		byte outputField = (byte) 0x01;

		int expectedPower = -70;
		int expectedTimeInMs = 1500;
		byte expectedOutputField = (byte) 0x01;
		int expectedTime1 = 0;
		int expectedTime3 = 0;

		ev3.initialise();
		ev3.moveMotorTime(outputField, 0, inputPower, 0, timeInMs, 0, true);

		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 1;

		assertEquals("Expected OutputField(Motor) doesn't match input", expectedOutputField, setOutputState[offset]);
		offset += 2;

		assertEquals("Expected Power not same as input Power", (byte) expectedPower, setOutputState[offset]);
		offset += 2;

		assertEquals("Unused StepTime 1 was not 0", (byte) expectedTime1, setOutputState[offset]);
		assertEquals("Unused StepTime 1 was not 0", (byte) (expectedTime1 >> 8), setOutputState[offset + 1]);
		offset += 3;

		assertEquals("Expected Time doesn't match input", (byte) expectedTimeInMs, setOutputState[offset]);
		assertEquals("Expected Time doesn't match input", (byte) (expectedTimeInMs >> 8), setOutputState[offset + 1]);
		offset += 3;

		assertEquals("Unused StepTime 3 was not 0", (byte) expectedTime3, setOutputState[offset]);
		assertEquals("Unused StepTime 3 was not 0", (byte) (expectedTime3 >> 8), setOutputState[offset + 1]);
	}

	public void testStopMotorTest() {
		byte outputField = (byte) 0x01;
		byte expectedOutputField = (byte) 0x01;

		ev3.initialise();
		ev3.stopMotor(outputField, 0, true);

		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 1;

		assertEquals("Expected OutputField(Motor) doesn't match input", expectedOutputField, setOutputState[offset]);
	}
}
