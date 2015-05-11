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

package org.catrobat.catroid.devices.albert;

import java.util.Arrays;

public class AlbertSendCommands {

	private final byte[]buffer = new byte[22];

	// Actor variables
	private int speedMotorLeft;		// -100...+100
	private int speedMotorRight;	// -100...+100
	private int buzzer;
	private int leftLedGreen;		// 0...255
	private int leftLedBlue; 		// 0...255
	private int leftLedRed;			// 0...255
	private int rightLedGreen;		// 0...255
	private int rightLedBlue;		// 0...255
	private int rightLedRed;		// 0...255
	private int frontLed;			// Front-LED 0...1
	private int bodyLed;			// Body-LED 0...255
	private byte sendFrameNumber = 1;

	public AlbertSendCommands() {
		buffer[0] = (byte) 0xAA;	// HEADER
		buffer[1] = (byte) 0x55;	// HEADER
		buffer[2] = (byte) 20;
		buffer[3] = (byte) 6;
		buffer[4] = (byte) 0x11;
		buffer[5] = sendFrameNumber;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) 0;		// Right motor
		buffer[9] = (byte) 0;		// Left motor
		buffer[10] = (byte) 0;		// Buzzer
		buffer[11] = (byte) 0;		// Left LED Red
		buffer[12] = (byte) 0;		// Left LED Green
		buffer[13] = (byte) 0;		// Left LED Blue
		buffer[14] = (byte) 0;		// Right LED Red
		buffer[15] = (byte) 0;		// Right LED Green
		buffer[16] = (byte) 0;		// Right LED Blue
		buffer[17] = (byte) 0;		// Front-LED 0...1
		buffer[18] = (byte) 0;		// Reserved
		buffer[19] = (byte) 0;		// Body-LED 0...255
		buffer[20] = (byte) 0x0D;	// TAIL
		buffer[21] = (byte) 0x0A;	// TAIL
	}

	public void setSpeedOfLeftMotor(int speed) {
		speedMotorLeft = speed;
	}

	public void setSpeedOfRightMotor(int speed) {
		speedMotorRight = speed;
	}

	public void setBuzzer(int buzz) {
		buzzer = buzz;
	}

	public void setFrontLed(int status) {
		frontLed = status;
	}

	public void setBodyLed(int bodyLedStatus) {
		bodyLed = bodyLedStatus;
	}

	public void setLeftEye(int red, int green, int blue) {
		leftLedRed = red;
		leftLedGreen = green;
		leftLedBlue = blue;
	}

	public void setRightEye(int red, int green, int blue) {
		rightLedRed = red;
		rightLedGreen = green;
		rightLedBlue = blue;
	}

	public byte[] getCommandMessage() {
		buffer[5] = (byte) (sendFrameNumber + 1);
		sendFrameNumber = (byte) (sendFrameNumber + 2);
		buffer[8] = (byte) speedMotorRight;
		buffer[9] = (byte) speedMotorLeft;
		buffer[10] = (byte) buzzer;
		buffer[11] = (byte) leftLedRed;
		buffer[12] = (byte) leftLedGreen;
		buffer[13] = (byte) leftLedBlue;
		buffer[14] = (byte) rightLedRed;
		buffer[15] = (byte) rightLedGreen;
		buffer[16] = (byte) rightLedBlue;
		buffer[17] = (byte) frontLed;
		buffer[19] = (byte) bodyLed;
		return Arrays.copyOf(buffer, buffer.length);
	}

	public void resetRobotAlbert() {
		setBuzzer(0);
		setBodyLed(0);
		setSpeedOfLeftMotor(0);
		setSpeedOfRightMotor(0);
		setLeftEye(0,0,0);
		setRightEye(0,0,0);
		setFrontLed(0);
	}
	public void setPauseState() {
		setBuzzer(0);
		setBodyLed(0);
		setSpeedOfLeftMotor(0);
		setSpeedOfRightMotor(0);
		setLeftEye(255,255,255);
		setRightEye(255,255,255);
		setFrontLed(0);
	}

}
