/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.devices.mindstorms.ev3.sensors;

public enum EV3SensorType {
	NO_SENSOR(0x00), NXT_TOUCH(0x01), NXT_LIGHT(0x02),
	NXT_SOUND(0x03), NXT_COLOR(0x04), NXT_ULTRASONIC(0x05),
	NXT_TEMPERATURE(0x06),

	EV3_LARGE_MOTOR(0x07), EV3_MEDIUM_MOTOR(0x08),
	EV3_TOUCH(0x10), EV3_COLOR(0x1D), EV3_ULTRASONIC(0x1E),
	EV3_GYRO(0x20), EV3_INFRARED(0x21),

	ENERGY_METER(0x63), IIC(0x64);

	private int sensorTypeValue;

	EV3SensorType(int sensorTypeValue) {
		this.sensorTypeValue = sensorTypeValue;
	}

	public byte getByte() {
		return (byte) sensorTypeValue;
	}
}
