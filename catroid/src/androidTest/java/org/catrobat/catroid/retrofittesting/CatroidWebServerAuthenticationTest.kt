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

package org.catrobat.catroid.retrofittesting

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.squareup.moshi.Moshi
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.GOOGLE_PROVIDER
import org.catrobat.catroid.koin.testModules
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.DeprecatedToken
import org.catrobat.catroid.retrofit.models.LoginUser
import org.catrobat.catroid.retrofit.models.OAuthLogin
import org.catrobat.catroid.retrofit.models.RefreshToken
import org.catrobat.catroid.retrofit.models.RegisterFailedResponse
import org.catrobat.catroid.retrofit.models.RegisterUser
import org.catrobat.catroid.testsuites.annotations.Cat.OutgoingNetworkTests
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_INVALID_UPLOAD_TOKEN
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_REGISTER_OK
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_REGISTER_UNPROCESSABLE_ENTITY
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_USER_DELETED
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.junit.MockitoJUnitRunner
import java.lang.System.currentTimeMillis

@RunWith(MockitoJUnitRunner::class)
@Category(OutgoingNetworkTests::class)
class CatroidWebServerAuthenticationTest : KoinTest {

    companion object {
        private const val STATUS_CODE_INVALID_CREDENTIALS = 401
    }

    private lateinit var newEmail: String
    private lateinit var newUserName: String
    private var email = "catroweb@localhost.at"
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

        newUserName = "APIUser" + currentTimeMillis()
        newEmail = "$newUserName@api.at"

