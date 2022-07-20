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

package org.catrobat.catroid.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME
import org.catrobat.catroid.common.Constants.SCREENSHOT_MANUAL_FILE_NAME
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.common.ScreenValues
import java.io.File

@SuppressWarnings("TooGenericExceptionCaught")
fun ProjectManager.getProjectBitmap(): Bitmap {
    val projectDir = File(DEFAULT_ROOT_DIRECTORY, currentProject.name)
    val sceneDir = File(projectDir, currentlyPlayingScene.name)
    val automaticScreenshot = File(sceneDir, SCREENSHOT_AUTOMATIC_FILE_NAME)
    val manualScreenshot = File(sceneDir, SCREENSHOT_MANUAL_FILE_NAME)
    val bitmapOptions = BitmapFactory.Options()
    bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888

    return try {
        val backgroundBitmapPath: String = when {
            automaticScreenshot.exists() -> automaticScreenshot.path
            manualScreenshot.exists() -> manualScreenshot.path
            else -> currentlyEditedScene.backgroundSprite.lookList[0].file.absolutePath
        }
        BitmapFactory.decodeFile(backgroundBitmapPath, bitmapOptions)
    } catch (e: IndexOutOfBoundsException) {
        Log.w("getProjectBitmap", "backgroundSprite has no looks! ${e.message}")
        val screenWidth = ScreenValues.SCREEN_WIDTH
        val screenHeight = ScreenValues.SCREEN_HEIGHT
        val bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        bitmap
    }
}
