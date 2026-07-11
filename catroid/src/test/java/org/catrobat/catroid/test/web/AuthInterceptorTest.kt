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

package org.catrobat.catroid.test.web

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.catrobat.catroid.retrofit.AuthInterceptor
import org.catrobat.catroid.web.JwtTokenStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AuthInterceptorTest {

    private lateinit var tokenStore: JwtTokenStore
    private lateinit var interceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain

    @Before
    fun setUp() {
        tokenStore = mockk(relaxed = true)
        interceptor = AuthInterceptor(tokenStore, "https://share.catrobat.org/api/")
        chain = mockk(relaxed = true)
    }

    @Test
    fun `attaches Bearer header when token exists`() {
        every { tokenStore.getAccessToken() } returns "test-jwt-token"

        val request = Request.Builder().url("https://share.catrobat.org/api/projects").build()
        every { chain.request() } returns request

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(null, ""))
            .build()
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == "Bearer test-jwt-token"
            })
        }
    }

    @Test
    fun `does not attach header when no token`() {
        every { tokenStore.getAccessToken() } returns null

        val request = Request.Builder().url("https://share.catrobat.org/api/projects").build()
        every { chain.request() } returns request

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(null, ""))
            .build()
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == null
            })
        }
    }

    @Test
    fun `retries with new token when another thread already refreshed`() {
        every { tokenStore.getAccessToken() } returns "old-token" andThen "new-token-from-other-thread"
        every { tokenStore.getRefreshToken() } returns "refresh-token"

        val request = Request.Builder().url("https://share.catrobat.org/api/projects").build()
        every { chain.request() } returns request

        val unauthorizedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body(ResponseBody.create(null, ""))
            .build()

        val successResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(null, ""))
            .build()

        every { chain.proceed(any()) } returns unauthorizedResponse andThen successResponse

        val result = interceptor.intercept(chain)

        assertEquals(200, result.code())
        verify {
            chain.proceed(match { req ->
                req.header("Authorization") == "Bearer new-token-from-other-thread"
            })
        }
    }

    @Test
    fun `clears tokens when another thread cleared token during 401 handling`() {
        every { tokenStore.getAccessToken() } returns "old-token" andThen null
        every { tokenStore.getRefreshToken() } returns null

        val request = Request.Builder().url("https://share.catrobat.org/api/projects").build()
        every { chain.request() } returns request

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body(ResponseBody.create(null, ""))
            .build()
        every { chain.proceed(any()) } returns response

        val result = interceptor.intercept(chain)

        assertEquals(401, result.code())
        verify { tokenStore.clearTokens() }
    }

    @Test
    fun `clears tokens on 401 when no refresh token`() {
        every { tokenStore.getAccessToken() } returns "expired-token"
        every { tokenStore.getRefreshToken() } returns null

        val request = Request.Builder().url("https://share.catrobat.org/api/projects").build()
        every { chain.request() } returns request

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body(ResponseBody.create(null, ""))
            .build()
        every { chain.proceed(any()) } returns response

        val result = interceptor.intercept(chain)

        assertEquals(401, result.code())
        verify { tokenStore.clearTokens() }
    }
}
