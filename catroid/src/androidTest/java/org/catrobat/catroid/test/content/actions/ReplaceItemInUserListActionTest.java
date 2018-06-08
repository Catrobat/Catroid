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
package org.catrobat.catroid.test.content.actions;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ReplaceItemInUserListActionTest {

	private static final String TEST_USERLIST_NAME = "testUserList";
	private static final double DOUBLE_VALUE_ITEM_TO_REPLACE_WITH = 4.0;
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<Object>();

	private Sprite testSprite;
	private Project project;
	private UserList userList;

	private ActionFactory actionFactory;

	@Before
	public void setUp() throws Exception {
		actionFactory = new ActionFactory();
		testSprite = new SingleSprite("testSprite");
		project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer().addProjectUserList(TEST_USERLIST_NAME);
		userList = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.getUserList(null, TEST_USERLIST_NAME);
		userList.setList(INITIALIZED_LIST_VALUES);
		INITIALIZED_LIST_VALUES.clear();
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
		INITIALIZED_LIST_VALUES.add(3.0);
	}

	@Test
	public void testReplaceNumericalValueInUserList() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);
		Object firstItemOfUserList = userList.getList().get(0);

		assertEquals(3, userList.getList().size());
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), firstItemOfUserList);
	}

	@Test
	public void testReplaceNumericalValueInUserListAtLastPosition() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new Formula(3), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);
		Object lastItemOfUserList = userList.getList().get(userList.getList().size() - 1);

		assertEquals(3, userList.getList().size());
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), lastItemOfUserList);
	}

	@Test
	public void testReplaceNumericalValueInUserListOutOfUserListBounds() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new Formula(4), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);

		assertEquals(3, userList.getList().size());
		assertEquals(1d, userList.getList().get(0));
		assertEquals(2d, userList.getList().get(1));
		assertEquals(3d, userList.getList().get(2));
	}

	@Test
	public void testReplaceItemWithInvalidUserList() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), null).act(1f);
		assertEquals(3, userList.getList().size());
	}

	@Test
	public void testReplaceNullFormula() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new Formula(1), null, userList).act(1f);
		Object firstItemOfUserList = userList.getList().get(0);

		assertEquals(3, userList.getList().size());
		assertEquals(0d, firstItemOfUserList);
	}

	@Test
	public void testNotANumberFormula() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new Formula(1), new Formula(Double.NaN), userList).act(1f);
		Object firstItemOfUserList = userList.getList().get(0);
		assertEquals(String.valueOf(Double.NaN), firstItemOfUserList);
	}
}
