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
import org.catrobat.catroid.content.bricks.RobotAlbertRgbLedEyeActionBrick.Eye;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.robot.albert.RobotAlbert;

public class RobotAlbertRgbLedEyeAction extends TemporalAction {
	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 255;

	private Eye eyeEnum;
	private Formula red;
	private Formula green;
	private Formula blue;
	private Sprite sprite;

	@Override
	protected void update(float percent) {

		int redValue = red.interpretInteger(sprite);
		if (redValue < MIN_VALUE) {
			redValue = MIN_VALUE;
		} else if (redValue > MAX_VALUE) {
			redValue = MAX_VALUE;
		}
		int greenValue = green.interpretInteger(sprite);
		if (greenValue < MIN_VALUE) {
			greenValue = MIN_VALUE;
		} else if (greenValue > MAX_VALUE) {
			greenValue = MAX_VALUE;
		}
		int blueValue = blue.interpretInteger(sprite);
		if (blueValue < MIN_VALUE) {
			blueValue = MIN_VALUE;
		} else if (blueValue > MAX_VALUE) {
			blueValue = MAX_VALUE;
		}

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

		RobotAlbert.sendRobotAlbertRgbLedEyeMessage(eye, redValue, greenValue, blueValue);
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
