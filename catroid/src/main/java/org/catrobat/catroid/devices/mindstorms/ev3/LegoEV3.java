/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.devices.mindstorms.ev3;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.devices.mindstorms.Mindstorms;
import org.catrobat.catroid.devices.mindstorms.MindstormsSensor;
import org.catrobat.catroid.formulaeditor.Sensors;

public interface LegoEV3 extends Mindstorms, BluetoothDevice {

	boolean isAlive();

	void playTone(int frequency, int duration, int volumeInPercent);

	EV3Motor getMotorA();
	EV3Motor getMotorB();
	EV3Motor getMotorC();
	EV3Motor getMotorD();

	void stopAllMovements();

	void moveMotorStepsSpeed(byte outputField, int chainLayer, int speed, int step1Tacho, int step2Tacho,
			int step3Tacho, boolean brake);
	void moveMotorTime(byte outputField, int chainLayer, int power, int step1TimeInMs, int step2TimeInMs, int step3TimeInMs, boolean brake);
	void moveMotorSpeed(byte outputField, int chainLayer, int speed);
	void stopMotor(byte outputField, int chainLayer, boolean brake);

	void setLed(int ledStatus);

	int getSensorValue(Sensors sensor);

	MindstormsSensor getSensor1();
	MindstormsSensor getSensor2();
	MindstormsSensor getSensor3();
	MindstormsSensor getSensor4();
}
