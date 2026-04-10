/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import androidx.core.graphics.createBitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants.CACHE_DIRECTORY
import org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME
import org.catrobat.catroid.common.Constants.SCREENSHOT_MANUAL_FILE_NAME
import org.catrobat.catroid.common.ScreenValues
import java.io.File
import java.util.UUID

private const val TAG = "ProjectManagerExtensions"
private const val SESSION_SCREENSHOT_DIRECTORY_NAME = "stageSessionScreenshots"
private val screenshotSessionDirectory: File by lazy {
    clearOldSessionScreenshots()
    File(getSessionScreenshotRootDirectory(), UUID.randomUUID().toString())
}

internal fun getSessionScreenshotRootDirectory(): File = File(CACHE_DIRECTORY, SESSION_SCREENSHOT_DIRECTORY_NAME)

fun ProjectManager.getSessionScreenshotFile(fileName: String): File =
    File(
        File(
            screenshotSessionDirectory,
            currentProject.directory.name
        ),
        currentlyPlayingScene.directory.name
    ).resolve(fileName)

@SuppressWarnings("TooGenericExceptionCaught")
fun ProjectManager.getProjectBitmap(): Bitmap {
    val sceneDir = currentlyPlayingScene.directory
    val bitmapOptions = BitmapFactory.Options()
    bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888

    val screenshotCandidates = listOf(
        getSessionScreenshotFile(SCREENSHOT_AUTOMATIC_FILE_NAME),
        getSessionScreenshotFile(SCREENSHOT_MANUAL_FILE_NAME),
        File(sceneDir, SCREENSHOT_AUTOMATIC_FILE_NAME),
        File(sceneDir, SCREENSHOT_MANUAL_FILE_NAME)
    )

    return screenshotCandidates
        .asSequence()
        .mapNotNull { decodeBitmapOrNull(it, bitmapOptions) }
        .firstOrNull()
        ?: getBackgroundBitmap(bitmapOptions)
        ?: createWhiteBitmap()
}

internal fun clearOldSessionScreenshots() {
    val screenshotCacheDirectory = getSessionScreenshotRootDirectory()
    if (screenshotCacheDirectory.exists() && !screenshotCacheDirectory.deleteRecursively()) {
        Log.w(TAG, "Could not clear old session screenshot cache at ${screenshotCacheDirectory.absolutePath}")
    }
}

private fun decodeBitmapOrNull(file: File, bitmapOptions: BitmapFactory.Options): Bitmap? {
    if (!file.exists()) {
        return null
    }

    return BitmapFactory.decodeFile(file.absolutePath, bitmapOptions)
        ?: run {
            Log.w(TAG, "Could not decode bitmap at ${file.absolutePath}")
            null
        }
}

@SuppressWarnings("TooGenericExceptionCaught")
private fun ProjectManager.getBackgroundBitmap(bitmapOptions: BitmapFactory.Options): Bitmap? {
    return try {
        decodeBitmapOrNull(currentlyEditedScene.backgroundSprite.lookList[0].file, bitmapOptions)
    } catch (e: IndexOutOfBoundsException) {
        Log.w(TAG, "backgroundSprite has no looks! ${e.message}")
        null
    }
}

private fun createWhiteBitmap(): Bitmap =
    createBitmap(
        ScreenValues.currentScreenResolution.width,
        ScreenValues.currentScreenResolution.height,
        Bitmap.Config.ARGB_8888
    ).apply {
        eraseColor(Color.WHITE)
    }
