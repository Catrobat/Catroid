/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.transfers.DeleteTestUserTask;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

/*
 * These tests need an internet connection
 */
@RunWith(AndroidJUnit4.class)
public class ServerCallsTest implements DeleteTestUserTask.OnDeleteTestUserCompleteListener {

	private static final String TAG = ServerCalls.class.getSimpleName();

	private static final int STATUS_CODE_USER_PASSWORD_TOO_SHORT = 753;
	private static final int STATUS_CODE_USER_ADD_EMAIL_EXISTS = 757;
	private static final int STATUS_CODE_USER_EMAIL_INVALID = 765;
	private static final int STATUS_CODE_AUTHENTICATION_FAILED = 601;
	private static final int STATUS_CODE_USER_NOT_EXISTING = 764;

	@Before
	public void setUp() throws Exception {
		ServerCalls.useTestUrl = true;
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects("uploadtestProject");
		ServerCalls.useTestUrl = false;
	}

	@Test
	public void testRegistrationOk() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";
			String token = Constants.NO_TOKEN;

			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, InstrumentationRegistry.getTargetContext());

			assertTrue(userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);

			Log.i(TAG, "tokenOk: " + tokenOk);
			assertTrue(tokenOk);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Webconnection error", e);
			fail("WebconnectionException: the token should be valid \nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getMessage());
		}
	}

	@Test
	public void testRegisterWithExistingUser() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, InstrumentationRegistry.getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue(userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			ServerCalls.getInstance().register(testUser, testPassword, testEmail, "de",
					"at", token, InstrumentationRegistry.getTargetContext());

			fail("WebconnectionException not thrown");
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertEquals(STATUS_CODE_USER_ADD_EMAIL_EXISTS, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	public void testRegisterAndLogin() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, InstrumentationRegistry.getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue(userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean userLoggedIn = ServerCalls.getInstance().login(testUser, testPassword, token, InstrumentationRegistry.getTargetContext());

			Log.i(TAG, "user logged in: " + userLoggedIn);
			assertTrue(userLoggedIn);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Webconnection error", e);
			fail("an exception should not be thrown! \nstatus code:" + e.getStatusCode() + "\nmessage: "
					+ e.getMessage());
		}
	}

	@Test
	public void testLoginWithNotExistingUser() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String token = Constants.NO_TOKEN;

			ServerCalls.getInstance().login(testUser, testPassword, token, InstrumentationRegistry.getTargetContext());

			fail("WebconnectionException not thrown");
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertEquals(STATUS_CODE_USER_NOT_EXISTING, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	public void testRegisterWithExistingUserAndLoginWithWrongPassword() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, InstrumentationRegistry.getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue(userRegistered);

			String wrongPassword = "wrongpassword";
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			ServerCalls.getInstance().login(testUser, wrongPassword, token, InstrumentationRegistry.getTargetContext());

			fail("WebconnectionException not thrown");
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertEquals(STATUS_CODE_AUTHENTICATION_FAILED, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	public void testRegisterWithNewUserButExistingEmail() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, InstrumentationRegistry.getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue(userRegistered);

			String newUser = "testUser" + System.currentTimeMillis();
			token = Constants.NO_TOKEN;
			ServerCalls.getInstance().register(newUser, testPassword, testEmail, "de", "at", token,
					InstrumentationRegistry.getTargetContext());

			fail("WebconnectionException not thrown");
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertEquals(STATUS_CODE_USER_ADD_EMAIL_EXISTS, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	public void testRegisterWithTooShortPassword() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "short";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			ServerCalls.getInstance().register(testUser, testPassword, testEmail, "de", "at", token,
					InstrumentationRegistry.getTargetContext());

			fail("WebconnectionException not thrown");
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertEquals(STATUS_CODE_USER_PASSWORD_TOO_SHORT, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	public void testRegisterWithInvalidEmail() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = "invalidEmail";

			String token = Constants.NO_TOKEN;
			ServerCalls.getInstance().register(testUser, testPassword, testEmail, "de", "at", token,
					InstrumentationRegistry.getTargetContext());

			fail("WebconnectionException not thrown");
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertEquals(STATUS_CODE_USER_EMAIL_INVALID, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	public void testCheckTokenWrong() {
		try {
			String wrongToken = "blub";
			String username = "badUser";
			boolean tokenOk = ServerCalls.getInstance().checkToken(wrongToken, username);

			Log.i(TAG, "tokenOk: " + tokenOk);
			assertFalse(tokenOk);
		} catch (WebconnectionException e) {
			Log.i(TAG, "Webconnection error (this is expected behaviour)", e);
			assertEquals(STATUS_CODE_AUTHENTICATION_FAILED, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	public void testCheckTokenOk() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, InstrumentationRegistry.getTargetContext());

			Log.i(TAG, "user registered: " + userRegistered);
			assertTrue(userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
			token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
			boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);

			Log.i(TAG, "tokenOk: " + tokenOk);
			assertTrue(tokenOk);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Webconnection error", e);
			fail("WebconnectionException \nstatus code:" + e.getStatusCode() + "\nmessage: " + e.getMessage());
		}
	}

	@Override
	public void onDeleteTestUserComplete(Boolean deleted) {
		assertTrue(deleted);
		Log.d(TAG, "testUserAccountsDeleted");
	}
}
