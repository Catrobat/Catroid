/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Category.FUNCTIONS;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class AddUserListToActiveFormulaUITest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] {
				{"MultipleSpriteAndProjectUserListsNumberOfItems",
						R.string.formula_editor_function_number_of_items,
						R.string.formula_editor_function_number_of_items_parameter,
						asList(new UserList("spriteList1"), new UserList("spriteList2")),
								asList(new UserList("projectList1"), new UserList("projectList2")),
						"spriteList2"},
				{"MultipleSpriteAndProjectUserListsItem",
						R.string.formula_editor_function_list_item,
						R.string.formula_editor_function_list_item_parameter,
						asList(new UserList("spriteList1"), new UserList("spriteList2")),
						asList(new UserList("projectList1"), new UserList("projectList2")),
						"spriteList2"},
				{"MultipleSpriteAndProjectUserListsContains",
						R.string.formula_editor_function_contains,
						R.string.formula_editor_function_contains_parameter,
						asList(new UserList("spriteList1"), new UserList("spriteList2")),
						asList(new UserList("projectList1"), new UserList("projectList2")),
						"spriteList2"},
				{"MultipleSpriteAndProjectUserListsIndexOfItem",
						R.string.formula_editor_function_index_of_item,
						R.string.formula_editor_function_index_of_item_parameter,
						asList(new UserList("spriteList1"), new UserList("spriteList2")),
						asList(new UserList("projectList1"), new UserList("projectList2")),
						"spriteList2"},
				{"NoSpriteUserLists",
						R.string.formula_editor_function_number_of_items,
						R.string.formula_editor_function_number_of_items_parameter,
						new ArrayList<UserList>(),
								asList(new UserList("projectList1"),
										new UserList("projectList2")),
						"projectList2"},
				{"NoProjectUserLists",
						R.string.formula_editor_function_number_of_items,
						R.string.formula_editor_function_number_of_items_parameter,
						asList(new UserList("spriteList1"), new UserList("spriteList2")),
						new ArrayList<UserList>(),
						"spriteList2"},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public int formulaName;

	@Parameterized.Parameter(2)
	public int formulaParameters;

	@Parameterized.Parameter(3)
	public List<UserList> spriteUserList;

	@Parameterized.Parameter(4)
	public List<UserList> projectUserList;

	@Parameterized.Parameter(5)
	public String expectedOutput;

	@Before
	public void setUp() throws Exception {
		Project project = UiTestUtils.createDefaultTestProject("AddUserListToActiveFormulaUITest");
		Script script = UiTestUtils.getDefaultTestScript(project);
		Sprite sprite = UiTestUtils.getDefaultTestSprite(project);
		projectUserList.forEach(project::addUserList);
		spriteUserList.forEach(sprite::addUserList);
		script.addBrick(new SetXBrick());
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(AddUserListToActiveFormulaUITest.class.getSimpleName());
	}

	@Test
	public void insertLastUserListToActiveFormula() {
		onBrickAtPosition(1).performEditFormula();
		String listFunction =
				getResourcesString(formulaName) + getResourcesString(formulaParameters);
		onFormulaEditor().performOpenCategory(FUNCTIONS).performSelect(listFunction);
		onFormulaEditor().checkShows(generateExpectedOutput(expectedOutput));
	}

	private String generateExpectedOutput(String expectedUserListName) {
		if (formulaName == R.string.formula_editor_function_number_of_items) {
			return getResourcesString(formulaName) + "( *" + expectedUserListName + "* ) ";
		} else if (formulaName == R.string.formula_editor_function_list_item || formulaName == R.string.formula_editor_function_index_of_item) {
			return getResourcesString(formulaName) + "( 1 , *" + expectedUserListName + "* ) ";
		} else {
			return getResourcesString(formulaName) + "( *" + expectedUserListName + "* , 1 ) ";
		}
	}
}
