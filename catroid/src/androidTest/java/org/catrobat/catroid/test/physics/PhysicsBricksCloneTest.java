/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.test.AndroidTestCase;
import android.util.Log;

import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
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
import org.catrobat.catroid.test.utils.Reflection;

public class PhysicsBricksCloneTest extends AndroidTestCase {

	private static final String TAG = PhysicsBricksCloneTest.class.getSimpleName();

	private static final int BRICK_FORMULA_VALUE = 1;
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";
	private static final String COLLISION_RECEIVER_TEST_MESSAGE = "Collision_receiver_test_message";

	Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new SingleSprite("testSprite");
	}

	public void testClonePhysicsBricksWithFormula() {

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

	public void testCloneCollisionReceiverBrick() {

		Brick brick = new CollisionReceiverBrick(new CollisionScript(COLLISION_RECEIVER_TEST_MESSAGE));

		Brick clonedBrick = null;
		try {
			clonedBrick = brick.clone();
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, "Cloning CollisionReceiverBrick not supported", e);
			fail("clone of CollisionReceiverBrick not supported");
		}

		CollisionScript brickReceiverScript = (CollisionScript) Reflection.getPrivateField(brick, "collisionScript");
		CollisionScript clonedBrickReceiverScript = (CollisionScript) Reflection.getPrivateField(clonedBrick, "collisionScript");

		String scriptReceiveMessage = brickReceiverScript.getBroadcastMessage();
		String clonedReceiveMessage = clonedBrickReceiverScript.getBroadcastMessage();

		assertFalse("CollisionScripts have same address", brickReceiverScript == clonedBrickReceiverScript);
		assertEquals("ReceiveMessages are not equal after clone()", scriptReceiveMessage, clonedReceiveMessage);
	}

	public void testCloneSetPhysicsObjectTypeBrick() {
		Brick dynamicBrick = new SetPhysicsObjectTypeBrick(PhysicsObject.Type.DYNAMIC);
		Brick fixedBrick = new SetPhysicsObjectTypeBrick(PhysicsObject.Type.FIXED);
		Brick noneBrick = new SetPhysicsObjectTypeBrick(PhysicsObject.Type.NONE);

		Brick clonedDynamicBrick = null;
		Brick clonedFixedBrick = null;
		Brick clonedNoneBrick = null;
		try {
			clonedDynamicBrick = dynamicBrick.clone();
			clonedFixedBrick = fixedBrick.clone();
			clonedNoneBrick = noneBrick.clone();
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, "Cloning SetPhysicsObjectTypeBrick not supported", e);
		}

		PhysicsObject.Type dynamicBrickType = (PhysicsObject.Type) Reflection.getPrivateField(dynamicBrick, "type");
		PhysicsObject.Type clonedDynamicBrickType = (PhysicsObject.Type) Reflection.getPrivateField(clonedDynamicBrick, "type");

		PhysicsObject.Type fixedBrickType = (PhysicsObject.Type) Reflection.getPrivateField(fixedBrick, "type");
		PhysicsObject.Type clonedFixedBrickType = (PhysicsObject.Type) Reflection.getPrivateField(clonedFixedBrick, "type");

		PhysicsObject.Type noneBrickType = (PhysicsObject.Type) Reflection.getPrivateField(noneBrick, "type");
		PhysicsObject.Type clonedNoneBrickType = (PhysicsObject.Type) Reflection.getPrivateField(clonedNoneBrick, "type");

		assertTrue("DYNAMIC Brick has wrong type after clone", dynamicBrickType.equals(clonedDynamicBrickType));
		assertTrue("FIXED Brick has wrong type after clone", fixedBrickType.equals(clonedFixedBrickType));
		assertTrue("NONE Brick has wrong type after clone", noneBrickType.equals(clonedNoneBrickType));

		assertTrue("Cloned DYNAMIC Brick has wrong Object-Type", clonedDynamicBrick.getClass().equals(dynamicBrick.getClass()));
		assertTrue("Cloned FIXED Brick has wrong Object-Type", clonedFixedBrick.getClass().equals(fixedBrick.getClass()));
		assertTrue("Cloned NONE Brick has wrong Object-Type", clonedNoneBrick.getClass().equals(noneBrick.getClass()));
	}

	private void brickClone(Brick brick, Brick.BrickField... brickFields) {
		try {
			Brick cloneBrick = brick.clone();
			for (Brick.BrickField brickField : brickFields) {
				Formula brickFormula = ((FormulaBrick) brick).getFormulaWithBrickField(brickField);
				Formula cloneBrickFormula = ((FormulaBrick) cloneBrick).getFormulaWithBrickField(brickField);
				cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
				try {
					assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
							cloneBrickFormula.interpretInteger(sprite));
				} catch (InterpretationException interpretationException) {
					Log.e(TAG, "InterpretationException thrown while interpreting.", interpretationException);
					fail("InterpretationException thrown while interpreting.");
				}
			}
		} catch (CloneNotSupportedException exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
			fail("cloning the brick failed");
		}
	}
}
