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

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.formulaeditor.Sensors;

public interface Phiro extends BluetoothDevice {

	void playTone(int toneFrequency, int duration);

	void moveLeftMotorForward(int speed);
	void moveLeftMotorBackward(int speed);

	void moveRightMotorForward(int speed);
	void moveRightMotorBackward(int speed);

	void stopLeftMotor();
	void stopRightMotor();
	void stopAllMovements();

	void setLeftRGBLightColor(int red, int green, int blue);
	void setRightRGBLightColor(int red, int green, int blue);

	void reportFirmwareVersion();

	int getSensorValue(Sensors sensor);
}
