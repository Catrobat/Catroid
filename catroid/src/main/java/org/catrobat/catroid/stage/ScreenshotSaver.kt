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

package org.catrobat.catroid.stage

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.badlogic.gdx.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.IOException

interface ScreenshotSaverCallback {
    fun screenshotSaved(success: Boolean)
}

class ScreenshotSaver(
    private val gdxFileHandler: Files,
    private val folder: String,
    private val width: Int,
    private val height: Int
) {

    companion object {
        private val TAG = ScreenshotSaver::class.java.simpleName
        private const val IMAGE_QUALITY = 100
        private const val NUMBER_OF_COLORS = 4
        private val VALID_FILENAME_REGEX = Regex("^[^<>:;,?\"*|/]+\$")
        private val ONLY_WHITESPACE_REGEX = Regex("\\s*")
    }

    fun saveScreenshotAndNotify(
        data: ByteArray?,
        fileName: String?,
        callback: ScreenshotSaverCallback,
        coroutineScope: CoroutineScope
    ) {
        if (data == null) {
            Log.d(TAG, "Screenshot data is null")
            callback.screenshotSaved(false)
        } else if (fileName == null ||
            ONLY_WHITESPACE_REGEX.matches(fileName) ||
            !VALID_FILENAME_REGEX.matches(fileName)
        ) {
            Log.d(TAG, "Screenshot filename invalid")
            callback.screenshotSaved(false)
        } else {
            coroutineScope.launch(Dispatchers.IO) {
                val success = saveScreenshot(
                    data,
                    fileName
                )
                callback.screenshotSaved(success)
            }
        }
    }

    private fun saveScreenshot(
        data: ByteArray,
        fileName: String
    ): Boolean {
        val length = data.size
        val fullScreenBitmap: Bitmap
        val colors = IntArray(length / NUMBER_OF_COLORS)

        if (colors.size != width * height || colors.isEmpty()) {
            return false
        }

        for (i in 0 until length step NUMBER_OF_COLORS) {
            colors[i / NUMBER_OF_COLORS] = retrieveColorFromData(data, i)
        }
        fullScreenBitmap = Bitmap.createBitmap(
            colors, 0, width, width, height,
            Bitmap.Config.ARGB_8888
        )

        val imageScene = gdxFileHandler.absolute(folder + fileName)
        val streamScene = imageScene.write(false)
        try {
            File(folder + Constants.NO_MEDIA_FILE).createNewFile()
            fullScreenBitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, streamScene)
            streamScene.close()

            val projectManager: ProjectManager by inject(ProjectManager::class.java)
            if (projectManager.currentProject != null) {
                val projectFolder = projectManager.currentProject.directory.absolutePath + "/"
                val imageProject = gdxFileHandler.absolute(projectFolder + fileName)
                val streamProject = imageProject.write(false)
                File(projectFolder + Constants.NO_MEDIA_FILE).createNewFile()
                fullScreenBitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, streamProject)
                streamProject.close()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Could not save screenshot to file", e)
            return false
        }

        return true
    }

    @SuppressWarnings("MagicNumber")
    private fun retrieveColorFromData(data: ByteArray, i: Int): Int {
        return Color.argb(
            255,
            data[i].toInt() and 0xff,
            data[i + 1].toInt() and 0xff,
            data[i + 2].toInt() and 0xff
        )
    }
}
