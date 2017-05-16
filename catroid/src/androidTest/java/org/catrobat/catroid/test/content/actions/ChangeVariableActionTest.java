/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class ChangeVariableActionTest extends AndroidTestCase {

	private static final String NOT_NUMERICAL_STRING = "changeVariable";
	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final double CHANGE_VARIABLE_VALUE = 11;
	private static final double INITIALIZED_VALUE = 0.0;
	private Sprite testSprite;
	private UserVariable userVariable;
	Project project;

	@Override
	protected void setUp() throws Exception {
		testSprite = new SingleSprite("testSprite");
		project = new Project(null, "testProject");
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().getCurrentScene().getDataContainer().addProjectUserVariable(TEST_USERVARIABLE);
		userVariable = ProjectManager.getInstance().getCurrentScene().getDataContainer()
				.getUserVariable(null, TEST_USERVARIABLE);
		super.setUp();
	}

	public void testChangeUserVariableWithNumericalFormula() {
		testSprite.getActionFactory().createChangeVariableAction(testSprite, new Formula(CHANGE_VARIABLE_VALUE), userVariable).act(1f);
		assertEquals("UserVariable did not change", CHANGE_VARIABLE_VALUE, userVariable.getValue());
	}

	public void testChangeUserVariableInvalidUserVariable() {
		testSprite.getActionFactory().createChangeVariableAction(testSprite, new Formula(CHANGE_VARIABLE_VALUE), null).act(1f);
		assertEquals("UserVariable changed, but should not!", INITIALIZED_VALUE, userVariable.getValue());
	}

	public void testChangeUserVariableWithNumericalStringFormula() {
		testSprite.getActionFactory().createChangeVariableAction(testSprite, new Formula(String.valueOf(CHANGE_VARIABLE_VALUE)), userVariable).act(1f);
		assertEquals("UserVariable did not change", CHANGE_VARIABLE_VALUE, userVariable.getValue());
	}

	public void testChangeUserVariableWithStringFormula() {
		testSprite.getActionFactory().createChangeVariableAction(testSprite, new Formula(NOT_NUMERICAL_STRING), userVariable).act(1f);
		assertEquals("UserVariable should not have changed!", INITIALIZED_VALUE, userVariable.getValue());
	}

	public void testNullFormula() {
		testSprite.getActionFactory().createChangeVariableAction(testSprite, null, userVariable).act(1f);
		assertEquals("UserVariable should not have changed!", INITIALIZED_VALUE, userVariable.getValue());
	}

	public void testNotANumberFormula() {
		testSprite.getActionFactory().createChangeVariableAction(testSprite, new Formula(Double.NaN), userVariable).act(1f);
		assertEquals("UserVariable should not have changed!", INITIALIZED_VALUE, userVariable.getValue());
	}
}
