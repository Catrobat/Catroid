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

package org.catrobat.catroid.ui.recyclerview.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.launch
import org.catrobat.catroid.R
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.ShareProject
import org.catrobat.catroid.retrofit.models.ShareCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesViewModel(private val webServer: WebService) : ViewModel() {
    private val categories = mutableMapOf<String, Int>()
        .apply {
            put("recent", R.string.main_menu_category_recent)
            put("most_downloaded", R.string.main_menu_category_most_downloaded)
            put("most_viewed", R.string.main_menu_category_most_viewed)
            put("scratch", R.string.main_menu_category_scratch_remixes)
            put("random", R.string.main_menu_category_random)
        }

    private val shareCategories = MutableLiveData<List<ShareCategory>>()

    fun getShareCategories(): LiveData<List<ShareCategory>> = shareCategories

    init {
        GlobalScope.launch {
            categories.map {
                fetchCategory(it.key)?.let { projects ->
                    ShareCategory(it.value, projects)
                }
            }
                .filterNotNull()
                .toList()
                .let {
                    shareCategories.postValue(it)
                }
        }
    }

    private fun fetchCategory(categoryName: String): List<ShareProject>? {
        return webServer.getProjectCategory(categoryName).execute().body()
    }
}
