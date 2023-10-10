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
import org.junit.runners.Parameterized
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.common.DefaultProjectHandler
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.ProjectManager
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.Arrays

@RunWith(Parameterized::class)
class ProjectRenamerSpecialCharactersTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var specialCharacterProjectName: String? = null
    @JvmField
    @Parameterized.Parameter(2)
    var specialCharacterEncodedProjectName: String? = null
    private val projectNameWithoutSpecialCharacter = "projectName"
    @Before
    @Throws(IOException::class)
    fun setUp() {
        TestUtils.deleteProjects(
            specialCharacterEncodedProjectName,
            projectNameWithoutSpecialCharacter
        )
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(
            specialCharacterEncodedProjectName,
            projectNameWithoutSpecialCharacter
        )
    }

    @Test
    @Throws(IOException::class)
    fun testRenameFromSpecialCharacter() {
        var project = DefaultProjectHandler.createAndSaveDefaultProject(
            specialCharacterProjectName,
            ApplicationProvider.getApplicationContext(), false
        )
        val renamedDirectory = renameProject(project.directory, projectNameWithoutSpecialCharacter)
        Assert.assertNotNull(renamedDirectory)
        Assert.assertEquals(projectNameWithoutSpecialCharacter, renamedDirectory!!.name)
        Assert.assertTrue(
            loadProject(
                renamedDirectory,
                ApplicationProvider.getApplicationContext()
            )
        )
        project = ProjectManager.getInstance().currentProject
        Assert.assertEquals(projectNameWithoutSpecialCharacter, project.name)
    }

    @Test
    @Throws(IOException::class)
    fun testRenameToSpecialCharacter() {
        var project = DefaultProjectHandler.createAndSaveDefaultProject(
            projectNameWithoutSpecialCharacter,
            ApplicationProvider.getApplicationContext(), false
        )
        val renamedDirectory = renameProject(project.directory, specialCharacterProjectName!!)
        Assert.assertNotNull(renamedDirectory)
        val expectedDirectory = File(project.directory.parent, specialCharacterEncodedProjectName)
        Assert.assertEquals(expectedDirectory, renamedDirectory)
        Assert.assertTrue(
            loadProject(
                renamedDirectory,
                ApplicationProvider.getApplicationContext()
            )
        )
        project = ProjectManager.getInstance().currentProject
        Assert.assertEquals(specialCharacterProjectName, project.name)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "smallerThanTest",
                        "test<Project",
                        "test%3CProject"
                    ),
                    arrayOf(
                        "greaterThanTest",
                        "test>Project",
                        "test%3EProject"
                    ),
                    arrayOf("percentTest", "test%Project", "test%25Project"),
                    arrayOf("slashTest", "test/Project", "test%2FProject"),
                    arrayOf("slashTest", "test/Project", "test%2FProject"),
                    arrayOf("quoteTest", "test\"Project", "test%22Project"),
                    arrayOf("colonTest", "test:Project", "test%3AProject"),
                    arrayOf(
                        "questionmarkTest",
                        "test?Project",
                        "test%3FProject"
                    ),
                    arrayOf(
                        "backslashTest",
                        "test\\Project",
                        "test%5CProject"
                    ),
                    arrayOf("pipeTest", "test|Project", "test%7CProject"),
                    arrayOf("asteriskTest", "test*Project", "test%2AProject"),
                    arrayOf("dotTest", "test.Project", "test.Project"),
                    arrayOf("dotOnlyTest", ".", "%2E")
                )
            )
        }
    }
}
