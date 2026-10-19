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
import com.squareup.moshi.JsonDataException
import org.catrobat.catroid.retrofit.models.AuthResponse
import org.catrobat.catroid.retrofit.models.RefreshRequest
import org.catrobat.catroid.web.JwtTokenStore
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException

/**
 * The one place that trades a refresh token for a new token pair.
 *
 * Refresh tokens are single-use on the server: each refresh returns a new one and invalidates
 * the old. Two refreshes with the same token therefore end with one of them rejected, which
 * would log the user out. All refreshes go through this lock, and a caller whose access token
 * was already replaced by another caller uses the new one instead of refreshing again.
 */
class TokenRefresher(private val tokenStore: JwtTokenStore, baseUrl: String) {

    enum class Outcome { REFRESHED, REJECTED, UNREACHABLE }

    private fun interface SyncRefreshService {
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

    /** Blocking; call it off the main thread. [rejectedAccessToken] is the token the server refused. */
    @Synchronized
    fun refresh(rejectedAccessToken: String): Outcome {
        val currentToken = tokenStore.getAccessToken()
        if (currentToken != null && currentToken != rejectedAccessToken) {
            return Outcome.REFRESHED
        }
        val refreshToken = tokenStore.getRefreshToken()
        if (currentToken == null || refreshToken == null) {
            tokenStore.clearTokens()
            return Outcome.REJECTED
        }

        return try {
            val response = refreshService.refreshToken(RefreshRequest(refreshToken)).execute()
            val body = response.body()
            when {
                response.isSuccessful && body != null -> {
                    tokenStore.setTokens(body.token, body.refreshToken)
                    Outcome.REFRESHED
                }
                // Only a rejected refresh token ends the session. A 5xx or 429 during a deploy
                // or under load says nothing about it.
                response.code() in SESSION_REJECTED_CODES -> {
                    tokenStore.clearTokens()
                    Outcome.REJECTED
                }
                else -> {
                    Log.w(TAG, "Token refresh returned ${response.code()}")
                    Outcome.UNREACHABLE
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "Token refresh failed", e)
            Outcome.UNREACHABLE
        } catch (e: JsonDataException) {
            Log.w(TAG, "Token refresh answered with an unexpected body", e)
            Outcome.UNREACHABLE
        }
    }

    private companion object {
        const val TAG = "TokenRefresher"
        val SESSION_REJECTED_CODES = setOf(400, 401, 403)
    }
}
