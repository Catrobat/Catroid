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

package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.content.actions.ClearUserListAction;
import org.catrobat.catroid.formulaeditor.UserList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ClearUserListActionTest {

	private static final String TEST_USER_LIST_NAME = "testUserList";
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<Object>();

	private ClearUserListAction action;
	private UserList userList;

	@Before
	public void setUp() throws Exception {
		userList = new UserList(TEST_USER_LIST_NAME, INITIALIZED_LIST_VALUES);
		action = new ClearUserListAction();

		INITIALIZED_LIST_VALUES.clear();
	}

	@Test
	public void testClearEmptyUserList() {
		action.setUserList(userList);
		action.act(1.0f);
		assertEquals(0, userList.getValue().size());
	}

	@Test
	public void testClearSingleEntryUserList() {
		INITIALIZED_LIST_VALUES.add(1.0);
		action.setUserList(userList);
		action.act(1.0f);
		assertEquals(0, userList.getValue().size());
	}

	@Test
	public void testClearMultipleEntryUserList() {
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
		action.setUserList(userList);
		action.act(1.0f);
		assertEquals(0, userList.getValue().size());
	}
}
