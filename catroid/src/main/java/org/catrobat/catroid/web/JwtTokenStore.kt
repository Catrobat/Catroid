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

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class JwtTokenStore(openPrefs: () -> SharedPreferences) {

    constructor(context: Context) : this({
        EncryptedSharedPreferences.create(
            PREFS_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    })

    private val prefs: SharedPreferences by lazy(openPrefs)

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    // Written synchronously: refresh tokens are single-use, so a new pair lost to process death
    // after the server already invalidated the old one would end the session.
    @Synchronized
    fun setTokens(accessToken: String, refreshToken: String) {
        prefs.edit(commit = true) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    /**
     * Stores the pair a refresh of [usedRefreshToken] returned, unless the session changed while
     * the refresh was on the network (logout, or a login as someone else).
     */
    @Synchronized
    fun replaceTokens(usedRefreshToken: String, accessToken: String, refreshToken: String): Boolean {
        if (getRefreshToken() != usedRefreshToken) {
            return false
        }
        setTokens(accessToken, refreshToken)
        return true
    }

    /** Ends the session [rejectedRefreshToken] belonged to, unless a newer one replaced it. */
    @Synchronized
    fun clearTokensIfCurrent(rejectedRefreshToken: String) {
        if (getRefreshToken() == rejectedRefreshToken) {
            clearTokens()
        }
    }

    @Synchronized
    fun clearTokens() {
        prefs.edit(commit = true) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USERNAME) // written by earlier builds of this migration
        }
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null

    companion object {
        private const val PREFS_NAME = "jwt_token_store"
        private const val KEY_ACCESS_TOKEN = "jwt_access_token"
        private const val KEY_REFRESH_TOKEN = "jwt_refresh_token"
        private const val KEY_USERNAME = "jwt_username"
    }
}
