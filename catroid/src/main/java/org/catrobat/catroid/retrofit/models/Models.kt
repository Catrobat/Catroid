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

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json

// --- Cursor pagination envelope ---

data class CursorPaginatedResponse<T>(
    val data: List<T>,
    @Json(name = "next_cursor") val nextCursor: String? = null,
    @Json(name = "has_more") val hasMore: Boolean = false
)

// --- Image variants (responsive images from API v2) ---

data class ImageVariantSet(
    @Json(name = "avif_1x") val avif1x: String? = null,
    @Json(name = "avif_2x") val avif2x: String? = null,
    @Json(name = "webp_1x") val webp1x: String? = null,
    @Json(name = "webp_2x") val webp2x: String? = null
) {
    fun getBestUrl(): String? = webp2x ?: webp1x ?: avif2x ?: avif1x
}

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

// --- Featured projects (from /api/projects/featured) ---

data class FeaturedProjectApi(
    val id: String,
    @Json(name = "project_id") val projectId: String,
    @Json(name = "project_url") val projectUrl: String,
    val url: String? = null,
    val name: String,
    val author: String,
    @Json(name = "featured_image") val featuredImage: ImageVariants? = null
) {
    fun toRoomEntity(): FeaturedProject = FeaturedProject(
        id = id,
        projectId = projectId,
        projectUrl = projectUrl,
        name = name,
        author = author,
        featuredImage = featuredImage?.getDetailUrl() ?: featuredImage?.getCardUrl() ?: ""
    )
}

// Room entity (stores best image URL as string)
@Entity(tableName = "featured_project")
data class FeaturedProject(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "project_id") val projectId: String,
    @ColumnInfo(name = "project_url") val projectUrl: String,
    val name: String,
    val author: String,
    @ColumnInfo(name = "featured_image") val featuredImage: String
)

// --- Project categories (from /api/projects/categories) ---

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
    @ColumnInfo(name = "private")
    var isPrivate: Boolean,
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
    @Json(name = "projects_list") val projectsList: List<ProjectResponseApi>
)

// --- Project response (from /api/projects endpoints) ---

data class ProjectResponseApi(
    val id: String,
    val name: String,
    val author: String,
    @Json(name = "author_id") val authorId: String? = null,
    @Json(name = "scratch_id") val scratchId: Int? = null,
    val description: String = "",
    val credits: String = "",
    val version: String = "",
    val views: Int = 0,
    val downloads: Int = 0,
    val reactions: Int = 0,
    val comments: Int = 0,
    @Json(name = "private")
    val isPrivate: Boolean = false,
    val flavor: String = "",
    val tags: Map<String, String>? = null,
    val extensions: Map<String, String>? = null,
    @Json(name = "uploaded_at") val uploadedAt: String? = null,
    @Json(name = "uploaded_string") val uploadedString: String = "",
    val screenshot: ImageVariants? = null,
    @Json(name = "project_url") val projectUrl: String = "",
    @Json(name = "download_url") val downloadUrl: String = "",
    val filesize: Double = 0.0,
    @Json(name = "not_for_kids") val notForKids: Int = 0,
    @Json(name = "retention_days") val retentionDays: Int? = null,
    @Json(name = "retention_expiry") val retentionExpiry: String? = null
) {
    fun getScreenshotUrl(): String? = screenshot?.getCardUrl()
}

// --- Upload response (minimal — avoids PHP empty-array vs object mismatch on tags) ---

data class ProjectUploadResponse(
    val id: String = ""
)

// --- Tags & Survey responses ---

data class TagsResponse(
    val data: List<TagItem> = emptyList()
)

data class TagItem(
    val id: String,
    val text: String
)

data class SurveyResponse(
    val url: String? = null
)

// --- Media Library responses ---

data class MediaCategoryPreview(
    val id: String,
    val name: String,
    val description: String? = null,
    val priority: Int = 0,
    @Json(name = "assets_count") val assetsCount: Int = 0,
    @Json(name = "preview_assets") val previewAssets: List<MediaAssetResponse> = emptyList()
)

data class MediaAssetResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    @Json(name = "file_type") val fileType: String = "",
    val extension: String = "",
    val size: Int = 0,
    val author: String? = null,
    val downloads: Int = 0,
    val active: Boolean = true,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
    @Json(name = "category_name") val categoryName: String? = null,
    val flavors: List<String> = emptyList(),
    @Json(name = "download_url") val downloadUrl: String = "",
    val thumbnail: ImageVariants? = null
)
