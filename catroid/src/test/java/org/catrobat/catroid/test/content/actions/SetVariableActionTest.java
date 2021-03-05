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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class SetVariableActionTest {

	private static final String TEST_USERVARIABLE = "testUservariable";
	private static final double SET_VARIABLE_VALUE = 17;
	private static final double INITIALIZED_VALUE = 0.0;
	private Sprite testSprite;
	private Project project;
	private UserVariable userVariable;

	@Before
	public void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);
		userVariable = new UserVariable(TEST_USERVARIABLE);
		project.addUserVariable(userVariable);
	}

	@Test
	public void testSetVariableWithNumericalFormula() {
		testSprite.getActionFactory().createSetVariableAction(testSprite, new SequenceAction(), new Formula(SET_VARIABLE_VALUE), userVariable).act(1f);
		assertEquals(SET_VARIABLE_VALUE, userVariable.getValue());
	}

	@Test
	public void testSetVariableWithInvalidUserVariable() {
		testSprite.getActionFactory().createSetVariableAction(testSprite, new SequenceAction(), new Formula(SET_VARIABLE_VALUE), null).act(1f);
		assertEquals(INITIALIZED_VALUE, userVariable.getValue());
	}

	@Test
	public void testSetVariableWithNumericalStringFormula() {
		String myString = "155";
		testSprite.getActionFactory().createSetVariableAction(testSprite, new SequenceAction(), new Formula(myString), userVariable).act(1f);
		assertEquals(Double.valueOf(myString), Double.valueOf((String) userVariable.getValue()));
	}

	@Test
	public void testSetVariableWithStringFormula() {
		String myString = "myString";
		testSprite.getActionFactory().createSetVariableAction(testSprite, new SequenceAction(), new Formula(myString), userVariable).act(1f);
		assertEquals(myString, (String) userVariable.getValue());
	}

	@Test
	public void testNullFormula() {
		testSprite.getActionFactory().createSetVariableAction(testSprite, new SequenceAction(), null, userVariable).act(1f);
		assertEquals(0d, userVariable.getValue());
	}

	@Test
	public void testNotANumberFormula() {
		testSprite.getActionFactory().createSetVariableAction(testSprite, new SequenceAction(), new Formula(Double.NaN), userVariable).act(1f);
		assertEquals(Double.NaN, userVariable.getValue());
	}
}
