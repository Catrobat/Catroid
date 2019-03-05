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

package org.catrobat.catroid.common.defaultprojectcreators;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DroneVideoLookData;
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
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.drone.ardrone.DroneBrickFactory;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.fragment.SpriteFactory;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ArDroneProjectCreator extends ProjectCreator {

	private static SpriteFactory spriteFactory = new SpriteFactory();

	public ArDroneProjectCreator() {
		defaultProjectNameResourceId = R.string.default_drone_project_name;
	}

	@Override
	public Project createDefaultProject(String projectName, Context context, boolean landscapeMode)
			throws IOException, IllegalArgumentException {

		if (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}

		Project defaultDroneProject = new Project(context, projectName, landscapeMode);
		defaultDroneProject.setDeviceData(context);
		XstreamSerializer.getInstance().saveProject(defaultDroneProject);
		ProjectManager.getInstance().setCurrentProject(defaultDroneProject);

		File sceneDir = defaultDroneProject.getDefaultScene().getDirectory();
		File imageDir = new File(sceneDir, IMAGE_DIRECTORY_NAME);

		String backgroundName = context.getString(R.string.add_look_drone_video);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(90f);
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(100.0);

		Script whenProjectStartsScript = new StartScript();
		whenProjectStartsScript.addBrick(turnLeftBrick);
		whenProjectStartsScript.addBrick(setSizeBrick);

		BrickBaseType brick = DroneBrickFactory.getInstanceOfDroneBrick(DroneBrickFactory.DroneBricks.DRONE_SWITCH_CAMERA_BRICK, 0, 0);
		Script whenSpriteTappedScript = new WhenScript();
		whenSpriteTappedScript.addBrick(brick);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.drone_project_background, context);

		File backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.ic_video, imageDir,
				backgroundName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), backgroundName);

		LookData backgroundLookData = new DroneVideoLookData(backgroundName, backgroundFile);
		sprite.getLookList().add(backgroundLookData);

		Sprite backgroundSprite = defaultDroneProject.getDefaultScene().getSpriteList().get(0);

		backgroundSprite.getLookList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);
		backgroundSprite.addScript(whenProjectStartsScript);
		backgroundSprite.addScript(whenSpriteTappedScript);

		String takeOffSpriteName = context.getString(R.string.default_drone_project_sprites_takeoff);

		File takeOffArrowFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_takeoff_2, imageDir, takeOffSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(takeOffSpriteName, DroneBrickFactory.DroneBricks
						.DRONE_TAKE_OFF_LAND_BRICK,
				-280, -200, takeOffArrowFile, 0, 0));

		String upSpriteName = context.getString(R.string.default_drone_project_sprites_up);

		File upFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_arrow_up, imageDir,
				upSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(upSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_UP_BRICK, -25, 335, upFile, 2000, 20));

		//Down Sprite
		String downSpriteName = context.getString(R.string.default_drone_project_sprites_down);

		File downFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_arrow_down, imageDir,
				downSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(downSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_DOWN_BRICK,
				225, 335, downFile, 2000, 20));

		//Forward Sprite
		String forwardSpriteName = context.getString(R.string.default_drone_project_sprites_forward);

		File forwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_go_forward, imageDir,
				forwardSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(forwardSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_FORWARD_BRICK, -25, -335, forwardFile, 2000, 20));

		//Backward Sprite
		String backwardSpriteName = context.getString(R.string.default_drone_project_sprites_back);

		File backwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_go_back, imageDir,
				downSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(backwardSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_BACKWARD_BRICK, 225, -335, backwardFile, 2000, 20));

		//Left Sprite
		String leftSpriteName = context.getString(R.string.default_drone_project_sprites_left);

		File leftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_go_left, imageDir,
				leftSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(leftSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_LEFT_BRICK, 100, -475, leftFile, 2000, 20));

		//Right Sprite
		String rightSpriteName = context.getString(R.string.default_drone_project_sprites_right);

		File rightFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_go_right, imageDir,
				rightSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(rightSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_RIGHT_BRICK, 100, -200, rightFile, 2000, 20));

		//Turn Left Sprite
		String turnLeftSpriteName = context.getString(R.string.default_drone_project_sprites_turn_left);

		File turnLeftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_turn_left, imageDir,
				turnLeftSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(turnLeftSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_TURN_LEFT_BRICK, 100, 200, turnLeftFile, 2000, 20));

		//Turn Right Sprite
		String turnRightSpriteName = context.getString(R.string.default_drone_project_sprites_turn_right);

		File turnRightFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_turn_right, imageDir,
				turnRightSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(turnRightSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_TURN_RIGHT_BRICK, 100, 475, turnRightFile, 2000, 20));

		//Flip Sprite
		String flipSpriteName = context.getString(R.string.default_drone_project_sprites_flip);

		File flipFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_flip, imageDir,
				flipSpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(flipSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_FLIP_BRICK, -280, 200, flipFile, 2000, 20));

		//Emergency Sprite
		String emergencySpriteName = context.getString(R.string.default_drone_project_sprites_emergency);

		File emergencyFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_drone_project_orange_go_emergency, imageDir,
				emergencySpriteName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		defaultDroneProject.getDefaultScene().addSprite(createDroneSprite(emergencySpriteName,
				DroneBrickFactory.DroneBricks.DRONE_GO_EMERGENCY, -280, 0, emergencyFile, 2000, 20));

		XstreamSerializer.getInstance().saveProject(defaultDroneProject);
		return defaultDroneProject;
	}

	private Sprite createDroneSprite(String spriteName, DroneBrickFactory.DroneBricks droneBrick, int xPosition,
			int yPosition, File lookFile, int timeInMilliseconds, int powerInPercent) {

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), spriteName);

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

		LookData lookData = new LookData(spriteName, lookFile);
		sprite.getLookList().add(lookData);

		sprite.addScript(whenSpriteTappedScript);
		sprite.addScript(whenProjectStartsScript);

		return sprite;
	}
}
