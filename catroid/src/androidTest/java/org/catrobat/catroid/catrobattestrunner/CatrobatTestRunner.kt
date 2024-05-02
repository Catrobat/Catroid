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
package org.catrobat.catroid.catrobattestrunner

import android.app.Instrumentation.ActivityResult
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.io.asynctask.unzipAndImportProjects
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.TestResult
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.io.IOException
import java.util.ArrayList

@RunWith(Parameterized::class)
class CatrobatTestRunner {
    var retries = 5

    @Rule
    @JvmField
    var baseActivityTestRule = ActivityTestRule(
        StageActivity::class.java, true, false
    )

    @JvmField
    @Parameterized.Parameter
    var assetPath: String = ""

    @JvmField
    @Parameterized.Parameter(1)
    var assetName: String = ""

    companion object {
        private const val TEST_ASSETS_ROOT = "catrobatTests"
        private const val TIMEOUT = 10_000

        @JvmStatic
        @Parameterized.Parameters(name = "{0} - {1}")
        @Throws(IOException::class)
        fun data(): Iterable<Array<Any>> = getCatrobatAssetsFromPath(TEST_ASSETS_ROOT)

        @Throws(IOException::class)
        private fun getCatrobatAssetsFromPath(path: String): List<Array<Any>> {
            val parameters: MutableList<Array<Any>> = ArrayList()
            val assets = InstrumentationRegistry.getInstrumentation().context.assets.list(path)
            if (null == assets) {
                Assert.fail("Could not load assets")
                return parameters
            }
            for (asset in assets) {
                if (asset.endsWith(Constants.CATROBAT_EXTENSION)) {
                    parameters.add(arrayOf(path, asset))
                } else {
                    parameters.addAll(getCatrobatAssetsFromPath("$path/$asset"))
                }
            }
            return parameters
        }
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val projectName = assetName.replace(Constants.CATROBAT_EXTENSION, "")
        TestUtils.deleteProjects(projectName)
        FlavoredConstants.DEFAULT_ROOT_DIRECTORY.mkdir()
        Constants.CACHE_DIRECTORY.mkdir()
        val inputStream = InstrumentationRegistry.getInstrumentation().context.assets
            .open("$assetPath/$assetName")
        val projectArchive = StorageOperations
            .copyStreamToDir(inputStream, Constants.CACHE_DIRECTORY, assetName)
        Assert.assertTrue(unzipAndImportProjects(arrayOf(projectArchive)))
        val projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName)
        Assert.assertTrue(
            loadProject(projectDir, ApplicationProvider.getApplicationContext())
        )
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        StorageOperations.deleteDir(Constants.CACHE_DIRECTORY)
        TestUtils.deleteProjects(assetName.replace(Constants.CATROBAT_EXTENSION, ""))
    }

    @Test
    @Throws(InterruptedException::class)
    fun run() {
        var result: ActivityResult? = null
        val messages = StringBuilder()

        for (runNr in 1..retries) {
            baseActivityTestRule.launchActivity(null)
            waitForReady()
            result = baseActivityTestRule.activityResult
            if (result?.resultCode == TestResult.STAGE_ACTIVITY_TEST_SUCCESS) {
                break
            }
            messages.append("Testrun Nr.: $runNr\n")
            messages.append(result?.resultData?.getStringExtra(TestResult.TEST_RESULT_MESSAGE))
            messages.append("\n\n")
            if (runNr != retries) {
                restart()
            }
        }

        if (result?.resultCode != TestResult.STAGE_ACTIVITY_TEST_SUCCESS) {
            Assert.fail(messages.toString())
        }
    }

    private fun restart() {
        try {
            tearDown()
            setUp()
        } catch (e: Exception) {
            Assert.fail(e.message)
        }
    }

    @Throws(InterruptedException::class)
    private fun waitForReady() {
        val intervalMillis = 10
        var waitedFor = 0
        while (waitedFor < TIMEOUT) {
            if (baseActivityTestRule.activity.isFinishing) {
                return
            }
            Thread.sleep(intervalMillis.toLong())
            waitedFor += intervalMillis
        }
        Assert.fail(
            """
            Timeout after ${TIMEOUT}ms
            Test never got into ready state - is the AssertEqualsBrick reached?
            
            """.trimIndent()
        )
    }
}
