/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.test.utils.TestErrorListenerInterface;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManagerTest extends AndroidTestCase {
	private static final String OLD_PROJECT = "OLD_PROJECT";
	private static final String NEW_PROJECT = "NEW_PROJECT";
	private static final String DOES_NOT_EXIST = "DOES_NOT_EXIST";

	private ProjectManager projectManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Utils.updateScreenWidthAndHeight(getContext());
		projectManager = ProjectManager.getInstance();
		// Prevent Utils from returning true in isApplicationDebuggable
		TestUtils.setPrivateField(Utils.class, null, "isUnderTest", true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		projectManager.setProject(null);
		TestUtils.deleteTestProjects();
	}

	public void testShouldReturnFalseIfVersionNumberTooHigh() {
		TestUtils.createTestProjectOnLocalStorageWithVersionCode(Integer.MAX_VALUE);

		boolean result = projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext(), null, false);
		assertFalse("Load project didn't return false", result);

		TestUtils.deleteTestProjects();
		TestUtils.createTestProjectOnLocalStorageWithVersionCode(0);

		result = projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext(), null, false);
		assertTrue("Load project didn't return true", result);
	}

	public void testShouldKeepExistingProjectIfCannotLoadNewProject() {
		TestUtils.createTestProjectOnLocalStorageWithVersionCodeAndName(0, OLD_PROJECT);

		boolean result = projectManager.loadProject(OLD_PROJECT, getContext(), null, false);
		assertTrue("Could not load project.", result);

		TestUtils.createTestProjectOnLocalStorageWithVersionCodeAndName(Integer.MAX_VALUE, NEW_PROJECT);

		result = projectManager.loadProject(NEW_PROJECT, getContext(), null, false);
		assertFalse("Load project didn't return false", result);

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't keep old project.", currentProject);
		assertEquals("Didn't keep old project.", OLD_PROJECT, currentProject.getName());

		TestUtils.deleteTestProjects(OLD_PROJECT, NEW_PROJECT);
	}

	public void testShouldLoadDefaultProjectIfCannotLoadAnotherProject() throws Exception {
		assertNull("Current project not null.", projectManager.getCurrentProject());

		boolean result = projectManager.loadProject(DOES_NOT_EXIST, getContext(), null, false);
		assertFalse("Load project didn't return false", result);

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't create default project.", currentProject);
		assertEquals("Didn't create default project.", getContext().getString(R.string.default_project_name),
				currentProject.getName());
	}

	public void testErrorListenerInterface() {
		TestErrorListenerInterface testErrorListener = new TestErrorListenerInterface();
		String errorMessage = getContext().getString(R.string.error_load_project);
		projectManager.loadProject(DOES_NOT_EXIST, getContext(), testErrorListener, true);
		assertTrue("Wrong error message in ErrorListener", testErrorListener.errorMessage.equals(errorMessage));
	}
}
