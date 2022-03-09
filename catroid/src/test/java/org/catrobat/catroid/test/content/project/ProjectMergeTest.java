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
package org.catrobat.catroid.test.content.project;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(JUnit4.class)
public class ProjectMergeTest {
	Project project1;
	Project project2;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() {
		Context mockContext = MockUtil.mockContextForProject(dependencyModules);
		project1 = new Project(mockContext, "testProject");
		project2 = new Project(mockContext, "testProject2");

		List<Object> initializedList1 = new ArrayList<Object>();
		List<Object> initializedList2 = new ArrayList<Object>();

		initializedList1.clear();
		initializedList1.add(1.0);
		initializedList1.add(2.0);

		initializedList2.clear();
		initializedList2.add(1.0);
		initializedList2.add(2.0);

		UserList userList1 = new UserList("TestUserList", initializedList1);
		project1.addUserList(userList1);

		UserList userList2 = new UserList("TestUserList", initializedList2);
		project2.addUserList(userList2);

		project1.addUserVariable(new UserVariable("testVariable", 1));
		project2.addUserVariable(new UserVariable("testVariable", 1));
	}

	@After
	public void tearDown() {
		project1.removeUserList("TestUserList");
		project1.removeUserVariable("TestVariable");
		project2.removeUserList("TestUserList");
		project2.removeUserVariable("TestVariable");
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testProjectNoGlobalVariableConflicts() {
		List<UserVariable> globalConflicts = projectManager.getValue().getGlobalVariableConflicts(project1,
				project2);
		assertEquals(globalConflicts.size(), 0);
	}

	@Test
	public void testProjectGlobalVariableConflict() {
		project2.getUserVariable("testVariable").setValue(2);
		List<UserVariable> globalConflicts = projectManager.getValue().getGlobalVariableConflicts(project1,
				project2);
		assertEquals(globalConflicts.size(), 1);

		assertEquals(globalConflicts.get(0), project1.getUserVariable("testVariable"));
	}

	@Test
	public void testProjectNoGlobalUserListConflicts() {
		List<UserList> globalConflicts = projectManager.getValue().getGlobalListConflicts(project1,
				project2);
		assertEquals(globalConflicts.size(), 0);
	}

	@Test
	public void testProjectGlobalUserListConflicts() {
		project2.getUserList("TestUserList").addListItem(1.0);
		List<UserList> globalConflicts = projectManager.getValue().getGlobalListConflicts(project1,
				project2);
		assertEquals(globalConflicts.size(), 1);
	}
}
