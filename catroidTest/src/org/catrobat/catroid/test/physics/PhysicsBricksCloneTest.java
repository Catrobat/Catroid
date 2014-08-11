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
package org.catrobat.catroid.test.physics;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
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
import org.catrobat.catroid.test.utils.Reflection;

public class PhysicsBricksCloneTest extends AndroidTestCase {

	private static final int BRICK_FORMULA_VALUE = 1;
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";
	private static final String COLLISION_RECEIVER_TEST_MESSAGE = "Collision_receiver_test_message";

	Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");

	}

	public void testClonePhysicsBricksWithFormula() {

		Brick brick = new SetBounceBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "bounceFactor");

		brick = new SetFrictionBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "friction");

		brick = new SetGravityBrick(sprite, new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "gravityX", "gravityY");

		brick = new SetMassBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "mass");

		brick = new SetVelocityBrick(sprite, new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "velocityX", "velocityY");

		brick = new TurnRightSpeedBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "degreesPerSecond");

		brick = new TurnLeftSpeedBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "degreesPerSecond");
	}

	public void testCloneCollisionReceiverBrick() {

		Brick brick = new CollisionReceiverBrick(sprite, new CollisionScript(sprite, COLLISION_RECEIVER_TEST_MESSAGE));

		Brick clonedBrick = brick.clone();

		CollisionScript brickReceiverScript = (CollisionScript) Reflection.getPrivateField(brick, "receiveScript");
		CollisionScript clonedBrickReceiverScript = (CollisionScript) Reflection.getPrivateField(clonedBrick, "receiveScript");

		String scriptReceiveMessage = brickReceiverScript.getBroadcastMessage();
		String clonedReceiveMessage = clonedBrickReceiverScript.getBroadcastMessage();

		String brickSpriteName = brick.getSprite().getName();
		String clonedBrickSpriteName = clonedBrick.getSprite().getName();

		assertFalse("CollisionScripts have same address", brickReceiverScript == clonedBrickReceiverScript);
		assertEquals("ReceiveMessages are not equal after clone()", scriptReceiveMessage, clonedReceiveMessage);
		assertEquals("Sprite names are not equal after clone()", brickSpriteName, clonedBrickSpriteName);
	}

	public void testCloneSetPhysicsObjectTypeBrick() {
		Brick dynamicBrick = new SetPhysicsObjectTypeBrick(sprite, PhysicsObject.Type.DYNAMIC);
		Brick fixedBrick = new SetPhysicsObjectTypeBrick(sprite, PhysicsObject.Type.FIXED);
		Brick noneBrick = new SetPhysicsObjectTypeBrick(sprite, PhysicsObject.Type.NONE);

		Brick clonedDynamicBrick = dynamicBrick.clone();
		Brick clonedFixedBrick = fixedBrick.clone();
		Brick clonedNoneBrick = noneBrick.clone();

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

	private void checkFormula(Sprite sprite, Formula brickFormula, Formula cloneBrickFormula) {
		assertEquals("Formulas of bricks are not equal after clone()", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));

		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));
	}

	private void brickClone(Brick brick, String formulaName) {
		Brick clonedBrick = brick.clone();
		Formula brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName);
		Formula cloneBrickFormula = (Formula) Reflection.getPrivateField(clonedBrick, formulaName);

		checkFormula(sprite, brickFormula, cloneBrickFormula);

		assertTrue("Cloned Brick has wrong Object-Type", clonedBrick.getClass().equals(brick.getClass()));
	}

	private void brickClone(Brick brick, String formulaName1, String formulaName2) {
		Brick clonedBrick = brick.clone();
		Formula brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName1);
		Formula cloneBrickFormula = (Formula) Reflection.getPrivateField(clonedBrick, formulaName1);

		checkFormula(sprite, brickFormula, cloneBrickFormula);

		brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName2);
		cloneBrickFormula = (Formula) Reflection.getPrivateField(clonedBrick, formulaName2);

		checkFormula(sprite, brickFormula, cloneBrickFormula);

		assertTrue("Cloned Brick has wrong Object-Type", clonedBrick.getClass().equals(brick.getClass()));
	}
}
