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
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.utils.FormulaTextFieldUtils.enterValueInFormulaTextFieldOnBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.SpinnerUtils.checkIfSpinnerOnBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.utils.SpinnerUtils.createNewVariableOnSpinnerInitial;
import static org.catrobat.catroid.uiespresso.content.brick.utils.SpinnerUtils.enterTextOnDialogue;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ReplaceItemInUserListTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("ReplaceItemInUserListBrick")
				.addBrick(new ReplaceItemInUserListBrick(1.0, 1));
		brickPosition = 1;
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testReplaceItemInUserListBrick() {
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_replace_item_in_userlist_replace_in_list);
		enterValueInFormulaTextFieldOnBrickAtPosition(2, R.id.brick_replace_item_in_userlist_at_index_edit_text,
				brickPosition);
		enterValueInFormulaTextFieldOnBrickAtPosition(2, R.id.brick_replace_item_in_userlist_value_edit_text,
				brickPosition);
	}

	@Test
	public void testCreateNewUserListAndDeletion() {
		String userListName = "testList1";
		String secondUserListName = "testList2";

		createNewVariableOnSpinnerInitial(R.id.replace_item_in_userlist_spinner, brickPosition, userListName);

		BrickTestUtils.onScriptList().atPosition(brickPosition)
				.onChildView(withId(R.id.replace_item_in_userlist_spinner))
				.perform(click());
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());
		enterTextOnDialogue(R.id.dialog_formula_editor_data_name_edit_text, secondUserListName);

		onView(withId(R.id.brick_replace_item_in_userlist_value_edit_text))
				.perform(click());
		onView(withText(R.string.formula_editor_data))
				.perform(click());
		onView(withText(secondUserListName))
				.perform(longClick());
		onView(withText(R.string.delete))
				.perform(click());

		pressBack();
		pressBack();

		BrickTestUtils.onScriptList().atPosition(brickPosition)
				.onChildView(withId(R.id.replace_item_in_userlist_spinner))
				.perform(click());

		onView(withText(secondUserListName))
				.check(doesNotExist());
		onView(withText(userListName))
				.check(matches(isDisplayed()));
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testCreateUserListInFormulaEditor() {
		String userListName = "testList1";

		onView(withId(R.id.brick_replace_item_in_userlist_value_edit_text))
				.perform(click());
		onView(withText(R.string.formula_editor_data))
				.perform(click());
		onView(withId(R.id.button_add))
				.perform(click());
		onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
				.perform(typeText(userListName));
		onView(withId(R.id.dialog_formula_editor_data_is_list_checkbox))
				.perform(click());
		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		pressBack();
		pressBack();

		BrickTestUtils.onScriptList().atPosition(brickPosition)
				.onChildView(withId(R.id.replace_item_in_userlist_spinner))
				.perform(click());
		onView(withText(userListName))
				.perform(click());
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.replace_item_in_userlist_spinner, brickPosition, userListName);
	}
}
