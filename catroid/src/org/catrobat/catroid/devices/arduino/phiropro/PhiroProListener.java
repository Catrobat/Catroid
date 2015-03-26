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

package org.catrobat.catroid.devices.arduino.phiropro;


import android.util.Log;

import org.catrobat.catroid.devices.arduino.common.firmata.IFirmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.AnalogMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.FirmwareVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cReplyMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.Message;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ProtocolVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.StringSysexMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SysexMessage;

class PhiroProListener implements IFirmata.Listener {

	private static final String TAG = PhiroProListener.class.getSimpleName();

	private static final int frontLeftSensor = 0;
	private static final int frontRightSensor = 1;
	private static final int sideLeftSensor = 2;
	private static final int sideRightSensor = 3;
	private static final int bottomLeftSensor = 4;
	private static final int bottomRightSensor = 5;

	private int[] counter = {0, 0, 0, 0, 0, 0};
	private int[] values = {0, 0, 0, 0, 0, 0};

	private int[] avg_sensor_values = {0, 0, 0, 0, 0, 0};

	private static final int AVG_INTERVAL = 10;

	@Override
	public void onAnalogMessageReceived(AnalogMessage message) {

		if (message.getValue() > 1023 || message.getValue() < 0) {
			return;
		}

		switch (message.getPin()) {
			case PhiroProImpl.PIN_SENSOR_SIDE_RIGHT:
				updateAnalogSensorValue(sideRightSensor, message);
				break;
			case PhiroProImpl.PIN_SENSOR_FRONT_RIGHT:
				updateAnalogSensorValue(frontRightSensor, message);
				break;
			case PhiroProImpl.PIN_SENSOR_BOTTOM_RIGHT:
				updateAnalogSensorValue(bottomRightSensor, message);
				break;
			case PhiroProImpl.PIN_SENSOR_BOTTOM_LEFT:
				updateAnalogSensorValue(bottomLeftSensor, message);
				break;
			case PhiroProImpl.PIN_SENSOR_FRONT_LEFT:
				updateAnalogSensorValue(frontLeftSensor, message);
				break;
			case PhiroProImpl.PIN_SENSOR_SIDE_LEFT:
				updateAnalogSensorValue(sideLeftSensor, message);
				break;

		}

//		Log.d(TAG, String.format("Received Analog Message: pin: %d, value: %d",
//				message.getPin(), message.getValue()));
	}

	private synchronized void updateAnalogSensorValue(int sensor, AnalogMessage message) {
		values[sensor] += message.getValue();
		counter[sensor]++;

		if (counter[sensor] >= AVG_INTERVAL) {
			avg_sensor_values[sensor] = values[sensor] / AVG_INTERVAL;
			values[sensor] = 0;
			counter[sensor] = 0;
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
		return avg_sensor_values[frontLeftSensor];
	}

	public int getFrontRightSensor() {
		return avg_sensor_values[frontRightSensor];
	}

	public int getSideLeftSensor() {
		return avg_sensor_values[sideLeftSensor];
	}

	public int getSideRightSensor() {
		return avg_sensor_values[sideRightSensor];
	}

	public int getBottomLeftSensor() {
		return avg_sensor_values[bottomLeftSensor];
	}

	public int getBottomRightSensor() {
		return avg_sensor_values[bottomRightSensor];
	}
}
