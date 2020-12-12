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
import org.catrobat.catroid.retrofit.models.FeaturedProject
import org.catrobat.catroid.retrofit.WebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeaturedProjectsViewModel(webServer: WebService) : ViewModel() {
    private val projects = MutableLiveData<List<FeaturedProject>>()

    fun getFeaturedProjects(): LiveData<List<FeaturedProject>> = projects

    init {
        webServer.getFeaturedProjects().enqueue(object : Callback<List<FeaturedProject>> {
            override fun onResponse(
                call: Call<List<FeaturedProject>>,
                response: Response<List<FeaturedProject>>
            ) {
                Log.d(javaClass.simpleName, response.body().toString())
                response.body()?.let {
                    projects.postValue(it)
                }
            }

            override fun onFailure(call: Call<List<FeaturedProject>>, t: Throwable) {
                Log.w(javaClass.simpleName, "failed to fetch featured projects!!", t)
            }
        })
    }
}
