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
