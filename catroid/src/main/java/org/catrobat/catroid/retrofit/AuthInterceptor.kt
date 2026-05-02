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

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import org.catrobat.catroid.retrofit.models.AuthResponse
import org.catrobat.catroid.retrofit.models.RefreshRequest
import org.catrobat.catroid.web.JwtTokenStore
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class AuthInterceptor(
    private val tokenStore: JwtTokenStore,
    private val baseUrl: String
) : Interceptor {

    private val lock = Any()

    private interface SyncRefreshService {
        @POST("authentication/refresh")
        fun refreshToken(@Body body: RefreshRequest): Call<AuthResponse>
    }

    private val refreshService: SyncRefreshService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(CatroidWebServer.moshi))
            .build()
            .create(SyncRefreshService::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenStore.getAccessToken()
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)

        if (response.code() != HTTP_UNAUTHORIZED || token == null) {
            return response
        }

        synchronized(lock) {
            val currentToken = tokenStore.getAccessToken()
            if (currentToken != null && currentToken != token) {
                response.close()
                val retryRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
                return chain.proceed(retryRequest)
            }
            if (currentToken == null) {
                tokenStore.clearTokens()
                return response
            }

            val refreshToken = tokenStore.getRefreshToken() ?: run {
                tokenStore.clearTokens()
                return response
            }

            return try {
                val refreshResponse = refreshService
                    .refreshToken(RefreshRequest(refreshToken))
                    .execute()

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val body = refreshResponse.body()!!
                    tokenStore.setTokens(body.token, body.refreshToken)

                    response.close()
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${body.token}")
                        .build()
                    chain.proceed(retryRequest)
                } else {
                    Log.w(TAG, "Token refresh returned ${refreshResponse.code()}")
                    tokenStore.clearTokens()
                    response
                }
            } catch (e: Exception) {
                Log.w(TAG, "Token refresh failed", e)
                tokenStore.clearTokens()
                response
            }
        }
    }

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val HTTP_UNAUTHORIZED = 401
    }
}
