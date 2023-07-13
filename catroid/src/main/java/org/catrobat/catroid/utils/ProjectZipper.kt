/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.utils

import android.util.Log
import org.catrobat.catroid.common.Constants.DEVICE_VARIABLE_JSON_FILE_NAME
import org.catrobat.catroid.io.ZipArchiver
import java.io.File
import java.io.IOException

/**
 * This class converts a Catroid project to a zip file
 */
object ProjectZipper {

    private val TAG = ProjectZipper::class.java.simpleName

    fun zipProjectToArchive(projectDirectory: File, archiveDirectory: File): File? {
        Log.d(TAG, "Zipping project...")

        return try {
            val fileList = projectDirectory.listFiles()
            val filteredFileList =
                fileList.filter { file -> file.name != DEVICE_VARIABLE_JSON_FILE_NAME }
            ZipArchiver().zip(archiveDirectory, filteredFileList.toTypedArray())
            Log.d(TAG, "Zipping done")
            archiveDirectory
        } catch (ioException: IOException) {
            Log.e(TAG, Log.getStackTraceString(ioException))
            archiveDirectory.delete()
            null
        }
    }
}
