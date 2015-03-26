/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import com.parrot.freeflight.drone.DroneProxy.ARDRONE_LED_ANIMATION;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.DroneVideoLook;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;

import java.util.ArrayList;
import java.util.List;

public class DroneStartVideoAction extends TemporalAction {

	private static final String TAG = DroneStartVideoAction.class.getSimpleName();
	private Sprite sprite;
	private int oldCameraId = 0;
	private Look originalBackgroundLook;

	public void setCameraId (int cameraId)
	{
		if (oldCameraId != cameraId) {
			DroneServiceWrapper.getInstance().getDroneService().switchCamera();
			oldCameraId = cameraId;
		}
	}

	public void setSprite (Sprite sprite)
	{
		this.sprite = sprite;
	}

	@Override
	protected void begin() {


		Sprite bgSprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0);

		if (bgSprite.look instanceof DroneVideoLook )
		{
			StageActivity.stageListener.addActor(originalBackgroundLook);
			StageActivity.stageListener.removeActor(bgSprite.look);
			bgSprite.look = originalBackgroundLook;
		}
		else
		{
			originalBackgroundLook = bgSprite.look;
			Look droneVideoLook = new DroneVideoLook(bgSprite);
			StageActivity.stageListener.removeActor(originalBackgroundLook);
			bgSprite.look = droneVideoLook;
			StageActivity.stageListener.addActor(droneVideoLook);
		}

		super.begin();
		bgSprite.look.setZIndex(0);
	}

	@Override
	protected void update(float percent) {
		//Nothing to do
	}
}
