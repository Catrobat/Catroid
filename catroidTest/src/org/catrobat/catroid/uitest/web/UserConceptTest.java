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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.UiThreadTest;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginBehavior;
import com.facebook.login.widget.LoginButton;
import com.robotium.solo.By;
import com.robotium.solo.Solo;
import com.robotium.solo.WebElement;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.transfers.DeleteTestUserTask;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.web.FacebookCalls;
import org.catrobat.catroid.web.ServerCalls;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

//Aborts on emulator
public class UserConceptTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> implements DeleteTestUserTask.OnDeleteTestUserCompleteListener {

	private String FACEBOOK_TESTUSER_NAME;
	private String GPLUS_TESTUSER_NAME;
	private String FACEBOOK_TESTUSER_MAIL;
	private String GPLUS_TESTUSER_MAIL;
	private String FACEBOOK_TESTUSER_PW;
	private String GPLUS_TESTUSER_PW;

	private String saveToken;
	private String signInDialogTitle;
	private String uploadDialogTitle;
	private String login;
	private String register;
	private String oauthUsername;
	private boolean testUserAccountsDeleted;
	private boolean configFileRead;
	private boolean facebookPermissionsRevoked;

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
		facebookPermissionsRevoked = false;
		FacebookCalls.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
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
		String cancel = solo.getString(R.string.cancel_button);
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
	public void testErrorsRegistration() throws Throwable {
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
		solo.waitForDialogToOpen(500);

		EditText email = (EditText) solo.getView(R.id.dialog_register_email);
		EditText username = (EditText) solo.getView(R.id.dialog_register_username);
		EditText password = (EditText) solo.getView(R.id.dialog_register_password);
		EditText passwordConfirmation = (EditText) solo
				.getView(R.id.dialog_register_password_confirm);

		assertTrue("The device E-Mail address is not proposed", email.getText().toString().equals
				(UtilDeviceInfo.getUserEmail(getActivity())));

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

	/*
	@Device
	public void testLoginErrors() throws Throwable {
		setTestUrl();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();

		navigateToNativeRegistrationDialog();
		fillNativeRegistrationDialog(getTestUserName(), true);
		//TODO: noch keine sinnvollen Fehlermeldungen

		CheckBox showPassword = (CheckBox) solo.getView(R.id.dialog_login_checkbox_showpassword);
		solo.clickOnView(showPassword);
		solo.sleep(300);
		solo.clickOnView(showPassword);
		solo.sleep(300);
		assertTrue("Password should be hidden" + "inputtype:" + password.getInputType(),
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

		//TODO: Fehlerhandling --> JSON error

		assertNotNull("Upload Dialog is not shown.", solo.getText(solo.getString(R.string.upload_project_dialog_title)));
	}
	*/

	@Device
	public void testFacebookSignInWithNewUser() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(true);
		fillOAuthUsernameDialog(FACEBOOK_TESTUSER_NAME);

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.clickOnButton(solo.getString(R.string.upload_button));
		assertTrue("Upload was not successful!", solo.waitForText("Project was successfully uploaded", 0, 10000));
	}

