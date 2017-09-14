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

import org.catrobat.catroid.devices.mindstorms.MindstormsMotor;

public class EV3Motor implements MindstormsMotor {
	private byte outputField;

	public EV3Motor(int port) {

		switch (port) {
			case 0:
				this.outputField = EV3MotorOutputByteCode.MOTOR_A_OUT.getByte();
				break;
			case 1:
				this.outputField = EV3MotorOutputByteCode.MOTOR_B_OUT.getByte();
				break;
			case 2:
				this.outputField = EV3MotorOutputByteCode.MOTOR_C_OUT.getByte();
				break;
			case 3:
				this.outputField = EV3MotorOutputByteCode.MOTOR_D_OUT.getByte();
				break;
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public void move(int speed) {
		move(speed, 0, false);
	}

	@Override
	public void move(int speed, int degrees) {
	}

	@Override
	public void move(int speed, int degrees, boolean reply) {
	}

	public byte getOutputField() {
		return outputField;
	}
}
