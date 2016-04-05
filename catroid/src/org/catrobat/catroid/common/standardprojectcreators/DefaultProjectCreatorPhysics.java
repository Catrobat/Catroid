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

package org.catrobat.catroid.common.standardprojectcreators;

import android.content.Context;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.HideBrick;
import org.catrobat.catroid.content.bricks.conditional.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.conditional.SetLookBrick;
import org.catrobat.catroid.content.bricks.conditional.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.conditional.ShowBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultProjectCreatorPhysics extends DefaultProjectCreator {

	private Vector2 backgroundImageScaleVector;

	public DefaultProjectCreatorPhysics() {
		standardProjectNameID = R.string.default_project_name_physics;
	}

	@Override
	public Project createDefaultProject(String projectName, Context context, boolean landscapeMode) throws IOException,
			IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		String backgroundName = context.getString(R.string.default_project_background_name);

		Project defaultPhysicsProject = new Project(context, projectName);
		defaultPhysicsProject.setDeviceData(context);
		StorageHandler.getInstance().saveProject(defaultPhysicsProject);
		ProjectManager.getInstance().setProject(defaultPhysicsProject);

		backgroundImageScaleVector = ImageEditing.calculateScaleFactorsToScreenSize(
				R.drawable.physics_background_480_800, context);
		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.physics_background_480_800, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
							+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.physics_background_480_800, context, true,
					backgroundImageScaleFactor);

		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(backgroundName);
		backgroundLookData.setLookFilename(backgroundFile.getName());

		// Background sprite
		Sprite backgroundSprite = defaultPhysicsProject.getSpriteList().get(0);
		backgroundSprite.getLookDataList().add(backgroundLookData);

		Sprite ball = new Sprite("Ball");
		Sprite leftButton = new Sprite("Left button");
		Sprite rightButton = new Sprite("Right button");
		Sprite leftArm = new Sprite("Left arm");
		Sprite rightArm = new Sprite("Right arm");

		Sprite[] upperBouncers = { new Sprite("Left cat bouncer"), new Sprite("Middle cat bouncer"),
				new Sprite("Right cat bouncer") };

		Sprite[] lowerBouncers = { new Sprite("Left circle bouncer"), new Sprite("Middle circle bouncer"),
				new Sprite("Right circle bouncer") };

		Sprite middleBouncer = new Sprite("Cat head bouncer");
		Sprite leftHardBouncer = new Sprite("Left hard bouncer");
		Sprite rightHardBouncer = new Sprite("Right hard bouncer");

		Sprite leftBottomWall = new Sprite("Left bottom wall");
		Sprite rightBottomWall = new Sprite("Right bottom wall");

		String restartName = "Restart Game";
		Sprite restart = new Sprite(restartName);

		final String leftButtonPressed = "Left button pressed";
		final String rightButtonPressed = "Right button pressed";

		final float armMovingSpeed = 500.0f;

		// Ball
		StartScript startScript = new StartScript();
		startScript.addBrick(new SetGravityBrick(new Vector2(0.0f, -35.0f * backgroundImageScaleVector.y)));
		ball.addScript(startScript);

		Script ballStartScript = createElement(context, projectName, ball, "physics_pinball",
				R.drawable.physics_pinball, new Vector2(0.0f, 380.0f),
				Float.NaN);
		setPhysicsProperties(ball, ballStartScript, PhysicsObject.Type.DYNAMIC, 60.0f, 40.0f);

		Brick foreverBrick = new ForeverBrick();
		Brick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick();
		Brick foreverEndBrick = new LoopEndBrick();

		ballStartScript.addBrick(foreverBrick);
		ballStartScript.addBrick(ifOnEdgeBounceBrick);
		ballStartScript.addBrick(foreverEndBrick);

		Script receiveResetBallScript = new BroadcastScript("reset_ball");
		receiveResetBallScript.addBrick(new HideBrick());
		ball.addScript(receiveResetBallScript);

		Script receiveStartBallScript = new BroadcastScript("start_ball");
		receiveStartBallScript.addBrick(new PlaceAtBrick(new Formula(0),
				new Formula(380.0f * backgroundImageScaleVector.y)));
		receiveStartBallScript.addBrick(new ShowBrick());
		ball.addScript(receiveStartBallScript);

		// Restart View
		Script startScriptRestart = createElement(context, projectName, restart, "physics_restart",
				R.drawable.physics_restart, new Vector2(0.0f, -490.0f), Float.NaN);
		setPhysicsProperties(restart, startScriptRestart, PhysicsObject.Type.FIXED, 60.0f, 40.0f);
		startScriptRestart.addBrick(new ComeToFrontBrick());

		Script physicsCollisionScript = new CollisionScript(restartName + PhysicsCollision.COLLISION_MESSAGE_CONNECTOR + "Ball");
		physicsCollisionScript.addBrick(new BroadcastBrick("reset_ball"));
		physicsCollisionScript.addBrick(new PlaceAtBrick(0, 0));
		restart.addScript(physicsCollisionScript);

		Script tapOnRestartScript = new WhenScript();
		tapOnRestartScript.addBrick(new BroadcastBrick("start_ball"));
		tapOnRestartScript.addBrick(new PlaceAtBrick(new Formula(0), new Formula(-490 * backgroundImageScaleVector.y)));
		restart.addScript(tapOnRestartScript);

		// Buttons
		createElement(context, projectName, leftButton, "physics_button", R.drawable.physics_button, new Vector2(-180.0f, -325.0f), Float.NaN, 70f);
		createButtonPressed(context, projectName, leftButton, leftButtonPressed);
		createElement(context, projectName, rightButton, "physics_button", R.drawable.physics_button, new Vector2(180.0f, -325.0f), Float.NaN, 70f);
		createButtonPressed(context, projectName, rightButton, rightButtonPressed);

		// Arms
		Script leftArmStartScript = createElement(context, projectName, leftArm, "physics_left_arm", R.drawable.physics_left_arm,
				new Vector2(-102.0f, -285f), Float.NaN);
		setPhysicsProperties(leftArm, leftArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(leftArm, leftButtonPressed, armMovingSpeed);
		Script rightArmStartScript = createElement(context, projectName, rightArm, "physics_right_arm", R.drawable.physics_right_arm, new Vector2(102.0f,
				-285.0f), Float.NaN);
		setPhysicsProperties(rightArm, rightArmStartScript, PhysicsObject.Type.FIXED, 50.0f, -1.0f);
		createMovingArm(rightArm, rightButtonPressed, -armMovingSpeed);

		Script leftBottomWallStartScript = createElement(context, projectName, leftBottomWall, "physics_wall_left", R.drawable.physics_wall_left,
				new Vector2(-180.0f, -220.0f), Float.NaN, 65f);
		setPhysicsProperties(leftBottomWall, leftBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);
		Script rightBottomWallStartScript = createElement(context, projectName, rightBottomWall, "physics_wall_right", R.drawable.physics_wall_right,
				new Vector2(180.0f, -220.0f), Float.NaN, 65f);
		setPhysicsProperties(rightBottomWall, rightBottomWallStartScript, PhysicsObject.Type.FIXED, 5.0f, -1.0f);

		// Hard Bouncer
		Script leftHardBouncerStartScript = createElement(context, projectName, leftHardBouncer, "physics_left_hard_bouncer",
				R.drawable.physics_left_hard_bouncer, new Vector2(-140.0f, -130.0f), Float.NaN);
		setPhysicsProperties(leftHardBouncer, leftHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);

		Script rightHardBouncerStartScript = createElement(context, projectName, rightHardBouncer, "physics_right_hard_bouncer",
				R.drawable.physics_right_hard_bouncer, new Vector2(140.0f, -130.0f), Float.NaN);
		setPhysicsProperties(rightHardBouncer, rightHardBouncerStartScript, PhysicsObject.Type.FIXED, 10.0f, -1.0f);

		// Lower circle bouncers
		Vector2[] lowerBouncersPositions = { new Vector2(-100.0f, 0.0f),
				new Vector2(0.0f, -70.0f), new Vector2(100.0f, 0.0f) };
		for (int index = 0; index < lowerBouncers.length; index++) {
			Script lowerBouncerStartScript = createElement(context, projectName, lowerBouncers[index], "physics_bouncer_100",
					R.drawable.physics_bouncer_100, lowerBouncersPositions[index], Float.NaN, 60f);
			setPhysicsProperties(lowerBouncers[index], lowerBouncerStartScript, PhysicsObject.Type.FIXED, 116.0f, -1.0f);
		}

		// Middle bouncer
		Script middleBouncerStartScript = createElement(context, projectName, middleBouncer, "physics_square", R.drawable.physics_square, new Vector2(0.0f,
				150.0f), Float.NaN, 65f);
		setPhysicsProperties(middleBouncer, middleBouncerStartScript, PhysicsObject.Type.FIXED, 40.0f, 80.0f);
		middleBouncerStartScript.addBrick(new TurnLeftSpeedBrick(100));

		// Upper bouncers
		Vector2[] upperBouncersPositions = { new Vector2(-150.0f, 200.0f), new Vector2(0.0f, 300.f),
				new Vector2(150.0f, 200.0f) };
		for (int index = 0; index < upperBouncers.length; index++) {
			Script upperBouncersStartScript = createElement(context, projectName, upperBouncers[index], "physics_bouncer_200",
					R.drawable.physics_bouncer_200, upperBouncersPositions[index], Float.NaN, 50f);
			setPhysicsProperties(upperBouncers[index], upperBouncersStartScript, PhysicsObject.Type.FIXED, 106.0f, -1.0f);
		}

		defaultPhysicsProject.addSprite(leftButton);
		defaultPhysicsProject.addSprite(rightButton);
		defaultPhysicsProject.addSprite(ball);
		defaultPhysicsProject.addSprite(leftArm);
		defaultPhysicsProject.addSprite(rightArm);
		defaultPhysicsProject.addSprite(middleBouncer);
		defaultPhysicsProject.addSprite(leftHardBouncer);
		defaultPhysicsProject.addSprite(rightHardBouncer);
		defaultPhysicsProject.addSprite(leftBottomWall);
		defaultPhysicsProject.addSprite(rightBottomWall);
		defaultPhysicsProject.addSprite(restart);

		for (Sprite sprite : upperBouncers) {
			defaultPhysicsProject.addSprite(sprite);
		}

		for (Sprite sprite : lowerBouncers) {
			defaultPhysicsProject.addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(defaultPhysicsProject);

		return defaultPhysicsProject;
	}

	private Script createElement(Context context, String projectName, Sprite sprite, String fileName, int fileId, Vector2 position, float angle) throws IOException {
		return createElement(context, projectName, sprite, fileName, fileId, position, angle, 100.0f);
	}

	private Script createElement(Context context, String projectName, Sprite sprite, String fileName, int fileId, Vector2 position, float angle, float scale) throws IOException {
		File file = UtilFile.copyImageFromResourceIntoProject(projectName, fileName, fileId, context, true, backgroundImageScaleFactor);

		LookData lookData = new LookData();
		lookData.setLookName(fileName);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		Script startScript = new StartScript();

		startScript.addBrick(new PlaceAtBrick(new Formula(position.x * backgroundImageScaleVector.x), new Formula(position.y * backgroundImageScaleVector.y)));

		if (scale != 100f) {
			SetSizeToBrick setSizeToBrick = new SetSizeToBrick(scale);
			startScript.addBrick(setSizeToBrick);
		}

		if (!Float.isNaN(angle)) {
			PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(new Formula(angle));
			startScript.addBrick(pointInDirectionBrick);
		}

		sprite.addScript(startScript);
		return startScript;
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
		File file = UtilFile.copyImageFromResourceIntoProject(projectName, filename, R.drawable.physics_button_pressed, context, true, backgroundImageScaleFactor);
		LookData lookData = new LookData();
		lookData.setLookName(filename);
		lookData.setLookFilename(file.getName());

		List<LookData> looks = sprite.getLookDataList();
		looks.add(lookData);

		SetLookBrick lookBrick = new SetLookBrick();
		lookBrick.setLook(lookData);

		WaitBrick waitBrick = new WaitBrick(200);

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
		broadcastScript.addBrick(new PointInDirectionBrick(90.0f));

		sprite.addScript(broadcastScript);
	}
}
