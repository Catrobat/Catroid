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

package org.catrobat.catroid.test.content;

import org.catrobat.catroid.content.ListWithoutDuplicates;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ListWithoutDuplicatesRenameTest<T> {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"UserVariable", UserVariable.class,
						new UserVariable[] {new UserVariable("x"), new UserVariable("y"), new UserVariable("z")}},
				{"UserList", UserList.class,
						new UserList[] {new UserList("list1"), new UserList("list2"), new UserList("list3")}},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<T> clazz;

	@Parameterized.Parameter(2)
	public UserData[] initialValues;

	private ListWithoutDuplicates<UserData> testListWithoutDuplicates;

	@Before
	public void setUp() {
		testListWithoutDuplicates = new ListWithoutDuplicates<UserData>();
		testListWithoutDuplicates.addAll(Arrays.asList(initialValues));
	}

	@Test
	public void renameAndDeleteObjectOfList() {
		UserData element = testListWithoutDuplicates.get(0);
		String changedName = element.getName() + "_renamed";
		element.setName(changedName);
		testListWithoutDuplicates.set(0, element);

		assertTrue(testListWithoutDuplicates.remove(element));
	}
}
