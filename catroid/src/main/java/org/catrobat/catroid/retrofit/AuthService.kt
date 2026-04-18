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

import org.catrobat.catroid.retrofit.models.AuthResponse
import org.catrobat.catroid.retrofit.models.LoginRequest
import org.catrobat.catroid.retrofit.models.OAuthLoginRequest
import org.catrobat.catroid.retrofit.models.RefreshRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {

    @POST("authentication")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("authentication/refresh")
    suspend fun refreshToken(@Body body: RefreshRequest): AuthResponse

    @POST("authentication/oauth")
    suspend fun oauthLogin(@Body body: OAuthLoginRequest): AuthResponse

    @GET("authentication")
    suspend fun checkToken(@Header("Authorization") bearer: String): Response<Unit>

    @DELETE("authentication")
    suspend fun logout(@Header("Authorization") bearer: String): Response<Unit>
}
