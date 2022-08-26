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

package org.catrobat.catroid.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.catrobat.catroid.retrofit.models.ProjectCategoryWithResponses
import org.catrobat.catroid.retrofit.models.ProjectResponse
import org.catrobat.catroid.retrofit.models.ProjectsCategory

@Dao
@SuppressWarnings("UnnecessaryAbstractClass")
abstract class ProjectsCategoryDao {

    @Transaction
    @Query("SELECT * FROM project_category")
    abstract fun getProjectsCategories(): Flow<List<ProjectCategoryWithResponses>>

    @Transaction
    open fun insertProjectCategoriesWithResponses(
        projectCategoryWithResponses: List<ProjectCategoryWithResponses>
    ) {
        nukeAll()

        projectCategoryWithResponses.forEach {
            if (!it.projectsList.isNullOrEmpty()) {
                insertProjectCategory(it.category)
                insertProjectResponses(it.projectsList)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insertProjectCategory(projectsCategory: ProjectsCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insertProjectResponses(projectResponseList: List<ProjectResponse>)

    @Query("DELETE FROM project_category")
    protected abstract fun deleteAllProjectsCategory()

    @Query("DELETE FROM project_response")
    protected abstract fun deleteAllProjectResponse()

    @Transaction
    open fun nukeAll() {
        deleteAllProjectsCategory()
        deleteAllProjectResponse()
    }
}
