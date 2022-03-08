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
package org.catrobat.catroid.test.utiltests

import android.content.SharedPreferences
import android.webkit.CookieManager
import org.catrobat.catroid.common.Constants.AUTHENTICATION_COOKIE_NAME
import org.catrobat.catroid.common.Constants.NO_TOKEN
import org.catrobat.catroid.common.Constants.REFRESH_TOKEN
import org.catrobat.catroid.common.Constants.REFRESH_TOKEN_COOKIE_NAME
import org.catrobat.catroid.common.Constants.TOKEN
import org.catrobat.catroid.utils.Utils.setLoginCookies
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

@RunWith(JUnit4::class)
class LoginCookieTest {
    private lateinit var cookieManagerMock: CookieManager
    private lateinit var sharedPreferencesMock: SharedPreferences
    private val url = "url"

    @Before
    fun setUp() {
        cookieManagerMock = mock(CookieManager::class.java)
        sharedPreferencesMock = mock(SharedPreferences::class.java)
    }

    @Test
    fun testNoTokenAvailable() {
        `when`(sharedPreferencesMock.getString(eq(TOKEN), eq(NO_TOKEN))).thenReturn(NO_TOKEN)
        `when`(sharedPreferencesMock.getString(eq(REFRESH_TOKEN), eq(NO_TOKEN))).thenReturn(REFRESH_TOKEN)

        setLoginCookies(url, sharedPreferencesMock, cookieManagerMock)
        verifyNoMoreInteractions(cookieManagerMock)
    }

    @Test
    fun testNoRefreshTokenAvailable() {
        `when`(sharedPreferencesMock.getString(eq(TOKEN), eq(NO_TOKEN))).thenReturn(TOKEN)
        `when`(sharedPreferencesMock.getString(eq(REFRESH_TOKEN), eq(NO_TOKEN))).thenReturn(NO_TOKEN)

        setLoginCookies(url, sharedPreferencesMock, cookieManagerMock)
        verifyNoMoreInteractions(cookieManagerMock)
    }

    @Test
    fun testSuccess() {
        `when`(sharedPreferencesMock.getString(eq(TOKEN), eq(NO_TOKEN))).thenReturn(TOKEN)
        `when`(sharedPreferencesMock.getString(eq(REFRESH_TOKEN), eq(NO_TOKEN))).thenReturn(REFRESH_TOKEN)

        setLoginCookies(url, sharedPreferencesMock, cookieManagerMock)
        verify(cookieManagerMock, times(1))?.setCookie(eq(url), eq(generateCookie(AUTHENTICATION_COOKIE_NAME, TOKEN)))
        verify(cookieManagerMock, times(1))?.setCookie(eq(url), eq(generateCookie(REFRESH_TOKEN_COOKIE_NAME, REFRESH_TOKEN)))
        verifyNoMoreInteractions(cookieManagerMock)
    }

    private fun generateCookie(name: String, value: String): String = "$name=$value"
}
