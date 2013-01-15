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
package org.catrobat.catroid.uitest.ui;

import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo = null;

	public ScriptActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();

		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoSoundsFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testOrientation() throws NameNotFoundException {
		Activity currentActivity = solo.getCurrentActivity();

		/// Method 1: Assert it is currently in portrait mode.
		assertEquals("ScriptActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, currentActivity
				.getResources().getConfiguration().orientation);

		/// Method 2: Retrieve info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = currentActivity.getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(currentActivity.getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);

		assertEquals(ScriptActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}

	public void testMainMenuButton() {
		UiTestUtils.clickOnUpActionBarButton(solo);

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Main menu is not displayed", MainMenuActivity.class);
	}

	public void testPlayProgramButton() {
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in StageActivity", StageActivity.class);

		solo.goBack();
		solo.goBack();

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in SoundActivity", ScriptActivity.class);
	}

	public void testChangeViaSpinner() {
		int scriptsSpinnerIndexRelativeToCurrentSelected = -2;
		int costumesSpinnerIndexRelativeToCurrentSelected = -1;
		int soundsSpinnerIndexRelativeToCurrentSelected = 0;

		int expectedNumberOfSpinnerItems = 3;
		int actualNumberOfSpinnerItems = solo.getCurrentSpinners().get(0).getAdapter().getCount();
		assertEquals("There should be " + expectedNumberOfSpinnerItems + " spinner items",
				expectedNumberOfSpinnerItems, actualNumberOfSpinnerItems);

		String sounds = solo.getString(R.string.sounds);
		clickOnSpinnerItem(soundsSpinnerIndexRelativeToCurrentSelected);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		clickOnSpinnerItem(scriptsSpinnerIndexRelativeToCurrentSelected);
		soundsSpinnerIndexRelativeToCurrentSelected = 2;

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		clickOnSpinnerItem(soundsSpinnerIndexRelativeToCurrentSelected);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertTrue("Sounds spinner item is not selected", solo.searchText(sounds, true));

		clickOnSpinnerItem(costumesSpinnerIndexRelativeToCurrentSelected);
		soundsSpinnerIndexRelativeToCurrentSelected = 1;

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		clickOnSpinnerItem(soundsSpinnerIndexRelativeToCurrentSelected);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertTrue("Sounds spinner item is not selected", solo.searchText(sounds, true));
	}

	public void testOverflowMenuItemSettings() {
		String settings = solo.getString(R.string.main_menu_settings);
		solo.clickOnMenuItem(settings, true);
		solo.assertCurrentActivity("Not in SettingsActivity", SettingsActivity.class);
		solo.goBack();
		solo.assertCurrentActivity("Not in SoundActivity", ScriptActivity.class);
	}

	private void clickOnSpinnerItem(int itemIndex) {
		solo.pressSpinnerItem(0, itemIndex);
	}
}
