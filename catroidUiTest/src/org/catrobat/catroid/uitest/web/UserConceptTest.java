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
package org.catrobat.catroid.uitest.web;

import java.util.ArrayList;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.web.ServerCalls;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class UserConceptTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private String saveToken;

	//private String testUser;

	public UserConceptTest() {
		super(MainMenuActivity.class);
		UiTestUtils.clearAllUtilTestProjects();
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Constants.TOKEN, "0");
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, saveToken).commit();
		Reflection.setPrivateField(ServerCalls.getInstance(), "emailForUiTests", null);
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testLicenceLinkPresent() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, null).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));

		assertTrue("Licence text not present", solo.searchText(solo.getString(R.string.register_terms)));
		assertTrue("Licence link not present",
				solo.searchText(solo.getString(R.string.register_pocketcode_terms_of_use_text)));
	}

	public void testRegisterNewUser() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.sleep(1000);

		fillLoginDialog(true);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	public void testRegisterWithValidTokenSaved() throws Throwable {
		setTestUrl();
		UiTestUtils.createValidUser(getActivity());

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.sleep(5000);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	public void testTokenPersistance() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, "").commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.sleep(1000);
		fillLoginDialog(true);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
		solo.goBack();

		solo.waitForDialogToClose(10000);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	public void testRegisterWithWrongToken() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, "wrong_token").commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.sleep(4000);
		fillLoginDialog(true);

		assertNotNull("Login Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	public void testRegisterWithShortPassword() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, null).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.sleep(1000);
		fillLoginDialog(false);

		assertNotNull("no error dialog is shown", solo.getText(solo.getString(R.string.register_error)));
		solo.clickOnButton(0);
		assertNotNull("Login Dialog is not shown.", solo.getText(solo.getString(R.string.login_register_dialog_title)));
	}

	public void testLoginWhenUploading() throws Throwable {
		setTestUrl();
		clearSharedPreferences();

		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.sleep(4000);

		String username = "MAXmUstermann"; //real username is MaxMustermann
		String password = "password";
		String testEmail = "max" + System.currentTimeMillis() + "@gmail.com";
		Reflection.setPrivateField(ServerCalls.getInstance(), "emailForUiTests", testEmail);
		EditText usernameEditText = (EditText) solo.getView(R.id.username);
		EditText passwordEditText = (EditText) solo.getView(R.id.password);
		solo.enterText(usernameEditText, username);
		solo.enterText(passwordEditText, password);
		solo.clickOnButton(solo.getString(R.string.login_or_register));
		solo.sleep(5000);

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

	private void fillLoginDialog(boolean correct) {
		assertNotNull("Login Dialog is not shown.", solo.getText(solo.getString(R.string.login_register_dialog_title)));

		// enter a username
		String testUser = "testUser" + System.currentTimeMillis();
		solo.clearEditText(0);
		solo.enterText(0, testUser);
		// enter a password
		String testPassword;
		if (correct) {
			testPassword = "blubblub";
		} else {
			testPassword = "short";
		}
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, testPassword);

		// set the email to use. we need a random email because the server does not allow same email with different users 
		String testEmail = testUser + "@gmail.com";
		Reflection.setPrivateField(ServerCalls.getInstance(), "emailForUiTests", testEmail);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testPassword));
		solo.sleep(1000);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testUser));
		solo.setActivityOrientation(Solo.PORTRAIT);

		int buttonId = android.R.id.button1;
		solo.clickOnView(solo.getView(buttonId));
	}

	private void clearSharedPreferences() {
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation()
				.getTargetContext());
		Editor edit = defaultSharedPreferences.edit();
		edit.clear();
		edit.commit();
	}
}
