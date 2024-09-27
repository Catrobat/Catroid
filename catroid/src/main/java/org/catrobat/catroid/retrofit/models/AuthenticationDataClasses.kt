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

package org.catrobat.catroid.retrofit.models

data class LoginUser(
    val username: String,
    val password: String
)

@SuppressWarnings("ConstructorParameterNaming")
data class LoginResponse(
    val token: String,
    val refresh_token: String
)

@SuppressWarnings("ConstructorParameterNaming")
data class RefreshToken(
    val refresh_token: String
)

@SuppressWarnings("ConstructorParameterNaming")
data class DeprecatedToken(
    val upload_token: String
)

@SuppressWarnings("ConstructorParameterNaming")
data class OAuthLogin(
    val id_token: String,
    val resource_owner: String
)

@SuppressWarnings("ConstructorParameterNaming")
data class RegisterUser(
    val dry_run: Boolean,
    val email: String,
    val username: String,
    val password: String
)

data class RegisterFailedResponse(
    val email: String?,
    val username: String?
)

data class Tag(
    val id: String,
    val text: String
)
