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

import org.catrobat.catroid.common.Constants
import java.io.File
import java.util.ArrayList

object ProjectImages {

    fun isImage(file: String): Boolean {
        return !file.endsWith(Constants.SCREENSHOT_AUTOMATIC_FILE_NAME) &&
            (file.endsWith(".jpg") || file.endsWith(".JPG") ||
            file.endsWith(".jpeg") || file.endsWith(".JPEG") ||
            file.endsWith(".png") || file.endsWith(".PNG") ||
            file.endsWith(".bmp") || file.endsWith(".BMP"))
    }

    fun getImagePathsFromDirectory(imagePaths: ArrayList<String>, file: File): ArrayList<String> {
        val directoryFiles = file.listFiles()
        if (directoryFiles != null) {
            for (i in directoryFiles.indices) {
                if (directoryFiles[i].isDirectory) {
                    getImagePathsFromDirectory(imagePaths, directoryFiles[i])
                } else if (isImage(directoryFiles[i].path)) {
                    imagePaths.add(directoryFiles[i].path)
                }
            }
        }
        return imagePaths
    }
}
