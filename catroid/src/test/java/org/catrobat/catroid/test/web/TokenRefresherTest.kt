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

import android.content.Context
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.catrobat.catroid.retrofit.TokenRefresher
import org.catrobat.catroid.retrofit.TokenRefresher.Outcome
import org.catrobat.catroid.web.JwtTokenStore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
class TokenRefresherTest {

    private val server = MockWebServer()
    private lateinit var tokenStore: JwtTokenStore
    private lateinit var refresher: TokenRefresher

    @Before
    fun setUp() {
        server.start()
        val prefs = RuntimeEnvironment.getApplication().getSharedPreferences("jwt-test", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        tokenStore = JwtTokenStore { prefs }
        tokenStore.setTokens("old-access", "old-refresh")
        refresher = TokenRefresher(tokenStore, server.url("/api/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `stores the new pair`() {
        server.enqueue(newPair())

        assertEquals(Outcome.REFRESHED, refresher.refresh("old-access"))
        assertTokens("new-access", "new-refresh")
    }

    @Test
    fun `parallel callers refresh once, because the refresh token is single-use`() {
        server.enqueue(newPair())

        val pool = Executors.newFixedThreadPool(2)
        val outcomes = pool.invokeAll(List(2) { Callable { refresher.refresh("old-access") } }).map { it.get() }
        pool.shutdown()

        assertEquals(listOf(Outcome.REFRESHED, Outcome.REFRESHED), outcomes)
        assertEquals(1, server.requestCount)
        assertTokens("new-access", "new-refresh")
    }

    @Test
    fun `keeps the session on a server error`() {
        server.enqueue(MockResponse().setResponseCode(503))

        assertEquals(Outcome.UNREACHABLE, refresher.refresh("old-access"))
        assertTokens("old-access", "old-refresh")
    }

    @Test
    fun `keeps the session on an unexpected body`() {
        server.enqueue(MockResponse().setBody("""{"token":"only-access"}"""))

        assertEquals(Outcome.UNREACHABLE, refresher.refresh("old-access"))
        assertTokens("old-access", "old-refresh")
    }

    @Test
    fun `ends the session when the refresh token is rejected`() {
        server.enqueue(MockResponse().setResponseCode(401))

        assertEquals(Outcome.REJECTED, refresher.refresh("old-access"))
        assertTokens(null, null)
    }

    @Test
    fun `a logout during the refresh stays a logout`() {
        answerRefreshAfter { tokenStore.clearTokens() }

        assertEquals(Outcome.REJECTED, refresher.refresh("old-access"))
        assertTokens(null, null)
    }

    @Test
    fun `a login as someone else during the refresh keeps the new account`() {
        answerRefreshAfter { tokenStore.setTokens("other-access", "other-refresh") }

        refresher.refresh("old-access")
        assertTokens("other-access", "other-refresh")
    }

    @Test
    fun `a rejected refresh does not log out an account that signed in meanwhile`() {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                tokenStore.setTokens("other-access", "other-refresh")
                return MockResponse().setResponseCode(401)
            }
        }

        assertEquals(Outcome.REJECTED, refresher.refresh("old-access"))
        assertTokens("other-access", "other-refresh")
    }

    private fun answerRefreshAfter(sessionChange: () -> Unit) {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                sessionChange()
                return newPair()
            }
        }
    }

    private fun newPair() = MockResponse().setBody("""{"token":"new-access","refresh_token":"new-refresh"}""")

    private fun assertTokens(access: String?, refresh: String?) {
        assertEquals(access, tokenStore.getAccessToken())
        assertEquals(refresh, tokenStore.getRefreshToken())
    }
}
