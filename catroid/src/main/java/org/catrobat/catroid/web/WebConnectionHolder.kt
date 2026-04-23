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

import okhttp3.ConnectionSpec.CLEARTEXT
import okhttp3.ConnectionSpec.COMPATIBLE_TLS
import okhttp3.ConnectionSpec.MODERN_TLS
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class WebConnectionHolder {
    var connections: MutableList<WebConnection> = ArrayList(MAX_CONNECTIONS)
    var okHttpClient: OkHttpClient

    companion object {
        private const val MAX_CONNECTIONS = 10
        private const val TIMEOUT_DURATION = 60L
    }

    init {
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .connectionSpecs(listOf(MODERN_TLS, COMPATIBLE_TLS, CLEARTEXT))
            .dispatcher(Dispatcher())
            .build()

        okHttpClient.dispatcher().maxRequests = MAX_CONNECTIONS
        okHttpClient.dispatcher().maxRequestsPerHost = MAX_CONNECTIONS
    }

    @Synchronized
    fun onPause() {
        connections.forEach { it.cancelCall() }
        connections.clear()
    }

    @Synchronized
    fun addConnection(connection: WebConnection): Boolean =
        if (connections.size < MAX_CONNECTIONS) {
            connections.add(connection)
            true
        } else false

    @Synchronized
    fun removeConnection(connection: WebConnection?) {
        connections.remove(connection)
    }
}
