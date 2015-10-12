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
package org.catrobat.catroid.devices.arduino;

import android.util.Log;

import name.antonsmirnov.firmata.IFirmata;
import name.antonsmirnov.firmata.message.AnalogMessage;
import name.antonsmirnov.firmata.message.DigitalMessage;
import name.antonsmirnov.firmata.message.FirmwareVersionMessage;
import name.antonsmirnov.firmata.message.I2cReplyMessage;
import name.antonsmirnov.firmata.message.ProtocolVersionMessage;
import name.antonsmirnov.firmata.message.StringSysexMessage;
import name.antonsmirnov.firmata.message.SysexMessage;

public class ArduinoListener implements IFirmata.Listener {

	private static final String TAG = ArduinoListener.class.getSimpleName();

	private int analogPin0 = 0;
	private int analogPin1 = 0;
	private int analogPin2 = 0;
	private int analogPin3 = 0;
	private int analogPin4 = 0;
	private int analogPin5 = 0;

	int[] portValue = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	@Override
	public void onAnalogMessageReceived(AnalogMessage message) {
		if (message.getValue() > 1023 || message.getValue() < 0) {
			return;
		}

		Log.d(TAG, String.format("Received Analog Message: %d | Value: %d", message.getPin(), message.getValue()));

		switch (message.getPin()) {
			case ArduinoImpl.PIN_ANALOG_0:
				analogPin0 = message.getValue();
				break;
			case ArduinoImpl.PIN_ANALOG_1:
				analogPin1 = message.getValue();
				break;
			case ArduinoImpl.PIN_ANALOG_2:
				analogPin2 = message.getValue();
				break;
			case ArduinoImpl.PIN_ANALOG_3:
				analogPin3 = message.getValue();
				break;
			case ArduinoImpl.PIN_ANALOG_4:
				analogPin4 = message.getValue();
				break;
			case ArduinoImpl.PIN_ANALOG_5:
				analogPin5 = message.getValue();
				break;
		}
	}

	@Override
	public void onDigitalMessageReceived(DigitalMessage message) {
		if (message.getValue() > 64 || message.getValue() < 0) {
			return;
		}

		Log.d(TAG, String.format("Received Digital Message: port: %d, value: %d",
				message.getPort(), message.getValue()));

		switch (message.getPort()) {
			case ArduinoImpl.PORT_DIGITAL_0:
				portValue [2] = (message.getValue() & 0x4) == 0 ? 0 : 1;
				portValue [3] = (message.getValue() & 0x8) == 0 ? 0 : 1;
				portValue [4] = (message.getValue() & 0x16) == 0 ? 0 : 1;
				portValue [5] = (message.getValue() & 0x32) == 0 ? 0 : 1;
				portValue [6] = (message.getValue() & 0x64) == 0 ? 0 : 1;
				break;
			case ArduinoImpl.PORT_DIGITAL_1:
				portValue [8] = (message.getValue() & 0x1) == 0 ? 0 : 1;
				portValue [9] = (message.getValue() & 0x2) == 0 ? 0 : 1;
				portValue [10] = (message.getValue() & 0x4) == 0 ? 0 : 1;
				portValue [11] = (message.getValue() & 0x8) == 0 ? 0 : 1;
				portValue [12] = (message.getValue() & 0x16) == 0 ? 0 : 1;
				portValue [13] = (message.getValue() & 0x32) == 0 ? 0 : 1;
				break;
		}

		for (int i = 0; i <= 13; i++) {
			Log.d(TAG, String.format("Digital Port Values: %d", portValue[i]));
		}
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

	public int getAnalogPin0() {
		return analogPin0;
	}

	public int getAnalogPin1() {
		return analogPin1;
	}

	public int getAnalogPin2() {
		return analogPin2;
	}

	public int getAnalogPin3() {
		return analogPin3;
	}

	public int getAnalogPin4() {
		return analogPin4;
	}

	public int getAnalogPin5() {
		return analogPin5;
	}

	public int getPortValue(int pin) {
		return portValue[pin];
	}

	public void setPortValue(int pin, int value) {
		this.portValue[pin] = value;
	}
}
