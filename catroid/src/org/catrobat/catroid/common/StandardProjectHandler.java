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
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.conditional.SetLookBrick;
import org.catrobat.catroid.content.bricks.conditional.TurnLeftBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physic.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physic.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physic.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physic.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physic.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public class StandardProjectHandler {

	private static double backgroundImageScaleFactor = 1;
	private static final String FILENAME_SEPARATOR = "_";

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createAndSaveStandardPhysicProject(projectName, context);
	}

	// XXX: Only needed for pinball game and demonstration purposes. 
	private static String projectName;
	private static Context context;

	public static Project createAndSaveStandardPhysicProject(String projectName, Context context) throws IOException {
		StandardProjectHandler.context = context;
		StandardProjectHandler.projectName = projectName;

		Project defaultProject = new Project(context, projectName);
		defaultProject.getXmlHeader().virtualScreenWidth = 480;
		defaultProject.getXmlHeader().virtualScreenHeight = 800;
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		Sprite background = defaultProject.getSpriteList().get(0);

		Sprite ball = new Sprite("Ball");
		Sprite ball2 = new Sprite("Ball2");
		Sprite ball3 = new Sprite("Ball3");

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

		// Ball
		StartScript startScript = new StartScript(ball);
		startScript.addBrick(new SetGravityBrick(ball, new Vector2(0.0f, -8.0f)));
		ball.addScript(startScript);

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

		// Ball2
		StartScript startScript2 = new StartScript(ball2);
		startScript2.addBrick(new SetGravityBrick(ball2, new Vector2(0.0f, -8.0f)));
		ball2.addScript(startScript2);

		Script ballStartScript2 = createElement(ball2, "pinball", R.drawable.pinball, new Vector2(-200.0f, 300.0f),
				Float.NaN);
		setPhysicProperties(ball2, ballStartScript2, PhysicsObject.Type.DYNAMIC, 20.0f, 80.0f);

		// Ball v2
		BroadcastBrick ballBroadcastBrick2 = new BroadcastBrick(ball2, ballBroadcastMessage);
		ballStartScript2.addBrick(ballBroadcastBrick2);
		ball2.addScript(ballStartScript2);

		BroadcastScript ballBroadcastScript2 = new BroadcastScript(ball2, ballBroadcastMessage);
		ballBroadcastScript2.addBrick(new PlaceAtBrick(ball2, -100, 300));
		ballBroadcastScript2.addBrick(new SetVelocityBrick(ball2, new Vector2()));
		SetLookBrick ballSetLookBrick2 = new SetLookBrick(ball2);
		ballSetLookBrick2.setLook(ball2.getLookDataList().get(0));
		ballBroadcastScript2.addBrick(ballSetLookBrick);
		ball2.addScript(ballBroadcastScript2);

		// Ball3
		StartScript startScript3 = new StartScript(ball3);
		startScript3.addBrick(new SetGravityBrick(ball3, new Vector2(0.0f, -8.0f)));
		ball3.addScript(startScript3);

		Script ballStartScript3 = createElement(ball3, "pinball", R.drawable.pinball, new Vector2(-200.0f, 300.0f),
				Float.NaN);
		setPhysicProperties(ball3, ballStartScript3, PhysicsObject.Type.DYNAMIC, 20.0f, 80.0f);

		// ball3 v2
		BroadcastBrick ballBroadcastBrick3 = new BroadcastBrick(ball3, ballBroadcastMessage);
		ballStartScript3.addBrick(ballBroadcastBrick3);
		ball3.addScript(ballStartScript);

		BroadcastScript ballBroadcastScript3 = new BroadcastScript(ball3, ballBroadcastMessage);
		ballBroadcastScript3.addBrick(new PlaceAtBrick(ball3, 0, 300));
		ballBroadcastScript3.addBrick(new SetVelocityBrick(ball3, new Vector2()));
		SetLookBrick ballSetLookBrick3 = new SetLookBrick(ball3);
		ballSetLookBrick3.setLook(ball3.getLookDataList().get(0));
		ballBroadcastScript3.addBrick(ballSetLookBrick3);
		ball3.addScript(ballBroadcastScript3);

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
		defaultProject.addSprite(ball2);
		defaultProject.addSprite(ball3);
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
