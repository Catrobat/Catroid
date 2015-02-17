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
 * Adrian Schnedlitz *
 */
public final class ArduinoIncomingPinData {

	private static ArduinoIncomingPinData instance = null;

	//	private static Semaphore receiveSyncSemaphore = new Semaphore(1);

	//Sensor variables
	private int arduinoDigitalSensor = 0; //0 or 1 (Low or High)
	private int arduinoAnalogSensor = 0; //0 - 1023

	private int arduinoDigitalSensorLowerByte = 0;
	private int arduinoDigitalSensorHigherByte = 0;
	private int arduinoAnalogSensorLowerByte = 0;
	private int arduinoAnalogSensorHigherByte = 0;

	private ArduinoIncomingPinData() {

	}

	public static ArduinoIncomingPinData getInstance() {
		if (instance == null) {
			instance = new ArduinoIncomingPinData();
		}
		return instance;
	}

	public int getArduinoDigitalSensor() {
		return arduinoDigitalSensor;
	}

	public void setArduinoDigitalSensor(int value) {
		arduinoDigitalSensor = value;
	}

	public int getArduinoAnalogSensor() {
		return arduinoAnalogSensor;
	}

	public void setArduinoAnalogSensor(int value) {
		arduinoAnalogSensor = value;
	}

	public int getArduinoDigitalSensorLowerByte() {
		return arduinoDigitalSensorLowerByte;
	}

	public void setArduinoDigitalSensorLowerByte(int arduinoDigitalSensorLowerByte) {
		this.arduinoDigitalSensorLowerByte = arduinoDigitalSensorLowerByte;
	}

	public int getArduinoDigitalSensorHigherByte() {
		return arduinoDigitalSensorHigherByte;
	}

	public void setArduinoDigitalSensorHigherByte(int arduinoDigitalSensorHigherByte) {
		this.arduinoDigitalSensorHigherByte = arduinoDigitalSensorHigherByte;
	}

	public int getArduinoAnalogSensorLowerByte() {
		return arduinoAnalogSensorLowerByte;
	}

	public void setArduinoAnalogSensorLowerByte(int arduinoAnalogSensorLowerByte) {
		this.arduinoAnalogSensorLowerByte = arduinoAnalogSensorLowerByte;
	}

	public int getArduinoAnalogSensorHigherByte() {
		return arduinoAnalogSensorHigherByte;
	}

	public void setArduinoAnalogSensorHigherByte(int arduinoAnalogSensorHigherByte) {
		this.arduinoAnalogSensorHigherByte = arduinoAnalogSensorHigherByte;
	}
}
