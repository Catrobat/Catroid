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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.StorageOperations.copyDir
import org.catrobat.catroid.io.StorageOperations.deleteDir
import org.catrobat.catroid.io.XstreamSerializer.renameProject
import org.catrobat.catroid.utils.FileMetaDataExtractor.encodeSpecialCharsForFileSystem
import org.koin.java.KoinJavaComponent
import java.io.File
import java.io.IOException

class ProjectCopier(
    private val sourceDir: File,
    private val destinationName: String
) {

    @JvmOverloads
    fun copyProjectAsync(
        onCopyProjectComplete: (Boolean) -> Unit = {},
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        dispatcherOnFinished: CoroutineDispatcher = Dispatchers.Main
    ) {
        scope.launch {
            val success = copyProject(sourceDir, destinationName)
            withContext(dispatcherOnFinished) {
                onCopyProjectComplete(success)
            }
        }
    }

    private fun copyProject(sourceDir: File, destinationName: String): Boolean {
        val destinationDir = File(
            sourceDir.parentFile,
            encodeSpecialCharsForFileSystem(destinationName)
        )
        return try {
            copyDir(sourceDir, destinationDir)
            renameProject(File(destinationDir, Constants.CODE_XML_FILE_NAME), destinationName)
            val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)
            projectManager.addNewDownloadedProject(destinationName)
            true
        } catch (e: IOException) {
            Log.e(TAG, "Something went wrong while copying ${sourceDir.absolutePath} to $destinationName", e)
            if (destinationDir.isDirectory) {
                Log.e(TAG, "Folder exists, trying to delete folder.")
                try {
                    deleteDir(destinationDir)
                } catch (deleteException: IOException) {
                    Log.e(TAG, "Cannot delete folder $destinationName", deleteException)
                }
            }
            false
        }
    }

    companion object {
        val TAG = ProjectCopier::class.java.simpleName
    }
}
