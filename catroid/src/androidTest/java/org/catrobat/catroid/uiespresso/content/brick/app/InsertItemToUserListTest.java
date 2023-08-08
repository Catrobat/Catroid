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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.formulaeditor.UserList;
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

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class InsertItemToUserListTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	private int brickPosition;
	private int valueToInsert;
	private int valueToInsert1;
	private Integer indexToInsert;
	private String userListName;

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		valueToInsert = 1992;
		valueToInsert1 = 2018;
		indexToInsert = 1;
		userListName = "NewList";
		UiTestUtils.createProjectAndGetStartScript("addItemToUserListTest")
				.addBrick(new InsertItemIntoUserListBrick(valueToInsert, indexToInsert));

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testInsertItemToUserListBrick() {
		onBrickAtPosition(0)
				.checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition)
				.checkShowsText(R.string.brick_insert_item_into_userlist_insert_into)
				.checkShowsText(R.string.brick_insert_item_into_userlist_into_list)
				.checkShowsText(R.string.brick_insert_item_into_userlist_at_position);

		onView(withId(R.id.brick_insert_item_into_userlist_value_edit_text))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();
		onDataList()
				.performAdd(userListName, FormulaEditorDataListWrapper.ItemType.LIST)
				.performClose();
		pressBack();

		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_insert_item_into_userlist_value_edit_text)
				.checkShowsNumber(valueToInsert);

		onBrickAtPosition(brickPosition)
				.onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.checkShowsText(userListName);
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_insert_item_into_userlist_at_index_edit_text)
				.checkShowsNumber(indexToInsert);

		//insert manually the values
		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_insert_item_into_userlist_value_edit_text)
				.performEnterNumber(valueToInsert1);
		onView(withId(R.id.brick_insert_item_into_userlist_at_index_edit_text))
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(indexToInsert + 1);

		pressBack();

		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_insert_item_into_userlist_value_edit_text)
				.checkShowsNumber(valueToInsert1);

		onBrickAtPosition(brickPosition)
				.onFormulaTextField(R.id.brick_insert_item_into_userlist_at_index_edit_text)
				.performEnterNumber(indexToInsert + 1);

		UserList userList = ProjectManager.getInstance().getCurrentProject().getUserList(userListName);

		assertEquals(0, userList.getValue().size());
	}
}
