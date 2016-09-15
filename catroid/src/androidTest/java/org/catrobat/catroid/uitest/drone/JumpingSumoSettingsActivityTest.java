/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.test.drone.JumpingSumoTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class JumpingSumoSettingsActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public JumpingSumoSettingsActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		JumpingSumoTestUtils.createDefaultJumpingSumoProject();
		SettingsActivity.enableJumpingSumoBricks(getActivity(), true);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.enableJumpingSumoBricks(getActivity(), false);
		TestUtils.deleteTestProjects();
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testJumpingSumoTermsOfUsePermanentAgree() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		SettingsActivity.setTermsOfServiceJSAgreedPermanently(getActivity(), false);
		assertFalse("Terms of service should not be accepted", SettingsActivity.areTermsOfServiceJSAgreedPermanently(
				getActivity()));
		assertFalse("Terms of service should not be accepted", preferences.getBoolean(SettingsActivity
				.SETTINGS_PARROT_JUMPING_SUMO_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, true));


		SettingsActivity.setTermsOfServiceJSAgreedPermanently(getActivity(), true);
		assertTrue("Terms of service should be permanently accepted", SettingsActivity
				.areTermsOfServiceJSAgreedPermanently(getActivity()));
		assertTrue("Terms of service should be permanently accepted", preferences.getBoolean(SettingsActivity
				.SETTINGS_PARROT_JUMPING_SUMO_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, false));
	}

	public void testJumpingSumoConnectToJumpingSumoDialog() {
		assertTrue("JumpingSumoBricks not activated!", SettingsActivity.isJSSharedPreferenceEnabled(getActivity()));

		ProjectManager.getInstance().initializeDefaultProject(getActivity());

		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.programs));
		solo.waitForText(solo.getString(R.string.default_project_name));
		solo.clickOnText(solo.getString(R.string.default_project_name));

		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));

		UiTestUtils.addNewBrick(solo, R.string.brick_jumping_sumo_turn);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrick(solo, -1.25f);

		UiTestUtils.clickOnPlayButton(solo);
		solo.waitForText(solo.getString(R.string.error_no_jumpingsumo_connected_title));
		assertTrue("JumpingSumoBrick present but no jumping sumo connection dialog", solo.searchText(
				solo.getString(R.string.error_no_jumpingsumo_connected_title)));
		solo.clickOnText(solo.getString(R.string.close));

		solo.waitForText(solo.getString(R.string.brick_jumping_sumo_turn));
		solo.clickOnText(solo.getString(R.string.brick_jumping_sumo_turn));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.yes));

		UiTestUtils.clickOnPlayButton(solo);
		solo.waitForText(solo.getString(R.string.error_no_jumpingsumo_connected_title));
		assertTrue("JumpingSumoBrick present but no jumping sumo connection dialog", !solo.searchText(solo.getString(R.string.error_no_jumpingsumo_connected_title)));
	}

	public void testJumpingSumoOrientation() throws PackageManager.NameNotFoundException {
		solo.waitForActivity(MainMenuActivity.class);
		solo.clickOnMenuItem(solo.getString(R.string.settings));
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		/// Method 1: Assert it is currently in portrait mode.
		assertEquals("SettingsActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, getActivity().getResources().getConfiguration().orientation);

		/// Method 2: Retreive info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = getActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(getActivity().getComponentName(), PackageManager.GET_META_DATA);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscapeMode mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);

		assertEquals(SettingsActivity.class.getSimpleName() + " not set to be in portrait mode in AndroidManifest.xml!", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, activityInfo.screenOrientation);
		solo.waitForText(solo.getString(R.string.preference_title_enable_jumpingsumo_bricks));
	}
}
