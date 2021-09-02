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

package org.catrobat.catroid.test.io.asynctask

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.common.Constants.CACHE_DIR
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.io.asynctask.ProjectImporter
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.contains
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProjectImporterTest {

    companion object {
        private lateinit var destinationDirectory: File
        private lateinit var validProject: File
        private lateinit var noCodeFileProject: File
        private lateinit var noNameProject: File
        private lateinit var specialCharactersProject: File

        @BeforeClass
        @JvmStatic
        @Throws(IOException::class)
        fun setUpTestClass() {
            destinationDirectory = File(
                DEFAULT_ROOT_DIRECTORY, ProjectImporterTest::class.java.simpleName)
            CACHE_DIR.mkdir()

            validProject = unzipProject("ValidProject.catrobat")
            noCodeFileProject = unzipProject("NoCodeFile.catrobat")
            noNameProject = unzipProject("NoProjectName.catrobat")
            specialCharactersProject = unzipProject("Special%25Characters.catrobat")
        }

        @AfterClass
        @JvmStatic
        @Throws(IOException::class)
        fun tearDownTestClass() {
            StorageOperations.deleteDir(CACHE_DIR)
        }

        private fun unzipProject(name: String): File {
            val inputStream =
                InstrumentationRegistry.getInstrumentation().context.assets.open(name)
            val destDir = File(CACHE_DIR, StorageOperations.getSanitizedFileName(name))

            if (destDir.exists()) {
                StorageOperations.deleteDir(destDir)
            }

            ZipArchiver().unzip(inputStream, destDir)

            return destDir
        }
    }

    @Before
    fun setUp() {
        destinationDirectory.mkdir()
    }

    @After
    fun tearDown() {
        StorageOperations.deleteDir(destinationDirectory)
    }

    @Test
    fun testImportSingleProject() {
        val importResult = ProjectImporter(destinationDirectory)
            .importProjects(listOf(validProject))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertTrue(importResult)
        assertThat(importedProjects, contains("ValidProject"))
    }

    @Test
    fun testImportProjectTwice() {
        val importResult = ProjectImporter(destinationDirectory)
            .importProjects(listOf(validProject, validProject))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertTrue(importResult)
        assertThat(importedProjects, contains("ValidProject", "ValidProject (1)"))
    }

    @Test
    fun testImportProjectWithSpecialCharacters() {
        val importResult = ProjectImporter(destinationDirectory)
            .importProjects(listOf(specialCharactersProject))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertTrue(importResult)
        assertThat(importedProjects, contains("Special%Characters"))
    }

    @Test
    fun testImportMultipleProjectsWithError() {
        val importResult = ProjectImporter(destinationDirectory)
            .importProjects(listOf(validProject, noCodeFileProject))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertFalse(importResult)
        assertThat(importedProjects, contains("ValidProject"))
    }

    @Test
    fun testAbortSecondImportIfFirstFails() {
        val importResult = ProjectImporter(destinationDirectory)
            .importProjects(listOf(noCodeFileProject, validProject))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertFalse(importResult)
        assertThat(importedProjects, `is`(emptyList()))
    }

    @Test
    fun testImportProjectWithoutCodeFile() {
        val importResult = ProjectImporter(destinationDirectory)
            .importProjects(listOf(noCodeFileProject))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertFalse(importResult)
        assertThat(importedProjects, `is`(emptyList()))
    }

    @Test
    fun testImportProjectWithoutProjectName() {
        val importResult = ProjectImporter(destinationDirectory)
            .importProjects(listOf(noNameProject))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertFalse(importResult)
        assertThat(importedProjects, `is`(emptyList()))
    }

    @Test
    fun testAsyncImport() {
        var listenerCalled = false
        val onFinishedListener: (Boolean) -> Unit = { importResult ->
            assertTrue(importResult)
            listenerCalled = true
        }

        runBlocking {
            ProjectImporter(destinationDirectory)
                .setListener(onFinishedListener)
                .importProjectsAsync(listOf(validProject))
                .join()

            val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

            assertTrue(listenerCalled)
            assertThat(importedProjects, contains("ValidProject"))
        }
    }
}
