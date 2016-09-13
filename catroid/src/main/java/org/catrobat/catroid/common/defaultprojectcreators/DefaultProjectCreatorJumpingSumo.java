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
import org.catrobat.catroid.drone.JumpingSumoBrickFactory;
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
		//flip brick
		String flipName = context.getString(R.string.default_jumping_sumo_project_sprites_flip);

		File flipFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, flipName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_flip, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(flipName, JumpingSumoBrickFactory.JumpingSumoBricks
						.JUMPING_SUMO_TURN, 0, (int) (-300 / landscapePortraitFactor), flipFile));

		//forward brick
		String forwardName = context.getString(R.string.default_jumping_sumo_project_sprites_forward);

		File forwardFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, forwardName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_forward, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(forwardName, JumpingSumoBrickFactory.JumpingSumoBricks
						.JUMPING_SUMO_FORWARD, (int) (-300 / landscapePortraitFactor), (int)
				(200 / landscapePortraitFactor), forwardFile, BrickValues
				.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS, (byte) BrickValues
				.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, 0));

		//backward brick
		String backwardName = context.getString(R.string.default_jumping_sumo_project_sprites_backward);

		File backwardFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, backwardName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_backward, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(backwardName, JumpingSumoBrickFactory
						.JumpingSumoBricks.JUMPING_SUMO_BACKWARD, (int) (-300 / landscapePortraitFactor), (int)
				(-200 / landscapePortraitFactor), backwardFile,
				BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS,
						(byte) BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, 0));

		//turn left brick
		String turnLeftName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_left);

		File turnLeftFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, turnLeftName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_left, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(turnLeftName, JumpingSumoBrickFactory
						.JumpingSumoBricks.JUMPING_SUMO_ROTATE_LEFT, (int) (-500 / landscapePortraitFactor), 0,
				turnLeftFile, 0,
						(byte) BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, (float) 90/*Math.PI /
						2*/));

		//turn right brick
		String turnRightName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_right);

		File turnRightFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, turnRightName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_right, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(turnRightName, JumpingSumoBrickFactory.JumpingSumoBricks
						.JUMPING_SUMO_ROTATE_RIGHT, (int) (-100 / landscapePortraitFactor), 0, turnRightFile, 0, (byte)
				BrickValues
						.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, (float) 90/*Math.PI / 2*/));

		//jump long brick
		String jumpLongName = context.getString(R.string.default_jumping_sumo_project_sprites_jump_long);

		File jumpLongFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, jumpLongName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_jump_long, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(jumpLongName, JumpingSumoBrickFactory.JumpingSumoBricks
						.JUMPING_SUMO_JUMP_LONG, (int) (500 / landscapePortraitFactor), 0, jumpLongFile));

		//jump high brick
		String jumpHighName = context.getString(R.string.default_jumping_sumo_project_sprites_jump_high);

		File jumpHighFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, jumpHighName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_jumping_sumo_project_jump_high, context, true,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(jumpHighName, JumpingSumoBrickFactory.JumpingSumoBricks
						.JUMPING_SUMO_JUMP_HIGH, (int) (250 / landscapePortraitFactor), 0, jumpHighFile));

		//animation brick
		String animationName = context.getString(R.string.default_jumping_sumo_project_sprites_animation);

		File animationFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, animationName + Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_project_bird_wing_down, context, true,
                iconImageScaleFactor);

        defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(animationName, JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_ANIMATION,
                (int) (350 / landscapePortraitFactor), 0, animationFile));

		//battery brick
		String batteryName = context.getString(R.string.user_variable_name_battery_status);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoBatterySprite(batteryName,
				JumpingSumoBrickFactory.JumpingSumoBricks
				.JUMPING_SUMO_SHOW_BATTERY_STATUS, 200, 350, 0, (byte) 0, 0));

		StorageHandler.getInstance().saveProject(defaultJumpingSumoProject);
		return defaultJumpingSumoProject;
	}

	private Sprite createJumpingSumoSprite(String spriteName, JumpingSumoBrickFactory.JumpingSumoBricks
			jumpingSumoBrick,	int xPosition, int yPosition, File lookFile) {
		return createJumpingSumoSprite(spriteName, jumpingSumoBrick, xPosition, yPosition, lookFile, 0, (byte) 0, 0);
	}

	private Sprite createJumpingSumoSprite(String spriteName, JumpingSumoBrickFactory.JumpingSumoBricks jumpingSumoBrick, int xPosition,
			int yPosition, File lookFile, int timeInMilliseconds, byte powerInPercent, float degree) {

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), spriteName);

		Script whenSpriteTappedScript = new WhenScript();

		BrickBaseType brick = JumpingSumoBrickFactory.getInstanceOfJumpingSumoBrick(jumpingSumoBrick,
				timeInMilliseconds, powerInPercent, degree, 0, 0);

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

	private Sprite createJumpingSumoBatterySprite(String spriteName, JumpingSumoBrickFactory.JumpingSumoBricks
			jumpingSumoBrick, int xPosition,
			int yPosition, int timeInMilliseconds, byte powerInPercent, float degree) {

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), spriteName);

		BrickBaseType brick = JumpingSumoBrickFactory.getInstanceOfJumpingSumoBrick(jumpingSumoBrick,
				timeInMilliseconds, powerInPercent, degree, xPosition, yPosition);

		Script whenProjectStartsScript = new StartScript();

		whenProjectStartsScript.addBrick(brick);

		sprite.addScript(whenProjectStartsScript);

		return sprite;
	}
}
