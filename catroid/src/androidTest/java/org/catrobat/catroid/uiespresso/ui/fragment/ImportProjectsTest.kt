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

package org.catrobat.catroid.uiespresso.ui.fragment

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import org.catrobat.catroid.R
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants.CACHE_DIR
import org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.runner.RunWith
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import java.io.File
import android.app.Activity

import android.app.Instrumentation.ActivityResult

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.catroid.common.Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.uiespresso.util.actions.CustomActions.wait
import org.hamcrest.Matchers.not

@Suppress("MethodOverloading")
@RunWith(AndroidJUnit4::class)
class ImportProjectsTest : KoinTest {

    private lateinit var filePickerStrategy: FilePickerStrategy
    private lateinit var testDirectory: File
    private lateinit var testProject: Project
    private lateinit var projectDirectory: File
    private lateinit var catrobatFile: File

    @get:Rule
    var baseActivityTestRule = IntentsTestRule(ProjectListActivity::class.java)

    @Before
    fun setUp() {
        filePickerStrategy = if (SDK_INT >= Build.VERSION_CODES.Q) {
            ScopedStorageFilePickerStrategy()
        } else {
            LegacyStorageFilePickerStrategy()
        }
        testDirectory = filePickerStrategy.getTestDirectory()
        initializeDirectories(testDirectory)
        createAndSaveProject("My First Project")
        moveProjectToTestDirectory()
        createProjectZipArchive()
    }

    private fun initializeDirectories(file: File) {
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    private fun createAndSaveProject(name: String) {
        testProject = Project(ApplicationProvider.getApplicationContext(), name)
        val sprite = Sprite("testSprite")
        val script = StartScript()
        script.addBrick(SetXBrick())
        sprite.addScript(script)
        testProject.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = testProject
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = testProject.defaultScene
        XstreamSerializer.getInstance().saveProject(testProject)
    }

    private fun moveProjectToTestDirectory() {
        projectDirectory = File(testDirectory,
                                testProject.directory.name + " Unzipped")
        StorageOperations.copyDir(testProject.directory, projectDirectory)
        TestUtils.deleteProjects(testProject.name)
    }

    private fun createProjectZipArchive() {
        catrobatFile = File(testDirectory,
                            testProject.directory.name + CATROBAT_EXTENSION)
        ZipArchiver().zip(catrobatFile, projectDirectory.listFiles())
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(testProject.name)
        StorageOperations.deleteDir(testDirectory)
        CACHE_DIR.listFiles()
            ?.filter { f -> f.name.contains(testProject.directory.name) }
            ?.forEach { f -> if (f.isDirectory) StorageOperations.deleteDir(f) else f.delete() }
    }

    @Test
    fun testImportZippedProject() {
        UiTestUtils.openActionBarMenu()
        filePickerStrategy.pickFile(catrobatFile)
        onView(ViewMatchers.isRoot()).perform(wait(2000))
        onView(withText("My First Project")).check(matches(isDisplayed()))
    }

    private interface FilePickerStrategy {
        fun getTestDirectory(): File
        fun pickFile(file: File)
    }

    private class ScopedStorageFilePickerStrategy : FilePickerStrategy {
        override fun getTestDirectory(): File = File(
            DEFAULT_ROOT_DIRECTORY, ImportProjectsTest::class.java.simpleName)

        override fun pickFile(file: File) {
            val resultData = Intent()
            resultData.data = Uri.fromFile(file)
            intending(not(isInternal())).respondWith(ActivityResult(Activity.RESULT_OK, resultData))
            onView(withText(R.string.import_project)).perform(click())
        }
    }

    private class LegacyStorageFilePickerStrategy : FilePickerStrategy {
        override fun getTestDirectory(): File = EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY

        override fun pickFile(file: File) {
            onView(withText(R.string.import_project)).perform(click())
            onRecyclerView().performOnItemWithText(file.name, click())
            onView(withId(R.id.confirm)).perform(click())
        }
    }
}
