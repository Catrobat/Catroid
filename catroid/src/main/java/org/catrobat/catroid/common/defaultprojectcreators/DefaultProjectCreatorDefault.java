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
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;

public class DefaultProjectCreatorDefault extends DefaultProjectCreator {
	private static final String TAG = DefaultProjectCreatorDefault.class.getSimpleName();

	public DefaultProjectCreatorDefault() {
		standardProjectNameID = R.string.default_project_name;
	}

	@Override
	public Project createDefaultProject(String projectName, Context context, boolean landscapeMode)
			throws
			IOException,
			IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}

		String birdLookName = context.getString(R.string.default_project_sprites_bird_name);
		String birdWingUpLookName = context.getString(R.string.default_project_sprites_bird_name_wing_up);
		String birdWingDownLookName = context.getString(R.string.default_project_sprites_bird_name_wing_down);

		String cloudSpriteName1 = context.getString(R.string.default_project_cloud_sprite_name_1);
		String cloudSpriteName2 = context.getString(R.string.default_project_cloud_sprite_name_2);

		String backgroundName = context.getString(R.string.default_project_background_name);
		String cloudName = context.getString(R.string.default_project_cloud_name);

		String tweet1 = context.getString(R.string.default_project_sprites_tweet_1);
		String tweet2 = context.getString(R.string.default_project_sprites_tweet_2);

		Project defaultProject = new Project(context, projectName, landscapeMode);
		String sceneName = defaultProject.getDefaultScene().getName();
		defaultProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		File backgroundFile;
		File cloudFile;

		if (landscapeMode) {
			backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
					R.drawable.default_project_background_landscape, context);
			cloudFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, backgroundName
							+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_project_clouds_landscape,
					context, true,
					backgroundImageScaleFactor);
			backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, backgroundName
							+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_project_background_landscape,
					context, true,
					backgroundImageScaleFactor);
		} else {
			backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
					R.drawable.default_project_background_portrait, context);
			backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, backgroundName
							+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_project_background_portrait,
					context, true,
					backgroundImageScaleFactor);
			cloudFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, backgroundName
							+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_project_clouds_portrait,
					context, true,
					backgroundImageScaleFactor);
		}
		File birdWingUpFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, birdWingUpLookName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_project_bird_wing_up, context, true,
				backgroundImageScaleFactor);
		File birdWingDownFile = UtilFile.copyImageFromResourceIntoProject(projectName, sceneName, birdWingDownLookName
						+ Constants.IMAGE_STANDARD_EXTENSION, R.drawable.default_project_bird_wing_down, context, true,
				backgroundImageScaleFactor);
		try {
			File soundFile1 = UtilFile.copySoundFromResourceIntoProject(projectName, sceneName, tweet1
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_tweet_1, context, true);
			File soundFile2 = UtilFile.copySoundFromResourceIntoProject(projectName, sceneName, tweet2
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_tweet_2, context, true);
			UtilFile.copyFromResourceIntoProject(projectName, sceneName, ".", StageListener
							.SCREENSHOT_AUTOMATIC_FILE_NAME,
					R.drawable.default_project_screenshot, context, false);

			Log.i(TAG, String.format("createAndSaveDefaultProject(%s) %s created%n %s created%n %s created%n %s "
							+ "created%n %s",
					projectName, backgroundFile.getName(), birdWingUpFile.getName(), birdWingDownFile.getName(),
					soundFile1.getName(), soundFile2.getName()));

			LookData backgroundLookData = new LookData();
			backgroundLookData.setLookName(backgroundName);
			backgroundLookData.setLookFilename(backgroundFile.getName());

			Sprite backgroundSprite = defaultProject.getDefaultScene().getSpriteList().get(0);
			backgroundSprite.getLookDataList().add(backgroundLookData);

			LookData birdWingUpLookData = new LookData();
			birdWingUpLookData.setLookName(birdWingUpLookName);
			birdWingUpLookData.setLookFilename(birdWingUpFile.getName());

			LookData birdWingDownLookData = new LookData();
			birdWingDownLookData.setLookName(birdWingDownLookName);
			birdWingDownLookData.setLookFilename(birdWingDownFile.getName());

			LookData cloudLookData = new LookData();
			cloudLookData.setLookName(cloudName);
			cloudLookData.setLookFilename(cloudFile.getName());

			SoundInfo soundInfo1 = new SoundInfo();
			soundInfo1.setTitle(tweet1);
			soundInfo1.setSoundFileName(soundFile1.getName());

			SoundInfo soundInfo2 = new SoundInfo();
			soundInfo2.setTitle(tweet2);
			soundInfo2.setSoundFileName(soundFile2.getName());

			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundInfo1.getChecksum(), soundInfo1.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(birdWingUpLookData.getChecksum(), birdWingUpLookData.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(birdWingDownLookData.getChecksum(), birdWingDownLookData.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(cloudLookData.getChecksum(), cloudLookData.getAbsolutePath());

			Sprite cloudSprite1 = new Sprite(cloudSpriteName1);
			Sprite cloudSprite2 = new Sprite(cloudSpriteName2);
			cloudSprite1.getLookDataList().add(cloudLookData);
			cloudSprite2.getLookDataList().add(cloudLookData.clone());

			Script cloudSpriteScript1 = new StartScript();
			Script cloudSpriteScript2 = new StartScript();

			PlaceAtBrick placeAtBrick1 = new PlaceAtBrick(0, 0);
			PlaceAtBrick placeAtBrick2 = new PlaceAtBrick(ScreenValues.SCREEN_WIDTH, 0);
			PlaceAtBrick placeAtBrick3 = new PlaceAtBrick(ScreenValues.SCREEN_WIDTH, 0);
			PlaceAtBrick placeAtBrick4 = new PlaceAtBrick(ScreenValues.SCREEN_WIDTH, 0);
			PlaceAtBrick placeAtBrick5 = new PlaceAtBrick(ScreenValues.SCREEN_WIDTH, 0);

			cloudSpriteScript1.addBrick(placeAtBrick1);
			cloudSpriteScript2.addBrick(placeAtBrick2);

			GlideToBrick glideToBrick1 = new GlideToBrick(-ScreenValues.SCREEN_WIDTH, 0, 5000);
			cloudSpriteScript1.addBrick(glideToBrick1);

			cloudSpriteScript1.addBrick(placeAtBrick3);

			ForeverBrick foreverBrick = new ForeverBrick();
			cloudSpriteScript1.addBrick(foreverBrick);
			cloudSpriteScript2.addBrick(foreverBrick);

			GlideToBrick glideToBrick2 = new GlideToBrick(-ScreenValues.SCREEN_WIDTH, 0, 10000);

			cloudSpriteScript1.addBrick(glideToBrick2);
			cloudSpriteScript1.addBrick(placeAtBrick4);

			cloudSpriteScript2.addBrick(glideToBrick2);
			cloudSpriteScript2.addBrick(placeAtBrick5);

			LoopEndlessBrick loopEndlessBrick = new LoopEndlessBrick(foreverBrick);
			cloudSpriteScript1.addBrick(loopEndlessBrick);
			cloudSprite1.addScript(cloudSpriteScript1);
			cloudSpriteScript2.addBrick(loopEndlessBrick);
			cloudSprite2.addScript(cloudSpriteScript2);

			defaultProject.getDefaultScene().addSprite(cloudSprite1);
			defaultProject.getDefaultScene().addSprite(cloudSprite2);

			Sprite birdSprite = new Sprite(birdLookName);
			birdSprite.getLookDataList().add(birdWingUpLookData);
			birdSprite.getLookDataList().add(birdWingDownLookData);
			birdSprite.getSoundList().add(soundInfo1);
			birdSprite.getSoundList().add(soundInfo2);
			Script birdStartScript = new StartScript();
			Script birdStartScriptTwo = new StartScript();
			ForeverBrick foreverBrickBird = new ForeverBrick();
			ForeverBrick foreverBrickTwo = new ForeverBrick();
			birdStartScript.addBrick(foreverBrickBird);
			birdStartScriptTwo.addBrick(foreverBrickTwo);

			FormulaElement randomElement = new FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.RAND.toString(), null);
			randomElement.setLeftChild(new FormulaElement(FormulaElement.ElementType.NUMBER, "-300", randomElement));
			randomElement.setRightChild(new FormulaElement(FormulaElement.ElementType.NUMBER, "300", randomElement));
			Formula randomGlide1 = new Formula(randomElement);
			FormulaElement randomElement2 = new FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.RAND.toString(), null);
			randomElement2.setLeftChild(new FormulaElement(FormulaElement.ElementType.NUMBER, "-200", randomElement));
			randomElement2.setRightChild(new FormulaElement(FormulaElement.ElementType.NUMBER, "200", randomElement));
			Formula randomGlide2 = new Formula(randomElement2);
			GlideToBrick glideToBrickBird = new GlideToBrick(randomGlide1, randomGlide2, new Formula(1));
			birdStartScript.addBrick(glideToBrickBird);

			NextLookBrick nextLookBrickBird = new NextLookBrick();
			WaitBrick waitBrick = new WaitBrick(200);
			birdStartScriptTwo.addBrick(nextLookBrickBird);
			birdStartScriptTwo.addBrick(waitBrick);
			LoopEndlessBrick loopEndlessBrickBird = new LoopEndlessBrick(foreverBrickBird);
			LoopEndlessBrick loopEndlessBrickTwo = new LoopEndlessBrick(foreverBrickTwo);
			birdStartScript.addBrick(loopEndlessBrickBird);
			birdStartScriptTwo.addBrick(loopEndlessBrickTwo);
			birdSprite.addScript(birdStartScript);
			birdSprite.addScript(birdStartScriptTwo);
			WhenScript whenScriptBird = new WhenScript();
			PlaySoundBrick playSoundBrickBird = new PlaySoundBrick();
			playSoundBrickBird.setSoundInfo(soundInfo1);
			whenScriptBird.addBrick(playSoundBrickBird);
			birdSprite.addScript(whenScriptBird);
			defaultProject.getDefaultScene().addSprite(birdSprite);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IOException(TAG, illegalArgumentException);
		}

		StorageHandler.getInstance().saveProject(defaultProject);

		return defaultProject;
	}
}
