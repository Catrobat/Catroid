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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class LegoEv3MotorTurnAngleAction extends TemporalAction {
	private static final int MAX_SPEED = 100;
	private static final int POWER_DOWN_RAMP_DEGREES = 20;

	private LegoEv3MotorTurnAngleBrick.Motor motorEnum;
	private Formula degrees;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {
		int degreesValue;
		try {
			degreesValue = degrees.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			degreesValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		int tmpAngle = degreesValue;
		int direction = 1;
		if (degreesValue < 0) {
			direction = -1;
			tmpAngle = degreesValue + (-2 * degreesValue);
		}

		int step2Angle = 0;
		int step3Angle = 0;
		if (tmpAngle > POWER_DOWN_RAMP_DEGREES) {
			step2Angle = tmpAngle - POWER_DOWN_RAMP_DEGREES;
			step3Angle = POWER_DOWN_RAMP_DEGREES;
		} else {
			step2Angle = tmpAngle;
		}

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
		}

		ev3.moveMotorStepsSpeed(outputField, 0, direction * MAX_SPEED, 0, step2Angle, step3Angle, true);
	}

	public void setMotorEnum(LegoEv3MotorTurnAngleBrick.Motor motorEnum) {
		this.motorEnum = motorEnum;
	}

	public void setDegrees(Formula degrees) {
		this.degrees = degrees;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
