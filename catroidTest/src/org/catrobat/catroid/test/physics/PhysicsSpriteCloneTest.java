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
package org.catrobat.catroid.test.physics;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.io.IOException;

public class PhysicsSpriteCloneTest extends InstrumentationTestCase {

	private static final String TAG = PhysicsSpriteCloneTest.class.getSimpleName();

	private Sprite sprite;
	private Project project;
	private static final int RECTANGLE125X125_RES_ID = org.catrobat.catroid.test.R.raw.rectangle_125x125;
	private static final String COLLISION_RECEIVER_TEST_MESSAGE = "Collision_receiver_test_message";
	private static final float BOUNCE_TEST_VALUE = 0.5f;
	private static final float FRICTION_TEST_VALUE = 0.5f;
	private static final Vector2 GRAVITY_TEST_VALUE = new Vector2(10.0f, 10.0f);
	private static final float MASS_TEST_VALUE = 5.0f;
	private static final PhysicsObject.Type TYPE_TEST_VALUE = PhysicsObject.Type.DYNAMIC;
	private static final Vector2 VELOCITY_TEST_VALUE = new Vector2(15.0f, 15.0f);
	private static final float TURN_LEFT_SPEED_TEST_VALUE = 2.0f;
	private static final float TURN_RIGHT_SPEED_TEST_VALUE = 3.0f;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		sprite = new Sprite("TestSprite");
		project.addSprite(sprite);
	}

	@Override
	protected void tearDown() throws Exception {
		sprite = null;
		project = null;

		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	private void checkIfScriptsAndBricksClassesOfSpriteAreEqual(Sprite sprite, Sprite clonedSprite) {
		int scriptCount = clonedSprite.getNumberOfScripts();
		for (int scriptIndex = 0; scriptIndex < scriptCount; scriptIndex++) {
			Script script = sprite.getScript(scriptIndex);
			Script clonedScript = clonedSprite.getScript(scriptIndex);
			assertEquals("Cloned script class not equal to origin.", script.getClass().toString(), clonedScript.getClass().toString());
			ScriptBrick scriptBrick = sprite.getScript(scriptIndex).getScriptBrick();
			ScriptBrick clonedScriptBrick = clonedSprite.getScript(scriptIndex).getScriptBrick();
			assertEquals("Cloned script brick class not equal to origin.", scriptBrick.getClass().toString(), clonedScriptBrick.getClass().toString());
			int brickCount = clonedSprite.getScript(scriptIndex).getBrickList().size();
			for (int brickIndex = 0; brickIndex < brickCount; brickIndex++) {
				Brick brick = sprite.getScript(scriptIndex).getBrickList().get(brickIndex);
				Brick clonedBrick = clonedSprite.getScript(scriptIndex).getBrickList().get(brickIndex);
				assertEquals("Cloned brick class not equal to origin.", brick.getClass().toString(), clonedBrick.getClass().toString());
			}
		}
	}

	public void testSpriteCloneWithPhysicsScriptAndBricks() {
		CollisionScript collisionScript = new CollisionScript(COLLISION_RECEIVER_TEST_MESSAGE);
		collisionScript.getScriptBrick();
		Brick setBounceBrick = new SetBounceBrick(BOUNCE_TEST_VALUE);
		Brick setFrictionBrick = new SetFrictionBrick(FRICTION_TEST_VALUE);
		Brick setGravityBrick = new SetGravityBrick(GRAVITY_TEST_VALUE);
		Brick setMassBrick = new SetMassBrick(MASS_TEST_VALUE);
		Brick setPhysicsObjectTypeBrick = new SetPhysicsObjectTypeBrick(TYPE_TEST_VALUE);
		Brick setVelocityBrick = new SetVelocityBrick(VELOCITY_TEST_VALUE);
		Brick turnLeftSpeedBrick = new TurnLeftSpeedBrick(TURN_LEFT_SPEED_TEST_VALUE);
		Brick turnRightSpeedBrick = new TurnRightSpeedBrick(TURN_RIGHT_SPEED_TEST_VALUE);

		collisionScript.addBrick(setBounceBrick);
		collisionScript.addBrick(setFrictionBrick);
		collisionScript.addBrick(setGravityBrick);
		collisionScript.addBrick(setMassBrick);
		collisionScript.addBrick(setPhysicsObjectTypeBrick);
		collisionScript.addBrick(setVelocityBrick);
		collisionScript.addBrick(turnLeftSpeedBrick);
		collisionScript.addBrick(turnRightSpeedBrick);

		sprite.addScript(collisionScript);

		Sprite clonedSprite = sprite.clone();

		checkIfScriptsAndBricksClassesOfSpriteAreEqual(sprite, clonedSprite);
	}

	public void testSpriteCloneWithCollisionScript() {
		CollisionScript collisionScript = new CollisionScript(COLLISION_RECEIVER_TEST_MESSAGE);
		collisionScript.getScriptBrick();
		Brick setBounceBrick = new SetBounceBrick(BOUNCE_TEST_VALUE);

		collisionScript.addBrick(setBounceBrick);
		sprite.addScript(collisionScript);

		Sprite clonedSprite = sprite.clone();

		checkIfScriptsAndBricksClassesOfSpriteAreEqual(sprite, clonedSprite);

		Script clonedScript = clonedSprite.getScript(0);
		assertTrue("Cloned script has wrong class.", clonedScript instanceof CollisionScript);
		ScriptBrick clonedScriptBrick = clonedScript.getScriptBrick();
		assertTrue("Cloned script brick has wrong class.", clonedScriptBrick instanceof CollisionReceiverBrick);
		String clonedBroadcastMessage = ((CollisionReceiverBrick) clonedScriptBrick).getBroadcastMessage();
		assertEquals("Cloned broadcast message is not equal to origin message.", COLLISION_RECEIVER_TEST_MESSAGE, clonedBroadcastMessage);

		Brick clonedSetBounceBrick = clonedScript.getBrickList().get(0);
		assertTrue("Cloned brick has wrong class.", clonedSetBounceBrick instanceof SetBounceBrick);

		Formula clonedSetBounceBrickFormula = ((FormulaBrick) clonedSetBounceBrick)
				.getFormulaWithBrickField(Brick.BrickField.PHYSICS_BOUNCE_FACTOR);
		float clonedBounceFactorValue = 0;
		try {
			clonedBounceFactorValue = clonedSetBounceBrickFormula.interpretFloat(clonedSprite);
		} catch (InterpretationException interpretationException) {
			Log.e(TAG, "InterpretationException thrown while interpreting.", interpretationException);
			fail("InterpretationException thrown while interpreting.");
		}
		assertEquals("Cloned bounce factor value is not equal to origin value.", BOUNCE_TEST_VALUE, clonedBounceFactorValue);
	}

	public void testSpriteClonePhysicsLookAndPhysicsObject() {
		WhenStartedBrick brick = new WhenStartedBrick();
		StartScript startScript = new StartScript(brick);
		Brick setPhysicsObjectTypeBrick = new SetPhysicsObjectTypeBrick(TYPE_TEST_VALUE);

		startScript.addBrick(setPhysicsObjectTypeBrick);
		sprite.addScript(startScript);

		PhysicsWorld physicsWorld = project.getPhysicsWorld();
		sprite.look = new Look(sprite);

		String rectangle125x125FileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("rectangle_125x125.png");
		File rectangle125x125File = null;
		LookData lookdata;
		try {
			rectangle125x125File = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
					rectangle125x125FileName, RECTANGLE125X125_RES_ID, getInstrumentation().getContext(),
					TestUtils.TYPE_IMAGE_FILE);
			lookdata = PhysicsTestUtils.generateLookData(rectangle125x125File);
			sprite.look.setLookData(lookdata);
		} catch (IOException e) {
			Log.e(TAG, "IOException caught", e);
		}
		assertNotNull("File must not be null.", rectangle125x125File);
		assertNotNull("Lookdata must not be null.", sprite.look.getLookData());

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);

		Sprite clonedSprite = sprite.clone();

		assertTrue("Look of cloned sprite is no look.", clonedSprite.look instanceof Look);

		PhysicsObject clonedPhysicsObject = physicsWorld.getPhysicsObject(clonedSprite);
		assertEquals("Cloned Physics Object must be equal.", physicsObject.getType(), clonedPhysicsObject.getType());
		clonedPhysicsObject.setType(PhysicsObject.Type.DYNAMIC);
		assertNotSame("Cloned Physics Object value must be different.", physicsObject.getType(), clonedPhysicsObject.getType());
	}
}
