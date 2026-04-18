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

package org.catrobat.catroid.web

import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import org.catrobat.catroid.common.Constants
import java.io.File
import java.io.IOException

class DownloadClient(private val okHttpClient: OkHttpClient) {

    fun downloadProject(
        url: String,
        destination: File,
        successCallback: () -> Unit,
        errorCallback: (code: Int, message: String) -> Unit,
        progressCallback: (progress: Long) -> Unit
    ) {
        val request = Request.Builder().url(url).build()
        val httpClientBuilder = okHttpClient.newBuilder()
        httpClientBuilder.networkInterceptors().add { chain ->
            val originalResponse = chain.proceed(chain.request())
            val body = ProgressResponseBody(originalResponse.body()) { progress ->
                progressCallback(progress)
            }
            originalResponse.newBuilder().body(body).build()
        }
        val httpClient = if (url.startsWith("http://")) {
            httpClientBuilder.connectionSpecs(listOf(ConnectionSpec.CLEARTEXT)).build()
        } else {
            httpClientBuilder.build()
        }

        try {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bufferedSink = destination.sink().buffer()
                response.body()?.let { bufferedSink.writeAll(it.source()) }
                bufferedSink.close()
                successCallback()
            } else {
                Log.v(TAG, "Download not successful")
                errorCallback(response.code(), "Download failed! HTTP Status code was ${response.code()}")
            }
        } catch (ioException: IOException) {
            Log.e(TAG, Log.getStackTraceString(ioException))
            errorCallback(WebConnectionException.ERROR_NETWORK, "I/O Exception")
        }
    }

    @Throws(IOException::class, WebConnectionException::class)
    fun downloadMedia(url: String, filePath: String, receiver: ResultReceiver) {
        val file = File(filePath)
        val parentDir = file.parentFile ?: throw IOException("No parent directory")
        if (!(parentDir.mkdirs() || parentDir.isDirectory)) {
            throw IOException("Directory not created")
        }

        val request = Request.Builder().url(url).build()
        val httpClientBuilder = okHttpClient.newBuilder()
        httpClientBuilder.networkInterceptors().add { chain ->
            val originalResponse = chain.proceed(chain.request())
            val body = ProgressResponseBody(originalResponse.body()) { progress ->
                val bundle = Bundle()
                bundle.putLong(ProgressResponseBody.TAG_PROGRESS, progress)
                receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, bundle)
            }
            originalResponse.newBuilder().body(body).build()
        }
        val httpClient = httpClientBuilder.build()
        val response = httpClient.newCall(request).execute()

        try {
            val bufferedSink = file.sink().buffer()
            response.body()?.let {
                bufferedSink.writeAll(it.source())
            } ?: throw WebConnectionException(WebConnectionException.ERROR_NETWORK, "FAIL")
            bufferedSink.close()
        } catch (e: IOException) {
            throw WebConnectionException(WebConnectionException.ERROR_NETWORK, Log.getStackTraceString(e))
        }
    }

    companion object {
        private const val TAG = "DownloadClient"
    }
}
