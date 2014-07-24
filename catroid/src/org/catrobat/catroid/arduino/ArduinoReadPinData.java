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

/**
 * @author Adrian Schnedlitz
 * 
 */

import android.os.Handler;
import android.util.Log;

public final class ArduinoReadPinData {

	private static ArduinoReadPinData instance = null;
	private static final int ARDUINO_ANALOG_UPDATE_INTERVAL = 50; //ToDo: check if this is enougth
	private Handler handler;
	private float[] incomingSensorValue = new float[2];
	private static boolean debugOutput = false;

	public static final String KEY_SETTINGS_ARDUINO_BRICKS = "setting_arduino_bricks";
	public boolean usingArduinoBricks = false;
	public boolean usingDigitalArduinoPin = true;

	//Periodic update the distance_value
	Runnable updateSensorValue = new Runnable() {
		@Override
		public void run() {

			if (usingDigitalArduinoPin) {
				incomingSensorValue[0] = ArduinoIncomingPinData.getInstance().getArduinoDigitalSensor();
			} else {
				incomingSensorValue[0] = ArduinoIncomingPinData.getInstance().getArduinoAnalogSensor();
			}

			if (debugOutput == true) {
				Log.d("ArduinoSensor", "DigitalSensorValue: "
						+ ArduinoIncomingPinData.getInstance().getArduinoDigitalSensor());
				Log.d("ArduinoSensor", "AnalogSensorValue: "
						+ ArduinoIncomingPinData.getInstance().getArduinoAnalogSensor());
			}

			handler.postDelayed(updateSensorValue, ARDUINO_ANALOG_UPDATE_INTERVAL);
		}
	};

	private ArduinoReadPinData() {
		handler = new Handler();
	};

	public static ArduinoReadPinData getArduinoSensorInstance() {
		if (instance == null) {
			instance = new ArduinoReadPinData();
		}
		return instance;
	}

	//if arduino bricks are used, set the variable true
	public void setBooleanArduinoBricks(boolean status) {
		usingArduinoBricks = status;
	}

	public boolean getBooleanArduinoBricksUsed() {
		return usingArduinoBricks;
	}
}
