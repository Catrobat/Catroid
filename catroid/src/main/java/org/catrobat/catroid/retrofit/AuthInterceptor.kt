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
package org.catrobat.catroid.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import org.catrobat.catroid.web.JwtTokenStore

class AuthInterceptor(
    private val tokenStore: JwtTokenStore,
    private val refresher: TokenRefresher
) : Interceptor {

    constructor(tokenStore: JwtTokenStore, baseUrl: String) : this(tokenStore, TokenRefresher(tokenStore, baseUrl))

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenStore.getAccessToken()
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header(HEADER_AUTHORIZATION, bearer(token))
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)

        if (response.code() != HTTP_UNAUTHORIZED || token == null) {
            return response
        }

        // Offline or a server error keeps the session: the refresh token may still be valid.
        if (refresher.refresh(token) != TokenRefresher.Outcome.REFRESHED) {
            return response
        }
        val newToken = tokenStore.getAccessToken() ?: return response
        response.close()
        return chain.proceed(
            originalRequest.newBuilder()
                .header(HEADER_AUTHORIZATION, bearer(newToken))
                .build()
        )
    }

    companion object {
        private const val HTTP_UNAUTHORIZED = 401
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private fun bearer(token: String) = BEARER_PREFIX + token
    }
}
