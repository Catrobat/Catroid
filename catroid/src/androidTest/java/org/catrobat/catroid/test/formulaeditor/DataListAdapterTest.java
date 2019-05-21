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

package org.catrobat.catroid.test.formulaeditor;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.content.ListWithoutDuplicates;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.recyclerview.adapter.DataListAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DataListAdapterTest {
	private String userVarNameX = "variable_x";
	private String userVarNameY = "variable_y";
	private String userListNameX = "list_x";
	private String userListNameY = "list_y";
	private String renameSuffix = "_renamed";

	private UserVariable userVariableX;
	private UserVariable userVariableY;
	private UserList userListX;
	private UserList userListY;

	private DataListAdapter dataListAdapter;
	private List<UserVariable> userVariables;
	private List<UserList> userLists;

	@Before
	public void setUp() {
		userVariableX = new UserVariable(userVarNameX);
		userVariableY = new UserVariable(userVarNameY);
		userListX = new UserList(userListNameX);
		userListY = new UserList(userListNameY);

		userVariables = new ListWithoutDuplicates<>();
		userVariables.addAll(Arrays.asList(userVariableX, userVariableY));

		userLists = new ListWithoutDuplicates<>();
		userLists.addAll(Arrays.asList(userListX, userListY));
	}

	@Test
	public void testRenameAndDeleteLocalItemViaAdapter() {
		dataListAdapter = new DataListAdapter(new ArrayList<>(),
				userVariables,
				new ArrayList<>(),
				new ArrayList<>());

		UserData localItem = userVariableX;
		dataListAdapter.setItemName(localItem, userVarNameX + renameSuffix);
		assertTrue(userVariables.contains(new UserVariable(userVarNameX + renameSuffix)));
		assertFalse(userVariables.contains(new UserVariable(userVarNameX)));

		dataListAdapter.remove(localItem);
		assertFalse(userVariables.contains(localItem));
	}

	@Test
	public void testRenameAndDeleteGlobalItemViaAdapter() {
		dataListAdapter = new DataListAdapter(new ArrayList<>(),
				new ArrayList<>(),
				userLists,
				new ArrayList<>());

		UserData globalItem = userListX;
		dataListAdapter.setItemName(globalItem, userListNameX + renameSuffix);
		assertTrue(userLists.contains(new UserList(userListNameX + renameSuffix)));
		assertFalse(userLists.contains(new UserList(userListNameX)));
		
		dataListAdapter.remove(globalItem);
		assertFalse(userLists.contains(globalItem));
	}

	@Test
	public void testRenameToExistingAndDeleteLocalItemViaAdapter() {
		dataListAdapter = new DataListAdapter(new ArrayList<>(),
				userVariables,
				new ArrayList<>(),
				new ArrayList<>());

		UserData localItem = userVariableX;
		dataListAdapter.setItemName(localItem, userVarNameY);
		assertTrue(userVariables.contains(new UserVariable(userVarNameX)));
		assertTrue(userVariables.contains(new UserVariable(userVarNameY)));
		assertEquals(2, userVariables.size());

		dataListAdapter.remove(localItem);
		assertFalse(userVariables.contains(localItem));
	}

	@Test
	public void testRenameToExistingAndDeleteGlobalItemViaAdapter() {
		dataListAdapter = new DataListAdapter(new ArrayList<>(),
				new ArrayList<>(),
				userLists,
				new ArrayList<>());

		UserData globalItem = userListX;
		dataListAdapter.setItemName(globalItem, userListNameY);
		assertTrue(userLists.contains(new UserList(userListNameX)));
		assertTrue(userLists.contains(new UserList(userListNameY)));
		assertEquals(2, userLists.size());

		dataListAdapter.remove(globalItem);
		assertFalse(userLists.contains(globalItem));
	}
}
