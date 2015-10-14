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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.DroneVideoLook;
import org.catrobat.catroid.stage.StageActivity;

public class DroneStartVideoAction extends TemporalAction {

	private boolean toggleVideoFormat = true;
	private Look videoBackgroundLookFullscreen;
	private Look videoBackgroundLookHorizental;

	@Override
	protected void begin() {
		Log.d(getClass().getSimpleName(), "toggleVideoFormat = " + toggleVideoFormat);
		Sprite backgroundSprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0);

		Log.d(getClass().getSimpleName(), "sprite name: " + backgroundSprite.getName());

		if (videoBackgroundLookFullscreen == null) {
			videoBackgroundLookFullscreen = new DroneVideoLook(backgroundSprite, true);
			StageActivity.stageListener.removeActor(backgroundSprite.look);
		}

		if (videoBackgroundLookHorizental == null) {
			videoBackgroundLookHorizental = new DroneVideoLook(backgroundSprite, false);
		}
		Log.d(getClass().getSimpleName(), "sprite look name: " + backgroundSprite.look.getName());

		//StageActivity.stageListener.removeActor(videoBackgroundLookFullscreen);
		if (toggleVideoFormat) {
			//backgroundSprite.look = videoBackgroundLookHorizental;
			StageActivity.stageListener.addActor(backgroundSprite.look);
			StageActivity.stageListener.removeActor(videoBackgroundLookFullscreen);
			videoBackgroundLookHorizental.setZIndex(0);
			toggleVideoFormat = false;
		} else {
			StageActivity.stageListener.removeActor(videoBackgroundLookHorizental);
			backgroundSprite.look = videoBackgroundLookFullscreen;
			StageActivity.stageListener.addActor(videoBackgroundLookFullscreen);
			videoBackgroundLookFullscreen.setZIndex(0);
			toggleVideoFormat = true;
		}

		super.begin();
	}

	@Override
	protected void update(float percent) {
		//Nothing to do
	}
}
