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
package org.catrobat.catroid.test.content.bricks;

import android.test.AndroidTestCase;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.lang.reflect.Constructor;

public class BrickCloneTest extends AndroidTestCase {

	private static final int BRICK_FORMULA_VALUE = 1;
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";
	private static final String VARIABLE_NAME = "test_variable";
	private static final String TAG = null;
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
	}

	public void testBrickCloneWithFormula() {
		Brick brick = new ChangeBrightnessByNBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.BRIGHTNESS_CHANGE);

		brick = new ChangeTransparencyByNBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.TRANSPARENCY_CHANGE);

		brick = new ChangeSizeByNBrick(new Formula(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.SIZE_CHANGE);

		brick = new ChangeVariableBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.VARIABLE_CHANGE);

		brick = new ChangeVolumeByNBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.VOLUME_CHANGE);

		brick = new ChangeXByNBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.X_POSITION_CHANGE);

		brick = new ChangeYByNBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.Y_POSITION_CHANGE);

		brick = new GoNStepsBackBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.STEPS);

		brick = new IfLogicBeginBrick(10);
		brickClone(brick, Brick.BrickField.IF_CONDITION);

		brick = new LegoNxtMotorMoveBrick(LegoNxtMotorMoveBrick.Motor.MOTOR_A, BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.LEGO_NXT_SPEED);

		brick = new LegoNxtMotorTurnAngleBrick(LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.LEGO_NXT_DEGREES);

		brick = new LegoNxtPlayToneBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.LEGO_NXT_FREQUENCY, Brick.BrickField.LEGO_NXT_DURATION_IN_SECONDS);

		brick = new MoveNStepsBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.STEPS);

		brick = new RepeatBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.TIMES_TO_REPEAT);

		brick = new SetBrightnessBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.BRIGHTNESS);

		brick = new SetTransparencyBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.TRANSPARENCY);

		brick = new SetSizeToBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.SIZE);

		brick = new SetVariableBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.VARIABLE);

		brick = new SetVolumeToBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.VOLUME);

		brick = new SetXBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.X_POSITION);

		brick = new SetYBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.Y_POSITION);

		brick = new TurnLeftBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.TURN_LEFT_DEGREES);

		brick = new TurnRightBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.TURN_RIGHT_DEGREES);

		brick = new VibrationBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.VIBRATE_DURATION_IN_SECONDS);

		brick = new WaitBrick(BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.TIME_TO_WAIT_IN_SECONDS);

		brick = new PlaceAtBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.X_POSITION, Brick.BrickField.Y_POSITION);

		brick = new GlideToBrick(BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE, BRICK_FORMULA_VALUE);
		brickClone(brick, Brick.BrickField.X_DESTINATION, Brick.BrickField.Y_DESTINATION,
				Brick.BrickField.DURATION_IN_SECONDS);

		brick = new NoteBrick(String.valueOf(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.NOTE);

		brick = new SpeakBrick(String.valueOf(BRICK_FORMULA_VALUE));
		brickClone(brick, Brick.BrickField.SPEAK);
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
		StartScript script = new StartScript();
		sprite.addScript(script);
		project.getDataContainer().addSpriteUserVariableToSprite(sprite, VARIABLE_NAME);
		UserVariable spriteVariable = project.getDataContainer().getUserVariable(VARIABLE_NAME, sprite);
		Formula formula = new Formula(new FormulaElement(ElementType.USER_VARIABLE, VARIABLE_NAME, null));

		// create brick - expects:
		// public SetVariableBrick(Formula variableFormula, UserVariable userVariable)
		Constructor<T> constructor = typeOfBrick
				.getDeclaredConstructor(Formula.class, UserVariable.class);
		T toBeTestedBrick = constructor.newInstance(formula, spriteVariable);

		// add brick to project
		script.addBrick(toBeTestedBrick);

		// get references
		Sprite clonedSprite = sprite.clone();
		@SuppressWarnings("unchecked")
		T clonedBrick = (T) clonedSprite.getScript(0).getBrick(0);
		UserVariable clonedVariable = project.getDataContainer().getUserVariable(VARIABLE_NAME, clonedSprite);
		UserVariable clonedVariableFromBrick = (UserVariable) Reflection.getPrivateField(UserVariableBrick.class, clonedBrick, "userVariable");

		// check them
		assertNotNull("variable should be in container", clonedVariable);
		assertNotSame("references shouldn't be the same", spriteVariable, clonedVariable);
		assertNotSame("references shouldn't be the same", spriteVariable, clonedVariableFromBrick);
		assertEquals("references should be the same", clonedVariable, clonedVariableFromBrick);
	}

	private void brickClone(Brick brick, Brick.BrickField... brickFields) {
		try {
			Brick cloneBrick = brick.clone();
			for (Brick.BrickField brickField : brickFields) {
				Formula brickFormula = ((FormulaBrick) brick).getFormulaWithBrickField(brickField);
				Formula cloneBrickFormula = ((FormulaBrick) cloneBrick).getFormulaWithBrickField(brickField);
				cloneBrickFormula.setRoot(new FormulaElement(ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
				assertNotSame("Error - brick.clone() not working properly", brickFormula.interpretInteger(sprite),
						cloneBrickFormula.interpretInteger(sprite));
			}
		} catch (CloneNotSupportedException exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
			fail("cloning the brick failed");
		} catch (InterpretationException interpretationException) {
			Log.e(TAG, Log.getStackTraceString(interpretationException));
			fail("Cloning of the brick failed: Formula interpretation failed.");
		}
	}
}

