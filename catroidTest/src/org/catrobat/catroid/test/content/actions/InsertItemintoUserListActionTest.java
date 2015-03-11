/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2014 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;

import java.util.ArrayList;
import java.util.List;

public class InsertItemintoUserListActionTest extends AndroidTestCase {

	private static final String TEST_USERLIST_NAME = "testUserList";
	private static final double DOUBLE_VALUE_ITEM_TO_ADD = 3.0;
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<Object>();

	private Sprite testSprite;
	private Project project;
	private UserList userList;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		project = new Project(null, "testProject");
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().getCurrentProject().getDataContainer().addProjectUserList(TEST_USERLIST_NAME);
		userList = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.getUserList(TEST_USERLIST_NAME, null);
		userList.setList(INITIALIZED_LIST_VALUES);
		INITIALIZED_LIST_VALUES.clear();
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
		super.setUp();
	}

	public void testInsertNumericalValueToUserList() {
		ExtendedActions.insertItemIntoUserList(testSprite, new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList).act(1f);
		Object firstItemOfUserList = userList.getList().get(0);

		assertEquals("UserList size not changed!", 3, userList.getList().size());
		assertEquals("UserList not changed!", DOUBLE_VALUE_ITEM_TO_ADD, firstItemOfUserList);
	}

	public void testInsertNumericalValueToUserListAtLastPosition() {
		ExtendedActions.insertItemIntoUserList(testSprite, new Formula(3), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList).act(1f);
		Object lastItemOfUserList = userList.getList().get(userList.getList().size() - 1);

		assertEquals("UserList size not changed!", 3, userList.getList().size());
		assertEquals("UserList not changed!", DOUBLE_VALUE_ITEM_TO_ADD, lastItemOfUserList);
	}

	public void testInsertItemWithInvalidUserList() {
		ExtendedActions.insertItemIntoUserList(testSprite, new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), null).act(1f);
		assertEquals("UserList changed, but should not!", 2, userList.getList().size());
	}

	public void testInsertNullFormula() {
		ExtendedActions.insertItemIntoUserList(testSprite, new Formula(1), null, userList).act(1f);
		Object firstItemOfUserList = userList.getList().get(0);

		assertEquals("UserList size not changed!", 3, userList.getList().size());
		assertEquals("UserList not changed!", 0d, firstItemOfUserList);
	}

	public void testNotANumberFormula() {
		ExtendedActions.insertItemIntoUserList(testSprite, new Formula(1), new Formula(Double.NaN), userList).act(1f);
		Object firstItemOfUserList = userList.getList().get(0);
		assertEquals("UserList not changed!", Double.NaN, firstItemOfUserList);
	}
}