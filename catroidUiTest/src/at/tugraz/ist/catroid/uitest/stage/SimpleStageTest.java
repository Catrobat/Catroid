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
package at.tugraz.ist.catroid.uitest.stage;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SimpleStageTest extends ActivityInstrumentationTestCase2<StageActivity> {

	private Solo solo;

	public SimpleStageTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testSimple() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };

		byte[] result = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		result = StageActivity.stageListener.getPixels(19, 19, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		result = StageActivity.stageListener.getPixels(-1, -1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);
		assertTrue("Just for FileTest", true);
	}

	private void createProject() {
		Values.SCREEN_HEIGHT = 20;
		Values.SCREEN_WIDTH = 20;
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		ProjectManager.getInstance().setProject(project);
		StorageHandler.getInstance().saveProject(project);
	}
}
