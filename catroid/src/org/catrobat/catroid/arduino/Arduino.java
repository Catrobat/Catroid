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
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.catrobat.catroid.bluetooth.BTConnectable;

import java.io.IOException;

public class Arduino implements BTConnectable {

	private static final String TAG = BTConnectable.class.getSimpleName();

	private static ArduinoCommunicator myCommunicator;

	private boolean isPairing;
	private static Handler btcHandler = null;
	private Handler recieverHandler;
	private Activity activity;

	private static final int SET_DIGITAL_PIN_VALUE_COMMAND = 100;

	public Arduino(Activity activity, Handler recieverHandler) {
		this.activity = activity;
		this.recieverHandler = recieverHandler;
	}

	public void startBTCommunicator(String macAddress) {

		if (myCommunicator != null) {
			try {
				myCommunicator.destroyConnection();
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}


		myCommunicator = new ArduinoBtCommunicator(recieverHandler, activity.getResources());
		btcHandler = myCommunicator.getHandler();

		((ArduinoBtCommunicator) myCommunicator).setMACAddress(macAddress);
		myCommunicator.start();
	}

	public void destroyCommunicator() {

		if (myCommunicator != null) {
			try {
				myCommunicator.destroyConnection();
			} catch (IOException ioException) {
				Log.e(TAG, Log.getStackTraceString(ioException));
			}
			myCommunicator = null;
		}
	}

	public void pauseCommunicator() {
		myCommunicator.stopSensors();
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
		int value = myCommunicator.sensors.getArduinoDigitalSensor();
		return value;
	}

	public static int getArduinoAnalogSensorMessage() {
		int value = myCommunicator.sensors.getArduinoAnalogSensor();
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
