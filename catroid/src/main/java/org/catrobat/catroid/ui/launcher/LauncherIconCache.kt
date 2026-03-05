/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.launcher

import android.graphics.Bitmap
import android.util.LruCache
import java.io.File

/**
 * Optional in-memory LRU cache for launcher icon bitmaps.
 *
 * Cache key: `<absolutePath>:<lastModified>` so a project whose thumbnail
 * changes on disk (new screenshot after running) is automatically invalidated.
 *
 * Thread-safe via [LruCache]'s built-in synchronisation.
 */
class LauncherIconCache(maxEntries: Int = DEFAULT_MAX_ENTRIES) {

    private val lru = object : LruCache<String, Bitmap>(maxEntries) {
        override fun sizeOf(key: String, value: Bitmap) = 1
    }

    fun get(projectDir: File): Bitmap? = lru.get(cacheKey(projectDir))

    fun put(projectDir: File, icon: Bitmap) {
        lru.put(cacheKey(projectDir), icon)
    }

    fun evict(projectDir: File) {
        lru.remove(cacheKey(projectDir))
    }

    /** Removes all entries — useful for low-memory callbacks. */
    fun clear() = lru.evictAll()

    internal fun cacheKey(projectDir: File): String =
        "${projectDir.absolutePath}:${projectDir.lastModified()}"

    companion object {
        const val DEFAULT_MAX_ENTRIES = 20
    }
}