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
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
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
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.fragment.SpriteFactory;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.PathBuilder;

import java.io.File;
import java.io.IOException;

public class ChromeCastProjectCreator extends ProjectCreator {

	private static final String TAG = ChromeCastProjectCreator.class.getSimpleName();

	private static SpriteFactory spriteFactory = new SpriteFactory();

	public ChromeCastProjectCreator() {
		defaultProjectNameResourceId = R.string.default_cast_project_name;
	}

	@Override
	public Project createDefaultProject(String projectName, Context context, boolean landscapeMode) throws
			IOException,
			IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}

		Project defaultProject = new Project(context, projectName, false, true);
		defaultProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		String birdLookName = context.getString(R.string.default_cast_project_sprites_bird_name);
		String birdWingUpLookName = context.getString(R.string.default_cast_project_sprites_bird_name_wing_up);
		String birdWingDownLookName = context.getString(R.string.default_cast_project_sprites_bird_name_wing_down);
		String birdWingUpLeftLookName = context.getString(R.string.default_cast_project_sprites_bird_name_wing_up_left);
		String birdWingDownLeftLookName = context.getString(R.string.default_cast_project_sprites_bird_name_wing_down_left);

		String cloudSpriteName1 = context.getString(R.string.default_cast_project_cloud_sprite_name1);
		String cloudSpriteName2 = context.getString(R.string.default_cast_project_cloud_sprite_name2);

		String backgroundName = context.getString(R.string.default_cast_project_background_name);
		String cloudName = context.getString(R.string.default_cast_project_cloud_name);

		String tweet1 = context.getString(R.string.default_cast_project_sprites_tweet_1);
		String tweet2 = context.getString(R.string.default_cast_project_sprites_tweet_2);

		String varDirection = context.getString(R.string.default_cast_project_var_direction);

		File backgroundFile;
		File cloudFile;

		File projectDir = new File(PathBuilder.buildProjectPath(defaultProject.getName()));
		File imgDir = new File(defaultProject.getDefaultScene().getDirectory(), Constants.IMAGE_DIRECTORY_NAME);
		File sndDir = new File(defaultProject.getDefaultScene().getDirectory(), Constants.SOUND_DIRECTORY_NAME);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background_landscape, context);
		cloudFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), imgDir,
				backgroundName + Constants.DEFAULT_IMAGE_EXTENSION,
				R.drawable.default_project_clouds_landscape,
				backgroundImageScaleFactor);

		backgroundFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), imgDir,
				backgroundName + Constants.DEFAULT_IMAGE_EXTENSION,
				R.drawable.default_project_background_landscape,
				backgroundImageScaleFactor);

		File birdWingUpFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), imgDir,
				birdWingUpLookName
						+ Constants.DEFAULT_IMAGE_EXTENSION,
				R.drawable.default_project_bird_wing_up,
				backgroundImageScaleFactor);

		File birdWingDownFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), imgDir,
				birdWingDownLookName + Constants.DEFAULT_IMAGE_EXTENSION,
				R.drawable.default_project_bird_wing_down,
				backgroundImageScaleFactor);

		File birdWingUpLeftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), imgDir,
				birdWingDownLookName + Constants.DEFAULT_IMAGE_EXTENSION,
				R.drawable.default_project_bird_wing_up_left,
				backgroundImageScaleFactor);
		File birdWingDownLeftFile = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), imgDir,
				birdWingDownLookName + Constants.DEFAULT_IMAGE_EXTENSION,
				R.drawable.default_project_bird_wing_down_left,
				backgroundImageScaleFactor);

		try {
			File soundFile1 = ResourceImporter.createSoundFileFromResourcesInDirectory(context.getResources(), sndDir,
					tweet1 + SoundRecorder.RECORDING_EXTENSION,
					R.raw.default_project_tweet_1);

			File soundFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(context.getResources(), sndDir,
					tweet2 + SoundRecorder.RECORDING_EXTENSION,
					R.raw.default_project_tweet_2);

			ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), projectDir,
					StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME,
					R.drawable.default_project_screenshot, 1);

			LookData backgroundLookData = new LookData();
			backgroundLookData.setName(backgroundName);
			backgroundLookData.setFileName(backgroundFile.getName());

			Sprite backgroundSprite = defaultProject.getDefaultScene().getSpriteList().get(0);
			backgroundSprite.getLookList().add(backgroundLookData);

			LookData birdWingUpLookData = new LookData();
			birdWingUpLookData.setName(birdWingUpLookName);
			birdWingUpLookData.setFileName(birdWingUpFile.getName());

			LookData birdWingDownLookData = new LookData();
			birdWingDownLookData.setName(birdWingDownLookName);
			birdWingDownLookData.setFileName(birdWingDownFile.getName());

			LookData birdWingUpLeftLookData = new LookData();
			birdWingUpLeftLookData.setName(birdWingUpLeftLookName);
			birdWingUpLeftLookData.setFileName(birdWingUpLeftFile.getName());

			LookData birdWingDownLeftLookData = new LookData();
			birdWingDownLeftLookData.setName(birdWingDownLeftLookName);
			birdWingDownLeftLookData.setFileName(birdWingDownLeftFile.getName());

			LookData cloudLookData = new LookData();
			cloudLookData.setName(cloudName);
			cloudLookData.setFileName(cloudFile.getName());

			SoundInfo soundInfo1 = new SoundInfo();
			soundInfo1.setName(tweet1);
			soundInfo1.setFile(soundFile1);
			SoundInfo soundInfo2 = new SoundInfo();
			soundInfo2.setName(tweet2);
			soundInfo2.setFile(soundFile2);

			DataContainer userVariables = defaultProject.getDefaultScene().getDataContainer();

			userVariables.addProjectUserVariable(varDirection);
			UserVariable direction = userVariables.getUserVariable(backgroundSprite, varDirection);

			//Clouds
			Sprite cloudSprite1 = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), cloudSpriteName1);
			Sprite cloudSprite2 = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), cloudSpriteName2);

			cloudSprite1.getLookList().add(cloudLookData);
			cloudSprite2.getLookList().add(cloudLookData);

			Script cloudSpriteScript1 = new StartScript();
			Script cloudSpriteScript2 = new StartScript();

			PlaceAtBrick placeAtBrick1 = new PlaceAtBrick(0, 0);
			PlaceAtBrick placeAtBrick2 = new PlaceAtBrick(ScreenValues.CAST_SCREEN_WIDTH, 0);

			cloudSpriteScript1.addBrick(placeAtBrick1);
			cloudSpriteScript2.addBrick(placeAtBrick2);

			GlideToBrick glideToBrick1 = new GlideToBrick(-ScreenValues.CAST_SCREEN_WIDTH, 0, 5000);
			cloudSpriteScript1.addBrick(glideToBrick1);

			cloudSpriteScript1.addBrick(placeAtBrick2);

			ForeverBrick foreverBrick = new ForeverBrick();
			cloudSpriteScript1.addBrick(foreverBrick);
			cloudSpriteScript2.addBrick(foreverBrick);

			GlideToBrick glideToBrick2 = new GlideToBrick(-ScreenValues.CAST_SCREEN_WIDTH, 0, 10000);

			cloudSpriteScript1.addBrick(glideToBrick2);
			cloudSpriteScript1.addBrick(placeAtBrick2);

			cloudSpriteScript2.addBrick(glideToBrick2);
			cloudSpriteScript2.addBrick(placeAtBrick2);

			LoopEndlessBrick loopEndlessBrick = new LoopEndlessBrick(foreverBrick);
			cloudSpriteScript1.addBrick(loopEndlessBrick);

			cloudSprite1.addScript(cloudSpriteScript1);
			cloudSpriteScript2.addBrick(loopEndlessBrick);
			cloudSprite2.addScript(cloudSpriteScript2);

			defaultProject.getDefaultScene().addSprite(cloudSprite1);
			defaultProject.getDefaultScene().addSprite(cloudSprite2);
			///Clouds

			//Bird
			Sprite birdSprite = spriteFactory.newInstance(SingleSprite.class.getSimpleName(), birdLookName);

			birdSprite.getLookList().add(birdWingUpLookData);
			birdSprite.getLookList().add(birdWingDownLookData);
			birdSprite.getLookList().add(birdWingUpLeftLookData);
			birdSprite.getLookList().add(birdWingDownLeftLookData);
			birdSprite.getSoundList().add(soundInfo1);
			birdSprite.getSoundList().add(soundInfo2);

			FormulaElement minX = new FormulaElement(FormulaElement.ElementType.NUMBER, "-640", null);
			FormulaElement maxX = new FormulaElement(FormulaElement.ElementType.NUMBER, "640", null);
			FormulaElement minY = new FormulaElement(FormulaElement.ElementType.NUMBER, "-360", null);
			FormulaElement maxY = new FormulaElement(FormulaElement.ElementType.NUMBER, "360", null);

			FormulaElement birdX = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_X.name(), null);
			FormulaElement birdY = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_Y.name(), null);

			Script birdScriptBroadcast = new StartScript();

			ForeverBrick foreverBrickBroadcast = new ForeverBrick();
			birdScriptBroadcast.addBrick(foreverBrickBroadcast);
			//Up
			IfLogicBeginBrick ifLogicBeginBrickUp = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.GAMEPAD_UP_PRESSED.name(), null)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickUp);
			IfLogicBeginBrick ifLogicBeginBrickMaxY = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.EQUAL.name(), null, maxY, birdY)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickMaxY);
			PlaceAtBrick placeTop = new PlaceAtBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_X.name(), null)), new Formula(
					new FormulaElement(FormulaElement.ElementType.NUMBER, "-360", null)));
			birdScriptBroadcast.addBrick(placeTop);
			IfLogicElseBrick ifLogicElseBrickMaxY = new IfLogicElseBrick(ifLogicBeginBrickMaxY);
			birdScriptBroadcast.addBrick(ifLogicElseBrickMaxY);
			IfLogicEndBrick ifLogicEndBrickMaxY = new IfLogicEndBrick(ifLogicElseBrickMaxY, ifLogicBeginBrickMaxY);
			birdScriptBroadcast.addBrick(ifLogicEndBrickMaxY);
			ChangeYByNBrick changeYByNBrickUp = new ChangeYByNBrick(5);
			birdScriptBroadcast.addBrick(changeYByNBrickUp);
			IfLogicElseBrick ifLogicElseBrickUp = new IfLogicElseBrick(ifLogicBeginBrickUp);
			birdScriptBroadcast.addBrick(ifLogicElseBrickUp);
			IfLogicEndBrick ifLogicEndBrickUp = new IfLogicEndBrick(ifLogicElseBrickUp, ifLogicBeginBrickUp);
			birdScriptBroadcast.addBrick(ifLogicEndBrickUp);
			//Down
			IfLogicBeginBrick ifLogicBeginBrickDown = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.GAMEPAD_DOWN_PRESSED.name(), null)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickDown);
			IfLogicBeginBrick ifLogicBeginBrickMinY = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.EQUAL.name(), null, minY, birdY)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickMinY);
			PlaceAtBrick placeBottom = new PlaceAtBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_X.name(), null)), new Formula(
					new FormulaElement(FormulaElement.ElementType.NUMBER, "360", null)));
			birdScriptBroadcast.addBrick(placeBottom);
			IfLogicElseBrick ifLogicElseBrickMinY = new IfLogicElseBrick(ifLogicBeginBrickMinY);
			birdScriptBroadcast.addBrick(ifLogicElseBrickMinY);
			IfLogicEndBrick ifLogicEndBrickMinY = new IfLogicEndBrick(ifLogicElseBrickMinY, ifLogicBeginBrickMinY);
			birdScriptBroadcast.addBrick(ifLogicEndBrickMinY);
			ChangeYByNBrick changeYByNBrickDown = new ChangeYByNBrick(-5);
			birdScriptBroadcast.addBrick(changeYByNBrickDown);
			IfLogicElseBrick ifLogicElseBrickDown = new IfLogicElseBrick(ifLogicBeginBrickDown);
			birdScriptBroadcast.addBrick(ifLogicElseBrickDown);
			IfLogicEndBrick ifLogicEndBrickDown = new IfLogicEndBrick(ifLogicElseBrickDown, ifLogicBeginBrickDown);
			birdScriptBroadcast.addBrick(ifLogicEndBrickDown);
			//Left
			IfLogicBeginBrick ifLogicBeginBrickLeft = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.GAMEPAD_LEFT_PRESSED.name(), null)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickLeft);
			SetVariableBrick setVariableBrick1 = new SetVariableBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.TRUE.name(), null)), direction);
			birdScriptBroadcast.addBrick(setVariableBrick1);
			SetLookBrick setLookBrickUpLeft = new SetLookBrick();
			setLookBrickUpLeft.setLook(birdWingUpLeftLookData);
			birdScriptBroadcast.addBrick(setLookBrickUpLeft);
			IfLogicBeginBrick ifLogicBeginBrickMinX = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.EQUAL.name(), null, minX, birdX)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickMinX);
			PlaceAtBrick placeRight = new PlaceAtBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.NUMBER, "640", null)), new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_Y.name(), null)));
			birdScriptBroadcast.addBrick(placeRight);
			IfLogicElseBrick ifLogicElseBrickMinX = new IfLogicElseBrick(ifLogicBeginBrickMinX);
			birdScriptBroadcast.addBrick(ifLogicElseBrickMinX);
			IfLogicEndBrick ifLogicEndBrickMinX = new IfLogicEndBrick(ifLogicElseBrickMinX, ifLogicBeginBrickMinX);
			birdScriptBroadcast.addBrick(ifLogicEndBrickMinX);
			ChangeXByNBrick changeXByNBrickLeft = new ChangeXByNBrick(-5);
			birdScriptBroadcast.addBrick(changeXByNBrickLeft);
			IfLogicElseBrick ifLogicElseBrickLeft = new IfLogicElseBrick(ifLogicBeginBrickLeft);
			birdScriptBroadcast.addBrick(ifLogicElseBrickLeft);
			IfLogicEndBrick ifLogicEndBrickLeft = new IfLogicEndBrick(ifLogicElseBrickLeft, ifLogicBeginBrickLeft);
			birdScriptBroadcast.addBrick(ifLogicEndBrickLeft);
			//Right
			IfLogicBeginBrick ifLogicBeginBrickRight = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.GAMEPAD_RIGHT_PRESSED.name(), null)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickRight);
			SetVariableBrick setVariableBrick2 = new SetVariableBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.FALSE.name(), null)), direction);
			birdScriptBroadcast.addBrick(setVariableBrick2);
			SetLookBrick setLookBrickUpRight = new SetLookBrick();
			setLookBrickUpRight.setLook(birdWingUpLookData);
			birdScriptBroadcast.addBrick(setLookBrickUpRight);
			IfLogicBeginBrick ifLogicBeginBrickMaxX = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.EQUAL.name(), null, maxX, birdX)));
			birdScriptBroadcast.addBrick(ifLogicBeginBrickMaxX);
			PlaceAtBrick placeLeft = new PlaceAtBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.NUMBER, "-640", null)), new Formula(
					new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_Y.name(), null)));
			birdScriptBroadcast.addBrick(placeLeft);
			IfLogicElseBrick ifLogicElseBrickMaxX = new IfLogicElseBrick(ifLogicBeginBrickMaxX);
			birdScriptBroadcast.addBrick(ifLogicElseBrickMaxX);
			IfLogicEndBrick ifLogicEndBrickMaxX = new IfLogicEndBrick(ifLogicElseBrickMaxX, ifLogicBeginBrickMaxX);
			birdScriptBroadcast.addBrick(ifLogicEndBrickMaxX);
			ChangeXByNBrick changeXByNBrickRight = new ChangeXByNBrick(5);
			birdScriptBroadcast.addBrick(changeXByNBrickRight);
			IfLogicElseBrick ifLogicElseBrickRight = new IfLogicElseBrick(ifLogicBeginBrickRight);
			birdScriptBroadcast.addBrick(ifLogicElseBrickRight);
			IfLogicEndBrick ifLogicEndBrickRight = new IfLogicEndBrick(ifLogicElseBrickRight, ifLogicBeginBrickRight);
			birdScriptBroadcast.addBrick(ifLogicEndBrickRight);

			LoopEndlessBrick loopEndlessBrickBroadcast = new LoopEndlessBrick(foreverBrickBroadcast);
			birdScriptBroadcast.addBrick(loopEndlessBrickBroadcast);

			//Bird
			birdSprite.addScript(birdScriptBroadcast);

			Script birdScriptButtonA = new WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_A));
			IfLogicBeginBrick ifLogicBeginBrickBirdRight = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, direction.getName(), null)));
			birdScriptButtonA.addBrick(ifLogicBeginBrickBirdRight);
			//Bird wings left
			SetLookBrick setLookBrickDownLeft = new SetLookBrick();
			setLookBrickDownLeft.setLook(birdWingDownLeftLookData);
			birdScriptButtonA.addBrick(setLookBrickDownLeft);
			WaitBrick waitBrick1 = new WaitBrick(100);
			birdScriptButtonA.addBrick(waitBrick1);
			setLookBrickUpLeft = new SetLookBrick();
			setLookBrickUpLeft.setLook(birdWingUpLeftLookData);
			birdScriptButtonA.addBrick(setLookBrickUpLeft);
			//bird wings right
			IfLogicElseBrick ifLogicElseBrickBirdRight = new IfLogicElseBrick(ifLogicBeginBrickBirdRight);
			birdScriptButtonA.addBrick(ifLogicElseBrickBirdRight);
			SetLookBrick setLookBrickDownRight = new SetLookBrick();
			setLookBrickDownRight.setLook(birdWingDownLookData);
			birdScriptButtonA.addBrick(setLookBrickDownRight);
			WaitBrick waitBrick2 = new WaitBrick(100);
			birdScriptButtonA.addBrick(waitBrick2);
			setLookBrickUpRight = new SetLookBrick();
			setLookBrickUpRight.setLook(birdWingUpLookData);
			birdScriptButtonA.addBrick(setLookBrickUpRight);
			//end if
			IfLogicEndBrick ifLogicEndBrickBirdRight = new IfLogicEndBrick(ifLogicElseBrickBirdRight, ifLogicBeginBrickBirdRight);
			birdScriptButtonA.addBrick(ifLogicEndBrickBirdRight);

			birdSprite.addScript(birdScriptButtonA);

			Script birdScriptButtonB = new WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_B));
			IfLogicBeginBrick ifLogicBeginBrickBirdLeft = new IfLogicBeginBrick(new Formula(
					new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, direction.getName(), null)));
			birdScriptButtonB.addBrick(ifLogicBeginBrickBirdLeft);
			//Bird sound
			PlaySoundBrick playSoundBrickBird1 = new PlaySoundBrick();
			playSoundBrickBird1.setSound(soundInfo1);
			birdScriptButtonB.addBrick(playSoundBrickBird1);
			IfLogicElseBrick ifLogicElseBrickBirdLeft = new IfLogicElseBrick(ifLogicBeginBrickBirdLeft);
			birdScriptButtonB.addBrick(ifLogicElseBrickBirdLeft);
			PlaySoundBrick playSoundBrickBird2 = new PlaySoundBrick();
			playSoundBrickBird2.setSound(soundInfo2);
			birdScriptButtonB.addBrick(playSoundBrickBird2);
			IfLogicEndBrick ifLogicEndBrickBirdLeft = new IfLogicEndBrick(ifLogicElseBrickBirdLeft, ifLogicBeginBrickBirdLeft);
			birdScriptButtonB.addBrick(ifLogicEndBrickBirdLeft);

			birdSprite.addScript(birdScriptButtonB);
			defaultProject.getDefaultScene().addSprite(birdSprite);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IOException(TAG, illegalArgumentException);
		}

		StorageHandler.getInstance().saveProject(defaultProject);

		return defaultProject;
	}
}
