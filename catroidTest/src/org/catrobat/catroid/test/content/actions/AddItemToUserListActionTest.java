/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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

public class AddItemToUserListActionTest extends AndroidTestCase {

	private static final String TEST_USERLIST_NAME = "testUserList";
	private static final double DOUBLE_VALUE_ITEM_TO_ADD = 3.0;
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<Object>();
	static {
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
	}
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
		super.setUp();
	}

	public void testAddNumericalValueToUserList() {
		ExtendedActions.addItemToUserList(testSprite, new Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList).act(1f);
		Object lastItemOfUserList = userList.getList().get(userList.getList().size() - 1);

		assertEquals("UserList size not changed!", 4, userList.getList().size());
		assertEquals("UserList not changed!", "3.0", lastItemOfUserList);
	}

	public void testAddItemWithInvalidUserList() {
		ExtendedActions.addItemToUserList(testSprite, new Formula(DOUBLE_VALUE_ITEM_TO_ADD), null).act(1f);
		assertEquals("UserList changed, but should not!", 2, userList.getList().size());
	}

	public void testAddNullFormula() {
		ExtendedActions.addItemToUserList(testSprite, null, userList).act(1f);
		Object lastItemOfUserList = userList.getList().get(userList.getList().size() - 1);
		assertEquals("UserList not changed!", 0d, lastItemOfUserList);
	}

	public void testNotANumberFormula() {
		ExtendedActions.addItemToUserList(testSprite, new Formula(Double.NaN), userList).act(1f);
		Object lastItemOfUserList = userList.getList().get(userList.getList().size() - 1);
		assertEquals("String UserVariable not changed!", String.valueOf(Double.NaN), lastItemOfUserList);
	}
}
