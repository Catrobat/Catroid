/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.conditional.GlideToBrick;
import org.catrobat.catroid.content.bricks.conditional.HideBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.conditional.SetLookBrick;
import org.catrobat.catroid.content.bricks.conditional.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.conditional.ShowBrick;
import org.catrobat.catroid.content.bricks.conditional.TurnLeftBrick;
import org.catrobat.catroid.drone.DroneBrickFactory;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.PhysicsWorld;
import org.catrobat.catroid.physic.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physic.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physic.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physic.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physic.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physic.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public final class StandardProjectHandler {

	private static final String TAG = StandardProjectHandler.class.getSimpleName();
	private static double backgroundImageScaleFactor = 1;
	private static final String FILENAME_SEPARATOR = "_";

	// Suppress default constructor for noninstantiability
	private StandardProjectHandler() {
		throw new AssertionError();
	}

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createAndSaveStandardPhysicProject(projectName, context);
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
		Script backgroundStartScript = new StartScript(backgroundSprite);

		SetLookBrick setLookBrick = new SetLookBrick(backgroundSprite);
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);

		//Takeoff sprite
		String takeOffSpriteName = context.getString(R.string.default_drone_project_sprites_takeoff);

		File takeOffArrowFile = UtilFile.copyImageFromResourceIntoProject(projectName, takeOffSpriteName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_takeoff, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(takeOffSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_TAKE_OFF_BRICK, -260, -200, takeOffArrowFile));

		//land Sprite start
		String landSpriteName = context.getString(R.string.default_drone_project_srpites_land);

		File landArrowFile = UtilFile.copyImageFromResourceIntoProject(projectName, takeOffSpriteName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_land, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(landSpriteName, DroneBrickFactory.DroneBricks.DRONE_LAND_BRICK,
				-260, -325, landArrowFile));

		//rotate Sprite start
		String rotateSpriteName = context.getString(R.string.default_drone_project_srpites_rotate);

		File rotateFile = UtilFile.copyImageFromResourceIntoProject(projectName, rotateSpriteName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_rotate, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(rotateSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_FLIP_BRICK, -260, -450, rotateFile));

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

		defaultDroneProject.addSprite(createDroneSprite(upSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_UP_BRICK, -100, -200, upFile, 2000));

		//Down Sprite
		String downSpriteName = context.getString(R.string.default_drone_project_sprites_down);

		File downFile = UtilFile.copyImageFromResourceIntoProject(projectName, downSpriteName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_arrow_down, context,
				true, backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(downSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_DOWN_BRICK, -100, -325, downFile, 2000));

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

		defaultDroneProject.addSprite(createDroneSprite(leftSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_LEFT_BRICK, 100, -325, leftFile, 2000));

		//Right Sprite
		String rightSpriteName = context.getString(R.string.default_drone_project_sprites_right);

		File rightFile = UtilFile.copyImageFromResourceIntoProject(projectName, rightSpriteName
				+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_drone_project_orange_go_right, context, true,
				backgroundImageScaleFactor);

		defaultDroneProject.addSprite(createDroneSprite(rightSpriteName,
				DroneBrickFactory.DroneBricks.DRONE_MOVE_RIGHT_BRICK, 260, -325, rightFile, 2000));

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

		Script whenSpriteTappedScript = new WhenScript(sprite);
		BrickBaseType brick = DroneBrickFactory.getInstanceOfDroneBrick(brickName, sprite, timeInMilliseconds,
				powerInPercent);
		whenSpriteTappedScript.addBrick(brick);

		Script whenProjectStartsScript = new StartScript(sprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, calculateValueRelativeToScaledBackground(xPostition),
				calculateValueRelativeToScaledBackground(yPosition));
		SetSizeToBrick setSizeBrick = new SetSizeToBrick(sprite, 50.0);

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
		defaultProject.getXmlHeader().virtualScreenWidth = 480;
		defaultProject.getXmlHeader().virtualScreenHeight = 800;
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
		return null;
	}

	// XXX: Only needed for pinball game and demonstration purposes. 
	private static String projectName;
	private static Context context;

	public static Project createAndSaveStandardPhysicProject(String projectName, Context context) throws IOException {
		StandardProjectHandler.context = context;
		StandardProjectHandler.projectName = projectName;

		Project defaultProject = new Project();
		PhysicsWorld physicsWorld = defaultProject.getPhysicWorld();

		Sprite background = defaultProject.getSpriteList().get(0);

		Sprite ball = new Sprite("Ball");

		Sprite leftButton = new Sprite("Left button");
		Sprite rightButton = new Sprite("Right button");

		Sprite leftArm = new Sprite("Left arm");
		Sprite rightArm = new Sprite("Right arm");

		Sprite[] upperBouncers = { new Sprite("Middle cat bouncer"), new Sprite("Right cat bouncer") };

		Sprite[] lowerBouncers = { new Sprite("Left wool bouncer"), new Sprite("Middle wool bouncer"),
				new Sprite("Right wool bouncer") };

		Sprite middleBouncer = new Sprite("Cat head bouncer");

		Sprite leftHardBouncer = new Sprite("Left hard bouncer");
		Sprite leftHardBouncerBouncer = new Sprite("Left hard bouncer bouncer");
		Sprite rightHardBouncer = new Sprite("Right hard bouncer");
		Sprite rightHardBouncerBouncer = new Sprite("Right hard bouncer bouncer");

		Sprite leftVerticalWall = new Sprite("Left vertical wall");
		Sprite leftBottomWall = new Sprite("Left bottom wall");
		Sprite rightVerticalWall = new Sprite("Right vertical wall");
		Sprite rightBottomWall = new Sprite("Right bottom wall");

		final String leftButtonPressed = "Left button pressed";
		final String rightButtonPressed = "Right button pressed";

		final float armMovingSpeed = 720.0f;
		float doodlydoo = 50.0f;

		// Background
		createElement(background, "background_480_800", R.drawable.background_480_800, new Vector2(), Float.NaN);
		StartScript startScript = new StartScript(ball);
		startScript.addBrick(new SetGravityBrick(ball, new Vector2(0.0f, -8.0f)));
		ball.addScript(startScript);

		// Ball
		Script ballStartScript = createElement(ball, "pinball", R.drawable.pinball, new Vector2(-200.0f, 300.0f),
				Float.NaN);
		setPhysicProperties(ball, ballStartScript, PhysicsObject.Type.DYNAMIC, 20.0f, 80.0f);

		// Ball v2
		String ballBroadcastMessage = "restart ball";
		BroadcastBrick ballBroadcastBrick = new BroadcastBrick(ball, ballBroadcastMessage);
		ballStartScript.addBrick(ballBroadcastBrick);
		ball.addScript(ballStartScript);

		BroadcastScript ballBroadcastScript = new BroadcastScript(ball, ballBroadcastMessage);
		ballBroadcastScript.addBrick(new PlaceAtBrick(ball, -200, 300));
		ballBroadcastScript.addBrick(new SetVelocityBrick(ball, new Vector2()));
		SetLookBrick ballSetLookBrick = new SetLookBrick(ball);
		ballSetLookBrick.setLook(ball.getLookDataList().get(0));
		ballBroadcastScript.addBrick(ballSetLookBrick);
		ball.addScript(ballBroadcastScript);

		// Buttons
		createElement(leftButton, "button", R.drawable.button, new Vector2(-175.0f, -330.0f), Float.NaN);
		createButtonPressed(leftButton, leftButtonPressed);
		createElement(rightButton, "button", R.drawable.button, new Vector2(175.0f, -330.0f), Float.NaN);
		createButtonPressed(rightButton, rightButtonPressed);

		// Arms
		Script leftArmStartScript = createElement(leftArm, "left_arm", R.drawable.left_arm,
				new Vector2(-80.0f, -315.0f), Float.NaN);
		setPhysicProperties(leftArm, leftArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(leftArm, leftButtonPressed, armMovingSpeed);
		Script rightArmStartScript = createElement(rightArm, "right_arm", R.drawable.right_arm, new Vector2(80.0f,
				-315.0f), Float.NaN);
		setPhysicProperties(rightArm, rightArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(rightArm, rightButtonPressed, -armMovingSpeed);

		// Lower walls
		Script leftVerticalWallStartScript = createElement(leftVerticalWall, "vertical_wall", R.drawable.vertical_wall,
				new Vector2(-232.0f, -160.0f), 8.0f);
		setPhysicProperties(leftVerticalWall, leftVerticalWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		Script rightVerticalWallStartScript = createElement(rightVerticalWall, "vertical_wall",
				R.drawable.vertical_wall, new Vector2(232.0f, -160.0f), -8.0f);
		setPhysicProperties(rightVerticalWall, rightVerticalWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);

		Script leftBottomWallStartScript = createElement(leftBottomWall, "wall_bottom", R.drawable.wall_bottom,
				new Vector2(-155.0f, -255.0f), 58.5f);
		setPhysicProperties(leftBottomWall, leftBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		Script rightBottomWallStartScript = createElement(rightBottomWall, "wall_bottom", R.drawable.wall_bottom,
				new Vector2(155.0f, -255.0f), -58.5f);
		setPhysicProperties(rightBottomWall, rightBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);

		// Hard Bouncer
		Script leftHardBouncerStartScript = createElement(leftHardBouncer, "left_hard_bouncer",
				R.drawable.left_hard_bouncer, new Vector2(-140.0f, -165.0f), Float.NaN);
		setPhysicProperties(leftHardBouncer, leftHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);
		Script leftHardBouncerBouncerStartScript = createElement(leftHardBouncerBouncer, "left_light_bouncer",
				R.drawable.left_light_bouncer, new Vector2(-129.0f, -163.0f), Float.NaN);
		setPhysicProperties(leftHardBouncerBouncer, leftHardBouncerBouncerStartScript, PhysicsObject.Type.FIXED,
				124.0f, -1.0f);

		Script rightHardBouncerStartScript = createElement(rightHardBouncer, "right_hard_bouncer",
				R.drawable.right_hard_bouncer, new Vector2(140.0f, -165.0f), Float.NaN);
		setPhysicProperties(rightHardBouncer, rightHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);
		Script rightHardBouncerBouncerStartScript = createElement(rightHardBouncerBouncer, "right_light_bouncer",
				R.drawable.right_light_bouncer, new Vector2(129.0f, -163.0f), Float.NaN);
		setPhysicProperties(rightHardBouncerBouncer, rightHardBouncerBouncerStartScript, PhysicsObject.Type.FIXED,
				124.0f, -1.0f);

		// Lower wool bouncers
		Vector2[] lowerBouncersPositions = { new Vector2(-100.0f, -80.0f + doodlydoo),
				new Vector2(0.0f, -140.0f + doodlydoo), new Vector2(100.0f, -80.0f + doodlydoo) };
		for (int index = 0; index < lowerBouncers.length; index++) {
			Script lowerBouncerStartScript = createElement(lowerBouncers[index], "wolle_bouncer",
					R.drawable.wolle_bouncer, lowerBouncersPositions[index], new Random().nextInt(360));
			setPhysicProperties(lowerBouncers[index], lowerBouncerStartScript, PhysicsObject.Type.FIXED, 116.0f, -1.0f);
		}

		// Middle bouncer
		Script middleBouncerStartScript = createElement(middleBouncer, "lego", R.drawable.lego, new Vector2(0.0f,
				75.0f + doodlydoo), Float.NaN);
		setPhysicProperties(middleBouncer, middleBouncerStartScript, PhysicsObject.Type.FIXED, 40.0f, 80.0f);
		middleBouncerStartScript.addBrick(new TurnLeftSpeedBrick(middleBouncer, 145));

		WhenScript whenPressedScript = new WhenScript(middleBouncer);
		whenPressedScript.setAction(0);

		BroadcastBrick bb = new BroadcastBrick(middleBouncer, ballBroadcastMessage);
		whenPressedScript.addBrick(bb);
		whenPressedScript.addBrick(new ChangeSizeByNBrick(middleBouncer, 20));
		middleBouncer.addScript(whenPressedScript);

		// Upper bouncers
		Vector2[] upperBouncersPositions = { new Vector2(0.0f, 240.f + doodlydoo),
				new Vector2(150.0f, 200.0f + doodlydoo) };
		for (int index = 0; index < upperBouncers.length; index++) {
			Script upperBouncersStartScript = createElement(upperBouncers[index], "cat_bouncer",
					R.drawable.cat_bouncer, upperBouncersPositions[index], Float.NaN);
			setPhysicProperties(upperBouncers[index], upperBouncersStartScript, PhysicsObject.Type.FIXED, 106.0f, -1.0f);
		}

		defaultProject.addSprite(leftButton);
		defaultProject.addSprite(rightButton);
		defaultProject.addSprite(ball);
		defaultProject.addSprite(leftArm);
		defaultProject.addSprite(rightArm);
		defaultProject.addSprite(middleBouncer);
		defaultProject.addSprite(leftHardBouncerBouncer);
		defaultProject.addSprite(leftHardBouncer);
		defaultProject.addSprite(rightHardBouncerBouncer);
		defaultProject.addSprite(rightHardBouncer);
		defaultProject.addSprite(leftVerticalWall);
		defaultProject.addSprite(leftBottomWall);
		defaultProject.addSprite(rightVerticalWall);
		defaultProject.addSprite(rightBottomWall);

		for (Sprite sprite : upperBouncers) {
			defaultProject.addSprite(sprite);
		}

		for (Sprite sprite : lowerBouncers) {
			defaultProject.addSprite(sprite);
		}

		return defaultProject;
	}

	private static Script createElement(Sprite sprite, String fileName, int fileId, Vector2 position, float angle)
			throws IOException {
		File file = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, fileName, fileId, context);
		LookData lookData = new LookData();
		lookData.setLookName(fileName);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		SetLookBrick lookBrick = new SetLookBrick(sprite);
		lookBrick.setLook(lookData);

		Script startScript = new StartScript(sprite);
		startScript.addBrick(new PlaceAtBrick(sprite, (int) position.x, (int) position.y));
		startScript.addBrick(lookBrick);

		if (!Float.isNaN(angle)) {
			TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, -angle + Look.getDegreeUserInterfaceOffset());
			startScript.addBrick(turnLeftBrick);
		}

		sprite.addScript(startScript);
		return startScript;
	}

	private static File copyFromResourceInProject(String projectName, String directoryName, String outputName,
			int fileId, Context context) throws IOException {
		return copyFromResourceInProject(projectName, directoryName, outputName, fileId, context, true);
	}

	private static File copyFromResourceInProject(String projectName, String directoryName, String outputName,
			int fileId, Context context, boolean prependMd5) throws IOException {
		final String filePath = Utils.buildPath(Utils.buildProjectPath(projectName), directoryName, outputName);
		File copiedFile = new File(filePath);
		if (!copiedFile.exists()) {
			copiedFile.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(copiedFile), Constants.BUFFER_8K);
		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		if (!prependMd5) {
			return copiedFile;
		}

		String directoryPath = Utils.buildPath(Utils.buildProjectPath(projectName), directoryName);
		String finalImageFileString = Utils.buildPath(directoryPath, Utils.md5Checksum(copiedFile) + FILENAME_SEPARATOR
				+ copiedFile.getName());
		File copiedFileWithMd5 = new File(finalImageFileString);
		copiedFile.renameTo(copiedFileWithMd5);

		return copiedFileWithMd5;
	}

	private static Script setPhysicProperties(Sprite sprite, Script startScript, PhysicsObject.Type type, float bounce,
			float friction) {
		if (startScript == null) {
			startScript = new StartScript(sprite);
		}

		startScript.addBrick(new SetPhysicsObjectTypeBrick(sprite, type));

		if (bounce >= 0.0f) {
			startScript.addBrick(new SetBounceBrick(sprite, bounce));
		}

		if (friction >= 0.0f) {
			startScript.addBrick(new SetFrictionBrick(sprite, friction));
		}

		sprite.addScript(startScript);
		return startScript;
	}

	private static void createButtonPressed(Sprite sprite, String broadcastMessage) throws IOException {
		MessageContainer.addMessage(broadcastMessage);

		WhenScript whenPressedScript = new WhenScript(sprite);
		whenPressedScript.setAction(0);

		BroadcastBrick leftButtonBroadcastBrick = new BroadcastBrick(sprite, broadcastMessage);

		String filename = "button_pressed";
		File file = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, filename,
				R.drawable.button_pressed, context);
		LookData lookData = new LookData();
		lookData.setLookName(filename);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		SetLookBrick lookBrick = new SetLookBrick(sprite);
		lookBrick.setLook(lookData);

		WaitBrick waitBrick = new WaitBrick(sprite, 500);

		SetLookBrick lookBack = new SetLookBrick(sprite);
		lookBack.setLook(looks.get(0));

		whenPressedScript.addBrick(leftButtonBroadcastBrick);
		whenPressedScript.addBrick(lookBrick);
		whenPressedScript.addBrick(waitBrick);
		whenPressedScript.addBrick(lookBack);
		sprite.addScript(whenPressedScript);
	}

	private static void createMovingArm(Sprite sprite, String broadcastMessage, float degreeSpeed) {
		BroadcastScript broadcastScript = new BroadcastScript(sprite, broadcastMessage);

		int waitInMillis = 110;

		broadcastScript.addBrick(new TurnLeftSpeedBrick(sprite, degreeSpeed));
		broadcastScript.addBrick(new WaitBrick(sprite, waitInMillis));

		broadcastScript.addBrick(new TurnLeftSpeedBrick(sprite, 0));
		broadcastScript.addBrick(new PointInDirectionBrick(sprite, Direction.UP));

		sprite.addScript(broadcastScript);
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
