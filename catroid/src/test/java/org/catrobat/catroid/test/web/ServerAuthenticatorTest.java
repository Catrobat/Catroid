/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.web.CatrobatWebClientKt;
import org.catrobat.catroid.web.ServerAuthenticator;
import org.catrobat.catroid.web.WebConnectionException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE;
import static org.catrobat.catroid.web.CatrobatWebClientKt.createFormEncodedRequest;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_COUNTRY_KEY;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_EMAIL_KEY;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_PASSWORD_KEY;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_USERNAME_KEY;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_ANSWER;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_STATUS_CODE;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_TOKEN;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.LOGIN_URL_APPENDING;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.REGISTRATION_URL_APPENDING;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_REGISTER_OK;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK;
import static org.catrobat.catroid.web.ServerAuthenticationConstants.TOKEN_LENGTH;
import static org.catrobat.catroid.web.ServerAuthenticator.TaskListener;
import static org.catrobat.catroid.web.ServerCalls.BASE_URL_TEST_HTTPS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerAuthenticatorTest {

	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String TOKEN = "TOKEN";
	private static final String EMAIL = "EMAIL";
	private static final String LANGUAGE = "LANGUAGE";
	private static final String COUNTRY = "COUNTRY";

	private TaskListener taskListenerMock;
	private OkHttpClient okHttpClientMock;
	private ServerAuthenticator authenticatorSpy;
	private SharedPreferences.Editor sharedPreferencesEditorMock;

	private MockedStatic<CatrobatWebClientKt> catrobatWebClientMock;

	@Before
	public void setUp() {
		catrobatWebClientMock = mockStatic(CatrobatWebClientKt.class);
		taskListenerMock = mock(TaskListener.class);
		okHttpClientMock = mock(OkHttpClient.class);
		SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
		sharedPreferencesEditorMock = mock(SharedPreferences.Editor.class);
		when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
		when(sharedPreferencesEditorMock.putString(anyString(), anyString())).thenReturn(sharedPreferencesEditorMock);
		authenticatorSpy = Mockito.spy(new ServerAuthenticator(USERNAME, PASSWORD, TOKEN, okHttpClientMock, BASE_URL_TEST_HTTPS, sharedPreferencesMock, taskListenerMock));
	}

	@After
	public void tearDown() {
		catrobatWebClientMock.close();
	}

	@Test
	public void testPerformCatrobatRegisterInit() {
		Mockito.doNothing().when(authenticatorSpy).performTask(anyString(), anyInt());
		authenticatorSpy.performCatrobatRegister(EMAIL, LANGUAGE, COUNTRY);
		verify(authenticatorSpy, times(1)).performTask(BASE_URL_TEST_HTTPS + REGISTRATION_URL_APPENDING, SERVER_RESPONSE_REGISTER_OK);

		verify(taskListenerMock, never()).onError(anyInt(), anyString());
		HashMap<String, String> expectedMap = new HashMap<>();
		expectedMap.put(CATROBAT_USERNAME_KEY, USERNAME);
		expectedMap.put(CATROBAT_PASSWORD_KEY, PASSWORD);
		expectedMap.put(CATROBAT_EMAIL_KEY, EMAIL);
		expectedMap.put(Constants.TOKEN, TOKEN);
		expectedMap.put(CATROBAT_COUNTRY_KEY, COUNTRY);
		expectedMap.put(DEVICE_LANGUAGE, LANGUAGE);
		assertEquals(expectedMap, authenticatorSpy.getPostValues());
	}

	@Test
	public void testPerformCatrobatLoginInit() {
		Mockito.doNothing().when(authenticatorSpy).performTask(anyString(), anyInt());
		authenticatorSpy.performCatrobatLogin();
		verify(authenticatorSpy, times(1)).performTask(BASE_URL_TEST_HTTPS + LOGIN_URL_APPENDING, SERVER_RESPONSE_TOKEN_OK);

		verify(taskListenerMock, never()).onError(anyInt(), anyString());
		HashMap<String, String> expectedMap = new HashMap<>();
		expectedMap.put(CATROBAT_USERNAME_KEY, USERNAME);
		expectedMap.put(CATROBAT_PASSWORD_KEY, PASSWORD);
		expectedMap.put(Constants.TOKEN, TOKEN);
		assertEquals(expectedMap, authenticatorSpy.getPostValues());
	}

	@Test
	public void testWhenServerConnectionFailsOnTaskFailedCalled() {
		Request requestMock = Mockito.mock(Request.class);
		Mockito.when(createFormEncodedRequest(anyMap(), anyString())).thenReturn(requestMock);

		int expectedStatusCode = 0;
		catrobatWebClientMock.when(() -> CatrobatWebClientKt.performCallWith(okHttpClientMock, any(Request.class))).thenThrow(new WebConnectionException(expectedStatusCode, "any string"));

		authenticatorSpy.performTask(BASE_URL_TEST_HTTPS, 0);
		verify(taskListenerMock, times(1)).onError(expectedStatusCode, null);
		verifyNoMoreInteractions(taskListenerMock);
	}

	@Test
	public void testInvalidResponseOnTaskFailedCalled() throws Exception {
		String responseString = "response";

		Request requestMock = Mockito.mock(Request.class);
		Mockito.when(createFormEncodedRequest(anyMap(), anyString())).thenReturn(requestMock);
		Mockito.when(CatrobatWebClientKt.performCallWith(okHttpClientMock, requestMock)).thenReturn(responseString);
		try (MockedConstruction<JSONObject> jsonObjectMock =
				Mockito.mockConstruction(JSONObject.class, (mock, object) ->
						when(mock.optString(anyString())).thenReturn("message"))) {
			doReturn(true).when(authenticatorSpy).isInvalidResponse(eq(0), any(JSONObject.class));
			authenticatorSpy.performTask(BASE_URL_TEST_HTTPS, 0);
			verify(taskListenerMock, times(1)).onError(0, "message");
			verifyNoMoreInteractions(taskListenerMock);
			JSONObject constructed = jsonObjectMock.constructed().get(0);
			verify(constructed, times(1)).optString(JSON_ANSWER);
			verify(constructed, times(1)).optInt(JSON_STATUS_CODE);
			verifyNoMoreInteractions(constructed);
		}
	}

	@Test
	public void testValidResponseUpdateSharedPreferences() throws Exception {
		Request requestMock = Mockito.mock(Request.class);
		Mockito.when(createFormEncodedRequest(anyMap(), anyString())).thenReturn(requestMock);
		Mockito.when(CatrobatWebClientKt.performCallWith(okHttpClientMock, requestMock)).thenReturn("");

		String expectedToken = "any TOKEN";
		String expectedEmail = "random EMAIL";

		try (MockedConstruction<JSONObject> jsonObjectMock = Mockito.mockConstruction(JSONObject.class, (mock, object) -> {
			when(mock.optString(Constants.TOKEN)).thenReturn(expectedToken);
			when(mock.optString(Constants.EMAIL)).thenReturn(expectedEmail);
		})) {
			doReturn(false).when(authenticatorSpy).isInvalidResponse(eq(0), any(JSONObject.class));
			authenticatorSpy.performTask(BASE_URL_TEST_HTTPS, 0);

			verify(taskListenerMock, atLeastOnce()).onSuccess();
			verifyNoMoreInteractions(taskListenerMock);

			verify(sharedPreferencesEditorMock, times(1)).putString(Constants.TOKEN, expectedToken);
			verify(sharedPreferencesEditorMock, times(1)).putString(Constants.USERNAME, USERNAME);
			verify(sharedPreferencesEditorMock, times(1)).putString(Constants.EMAIL, expectedEmail);
			verify(sharedPreferencesEditorMock, times(1)).apply();
			verifyNoMoreInteractions(sharedPreferencesEditorMock);
		}
	}

	@Test
	public void testWrongStateIsInvalidResponseMethod() {
		JSONObject responseJsonObjectMock = Mockito.mock(JSONObject.class);
		when(responseJsonObjectMock.optInt(JSON_STATUS_CODE)).thenReturn(-1);
		assertTrue(authenticatorSpy.isInvalidResponse(0, responseJsonObjectMock));
	}

	@Test
	public void testWrongTokenIsInvalidResponseMethod() {
		JSONObject responseJsonObjectMock = Mockito.mock(JSONObject.class);
		when(responseJsonObjectMock.optInt(JSON_STATUS_CODE)).thenReturn(0);
		when(responseJsonObjectMock.optString(JSON_TOKEN)).thenReturn("invalid");
		assertTrue(authenticatorSpy.isInvalidResponse(0, responseJsonObjectMock));
	}

	@Test
	public void testValidResponse() {
		JSONObject responseJsonObjectMock = Mockito.mock(JSONObject.class);
		when(responseJsonObjectMock.optInt(JSON_STATUS_CODE)).thenReturn(0);
		String validToken = StringUtils.repeat("1", TOKEN_LENGTH);
		when(responseJsonObjectMock.optString(JSON_TOKEN)).thenReturn(validToken);
		assertFalse(authenticatorSpy.isInvalidResponse(0, responseJsonObjectMock));
	}
}
