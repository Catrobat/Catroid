/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.test.web

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import org.catrobat.catroid.common.Constants.ERROR_BAD_REQUEST
import org.catrobat.catroid.web.WebConnection
import org.catrobat.catroid.web.WebConnection.WebRequestListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito.doNothing
import org.powermock.api.mockito.PowerMockito.doReturn
import org.powermock.api.mockito.PowerMockito.mock

@RunWith(JUnit4::class)
class WebConnectionTest {
    private lateinit var listener: WebRequestListener
    private lateinit var okHttpClient: OkHttpClient

    companion object {
        private const val BASE_URL_TEST_HTTPS = "https://catroid-test.catrob.at/pocketcode/"
    }

    @Before
    fun setUp() {
        listener = mock(WebRequestListener::class.java)
        okHttpClient = mock(OkHttpClient::class.java)
        doNothing().`when`(listener).onRequestError(anyString())
    }

    @Test
    fun testSendRequestWithIncompleteURL() {
        WebConnection(mock(OkHttpClient::class.java), listener, "https/").sendWebRequest()
        verify(listener, times(1)).onRequestError(ERROR_BAD_REQUEST.toString())
    }

    @Test
    fun testSendRequestWithMalformedUrl() {
        WebConnection(okHttpClient, listener, "test").sendWebRequest()
        verify(listener, times(1)).onRequestError(ERROR_BAD_REQUEST.toString())
    }

    @Test
    fun testSendRequest() {
        val call = mock(Call::class.java)
        doReturn(call).`when`(okHttpClient).newCall(any(Request::class.java))

        WebConnection(okHttpClient, listener, BASE_URL_TEST_HTTPS).sendWebRequest()
        verify(call, times(1)).enqueue(any())
    }
}
