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
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.HashMap;
import java.util.List;

public class ChangeVariableActionTest extends AndroidTestCase {

	private static final String NOT_NUMERICAL_STRING = "changeVariable";
	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final double CHANGE_VARIABLE_VALUE = 11;
	private static final double INITIALIZED_VALUE = 0.0;
	private Sprite testSprite;
	private StartScript testScript;
	private Project project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testSprite = new Sprite("testSprite");
		project = new Project(null, "testProject");
		testScript = new StartScript();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().addProjectUserVariable(TEST_USERVARIABLE);
	}

	public void testChangeUserVariableWithNumericalFormula() {

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		ChangeVariableBrick changeBrick = new ChangeVariableBrick(new Formula(CHANGE_VARIABLE_VALUE),
				userVariable);

		testScript.addBrick(changeBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);
		assertEquals("UserVariable did not change", CHANGE_VARIABLE_VALUE, userVariable.getValue());
	}

	public void testChangeUserVariableInvalidUserVariable() {

		ChangeVariableBrick changeBrick = new ChangeVariableBrick(CHANGE_VARIABLE_VALUE);
		testScript.addBrick(changeBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);
		assertEquals("UserVariable changed, but should not!", INITIALIZED_VALUE, userVariable.getValue());
	}

	public void testChangeUserVariableWithNumericalStringFormula() {

		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		Formula changeFormula = new Formula(String.valueOf(CHANGE_VARIABLE_VALUE));
		ChangeVariableBrick changeVariableBrick = new ChangeVariableBrick(changeFormula, userVariable);
		testScript.addBrick(changeVariableBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);
		assertEquals("UserVariable did not change!", Double.valueOf(CHANGE_VARIABLE_VALUE), userVariable.getValue());
	}

	public void testChangeUserVariableWithStringFormula() {
		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		Formula validFormula = new Formula(NOT_NUMERICAL_STRING);
		ChangeVariableBrick changeVariableBrick = new ChangeVariableBrick(validFormula, userVariable);
		testScript.addBrick(changeVariableBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);


		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);
		assertEquals("UserVariable should not have changed!", INITIALIZED_VALUE, userVariable.getValue());
	}

	public void testNullFormula() {
		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		ChangeVariableBrick changeVariableBrick = new ChangeVariableBrick(testSprite, null, userVariable);
		testScript.addBrick(changeVariableBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);
		assertEquals("UserVariable should not have changed!", INITIALIZED_VALUE, userVariable.getValue());
	}

	public void testNotANumberFormula() {
		UserVariable userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);

		ChangeVariableBrick changeVariableBrick = new ChangeVariableBrick(testSprite, new Formula(Double.NaN),
				userVariable);
		testScript.addBrick(changeVariableBrick);
		testSprite.addScript(testScript);
		project.addSprite(testSprite);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
		testSprite.look.act(1f);

		userVariable = ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.getUserVariable(TEST_USERVARIABLE, null);
		assertEquals("UserVariable should not have changed!", INITIALIZED_VALUE, userVariable.getValue());
	}
}
