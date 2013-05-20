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
package org.catrobat.catroid.test.content.actions;

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

import android.test.AndroidTestCase;

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

		ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.addProjectUserVariable(TEST_USERVARIABLE);

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		SetVariableBrick setVariableBrick = new SetVariableBrick(testSprite, new Formula(IF_TRUE_VALUE), userVariable);

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "1", null), new FormulaElement(ElementType.NUMBER, "2", null)));

		testScript = new StartScript(testSprite);

		repeatBrick = new RepeatBrick(testSprite, 2);
		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, validFormula);
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);
		repeatBrick.setLoopEndBrick(new LoopEndBrick(testSprite, repeatBrick));

		ifLogicBeginBrick2 = new IfLogicBeginBrick(testSprite, validFormula);
		ifLogicElseBrick2 = new IfLogicElseBrick(testSprite, ifLogicBeginBrick2);
		ifLogicEndBrick2 = new IfLogicEndBrick(testSprite, ifLogicElseBrick2, ifLogicBeginBrick2);

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

		testSprite.createStartScriptActionSequence();
		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1f);
		}

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", IF_TRUE_VALUE, userVariable.getValue().intValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

	}

	public void testIfBrick() throws InterruptedException {
		testSprite.removeAllScripts();

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

		ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.addProjectUserVariable(TEST_USERVARIABLE);

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		SetVariableBrick setVariableBrick = new SetVariableBrick(testSprite, new Formula(IF_TRUE_VALUE), userVariable);

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "1", null), new FormulaElement(ElementType.NUMBER, "2", null)));

		testScript = new StartScript(testSprite);

		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, validFormula);
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);

		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(setVariableBrick);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(ifLogicEndBrick);

		testSprite.addScript(testScript);
		project.addSprite(testSprite);

		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		testSprite.createStartScriptActionSequence();
		testSprite.look.act(100f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", IF_TRUE_VALUE, userVariable.getValue().intValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

	}

	public void testIfElseBrick() throws InterruptedException {
		testSprite.removeAllScripts();

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.addProjectUserVariable(TEST_USERVARIABLE);

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		SetVariableBrick setVariableBrick = new SetVariableBrick(testSprite, new Formula(IF_FALSE_VALUE), userVariable);

		Formula invalidFormula = new Formula(1);
		invalidFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(ElementType.NUMBER, "2", null), new FormulaElement(ElementType.NUMBER, "1", null)));

		testScript = new StartScript(testSprite);

		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, invalidFormula);
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);

		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(setVariableBrick);
		testScript.addBrick(ifLogicEndBrick);

		testSprite.addScript(testScript);
		project.addSprite(testSprite);

		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		testSprite.createStartScriptActionSequence();

		testSprite.look.act(100f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		assertEquals("IfBrick not executed as expected", IF_FALSE_VALUE, userVariable.getValue().intValue());
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);

	}

}
