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

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.ardrone.DroneConfigManager;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class DroneSetAltitudeAction extends TemporalAction {

	private Formula altitude;
	private Formula verticalSpeed;
	private Formula rotationSpeed;
	private Formula tiltAngle;
	private Sprite sprite;

	@Override
	protected void update(float percent) {

		int altitudeValue = updateFormulaValue(altitude);
		int verticalSpeedValue = updateFormulaValue(verticalSpeed);
		int rotationSpeedValue = updateFormulaValue(rotationSpeed);
		int tiltAngleValue = updateFormulaValue(tiltAngle);

		DroneConfigManager.getInstance().setAltitude(altitudeValue);
		DroneConfigManager.getInstance().setVerticalSpeed(verticalSpeedValue);
		DroneConfigManager.getInstance().setRotationSpeed(rotationSpeedValue);
		DroneConfigManager.getInstance().setTiltAngle(tiltAngleValue);
	}

	private int updateFormulaValue(Formula rgbFormula) {
		int value;

		try {
			value = rgbFormula.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			value = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		return value;
	}

	public void setAltitude(Formula altitude) {
		this.altitude = altitude;
	}

	public void setVerticalSpeed(Formula verticalSpeed) {
		this.verticalSpeed = verticalSpeed;
	}

	public void setRotationSpeed(Formula rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public void setTiltAngle(Formula tiltAngle) {
		this.tiltAngle = tiltAngle;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
