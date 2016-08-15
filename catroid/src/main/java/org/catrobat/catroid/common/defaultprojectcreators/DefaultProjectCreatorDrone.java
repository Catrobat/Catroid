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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DroneVideoLookData;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.drone.DroneBrickFactory;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;

public class DefaultProjectCreatorDrone extends DefaultProjectCreator {

	public DefaultProjectCreatorDrone() {
		standardProjectNameID = R.string.default_drone_project_name;
	}

	@Override
	public Project createDefaultProject(String projectName, Context context, boolean landscapeMode) throws IOException,
			IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}

		String backgroundName = context.getString(R.string.add_look_drone_video);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(90f);
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(100.0);

		Script whenProjectStartsScript = new StartScript();
		whenProjectStartsScript.addBrick(turnLeftBrick);
		whenProjectStartsScript.addBrick(setSizeBrick);

		BrickBaseType brick = DroneBrickFactory.getInstanceOfDroneBrick(DroneBrickFactory.DroneBricks.DRONE_SWITCH_CAMERA_BRICK, 0, 0);
		Script whenSpriteTappedScript = new WhenScript();
		whenSpriteTappedScript.addBrick(brick);

		Project defaultDroneProject = new Project(context, projectName, landscapeMode);
		defaultDroneProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultDroneProject);
		ProjectManager.getInstance().setProject(defaultDroneProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.drone_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.ic_video, context,
				true, backgroundImageScaleFactor);

		Sprite sprite = new Sprite(backgroundName);

		LookData backgroundLookData = new DroneVideoLookData();
		backgroundLookData.setLookName(context.getString(R.string.add_look_drone_video));
		backgroundLookData.setLookFilename(backgroundFile.getName());
		sprite.getLookDataList().add(backgroundLookData);

		Sprite backgroundSprite = defaultDroneProject.getSpriteList().get(0);

		backgroundSprite.getLookDataList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);
		backgroundSprite.addScript(whenProjectStartsScript);
		backgroundSprite.addScript(whenSpriteTappedScript);

		String takeOffSpriteName = context.getString(R.string.default_drone_project_sprites_takeoff);

		File takeOffArrowFile = UtilFile.copyImageFromResourceIntoProject(projectName, takeOffSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_takeoff, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(takeOffSpriteName, DroneBrickFactory.DroneBricks.DRONE_TAKE_OFF_LAND_BRICK,
				200, -500, takeOffArrowFile));

		String rotateSpriteName = context.getString(R.string.default_drone_project_sprites_rotate);

		File rotateFile = UtilFile.copyImageFromResourceIntoProject(projectName, rotateSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_rotate, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(rotateSpriteName, DroneBrickFactory.DroneBricks.DRONE_FLIP_BRICK,
				-260, -500, rotateFile));

		String upSpriteName = context.getString(R.string.default_drone_project_sprites_up);

		File upFile = UtilFile.copyImageFromResourceIntoProject(projectName, upSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_arrow_up, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(upSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_UP_BRICK, -100,
				500, upFile, 2000));

		//Down Sprite
		String downSpriteName = context.getString(R.string.default_drone_project_sprites_down);

		File downFile = UtilFile.copyImageFromResourceIntoProject(projectName, downSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_arrow_down, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(downSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_DOWN_BRICK,
				-100, -500, downFile, 2000));

		//Forward Sprite
		String forwardSpriteName = context.getString(R.string.default_drone_project_sprites_forward);

		File forwardFile = UtilFile.copyImageFromResourceIntoProject(projectName, forwardSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_go_forward, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(forwardSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_FORWARD_BRICK, 100, 300, forwardFile, 2000));

		//Backward Sprite
		String backwardSpriteName = context.getString(R.string.default_drone_project_sprites_back);

		File backwardFile = UtilFile.copyImageFromResourceIntoProject(projectName, downSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_go_back, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(backwardSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_BACKWARD_BRICK, 100, -300, backwardFile, 2000));

		//Left Sprite
		String leftSpriteName = context.getString(R.string.default_drone_project_sprites_left);

		File leftFile = UtilFile.copyImageFromResourceIntoProject(projectName, leftSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_go_left, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(leftSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_LEFT_BRICK,
				0, -100, leftFile, 2000));

		//Right Sprite
		String rightSpriteName = context.getString(R.string.default_drone_project_sprites_right);

		File rightFile = UtilFile.copyImageFromResourceIntoProject(projectName, rightSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_go_right, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(rightSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_RIGHT_BRICK,
				0, 100, rightFile, 2000));

		//Turn Left Sprite
		String turnLeftSpriteName = context.getString(R.string.default_drone_project_sprites_turn_left);

		File turnLeftFile = UtilFile.copyImageFromResourceIntoProject(projectName, turnLeftSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_turn_left, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(turnLeftSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_TURN_LEFT_BRICK, 200, -100, turnLeftFile, 2000));

		//Turn Right Sprite
		String turnRightSpriteName = context.getString(R.string.default_drone_project_sprites_turn_right);

		File turnRightFile = UtilFile.copyImageFromResourceIntoProject(projectName, turnRightSpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_turn_right, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(turnRightSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_TURN_RIGHT_BRICK, 200, 100, turnRightFile, 2000));

		//Emergency Sprite
		String emergencySpriteName = context.getString(R.string.default_drone_project_sprites_emergency);

		File emergencyFile = UtilFile.copyImageFromResourceIntoProject(projectName, emergencySpriteName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_drone_project_orange_go_emergency, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(emergencySpriteName,
				DroneBrickFactory.DroneBricks.DRONE_GO_EMERGENCY, 200, 500, emergencyFile, 2000));

		StorageHandler.getInstance().saveProject(defaultDroneProject);
		return defaultDroneProject;
	}

	private Sprite createDroneSprite(String spriteName, DroneBrickFactory.DroneBricks droneBrick, int xPosition,
			int yPosition, File lookFile) {
		return createDroneSprite(spriteName, droneBrick, xPosition, yPosition, lookFile, 0, 0);
	}

	private Sprite createDroneSprite(String spriteName, DroneBrickFactory.DroneBricks brickName, int xPosition,
			int yPosition, File lookFile, int timeInMilliseconds) {
		return createDroneSprite(spriteName, brickName, xPosition, yPosition, lookFile, timeInMilliseconds, 20);
	}

	private Sprite createDroneSprite(String spriteName, DroneBrickFactory.DroneBricks droneBrick, int xPosition,
			int yPosition, File lookFile, int timeInMilliseconds, int powerInPercent) {

		Sprite sprite = new Sprite(spriteName);

		Script whenSpriteTappedScript = new WhenScript();

		BrickBaseType brick = DroneBrickFactory.getInstanceOfDroneBrick(droneBrick, timeInMilliseconds, powerInPercent);

		whenSpriteTappedScript.addBrick(brick);

		Script whenProjectStartsScript = new StartScript();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(calculateValueRelativeToScaledBackground(xPosition),
				calculateValueRelativeToScaledBackground(yPosition));
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(50.0);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(90f);

		whenProjectStartsScript.addBrick(placeAtBrick);
		whenProjectStartsScript.addBrick(setSizeBrick);
		whenProjectStartsScript.addBrick(turnLeftBrick);

		LookData lookData = new LookData();
		lookData.setLookName(spriteName + " icon");

		lookData.setLookFilename(lookFile.getName());

		sprite.getLookDataList().add(lookData);

		sprite.addScript(whenSpriteTappedScript);
		sprite.addScript(whenProjectStartsScript);

		return sprite;
	}
}
