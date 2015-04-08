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

package org.catrobat.catroid.common.standardprojectcreators;

import android.content.Context;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.conditional.SetLookBrick;
import org.catrobat.catroid.content.bricks.conditional.SetXBrick;
import org.catrobat.catroid.content.bricks.conditional.SetYBrick;
import org.catrobat.catroid.content.bricks.conditional.TurnLeftBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
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

public class StandardProjectCreatorPhysics extends StandardProjectCreator {

	private static final String FILENAME_SEPARATOR = "_";

	public StandardProjectCreatorPhysics() {
		standardProjectNameID = R.string.default_project_name_physics;
	}

	@Override
	public Project createStandardProject(String projectName, Context context) throws IOException,
			IllegalArgumentException {
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		Project defaultPhysicsProject = new Project(context, projectName);
		defaultPhysicsProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultPhysicsProject);
		ProjectManager.getInstance().setProject(defaultPhysicsProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.background_480_800, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor
		);

		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(backgroundName);
		backgroundLookData.setLookFilename(backgroundFile.getName());
		// Background
		Sprite background = defaultPhysicsProject.getSpriteList().get(0);

		Sprite backgroundSprite = defaultPhysicsProject.getSpriteList().get(0);

		// Background sprite
		backgroundSprite.getLookDataList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript();

		SetLookBrick setLookBrick = new SetLookBrick();
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		backgroundSprite.addScript(backgroundStartScript);

		// Square sprite
		Sprite square = new Sprite("square");

		Script squareStartScript = createElement(context, projectName, square, "square", R.drawable.square, new Vector2(0.0f, 400.0f),
				Float.NaN);
		squareStartScript.addBrick(new SetXBrick(100));
		squareStartScript.addBrick(new SetYBrick(-200));
		squareStartScript.addBrick(new PointInDirectionBrick(135.0));

		defaultPhysicsProject.addSprite(square);

		// Physics Square Sprite
		Sprite squareP = new Sprite("square");
		StartScript startScriptSquareP = new StartScript();
		startScriptSquareP.addBrick(new SetGravityBrick(new Vector2(0.0f, -1.0f)));
		squareP.addScript(startScriptSquareP);

		Script squareStartScriptP = createElement(context, projectName, squareP, "square", R.drawable.square, new Vector2(0.0f, 400.0f),
				Float.NaN);
		squareStartScriptP.addBrick(new PointInDirectionBrick(225.0));
		setPhysicsProperties(squareP, squareStartScriptP, PhysicsObject.Type.DYNAMIC, 60.0f, 40.0f);

		defaultPhysicsProject.addSprite(squareP);

		Sprite ball = new Sprite("Ball");
		Sprite ball2 = new Sprite("Ball2");
		Sprite ball3 = new Sprite("Ball3");
		Sprite leftButton = new Sprite("Left button");
		Sprite rightButton = new Sprite("Right button");
		Sprite leftArm = new Sprite("Left arm");
		Sprite rightArm = new Sprite("Right arm");

		Sprite[] upperBouncers = {new Sprite("Middle cat bouncer"), new Sprite("Right cat bouncer")};

		Sprite[] lowerBouncers = {new Sprite("Left wool bouncer"), new Sprite("Middle wool bouncer"),
				new Sprite("Right wool bouncer")};

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
		createElement(context, projectName, background, "background_480_800", R.drawable.background_480_800, new Vector2(), Float.NaN);

		// Ball
		StartScript startScript = new StartScript();
		startScript.addBrick(new SetGravityBrick(new Vector2(0.0f, -1.0f)));
		ball.addScript(startScript);

		Script ballStartScript = createElement(context, projectName, ball, "pinball", R.drawable.pinball, new Vector2(0.0f, 250.0f),
				Float.NaN);
		setPhysicsProperties(ball, ballStartScript, PhysicsObject.Type.DYNAMIC, 60.0f, 40.0f);

		//		// Ball v2
		String ballBroadcastMessage = "restart ball";
		BroadcastBrick ballBroadcastBrick = new BroadcastBrick(ballBroadcastMessage);
		ballStartScript.addBrick(ballBroadcastBrick);
		ball.addScript(ballStartScript);

		BroadcastScript ballBroadcastScript = new BroadcastScript(ballBroadcastMessage);
		ballBroadcastScript.addBrick(new PlaceAtBrick(-200, -50));
		ballBroadcastScript.addBrick(new SetVelocityBrick(new Vector2()));
		SetLookBrick ballSetLookBrick = new SetLookBrick();
		ballSetLookBrick.setLook(ball.getLookDataList().get(0));
		ballBroadcastScript.addBrick(ballSetLookBrick);
		ball.addScript(ballBroadcastScript);

