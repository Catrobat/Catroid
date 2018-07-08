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
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ParserTestUserLists {

	private static final String PROJECT_USER_LIST_NAME_2 = "project_user_list_2";
	private static final String PROJECT_USER_LIST_NAME = "project_user_list";
	private static final String SPRITE_USER_LIST_NAME = "sprite_user_list";

	private static final String PROJECT_USER_VARIABLE = "projectUserVariable";

	private static final String EMPTY_USER_LIST_INTERPRETATION_VALUE = "";

	private Sprite sprite;
	private DataContainer dataContainer;

	@Before
	public void setUp() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		sprite = new SingleSprite("sprite");

		StartScript startScript = new StartScript();
		ChangeSizeByNBrick changeBrick = new ChangeSizeByNBrick(10);
		sprite.addScript(startScript);
		startScript.addBrick(changeBrick);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();
	}

	@Test
	public void testUserListInterpretationMultipleStringAndNumbers() {
		List<Object> userListValuesMultipleNumbersStringInteger = new ArrayList<>();
		userListValuesMultipleNumbersStringInteger.add(1.0);
		userListValuesMultipleNumbersStringInteger.add("2");
		userListValuesMultipleNumbersStringInteger.add(3.0);
		userListValuesMultipleNumbersStringInteger.add("4");

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME,
				userListValuesMultipleNumbersStringInteger)));

		assertEquals("1234",
				interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationSingleNumberString() {
		List<Object> userListValuesSingleNumberString = new ArrayList<>();
		userListValuesSingleNumberString.add("1");

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesSingleNumberString)));
		assertEquals("1", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMultipleNumberString() {
		List<Object> userListValuesMultipleNumberString = new ArrayList<>();
		userListValuesMultipleNumberString.add("1");
		userListValuesMultipleNumberString.add("2");
		userListValuesMultipleNumberString.add("3");

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumberString)));
		assertEquals("123", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationMultipleNumbers() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertEquals("123", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationStringsAndNumbers() {
		List<Object> userListValuesStringsAndNumbers = new ArrayList<>();
		userListValuesStringsAndNumbers.add("Hello");
		userListValuesStringsAndNumbers.add(42.0);
		userListValuesStringsAndNumbers.add("WORLDS");

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesStringsAndNumbers)));
		assertEquals("Hello 42.0 WORLDS", interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListInterpretationEmptyList() {
		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME)));
		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME));
	}

	@Test
	public void testUserListReset() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(dataContainer.addUserList(new UserList(SPRITE_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME_2, userListValuesMultipleNumbers)));

		dataContainer.resetUserData();

		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(SPRITE_USER_LIST_NAME));
		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME));
		assertEquals(EMPTY_USER_LIST_INTERPRETATION_VALUE, interpretUserList(PROJECT_USER_LIST_NAME_2));
	}

	@Test
	public void testNotExistingUserList() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.USER_LIST,
				"NOT_EXISTING_USER_LIST", 0);
	}

	@Test
	public void testFunctionListItem() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(dataContainer.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE)));

		String index = "1";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				sprite);

		index = "0";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				sprite);

		index = "4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "",
				sprite);

		index = "1.4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				sprite);

		index = "1.0";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.STRING, index,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0,
				sprite);

		dataContainer.getUserVariable(sprite, PROJECT_USER_VARIABLE).setValue("1");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0, sprite);

		dataContainer.getUserVariable(sprite, PROJECT_USER_VARIABLE).setValue("0");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", sprite);

		dataContainer.getUserVariable(sprite, PROJECT_USER_VARIABLE).setValue("4");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", sprite);

		dataContainer.getUserVariable(sprite, PROJECT_USER_VARIABLE).setValue(1d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, 1.0, sprite);

		dataContainer.getUserVariable(sprite, PROJECT_USER_VARIABLE).setValue(0d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", sprite);

		dataContainer.getUserVariable(sprite, PROJECT_USER_VARIABLE).setValue(4d);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE, InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, "", sprite);
	}

	@Test
	public void testFunctionLength() {
		List<Object> userListValuesStrings = new ArrayList<>();
		userListValuesStrings.add("Hello");
		userListValuesStrings.add("my");
		userListValuesStrings.add("worlds");

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME)));

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH,
				InternTokenType.USER_LIST, PROJECT_USER_LIST_NAME, (double) 0, sprite);

		dataContainer.getUserList(sprite, PROJECT_USER_LIST_NAME).setList(userListValuesStrings);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, (double) 15, sprite);

		ArrayList<Object> userList = new ArrayList<>();
		userList.add("0");
		dataContainer.getUserList(sprite, PROJECT_USER_LIST_NAME).setList(userList);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, (double) 1, sprite);

		userList.clear();
		userList.add("0.0");
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, (double) 3, sprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, "0",
				(double) 1, sprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, "0.0",
				(double) 3, sprite);
	}

	@Test
	public void testFunctionNumberOfItems() {
		List<Object> userListValuesStrings = new ArrayList<>();
		userListValuesStrings.add("Hello");
		userListValuesStrings.add("my");
		userListValuesStrings.add("worlds");

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME)));

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.NUMBER_OF_ITEMS, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, (double) 0, sprite);

		dataContainer.getUserList(sprite, PROJECT_USER_LIST_NAME).setList(userListValuesStrings);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, (double) 15, sprite);
	}

	@Test
	public void testFunctionContains() {
		List<Object> userListValuesMultipleNumbers = new ArrayList<>();
		userListValuesMultipleNumbers.add(1.0);
		userListValuesMultipleNumbers.add(2.0);
		userListValuesMultipleNumbers.add(3.0);

		assertTrue(dataContainer.addUserList(new UserList(PROJECT_USER_LIST_NAME, userListValuesMultipleNumbers)));
		assertTrue(dataContainer.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE, 1.0)));

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, InternTokenType.NUMBER, "1", 1d, sprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, InternTokenType.STRING, "1", 1d, sprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, InternTokenType.STRING, "1.00", 1d, sprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, InternTokenType.NUMBER, "0", 0d, sprite);

		List<Object> userListValuesStringsAndNumbers = new ArrayList<>();
		userListValuesStringsAndNumbers.add("Hello");
		userListValuesStringsAndNumbers.add(42.0);
		userListValuesStringsAndNumbers.add("WORLDS");

		dataContainer.getUserList(sprite, PROJECT_USER_LIST_NAME).setList(userListValuesStringsAndNumbers);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, InternTokenType.STRING, "Hello", 1d, sprite);

		dataContainer.getUserVariable(sprite, PROJECT_USER_VARIABLE).setValue("Hello");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST,
				PROJECT_USER_LIST_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE, 1d, sprite);
	}

	private Object interpretUserList(String userListName) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.USER_LIST, userListName));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		Formula userVariableFormula = new Formula(parseTree);

		return userVariableFormula.interpretObject(sprite);
	}
}
