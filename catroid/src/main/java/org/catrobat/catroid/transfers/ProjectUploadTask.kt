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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofit.models.ProjectUploadResponseApi
import org.catrobat.catroid.web.ServerAuthenticationConstants.SERVER_RESPONSE_REGISTER_OK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProjectUploadTask(private val webServer: WebService) {
    private val projectUploadResponse = MutableLiveData<ProjectUploadResponseApi>()
    fun getProjectUploadResponse(): LiveData<ProjectUploadResponseApi> = projectUploadResponse

    private var errorMessage: String = String()
    fun getErrorMessage(): String = errorMessage

    fun uploadProject(
        projectZip: File,
        checksum: String,
        idToken: String,
        flavor: String? = null,
        isPrivate: Boolean? = null
    ) {
        Log.d(TAG, "Starting project upload")

        val requestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            projectZip
        )

        val body = MultipartBody.Part.createFormData("file", projectZip.name, requestBody)

        val map: HashMap<String, RequestBody> = HashMap()
        map["checksum"] = RequestBody.create(MultipartBody.FORM, checksum)

        if (isPrivate != null) {
            map["private"] = RequestBody.create(MultipartBody.FORM, isPrivate.toString())
        }

        if (flavor != null) {
            map["flavor"] = RequestBody.create(MultipartBody.FORM, flavor)
        }

        val uploadProjectCall: Call<ProjectUploadResponseApi> = webServer.uploadProject(
            "Bearer $idToken", map, body
        )

        uploadProjectCall.enqueue(object : Callback<ProjectUploadResponseApi> {
            override fun onResponse(
                call: Call<ProjectUploadResponseApi>,
                response: Response<ProjectUploadResponseApi>
            ) {
                when (val statusCode = response.code()) {
                    SERVER_RESPONSE_REGISTER_OK -> {
                        val tokenReceived = response.body()?.id
                        tokenReceived?.let {
                            projectUploadResponse.postValue(response.body())
                        } ?: run {
                            errorMessage = "Project Upload failed"
                            projectUploadResponse.postValue(null)
                        }
                    }
                    else -> {
                        Log.e(
                            TAG,
                            "Not accepted StatusCode: $statusCode on project upload; Server " + "Answer: ${response.body()}"
                        )
                        errorMessage = "Project could not be uploaded!"
                        projectUploadResponse.postValue(null)
                    }
                }
            }

            override fun onFailure(call: Call<ProjectUploadResponseApi>, throwable: Throwable) {
                Log.e(TAG, "onFailure $throwable.message")
                errorMessage = throwable.message.orEmpty()
                projectUploadResponse.postValue(null)
            }
        })
    }

    fun clear() {
        projectUploadResponse.value = null
    }

    companion object {
        private val TAG = ProjectUploadTask::class.java.simpleName
    }
}
