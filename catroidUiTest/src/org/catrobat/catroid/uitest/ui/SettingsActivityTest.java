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
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class SettingsActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public SettingsActivityTest() {
		super(MainMenuActivity.class);
	}

	public void testToggleMindstormBricks() {
		String settings = solo.getString(R.string.main_menu_settings);
		String mindstormsPreferenceString = solo.getString(R.string.preference_title_enable_mindstorm_bricks);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//disable mindstorm bricks, if enabled at start
		if (preferences.getBoolean("setting_mindstorm_bricks", false)) {
			solo.clickOnMenuItem(settings);
			solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
			solo.clickOnText(mindstormsPreferenceString);
			solo.goBack();
		}

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		assertFalse("Lego brick category is showing!", solo.searchText(categoryLegoNXTLabel));
		solo.goBack();
		// 0 is the Home Button in the ActionBar
		solo.clickOnImage(0);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnMenuItem(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		assertTrue("Wrong title", solo.searchText(solo.getString(R.string.preference_title)));

		solo.clickOnText(mindstormsPreferenceString);
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		assertTrue("Lego brick category is not showing!", solo.searchText(categoryLegoNXTLabel));
	}

	public void testOrientation() throws NameNotFoundException {
		/// Method 1: Assert it is currently in portrait mode.
		assertEquals("SettingsActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, getActivity()
				.getResources().getConfiguration().orientation);

		/// Method 2: Retreive info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = getActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(getActivity().getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);

		assertEquals(
				SettingsActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}
}
