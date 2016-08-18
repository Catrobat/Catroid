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
package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.RepeatUntilAction;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.HashMap;
import java.util.List;

public class RepeatUntilActionTest extends InstrumentationTestCase {

	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite testSprite;
	private Script testScript;
	private int delta = 5;
	private UserVariable userVariable;
	private static final int START_VALUE = 3;
	private static final int TRUE_VALUE = 6;
	private static final String TEST_USERVARIABLE = "testUservariable";
	private Project project;

	@Override
	protected void setUp() throws Exception {
		testSprite = new SingleSprite("testSprite");
		project = new Project(null, "testProject");
		testScript = new StartScript();
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new SingleSprite("testSprite1"));
		ProjectManager.getInstance().getCurrentScene().getDataContainer().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentScene().getDataContainer().addProjectUserVariable(TEST_USERVARIABLE);
		userVariable = ProjectManager.getInstance().getCurrentScene().getDataContainer()
				.getUserVariable(TEST_USERVARIABLE, null);

		super.setUp();
	}

	public void testRepeatBrick() throws InterruptedException {

		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(START_VALUE), userVariable);

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_OR_EQUAL.name(), null,
				new FormulaElement(ElementType.NUMBER, String.valueOf(TRUE_VALUE), null),
				new FormulaElement(ElementType.USER_VARIABLE, userVariable.getName(), null)));

		RepeatUntilBrick repeatBrick = new RepeatUntilBrick(validFormula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);

		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;

		testScript.addBrick(setVariableBrick);
		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));

		Formula validFormula2 = new Formula(1);
		validFormula2.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null,
				new FormulaElement(ElementType.NUMBER, String.valueOf(1), null),
				new FormulaElement(ElementType.USER_VARIABLE, userVariable.getName(), null)));

		SetVariableBrick setVariableBrick2 = new SetVariableBrick(validFormula2, userVariable);
		testScript.addBrick(setVariableBrick2);
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals("Executed the wrong number of times!", (TRUE_VALUE - START_VALUE) * deltaY,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNoRepeat() {
		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "1", null), new FormulaElement(ElementType
				.NUMBER,
				"2",
				null)));

		this.testWithFormula(validFormula, 0.0f);
	}

	public void testBrickWithInValidStringFormula() {
		Formula stringFormula = new Formula(String.valueOf(NOT_NUMERICAL_STRING));
		testWithFormula(stringFormula, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		Action repeatedAction = testSprite.getActionFactory().createSetXAction(testSprite, new Formula(10));
		Action repeatAction = testSprite.getActionFactory().createRepeatUntilAction(testSprite, null, repeatedAction);

		repeatAction.act(1.0f);
		int repeatCountValue = ((RepeatUntilAction) repeatAction).getExecutedCount();
		assertEquals("Null Formula should not have been possible to interpret!", 0, repeatCountValue);
	}

	public void testNotANumberFormula() {
		Formula notANumber = new Formula(Double.NaN);
		testWithFormula(notANumber, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	private void testWithFormula(Formula formula, Float expected) {
		RepeatUntilBrick repeatBrick = new RepeatUntilBrick(formula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(delta));
		testScript.addBrick(loopEndBrick);
		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals("Executed the wrong number of times!", expected,
				testSprite.look.getYInUserInterfaceDimensionUnit());
	}
}
