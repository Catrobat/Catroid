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
import org.catrobat.catroid.BuildConfig
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
        try {
            executeDownload(url, destination) { progress -> progressCallback(progress) }
            successCallback()
        } catch (e: WebConnectionException) {
            Log.v(TAG, "Download not successful")
            errorCallback(e.statusCode, e.message ?: "Download failed")
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
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

        executeDownload(url, file) { progress ->
            val bundle = Bundle()
            bundle.putLong(ProgressResponseBody.TAG_PROGRESS, progress)
            receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, bundle)
        }
    }

    private fun executeDownload(url: String, destination: File, progressCallback: (Long) -> Unit) {
        val request = Request.Builder().url(url).build()
        val httpClient = buildClientWithProgress(url, progressCallback)
        val response = httpClient.newCall(request).execute()

        response.use {
            if (!it.isSuccessful) {
                throw WebConnectionException(it.code(), "Download failed! HTTP ${it.code()}")
            }

            try {
                destination.sink().buffer().use { bufferedSink ->
                    it.body()?.let { body -> bufferedSink.writeAll(body.source()) }
                        ?: throw WebConnectionException(WebConnectionException.ERROR_NETWORK, "Empty response body")
                }
            } catch (e: IOException) {
                throw WebConnectionException(WebConnectionException.ERROR_NETWORK, Log.getStackTraceString(e))
            }
        }
    }

    private fun buildClientWithProgress(url: String, progressCallback: (Long) -> Unit): OkHttpClient {
        val builder = okHttpClient.newBuilder()
        builder.networkInterceptors().add { chain ->
            val originalResponse = chain.proceed(chain.request())
            val body = ProgressResponseBody(originalResponse.body()) { progress ->
                progressCallback(progress)
            }
            originalResponse.newBuilder().body(body).build()
        }
        return if (url.startsWith("http://") && BuildConfig.DEBUG) {
            builder.connectionSpecs(listOf(ConnectionSpec.CLEARTEXT)).build()
        } else {
            builder.build()
        }
    }

    companion object {
        private const val TAG = "DownloadClient"
    }
}
