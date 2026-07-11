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
import org.catrobat.catroid.retrofit.AuthService
import org.catrobat.catroid.retrofit.CatroidWebServer
import org.catrobat.catroid.retrofit.models.ApiErrorResponse
import org.catrobat.catroid.retrofit.models.AuthResponse
import org.catrobat.catroid.retrofit.models.LoginRequest
import org.catrobat.catroid.retrofit.models.OAuthLoginRequest
import org.catrobat.catroid.retrofit.models.RefreshRequest
import retrofit2.HttpException
import java.io.IOException

class LoginRepository(
    private val authService: AuthService,
    private val tokenStore: JwtTokenStore
) {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return safeAuthCall {
            val response = authService.login(LoginRequest(username, password))
            tokenStore.setTokens(response.token, response.refreshToken)
            tokenStore.setUsername(username)
            response
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
        return safeAuthCall {
            val response = authService.oauthLogin(
                OAuthLoginRequest(idToken = idToken, resourceOwner = "google")
            )
            tokenStore.setTokens(response.token, response.refreshToken)
            response.username?.let { tokenStore.setUsername(it) }
            response
        }
    }

    private inline fun <T> safeAuthCall(block: () -> T): Result<T> =
        runCatching { block() }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { throwable ->
                if (throwable is HttpException) {
                    Result.failure(Exception(extractErrorMessage(throwable) ?: throwable.message()))
                } else {
                    Result.failure(throwable)
                }
            }
        )

    suspend fun validateToken(): Boolean {
        val token = tokenStore.getAccessToken() ?: return false
        return try {
            authService.checkToken("Bearer $token").isSuccessful
        } catch (_: IOException) {
            false
        } catch (_: HttpException) {
            false
        }
    }

    fun clearLocalSession() {
        tokenStore.clearTokens()
    }

    private fun extractErrorMessage(e: HttpException): String? {
        return try {
            val errorBody = e.response()?.errorBody()?.string() ?: return null
            val adapter = CatroidWebServer.moshi.adapter(ApiErrorResponse::class.java)
            adapter.fromJson(errorBody)?.error?.message
        } catch (_: IOException) {
            null
        } catch (_: JsonDataException) {
            null
        }
    }

    suspend fun refreshToken(): Result<AuthResponse> {
        val refreshToken = tokenStore.getRefreshToken()
            ?: return Result.failure(IllegalStateException("No refresh token available"))
        return try {
            val response = authService.refreshToken(RefreshRequest(refreshToken))
            tokenStore.setTokens(response.token, response.refreshToken)
            Result.success(response)
        } catch (e: IOException) {
            tokenStore.clearTokens()
            Result.failure(e)
        } catch (e: HttpException) {
            tokenStore.clearTokens()
            Result.failure(e)
        }
    }

    suspend fun logout() {
        val token = tokenStore.getAccessToken()
        if (token != null) {
            // Best effort — clear local tokens regardless of the server response
            runCatching { authService.logout("Bearer $token") }
        }
        tokenStore.clearTokens()
    }

    fun isLoggedIn(): Boolean = tokenStore.isLoggedIn()

    fun getUsername(): String? = tokenStore.getUsername()
}
