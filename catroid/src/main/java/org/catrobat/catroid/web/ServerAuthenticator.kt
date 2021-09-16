/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.web

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.VisibleForTesting
import okhttp3.OkHttpClient
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.LoginResponse
import org.catrobat.catroid.retrofit.models.User
import org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_COUNTRY_KEY
import org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_EMAIL_KEY
import org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_PASSWORD_KEY
import org.catrobat.catroid.web.ServerAuthenticationConstants.CATROBAT_USERNAME_KEY
import org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_ANSWER
import org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_STATUS_CODE
import org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_TOKEN
import org.catrobat.catroid.web.ServerAuthenticationConstants.NEW_TOKEN_LENGTH
import org.catrobat.catroid.web.ServerAuthenticationConstants.REGISTRATION_URL_APPENDING
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_REGISTER_OK
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import org.catrobat.catroid.web.ServerAuthenticationConstants.TOKEN_LENGTH
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ServerAuthenticator(
    var username: String,
    var password: String,
    private val token: String,
    private val webServer: WebService,
    private val okHttpClient: OkHttpClient,
    private val baseUrl: String,
    val sharedPreferences: SharedPreferences,
    private val taskListener: TaskListener
) {

    @VisibleForTesting
    val postValues = HashMap<String, String>()
    private val tag = ServerAuthenticator::class.java.simpleName

    fun performCatrobatRegister(
        userEmail: String,
        language: String,
        country: String
    ) {
        postValues[CATROBAT_USERNAME_KEY] = username
        postValues[CATROBAT_PASSWORD_KEY] = password
        postValues[CATROBAT_EMAIL_KEY] = userEmail
        if (token != Constants.NO_TOKEN) {
            postValues[Constants.TOKEN] = token
        }
        postValues[CATROBAT_COUNTRY_KEY] = country
        postValues[DEVICE_LANGUAGE] = language

        val url = baseUrl + REGISTRATION_URL_APPENDING
        performTask(url, SERVER_RESPONSE_REGISTER_OK)
    }

    fun performCatrobatLogin() {
        val loginCall: Call <LoginResponse> = webServer.login(User(username, password))

        loginCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val tokenReceived = response.body()?.token
                tokenReceived?.let {
                    if (isInvalidResponseRetrofit(SERVER_RESPONSE_TOKEN_OK, response.code(), response
                            .message(), tokenReceived)) {
                        taskListener.onError(response.code(), response.message())
                        return
                    }
                    // TODO (refresh token is null)
                    val refreshToken = response.body()?.refresh_token
                    Log.e(javaClass.simpleName, "$tokenReceived  $refreshToken")

                    val sharedPreferencesEditor = sharedPreferences.edit()
                    sharedPreferencesEditor.putString(Constants.TOKEN, tokenReceived)
                    sharedPreferencesEditor.putString(Constants.USERNAME, username)
                    sharedPreferencesEditor.apply()
                    taskListener.onSuccess()
                    return
                }
                isInvalidResponseRetrofit(SERVER_RESPONSE_TOKEN_OK, response.code(), response.message())
                taskListener.onError(response.code(), response.message())
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(javaClass.simpleName, t.message.orEmpty())
                taskListener.onError(t.hashCode(), t.message.orEmpty())
            }
        })
    }

    @VisibleForTesting
    fun performTask(
        serverUrl: String,
        acceptedStatusCode: Int
    ) {
        val resultString = try {
            val request = postValues.createFormEncodedRequest(serverUrl)
            okHttpClient.performCallWith(request)
        } catch (exception: WebconnectionException) {
            Log.e(tag, exception.message.orEmpty())
            taskListener.onError(exception.statusCode, null)
            return
        }

        val resultJsonObject = JSONObject(resultString)
        if (isInvalidResponse(acceptedStatusCode, resultJsonObject)) {
            val statusCode = resultJsonObject.optInt(JSON_STATUS_CODE)
            val serverAnswer = resultJsonObject.optString(JSON_ANSWER)
            taskListener.onError(statusCode, serverAnswer)
            return
        }

        val tokenReceived = resultJsonObject.optString(JSON_TOKEN)
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(Constants.TOKEN, tokenReceived)
        sharedPreferencesEditor.putString(Constants.USERNAME, username)

        val eMail = resultJsonObject.optString(Constants.EMAIL)
        if (eMail.isNotEmpty()) {
            sharedPreferencesEditor.putString(Constants.EMAIL, eMail)
        }
        sharedPreferencesEditor.apply()
        taskListener.onSuccess()
    }

    @VisibleForTesting
    fun isInvalidResponse(acceptedStatusCode: Int, resultJsonObject: JSONObject): Boolean {
        val statusCode = resultJsonObject.optInt(JSON_STATUS_CODE)
        val serverAnswer = resultJsonObject.optString(JSON_ANSWER)
        val tokenReceived = resultJsonObject.optString(JSON_TOKEN)

        if (acceptedStatusCode != statusCode) {
            Log.i(tag, "Not accepted StatusCode: $statusCode; Server Answer: $serverAnswer")
            return true
        }
        if (tokenReceived.length != TOKEN_LENGTH) {
            Log.e(tag, "Invalid TokenError: $tokenReceived; StatusCode: $statusCode Server " +
                "Answer: $serverAnswer")
            return true
        }
        return false
    }

    fun isInvalidResponseRetrofit(acceptedStatusCode: Int, statusCode: Int, serverAnswer: String, tokenReceived: String = ""):
        Boolean {
        if (acceptedStatusCode != statusCode) {
            Log.i(tag, "Not accepted StatusCode: $statusCode; Server Answer: $serverAnswer")
            return true
        }
        if (tokenReceived.length != NEW_TOKEN_LENGTH) {
            Log.e(tag, "Invalid TokenError: $tokenReceived; StatusCode: $statusCode Server Answer: $serverAnswer")
            return true
        }
        return false
    }

    interface TaskListener {
        fun onError(statusCode: Int, errorMessage: String?)
        fun onSuccess()
    }
}
