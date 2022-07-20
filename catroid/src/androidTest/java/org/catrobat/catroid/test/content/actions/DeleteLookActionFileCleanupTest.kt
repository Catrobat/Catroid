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

package org.catrobat.catroid.test.content.actions

import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.LookFileGarbageCollector
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class DeleteLookActionFileCleanupTest(
    private val name: String,
    private val numberOfAdditionalScenes: Int,
    private val numberOfSprites: Int,
    private val numberOfLooks: Int,
    private val numberOfUnusedFiles: Int
) {
    private lateinit var project: Project
    private var correctFileList: MutableList<Array<File>?> = ArrayList()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("Empty project with no unused files", 0, 0, 0, 0),
            arrayOf("Empty project with multiple unused files", 0, 0, 0, 5),
            arrayOf("Multiple sprites, no looks and no unused files", 0, 5, 0, 0),
            arrayOf("Multiple sprites, no looks and multiple unused files", 0, 5, 0, 5),
            arrayOf("Multiple sprites, looks and no unused files", 0, 5, 5, 0),
            arrayOf("Multiple sprites, looks and multiple unused files", 0, 5, 5, 5),
            arrayOf("Multiple scenes, sprites, no looks and no unused files", 5, 5, 0, 0),
            arrayOf("Multiple scenes, sprites, no looks and multiple unused files", 5, 5, 0, 5),
            arrayOf("Multiple scenes, sprites, looks and no unused files", 5, 5, 5, 0),
            arrayOf("Multiple scenes, sprites, looks and multiple unused files", 5, 5, 5, 5)
        )
    }

    @Before
    fun setUp() {
        this.createTestProject()
        project.sceneList.forEach() { scene ->
            val imageDirectory = File(scene.directory, Constants.IMAGE_DIRECTORY_NAME)
            correctFileList.add(imageDirectory.listFiles())
        }
        addUnusedLookFilesToImageDirectory()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(name)
    }

    @Test
    fun testFileCleanUp() {
        for (scene in project.sceneList) {
            val imageDirectory =
                File(scene.directory, Constants.IMAGE_DIRECTORY_NAME)
            val nomediaOffset = 1
            val totalNumberOfLooks = numberOfLooks * numberOfSprites + numberOfUnusedFiles +
                nomediaOffset
            assertEquals(totalNumberOfLooks, imageDirectory.listFiles()?.size)
        }

        LookFileGarbageCollector().cleanUpUnusedLookFiles(project)

        for ((index, scene) in project.sceneList.withIndex()) {
            val imageDirectory = File(scene.directory, Constants.IMAGE_DIRECTORY_NAME)
            assertArrayEquals(correctFileList[index], imageDirectory.listFiles())
        }
    }

    private fun createTestProject() {
        project = Project(ApplicationProvider.getApplicationContext(), name)

        repeat(numberOfAdditionalScenes) { sceneNumber ->
            Scene("Scene $sceneNumber", project)
        }
        XstreamSerializer.getInstance().saveProject(project)
        for (scene in project.sceneList) {
            repeat(numberOfSprites) { spriteNumber ->
                val sprite = Sprite("Sprite $spriteNumber")
                addLooksToSprite(sprite, numberOfLooks, scene)
                scene.addSprite(sprite)
            }
        }

        XstreamSerializer.getInstance().saveProject(project)
    }

    private fun addLooksToSprite(sprite: Sprite, numberOfLooks: Int, scene: Scene) {
        repeat(numberOfLooks) { lookNumber ->
            addLookDataToSprite(sprite, scene, "${sprite.name} Look $lookNumber")
        }
    }

    private fun addLookDataToSprite(sprite: Sprite, currentScene: Scene, name: String) {
        val imageDirectory = File(currentScene.directory, Constants.IMAGE_DIRECTORY_NAME)
        val lookDataFile = File(imageDirectory, name)
        lookDataFile.createNewFile()
        val lookData = LookData(name, lookDataFile)
        sprite.lookList?.add(lookData)
        sprite.look.lookData = sprite.lookList.first()
    }

    private fun addUnusedLookFilesToImageDirectory() {
        repeat(numberOfUnusedFiles) { fileNumber ->
            val imageDirectory =
                File(project.defaultScene?.directory, Constants.IMAGE_DIRECTORY_NAME)
            val lookDataFile = File(imageDirectory, "UnusedLookFile $fileNumber")
            lookDataFile.createNewFile()
        }
    }
}
