/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.retrofittesting

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockWebServer
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.User
import org.catrobat.catroid.testsuites.annotations.Cat.OutgoingNetworkTests
import org.catrobat.catroid.web.ServerAuthenticationConstants.NEW_TOKEN_LENGTH
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@Category(OutgoingNetworkTests::class)
class CatroidWebServerAuthenticationTest : KoinTest {

    companion object {
        private const val STATUS_CODE_INVALID_CREDENTIALS = 401
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var context: Context

    private val webServer: WebService by inject()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        MockitoAnnotations.initMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLoginWithInvalidCredentials() {
        val actualResponse = webServer.login(User("WrongUser", "WrongPassword")).execute()
        assertEquals(actualResponse.code(), STATUS_CODE_INVALID_CREDENTIALS)
    }

    @Test
    fun testLoginAndCheckResponseCode200Returned() {
        // TODO: get other login testuser
        val actualResponse = webServer.login(User("MStoeg", "testPassword")).execute()
        assertTrue(actualResponse.code().toString().containsOkHttpCode())
    }

    @Test
    fun testLoginResponseHasCorrectStructure() {
        // TODO: get other login testuser
        val response = webServer.login(User("MStoeg", "testPassword")).execute().body()
        assertNotNull(response)
        assertNotNull(response?.token)
        // TODO: refresh token is null
        // assertNotNull(response?.refresh_token)
    }

    @Test
    fun testLoginResponseHasCorrectToken() {
        // TODO: get other login testuser
        val response = webServer.login(User("MStoeg", "testPassword")).execute().body()
        assertNotNull(response)
        assertNotNull(response?.token)
        assertEquals(response?.token?.length, NEW_TOKEN_LENGTH)
        // TODO: refresh token is null
        // assertNotNull(response?.refresh_token)
        // assertEquals(response?.refresh_token?.length, NEW_TOKEN_LENGTH)
    }

    private fun String.containsOkHttpCode() = contains(200.toString())
}
