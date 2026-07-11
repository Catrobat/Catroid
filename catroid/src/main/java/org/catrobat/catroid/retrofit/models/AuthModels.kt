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

package org.catrobat.catroid.retrofit.models

import com.squareup.moshi.Json

data class LoginRequest(
    val username: String,
    val password: String
)

data class RefreshRequest(
    @Json(name = "refresh_token") val refreshToken: String
)

data class OAuthLoginRequest(
    @Json(name = "id_token") val idToken: String,
    @Json(name = "resource_owner") val resourceOwner: String
)

data class AuthResponse(
    val token: String,
    @Json(name = "refresh_token") val refreshToken: String,
    val username: String? = null
)

data class ApiErrorResponse(
    val error: ApiErrorDetail
)

data class ApiErrorDetail(
    val code: Int,
    val type: String,
    val message: String,
    val details: List<ApiFieldError>? = null
)

data class ApiFieldError(
    val field: String,
    val message: String
)
