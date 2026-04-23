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
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ReplaceItemInUserListTest {
	private int brickPosition;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		UiTestUtils.createProjectAndGetStartScript("ReplaceItemInUserListBrick")
				.addBrick(new ReplaceItemInUserListBrick(1.0, 1));
		brickPosition = 1;
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCreateNewUserListAndDeletion() {
		String userListName = "TestList1";
		String secondUserListName = "TestList2";

		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.replace_item_in_userlist_spinner)
				.performNewVariable(userListName);

		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.replace_item_in_userlist_spinner)
				.performNewVariable(secondUserListName);

		onView(withId(R.id.brick_replace_item_in_userlist_value_edit_text))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();
		onDataList().onListAtPosition(1)
				.performDelete();
		onDataList()
				.performClose();
		pressBack();

		onBrickAtPosition(brickPosition).onChildView(withId(R.id.replace_item_in_userlist_spinner))
				.perform(click());

		onView(withText(secondUserListName))
				.check(doesNotExist());
		onView(withText(userListName))
				.check(matches(isDisplayed()));
		onView(withText(R.string.new_option))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCreateUserListInFormulaEditor() {
		String userListName = "TestList1";

		onView(withId(R.id.brick_replace_item_in_userlist_value_edit_text))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();
		onDataList()
				.performAdd(userListName, FormulaEditorDataListWrapper.ItemType.LIST)
				.performClose();
		pressBack();

		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.replace_item_in_userlist_spinner)
				.perform(click());
		onView(withText(userListName))
				.check(matches(isDisplayed()));
	}
}
