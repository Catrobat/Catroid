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
import at.tugraz.ist.catroid.web.ServerCalls;

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

		assertNotNull("Upload Dialog is not shown.",
				solo.getText(getActivity().getString(R.string.upload_project_dialog_title)));
		solo.sleep(2000);
	}

	public void testRegisterWithValidTokenSaved() throws Throwable {
		setTestUrl();
		UiTestUtils.createValidUser(getActivity());

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(5000);

		assertNotNull("Upload Dialog is not shown.",
				solo.getText(getActivity().getString(R.string.upload_project_dialog_title)));
		solo.sleep(2000);
	}

	public void testTokenPersistance() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, "").commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(1000);
		fillLoginDialog(true);

		assertNotNull("Upload Dialog is not shown.",
				solo.getText(getActivity().getString(R.string.upload_project_dialog_title)));
		solo.goBack();

		solo.waitForDialogToClose(10000);

		assertNotNull("Upload Dialog is not shown.",
				solo.getText(getActivity().getString(R.string.upload_project_dialog_title)));
		solo.sleep(2000);
	}

	public void testRegisterWithWrongToken() throws Throwable {

		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, "wrong_token").commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(4000);
		fillLoginDialog(true);

		assertNotNull("Login Dialog is not shown.",
				solo.getText(getActivity().getString(R.string.upload_project_dialog_title)));
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
		assertNotNull("Login Dialog is not shown.",
				solo.getText(getActivity().getString(R.string.login_register_dialog_title)));
		solo.sleep(2000);
	}

	public void testOrientationChange() throws Throwable {
		setTestUrl();
		String testText1 = "testText1";
		String testText2 = "testText2";

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, null).commit();
		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(500);
		solo.clearEditText(0);
		solo.enterText(0, testText1);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testText1));
		solo.clickOnEditText(1);
		solo.clearEditText(1);
		solo.enterText(1, testText2);
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testText1));
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testText2));
	}

	private void setTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	private void fillLoginDialog(boolean correct) {

		assertNotNull("Login Dialog is not shown.",
				solo.getText(getActivity().getString(R.string.login_register_dialog_title)));

		// enter a username
		String testUser = "testUser" + System.currentTimeMillis();
		solo.clearEditText(0);
		solo.enterText(0, testUser);
		solo.goBack();
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
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testPassword));
		solo.sleep(1000);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testUser));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.goBack();

		solo.clickOnButton(0);

		solo.waitForDialogToClose(10000);
	}
}
