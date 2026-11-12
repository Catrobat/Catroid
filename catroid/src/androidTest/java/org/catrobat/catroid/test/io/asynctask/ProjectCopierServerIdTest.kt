/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.ProjectCopier
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.utils.FileMetaDataExtractor.encodeSpecialCharsForFileSystem
import org.catrobat.catroid.utils.ProjectIdUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ProjectCopierServerIdTest {

    private val ownId = "0f8fad5b-d9cb-469f-a165-70867728950e"
    private val copyName = "ProjectCopierServerIdTest copy"
    private lateinit var sourceDir: File

    @Before
    fun setUp() {
        val project = Project(ApplicationProvider.getApplicationContext(), ProjectCopierServerIdTest::class.java.simpleName)
        ProjectManager.getInstance().currentProject = project
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
        sourceDir = project.directory
        File(sourceDir, Constants.SERVER_PROJECT_ID_FILE_NAME).writeText(ownId)
    }

    @After
    fun tearDown() {
        StorageOperations.deleteDir(sourceDir)
        StorageOperations.deleteDir(copyDir())
    }

    @Test
    fun copyIsUploadedAsANewProject() {
        val copied = CompletableFuture<Boolean>()
        runBlocking {
            ProjectCopier(sourceDir, copyName).copyProjectAsync(
                { copied.complete(it) },
                this,
                Dispatchers.Unconfined
            )
        }

        assertTrue(copied.get(10, TimeUnit.SECONDS))
        assertNull(ProjectIdUtils.readServerProjectId(copyDir()))
        assertEquals(ownId, ProjectIdUtils.readServerProjectId(sourceDir))
    }

    private fun copyDir() = File(sourceDir.parentFile, encodeSpecialCharsForFileSystem(copyName))
}
