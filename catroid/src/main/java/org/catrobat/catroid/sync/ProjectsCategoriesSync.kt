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

package org.catrobat.catroid.sync

import android.util.Log
import androidx.annotation.WorkerThread
import org.catrobat.catroid.db.AppDatabase
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.ProjectsCategoryApi
import org.catrobat.catroid.ui.recyclerview.repository.LocalHashVersionRepository
import org.catrobat.catroid.utils.toProjectCategoryWithResponsesList

interface ProjectsCategoriesSync {

    // after language change from settings call it with force = true
    fun sync(force: Boolean = false)
}

class DefaultProjectsCategoriesSync(
    private val webService: WebService,
    private val appDatabase: AppDatabase,
    private val localHashVersionRepository: LocalHashVersionRepository
) : ProjectsCategoriesSync {

    @WorkerThread
    override fun sync(force: Boolean) {
        val localHashVersion = localHashVersionRepository.getProjectsCategoriesHashVersion()
        val response = webService.getProjectCategories().execute()
        val serverHashVersion = response.headers().get("x-response-hash")
        Log.d(javaClass.simpleName, "local stored hash version: $localHashVersion")
        Log.d(javaClass.simpleName, "server hash version: $serverHashVersion")
        if (requireUpdate(localHashVersion, serverHashVersion) || force) {
            update(response.body())
            localHashVersionRepository.setProjectsCategoriesHashVersion(serverHashVersion)
        } else {
            Log.d(javaClass.simpleName, "no update needed! you've latest version :)")
        }
    }

    private fun update(body: List<ProjectsCategoryApi>?) {
        Log.d(javaClass.simpleName, "updating projectsCategories")
        Log.d(javaClass.simpleName, "$body")

        body?.toProjectCategoryWithResponsesList()?.let {
            appDatabase.projectCategoryDao().nukeAll()
            appDatabase.projectCategoryDao().insertProjectCategoriesWithResponses(it)
        }
    }

    private fun requireUpdate(localHashVersion: String?, serverHashVersion: String?) =
        localHashVersion == null || localHashVersion != serverHashVersion
}
