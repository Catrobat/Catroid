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
package org.catrobat.catroid.test.merge;

import android.content.Context;

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.merge.ConflictHelper;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ConflictHelperTest {
	private Project firstProject;
	private Project secondProject;
	private ConflictHelper conflictHelper;

	@Before
	public void setUp() {
		conflictHelper = new ConflictHelper();
		Context mockContext = MockUtil.mockContextForProject();
		firstProject = new Project(mockContext, "testProject");
		secondProject = new Project(mockContext, "testProject2");
		firstProject.addUserList(new UserList("TestUserList1", Arrays.asList(1.0, 2.0)));
		secondProject.addUserList(new UserList("TestUserList2", Arrays.asList(1.0, 2.0)));
		firstProject.addUserVariable(new UserVariable("testVariable1", 1));
		secondProject.addUserVariable(new UserVariable("testVariable2", 1));
	}

	@After
	public void tearDown() throws IOException {

	}

	@Test
	public void testProjectNoGlobalVariableConflicts() {
		assertFalse(conflictHelper.conflictExist(firstProject, secondProject));
	}

	@Test
	public void testProjectGlobalVariableConflict() {
		secondProject.addUserVariable(new UserVariable("testVariable1", 1));
		assertTrue(conflictHelper.conflictExist(firstProject, secondProject));
	}

	@Test
	public void testProjectNoGlobalUserListConflicts() {
		assertFalse(conflictHelper.conflictExist(firstProject, secondProject));
	}

	@Test
	public void testProjectGlobalUserListConflicts() {
		secondProject.addUserList(new UserList("TestUserList1", Arrays.asList(1.0, 2.0)));
		assertTrue(conflictHelper.conflictExist(firstProject, secondProject));
	}
}

