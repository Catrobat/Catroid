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
import java.util.Calendar;
import java.util.Locale;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.web.ServerCalls;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.UiThreadTest;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

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

		fillRegistrationDialogs(true);

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
		solo.goBack();

		solo.waitForDialogToClose(10000);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testRegisterWithWrongToken() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, "wrong_token").commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);
		fillLoginDialog(true);
		solo.sleep(1000);

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
		fillRegistrationDialogs(false);

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
		fillLoginDialog(true);

		solo.waitForText(uploadDialogTitle);
		solo.goBack();
		solo.sleep(200);
		solo.goBack();
		String cancel = solo.getString(R.string.cancel_button);
		if (solo.searchText(cancel)) {
			solo.clickOnText(cancel);
		}

		clearSharedPreferences();

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForText(loginDialogTitle);

		username = username.toLowerCase(Locale.ENGLISH);
		fillLoginDialog(true);
		solo.waitForText(uploadDialogTitle);

		TextView uploadProject = (TextView) solo.getView(R.id.dialog_upload_size_of_project);
		ArrayList<View> currentViews = solo.getCurrentViews();
		assertTrue("Cannot login because username is upper or lower case", currentViews.contains(uploadProject));
	}

	public void testAlreadyRegistered() throws Throwable {
		setTestUrl();
		clearSharedPreferences();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.sleep(1000);

		assertTrue("Registration dialog not shown", solo.searchText(solo.getString(R.string.register_dialog_title)));
		solo.clickOnButton(solo.getString(R.string.already_registered_login));
		solo.sleep(300);
		assertTrue("Login dialog not shown", solo.searchText(solo.getString(R.string.login_dialog_title)));
	}

	public void testRegisterErrors() throws Throwable {
		setTestUrl();
		clearSharedPreferences();

		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.sleep(2000);

		RadioButton male = (RadioButton) solo.getView(R.id.gender_male);
		RadioButton female = (RadioButton) solo.getView(R.id.gender_female);
		assertTrue("Male radio button is not checked", male.isChecked());
		solo.clickOnRadioButton(1);
		solo.sleep(50);
		assertTrue("Female radio button is not checked", female.isChecked());
		assertFalse("Male radio button is still checked after selecting female", male.isChecked());
		solo.clickOnRadioButton(0);
		solo.sleep(50);
		assertTrue("Male radio button is not checked", male.isChecked());
		assertFalse("Female radio button is still checked after selecting male", female.isChecked());
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(500);

		Spinner countrySpinner = (Spinner) solo.getView(R.id.country);
		int selectedItemPosition = countrySpinner.getSelectedItemPosition();

		String[] countryList = getActivity().getResources().getStringArray(R.array.countries_array);
		String userCountry = UtilDeviceInfo.getUserCountryCode(getActivity());
		int position = 0;
		for (int stringArrayPosition = 0; stringArrayPosition <= countryList.length; stringArrayPosition++) {
			String currentItem = countryList[position];
			int countryPosition = currentItem.indexOf("/");
			String countryCode = currentItem.substring(0, countryPosition);
			if (countryCode.equals(userCountry.toLowerCase())) {
				break;
			}
			position++;
		}
		assertEquals("Wrong default value selected in country spinner", selectedItemPosition, position);

		solo.pressSpinnerItem(0, 3);
		solo.sleep(1000);
		int newSelectedItemPosition = countrySpinner.getSelectedItemPosition();
		assertEquals("Wrong value selected in country spinner", selectedItemPosition + 3, newSelectedItemPosition);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(300);

		Button nextButton = (Button) solo.getView(R.id.next_button);
		assertFalse("Next button is enabled!", nextButton.isEnabled());
		EditText city = (EditText) solo.getView(R.id.city);
		solo.enterText(city, "Graz");
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(300);

		Spinner monthSpinner = (Spinner) solo.getView(R.id.birthday_month);
		Spinner yearSpinner = (Spinner) solo.getView(R.id.birthday_year);
		String selectedMonth = monthSpinner.getSelectedItem().toString();
		String selectedYear = yearSpinner.getSelectedItem().toString();

		String monthJanuary = "";
		String monthFebruary = "";
		if (userCountry.toLowerCase().equals("de")) {
			monthJanuary = "Jänner";
			monthFebruary = "Februar";
		} else {
			monthJanuary = "January";
			monthFebruary = "February";
		}

		assertEquals("Month spinner initialized with wrong value", monthJanuary, selectedMonth);
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		assertEquals("Year spinner initialized with wrong value", currentYear - 10, Integer.parseInt(selectedYear));
		solo.pressSpinnerItem(0, 1);
		solo.sleep(500);
		solo.pressSpinnerItem(1, 2);
		solo.sleep(500);
		selectedMonth = monthSpinner.getSelectedItem().toString();
		selectedYear = yearSpinner.getSelectedItem().toString();

		assertEquals("Wrong value selected in month spinner", monthFebruary, selectedMonth);
		assertEquals("Wrong value selected in year spinner", currentYear - 10 + 2, Integer.parseInt(selectedYear));
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(300);

		String testUser = "testUser" + System.currentTimeMillis();
		EditText username = (EditText) solo.getView(R.id.username);
		EditText password = (EditText) solo.getView(R.id.password);
		EditText passwordConfirmation = (EditText) solo.getView(R.id.password_confirmation);
		solo.clearEditText(username);
		solo.enterText(username, testUser);
		String testPassword = "testpassword";
		solo.clearEditText(password);
		solo.clearEditText(passwordConfirmation);
		solo.enterText(password, testPassword);
		solo.enterText(passwordConfirmation, testPassword + "wrong");
		// set the email to use. we need a random email because the server does not allow same email with different users 
		String testEmail = testUser + "@gmail.com";
		Reflection.setPrivateField("emailForUiTests", testEmail, false);
		solo.clickOnButton(solo.getString(R.string.register));
		assertTrue("Wrong password confirmation was accepted",
				solo.waitForText(solo.getString(R.string.register_password_mismatch)));
		solo.clickOnButton(0);
		solo.clearEditText(passwordConfirmation);
		solo.enterText(passwordConfirmation, testPassword);
		//Check show password is checked and unchecked because solo automatically shows hidden password
		CheckBox showPassword = (CheckBox) solo.getView(R.id.show_password);
		solo.clickOnView(showPassword);
		//solo.clickOnCheckBox(R.string.show_password);
		solo.sleep(300);
		solo.clickOnView(showPassword);
		solo.sleep(300);
		assertTrue("Password should be hidden" + "inputtype:" + password.getInputType(),
				password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
		assertTrue(
				"Password confirmation should be hidden",
				passwordConfirmation.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
		solo.clickOnView(showPassword);
		assertTrue("Password should be visible", password.getInputType() == InputType.TYPE_CLASS_TEXT);
		assertTrue("Password confirmation should be visible",
				passwordConfirmation.getInputType() == InputType.TYPE_CLASS_TEXT);
		solo.clickOnButton(solo.getString(R.string.register));
		solo.sleep(1000);

		assertTrue("No registration completed text shown",
				solo.waitForText(solo.getString(R.string.registration_completed), 1, 30000));
		solo.clickOnButton(solo.getString(R.string.upload_button));
		solo.sleep(500);

		assertTrue("Upload dialog not displayed",
				solo.waitForText(solo.getString(R.string.upload_project_dialog_title)));
	}

	private void setTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	private void clearSharedPreferences() {
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation()
				.getTargetContext());
		Editor edit = defaultSharedPreferences.edit();
		edit.clear();
		edit.commit();
	}

	private void fillLoginDialog(boolean correctPassword) {
		String testUser = "testUser" + System.currentTimeMillis();
		EditText username = (EditText) solo.getView(R.id.username);
		EditText password = (EditText) solo.getView(R.id.password);
		solo.clearEditText(username);
		solo.enterText(username, testUser);
		String testPassword;
		if (correctPassword) {
			testPassword = "blubblub";
		} else {
			testPassword = "short";
		}
		solo.clearEditText(password);
		solo.enterText(password, testPassword);

		// set the email to use. we need a random email because the server does not allow same email with different users 
		String testEmail = testUser + "@gmail.com";
		Reflection.setPrivateField("emailForUiTests", testEmail, false);
		solo.sleep(1000);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testPassword));
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testUser));
		solo.clickOnButton(solo.getString(R.string.login));
		solo.sleep(500);
	}

	private void fillRegistrationDialogsUntilStepFive() {
		assertNotNull("Register Dialog is not shown.", solo.getText(solo.getString(R.string.register_dialog_title)));

		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(500);

		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(300);

		EditText city = (EditText) solo.getView(R.id.city);
		solo.enterText(city, "Graz");
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(500);

		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(300);
	}

	private void fillRegistrationDialogs(boolean correctPassword) {
		assertNotNull("Register Dialog is not shown.", solo.getText(solo.getString(R.string.register_dialog_title)));

		solo.sleep(300);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(500);

		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(300);

		EditText city = (EditText) solo.getView(R.id.city);
		solo.enterText(city, "Graz");
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(500);

		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.sleep(300);

		// enter a username
		String testUser = "testUser" + System.currentTimeMillis();
		EditText username = (EditText) solo.getView(R.id.username);
		EditText password = (EditText) solo.getView(R.id.password);
		EditText passwordConfirmation = (EditText) solo.getView(R.id.password_confirmation);
		solo.clearEditText(username);
		solo.enterText(username, testUser);
		// enter a password
		String testPassword;
		if (correctPassword) {
			testPassword = "blubblub";
		} else {
			testPassword = "short";
		}
		solo.clearEditText(password);
		solo.clearEditText(passwordConfirmation);
		//solo.clickOnEditText(password);
		solo.enterText(password, testPassword);
		solo.enterText(passwordConfirmation, testPassword);

		// set the email to use. we need a random email because the server does not allow same email with different users 
		String testEmail = testUser + "@gmail.com";
		Reflection.setPrivateField("emailForUiTests", testEmail, false);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testPassword));
		solo.sleep(1000);
		assertTrue("EditTextField got cleared after changing orientation", solo.searchText(testUser));
		//solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnButton(solo.getString(R.string.register));
		solo.sleep(500);
	}
}
