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
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.koin.testModules
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.User
import org.catrobat.catroid.testsuites.annotations.Cat.OutgoingNetworkTests
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
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

    private var username = "catroweb"
    private var invalidUsername = "InvalidUser"
    private var password = "catroweb"
    private var wrongPassword = "WrongPassword"
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences

    private val webServer: WebService by inject()

    @Before
    fun setUp() {
        stopKoin()
        startKoin { modules(testModules) }
        context = InstrumentationRegistry.getInstrumentation().context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testLoginWithInvalidCredentials() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val actualResponse = webServer.login("Bearer $token", User(invalidUsername, password)).execute()
        assertEquals(actualResponse.code(), STATUS_CODE_INVALID_CREDENTIALS)
    }

    @Test
    fun testLoginWrongPassword() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val actualResponse = webServer.login("Bearer $token", User(username, wrongPassword)).execute()
        assertEquals(actualResponse.code(), STATUS_CODE_INVALID_CREDENTIALS)
    }

    @Test
    fun testLoginOk() {
        var token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.login("Bearer $token", User(username, password)).execute()

        assertEquals(response.code(), SERVER_RESPONSE_TOKEN_OK)

        val responseBody = response.body()
        assertNotNull(responseBody)
        assertNotNull(responseBody?.token)

        token = responseBody?.token
        val responseCheckToken = webServer.checkToken("Bearer $token").execute()
        assertEquals(responseCheckToken.code(), SERVER_RESPONSE_TOKEN_OK)
    }
}
