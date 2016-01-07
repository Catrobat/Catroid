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
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.drone.DroneBrickFactory;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
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

		String birdLookName = context.getString(R.string.default_project_sprites_bird_name);
		String birdWingUpLookName = context.getString(R.string.default_project_sprites_bird_name_wing_up);
		String birdWingDownLookName = context.getString(R.string.default_project_sprites_bird_name_wing_down);
		String backgroundName = context.getString(R.string.default_project_backgroundname);
		String tweet1 = context.getString(R.string.default_project_sprites_tweet_1);
		String tweet2 = context.getString(R.string.default_project_sprites_tweet_2);

		Project defaultProject = new Project(context, projectName);
		defaultProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor);
		File birdWingUpFile = UtilFile.copyImageFromResourceIntoProject(projectName, birdWingUpLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_bird_wing_up, context, true,
				backgroundImageScaleFactor);
		File birdWingDownFile = UtilFile.copyImageFromResourceIntoProject(projectName, birdWingDownLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_bird_wing_down, context, true,
				backgroundImageScaleFactor);
		try {
			File soundFile1 = UtilFile.copySoundFromResourceIntoProject(projectName, tweet1
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_tweet_1, context, true);
			File soundFile2 = UtilFile.copySoundFromResourceIntoProject(projectName, tweet2
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_tweet_2, context, true);
			UtilFile.copyFromResourceIntoProject(projectName, ".", StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME,
					R.drawable.default_project_screenshot, context, false);

			Log.i(TAG, String.format("createAndSaveStandardProject(%s) %s created%n %s created%n %s created%n %s created%n %s",
					projectName, backgroundFile.getName(), birdWingUpFile.getName(), birdWingDownFile.getName(),
					soundFile1.getName(), soundFile2.getName()));
			LookData birdWingUpLookData = new LookData();
			birdWingUpLookData.setLookName(birdWingUpLookName);
			birdWingUpLookData.setLookFilename(birdWingUpFile.getName());

			LookData birdWingDownLookData = new LookData();
			birdWingDownLookData.setLookName(birdWingDownLookName);
			birdWingDownLookData.setLookFilename(birdWingDownFile.getName());

			LookData backgroundLookData = new LookData();
			backgroundLookData.setLookName(backgroundName);
			backgroundLookData.setLookFilename(backgroundFile.getName());

			SoundInfo soundInfo1 = new SoundInfo();
			soundInfo1.setTitle(tweet1);
			soundInfo1.setSoundFileName(soundFile1.getName());

			SoundInfo soundInfo2 = new SoundInfo();
			soundInfo2.setTitle(tweet2);
			soundInfo2.setSoundFileName(soundFile2.getName());

			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundInfo1.getChecksum(), soundInfo1.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());

			Sprite backgroundSprite = defaultProject.getSpriteList().get(0);
			backgroundSprite.getLookDataList().add(backgroundLookData);
			Script backgroundStartScript = new StartScript();

			SetLookBrick setLookBrick = new SetLookBrick();
			setLookBrick.setLook(backgroundLookData);
			backgroundStartScript.addBrick(setLookBrick);
			SetSizeToBrick setSizeToBrick = new SetSizeToBrick(200);
			backgroundStartScript.addBrick(setSizeToBrick);
			PlaceAtBrick placeAtBrick = new PlaceAtBrick((int) (1200*backgroundImageScaleFactor), 0);
			PlaceAtBrick placeAtBrick1 = new PlaceAtBrick((int) (1200*backgroundImageScaleFactor), 0);
			backgroundStartScript.addBrick(placeAtBrick);
			ForeverBrick foreverBrick = new ForeverBrick();
			backgroundStartScript.addBrick(foreverBrick);
			GlideToBrick glideToBrick = new GlideToBrick(-(int) (1299*backgroundImageScaleFactor), 0, 5000);
			backgroundStartScript.addBrick(glideToBrick);
			backgroundStartScript.addBrick(placeAtBrick1);
			LoopEndlessBrick loopEndlessBrick = new LoopEndlessBrick(foreverBrick);
			backgroundStartScript.addBrick(loopEndlessBrick);
			backgroundSprite.addScript(backgroundStartScript);

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

			FormulaElement randomElement = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(), null);
			randomElement.setLeftChild(new FormulaElement(ElementType.NUMBER, "-300", randomElement));
			randomElement.setRightChild(new FormulaElement(ElementType.NUMBER, "300", randomElement));
			Formula randomGlide1 = new Formula(randomElement);
			FormulaElement randomElement2 = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(), null);
			randomElement2.setLeftChild(new FormulaElement(ElementType.NUMBER, "-200", randomElement));
			randomElement2.setRightChild(new FormulaElement(ElementType.NUMBER, "200", randomElement));
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
			defaultProject.addSprite(birdSprite);

			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(birdWingUpLookData.getChecksum(), birdWingUpLookData.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(birdWingDownLookData.getChecksum(), birdWingDownLookData.getAbsolutePath());
			ProjectManager.getInstance().getFileChecksumContainer().addChecksum(backgroundLookData.getChecksum(), backgroundLookData.getAbsolutePath());

			StorageHandler.getInstance().fillChecksumContainer();
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
