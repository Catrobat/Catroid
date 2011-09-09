/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.web;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilToken;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

import com.jayway.android.robotium.solo.Solo;

public class UserConceptTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String saveToken;

	public UserConceptTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		UiTestUtils.clearAllUtilTestProjects();
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Consts.TOKEN, "0");
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, saveToken).commit();
		UiTestUtils.setPrivateField("emailForUiTests", ServerCalls.getInstance(), null, false);
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testRegisterNewUser() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, null).commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(1000);

		fillLoginDialog(true);

		assertNotNull("Upload Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));
		solo.sleep(2000);
	}

	public void testRegisterWithValidTokenSaved() throws Throwable {
		setTestUrl();
		createValidUser();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(5000);

		assertNotNull("Upload Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));
		solo.sleep(2000);
	}

	public void testTokenPersistance() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, "").commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(1000);
		fillLoginDialog(true);

		assertNotNull("Upload Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));
		solo.goBack();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.waitForDialogToClose(10000);

		assertNotNull("Upload Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));
		solo.sleep(2000);
	}

	public void testRegisterWithWrongToken() throws Throwable {

		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, "wrong_token").commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(5000);
		fillLoginDialog(true);

		assertNotNull("Login Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));
		solo.sleep(2000);
	}

	public void testRegisterWithShortPassword() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, null).commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(1000);
		fillLoginDialog(false);

		assertNotNull("no error dialog is shown", solo.getText(getActivity().getString(R.string.register_error)));
		solo.clickOnButton(0);
		assertNotNull("Login Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.login_register_dialog_title)));
		solo.sleep(2000);
	}

	private void setTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	private void createValidUser() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = UtilToken.calculateToken(testUser, testPassword);
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token);

			assertTrue("no new account created", userRegistered);

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			prefs.edit().putString(Consts.TOKEN, token).commit();

		} catch (WebconnectionException e) {
			e.printStackTrace();
			assertFalse("exception during user creation, see logcat for details", true);
		}

	}

	private void fillLoginDialog(boolean correct) {

		assertNotNull("Login Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.login_register_dialog_title)));

		// enter a username
		String testUser = "testUser" + System.currentTimeMillis();
		solo.clearEditText(0);
		solo.clickOnEditText(0);
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
		UiTestUtils.setPrivateField("emailForUiTests", ServerCalls.getInstance(), testEmail, false);
		solo.clickOnButton(getActivity().getString(R.string.login_or_register));

		solo.waitForDialogToClose(10000);
	}
}
