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
package org.catrobat.catroid.uitest.drone;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.widget.ListView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.test.drone.DroneTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class DroneSettingsActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public DroneSettingsActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		DroneTestUtils.createStandardDroneProject();
		SettingsActivity.enableARDroneBricks(getActivity(), true);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.enableARDroneBricks(getActivity(), false);
		TestUtils.deleteTestProjects();
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testDroneTermsOfUsePermanentAgree() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		SettingsActivity.setTermsOfServiceAgreedPermanently(getActivity(), false);
		assertFalse("Terms of servie should not be accepted", SettingsActivity.areTermsOfServiceAgreedPermanently(getActivity()));
		assertFalse("Terms of servie should not be accepted", preferences.getBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, true));

		SettingsActivity.setTermsOfServiceAgreedPermanently(getActivity(), true);
		assertTrue("Terms of servie should be permanently accepted", SettingsActivity.areTermsOfServiceAgreedPermanently(getActivity()));
		assertTrue("Terms of servie should be permanently accepted", preferences.getBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, false));
	}


	public void testDroneConnectToDroneDialog() {
		assertTrue("DroneBricks not activated!", SettingsActivity.isDroneSharedPreferenceEnabled(getActivity()));

		ProjectManager.getInstance().initializeStandardProject(getActivity());

		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.programs));
		solo.waitForText(solo.getString(R.string.default_project_name));
		solo.clickOnText(solo.getString(R.string.default_project_name));

		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));

		UiTestUtils.addNewBrick(solo, R.string.brick_drone_takeoff_land);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrick(solo, -1.25f);

		UiTestUtils.clickOnPlayButton(solo);
		solo.waitForText(solo.getString(R.string.error_no_drone_connected_title));
		assertTrue("DroneBrick present but no drone connection dialog", solo.searchText(solo.getString(R.string.error_no_drone_connected_title)));
		solo.clickOnText(solo.getString(R.string.close));

		solo.waitForText(solo.getString(R.string.brick_drone_takeoff_land));
		solo.clickOnText(solo.getString(R.string.brick_drone_takeoff_land));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.yes));

		UiTestUtils.clickOnPlayButton(solo);
		solo.waitForText(solo.getString(R.string.error_no_drone_connected_title));
		assertTrue("DroneBrick present but no drone connection dialog", !solo.searchText(solo.getString(R.string.error_no_drone_connected_title)));
	}

	public void testDroneSettingsActivity() {

		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.clickOnMenuItem(solo.getString(R.string.settings));
		solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
		solo.clickOnText(solo.getString(R.string.preference_description_quadcopter_bricks));

		solo.waitForText(solo.getString(R.string.brick_drone_set_config));
		solo.clickOnText(solo.getString(R.string.brick_drone_set_config));
		solo.waitForText(solo.getString(R.string.drone_config_indoor_without_hull));
		solo.clickOnText(solo.getString(R.string.drone_config_indoor_without_hull));
		assertTrue("Wrong Settings set, altitude limit should be " + solo.getString(R.string.drone_set_altitude_max_3m),
				solo.waitForText(solo.getString(R.string.drone_set_altitude_max_3m)));
		assertTrue("Wrong Settings set, vertical speed limit should be " + solo.getString(R.string.drone_set_vertical_indoor),
				solo.waitForText(solo.getString(R.string.drone_set_vertical_indoor)));
		assertTrue("Wrong Settings set, rotation speed limit should be " + solo.getString(R.string.drone_set_rotation_indoor),
				solo.waitForText(solo.getString(R.string.drone_set_rotation_indoor)));
		assertTrue("Wrong Settings set, tilt limit should be " + solo.getString(R.string.drone_set_tilt_indoor),
				solo.waitForText(solo.getString(R.string.drone_set_tilt_indoor)));

		solo.clickOnText(solo.getString(R.string.brick_drone_set_config));
		solo.waitForText(solo.getString(R.string.brick_drone_set_config));
		solo.clickOnText(solo.getString(R.string.drone_config_outdoor_without_hull));
		assertTrue("Wrong Settings set, altitude limit should be " + solo.getString(R.string.drone_set_altitude_max_3m),
				solo.waitForText(solo.getString(R.string.drone_set_altitude_max_3m)));
		assertTrue("Wrong Settings set, vertical speed limit should be " + solo.getString(R.string.drone_set_vertical_outdoor),
				solo.waitForText(solo.getString(R.string.drone_set_vertical_outdoor)));
		assertTrue("Wrong Settings set, rotation speed limit should be " + solo.getString(R.string.drone_set_rotation_outdoor),
				solo.waitForText(solo.getString(R.string.drone_set_rotation_outdoor)));
		assertTrue("Wrong Settings set, tilt limit should be " + solo.getString(R.string.drone_set_tilt_outdoor),
				solo.waitForText(solo.getString(R.string.drone_set_tilt_outdoor)));

		solo.clickOnText(solo.getString(R.string.brick_drone_set_config));
		solo.waitForText(solo.getString(R.string.drone_config_indoor_with_hull));
		solo.clickOnText(solo.getString(R.string.drone_config_indoor_with_hull));
		assertTrue("Wrong Settings set, altitude limit should be " + solo.getString(R.string.drone_set_altitude_max_3m),
				solo.waitForText(solo.getString(R.string.drone_set_altitude_max_3m)));
		assertTrue("Wrong Settings set, vertical speed limit should be " + solo.getString(R.string.drone_set_vertical_indoor),
				solo.waitForText(solo.getString(R.string.drone_set_vertical_indoor)));
		assertTrue("Wrong Settings set, rotation speed limit should be " + solo.getString(R.string.drone_set_rotation_indoor),
				solo.waitForText(solo.getString(R.string.drone_set_rotation_indoor)));
		assertTrue("Wrong Settings set, tilt limit should be " + solo.getString(R.string.drone_set_tilt_indoor),
				solo.waitForText(solo.getString(R.string.drone_set_tilt_indoor)));

		solo.clickOnText(solo.getString(R.string.brick_drone_set_config));
		solo.waitForText(solo.getString(R.string.drone_config_outdoor_with_hull));
		solo.clickOnText(solo.getString(R.string.drone_config_outdoor_with_hull));
		assertTrue("Wrong Settings set, altitude limit should be " + solo.getString(R.string.drone_set_altitude_max_3m),
				solo.waitForText(solo.getString(R.string.drone_set_altitude_max_3m)));
		assertTrue("Wrong Settings set, vertical speed limit should be " + solo.getString(R.string.drone_set_vertical_outdoor),
				solo.waitForText(solo.getString(R.string.drone_set_vertical_outdoor)));
		assertTrue("Wrong Settings set, rotation speed limit should be " + solo.getString(R.string.drone_set_rotation_outdoor),
				solo.waitForText(solo.getString(R.string.drone_set_rotation_outdoor)));
		assertTrue("Wrong Settings set, tilt limit should be " + solo.getString(R.string.drone_set_tilt_outdoor),
				solo.waitForText(solo.getString(R.string.drone_set_tilt_outdoor)));

		solo.goBack();
		solo.goBack();
	}

	public void testOrientation() throws PackageManager.NameNotFoundException {
		solo.waitForActivity(MainMenuActivity.class);
		solo.clickOnMenuItem(solo.getString(R.string.settings));
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		/// Method 1: Assert it is currently in portrait mode.
		assertEquals("SettingsActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, getActivity().getResources().getConfiguration().orientation);

		/// Method 2: Retreive info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = getActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(getActivity().getComponentName(), PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscapeMode mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.landscapeMode);

		assertEquals(SettingsActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
	}

	public void testToggleDroneBricks() {
		solo.waitForActivity(MainMenuActivity.class);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		solo.waitForActivity(ScriptActivity.class);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(solo.getString(R.string.category_control));

		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);

		assertTrue("Drone brick category is not showing!", solo.searchText(solo.getString(R.string.category_drone)));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class);
		solo.clickOnActionBarHomeButton();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnMenuItem(solo.getString(R.string.settings));
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		assertTrue("Wrong title", solo.searchText(solo.getString(R.string.preference_title)));

		solo.clickOnText(solo.getString(R.string.preference_description_quadcopter_bricks));
		solo.waitForText(solo.getString(R.string.preference_description_quadcopter_bricks));
		solo.clickOnText(solo.getString(R.string.preference_description_quadcopter_bricks));
		solo.goBack();
		solo.goBack();

		solo.waitForActivity(MainMenuActivity.class);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		solo.waitForText(solo.getString(R.string.category_control));

		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);

		assertFalse("Drone brick category is showing!", solo.searchText(solo.getString(R.string.category_drone)));
	}

	public void testToggleDroneSensors() {
		UiTestUtils.clearAllUtilTestProjects();

		UiTestUtils.createEmptyProject();

		solo.waitForActivity(MainMenuActivity.class);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		solo.waitForActivity(ScriptActivity.class);
		UiTestUtils.addNewBrick(solo, R.string.brick_drone_move_up);
		UiTestUtils.dragFloatingBrick(solo, -1.25f);
		solo.clickOnView(solo.getView(R.id.brick_drone_move_edit_text_second));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensors));
		assertTrue("Drone sensors are not showing!", solo.searchText("drone_"));
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class);
		solo.clickOnActionBarHomeButton();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnMenuItem(solo.getString(R.string.settings));
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		assertTrue("Wrong title", solo.searchText(solo.getString(R.string.preference_title)));

		solo.clickOnText(solo.getString(R.string.preference_description_quadcopter_bricks));
		solo.waitForText(solo.getString(R.string.preference_description_quadcopter_bricks));
		solo.clickOnText(solo.getString(R.string.preference_description_quadcopter_bricks));
		solo.goBack();
		solo.goBack();

		assertFalse("Drone preference should now be disable", SettingsActivity.isDroneSharedPreferenceEnabled(getActivity()));

		solo.waitForActivity(MainMenuActivity.class);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		solo.waitForActivity(ScriptActivity.class);
		solo.clickOnView(solo.getView(R.id.brick_drone_move_edit_text_second));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensors));
		assertFalse("Drone sensors are showing!", solo.searchText("drone_"));
	}
}
