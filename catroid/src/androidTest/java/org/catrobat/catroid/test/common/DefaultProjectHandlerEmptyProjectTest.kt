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
package org.catrobat.catroid.test.common

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DefaultProjectHandlerEmptyProjectTest {

    companion object {
        private const val projectName = "empty"
    }

    private lateinit var project: Project

    @After
    @Throws(Exception::class)
    fun tearDown() {
        StorageOperations.deleteDir(project.directory)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateAndSaveEmptyProject() {
        val landscapeMode = false
        val isCastProject = false
        val height = 1500
        val width = 1000

        project = DefaultProjectHandler.createAndSaveEmptyProject(
            projectName, ApplicationProvider.getApplicationContext(), landscapeMode,
            isCastProject, height, width
        )
        val header = project.xmlHeader

        assertTrue(project.directory.exists())
        assertEquals(landscapeMode, header.islandscapeMode())
        assertEquals(isCastProject, header.isCastProject)
        assertEquals(height, header.getVirtualScreenHeight())
        assertEquals(width, header.getVirtualScreenWidth())
        assertEquals(height, ScreenValues.SCREEN_HEIGHT)
        assertEquals(width, ScreenValues.SCREEN_WIDTH)
    }

    @Test(expected = IOException::class)
    @Throws(Exception::class)
    fun testCreateAndSaveEmptyProject_projectAlreadyExists() {

        project = DefaultProjectHandler.createAndSaveEmptyProject(
            projectName, ApplicationProvider.getApplicationContext(), true,
            false, 1000, 1500
        )

        // throws exception
        val project2 = DefaultProjectHandler.createAndSaveEmptyProject(
            projectName, ApplicationProvider.getApplicationContext(), true,
            false, 1000, 1500
        )

        StorageOperations.deleteDir(project2.directory)
    }
}
