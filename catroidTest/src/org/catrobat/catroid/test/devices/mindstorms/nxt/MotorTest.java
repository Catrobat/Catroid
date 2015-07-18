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

package org.catrobat.catroid.test.devices.mindstorms.nxt;

import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTMotor;

public class MotorTest extends AndroidTestCase {

	private NXTMotor motor;
	private ConnectionDataLogger logger;
	private static final byte DIRECT_COMMAND_HEADER = (byte) (CommandType.DIRECT_COMMAND.getByte() | 0x80);

	private static final int USED_PORT = 0;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.logger = ConnectionDataLogger.createLocalConnectionLogger();
		MindstormsConnection mindstormsConnection = new MindstormsConnectionImpl(logger.getConnectionProxy());
		mindstormsConnection.init();
		this.motor = new NXTMotor(USED_PORT, mindstormsConnection);
	}

	@Override
	protected void tearDown() throws Exception {
		logger.disconnectAndDestroy();
		super.tearDown();
	}

	public void testSimpleMotorTest() {
		int inputSpeed = 70;
		int degrees = 360;
		byte motorRegulationSpeed = 0x01;
		byte expectedTurnRatio = 100;

		motor.move(inputSpeed, degrees);
		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		assertEquals("DIRECT_COMMAND_HEADER check failed, should equals to setOutputState[0]", DIRECT_COMMAND_HEADER, setOutputState[0]);
		assertEquals("SET_OUTPUT_STATE check failed, should equals to setOutputState[1]", CommandByte.SET_OUTPUT_STATE.getByte(), setOutputState[1]);
		assertEquals("USED_PORT check failed, should equals to setOutputState[2]", USED_PORT, setOutputState[2]);

		assertEquals("inputSpeed should equals setOutputState[3]", inputSpeed, setOutputState[3]);

		assertEquals("Motor mode should equals to setOutputState[4]",
				NXTMotor.MotorMode.BREAK | NXTMotor.MotorMode.ON | NXTMotor.MotorMode.REGULATED, setOutputState[4]);

		assertEquals("Motor regulation speed should equals to setOutputSate[5]", motorRegulationSpeed, setOutputState[5]);
		assertEquals("Expected turn ratio should euqals to setOutputState[6]", expectedTurnRatio, setOutputState[6]);
		assertEquals("Expected Motor Run State should equals to setOutputState[7]", NXTMotor.MotorRunState.RUNNING.getByte(), setOutputState[7]);
		checkDegrees(degrees, setOutputState);
	}

	public void testMotorSpeedOverHundred() {

		int inputSpeed = 120;
		int expectedSpeed = 100;
		int degrees = 360;
		byte motorRegulationSpeed = 0x01;
		byte expectedTurnRatio = 100;

		motor.move(inputSpeed, degrees);
		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		assertEquals("DIRECT_COMMAND_HEADER check failed, should equals to setOutputState[0]", DIRECT_COMMAND_HEADER, setOutputState[0]);
		assertEquals("SET_OUTPUT_STATE check failed, should equals to setOutputState[1]", CommandByte.SET_OUTPUT_STATE.getByte(), setOutputState[1]);
		assertEquals("USED_PORT check failed, should equals to setOutputState[2]", USED_PORT, setOutputState[2]);
		assertEquals("expectedSpeed should equals setOutputState[3]", expectedSpeed, setOutputState[3]);

		assertEquals("Motor mode should equals to setOutputState[4]",
				NXTMotor.MotorMode.BREAK | NXTMotor.MotorMode.ON | NXTMotor.MotorMode.REGULATED, setOutputState[4]);

		assertEquals("Motor regulation speed should equals to setOutputSate[5]", motorRegulationSpeed, setOutputState[5]);
		assertEquals("Expected turn ratio should euqals to setOutputState[6]", expectedTurnRatio, setOutputState[6]);
		assertEquals("Expected Motor Run State should equals to setOutputState[7]", NXTMotor.MotorRunState.RUNNING.getByte(), setOutputState[7]);
		checkDegrees(degrees, setOutputState);
	}

	public void testMotorWithZeroValues() {

		int inputSpeed = 0;
		int degrees = 0;
		byte motorRegulationSpeed = 0x01;
		byte expectedTurnRatio = 100;

		motor.move(inputSpeed, degrees);
		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		assertEquals("DIRECT_COMMAND_HEADER check failed, should equals to setOutputState[0]", DIRECT_COMMAND_HEADER, setOutputState[0]);
		assertEquals("SET_OUTPUT_STATE check failed, should equals to setOutputState[1]", CommandByte.SET_OUTPUT_STATE.getByte(), setOutputState[1]);
		assertEquals("USED_PORT check failed, should equals to setOutputState[2]", USED_PORT, setOutputState[2]);

		assertEquals("inputSpeed should equals setOutputState[3]", inputSpeed, setOutputState[3]);

		assertEquals("Motor mode should equals to setOutputState[4]",
				NXTMotor.MotorMode.BREAK | NXTMotor.MotorMode.ON | NXTMotor.MotorMode.REGULATED, setOutputState[4]);

		assertEquals("Motor regulation speed should equals to setOutputSate[5]", motorRegulationSpeed, setOutputState[5]);
		assertEquals("Expected turn ratio should euqals to setOutputState[6]", expectedTurnRatio, setOutputState[6]);
		assertEquals("Expected Motor Run State should equals to setOutputState[7]", NXTMotor.MotorRunState.RUNNING.getByte(), setOutputState[7]);
		checkDegrees(degrees, setOutputState);
	}

	public void testMotorWithNegativeSpeedOverHundred() {

		int inputSpeed = -120;
		int expectedSpeed = -100;
		int degrees = 360;
		byte motorRegulationSpeed = 0x01;
		byte expectedTurnRatio = 100;

		motor.move(inputSpeed, degrees);
		byte[] setOutputState = this.logger.getNextSentMessage(0, 2);

		assertEquals("DIRECT_COMMAND_HEADER check failed, should equals to setOutputState[0]", DIRECT_COMMAND_HEADER, setOutputState[0]);
		assertEquals("SET_OUTPUT_STATE check failed, should equals to setOutputState[1]", CommandByte.SET_OUTPUT_STATE.getByte(), setOutputState[1]);
		assertEquals("USED_PORT check failed, should equals to setOutputState[2]", USED_PORT, setOutputState[2]);

		assertEquals("ExpectedSpeed should equals setOutputState[3]", expectedSpeed, setOutputState[3]);

		assertEquals("Motor mode should equals to setOutputState[4]",
				NXTMotor.MotorMode.BREAK | NXTMotor.MotorMode.ON | NXTMotor.MotorMode.REGULATED, setOutputState[4]);

		assertEquals("Motor regulation speed should equals to setOutputSate[5]", motorRegulationSpeed, setOutputState[5]);
		assertEquals("Expected turn ratio should euqals to setOutputState[6]", expectedTurnRatio, setOutputState[6]);
		assertEquals("Expected Motor Run State should equals to setOutputState[7]", NXTMotor.MotorRunState.RUNNING.getByte(), setOutputState[7]);
		checkDegrees(degrees, setOutputState);
	}

	public void checkDegrees(int degrees, byte[] setOutputState) {
		assertEquals("Degree check failed, value should equals to setOutputState[8]", (byte) degrees, setOutputState[8]);
		assertEquals("Degree check failed, value should equals to setOutputState[9]", (byte) (degrees >> 8), setOutputState[9]);
	}
}
