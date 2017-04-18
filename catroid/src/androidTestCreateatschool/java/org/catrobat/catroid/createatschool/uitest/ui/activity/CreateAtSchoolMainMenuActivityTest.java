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
package org.catrobat.catroid.createatschool.uitest.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.createatschool.ui.CreateAtSchoolMainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;

public class CreateAtSchoolMainMenuActivityTest extends BaseActivityInstrumentationTestCase<CreateAtSchoolMainMenuActivity> {

	private static final String TAG = CreateAtSchoolMainMenuActivityTest.class.getSimpleName();
	private String login;
	private String passwordForgotten;
	private String cancel;
	private String logoutSuccessful;

	public CreateAtSchoolMainMenuActivityTest() {
		super(CreateAtSchoolMainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.setFakeToken(getActivity(), false);
		solo.sleep(300);
		login = solo.getString(R.string.login);
		passwordForgotten = solo.getString(R.string.password_forgotten);
		cancel = solo.getString(R.string.cancel);
		logoutSuccessful = solo.getString(R.string.logout_successful);
	}

	@Override
	protected void tearDown() throws Exception {
		WifiManager wifiManager = (WifiManager) getInstrumentation().getTargetContext().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
		super.tearDown();
	}

	public void testLoginDialogAppearsIfNotLoggedIn() {
		solo.waitForDialogToOpen();
		assertTrue("LoginDialog did not appear!", solo.searchText(login, 1, false, true));
	}

	public void testLoginDialogViewsAndBehavior() {
		solo.waitForDialogToOpen();
		assertTrue("LoginDialog did not appear!", solo.searchText(login, 1, false, true));
		assertFalse("Password forgotten shouldn't be visible!", solo.searchText(passwordForgotten, 1, false, true));
		assertFalse("Cancel button shouldn't be visible!", solo.searchText(cancel, 1, false, true));

		solo.goBack();
		solo.sleep(300);
		assertTrue("Dialog shouldn't be dismissible with back button press!", solo.searchText(login, 1, false, true));
	}

	public void testLoginDialogReappearsAfterLogout() {
		solo.waitForDialogToOpen();
		assertTrue("LoginDialog did not appear!", solo.searchText(login, 1, false, true));

		UiTestUtils.setFakeToken(getActivity(), true);
		solo.sleep(500);
		goToHomeActivity(getActivity());
		solo.sleep(500);
		assertFalse("LoginDialog appeared with valid token!", solo.searchText(login, 1, false, true));

		UiTestUtils.clickOnActionBar(solo, R.id.menu_logout);
		assertTrue("Logout wasn't successful!", solo.searchText(logoutSuccessful, 1, false, true));
		assertTrue("LoginDialog should reappear after logout!", solo.searchText(login, 1, false, true));
	}

	public void testNoInternetDialog() {
		solo.waitForDialogToOpen();
		WifiManager wifiManager = (WifiManager) getInstrumentation().getTargetContext().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
		solo.sleep(1000);
		goToHomeActivity(getActivity());

		assertTrue("No noInternetDialog appeared!", solo.searchText(solo.getString(R.string.error_no_internet)));
	}

	public void testLoginWithOtherThanCreateAtSchoolUser() {
		setTestUrl();
		String testUserName = "testUser" + System.currentTimeMillis();
		String testPassword = "password";
		String testUserMail = testUserName + "@catrob.at";
		UiTestUtils.createValidUserWithCredentials(getActivity(), testUserName, testPassword, testUserMail);
		Utils.logoutUser(getActivity());

		solo.waitForDialogToOpen();
		UiTestUtils.fillNativeLoginDialog(solo, testUserName, testPassword);
		solo.waitForDialogToOpen();
		assertTrue("No NoCreateAtSchoolUserErrorDialog appeared!", solo.searchText(solo.getString(R.string.error_no_nolb_user)));

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		assertEquals("Token has been set but shouldn't be!", Constants.NO_TOKEN, sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN));
		assertEquals("Username has been set but shouldn't be!", Constants.NO_USERNAME, sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME));
	}

	private static void goToHomeActivity(Activity activity) {
		Intent intent = new Intent(activity, CreateAtSchoolMainMenuActivity.class);
		activity.startActivity(intent);
	}

	private void setTestUrl() {
		try {
			runTestOnUiThread(new Runnable() {
				public void run() {
					ServerCalls.useTestUrl = true;
				}
			});
		} catch (Throwable throwable) {
			Log.e(TAG, throwable.getMessage());
		}
	}
}
