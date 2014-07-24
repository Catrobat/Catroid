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

public class ArduinoControlCommands {

	private byte[] buffer = new byte[3];

	private int pinNumberLowerByte;
	private int pinNumberHigherByte;
	private int pinValue;

	public void setPinNumberLowerByte(int newPinNumberLowerByte) {
		pinNumberLowerByte = newPinNumberLowerByte;
	}

	public void setPinNumberHigherByte(int newPinNumberHigherByte) {
		pinNumberHigherByte = newPinNumberHigherByte;
	}

	public void setPinValue(int newPinValue) {
		pinValue = newPinValue;
	}

	public byte[] resetArduino() {
		buffer[0] = 48; //0
		buffer[1] = 50; //2
		buffer[2] = 72; //H
		return buffer;
	}

	public byte[] pauseArduino() {
		buffer[0] = 00; //0
		buffer[1] = 00; //0
		buffer[2] = 00; //0
		return buffer;
	}

	public byte[] getCommandMessage() {
		buffer[0] = (byte) pinNumberLowerByte;
		buffer[1] = (byte) pinNumberHigherByte;
		buffer[2] = (byte) pinValue;
		return buffer;
	}

}
