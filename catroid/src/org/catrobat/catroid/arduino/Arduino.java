/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.arduino;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.catrobat.catroid.bluetooth.BTConnectable;
import org.catrobat.catroid.bluetooth.DeviceListActivity;

import java.io.IOException;

public class Arduino implements BTConnectable {

	private static final int SET_DIGITAL_PIN_VALUE_COMMAND = 100;

	private static final String TAG = Arduino.class.getSimpleName();
	private static Handler btcHandler;
	private static ArduinoCommunicator myArduinoCommunicator;
	private boolean isPairing;
	private Handler recieverHandler;
	private Activity activity;

	public Arduino(Activity activity, Handler recieverHandler) {
		this.activity = activity;
		this.recieverHandler = recieverHandler;
	}

	public void startBTCommunicator(String macAddress) {
		if (myArduinoCommunicator != null) {
			try {
				myArduinoCommunicator.destroyArduinoConnection();
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}


		myArduinoCommunicator = new ArduinoBtCommunicator(recieverHandler, activity.getResources());
		btcHandler = myArduinoCommunicator.getHandler();

		((ArduinoBtCommunicator) myArduinoCommunicator).setMACAddress(macAddress);
		myArduinoCommunicator.start();
	}

	public void destroyCommunicator() {

		if (myArduinoCommunicator != null) {
			try {
				myArduinoCommunicator.destroyArduinoConnection();
			} catch (IOException ioException) {
				Log.e(TAG, Log.getStackTraceString(ioException));
			}
			myArduinoCommunicator = null;
		}
	}

	public void pauseCommunicator() {
		myArduinoCommunicator.stopSensors();
	}

	public static synchronized void sendArduinoDigitalPinMessage(int pinLowerByte, int pinHigherByte, int value) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("pinLowerByte", pinLowerByte);
		myBundle.putInt("pinHigherByte", pinHigherByte);
		myBundle.putInt("value", value);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = SET_DIGITAL_PIN_VALUE_COMMAND;
		btcHandler.sendMessage(myMessage);
	}

	public static int getArduinoDigitalSensorMessage() {
		int value = myArduinoCommunicator.sensors.getArduinoDigitalSensor();
		return value;
	}

	public static int getArduinoAnalogSensorMessage() {
		int value = myArduinoCommunicator.sensors.getArduinoAnalogSensor();
		return value;
	}

	public static Handler getBTCHandler() {
		return btcHandler;
	}

	@Override
	public boolean isPairing() {
		return isPairing;
	}
}
