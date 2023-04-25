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

package org.catrobat.catroid.transfers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.ProjectResponse
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_TOKEN_OK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetUserProjectsTask(private val webServer: WebService) {
    private val projectResponse = MutableLiveData<List<ProjectResponse>>()
    fun getUserProjectsResponse(): LiveData<List<ProjectResponse>> = projectResponse

    private var errorMessage: String = String()
    fun getErrorMessage(): String = errorMessage

    fun getProjectsFromUser(idToken: String) {
        Log.d(TAG, "Starting getUserProjects")

        val getUserProjectsCall: Call<List<ProjectResponse>> = webServer.getUserProjects(
            "Bearer $idToken"
        )

        getUserProjectsCall.enqueue(object : Callback<List<ProjectResponse>> {
            override fun onResponse(
                call: Call<List<ProjectResponse>>,
                response: Response<List<ProjectResponse>>
            ) {
                when (val statusCode = response.code()) {
                    SERVER_RESPONSE_TOKEN_OK -> {
                        if (response.body()?.isEmpty() == true) {
                            Log.d(TAG, "User has no uploaded projects")
                        }
                        Log.d(TAG, response.body().toString())
                        projectResponse.postValue(response.body())
                    }
                    else -> {
                        Log.e(
                            TAG,
                            "Not accepted StatusCode: $statusCode on getting users projects; " +
                                "Server" + " " + "Answer: ${response.body()}"
                        )
                        errorMessage = "Get User Projects failed!"
                        projectResponse.postValue(null)
                    }
                }
            }

            override fun onFailure(call: Call<List<ProjectResponse>>, throwable: Throwable) {
                Log.e(TAG, "onFailure ${throwable.message}")
                errorMessage = throwable.message.orEmpty()
                projectResponse.postValue(null)
            }
        })
    }

    fun clear() {
        projectResponse.value = null
    }

    companion object {
        private val TAG = GetUserProjectsTask::class.java.simpleName
    }
}
