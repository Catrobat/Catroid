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

package org.catrobat.catroid.web.requests

import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.transfers.project.ProjectUploadData
import org.catrobat.catroid.transfers.project.UPLOAD_FILE_NAME
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.web.ServerCalls

private const val FILE_UPLOAD_TAG = "upload"
private const val PROJECT_NAME_TAG = "projectTitle"
private const val PROJECT_DESCRIPTION_TAG = "projectDescription"
private const val PROJECT_CHECKSUM_TAG = "fileChecksum"
private const val USER_EMAIL = "userEmail"
private const val DEVICE_LANGUAGE = "deviceLanguage"
private val MEDIA_TYPE_ZIPFILE = MediaType.parse("application/zip")
private const val FILE_UPLOAD_URL = FlavoredConstants.BASE_UPLOAD_URL + "api/upload/upload.json"

fun createUploadRequest(
    uploadData: ProjectUploadData
): Request {

    Log.v(ServerCalls.TAG, "Building request to upload to: $FILE_UPLOAD_URL")

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            FILE_UPLOAD_TAG, UPLOAD_FILE_NAME,
            RequestBody.create(MEDIA_TYPE_ZIPFILE, uploadData.projectArchive)
        )
        .addFormDataPart(PROJECT_NAME_TAG, uploadData.projectName)
        .addFormDataPart(PROJECT_DESCRIPTION_TAG, uploadData.projectDescription)
        .addFormDataPart(USER_EMAIL, uploadData.userEmail)
        .addFormDataPart(PROJECT_CHECKSUM_TAG, Utils.md5Checksum(uploadData.projectArchive))
        .addFormDataPart(Constants.TOKEN, uploadData.token)
        .addFormDataPart(Constants.USERNAME, uploadData.username)
        .addFormDataPart(DEVICE_LANGUAGE, uploadData.language)
        .build()

    return Request.Builder()
        .url(FILE_UPLOAD_URL)
        .post(requestBody)
        .build()
}
