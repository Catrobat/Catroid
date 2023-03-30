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

import org.catrobat.catroid.io.asynctask.renameProject
import org.catrobat.catroid.io.asynctask.loadProject
import org.junit.runner.RunWith
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.common.DefaultProjectHandler
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.Project
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProjectRenamerTest {
    private val projectName = "testProject"
    private val renamedProjectName = "renamedTestProject"
    private var defaultProject: Project? = null
    @Before
    @Throws(IOException::class)
    fun setUp() {
        TestUtils.deleteProjects(projectName, renamedProjectName)
        defaultProject = DefaultProjectHandler.createAndSaveDefaultProject(
            projectName,
            ApplicationProvider.getApplicationContext(), false
        )
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName, renamedProjectName)
    }

    @Test
    @Throws(IOException::class)
    fun projectRenameTaskTest() {
        val renamedDirectory = renameProject(defaultProject!!.directory, renamedProjectName)
        Assert.assertNotNull(renamedDirectory)
        Assert.assertEquals(renamedProjectName, renamedDirectory!!.name)
        Assert.assertTrue(
            loadProject(
                renamedDirectory,
                ApplicationProvider.getApplicationContext()
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun projectDirectoryRenameTest() {
        val expectedDirectory = File(defaultProject!!.directory.parent, renamedProjectName)
        val renamedDirectory = renameProject(defaultProject!!.directory, renamedProjectName)
        Assert.assertNotNull(renamedDirectory)
        Assert.assertEquals(expectedDirectory, renamedDirectory)
        Assert.assertTrue(
            loadProject(
                renamedDirectory,
                ApplicationProvider.getApplicationContext()
            )
        )
    }
}