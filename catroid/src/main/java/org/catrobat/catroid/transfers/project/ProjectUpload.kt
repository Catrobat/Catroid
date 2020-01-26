/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.content.SharedPreferences
import android.util.Log
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.DEVICE_VARIABLE_JSON_FILENAME
import org.catrobat.catroid.common.Constants.UPLOAD_IMAGE_SCALE_HEIGHT
import org.catrobat.catroid.common.Constants.UPLOAD_IMAGE_SCALE_WIDTH
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.utils.ImageEditing
import org.catrobat.catroid.web.ServerCalls
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Locale

typealias UploadProjectSuccessCallback = (projectId: String) -> Unit
typealias UploadProjectErrorCallback = (errorCode: Int, errorMessage: String) -> Unit

class ProjectUpload(
    private val projectDirectory: File,
    private val projectName: String,
    private val projectDescription: String,
    private val userEmail: String,
    private val sceneNames: Array<String>?,
    private val archiveDirectory: File,
    private val zipArchiver: ZipArchiver,
    private val screenshotLoader: ProjectAndSceneScreenshotLoader,
    private val sharedPreferences: SharedPreferences,
    private val serverCalls: ServerCalls
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

        val projectUploadData = createUploadData(projectArchive)

        scaleSceneScreenshots(projectName, sceneNames)

        serverCalls.uploadProject(
            projectUploadData,
            { projectId, successUsername, successToken ->
                sharedPreferences.edit()
                    .putString(Constants.TOKEN, successToken)
                    .putString(Constants.USERNAME, successUsername)
                    .apply()

                successCallback(projectId)
                projectArchive.delete()
            },
            { errorCode, errorMessage ->
                errorCallback(
                    errorCode,
                    errorMessage
                )
            }
        )
    }

    private fun createUploadData(projectArchive: File): ProjectUploadData {
        val token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN)
        val username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME)

        return ProjectUploadData(
            projectName = projectName,
            projectDescription = projectDescription,
            projectArchive = projectArchive,
            userEmail = userEmail,
            language = Locale.getDefault().language,
            token = token ?: Constants.NO_TOKEN,
            username = username ?: Constants.NO_USERNAME
        )
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
            val filteredFileList = fileList.filter { file -> file.name != DEVICE_VARIABLE_JSON_FILENAME }

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
    }
}
