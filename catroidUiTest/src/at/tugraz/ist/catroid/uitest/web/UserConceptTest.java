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
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private void setTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	public void testRegisterWithValidTokenSaved() throws Throwable {
		setTestUrl();
		createValidUser();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(2000);

		assertNotNull("Upload Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));
	}

	public void testRegisterWithNoTokenSaved() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, "").commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(1000);
		fillLoginDialog();

		assertNotNull("Upload Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));
	}

	public void testRegisterWithWrongToken() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Consts.TOKEN, "wrong_token").commit();

		solo.clickOnText(getActivity().getString(R.string.upload_project));
		solo.sleep(3000);
		fillLoginDialog();

		assertNotNull("Upload Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.upload_project_dialog_title)));

	}

	private void createValidUser() {
		boolean registrationOk = false;
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pws";
			String token = UtilToken.calculateToken(testUser, testPassword);
			registrationOk = ServerCalls.getInstance().registration(testUser, testPassword, "mail", "de", "at", token);

			if (registrationOk) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				prefs.edit().putString(Consts.TOKEN, token).commit();
			}

		} catch (WebconnectionException e) {
			e.printStackTrace();
		}
		assertTrue("registration failed", registrationOk);

	}

	private void fillLoginDialog() {

		assertNotNull("Login Dialog is not shown.", solo.getText(getActivity().getString(
				R.string.login_register_dialog_title)));

		// enter a username
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, "testUser" + System.currentTimeMillis());

		// enter a password
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, "blub");

		solo.clickOnButton(getActivity().getString(R.string.login_or_register));
		solo.sleep(3000);
	}

}
