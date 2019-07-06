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
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.merge.MergeTask;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnit4.class)
public class MergeTaskTest {
	private Project firstProject;
	private Project secondProject;
	private MergeTask mergeTask;

	@Before
	public void setUp() {
		Context mockContext = MockUtil.mockContextForProject();
		firstProject = new Project(mockContext, "testProjectOne");
		secondProject = new Project(mockContext, "testProjectTwo");

		firstProject.addUserList(new UserList("userListOne"));
		firstProject.addUserVariable(new UserVariable("userVariableOne"));
		firstProject.addScene(new Scene("sceneOne", firstProject));

		secondProject.addUserList(new UserList("userListTwo"));
		secondProject.addUserVariable(new UserVariable("userVariableTwo"));
		secondProject.addScene(new Scene("sceneTwo", secondProject));

		mergeTask = new MergeTask(firstProject, secondProject, mockContext, false);
	}

	@After
	public void tearDown() throws IOException {

	}

	@Test
	public void mergeTaskNoConflictTest() {
		assertTrue(mergeTask.performMerge("mergedProject"));
	}

	@Test
	public void mergeTaskWithSceneConflictTest() {
		firstProject.addScene(new Scene("sceneTwo", secondProject));
		assertFalse(mergeTask.performMerge("mergedProject"));
	}

	@Test
	public void mergeTaskWithUserListConflictTest() {
		firstProject.addUserList(new UserList("userListTwo"));
		assertFalse(mergeTask.performMerge("mergedProject"));
	}

	@Test
	public void mergeTaskWithUserVariableConflictTest() {
		firstProject.addUserVariable(new UserVariable("userVariableOne"));
		assertFalse(mergeTask.performMerge("mergedProject"));
	}
}
