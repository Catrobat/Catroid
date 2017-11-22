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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class InsertItemIntoUserListBrickTest {

	private int brickPosition;
	private int value;
	private int indexToInsert;
	private InsertItemIntoUserListBrick insertItemIntoUserListBrick;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		value = 10;
		indexToInsert = 1;
		insertItemIntoUserListBrick = new InsertItemIntoUserListBrick(value, indexToInsert);
		BrickTestUtils.createProjectAndGetStartScript("insertItemOfUserListBrickTest1")
				.addBrick(insertItemIntoUserListBrick);
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testInsertItemIntoUserListBrickBasicLayout() {
		int newPosition = 3;
		int newValue = 5;
		String userListName = "test1";

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_insert_item_into_userlist_at_position);

		onBrickAtPosition(brickPosition).onSpinner(R.id.insert_item_into_userlist_spinner)
				.checkShowsText(R.string.brick_variable_spinner_create_new_variable);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_insert_item_into_userlist_at_index_edit_text)
				.performEnterNumber(newPosition)
				.checkShowsNumber(newPosition);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_insert_item_into_userlist_value_edit_text)
				.performEnterNumber(newValue)
				.checkShowsNumber(newValue);

		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.performNewVariableInitial(userListName)
				.checkShowsText(userListName);
	}

	@Category({Cat.AppUi.class, Level.Detailed.class})
	@Test
	public void testInsertItemIntoUserListBrickMultipleLists() {
		String firstUserListName = "test1";
		String secondUserListName = "test2";
		UserList userList;

		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.performNewVariableInitial(firstUserListName);

		userList = insertItemIntoUserListBrick.getUserList();
		assertNotNull(userList);
		assertEquals(userList.getName(), firstUserListName);

		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.performNewVariable(secondUserListName)
				.checkShowsText(secondUserListName);

		userList = insertItemIntoUserListBrick.getUserList();
		assertNotNull(userList);
		assertEquals(userList.getName(), secondUserListName);

		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_insert_item_into_userlist_at_index_edit_text))
				.perform(click());

		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(allOf(withText(secondUserListName), isDisplayed()))
				.perform(longClick());
		onView(withText(R.string.delete))
				.perform(click());
		pressBack();

		onView(withText(secondUserListName))
				.check(doesNotExist());

		userList = insertItemIntoUserListBrick.getUserList();
		assertNotNull(userList);
		assertEquals(userList.getName(), firstUserListName);
	}

	@Category({Cat.AppUi.class, Level.Detailed.class})
	@Test
	public void testCreateUserListInFormulaEditor() {
		UserList userList;
		String userListName = "test1";
		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_insert_item_into_userlist_at_index_edit_text))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		BrickTestUtils.createUserListFromDataFragment(userListName, true);
		pressBack();
		pressBack();
		userList = insertItemIntoUserListBrick.getUserList();
		assertNotNull(userList);
		assertEquals(userList.getName(), userListName);
	}
}
