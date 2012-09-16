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
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class InitializeStageLandscapeTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;

	public InitializeStageLandscapeTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
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

	public void testStartStageInLandscape() {
		createProject();
		solo.sleep(100);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);

		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };

		byte[] result = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		result = StageActivity.stageListener.getPixels(19, 19, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		result = StageActivity.stageListener.getPixels(-1, -1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, result);

		solo.goBack();
		solo.goBack();
		assertTrue("Just for FileTest", true);
	}

	private void createProject() {
		Values.SCREEN_HEIGHT = 20;
		Values.SCREEN_WIDTH = 20;

		UiTestUtils.createEmptyProject();
	}
}
