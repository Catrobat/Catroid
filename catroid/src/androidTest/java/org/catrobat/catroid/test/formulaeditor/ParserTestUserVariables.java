/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;

import java.util.LinkedList;
import java.util.List;

public class ParserTestUserVariables extends AndroidTestCase {

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

	@Override
	protected void setUp() {
		Project project = new Project(null, "testProject");
		firstSprite = new Sprite("firstSprite");
		StartScript startScript = new StartScript();
		ChangeSizeByNBrick changeBrick = new ChangeSizeByNBrick(10);
		firstSprite.addScript(startScript);
		startScript.addBrick(changeBrick);
		project.getDefaultScene().addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		UserBrick userBrick = new UserBrick(new UserScriptDefinitionBrick());
		ProjectManager.getInstance().setCurrentUserBrick(userBrick);
		DataContainer userVariableContainer = ProjectManager.getInstance().getCurrentScene()
				.getDataContainer();
		userVariableContainer.addProjectUserVariable(PROJECT_USER_VARIABLE).setValue(USER_VARIABLE_1_VALUE_TYPE_DOUBLE);
		userVariableContainer.addSpriteUserVariableToSprite(firstSprite, SPRITE_USER_VARIABLE).setValue(
				USER_VARIABLE_2_VALUE_TYPE_DOUBLE);
		userVariableContainer.addProjectUserVariable(PROJECT_USER_VARIABLE_2).setValue(
				USER_VARIABLE_3_VALUE_TYPE_STRING);
		userVariableContainer.addUserBrickVariableToUserBrick(userBrick, USER_BRICK_VARIABLE, 0d)
				.setValue(USER_VARIABLE_VALUE3);
	}

	public void testUserVariableInterpretation() {
		assertEquals("Formula interpretation of ProjectUserVariable is not as expected",
				USER_VARIABLE_1_VALUE_TYPE_DOUBLE, interpretUserVariable(PROJECT_USER_VARIABLE));
		assertEquals("Formula interpretation of SpriteUserVariable is not as expected",
				USER_VARIABLE_2_VALUE_TYPE_DOUBLE, interpretUserVariable(SPRITE_USER_VARIABLE));
		assertEquals("Formula interpretation of ProjectUserVariable2 is not as expected",
				USER_VARIABLE_3_VALUE_TYPE_STRING, interpretUserVariable(PROJECT_USER_VARIABLE_2));
		assertEquals("Formula interpretation of UserBrickVariable is not as expected",
				USER_VARIABLE_VALUE3, interpretUserVariable(USER_BRICK_VARIABLE));
	}

	public void testUserVariableResetting() {
		ProjectManager.getInstance().getCurrentScene().getDataContainer().resetAllDataObjects();

		assertEquals("ProjectUserVariable did not reset", USER_VARIABLE_RESET,
				interpretUserVariable(PROJECT_USER_VARIABLE));
		assertEquals("SpriteUserVariable did not reset", USER_VARIABLE_RESET,
				interpretUserVariable(SPRITE_USER_VARIABLE));
		assertEquals("ProjectUserVariable2 did not reset", USER_VARIABLE_RESET,
				interpretUserVariable(PROJECT_USER_VARIABLE_2));
		assertEquals("UserBrickVariable was reset, but shouldn't be", USER_VARIABLE_VALUE3,
				interpretUserVariable(USER_BRICK_VARIABLE));
	}

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
