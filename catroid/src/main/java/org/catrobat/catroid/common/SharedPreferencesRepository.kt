/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.common

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesRepository {
    private const val PREFERENCES_NAME = "CATROID_APP"
    private const val NEVER_OPENED_PROJECTS_LIST = "NEVER_OPENED_PROJECTS_LIST"

    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveNeverOpenedProjectsList(context: Context, neverOpenedProjectsList: List<String>) {
        val set: Set<String> = neverOpenedProjectsList.toSet()

        getPreferences(context)
            .edit()
            .putStringSet(NEVER_OPENED_PROJECTS_LIST, set)
            .apply()
    }

    fun getNeverOpenedProjectsList(context: Context): List<String> {
        val set = getPreferences(context).getStringSet(
            NEVER_OPENED_PROJECTS_LIST,
            null
        ) ?: return ArrayList()
        return set.toList()
    }
}
