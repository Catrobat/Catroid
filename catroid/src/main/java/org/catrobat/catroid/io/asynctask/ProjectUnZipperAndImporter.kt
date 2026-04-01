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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.CACHE_DIRECTORY
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.utils.FileMetaDataExtractor
import java.io.File
import java.io.IOException

private val TAG = ProjectUnZipperAndImporter::class.java.simpleName

class ProjectUnZipperAndImporter @JvmOverloads constructor(
    val onImportFinished: (Boolean) -> Unit = {},
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    fun unZipAndImportAsync(files: Array<File>) {
        scope.launch {
            val success = unzipAndImportProjects(files)
            withContext(Dispatchers.Main) {
                onImportFinished(success)
            }
        }
    }
}

fun unzipAndImportProjects(files: Array<File>): Boolean {
    var success = true
    files.forEach { projectDir ->
        success = success && unzipAndImportProject(projectDir)
    }
    return success
}

private fun unzipAndImportProject(projectDir: File): Boolean = try {
    val cachedProjectDir = File(CACHE_DIRECTORY, StorageOperations.getSanitizedFileName(projectDir.name))
    if (cachedProjectDir.isDirectory) {
        StorageOperations.deleteDir(cachedProjectDir)
    }
    ZipArchiver().unzip(projectDir, cachedProjectDir)
    importProject(cachedProjectDir)
} catch (e: IOException) {
    Log.e(TAG, "Cannot unzip project " + projectDir.name, e)
    false
}

private fun getProjectName(projectDir: File): String? {
    val xmlFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
    if (!xmlFile.exists()) {
        Log.e(TAG, "No xml file found for project " + projectDir.name)
        return null
    }
    return try {
        ProjectMetaDataParser(xmlFile).projectMetaData.name
    } catch (e: IOException) {
        Log.d(TAG, "Cannot extract projectName from xml", e)
        null
    }
}

private fun importProject(projectDir: File): Boolean {
    var projectName = getProjectName(projectDir) ?: return false
    projectName = UniqueNameProvider().getUniqueName(projectName, FileMetaDataExtractor
        .getProjectNames(FlavoredConstants.DEFAULT_ROOT_DIRECTORY))
    val destinationDirectory = File(
        FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
        FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName))
    return try {
        copyProject(projectDir, destinationDirectory, projectName)
        true
    } catch (e: IOException) {
        Log.e(TAG, "Something went wrong while importing project ${projectDir.name}", e)
        errorWhileImporting(projectDir, destinationDirectory)
        false
    }
}

private fun copyProject(projectDir: File, destinationDirectory: File, projectName: String) {
    StorageOperations.copyDir(projectDir, destinationDirectory)
    XstreamSerializer.renameProject(File(destinationDirectory, Constants.CODE_XML_FILE_NAME), projectName)
}

private fun errorWhileImporting(projectDir: File, destinationDirectory: File) {
    if (destinationDirectory.isDirectory) {
        Log.e(TAG, "Folder exists, trying to delete folder.")
        try {
            StorageOperations.deleteDir(projectDir)
        } catch (deleteException: IOException) {
            Log.e(TAG, "Cannot delete folder $projectDir", deleteException)
        }
    }
}
