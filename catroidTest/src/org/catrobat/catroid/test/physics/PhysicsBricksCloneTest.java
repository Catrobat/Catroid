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
package org.catrobat.catroid.test.physics;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;
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
import org.catrobat.catroid.test.utils.TestUtils;

import java.lang.reflect.Constructor;

public class PhysicsBricksCloneTest extends AndroidTestCase {

	private static final int BRICK_FORMULA_VALUE = 1;
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";
	private static final String COLLISION_RECEIVER_TEST_MESSAGE = "Collision_receiver_test_message";
	private static final String VARIABLE_NAME = "test_variable";

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

	private void brickClone(Brick brick, String formulaName) {
		Brick clonedBrick = brick.clone();
		Formula brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName);
		Formula cloneBrickFormula = (Formula) Reflection.getPrivateField(clonedBrick, formulaName);

		assertEquals("Formulas of bricks are not equal after clone()", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));

		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		assertTrue("Cloned Brick has wrong Object-Type", clonedBrick.getClass().equals(brick.getClass()));
	}

	private void brickClone(Brick brick, String formulaName1, String formulaName2) {
		Brick clonedBrick = brick.clone();
		Formula brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName1);
		Formula cloneBrickFormula = (Formula) Reflection.getPrivateField(clonedBrick, formulaName1);

		assertEquals("Formulas of bricks are not equal after clone()", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName2);
		cloneBrickFormula = (Formula) Reflection.getPrivateField(clonedBrick, formulaName2);

		assertEquals("Formulas of bricks are not equal after clone()", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		assertTrue("Cloned Brick has wrong Object-Type", clonedBrick.getClass().equals(brick.getClass()));
	}

	public void testVariableReferencesSetVariableBrick() throws Exception {
		testVariableReferences(SetVariableBrick.class);
	}

	public void testVariableReferencesChangeVariableBrick() throws Exception {
		testVariableReferences(ChangeVariableBrick.class);
	}

	private <T extends Brick> void testVariableReferences(Class<T> typeOfBrick) throws Exception {
		// set up project
		Project project = new Project(null, TestUtils.DEFAULT_TEST_PROJECT_NAME);
		ProjectManager.getInstance().setProject(project);
		project.addSprite(sprite);
		StartScript script = new StartScript(sprite);
		sprite.addScript(script);
		project.getUserVariables().addSpriteUserVariableToSprite(sprite, VARIABLE_NAME);
		UserVariable spriteVariable = project.getUserVariables().getUserVariable(VARIABLE_NAME, sprite);
		Formula formula = new Formula(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, VARIABLE_NAME, null));

		// create brick - expects:
		// public SetVariableBrick(Sprite sprite, Formula variableFormula, UserVariable userVariable)
		Constructor<T> constructor = typeOfBrick
				.getDeclaredConstructor(Sprite.class, Formula.class, UserVariable.class);
		T toBeTestedBrick = constructor.newInstance(sprite, formula, spriteVariable);

		// add brick to project
		script.addBrick(toBeTestedBrick);

		// get references
		Sprite clonedSprite = sprite.clone();
		@SuppressWarnings("unchecked")
		T clonedBrick = (T) clonedSprite.getScript(0).getBrick(0);
		UserVariable clonedVariable = project.getUserVariables().getUserVariable(VARIABLE_NAME, clonedSprite);
		UserVariable clonedVariableFromBrick = (UserVariable) Reflection.getPrivateField(clonedBrick, "userVariable");

		// check them
		assertNotNull("variable should be in container", clonedVariable);
		assertNotSame("references shouldn't be the same", spriteVariable, clonedVariable);
		assertNotSame("references shouldn't be the same", spriteVariable, clonedVariableFromBrick);
		assertEquals("references should be the same", clonedVariable, clonedVariableFromBrick);
	}
}
