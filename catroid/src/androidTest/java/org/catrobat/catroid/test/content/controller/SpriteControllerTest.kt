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
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.uiespresso.util.FileTestUtils
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ResourceImporter
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
class SpriteControllerTest {
    private var project: Project? = null
    private var scene: Scene? = null
    private var sprite: Sprite? = null
    private var backpackListManager: BackpackListManager? = null
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
    @Throws(IOException::class)
    fun testCopySprite() {
        val controller = SpriteController()
        val spriteVarName = "spriteVar"
        val spriteListName = "spriteList"
        val userDefinedBrick = UserDefinedBrick()
        Assert.assertTrue(sprite!!.addUserVariable(UserVariable(spriteVarName)))
        Assert.assertTrue(sprite!!.addUserList(UserList(spriteListName)))
        sprite!!.addUserDefinedBrick(userDefinedBrick)
        val copy = controller.copy(sprite, project, scene)
        Assert.assertEquals(2, scene!!.spriteList.size)
        Assert.assertEquals(sprite!!.lookList.size, copy.lookList.size)
        Assert.assertEquals(sprite!!.soundList.size, copy.soundList.size)
        Assert.assertEquals(sprite!!.numberOfScripts, copy.numberOfScripts)
        Assert.assertEquals(sprite!!.numberOfBricks, copy.numberOfBricks)
        Assert.assertNotNull(sprite!!.getUserVariable(spriteVarName))
        Assert.assertNotNull(copy.getUserVariable(spriteVarName))
        Assert.assertNotSame(
            sprite!!.getUserVariable(spriteVarName),
            copy.getUserVariable(spriteVarName)
        )
        Assert.assertNotNull(sprite!!.getUserList(spriteListName))
        Assert.assertNotNull(copy.getUserList(spriteListName))
        Assert.assertNotSame(
            sprite!!.getUserList(spriteListName),
            copy.getUserList(spriteListName)
        )
        Assert.assertNotNull(sprite!!.getUserDefinedBrickWithSameUserData(userDefinedBrick))
        Assert.assertNotNull(copy.getUserDefinedBrickWithSameUserData(userDefinedBrick))
        Assert.assertNotSame(
            sprite!!.getUserDefinedBrickWithSameUserData(userDefinedBrick),
            copy.getUserDefinedBrickWithSameUserData(userDefinedBrick)
        )
        FileTestUtils.assertFileExists(copy.lookList[0].file)
        FileTestUtils.assertFileExists(copy.soundList[0].file)
    }

    @Test
    fun testConvertSpriteToGroupItemSprite() {
        val controller = SpriteController()
        val spriteVarName = "spriteVar"
        val spriteListName = "spriteList"
        val userDefinedBrick = UserDefinedBrick()
        Assert.assertTrue(sprite!!.addUserVariable(UserVariable(spriteVarName)))
        Assert.assertTrue(sprite!!.addUserList(UserList(spriteListName)))
        sprite!!.addUserDefinedBrick(userDefinedBrick)
        sprite!!.setConvertToGroupItemSprite(true)
        val groupItemSprite = controller.convert(sprite)
        Assert.assertEquals(2, scene!!.spriteList.size)
        Assert.assertEquals(sprite!!.lookList.size, groupItemSprite.lookList.size)
        Assert.assertEquals(sprite!!.soundList.size, groupItemSprite.soundList.size)
        Assert.assertEquals(sprite!!.numberOfScripts, groupItemSprite.numberOfScripts)
        Assert.assertEquals(sprite!!.numberOfBricks, groupItemSprite.numberOfBricks)
        Assert.assertNotNull(sprite!!.getUserVariable(spriteVarName))
        Assert.assertNotNull(groupItemSprite.getUserVariable(spriteVarName))
        Assert.assertNotNull(sprite!!.getUserList(spriteListName))
        Assert.assertNotNull(groupItemSprite.getUserList(spriteListName))
        Assert.assertNotNull(sprite!!.getUserDefinedBrickWithSameUserData(userDefinedBrick))
        Assert.assertNotNull(groupItemSprite.getUserDefinedBrickWithSameUserData(userDefinedBrick))
        FileTestUtils.assertFileExists(groupItemSprite.lookList[0].file)
        FileTestUtils.assertFileExists(groupItemSprite.soundList[0].file)
    }

    @Test
    fun testDeleteSprite() {
        val controller = SpriteController()
        val spriteVarName = "spriteVar"
        val spriteListName = "spriteList"
        Assert.assertTrue(sprite!!.addUserVariable(UserVariable(spriteVarName)))
        Assert.assertTrue(sprite!!.addUserList(UserList(spriteListName)))
        val deletedLookFile = sprite!!.lookList[0].file
        val deletedSoundFile = sprite!!.soundList[0].file
        controller.delete(sprite)
        Assert.assertEquals(2, scene!!.spriteList.size)
        FileTestUtils.assertFileDoesNotExist(deletedLookFile)
        FileTestUtils.assertFileDoesNotExist(deletedSoundFile)
    }

