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
package org.catrobat.catroid.test.stage

import androidx.test.rule.ActivityTestRule
import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.android.AndroidFiles
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.stage.ScreenshotSaver
import org.catrobat.catroid.stage.ScreenshotSaverCallback
import org.catrobat.catroid.stage.StageActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory.times
import java.io.File

@RunWith(Parameterized::class)
class ScreenshotSaverTest(private val name: String, private val fileName: String?, private val expectedResult: Boolean) {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(StageActivity::class.java, false, true)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf(
                arrayOf("VALID", "valid.png", true),
                arrayOf("NULL", null, false),
                arrayOf("WHITESPACES", "    ", false),
                arrayOf("ILLEGAL_CHARACTERS", "|\\?*<\":>+[]/'", false)
            )

        private const val NUMBER_OF_COLORS = 4
    }

    private lateinit var screenshotSaver: ScreenshotSaver
    private lateinit var dummyData: ByteArray
    private lateinit var gdxFileHandler: Files

    @Before
    fun setUp() {
        ScreenValues.setToDefaultScreenSize()
        val height = ScreenValues.SCREEN_HEIGHT
        val width = ScreenValues.SCREEN_WIDTH
        dummyData = ByteArray(NUMBER_OF_COLORS * width * height)
        val stageActivity = activityTestRule.activity
        val folder = stageActivity.cacheDir.absolutePath + "/"
        gdxFileHandler = AndroidFiles(stageActivity.assets, stageActivity.filesDir.absolutePath)
        screenshotSaver = ScreenshotSaver(gdxFileHandler as AndroidFiles, folder, width, height)
    }

    @After
    fun tearDown() {
        val dir = File(gdxFileHandler.localStoragePath)
        StorageOperations.deleteDir(dir)
    }

    @Test
    fun testSaveScreenshotAndNotify() {
        val callbackMock = Mockito.mock(ScreenshotSaverCallback::class.java)
        runBlocking {
            screenshotSaver.saveScreenshotAndNotify(dummyData, fileName, callbackMock, this)
        }
        Mockito.verify(callbackMock, times(1)).screenshotSaved(eq(expectedResult))
    }
}
