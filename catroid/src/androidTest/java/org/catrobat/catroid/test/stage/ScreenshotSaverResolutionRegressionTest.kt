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

package org.catrobat.catroid.test.stage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.rule.ActivityTestRule
import com.badlogic.gdx.backends.android.AndroidFiles
import com.badlogic.gdx.backends.android.DefaultAndroidFiles
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.stage.ScreenshotSaver
import org.catrobat.catroid.stage.ScreenshotSaverCallback
import org.catrobat.catroid.stage.StageActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.io.File

class ScreenshotSaverResolutionRegressionTest {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(StageActivity::class.java, false, true)

    private lateinit var screenshotSaver: ScreenshotSaver
    private lateinit var dummyData: ByteArray
    private lateinit var folder: String
    private lateinit var sceneScreenshotFile: File
    private lateinit var projectScreenshotFile: File
    private var width = 0
    private var height = 0

    @Before
    fun setUp() {
        ScreenValues.setToDefaultScreenSize()
        height = ScreenValues.currentScreenResolution.height
        width = ScreenValues.currentScreenResolution.width
        dummyData = ByteArray(NUMBER_OF_COLORS * width * height)

        val stageActivity = activityTestRule.activity
        val projectManager = ProjectManager.getInstance()
        folder = projectManager.currentlyPlayingScene.directory.absolutePath + "/"
        sceneScreenshotFile = File(folder + FILE_NAME)
        projectScreenshotFile = File(projectManager.currentProject.directory, FILE_NAME)

        deleteTestFiles()

        val gdxFileHandler: AndroidFiles = DefaultAndroidFiles(stageActivity.assets, stageActivity, true)
        screenshotSaver = ScreenshotSaver(gdxFileHandler, folder, width, height)
    }

    @After
    fun tearDown() {
        deleteTestFiles()
    }

    @Test
    fun testSaveScreenshotAndNotifyKeepsFullResolutionOnlyInCache() {
        val callbackMock = Mockito.mock(ScreenshotSaverCallback::class.java)

        runBlocking {
            screenshotSaver.saveScreenshotAndNotify(dummyData, FILE_NAME, callbackMock, this)
        }

        Mockito.verify(callbackMock).screenshotSaved(true)

        assertTrue("Scene screenshot was not created", sceneScreenshotFile.exists())
        assertTrue("Project screenshot was not created", projectScreenshotFile.exists())

        val sceneScreenshotBitmap = decodeBitmap(sceneScreenshotFile)
        assertTrue(
            "Scene screenshot should stay thumbnail-sized for project persistence",
            sceneScreenshotBitmap.width <= MAX_PERSISTED_SCREENSHOT_DIMENSION &&
                sceneScreenshotBitmap.height <= MAX_PERSISTED_SCREENSHOT_DIMENSION
        )
        assertTrue(
            "Scene screenshot should be smaller than the original framebuffer",
            sceneScreenshotBitmap.width < width || sceneScreenshotBitmap.height < height
        )

        val projectScreenshotBitmap = decodeBitmap(projectScreenshotFile)
        assertTrue(
            "Project screenshot should stay thumbnail-sized for export and storage",
            projectScreenshotBitmap.width <= MAX_PERSISTED_SCREENSHOT_DIMENSION &&
                projectScreenshotBitmap.height <= MAX_PERSISTED_SCREENSHOT_DIMENSION
        )

        val sessionScreenshotFile = findSessionScreenshotFiles().single()
        val sessionScreenshotBitmap = decodeBitmap(sessionScreenshotFile)

        assertEquals("Cached session screenshot width should match the framebuffer width", width, sessionScreenshotBitmap.width)
        assertEquals("Cached session screenshot height should match the framebuffer height", height, sessionScreenshotBitmap.height)
    }

    companion object {
        private const val FILE_NAME = "session_resolution_regression_unique.png"
        private const val MAX_PERSISTED_SCREENSHOT_DIMENSION = 480
        private const val NUMBER_OF_COLORS = 4
    }

    private fun decodeBitmap(file: File): Bitmap {
        val bitmapOptions = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return BitmapFactory.decodeFile(file.absolutePath, bitmapOptions)
    }

    private fun findSessionScreenshotFiles(): List<File> {
        return Constants.CACHE_DIRECTORY
            .walkTopDown()
            .filter { it.isFile && it.name == FILE_NAME }
            .toList()
    }

    private fun deleteTestFiles() {
        sceneScreenshotFile.delete()
        projectScreenshotFile.delete()
        findSessionScreenshotFiles().forEach { it.delete() }
    }
}
