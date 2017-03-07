/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl;

public class LegoEV3MotorTest extends AndroidTestCase {

	private Context applicationContext;

	private LegoEV3 ev3;
	ConnectionDataLogger logger;

	private static final int BASIC_MESSAGE_BYTE_OFFSET = 6;
	private static final int POWER_DOWN_RAMP_DEGREES = 20;

	private static final byte LONG_PARAMETER_BYTE_ONE_FOLLOW = (byte) 0x81;
	private static final byte LONG_PARAMETER_BYTE_TWO_FOLLOW = (byte) 0x82;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		applicationContext = this.getContext().getApplicationContext();

		ev3 = new LegoEV3Impl(this.applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		ev3.setConnection(logger.getConnectionProxy());
	}

	public void testMotorMoveTest() {
		int inputSpeed = -70;
		byte outputField = (byte) 0x01;

		int expectedSpeed = (EV3CommandByte.EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & inputSpeed)
				| EV3CommandByte.EV3CommandParamByteCode.PARAM_SHORT_SIGN_NEGATIVE.getByte();

		byte expectedOutputField = (byte) 0x01;

		byte startCmdCode = (byte) 0xA6;

		ev3.initialise();
		ev3.moveMotorSpeed(outputField, 0, inputSpeed);

		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 1;

		assertEquals("Expected OutputField(Motor) doesn't match input", expectedOutputField, setOutputState[offset]);
		offset += 1;

		assertEquals("Expected Speed not same as input Speed", (byte) expectedSpeed, setOutputState[offset]);

		setOutputState = this.logger.getNextSentMessage(0, 2);
		assertEquals("Set Speed Command wasn't followed by Start Command", startCmdCode, setOutputState[5]);

		offset = BASIC_MESSAGE_BYTE_OFFSET + 1;
		assertEquals("Expected OutputField(Motor) in Start-Cmd doesn't match input", expectedOutputField,
				setOutputState[offset]);
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

	public void testMotorTurnAngle360DegreeTest() {
		int step2Degrees = 360 - POWER_DOWN_RAMP_DEGREES;
		int step3Degrees = POWER_DOWN_RAMP_DEGREES;
		int inputSpeed = -70;
		byte outputField = (byte) 0x01;
		int expectedStep1Degrees = 0;
		int expectedStep2Degrees = 360 - POWER_DOWN_RAMP_DEGREES;
		int expectedStep3Degrees = POWER_DOWN_RAMP_DEGREES;
		int expectedSpeed = -70;
		byte expectedOutputField = (byte) 0x01;

		ev3.initialise();
		ev3.moveMotorStepsSpeed(outputField, 0, inputSpeed, 0, step2Degrees, step3Degrees, true);

		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 1;

		assertEquals("Expected OutputField(Motor) doesn't match input", expectedOutputField, setOutputState[offset]);
		offset += 2;

		assertEquals("Expected Power not same as input Power", (byte) expectedSpeed, setOutputState[offset]);
		offset += 1;

		assertEquals("Control-Byte for unused Step 1 wrong", LONG_PARAMETER_BYTE_ONE_FOLLOW,
				setOutputState[offset]);
		offset += 1;
		assertEquals("Unused Step 1 was not 0", (byte) expectedStep1Degrees, setOutputState[offset]);
		offset += 1;

		assertEquals("Control-Byte for input degree wrong", LONG_PARAMETER_BYTE_TWO_FOLLOW,
				setOutputState[offset]);
		offset += 1;
		assertEquals("Expected Degrees don't match input", (byte) expectedStep2Degrees, setOutputState[offset]);
		assertEquals("Expected Degrees don't match input", (byte) (expectedStep2Degrees >> 8),
				setOutputState[offset + 1]);
		offset += 2;

		assertEquals("Control-Byte for Step 3 wrong", LONG_PARAMETER_BYTE_ONE_FOLLOW,
				setOutputState[offset]);
		offset += 1;
		assertEquals("Unused Step 3 was not 20", (byte) expectedStep3Degrees, setOutputState[offset]);
	}

	public void testMotorTurnAngleMinus15DegreeTest() {
		int step2Degrees = 15;
		int step3Degrees = 0;
		int inputSpeed = -70;
		byte outputField = (byte) 0x01;
		int expectedStep1Degrees = 0;
		int expectedStep2Degrees = 15;
		int expectedStep3Degrees = 0;
		int expectedSpeed = -70;
		byte expectedOutputField = (byte) 0x01;

		ev3.initialise();
		ev3.moveMotorStepsSpeed(outputField, 0, inputSpeed, 0, step2Degrees, step3Degrees, true);

		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 1;

		assertEquals("Expected OutputField(Motor) doesn't match input", expectedOutputField, setOutputState[offset]);
		offset += 2;

		assertEquals("Expected Power not same as input Power", (byte) expectedSpeed, setOutputState[offset]);
		offset += 1;

		assertEquals("Control-Byte for unused Step 1 wrong", LONG_PARAMETER_BYTE_ONE_FOLLOW,
				setOutputState[offset]);
		offset += 1;
		assertEquals("Unused Step 1 was not 0", (byte) expectedStep1Degrees, setOutputState[offset]);
		offset += 1;

		assertEquals("Control-Byte for input degree wrong", LONG_PARAMETER_BYTE_ONE_FOLLOW,
				setOutputState[offset]);
		offset += 1;
		assertEquals("Expected Degrees don't match input", (byte) expectedStep2Degrees, setOutputState[offset]);
		offset += 1;

		assertEquals("Control-Byte for Step 3 wrong", LONG_PARAMETER_BYTE_ONE_FOLLOW,
				setOutputState[offset]);
		offset += 1;
		assertEquals("Unused Step 3 was not 20", (byte) expectedStep3Degrees, setOutputState[offset]);
	}
}
