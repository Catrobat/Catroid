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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@SuppressWarnings("ConstructorParameterNaming")
@Entity(tableName = "featured_project")
data class FeaturedProject(
    @PrimaryKey
    val id: String,
    val project_id: String,
    val project_url: String,
    val name: String,
    val author: String,
    val featured_image: String
)

@Entity(tableName = "project_response", primaryKeys = ["id", "categoryType"])
data class ProjectResponse(
    var id: String,
    var name: String,
    var author: String,
    var description: String,
    var version: String,
    var views: Int,
    var download: Int,
    var private: Boolean,
    var flavor: String,
    var tags: List<String>,
    var uploaded: Long,
    var uploadedString: String,
    var screenshotLarge: String,
    var screenshotSmall: String,
    var projectUrl: String,
    var downloadUrl: String,
    var fileSize: Double,
    var categoryType: String
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        0,
        0,
        false,
        "",
        emptyList<String>(),
        0L,
        "",
        "",
        "",
        "",
        "",
        0.0,
        ""
    )
}

@Entity(tableName = "project_category")
data class ProjectsCategory(
    @PrimaryKey
    val type: String,
    val name: String
)

data class ProjectCategoryWithResponses(
    @Embedded val category: ProjectsCategory,
    @Relation(
        parentColumn = "type",
        entityColumn = "categoryType"
    )
    val projectsList: List<ProjectResponse>
)

data class ProjectsCategoryApi(
    val type: String,
    val name: String,
    val projectsList: List<ProjectResponseApi>
)

@SuppressWarnings("ConstructorParameterNaming")
data class ProjectResponseApi(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val version: String,
    val views: Int,
    val download: Int,
    val private: Boolean,
    val flavor: String,
    val tags: List<String>,
    val uploaded: Long,
    val uploaded_string: String,
    val screenshot_large: String,
    val screenshot_small: String,
    val project_url: String,
    val download_url: String,
    val filesize: Double
)
