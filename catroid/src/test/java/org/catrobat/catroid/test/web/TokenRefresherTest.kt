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
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.catrobat.catroid.retrofit.TokenRefresher
import org.catrobat.catroid.retrofit.TokenRefresher.Outcome
import org.catrobat.catroid.web.JwtTokenStore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@RunWith(JUnit4::class)
class TokenRefresherTest {

    private val server = MockWebServer()
    private lateinit var tokenStore: JwtTokenStore
    private lateinit var refresher: TokenRefresher

    @Before
    fun setUp() {
        server.start()
        tokenStore = mockk(relaxed = true)
        refresher = TokenRefresher(tokenStore, server.url("/api/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `stores the new pair`() {
        storedTokens("old-access", "old-refresh")
        server.enqueue(MockResponse().setBody("""{"token":"new-access","refresh_token":"new-refresh"}"""))

        assertEquals(Outcome.REFRESHED, refresher.refresh("old-access"))
        verify { tokenStore.setTokens("new-access", "new-refresh") }
    }

    @Test
    fun `parallel callers refresh once, because the refresh token is single-use`() {
        var access = "old-access"
        every { tokenStore.getAccessToken() } answers { access }
        every { tokenStore.getRefreshToken() } returns "old-refresh"
        every { tokenStore.setTokens(any(), any()) } answers { access = firstArg() }
        server.enqueue(MockResponse().setBody("""{"token":"new-access","refresh_token":"new-refresh"}"""))

        val pool = Executors.newFixedThreadPool(2)
        val outcomes = pool.invokeAll(List(2) { Callable { refresher.refresh("old-access") } }).map { it.get() }
        pool.shutdown()

        assertEquals(listOf(Outcome.REFRESHED, Outcome.REFRESHED), outcomes)
        assertEquals(1, server.requestCount)
        verify(exactly = 0) { tokenStore.clearTokens() }
    }

    @Test
    fun `keeps the session on a server error`() {
        storedTokens("old-access", "old-refresh")
        server.enqueue(MockResponse().setResponseCode(503))

        assertEquals(Outcome.UNREACHABLE, refresher.refresh("old-access"))
        verify(exactly = 0) { tokenStore.clearTokens() }
    }

    @Test
    fun `keeps the session on an unexpected body`() {
        storedTokens("old-access", "old-refresh")
        server.enqueue(MockResponse().setBody("""{"token":"only-access"}"""))

        assertEquals(Outcome.UNREACHABLE, refresher.refresh("old-access"))
        verify(exactly = 0) { tokenStore.clearTokens() }
    }

    @Test
    fun `ends the session when the refresh token is rejected`() {
        storedTokens("old-access", "old-refresh")
        server.enqueue(MockResponse().setResponseCode(401))

        assertEquals(Outcome.REJECTED, refresher.refresh("old-access"))
        verify { tokenStore.clearTokens() }
    }

    private fun storedTokens(access: String, refresh: String) {
        every { tokenStore.getAccessToken() } returns access
        every { tokenStore.getRefreshToken() } returns refresh
    }
}
