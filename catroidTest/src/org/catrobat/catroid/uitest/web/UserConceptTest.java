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
package org.catrobat.catroid.uitest.web;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.web.ServerCalls;

import java.util.ArrayList;
import java.util.Locale;

//Aborts on emulator
public class UserConceptTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String saveToken;
	private String loginDialogTitle;
	private String uploadDialogTitle;

	public UserConceptTest() {
		super(MainMenuActivity.class);
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		super.setUp();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Constants.TOKEN, Constants.NO_TOKEN);
		loginDialogTitle = solo.getString(R.string.login_register_dialog_title);
		uploadDialogTitle = solo.getString(R.string.upload_project_dialog_title);
		solo.waitForActivity(MainMenuActivity.class);
		UiTestUtils.createEmptyProject();
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, saveToken).commit();
		Reflection.setPrivateField(ServerCalls.getInstance(), "emailForUiTests", null);
		super.tearDown();
	}

	@Device
	public void testLicenceLinkPresent() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, null).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);

		assertTrue("Licence text not present", solo.searchText(solo.getString(R.string.register_terms)));
		assertTrue("Licence link not present",
				solo.searchText(solo.getString(R.string.register_pocketcode_terms_of_use_text)));
	}

	@Device
	public void testRegisterNewUser() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);

		fillLoginDialog(true);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testRegisterWithValidTokenSaved() throws Throwable {
		setTestUrl();
		UiTestUtils.createValidUser(getActivity());

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(uploadDialogTitle);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testTokenPersistance() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, "").commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);
		fillLoginDialog(true);

		solo.waitForText(uploadDialogTitle);
		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));

		solo.waitForDialogToClose(10000);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testRegisterWithWrongToken() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, "wrong_token").commit();

		UiTestUtils.clickOnText(solo, solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);
		fillLoginDialog(true);

		assertNotNull("Upload Dialog is not shown.", uploadDialogTitle);
		UiTestUtils.goBackToHome(getInstrumentation());
	}

	@Device
	public void testRegisterWithShortPassword() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, null).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);
		fillLoginDialog(false);

		assertNotNull("no error dialog is shown", solo.getText(solo.getString(R.string.register_error)));
		solo.clickOnButton(0);
		assertNotNull("Login Dialog is not shown.", solo.getText(solo.getString(R.string.login_register_dialog_title)));
	}

	@Device
	public void testRegisterUsernameDifferentCases() throws Throwable {
		setTestUrl();
		clearSharedPreferences();

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);

		String username = "UpperCaseUser" + System.currentTimeMillis();
		fillLoginDialogWithUsername(true, username);

		solo.waitForText(uploadDialogTitle);
		String cancel = solo.getString(R.string.cancel_button);
		if (solo.searchText(cancel)) {
			solo.clickOnText(cancel);
		}

		clearSharedPreferences();

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);

		username = username.toLowerCase(Locale.ENGLISH);
		fillLoginDialogWithUsername(true, username);
		solo.waitForText(uploadDialogTitle);

		TextView uploadProject = (TextView) solo.getView(R.id.dialog_upload_size_of_project);
		ArrayList<View> currentViews = solo.getCurrentViews();
		assertTrue("Cannot login because username is upper or lower case", currentViews.contains(uploadProject));
	}

	private void setTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	private void fillLoginDialogWithUsername(boolean correct, String username) {
		assertNotNull("Login Dialog is not shown.", solo.getText(solo.getString(R.string.login_register_dialog_title)));
		// enter a username
		String testUser = username;
		EditText projectNameEditText = (EditText) solo.getView(R.id.username);
		solo.clearEditText(projectNameEditText);
		solo.enterText(projectNameEditText, testUser);
		solo.sendKey(Solo.ENTER);
		// enter a password
		String testPassword;
		if (correct) {
			testPassword = "blubblub";
		} else {
			testPassword = "short";
		}
		EditText passwordEditText = (EditText) solo.getView(R.id.password);
		solo.clearEditText(passwordEditText);
		solo.clickOnView(passwordEditText);
		solo.enterText(passwordEditText, testPassword);

		// set the email to use. we need a random email because the server does not allow same email with different users
		String testEmail = testUser + "@gmail.com";
		Reflection.setPrivateField(ServerCalls.getInstance(), "emailForUiTests", testEmail);
		solo.sendKey(Solo.ENTER);

		int buttonId = android.R.id.button1;
		solo.clickOnView(solo.getView(buttonId));
	}

	private void fillLoginDialog(boolean correct) {
		fillLoginDialogWithUsername(correct, "testUser" + System.currentTimeMillis());
	}

	private void clearSharedPreferences() {
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation()
				.getTargetContext());
		Editor edit = defaultSharedPreferences.edit();
		edit.clear();
		edit.commit();
	}
}
