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

package org.catrobat.catroid.test.transfers;

import android.content.SharedPreferences;
import android.webkit.CookieManager;

import org.catrobat.catroid.ui.WebViewActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.catrobat.catroid.common.Constants.NO_TOKEN;
import static org.catrobat.catroid.common.Constants.NO_USERNAME;
import static org.catrobat.catroid.common.Constants.TOKEN;
import static org.catrobat.catroid.common.Constants.TOKEN_COOKIE_NAME;
import static org.catrobat.catroid.common.Constants.USERNAME;
import static org.catrobat.catroid.common.Constants.USERNAME_COOKIE_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class WebViewActivityLoginCookieTest {

	private CookieManager cookieManagerMock;
	private SharedPreferences sharedPreferencesMock;
	private String urlStub = "url";
	private String username = "username #1";
	private String token = "token";

	@Before
	public void setUp() throws Exception {
		cookieManagerMock = Mockito.mock(CookieManager.class);
		sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
	}

	@Test
	public void testNoUserNameAvailable() {
		when(sharedPreferencesMock.getString(eq(USERNAME), eq(NO_USERNAME))).thenReturn(NO_USERNAME);
		when(sharedPreferencesMock.getString(eq(TOKEN), eq(NO_TOKEN))).thenReturn(token);

		WebViewActivity.setLoginCookies(urlStub, sharedPreferencesMock, cookieManagerMock);

		verifyNoMoreInteractions(cookieManagerMock);
	}

	@Test
	public void testNoTokenAvailable() {
		when(sharedPreferencesMock.getString(eq(USERNAME), eq(NO_USERNAME))).thenReturn(username);
		when(sharedPreferencesMock.getString(eq(TOKEN), eq(NO_TOKEN))).thenReturn(NO_TOKEN);

		WebViewActivity.setLoginCookies(urlStub, sharedPreferencesMock, cookieManagerMock);

		verifyNoMoreInteractions(cookieManagerMock);
	}

	@Test
	public void testSuccess() throws UnsupportedEncodingException {
		when(sharedPreferencesMock.getString(eq(USERNAME), eq(NO_USERNAME))).thenReturn(username);
		when(sharedPreferencesMock.getString(eq(TOKEN), eq(NO_TOKEN))).thenReturn(token);

		WebViewActivity.setLoginCookies(urlStub, sharedPreferencesMock, cookieManagerMock);

		verify(cookieManagerMock, times(1))
				.setCookie(eq(urlStub), eq(generateCookie(USERNAME_COOKIE_NAME, URLEncoder.encode(username, "UTF-8"))));
		verify(cookieManagerMock, times(1))
				.setCookie(eq(urlStub), eq(generateCookie(TOKEN_COOKIE_NAME, token)));
		verifyNoMoreInteractions(cookieManagerMock);
	}

	private String generateCookie(String name, String value) {
		return name + "=" + value;
	}
}
