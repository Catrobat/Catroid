/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.common;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.DroneLandBrick;
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;

public final class StandardProjectHandler {

	private static final String TAG = StandardProjectHandler.class.getSimpleName();
	private static double backgroundImageScaleFactor = 1;
	public static final String TAG = StandardProjectHandler.class.getSimpleName();

	// Suppress default constructor for noninstantiability
	private StandardProjectHandler() {
		throw new AssertionError();
	}

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createAndSaveStandardProject(projectName, context);
	}

	public static Project createAndSaveStandardDroneProject(Context context) throws IOException {
		Log.d(TAG, "createAndSaveStandardDroneProject");
		String projectName = context.getString(R.string.default_drone_project_name);
		return createAndSaveStandardDroneProject(projectName, context);
	}

	public static Project createAndSaveStandardDroneProject(String projectName, Context context) throws IOException,
			IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}

		String backgroundName = context.getString(R.string.default_project_backgroundname);
		String takeOffSpriteName = context.getString(R.string.default_drone_project_sprites_takeoff);
		String landSpriteName = context.getString(R.string.default_drone_project_srpites_land);
		String blinkLedSpriteName = context.getString(R.string.default_drone_project_sprites_blink_led);
		String takeOffLookName = context.getString(R.string.default_drone_project_sprites_takeoff) + " arrow";
		String landLookName = context.getString(R.string.default_drone_project_srpites_land) + " arrow";

		String playLedName = context.getString(R.string.default_drone_project_sprites_takeoff) + " arrow";

		Project defaultDroneProject = new Project(context, projectName);
		defaultDroneProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultDroneProject);
		ProjectManager.getInstance().setProject(defaultDroneProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor);

		File takeOffArrowFile = UtilFile.copyImageFromResourceIntoProject(projectName, takeOffSpriteName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_drone_go_top_orange, context,
				true, backgroundImageScaleFactor);

		File landArrowFile = UtilFile.copyImageFromResourceIntoProject(projectName, takeOffSpriteName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_drone_go_bottom_orange, context,
				true, backgroundImageScaleFactor);

		File playLedFile = UtilFile.copyImageFromResourceIntoProject(projectName, playLedName
				+ Constants.IMAGE_STANDARD_EXTENTION,
				R.drawable.default_drone_project_084323_orange_white_pearl_icon_business_light_bulb2_sc52, context,
				true, backgroundImageScaleFactor);

		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(backgroundName);
		backgroundLookData.setLookFilename(backgroundFile.getName());

		Sprite backgroundSprite = defaultDroneProject.getSpriteList().get(0);

		// Background sprite
		backgroundSprite.getLookDataList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript(backgroundSprite);

		SetLookBrick setLookBrick = new SetLookBrick(backgroundSprite);
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);

		//Takeoff sprite
		Sprite takeOffSprite = new Sprite(takeOffSpriteName);
		defaultDroneProject.addSprite(takeOffSprite);

		Script whenSpriteTappedScript = new WhenScript(takeOffSprite);
		DroneTakeOffBrick takeOffBrick = new DroneTakeOffBrick(takeOffSprite);
		whenSpriteTappedScript.addBrick(takeOffBrick);

		Script whenProjectStartsScript = new StartScript(takeOffSprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(takeOffSprite, calculateValueRelativeToScaledBackground(0),
				calculateValueRelativeToScaledBackground(-400));
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(takeOffSprite, 50.0);

		whenProjectStartsScript.addBrick(placeAtBrick);
		whenProjectStartsScript.addBrick(setSizeBrick);

		LookData takeOffLookData = new LookData();
		takeOffLookData.setLookName(takeOffLookName);
		takeOffLookData.setLookFilename(takeOffArrowFile.getName());

		takeOffSprite.getLookDataList().add(takeOffLookData);

		takeOffSprite.addScript(whenSpriteTappedScript);
		takeOffSprite.addScript(whenProjectStartsScript);
		//Takeoff sprite end

		//land Sprite start
		Sprite landSprite = new Sprite(landSpriteName);
		defaultDroneProject.addSprite(landSprite);

		Script whenLandSpriteTappedScript = new WhenScript(landSprite);
		DroneLandBrick landBrick = new DroneLandBrick(landSprite);
		whenLandSpriteTappedScript.addBrick(landBrick);

		Script whenProjectStartsLandScript = new StartScript(landSprite);
		PlaceAtBrick placeAtBrickLandSprite = new PlaceAtBrick(landSprite,
				calculateValueRelativeToScaledBackground(200), calculateValueRelativeToScaledBackground(-400));
		SetSizeToBrick setSizeBrickLandSprite = new SetSizeToBrick(landSprite, 50.0);

		whenProjectStartsLandScript.addBrick(placeAtBrickLandSprite);
		whenProjectStartsLandScript.addBrick(setSizeBrickLandSprite);

		LookData landLookData = new LookData();
		landLookData.setLookName(landLookName);
		landLookData.setLookFilename(landArrowFile.getName());

		landSprite.getLookDataList().add(landLookData);

		landSprite.addScript(whenProjectStartsLandScript);
		landSprite.addScript(whenLandSpriteTappedScript);
		//land Sprite end

		//Led Sprite
		Sprite blinkLedSprite = new Sprite(blinkLedSpriteName);
		defaultDroneProject.addSprite(blinkLedSprite);

		Script whenBlinkLedSpriteTappedScript = new WhenScript(blinkLedSprite);
		DronePlayLedAnimationBrick blinkLedBrick = new DronePlayLedAnimationBrick(blinkLedSprite);
		whenBlinkLedSpriteTappedScript.addBrick(blinkLedBrick);

		Script whenProjectStartsScriptLed = new StartScript(blinkLedSprite);
		PlaceAtBrick placeAtBrickLed = new PlaceAtBrick(blinkLedSprite, calculateValueRelativeToScaledBackground(-200),
				calculateValueRelativeToScaledBackground(-400));
		SetSizeToBrick setSizeBrickLed = new SetSizeToBrick(blinkLedSprite, 50.0);

		whenProjectStartsScriptLed.addBrick(placeAtBrickLed);
		whenProjectStartsScriptLed.addBrick(setSizeBrickLed);

		LookData blinkLedLookData = new LookData();
		blinkLedLookData.setLookName(playLedName);
		blinkLedLookData.setLookFilename(playLedFile.getName());

		blinkLedSprite.getLookDataList().add(blinkLedLookData);

		blinkLedSprite.addScript(whenBlinkLedSpriteTappedScript);
		blinkLedSprite.addScript(whenProjectStartsScriptLed);

		StorageHandler.getInstance().saveProject(defaultDroneProject);

		return defaultDroneProject;
	}

	public static Project createAndSaveStandardProject(String projectName, Context context) throws IOException,
			IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		String moleLookName = context.getString(R.string.default_project_sprites_mole_name);
		String mole1Name = moleLookName + " 1";
		String mole2Name = moleLookName + " 2";
		String mole3Name = moleLookName + " 3";
		String mole4Name = moleLookName + " 4";
		String whackedMoleLookName = context.getString(R.string.default_project_sprites_mole_whacked);
		String movingMoleLookName = context.getString(R.string.default_project_sprites_mole_moving);
		String soundName = context.getString(R.string.default_project_sprites_mole_sound);
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		String varRandomFrom = context.getString(R.string.default_project_var_random_from);
		String varRandomTo = context.getString(R.string.default_project_var_random_to);

		Project defaultProject = new Project(context, projectName);
		defaultProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor
		);
		File movingMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, movingMoleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_moving, context, true,
				backgroundImageScaleFactor
		);
		File diggedOutMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, moleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_digged_out, context, true,
				backgroundImageScaleFactor
		);
		File whackedMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, whackedMoleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_whacked, context, true,
				backgroundImageScaleFactor
		);
		try {
			File soundFile1 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "1"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_1, context, true);
			File soundFile2 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "2"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_2, context, true);
			File soundFile3 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "3"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_3, context, true);
			File soundFile4 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "4"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_4, context, true);
			UtilFile.copyFromResourceIntoProject(projectName, ".", StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME,
					R.drawable.default_project_screenshot, context, false);

			LookData movingMoleLookData = new LookData();
			movingMoleLookData.setLookName(movingMoleLookName);
			movingMoleLookData.setLookFilename(movingMoleFile.getName());

			LookData diggedOutMoleLookData = new LookData();
			diggedOutMoleLookData.setLookName(moleLookName);
			diggedOutMoleLookData.setLookFilename(diggedOutMoleFile.getName());

			LookData whackedMoleLookData = new LookData();
			whackedMoleLookData.setLookName(whackedMoleLookName);
			whackedMoleLookData.setLookFilename(whackedMoleFile.getName());

			LookData backgroundLookData = new LookData();
			backgroundLookData.setLookName(backgroundName);
			backgroundLookData.setLookFilename(backgroundFile.getName());

			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setTitle(soundName);
			soundInfo.setSoundFileName(soundFile1.getName());

			UserVariablesContainer userVariables = defaultProject.getUserVariables();
			Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

			userVariables.addProjectUserVariable(varRandomFrom);
			UserVariable randomFrom = userVariables.getUserVariable(varRandomFrom, backgroundSprite);

			userVariables.addProjectUserVariable(varRandomTo);
			UserVariable randomTo = userVariables.getUserVariable(varRandomTo, backgroundSprite);

			// Background sprite
			backgroundSprite.getLookDataList().add(backgroundLookData);
			Script backgroundStartScript = new StartScript(backgroundSprite);

			SetLookBrick setLookBrick = new SetLookBrick(backgroundSprite);
			setLookBrick.setLook(backgroundLookData);
			backgroundStartScript.addBrick(setLookBrick);

			SetVariableBrick setVariableBrick = new SetVariableBrick(backgroundSprite, new Formula(1), randomFrom);
			backgroundStartScript.addBrick(setVariableBrick);

			setVariableBrick = new SetVariableBrick(backgroundSprite, new Formula(5), randomTo);
			backgroundStartScript.addBrick(setVariableBrick);

			backgroundSprite.addScript(backgroundStartScript);

			FormulaElement randomElement = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(), null);
			randomElement.setLeftChild(new FormulaElement(ElementType.USER_VARIABLE, varRandomFrom, randomElement));
			randomElement.setRightChild(new FormulaElement(ElementType.USER_VARIABLE, varRandomTo, randomElement));
			Formula randomWait = new Formula(randomElement);

			FormulaElement waitOneOrTwoSeconds = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(), null);
			waitOneOrTwoSeconds.setLeftChild(new FormulaElement(ElementType.NUMBER, "1", waitOneOrTwoSeconds));
			waitOneOrTwoSeconds.setRightChild(new FormulaElement(ElementType.NUMBER, "2", waitOneOrTwoSeconds));

			// Mole 1 sprite
			Sprite mole1Sprite = new Sprite(mole1Name);
			mole1Sprite.getLookDataList().add(movingMoleLookData);
			mole1Sprite.getLookDataList().add(diggedOutMoleLookData);
			mole1Sprite.getLookDataList().add(whackedMoleLookData);
			mole1Sprite.getSoundList().add(soundInfo);

			Script mole1StartScript = new StartScript(mole1Sprite);
			Script mole1WhenScript = new WhenScript(mole1Sprite);

			// start script
			SetSizeToBrick setSizeToBrick = new SetSizeToBrick(mole1Sprite, new Formula(30));
			mole1StartScript.addBrick(setSizeToBrick);

			ForeverBrick foreverBrick = new ForeverBrick(mole1Sprite);
			mole1StartScript.addBrick(foreverBrick);

			PlaceAtBrick placeAtBrick = new PlaceAtBrick(mole1Sprite, calculateValueRelativeToScaledBackground(-160),
					calculateValueRelativeToScaledBackground(-110));
			mole1StartScript.addBrick(placeAtBrick);

			WaitBrick waitBrick = new WaitBrick(mole1Sprite, new Formula(waitOneOrTwoSeconds));
			mole1StartScript.addBrick(waitBrick);

			ShowBrick showBrick = new ShowBrick(mole1Sprite);
			mole1StartScript.addBrick(showBrick);

			setLookBrick = new SetLookBrick(mole1Sprite);
			setLookBrick.setLook(movingMoleLookData);
			mole1StartScript.addBrick(setLookBrick);

			GlideToBrick glideToBrick = new GlideToBrick(mole1Sprite, calculateValueRelativeToScaledBackground(-160),
					calculateValueRelativeToScaledBackground(-95), 100);
			mole1StartScript.addBrick(glideToBrick);

			setLookBrick = new SetLookBrick(mole1Sprite);
			setLookBrick.setLook(diggedOutMoleLookData);
			mole1StartScript.addBrick(setLookBrick);

			waitBrick = new WaitBrick(mole1Sprite, randomWait.clone());
			mole1StartScript.addBrick(waitBrick);

			HideBrick hideBrick = new HideBrick(mole1Sprite);
			mole1StartScript.addBrick(hideBrick);

			waitBrick = new WaitBrick(mole1Sprite, randomWait.clone());
			mole1StartScript.addBrick(waitBrick);

			LoopEndlessBrick loopEndlessBrick = new LoopEndlessBrick(mole1Sprite, foreverBrick);
			mole1StartScript.addBrick(loopEndlessBrick);

			// when script
			PlaySoundBrick playSoundBrick = new PlaySoundBrick(mole1Sprite);
			playSoundBrick.setSoundInfo(soundInfo);
			mole1WhenScript.addBrick(playSoundBrick);

			setLookBrick = new SetLookBrick(mole1Sprite);
			setLookBrick.setLook(whackedMoleLookData);
			mole1WhenScript.addBrick(setLookBrick);

			waitBrick = new WaitBrick(mole1Sprite, 1500);
			mole1WhenScript.addBrick(waitBrick);

			hideBrick = new HideBrick(mole1Sprite);
			mole1WhenScript.addBrick(hideBrick);

			mole1Sprite.addScript(mole1StartScript);
			mole1Sprite.addScript(mole1WhenScript);
			defaultProject.addSprite(mole1Sprite);

			StorageHandler.getInstance().fillChecksumContainer();

			// Mole 2 sprite
			Sprite mole2Sprite = mole1Sprite.clone();
			mole2Sprite.getSoundList().get(0).setSoundFileName(soundFile2.getName());
			mole2Sprite.setName(mole2Name);
			defaultProject.addSprite(mole2Sprite);

			Script tempScript = mole2Sprite.getScript(0);
			placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
			placeAtBrick.setXPosition(new Formula(calculateValueRelativeToScaledBackground(160)));
			placeAtBrick.setYPosition(new Formula(calculateValueRelativeToScaledBackground(-110)));

			glideToBrick = (GlideToBrick) tempScript.getBrick(6);
			glideToBrick.setXDestination(new Formula(calculateValueRelativeToScaledBackground(160)));
			glideToBrick.setYDestination(new Formula(calculateValueRelativeToScaledBackground(-95)));

			// Mole 3 sprite
			Sprite mole3Sprite = mole1Sprite.clone();
			mole3Sprite.getSoundList().get(0).setSoundFileName(soundFile3.getName());
			mole3Sprite.setName(mole3Name);
			defaultProject.addSprite(mole3Sprite);

			tempScript = mole3Sprite.getScript(0);
			placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
			placeAtBrick.setXPosition(new Formula(calculateValueRelativeToScaledBackground(-160)));
			placeAtBrick.setYPosition(new Formula(calculateValueRelativeToScaledBackground(-290)));

			glideToBrick = (GlideToBrick) tempScript.getBrick(6);
			glideToBrick.setXDestination(new Formula(calculateValueRelativeToScaledBackground(-160)));
			glideToBrick.setYDestination(new Formula(calculateValueRelativeToScaledBackground(-275)));

			// Mole 4 sprite
			Sprite mole4Sprite = mole1Sprite.clone();
			mole4Sprite.getSoundList().get(0).setSoundFileName(soundFile4.getName());
			mole4Sprite.setName(mole4Name);
			defaultProject.addSprite(mole4Sprite);

			tempScript = mole4Sprite.getScript(0);
			placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
			placeAtBrick.setXPosition(new Formula(calculateValueRelativeToScaledBackground(160)));
			placeAtBrick.setYPosition(new Formula(calculateValueRelativeToScaledBackground(-290)));

			glideToBrick = (GlideToBrick) tempScript.getBrick(6);
			glideToBrick.setXDestination(new Formula(calculateValueRelativeToScaledBackground(160)));
			glideToBrick.setYDestination(new Formula(calculateValueRelativeToScaledBackground(-275)));
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IOException(TAG, illegalArgumentException);
		}

		StorageHandler.getInstance().saveProject(defaultProject);

		return defaultProject;
	}

	public static Project createAndSaveEmptyProject(String projectName, Context context) {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		Project emptyProject = new Project(context, projectName);
		emptyProject.setDeviceData(context);
		StorageHandler.getInstance().saveProject(emptyProject);
		ProjectManager.getInstance().setProject(emptyProject);

		return emptyProject;
	}

	private static int calculateValueRelativeToScaledBackground(int value) {
		int returnValue = (int) (value * backgroundImageScaleFactor);
		int differenceToNextFive = returnValue % 5;
		return returnValue - differenceToNextFive;
	}
}
