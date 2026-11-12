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

import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.catrobat.catroid.retrofit.AuthService
import org.catrobat.catroid.retrofit.CatroidWebServer
import org.catrobat.catroid.retrofit.TokenRefresher
import org.catrobat.catroid.retrofit.models.ApiErrorResponse
import org.catrobat.catroid.retrofit.models.AuthResponse
import org.catrobat.catroid.retrofit.models.LoginRequest
import org.catrobat.catroid.retrofit.models.OAuthLoginRequest
import org.catrobat.catroid.retrofit.models.RefreshRequest
import retrofit2.HttpException
import java.io.IOException

class LoginRepository(
    private val authService: AuthService,
    private val tokenStore: JwtTokenStore,
    private val refresher: TokenRefresher
) {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return safeAuthCall {
            val response = authService.login(LoginRequest(username, password))
            tokenStore.setTokens(response.token, response.refreshToken)
            response
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
        return safeAuthCall {
            val response = authService.oauthLogin(
                OAuthLoginRequest(idToken = idToken, resourceOwner = "google")
            )
            tokenStore.setTokens(response.token, response.refreshToken)
            response
        }
    }

    /** The server refused the login; [errorCode] is its machine-readable reason, if it sent one. */
    class LoginRefused(val status: Int, val errorCode: String?) : Exception("Login refused with $status")

    private inline fun <T> safeAuthCall(block: () -> T): Result<T> =
        runCatching { block() }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { throwable ->
                if (throwable is HttpException) {
                    Result.failure(LoginRefused(throwable.code(), readErrorBody(throwable)?.errorCode))
                } else {
                    Result.failure(throwable)
                }
            }
        )

    /**
     * Asks the server whether the stored session is still valid, refreshing an expired access
     * token on the way: it lives an hour, the refresh token a month.
     *
     * @return true or false when the server answered, null when it could not be asked (offline,
     * server error). Only a definite answer may end the session, a network blip must not.
     */
    suspend fun validateToken(): Boolean? {
        val token = tokenStore.getAccessToken() ?: return false
        return try {
            val response = authService.checkToken(bearer(token))
            when {
                response.isSuccessful -> true
                response.code() == HTTP_UNAUTHORIZED -> when (refreshBlocking(token)) {
                    TokenRefresher.Outcome.REFRESHED -> true
                    TokenRefresher.Outcome.REJECTED -> false
                    TokenRefresher.Outcome.UNREACHABLE -> null
                }
                else -> null
            }
        } catch (_: IOException) {
            null
        }
    }

    private suspend fun refreshBlocking(token: String) =
        withContext(Dispatchers.IO) { refresher.refresh(token) }

    fun clearLocalSession() {
        tokenStore.clearTokens()
    }

    private fun readErrorBody(e: HttpException): ApiErrorResponse? {
        return try {
            val errorBody = e.response()?.errorBody()?.string() ?: return null
            CatroidWebServer.moshi.adapter(ApiErrorResponse::class.java).fromJson(errorBody)
        } catch (_: IOException) {
            null
        } catch (_: JsonDataException) {
            null
        }
    }

    /**
     * Best effort: the local session is cleared regardless of the server response.
     *
     * The server only revokes for a valid access token, and the stored one has usually expired
     * (it lives an hour). In that case the refresh token buys a fresh pair, and the new refresh
     * token is the one revoked; the old one is single-use and already spent by then.
     */
    suspend fun revoke(accessToken: String?, refreshToken: String?) {
        if (refreshToken == null) {
            return
        }
        runCatching {
            val response = authService.logout(bearer(accessToken.orEmpty()), refreshToken)
            if (response.code() == HTTP_UNAUTHORIZED) {
                val fresh = authService.refreshToken(RefreshRequest(refreshToken))
                authService.logout(bearer(fresh.token), fresh.refreshToken)
            }
        }
    }

    private fun bearer(token: String) = "Bearer $token"

    fun isLoggedIn(): Boolean = tokenStore.isLoggedIn()

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
    }
}
