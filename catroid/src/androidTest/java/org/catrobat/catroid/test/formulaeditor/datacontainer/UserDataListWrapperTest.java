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

package org.catrobat.catroid.test.formulaeditor.datacontainer;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.UserDataListWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserDataListWrapperTest {

	@Test
	public void testReferencesWithAdd() {

		String varName = "var0";

		UserVariable var = new UserVariable(varName);
		List<UserVariable> variables = new ArrayList<>();

		UserDataListWrapper<UserVariable> userDataList = new UserDataListWrapper<>(variables);
		userDataList.add(var);

		assertEquals(userDataList.contains(varName), variables.contains(var));
	}

	@Test
	public void testReferencesWithContains() {

		String varName = "var0";

		UserVariable var = new UserVariable(varName);
		List<UserVariable> variables = new ArrayList<>();
		variables.add(var);

		UserDataListWrapper<UserVariable> userDataList = new UserDataListWrapper<>(variables);

		assertEquals(userDataList.contains(varName), variables.contains(var));
	}

	@Test
	public void testReferencesWithGet() {

		String varName = "var0";

		UserVariable var = new UserVariable(varName);
		List<UserVariable> variables = new ArrayList<>();
		variables.add(var);

		UserDataListWrapper<UserVariable> userDataList = new UserDataListWrapper<>(variables);

		assertEquals(var, userDataList.get(varName));
	}

	@Test
	public void testReferencesWithRemove() {

		String varName = "var0";

		UserVariable var = new UserVariable(varName);
		List<UserVariable> variables = new ArrayList<>();
		variables.add(var);

		UserDataListWrapper<UserVariable> userDataList = new UserDataListWrapper<>(variables);
		userDataList.remove(varName);

		assertEquals(userDataList.size(), variables.size());
		assertTrue(variables.isEmpty());
	}
}
