/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.UiThreadTest;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginBehavior;
import com.robotium.solo.By;
import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.transfers.DeleteTestUserTask;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.web.ServerCalls;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

//Aborts on emulator
public class UserConceptTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> implements DeleteTestUserTask.OnDeleteTestUserCompleteListener {

	private String saveToken;
	private String signInDialogTitle;
	private String uploadDialogTitle;
	private String login;
	private String register;
	private String oauthUsername;
	private boolean testUserAccountsDeleted;
	private boolean configFileRead;
	private Map<String, String> configMap;

	public UserConceptTest() {
		super(MainMenuActivity.class);
	}

	@Override
	@UiThreadTest
	public void setUp() throws Exception {
		super.setUp();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = prefs.getString(Constants.TOKEN, Constants.NO_TOKEN);
		signInDialogTitle = solo.getString(R.string.sign_in_dialog_title);
		login = solo.getString(R.string.login);
		register = solo.getString(R.string.register);
		uploadDialogTitle = solo.getString(R.string.upload_project_dialog_title);
		oauthUsername = solo.getString(R.string.signin_choose_username);
		testUserAccountsDeleted = false;
		configFileRead = false;
		ServerCalls.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
		solo.waitForActivity(MainMenuActivity.class);
		UiTestUtils.createEmptyProject();
		setActivityInitialTouchMode(true);
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
		solo.waitForText(signInDialogTitle);
		solo.scrollDown();

		assertTrue("Licence text not present", solo.searchText(solo.getString(R.string.register_terms)));
		assertTrue("Licence link not present",
				solo.searchText(solo.getString(R.string.register_pocketcode_terms_of_use_text)));
	}

	@Device
	public void testRegisterNewUser() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		navigateToNativeRegistrationDialog();
		fillNativeRegistrationDialog(getTestUserName(), true);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testRegisterNewUserFromMenu() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		navigateToMenuNativeRegistrationDialog();
		fillNativeRegistrationDialog(getTestUserName(), true);

		assertTrue("Not registered!", solo.searchText(solo.getString(R.string
				.new_user_registered)));
		assertFalse("Upload Dialog is shown.", solo.searchText(solo.getString(R.string
				.upload_project_dialog_title)));
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
		solo.sleep(300);

		navigateToNativeRegistrationDialog();
		fillNativeRegistrationDialog(getTestUserName(), true);

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
		solo.sleep(300);

		navigateToNativeRegistrationDialog();
		fillNativeRegistrationDialog(getTestUserName(), true);

