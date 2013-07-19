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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

import android.test.AndroidTestCase;

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
		projectManager = ProjectManager.INSTANCE;
		// Prevent Utils from returning true in isApplicationDebuggable
		Reflection.setPrivateField(Utils.class, "isUnderTest", true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		projectManager.setProject(null);
		TestUtils.deleteTestProjects(OLD_PROJECT, NEW_PROJECT);
	}

	public void testShouldReturnFalseIfCatrobatLanguageVersionNotSupported() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED);

		boolean result = projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext(), false);
		assertFalse("Load project didn't return false", result);

		TestUtils.deleteTestProjects();

		TestUtils
				.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(Constants.SUPPORTED_CATROBAT_LANGUAGE_VERSION);

		result = projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext(), false);
		assertTrue("Load project didn't return true", result);
	}

	public void testShouldKeepExistingProjectIfCannotLoadNewProject() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersionAndName(
				Constants.SUPPORTED_CATROBAT_LANGUAGE_VERSION, OLD_PROJECT);

		boolean result = projectManager.loadProject(OLD_PROJECT, getContext(), false);
		assertTrue("Could not load project.", result);

		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED);

		result = projectManager.loadProject(NEW_PROJECT, getContext(), false);
		assertFalse("Load project didn't return false", result);

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't keep old project.", currentProject);
		assertEquals("Didn't keep old project.", OLD_PROJECT, currentProject.getName());

		TestUtils.deleteTestProjects(OLD_PROJECT, NEW_PROJECT);
	}

	public void testShouldLoadDefaultProjectIfCannotLoadAnotherProject() throws Exception {
		assertNull("Current project not null.", projectManager.getCurrentProject());

		boolean result = projectManager.loadProject(DOES_NOT_EXIST, getContext(), false);
		assertFalse("Load project didn't return false", result);

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't create default project.", currentProject);
		assertEquals("Didn't create default project.", getContext().getString(R.string.default_project_name),
				currentProject.getName());
	}
}
