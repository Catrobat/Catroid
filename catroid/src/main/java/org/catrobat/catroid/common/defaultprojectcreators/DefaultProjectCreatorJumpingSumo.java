/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.common.defaultprojectcreators;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoBrickFactory;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.fragment.SpriteFactory;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;

public class DefaultProjectCreatorJumpingSumo extends DefaultProjectCreator {

	private static SpriteFactory spriteFactory = new SpriteFactory();

	private static final String TAG = DefaultProjectCreatorJumpingSumo.class.getSimpleName();
	protected static double iconImageScaleFactor = 1.8;
	public DefaultProjectCreatorJumpingSumo() {
		standardProjectNameID = R.string.default_jumping_sumo_project_name;
	}

	@Override
	public Project createDefaultProject(String projectName, Context context, boolean landscapeMode) throws IOException,
			IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}

		double landscapePortraitFactor = 1.63;
		//landscapePortraitFactor = ScreenValues.getAspectRatio();

		landscapeMode = true;
		Log.d(TAG, "create default project");
		String backgroundName = context.getString(R.string.add_look_drone_video);

		SetSizeToBrick setSizeBrick = new SetSizeToBrick(60.0);

		Script whenProjectStartsScript = new StartScript();
		whenProjectStartsScript.addBrick(setSizeBrick);

		Script whenSpriteTappedScript = new WhenScript();
		Project defaultJumpingSumoProject = new Project(context, projectName, landscapeMode);
		String sceneName = defaultJumpingSumoProject.getDefaultScene().getName();
		defaultJumpingSumoProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultJumpingSumoProject);
		ProjectManager.getInstance().setProject(defaultJumpingSumoProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.drone_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.ic_video, context,
				true, backgroundImageScaleFactor);

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), backgroundName);

		//LookData backgroundLookData = new DroneVideoLookData();
		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(context.getString(R.string.add_look_jumping_sumo_video));
		backgroundLookData.setLookFilename(backgroundFile.getName());
		sprite.getLookDataList().add(backgroundLookData);

		Sprite backgroundSprite = defaultJumpingSumoProject.getDefaultScene().getSpriteList().get(0);

		backgroundSprite.getLookDataList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);
		backgroundSprite.addScript(whenProjectStartsScript);
		backgroundSprite.addScript(whenSpriteTappedScript);

		//icons from http://findicons.com/search/arrow#ajax
		//turn left brick
		String turnLeftName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_left);

		File turnLeftFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, turnLeftName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_left, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(turnLeftName, JumpingSumoBrickFactory
						.JumpingSumoBricks.JUMPING_SUMO_ROTATE_LEFT, (int) (-500 / landscapePortraitFactor), 0,
				turnLeftFile, (float) 90/*Math.PI /
						2*/));

		//turn right brick
		String turnRightName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_right);

		File turnRightFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, turnRightName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_right, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(turnRightName, JumpingSumoBrickFactory.JumpingSumoBricks
						.JUMPING_SUMO_ROTATE_RIGHT, (int) (-200 / landscapePortraitFactor), 0, turnRightFile, (float) 90/*Math.PI / 2*/));

		return defaultJumpingSumoProject;
	}

	private Sprite createJumpingSumoSprite(String spriteName, JumpingSumoBrickFactory.JumpingSumoBricks jumpingSumoBrick, int xPosition,
			int yPosition, File lookFile, float degree) {

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), spriteName);

		Script whenSpriteTappedScript = new WhenScript();

		BrickBaseType brick = JumpingSumoBrickFactory.getInstanceOfJumpingSumoBrick(jumpingSumoBrick, degree);

		whenSpriteTappedScript.addBrick(brick);

		Script whenProjectStartsScript = new StartScript();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(calculateValueRelativeToScaledBackground(xPosition),
				calculateValueRelativeToScaledBackground(yPosition));
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(40.0);

		whenProjectStartsScript.addBrick(placeAtBrick);
		whenProjectStartsScript.addBrick(setSizeBrick);

		LookData lookData = new LookData();
		lookData.setLookName(spriteName + " icon");

		lookData.setLookFilename(lookFile.getName());

		sprite.getLookDataList().add(lookData);

		sprite.addScript(whenSpriteTappedScript);
		sprite.addScript(whenProjectStartsScript);

		return sprite;
	}
}
