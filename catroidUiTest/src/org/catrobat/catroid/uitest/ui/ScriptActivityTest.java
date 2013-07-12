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
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public ScriptActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
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
		UiTestUtils.waitForFragment(solo, R.id.fragment_script_relative_layout);

		checkMainMenuButton();

		UiTestUtils.getIntoLooksFromMainMenu(solo, true);
		UiTestUtils.waitForFragment(solo, R.id.fragment_look_relative_layout);

		checkMainMenuButton();

		UiTestUtils.getIntoSoundsFromMainMenu(solo);
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound_relative_layout);

		checkMainMenuButton();
	}

	public void testPlayProgramButton() {
		UiTestUtils.waitForFragment(solo, R.id.fragment_script_relative_layout);

		String scripts = solo.getString(R.string.scripts);
		String looks = solo.getString(R.string.category_looks);
		String sounds = solo.getString(R.string.sounds);

		assertTrue("Spinner item '" + scripts + "' not shown", solo.waitForText(scripts, 0, 300, false, true));

		checkplayProgramButton();

		UiTestUtils.changeToFragmentViaActionbar(solo, scripts, looks);
		UiTestUtils.waitForFragment(solo, R.id.fragment_look_relative_layout);
		assertTrue("Spinner item '" + looks + "' not shown", solo.waitForText(looks, 0, 300, false, true));

		checkplayProgramButton();

		UiTestUtils.changeToFragmentViaActionbar(solo, looks, sounds);
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound_relative_layout);
		assertTrue("Spinner item '" + sounds + "' not shown", solo.waitForText(sounds, 0, 300, false, true));

		checkplayProgramButton();
	}

	public void testChangeViaSpinner() {
		int scriptsSpinnerIndexRelativeToCurrentSelected = 0;
		int looksSpinnerIndexRelativeToCurrentSelected = 1;
		int soundsSpinnerIndexRelativeToCurrentSelected = 2;

		int expectedNumberOfSpinnerItems = 3;
		int currentNumberOfSpinnerItems = UiTestUtils.getActionBarSpinnerItemCount(solo);
		assertEquals("There should be " + expectedNumberOfSpinnerItems + " spinner items",
				expectedNumberOfSpinnerItems, currentNumberOfSpinnerItems);

		String scripts = solo.getString(R.string.scripts);
		String looks = solo.getString(R.string.category_looks);
		String sounds = solo.getString(R.string.sounds);

		final int timeToWait = 300;

		assertTrue("Spinner item '" + scripts + "' not selected", solo.waitForText(scripts, 0, timeToWait, false, true));
		UiTestUtils.waitForFragment(solo, R.id.fragment_script_relative_layout);

		UiTestUtils.clickOnActionBarSpinnerItem(solo, scriptsSpinnerIndexRelativeToCurrentSelected);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.waitForFragment(solo, R.id.fragment_script_relative_layout);
		assertTrue("Spinner item '" + scripts + "' not selected", solo.waitForText(scripts, 0, timeToWait, false, true));

		UiTestUtils.clickOnActionBarSpinnerItem(solo, looksSpinnerIndexRelativeToCurrentSelected);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.waitForFragment(solo, R.id.fragment_look_relative_layout);
		assertTrue("Spinner item '" + looks + "' not selected", solo.waitForText(looks, 0, timeToWait, false, true));

		soundsSpinnerIndexRelativeToCurrentSelected = 1;

		UiTestUtils.clickOnActionBarSpinnerItem(solo, soundsSpinnerIndexRelativeToCurrentSelected);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound_relative_layout);
		assertTrue("Spinner item '" + sounds + "' not selected", solo.waitForText(sounds, 0, timeToWait, false, true));

		scriptsSpinnerIndexRelativeToCurrentSelected = -2;

		UiTestUtils.clickOnActionBarSpinnerItem(solo, scriptsSpinnerIndexRelativeToCurrentSelected);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.waitForFragment(solo, R.id.fragment_script_relative_layout);
		assertTrue("Spinner item '" + scripts + "' not selected", solo.waitForText(scripts, 0, timeToWait, false, true));
	}

	public void testOverflowMenuItemSettings() {
		UiTestUtils.waitForFragment(solo, R.id.fragment_script_relative_layout);

		String scripts = solo.getString(R.string.scripts);
		String looks = solo.getString(R.string.category_looks);
		String sounds = solo.getString(R.string.sounds);

		assertTrue("Spinner item '" + scripts + "' not shown", solo.waitForText(scripts, 0, 300, false, true));

		checkSettingsAndGoBack();

		UiTestUtils.changeToFragmentViaActionbar(solo, scripts, looks);
		UiTestUtils.waitForFragment(solo, R.id.fragment_look_relative_layout);
		assertTrue("Spinner item '" + looks + "' not shown", solo.waitForText(looks, 0, 300, false, true));

		checkSettingsAndGoBack();

		UiTestUtils.changeToFragmentViaActionbar(solo, looks, sounds);
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound_relative_layout);
		assertTrue("Spinner item '" + sounds + "' not shown", solo.waitForText(sounds, 0, 300, false, true));

		checkSettingsAndGoBack();
	}

	private void checkplayProgramButton() {
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in StageActivity", StageActivity.class);

		solo.goBack();
		solo.goBack();

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in SoundActivity", ScriptActivity.class);
	}

	private void checkMainMenuButton() {
		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Main menu is not displayed", MainMenuActivity.class);
	}

	private void checkSettingsAndGoBack() {
		solo.clickOnMenuItem(solo.getString(R.string.main_menu_settings), true);
		solo.assertCurrentActivity("Not in " + SettingsActivity.class.getSimpleName(), SettingsActivity.class);
		solo.goBack();
		solo.assertCurrentActivity("Not in " + ScriptActivity.class.getSimpleName(), ScriptActivity.class);
	}
}