        context = getInstrumentation().context
        sharedPreferences = getDefaultSharedPreferences(context)
        initMocks(this)
    }

    @Test
    fun testLoginWithInvalidCredentials() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val actualResponse = webServer.login("Bearer $token", LoginUser(invalidUsername, password)).execute()
        assertEquals(actualResponse.code(), STATUS_CODE_INVALID_CREDENTIALS)
    }

    @Test
    fun testLoginWrongPassword() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val actualResponse = webServer.login("Bearer $token", LoginUser(username, wrongPassword)).execute()
        assertEquals(actualResponse.code(), STATUS_CODE_INVALID_CREDENTIALS)
    }

    @Test
    fun testLoginOk() {
        var token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.login("Bearer $token", LoginUser(username, password)).execute()

        assertEquals(response.code(), SERVER_RESPONSE_TOKEN_OK)

        val responseBody = response.body()
        assertNotNull(responseBody)
        assertNotNull(responseBody?.token)
        assertNotNull(responseBody?.refresh_token)

        token = responseBody?.token
        val responseCheckToken = webServer.checkToken("Bearer $token").execute()
        assertEquals(responseCheckToken.code(), SERVER_RESPONSE_TOKEN_OK)
    }

    @Test
    fun testRefreshTokenOk() {
        var token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.login("Bearer $token", LoginUser(username, password)).execute()

        token = response.body()?.token
        val refreshToken = response.body()?.refresh_token.orEmpty()

        val responseRefreshToken = webServer.refreshToken("Bearer $token", RefreshToken(refreshToken)).execute()
        assertEquals(responseRefreshToken.code(), SERVER_RESPONSE_TOKEN_OK)

        val responseBody = responseRefreshToken.body()
        assertNotNull(responseBody)
        assertNotNull(responseBody?.token)
        assertNotNull(responseBody?.refresh_token)

        token = responseBody?.token
        val responseCheckToken = webServer.checkToken("Bearer $token").execute()
        assertEquals(responseCheckToken.code(), SERVER_RESPONSE_TOKEN_OK)
    }

    @Test
    fun testRefreshTokenExpired() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val refreshToken = "7edb0420a64bfc901caffb0a34de7bbd6fe11b316d3bde2796d16db42ce939abcccca65091a9f914ec031d278852e8f62b0b3374c988147b5db4d1e9ae71fe4a"

        val responseRefreshToken = webServer.refreshToken("Bearer $token", RefreshToken(refreshToken)).execute()
        assertEquals(responseRefreshToken.code(), SERVER_RESPONSE_INVALID_UPLOAD_TOKEN)
    }

    @Test
    fun testOAuthInvalidToken() {
        val idToken = "eyJhbaciOiJSUzI1NiIsImtpdCI6IjcyOTE4OTQ1MGQ0OTAyODU3MDQyNTI2NmYwM2U3MzdmNDVhZjI5MzIiLCJ0eXAiOiJKV1QifQ"
        val responseOAuthToken = webServer.oAuthLogin(OAuthLogin(idToken, GOOGLE_PROVIDER)).execute()
        assertEquals(responseOAuthToken.code(), SERVER_RESPONSE_INVALID_UPLOAD_TOKEN)
    }

    @Test
    fun testRegistrationOk() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.register("Bearer $token", RegisterUser(true, newEmail, newUserName, password)).execute()

        val responseBody = response.body()
        assertNotNull(responseBody)
        assertNotNull(responseBody?.token)
        assertNotNull(responseBody?.refresh_token)
        assertEquals(response.code(), SERVER_RESPONSE_REGISTER_OK)

        deleteUser(responseBody?.token)
    }

    @Test
    fun testRegisterWithNewUserButExistingEmail() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.register("Bearer $token", RegisterUser(true, email, newUserName, password)).execute()

        assertEquals(response.code(), SERVER_RESPONSE_REGISTER_UNPROCESSABLE_ENTITY)

        assertNotNull(response.errorBody())
        val errorBody = response.errorBody()?.string()
        assertNotNull(parseRegisterErrorMessage(errorBody)?.email)
    }

    @Test
    fun testRegisterWithExistingUserButNewEmail() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.register("Bearer $token", RegisterUser(true, newEmail, username, password)).execute()

        assertEquals(response.code(), SERVER_RESPONSE_REGISTER_UNPROCESSABLE_ENTITY)

        assertNotNull(response.errorBody())
        val errorBody = response.errorBody()?.string()
        assertNotNull(parseRegisterErrorMessage(errorBody)?.username)
    }

    @Test
    fun testRegisterAndLogin() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val registrationResponse = webServer.register("Bearer $token", RegisterUser(true, newEmail, newUserName, password)).execute()

        val loginResponse = webServer.login("Bearer $token", LoginUser(newUserName, password)).execute()

        assertEquals(registrationResponse.code(), SERVER_RESPONSE_REGISTER_OK)
        assertEquals(loginResponse.code(), SERVER_RESPONSE_TOKEN_OK)

        deleteUser(loginResponse.body()?.token)
    }

    @Test
    fun testUpgradeExpiredToken() {
        val token = "ee447d8d9013f72ba8f170a48efbedbf"
        val upgradeResponse = webServer.upgradeToken(DeprecatedToken(token)).execute()
        assertEquals(upgradeResponse.code(), SERVER_RESPONSE_INVALID_UPLOAD_TOKEN)
    }

    @Test
    fun testExpireTokenOk() {
        var token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val response = webServer.login("Bearer $token", LoginUser(username, password)).execute()

        token = response.body()?.token
        val refreshToken = response.body()?.refresh_token.orEmpty()

        val responseExpireToken = webServer.expireToken("Bearer $token", refreshToken).execute()
        assertEquals(responseExpireToken.code(), SERVER_RESPONSE_TOKEN_OK)

        val responseRefreshToken = webServer.refreshToken("Bearer $token", RefreshToken(refreshToken)).execute()
        assertEquals(responseRefreshToken.code(), SERVER_RESPONSE_INVALID_UPLOAD_TOKEN)
    }

    @Test
    fun testExpireTokenWithExpiredToken() {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val refreshToken = "7edb0420a64bfc901caffb0a34de7bbd6fe11b316d3bde2796d16db42ce939abcccca65091a9f914ec031d278852e8f62b0b3374c988147b5db4d1e9ae71fe4a"

        val responseExpireToken = webServer.expireToken("Bearer $token", refreshToken).execute()
        assertEquals(responseExpireToken.code(), SERVER_RESPONSE_INVALID_UPLOAD_TOKEN)
    }

    @Test
    fun testGetUserProjects() {
        var token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val loginResponse = webServer.login("Bearer $token", LoginUser(username, password))
            .execute()
        token = loginResponse.body()?.token
        val response = webServer.getUserProjects("Bearer $token").execute()

        assertEquals(response.code(), SERVER_RESPONSE_TOKEN_OK)
    }

    private fun parseRegisterErrorMessage(errorBody: String?) =
        Moshi.Builder().build().adapter<RegisterFailedResponse>(RegisterFailedResponse::class.java).fromJson(errorBody)

    private fun deleteUser(token: String?) {
        val response = webServer.deleteUser("Bearer $token").execute()
        assertEquals(response.code(), SERVER_RESPONSE_USER_DELETED)
    }
}
