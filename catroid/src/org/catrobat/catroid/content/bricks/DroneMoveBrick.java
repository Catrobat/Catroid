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
package org.catrobat.catroid.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public abstract class DroneMoveBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public DroneMoveBrick() {
		addAllowedBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		addAllowedBrickField(BrickField.DRONE_POWER_IN_PERCENT);
	}

	public DroneMoveBrick(int durationInMilliseconds, int powerInPercent) {
		initializeBrickFields(new Formula(durationInMilliseconds / 1000.0), new Formula(powerInPercent));
	}

	public DroneMoveBrick(Formula durationInSeconds, Formula powerInPercent) {
		initializeBrickFields(durationInSeconds, powerInPercent);
	}

	private void initializeBrickFields(Formula durationInSeconds, Formula powerInPercent) {
		addAllowedBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		addAllowedBrickField(BrickField.DRONE_POWER_IN_PERCENT);
		setFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS, durationInSeconds);
		setFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT, powerInPercent);
	}

	public void setPower(Formula powerInPercent) {
		setFormulaWithBrickField(BrickField.DRONE_POWER_IN_PERCENT, powerInPercent);
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		setFormulaWithBrickField(BrickField.DRONE_TIME_TO_FLY_IN_SECONDS, timeToWaitInSeconds);
	}

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}
}
