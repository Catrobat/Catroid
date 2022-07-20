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

package org.catrobat.catroid.ui.recyclerview.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

interface LocalHashVersionRepository {

    fun getFeaturedProjectsHashVersion(): String?
    fun setFeaturedProjectsHashVersion(hashVersion: String?)

    fun getProjectsCategoriesHashVersion(): String?
    fun setProjectsCategoriesHashVersion(hashVersion: String?)

    fun reset()
}

class DefaultLocalHashVersionRepository(
    context: Context
) : LocalHashVersionRepository {
    private val encryptedPref: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "hashVersionsSharedPref",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val FEATURED_PROJECTS_HASH_VERSION = "featured_projects_hash_version"
        private const val PROJECT_CATEGORIES_HASH_VERSION = "project_categories_hash_version"
    }

    override fun getFeaturedProjectsHashVersion() =
        encryptedPref.getString(FEATURED_PROJECTS_HASH_VERSION, null)

    override fun setFeaturedProjectsHashVersion(hashVersion: String?) {
        with(encryptedPref.edit()) {
            putString(FEATURED_PROJECTS_HASH_VERSION, hashVersion)
            apply()
        }
    }

    override fun getProjectsCategoriesHashVersion() =
        encryptedPref.getString(PROJECT_CATEGORIES_HASH_VERSION, null)

    override fun setProjectsCategoriesHashVersion(hashVersion: String?) {
        with(encryptedPref.edit()) {
            putString(PROJECT_CATEGORIES_HASH_VERSION, hashVersion)
            apply()
        }
    }

    override fun reset() {
        with(encryptedPref.edit()) {
            clear()
            apply()
        }
    }
}
