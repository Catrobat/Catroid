/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(JUnit4.class)
public class IfLogicActionTest {

	private static final int IF_TRUE_VALUE = 42;
	private static final int IF_FALSE_VALUE = 32;

	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final String TRUE = "1.0";

	private Sprite testSprite;
	private StartScript testScript;
	private UserVariable userVariable;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "testProject");

		testSprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(testSprite);

		testSprite.removeAllScripts();
		testScript = new StartScript();
		testSprite.addScript(testScript);

		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentSprite(testSprite);

		project.removeUserVariable(TEST_USERVARIABLE);

		userVariable = new UserVariable(TEST_USERVARIABLE);
		project.addUserVariable(userVariable);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testNestedIfBrick() {
		Formula formula = new Formula(1);
		formula.setRoot(
				new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
						new FormulaElement(ElementType.NUMBER, "1", null),
						new FormulaElement(ElementType.NUMBER, "2", null)));

		IfLogicBeginBrick ifElseBrick = new IfLogicBeginBrick(formula);

		IfLogicBeginBrick innerIfElseBrick = new IfLogicBeginBrick(formula);
		innerIfElseBrick.addBrickToIfBranch(new SetVariableBrick(new Formula(IF_TRUE_VALUE), userVariable));

		ifElseBrick.addBrickToIfBranch(innerIfElseBrick);

		testScript.addBrick(ifElseBrick);
		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1f);
		}

		assertEquals((double) IF_TRUE_VALUE, userVariable.getValue());
	}

	@Test
	public void testIfBrick() {
		Formula formula = new Formula(1);
		formula.setRoot(
				new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
						new FormulaElement(ElementType.NUMBER, "1", null),
						new FormulaElement(ElementType.NUMBER, "2", null)));

		IfLogicBeginBrick ifElseBrick = new IfLogicBeginBrick(formula);
		ifElseBrick.addBrickToIfBranch(new SetVariableBrick(new Formula(IF_TRUE_VALUE), userVariable));
		testScript.addBrick(ifElseBrick);

		testSprite.initializeEventThreads(EventId.START);
		testSprite.look.act(100f);

		assertEquals((double) IF_TRUE_VALUE, userVariable.getValue());
	}

	@Test
	public void testIfElseBrick() {
		Formula formula = new Formula(1);
		formula.setRoot(
				new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
						new FormulaElement(ElementType.NUMBER, "2", null),
						new FormulaElement(ElementType.NUMBER, "1", null)));

		IfLogicBeginBrick ifElseBrick = new IfLogicBeginBrick(formula);
		ifElseBrick.addBrickToElseBranch(new SetVariableBrick(new Formula(IF_FALSE_VALUE), userVariable));
		testScript.addBrick(ifElseBrick);

		testSprite.initializeEventThreads(EventId.START);
		testSprite.look.act(100f);

		assertEquals((double) IF_FALSE_VALUE, userVariable.getValue());
	}

	@Test
	public void testBrickWithValidStringFormula() {
		testFormula(new Formula(String.valueOf(TRUE)), (double) IF_TRUE_VALUE);
	}

	@Test
	public void testBrickWithInValidStringFormula() {
		testFormula(new Formula(String.valueOf(NOT_NUMERICAL_STRING)), 0.0);
	}

	@Test
	public void testNullFormula() throws Exception {
		Object userVariableExpected = userVariable.getValue();

		Action ifAction = testSprite.getActionFactory()
				.createSetVariableAction(testSprite, new SequenceAction(), new Formula(IF_TRUE_VALUE), userVariable);
		Action elseAction = testSprite.getActionFactory()
				.createSetVariableAction(testSprite, new SequenceAction(), new Formula(IF_FALSE_VALUE), userVariable);
		Action ifLogicAction = testSprite.getActionFactory()
				.createIfLogicAction(testSprite, new SequenceAction(), null, ifAction, elseAction);

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
		IfLogicBeginBrick ifThenElseBrick = new IfLogicBeginBrick(formula);
		ifThenElseBrick
				.addBrickToIfBranch(new SetVariableBrick(new Formula(IF_TRUE_VALUE), userVariable));
		ifThenElseBrick
				.addBrickToElseBranch(new SetVariableBrick(new Formula(IF_FALSE_VALUE), userVariable));

		testScript.addBrick(ifThenElseBrick);

		testSprite.initializeEventThreads(EventId.START);
		testSprite.look.act(1f);

		assertEquals(expected, userVariable.getValue());
	}
}
