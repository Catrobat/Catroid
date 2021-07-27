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
package org.catrobat.catroid.ui.filepicker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import java.io.File
import java.util.ArrayList

class ProjectFileLister {

    @JvmOverloads
    fun listProjectFilesAsync(
        startDir: List<File>,
        onListProjectsComplete: (List<File>) -> Unit = {},
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        dispatcherOnFinished: CoroutineDispatcher = Dispatchers.Main
    ) {
        scope.launch {
            val projects = listProjectFiles(startDir)
            withContext(dispatcherOnFinished) {
                onListProjectsComplete(projects)
            }
        }
    }

    fun listProjectFiles(startDir: List<File>): List<File> {
        val files: ArrayList<File> = ArrayList()
        startDir.forEach { file ->
            findProjectFiles(file, files)
        }
        getAllProjectsFromPocketCodeFolder(files)
        return files
    }

    private fun findProjectFiles(dir: File, projectFiles: ArrayList<File>) {
        // this check will prevent a future crash on android 11
        if (dir.canRead() && dir.listFiles() != null) {
            dir.walk().forEach {
                if (it.name.endsWith(Constants.CATROBAT_EXTENSION)) {
                    projectFiles.add(it)
                }
            }
        }
    }

    private fun getAllProjectsFromPocketCodeFolder(projectFiles: ArrayList<File>) {
        FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY.walk().forEach {
            if (validProjectFile(it)) projectFiles.add(it)
        }
    }

    private fun validProjectFile(file: File): Boolean {
        return file.name != Constants.BACKPACK_DIRECTORY_NAME &&
            file.name != Constants.TMP_DIR_NAME &&
            file.isDirectory &&
            File(file, Constants.CODE_XML_FILE_NAME).exists()
    }
}
