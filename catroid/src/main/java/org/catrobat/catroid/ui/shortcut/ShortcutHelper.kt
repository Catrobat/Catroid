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

package org.catrobat.catroid.ui.shortcut

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.utils.FileMetaDataExtractor
import java.io.File

/**
 * Utility for creating and managing pinned home-screen shortcuts for Catroid projects.
 *
 * **ID strategy:** Shortcut ID = encoded directory name (unique per project). This is
 * the filesystem-safe encoding of the project name produced by
 * [FileMetaDataExtractor.encodeSpecialCharsForFileSystem]. The directory name is guaranteed
 * unique within Catroid's project root.
 *
 * A stable UUID is also stored in `code.xml` (see [org.catrobat.catroid.content.XmlHeader.getProjectUuid])
 * for future-proofing — e.g. if we later want to survive renames without re-pinning — but
 * the current shortcut ID is still the encoded directory name.
 */
object ShortcutHelper {

    private const val TAG = "ShortcutHelper"

    fun isShortcutSupported(context: Context): Boolean =
        ShortcutManagerCompat.isRequestPinShortcutSupported(context)

    /**
     * Loads the project screenshot bitmap from the project directory on a background thread.
     * Checks project root first, then searches scene subdirectories (which is where Catroid
     * typically stores automatic_screenshot.png and manual_screenshot.png).
     *
     * First attempts full-resolution ARGB_8888. On OutOfMemoryError, retries at half-resolution
     * with RGB_565. If both fail, returns null.
     */
    suspend fun loadProjectIcon(projectName: String): Bitmap? = withContext(Dispatchers.IO) {
        val encodedName = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)
        val projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, encodedName)
        val screenshotFile = findScreenshotFile(projectDir) ?: return@withContext null
        decodeBitmapWithFallback(screenshotFile.absolutePath)
    }

    /**
     * Searches for the best available screenshot file in the project directory.
     * Priority: manual_screenshot > automatic_screenshot.
     * Looks first in the project root, then in scene subdirectories.
     */
    private fun findScreenshotFile(projectDir: File): File? {
        if (!projectDir.exists() || !projectDir.isDirectory) return null

        // Check project root first
        val rootManual = File(projectDir, Constants.SCREENSHOT_MANUAL_FILE_NAME)
        if (rootManual.exists() && rootManual.length() > 0) return rootManual

        val rootAutomatic = File(projectDir, Constants.SCREENSHOT_AUTOMATIC_FILE_NAME)
        if (rootAutomatic.exists() && rootAutomatic.length() > 0) return rootAutomatic

        // Search scene subdirectories
        val sceneDirs = projectDir.listFiles { file -> file.isDirectory } ?: return null
        for (sceneDir in sceneDirs) {
            val manual = File(sceneDir, Constants.SCREENSHOT_MANUAL_FILE_NAME)
            if (manual.exists() && manual.length() > 0) return manual

            val automatic = File(sceneDir, Constants.SCREENSHOT_AUTOMATIC_FILE_NAME)
            if (automatic.exists() && automatic.length() > 0) return automatic
        }

        return null
    }

    private const val ICON_SIZE = 192 // px — standard adaptive icon size

    private fun decodeBitmapWithFallback(path: String): Bitmap? {
        // Attempt ARGB_8888
        try {
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val bitmap = BitmapFactory.decodeFile(path, options)
            if (bitmap != null) return scaleBitmap(bitmap)
        } catch (_: OutOfMemoryError) {
            Log.w(TAG, "OOM loading icon, retrying at half-resolution")
        }

        // Retry at half-resolution with RGB_565
        try {
            val options = BitmapFactory.Options().apply {
                inSampleSize = 2
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            val bitmap = BitmapFactory.decodeFile(path, options)
            if (bitmap != null) return scaleBitmap(bitmap)
        } catch (_: OutOfMemoryError) {
            Log.e(TAG, "OOM loading half-resolution icon, falling back to default")
        }

        return null
    }

    private fun scaleBitmap(source: Bitmap): Bitmap {
        if (source.width <= ICON_SIZE && source.height <= ICON_SIZE) return source
        val scaled = Bitmap.createScaledBitmap(source, ICON_SIZE, ICON_SIZE, true)
        if (scaled !== source) source.recycle()
        return scaled
    }

    /**
     * Requests a pinned shortcut for the given project. Must be called on the main thread.
     *
     * Shortcut ID = encoded directory name (unique per project).
     *
     * @param context      Application or Activity context
     * @param projectName  The project name (used as the shortcut label)
     * @param icon         Optional pre-loaded bitmap for the icon; uses the default app icon if null
     */
    fun pinProject(context: Context, projectName: String, icon: Bitmap?) {
        if (!isShortcutSupported(context)) {
            Toast.makeText(
                context,
                R.string.shortcut_not_supported,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val shortcutId = encodeShortcutId(projectName)

        val trampolineIntent = Intent(context, ShortcutTrampolineActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(ShortcutTrampolineActivity.EXTRA_PROJECT_NAME, projectName)
        }

        val iconCompat = if (icon != null) {
            IconCompat.createWithAdaptiveBitmap(icon)
        } else {
            IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground)
        }

        val shortcutInfo = ShortcutInfoCompat.Builder(context, shortcutId)
            .setShortLabel(projectName)
            .setLongLabel(projectName)
            .setIcon(iconCompat)
            .setIntent(trampolineIntent)
            .build()

        val callbackIntent = ShortcutManagerCompat.createShortcutResultIntent(context, shortcutInfo)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            callbackIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, pendingIntent.intentSender)
    }

    /**
     * Updates any existing pinned shortcut after a project rename.
     *
     * Disables the old shortcut (by old encoded directory name) and — if the launcher supports
     * updating — pushes a new dynamic shortcut with the new label and intent so existing
     * home-screen icons reflect the new name.
     *
     * Call from a coroutine; the icon load runs on [Dispatchers.IO] internally.
     *
     * @param context  Application or Activity context
     * @param oldName  The previous project name (before rename)
     * @param newName  The new project name (after rename)
     */
    suspend fun updateShortcutOnRename(context: Context, oldName: String, newName: String) {
        val oldShortcutId = encodeShortcutId(oldName)
        val newShortcutId = encodeShortcutId(newName)

        // Disable the old shortcut so tapping it shows "project not found" gracefully
        try {
            ShortcutManagerCompat.disableShortcuts(
                context,
                listOf(oldShortcutId),
                context.getString(R.string.shortcut_project_not_found)
            )
        } catch (e: Exception) {
            Log.w(TAG, "Could not disable old shortcut '$oldShortcutId': ${e.message}")
        }

        // Build a replacement shortcut with the new name
        val icon = loadProjectIcon(newName)
        val iconCompat = if (icon != null) {
            IconCompat.createWithAdaptiveBitmap(icon)
        } else {
            IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground)
        }

        val trampolineIntent = Intent(context, ShortcutTrampolineActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(ShortcutTrampolineActivity.EXTRA_PROJECT_NAME, newName)
        }

        val newShortcut = ShortcutInfoCompat.Builder(context, newShortcutId)
            .setShortLabel(newName)
            .setLongLabel(newName)
            .setIcon(iconCompat)
            .setIntent(trampolineIntent)
            .build()

        // Push a dynamic shortcut so the launcher can pick up the change.
        // Pinned shortcuts cannot be directly updated, but pushing a dynamic shortcut
        // with the new ID keeps the shortcut registry consistent.
        try {
            ShortcutManagerCompat.pushDynamicShortcut(context, newShortcut)
        } catch (e: Exception) {
            Log.w(TAG, "Could not push updated shortcut '$newShortcutId': ${e.message}")
        }
    }

    /**
     * Encodes a project name into the shortcut ID (= encoded directory name).
     */
    private fun encodeShortcutId(projectName: String): String =
        FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)
}
