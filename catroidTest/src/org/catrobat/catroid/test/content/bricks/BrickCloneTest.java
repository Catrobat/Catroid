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
package org.catrobat.catroid.test.content.bricks;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.lang.reflect.Constructor;

public class BrickCloneTest extends AndroidTestCase {

	private static final int BRICK_FORMULA_VALUE = 1;
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";
	private static final String VARIABLE_NAME = "test_variable";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");

	}

	public void testBrickCloneWithFormula() {
		Brick brick = new ChangeBrightnessByNBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "changeBrightness");

		brick = new ChangeGhostEffectByNBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "changeGhostEffect");

		brick = new ChangeSizeByNBrick(sprite, new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, "size");

		brick = new ChangeVariableBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "variableFormula");

		brick = new ChangeVolumeByNBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "volume");

		brick = new ChangeXByNBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "xMovement");

		brick = new ChangeYByNBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "yMovement");

		brick = new GoNStepsBackBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "steps");

		brick = new IfLogicBeginBrick(sprite, 10);
		brickClone(brick, "ifCondition");

		brick = new LegoNxtMotorActionBrick(sprite, LegoNxtMotorActionBrick.Motor.MOTOR_A, BRICK_FORMULA_VALUE);
		brickClone(brick, "speed");

		brick = new LegoNxtMotorTurnAngleBrick(sprite, LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, BRICK_FORMULA_VALUE);
		brickClone(brick, "degrees");

		brick = new MoveNStepsBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "steps");

		brick = new RepeatBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "timesToRepeat");

		brick = new SetBrightnessBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "brightness");

		brick = new SetGhostEffectBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "transparency");

		brick = new SetSizeToBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "size");

		brick = new SetVariableBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "variableFormula");

		brick = new SetVolumeToBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "volume");

		brick = new SetXBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "xPosition");

		brick = new SetYBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "yPosition");

		brick = new TurnLeftBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "degrees");

		brick = new TurnRightBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "degrees");

		brick = new WaitBrick(sprite, BRICK_FORMULA_VALUE);
		brickClone(brick, "timeToWaitInSeconds");

		brick = new PlaceAtBrick(sprite, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE);
		brickClone(brick, "xPosition", "yPosition");

		brick = new LegoNxtPlayToneBrick(sprite, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE);
		brickClone(brick, "frequency", "durationInSeconds");

		brick = new GlideToBrick(sprite, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE);
		brickClone(brick, "xDestination", "yDestination", "durationInSeconds");
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
		Formula formula = new Formula(new FormulaElement(ElementType.USER_VARIABLE, VARIABLE_NAME, null));

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

	private void brickClone(Brick brick, String formulaName) {
		Brick cloneBrick = brick.clone();
		Formula brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName);
		Formula cloneBrickFormula = (Formula) Reflection.getPrivateField(cloneBrick, formulaName);
		cloneBrickFormula.setRoot(new FormulaElement(ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));
	}

	private void brickClone(Brick brick, String formulaName1, String formulaName2) {
		Brick cloneBrick = brick.clone();
		Formula brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName1);
		Formula cloneBrickFormula = (Formula) Reflection.getPrivateField(cloneBrick, formulaName1);
		cloneBrickFormula.setRoot(new FormulaElement(ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName2);
		cloneBrickFormula = (Formula) Reflection.getPrivateField(cloneBrick, formulaName2);
		cloneBrickFormula.setRoot(new FormulaElement(ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));
	}

	private void brickClone(Brick brick, String formulaName1, String formulaName2, String formulaName3) {
		Brick cloneBrick = brick.clone();
		Formula brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName1);
		Formula cloneBrickFormula = (Formula) Reflection.getPrivateField(cloneBrick, formulaName1);
		cloneBrickFormula.setRoot(new FormulaElement(ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		cloneBrick = brick.clone();
		brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName2);
		cloneBrickFormula = (Formula) Reflection.getPrivateField(cloneBrick, formulaName2);
		cloneBrickFormula.setRoot(new FormulaElement(ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));

		cloneBrick = brick.clone();
		brickFormula = (Formula) Reflection.getPrivateField(brick, formulaName3);
		cloneBrickFormula = (Formula) Reflection.getPrivateField(cloneBrick, formulaName3);
		cloneBrickFormula.setRoot(new FormulaElement(ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
				cloneBrickFormula.interpretInteger(sprite));
	}

}
