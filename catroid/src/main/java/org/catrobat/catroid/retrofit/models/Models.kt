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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

// --- Cursor pagination envelope ---

@SuppressWarnings("ConstructorParameterNaming")
data class CursorPaginatedResponse<T>(
    val data: List<T>,
    val next_cursor: String? = null,
    val has_more: Boolean = false
)

// --- Image variants (responsive images from API v2) ---

@SuppressWarnings("ConstructorParameterNaming")
data class ImageVariantSet(
    val avif_1x: String? = null,
    val avif_2x: String? = null,
    val webp_1x: String? = null,
    val webp_2x: String? = null
) {
    fun getBestUrl(): String? = webp_2x ?: webp_1x ?: avif_2x ?: avif_1x
}

@SuppressWarnings("ConstructorParameterNaming")
data class ImageVariants(
    val thumb: ImageVariantSet? = null,
    val card: ImageVariantSet? = null,
    val detail: ImageVariantSet? = null,
    val width: Int? = null,
    val height: Int? = null
) {
    fun getCardUrl(): String? = card?.getBestUrl() ?: thumb?.getBestUrl()
    fun getDetailUrl(): String? = detail?.getBestUrl() ?: card?.getBestUrl()
    fun getThumbUrl(): String? = thumb?.getBestUrl() ?: card?.getBestUrl()
}

// --- Featured projects ---

// API response model (from /api/projects/featured)
@SuppressWarnings("ConstructorParameterNaming")
data class FeaturedProjectApi(
    val id: String,
    val project_id: String,
    val project_url: String,
    val url: String? = null,
    val name: String,
    val author: String,
    val featured_image: ImageVariants? = null
) {
    fun toRoomEntity(): FeaturedProject = FeaturedProject(
        id = id,
        project_id = project_id,
        project_url = project_url,
        name = name,
        author = author,
        featured_image = featured_image?.getDetailUrl() ?: featured_image?.getCardUrl() ?: ""
    )
}

// Room entity (stores best image URL as string)
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

// --- Project categories (from /projects/categories) ---

@SuppressWarnings("ConstructorParameterNaming")
data class ProjectsCategoryListResponse(
    val data: List<ProjectsCategoryApi>
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

@SuppressWarnings("ConstructorParameterNaming")
data class ProjectsCategoryApi(
    val type: String,
    val name: String,
    val projects_list: List<ProjectResponseApi>
) {
    // Keep old accessor name for compatibility with existing code
    val projectsList: List<ProjectResponseApi> get() = projects_list
}

@SuppressWarnings("ConstructorParameterNaming")
data class ProjectResponseApi(
    val id: String,
    val name: String,
    val author: String,
    val author_id: String? = null,
    val description: String = "",
    val version: String = "",
    val views: Int = 0,
    val downloads: Int = 0,
    val reactions: Int = 0,
    val comments: Int = 0,
    val private: Boolean = false,
    val flavor: String = "",
    val tags: Any? = null,
    val uploaded_at: String? = null,
    val uploaded_string: String = "",
    val screenshot: ImageVariants? = null,
    val project_url: String = "",
    val download_url: String = "",
    val filesize: Double = 0.0
) {
    fun getScreenshotUrl(): String? = screenshot?.getCardUrl()
}

// --- Tags & Survey responses ---

@SuppressWarnings("ConstructorParameterNaming")
data class TagsResponse(
    val data: List<TagItem> = emptyList()
)

data class TagItem(
    val id: String,
    val text: String
)

@SuppressWarnings("ConstructorParameterNaming")
data class SurveyResponse(
    val url: String? = null
)

// --- Media Library responses ---

@SuppressWarnings("ConstructorParameterNaming")
data class MediaLibraryResponse(
    val data: List<MediaCategoryPreview> = emptyList(),
    val next_cursor: String? = null,
    val has_more: Boolean = false
)

@SuppressWarnings("ConstructorParameterNaming")
data class MediaCategoryPreview(
    val id: String,
    val name: String,
    val description: String? = null,
    val priority: Int = 0,
    val asset_count: Int = 0,
    val preview_assets: List<MediaAssetResponse> = emptyList()
)

@SuppressWarnings("ConstructorParameterNaming")
data class MediaAssetResponse(
    val id: String,
    val name: String,
    val file_type: String = "",
    val extension: String = "",
    val download_url: String = "",
    val thumbnail: ImageVariants? = null,
    val category_name: String? = null,
    val flavors: List<String> = emptyList()
)
