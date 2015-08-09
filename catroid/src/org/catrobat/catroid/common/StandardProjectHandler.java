/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import org.catrobat.catroid.content.bricks.BrickBaseType;
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
import org.catrobat.catroid.drone.DroneBrickFactory;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.UserVariable;
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

	// Suppress default constructor for noninstantiability
	private StandardProjectHandler() {
		throw new AssertionError();
	}

	public static Project createAndSaveStandardProject(Context context, boolean landscape) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		Project standardProject = null;

		if (StorageHandler.getInstance().projectExists(projectName)) {
			StorageHandler.getInstance().deleteProject(projectName);
		}

		try {
			standardProject = createAndSaveStandardProject(projectName, context, landscape);
		} catch (IllegalArgumentException ilArgument) {
			Log.e(TAG, "Could not create standard project!", ilArgument);
		}

		return standardProject;
	}

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		return createAndSaveStandardProject(context, false);
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

		Project defaultDroneProject = new Project(context, projectName);
		defaultDroneProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultDroneProject);
		ProjectManager.getInstance().setProject(defaultDroneProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor);

		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(backgroundName);
		backgroundLookData.setLookFilename(backgroundFile.getName());

		Sprite backgroundSprite = defaultDroneProject.getSpriteList().get(0);

		// Background sprite
		backgroundSprite.getLookDataList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);

		//Takeoff sprite
		String takeOffSpriteName = context.getString(R.string.default_drone_project_sprites_takeoff);

		File takeOffArrowFile = UtilFile.copyImageFromResourceIntoProject(projectName, takeOffSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_takeoff, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(takeOffSpriteName, DroneBrickFactory.DroneBricks.DRONE_TAKE_OFF_BRICK,
				-260, -200, takeOffArrowFile));

		//land Sprite start
		String landSpriteName = context.getString(R.string.default_drone_project_srpites_land);

		File landArrowFile = UtilFile.copyImageFromResourceIntoProject(projectName, takeOffSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_land, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(landSpriteName, DroneBrickFactory.DroneBricks.DRONE_LAND_BRICK, -260,
				-325, landArrowFile));

		//rotate Sprite start
		String rotateSpriteName = context.getString(R.string.default_drone_project_srpites_rotate);

		File rotateFile = UtilFile.copyImageFromResourceIntoProject(projectName, rotateSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_rotate, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(rotateSpriteName, DroneBrickFactory.DroneBricks.DRONE_FLIP_BRICK,
				-260, -450, rotateFile));

		//Led Sprite
		//TODO Drone: add when PlayLedAnimationBrick works
		//String blinkLedSpriteName = context.getString(R.string.default_drone_project_sprites_blink_led);

		//TODO Drone: add when PlayLedAnimationBrick works
		//File playLedFile = UtilFile.copyImageFromResourceIntoProject(projectName, blinkLedSpriteName
		//		+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_light_bulb, context,
		//		true, backgroundImageScaleFactor);

		//TODO Drone: add when PlayLedAnimationBrick works
		//defaultDroneProject.addSprite(createDroneSprite(blinkLedSpriteName,
		//		DroneUtils.DroneBricks.DRONE_PLAY_LED_ANIMATION_BRICK, -100, -450, playLedFile));

		//Up Sprite
		String upSpriteName = context.getString(R.string.default_drone_project_sprites_up);

		File upFile = UtilFile.copyImageFromResourceIntoProject(projectName, upSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_arrow_up, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(upSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_UP_BRICK, -100,
				-200, upFile, 2000));

		//Down Sprite
		String downSpriteName = context.getString(R.string.default_drone_project_sprites_down);

		File downFile = UtilFile.copyImageFromResourceIntoProject(projectName, downSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_arrow_down, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(downSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_DOWN_BRICK,
				-100, -325, downFile, 2000));

		//Forward Sprite
		String forwardSpriteName = context.getString(R.string.default_drone_project_sprites_forward);

		File forwardFile = UtilFile.copyImageFromResourceIntoProject(projectName, forwardSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_go_forward, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(forwardSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_FORWARD_BRICK, 180, -75, forwardFile, 2000));

		//Backward Sprite
		String backwardpriteName = context.getString(R.string.default_drone_project_sprites_back);

		File backwardFile = UtilFile.copyImageFromResourceIntoProject(projectName, downSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_go_back, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(backwardpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_BACKWARD_BRICK, 180, -450, backwardFile, 2000));

		//Left Sprite
		String leftSpriteName = context.getString(R.string.default_drone_project_sprites_left);

		File leftFile = UtilFile.copyImageFromResourceIntoProject(projectName, leftSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_go_left, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(leftSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_LEFT_BRICK,
				100, -325, leftFile, 2000));

		//Right Sprite
		String rightSpriteName = context.getString(R.string.default_drone_project_sprites_right);

		File rightFile = UtilFile.copyImageFromResourceIntoProject(projectName, rightSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_go_right, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(rightSpriteName, DroneBrickFactory.DroneBricks.DRONE_MOVE_RIGHT_BRICK,
				260, -325, rightFile, 2000));

		//Turn Left Sprite
		String turnLeftSpriteName = context.getString(R.string.default_drone_project_sprites_turn_left);

		File turnLeftFile = UtilFile.copyImageFromResourceIntoProject(projectName, turnLeftSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_turn_left, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(turnLeftSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_TURN_LEFT_BRICK, 100, -200, turnLeftFile, 2000));

		//Turn Right Sprite
		String turnRightSpriteName = context.getString(R.string.default_drone_project_sprites_turn_right);

		File turnrightFile = UtilFile.copyImageFromResourceIntoProject(projectName, turnRightSpriteName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_turn_right, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(turnRightSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_TURN_RIGHT_BRICK, 260, -200, turnrightFile, 2000));

		StorageHandler.getInstance().saveProject(defaultDroneProject);
		return defaultDroneProject;
	}

	private static Sprite createDroneSprite(String spriteName, DroneBrickFactory.DroneBricks brickName, int xPostition,
			int yPosition, File lookFile) {
		return createDroneSprite(spriteName, brickName, xPostition, yPosition, lookFile, 0, 0);
	}

	private static Sprite createDroneSprite(String spriteName, DroneBrickFactory.DroneBricks brickName, int xPostition,
			int yPosition, File lookFile, int timeInMilliseconds) {
		return createDroneSprite(spriteName, brickName, xPostition, yPosition, lookFile, timeInMilliseconds, 20);
	}

	private static Sprite createDroneSprite(String spriteName, DroneBrickFactory.DroneBricks brickName, int xPostition,
			int yPosition, File lookFile, int timeInMilliseconds, int powerInPercent) {
		//
		Sprite sprite = new Sprite(spriteName);
		//defaultDroneProject.addSprite(takeOffSprite);

		Script whenSpriteTappedScript = new WhenScript();
		BrickBaseType brick = DroneBrickFactory.getInstanceOfDroneBrick(brickName, sprite, timeInMilliseconds, powerInPercent);
		whenSpriteTappedScript.addBrick(brick);

		Script whenProjectStartsScript = new StartScript();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(calculateValueRelativeToScaledBackground(xPostition),
				calculateValueRelativeToScaledBackground(yPosition));
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(50.0);

		whenProjectStartsScript.addBrick(placeAtBrick);
		whenProjectStartsScript.addBrick(setSizeBrick);

		LookData lookData = new LookData();
		lookData.setLookName(spriteName + " icon");

		lookData.setLookFilename(lookFile.getName());

		sprite.getLookDataList().add(lookData);

		sprite.addScript(whenSpriteTappedScript);
		sprite.addScript(whenProjectStartsScript);

		return sprite;
	}

	public static Project createAndSaveStandardProject(String projectName, Context context, boolean landscape) throws
			IOException,
			IllegalArgumentException {
		// temporarily until standard landscape project exists.
		if (landscape) {
			return createAndSaveEmptyProject(projectName, context, landscape);
		}
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

		Project defaultProject = new Project(context, projectName, landscape);
		defaultProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor);
		File movingMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, movingMoleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_moving, context, true,
				backgroundImageScaleFactor);
		File diggedOutMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, moleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_digged_out, context, true,
				backgroundImageScaleFactor);
		File whackedMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, whackedMoleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_whacked, context, true,
				backgroundImageScaleFactor);
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

			Log.i(TAG, String.format("createAndSaveStandardProject(%s) %s created%n %s created%n %s created%n %s created%n %s created%n %s created%n %s created%n %s created%n",
					projectName, backgroundFile.getName(), movingMoleFile.getName(), diggedOutMoleFile.getName(), whackedMoleFile.getName(),
					soundFile1.getName(), soundFile2.getName(), soundFile3.getName(), soundFile4.getName()));

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

			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());

			DataContainer userVariables = defaultProject.getDataContainer();
			Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

			userVariables.addProjectUserVariable(varRandomFrom);
			UserVariable randomFrom = userVariables.getUserVariable(varRandomFrom, backgroundSprite);

			userVariables.addProjectUserVariable(varRandomTo);
			UserVariable randomTo = userVariables.getUserVariable(varRandomTo, backgroundSprite);

			// Background sprite
			backgroundSprite.getLookDataList().add(backgroundLookData);
			Script backgroundStartScript = new StartScript();

			SetLookBrick setLookBrick = new SetLookBrick();
			setLookBrick.setLook(backgroundLookData);
			backgroundStartScript.addBrick(setLookBrick);

			SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(1), randomFrom);
			backgroundStartScript.addBrick(setVariableBrick);

			setVariableBrick = new SetVariableBrick(new Formula(5), randomTo);
			backgroundStartScript.addBrick(setVariableBrick);

			backgroundSprite.addScript(backgroundStartScript);

			FormulaElement randomElement = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(), null);
			randomElement.setLeftChild(new FormulaElement(ElementType.USER_VARIABLE, varRandomFrom, randomElement));
			randomElement.setRightChild(new FormulaElement(ElementType.USER_VARIABLE, varRandomTo, randomElement));
			Formula randomWait = new Formula(randomElement);

			FormulaElement waitOneOrTwoSeconds = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(),
					null);
			waitOneOrTwoSeconds.setLeftChild(new FormulaElement(ElementType.NUMBER, "1", waitOneOrTwoSeconds));
			waitOneOrTwoSeconds.setRightChild(new FormulaElement(ElementType.NUMBER, "2", waitOneOrTwoSeconds));

			// Mole 1 sprite
			Sprite mole1Sprite = new Sprite(mole1Name);
			mole1Sprite.getLookDataList().add(movingMoleLookData);
			mole1Sprite.getLookDataList().add(diggedOutMoleLookData);
			mole1Sprite.getLookDataList().add(whackedMoleLookData);
			mole1Sprite.getSoundList().add(soundInfo);

			Script mole1StartScript = new StartScript();
			Script mole1WhenScript = new WhenScript();

			// start script
			SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(30));
			mole1StartScript.addBrick(setSizeToBrick);

			ForeverBrick foreverBrick = new ForeverBrick();
			mole1StartScript.addBrick(foreverBrick);

			PlaceAtBrick placeAtBrick = new PlaceAtBrick(calculateValueRelativeToScaledBackground(-160),
					calculateValueRelativeToScaledBackground(-110));
			mole1StartScript.addBrick(placeAtBrick);

			WaitBrick waitBrick = new WaitBrick(new Formula(waitOneOrTwoSeconds));
			mole1StartScript.addBrick(waitBrick);

			ShowBrick showBrick = new ShowBrick();
			mole1StartScript.addBrick(showBrick);

			setLookBrick = new SetLookBrick();
			setLookBrick.setLook(movingMoleLookData);
			mole1StartScript.addBrick(setLookBrick);

			GlideToBrick glideToBrick = new GlideToBrick(calculateValueRelativeToScaledBackground(-160),
					calculateValueRelativeToScaledBackground(-95), 100);
			mole1StartScript.addBrick(glideToBrick);

			setLookBrick = new SetLookBrick();
			setLookBrick.setLook(diggedOutMoleLookData);
			mole1StartScript.addBrick(setLookBrick);

			//add filechecksums
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(movingMoleLookData.getChecksum(), movingMoleLookData.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(diggedOutMoleLookData.getChecksum(), diggedOutMoleLookData.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(whackedMoleLookData.getChecksum(), whackedMoleLookData.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(backgroundLookData.getChecksum(), backgroundLookData.getAbsolutePath());

			waitBrick = new WaitBrick(randomWait.clone());
			mole1StartScript.addBrick(waitBrick);

			HideBrick hideBrick = new HideBrick();
			mole1StartScript.addBrick(hideBrick);

			waitBrick = new WaitBrick(randomWait.clone());
			mole1StartScript.addBrick(waitBrick);

			LoopEndlessBrick loopEndlessBrick = new LoopEndlessBrick(foreverBrick);
			mole1StartScript.addBrick(loopEndlessBrick);

			// when script
			PlaySoundBrick playSoundBrick = new PlaySoundBrick();
			playSoundBrick.setSoundInfo(soundInfo);
			mole1WhenScript.addBrick(playSoundBrick);

			setLookBrick = new SetLookBrick();
			setLookBrick.setLook(whackedMoleLookData);
			mole1WhenScript.addBrick(setLookBrick);

			waitBrick = new WaitBrick(1500);
			mole1WhenScript.addBrick(waitBrick);

			hideBrick = new HideBrick();
			mole1WhenScript.addBrick(hideBrick);

			mole1Sprite.addScript(mole1StartScript);
			mole1Sprite.addScript(mole1WhenScript);
			defaultProject.addSprite(mole1Sprite);

			StorageHandler.getInstance().fillChecksumContainer();

			// Mole 2 sprite
			Sprite mole2Sprite = mole1Sprite.clone();
			mole2Sprite.getSoundList().get(0).setSoundFileName(soundFile2.getName());

			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundFile2.getName(), soundFile2.getAbsolutePath());

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

			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundFile3.getName(), soundFile3.getAbsolutePath());

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

			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundFile4.getName(), soundFile4.getAbsolutePath());

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

	public static Project createAndSaveStandardProject(String projectName, Context context) throws
			IOException,
			IllegalArgumentException {
		return createAndSaveStandardProject(projectName, context, false);
	}

	public static Project createAndSaveEmptyProject(String projectName, Context context, boolean landscape) {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		Project emptyProject = new Project(context, projectName, landscape);
		emptyProject.setDeviceData(context);
		StorageHandler.getInstance().saveProject(emptyProject);
		ProjectManager.getInstance().setProject(emptyProject);

		return emptyProject;
	}

	public static Project createAndSaveEmptyProject(String projectName, Context context) {
		return createAndSaveEmptyProject(projectName, context, false);
	}

	private static int calculateValueRelativeToScaledBackground(int value) {
		int returnValue = (int) (value * backgroundImageScaleFactor);
		int differenceToNextFive = returnValue % 5;
		return returnValue - differenceToNextFive;
	}
}
