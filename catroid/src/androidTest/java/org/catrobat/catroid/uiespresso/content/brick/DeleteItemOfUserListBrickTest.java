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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.annotations.FlakyTest;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfSpinnerOnBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.createNewVariableOnSpinner;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.createNewVariableOnSpinnerInitial;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterValueInFormulaTextFieldOnBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.onScriptList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeleteItemOfUserListBrickTest {
	private static int brickPosition;
	private DeleteItemOfUserListBrick deleteItemOfUserListBrick;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		deleteItemOfUserListBrick = new DeleteItemOfUserListBrick();

		BrickTestUtils.createProjectAndGetStartScript("deleteItemOfUserListBrickTest1")
				.addBrick(deleteItemOfUserListBrick);
		brickPosition = 1;
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	@FlakyTest
	public void testDeleteItemOfUserListBrickBasicLayout() {
		int newPosition = 3;
		String userListName = "test1";

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_delete_item_from_userlist);
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.delete_item_of_userlist_spinner, brickPosition,
				R.string.brick_variable_spinner_create_new_variable);

		enterValueInFormulaTextFieldOnBrickAtPosition(newPosition, R.id.brick_delete_item_of_userlist_edit_text,
				brickPosition);
		createNewVariableOnSpinnerInitial(R.id.delete_item_of_userlist_spinner, brickPosition, userListName);
	}

	@Test
	@FlakyTest
	public void testDeleteItemOfUserListBrickMultipleLists() {
		String firstUserListName = "test1";
		String secondUserListName = "test2";
		UserList userList;

		createNewVariableOnSpinnerInitial(R.id.delete_item_of_userlist_spinner, brickPosition, firstUserListName);
		userList = deleteItemOfUserListBrick.getUserList();
		assertNotNull("UserList is null", userList);
		assertTrue("UserList Name not as expected", userList.getName().equals(firstUserListName));

		createNewVariableOnSpinner(R.id.delete_item_of_userlist_spinner, brickPosition, secondUserListName);
		userList = deleteItemOfUserListBrick.getUserList();
		assertNotNull("UserList is null", userList);
		// todo: CAT-2359 to fix this
		assertTrue("UserList Name not as expected", userList.getName().equals(secondUserListName));

		onScriptList().atPosition(brickPosition).onChildView(withId(R.id.brick_delete_item_of_userlist_edit_text))
				.perform(click());

		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(withText(secondUserListName))
				.perform(longClick());
		onView(withText(R.string.delete))
				.perform(click());
		pressBack();
		onView(withText(secondUserListName))
				.check(doesNotExist());

		userList = deleteItemOfUserListBrick.getUserList();
		assertNotNull("UserList is null", userList);
		assertTrue("UserList Name not as expected", userList.getName().equals(firstUserListName));
	}

	@Test
	public void testCreateUserListInFormulaEditor() {
		String userListName = "test1";
		onScriptList().atPosition(brickPosition).onChildView(withId(R.id.brick_delete_item_of_userlist_edit_text))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		BrickTestUtils.createUserListFromDataFragment(userListName, true);
	}
}
