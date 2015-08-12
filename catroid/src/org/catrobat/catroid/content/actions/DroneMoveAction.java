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
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public abstract class DroneMoveAction extends TemporalAction {

	private Sprite sprite;
	private Formula duration;
	private Formula powerInPercent;

	protected static final float DRONE_MOVE_SPEED_STOP = 0.0f;

	@Override
	protected void begin() {
		Float newDuration;
		try {
			newDuration = duration == null ? Float.valueOf(DRONE_MOVE_SPEED_STOP) : duration.interpretFloat(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			newDuration = Float.valueOf(DRONE_MOVE_SPEED_STOP);
		}
		super.setDuration(newDuration);
	}

	public void setDelay(Formula delay) {
		this.duration = delay;
	}

	public void setPower(Formula powerInPercent) {
		this.powerInPercent = powerInPercent;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	protected float getPowerNormalized() {
		Float normalizedPower;
		try {
			normalizedPower = duration == null ? Float.valueOf(DRONE_MOVE_SPEED_STOP) : powerInPercent.interpretFloat(sprite) / 100;
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			normalizedPower = Float.valueOf(DRONE_MOVE_SPEED_STOP);
		}
		return normalizedPower;
	}

	protected DroneControlService getDroneService() {
		return DroneServiceWrapper.getInstance().getDroneService();
	}

	protected abstract void move();

	protected abstract void moveEnd();

	@Override
	protected void update(float percent) {
		//Log.d(TAG, "update!");
		this.move();
	}

	// TODO: complete the method
	@Override
	public boolean act(float delta) {
		Boolean superReturn = super.act(delta);
		//Log.d(TAG, "Do Drone Stuff once, superReturn = " + superReturn.toString());
		return superReturn;
	}

	@Override
	protected void end() {
		super.end();
		moveEnd();
	}

	protected void setCommandAndYawEnabled(boolean enable) {
		getDroneService().setProgressiveCommandEnabled(enable);
		getDroneService().setProgressiveCommandCombinedYawEnabled(enable);
	}
}
