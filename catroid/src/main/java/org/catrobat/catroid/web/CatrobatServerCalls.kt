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

import android.util.Log
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import org.catrobat.catroid.web.ServerAuthenticationConstants.FILE_SURVEY_URL_HTTP
import java.io.File
import java.io.IOException

class CatrobatServerCalls(private val okHttpClient: OkHttpClient = CatrobatWebClient.client) {
    private val tag = CatrobatServerCalls::class.java.simpleName

    @Throws(WebconnectionException::class)
    private fun getRequest(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        return okHttpClient.performCallWith(request)
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
