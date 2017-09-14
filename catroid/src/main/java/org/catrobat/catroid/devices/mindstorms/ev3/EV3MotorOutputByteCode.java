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

package org.catrobat.catroid.devices.mindstorms.ev3;

public enum EV3MotorOutputByteCode {
	MOTOR_A_OUT(0x01), MOTOR_B_OUT(0x02), MOTOR_C_OUT(0x04), MOTOR_D_OUT(0x08);

	private int ev3MotorOutputValue;

	EV3MotorOutputByteCode(int ev3MotorOutputValue) {
		this.ev3MotorOutputValue = ev3MotorOutputValue;
	}

	public byte getByte() {
		return (byte) ev3MotorOutputValue;
	}
}
