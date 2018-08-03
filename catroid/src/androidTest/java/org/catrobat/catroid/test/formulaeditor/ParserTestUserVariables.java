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
package org.catrobat.catroid.test.formulaeditor;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ParserTestUserVariables {

	private static final double USER_VARIABLE_1_VALUE_TYPE_DOUBLE = 5d;
	private static final String PROJECT_USER_VARIABLE = "projectUserVariable";
	private static final double USER_VARIABLE_2_VALUE_TYPE_DOUBLE = 3.141592d;
	private static final String SPRITE_USER_VARIABLE = "spriteUserVariable";
	private static final double USER_VARIABLE_VALUE3 = 1.68d;
	private static final String USER_BRICK_VARIABLE = "userBrickVariable";
	private static final double USER_VARIABLE_RESET = 0.0d;
	private static final String USER_VARIABLE_3_VALUE_TYPE_STRING = "My Little User Variable";
	private static final String PROJECT_USER_VARIABLE_2 = "projectUserVariable2";
	private Sprite firstSprite;

	@Before
	public void setUp() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		firstSprite = new SingleSprite("firstSprite");
		StartScript startScript = new StartScript();
		ChangeSizeByNBrick changeBrick = new ChangeSizeByNBrick(10);
		firstSprite.addScript(startScript);
		startScript.addBrick(changeBrick);
		project.getDefaultScene().addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		UserBrick userBrick = new UserBrick(new UserScriptDefinitionBrick());
		ProjectManager.getInstance().setCurrentUserBrick(userBrick);

		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();

		dataContainer
				.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE, USER_VARIABLE_1_VALUE_TYPE_DOUBLE));
		dataContainer
				.addUserVariable(firstSprite, new UserVariable(SPRITE_USER_VARIABLE, USER_VARIABLE_2_VALUE_TYPE_DOUBLE));
		dataContainer
				.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE_2, USER_VARIABLE_3_VALUE_TYPE_STRING));

		dataContainer.addUserVariable(userBrick, new UserVariable(USER_BRICK_VARIABLE, USER_VARIABLE_VALUE3));
	}

	@Test
	public void testUserVariableInterpretation() {
		assertEquals(USER_VARIABLE_1_VALUE_TYPE_DOUBLE, interpretUserVariable(PROJECT_USER_VARIABLE));
		assertEquals(USER_VARIABLE_2_VALUE_TYPE_DOUBLE, interpretUserVariable(SPRITE_USER_VARIABLE));
		assertEquals(USER_VARIABLE_3_VALUE_TYPE_STRING, interpretUserVariable(PROJECT_USER_VARIABLE_2));
		assertEquals(USER_VARIABLE_VALUE3, interpretUserVariable(USER_BRICK_VARIABLE));
	}

	@Test
	public void testUserVariableResetting() {
		ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer().resetUserData();

		assertEquals(USER_VARIABLE_RESET, interpretUserVariable(PROJECT_USER_VARIABLE));
		assertEquals(USER_VARIABLE_RESET, interpretUserVariable(SPRITE_USER_VARIABLE));
		assertEquals(USER_VARIABLE_RESET, interpretUserVariable(PROJECT_USER_VARIABLE_2));
		assertEquals(USER_VARIABLE_VALUE3, interpretUserVariable(USER_BRICK_VARIABLE));
	}

	@Test
	public void testNotExistingUserVariable() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.USER_VARIABLE, "NOT_EXISTING_USER_VARIABLE", 0);
	}

	private Object interpretUserVariable(String userVariableName) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, userVariableName));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		Formula userVariableFormula = new Formula(parseTree);

		return userVariableFormula.interpretObject(firstSprite);
	}
}
