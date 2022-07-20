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

import okhttp3.ConnectionSpec
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object CatrobatWebClient {
    val client: OkHttpClient = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
        .build()
}

@Throws(WebconnectionException::class)
fun OkHttpClient.performCallWith(request: Request): String {
    var message = "Bad Connection"
    var statusCode = WebconnectionException.ERROR_NETWORK
    try {
        val response = this.newCall(request).execute()
        response.body()?.let {
            return it.string()
        }
        statusCode = response.code()
        message = response.message()
    } catch (e: IOException) {
        e.message?.let {
            message = it
        }
    }

    throw WebconnectionException(statusCode, message)
}

fun Map<String, String>.createFormEncodedRequest(url: String): Request {
    val formEncodingBuilder = FormBody.Builder()
    for ((key, value) in this) {
        formEncodingBuilder.add(key, value)
    }
    return Request.Builder()
        .url(url)
        .post(formEncodingBuilder.build())
        .build()
}
