/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.test.InstrumentationTestCase;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.transfers.DeleteTestUserTask;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebScratchProgramException;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;
import java.util.List;
import java.util.Locale;

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

	public void testScratchSearchWithEmptyQueryParam() {
		try {
			ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("", 20, 0);
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", programDataList);
			assertTrue("Empty query should to no results!", programDataList.size() == 0);
			assertNotNull("No search result returned", searchResult);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	private void checkProgramData(ScratchProgramData programData) {
		final String imageBaseURL = "https://cdn2.scratch.mit.edu/get_image/project/";
		assertTrue("Invalid program ID", programData.getId() > 0);

		assertNotNull("Invalid program title", programData.getTitle());
		assertTrue("Invalid program title", programData.getTitle().length() > 0);

		assertNotNull("Program has invalid owner", programData.getOwner());
		assertTrue("Program has invalid owner", programData.getOwner().length() > 0);

		assertNotNull("Program has invalid notes & credits description", programData.getNotesAndCredits());
		assertNotNull("Program has invalid instructions-description", programData.getInstructions());

		assertNotNull("Program contains no screenshot-image URL", programData.getImage());
		assertNotNull("Program contains no screenshot-image URL", programData.getImage().getUrl());
		assertTrue("Screenshot-image URL does not start with base URL any more: " + imageBaseURL,
				programData.getImage().getUrl().toString().startsWith(imageBaseURL));

		final int[] imageSize = Utils.extractImageSizeFromScratchImageURL(programData.getImage().getUrl().toString());
		assertTrue("Invalid width extracated of image URL", programData.getImage().getWidth() == imageSize[0]);
		assertTrue("Invalid height extracted from image URL", programData.getImage().getHeight() == imageSize[1]);
		final String imageURLWithoutQuery = programData.getImage().getUrl().toString().split("\\?")[0];
		final String expectedImageURLWithoutQuery = String.format(Locale.getDefault(), "%s%d_%dx%d.png", imageBaseURL,
				programData.getId(), imageSize[0], imageSize[1]);
		assertEquals("Image URL is corrupt!", imageURLWithoutQuery, expectedImageURLWithoutQuery);

		assertNotNull("Program has no modified date", programData.getModifiedDate());
		assertNotNull("Program has no shared date", programData.getSharedDate());

		assertTrue("View-counter-value of program is invalid", programData.getViews() >= 0);
		assertTrue("Love-counter-value of program is invalid", programData.getLoves() >= 0);
		assertTrue("Favorites-counter-value of program is invalid", programData.getFavorites() >= 0);
	}

	public void testScratchSearchWithQueryParam() {
		try {
			ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", 20, 0);
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", searchResult);
			assertNotNull("Invalid search result", programDataList);
			assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
			assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);
			assertTrue("Search result is too big...", searchResult.getProgramDataList().size() <= 20);

			for (ScratchProgramData programData : programDataList) {
				checkProgramData(programData);
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testScratchSearchMaxNumberOfItemsParam() {
		try {
			final int maxNumberOfItems = 10;

			ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", maxNumberOfItems, 0);
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", searchResult);
			assertNotNull("Invalid search result", programDataList);
			assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
			assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);
			assertTrue("Search result is too big...", searchResult.getProgramDataList().size() <= maxNumberOfItems);

			for (ScratchProgramData programData : programDataList) {
				checkProgramData(programData);
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testScratchSearchPagination() {
		try {
			for (int pageIndex = 1; pageIndex < 3; pageIndex++) {
				ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", 20, pageIndex);
				List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

				assertNotNull("Invalid search result", searchResult);
				assertNotNull("Invalid search result", programDataList);
				assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
				assertTrue("Wrong page number", searchResult.getPageNumber() == pageIndex);
				assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);
				assertTrue("Search result is too big...", searchResult.getProgramDataList().size() <= 20);

				for (ScratchProgramData programData : programDataList) {
					checkProgramData(programData);
				}
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testFetchDefaultScratchPrograms() {
		try {
			ScratchSearchResult searchResult = ServerCalls.getInstance().fetchDefaultScratchPrograms();
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull("Invalid search result", searchResult);
			assertNotNull("Invalid search result", programDataList);
			assertTrue("WTH?? No search results returned!", programDataList.size() > 0);
			assertTrue("Wrong page number", searchResult.getPageNumber() == 0);
			assertTrue("No projects found!", searchResult.getProgramDataList().size() > 0);

			for (ScratchProgramData programData : programDataList) {
				checkProgramData(programData);
			}
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
	}

	public void testFetchScratchProgramDetails() {
		try {
			final long expectedProgramID = 10205819;
			final String expectedProgramTitle = "Dancin' in the Castle";
			final String expectedProgramOwner = "jschombs";
			ScratchProgramData programData = ServerCalls.getInstance().fetchScratchProgramDetails(expectedProgramID);

			checkProgramData(programData);
			assertEquals("Invalid program ID", programData.getId(), expectedProgramID);
			assertEquals("Wrong program title?! Maybe the program owner changed the program title...",
					programData.getTitle(), expectedProgramTitle);
			assertEquals("Program has invalid owner", programData.getOwner(), expectedProgramOwner);
		} catch (InterruptedIOException e) {
			fail("Task has been interrupted/cancelled! This should not happen here!");
		} catch (WebconnectionException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		} catch (WebScratchProgramException e) {
			fail("WebconnectionException:\nstatus code:" + e.getStatusCode()
					+ "\nmessage: " + e.getLocalizedMessage());
		}
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

	@Override
	public void onDeleteTestUserComplete(Boolean deleted) {
		assertTrue("Test user accounts could not be deleted on server!", deleted);
		Log.d(TAG, "testUserAccountsDeleted");
	}
}
