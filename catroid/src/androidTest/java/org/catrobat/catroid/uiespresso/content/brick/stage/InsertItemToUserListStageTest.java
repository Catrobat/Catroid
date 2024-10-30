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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

@RunWith(AndroidJUnit4.class)
public class InsertItemToUserListStageTest {
	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);
	private Float valueToInsert;
	private UserList userList;

	@Before
	public void setUp() throws Exception {
		valueToInsert = 99.99f;
		userList = new UserList("LIST");
		Integer indexToInsert = 1;
		UiTestUtils.createProjectAndGetStartScript("addItemToUserListStageTest")
				.addBrick(new InsertItemIntoUserListBrick(new Formula(valueToInsert), new Formula(indexToInsert), userList));
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testUserListHasItems() {
		onView(isRoot())
				.perform(CustomActions.wait(500));

		float value = Float.valueOf(userList.getValue().get(0).toString());

		assertEquals(valueToInsert, value, Float.MIN_VALUE);
		assertEquals(1, userList.getValue().size());
	}
}
