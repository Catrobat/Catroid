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
package org.catrobat.catroid.io.asynctask

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer

import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

class ProjectImporter @JvmOverloads constructor(
    private val destinationDirectory: File = DEFAULT_ROOT_DIRECTORY
) {

    private var onFinishedListener: WeakReference<(Boolean) -> Unit>? = null

    companion object {
        val TAG: String = ProjectImporter::class.java.simpleName
    }

    fun setListener(listener: (Boolean) -> Unit): ProjectImporter {
        onFinishedListener = WeakReference(listener)
        return this
    }

    fun importProjectsAsync(
        files: List<File?>,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): Job {
        return scope.launch {
            val success = importProjects(files)
            withContext(Dispatchers.Main) {
                onFinishedListener?.get()?.invoke(success)
            }
        }
    }

    fun importProjects(files: List<File?>): Boolean {
        var success = true
        for (projectDir in files) {
            projectDir ?: continue
            success = success && importProject(projectDir)
        }
        return success
    }

    private fun importProject(projectDir: File): Boolean {
        val projectName = getProjectName(projectDir) ?: return false

        val uniqueProjectName = UniqueNameProvider().getUniqueName(
            projectName, FileMetaDataExtractor.getProjectNames(destinationDirectory))

        val destinationDirectory = File(
            destinationDirectory,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(uniqueProjectName))

        return try {
            copyProject(projectDir, destinationDirectory, uniqueProjectName)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Something went wrong while importing project ${projectDir.name}", e)
            cleanUpDirectory(projectDir, destinationDirectory)
            false
        }
    }

    private fun getProjectName(projectDir: File): String? {
        val xmlFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
        if (!xmlFile.exists()) {
            Log.e(TAG, "No xml file found for project ${projectDir.name}")
            return null
        }

        return try {
            ProjectMetaDataParser(xmlFile).projectMetaData.name
        } catch (e: IOException) {
            Log.d(TAG, "Cannot extract projectName from xml", e)
            null
        }
    }

    private fun copyProject(projectDir: File, destinationDirectory: File, projectName: String) {
        StorageOperations.copyDir(projectDir, destinationDirectory)
        XstreamSerializer.renameProject(
            File(destinationDirectory, Constants.CODE_XML_FILE_NAME), projectName)
    }

    private fun cleanUpDirectory(projectDir: File, destinationDirectory: File) {
        if (destinationDirectory.isDirectory) {
            Log.e(TAG, "Folder exists, trying to delete folder.")
            try {
                StorageOperations.deleteDir(projectDir)
            } catch (deleteException: IOException) {
                Log.e(TAG, "Cannot delete folder $projectDir", deleteException)
            }
        }
    }
}
