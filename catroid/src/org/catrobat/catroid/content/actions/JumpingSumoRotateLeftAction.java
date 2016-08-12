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
import com.parrot.arsdk.arcontroller.ARDeviceController;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateBrick;
import org.catrobat.catroid.drone.JumpingSumoDeviceController;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class JumpingSumoRotateLeftAction extends TemporalAction {

	private Sprite sprite;
	private Formula degree;
	private float newDegree;

	protected static final float JUMPING_SUMO_ROTATE_ZERO = 0.0f;


	private ARDeviceController deviceController;
	private JumpingSumoDeviceController controller;
	private static final String TAG = JumpingSumoRotateLeftAction.class.getSimpleName();
	private JumpingSumoRotateBrick.AngularDimension dimension;



	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setDegree(Formula degree) {
		this.degree = degree;
	}

	public void setAngularDimension(JumpingSumoRotateBrick.AngularDimension dim) {
		dimension = dim;
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
				newDegree = degree.interpretFloat(sprite);
			}
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			newDegree = Float.valueOf(JUMPING_SUMO_ROTATE_ZERO);
		}
	}

	protected void move() {
		if (deviceController != null) {
			if (JumpingSumoRotateBrick.AngularDimension.DEGREE == dimension) {
				newDegree = (float) (newDegree * Math.PI / 180);
			}
			Log.d(TAG, "rotate with " + newDegree);
			Log.d(TAG, "Angel dim: " + dimension);
			deviceController.getFeatureJumpingSumo().sendPilotingAddCapOffset(-newDegree);
			deviceController.getFeatureJumpingSumo().setPilotingPCMDFlag((byte) 1);
			Log.d(TAG, "send move command JS");
		} else {
			Log.d(TAG, "error: send move command JS");
		}
	}

	protected void moveEnd() {
		if (deviceController != null) {
			deviceController.getFeatureJumpingSumo().setPilotingPCMDFlag((byte) 0);
			Log.d(TAG, "send stop command JS");
		} else {
			Log.d(TAG, "error: send stop command JS");
		}
	}

	@Override
	protected void update(float percent) {
		this.move();
	}

	@Override
	protected void end() {
		super.end();
		moveEnd();
	}
}
