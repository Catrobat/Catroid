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
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class ProjectManagerTest extends AndroidTestCase {

	public void testShouldReturnFalseIfVersionNumberTooHigh() {
		TestUtils.createTestProjectOnLocalStorageWithVersionCode(Integer.MAX_VALUE);

		boolean result = ProjectManager.INSTANCE.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext(), false);
		assertFalse("Load project didn't return false", result);

		TestUtils.clearAllUtilTestProjects();
		TestUtils.createTestProjectOnLocalStorageWithVersionCode(0);

		result = ProjectManager.INSTANCE.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext(), false);
		assertTrue("Load project didn't return true", result);

		TestUtils.clearAllUtilTestProjects();
	}
}
