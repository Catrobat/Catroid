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
package org.catrobat.catroid.test.web;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.transfers.DeleteTestUserTask;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * These tests need an internet connection
 */
public class ServerCallsTest extends InstrumentationTestCase implements DeleteTestUserTask.OnDeleteTestUserCompleteListener {
	private static final String TAG = ServerCalls.class.getSimpleName();
	public static final int STATUS_CODE_USER_PASSWORD_TOO_SHORT = 753;
	public static final int STATUS_CODE_USER_ADD_EMAIL_EXISTS = 757;
	public static final int STATUS_CODE_USER_EMAIL_INVALID = 765;
	public static final int STATUS_CODE_AUTHENTICATION_FAILED = 601;
	public static final int STATUS_CODE_USER_NOT_EXISTING = 764;

	private boolean testUserAccountsDeleted = false;
	private boolean configFileRead = false;
	private boolean facebookTestUserCreated = false;
	private boolean facebookTestUserDeleted = false;
	private String facebookTestUserId;

	private Map<String, String> configMap;

	public ServerCallsTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServerCalls.useTestUrl = true;
		testUserAccountsDeleted = false;
		configFileRead = false;
		facebookTestUserCreated = false;
		facebookTestUserDeleted = false;
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.deleteTestProjects("uploadtestProject");
		ServerCalls.useTestUrl = false;
		super.tearDown();
	}

	public void testRegistrationOk() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";
			String token = Constants.NO_TOKEN;

			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, getInstrumentation().getTargetContext());

			assertTrue("Should be a new user, but server response indicates that this user already exists",
					userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);

			Log.i(TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Webconnection error", e);
			fail("WebconnectionException: the token should be valid \nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getMessage());
		}
	}

	public void testRegisterWithExistingUser() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, getInstrumentation().getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server response indicates that this user already exists",
					userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			ServerCalls.getInstance().register(testUser, testPassword, testEmail, "de",
					"at", token, getInstrumentation().getTargetContext());

			assertFalse("should never be reached because the user is already registered and can't be registered again",
					true);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertTrue("an exception should be thrown because the user(-e-mail) is already registered", true);
			assertEquals("wrong status code from server", STATUS_CODE_USER_ADD_EMAIL_EXISTS, e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testRegisterAndLogin() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, getInstrumentation().getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server response indicates that this user already exists",
					userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean userLoggedIn = ServerCalls.getInstance().login(testUser, testPassword, token, getInstrumentation().getTargetContext());

			Log.i(TAG, "user logged in: " + userLoggedIn);
			assertTrue("User should be logged in, but server response indicates that he was not",
					userLoggedIn);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Webconnection error", e);
			fail("an exception should not be thrown! \nstatus code:" + e.getStatusCode() + "\nmessage: "
					+ e.getMessage());
		}
	}

	public void testLoginWithNotExistingUser() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String token = Constants.NO_TOKEN;

			ServerCalls.getInstance().login(testUser, testPassword, token, getInstrumentation().getTargetContext());

			assertFalse("should never be reached because the user to be logged in is not existing",
					true);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertTrue("an exception should be thrown because the user does not exist", true);
			assertEquals("wrong status code from server", STATUS_CODE_USER_NOT_EXISTING, e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testRegisterWithExistingUserAndLoginWithWrongPassword() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, getInstrumentation().getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server response indicates that this user already exists",
					userRegistered);

			String wrongPassword = "wrongpassword";
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			ServerCalls.getInstance().login(testUser, wrongPassword, token, getInstrumentation().getTargetContext());

			assertFalse("should never be reached because the password is wrong", true);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertTrue("an exception should be thrown because the password is wrong", true);
			assertEquals("wrong status code from server", STATUS_CODE_AUTHENTICATION_FAILED, e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testRegisterWithNewUserButExistingEmail() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, getInstrumentation().getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			String newUser = "testUser" + System.currentTimeMillis();
			token = Constants.NO_TOKEN;
			ServerCalls.getInstance().register(newUser, testPassword, testEmail, "de", "at", token,
					getInstrumentation().getTargetContext());

			assertFalse(
					"should never be reached because two registrations with the same email address are not allowed",
					true);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertTrue("an exception should be thrown because the email already exists on the server", true);
			assertEquals("wrong status code from server", STATUS_CODE_USER_ADD_EMAIL_EXISTS,
					e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testRegisterWithTooShortPassword() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "short";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			ServerCalls.getInstance().register(testUser, testPassword, testEmail, "de", "at", token,
					getInstrumentation().getTargetContext());

			assertFalse("should never be reached because the password is too short", true);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertTrue("an exception should be thrown because the password is too short", true);
			assertEquals("wrong status code from server", STATUS_CODE_USER_PASSWORD_TOO_SHORT,
					e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testRegisterWithInvalidEmail() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = "invalidEmail";

			String token = Constants.NO_TOKEN;
			ServerCalls.getInstance().register(testUser, testPassword, testEmail, "de", "at", token,
					getInstrumentation().getTargetContext());

			assertFalse("should never be reached because the email is not valid", true);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertTrue("an exception should be thrown because the email is not valid", true);
			assertEquals("wrong status code from server", STATUS_CODE_USER_EMAIL_INVALID,
					e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testCheckTokenWrong() {
		try {
			String wrongToken = "blub";
			String username = "badUser";
			boolean tokenOk = ServerCalls.getInstance().checkToken(wrongToken, username);

			Log.i(TAG, "tokenOk: " + tokenOk);
			assertFalse("should not be reached, exception is thrown", tokenOk);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertTrue("exception is thrown if we pass a wrong token", true);
			assertEquals("wrong status code from server", STATUS_CODE_AUTHENTICATION_FAILED, e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testCheckTokenOk() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, getInstrumentation().getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);

			Log.i(TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Webconnection error", e);
			fail("WebconnectionException \nstatus code:" + e.getStatusCode() + "\nmessage: " + e.getMessage());
		}
	}

	public void testFacebookServerCalls() {
		waitForFacebookTestReady();
		try {
			boolean openAuthFacebookTokenAvailable = ServerCalls.getInstance().checkOAuthToken(facebookTestUserId,
					Constants.FACEBOOK, getInstrumentation().getTargetContext());
			Log.i(TAG, "OAuth Facebook token available: " + openAuthFacebookTokenAvailable);
			assertFalse("Facebook Token should not be available, but server response indicates that it is.",
					openAuthFacebookTokenAvailable);

			boolean facebookEmailAvailable = ServerCalls.getInstance().checkEMailAvailable(
					configMap.get(UiTestUtils.CONFIG_FACEBOOK_MAIL));
			Log.i(TAG, "E-Mail available: " + facebookEmailAvailable);
			assertFalse("E-Mail should not be available, but server response indicates that it is.",
					facebookEmailAvailable);

			boolean facebookUserNameAvailable = ServerCalls.getInstance().checkUserNameAvailable(
					configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME));
			Log.i(TAG, "Username available: " + facebookUserNameAvailable);
			assertFalse("Username should not be available, but server response indicates that it is.",
					facebookUserNameAvailable);

			boolean tokenExchanged = ServerCalls.getInstance().facebookExchangeToken(AccessToken
							.getCurrentAccessToken().getToken(), facebookTestUserId,
					configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME), configMap.get(UiTestUtils.CONFIG_FACEBOOK_MAIL),
					"at");

			Log.i(TAG, "tokenExchanged: " + tokenExchanged);
			assertTrue("Token should be exchanged, but server response indicates that it was not.",
					tokenExchanged);

			boolean loggedIn = ServerCalls.getInstance().facebookLogin(configMap.get(UiTestUtils.CONFIG_FACEBOOK_MAIL),
					configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME), facebookTestUserId, "at", getInstrumentation().getTargetContext());

			Log.i(TAG, "loggedIn: " + loggedIn);
			assertTrue("User should be logged in, but server response indicates that he is not.",
					loggedIn);

			openAuthFacebookTokenAvailable = ServerCalls.getInstance().checkOAuthToken(facebookTestUserId,
					Constants.FACEBOOK, getInstrumentation().getTargetContext());
			Log.i(TAG, "OAuth Facebook token available: " + openAuthFacebookTokenAvailable);
			assertTrue("Facebook Token should be available, but server response indicates that it is not.",
					openAuthFacebookTokenAvailable);

			facebookEmailAvailable = ServerCalls.getInstance().checkEMailAvailable(configMap.get(UiTestUtils
					.CONFIG_FACEBOOK_MAIL));
			Log.i(TAG, "E-Mail available: " + facebookEmailAvailable);
			assertTrue("E-Mail should be available, but server response indicates that it is not.",
					facebookEmailAvailable);

			facebookUserNameAvailable = ServerCalls.getInstance().checkUserNameAvailable(configMap.get(UiTestUtils.CONFIG_FACEBOOK_NAME));
			Log.i(TAG, "Username available: " + facebookUserNameAvailable);
			assertTrue("Username should be available, but server response indicates that it is not.",
					facebookUserNameAvailable);

			JSONObject facebookUserInfo = ServerCalls.getInstance().getFacebookUserInfo(facebookTestUserId, null);
			assertTrue("server response doesn't have facebook username", facebookUserInfo.has(Constants.USERNAME));
			String name = facebookUserInfo.getString(Constants.USERNAME);
			Log.i(TAG, "Username: " + name);
			assertTrue("server response doesn't have facebook email", facebookUserInfo.has(Constants.EMAIL));
			String email = facebookUserInfo.getString(Constants.EMAIL);
			Log.i(TAG, "email: " + email);
			assertTrue("server response doesn't have facebook locale", facebookUserInfo.has(Constants.LOCALE));
			String locale = facebookUserInfo.getString(Constants.LOCALE);
			Log.i(TAG, "locale: " + locale);

			assertTrue("Username should be available, but server response indicates that it is not.",
					facebookUserNameAvailable);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Webconnection occurred", e);
		} catch (JSONException e) {
			Log.e(TAG, "JSONException", e);
		} finally {
			waitForFacebookTestUserDeleted();
		}
	}

	private void createFacebookTestUser() {
		final String facebookAppId = getInstrumentation().getTargetContext().getString(R.string.facebook_app_id);
		AccessToken accessToken = new AccessToken(configMap.get(UiTestUtils.CONFIG_FACEBOOK_APP_TOKEN), facebookAppId,
				facebookAppId, null, null, null, null, null);
		AccessToken.setCurrentAccessToken(accessToken);
		Bundle params = new Bundle();
		params.putString("installed", "true");
		final boolean[] tryAgainIfNecessary = { true };
		new GraphRequest(AccessToken.getCurrentAccessToken(),
				"/" + facebookAppId + "/accounts/test-users",
				params,
				HttpMethod.POST,
				new GraphRequest.Callback() {
					public void onCompleted(GraphResponse response) {
						try {
							if ((response == null || response.getJSONObject() == null) && tryAgainIfNecessary[0]) {
								//Sometimes Facebook returns a response just after the second call..
								tryAgainIfNecessary[0] = false;
								createFacebookTestUser();
							}
							if (response != null) {
								Log.i(TAG, "response: " + response.getRawResponse());
								facebookTestUserId = response.getJSONObject().getString("id");
								String accessTokenTestUser = response.getJSONObject().getString("access_token");
								Log.i(TAG, "test user id: " + facebookTestUserId);

								AccessToken accessToken = new AccessToken(accessTokenTestUser, facebookAppId,
										facebookTestUserId, null, null, null, null, null);
								AccessToken.setCurrentAccessToken(accessToken);
							}
						} catch (JSONException e) {
							Log.e(TAG, "JSONException occurred", e);
						}
						facebookTestUserCreated = true;
						Log.d(TAG, "facebookTestUserCreated");
					}
				}
		).executeAndWait();
	}

	private void deleteFacebookTestUser() {
		new GraphRequest(
				AccessToken.getCurrentAccessToken(),
				"/" + facebookTestUserId,
				null,
				HttpMethod.DELETE,
				new GraphRequest.Callback() {
					public void onCompleted(GraphResponse response) {
						Log.i(TAG, "response: " + response.getRawResponse());
						facebookTestUserDeleted = true;
					}
				}
		).executeAndWait();
	}

	private void waitForFacebookTestUserDeleted() {
		if (facebookTestUserId == null || facebookTestUserId.isEmpty()) {
			return;
		}
		deleteFacebookTestUser();
		while (!facebookTestUserDeleted) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Log.e(TAG, "InterruptedException occurred", e);
			}
		}
	}

	private void waitForFacebookTestReady() {
		FacebookSdk.sdkInitialize(getInstrumentation().getTargetContext());
		deleteTestUserAccountsOnServer();
		readConfigFile();
		while (!testUserAccountsDeleted || !configFileRead || !facebookTestUserCreated) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Log.e(TAG, "InterruptedException occurred", e);
			}
		}
	}

	private void deleteTestUserAccountsOnServer() {
		DeleteTestUserTask deleteTestUserTask = new DeleteTestUserTask(getInstrumentation().getTargetContext());
		deleteTestUserTask.setOnDeleteTestUserCompleteListener(this);
		deleteTestUserTask.execute();
	}

	@Override
	public void onDeleteTestUserComplete(Boolean deleted) {
		assertTrue("Test user accounts could not be deleted on server!", deleted);
		testUserAccountsDeleted = true;
		Log.d(TAG, "testUserAccountsDeleted");
	}

	private void readConfigFile() {
		configMap = UiTestUtils.readConfigFile(getInstrumentation().getContext());
		configFileRead = true;
		Log.d(TAG, "configFileRead");
		createFacebookTestUser();
	}
}