    @Test
    @Throws(IOException::class)
    fun testPackSprite() {
        val controller = SpriteController()
        val packedSprite = controller.pack(sprite)
        Assert.assertEquals(0, backpackListManager!!.sprites.size)
        Assert.assertEquals(sprite!!.lookList.size, packedSprite.lookList.size)
        Assert.assertEquals(sprite!!.soundList.size, packedSprite.soundList.size)
        Assert.assertEquals(sprite!!.numberOfScripts, packedSprite.numberOfScripts)
        Assert.assertEquals(sprite!!.numberOfBricks, packedSprite.numberOfBricks)
        FileTestUtils.assertFileExistsInDirectory(
            packedSprite.lookList[0].file,
            backpackListManager!!.backpackImageDirectory
        )
        FileTestUtils.assertFileExistsInDirectory(
            packedSprite.soundList[0].file,
            backpackListManager!!.backpackSoundDirectory
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteSpriteFromBackPack() {
        val controller = SpriteController()
        val packedSprite = controller.pack(sprite)
        controller.delete(packedSprite)
        Assert.assertEquals(0, BackpackListManager.getInstance().sprites.size)
        FileTestUtils.assertFileDoesNotExistInDirectory(
            packedSprite.lookList[0].file,
            backpackListManager!!.backpackImageDirectory
        )
        FileTestUtils.assertFileDoesNotExistInDirectory(
            packedSprite.soundList[0].file,
            backpackListManager!!.backpackSoundDirectory
        )
    }

    @Test
    @Throws(IOException::class)
    fun testUnpackSprite() {
        val controller = SpriteController()
        val packedSprite = controller.pack(sprite)
        val unpackedSprite = controller.unpack(packedSprite, project, scene)
        Assert.assertEquals(0, BackpackListManager.getInstance().sprites.size)
        FileTestUtils.assertFileExistsInDirectory(
            packedSprite.lookList[0].file,
            backpackListManager!!.backpackImageDirectory
        )
        FileTestUtils.assertFileExistsInDirectory(
            packedSprite.soundList[0].file,
            backpackListManager!!.backpackSoundDirectory
        )
        Assert.assertEquals(2, scene!!.spriteList.size)
        Assert.assertEquals(sprite!!.lookList.size, unpackedSprite.lookList.size)
        Assert.assertEquals(sprite!!.soundList.size, unpackedSprite.soundList.size)
        Assert.assertEquals(sprite!!.numberOfScripts, unpackedSprite.numberOfScripts)
        Assert.assertEquals(sprite!!.numberOfBricks, unpackedSprite.numberOfBricks)
        FileTestUtils.assertFileExists(unpackedSprite.lookList[0].file)
        FileTestUtils.assertFileExists(unpackedSprite.soundList[0].file)
    }

    @Test
    @Throws(IOException::class)
    fun testDeepCopySprite() {
        val controller = SpriteController()
        val copy = controller.copy(sprite, project, scene)
        Assert.assertEquals(2, scene!!.spriteList.size)
        Assert.assertEquals(sprite!!.lookList.size, copy.lookList.size)
        Assert.assertEquals(sprite!!.soundList.size, copy.soundList.size)
        Assert.assertEquals(sprite!!.numberOfScripts, copy.numberOfScripts)
        Assert.assertEquals(sprite!!.numberOfBricks, copy.numberOfBricks)
        FileTestUtils.assertFileExists(copy.lookList[0].file)
        FileTestUtils.assertFileExists(copy.soundList[0].file)
        controller.delete(sprite)
        Assert.assertEquals(sprite!!.lookList.size, copy.lookList.size)
        Assert.assertEquals(sprite!!.soundList.size, copy.soundList.size)
        Assert.assertEquals(sprite!!.numberOfScripts, copy.numberOfScripts)
        Assert.assertEquals(sprite!!.numberOfBricks, copy.numberOfBricks)
        FileTestUtils.assertFileExists(copy.lookList[0].file)
        FileTestUtils.assertFileExists(copy.soundList[0].file)
        FileTestUtils.assertFileDoesNotExist(sprite!!.lookList[0].file)
        FileTestUtils.assertFileDoesNotExist(sprite!!.soundList[0].file)
    }

    @Throws(IOException::class)
    private fun createProject() {
        project = Project(ApplicationProvider.getApplicationContext(), "SpriteControllerTest")
        scene = project!!.defaultScene
        ProjectManager.getInstance().currentProject = project
        sprite = Sprite("testSprite")
        scene?.addSprite(sprite)
        val script = StartScript()
        val placeAtBrick = PlaceAtBrick(0, 0)
        script.addBrick(placeAtBrick)
        sprite!!.addScript(script)
        XstreamSerializer.getInstance().saveProject(project)
        val imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.red_image,
            File(project!!.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            "red_image.bmp", 1.0
        )
        sprite!!.lookList.add(LookData("testLook", imageFile))
        val soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.longsound,
            File(project!!.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "longsound.mp3"
        )
        sprite!!.soundList.add(SoundInfo("testSound", soundFile))
        XstreamSerializer.getInstance().saveProject(project)
    }

    @Throws(IOException::class)
    private fun deleteProject() {
        if (project!!.directory.exists()) {
            StorageOperations.deleteDir(project!!.directory)
        }
    }
}