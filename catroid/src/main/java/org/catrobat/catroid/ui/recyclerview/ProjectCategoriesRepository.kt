/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.catrobat.catroid.R
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.FeaturedProject
import org.catrobat.catroid.retrofit.models.ProjectCategory
import org.catrobat.catroid.retrofit.models.ProjectResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface DefaultProjectCategoriesRepository {

    fun getProjectCategories(): LiveData<List<ProjectCategory>>
    fun getFeaturedProjects(): LiveData<List<FeaturedProject>>
    fun triggerFreshUpdate()
}

class ProjectCategoriesRepository(
    private val webServer: WebService
) : DefaultProjectCategoriesRepository {

    companion object {
        const val RECENT_CATEGORY = "recent"
        const val RANDOM_CATEGORY = "random"
        const val MOST_VIEWED_CATEGORY = "most_viewed"
        const val MOST_DOWNLOADED_CATEGORY = "most_downloaded"
        const val SCRATCH_CATEGORY = "scratch"
        const val RECOMMENDED_CATEGORY = "recommended"

        val categoryPairList = mutableListOf<Pair<String, Int>>()
            .apply {
                add(Pair(RECENT_CATEGORY, R.string.main_menu_category_recent))
                add(Pair(MOST_VIEWED_CATEGORY, R.string.main_menu_category_most_viewed))
                add(Pair(MOST_DOWNLOADED_CATEGORY, R.string.main_menu_category_most_downloaded))
                add(Pair(SCRATCH_CATEGORY, R.string.main_menu_category_scratch_remixes))
                add(Pair(RANDOM_CATEGORY, R.string.main_menu_category_random))
                add(Pair(RECOMMENDED_CATEGORY, R.string.main_menu_category_recommended))
            }
    }

    private val lazySetOfCategories = MutableLiveData<List<ProjectCategory>>()
    private val categoriesList = mutableListOf<ProjectCategory>()
    private val featuredProjects = MutableLiveData<List<FeaturedProject>>()

    private fun fetchFeaturedProjects() {
        webServer.getFeaturedProjects().enqueue(object : Callback<List<FeaturedProject>> {
            override fun onResponse(
                call: Call<List<FeaturedProject>>,
                response: Response<List<FeaturedProject>>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    featuredProjects.postValue(body)
                }
            }

            override fun onFailure(call: Call<List<FeaturedProject>>, t: Throwable) {
                Log.w(javaClass.simpleName, "failed to fetch featured projects!!", t)
            }
        })
    }

    private fun fetchCategories() {
        categoriesList.clear()
        categoryPairList.forEach {
            fetchCategory(it.first, it.second)
        }
    }

    private fun fetchCategory(categoryType: String, categoryNameId: Int) {
        webServer.getProjectCategory(categoryType)
            .enqueue(object : Callback<List<ProjectResponse>> {
                override fun onResponse(
                    call: Call<List<ProjectResponse>>,
                    response: Response<List<ProjectResponse>>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        val category = ProjectCategory(categoryType, categoryNameId, body)
                        appendCategory(category)
                    }
                }

                override fun onFailure(call: Call<List<ProjectResponse>>, t: Throwable) {
                    Log.w(
                        javaClass.simpleName,
                        "failed to fetch project category $categoryType!!",
                        t
                    )
                }
            })
    }

    private fun appendCategory(projectCategory: ProjectCategory) {
        categoriesList.add(projectCategory)
        lazySetOfCategories.postValue(categoriesList)
    }

    override fun getProjectCategories(): LiveData<List<ProjectCategory>> = lazySetOfCategories

    override fun getFeaturedProjects(): LiveData<List<FeaturedProject>> = featuredProjects

    override fun triggerFreshUpdate() {
        fetchFeaturedProjects()
        fetchCategories()
    }
}
