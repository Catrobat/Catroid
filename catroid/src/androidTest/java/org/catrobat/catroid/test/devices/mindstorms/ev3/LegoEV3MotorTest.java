/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LegoEV3MotorTest {

	private Context applicationContext;

	private LegoEV3 ev3;
	ConnectionDataLogger logger;

	private static final int BASIC_MESSAGE_BYTE_OFFSET = 6;
	private static final int POWER_DOWN_RAMP_DEGREES = 20;

	private static final byte LONG_PARAMETER_BYTE_ONE_FOLLOW = (byte) 0x81;
	private static final byte LONG_PARAMETER_BYTE_TWO_FOLLOW = (byte) 0x82;

	@Before
	public void setUp() throws Exception {

		applicationContext = ApplicationProvider.getApplicationContext().getApplicationContext();

		ev3 = new LegoEV3Impl(this.applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		ev3.setConnection(logger.getConnectionProxy());
	}

	@Test
	public void testMotorMoveTest() throws MindstormsException {
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

		assertEquals(expectedOutputField, setOutputState[offset]);
		offset += 1;

		assertEquals((byte) expectedSpeed, setOutputState[offset]);

		setOutputState = this.logger.getNextSentMessage(0, 2);
		assertEquals(startCmdCode, setOutputState[5]);

		offset = BASIC_MESSAGE_BYTE_OFFSET + 1;
		assertEquals(expectedOutputField, setOutputState[offset]);
	}

	@Test
	public void testStopMotorTest() throws MindstormsException {
		byte outputField = (byte) 0x01;
		byte expectedOutputField = (byte) 0x01;

		ev3.initialise();
		ev3.stopMotor(outputField, 0, true);

		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		int offset = BASIC_MESSAGE_BYTE_OFFSET + 1;

		assertEquals(expectedOutputField, setOutputState[offset]);
	}

	@Test
	public void testMotorTurnAngle360DegreeTest() throws MindstormsException {
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

		assertEquals(expectedOutputField, setOutputState[offset]);
		offset += 2;

		assertEquals((byte) expectedSpeed, setOutputState[offset]);
		offset += 1;

		assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset]);
		offset += 1;
		assertEquals((byte) expectedStep1Degrees, setOutputState[offset]);
		offset += 1;

		assertEquals(LONG_PARAMETER_BYTE_TWO_FOLLOW, setOutputState[offset]);
		offset += 1;
		assertEquals((byte) expectedStep2Degrees, setOutputState[offset]);
		assertEquals((byte) (expectedStep2Degrees >> 8), setOutputState[offset + 1]);
		offset += 2;

		assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset]);
		offset += 1;
		assertEquals((byte) expectedStep3Degrees, setOutputState[offset]);
	}

	@Test
	public void testMotorTurnAngleMinus15DegreeTest() throws MindstormsException {
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

		assertEquals(expectedOutputField, setOutputState[offset]);
		offset += 2;

		assertEquals((byte) expectedSpeed, setOutputState[offset]);
		offset += 1;

		assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset]);
		offset += 1;
		assertEquals((byte) expectedStep1Degrees, setOutputState[offset]);
		offset += 1;

		assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset]);
		offset += 1;
		assertEquals((byte) expectedStep2Degrees, setOutputState[offset]);
		offset += 1;

		assertEquals(LONG_PARAMETER_BYTE_ONE_FOLLOW, setOutputState[offset]);
		offset += 1;
		assertEquals((byte) expectedStep3Degrees, setOutputState[offset]);
	}
}
