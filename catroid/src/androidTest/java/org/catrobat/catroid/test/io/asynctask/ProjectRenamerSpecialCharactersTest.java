/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.test.io.asynctask;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import androidx.test.core.app.ApplicationProvider;

import static org.catrobat.catroid.io.asynctask.ProjectLoaderKt.loadProject;
import static org.catrobat.catroid.io.asynctask.ProjectRenamerKt.renameProject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ProjectRenamerSpecialCharactersTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"smallerThanTest", "test<Project", "test%3CProject"},
				{"greaterThanTest", "test>Project", "test%3EProject"},
				{"percentTest", "test%Project", "test%25Project"},
				{"slashTest", "test/Project", "test%2FProject"},
				{"slashTest", "test/Project", "test%2FProject"},
				{"quoteTest", "test\"Project", "test%22Project"},
				{"colonTest", "test:Project", "test%3AProject"},
				{"questionmarkTest", "test?Project", "test%3FProject"},
				{"backslashTest", "test\\Project", "test%5CProject"},
				{"pipeTest", "test|Project", "test%7CProject"},
				{"asteriskTest", "test*Project", "test%2AProject"},
				{"dotTest", "test.Project", "test.Project"},
				{"dotOnlyTest", ".", "%2E"}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public String specialCharacterProjectName;

	@Parameterized.Parameter(2)
	public String specialCharacterEncodedProjectName;

	private final String projectNameWithoutSpecialCharacter = "projectName";

	@Before
	public void setUp() throws IOException {
		TestUtils.deleteProjects(specialCharacterEncodedProjectName, projectNameWithoutSpecialCharacter);
	}

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(specialCharacterEncodedProjectName, projectNameWithoutSpecialCharacter);
	}

	@Test
	public void testRenameFromSpecialCharacter() throws IOException {
		Project project = DefaultProjectHandler.createAndSaveDefaultProject(specialCharacterProjectName,
				ApplicationProvider.getApplicationContext(), false);

		File renamedDirectory = renameProject(project.getDirectory(), projectNameWithoutSpecialCharacter);
		assertNotNull(renamedDirectory);

		assertEquals(projectNameWithoutSpecialCharacter, renamedDirectory.getName());

		assertTrue(loadProject(renamedDirectory, ApplicationProvider.getApplicationContext()));

		project = ProjectManager.getInstance().getCurrentProject();
		assertEquals(projectNameWithoutSpecialCharacter, project.getName());
	}

	@Test
	public void testRenameToSpecialCharacter() throws IOException {
		Project project = DefaultProjectHandler.createAndSaveDefaultProject(projectNameWithoutSpecialCharacter,
				ApplicationProvider.getApplicationContext(), false);

		File renamedDirectory = renameProject(project.getDirectory(), specialCharacterProjectName);
		assertNotNull(renamedDirectory);

		File expectedDirectory = new File(project.getDirectory().getParent(), specialCharacterEncodedProjectName);

		assertEquals(expectedDirectory, renamedDirectory);

		assertTrue(loadProject(renamedDirectory, ApplicationProvider.getApplicationContext()));

		project = ProjectManager.getInstance().getCurrentProject();
		assertEquals(specialCharacterProjectName, project.getName());
	}
}
