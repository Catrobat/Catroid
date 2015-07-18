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

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick.Eye;
import org.catrobat.catroid.devices.arduino.phiro.Phiro;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class PhiroRGBLightAction extends TemporalAction {
	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 255;

	private Eye eyeEnum;
	private Formula red;
	private Formula green;
	private Formula blue;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {

		int redValue = updateFormulaValue(red);
		int greenValue = updateFormulaValue(green);
		int blueValue = updateFormulaValue(blue);

		Phiro phiro = btService.getDevice(BluetoothDevice.PHIRO);

		if (eyeEnum.equals(Eye.LEFT)) {
			phiro.setLeftRGBLightColor(redValue, greenValue, blueValue);
		} else if (eyeEnum.equals(Eye.RIGHT)) {
			phiro.setRightRGBLightColor(redValue, greenValue, blueValue);
		} else if (eyeEnum.equals(Eye.BOTH)) {
			phiro.setLeftRGBLightColor(redValue, greenValue, blueValue);
			phiro.setRightRGBLightColor(redValue, greenValue, blueValue);
		} else {
			Log.d("Phiro", "Error: EyeEnum:" + eyeEnum);
		}
	}

	private int updateFormulaValue(Formula rgbFormula) {

		int rgbValue;

		try {
			rgbValue = rgbFormula.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			rgbValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		if (rgbValue < MIN_VALUE) {
			rgbValue = MIN_VALUE;
		} else if (rgbValue > MAX_VALUE) {
			rgbValue = MAX_VALUE;
		}

		return rgbValue;
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
