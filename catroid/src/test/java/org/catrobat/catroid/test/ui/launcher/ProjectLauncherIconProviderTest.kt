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
import org.catrobat.catroid.ui.launcher.ProjectLauncherIconProvider
import org.catrobat.catroid.ui.launcher.ProjectLauncherIconProvider.Companion.ICON_SIZE_PX
import org.catrobat.catroid.ui.launcher.ProjectLauncherIconProvider.Companion.SCENES_SUBDIR
import org.catrobat.catroid.ui.launcher.ProjectLauncherIconProvider.Companion.SCREENSHOTS_SUBDIR
import org.catrobat.catroid.ui.launcher.ProjectLauncherIconProvider.Companion.THUMBNAIL_FILE_NAME
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ProjectLauncherIconProviderTest {

    private lateinit var tempDir: File

    @Before
    fun setUp() {
        tempDir = createTempDir("providerTest")
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    // --- Helper ---

    private fun createBitmap(width: Int, height: Int, color: Int = Color.RED): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(color)
        }

    private fun decoderReturning(bitmap: Bitmap): ProjectLauncherIconProvider.BitmapDecoder =
        ProjectLauncherIconProvider.BitmapDecoder { bitmap }

    private fun decoderReturningNull(): ProjectLauncherIconProvider.BitmapDecoder =
        ProjectLauncherIconProvider.BitmapDecoder { null }

    // --- Thumbnail exists ---

    @Test
    fun `getIconForProject returns icon when thumbnail exists`() {
        File(tempDir, THUMBNAIL_FILE_NAME).createNewFile()

        val sourceBitmap = createBitmap(200, 200, Color.BLUE)
        val provider = ProjectLauncherIconProvider(decoderReturning(sourceBitmap))

        val icon = provider.getIconForProject(tempDir)

        assertNotNull(icon)
        assertEquals(ICON_SIZE_PX, icon.width)
        assertEquals(ICON_SIZE_PX, icon.height)
    }

    // --- Thumbnail missing, scene screenshot fallback ---

    @Test
    fun `getIconForProject returns icon from scene screenshot when thumbnail is missing`() {
        val sceneDir = File(tempDir, "$SCENES_SUBDIR/Scene1/$SCREENSHOTS_SUBDIR")
        sceneDir.mkdirs()
        File(sceneDir, "auto_screenshot.png").createNewFile()

        val sourceBitmap = createBitmap(320, 480)
        val provider = ProjectLauncherIconProvider(decoderReturning(sourceBitmap))

        val icon = provider.getIconForProject(tempDir)

        assertNotNull(icon)
        assertEquals(ICON_SIZE_PX, icon.width)
        assertEquals(ICON_SIZE_PX, icon.height)
    }

    // --- Thumbnail missing, no screenshot → fallback ---

    @Test
    fun `getIconForProject returns fallback icon when no thumbnail and no screenshot`() {
        val provider = ProjectLauncherIconProvider(decoderReturningNull())

        val icon = provider.getIconForProject(tempDir)

        assertNotNull(icon)
        assertEquals(ICON_SIZE_PX, icon.width)
        assertEquals(ICON_SIZE_PX, icon.height)
    }

    @Test
    fun `getIconForProject returns fallback when thumbnail file exists but decoder returns null`() {
        File(tempDir, THUMBNAIL_FILE_NAME).createNewFile()

        val provider = ProjectLauncherIconProvider(decoderReturningNull())

        val icon = provider.getIconForProject(tempDir)

        assertNotNull(icon)
        assertEquals(ICON_SIZE_PX, icon.width)
        assertEquals(ICON_SIZE_PX, icon.height)
    }

    // --- Scaling ---

    @Test
    fun `centreSquareCrop produces expected size from landscape source`() {
        val source = createBitmap(400, 200)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    @Test
    fun `centreSquareCrop produces expected size from portrait source`() {
        val source = createBitmap(200, 400)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    @Test
    fun `centreSquareCrop produces expected size from square source`() {
        val source = createBitmap(300, 300)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    @Test
    fun `centreSquareCrop with source already at ICON_SIZE does not rescale`() {
        val source = createBitmap(ICON_SIZE_PX, ICON_SIZE_PX)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    // --- Odd dimensions ---

    @Test
    fun `centreSquareCrop does not crash on 1x1 bitmap`() {
        val source = createBitmap(1, 1)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    @Test
    fun `centreSquareCrop does not crash on odd dimensions (3x7)`() {
        val source = createBitmap(3, 7)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    @Test
    fun `centreSquareCrop does not crash on odd dimensions (7x3)`() {
        val source = createBitmap(7, 3)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    @Test
    fun `centreSquareCrop does not crash on very large dimensions`() {
        val source = createBitmap(2000, 1000)
        val provider = ProjectLauncherIconProvider()

        val cropped = provider.centreSquareCrop(source, ICON_SIZE_PX)

        assertEquals(ICON_SIZE_PX, cropped.width)
        assertEquals(ICON_SIZE_PX, cropped.height)
    }

    // --- Rounded corners ---

    @Test
    fun `applyRoundedCorners preserves dimensions`() {
        val source = createBitmap(ICON_SIZE_PX, ICON_SIZE_PX, Color.GREEN)
        val provider = ProjectLauncherIconProvider()

        val result = provider.applyRoundedCorners(source, 20f)

        assertEquals(ICON_SIZE_PX, result.width)
        assertEquals(ICON_SIZE_PX, result.height)
    }

    @Test
    fun `applyRoundedCorners produces transparent corners`() {
        val source = createBitmap(ICON_SIZE_PX, ICON_SIZE_PX, Color.GREEN)
        val provider = ProjectLauncherIconProvider()

        val result = provider.applyRoundedCorners(source, 20f)

        // The very top-left pixel (0,0) should be transparent after rounding
        val topLeftPixel = result.getPixel(0, 0)
        assertEquals("Top-left pixel should be transparent after rounding",
            0, Color.alpha(topLeftPixel))
    }

    // --- Fallback bitmap ---

    @Test
    fun `createFallbackBitmap returns correct dimensions`() {
        val provider = ProjectLauncherIconProvider()

        val fallback = provider.createFallbackBitmap()

        assertEquals(ICON_SIZE_PX, fallback.width)
        assertEquals(ICON_SIZE_PX, fallback.height)
    }

    // --- loadSourceBitmap edge cases ---

    @Test
    fun `loadSourceBitmap returns fallback for empty project directory`() {
        val provider = ProjectLauncherIconProvider(decoderReturningNull())

        val bitmap = provider.loadSourceBitmap(tempDir)

        assertNotNull(bitmap)
        assertEquals(ICON_SIZE_PX, bitmap.width)
        assertEquals(ICON_SIZE_PX, bitmap.height)
    }

    @Test
    fun `loadSourceBitmap returns fallback for non-existent directory`() {
        val nonExistent = File(tempDir, "does_not_exist")
        val provider = ProjectLauncherIconProvider(decoderReturningNull())

        val bitmap = provider.loadSourceBitmap(nonExistent)

        assertNotNull(bitmap)
        assertEquals(ICON_SIZE_PX, bitmap.width)
    }

    @Test
    fun `loadSourceBitmap prefers thumbnail over scene screenshot`() {
        File(tempDir, THUMBNAIL_FILE_NAME).createNewFile()
        val sceneDir = File(tempDir, "$SCENES_SUBDIR/Scene1/$SCREENSHOTS_SUBDIR")
        sceneDir.mkdirs()
        File(sceneDir, "auto_screenshot.png").createNewFile()

        val thumbnailBitmap = createBitmap(100, 100, Color.BLUE)
        val provider = ProjectLauncherIconProvider(decoderReturning(thumbnailBitmap))

        val bitmap = provider.loadSourceBitmap(tempDir)

        // Should be the thumbnail bitmap (BLUE), not a fallback
        assertEquals(Color.BLUE, bitmap.getPixel(50, 50))
    }

    @Test
    fun `loadSourceBitmap falls back to scene screenshot when thumbnail decode fails`() {
        File(tempDir, THUMBNAIL_FILE_NAME).createNewFile()
        val sceneDir = File(tempDir, "$SCENES_SUBDIR/Scene1/$SCREENSHOTS_SUBDIR")
        sceneDir.mkdirs()
        File(sceneDir, "auto_screenshot.png").createNewFile()

        var callCount = 0
        val screenshotBitmap = createBitmap(200, 200, Color.GREEN)
        val selectiveDecoder = ProjectLauncherIconProvider.BitmapDecoder { path ->
            callCount++
            if (callCount == 1) null else screenshotBitmap
        }
        val provider = ProjectLauncherIconProvider(selectiveDecoder)

        val bitmap = provider.loadSourceBitmap(tempDir)

        assertEquals(Color.GREEN, bitmap.getPixel(100, 100))
    }

    @Test
    fun `loadSourceBitmap ignores non-png files in screenshots`() {
        val sceneDir = File(tempDir, "$SCENES_SUBDIR/Scene1/$SCREENSHOTS_SUBDIR")
        sceneDir.mkdirs()
        File(sceneDir, "notes.txt").createNewFile() // not a .png

        val provider = ProjectLauncherIconProvider(decoderReturningNull())

        val bitmap = provider.loadSourceBitmap(tempDir)

        // Should be fallback since no .png screenshot
        assertEquals(ICON_SIZE_PX, bitmap.width)
    }
}
