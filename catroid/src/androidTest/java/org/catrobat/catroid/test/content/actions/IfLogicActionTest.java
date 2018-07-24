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
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class IfLogicActionTest {

	private static final int IF_TRUE_VALUE = 42;
	private static final int IF_FALSE_VALUE = 32;
	private static final String TEST_USERVARIABLE = "testUservariable";
	private Sprite testSprite;
	private StartScript testScript;
	private IfLogicBeginBrick ifLogicBeginBrick;
	private IfLogicElseBrick ifLogicElseBrick;
	private IfLogicEndBrick ifLogicEndBrick;
	private Project project;
	private IfLogicBeginBrick ifLogicBeginBrick2;
	private IfLogicElseBrick ifLogicElseBrick2;
	private IfLogicEndBrick ifLogicEndBrick2;
	private RepeatBrick repeatBrick;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final String TRUE = "1.0";
	private UserVariable userVariable;

	@Before
	public void setUp() throws Exception {
		testSprite = new SingleSprite("testSprite");
		project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new SingleSprite("testSprite1"));
		ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer().removeUserVariable(TEST_USERVARIABLE);

		userVariable = new UserVariable(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer().addUserVariable(userVariable);
	}

	@Test
	public void testNestedIfBrick() throws InterruptedException {
		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(IF_TRUE_VALUE), userVariable);

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "1", null), new FormulaElement(ElementType.NUMBER, "2", null)));

		testScript = new StartScript();

		repeatBrick = new RepeatBrick(2);
		ifLogicBeginBrick = new IfLogicBeginBrick(validFormula);
		ifLogicElseBrick = new IfLogicElseBrick(ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(ifLogicElseBrick, ifLogicBeginBrick);
		repeatBrick.setLoopEndBrick(new LoopEndBrick(repeatBrick));

		ifLogicBeginBrick2 = new IfLogicBeginBrick(validFormula);
		ifLogicElseBrick2 = new IfLogicElseBrick(ifLogicBeginBrick2);
		ifLogicEndBrick2 = new IfLogicEndBrick(ifLogicElseBrick2, ifLogicBeginBrick2);

		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(ifLogicBeginBrick2);
		testScript.addBrick(setVariableBrick);
		testScript.addBrick(ifLogicElseBrick2);
		testScript.addBrick(ifLogicEndBrick2);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		testSprite.initializeEventThreads(EventId.START);
		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1f);
		}

		userVariable = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.getUserVariable(null, TEST_USERVARIABLE);

		assertEquals(Double.valueOf(IF_TRUE_VALUE), userVariable.getValue());
	}

	@Test
	public void testIfBrick() throws InterruptedException {
		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(IF_TRUE_VALUE), userVariable);

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "1", null), new FormulaElement(ElementType.NUMBER, "2", null)));

		testScript = new StartScript();

		ifLogicBeginBrick = new IfLogicBeginBrick(validFormula);
		ifLogicElseBrick = new IfLogicElseBrick(ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(ifLogicElseBrick, ifLogicBeginBrick);

		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(setVariableBrick);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.initializeEventThreads(EventId.START);
		testSprite.look.act(100f);

		userVariable = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.getUserVariable(null, TEST_USERVARIABLE);

		assertEquals(Double.valueOf(IF_TRUE_VALUE), userVariable.getValue());
	}

	@Test
	public void testIfElseBrick() throws InterruptedException {
		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(IF_FALSE_VALUE), userVariable);

		Formula invalidFormula = new Formula(1);
		invalidFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "2", null), new FormulaElement(ElementType.NUMBER, "1", null)));

		testScript = new StartScript();

		ifLogicBeginBrick = new IfLogicBeginBrick(invalidFormula);
		ifLogicElseBrick = new IfLogicElseBrick(ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(ifLogicElseBrick, ifLogicBeginBrick);

		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(setVariableBrick);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.initializeEventThreads(EventId.START);
		testSprite.look.act(100f);

		userVariable = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.getUserVariable(null, TEST_USERVARIABLE);

		assertEquals(Double.valueOf(IF_FALSE_VALUE), userVariable.getValue());
	}

	@Test
	public void testBrickWithValidStringFormula() {
		testFormula(new Formula(String.valueOf(TRUE)), Double.valueOf(IF_TRUE_VALUE));
	}

	@Test
	public void testBrickWithInValidStringFormula() {
		testFormula(new Formula(String.valueOf(NOT_NUMERICAL_STRING)), 0.0);
	}

	@Test
	public void testNullFormula() throws Exception {
		Object userVariableExpected = userVariable.getValue();
		Action ifAction = testSprite.getActionFactory().createSetVariableAction(testSprite, new Formula(IF_TRUE_VALUE),
				userVariable);
		Action elseAction = testSprite.getActionFactory().createSetVariableAction(testSprite,
				new Formula(IF_FALSE_VALUE), userVariable);

		Action ifLogicAction = testSprite.getActionFactory().createIfLogicAction(testSprite, null, ifAction,
				elseAction);
		ifLogicAction.act(1.0f);
		Object isInterpretedCorrectly = Reflection.getPrivateField(ifLogicAction, "isInterpretedCorrectly");
		assertFalse((Boolean) isInterpretedCorrectly);
		assertEquals(userVariableExpected, userVariable.getValue());
	}

	@Test
	public void testNotANumberFormula() {
		testFormula(new Formula(Double.NaN), 0.0);
	}

	private void testFormula(Formula formula, Object expected) {
		SetVariableBrick setVariableBrickIfTrue = new SetVariableBrick(new Formula(IF_TRUE_VALUE),
				userVariable);
		SetVariableBrick setVariableBrickIfFalse = new SetVariableBrick(new Formula(IF_FALSE_VALUE),
				userVariable);

		testScript = new StartScript();
		ifLogicBeginBrick = new IfLogicBeginBrick(formula);
		ifLogicElseBrick = new IfLogicElseBrick(ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(ifLogicElseBrick, ifLogicBeginBrick);
		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(setVariableBrickIfTrue);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(setVariableBrickIfFalse);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.initializeEventThreads(EventId.START);
		testSprite.look.act(1f);
		userVariable = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.getUserVariable(null, TEST_USERVARIABLE);

		assertEquals(expected, userVariable.getValue());
	}
}
