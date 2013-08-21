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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UserConceptTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final String TEST_USERNAME = "testUser";
	private String saveToken;
	private String saveEmail;
	private String loginDialogTitle;
	private String registerDialogTitle;
	private String uploadDialogTitle;

	public UserConceptTest() {
		super(MainMenuActivity.class);
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		super.setUp();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		saveEmail = sharedPreferences.getString(Constants.EMAIL, Constants.NO_EMAIL);
		loginDialogTitle = solo.getString(R.string.login_dialog_title);
		registerDialogTitle = solo.getString(R.string.register_dialog_title);
		uploadDialogTitle = solo.getString(R.string.upload_project_dialog_title);
		solo.waitForActivity(MainMenuActivity.class);
	}

	@Override
	public void tearDown() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.TOKEN, saveToken).commit();
		sharedPreferences.edit().putString(Constants.EMAIL, saveEmail).commit();
		Reflection.setPrivateField(ServerCalls.getInstance(), "emailForUiTests", null);
		super.tearDown();
	}

	@Device
	public void testLicenceLinkPresent() throws Throwable {
		setTestUrl();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.TOKEN, null).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		fillRegistrationDialogUntilStepFive();

		assertTrue("Licence text not present", solo.searchText(solo.getString(R.string.register_terms)));
		assertTrue("Licence link not present",
				solo.searchText(solo.getString(R.string.register_pocketcode_terms_of_use_text)));
	}

	@Device
	public void testRegisterNewUser() throws Throwable {
		setTestUrl();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(registerDialogTitle);

		fillRegistrationDialog(true, TEST_USERNAME, true);

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

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.TOKEN, "").commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(registerDialogTitle);
		fillRegistrationDialog(true, TEST_USERNAME, true);

		solo.waitForText(uploadDialogTitle);
		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.cancel_button));

		solo.waitForDialogToClose(1000);
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForDialogToOpen(1000);
		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testRegisterWithWrongToken() throws Throwable {
		setTestUrl();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.TOKEN, "wrong_token").commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));

		assertTrue("Registration Dialog not shown - wrong token was accepted", solo.waitForText(registerDialogTitle));
		UiTestUtils.goBackToHome(getInstrumentation());
	}

	@Device
	public void testRegisterWithShortPassword() throws Throwable {
		setTestUrl();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.TOKEN, null).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(registerDialogTitle);
		fillRegistrationDialog(false, TEST_USERNAME, false);

		assertNotNull("no error dialog is shown", solo.getText(solo.getString(R.string.register_error)));
		solo.clickOnButton(0);
		assertNotNull("Registration Dialog is not shown.", solo.getText(solo.getString(R.string.register_dialog_title)));
	}

	@Device
	public void testRegisterUsernameDifferentCases() throws Throwable {
		setTestUrl();
		clearSharedPreferences();

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForText(registerDialogTitle);

		String username = "UpperCaseUser" + System.currentTimeMillis();
		fillRegistrationDialog(true, TEST_USERNAME, true);

		solo.waitForText(uploadDialogTitle);
		solo.goBack();
		String cancel = solo.getString(R.string.cancel_button);
		if (solo.searchText(cancel)) {
			solo.clickOnText(cancel);
		}

		clearSharedPreferences();

		username = username.toLowerCase(Locale.ENGLISH);
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(registerDialogTitle);
		fillRegistrationDialog(true, username, true);
		solo.waitForText(uploadDialogTitle);

		TextView uploadProject = (TextView) solo.getView(R.id.dialog_upload_size_of_project);
		ArrayList<View> currentViews = solo.getCurrentViews();
		assertTrue("Cannot login because username is upper or lower case", currentViews.contains(uploadProject));
	}

	@Device
	public void testAlreadyRegistered() throws Throwable {
		setTestUrl();
		clearSharedPreferences();
		String testUser = "testUser" + System.currentTimeMillis();
		String testPassword = "pwspws";
		String testEmail = testUser + "@gmail.com";

		registerCorrectUser(testUser, testPassword, testEmail);
		clearSharedPreferences();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForDialogToOpen(500);

		assertTrue("Already registered dialog not shown",
				solo.searchText(registerDialogTitle) && solo.searchText(loginDialogTitle));

		solo.clickOnButton(solo.getString(R.string.login));
		solo.waitForDialogToOpen(500);

		fillLoginDialog(testUser, testPassword, testEmail, true);
		assertTrue("Upload dialog not displayed",
				solo.waitForText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testRegisterWithGivenMail() throws Throwable {
		setTestUrl();
		clearSharedPreferences();
		String testUser = "testUser" + System.currentTimeMillis();
		String testPassword = "pwspws";
		String testEmail = testUser + "@gmail.com";

		registerCorrectUser(testUser, testPassword, testEmail);
		clearSharedPreferences();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForDialogToOpen(500);

		assertTrue("Already registered dialog not shown",
				solo.searchText(registerDialogTitle) && solo.searchText(loginDialogTitle));

		solo.clickOnButton(solo.getString(R.string.register));
		solo.waitForDialogToOpen(500);

		solo.waitForDialogToOpen(500);
		solo.clickOnRadioButton(0);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.goBack();
		solo.enterText(0, testEmail);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		assertTrue("E-Mail already exists error dialog not shown", solo.searchText("E-Mail already registered"));

		solo.clickOnButton(0);
		solo.waitForDialogToOpen(500);
		solo.clickOnEditText(0);
		solo.goBack();
		solo.clearEditText(0);
		testEmail = "invalid";
		solo.enterText(0, testEmail);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		assertTrue("E-Mail already exists error dialog not shown", solo.searchText("E-Mail is invalid"));
	}

	@Device
	public void testBackDataPersistance() throws Throwable {
		setTestUrl();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));

		solo.waitForDialogToOpen(500);
		assertTrue("Already registered dialog not shown",
				solo.searchText(registerDialogTitle) && solo.searchText(loginDialogTitle));
		solo.clickOnButton(solo.getString(R.string.register));

		String otherGender = "male or female?";
		solo.waitForDialogToOpen(500);
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, otherGender);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		Spinner countrySpinner = (Spinner) solo.getView(R.id.dialog_register_country_spinner_country);
		solo.pressSpinnerItem(0, 43);
		String selectedCountry = countrySpinner.getSelectedItem().toString();
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.goBack();
		String email = System.currentTimeMillis() + "@gmail.com";
		solo.enterText(0, email);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		Spinner monthSpinner = (Spinner) solo.getView(R.id.dialog_register_birthday_spinner_month);
		Spinner yearSpinner = (Spinner) solo.getView(R.id.dialog_register_birthday_spinner_year);
		solo.pressSpinnerItem(0, 2);
		solo.pressSpinnerItem(1, 2);
		String selectedMonth = monthSpinner.getSelectedItem().toString();
		String selectedYear = yearSpinner.getSelectedItem().toString();
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.waitForDialogToOpen(500);
		solo.clickOnButton(solo.getString(R.string.previous_registration_step));

		solo.waitForDialogToOpen(500);
		monthSpinner = (Spinner) solo.getView(R.id.dialog_register_birthday_spinner_month);
		yearSpinner = (Spinner) solo.getView(R.id.dialog_register_birthday_spinner_year);
		assertEquals("Wrong month selected", selectedMonth, monthSpinner.getSelectedItem().toString());
		assertEquals("Wrong year selected", selectedYear, yearSpinner.getSelectedItem().toString());
		solo.clickOnButton(solo.getString(R.string.previous_registration_step));

		solo.waitForDialogToOpen(500);
		assertTrue("Wrong email shown", solo.searchText(email));
		solo.clickOnButton(solo.getString(R.string.previous_registration_step));

		solo.waitForDialogToOpen(500);
		assertTrue("Wrong country selected", solo.searchText(selectedCountry));
		solo.clickOnButton(solo.getString(R.string.previous_registration_step));

		solo.waitForDialogToOpen(500);
		assertTrue("Gender not shown", solo.searchText(otherGender));
		RadioButton other = (RadioButton) solo.getView(R.id.dialog_register_gender_radiobutton_other);
		assertTrue("Gender not selected", other.isChecked());
	}

	@Device
	public void testRegisterErrors() throws Throwable {
		setTestUrl();
		clearSharedPreferences();

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForDialogToOpen(500);

		assertTrue("Already registered dialog not shown",
				solo.searchText(registerDialogTitle) && solo.searchText(loginDialogTitle));

		solo.clickOnButton(solo.getString(R.string.register));

		solo.waitForDialogToOpen(500);
		RadioButton male = (RadioButton) solo.getView(R.id.dialog_register_gender_radiobutton_male);
		RadioButton female = (RadioButton) solo.getView(R.id.dialog_register_gender_radiobutton_female);
		RadioButton other = (RadioButton) solo.getView(R.id.dialog_register_gender_radiobutton_other);
		assertFalse("There is a checked radio button in the beginning",
				male.isChecked() || female.isChecked() || other.isChecked());
		solo.clickOnRadioButton(1);
		solo.sleep(50);
		assertTrue("Female radio button is not checked", female.isChecked());
		assertFalse("Male radio button is still checked after selecting female", male.isChecked());
		assertFalse("Other gender radio button is checked after selecting female", other.isChecked());
		solo.clickOnEditText(0);
		solo.sleep(50);
		assertTrue("Other gender radio button is not checked", other.isChecked());
		assertFalse("Male radio button is still checked after selecting male", male.isChecked());
		assertFalse("Female radio button is still checked after selecting male", female.isChecked());
		solo.clickOnRadioButton(0);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		Spinner countrySpinner = (Spinner) solo.getView(R.id.dialog_register_country_spinner_country);
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

		solo.waitForDialogToOpen(500);
		Button nextButton = (Button) solo.getView(android.R.id.button1);
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.goBack();
		assertFalse("Next button is enabled!", nextButton.isEnabled());
		EditText email = (EditText) solo.getView(R.id.dialog_register_edittext_email);
		solo.enterText(email, TEST_USERNAME + System.currentTimeMillis() + "@gmail.com");
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		Spinner monthSpinner = (Spinner) solo.getView(R.id.dialog_register_birthday_spinner_month);
		Spinner yearSpinner = (Spinner) solo.getView(R.id.dialog_register_birthday_spinner_year);
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

		solo.waitForDialogToOpen(500);
		EditText username = (EditText) solo.getView(R.id.dialog_register_edittext_username);
		EditText password = (EditText) solo.getView(R.id.dialog_register_edittext_password);
		EditText passwordConfirmation = (EditText) solo
				.getView(R.id.dialog_register_username_password_edittext_password_confirmation);
		solo.clearEditText(username);
		solo.enterText(username, TEST_USERNAME + System.currentTimeMillis());
		String testPassword = "testpassword";
		solo.clearEditText(password);
		solo.clearEditText(passwordConfirmation);
		solo.enterText(password, testPassword);
		solo.enterText(passwordConfirmation, testPassword + "wrong");
		solo.clickOnText(solo.getString(R.string.register));
		assertTrue("Password wrong dialog not shown",
				solo.searchText(solo.getString(R.string.register_password_mismatch)));
		solo.clickOnButton(0);

		solo.waitForDialogToOpen(500);
		username = (EditText) solo.getView(R.id.dialog_register_edittext_username);
		password = (EditText) solo.getView(R.id.dialog_register_edittext_password);
		passwordConfirmation = (EditText) solo
				.getView(R.id.dialog_register_username_password_edittext_password_confirmation);
		solo.clearEditText(username);
		solo.enterText(username, TEST_USERNAME + System.currentTimeMillis());
		solo.clearEditText(password);
		solo.enterText(password, testPassword);
		solo.clearEditText(passwordConfirmation);
		solo.enterText(passwordConfirmation, testPassword);
		//Check show password is checked and unchecked because solo automatically shows hidden password
		CheckBox showPassword = (CheckBox) solo.getView(R.id.dialog_register_username_password_checkbox_showpassword);
		solo.clickOnView(showPassword);
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

		solo.waitForDialogToOpen(2000);
		assertTrue("No registration completed text shown",
				solo.waitForText(solo.getString(R.string.registration_completed)));
		solo.clickOnButton(solo.getString(R.string.upload_button));

		solo.waitForDialogToOpen(500);
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

	private void fillLoginDialog(String username, String password, String email, boolean correctPassword) {
		EditText usernameEdittext = (EditText) solo.getView(R.id.dialog_register_edittext_username);
		EditText passwordEdittext = (EditText) solo.getView(R.id.dialog_register_edittext_password);
		EditText emailEdittext = (EditText) solo.getView(R.id.dialog_register_edittext_email);
		solo.clearEditText(usernameEdittext);
		solo.enterText(usernameEdittext, username);
		if (!correctPassword) {
			password = "short";
		}
		solo.clearEditText(passwordEdittext);
		solo.enterText(passwordEdittext, password);

		solo.clearEditText(emailEdittext);
		solo.enterText(emailEdittext, email);
		solo.clickOnButton(solo.getString(R.string.login));
		solo.waitForDialogToOpen(1000);
	}

	private void fillRegistrationDialogUntilStepFive() {
		solo.waitForDialogToOpen(500);
		assertNotNull("Register Dialog is not shown.", solo.getText(solo.getString(R.string.register_dialog_title)));
		solo.clickOnButton(registerDialogTitle);

		solo.waitForDialogToOpen(500);
		solo.clickOnRadioButton(0);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.goBack();
		solo.enterText(0, System.currentTimeMillis() + "@gmail.com");
		solo.clickOnButton(solo.getString(R.string.next_registration_step));

		solo.waitForDialogToOpen(500);
		solo.clickOnButton(solo.getString(R.string.next_registration_step));
		solo.waitForDialogToOpen(500);
	}

	private void fillRegistrationDialog(boolean correctPassword, String username, boolean completeStepSix) {
		fillRegistrationDialogUntilStepFive();

		// enter a username
		EditText usernameEditText = (EditText) solo.getView(R.id.dialog_register_edittext_username);
		EditText password = (EditText) solo.getView(R.id.dialog_register_edittext_password);
		EditText passwordConfirmation = (EditText) solo
				.getView(R.id.dialog_register_username_password_edittext_password_confirmation);

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.goBack();
		solo.enterText(usernameEditText, username);
		// enter a password
		String testPassword;
		if (correctPassword) {
			testPassword = "blubblub";
		} else {
			testPassword = "short";
		}
		solo.clickOnEditText(1);
		solo.clearEditText(1);
		solo.goBack();
		solo.enterText(password, testPassword);

		solo.clickOnEditText(2);
		solo.clearEditText(2);
		solo.goBack();
		solo.enterText(passwordConfirmation, testPassword);

		solo.clickOnButton(solo.getString(R.string.register));
		solo.waitForDialogToOpen(5000);

		if (completeStepSix) {
			solo.clickOnButton(solo.getString(R.string.upload_button));
			solo.waitForDialogToOpen(500);
		}
	}

	private void registerCorrectUser(String testUser, String testPassword, String testEmail) {
		String token = Constants.NO_TOKEN;
		try {
			ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail, "de", "at", token,
					"male", "January", "2000", getActivity());
		} catch (WebconnectionException e) {
			e.printStackTrace();
		}
	}
}
