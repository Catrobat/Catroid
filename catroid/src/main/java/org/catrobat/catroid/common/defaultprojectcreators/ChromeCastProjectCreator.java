/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.DEFAULT_SOUND_EXTENSION;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.ScreenValues.CAST_SCREEN_HEIGHT;
import static org.catrobat.catroid.common.ScreenValues.CAST_SCREEN_WIDTH;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.OPERATOR;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.SENSOR;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.USER_VARIABLE;

public class ChromeCastProjectCreator extends ProjectCreator {

	public ChromeCastProjectCreator() {
		defaultProjectNameResourceId = R.string.default_cast_project_name;
	}

	@Override
	public Project createDefaultProject(String name, Context context, boolean landscapeMode) throws IOException {
		Project project = new Project(context, name, true, true);

		if (project.getDirectory().exists()) {
			throw new IOException("Cannot create new project at "
					+ project.getDirectory().getAbsolutePath()
					+ ", directory already exists.");
		}

		XstreamSerializer.getInstance().saveProject(project);

		if (!project.getDirectory().isDirectory()) {
			throw new FileNotFoundException("Cannot create project at " + project.getDirectory().getAbsolutePath());
		}

		int backgroundDrawableId = R.drawable.default_project_background_landscape;
		int cloudDrawableId = R.drawable.default_project_clouds_landscape;
		int screenshotDrawableId = R.drawable.default_project_screenshot_landscape;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), backgroundDrawableId, options);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactor(
				options.outWidth,
				options.outHeight,
				CAST_SCREEN_WIDTH,
				CAST_SCREEN_HEIGHT);

		Scene scene = project.getDefaultScene();

		File imageDir = new File(scene.getDirectory(), IMAGE_DIRECTORY_NAME);
		File soundDir = new File(scene.getDirectory(), SOUND_DIRECTORY_NAME);

		String imageFileName = "img" + DEFAULT_IMAGE_EXTENSION;
		String soundFileName = "snd" + DEFAULT_SOUND_EXTENSION;

		File backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				backgroundDrawableId,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		File cloudFile1 = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				cloudDrawableId,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		File cloudFile2 = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				cloudDrawableId,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		File birdWingUpFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_project_bird_wing_up,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		File birdWingDownFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_project_bird_wing_down,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		File birdLeftWingUpFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_project_bird_wing_up_left,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		File birdLeftWingDownFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				R.drawable.default_project_bird_wing_down_left,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		File tweetFile1 = ResourceImporter.createSoundFileFromResourcesInDirectory(context.getResources(),
				R.raw.default_project_tweet_1,
				soundDir,
				soundFileName);

		File tweetFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(context.getResources(),
				R.raw.default_project_tweet_2,
				soundDir,
				soundFileName);

		ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				screenshotDrawableId,
				scene.getDirectory(),
				SCREENSHOT_AUTOMATIC_FILE_NAME,
				1);

		Sprite background = scene.getSpriteList().get(0);
		background.getLookList()
				.add(new LookData(context.getString(R.string.default_cast_project_background_name), backgroundFile));

		Sprite cloud1 = new Sprite(context.getString(R.string.default_cast_project_cloud_sprite_name1));
		Sprite cloud2 = new Sprite(context.getString(R.string.default_cast_project_cloud_sprite_name2));
		Sprite bird = new Sprite(context.getString(R.string.default_cast_project_sprites_bird_name));

		scene.addSprite(cloud1);
		scene.addSprite(cloud2);
		scene.addSprite(bird);

		cloud1.getLookList()
				.add(new LookData(context.getString(R.string.default_cast_project_cloud_name), cloudFile1));
		cloud2.getLookList()
				.add(new LookData(context.getString(R.string.default_cast_project_cloud_name), cloudFile2));
		bird.getLookList()
				.add(new LookData(context.getString(R.string.default_cast_project_sprites_bird_name_wing_up), birdWingUpFile));
		bird.getLookList()
				.add(new LookData(context.getString(R.string.default_cast_project_sprites_bird_name_wing_down), birdWingDownFile));
		bird.getLookList()
				.add(new LookData(context.getString(R.string.default_cast_project_sprites_bird_name_wing_up_left), birdLeftWingUpFile));
		bird.getLookList()
				.add(new LookData(context.getString(R.string.default_cast_project_sprites_bird_name_wing_down_left), birdLeftWingDownFile));
		bird.getSoundList()
				.add(new SoundInfo(context.getString(R.string.default_cast_project_sprites_tweet_1), tweetFile1));
		bird.getSoundList()
				.add(new SoundInfo(context.getString(R.string.default_cast_project_sprites_tweet_2), tweetFile2));

		Script script = new StartScript();

		script.addBrick(new PlaceAtBrick(new Formula(0), new Formula(0)));
		script.addBrick(new GlideToBrick(new Formula(-CAST_SCREEN_WIDTH), new Formula(0), new Formula(5)));
		script.addBrick(new PlaceAtBrick(CAST_SCREEN_WIDTH, 0));

		ForeverBrick loopBrick = new ForeverBrick();
		loopBrick.addBrick(new GlideToBrick(new Formula(-CAST_SCREEN_WIDTH), new Formula(0), new Formula(10)));
		loopBrick.addBrick(new PlaceAtBrick(CAST_SCREEN_WIDTH, 0));

		script.addBrick(loopBrick);
		cloud1.addScript(script);

		script = new StartScript();
		script.addBrick(new PlaceAtBrick(new Formula(CAST_SCREEN_WIDTH), new Formula(0)));

		loopBrick = new ForeverBrick();
		loopBrick.addBrick(new GlideToBrick(new Formula(-CAST_SCREEN_WIDTH), new Formula(0), new Formula(10)));
		loopBrick.addBrick(new PlaceAtBrick(new Formula(CAST_SCREEN_WIDTH), new Formula(0)));

		script.addBrick(loopBrick);
		cloud2.addScript(script);

		script = new StartScript();

		loopBrick = new ForeverBrick();

		FormulaElement minX = new FormulaElement(NUMBER, "-640", null);
		FormulaElement maxX = new FormulaElement(NUMBER, "640", null);
		FormulaElement minY = new FormulaElement(NUMBER, "-360", null);
		FormulaElement maxY = new FormulaElement(NUMBER, "360", null);

		FormulaElement birdX = new FormulaElement(SENSOR, Sensors.OBJECT_X.name(), null);
		FormulaElement birdY = new FormulaElement(SENSOR, Sensors.OBJECT_Y.name(), null);

		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.GAMEPAD_UP_PRESSED.name(), null)));

		IfLogicBeginBrick innerIfBrick = new IfLogicBeginBrick(new Formula(
				new FormulaElement(OPERATOR, Operators.EQUAL.name(), null, maxY, birdY)));
		innerIfBrick.addBrickToIfBranch(new PlaceAtBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.OBJECT_X.name(), null)),
				new Formula(new FormulaElement(NUMBER, "-360", null))));
		ifBrick.addBrickToIfBranch(innerIfBrick);
		ifBrick.addBrickToIfBranch(new ChangeYByNBrick(new Formula(5)));

		script.addBrick(ifBrick);

		ifBrick = new IfLogicBeginBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.GAMEPAD_DOWN_PRESSED.name(), null)));

		innerIfBrick = new IfLogicBeginBrick(new Formula(
				new FormulaElement(OPERATOR, Operators.EQUAL.name(), null, minY, birdY)));
		innerIfBrick.addBrickToIfBranch(new PlaceAtBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.OBJECT_X.name(), null)),
				new Formula(new FormulaElement(NUMBER, "360", null))));
		ifBrick.addBrickToIfBranch(innerIfBrick);
		ifBrick.addBrickToIfBranch(new ChangeYByNBrick(new Formula(-5)));

		script.addBrick(ifBrick);

		UserVariable directionVar = new UserVariable(context.getString(R.string.default_cast_project_var_direction));
		project.addUserVariable(directionVar);

		ifBrick = new IfLogicBeginBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.GAMEPAD_LEFT_PRESSED.name(), null)));

		ifBrick.addBrickToIfBranch(new SetVariableBrick(new Formula(
				new FormulaElement(FUNCTION, Functions.TRUE.name(), null)), directionVar));

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(bird.getLookList().get(2));
		ifBrick.addBrickToIfBranch(setLookBrick);

		innerIfBrick = new IfLogicBeginBrick(new Formula(
				new FormulaElement(OPERATOR, Operators.EQUAL.name(), null, minX, birdX)));
		innerIfBrick.addBrickToIfBranch(new PlaceAtBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.OBJECT_X.name(), null)),
				new Formula(new FormulaElement(NUMBER, "640", null))));
		ifBrick.addBrickToIfBranch(innerIfBrick);
		ifBrick.addBrickToIfBranch(new ChangeYByNBrick(new Formula(-5)));

		script.addBrick(ifBrick);

		script.addBrick(loopBrick);
		bird.addScript(script);

		ifBrick = new IfLogicBeginBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.GAMEPAD_RIGHT_PRESSED.name(), null)));

		ifBrick.addBrickToIfBranch(new SetVariableBrick(new Formula(
				new FormulaElement(FUNCTION, Functions.TRUE.name(), null)), directionVar));

		setLookBrick = new SetLookBrick();
		setLookBrick.setLook(bird.getLookList().get(0));
		ifBrick.addBrickToIfBranch(setLookBrick);

		innerIfBrick = new IfLogicBeginBrick(new Formula(
				new FormulaElement(OPERATOR, Operators.EQUAL.name(), null, maxX, birdX)));
		innerIfBrick.addBrickToIfBranch(new PlaceAtBrick(
				new Formula(new FormulaElement(SENSOR, Sensors.OBJECT_X.name(), null)),
				new Formula(new FormulaElement(NUMBER, "-640", null))));
		ifBrick.addBrickToIfBranch(innerIfBrick);
		ifBrick.addBrickToIfBranch(new ChangeYByNBrick(new Formula(5)));

		script.addBrick(ifBrick);

		script.addBrick(loopBrick);
		bird.addScript(script);

		script = new WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_A));

		ifBrick = new IfLogicBeginBrick(new Formula(
				new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, directionVar.getName(), null)));

		setLookBrick = new SetLookBrick();
		setLookBrick.setLook(bird.getLookList().get(3));
		ifBrick.addBrickToIfBranch(setLookBrick);

		ifBrick.addBrickToIfBranch(new WaitBrick(new Formula(0.1)));

		setLookBrick = new SetLookBrick();
		setLookBrick.setLook(bird.getLookList().get(2));
		ifBrick.addBrickToIfBranch(setLookBrick);

		setLookBrick = new SetLookBrick();
		setLookBrick.setLook(bird.getLookList().get(0));
		ifBrick.addBrickToIfBranch(setLookBrick);

		ifBrick.addBrickToIfBranch(new WaitBrick(new Formula(0.1)));

		setLookBrick = new SetLookBrick();
		setLookBrick.setLook(bird.getLookList().get(1));
		ifBrick.addBrickToIfBranch(setLookBrick);

		script.addBrick(ifBrick);
		bird.addScript(script);

		script = new WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_B));

		ifBrick = new IfLogicBeginBrick(
				new Formula(new FormulaElement(USER_VARIABLE, directionVar.getName(), null)));

		PlaySoundBrick playSoundBrick = new PlaySoundBrick();
		playSoundBrick.setSound(bird.getSoundList().get(0));
		ifBrick.addBrickToIfBranch(playSoundBrick);

		playSoundBrick = new PlaySoundBrick();
		playSoundBrick.setSound(bird.getSoundList().get(1));
		ifBrick.addBrickToElseBranch(playSoundBrick);

		script.addBrick(ifBrick);
		bird.addScript(script);

		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}
}
