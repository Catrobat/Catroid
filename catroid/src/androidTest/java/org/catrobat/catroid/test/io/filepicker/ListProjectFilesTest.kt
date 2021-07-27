/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.test.io.filepicker

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME
import org.catrobat.catroid.common.Constants.TMP_DIR_NAME
import org.catrobat.catroid.common.FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.filepicker.FilePickerActivity
import org.catrobat.catroid.ui.filepicker.ProjectFileLister
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import java.io.File
import java.io.IOException

@RunWith(JUnit4::class)
class ListProjectFilesTest {
    private lateinit var projectFileLister: ProjectFileLister
    private lateinit var tmpFolder: File
    private lateinit var filePickerActivity: FilePickerActivity

    @Before
    @Throws(IOException::class)
    fun setUp() {
        tmpFolder = File(
            ApplicationProvider.getApplicationContext<Context>().cacheDir,
            "ListProjectFilesTestTmp"
        )
        filePickerActivity = mock(FilePickerActivity::class.java)
        projectFileLister = ProjectFileLister()

        if (tmpFolder.isDirectory) {
            StorageOperations.deleteDir(tmpFolder)
        }
        tmpFolder.mkdirs()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (EXTERNAL_STORAGE_ROOT_DIRECTORY.isDirectory) {
                StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_DIRECTORY)
            }
            EXTERNAL_STORAGE_ROOT_DIRECTORY.mkdirs()
        }
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        if (tmpFolder.isDirectory) {
            StorageOperations.deleteDir(tmpFolder)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && EXTERNAL_STORAGE_ROOT_DIRECTORY.isDirectory) {
            StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_DIRECTORY)
        }
    }

    @Test
    @Throws(IOException::class)
    fun testListCatrobatFiles() {
        val subFolder = File(tmpFolder, "subfolder")
        assertTrue(subFolder.mkdirs())
        val validFile0 = File(tmpFolder, "projectA.catrobat")
        assertTrue(validFile0.createNewFile())
        val validFile1 = File(subFolder, "projectB.catrobat")
        assertTrue(validFile1.createNewFile())
        val invalidFile0 = File(tmpFolder, "projectWithoutExtension")
        assertTrue(invalidFile0.createNewFile())
        val invalidFile1 = File(tmpFolder, "project.catrobat.somethingelse")
        assertTrue(invalidFile1.createNewFile())
        val invalidFile2 = File(subFolder, ".projectB.catrobat.somethingelse")
        assertTrue(invalidFile2.createNewFile())

        val projectFiles = projectFileLister.listProjectFiles(listOf(tmpFolder))

        assertTrue(projectFiles.containsAll(listOf(validFile0, validFile1)))
        listOf(invalidFile0, invalidFile1, invalidFile2).forEach { assertFalse(projectFiles.contains(it)) }
    }

    @Test
    @Throws(IOException::class)
    fun testListProjectsOnExternalStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val backpackFolder = File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "backpack")
            assertTrue(backpackFolder.mkdirs())
            val externalTempFolder = File(EXTERNAL_STORAGE_ROOT_DIRECTORY, TMP_DIR_NAME)
            assertTrue(externalTempFolder.mkdirs())
            val emptyFolder = File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "emptyFolder")
            assertTrue(emptyFolder.mkdirs())
            val validProjectFolder0 = File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "validProjectFolder0")
            assertTrue(validProjectFolder0.mkdirs())
            val validProjectFolder1 = File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "validProjectFolder1")
            assertTrue(validProjectFolder1.mkdirs())
            assertTrue(File(validProjectFolder0, CODE_XML_FILE_NAME).createNewFile())
            assertTrue(File(validProjectFolder1, CODE_XML_FILE_NAME).createNewFile())

            val projectFiles: List<File> = projectFileLister.listProjectFiles(listOf(tmpFolder))

            assertTrue(projectFiles.containsAll(listOf(validProjectFolder0, validProjectFolder1)))
            listOf(backpackFolder, externalTempFolder, emptyFolder).forEach { assertFalse(projectFiles.contains(it)) }
        }
    }
}
