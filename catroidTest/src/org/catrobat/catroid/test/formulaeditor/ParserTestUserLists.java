/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ParserTestUserLists extends AndroidTestCase {

	private static final String PROJECT_USER_LIST_NAME_2 = "project_user_list_2";
	private static final String PROJECT_USER_LIST_NAME = "project_user_list";
	private static final String SPRITE_USER_LIST_NAME = "sprite_user_list";
	private Sprite firstSprite;
	private static final Double DELTA = 0.01;

	private static final Double EMPTY_USER_LIST_INTERPRETATION_VALUE = 0d;

	private static final String USER_LIST_VALUES_SINGLE_NUMBER_STRING_INTERPRETATION_VALUE = "1";
	private static final List<Object> USER_LIST_VALUES_SINGLE_NUMBER_STRING = new ArrayList<Object>();
	static {
		USER_LIST_VALUES_SINGLE_NUMBER_STRING.add("1");
	}

	private static final String USER_LIST_VALUES_MULTIPLE_NUMBER_STRING_INTERPRETATION_VALUE = "123";
	private static final List<Object> USER_LIST_VALUES_MULTIPLE_NUMBER_STRING = new ArrayList<Object>();
	static {
		USER_LIST_VALUES_MULTIPLE_NUMBER_STRING.add("1");
		USER_LIST_VALUES_MULTIPLE_NUMBER_STRING.add("2");
		USER_LIST_VALUES_MULTIPLE_NUMBER_STRING.add("3");
	}

	private static final String USER_LIST_VALUES_MULTIPLE_NUMBERS_INTERPRETATION_VALUE = "123";
	private static final List<Object> USER_LIST_VALUES_MULTIPLE_NUMBERS = new ArrayList<Object>();
	static {
		USER_LIST_VALUES_MULTIPLE_NUMBERS.add(1.0);
		USER_LIST_VALUES_MULTIPLE_NUMBERS.add(2.0);
		USER_LIST_VALUES_MULTIPLE_NUMBERS.add(3.0);
	}

	private static final String USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER_INTERPRETATION_VALUE = "1234";
	private static final List<Object> USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER = new ArrayList<Object>();
	static {
		USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER.add(1.0);
		USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER.add("2");
		USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER.add(3.0);
		USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER.add("4");
	}

	private static final String USER_LIST_VALUES_STRINGS_AND_NUMBERS_INTERPRETATION_VALUE = "Hello 42.0 WORLDS";
	private static final List<Object> USER_LIST_VALUES_STRINGS_AND_NUMBERS = new ArrayList<Object>();
	static {
		USER_LIST_VALUES_STRINGS_AND_NUMBERS.add("Hello");
		USER_LIST_VALUES_STRINGS_AND_NUMBERS.add(42.0);
		USER_LIST_VALUES_STRINGS_AND_NUMBERS.add("WORLDS");
	}

	private static final int USER_LIST_VALUES_STRINGS_LENGTH_INTERPRETATION_VALUE = 15;
	private static final List<Object> USER_LIST_VALUES_STRINGS = new ArrayList<Object>();
	static {
		USER_LIST_VALUES_STRINGS.add("Hello");
		USER_LIST_VALUES_STRINGS.add("my");
		USER_LIST_VALUES_STRINGS.add("worlds");
	}

	private static final String PROJECT_USER_VARIABLE = "projectUserVariable";

	private DataContainer dataContainer;

	@Override
	protected void setUp() {
		Project project = new Project(null, "testProject");
		firstSprite = new Sprite("firstSprite");
		StartScript startScript = new StartScript();
		ChangeSizeByNBrick changeBrick = new ChangeSizeByNBrick(10);
		firstSprite.addScript(startScript);
		startScript.addBrick(changeBrick);
		project.addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		dataContainer.addProjectUserList(PROJECT_USER_LIST_NAME);
		dataContainer.addSpriteUserListToSprite(firstSprite, SPRITE_USER_LIST_NAME);
		dataContainer.addProjectUserList(PROJECT_USER_LIST_NAME_2);
	}

	public void testUserListInterpretationMultipleStringAndNumbers() {
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(
				USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER);
		assertEquals("Formula interpretation of List is not as expected",
				USER_LIST_VALUES_MULTIPLE_NUMBERS_STRING_INTEGER_INTERPRETATION_VALUE,
				interpretUserList(PROJECT_USER_LIST_NAME));
	}

	public void testUserListInterpretationSingleNumberString() {
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(
				USER_LIST_VALUES_SINGLE_NUMBER_STRING);
		assertEquals("Formula interpretation of List is not as expected",
				USER_LIST_VALUES_SINGLE_NUMBER_STRING_INTERPRETATION_VALUE,
				interpretUserList(PROJECT_USER_LIST_NAME));
	}

	public void testUserListInterpretationMultipleNumberString() {
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(
				USER_LIST_VALUES_MULTIPLE_NUMBER_STRING);
		assertEquals("Formula interpretation of List is not as expected",
				USER_LIST_VALUES_MULTIPLE_NUMBER_STRING_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME));
	}

	public void testUserListInterpretationMultipleNumbers() {
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_MULTIPLE_NUMBERS);
		assertEquals("Formula interpretation of List is not as expected",
				USER_LIST_VALUES_MULTIPLE_NUMBERS_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME));
	}

	public void testUserListInterpretationStringsAndNumbers() {
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite)
				.setList(USER_LIST_VALUES_STRINGS_AND_NUMBERS);
		assertEquals("Formula interpretation of List is not as expected",
				USER_LIST_VALUES_STRINGS_AND_NUMBERS_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME));
	}

	public void testUserListInterpretationEmptyList() {
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).getList().clear();

		assertEquals("Formula interpretation of List is not as expected", EMPTY_USER_LIST_INTERPRETATION_VALUE,
				(Double) interpretUserList(PROJECT_USER_LIST_NAME), DELTA);
	}

	public void testUserListReset() {
		dataContainer.addSpriteUserList(SPRITE_USER_LIST_NAME);
		dataContainer.addSpriteUserList(PROJECT_USER_LIST_NAME_2);
		dataContainer.addSpriteUserList(PROJECT_USER_LIST_NAME);

		dataContainer.getUserList(SPRITE_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_MULTIPLE_NUMBERS);
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_MULTIPLE_NUMBERS);
		dataContainer.getUserList(PROJECT_USER_LIST_NAME_2, firstSprite).setList(USER_LIST_VALUES_MULTIPLE_NUMBERS);

		dataContainer.resetAllDataObjects();

		assertEquals("Sprite UserList did not reset", EMPTY_USER_LIST_INTERPRETATION_VALUE,
				interpretUserList(SPRITE_USER_LIST_NAME));
		assertEquals("Project UserList did not reset", EMPTY_USER_LIST_INTERPRETATION_VALUE,
				interpretUserList(PROJECT_USER_LIST_NAME));
		assertEquals("Project UserList 2 did not reset", EMPTY_USER_LIST_INTERPRETATION_VALUE,
				interpretUserList(PROJECT_USER_LIST_NAME_2));
	}

	public void testNotExistingUserList() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.USER_LIST, "NOT_EXISTING_USER_LIST", 0);
	}

	public void testFunctionListItem() {
		dataContainer.addSpriteUserList(PROJECT_USER_LIST_NAME);
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_MULTIPLE_NUMBERS);
		dataContainer.addProjectUserVariable(PROJECT_USER_VARIABLE);

		String index = "1";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				firstSprite);

		index = "0";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				firstSprite);

		index = "4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				firstSprite);

		index = "1.4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				firstSprite);

		index = "1.0";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.STRING, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				firstSprite);

		dataContainer.getUserVariable(PROJECT_USER_VARIABLE, firstSprite).setValue("1");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				firstSprite);

		dataContainer.getUserVariable(PROJECT_USER_VARIABLE, firstSprite).setValue("0");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				firstSprite);

		dataContainer.getUserVariable(PROJECT_USER_VARIABLE, firstSprite).setValue("4");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				firstSprite);

		dataContainer.getUserVariable(PROJECT_USER_VARIABLE, firstSprite).setValue(1d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				firstSprite);

		dataContainer.getUserVariable(PROJECT_USER_VARIABLE, firstSprite).setValue(0d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				firstSprite);

		dataContainer.getUserVariable(PROJECT_USER_VARIABLE, firstSprite).setValue(4d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				firstSprite);
	}

	public void testFunctionLength() {
		dataContainer.addProjectUserList(PROJECT_USER_LIST_NAME);
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(new ArrayList<Object>());

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				(double) 0, firstSprite);

		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_STRINGS);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				(double) USER_LIST_VALUES_STRINGS_LENGTH_INTERPRETATION_VALUE, firstSprite);

		ArrayList<Object> userList = new ArrayList<Object>();
		userList.add("0");
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(userList);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				(double) 1, firstSprite);

		userList.clear();
		userList.add("0.0");
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				(double) 3, firstSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, "0",
				(double) 1, firstSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, "0.0",
				(double) 3, firstSprite);
	}

	public void testFunctionNumberOfItems() {
		dataContainer.addProjectUserList(PROJECT_USER_LIST_NAME);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.NUMBER_OF_ITEMS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				(double) 0, firstSprite);

		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_STRINGS);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				(double) USER_LIST_VALUES_STRINGS_LENGTH_INTERPRETATION_VALUE, firstSprite);
	}

	public void testFunctionContains() {
		dataContainer.addProjectUserList(PROJECT_USER_LIST_NAME);
		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_MULTIPLE_NUMBERS);
		dataContainer.addProjectUserVariable(PROJECT_USER_VARIABLE);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				InternTokenType.NUMBER, "1", 1d, firstSprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				InternTokenType.STRING, "1", 1d, firstSprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				InternTokenType.STRING, "1.00", 1d, firstSprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				InternTokenType.NUMBER, "0", 0d, firstSprite);

		dataContainer.getUserList(PROJECT_USER_LIST_NAME, firstSprite).setList(USER_LIST_VALUES_STRINGS_AND_NUMBERS);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				InternTokenType.STRING, "Hello", 1d, firstSprite);

		dataContainer.getUserVariable(PROJECT_USER_VARIABLE, firstSprite).setValue("Hello");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, 1d, firstSprite);
	}

	private Object interpretUserList(String userListName) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.USER_LIST, userListName));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		Formula userVariableFormula = new Formula(parseTree);

		return userVariableFormula.interpretObject(firstSprite);
	}
}
