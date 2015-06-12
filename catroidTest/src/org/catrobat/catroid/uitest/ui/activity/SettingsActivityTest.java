/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui.activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.widget.ListView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.test.drone.DroneTestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class SettingsActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String settings;

	public SettingsActivityTest() {
		super(MainMenuActivity.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		settings = solo.getString(R.string.settings);
	}

	public void testToggleDroneBricks() {
		String dronePreferenceString = solo.getString(R.string.preference_description_quadcopter_bricks);
		String categoryDroneLabel = solo.getString(R.string.category_drone);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//disable drone bricks if enabled
		if (preferences.getBoolean(SettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, false)) {
			solo.clickOnMenuItem(settings);
			solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
			solo.clickOnText(dronePreferenceString);
			solo.goBack();
			solo.waitForActivity(MainMenuActivity.class);
		}

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		solo.waitForActivity(ScriptActivity.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(solo.getString(R.string.category_control));

		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);

		assertFalse("Drone brick category is showing!", solo.searchText(categoryDroneLabel));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class);
		solo.clickOnActionBarHomeButton();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnMenuItem(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		assertTrue("Wrong title", solo.searchText(solo.getString(R.string.preference_title)));

		solo.clickOnText(dronePreferenceString);

		solo.goBack();

		assertTrue("Drone preference should now be enabled",
				preferences.getBoolean(SettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, false));

		solo.waitForActivity(MainMenuActivity.class);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		solo.waitForText(solo.getString(R.string.category_control));

		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);

		assertTrue("Drone brick category is not showing!", solo.searchText(categoryDroneLabel));
	}

	public void testToggleMindstormsNXTBricks() {
		DroneTestUtils.disableARDroneBricks(getActivity());
		String mindstormsPreferenceString = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);

		//disable mindstorms bricks, if enabled at start
		if (SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(getInstrumentation().getTargetContext())) {
			solo.clickOnMenuItem(settings);
			solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
			solo.clickOnText(mindstormsPreferenceString); // submenu
			solo.sleep(200);
			solo.clickOnText(mindstormsPreferenceString); // checkbox
			solo.goBack();
			solo.goBack();
		}

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		assertFalse("Lego brick category is showing!", solo.searchText(categoryLegoNXTLabel));
		solo.goBack();
		UiTestUtils.clickOnHomeActionBarButton(solo);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnMenuItem(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		assertTrue("Wrong title", solo.searchText(solo.getString(R.string.preference_title)));

		solo.clickOnText(mindstormsPreferenceString); // submenu
		solo.sleep(200);
		solo.clickOnText(mindstormsPreferenceString); // checkbox
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.sleep(200);
		solo.scrollListToBottom(fragmentListView);
		solo.sleep(100);
		assertTrue("Lego brick category is not showing!", solo.searchText(categoryLegoNXTLabel));
	}

	public void testOrientation() throws NameNotFoundException {
		solo.clickOnMenuItem(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

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

	public void testDroneTermsOfUsePermanentAgree() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		preferences.edit().putBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, false)
				.commit();

		assertFalse("Terms of servie should not be accepted",
				SettingsActivity.areTermsOfServiceAgreedPermanently(getActivity()));

		assertFalse("Terms of servie should not be accepted",
				preferences.getBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, true));

		SettingsActivity.setTermsOfServiceAgreedPermanently(getActivity(), true);
		assertTrue("Terms of servie should be permanently accepted",
				SettingsActivity.areTermsOfServiceAgreedPermanently(getActivity()));

		assertTrue("Terms of servie should be permanently accepted",
				preferences.getBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, false));
	}
}
