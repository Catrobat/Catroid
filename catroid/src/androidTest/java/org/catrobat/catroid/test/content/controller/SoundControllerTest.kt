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
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.recyclerview.controller.SoundController
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
class SoundControllerTest {
    private var project: Project? = null
    private var scene: Scene? = null
    private var sprite: Sprite? = null
    private var soundInfo: SoundInfo? = null
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
    fun testCopySound() {
        val controller = SoundController()
        val copy = controller.copy(soundInfo, scene, sprite)
        Assert.assertEquals(1, sprite!!.soundList.size)
        FileTestUtils.assertFileExists(copy.file)
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteSound() {
        val controller = SoundController()
        val deletedSoundFile = soundInfo!!.file
        controller.delete(soundInfo)
        Assert.assertEquals(1, sprite!!.soundList.size)
        FileTestUtils.assertFileDoesNotExist(deletedSoundFile)
    }

    @Test
    @Throws(IOException::class)
    fun testPackSound() {
        val controller = SoundController()
        val packedSound = controller.pack(soundInfo)
        Assert.assertEquals(0, backpackListManager!!.backpackedSounds.size)
        FileTestUtils.assertFileExists(packedSound.file)
        FileTestUtils.assertFileExistsInDirectory(
            packedSound.file,
            backpackListManager!!.backpackSoundDirectory
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteSoundFromBackPack() {
        val controller = SoundController()
        val packedSound = controller.pack(soundInfo)
        controller.delete(packedSound)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpackedSounds.size)
        FileTestUtils.assertFileDoesNotExist(packedSound.file)
        FileTestUtils.assertFileDoesNotExistInDirectory(
            packedSound.file,
            backpackListManager!!.backpackSoundDirectory
        )
        Assert.assertEquals(1, sprite!!.soundList.size)
        FileTestUtils.assertFileExists(soundInfo!!.file)
    }

    @Test
    @Throws(IOException::class)
    fun testUnpackSound() {
        val controller = SoundController()
        val packedSound = controller.pack(soundInfo)
        val unpackedSound = controller.unpack(packedSound, scene, sprite)
        Assert.assertEquals(0, BackpackListManager.getInstance().backpackedSounds.size)
        FileTestUtils.assertFileExists(packedSound.file)
        FileTestUtils.assertFileExistsInDirectory(
            packedSound.file,
            backpackListManager!!.backpackSoundDirectory
        )
        Assert.assertEquals(1, sprite!!.soundList.size)
        FileTestUtils.assertFileExists(unpackedSound.file)
    }

    @Test
    @Throws(IOException::class)
    fun testDeepCopySound() {
        val controller = SoundController()
        val copy = controller.copy(soundInfo, scene, sprite)
        FileTestUtils.assertFileExists(copy.file)
        controller.delete(copy)
        FileTestUtils.assertFileDoesNotExist(copy.file)
        FileTestUtils.assertFileExists(soundInfo!!.file)
    }

    @Throws(IOException::class)
    private fun createProject() {
        project = Project(ApplicationProvider.getApplicationContext(), "SoundControllerTest")
        scene = project!!.defaultScene
        ProjectManager.getInstance().currentProject = project
        sprite = Sprite("testSprite")
        scene?.addSprite(sprite)
        XstreamSerializer.getInstance().saveProject(project)
        val soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.longsound,
            File(project!!.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "longsound.mp3"
        )
        soundInfo = SoundInfo("testSound", soundFile)
        sprite!!.soundList.add(soundInfo)
        XstreamSerializer.getInstance().saveProject(project)
    }

    @Throws(IOException::class)
    private fun deleteProject() {
        if (project!!.directory.exists()) {
            StorageOperations.deleteDir(project!!.directory)
        }
    }
}