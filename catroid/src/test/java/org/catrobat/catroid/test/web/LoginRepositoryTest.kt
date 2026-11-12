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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.catrobat.catroid.retrofit.AuthService
import org.catrobat.catroid.retrofit.TokenRefresher
import org.catrobat.catroid.retrofit.models.AuthResponse
import org.catrobat.catroid.retrofit.models.RefreshRequest
import org.catrobat.catroid.web.JwtTokenStore
import org.catrobat.catroid.web.LoginRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@RunWith(JUnit4::class)
class LoginRepositoryTest {

    private lateinit var authService: AuthService
    private lateinit var tokenStore: JwtTokenStore
    private lateinit var loginRepository: LoginRepository
    private lateinit var refresher: TokenRefresher

    @Before
    fun setUp() {
        authService = mockk(relaxed = true)
        tokenStore = mockk(relaxed = true)
        refresher = mockk()
        loginRepository = LoginRepository(authService, tokenStore, refresher)
    }

    @Test
    fun `login stores tokens on success`() = runBlocking {
        val authResponse = AuthResponse("access-token-123", "refresh-token-456")
        coEvery { authService.login(any()) } returns authResponse

        val result = loginRepository.login("testuser", "password123")

        assertTrue(result.isSuccess)
        verify { tokenStore.setTokens("access-token-123", "refresh-token-456") }
    }

    @Test
    fun `login returns failure on exception`() = runBlocking {
        coEvery { authService.login(any()) } throws RuntimeException("Network error")

        val result = loginRepository.login("testuser", "password123")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `loginWithGoogle stores tokens on success`() = runBlocking {
        val authResponse = AuthResponse("google-access", "google-refresh")
        coEvery { authService.oauthLogin(any()) } returns authResponse

        val result = loginRepository.loginWithGoogle("google-id-token")

        assertTrue(result.isSuccess)
        verify { tokenStore.setTokens("google-access", "google-refresh") }
    }

    @Test
    fun `wrong credentials are reported with their status`() = runBlocking {
        coEvery { authService.login(any()) } throws httpError(401, """{"code":401,"message":"Invalid credentials."}""")

        val refusal = loginRepository.login("testuser", "wrong").exceptionOrNull() as LoginRepository.LoginRefused

        assertEquals(401, refusal.status)
        assertNull(refusal.errorCode)
    }

    @Test
    fun `a suspended account is reported with the server's error code`() = runBlocking {
        coEvery { authService.login(any()) } throws httpError(
            401, """{"code":401,"message":"Account suspended.","error_code":"account_suspended"}"""
        )

        val refusal = loginRepository.login("testuser", "password").exceptionOrNull() as LoginRepository.LoginRefused

        assertEquals("account_suspended", refusal.errorCode)
    }

    @Test
    fun `an HTML error page is still a refusal`() = runBlocking {
        coEvery { authService.login(any()) } throws httpError(502, "<html>Bad Gateway</html>")

        val refusal = loginRepository.login("testuser", "password").exceptionOrNull() as LoginRepository.LoginRefused

        assertEquals(502, refusal.status)
    }

    private fun httpError(code: Int, body: String) = HttpException(
        Response.error<Any>(code, ResponseBody.create(MediaType.parse("application/json"), body))
    )

    @Test
    fun `validateToken is true when the server accepts the token`() = runBlocking {
        every { tokenStore.getAccessToken() } returns "token"
        coEvery { authService.checkToken(any()) } returns Response.success(Unit)

        assertEquals(true, loginRepository.validateToken())
    }

    @Test
    fun `validateToken is false when the server rejects the token`() = runBlocking {
        every { tokenStore.getAccessToken() } returns "token"
        coEvery { authService.checkToken(any()) } returns
            Response.error(401, ResponseBody.create(null, ""))
        every { refresher.refresh("token") } returns TokenRefresher.Outcome.REJECTED

        assertEquals(false, loginRepository.validateToken())
    }

    @Test
    fun `validateToken is unknown when offline`() = runBlocking {
        every { tokenStore.getAccessToken() } returns "token"
        coEvery { authService.checkToken(any()) } throws IOException("offline")

        assertNull(loginRepository.validateToken())
    }

    @Test
    fun `validateToken is unknown on server error`() = runBlocking {
        every { tokenStore.getAccessToken() } returns "token"
        coEvery { authService.checkToken(any()) } returns
            Response.error(503, ResponseBody.create(null, ""))

        assertNull(loginRepository.validateToken())
    }

    @Test
    fun `isLoggedIn delegates to tokenStore`() {
        every { tokenStore.isLoggedIn() } returns true
        assertTrue(loginRepository.isLoggedIn())

        every { tokenStore.isLoggedIn() } returns false
        assertFalse(loginRepository.isLoggedIn())
    }

    @Test
    fun `validateToken refreshes an expired access token`() = runBlocking {
        every { tokenStore.getAccessToken() } returns "expired"
        coEvery { authService.checkToken(any()) } returns
            Response.error(401, ResponseBody.create(null, ""))
        every { refresher.refresh("expired") } returns TokenRefresher.Outcome.REFRESHED

        assertEquals(true, loginRepository.validateToken())
    }

    @Test
    fun `validateToken is unknown when the refresh cannot reach the server`() = runBlocking {
        every { tokenStore.getAccessToken() } returns "expired"
        coEvery { authService.checkToken(any()) } returns
            Response.error(401, ResponseBody.create(null, ""))
        every { refresher.refresh("expired") } returns TokenRefresher.Outcome.UNREACHABLE

        assertNull(loginRepository.validateToken())
    }

    @Test
    fun `revoke sends the refresh token`() = runBlocking {
        coEvery { authService.logout(any(), any()) } returns Response.success(Unit)

        loginRepository.revoke("access", "refresh")

        coVerify(exactly = 1) { authService.logout("Bearer access", "refresh") }
    }

    @Test
    fun `revoke with an expired access token revokes the refreshed session`() = runBlocking {
        coEvery { authService.logout("Bearer expired", "refresh") } returns
            Response.error(401, ResponseBody.create(null, ""))
        coEvery { authService.refreshToken(RefreshRequest("refresh")) } returns AuthResponse("fresh", "fresh-refresh")
        coEvery { authService.logout("Bearer fresh", "fresh-refresh") } returns Response.success(Unit)

        loginRepository.revoke("expired", "refresh")

        coVerify { authService.logout("Bearer fresh", "fresh-refresh") }
    }

    @Test
    fun `revoke never throws`() = runBlocking {
        coEvery { authService.logout(any(), any()) } throws IOException("offline")

        loginRepository.revoke("access", "refresh")
    }
}
