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
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.HashMap;
import java.util.List;

public class IfLogicActionTest extends AndroidTestCase {

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testSprite = new Sprite("testSprite");
		project = new Project(null, "testProject");
	}

	public void testNestedIfBrick() throws InterruptedException {
		testSprite.removeAllScripts();

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

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
		project.addSprite(testSprite);

		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1f);
		}

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", IF_TRUE_VALUE, ((Double) userVariable.getValue()).intValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

	}

	public void testIfBrick() throws InterruptedException {
		testSprite.removeAllScripts();

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

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
		project.addSprite(testSprite);

		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(100f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", IF_TRUE_VALUE, ((Double) userVariable.getValue()).intValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

	}

	public void testIfElseBrick() throws InterruptedException {
		testSprite.removeAllScripts();

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

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
		project.addSprite(testSprite);

		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		testSprite.look.act(100f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", IF_FALSE_VALUE, ((Double) userVariable.getValue()).intValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
	}

	public void testBrickWithValidStringFormula() {
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);
		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		SetVariableBrick setVariableBrickIfTrue = new SetVariableBrick(testSprite, new Formula(IF_TRUE_VALUE),
				userVariable);
		SetVariableBrick setVariableBrickIfFalse = new SetVariableBrick(testSprite, new Formula(IF_FALSE_VALUE),
				userVariable);

		Formula validFormula = new Formula(String.valueOf(TRUE));

		testScript = new StartScript(testSprite);
		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, validFormula);
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);
		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(setVariableBrickIfTrue);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(setVariableBrickIfFalse);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);
		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", Integer.valueOf(IF_TRUE_VALUE).doubleValue(),
				userVariable.getValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
	}

	public void testBrickWithInValidStringFormula() {
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);
		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		SetVariableBrick setVariableBrickIfTrue = new SetVariableBrick(testSprite, new Formula(IF_TRUE_VALUE),
				userVariable);
		SetVariableBrick setVariableBrickIfFalse = new SetVariableBrick(testSprite, new Formula(IF_FALSE_VALUE),
				userVariable);

		Formula invalidFormula = new Formula(String.valueOf(NOT_NUMERICAL_STRING));

		testScript = new StartScript(testSprite);
		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, invalidFormula);
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);
		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(setVariableBrickIfTrue);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(setVariableBrickIfFalse);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);
		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", 0.0, userVariable.getValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
	}

	public void testNullFormula() {
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);
		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		SetVariableBrick setVariableBrickIfTrue = new SetVariableBrick(testSprite, new Formula(IF_TRUE_VALUE),
				userVariable);
		SetVariableBrick setVariableBrickIfFalse = new SetVariableBrick(testSprite, new Formula(IF_FALSE_VALUE),
				userVariable);

		testScript = new StartScript(testSprite);
		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, null);
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);
		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(setVariableBrickIfTrue);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(setVariableBrickIfFalse);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);
		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", 0.0, userVariable.getValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
	}

	public void testNotANumberFormula() {
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);
		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		SetVariableBrick setVariableBrickIfTrue = new SetVariableBrick(testSprite, new Formula(IF_TRUE_VALUE),
				userVariable);
		SetVariableBrick setVariableBrickIfFalse = new SetVariableBrick(testSprite, new Formula(IF_FALSE_VALUE),
				userVariable);

		testScript = new StartScript(testSprite);
		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, new Formula(Double.NaN));
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);
		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(setVariableBrickIfTrue);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(setVariableBrickIfFalse);
		testScript.addBrick(ifLogicEndBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);
		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", 0.0, userVariable.getValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
	}
}
