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
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.fragment.SpriteFactory;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.catrobat.catroid.common.BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT;
import static org.catrobat.catroid.common.BrickValues.JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS;
import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH;
import static org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick.Animation;

public class JumpingSumoProjectCreator extends ProjectCreator {

	private static SpriteFactory spriteFactory = new SpriteFactory();

	public JumpingSumoProjectCreator() {
		defaultProjectNameResourceId = R.string.default_jumping_sumo_project_name;
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

		Scene scene = project.getDefaultScene();

		File sceneDir = scene.getDirectory();
		File imageDir = new File(sceneDir, IMAGE_DIRECTORY_NAME);
		String imageFileName = "img" + DEFAULT_IMAGE_EXTENSION;

		double landscapePortraitFactor = 1.63;

		String backgroundName = context.getString(R.string.add_look_jumping_sumo_video);

		SetSizeToBrick setSizeBrick = new SetSizeToBrick(60.0);

		Script whenProjectStartsScript = new StartScript();
		whenProjectStartsScript.addBrick(setSizeBrick);

		Script whenSpriteTappedScript = new WhenScript();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), R.drawable.drone_project_background, options);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactor(
				options.outWidth,
				options.outHeight,
				SCREEN_WIDTH,
				SCREEN_HEIGHT);

		File backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.ic_video,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		Sprite sprite = spriteFactory.newInstance(Sprite.class.getSimpleName(), backgroundName);

		LookData backgroundLookData = new LookData(backgroundName, backgroundFile);
		sprite.getLookList().add(backgroundLookData);

		Sprite backgroundSprite = scene.getSpriteList().get(0);

		backgroundSprite.getLookList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);
		backgroundSprite.addScript(whenProjectStartsScript);
		backgroundSprite.addScript(whenSpriteTappedScript);

		double iconImageScaleFactor = 1.8;

		String forwardName = context.getString(R.string.default_jumping_sumo_project_sprites_forward);
		File forwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_forward,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(forwardName,
				(int) (-350 / landscapePortraitFactor),
				(int) (150 / landscapePortraitFactor),
				forwardFile,
				new JumpingSumoMoveForwardBrick(
						JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS,
						JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT)));

		String backwardName = context.getString(R.string.default_jumping_sumo_project_sprites_backward);
		File backwardFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_backward, imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(backwardName,
				(int) (-350 / landscapePortraitFactor),
				(int) (-150 / landscapePortraitFactor),
				backwardFile,
				new JumpingSumoMoveBackwardBrick(
						JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS,
						JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT)));

		String animationName = context.getString(R.string.default_jumping_sumo_project_sprites_animation);
		File animationFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_animations,
				imageDir,
				imageFileName,
				0.75);

		scene.addSprite(createDroneSprite(animationName,
				0,
				(int) (325 / landscapePortraitFactor),
				animationFile,
				new JumpingSumoAnimationsBrick(Animation.SPIN)));

		String soundName = context.getString(R.string.default_jumping_sumo_project_sprites_sound);
		File soundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_sound,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(soundName,
				(int) (250 / landscapePortraitFactor),
				(int) (325 / landscapePortraitFactor), soundFile, new JumpingSumoSoundBrick()));

		String noSoundName = context.getString(R.string.default_jumping_sumo_project_sprites_no_sound);
		File noSoundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_sound_off,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(noSoundName,
				(int) (375 / landscapePortraitFactor),
				(int) (325 / landscapePortraitFactor),
				noSoundFile,
				new JumpingSumoNoSoundBrick()));

		String jumpLongName = context.getString(R.string.default_jumping_sumo_project_sprites_jump_long);
		File jumpLongFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_jump_long,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(jumpLongName,
				(int) (500 / landscapePortraitFactor),
				(int) (-100 / landscapePortraitFactor),
				jumpLongFile,
				new JumpingSumoJumpLongBrick()));

		String jumpHighName = context.getString(R.string.default_jumping_sumo_project_sprites_jump_high);
		File jumpHighFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_jump_high,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(jumpHighName,
				(int) (500 / landscapePortraitFactor),
				(int) (50 / landscapePortraitFactor), jumpHighFile,
				new JumpingSumoJumpHighBrick()));

		String turnLeftName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_left);
		File turnLeftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_left,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(turnLeftName,
				(int) (-500 / landscapePortraitFactor),
				0,
				turnLeftFile,
				new JumpingSumoRotateLeftBrick(new Formula(90.0f))));

		String turnRightName = context.getString(R.string.default_jumping_sumo_project_sprites_turn_right);
		File turnRightFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_right,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(turnRightName,
				(int) (-200 / landscapePortraitFactor),
				0,
				turnRightFile,
				new JumpingSumoRotateRightBrick(new Formula(90.0f))));

		String flipName = context.getString(R.string.default_jumping_sumo_project_sprites_flip);
		File flipFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_flip,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(flipName,
				(int) (500 / landscapePortraitFactor),
				(int) (-250 / landscapePortraitFactor),
				flipFile,
				new JumpingSumoTurnBrick()));

		String pictureName = context.getString(R.string.default_jumping_sumo_project_sprites_picture);
		File pictureFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_jumping_sumo_project_camera,
				imageDir,
				imageFileName,
				iconImageScaleFactor);

		scene.addSprite(createDroneSprite(pictureName,
				(int) (500 / landscapePortraitFactor),
				(int) (325 / landscapePortraitFactor),
				pictureFile,
				new JumpingSumoTakingPictureBrick()));

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

		script.addBrick(new SetSizeToBrick(new Formula(40.0d)));
		sprite.addScript(script);

		return sprite;
	}
}
