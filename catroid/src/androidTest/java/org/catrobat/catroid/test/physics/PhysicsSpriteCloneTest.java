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
package org.catrobat.catroid.test.physics;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
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
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

@RunWith(AndroidJUnit4.class)
public class PhysicsSpriteCloneTest {

	private Sprite sprite;
	private Project project;
	private static final float BOUNCE_TEST_VALUE = 0.5f;
	private static final float FRICTION_TEST_VALUE = 0.5f;
	private static final Vector2 GRAVITY_TEST_VALUE = new Vector2(10.0f, 10.0f);
	private static final float MASS_TEST_VALUE = 5.0f;
	private static final PhysicsObject.Type TYPE_TEST_VALUE = PhysicsObject.Type.DYNAMIC;
	private static final Vector2 VELOCITY_TEST_VALUE = new Vector2(15.0f, 15.0f);
	private static final float TURN_LEFT_SPEED_TEST_VALUE = 2.0f;
	private static final float TURN_RIGHT_SPEED_TEST_VALUE = 3.0f;

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects();

		project = new Project(InstrumentationRegistry.getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		sprite = new SingleSprite("TestSprite");
		project.getDefaultScene().addSprite(sprite);
	}

	@After
	public void tearDown() throws Exception {
		sprite = null;
		project = null;

		TestUtils.deleteProjects();
	}

	private void checkIfScriptsAndBricksClassesOfSpriteAreEqual(Sprite sprite, Sprite clonedSprite) {
		int scriptCount = clonedSprite.getNumberOfScripts();
		for (int scriptIndex = 0; scriptIndex < scriptCount; scriptIndex++) {
			Script script = sprite.getScript(scriptIndex);
			Script clonedScript = clonedSprite.getScript(scriptIndex);
			assertEquals(script.getClass().toString(), clonedScript.getClass().toString());
			ScriptBrick scriptBrick = sprite.getScript(scriptIndex).getScriptBrick();
			ScriptBrick clonedScriptBrick = clonedSprite.getScript(scriptIndex).getScriptBrick();
			assertEquals(scriptBrick.getClass().toString(), clonedScriptBrick.getClass().toString());
			int brickCount = clonedSprite.getScript(scriptIndex).getBrickList().size();
			for (int brickIndex = 0; brickIndex < brickCount; brickIndex++) {
				Brick brick = sprite.getScript(scriptIndex).getBrickList().get(brickIndex);
				Brick clonedBrick = clonedSprite.getScript(scriptIndex).getBrickList().get(brickIndex);
				assertEquals(brick.getClass().toString(), clonedBrick.getClass().toString());
			}
		}
	}

	@Test
	public void testSpriteCloneWithPhysicsScriptAndBricks() throws IOException {
		CollisionScript collisionScript = new CollisionScript(null);
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

		Sprite clonedSprite = new SpriteController().copy(sprite, project.getDefaultScene(), project.getDefaultScene());

		checkIfScriptsAndBricksClassesOfSpriteAreEqual(sprite, clonedSprite);
	}

	@Test
	public void testSpriteCloneWithCollisionScript() throws IOException, InterpretationException {
		CollisionScript collisionScript = new CollisionScript(null);
		collisionScript.getScriptBrick();
		Brick setBounceBrick = new SetBounceBrick(BOUNCE_TEST_VALUE);

		collisionScript.addBrick(setBounceBrick);
		sprite.addScript(collisionScript);

		Sprite clonedSprite = new SpriteController().copy(sprite, project.getDefaultScene(), project.getDefaultScene());

		checkIfScriptsAndBricksClassesOfSpriteAreEqual(sprite, clonedSprite);

		Script clonedScript = clonedSprite.getScript(0);
		assertTrue(clonedScript instanceof CollisionScript);
		ScriptBrick clonedScriptBrick = clonedScript.getScriptBrick();
		assertTrue(clonedScriptBrick instanceof CollisionReceiverBrick);

		Brick clonedSetBounceBrick = clonedScript.getBrickList().get(0);
		assertTrue(clonedSetBounceBrick instanceof SetBounceBrick);

		Formula clonedSetBounceBrickFormula = ((FormulaBrick) clonedSetBounceBrick)
				.getFormulaWithBrickField(Brick.BrickField.PHYSICS_BOUNCE_FACTOR);
		float clonedBounceFactorValue = 0;
		clonedBounceFactorValue = clonedSetBounceBrickFormula.interpretFloat(clonedSprite);

		assertEquals(BOUNCE_TEST_VALUE, clonedBounceFactorValue);
	}

	@Test
	public void testSpriteClonePhysicsLookAndPhysicsObject() throws IOException {
		StartScript startScript = new StartScript();
		Brick setPhysicsObjectTypeBrick = new SetPhysicsObjectTypeBrick(TYPE_TEST_VALUE);

		startScript.addBrick(setPhysicsObjectTypeBrick);
		sprite.addScript(startScript);

		PhysicsWorld physicsWorld = project.getDefaultScene().getPhysicsWorld();
		sprite.look = new Look(sprite);

		String rectangle125x125FileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("rectangle_125x125.png");
		LookData lookdata;

		File rectangle125x125File = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.rectangle_125x125,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				rectangle125x125FileName,
				1);

		lookdata = PhysicsTestUtils.generateLookData(rectangle125x125File);
		sprite.look.setLookData(lookdata);

		assertNotNull(rectangle125x125File);
		assertNotNull(sprite.look.getLookData());

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);

		Sprite clonedSprite = new SpriteController().copy(sprite, project.getDefaultScene(), project.getDefaultScene());

		assertNotNull(clonedSprite.look);

		PhysicsObject clonedPhysicsObject = physicsWorld.getPhysicsObject(clonedSprite);
		assertEquals(physicsObject.getType(), clonedPhysicsObject.getType());
		clonedPhysicsObject.setType(PhysicsObject.Type.FIXED);
		assertNotSame(physicsObject.getType(), clonedPhysicsObject.getType());
	}
}
