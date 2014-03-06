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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.RobotAlbertMotorActionBrick.Motor;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.robot.albert.RobotAlbert;

public class RobotAlbertMotorActionAction extends TemporalAction {
	private static final int MIN = -100;
	private static final int MAX = 100;

	private Motor motorEnum;
	private Formula speed;
	private Sprite sprite;

	@Override
	protected void update(float percent) {

		int speedValue = speed.interpretInteger(sprite);
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

		RobotAlbert.sendRobotAlbertMotorMessage(motor, speedValue);
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
