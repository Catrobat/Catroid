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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick.Motor;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;

public class LegoEv3MotorStopAction extends TemporalAction {

	private Motor motorEnum;
	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {

		LegoEV3 ev3 = btService.getDevice(BluetoothDevice.LEGO_EV3);
		if (ev3 == null) {
			return;
		}

		byte outputField = (byte) 0x00;

		switch (motorEnum) {
			case MOTOR_A:
				outputField = (byte) 0x01;
				break;
			case MOTOR_B:
				outputField = (byte) 0x02;
				break;
			case MOTOR_C:
				outputField = (byte) 0x04;
				break;
			case MOTOR_D:
				outputField = (byte) 0x08;
				break;
			case MOTOR_B_C:
				outputField = (byte) 0x06;
				break;
			case ALL_MOTORS:
				outputField = (byte) 0x0F;
				break;
		}

		ev3.stopMotor(outputField, 0, true);
	}

	public void setMotorEnum(Motor motorEnum) {
		this.motorEnum = motorEnum;
	}
}
