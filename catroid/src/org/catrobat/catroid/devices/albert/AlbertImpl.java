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

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;

import java.util.UUID;

public class AlbertImpl implements Albert {

	public static final int EYE_LEFT = 0;
	public static final int EYE_RIGHT = 1;
	public static final int EYE_BOTH = 2;
	public static final int MOTOR_LEFT = 0;
	public static final int MOTOR_RIGHT = 1;
	public static final int MOTOR_BOTH = 2;
	private static final UUID ALBERT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc");
	public static boolean ALBERT_SENSOR_DEBUG_OUTPUT = true;
	private AlbertSendCommands commands;
	private AlbertConnection albertConnection;

	@Override
	public String getName() {
		return "Albert";
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return BluetoothDevice.ALBERT;
	}

	@Override
	public void setConnection(BluetoothConnection connection) {
		this.albertConnection = new AlbertConnection(connection);
		commands = new AlbertSendCommands();
	}

	@Override
	public void disconnect() {
		this.albertConnection.disconnect();
	}

	@Override
	public boolean isAlive() {
		return false;
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return ALBERT_UUID;
	}

	@Override
	public void initialise() {

	}

	@Override
	public void start() {

	}

	@Override
	public void pause() {
		//TODO: albert pauseState and re-enable old state on resume
//		commands.setPauseState();
//		albertConnection.send(commands);
	}

	@Override
	public void destroy() {
		commands.resetRobotAlbert();
		albertConnection.send(commands);
	}

	@Override
	public void move(int motor, int speed) {
		if (motor == MOTOR_LEFT) {
			commands.setSpeedOfLeftMotor(speed);
		} else if (motor == MOTOR_RIGHT) {
			commands.setSpeedOfRightMotor(speed);
		} else if (motor == MOTOR_BOTH) {
			commands.setSpeedOfLeftMotor(speed);
			commands.setSpeedOfRightMotor(speed);
		} else {
			return;
		}
		albertConnection.send(commands);
	}

	@Override
	public void setBuzzer(int frequency) {
		commands.setBuzzer(frequency);
		albertConnection.send(commands);
	}

	@Override
	public void setFrontLed(int status) {
		commands.setFrontLed(status);
		albertConnection.send(commands);
	}

	@Override
	public void setBodyLed(int value) {
		commands.setBodyLed(value);
		albertConnection.send(commands);
	}

	@Override
	public void setRgbLedEye(int eye, int red, int green, int blue) {
		if (eye == EYE_LEFT) {
			commands.setLeftEye(red, green, blue);
		} else if (eye == EYE_RIGHT) {
			commands.setRightEye(red, green, blue);
		} else if (eye == EYE_BOTH) {
			commands.setLeftEye(red, green, blue);
			commands.setRightEye(red, green, blue);
		} else {
			return;
		}
		albertConnection.send(commands);
	}

	@Override
	public int getDistanceLeft() {
		return 0;
	}

	@Override
	public int getDistanceRight() {
		return 0;
	}

}
