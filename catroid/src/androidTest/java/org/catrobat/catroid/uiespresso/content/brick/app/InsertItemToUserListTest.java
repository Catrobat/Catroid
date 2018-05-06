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
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class InsertItemToUserListTest {

	private int brickPosition;
	private double elementValue;
	private int elementIndex;
	private String newList;
	private String oldList;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		elementValue = 42.5;
		elementIndex = 0;
		newList = "newList";
		oldList = "oldList";

		BrickTestUtils.createProjectAndGetStartScript("addItemToUserListTest")
				.addBrick(new InsertItemIntoUserListBrick(new Formula(elementValue), new Formula(elementIndex),
						new UserList(newList)));
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testInsertItemIntoUserListBrick() {
		onBrickAtPosition(0)
				.checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition)
				.checkShowsText(R.string.brick_insert_item_into_userlist_at_position);
		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_insert_item_into_userlist_value_edit_text)
				.checkShowsNumber(elementValue);
		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_insert_item_into_userlist_at_index_edit_text)
				.checkShowsNumber(elementIndex);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCreateUserListInSpinner() {
		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.performNewVariableInitial(newList)
				.checkShowsText(newList);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testSelectUserListInSpinner() {
		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.performNewVariableInitial(oldList);
		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.performNewVariable(newList);

		onBrickAtPosition(brickPosition).onVariableSpinner(R.id.insert_item_into_userlist_spinner)
				.performSelectList(oldList);

		onBrickAtPosition(brickPosition)
				.checkShowsText(oldList);
	}
}
