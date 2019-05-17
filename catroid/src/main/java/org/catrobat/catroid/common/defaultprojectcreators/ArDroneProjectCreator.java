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
import android.graphics.BitmapFactory;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH;

public class ArDroneProjectCreator extends ProjectCreator {

	public ArDroneProjectCreator() {
		defaultProjectNameResourceId = R.string.default_drone_project_name;
	}

	@Override
	public Project createDefaultProject(String name, Context context, boolean landscapeMode) throws IOException {
		Project project = new Project(context, name, landscapeMode);

		if (project.getDirectory().exists()) {
			throw new IOException("Cannot create new project at "
					+ project.getDirectory().getAbsolutePath()
					+ ", directory already exists.");
		}

		XstreamSerializer.getInstance().saveProject(project);

		if (!project.getDirectory().isDirectory()) {
			throw new FileNotFoundException("Cannot create project at " + project.getDirectory().getAbsolutePath());
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), R.drawable.drone_project_background, options);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactor(
				options.outWidth,
				options.outHeight,
				SCREEN_WIDTH,
				SCREEN_HEIGHT);

		Scene scene = project.getDefaultScene();

		File imageDir = new File(scene.getDirectory(), IMAGE_DIRECTORY_NAME);
		String imageFileName = "img" + DEFAULT_IMAGE_EXTENSION;

		File backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.ic_video,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		Sprite background = scene.getSpriteList().get(0);
		background.getLookList()
				.add(new LookData(context.getString(R.string.add_look_drone_video), backgroundFile));

		Script script = new StartScript();
		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(background.getLookList().get(0));
		background.addScript(script);

		script = new StartScript();
		script.addBrick(new TurnLeftBrick(new Formula(90d)));
		script.addBrick(new SetSizeToBrick(new Formula(100.0d)));
		background.addScript(script);

		script = new WhenScript();
		script.addBrick(new DroneSwitchCameraBrick());
		background.addScript(script);

		String spriteName = context.getString(R.string.default_drone_project_sprites_takeoff);
		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_takeoff_2,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(spriteName,
				-280, -200, imageFile, new DroneTakeOffLandBrick()));

		String upSpriteName = context.getString(R.string.default_drone_project_sprites_up);
		File upFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_arrow_up,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(upSpriteName,
				-25, 335, upFile, new DroneMoveUpBrick(2000, 20)));

		String downSpriteName = context.getString(R.string.default_drone_project_sprites_down);
		File downFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_arrow_down,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(downSpriteName,
				225, 335, downFile, new DroneMoveDownBrick(2000, 20)));

		String forwardSpriteName = context.getString(R.string.default_drone_project_sprites_forward);
		File forwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_go_forward,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(forwardSpriteName,
				-25, -335, forwardFile, new DroneMoveForwardBrick(2000, 20)));

		String backwardSpriteName = context.getString(R.string.default_drone_project_sprites_back);
		File backwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_go_back,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(backwardSpriteName,
				225, -335, backwardFile, new DroneMoveBackwardBrick(2000, 20)));

		String leftSpriteName = context.getString(R.string.default_drone_project_sprites_left);
		File leftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_go_left,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(leftSpriteName,
				100, -475, leftFile, new DroneMoveLeftBrick(2000, 20)));

		String rightSpriteName = context.getString(R.string.default_drone_project_sprites_right);
		File rightFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_go_right,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(rightSpriteName,
				100, -200, rightFile, new DroneMoveRightBrick(2000, 20)));

		String turnLeftSpriteName = context.getString(R.string.default_drone_project_sprites_turn_left);
		File turnLeftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_turn_left,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(turnLeftSpriteName,
				100, 200, turnLeftFile, new DroneTurnLeftBrick(2000, 20)));

		String turnRightSpriteName = context.getString(R.string.default_drone_project_sprites_turn_right);
		File turnRightFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_turn_right,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(turnRightSpriteName,
				100, 475, turnRightFile, new DroneTurnRightBrick(2000, 20)));

		String flipSpriteName = context.getString(R.string.default_drone_project_sprites_flip);
		File flipFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_flip,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(flipSpriteName,
				-280, 200, flipFile, new DroneFlipBrick()));

		String emergencySpriteName = context.getString(R.string.default_drone_project_sprites_emergency);
		File emergencyFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_drone_project_orange_go_emergency,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		scene.addSprite(createDroneSprite(emergencySpriteName,
				-280, 0, emergencyFile, new DroneEmergencyBrick()));

		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}

	private Sprite createDroneSprite(String spriteName, int xPosition, int yPosition, File lookFile, Brick droneBrick) {
		Sprite sprite = new Sprite(spriteName);
		sprite.getLookList()
				.add(new LookData(spriteName, lookFile));

		Script script = new WhenScript();
		script.addBrick(droneBrick);
		sprite.addScript(script);

		script = new StartScript();
		script.addBrick(new PlaceAtBrick(
				calculateValueRelativeToScaledBackground(xPosition),
				calculateValueRelativeToScaledBackground(yPosition)));

		script.addBrick(new SetSizeToBrick(new Formula(50.0d)));
		script.addBrick(new TurnLeftBrick(new Formula(90d)));
		sprite.addScript(script);

		return sprite;
	}
}
