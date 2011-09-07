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
package at.tugraz.ist.catroid.test.web;

import android.test.AndroidTestCase;
import android.util.Log;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilToken;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

/*
 * This tests need an internet connection
 */
public class ServerCallsTest extends AndroidTestCase {
	private static final String LOG_TAG = ServerCalls.class.getSimpleName();

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
		TestUtils.clearProject("uploadtestProject");
		ServerCalls.useTestUrl = false;
		super.tearDown();
	}

	public void testRegistrationOk() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = UtilToken.calculateToken(testUser, testPassword);
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token);

			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			boolean tokenOk = ServerCalls.getInstance().checkToken(token);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);
		} catch (WebconnectionException e) {
			e.printStackTrace();
			assertFalse("an exception should not be thrown, the token should be valid", true);
		}

	}

	public void testRegisterWithExistingUser() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = UtilToken.calculateToken(testUser, testPassword);
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token);

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail, "de",
					"at", token);

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertFalse("Should be an existing user, but server responce indicates that this user is new",
					userRegistered);

		} catch (WebconnectionException e) {
			assertFalse("an exception should not be thrown", true);
			e.printStackTrace();
		}

	}

	public void testRegisterWithExistingUserButWrongPws() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = UtilToken.calculateToken(testUser, testPassword);
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token);

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			String wrongPassword = "wrongpassword";
			token = UtilToken.calculateToken(testUser, wrongPassword);
			ServerCalls.getInstance().registerOrCheckToken(testUser, wrongPassword, testEmail, "de", "at", token);

			assertFalse("should never be reached because the password is wrong", true);

		} catch (WebconnectionException e) {
			e.printStackTrace();
			assertTrue("an exception should be thrown because the password is wrong", true);
		}

	}

	public void testCheckTokenAnonymous() {
		try {
			String anonymousToken = "0";
			boolean tokenOk = ServerCalls.getInstance().checkToken(anonymousToken);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);

		} catch (WebconnectionException e) {
			assertFalse("an exception should not be thrown", true);
			e.printStackTrace();
		}

	}

	public void testCheckTokenWrong() {
		try {
			String wrongToken = "blub";
			boolean tokenOk = ServerCalls.getInstance().checkToken(wrongToken);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertFalse("should not be reanched, exception is thrown", tokenOk);

		} catch (WebconnectionException e) {
			assertTrue("exception is thrown if we pass a wrong token", true);
		}
	}

	public void testCheckTokenOk() {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = UtilToken.calculateToken(testUser, testPassword);
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token);

			Log.i(LOG_TAG, "user registered: " + userRegistered);
			assertTrue("Should be a new user, but server responce indicates that this user already exists",
					userRegistered);

			boolean tokenOk = ServerCalls.getInstance().checkToken(token);

			Log.i(LOG_TAG, "tokenOk: " + tokenOk);
			assertTrue("token should be ok", tokenOk);

		} catch (WebconnectionException e) {
			assertFalse("an exception should not be thrown", true);
			e.printStackTrace();
		}

	}

}