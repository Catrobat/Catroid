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

import android.content.Context;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManagerTest extends InstrumentationTestCase {
	private static final String OLD_PROJECT = TestUtils.TEST_PROJECT_NAME1;
	private static final String NEW_PROJECT = TestUtils.TEST_PROJECT_NAME2;

	private Context context;
	private ProjectManager projectManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();

		Utils.updateScreenWidthAndHeight(context);
		projectManager = ProjectManager.getInstance();
		// Prevent Utils from returning true in isApplicationDebuggable
		TestUtils.setPrivateField(Utils.class, null, "isUnderTest", true);
	}

	@Override
	protected void tearDown() throws Exception {
		projectManager.setProject(null);
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testZShouldFindNoTestProjects() {
		assertFalse("Test project present.", TestUtils.TEST_PROJECT_DIR1.exists());
		assertFalse("Test project present.", TestUtils.TEST_PROJECT_DIR2.exists());
		assertNull("Current project not null.", projectManager.getCurrentProject());
	}

	public void testShouldReturnFalseIfVersionNumberTooHigh() throws InterruptedException {
		TestUtils.createTestProjectOnLocalStorageWithVersionCode(this, Integer.MAX_VALUE);

		boolean result = projectManager.loadProject(TestUtils.TEST_PROJECT_NAME1, context, false);
		assertFalse("Load project didn't return false", result);

		projectManager.setProject(null);
		TestUtils.deleteTestProjects();
		TestUtils.createTestProjectOnLocalStorageWithVersionCode(this, 0);

		result = projectManager.loadProject(TestUtils.TEST_PROJECT_NAME1, context, false);
		assertTrue("Load project didn't return true", result);
	}

	public void testShouldKeepExistingProjectIfCannotLoadNewProject() throws InterruptedException {
		TestUtils.createTestProjectOnLocalStorageWithVersionCodeAndName(this, 0, OLD_PROJECT);

		boolean result = projectManager.loadProject(OLD_PROJECT, context, false);
		assertTrue("Could not load project.", result);

		TestUtils.createTestProjectOnLocalStorageWithVersionCodeAndName(this, Integer.MAX_VALUE, NEW_PROJECT);

		result = projectManager.loadProject(NEW_PROJECT, context, false);
		assertFalse("Load project didn't return false", result);

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't keep old project.", currentProject);
		assertEquals("Didn't keep old project.", OLD_PROJECT, currentProject.getName());
	}

	public void testShouldLoadDefaultProjectIfCannotLoadAnotherProject() throws Exception {
		assertNull("Current project not null.", projectManager.getCurrentProject());

		boolean result = projectManager.loadProject("DOES_NOT_EXIST", context, false);
		assertFalse("Load project didn't return false", result);

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull("Didn't create default project.", currentProject);
		assertEquals("Didn't create default project.", context.getString(R.string.default_project_name),
				currentProject.getName());
	}
}
