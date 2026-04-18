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

package org.catrobat.catroid.transfers.project

import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.catrobat.catroid.common.Constants.DEVICE_VARIABLE_JSON_FILE_NAME
import org.catrobat.catroid.common.Constants.UPLOAD_IMAGE_SCALE_HEIGHT
import org.catrobat.catroid.common.Constants.UPLOAD_IMAGE_SCALE_WIDTH
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.utils.ImageEditing
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

typealias UploadProjectSuccessCallback = (projectId: String) -> Unit
typealias UploadProjectErrorCallback = (errorCode: Int, errorMessage: String) -> Unit

class ProjectUpload(
    private val projectDirectory: File,
    private val projectName: String,
    private val projectDescription: String,
    private val sceneNames: Array<String>?,
    private val archiveDirectory: File,
    private val zipArchiver: ZipArchiver,
    private val screenshotLoader: ProjectAndSceneScreenshotLoader,
    private val webService: WebService
) {

    fun start(
        successCallback: UploadProjectSuccessCallback,
        errorCallback: UploadProjectErrorCallback
    ) {
        val projectArchive = zipProjectToArchive(projectDirectory, archiveDirectory)
        if (projectArchive == null) {
            errorCallback(UPLOAD_ZIP_ERROR, UPLOAD_ZIP_ERROR_MESSAGE)
            return
        }

        scaleSceneScreenshots(projectName, sceneNames)

        try {
            val checksum = Utils.md5Checksum(projectArchive)
            val filePart = MultipartBody.Part.createFormData(
                "file",
                UPLOAD_FILE_NAME,
                RequestBody.create(MediaType.parse("application/zip"), projectArchive)
            )
            val checksumPart = RequestBody.create(MediaType.parse("text/plain"), checksum)

            val response = webService.uploadProject(filePart, checksumPart).execute()

            if (response.isSuccessful) {
                val body = response.body()
                val projectId = body?.id ?: ""
                successCallback(projectId)
            } else {
                val errorBody = response.errorBody()?.string() ?: UPLOAD_FAILED_MESSAGE
                errorCallback(response.code(), errorBody)
            }
        } catch (e: Exception) {
            Log.e(TAG, UPLOAD_FAILED_MESSAGE, e)
            errorCallback(UPLOAD_NETWORK_ERROR, e.message ?: UPLOAD_FAILED_MESSAGE)
        } finally {
            if (!projectArchive.delete()) {
                Log.w(TAG, "Failed to delete project archive: ${projectArchive.absolutePath}")
            }
        }
    }

    private fun scaleSceneScreenshots(projectName: String, sceneNames: Array<String>?) {
        sceneNames?.mapNotNull { screenshotLoader.getScreenshotFile(projectName, it, false) }
            ?.filter { it.exists() && it.length() > 0 }
            ?.forEach {
                try {
                    ImageEditing.scaleImageFile(it, UPLOAD_IMAGE_SCALE_WIDTH, UPLOAD_IMAGE_SCALE_HEIGHT)
                } catch (ex: FileNotFoundException) {
                    Log.e(TAG, Log.getStackTraceString(ex))
                }
            }
    }

    private fun zipProjectToArchive(projectDirectory: File, archiveDirectory: File): File? {
        return try {
            val fileList = projectDirectory.listFiles()
            val filteredFileList = fileList.filter { file -> file.name != DEVICE_VARIABLE_JSON_FILE_NAME }

            zipArchiver.zip(archiveDirectory, filteredFileList.toTypedArray())
            archiveDirectory
        } catch (ioException: IOException) {
            Log.e(TAG, Log.getStackTraceString(ioException))
            archiveDirectory.delete()
            null
        }
    }

    companion object {
        private val TAG = ProjectUpload::class.java.simpleName
        const val UPLOAD_ZIP_ERROR = 32_202
        const val UPLOAD_ZIP_ERROR_MESSAGE = "Failed to zip directory for upload"
        const val UPLOAD_FAILED_MESSAGE = "Upload failed"
        const val UPLOAD_NETWORK_ERROR = 32_203
    }
}
