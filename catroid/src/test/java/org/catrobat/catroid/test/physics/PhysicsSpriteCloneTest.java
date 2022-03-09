/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetBounceBrick;
import org.catrobat.catroid.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.content.bricks.SetGravityBrick;
import org.catrobat.catroid.content.bricks.SetMassBrick;
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(JUnit4.class)
public class PhysicsSpriteCloneTest {

	private Project project;
	private Sprite sprite;
	private static final float BOUNCE_TEST_VALUE = 0.5f;
	private static final float FRICTION_TEST_VALUE = 0.5f;
	private static final Vector2 GRAVITY_TEST_VALUE = new Vector2(10.0f, 10.0f);
	private static final float MASS_TEST_VALUE = 5.0f;
	private static final PhysicsObject.Type TYPE_TEST_VALUE = PhysicsObject.Type.DYNAMIC;
	private static final Vector2 VELOCITY_TEST_VALUE = new Vector2(15.0f, 15.0f);
	private static final float TURN_LEFT_SPEED_TEST_VALUE = 2.0f;
	private static final float TURN_RIGHT_SPEED_TEST_VALUE = 3.0f;
	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		project = new Project(context, getClass().getSimpleName());
		projectManager.getValue().setCurrentProject(project);

		sprite = new Sprite("TestSprite");
		project.getDefaultScene().addSprite(sprite);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
		sprite = null;
		project = null;
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
		WhenBounceOffScript whenBounceOffScript = new WhenBounceOffScript(null);
		whenBounceOffScript.getScriptBrick();
		Brick setBounceBrick = new SetBounceBrick(BOUNCE_TEST_VALUE);
		Brick setFrictionBrick = new SetFrictionBrick(FRICTION_TEST_VALUE);
		Brick setGravityBrick = new SetGravityBrick(GRAVITY_TEST_VALUE);
		Brick setMassBrick = new SetMassBrick(MASS_TEST_VALUE);
		Brick setPhysicsObjectTypeBrick = new SetPhysicsObjectTypeBrick(TYPE_TEST_VALUE);
		Brick setVelocityBrick = new SetVelocityBrick(VELOCITY_TEST_VALUE);
		Brick turnLeftSpeedBrick = new TurnLeftSpeedBrick(TURN_LEFT_SPEED_TEST_VALUE);
		Brick turnRightSpeedBrick = new TurnRightSpeedBrick(TURN_RIGHT_SPEED_TEST_VALUE);

		whenBounceOffScript.addBrick(setBounceBrick);
		whenBounceOffScript.addBrick(setFrictionBrick);
		whenBounceOffScript.addBrick(setGravityBrick);
		whenBounceOffScript.addBrick(setMassBrick);
		whenBounceOffScript.addBrick(setPhysicsObjectTypeBrick);
		whenBounceOffScript.addBrick(setVelocityBrick);
		whenBounceOffScript.addBrick(turnLeftSpeedBrick);
		whenBounceOffScript.addBrick(turnRightSpeedBrick);

		sprite.addScript(whenBounceOffScript);

		Sprite clonedSprite = new SpriteController().copy(sprite, project, project.getDefaultScene());

		checkIfScriptsAndBricksClassesOfSpriteAreEqual(sprite, clonedSprite);
	}

	@Test
	public void testSpriteCloneWithBounceOffScript() throws IOException, InterpretationException {
		WhenBounceOffScript whenBounceOffScript = new WhenBounceOffScript(null);
		whenBounceOffScript.getScriptBrick();
		Brick setBounceBrick = new SetBounceBrick(BOUNCE_TEST_VALUE);

		whenBounceOffScript.addBrick(setBounceBrick);
		sprite.addScript(whenBounceOffScript);

		Sprite clonedSprite = new SpriteController().copy(sprite, project, project.getDefaultScene());

		checkIfScriptsAndBricksClassesOfSpriteAreEqual(sprite, clonedSprite);

		Script clonedScript = clonedSprite.getScript(0);
		assertTrue(clonedScript instanceof WhenBounceOffScript);
		ScriptBrick clonedScriptBrick = clonedScript.getScriptBrick();
		assertTrue(clonedScriptBrick instanceof WhenBounceOffBrick);

		Brick clonedSetBounceBrick = clonedScript.getBrickList().get(0);
		assertTrue(clonedSetBounceBrick instanceof SetBounceBrick);

		Formula clonedSetBounceBrickFormula = ((FormulaBrick) clonedSetBounceBrick)
				.getFormulaWithBrickField(Brick.BrickField.PHYSICS_BOUNCE_FACTOR);
		Scope clonedScope = new Scope(project, clonedSprite, new SequenceAction());
		float clonedBounceFactorValue = 0;
		clonedBounceFactorValue = clonedSetBounceBrickFormula.interpretFloat(clonedScope);

		assertEquals(BOUNCE_TEST_VALUE, clonedBounceFactorValue);
	}
}
