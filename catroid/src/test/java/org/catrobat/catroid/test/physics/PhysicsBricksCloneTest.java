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

import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class PhysicsBricksCloneTest {

	private static final int BRICK_FORMULA_VALUE = 1;
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";

	Sprite sprite;

	@Before
	public void setUp() throws Exception {
		sprite = new SingleSprite("testSprite");
	}

	@Test
	public void testClonePhysicsBricksWithFormula() throws Exception {
		Brick brick = new SetBounceBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.PHYSICS_BOUNCE_FACTOR);

		brick = new SetFrictionBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.PHYSICS_FRICTION);

		brick = new SetGravityBrick(new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.PHYSICS_GRAVITY_X, Brick.BrickField.PHYSICS_GRAVITY_Y);

		brick = new SetMassBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.PHYSICS_MASS);

		brick = new SetVelocityBrick(new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.PHYSICS_VELOCITY_X, Brick.BrickField.PHYSICS_VELOCITY_Y);

		brick = new TurnRightSpeedBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.PHYSICS_TURN_RIGHT_SPEED);

		brick = new TurnLeftSpeedBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.PHYSICS_TURN_LEFT_SPEED);
	}

	@Test
	public void testCloneCollisionReceiverBrick() throws Exception {
		Brick brick = new CollisionReceiverBrick(new CollisionScript(null));
		Brick clonedBrick = brick.clone();

		CollisionScript brickReceiverScript = (CollisionScript) ((CollisionReceiverBrick) brick).getScript();
		CollisionScript clonedBrickReceiverScript = (CollisionScript) ((CollisionReceiverBrick) clonedBrick).getScript();

		assertNotSame(brickReceiverScript, clonedBrickReceiverScript);
	}

	@Test
	public void testCloneSetPhysicsObjectTypeBrick() throws Exception {
		Brick dynamicBrick = new SetPhysicsObjectTypeBrick(PhysicsObject.Type.DYNAMIC);
		Brick fixedBrick = new SetPhysicsObjectTypeBrick(PhysicsObject.Type.FIXED);
		Brick noneBrick = new SetPhysicsObjectTypeBrick(PhysicsObject.Type.NONE);

		Brick clonedDynamicBrick = dynamicBrick.clone();
		Brick clonedFixedBrick = fixedBrick.clone();
		Brick clonedNoneBrick = noneBrick.clone();

		PhysicsObject.Type dynamicBrickType = ((SetPhysicsObjectTypeBrick) dynamicBrick).getType();
		PhysicsObject.Type clonedDynamicBrickType = ((SetPhysicsObjectTypeBrick) clonedDynamicBrick).getType();
		assertEquals(dynamicBrickType, clonedDynamicBrickType);
		assertThat(clonedDynamicBrick, is(instanceOf(dynamicBrick.getClass())));
		assertNotSame(clonedDynamicBrick, dynamicBrickType);

		PhysicsObject.Type fixedBrickType = ((SetPhysicsObjectTypeBrick) fixedBrick).getType();
		PhysicsObject.Type clonedFixedBrickType = ((SetPhysicsObjectTypeBrick) clonedFixedBrick).getType();
		assertEquals(fixedBrickType, clonedFixedBrickType);
		assertThat(clonedFixedBrick, is(instanceOf(fixedBrick.getClass())));
		assertNotSame(clonedFixedBrick, fixedBrick);

		PhysicsObject.Type noneBrickType = ((SetPhysicsObjectTypeBrick) noneBrick).getType();
		PhysicsObject.Type clonedNoneBrickType = ((SetPhysicsObjectTypeBrick) clonedNoneBrick).getType();
		assertEquals(noneBrickType, clonedNoneBrickType);
		assertThat(clonedNoneBrick, is(instanceOf(noneBrick.getClass())));
		assertNotSame(clonedNoneBrick, noneBrick);
	}

	private void brickClone(Brick brick, Brick.BrickField... brickFields) throws Exception {
		Brick cloneBrick = brick.clone();
		for (Brick.BrickField brickField : brickFields) {
			Formula brickFormula = ((FormulaBrick) brick).getFormulaWithBrickField(brickField);
			Formula cloneBrickFormula = ((FormulaBrick) cloneBrick).getFormulaWithBrickField(brickField);
			cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
			assertNotSame(brickFormula.interpretInteger(sprite),
						cloneBrickFormula.interpretInteger(sprite));
		}
	}
}
