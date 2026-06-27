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
import androidx.core.graphics.scale
import androidx.core.net.toUri
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

    /**
     * POCO phones report [ShortcutManagerCompat.isRequestPinShortcutSupported] as true
     * but silently fail to create shortcuts. The feature is hidden on those devices.
     */
    fun isShortcutSupported(context: Context): Boolean =
        !isPocoDevice() && ShortcutManagerCompat.isRequestPinShortcutSupported(context)

    fun isPocoDevice(): Boolean =
        android.os.Build.MANUFACTURER.contains("POCO", ignoreCase = true)

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
        val scaled = source.scale(ICON_SIZE, ICON_SIZE, true)
        if (scaled !== source) source.recycle()
        return scaled
    }

    /**
     * Requests a pinned shortcut for the given project. Must be called on the main thread.
     *
     * Also pushes a dynamic shortcut with the same ID so that
     * [updateShortcutOnRename] and [removeShortcutsForProjects] can manage it later.
     *
     * Shortcut ID = encoded directory name at time of pinning.
     */
    fun pinProject(context: Context, projectName: String, icon: Bitmap?): Boolean {
        val shortcutInfo = buildShortcutInfo(context, projectName, icon)

        // Guard: prevent duplicate pins for the same project
        val existingShortcuts = ShortcutManagerCompat.getDynamicShortcuts(context)
        if (existingShortcuts.any { it.id == shortcutInfo.id }) {
            Toast.makeText(
                context,
                R.string.shortcut_already_pinned,
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Register as dynamic shortcut first — required for updateShortcuts / remove to work
        try {
            ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo)
        } catch (e: Exception) {
            Log.w(TAG, "Could not push dynamic shortcut: ${e.message}")
        }

        // Request the pinned shortcut on the home screen
        val callbackIntent = ShortcutManagerCompat.createShortcutResultIntent(context, shortcutInfo)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            callbackIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, pendingIntent.intentSender)
    }

    /**
     * Updates the label and intent of an existing pinned shortcut after a project rename.
     *
     * Uses [ShortcutManagerCompat.updateShortcuts] to change the shortcut in-place —
     * the home-screen icon stays, but its label and launch intent switch to the new name.
     *
     * The shortcut ID stays the same (= encoded old directory name). Only the displayed
     * label and the intent extra change.
     */
    suspend fun updateShortcutOnRename(context: Context, oldName: String, newName: String) {
        val shortcutId = encodeShortcutId(oldName)

        val icon = loadProjectIcon(newName)
        val updatedShortcut = buildShortcutInfo(context, newName, icon, shortcutId)

        try {
            ShortcutManagerCompat.updateShortcuts(context, listOf(updatedShortcut))
        } catch (e: Exception) {
            Log.w(TAG, "Could not update shortcut for rename '$oldName' -> '$newName': ${e.message}")
        }
    }

    /**
     * Removes shortcuts for deleted projects.
     *
     * Removes dynamic shortcuts and disables pinned shortcuts so they show
     * a "project not found" message if tapped.
     *
     * Note: Android does not allow apps to programmatically remove pinned shortcuts
     * from the home screen. Disabling is the closest the API offers — the launcher
     * will either hide or dim the icon depending on the device.
     */
    fun removeShortcutsForProjects(context: Context, projectNames: List<String>) {
        if (projectNames.isEmpty()) return

        val shortcutIds = projectNames.map { encodeShortcutId(it) }

        // removeLongLivedShortcuts fully removes pinned shortcuts from the home screen
        try {
            ShortcutManagerCompat.removeLongLivedShortcuts(context, shortcutIds)
        } catch (e: Exception) {
            Log.w(TAG, "Could not remove long-lived shortcuts: ${e.message}")
        }

        try {
            ShortcutManagerCompat.removeDynamicShortcuts(context, shortcutIds)
        } catch (e: Exception) {
            Log.w(TAG, "Could not remove dynamic shortcuts: ${e.message}")
        }

        try {
            ShortcutManagerCompat.disableShortcuts(
                context,
                shortcutIds,
                context.getString(R.string.shortcut_project_not_found)
            )
        } catch (e: Exception) {
            Log.w(TAG, "Could not disable shortcuts: ${e.message}")
        }
    }

    /**
     * Builds a [ShortcutInfoCompat] for a project.
     *
     * @param overrideId  If non-null, uses this as the shortcut ID instead of encoding the name.
     *                    Needed for rename where the ID must stay the same as the original.
     */
    private fun buildShortcutInfo(
        context: Context,
        projectName: String,
        icon: Bitmap?,
        overrideId: String? = null
    ): ShortcutInfoCompat {
        val shortcutId = overrideId ?: encodeShortcutId(projectName)

        val trampolineIntent = Intent(context, ShortcutTrampolineActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(ShortcutTrampolineActivity.EXTRA_PROJECT_NAME, projectName)
        }

        val iconCompat = if (icon != null) {
            IconCompat.createWithAdaptiveBitmap(icon)
        } else {
            IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground)
        }

        return ShortcutInfoCompat.Builder(context, shortcutId)
            .setShortLabel(projectName)
            .setLongLabel(projectName)
            .setLongLived(true)
            .setIcon(iconCompat)
            .setIntent(trampolineIntent)
            .build()
    }

    /**
     * Encodes a project name into the shortcut ID (= encoded directory name).
     */
    private fun encodeShortcutId(projectName: String): String =
        FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName)

    // --- MIUI / Xiaomi Compatibility ---

    private const val MIUI_INSTALL_SHORTCUT_OP_CODE = 10017

    /**
     * Checks if the device is a Xiaomi-related brand (Redmi, POCO, etc.)
     */
    fun isXiaomiDevice(): Boolean {
        val manufacturer = android.os.Build.MANUFACTURER
        return manufacturer.contains("Xiaomi", ignoreCase = true) ||
                manufacturer.contains("Redmi", ignoreCase = true) ||
                manufacturer.contains("POCO", ignoreCase = true) ||
                manufacturer.contains("Blackshark", ignoreCase = true)
    }

    /**
     * Architectural Note: Most OEM implementations (including MIUI) share a combined limit
     * for both dynamic and pinned shortcuts per activity (usually 5-10).
     *
     * Calling [ShortcutManagerCompat.getMaxShortcutCountPerActivity] before showing
     * the "Pin" UI ensures we don't present an option that the OS will reject.
     */
    fun canAddMoreShortcuts(context: Context): Boolean =
        ShortcutManagerCompat.getMaxShortcutCountPerActivity(context) > 0

    /**
     * Performs a 'Silent Probe' to detect if shortcut creation is blocked by the OS.
     * This is a reliable fallback for Xiaomi/HyperOS when reflection checks are blocked.
     *
     * 1. Pushes a dummy dynamic shortcut.
     * 2. Waits 200ms to allow MIUI's throttled shortcut manager to sync.
     * 3. Checks if the shortcut actually exists in the dynamic list.
     * 4. Cleans up the dummy shortcut.
     */
    suspend fun probeIsShortcutCreationBlocked(context: Context): Boolean = withContext(Dispatchers.IO) {
        if (!isShortcutSupported(context)) return@withContext true

        val probeId = "miui_probe_${System.currentTimeMillis()}"
        val probeShortcut = ShortcutInfoCompat.Builder(context, probeId)
            .setShortLabel("Probe")
            .setIntent(Intent(Intent.ACTION_VIEW))
            .build()

        try {
            // 1. Push dummy
            ShortcutManagerCompat.pushDynamicShortcut(context, probeShortcut)

            // 2. Timeout guard: MIUI can be stale immediately after push
            kotlinx.coroutines.delay(200)

            // 3. Verify existence
            val dynamicShortcuts = ShortcutManagerCompat.getDynamicShortcuts(context)
            val exists = dynamicShortcuts.any { it.id == probeId }

            // 4. Cleanup
            ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(probeId))

            !exists // If it doesn't exist, creation is blocked
        } catch (e: Exception) {
            Log.w(TAG, "Silent probe failed: ${e.message}")
            false // Default to not blocked on error
        }
    }

    /**
     * Checks if the "Install shortcut" permission is granted on MIUI.
     * Uses reflection to access the hidden 'checkOp' method in AppOpsManager.
     */
    fun isShortcutPermissionGranted(context: Context): Boolean {
        if (!isXiaomiDevice()) return true

        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        
        // Try checkOpNoThrow first (API 19+)
        try {
            val method = appOpsManager.javaClass.getMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result = method.invoke(
                appOpsManager,
                MIUI_INSTALL_SHORTCUT_OP_CODE,
                android.os.Process.myUid(),
                context.packageName
            ) as Int
            return result == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.w(TAG, "checkOpNoThrow failed or blocked, trying fallback", e)
        }

        // Fallback to checkOp
        return try {
            val method = appOpsManager.javaClass.getMethod(
                "checkOp",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result = method.invoke(
                appOpsManager,
                MIUI_INSTALL_SHORTCUT_OP_CODE,
                android.os.Process.myUid(),
                context.packageName
            ) as Int
            result == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.e(TAG, "MIUI permission reflection failed completely. Defaulting to 'granted' for safety.", e)
            // If reflection is blocked or API changed (e.g. HyperOS), default to TRUE 
            // so we don't block the user from trying to pin.
            true
        }
    }

    /**
     * Launches the MIUI-specific Permission Editor activity.
     */
    fun openMiuiPermissionEditor(context: Context) {
        try {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                putExtra("extra_pkgname", context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "MIUI Permission Editor not found, falling back to app settings", e)
            openStandardAppSettings(context)
        }
    }

    private fun openStandardAppSettings(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${context.packageName}".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open even standard app settings", e)
        }
    }
}

