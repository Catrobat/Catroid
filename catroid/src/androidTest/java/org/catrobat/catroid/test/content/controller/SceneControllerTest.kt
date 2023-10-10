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
package org.catrobat.catroid.test.content.controller

import org.junit.runner.RunWith
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.recyclerview.controller.SceneController
import org.catrobat.catroid.uiespresso.util.FileTestUtils
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.HideTextBrick
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.test.R
import org.junit.After
import org.junit.Test
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SceneControllerTest {
    private var project: Project? = null
    private var scene: Scene? = null
    private var backpackListManager: BackpackListManager? = null
    private val newName = "new Scene Name"
    @Before
    @Throws(IOException::class)
    fun setUp() {
        backpackListManager = BackpackListManager.getInstance()
        TestUtils.clearBackPack(backpackListManager)
        createProject()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        deleteProject()
        TestUtils.clearBackPack(backpackListManager)
    }

    @Test
    fun testRenameScene() {
        val previousName = scene!!.name
        val controller = SceneController()
        controller.rename(scene, newName)
        Assert.assertEquals(newName, scene!!.name)
        Assert.assertEquals(File(project!!.directory, newName), scene!!.directory)
        FileTestUtils.assertFileDoesNotExist(File(project!!.directory, previousName))
        FileTestUtils.assertFileExists(File(project!!.directory, newName))
    }

    @Test
    @Throws(IOException::class)
    fun testCopyScene() {
        val controller = SceneController()
        val copy = controller.copy(scene, project)
        Assert.assertEquals(1, project!!.sceneList.size)
        Assert.assertEquals(scene!!.spriteList.size, copy.spriteList.size)
        for (i in copy.spriteList.indices) {
            Assert.assertEquals(
                scene!!.spriteList[i].lookList.size,
                copy.spriteList[i].lookList.size
            )
            Assert.assertEquals(
                scene!!.spriteList[i].soundList.size,
                copy.spriteList[i].soundList.size
            )
            Assert.assertEquals(
                scene!!.spriteList[i].numberOfScripts,
                copy.spriteList[i].numberOfScripts
            )
            Assert.assertEquals(
                scene!!.spriteList[i].numberOfBricks,
                copy.spriteList[i].numberOfBricks
            )
        }
        assertScreenshotFileExistsInScene(Constants.SCREENSHOT_AUTOMATIC_FILE_NAME, copy)
        assertLookFileExistsInScene(copy.spriteList[1].lookList[0].file.name, copy)
        assertSoundFileExistsInScene(copy.spriteList[1].soundList[0].file.name, copy)
    }

    @Test
    @Throws(IOException::class)
    fun testCopySceneWithManualScreenshot() {
        ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.icon,
            File(scene!!.directory.path),
            Constants.SCREENSHOT_MANUAL_FILE_NAME, 1.0
        )
        XstreamSerializer.getInstance().saveProject(project)
        val controller = SceneController()
        val copy = controller.copy(scene, project)
        assertScreenshotFileExistsInScene(Constants.SCREENSHOT_MANUAL_FILE_NAME, copy)
        FileTestUtils.assertFileDoesNotExist(
            File(
                copy.directory,
                Constants.SCREENSHOT_AUTOMATIC_FILE_NAME
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteScene() {
        val controller = SceneController()
        val deletedSceneDirectory = scene!!.directory
        controller.delete(scene)
        Assert.assertEquals(0, project!!.sceneList.size)
        FileTestUtils.assertFileDoesNotExist(deletedSceneDirectory)
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteAfterRenameScene() {
        val controller = SceneController()
        val sceneToRename = Scene("Scene To Rename", project!!)
        project!!.addScene(sceneToRename)
        XstreamSerializer.getInstance().saveProject(project)
        val renamedSceneDirectory = sceneToRename.directory
        controller.rename(sceneToRename, newName)
        controller.delete(sceneToRename)
        Assert.assertEquals(1, project!!.sceneList.size)
        FileTestUtils.assertFileDoesNotExist(renamedSceneDirectory)
    }

    @Test
    @Throws(IOException::class)
    fun testPackScene() {
        val controller = SceneController()
        val packedScene = controller.pack(scene)
        Assert.assertEquals(0, BackpackListManager.getInstance().scenes.size)
        Assert.assertEquals(
            File(backpackListManager!!.backpackSceneDirectory, packedScene.name),
            packedScene.directory
        )
        FileTestUtils.assertFileExists(packedScene.directory)
        Assert.assertEquals(scene!!.spriteList.size, packedScene.spriteList.size)
        for (i in packedScene.spriteList.indices) {
            Assert.assertEquals(
                scene!!.spriteList[i].lookList.size,
                packedScene.spriteList[i].lookList.size
            )
            Assert.assertEquals(
                scene!!.spriteList[i].soundList.size,
                packedScene.spriteList[i].soundList.size
            )
            Assert.assertEquals(
                scene!!.spriteList[i].numberOfScripts,
                packedScene.spriteList[i].numberOfScripts
            )
            Assert.assertEquals(
                scene!!.spriteList[i].numberOfBricks,
                packedScene.spriteList[i].numberOfBricks
            )
        }
        assertScreenshotFileExistsInScene(Constants.SCREENSHOT_AUTOMATIC_FILE_NAME, packedScene)
        assertLookFileExistsInScene(packedScene.spriteList[1].lookList[0].file.name, packedScene)
        assertSoundFileExistsInScene(packedScene.spriteList[1].soundList[0].file.name, packedScene)
    }

    @Test
    @Throws(IOException::class)
    fun testUnpackScene() {
        val controller = SceneController()
        val packedScene = controller.pack(scene)
        val unpackedScene = controller.unpack(packedScene, project)
        Assert.assertEquals(0, BackpackListManager.getInstance().scenes.size)
        Assert.assertEquals(1, project!!.sceneList.size)
        Assert.assertEquals(scene!!.spriteList.size, unpackedScene.spriteList.size)
        for (i in unpackedScene.spriteList.indices) {
            Assert.assertEquals(
                scene!!.spriteList[i].lookList.size,
                unpackedScene.spriteList[i].lookList.size
            )
            Assert.assertEquals(
                scene!!.spriteList[i].soundList.size,
                unpackedScene.spriteList[i].soundList.size
            )
            Assert.assertEquals(
                scene!!.spriteList[i].numberOfScripts,
                unpackedScene.spriteList[i].numberOfScripts
            )
            Assert.assertEquals(
                scene!!.spriteList[i].numberOfBricks,
                unpackedScene.spriteList[i].numberOfBricks
            )
        }
        assertScreenshotFileExistsInScene(Constants.SCREENSHOT_AUTOMATIC_FILE_NAME, unpackedScene)
        assertLookFileExistsInScene(
            unpackedScene.spriteList[1].lookList[0].file.name,
            unpackedScene
        )
        assertSoundFileExistsInScene(
            unpackedScene.spriteList[1].soundList[0].file.name,
            unpackedScene
        )
    }

    private fun assertScreenshotFileExistsInScene(fileName: String, scene: Scene) {
        FileTestUtils.assertFileExists(File(scene.directory, fileName))
    }

    private fun assertLookFileExistsInScene(fileName: String, scene: Scene) {
        FileTestUtils.assertFileExists(
            File(
                File(scene.directory, Constants.IMAGE_DIRECTORY_NAME),
                fileName
            )
        )
    }

    private fun assertSoundFileExistsInScene(fileName: String, scene: Scene) {
        FileTestUtils.assertFileExists(
            File(
                File(scene.directory, Constants.SOUND_DIRECTORY_NAME),
                fileName
            )
        )
    }

    @Throws(IOException::class)
    private fun createProject() {
        project = Project(ApplicationProvider.getApplicationContext(), "SpriteControllerTest")
        scene = project!!.defaultScene
        ProjectManager.getInstance().currentProject = project
        val sprite = Sprite("testSprite")
        scene?.addSprite(sprite)
        val spriteVarName = "spriteVar"
        val spriteListName = "spriteList"
        Assert.assertTrue(sprite.addUserVariable(UserVariable(spriteVarName)))
        Assert.assertTrue(sprite.addUserList(UserList(spriteListName)))
        val script = StartScript()
        val placeAtBrick = PlaceAtBrick(0, 0)
        script.addBrick(placeAtBrick)
        script.addBrick(HideTextBrick())
        script.addBrick(AddItemToUserListBrick())
        script.addBrick(AssertUserListsBrick())
        sprite.addScript(script)
        XstreamSerializer.getInstance().saveProject(project)
        ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.icon,
            File(scene?.getDirectory()?.path),
            Constants.SCREENSHOT_AUTOMATIC_FILE_NAME, 1.0
        )
        val imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.red_image,
            File(project!!.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            "red_image.png", 1.0
        )
        sprite.lookList.add(LookData("testLook", imageFile))
        val soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.longsound,
            File(project!!.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "longsound.mp3"
        )
        sprite.soundList.add(SoundInfo("testSound", soundFile))
        XstreamSerializer.getInstance().saveProject(project)
    }

    @Throws(IOException::class)
    private fun deleteProject() {
        if (project!!.directory.exists()) {
            StorageOperations.deleteDir(project!!.directory)
        }
    }
}