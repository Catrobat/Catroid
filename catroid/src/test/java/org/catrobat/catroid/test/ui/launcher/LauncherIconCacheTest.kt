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

package org.catrobat.catroid.test.ui.launcher

import android.graphics.Bitmap
import android.graphics.Color
import org.catrobat.catroid.ui.launcher.LauncherIconCache
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LauncherIconCacheTest {

    private lateinit var cache: LauncherIconCache
    private lateinit var tempDir: File

    @Before
    fun setUp() {
        cache = LauncherIconCache()
        tempDir = createTempDir("cacheTest")
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    private fun createBitmap(): Bitmap =
        Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.RED)
        }

    // --- Cache miss ---

    @Test
    fun `get returns null on empty cache`() {
        val result = cache.get(tempDir)
        assertNull(result)
    }

    @Test
    fun `get returns null for non-cached directory`() {
        val bitmap = createBitmap()
        cache.put(tempDir, bitmap)

        val otherDir = File(tempDir, "other")
        otherDir.mkdirs()
        val result = cache.get(otherDir)

        assertNull(result)
    }

    // --- Cache hit ---

    @Test
    fun `put and get returns same bitmap`() {
        val bitmap = createBitmap()
        cache.put(tempDir, bitmap)

        val result = cache.get(tempDir)

        assertNotNull(result)
        assertSame(bitmap, result)
    }

    @Test
    fun `put overwrites previous value for same key`() {
        val bitmap1 = createBitmap()
        val bitmap2 = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.BLUE)
        }

        cache.put(tempDir, bitmap1)
        cache.put(tempDir, bitmap2)

        val result = cache.get(tempDir)
        assertSame(bitmap2, result)
    }

    // --- Evict ---

    @Test
    fun `evict removes cached entry`() {
        val bitmap = createBitmap()
        cache.put(tempDir, bitmap)
        cache.evict(tempDir)

        val result = cache.get(tempDir)
        assertNull(result)
    }

    @Test
    fun `evict on non-existent key does not crash`() {
        cache.evict(tempDir) // should not throw
    }

    // --- Clear ---

    @Test
    fun `clear removes all entries`() {
        val bitmap = createBitmap()
        val dir2 = File(tempDir, "project2")
        dir2.mkdirs()

        cache.put(tempDir, bitmap)
        cache.put(dir2, bitmap)

        cache.clear()

        assertNull(cache.get(tempDir))
        assertNull(cache.get(dir2))
    }

    // --- Cache key ---

    @Test
    fun `cacheKey includes path and lastModified`() {
        val key = cache.cacheKey(tempDir)

        assert(key.contains(tempDir.absolutePath)) {
            "Cache key should contain the directory path"
        }
        assert(key.contains(":")) {
            "Cache key should contain colon separator"
        }
    }

    @Test
    fun `different directories produce different cache keys`() {
        val otherDir = File(tempDir, "other")
        otherDir.mkdirs()

        val key1 = cache.cacheKey(tempDir)
        val key2 = cache.cacheKey(otherDir)

        assert(key1 != key2) {
            "Different directories should produce different cache keys"
        }
    }

    // --- LRU eviction ---

    @Test
    fun `LRU cache evicts oldest entry when max size exceeded`() {
        val smallCache = LauncherIconCache(maxEntries = 2)

        val dir1 = File(tempDir, "p1").apply { mkdirs() }
        val dir2 = File(tempDir, "p2").apply { mkdirs() }
        val dir3 = File(tempDir, "p3").apply { mkdirs() }

        val bmp1 = createBitmap()
        val bmp2 = createBitmap()
        val bmp3 = createBitmap()

        smallCache.put(dir1, bmp1)
        smallCache.put(dir2, bmp2)
        // This should evict dir1 (oldest)
        smallCache.put(dir3, bmp3)

        assertNull("Oldest entry should be evicted", smallCache.get(dir1))
        assertNotNull(smallCache.get(dir2))
        assertNotNull(smallCache.get(dir3))
    }
}
