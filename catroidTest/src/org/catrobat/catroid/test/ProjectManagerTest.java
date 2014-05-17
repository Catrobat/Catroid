/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

public class ProjectManagerTest extends AndroidTestCase {

	private static final String OLD_PROJECT = "OLD_PROJECT";
	private static final String NEW_PROJECT = "NEW_PROJECT";
	private static final String DOES_NOT_EXIST = "DOES_NOT_EXIST";

	private static final float CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED = 0.0f;

	private ProjectManager projectManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Utils.updateScreenWidthAndHeight(getContext());
		projectManager = ProjectManager.getInstance();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		projectManager.setProject(null);
		TestUtils.deleteTestProjects(OLD_PROJECT, NEW_PROJECT);
	}

	public void testShouldReturnFalseIfCatrobatLanguageVersionNotSupported() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED);

		try {
			projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext());
			fail("Project shouldn't be compatible");
		} catch (CompatibilityProjectException compatibilityException) {
			assertTrue("Incompatible project correctly identified", true);
		} catch (ProjectException projectException) {
			fail("Failed to identify incompatible project");
		}

		TestUtils.deleteTestProjects();

		TestUtils
				.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);

		try {
			projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext());
			assertTrue("Load project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Error loading project");
		}

	}

	public void testShouldKeepExistingProjectIfCannotLoadNewProject() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(
				Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, OLD_PROJECT);

		try {
			projectManager.loadProject(OLD_PROJECT, getContext());
			assertTrue("Load old project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Could not load project.");
		}

		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED);

		try {
			projectManager.loadProject(NEW_PROJECT, getContext());
			fail("Load project didn't failed to load project");
		} catch (ProjectException projectException) {
			assertTrue("Failure test of loading project is successful", true);
		}

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't keep old project.", currentProject);
		assertEquals("Didn't keep old project.", OLD_PROJECT, currentProject.getName());

		TestUtils.deleteTestProjects(OLD_PROJECT, NEW_PROJECT);
	}

	public void testShouldLoadDefaultProjectIfCannotLoadAnotherProject() throws Exception {
		assertNull("Current project not null.", projectManager.getCurrentProject());

		try {
			projectManager.loadProject(DOES_NOT_EXIST, getContext());
			fail("Load project didn't failed to load project");
		} catch (ProjectException projectException) {
			assertTrue("Failure test of loading project is successful", true);
		}

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't create default project.", currentProject);
		assertEquals("Didn't create default project.", getContext().getString(R.string.default_project_name),
				currentProject.getName());
	}
}
