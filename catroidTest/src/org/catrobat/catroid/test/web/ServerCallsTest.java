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
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.File;

/*
 * This tests need an internet connection
 */
public class ServerCallsTest extends AndroidTestCase {
	private static final String LOG_TAG = ServerCalls.class.getSimpleName();
	public static final int STATUS_CODE_USER_PASSWORD_TOO_SHORT = 753;
	public static final int STATUS_CODE_USER_ADD_EMAIL_EXISTS = 757;
	public static final int STATUS_CODE_USER_EMAIL_INVALID = 765;
	public static final int STATUS_CODE_AUTHENTICATION_FAILED = 601;

	public ServerCallsTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServerCalls.useTestUrl = true;
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

			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token, getContext());

			assertTrue("Should be a new user, but server response indicates that this user already exists",
					userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);
		} catch (WebconnectionException e) {
			e.printStackTrace();
			assertFalse("WebconnectionException: the token should be valid \nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getMessage(), true);
		}

	}

	public void testRegisterWithExistingUser() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token, getContext());

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server response indicates that this user already exists",
					userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail, "de",
					"at", token, getContext());

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertFalse("Should be an existing user, but server responce indicates that this user is new",
					userRegistered);
		} catch (WebconnectionException e) {
			e.printStackTrace();
			assertFalse(
					"an exception should not be thrown! \nstatus code:" + e.getStatusCode() + "\nmessage: "
							+ e.getMessage(), true);
		}

	}

	public void testRegisterWithExistingUserButWrongPws() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token, getContext());

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server response indicates that this user already exists",
					userRegistered);

			String wrongPassword = "wrongpassword";
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			ServerCalls.getInstance().registerOrCheckToken(testUser, wrongPassword, testEmail, "de", "at", token,
					getContext());

			assertFalse("should never be reached because the password is wrong", true);

		} catch (WebconnectionException e) {
			e.printStackTrace();
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
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token, getContext());

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			String newUser = "testUser" + System.currentTimeMillis();
			token = Constants.NO_TOKEN;
			ServerCalls.getInstance().registerOrCheckToken(newUser, testPassword, testEmail, "de", "at", token,
					getContext());

			assertFalse(
					"should never be reached because two registrations with the same email address are not allowed",
					true);

		} catch (WebconnectionException e) {
			e.printStackTrace();
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
			ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail, "de", "at", token,
					getContext());

			assertFalse("should never be reached because the password is too short", true);

		} catch (WebconnectionException e) {
			e.printStackTrace();
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
			ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail, "de", "at", token,
					getContext());

			assertFalse("should never be reached because the email is not valid", true);

		} catch (WebconnectionException e) {
			e.printStackTrace();
			assertTrue("an exception should be thrown because the email is not valid", true);
			assertEquals("wrong status code from server", STATUS_CODE_USER_EMAIL_INVALID,
					e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testCheckTokenAnonymous() {
		try {
			String anonymousToken = "0";
			String username = "anonymous";
			boolean tokenOk = ServerCalls.getInstance().checkToken(anonymousToken, username);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);

		} catch (WebconnectionException e) {
			assertFalse(
					"an exception should not be thrown! \nstatus code:" + e.getStatusCode() + "\nmessage: "
							+ e.getMessage(), true);
			e.printStackTrace();
		}

	}

	public void testCheckTokenWrong() {
		try {
			String wrongToken = "blub";
			String username = "badUser";
			boolean tokenOk = ServerCalls.getInstance().checkToken(wrongToken, username);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertFalse("should not be reanched, exception is thrown", tokenOk);

		} catch (WebconnectionException e) {
			assertTrue("exception is thrown if we pass a wrong token", true);
			assertEquals("wrong status code from server", STATUS_CODE_AUTHENTICATION_FAILED, e.getStatusCode());
			assertNotNull("no error message available", e.getMessage());
			assertTrue("no error message available", e.getMessage().length() > 0);
		}
	}

	public void testUploadWithExistingUserWithoutEmail() {
		File zipFile = null;
		try {
			Project project = TestUtils
					.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);

			Reflection.setPrivateField(project.getXmlHeader(), "applicationVersion", "0.7.3beta");
			StorageHandler.getInstance().saveProject(project);

			String projectPath = Constants.DEFAULT_ROOT + "/" + TestUtils.DEFAULT_TEST_PROJECT_NAME;

			File directoryPath = new File(projectPath);
			String[] paths = directoryPath.list();

			for (int i = 0; i < paths.length; i++) {
				paths[i] = Utils.buildPath(directoryPath.getAbsolutePath(), paths[i]);
			}

			String zipFileString = Utils.buildPath(Constants.TMP_PATH, "upload" + Constants.CATROBAT_EXTENSION);
			zipFile = new File(zipFileString);
			if (!zipFile.exists()) {
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
			}

			UtilZip.writeToZipFile(paths, zipFileString);

			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";
			String token = Constants.NO_TOKEN;

			ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail, "de", "at", token,
					getContext());
			token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.TOKEN, "");
			ServerCalls.useTestUrl = true;
			ServerCalls.getInstance().uploadProject("test", "", zipFileString, null, "de", token, testUser, null, 0,
					getContext());

		} catch (Exception exception) {
			Log.e(LOG_TAG, "testUploadWithExistingUserWithoutEmail: error", exception);
			fail("Upload with existing user but without e-mail failed!");
		} finally {
			if (zipFile != null) {
				zipFile.delete();
			}
		}

	}

	public void testCheckTokenOk() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token, getContext());

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);

		} catch (WebconnectionException e) {
			assertFalse("WebconnectionException \nstatus code:" + e.getStatusCode() + "\nmessage: " + e.getMessage(),
					true);
			e.printStackTrace();
		}

	}

}
