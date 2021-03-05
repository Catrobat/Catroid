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
package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.RepeatUntilAction;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RepeatUntilActionTest {

	private static final int START_VALUE = 3;
	private static final int TRUE_VALUE = 6;

	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final String TEST_USERVARIABLE_2 = "testUservariable2";

	private Sprite testSprite;
	private Script testScript;

	private UserVariable userVariable;
	private UserVariable userVariable2;

	@Before
	public void setUp() throws Exception {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");

		testSprite = new Sprite("testSprite");
		testScript = new StartScript();
		testSprite.removeAllScripts();
		testSprite.addScript(testScript);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(testSprite);

		project.removeUserVariable(TEST_USERVARIABLE);
		userVariable = new UserVariable(TEST_USERVARIABLE);
		project.addUserVariable(userVariable);

		project.removeUserVariable(TEST_USERVARIABLE_2);
		userVariable2 = new UserVariable(TEST_USERVARIABLE_2);
		project.addUserVariable(userVariable2);
	}

	@Test
	public void testRepeatBrick() {
		Formula repeatUntilFormula = new Formula(1);
		repeatUntilFormula.setRoot(
				new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_OR_EQUAL.name(), null,
						new FormulaElement(ElementType.NUMBER, String.valueOf(TRUE_VALUE), null),
						new FormulaElement(ElementType.USER_VARIABLE, userVariable.getName(), null)));

		Formula setVariableFormula = new Formula(1);
		setVariableFormula.setRoot(
				new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null,
						new FormulaElement(ElementType.NUMBER, String.valueOf(1), null),
						new FormulaElement(ElementType.USER_VARIABLE, userVariable.getName(), null)));

		int deltaY = -10;

		testScript.addBrick(new SetVariableBrick(new Formula(START_VALUE), userVariable));

		RepeatUntilBrick repeatUntilBrick = new RepeatUntilBrick(repeatUntilFormula);
		repeatUntilBrick.addBrick(new ChangeYByNBrick(new Formula(deltaY)));
		repeatUntilBrick.addBrick(new SetVariableBrick(setVariableFormula, userVariable));
		testScript.addBrick(repeatUntilBrick);

		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals((TRUE_VALUE - START_VALUE) * deltaY, (int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNoRepeat() {
		Formula formula = new Formula(1);
		formula.setRoot(
				new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
						new FormulaElement(ElementType.NUMBER, "1", null),
						new FormulaElement(ElementType.NUMBER, "2", null)));

		testWithFormula(formula, 0.0f);
	}

	@Test
	public void testBrickWithInValidStringFormula() {
		Formula stringFormula = new Formula(String.valueOf(NOT_NUMERICAL_STRING));
		testWithFormula(stringFormula, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		Action repeatedAction = testSprite.getActionFactory()
				.createSetXAction(testSprite, new SequenceAction(), new Formula(10));
		Action repeatAction = testSprite.getActionFactory()
				.createRepeatUntilAction(testSprite, new SequenceAction(), null, repeatedAction, true);

		repeatAction.act(1.0f);
		int repeatCountValue = ((RepeatUntilAction) repeatAction).getExecutedCount();
		assertEquals(0, repeatCountValue);
	}

	@Test
	public void testNotANumberFormula() {
		Formula notANumber = new Formula(Double.NaN);
		testWithFormula(notANumber, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	private void testWithFormula(Formula formula, Float expected) {
		int delta = 5;

		RepeatUntilBrick repeatUntilBrick = new RepeatUntilBrick(formula);
		repeatUntilBrick.addBrick(new ChangeYByNBrick(delta));
		testScript.addBrick(repeatUntilBrick);

		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals(expected, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testConditionCheckedOnlyAtEnd() {
		Formula formula = new Formula(1);
		formula.setRoot(
				new FormulaElement(ElementType.OPERATOR, Operators.EQUAL.name(), null,
						new FormulaElement(ElementType.NUMBER, String.valueOf(TRUE_VALUE), null),
						new FormulaElement(ElementType.USER_VARIABLE, userVariable.getName(), null)));

		RepeatUntilBrick repeatUntilBrick = new RepeatUntilBrick(formula);
		repeatUntilBrick.addBrick(new SetVariableBrick(new Formula(TRUE_VALUE), userVariable));
		repeatUntilBrick.addBrick(new SetVariableBrick(new Formula(TRUE_VALUE), userVariable2));
		testScript.addBrick(repeatUntilBrick);

		testSprite.initializeEventThreads(EventId.START);
		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}

		int valueOfUserVariable = ((Double) userVariable.getValue()).intValue();
		int valueOfUserVariable2 = ((Double) userVariable2.getValue()).intValue();

		assertEquals(TRUE_VALUE, valueOfUserVariable);
		assertEquals(TRUE_VALUE, valueOfUserVariable2);
	}
}
