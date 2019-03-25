/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.fragment.SpriteFactory;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class JumpingSumoProjectCreator extends ProjectCreator {

	private static SpriteFactory spriteFactory = new SpriteFactory();

	public JumpingSumoProjectCreator() {
		defaultProjectNameResourceId = R.string.default_jumping_sumo_project_name;
	}

	@Override
	public Project createDefaultProject(String projectName, Context context, boolean landscapeMode) throws IOException,
			IllegalArgumentException {

		if (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}

		Project defaultJumpingSumoProject = new Project(context, projectName, landscapeMode);
		defaultJumpingSumoProject.setDeviceData(context);
		XstreamSerializer.getInstance().saveProject(defaultJumpingSumoProject);
		ProjectManager.getInstance().setCurrentProject(defaultJumpingSumoProject);

		File sceneDir = defaultJumpingSumoProject.getDefaultScene().getDirectory();
		File imageDir = new File(sceneDir, IMAGE_DIRECTORY_NAME);

		double landscapePortraitFactor = 1.63;

		String backgroundName = context.getString(R.string.add_look_jumping_sumo_video);

		SetSizeToBrick setSizeBrick = new SetSizeToBrick(60.0);

		Script whenProjectStartsScript = new StartScript();
		whenProjectStartsScript.addBrick(setSizeBrick);

		Script whenSpriteTappedScript = new WhenScript();

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.drone_project_background, context);

		File backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.ic_video, imageDir,
				backgroundName + Constants.DEFAULT_IMAGE_EXTENSION,
				backgroundImageScaleFactor);

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), backgroundName);

		LookData backgroundLookData = new LookData(backgroundName, backgroundFile);
		sprite.getLookList().add(backgroundLookData);

		Sprite backgroundSprite = defaultJumpingSumoProject.getDefaultScene().getSpriteList().get(0);

		backgroundSprite.getLookList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);
		backgroundSprite.addScript(whenProjectStartsScript);
		backgroundSprite.addScript(whenSpriteTappedScript);

		String forwardName = context.getString(R.string.default_jumping_sumo_project_sprites_forward);

		double iconImageScaleFactor = 1.8;
		File forwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_forward, imageDir,
				forwardName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(forwardName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_FORWARD, (int) (-350 / landscapePortraitFactor),
				(int) (150 / landscapePortraitFactor), forwardFile,
				BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS,
				(byte) BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, (byte) 0, 0));

		String backwardName = context.getString(R.string.default_jumping_sumo_project_sprites_backward);

		File backwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_backward, imageDir,
				backwardName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(backwardName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_BACKWARD,
				(int) (-350 / landscapePortraitFactor), (int) (-150 / landscapePortraitFactor), backwardFile,
				BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS,
				(byte) BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, (byte) 0, 0));

		String animationName = context.getString(R.string.default_jumping_sumo_project_sprites_animation);

		File animationFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_animations, imageDir,
				animationName + Constants.DEFAULT_IMAGE_EXTENSION,
				0.75);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(animationName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_ANIMATIONS, 0,
				(int) (325 / landscapePortraitFactor), animationFile, 0, (byte) 0, (byte) 0, 0));

		String soundName = context.getString(R.string.default_jumping_sumo_project_sprites_sound);

		File soundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_sound, imageDir,
				soundName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(soundName, JumpingSumoBrickFactory.JumpingSumoBricks
				.JUMPING_SUMO_SOUND, (int) (250 / landscapePortraitFactor), (int)
				(325 / landscapePortraitFactor), soundFile, 0, (byte) 0, (byte) BrickValues
				.JUMPING_SUMO_SOUND_BRICK_DEFAULT_VOLUME_PERCENT, 0));

		String noSoundName = context.getString(R.string.default_jumping_sumo_project_sprites_no_sound);

		File noSoundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_sound_off, imageDir,
				noSoundName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(noSoundName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_NO_SOUND,
				(int) (375 / landscapePortraitFactor), (int) (325 / landscapePortraitFactor), noSoundFile,
				0, (byte) 0, (byte) 0, 0));

		String jumpLongName = context.getString(R.string.default_jumping_sumo_project_sprites_jump_long);

		File jumpLongFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_jump_long, imageDir,
				jumpLongName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(jumpLongName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_JUMP_LONG,
				(int) (500 / landscapePortraitFactor), (int) (-100 / landscapePortraitFactor), jumpLongFile,
				0, (byte) 0, (byte) 0, 0));

		String jumpHighName = context.getString(R.string.default_jumping_sumo_project_sprites_jump_high);

		File jumpHighFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_jump_high, imageDir,
				jumpHighName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(jumpHighName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_JUMP_HIGH, (int) (500 / landscapePortraitFactor),
				(int) (50 / landscapePortraitFactor), jumpHighFile, 0, (byte) 0, (byte) 0, 0));

		String turnLeftName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_left);

		File turnLeftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_left, imageDir,
				turnLeftName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(turnLeftName, JumpingSumoBrickFactory
						.JumpingSumoBricks.JUMPING_SUMO_ROTATE_LEFT, (int) (-500 / landscapePortraitFactor), 0,
				turnLeftFile, 0, (byte) BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, (byte) 0,
				(float) 90/*Math.PI / 2*/));

		String turnRightName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_right);

		File turnRightFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_right, imageDir,
				turnRightName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(turnRightName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_ROTATE_RIGHT,
				(int) (-200 / landscapePortraitFactor), 0, turnRightFile, 0, (byte)
						BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT, (byte) 0, (float) 90));

		String flipName = context.getString(R.string.default_jumping_sumo_project_sprites_flip);

		File flipFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_flip, imageDir,
				flipName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(flipName,
				JumpingSumoBrickFactory.JumpingSumoBricks.JUMPING_SUMO_TURN, (int) (500 / landscapePortraitFactor),
				(int) (-250 / landscapePortraitFactor), flipFile, 0, (byte) 0, (byte) 0, 0));

		String pictureName = context.getString(R.string.default_jumping_sumo_project_sprites_picture);

		File pictureFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), R.drawable.default_jumping_sumo_project_camera, imageDir,
				pictureName + Constants.DEFAULT_IMAGE_EXTENSION,
				iconImageScaleFactor);

		defaultJumpingSumoProject.getDefaultScene().addSprite(createJumpingSumoSprite(pictureName, JumpingSumoBrickFactory.JumpingSumoBricks
				.JUMPING_SUMO_PICTURE, (int) (500 / landscapePortraitFactor), (int)
				(325 / landscapePortraitFactor), pictureFile, 0, (byte) 0, (byte) 0, 0));

		XstreamSerializer.getInstance().saveProject(defaultJumpingSumoProject);
		return defaultJumpingSumoProject;
	}

	private Sprite createJumpingSumoSprite(String spriteName, JumpingSumoBrickFactory.JumpingSumoBricks jumpingSumoBrick, int xPosition,
			int yPosition, File lookFile, int timeInMilliseconds, byte powerInPercent, byte volumeInPercent, float
			degree) {

		Sprite sprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), spriteName);

		Script whenSpriteTappedScript = new WhenScript();

		BrickBaseType brick = JumpingSumoBrickFactory.getInstanceOfJumpingSumoBrick(jumpingSumoBrick,
				timeInMilliseconds, powerInPercent, volumeInPercent, degree);

		whenSpriteTappedScript.addBrick(brick);

		Script whenProjectStartsScript = new StartScript();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(calculateValueRelativeToScaledBackground(xPosition),
				calculateValueRelativeToScaledBackground(yPosition));
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(40.0);

		whenProjectStartsScript.addBrick(placeAtBrick);
		whenProjectStartsScript.addBrick(setSizeBrick);

		LookData lookData = new LookData(spriteName, lookFile);
		sprite.getLookList().add(lookData);

		sprite.addScript(whenSpriteTappedScript);
		sprite.addScript(whenProjectStartsScript);

		return sprite;
	}
}
