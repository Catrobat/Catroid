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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.RobotAlbertRgbLedEyeActionBrick.Eye;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.robot.albert.RobotAlbert;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class RobotAlbertRgbLedEyeAction extends TemporalAction {
	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 255;

	private Eye eyeEnum;
	private Eye eye;
	private Formula red;
	private Formula green;
	private Formula blue;
	private Sprite sprite;

	@Override
	protected void update(float percent) {

		int redValue = red.interpretInteger(MIN_VALUE, MAX_VALUE, sprite);
		int greenValue = green.interpretInteger(MIN_VALUE, MAX_VALUE, sprite);
		int blueValue = blue.interpretInteger(MIN_VALUE, MAX_VALUE, sprite);

		/*
		 * if (motorEnum.equals(Motor.MOTOR_A_C)) {
		 * LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_A.ordinal(), speedValue, 0);
		 * LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_C.ordinal(), speedValue, 0);
		 * } else {
		 * LegoNXT.sendBTCMotorMessage(NO_DELAY, motorEnum.ordinal(), speedValue, 0);
		 * }
		 */

		//LegoNXT.sendBTCMotorMessage((int) (duration * 1000), motor, 0, 0);
		//RobotAlbert.sendRobotAlbertMotorMessage();
		int eye = 2;
		if (eyeEnum.equals(Eye.Left)) {
			eye = Eye.Left.ordinal();
		} else if (eyeEnum.equals(Eye.Right)) {
			eye = Eye.Right.ordinal();
		} else if (eyeEnum.equals(Eye.Both)) {
			eye = Eye.Both.ordinal();
		} else {
			Log.d("Albert", "Error: EyeEnum:" + eyeEnum);
		}

		Log.d("RobotAlbert", "RobotAlbertRgbLedEyeAction before send: rbg=" + red + "|" + green + "|" + blue);
		RobotAlbert.sendRobotAlbertRgbLedEyeMessage(eye, redValue, greenValue, blueValue);
		Log.d("RobotAlbert", "RobotAlbertRgbLedEyeAction after sended");
	}

	public void setEyeEnum(Eye eyeEnum) {
		this.eyeEnum = eyeEnum;
	}

	public void setRed(Formula red) {
		this.red = red;
	}

	public void setGreen(Formula green) {
		this.green = green;
	}

	public void setBlue(Formula blue) {
		this.blue = blue;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}
