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
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.Operators;
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
import static org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.OPERATOR;

public class DefaultProjectCreator extends ProjectCreator {

	public DefaultProjectCreator() {
		defaultProjectNameResourceId = R.string.default_project_name;
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

		int backgroundDrawableId;
		int cloudDrawableId;
		int screenshotDrawableId;

		if (landscapeMode) {
			backgroundDrawableId = R.drawable.default_project_background_landscape;
			cloudDrawableId = R.drawable.default_project_clouds_landscape;
			screenshotDrawableId = R.drawable.default_project_screenshot_landscape;
		} else {
			backgroundDrawableId = R.drawable.default_project_background_portrait;
			cloudDrawableId = R.drawable.default_project_clouds_portrait;
			screenshotDrawableId = R.drawable.default_project_screenshot;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), backgroundDrawableId, options);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactor(
				options.outWidth,
				options.outHeight,
				SCREEN_WIDTH,
				SCREEN_HEIGHT);

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
				.add(new LookData(context.getString(R.string.default_project_background_name), backgroundFile));

		Sprite cloud1 = new Sprite(context.getString(R.string.default_project_cloud_sprite_name_1));
		Sprite cloud2 = new Sprite(context.getString(R.string.default_project_cloud_sprite_name_2));
		Sprite bird = new Sprite(context.getString(R.string.default_project_sprites_animal_name));

		scene.addSprite(cloud1);
		scene.addSprite(cloud2);
		scene.addSprite(bird);

		cloud1.getLookList()
				.add(new LookData(context.getString(R.string.default_project_cloud_name), cloudFile1));
		cloud2.getLookList()
				.add(new LookData(context.getString(R.string.default_project_cloud_name), cloudFile2));
		bird.getLookList()
				.add(new LookData(context.getString(R.string.default_project_sprites_animal_wings_up), birdWingUpFile));
		bird.getLookList()
				.add(new LookData(context.getString(R.string.default_project_sprites_animal_wings_down), birdWingDownFile));
		bird.getSoundList()
				.add(new SoundInfo(context.getString(R.string.default_project_sprites_tweet_1), tweetFile1));
		bird.getSoundList()
				.add(new SoundInfo(context.getString(R.string.default_project_sprites_tweet_2), tweetFile2));

		Script script = new StartScript();
		script.addBrick(new PlaceAtBrick(new Formula(0), new Formula(0)));
		script.addBrick(new GlideToBrick(new Formula(-SCREEN_WIDTH), new Formula(0), new Formula(5)));
		script.addBrick(new PlaceAtBrick(new Formula(SCREEN_WIDTH), new Formula(0)));

		ForeverBrick loopBrick = new ForeverBrick();
		loopBrick.addBrick(new GlideToBrick(new Formula(-SCREEN_WIDTH), new Formula(0), new Formula(10)));
		loopBrick.addBrick(new PlaceAtBrick(new Formula(SCREEN_WIDTH), new Formula(0)));

		script.addBrick(loopBrick);
		cloud1.addScript(script);

		script = new StartScript();
		script.addBrick(new PlaceAtBrick(new Formula(SCREEN_WIDTH), new Formula(0)));

		loopBrick = new ForeverBrick();
		loopBrick.addBrick(new GlideToBrick(new Formula(-SCREEN_WIDTH), new Formula(0), new Formula(10)));
		loopBrick.addBrick(new PlaceAtBrick(new Formula(SCREEN_WIDTH), new Formula(0)));

		script.addBrick(loopBrick);
		cloud2.addScript(script);

		script = new StartScript();

		loopBrick = new ForeverBrick();

		FormulaElement randomElement1 = new FormulaElement(
				FUNCTION,
				Functions.RAND.toString(), null);

		FormulaElement randomElement1LeftChild = new FormulaElement(
				OPERATOR,
				Operators.MINUS.toString(),
				randomElement1);

		randomElement1LeftChild.setRightChild(new FormulaElement(
				NUMBER,
				"300",
				randomElement1LeftChild));

		randomElement1.setLeftChild(randomElement1LeftChild);
		randomElement1.setRightChild(new FormulaElement(
				NUMBER,
				"300",
				randomElement1));

		Formula randomGlide1 = new Formula(randomElement1);

		FormulaElement randomElement2 = new FormulaElement(
				FUNCTION,
				Functions.RAND.toString(),
				null);

		FormulaElement randomElement2LeftChild = new FormulaElement(
				OPERATOR,
				Operators.MINUS.toString(),
				randomElement2);

		randomElement2LeftChild.setRightChild(new FormulaElement(
				NUMBER,
				"200",
				randomElement2LeftChild));

		randomElement2.setLeftChild(randomElement2LeftChild);
		randomElement2.setRightChild(new FormulaElement(
				NUMBER,
				"200",
				randomElement2));

		Formula randomGlide2 = new Formula(randomElement2);

		GlideToBrick glideToBrick = new GlideToBrick(randomGlide1, randomGlide2, new Formula(1));
		loopBrick.addBrick(glideToBrick);

		script.addBrick(loopBrick);
		bird.addScript(script);

		script = new StartScript();

		loopBrick = new ForeverBrick();
		loopBrick.addBrick(new NextLookBrick());
		loopBrick.addBrick(new WaitBrick(new Formula(0.2)));

		script.addBrick(loopBrick);
		bird.addScript(script);

		script = new WhenScript();
		PlaySoundBrick playSoundBrick = new PlaySoundBrick();
		playSoundBrick.setSound(bird.getSoundList().get(0));
		script.addBrick(playSoundBrick);
		bird.addScript(script);

		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}
}
