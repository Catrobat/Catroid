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
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.koin.java.KoinJavaComponent
import java.io.File
import java.io.IOException

private val TAG = ProjectRenamer::class.java.simpleName

class ProjectRenamer(
    private val projectDirectory: File,
    private val destinationName: String
) {
    @JvmOverloads
    fun renameProjectAsync(
        onRenameProjectComplete: (Boolean) -> Unit = {},
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        dispatcherOnFinished: CoroutineDispatcher = Dispatchers.Main
    ) {
        scope.launch {
            val success = renameProject(projectDirectory, destinationName) != null
            withContext(dispatcherOnFinished) {
                onRenameProjectComplete(success)
            }
        }
    }
}

fun renameProject(projectDirectory: File, destinationName: String): File? {
    val destinationDirectory = File(
        projectDirectory.parent,
        FileMetaDataExtractor.encodeSpecialCharsForFileSystem(destinationName)
    )
    val file = File(
        destinationDirectory,
        Constants.CODE_XML_FILE_NAME
    )

    val success = tryRenameTo(projectDirectory, destinationDirectory) && tryRenameProject(
        file,
        destinationName
    )
    if (!success) {
        return null
    }
    val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)
    projectManager.moveChangedFlag(projectDirectory.name, destinationName)
    return destinationDirectory
}

fun tryRenameTo(projectDirectory: File, destinationDirectory: File): Boolean {
    return try {
        projectDirectory.renameTo(destinationDirectory)
        true
    } catch (e: IOException) {
        Log.e(TAG, "Cannot rename project directory ${projectDirectory.absolutePath} to ${destinationDirectory.name}", e)
        false
    }
}

fun tryRenameProject(projectDirectory: File, destinationName: String): Boolean {
    return try {
        XstreamSerializer.renameProject(projectDirectory, destinationName)
        true
    } catch (e: IOException) {
        Log.e(TAG, "Cannot rename project directory ${projectDirectory.absolutePath} to $destinationName", e)
        false
    }
}
