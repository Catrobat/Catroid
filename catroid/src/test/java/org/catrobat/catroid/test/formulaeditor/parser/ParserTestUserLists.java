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
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ParserTestUserLists {

	private static final String PROJECT_USER_LIST_NAME_2 = "project_user_list_2";
	private static final String PROJECT_USER_LIST_NAME = "project_user_list";
	private static final String SPRITE_USER_LIST_NAME = "sprite_user_list";
	private static final String EMPTY_USER_LIST_INTERPRETATION_VALUE = "";

	private static final String PROJECT_USER_VARIABLE = "projectUserVariable";

	private Sprite sprite;
	private Project project;
	private Scope scope;

	@Before
	public void setUp() {
		project = new Project(MockUtil.mockContextForProject(), "testProject");
		sprite = new Sprite("sprite");

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
	public void testUserListInterpretationMultipleStringAndNumbers() {
		List<Object> userListValuesMultipleNumbersStringInteger = new ArrayList<>();
		userListValuesMultipleNumbersStringInteger.add(1.0);
		userListValuesMultipleNumbersStringInteger.add("2");
		userListValuesMultipleNumbersStringInteger.add(3.0);
		userListValuesMultipleNumbersStringInteger.add("4");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbersStringInteger)));

		assertEquals("1234", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationSingleNumberString() {
		List<Object> userListValuesSingleNumberString = new ArrayList<>();
		userListValuesSingleNumberString.add("1");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesSingleNumberString)));
		assertEquals("1", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMultipleNumberString() {
		List<Object> userListValuesMultipleNumberString = new ArrayList<>();
		userListValuesMultipleNumberString.add("1");
		userListValuesMultipleNumberString.add("2");
		userListValuesMultipleNumberString.add("3");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumberString)));
		assertEquals("123", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMultipleNumbers() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertEquals("123", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMultipleBigNumbers() {
		List<Object> userListValuesMultipleBigNumbers = new ArrayList<>();
		userListValuesMultipleBigNumbers.add(1);
		userListValuesMultipleBigNumbers.add(2_000);
		userListValuesMultipleBigNumbers.add(3_000_000);

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleBigNumbers)));
		assertEquals("1 2000 3000000", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMultipleBigNegativeNumbers() {
		List<Object> userListValuesMultipleBigNegativeNumbers = new ArrayList<>();
		userListValuesMultipleBigNegativeNumbers.add(-1);
		userListValuesMultipleBigNegativeNumbers.add(-2_000);
		userListValuesMultipleBigNegativeNumbers.add(-3_000_000);

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleBigNegativeNumbers)));
		assertEquals("-1 -2000 -3000000", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMultipleDoubles() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.1);
		userListValuesMultipleNumbers.add(2.2);
		userListValuesMultipleNumbers.add(3.3);

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertEquals("1.1 2.2 3.3", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationStrings() {
		List<Object> userListValuesStrings = new ArrayList<>();
		userListValuesStrings.add("Hello");
		userListValuesStrings.add("world");
		userListValuesStrings.add("!");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesStrings)));
		assertEquals("Hello world !", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationCharacters() {
		List<Object> userListValuesCharacters = new ArrayList<>();
		userListValuesCharacters.add('H');
		userListValuesCharacters.add('e');
		userListValuesCharacters.add('l');
		userListValuesCharacters.add('l');
		userListValuesCharacters.add('o');

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesCharacters)));
		assertEquals("Hello", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMixed() {
		List<Object> userListValuesMixed = new ArrayList<>();
		userListValuesMixed.add("Hello");
		userListValuesMixed.add("!");
		userListValuesMixed.add(42.0);
		userListValuesMixed.add(3.1415);
		userListValuesMixed.add("WORLDS");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMixed)));
		assertEquals("Hello ! 42 3.1415 WORLDS", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationEmptyList() {
		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME)));
		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListReset() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(project.addUserList(new UserList(SPRITE_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME_2, userListValuesMultipleNumbers)));

		UserDataWrapper.resetAllUserData(project);

		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(SPRITE_USER_LIST_NAME));
		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME));
		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME_2));
	}

	@Test
	public void testNotExistingUserList() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.USER_LIST, "NOT_EXISTING_USER_LIST", 0, scope);
	}

	@Test
	public void testFunctionListItem() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE)));

		String index = "1";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0, scope);

		index = "0";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", scope);

		index = "4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", scope);

		index = "1.4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0, scope);

		index = "1.0";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.STRING, index, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0, scope);

		UserDataWrapper.getUserVariable(PROJECT_USER_VARIABLE, scope).setValue("1");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0, scope);

		UserDataWrapper.getUserVariable(PROJECT_USER_VARIABLE, scope).setValue("0");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", scope);

		UserDataWrapper.getUserVariable(PROJECT_USER_VARIABLE, scope).setValue("4");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", scope);

		UserDataWrapper.getUserVariable(PROJECT_USER_VARIABLE, scope).setValue(1d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0, scope);

		UserDataWrapper.getUserVariable(PROJECT_USER_VARIABLE, scope).setValue(0d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", scope);

		UserDataWrapper.getUserVariable(PROJECT_USER_VARIABLE, scope).setValue(4d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", scope);
	}

	@Test
	public void testFunctionLength() {
		List<Object> userListValuesStrings = new ArrayList<>();
		userListValuesStrings.add("Hello");
		userListValuesStrings.add("my");
		userListValuesStrings.add("worlds");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME)));

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, (double) 0, scope);

		UserDataWrapper.getUserList(PROJECT_USER_LIST_NAME, scope).setValue(userListValuesStrings);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, (double) 15, scope);

		ArrayList<Object> userList = new ArrayList<>();
		userList.add("0");
		UserDataWrapper.getUserList(PROJECT_USER_LIST_NAME, scope).setValue(userList);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, (double) 1, scope);

		userList.clear();
		userList.add("0.0");
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, (double) 3, scope);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, "0", (double) 1, scope);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, "0.0", (double) 3, scope);
	}

	@Test
	public void testFunctionNumberOfItems() {
		List<Object> userListValuesStrings = new ArrayList<>();
		userListValuesStrings.add("Hello");
		userListValuesStrings.add("my");
		userListValuesStrings.add("worlds");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME)));

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.NUMBER_OF_ITEMS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, (double) 0, scope);

		UserDataWrapper.getUserList(PROJECT_USER_LIST_NAME, scope).setValue(userListValuesStrings);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, (double) 15, scope);
	}

	@Test
	public void testFunctionContains() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE, 1.0)));

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, InternTokenType.NUMBER, "1", 1d, scope);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, InternTokenType.STRING, "1", 1d, scope);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, InternTokenType.STRING, "1.00", 1d, scope);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, InternTokenType.NUMBER, "0", 0d, scope);

		List<Object> userListValuesStringsAndNumbers = new ArrayList<>();
		userListValuesStringsAndNumbers.add("Hello");
		userListValuesStringsAndNumbers.add(42.0);
		userListValuesStringsAndNumbers.add("WORLDS");

		UserDataWrapper.getUserList(PROJECT_USER_LIST_NAME, scope).setValue(userListValuesStringsAndNumbers);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, InternTokenType.STRING, "Hello", 1d, scope);

		UserDataWrapper.getUserVariable(PROJECT_USER_VARIABLE, scope).setValue("Hello");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, 1d, scope);
	}

	@Test
	public void testFunctionFlatten() {
		List<Object> userListValuesMultipleStrings = new ArrayList<>();
		userListValuesMultipleStrings.add("a");
		userListValuesMultipleStrings.add("2");
		userListValuesMultipleStrings.add("c");
		userListValuesMultipleStrings.add("4");
		userListValuesMultipleStrings.add("d");

		assertTrue(project.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleStrings)));
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.FLATTEN, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "a2c4d", scope);

		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);
		userListValuesMultipleNumbers.add(4.0);
		userListValuesMultipleNumbers.add(5.0);

		UserDataWrapper.getUserList(PROJECT_USER_LIST_NAME, scope).setValue(userListValuesMultipleNumbers);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.FLATTEN, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "12345", scope);
	}

	private Object interpretUserList(String userListName) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.USER_LIST, userListName));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);
		Formula userVariableFormula = new Formula(parseTree);

		return userVariableFormula.interpretObject(scope);
	}
}
