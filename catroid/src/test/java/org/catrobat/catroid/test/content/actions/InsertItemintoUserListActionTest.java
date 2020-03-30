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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class InsertItemintoUserListActionTest {

	private static final String TEST_USERLIST_NAME = "testUserList";
	private static final double DOUBLE_VALUE_ITEM_TO_ADD = 3.0;
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<>();

	private Sprite testSprite;
	private Project project;
	private UserList userList;

	private ActionFactory actionFactory;

	@Before
	public void setUp() throws Exception {
		actionFactory = new ActionFactory();
		testSprite = new SingleSprite("testSingleSprite");
		project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);

		INITIALIZED_LIST_VALUES.clear();
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
		userList = new UserList(TEST_USERLIST_NAME, INITIALIZED_LIST_VALUES);
		project.addUserList(userList);
	}

	@Test
	public void testInsertNumericalValueToUserList() {
		actionFactory.createInsertItemIntoUserListAction(testSprite, new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList).act(1f);
		Object firstItemOfUserList = userList.getValue().get(0);

		assertEquals(3, userList.getValue().size());
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_ADD), firstItemOfUserList);
	}

	@Test
	public void testInsertNumericalValueToUserListAtLastPosition() {
		actionFactory.createInsertItemIntoUserListAction(testSprite, new Formula(3), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList).act(1f);
		Object lastItemOfUserList = userList.getValue().get(userList.getValue().size() - 1);

		assertEquals(3, userList.getValue().size());
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_ADD), lastItemOfUserList);
	}

	@Test
	public void testInsertItemWithInvalidUserList() {
		actionFactory.createInsertItemIntoUserListAction(testSprite, new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), null).act(1f);
		assertEquals(2, userList.getValue().size());
	}

	@Test
	public void testInsertNullFormula() {
		actionFactory.createInsertItemIntoUserListAction(testSprite, new Formula(1), null, userList).act(1f);
		Object firstItemOfUserList = userList.getValue().get(0);

		assertEquals(3, userList.getValue().size());
		assertEquals(0d, firstItemOfUserList);
	}

	@Test
	public void testNotANumberFormula() {
		actionFactory.createInsertItemIntoUserListAction(testSprite, new Formula(1), new Formula(Double.NaN), userList).act(1f);
		Object firstItemOfUserList = userList.getValue().get(0);
		assertEquals(String.valueOf(Double.NaN), firstItemOfUserList);
	}
}
