/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.test;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.UtilZip;

import java.io.File;

public class ProjectManagerTest extends InstrumentationTestCase {

	private static final String OLD_PROJECT = "OLD_PROJECT";
	private static final String NEW_PROJECT = "NEW_PROJECT";
	private static final String DOES_NOT_EXIST = "DOES_NOT_EXIST";

	private static final float CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED = 0.0f;
	private static final String ZIP_FILENAME_WRONG_NESTING_BRICKS = "CoinCatcher2.catrobat";
	private static final String PROJECT_NAME_NESTING_BRICKS = "Coin Catcher 2";

	private ProjectManager projectManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UtilUi.updateScreenWidthAndHeight(getInstrumentation().getTargetContext());
		projectManager = ProjectManager.getInstance();
		Reflection.setPrivateField(ProjectManager.class, ProjectManager.getInstance(), "asynchronousTask", false);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		projectManager.setProject(null);
		TestUtils.deleteTestProjects(OLD_PROJECT, NEW_PROJECT);
		TestUtils.removeFromPreferences(getInstrumentation().getTargetContext(), Constants.PREF_PROJECTNAME_KEY);
	}

	public void testShouldReturnFalseIfCatrobatLanguageVersionNotSupported() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED);

		try {
			projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getInstrumentation().getTargetContext());
			fail("Project shouldn't be compatible");
		} catch (CompatibilityProjectException expected) {
		} catch (ProjectException projectException) {
			fail("Failed to identify incompatible project");
		}

		TestUtils.deleteTestProjects();

		TestUtils
				.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);

		try {
			projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getInstrumentation().getTargetContext());
			assertTrue("Load project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Error loading project");
		}
	}

	public void testShouldKeepExistingProjectIfCannotLoadNewProject() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(
				Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, OLD_PROJECT);

		try {
			projectManager.loadProject(OLD_PROJECT, getInstrumentation().getTargetContext());
			assertTrue("Load old project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Could not load project.");
		}

		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED);

		try {
			projectManager.loadProject(NEW_PROJECT, getInstrumentation().getTargetContext());
			fail("Load project didn't failed to load project");
		} catch (ProjectException expected) {
		}

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't keep old project.", currentProject);
		assertEquals("Didn't keep old project.", OLD_PROJECT, currentProject.getName());

		TestUtils.deleteTestProjects(OLD_PROJECT, NEW_PROJECT);
	}

	public void testShouldLoadAnotherProjectIfCannotLoadSpecificProject() throws Exception {
		assertNull("Current project not null.", projectManager.getCurrentProject());

		try {
			projectManager.loadProject(DOES_NOT_EXIST, getInstrumentation().getTargetContext());
			fail("Load project didn't failed to load project");
		} catch (ProjectException expected) {
		}

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Could not load any project.", currentProject);
	}

	public void testSavingAProjectDuringDelete() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(
				Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, TestUtils.DEFAULT_TEST_PROJECT_NAME);

		try {
			projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getInstrumentation().getTargetContext());
		} catch (CompatibilityProjectException compatibilityException) {
			fail("Incompatible project");
		} catch (ProjectException projectException) {
			fail("Failed to identify project");
		}

		Project currentProject = projectManager.getCurrentProject();
		assertNotNull(String.format("Could not load %s project.", TestUtils.DEFAULT_TEST_PROJECT_NAME), currentProject);

		File directory = new File(Constants.DEFAULT_ROOT + "/" + TestUtils.DEFAULT_TEST_PROJECT_NAME);
		assertTrue(String.format("Directory %s does not exist", directory.getPath()), directory.exists());

		// simulate multiple saving trigger asynchronous (occurs in black box testing)
		for (int i = 0; i < 3; i++) {
			currentProject.setDescription(currentProject.getDescription() + i);
			projectManager.saveProject(getInstrumentation().getTargetContext());
		}

		// simulate deletion, saveProject asyncTask will be "automatically" cancelled (Please remark: there is still a chance
		// of a race condition, because we rely on a "project" reference which gets used in a multithreaded environment)
		projectManager.setProject(null);
		TestUtils.deleteTestProjects();

		assertFalse(String.format("Directory %s does still exist", directory.getPath()), directory.exists());
	}

	public void testLoadProjectWithInvalidNestingBrickReferences() throws CompatibilityProjectException, OutdatedVersionProjectException, LoadingProjectException {
		TestUtils.copyAssetProjectZipFile(getInstrumentation().getContext(), ZIP_FILENAME_WRONG_NESTING_BRICKS, Constants.TMP_PATH);
		UtilZip.unZipFile(Constants.TMP_PATH + "/" + ZIP_FILENAME_WRONG_NESTING_BRICKS, Constants.DEFAULT_ROOT + "/"
				+ PROJECT_NAME_NESTING_BRICKS);

		projectManager.loadProject(PROJECT_NAME_NESTING_BRICKS, getInstrumentation().getTargetContext());
		Project project = projectManager.getCurrentProject();

		assertTrue("Cannot load " + PROJECT_NAME_NESTING_BRICKS + " project", project != null);
		assertEquals("Wrong project loaded", PROJECT_NAME_NESTING_BRICKS, project.getName());

		assertTrue("Nesting brick references not correct!", projectManager.checkNestingBrickReferences(false, false));

		UtilZip.deleteZipFile(ZIP_FILENAME_WRONG_NESTING_BRICKS, Constants.TMP_PATH);
		TestUtils.deleteTestProjects(PROJECT_NAME_NESTING_BRICKS);
	}
}
