/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.io

import android.util.Log
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.io.asynctask.ProjectLoader
import java.io.File
import java.io.IOException

class LookFileGarbageCollector {
    fun cleanUpUnusedLookFiles(project: Project) {
        synchronized(LOCK) {
            project.sceneList.forEach { scene ->
                deleteUnusedLookFiles(scene, getAllFileNamesToKeep(scene))
            }
        }
    }

    private fun getAllFileNamesToKeep(scene: Scene): List<String> = getLookDataFileNames(scene) + getOtherFileNamesToKeep()

    private fun getLookDataFileNames(scene: Scene): List<String> {
        return scene.spriteList.flatMap { sprite ->
            sprite.lookList.flatMap { lookData ->
                listOf(lookData.file.name)
            }
        }
    }

    private fun getOtherFileNamesToKeep(): List<String> = listOf(".nomedia")

    private fun deleteUnusedLookFiles(scene: Scene, fileNamesToKeep: List<String>) {
        val imageDirectory = File(scene.directory, Constants.IMAGE_DIRECTORY_NAME)
        val imageDirectoryFileList = imageDirectory.listFiles() ?: return
        imageDirectoryFileList.filter { file -> !fileNamesToKeep.contains(file.name) }
            .forEach { file -> tryDeleteLookFile(file) }
    }

    private fun tryDeleteLookFile(file: File) {
        try {
            StorageOperations.deleteFile(file)
        } catch (e: IOException) {
            Log.e(
                ProjectLoader.TAG, "Error while deleting file ${file.name} during " +
                    "cleanup of unused LookFile", e
            )
        }
    }

    companion object {
        val LOCK = Object()
    }
}
