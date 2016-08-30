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
import com.parrot.arsdk.arcommands.ARCOMMANDS_JUMPINGSUMO_PILOTING_POSTURE_TYPE_ENUM;
import com.parrot.arsdk.arcontroller.ARDeviceController;

import org.catrobat.catroid.drone.JumpingSumoDeviceController;
import org.catrobat.catroid.drone.JumpingSumoPosition;

public class JumpingSumoTurnAction extends TemporalAction {

	private ARDeviceController deviceController;
	private JumpingSumoDeviceController controller;
	private static final String TAG = JumpingSumoTurnAction.class.getSimpleName();

	@Override
	protected void begin() {
		super.begin();
		controller = JumpingSumoDeviceController.getInstance();
		deviceController = controller.getDeviceController();
		JumpingSumoPosition position = JumpingSumoPosition.getInstance();
		boolean pos = position.getPostion();
		if (deviceController != null) {
			if (pos) {
				deviceController.getFeatureJumpingSumo().sendPilotingPosture(ARCOMMANDS_JUMPINGSUMO_PILOTING_POSTURE_TYPE_ENUM.ARCOMMANDS_JUMPINGSUMO_PILOTING_POSTURE_TYPE_KICKER);
				Log.d(TAG, "send turn command JS down");
				position.setPostion(false);
			} else {
				deviceController.getFeatureJumpingSumo().sendPilotingPosture(ARCOMMANDS_JUMPINGSUMO_PILOTING_POSTURE_TYPE_ENUM.ARCOMMANDS_JUMPINGSUMO_PILOTING_POSTURE_TYPE_JUMPER);
				Log.d(TAG, "send turn command JS up");
				position.setPostion(true);
			}
		} else {
			Log.d(TAG, "error: send turn command JS");
		}
	}
	@Override
	protected void update(float percent) {
		//Nothing to do
	}
}
