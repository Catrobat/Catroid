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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.raspberrypi.RPiSocketConnection;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class RaspiPwmAction extends TemporalAction {

	private Formula pinNumberFormula;
	private Formula pwmFrequencyFormula;
	private Formula pwmPercentageFormula;

	private Sprite sprite;

	private int pinInterpretation;
	private double frequencyInterpretation;
	private double percentageInterpretation;

	@Override
	protected void begin() {
		try {
			pinInterpretation = pinNumberFormula == null ? Integer.valueOf(0) : pinNumberFormula.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			pinInterpretation = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed. (pin)",
					interpretationException);
		}

		try {
			frequencyInterpretation = pwmFrequencyFormula == null ? Double.valueOf(0) : pwmFrequencyFormula
					.interpretDouble(sprite);
		} catch (InterpretationException interpretationException) {
			frequencyInterpretation = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed. (frequency)",
					interpretationException);
		}

		try {
			percentageInterpretation = pwmPercentageFormula == null ? Double.valueOf(0) : pwmPercentageFormula
					.interpretDouble(sprite);
		} catch (InterpretationException interpretationException) {
			percentageInterpretation = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed. (percentage)",
					interpretationException);
		}
	}

	@Override
	protected void update(float percent) {

		RPiSocketConnection connection = RaspberryPiService.getInstance().connection;
		try {
			Log.d(getClass().getSimpleName(), "RPi pwm pin=" + pinInterpretation + ", " + percentageInterpretation
					+ "%, " + frequencyInterpretation + "Hz");
			connection.setPWM(pinInterpretation, frequencyInterpretation, percentageInterpretation);
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "RPi: exception during setPwm: " + e);
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPinNumberFormula(Formula pinNumberFormula) {
		this.pinNumberFormula = pinNumberFormula;
	}

	public void setPwmFrequencyFormula(Formula pwmFrequencyFormula) {
		this.pwmFrequencyFormula = pwmFrequencyFormula;
	}

	public void setPwmPercentageFormula(Formula pwmPercentageFormula) {
		this.pwmPercentageFormula = pwmPercentageFormula;
	}
}
