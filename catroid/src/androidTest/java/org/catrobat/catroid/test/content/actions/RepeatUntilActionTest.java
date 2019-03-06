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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RepeatUntilActionTest {

	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite testSprite;
	private Script testScript;
	private UserVariable userVariable;
	private UserVariable userVariable2;
	private static final int START_VALUE = 3;
	private static final int TRUE_VALUE = 6;
	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final String TEST_USERVARIABLE_2 = "testUservariable2";

	@Before
	public void setUp() throws Exception {
		testSprite = new SingleSprite("testSprite");
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		testScript = new StartScript();
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(new SingleSprite("testSprite1"));

		project.removeUserVariable(TEST_USERVARIABLE);
		userVariable = new UserVariable(TEST_USERVARIABLE);
		project.addUserVariable(userVariable);

		project.removeUserVariable(TEST_USERVARIABLE_2);
		userVariable2 = new UserVariable(TEST_USERVARIABLE_2);
		project.addUserVariable(userVariable2);
	}

	@Test
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
		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals((TRUE_VALUE - START_VALUE) * deltaY, (int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNoRepeat() {
		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "1", null), new FormulaElement(ElementType
				.NUMBER,
				"2",
				null)));

		this.testWithFormula(validFormula, 0.0f);
	}

	@Test
	public void testBrickWithInValidStringFormula() {
		Formula stringFormula = new Formula(String.valueOf(NOT_NUMERICAL_STRING));
		testWithFormula(stringFormula, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		Action repeatedAction = testSprite.getActionFactory().createSetXAction(testSprite, new Formula(10));
		Action repeatAction = testSprite.getActionFactory().createRepeatUntilAction(testSprite, null, repeatedAction);

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
		RepeatUntilBrick repeatBrick = new RepeatUntilBrick(formula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(repeatBrick);
		int delta = 5;
		testScript.addBrick(new ChangeYByNBrick(delta));
		testScript.addBrick(loopEndBrick);
		testSprite.addScript(testScript);
		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals(expected, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testConditionCheckedOnlyAtEnd() {
		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.EQUAL.name(), null,
				new FormulaElement(ElementType.NUMBER, String.valueOf(TRUE_VALUE), null),
				new FormulaElement(ElementType.USER_VARIABLE, userVariable.getName(), null)));

		RepeatUntilBrick repeatBrick = new RepeatUntilBrick(validFormula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new SetVariableBrick(new Formula(TRUE_VALUE), userVariable));
		testScript.addBrick(new SetVariableBrick(new Formula(TRUE_VALUE), userVariable2));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
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
