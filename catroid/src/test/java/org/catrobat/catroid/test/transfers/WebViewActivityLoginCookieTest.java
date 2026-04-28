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

package org.catrobat.catroid.test.transfers;

import android.webkit.CookieManager;

import org.catrobat.catroid.ui.WebViewActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JUnit4.class)
public class WebViewActivityLoginCookieTest {

	private CookieManager cookieManagerMock;
	private String urlStub = "https://share.catrobat.org";
	private String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature";

	@Before
	public void setUp() {
		cookieManagerMock = Mockito.mock(CookieManager.class);
	}

	@Test
	public void testSetLoginCookiesWithJwtToken() {
		WebViewActivity.setLoginCookies(urlStub, cookieManagerMock, jwtToken);
		String expectedCookie = "BEARER=" + jwtToken + "; HttpOnly; Secure; Path=/; SameSite=Strict";
		verify(cookieManagerMock, times(1)).setCookie(urlStub, expectedCookie);
	}

	@Test
	public void testSetLoginCookiesWithNullToken() {
		WebViewActivity.setLoginCookies(urlStub, cookieManagerMock, null);
		verifyNoMoreInteractions(cookieManagerMock);
	}

	@Test
	public void testSetLoginCookiesWithEmptyToken() {
		WebViewActivity.setLoginCookies(urlStub, cookieManagerMock, "");
		verifyNoMoreInteractions(cookieManagerMock);
	}

	@Test
	public void testExtractBearerFromCookies() {
		String cookies = "session=abc; BEARER=my-jwt-token; other=value";
		assertEquals("my-jwt-token", WebViewActivity.extractBearerFromCookies(cookies));
	}

	@Test
	public void testExtractBearerFromCookiesNoBearerPresent() {
		String cookies = "session=abc; other=value";
		assertNull(WebViewActivity.extractBearerFromCookies(cookies));
	}

	@Test
	public void testExtractBearerFromNullCookies() {
		assertNull(WebViewActivity.extractBearerFromCookies(null));
	}
}
