/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.utils.FileMetaDataExtractor
import java.io.File
import java.io.IOException

private const val TAG = "ProjectRenamer"

/**
 * Moves a project folder and rewrites the `<programName>` in its code.xml.
 *
 * If rewriting code.xml fails after the folder was moved, the folder is moved back, so the
 * project is never left half renamed. Returns the renamed directory, or null on failure.
 */
fun renameProject(projectDirectory: File, destinationName: String): File? {
    synchronized(XstreamSerializer.getSaveLock()) {
        val destinationDirectory = File(
            projectDirectory.parent,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(destinationName)
        )
        val codeFile = File(destinationDirectory, Constants.CODE_XML_FILE_NAME)

        if (!tryRenameTo(projectDirectory, destinationDirectory)) {
            return null
        }
        if (!tryRenameProject(codeFile, destinationName)) {
            if (!tryRenameTo(destinationDirectory, projectDirectory)) {
                Log.e(TAG, "Rollback of ${destinationDirectory.name} failed")
            }
            return null
        }

        ProjectManager.getInstance().moveChangedFlag(projectDirectory.name, destinationName)
        return destinationDirectory
    }
}

fun tryRenameTo(projectDirectory: File, destinationDirectory: File): Boolean {
    return try {
        if (isCaseOnlyRename(projectDirectory, destinationDirectory)) {
            renameViaTemporaryDirectory(projectDirectory, destinationDirectory)
        } else {
            projectDirectory.renameTo(destinationDirectory)
        }
    } catch (e: IOException) {
        Log.e(TAG, "Cannot rename project directory ${projectDirectory.absolutePath} to ${destinationDirectory.name}", e)
        false
    } catch (e: SecurityException) {
        Log.e(TAG, "Cannot rename project directory ${projectDirectory.absolutePath} to ${destinationDirectory.name}", e)
        false
    }
}

private fun isCaseOnlyRename(projectDirectory: File, destinationDirectory: File): Boolean =
    projectDirectory.name.equals(destinationDirectory.name, ignoreCase = true) &&
        projectDirectory.name != destinationDirectory.name

/**
 * On case-insensitive storage, "game" -> "Game" cannot be renamed directly, so the folder is
 * moved through a temporary name. If the second step fails, the folder is moved back.
 */
private fun renameViaTemporaryDirectory(projectDirectory: File, destinationDirectory: File): Boolean {
    val tempDirectory = File(
        projectDirectory.parentFile,
        "${projectDirectory.name}_tmp_${System.currentTimeMillis()}"
    )
    if (!projectDirectory.renameTo(tempDirectory)) {
        return false
    }
    if (tempDirectory.renameTo(destinationDirectory)) {
        return true
    }
    if (!tempDirectory.renameTo(projectDirectory)) {
        Log.e(TAG, "Cannot rollback temporary directory to ${projectDirectory.absolutePath}")
    }
    return false
}

fun tryRenameProject(codeFile: File, destinationName: String): Boolean {
    return try {
        XstreamSerializer.renameProject(codeFile, destinationName)
    } catch (e: IOException) {
        Log.e(TAG, "Cannot rename project in ${codeFile.absolutePath} to $destinationName", e)
        false
    }
}
