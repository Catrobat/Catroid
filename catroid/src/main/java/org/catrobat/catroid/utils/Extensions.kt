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

package org.catrobat.catroid.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import org.catrobat.catroid.retrofit.models.ProjectCategoryWithResponses
import org.catrobat.catroid.retrofit.models.ProjectResponse
import org.catrobat.catroid.retrofit.models.ProjectResponseApi
import org.catrobat.catroid.retrofit.models.ProjectsCategory
import org.catrobat.catroid.retrofit.models.ProjectsCategoryApi

fun ImageView.loadImageFromUrl(url: String) {
    Glide.with(context)
        .load(url)
        .apply {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        }
        .centerInside()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun Context.dpToPx(dp: Float): Float {
    val displayMetrics = resources.displayMetrics
    val pixelScaleFactor = displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT
    return dp * pixelScaleFactor
}

fun View.setVisibleOrGone(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun List<ProjectResponseApi>.toProjectResponsesList(projectType: String): List<ProjectResponse> {
    return this.map { src ->
        ProjectResponse(
            id = src.id,
            name = src.name,
            author = src.author,
            description = src.description,
            version = src.version,
            views = src.views,
            download = src.download,
            private = src.private,
            flavor = src.flavor,
            tags = src.tags,
            uploaded = src.uploaded,
            uploadedString = src.uploaded_string,
            screenshotSmall = src.screenshot_small,
            screenshotLarge = src.screenshot_large,
            projectUrl = src.project_url,
            downloadUrl = src.download_url,
            fileSize = src.filesize,
            categoryType = projectType
        )
    }.toMutableList()
}

fun ProjectsCategoryApi.convertToProjectsCategory() = ProjectsCategory(this.type, this.name)

fun ProjectsCategoryApi.toProjectCategoryWithResponses(): ProjectCategoryWithResponses {
    return ProjectCategoryWithResponses(
        this.convertToProjectsCategory(),
        this.projectsList.toProjectResponsesList(type)
    )
}

fun List<ProjectsCategoryApi>.toProjectCategoryWithResponsesList() =
    this.map { it.toProjectCategoryWithResponses() }.toMutableList()