		assertNotNull("Upload Dialog is not shown.", uploadDialogTitle);
		UiTestUtils.goBackToHome(getInstrumentation());
	}

	@Device
	public void testRegisterWithShortPassword() throws Throwable {
		setTestUrl();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, null).commit();
		solo.sleep(300);

		navigateToNativeRegistrationDialog();
		fillNativeRegistrationDialog(getTestUserName(), false);

		assertNotNull("no error dialog is shown", solo.getText(solo.getString(R.string.register_error)));
		solo.clickOnButton(0);
		assertNotNull("Registration Dialog is not shown.", solo.getText(register));
	}

	@Device
	public void testRegisterUsernameDifferentCases() throws Throwable {
		setTestUrl();
		clearSharedPreferences();
		solo.sleep(300);

		navigateToNativeRegistrationDialog();

		String username = "UpperCaseUser" + System.currentTimeMillis();
		fillNativeRegistrationDialog(username, true);

		solo.waitForText(uploadDialogTitle);
		String cancel = solo.getString(R.string.cancel);
		if (solo.searchText(cancel)) {
			solo.clickOnText(cancel);
		}

		clearSharedPreferences();

		navigateToNativeLoginDialog();

		username = username.toLowerCase(Locale.ENGLISH);
		fillNativeLoginDialog(username, "topsecret");
		solo.waitForText(uploadDialogTitle);

		TextView uploadProject = (TextView) solo.getView(R.id.dialog_upload_size_of_project);
		ArrayList<View> currentViews = solo.getCurrentViews();
		assertTrue("Cannot login because username is upper or lower case", currentViews.contains(uploadProject));
	}

	@Device
	public void testRegisterErrors() throws Throwable {
		setTestUrl();
		clearSharedPreferences();
		String testUser = "testUser" + System.currentTimeMillis();
		String wrongTestUser = "TestÜser";
		String testPassword = "pwspws";
		String testEmail = testUser + "@gmail.com";
		String wrongTestEmail = testUser;

		solo.clickOnButton(solo.getString(R.string.main_menu_upload));
		solo.waitForDialogToOpen(500);
		solo.sendKey(Solo.ENTER);

		assertTrue("Signin dialog not shown",
				solo.searchText(signInDialogTitle));
		solo.clickOnButton(register);
		solo.waitForDialogToOpen();

		EditText email = (EditText) solo.getView(R.id.dialog_register_email);
		EditText username = (EditText) solo.getView(R.id.dialog_register_username);
		EditText password = (EditText) solo.getView(R.id.dialog_register_password);
		EditText passwordConfirmation = (EditText) solo
				.getView(R.id.dialog_register_password_confirm);

		assertTrue("The device E-Mail address is not proposed", email.getText().toString()
				.equals(UtilDeviceInfo.getUserEmail(getActivity())));

		solo.clickOnButton(register);
		solo.waitForDialogToOpen(500);
		assertTrue("No username blank error appeared", solo.searchText("Username must not be blank"));
		solo.clickOnButton(0);

		solo.clearEditText(username);
		solo.enterText(username, wrongTestUser);
		solo.sendKey(Solo.ENTER);

		solo.clickOnButton(register);
		solo.waitForDialogToOpen(500);
		assertTrue("No username not valid error appeared", solo.searchText("This value is not valid"));
		solo.clickOnButton(0);

		solo.clearEditText(username);
		solo.enterText(username, testUser);
		solo.sendKey(Solo.ENTER);
		solo.clearEditText(email);

		solo.clickOnButton(register);
		solo.waitForDialogToOpen(500);
		assertTrue("No email blank error appeared", solo.searchText("email must not be blank"));
		solo.clickOnButton(0);

		solo.clearEditText(email);
		solo.enterText(email, wrongTestEmail);
		solo.sendKey(Solo.ENTER);

		solo.clickOnButton(register);
		solo.waitForDialogToOpen(500);
		assertTrue("No invalid email error appeared", solo.searchText("Your email seems to be invalid"));
		solo.clickOnButton(0);

		solo.clearEditText(email);
		solo.enterText(email, testEmail);
		solo.sendKey(Solo.ENTER);

		solo.clickOnButton(register);
		solo.waitForDialogToOpen(500);
		assertTrue("No password missing error appeared", solo.searchText("The password is missing"));
		solo.clickOnButton(0);

		solo.clearEditText(password);
		solo.clearEditText(passwordConfirmation);
		solo.enterText(password, testPassword);
		solo.sendKey(Solo.ENTER);
		solo.enterText(passwordConfirmation, testPassword + "wrong");
		solo.sendKey(Solo.ENTER);
		solo.clickOnButton(register);

		solo.waitForDialogToOpen(500);
		assertTrue("No password do not match error appeared", solo.searchText(solo.getString(R.string.register_password_mismatch)));
		solo.clickOnButton(0);

		//Check show password is checked and unchecked because solo automatically shows hidden password
		CheckBox showPassword = (CheckBox) solo.getView(R.id.dialog_register_checkbox_showpassword);
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
		solo.sleep(300);
		assertTrue("Password should be visible", password.getInputType() == InputType
				.TYPE_CLASS_TEXT);
		assertTrue("Password confirmation should be visible",
				passwordConfirmation.getInputType() == InputType.TYPE_CLASS_TEXT);

		solo.clearEditText(passwordConfirmation);
		solo.enterText(passwordConfirmation, testPassword);
		solo.sendKey(Solo.ENTER);
		solo.clickOnButton(register);

		solo.waitForDialogToOpen(2000);
		assertTrue("No upload dialog shown",
				solo.waitForText(uploadDialogTitle));
	}

	@Device
	public void testLoginWithRegisteredUser() throws Throwable {
		setTestUrl();

		String testUser = "testUser" + System.currentTimeMillis();
		String testPassword = "pwspws";
		String testEmail = testUser + "@gmail.com";
		UiTestUtils.createValidUserWithCredentials(getActivity(), testUser, testPassword, testEmail);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		navigateToNativeLoginDialog();
		fillNativeLoginDialog(testUser, testPassword);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testLoginFromMenuWithRegisteredUser() throws Throwable {
		setTestUrl();

		String testUser = "testUser" + System.currentTimeMillis();
		String testPassword = "pwspws";
		String testEmail = testUser + "@gmail.com";
		UiTestUtils.createValidUserWithCredentials(getActivity(), testUser, testPassword, testEmail);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		navigateToMenuLoginDialog();
		fillNativeLoginDialog(testUser, testPassword);

		assertTrue("Not logged in!", solo.searchText(solo.getString(R.string
				.user_logged_in)));

		assertFalse("Upload Dialog is shown.", solo.searchText(solo.getString(R.string
				.upload_project_dialog_title)));
	}

	@Device
	public void testLoginErrors() throws Throwable {
		setTestUrl();

		String wrongTestUser = "TestÜser";
		String testUser = "testUser" + System.currentTimeMillis();
		String wrongTestPassword = "short";
		String testPassword = "pwspws";
		String testEmail = testUser + "@gmail.com";
		UiTestUtils.createValidUserWithCredentials(getActivity(), testUser, testPassword, testEmail);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		navigateToNativeLoginDialog();
		assertTrue("Signin dialog not shown",
				solo.searchText(login));

		EditText username = (EditText) solo.getView(R.id.dialog_login_username);
		EditText password = (EditText) solo.getView(R.id.dialog_login_password);
		CheckBox showPassword = (CheckBox) solo.getView(R.id.dialog_login_checkbox_showpassword);

		solo.clickOnButton(login);
		solo.waitForDialogToOpen(500);
		assertTrue("No username blank error appeared", solo.searchText("Username must not be blank"));
		solo.clickOnButton(0);
		solo.sendKey(Solo.ENTER);

		solo.clearEditText(username);
		solo.enterText(username, wrongTestUser);
		solo.sendKey(Solo.ENTER);

		solo.clickOnButton(login);
		solo.waitForDialogToOpen(500);
		assertTrue("No username not valid error appeared", solo.searchText("This value is not valid"));
		solo.clickOnButton(0);

		solo.clearEditText(username);
		solo.enterText(username, testUser);
		solo.sendKey(Solo.ENTER);

		solo.clickOnButton(login);
		solo.waitForDialogToOpen(500);
		assertTrue("No password missing error appeared", solo.searchText("The password is missing"));
		solo.clickOnButton(0);

		solo.clearEditText(password);
		solo.clickOnView(password);
		solo.enterText(password, wrongTestPassword);
		solo.sendKey(Solo.ENTER);

		solo.clickOnButton(login);
		solo.waitForDialogToOpen(500);
		assertTrue("No password too short error appeared", solo.searchText("Your password must have at least 6 "
				+ "characters"));
		solo.clickOnButton(0);

		solo.clearEditText(password);
		solo.clickOnView(password);
		solo.enterText(password, testPassword);
		solo.sendKey(Solo.ENTER);

		solo.clickOnView(showPassword);
		solo.sleep(300);
		assertTrue("Password should be hidden" + "input type:" + password.getInputType(),
				password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
		solo.clickOnView(showPassword);
		solo.sleep(300);
		assertTrue("Password should be visible", password.getInputType() == InputType
				.TYPE_CLASS_TEXT);

		solo.clickOnButton(login);

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}

	@Device
	public void testLoginWithNotExistingUser() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		navigateToNativeLoginDialog();
		fillNativeLoginDialog(getTestUserName(), "testpassword");

		assertTrue("No username does not exist error appeared", solo.searchText("This username does not exist"));
	}

	@Device
	public void testFacebookSignInWithNewUser() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(true);
		fillOAuthUsernameDialog(configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME), true);

		assertTrue("No upload dialog appeared!", solo.searchText(uploadDialogTitle, 1, true, true));
	}

	@Device
	public void testFacebookSignInWithoutMailPermission() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(false);
		fillOAuthUsernameDialog(configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME), true);

		assertTrue("No upload dialog appeared!", solo.searchText(uploadDialogTitle, 1, true, true));
	}

	@Device
	public void testFacebookMultipleSignIn() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(true);
		fillOAuthUsernameDialog(configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME), true);

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.sendKey(Solo.ENTER);
		solo.hideSoftKeyboard();
		solo.sleep(300);
		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		getActivity().startActivity(intent);
		solo.waitForActivity(MainMenuActivity.class);
		solo.sleep(300);

		clearSharedPreferences();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(3000);
		solo.clickOnWebElement(By.textContent("OK"));
		solo.waitForDialogToOpen();
		assertTrue("No upload dialog appeared!", solo.searchText(uploadDialogTitle, 1, true, true));
	}

	@Device
	public void testCancelAndReopenFacebookLoginDialog() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);
		solo.goBack();
		solo.sleep(500);
		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(true);
		fillOAuthUsernameDialog(configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME), true);

		assertTrue("No upload dialog appeared!", solo.searchText(uploadDialogTitle, 1, true, true));
	}

	@Device
	public void testGPlusSignInWithNewUser() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_gplus_login_button));
		solo.sleep(5000);

		fillOAuthUsernameDialog(configMap.get(UiTestUtils.CONFIG_GPLUS_NAME), true);

		assertTrue("No upload dialog appeared!", solo.searchText(uploadDialogTitle, 1, true, true));
	}

	@Device
	public void testGPlusMultipleSignIn() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_gplus_login_button));
		solo.sleep(5000);

		fillOAuthUsernameDialog(configMap.get(UiTestUtils.CONFIG_GPLUS_NAME), true);

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.sendKey(Solo.ENTER);
		solo.hideSoftKeyboard();
		solo.sleep(300);
		Intent intent = new Intent(getActivity(), MainMenuActivity.class);
		getActivity().startActivity(intent);
		solo.waitForActivity(MainMenuActivity.class);
		solo.sleep(300);

		clearSharedPreferences();

		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_gplus_login_button));
		solo.waitForDialogToOpen();
		assertTrue("No upload dialog appeared!", solo.searchText(uploadDialogTitle, 1, true, true));
	}

	private void fillFacebookLoginDialogs(boolean eMailPermission) {
		solo.waitForWebElement(By.name("login"));

		if (solo.searchText("Log out")) {
			solo.clickOnButton(0);
			solo.waitForDialogToClose();
			solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
			solo.waitForWebElement(By.name("login"));
		}

		solo.clearTextInWebElement(By.name("email"));
		solo.clearTextInWebElement(By.name("pass"));
		solo.enterTextInWebElement(By.name("email"), configMap.get(UiTestUtils.CONFIG_FACEBOOK_MAIL));
		solo.enterTextInWebElement(By.name("pass"), configMap.get(UiTestUtils.CONFIG_FACEBOOK_PASSWORD));
		solo.clickOnWebElement(By.name("login"));
		solo.sleep(3000);
		if (!eMailPermission) {
			solo.clickOnWebElement(By.id("u_0_9")); //Edit the info you provide
			solo.sleep(100);
			solo.clickOnWebElement(By.textContent("Email address")); //unset email permission
		}

		solo.clickOnWebElement(By.textContent("OK"));
	}

	private void setTestUrl() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				ServerCalls.useTestUrl = true;
			}
		});
	}

	private void fillNativeRegistrationDialog(String username, boolean correctPassword) {
		assertNotNull("Registration Dialog is not shown.", solo.getText(register));
		solo.sendKey(Solo.ENTER);
		// enter a username
		String testUser = username;
		EditText userNameEditText = (EditText) solo.getView(R.id.dialog_register_username);
		solo.clearEditText(userNameEditText);
		solo.enterText(userNameEditText, testUser);
		solo.sendKey(Solo.ENTER);

		// set the email to use. we need a random email because the server does not allow same email with different users
		String testEmail = testUser + "@gmail.com";
		solo.sendKey(Solo.ENTER);
		EditText eMailEditText = (EditText) solo.getView(R.id.dialog_register_email);
		String deviceEmail = UtilDeviceInfo.getUserEmail(getActivity());
		if (deviceEmail != null) {
			//Test device should have an account..
			assertTrue("The device E-Mail address" + deviceEmail + "is not proposed",
					eMailEditText.getText().toString().equals(UtilDeviceInfo.getUserEmail(getActivity())));
		}
		solo.clearEditText(eMailEditText);
		solo.clickOnView(eMailEditText);
		solo.enterText(eMailEditText, testEmail);
		solo.sendKey(Solo.ENTER);

		// enter a password
		String testPassword;
		if (correctPassword) {
			testPassword = "topsecret";
		} else {
			testPassword = "short";
		}
		EditText passwordEditText = (EditText) solo.getView(R.id.dialog_register_password);
		EditText passwordConfirmEditText = (EditText) solo.getView(R.id.dialog_register_password_confirm);
		solo.clearEditText(passwordEditText);
		solo.clickOnView(passwordEditText);
		solo.enterText(passwordEditText, testPassword);
		solo.sendKey(Solo.ENTER);
		solo.clearEditText(passwordConfirmEditText);
		solo.clickOnView(passwordConfirmEditText);
		solo.enterText(passwordConfirmEditText, testPassword);
		solo.sendKey(Solo.ENTER);

		int buttonId = android.R.id.button1;
		solo.clickOnView(solo.getView(buttonId));
	}

	private void fillNativeLoginDialog(String username, String password) {
		assertNotNull("Login Dialog is not shown.", solo.getText(login));
		solo.sendKey(Solo.ENTER);
		// enter a username
		EditText userNameEditText = (EditText) solo.getView(R.id.dialog_login_username);
		solo.clearEditText(userNameEditText);
		solo.enterText(userNameEditText, username);
		solo.sendKey(Solo.ENTER);

		// enter a password
		EditText passwordEditText = (EditText) solo.getView(R.id.dialog_login_password);
		solo.clearEditText(passwordEditText);
		solo.clickOnView(passwordEditText);
		solo.enterText(passwordEditText, password);
		solo.sendKey(Solo.ENTER);
		solo.clickOnButton(login);
	}

	private void fillOAuthUsernameDialog(String username, boolean proceed) {
		solo.waitForText(oauthUsername);
		assertNotNull("Choose OAuth username dialog is not shown.", solo.getText(oauthUsername));
		EditText userNameEditText = (EditText) solo.getView(R.id.dialog_signin_oauth_username);
		solo.clearEditText(userNameEditText);
		solo.enterText(userNameEditText, username);
		solo.sendKey(Solo.ENTER);
		if (proceed) {
			solo.clickOnButton(solo.getString(R.string.ok));
			solo.waitForDialogToClose();
		}
	}

	private void navigateToNativeRegistrationDialog() {
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);
		solo.clickOnButton(register);
		solo.waitForText(solo.getString(R.string.accountName));
	}

	private void navigateToMenuNativeRegistrationDialog() {
		solo.sendKey(solo.MENU);
		solo.clickOnMenuItem(solo.getString(R.string.main_menu_login));
		solo.waitForText(signInDialogTitle);
		solo.clickOnButton(register);
		solo.waitForText(solo.getString(R.string.accountName));
	}

	private void navigateToNativeLoginDialog() {
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);
		solo.clickOnButton(login);
		solo.waitForDialogToOpen();
		solo.sendKey(Solo.ENTER);
		solo.waitForText(solo.getString(R.string.username));
	}

	private void navigateToMenuLoginDialog() {
		solo.sendKey(solo.MENU);
		solo.clickOnMenuItem(solo.getString(R.string.main_menu_login));
		solo.waitForText(signInDialogTitle);
		solo.clickOnButton(login);
		solo.waitForDialogToOpen();
		solo.sendKey(Solo.ENTER);
		solo.waitForText(solo.getString(R.string.username));
	}

	private String getTestUserName() {
		return "testUser" + System.currentTimeMillis();
	}

	private void clearSharedPreferences() {
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation()
				.getTargetContext());
		Editor edit = defaultSharedPreferences.edit();
		edit.clear();
		edit.commit();
	}

	private void waitFacebookOrGoogleTestReady() {

		readConfigFile();
		deleteTestUserAccountsOnServer();
		clearSharedPreferences();
		AccessToken.setCurrentAccessToken(null);

		while (!configFileRead || !testUserAccountsDeleted) {
			solo.sleep(200);
		}
	}

	private void readConfigFile() {
		configMap = UiTestUtils.readConfigFile(getInstrumentation().getContext());
		configFileRead = true;
	}

	private void deleteTestUserAccountsOnServer() {
		DeleteTestUserTask deleteTestUserTask = new DeleteTestUserTask(getActivity());
		deleteTestUserTask.setOnDeleteTestUserCompleteListener(this);
		deleteTestUserTask.execute();
	}

	@Override
	public void onDeleteTestUserComplete(Boolean deleted) {
		assertTrue("Test user accounts could not be deleted on server!", deleted);
		testUserAccountsDeleted = true;
	}
}
