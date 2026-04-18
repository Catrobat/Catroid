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

import org.catrobat.catroid.retrofit.AuthService
import org.catrobat.catroid.retrofit.models.AuthResponse
import org.catrobat.catroid.retrofit.models.LoginRequest
import org.catrobat.catroid.retrofit.models.OAuthLoginRequest
import org.catrobat.catroid.retrofit.models.RefreshRequest

class LoginRepository(
    private val authService: AuthService,
    private val tokenStore: JwtTokenStore
) {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = authService.login(LoginRequest(username, password))
            tokenStore.setTokens(response.token, response.refresh_token)
            tokenStore.setUsername(username)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
        return try {
            val response = authService.oauthLogin(
                OAuthLoginRequest(id_token = idToken, resource_owner = "google")
            )
            tokenStore.setTokens(response.token, response.refresh_token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<AuthResponse> {
        val refreshToken = tokenStore.getRefreshToken()
            ?: return Result.failure(IllegalStateException("No refresh token available"))
        return try {
            val response = authService.refreshToken(RefreshRequest(refreshToken))
            tokenStore.setTokens(response.token, response.refresh_token)
            Result.success(response)
        } catch (e: Exception) {
            tokenStore.clearTokens()
            Result.failure(e)
        }
    }

    suspend fun logout() {
        val token = tokenStore.getAccessToken()
        if (token != null) {
            try {
                authService.logout("Bearer $token")
            } catch (_: Exception) {
                // Best effort — clear local tokens regardless
            }
        }
        tokenStore.clearTokens()
    }

    fun isLoggedIn(): Boolean = tokenStore.isLoggedIn()

    fun getUsername(): String? = tokenStore.getUsername()
}
