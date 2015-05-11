/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.RobotAlbertMotorBrick.Motor;
import org.catrobat.catroid.devices.albert.Albert;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
//import org.catrobat.catroid.robot.albert.RobotAlbert;

public class RobotAlbertMotorAction extends TemporalAction {
	private static final int MIN = -100;
	private static final int MAX = 100;

	private Motor motorEnum;
	private Formula speed;
	private Sprite sprite;
	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {
		int speedValue = 0;
		try {
			speedValue = speed.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Couldn't interpret Formula.", interpretationException);
		}
		if (speedValue < MIN) {
			speedValue = MIN;
		} else if (speedValue > MAX) {
			speedValue = MAX;
		}

		int motor = 2;
		if (motorEnum.equals(Motor.Left)) {
			motor = Motor.Left.ordinal();
		} else if (motorEnum.equals(Motor.Right)) {
			motor = Motor.Right.ordinal();
		} else if (motorEnum.equals(Motor.Both)) {
			motor = Motor.Both.ordinal();
		} else {
			Log.d("Albert", "Error: motorEnum:" + motorEnum);
		}
		Albert albert = btService.getDevice(BluetoothDevice.ALBERT);
		if (albert == null) {
			return;
		}
		albert.move(motor, speedValue);
	}

	public void setMotorEnum(Motor motorEnum) {
		this.motorEnum = motorEnum;
	}

	public void setSpeed(Formula speed) {
		this.speed = speed;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}
