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

package org.catrobat.catroid.web

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.web.ServerAuthenticationConstants.CHECK_EMAIL_AVAILABLE_URL
import org.catrobat.catroid.web.ServerAuthenticationConstants.CHECK_GOOGLE_TOKEN_URL
import org.catrobat.catroid.web.ServerAuthenticationConstants.CHECK_TOKEN_URL
import org.catrobat.catroid.web.ServerAuthenticationConstants.CHECK_USERNAME_AVAILABLE_URL
import org.catrobat.catroid.web.ServerAuthenticationConstants.EMAIL_AVAILABLE
import org.catrobat.catroid.web.ServerAuthenticationConstants.FILE_SURVEY_URL_HTTP
import org.catrobat.catroid.web.ServerAuthenticationConstants.FILE_TAG_URL_HTTP
import org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_ANSWER
import org.catrobat.catroid.web.ServerAuthenticationConstants.JSON_STATUS_CODE
import org.catrobat.catroid.web.ServerAuthenticationConstants.OAUTH_TOKEN_AVAILABLE
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import org.catrobat.catroid.web.ServerAuthenticationConstants.SIGNIN_EMAIL_KEY
import org.catrobat.catroid.web.ServerAuthenticationConstants.SIGNIN_OAUTH_ID_KEY
import org.catrobat.catroid.web.ServerAuthenticationConstants.SIGNIN_USERNAME_KEY
import org.catrobat.catroid.web.ServerAuthenticationConstants.USERNAME_AVAILABLE
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.HashMap

class CatrobatServerCalls(private val okHttpClient: OkHttpClient = CatrobatWebClient.client) {
    private val tag = CatrobatServerCalls::class.java.simpleName