		// Ball2
		StartScript startScript2 = new StartScript();
		startScript2.addBrick(new SetGravityBrick(new Vector2(0.0f, -1.0f)));
		ball2.addScript(startScript2);

		Script ballStartScript2 = createElement(context, projectName, ball2, "pinball", R.drawable.pinball, new Vector2(0.0f, 300.0f),
				Float.NaN);
		setPhysicsProperties(ball2, ballStartScript2, PhysicsObject.Type.DYNAMIC, 60.0f, 40.0f);
		//
		//		// Ball v2
		BroadcastBrick ballBroadcastBrick2 = new BroadcastBrick(ballBroadcastMessage);
		ballStartScript2.addBrick(ballBroadcastBrick2);
		ball2.addScript(ballStartScript2);
		//
		BroadcastScript ballBroadcastScript2 = new BroadcastScript(ballBroadcastMessage);
		ballBroadcastScript2.addBrick(new PlaceAtBrick(-100, 300));
		ballBroadcastScript2.addBrick(new SetVelocityBrick(new Vector2()));
		SetLookBrick ballSetLookBrick2 = new SetLookBrick();
		ballSetLookBrick2.setLook(ball2.getLookDataList().get(0));
		ballBroadcastScript2.addBrick(ballSetLookBrick);
		ball2.addScript(ballBroadcastScript2);
		//
		//		// Ball3
		StartScript startScript3 = new StartScript();
		startScript3.addBrick(new SetGravityBrick(new Vector2(0.0f, -8.0f)));
		ball3.addScript(startScript3);
		//
		Script ballStartScript3 = createElement(context, projectName, ball3, "pinball", R.drawable.pinball, new Vector2(-200.0f, 300.0f),
				Float.NaN);
		setPhysicsProperties(ball3, ballStartScript3, PhysicsObject.Type.DYNAMIC, 20.0f, 80.0f);
		//
		// ball3 v2
		BroadcastBrick ballBroadcastBrick3 = new BroadcastBrick(ballBroadcastMessage);
		ballStartScript3.addBrick(ballBroadcastBrick3);
		ball3.addScript(ballStartScript);
		//
		BroadcastScript ballBroadcastScript3 = new BroadcastScript(ballBroadcastMessage);
		ballBroadcastScript3.addBrick(new PlaceAtBrick(0, 300));
		ballBroadcastScript3.addBrick(new SetVelocityBrick(new Vector2()));
		SetLookBrick ballSetLookBrick3 = new SetLookBrick();
		ballSetLookBrick3.setLook(ball3.getLookDataList().get(0));
		ballBroadcastScript3.addBrick(ballSetLookBrick3);
		ball3.addScript(ballBroadcastScript3);
		//
		// Buttons
		createElement(context, projectName, leftButton, "button", R.drawable.button, new Vector2(-175.0f, -330.0f), Float.NaN);
		createButtonPressed(context, projectName, leftButton, leftButtonPressed);
		createElement(context, projectName, rightButton, "button", R.drawable.button, new Vector2(175.0f, -330.0f), Float.NaN);
		createButtonPressed(context, projectName, rightButton, rightButtonPressed);
		//
		//		// Arms
		Script leftArmStartScript = createElement(context, projectName, leftArm, "left_arm", R.drawable.left_arm,
				new Vector2(-80.0f, -315.0f), Float.NaN);
		setPhysicsProperties(leftArm, leftArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(leftArm, leftButtonPressed, armMovingSpeed);
		Script rightArmStartScript = createElement(context, projectName, rightArm, "right_arm", R.drawable.right_arm, new Vector2(80.0f,
				-315.0f), Float.NaN);
		setPhysicsProperties(rightArm, rightArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(rightArm, rightButtonPressed, -armMovingSpeed);
		//
		// Lower walls
		Script leftVerticalWallStartScript = createElement(context, projectName, leftVerticalWall, "vertical_wall", R.drawable.vertical_wall,
				new Vector2(-232.0f, -160.0f), 8.0f);
		setPhysicsProperties(leftVerticalWall, leftVerticalWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		Script rightVerticalWallStartScript = createElement(context, projectName, rightVerticalWall, "vertical_wall",
				R.drawable.vertical_wall, new Vector2(232.0f, -160.0f), -8.0f);
		setPhysicsProperties(rightVerticalWall, rightVerticalWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		//
		Script leftBottomWallStartScript = createElement(context, projectName, leftBottomWall, "wall_bottom", R.drawable.wall_bottom,
				new Vector2(0.0f, -100.0f), 90f);
		setPhysicsProperties(leftBottomWall, leftBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		Script rightBottomWallStartScript = createElement(context, projectName, rightBottomWall, "wall_bottom", R.drawable.wall_bottom,
				new Vector2(155.0f, -255.0f), -58.5f);
		setPhysicsProperties(rightBottomWall, rightBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		//
		//		// Hard Bouncer
		Script leftHardBouncerStartScript = createElement(context, projectName, leftHardBouncer, "left_hard_bouncer",
				R.drawable.left_hard_bouncer, new Vector2(-140.0f, -165.0f), Float.NaN);
		setPhysicsProperties(leftHardBouncer, leftHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);
		Script leftHardBouncerBouncerStartScript = createElement(context, projectName, leftHardBouncerBouncer, "left_light_bouncer",
				R.drawable.left_light_bouncer, new Vector2(-129.0f, -163.0f), Float.NaN);
		setPhysicsProperties(leftHardBouncerBouncer, leftHardBouncerBouncerStartScript, PhysicsObject.Type.FIXED,
				124.0f, -1.0f);
		//
		Script rightHardBouncerStartScript = createElement(context, projectName, rightHardBouncer, "right_hard_bouncer",
				R.drawable.right_hard_bouncer, new Vector2(140.0f, -165.0f), Float.NaN);
		setPhysicsProperties(rightHardBouncer, rightHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);
		Script rightHardBouncerBouncerStartScript = createElement(context, projectName, rightHardBouncerBouncer, "right_light_bouncer",
				R.drawable.right_light_bouncer, new Vector2(129.0f, -163.0f), Float.NaN);
		setPhysicsProperties(rightHardBouncerBouncer, rightHardBouncerBouncerStartScript, PhysicsObject.Type.FIXED,
				124.0f, -1.0f);
		//
		//		// Lower wool bouncers
		Vector2[] lowerBouncersPositions = {new Vector2(-100.0f, -80.0f + doodlydoo),
				new Vector2(0.0f, -140.0f + doodlydoo), new Vector2(100.0f, -80.0f + doodlydoo)};
		for (int index = 0; index < lowerBouncers.length; index++) {
			Script lowerBouncerStartScript = createElement(context, projectName, lowerBouncers[index], "wolle_bouncer",
					R.drawable.wolle_bouncer, lowerBouncersPositions[index], new Random().nextInt(360));
			setPhysicsProperties(lowerBouncers[index], lowerBouncerStartScript, PhysicsObject.Type.FIXED, 116.0f, -1.0f);
		}
		//
		//		// Middle bouncer
		Script middleBouncerStartScript = createElement(context, projectName, middleBouncer, "lego", R.drawable.lego, new Vector2(0.0f,
				75.0f + doodlydoo), Float.NaN);
		setPhysicsProperties(middleBouncer, middleBouncerStartScript, PhysicsObject.Type.FIXED, 40.0f, 80.0f);
		middleBouncerStartScript.addBrick(new TurnLeftSpeedBrick(145));
		//
		WhenScript whenPressedScript = new WhenScript();
		whenPressedScript.setAction(0);
		//
		BroadcastBrick bb = new BroadcastBrick(ballBroadcastMessage);
		whenPressedScript.addBrick(bb);
		whenPressedScript.addBrick(new ChangeSizeByNBrick(20));
		middleBouncer.addScript(whenPressedScript);

		// Upper bouncers
		Vector2[] upperBouncersPositions = {new Vector2(0.0f, 240.f + doodlydoo),
				new Vector2(150.0f, 200.0f + doodlydoo)};
		for (int index = 0; index < upperBouncers.length; index++) {
			Script upperBouncersStartScript = createElement(context, projectName, upperBouncers[index], "cat_bouncer",
					R.drawable.cat_bouncer, upperBouncersPositions[index], Float.NaN);
			setPhysicsProperties(upperBouncers[index], upperBouncersStartScript, PhysicsObject.Type.FIXED, 106.0f, -1.0f);
		}

		defaultPhysicsProject.addSprite(leftButton);
		defaultPhysicsProject.addSprite(rightButton);
		defaultPhysicsProject.addSprite(ball);
		defaultPhysicsProject.addSprite(ball2);
		defaultPhysicsProject.addSprite(ball3);
		defaultPhysicsProject.addSprite(leftArm);
		defaultPhysicsProject.addSprite(rightArm);
		defaultPhysicsProject.addSprite(middleBouncer);
		defaultPhysicsProject.addSprite(leftHardBouncerBouncer);
		defaultPhysicsProject.addSprite(leftHardBouncer);
		defaultPhysicsProject.addSprite(rightHardBouncerBouncer);
		defaultPhysicsProject.addSprite(rightHardBouncer);
		defaultPhysicsProject.addSprite(leftVerticalWall);
		defaultPhysicsProject.addSprite(leftBottomWall);
		defaultPhysicsProject.addSprite(rightVerticalWall);
		defaultPhysicsProject.addSprite(rightBottomWall);

		for (Sprite sprite : upperBouncers) {
			defaultPhysicsProject.addSprite(sprite);
		}

		for (Sprite sprite : lowerBouncers) {
			defaultPhysicsProject.addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(defaultPhysicsProject);

		return defaultPhysicsProject;
	}

	private Script createElement(Context context, String projectName, Sprite sprite, String fileName, int fileId, Vector2 position, float angle)
			throws IOException {
		File file = UtilFile.copyImageFromResourceIntoProject(projectName, fileName, fileId, context, true, backgroundImageScaleFactor);
		//File file = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, fileName, fileId, context);
		LookData lookData = new LookData();
		lookData.setLookName(fileName);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		SetLookBrick lookBrick = new SetLookBrick();
		lookBrick.setLook(lookData);

		Script startScript = new StartScript();
		startScript.addBrick(new PlaceAtBrick((int) position.x, (int) position.y));
		startScript.addBrick(lookBrick);

		if (!Float.isNaN(angle)) {
			TurnLeftBrick turnLeftBrick = new TurnLeftBrick(-angle + Look.getDegreeUserInterfaceOffset());
			startScript.addBrick(turnLeftBrick);
		}

		sprite.addScript(startScript);
		return startScript;
	}

	private File copyFromResourceInProject(String projectName, String directoryName, String outputName,
			int fileId, Context context) throws IOException {
		return copyFromResourceInProject(projectName, directoryName, outputName, fileId, context, true);
	}

	private File copyFromResourceInProject(String projectName, String directoryName, String outputName,
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

	private Script setPhysicsProperties(Sprite sprite, Script startScript, PhysicsObject.Type type,
			float bounce, float friction) {
		if (startScript == null) {
			startScript = new StartScript();
		}

		startScript.addBrick(new SetPhysicsObjectTypeBrick(type));

		if (bounce >= 0.0f) {
			startScript.addBrick(new SetBounceBrick(bounce));
		}

		if (friction >= 0.0f) {
			startScript.addBrick(new SetFrictionBrick(friction));
		}

		sprite.addScript(startScript);
		return startScript;
	}

	private void createButtonPressed(Context context, String projectName, Sprite sprite, String broadcastMessage) throws IOException {
		MessageContainer.addMessage(broadcastMessage);

		WhenScript whenPressedScript = new WhenScript();
		whenPressedScript.setAction(0);

		BroadcastBrick leftButtonBroadcastBrick = new BroadcastBrick(broadcastMessage);

		String filename = "button_pressed";
		File file = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, filename,
				R.drawable.button_pressed, context);
		LookData lookData = new LookData();
		lookData.setLookName(filename);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		SetLookBrick lookBrick = new SetLookBrick();
		lookBrick.setLook(lookData);

		WaitBrick waitBrick = new WaitBrick(500);

		SetLookBrick lookBack = new SetLookBrick();
		lookBack.setLook(looks.get(0));

		whenPressedScript.addBrick(leftButtonBroadcastBrick);
		whenPressedScript.addBrick(lookBrick);
		whenPressedScript.addBrick(waitBrick);
		whenPressedScript.addBrick(lookBack);
		sprite.addScript(whenPressedScript);
	}

	private void createMovingArm(Sprite sprite, String broadcastMessage, float degreeSpeed) {
		BroadcastScript broadcastScript = new BroadcastScript(broadcastMessage);

		int waitInMillis = 110;

		broadcastScript.addBrick(new TurnLeftSpeedBrick(degreeSpeed));
		broadcastScript.addBrick(new WaitBrick(waitInMillis));

		broadcastScript.addBrick(new TurnLeftSpeedBrick(0));
		broadcastScript.addBrick(new PointInDirectionBrick(PointInDirectionBrick.Direction.UP));

		sprite.addScript(broadcastScript);
	}
}
