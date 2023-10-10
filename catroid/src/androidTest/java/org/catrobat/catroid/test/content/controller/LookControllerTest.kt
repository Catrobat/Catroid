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
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.recyclerview.controller.LookController
import org.catrobat.catroid.uiespresso.util.FileTestUtils
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.test.R
import org.junit.After
import org.junit.Test
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LookControllerTest {
    private var project: Project? = null
    private var scene: Scene? = null
    private var sprite: Sprite? = null
    private var lookData: LookData? = null
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
    fun testCopyLook() {
        val controller = LookController()
        val copy = controller.copy(lookData, scene, sprite)
        Assert.assertEquals(1, sprite!!.lookList.size)
        FileTestUtils.assertFileExists(copy.file)
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteLook() {
        val controller = LookController()
        val deletedLookFile = lookData!!.file
        controller.delete(lookData)
        Assert.assertEquals(1, sprite!!.lookList.size)
        FileTestUtils.assertFileDoesNotExist(deletedLookFile)
    }

    @Test
    @Throws(IOException::class)
    fun testPackLook() {
        val controller = LookController()
        val packedLook = controller.pack(lookData)
        Assert.assertEquals(0, backpackListManager!!.backpackedLooks.size)
        FileTestUtils.assertFileExistsInDirectory(
            packedLook.file,
            backpackListManager!!.backpackImageDirectory
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteLookFromBackPack() {
        val controller = LookController()
        val packedLook = controller.pack(lookData)
        controller.delete(packedLook)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpackedLooks.size)
        FileTestUtils.assertFileDoesNotExistInDirectory(
            packedLook.file,
            backpackListManager!!.backpackImageDirectory
        )
        Assert.assertEquals(1, sprite!!.lookList.size)
        FileTestUtils.assertFileExists(lookData!!.file)
    }

    @Test
    @Throws(IOException::class)
    fun testUnpackLook() {
        val controller = LookController()
        val packedLook = controller.pack(lookData)
        val unpackedLook = controller.unpack(packedLook, scene, sprite)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpackedLooks.size)
        FileTestUtils.assertFileExistsInDirectory(
            packedLook.file,
            backpackListManager!!.backpackImageDirectory
        )
        Assert.assertEquals(1, sprite!!.lookList.size)
        FileTestUtils.assertFileExists(unpackedLook.file)
    }

    @Test
    @Throws(IOException::class)
    fun testDeepCopyLook() {
        val controller = LookController()
        val copy = controller.copy(lookData, scene, sprite)
        FileTestUtils.assertFileExists(copy.file)
        controller.delete(copy)
        FileTestUtils.assertFileDoesNotExist(copy.file)
        FileTestUtils.assertFileExists(lookData!!.file)
    }

    @Throws(IOException::class)
    private fun createProject() {
        project = Project(ApplicationProvider.getApplicationContext(), "LookControllerTest")
        scene = project!!.defaultScene
        ProjectManager.getInstance().currentProject = project
        sprite = Sprite("testSprite")
        project!!.defaultScene.addSprite(sprite)
        XstreamSerializer.getInstance().saveProject(project)
        val imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.red_image,
            File(project!!.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            "red_image.bmp", 1.0
        )
        lookData = LookData("testLook", imageFile)
        sprite!!.lookList.add(lookData)
        XstreamSerializer.getInstance().saveProject(project)
    }

    @Throws(IOException::class)
    private fun deleteProject() {
        if (project!!.directory.exists()) {
            StorageOperations.deleteDir(project!!.directory)
        }
    }
}