    @Throws(WebconnectionException::class)
    fun checkToken(token: String, username: String, baseUrl: String): Boolean {
        try {
            val postValues = HashMap<String, String>()
            postValues[Constants.TOKEN] = token
            postValues[SIGNIN_USERNAME_KEY] = username

            val serverUrl = baseUrl + CHECK_TOKEN_URL

            val request = postValues.createFormEncodedRequest(serverUrl)
            val resultString = okHttpClient.performCallWith(request)

            val jsonObject = JSONObject(resultString)
            val statusCode = jsonObject.getInt(JSON_STATUS_CODE)
            val serverAnswer = jsonObject.optString(JSON_ANSWER)

            return if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
                true
            } else {
                throw WebconnectionException(statusCode, "server response token ok, but error: $serverAnswer")
            }
        } catch (e: JSONException) {
            throw WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e))
        }
    }

    @Throws(WebconnectionException::class)
    private fun getRequest(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        return okHttpClient.performCallWith(request)
    }

    fun getTags(language: String?): String {
        return try {
            var serverUrl = FILE_TAG_URL_HTTP
            if (language != null) {
                serverUrl += "?language=$language"
            }
            getRequest(serverUrl)
        } catch (e: WebconnectionException) {
            Log.e(tag, Log.getStackTraceString(e))
            ""
        }
    }

    fun getSurvey(language: String?): String {
        return try {
            var serverUrl = FILE_SURVEY_URL_HTTP
            if (language != null) {
                serverUrl += language
            }
            getRequest(serverUrl)
        } catch (e: WebconnectionException) {
            Log.e(tag, Log.getStackTraceString(e))
            ""
        }
    }

    @Throws(WebconnectionException::class)
    fun checkOAuthToken(id: String, oauthProvider: String, context: Context?): Boolean? {
        var statusCode: Int
        var message: String
        try {
            val postValues = HashMap<String, String>()
            postValues[SIGNIN_OAUTH_ID_KEY] = id

            val serverUrl = when (oauthProvider) {
                Constants.GOOGLE_PLUS -> CHECK_GOOGLE_TOKEN_URL
                else -> throw WebconnectionException(-1, "OAuth provider not supported!")
            }

            val request = postValues.createFormEncodedRequest(serverUrl)
            val resultString = okHttpClient.performCallWith(request)

            val jsonObject = JSONObject(resultString)
            statusCode = jsonObject.getInt(JSON_STATUS_CODE)
            if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
                val serverEmail = jsonObject.optString(SIGNIN_EMAIL_KEY)
                val serverUsername = jsonObject.optString(SIGNIN_USERNAME_KEY)
                val tokenAvailable = jsonObject.getBoolean(OAUTH_TOKEN_AVAILABLE)

                if (tokenAvailable && oauthProvider == Constants.GOOGLE_PLUS) {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    sharedPreferences.edit()
                        .putString(Constants.GOOGLE_USERNAME, serverUsername)
                        .putString(Constants.GOOGLE_EMAIL, serverEmail)
                        .apply()
                }

                return tokenAvailable
            }
            message = resultString
        } catch (e: JSONException) {
            statusCode = WebconnectionException.ERROR_JSON
            message = Log.getStackTraceString(e)
        }
        throw WebconnectionException(statusCode, message)
    }

    @Throws(WebconnectionException::class)
    fun isEMailAvailable(email: String): Boolean {
        try {
            val postValues = HashMap<String, String>()
            postValues[SIGNIN_EMAIL_KEY] = email

            val serverUrl = CHECK_EMAIL_AVAILABLE_URL
            val request = postValues.createFormEncodedRequest(serverUrl)
            val resultString = okHttpClient.performCallWith(request)

            val jsonObject = JSONObject(resultString)
            val statusCode = jsonObject.getInt(JSON_STATUS_CODE)
            if (statusCode != SERVER_RESPONSE_TOKEN_OK) {
                throw WebconnectionException(statusCode, resultString)
            }

            return jsonObject.getBoolean(EMAIL_AVAILABLE)
        } catch (e: JSONException) {
            throw WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e))
        }
    }

    @Throws(WebconnectionException::class)
    fun isUserNameAvailable(username: String): Boolean {
        var resultString = ""
        try {
            val postValues = HashMap<String, String>()
            postValues[SIGNIN_USERNAME_KEY] = username

            val serverUrl = CHECK_USERNAME_AVAILABLE_URL
            val request = postValues.createFormEncodedRequest(serverUrl)
            resultString = okHttpClient.performCallWith(request)

            val jsonObject = JSONObject(resultString)
            val statusCode = jsonObject.getInt(JSON_STATUS_CODE)
            if (statusCode != SERVER_RESPONSE_TOKEN_OK) {
                throw WebconnectionException(statusCode, resultString)
            }

            return jsonObject.getBoolean(USERNAME_AVAILABLE)
        } catch (jsonException: JSONException) {
            Log.e(tag, Log.getStackTraceString(jsonException))
            throw WebconnectionException(WebconnectionException.ERROR_JSON, resultString)
        }
    }

    @Throws(WebconnectionException::class)
    fun deleteTestUserAccountsOnServer(): Boolean {
        try {
            val resultString = getRequest("")
            val jsonObject = JSONObject(resultString)
            val statusCode = jsonObject.getInt(JSON_STATUS_CODE)
            if (statusCode != SERVER_RESPONSE_TOKEN_OK) {
                throw WebconnectionException(statusCode, resultString)
            }
            return true
        } catch (e: JSONException) {
            throw WebconnectionException(WebconnectionException.ERROR_JSON, Log.getStackTraceString(e))
        }
    }

    fun downloadProject(
        url: String,
        destination: File,
        successCallback: DownloadSuccessCallback,
        errorCallback: DownloadErrorCallback,
        progressCallback: DownloadProgressCallback
    ) {
        val request = Request.Builder().url(url).build()
        val httpClientBuilder = okHttpClient.newBuilder()
        httpClientBuilder.networkInterceptors()
            .add(Interceptor { chain ->
                val originalResponse =
                    chain.proceed(chain.request())
                val body = ProgressResponseBody(
                    originalResponse.body(),
                    progressCallback
                )
                originalResponse.newBuilder().body(body).build()
            })
        val httpClient = if (url.startsWith("http://")) {
            httpClientBuilder
                .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT))
                .build()
        } else {
            httpClientBuilder.build()
        }

        try {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bufferedSink = Okio.buffer(Okio.sink(destination))
                response.body()?.let { bufferedSink.writeAll(it.source()) }
                bufferedSink.close()
                successCallback.onSuccess()
            } else {
                Log.v(tag, "Download not successful")
                errorCallback.onError(response.code(), "Download failed! HTTP Status code was " + response.code())
            }
        } catch (ioException: IOException) {
            Log.e(tag, Log.getStackTraceString(ioException))
            errorCallback.onError(WebconnectionException.ERROR_NETWORK, "I/O Exception")
        }
    }

    interface DownloadSuccessCallback {
        fun onSuccess()
    }

    interface DownloadErrorCallback {
        fun onError(code: Int, message: String)
    }

    interface DownloadProgressCallback {
        fun onProgress(progress: Long)
    }
}