	@Device
	public void testFacebookSignInWithoutMailPermission() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(false);
		fillOAuthUsernameDialog(FACEBOOK_TESTUSER_NAME);

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.clickOnButton(solo.getString(R.string.upload_button));
		assertTrue("Upload was not successful!", solo.waitForText("Project was successfully uploaded", 0, 10000));
	}

	@Device
	public void testFacebookMultipleSignIn() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(true);
		fillOAuthUsernameDialog(FACEBOOK_TESTUSER_NAME);

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.clickOnButton(solo.getString(R.string.cancel_button));
		solo.sendKey(Solo.ENTER);
		solo.waitForDialogToClose();
		clearSharedPreferences();

		solo.hideSoftKeyboard();
		solo.scrollDown();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.clickOnButton(solo.getString(R.string.upload_button));
		assertTrue("Upload was not successful!", solo.waitForText("Project was successfully uploaded", 0, 10000));
	}

	@Device
	public void testCancelAndReopenFacebookLoginDialog() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);
		solo.clickOnWebElement(By.name("Cancel"));
		solo.waitForDialogToClose();
		solo.sleep(300);
		solo.clickOnView(solo.getView(R.id.dialog_sign_in_facebook_login_button));
		solo.sleep(5000);

		fillFacebookLoginDialogs(true);
		fillOAuthUsernameDialog(FACEBOOK_TESTUSER_NAME);

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.clickOnButton(solo.getString(R.string.upload_button));
		assertTrue("Upload was not successful!", solo.waitForText("Project was successfully uploaded", 0, 10000));
	}

	@Device
	public void testGPlusSignInWithNewUser() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_gplus_login_button));
		solo.sleep(5000);

		fillOAuthUsernameDialog(GPLUS_TESTUSER_NAME);
		solo.sleep(5000);
		ArrayList<View> views = solo.getCurrentViews();
		ArrayList<WebElement> elements = solo.getCurrentWebElements();

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 50000));
		solo.clickOnButton(solo.getString(R.string.upload_button));
		assertTrue("Upload was not successful!", solo.waitForText("Project was successfully uploaded", 0, 10000));
	}

	@Device
	public void testGPlusMultipleSignIn() throws Throwable {
		waitFacebookOrGoogleTestReady();
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_gplus_login_button));
		solo.sleep(5000);

		fillOAuthUsernameDialog(GPLUS_TESTUSER_NAME);

		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.clickOnButton(solo.getString(R.string.cancel_button));
		solo.sendKey(Solo.ENTER);
		solo.waitForDialogToClose();
		clearSharedPreferences();

		UiTestUtils.goBackToHome(getInstrumentation());
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);

		solo.clickOnView(solo.getView(R.id.dialog_sign_in_gplus_login_button));
		assertTrue("No upload dialog appeared!", solo.waitForText(uploadDialogTitle, 0, 10000));
		solo.clickOnButton(solo.getString(R.string.upload_button));
		assertTrue("Upload was not successful!", solo.waitForText("Project was successfully uploaded", 0, 10000));
	}

	private void fillFacebookLoginDialogs(boolean eMailPermission) {
		solo.waitForWebElement(By.name("login"));

		if(solo.searchText("Log out")) {
			solo.clickOnButton(0);
			solo.waitForDialogToClose();
			solo.clickOnView(solo.getView(R.id.dialog_sign_in_gplus_login_button));
			solo.waitForWebElement(By.name("login"));
		}

		solo.clearTextInWebElement(By.name("email"));
		solo.clearTextInWebElement(By.name("pass"));
		solo.enterTextInWebElement(By.name("email"), FACEBOOK_TESTUSER_MAIL);
		solo.enterTextInWebElement(By.name("pass"), FACEBOOK_TESTUSER_PW);
		solo.clickOnWebElement(By.name("login"));
		solo.sleep(3000);
		ArrayList<WebElement> elements = solo.getCurrentWebElements();
		if(!eMailPermission) {
			solo.clickOnWebElement(By.id("u_0_6")); //edit info you provide
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
		assertTrue("The device E-Mail address is not proposed", eMailEditText.getText().toString().equals
				(UtilDeviceInfo.getUserEmail(getActivity())));
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
		//int buttonId = android.R.id.button1;
		//solo.clickOnView(solo.getView(buttonId));
	}

	private void fillOAuthUsernameDialog(String username) {
		solo.waitForText(oauthUsername);
		assertNotNull("Choose OAuth username dialog is not shown.", solo.getText(oauthUsername));
		EditText userNameEditText = (EditText) solo.getView(R.id.dialog_signin_oauth_username);
		solo.clearEditText(userNameEditText);
		solo.enterText(userNameEditText, username);
		solo.sendKey(Solo.ENTER);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForDialogToClose();
	}

	private void navigateToNativeRegistrationDialog() {
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);
		solo.clickOnButton(register);
		solo.waitForText(solo.getString(R.string.accountName));
	}

	private void navigateToNativeLoginDialog() {
		solo.clickOnText(solo.getString(R.string.main_menu_upload));
		solo.waitForText(signInDialogTitle);
		solo.clickOnButton(login);
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
		deleteTestUserAccountsOnServer();
		clearSharedPreferences();
		revokeFacebookPermissions();
		readConfigFile();
		while (!testUserAccountsDeleted || !configFileRead || !facebookPermissionsRevoked) {
			solo.sleep(200);
		}
	}

	private void revokeFacebookPermissions() {
		if (AccessToken.getCurrentAccessToken() == null) {
			facebookPermissionsRevoked = true;
			return;
		}

		new GraphRequest(
				AccessToken.getCurrentAccessToken(),
				"/me/permissions",
				null,
				HttpMethod.DELETE,
				new GraphRequest.Callback() {
					public void onCompleted(GraphResponse response) {
						Log.d("response", response.toString());
						facebookPermissionsRevoked = true;
					}
				}
		).executeAndWait();
	}

	private void readConfigFile() {
		try {

			String config_fb_name = "facebook_testuser_name";
			String config_fb_mail = "facebook_testuser_mail";
			String config_fb_pw = "facebook_testuser_password";
			String config_gplus_name = "gplus_testuser_name";
			String config_gplus_mail = "gplus_testuser_mail";
			String config_gplus_pw = "gplus_testuser_password";

			InputStream stream = getActivity().getAssets().open("oauth_config.xml");
			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			String text = new String(buffer);
			FACEBOOK_TESTUSER_NAME = text.substring(text.indexOf(config_fb_name) + config_fb_name.length() + 1, text.indexOf
					("/" + config_fb_name) - 1);
			FACEBOOK_TESTUSER_MAIL = text.substring(text.indexOf(config_fb_mail) + config_fb_mail.length() + 1, text
					.indexOf("/" + config_fb_mail) - 1);
			FACEBOOK_TESTUSER_PW = text.substring(text.indexOf(config_fb_pw) + config_fb_pw.length() + 1, text.indexOf
					("/" + config_fb_pw) - 1);
			GPLUS_TESTUSER_NAME = text.substring(text.indexOf(config_gplus_name) + config_gplus_name.length() + 1, text.indexOf
					("/" + config_gplus_name) - 1);
			GPLUS_TESTUSER_MAIL = text.substring(text.indexOf(config_gplus_mail) + config_gplus_mail.length() + 1, text
					.indexOf
							("/" + config_gplus_mail) - 1);
			GPLUS_TESTUSER_PW = text.substring(text.indexOf(config_gplus_pw) + config_gplus_pw.length() + 1, text
					.indexOf
							("/" + config_gplus_pw) - 1);

			configFileRead = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
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
