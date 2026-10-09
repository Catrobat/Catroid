/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor.parser;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ParserTestUserVariables {

	private Project project;
	private Sprite sprite;
	private Scope scope;

	@Before
	public void setUp() {
		project = new Project(MockUtil.mockContextForProject(), "testProject");
		sprite = new Sprite("testSprite");
		StartScript startScript = new StartScript();
		ChangeSizeByNBrick changeBrick = new ChangeSizeByNBrick(10);
		sprite.addScript(startScript);
		startScript.addBrick(changeBrick);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		scope = new Scope(project, sprite, new SequenceAction());
	}

	@Test
	public void testUserVariableInterpretationInteger() {
		final String userVariable1 = "userVariable1";
		final int userVariable1Value = 123;
		final String userVariable2 = "userVariable2";
		final int userVariable2Value = 1000;
		final String userVariable3 = "userVariable3";
		final int userVariable3Value = -1_000_000;
		project.addUserVariable(new UserVariable(userVariable1, userVariable1Value));
		project.addUserVariable(new UserVariable(userVariable2, userVariable2Value));
		project.addUserVariable(new UserVariable(userVariable3, userVariable3Value));

		assertEquals((double) userVariable1Value, interpretUserVariable(userVariable1));
		assertEquals((double) userVariable2Value, interpretUserVariable(userVariable2));
		assertEquals((double) userVariable3Value, interpretUserVariable(userVariable3));
	}

	@Test
	public void testUserVariableInterpretationDouble() {
		final String userVariable1 = "userVariable1";
		final double userVariable1Value = 5d;
		final String userVariable2 = "userVariable2";
		final double userVariable2Value = 3.141592d;
		project.addUserVariable(new UserVariable(userVariable1, userVariable1Value));
		project.addUserVariable(new UserVariable(userVariable2, userVariable2Value));

		assertEquals(userVariable1Value, interpretUserVariable(userVariable1));
		assertEquals(userVariable2Value, interpretUserVariable(userVariable2));
	}

	@Test
	public void testUserVariableInterpretationString() {
		final String userVariable1 = "userVariable1";
		final String userVariable1Value = "Hello";
		project.addUserVariable(new UserVariable(userVariable1, userVariable1Value));

		assertEquals(userVariable1Value, interpretUserVariable(userVariable1));
	}

	@Test
	public void testUserVariableInterpretationCharacter() {
		final String userVariable1 = "userVariable1";
		final char userVariable1Value = 'X';
		project.addUserVariable(new UserVariable(userVariable1, userVariable1Value));

		assertEquals(userVariable1Value, interpretUserVariable(userVariable1));
	}

	@Test
	public void testUserVariableInterpretationBoolean() {
		final String userVariable1 = "userVariable1";
		final boolean userVariable1Value = true;
		final String userVariable2 = "userVariable2";
		final boolean userVariable2Value = false;
		project.addUserVariable(new UserVariable(userVariable1, userVariable1Value));
		project.addUserVariable(new UserVariable(userVariable2, userVariable2Value));

		assertEquals(1.0, interpretUserVariable(userVariable1));
		assertEquals(0.0, interpretUserVariable(userVariable2));
	}

	@Test
	public void testSpriteUserVariableInterpretation() {
		final String spriteUserVariable = "spriteUserVariable";
		final String spriteUserVariableValue = "My Little User Variable";
		sprite.addUserVariable(new UserVariable(spriteUserVariable, spriteUserVariableValue));

		assertEquals(spriteUserVariableValue, interpretUserVariable(spriteUserVariable));
	}

	@Test
	public void testUserVariableResetting() {
		final String userVariable1 = "userVariable1";
		final String spriteUserVariable = "spriteUserVariable";
		project.addUserVariable(new UserVariable(userVariable1, "Test"));
		sprite.addUserVariable(new UserVariable(spriteUserVariable, "Test"));
		UserDataWrapper.resetAllUserData(project);

		assertEquals(0.0, interpretUserVariable(userVariable1));
		assertEquals(0.0, interpretUserVariable(spriteUserVariable));
	}

	@Test
	public void testUserVariableNotExisting() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.USER_VARIABLE, "NOT_EXISTING_USER_VARIABLE", 0, scope);
	}

	private Object interpretUserVariable(String userVariableName) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, userVariableName));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);
		Formula userVariableFormula = new Formula(parseTree);

		return userVariableFormula.interpretObject(scope);
	}
}
