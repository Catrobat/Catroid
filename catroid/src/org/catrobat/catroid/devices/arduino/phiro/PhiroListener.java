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
package org.catrobat.catroid.devices.arduino.phiro;

import android.util.Log;

import name.antonsmirnov.firmata.IFirmata;
import name.antonsmirnov.firmata.message.AnalogMessage;
import name.antonsmirnov.firmata.message.DigitalMessage;
import name.antonsmirnov.firmata.message.FirmwareVersionMessage;
import name.antonsmirnov.firmata.message.I2cReplyMessage;
import name.antonsmirnov.firmata.message.ProtocolVersionMessage;
import name.antonsmirnov.firmata.message.StringSysexMessage;
import name.antonsmirnov.firmata.message.SysexMessage;

class PhiroListener implements IFirmata.Listener {

	private static final String TAG = PhiroListener.class.getSimpleName();

	private int frontLeftSensor = 0;
	private int frontRightSensor = 0;
	private int sideLeftSensor = 0;
	private int sideRightSensor = 0;
	private int bottomLeftSensor = 0;
	private int bottomRightSensor = 0;

	@Override
	public void onAnalogMessageReceived(AnalogMessage message) {
		if (message.getValue() > 1023 || message.getValue() < 0) {
			return;
		}

//		Log.d(TAG, String.format("Pin: %d | Value: %d", message.getPin() ,message.getValue()));

		switch (message.getPin()) {
			case PhiroImpl.PIN_SENSOR_SIDE_RIGHT:
				sideRightSensor = message.getValue();
				break;
			case PhiroImpl.PIN_SENSOR_FRONT_RIGHT:
				frontRightSensor = message.getValue();
				break;
			case PhiroImpl.PIN_SENSOR_BOTTOM_RIGHT:
				bottomRightSensor = message.getValue();
				break;
			case PhiroImpl.PIN_SENSOR_BOTTOM_LEFT:
				bottomLeftSensor = message.getValue();
				break;
			case PhiroImpl.PIN_SENSOR_FRONT_LEFT:
				frontLeftSensor = message.getValue();
				break;
			case PhiroImpl.PIN_SENSOR_SIDE_LEFT:
				sideLeftSensor = message.getValue();
				break;
		}
	}

	@Override
	public void onDigitalMessageReceived(DigitalMessage message) {
		Log.d(TAG, String.format("Received Digital Message: pin: %d, value: %d",
				message.getPort(), message.getValue()));
	}

	@Override
	public void onFirmwareVersionMessageReceived(FirmwareVersionMessage message) {
		Log.d(TAG, String.format("Received Firmware Version Message: Name: %s, Version Major: %d, Minor: %d",
				message.getName(), message.getMajor(), message.getMinor()));
	}

	@Override
	public void onProtocolVersionMessageReceived(ProtocolVersionMessage message) {
		Log.d(TAG, String.format("Received Protocol Version Message: Version Major: %d, Minor: %d",
				message.getMajor(), message.getMinor()));
	}

	@Override
	public void onSysexMessageReceived(SysexMessage message) {
		Log.d(TAG, "Sysex Message received: " + message.getCommand());
	}

	@Override
	public void onStringSysexMessageReceived(StringSysexMessage message) {
		Log.d(TAG, "String Sysex Message received: " + message.getCommand());
	}

	@Override
	public void onI2cMessageReceived(I2cReplyMessage message) {
		Log.d(TAG, "I2C Message received: " + message.getCommand());
	}

	@Override
	public void onUnknownByteReceived(int byteValue) {
		//Log.d(TAG, "Unkown Byte received. Byte value: " + byteValue);
	}

	public int getFrontLeftSensor() {
		return frontLeftSensor;
	}

	public int getFrontRightSensor() {
		return frontRightSensor;
	}

	public int getSideLeftSensor() {
		return sideLeftSensor;
	}

	public int getSideRightSensor() {
		return sideRightSensor;
	}

	public int getBottomLeftSensor() {
		return bottomLeftSensor;
	}

	public int getBottomRightSensor() {
		return bottomRightSensor;
	}
}
