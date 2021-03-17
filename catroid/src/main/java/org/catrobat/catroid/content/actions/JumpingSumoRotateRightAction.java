/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import com.parrot.arsdk.arcontroller.ARDeviceController;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDeviceController;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class JumpingSumoRotateRightAction extends TemporalAction {

	private Scope scope;
	private Formula degree;
	private float newDegree;
	private float duration;

	protected static final float JUMPING_SUMO_ROTATE_ZERO = 0.0f;

	private ARDeviceController deviceController;
	private JumpingSumoDeviceController controller;
	private static final String TAG = JumpingSumoRotateRightAction.class.getSimpleName();

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setDegree(Formula degree) {
		this.degree = degree;
	}

	@Override
	protected void begin() {
		super.begin();
		controller = JumpingSumoDeviceController.getInstance();
		deviceController = controller.getDeviceController();
		try {
			if (degree == null) {
				newDegree = Float.valueOf(JUMPING_SUMO_ROTATE_ZERO);
			} else {
				newDegree = degree.interpretFloat(scope);
			}
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			newDegree = Float.valueOf(JUMPING_SUMO_ROTATE_ZERO);
		}
		move();
	}

	protected void move() {
		if (deviceController != null) {
			newDegree = (float) (newDegree * Math.PI / 180);
			deviceController.getFeatureJumpingSumo().setPilotingPCMDFlag((byte) 1);
			deviceController.getFeatureJumpingSumo().sendPilotingAddCapOffset(newDegree);
			duration = 1.0f;
			super.setDuration(duration);
			Log.d(TAG, "send move command JS");
		}
	}

	protected void moveEnd() {
		if (deviceController != null) {
			deviceController.getFeatureJumpingSumo().setPilotingPCMDFlag((byte) 0);
			Log.d(TAG, "send stop command JS");
		}
	}

	@Override
	protected void update(float percent) {
		//Nothing to do
	}

	@Override
	protected void end() {
		super.end();
		moveEnd();
	}
}
