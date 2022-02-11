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
package org.catrobat.catroid.io.asynctask

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.common.Constants.CACHE_DIR
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.ZipArchiver
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
    val cachedProjectDir = File(CACHE_DIR, StorageOperations.getSanitizedFileName(projectDir.name))
    if (cachedProjectDir.isDirectory) {
        StorageOperations.deleteDir(cachedProjectDir)
    }
    ZipArchiver().unzip(projectDir, cachedProjectDir)
    ProjectImportTask.task(listOf(cachedProjectDir))
} catch (e: IOException) {
    Log.e(TAG, "Cannot unzip project " + projectDir.name, e)
    false
}
