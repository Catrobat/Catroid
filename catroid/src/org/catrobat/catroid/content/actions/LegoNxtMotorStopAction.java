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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick.Motor;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;

public class LegoNxtMotorStopAction extends TemporalAction {

	private Motor motorEnum;
	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {

		LegoNXT nxt = btService.getDevice(BluetoothDevice.LEGO_NXT);
		if (nxt == null) {
			return;
		}

		switch (motorEnum) {
			case MOTOR_A:
				nxt.getMotorA().stop();
				break;
			case MOTOR_B:
				nxt.getMotorB().stop();
				break;
			case MOTOR_C:
				nxt.getMotorC().stop();
				break;
			case MOTOR_B_C:
				nxt.getMotorB().stop();
				nxt.getMotorC().stop();
				break;
			case ALL_MOTORS:
				nxt.stopAllMovements();
				break;
		}
	}

	public void setMotorEnum(Motor motorEnum) {
		this.motorEnum = motorEnum;
	}
}
