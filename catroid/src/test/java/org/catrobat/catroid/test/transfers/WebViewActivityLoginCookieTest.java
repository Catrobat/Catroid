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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.WebViewActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JUnit4.class)
public class WebViewActivityLoginCookieTest {

	private CookieManager cookieManagerMock;
	private String urlStub = Constants.MAIN_URL_HTTPS;
	private static final String EXPIRED_COOKIE = "BEARER=; Max-Age=0; HttpOnly; Secure; Path=/; SameSite=Strict";
	private String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature";

	@Before
	public void setUp() {
		cookieManagerMock = mock(CookieManager.class);
	}

	@Test
	public void testSetLoginCookiesWithJwtToken() {
		WebViewActivity.setLoginCookies(urlStub, cookieManagerMock, jwtToken);
		String expectedCookie = "BEARER=" + jwtToken + "; HttpOnly; Secure; Path=/; SameSite=Strict";
		verify(cookieManagerMock, times(1)).setCookie(urlStub, expectedCookie);
	}

	@Test
	public void testSetLoginCookiesWithNullTokenRemovesTheCookie() {
		WebViewActivity.setLoginCookies(urlStub, cookieManagerMock, null);
		verify(cookieManagerMock).setCookie(urlStub, EXPIRED_COOKIE);
		verifyNoMoreInteractions(cookieManagerMock);
	}

	@Test
	public void testSetLoginCookiesWithEmptyTokenRemovesTheCookie() {
		WebViewActivity.setLoginCookies(urlStub, cookieManagerMock, "");
		verify(cookieManagerMock).setCookie(urlStub, EXPIRED_COOKIE);
		verifyNoMoreInteractions(cookieManagerMock);
	}

	@Test
	public void testIsLogoutUrlForThemedShareLogoutRoute() {
		assertTrue(WebViewActivity.isLogoutUrl(Constants.MAIN_URL_HTTPS + "/pocketcode/logout"));
		assertTrue(WebViewActivity.isLogoutUrl(Constants.MAIN_URL_HTTPS + "/luna/logout/"));
		assertTrue(WebViewActivity.isLogoutUrl(Constants.MAIN_URL_HTTPS + "/app/logout?next=%2F"));
	}

	@Test
	public void testIsLogoutUrlIgnoresOtherPagesAndHosts() {
		assertFalse(WebViewActivity.isLogoutUrl(Constants.MAIN_URL_HTTPS + "/pocketcode/logout-help"));
		assertFalse(WebViewActivity.isLogoutUrl(Constants.MAIN_URL_HTTPS + "/app/login"));
		assertFalse(WebViewActivity.isLogoutUrl("https://example.org/pocketcode/logout"));
		assertFalse(WebViewActivity.isLogoutUrl(null));
	}

	@Test
	public void testSetLoginCookiesNeverLeavesTheShareHost() {
		WebViewActivity.setLoginCookies("https://share.catrob.at/app/", cookieManagerMock, jwtToken);
		WebViewActivity.setLoginCookies("https://share.catrobat.org.example.com/", cookieManagerMock, jwtToken);
		WebViewActivity.setLoginCookies("http://share.catrobat.org/", cookieManagerMock, jwtToken);
		WebViewActivity.setLoginCookies(null, cookieManagerMock, jwtToken);
		verifyNoMoreInteractions(cookieManagerMock);
	}

	@Test
	public void testIsShareUrl() {
		assertTrue(WebViewActivity.isShareUrl(Constants.MAIN_URL_HTTPS + "/pocketcode/project/1"));
		assertTrue(WebViewActivity.isShareUrl(Constants.MAIN_URL_HTTPS.toUpperCase(java.util.Locale.ROOT) + "/app/"));
		assertFalse(WebViewActivity.isShareUrl("https://share.catrob.at/app/"));
		assertFalse(WebViewActivity.isShareUrl("https://share.catrobat.org.example.com/"));
		assertFalse(WebViewActivity.isShareUrl("https://share.catrobat.org:8443/"));
		assertFalse(WebViewActivity.isShareUrl("not a url"));
	}
}
