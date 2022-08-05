/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.transfers.DeleteTestUserTask;
import org.catrobat.catroid.web.CatrobatServerCalls;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.ServerAuthenticator;
import org.catrobat.catroid.web.ServerAuthenticator.TaskListener;
import org.catrobat.catroid.web.WebconnectionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Category(Cat.OutgoingNetworkTests.class)
@RunWith(AndroidJUnit4.class)
public class AuthenticationCallsTest implements DeleteTestUserTask.OnDeleteTestUserCompleteListener {

	private static final int STATUS_CODE_USER_PASSWORD_TOO_SHORT = 753;
	private static final int STATUS_CODE_USER_ADD_EMAIL_EXISTS = 757;
	private static final int STATUS_CODE_USER_EMAIL_INVALID = 765;
	private static final int STATUS_CODE_AUTHENTICATION_FAILED = 601;
	private static final int STATUS_CODE_TOKEN_FAILED = 1001;
	private static final int STATUS_CODE_USERNAME_NOT_FOUND = 803;
	private static final String BASE_URL_TEST_HTTPS = "https://develop-web.catrobat.ist.tugraz.at/app/";

	private ServerAuthenticator authenticator;
	private String token = Constants.NO_TOKEN;
	private String testUser;
	private String testEmail;
	private TaskListener listenerMock;
	private SharedPreferences sharedPreferences;

	@Before
	public void setUp() throws Exception {
		testUser = "testUser" + System.currentTimeMillis();
		testEmail = testUser + "@gmail.com";
		String testPassword = "pwspws";
		listenerMock = Mockito.mock(TaskListener.class);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
		authenticator = new ServerAuthenticator(testUser, testPassword, token, CatrobatWebClient.INSTANCE.getClient(),
				BASE_URL_TEST_HTTPS, sharedPreferences, listenerMock);
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects("uploadtestProject");
	}

	@Test
	@Flaky
	public void testRegistrationOk() throws WebconnectionException {
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, never()).onError(anyInt(), anyString());
		verify(listenerMock, atLeastOnce()).onSuccess();

		token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		assertTrue(new CatrobatServerCalls().checkToken(token, testUser, BASE_URL_TEST_HTTPS));
	}

	@Test
	@Flaky
	public void testRegisterWithExistingUser() {
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, never()).onError(anyInt(), anyString());
		verify(listenerMock, atLeastOnce()).onSuccess();

		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, times(1)).onError(eq(STATUS_CODE_USER_ADD_EMAIL_EXISTS), Mockito.matches(".+"));
	}

	@Test
	@Flaky
	public void testRegisterAndLogin() {
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, never()).onError(anyInt(), anyString());
		verify(listenerMock, atLeastOnce()).onSuccess();

		authenticator.performCatrobatLogin();
		verify(listenerMock, never()).onError(anyInt(), anyString());
	}

	@Test
	@Flaky
	public void testLoginWithNotExistingUser() {
		authenticator.performCatrobatLogin();
		verify(listenerMock, times(1)).onError(eq(STATUS_CODE_USERNAME_NOT_FOUND), Mockito.matches(".+"));
		verify(listenerMock, never()).onSuccess();
	}

	@Test
	@Flaky
	public void testRegisterWithExistingUserAndLoginWithWrongPassword() {
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, never()).onError(anyInt(), anyString());
		verify(listenerMock, atLeastOnce()).onSuccess();

		authenticator.setPassword("wrongPassword");
		authenticator.performCatrobatLogin();
		verify(listenerMock, times(1)).onError(eq(STATUS_CODE_AUTHENTICATION_FAILED), Mockito.matches(".+"));
	}

	@Test
	@Flaky
	public void testRegisterWithNewUserButExistingEmail() {
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, never()).onError(anyInt(), anyString());
		verify(listenerMock, atLeastOnce()).onSuccess();

		String newUser = "testUser" + System.currentTimeMillis();
		authenticator.setUsername(newUser);
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, times(1)).onError(eq(STATUS_CODE_USER_ADD_EMAIL_EXISTS), Mockito.matches(".+"));
	}

	@Test
	@Flaky
	public void testRegisterWithTooShortPassword() {
		authenticator.setPassword("short");
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, times(1)).onError(eq(STATUS_CODE_USER_PASSWORD_TOO_SHORT), Mockito.matches(".+"));
		verify(listenerMock, never()).onSuccess();
	}

	@Test
	@Flaky
	public void testRegisterWithInvalidEmail() {
		authenticator.performCatrobatRegister("invalidEmail", "de", "at");
		verify(listenerMock, times(1)).onError(eq(STATUS_CODE_USER_EMAIL_INVALID), Mockito.matches(".+"));
		verify(listenerMock, never()).onSuccess();
	}

	@Test
	@Flaky
	public void testCheckTokenWrong() {
		String wrongToken = "blub";
		String username = "badUser";
		try {
			boolean tokenOk = new CatrobatServerCalls().checkToken(wrongToken, username, BASE_URL_TEST_HTTPS);

			assertFalse(tokenOk);
		} catch (WebconnectionException e) {
			assertEquals(STATUS_CODE_TOKEN_FAILED, e.getStatusCode());
			assertNotNull(e.getMessage());
			assertThat(e.getMessage().length(), is(greaterThan(0)));
		}
	}

	@Test
	@Flaky
	public void testCheckTokenOk() throws WebconnectionException {
		authenticator.performCatrobatRegister(testEmail, "de", "at");
		verify(listenerMock, never()).onError(anyInt(), anyString());
		verify(listenerMock, atLeastOnce()).onSuccess();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
		token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		assertTrue(new CatrobatServerCalls().checkToken(token, testUser, BASE_URL_TEST_HTTPS));
	}

	@Override
	public void onDeleteTestUserComplete(Boolean deleted) {
		assertTrue(deleted);
	}
}
