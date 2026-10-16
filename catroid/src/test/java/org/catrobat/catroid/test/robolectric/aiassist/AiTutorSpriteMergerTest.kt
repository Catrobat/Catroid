/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.test.robolectric.aiassist

import android.os.Build
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME
import org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.aiassist.merge.AiTutorSpriteMerger
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AiTutorSpriteMergerTest {

    private lateinit var activity: SpriteActivity
    private lateinit var project: Project
    private lateinit var originalSprite: Sprite
    private lateinit var lookFile: File
    private lateinit var soundFile: File

    @Before
    fun setUp() {
        val controller = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = controller.get()
        project = Project(activity, javaClass.simpleName)

        val imageDirectory = File(project.defaultScene.directory, IMAGE_DIRECTORY_NAME)
        val soundDirectory = File(project.defaultScene.directory, SOUND_DIRECTORY_NAME)
        imageDirectory.mkdirs()
        soundDirectory.mkdirs()
        lookFile = File(imageDirectory, "heroLook.png").apply { writeBytes(byteArrayOf(0)) }
        soundFile = File(soundDirectory, "heroSound.mp3").apply { writeBytes(byteArrayOf(0)) }

        originalSprite = Sprite("Hero").apply {
            lookList.add(LookData("heroLook", lookFile))
            soundList.add(SoundInfo("heroSound", soundFile))
            addScript(StartScript().apply { addBrick(SetXBrick()) })
        }
        project.defaultScene.addSprite(originalSprite)
        ProjectManager.getInstance().apply {
            currentProject = project
            currentSprite = originalSprite
            currentlyEditedScene = project.defaultScene
        }
        controller.create().resume()
    }

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
        StorageOperations.deleteDir(project.directory)
    }

    @Test
    fun renamedAiSpriteKeepsOriginalName() {
        val aiSprite = aiCopyOf(originalSprite).apply { name = "Hero2" }

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        assertEquals("Hero", aiSprite.name)
    }

    @Test
    fun emptyAiLookListKeepsOriginalLooks() {
        val aiSprite = aiCopyOf(originalSprite).apply { lookList.clear() }

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        assertEquals(1, aiSprite.lookList.size)
        assertEquals(lookFile, aiSprite.lookList[0].file)
    }

    @Test
    fun emptyAiSoundListKeepsOriginalSounds() {
        val aiSprite = aiCopyOf(originalSprite).apply { soundList.clear() }

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        assertEquals(1, aiSprite.soundList.size)
        assertEquals(soundFile, aiSprite.soundList[0].file)
    }

    @Test
    fun mediaAlreadyInAiSpriteIsNotDuplicated() {
        val aiSprite = aiCopyOf(originalSprite)

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        assertEquals(1, aiSprite.lookList.size)
        assertEquals(1, aiSprite.soundList.size)
    }

    @Test
    fun lookFileSurvivesSaveAndReload() {
        val aiSprite = aiCopyOf(originalSprite).apply { lookList.clear() }

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        val spriteList = project.defaultScene.spriteList
        spriteList[spriteList.indexOf(originalSprite)] = aiSprite
        XstreamSerializer.getInstance().saveProject(project)

        assertTrue(loadProject(project.directory, activity))
        assertTrue(lookFile.exists())

        val reloadedSprite =
            ProjectManager.getInstance().currentProject.defaultScene.getSprite("Hero")
        assertNotNull(reloadedSprite)
        assertEquals(1, reloadedSprite.lookList.size)
    }

    @Test
    fun newAiMediaIsDropped() {
        val aiSprite = aiCopyOf(originalSprite) { xml ->
            xml.replace(
                "</lookList>",
                "<look fileName=\"newLook.png\" name=\"newLook\"/></lookList>"
            )
                .replace(
                    "</soundList>",
                    "<sound fileName=\"newSound.mp3\" name=\"newSound\"/></soundList>"
                )
        }

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        assertEquals(listOf("heroLook"), aiSprite.lookList.map { it.name })
        assertEquals(listOf("heroSound"), aiSprite.soundList.map { it.name })
    }

    @Test
    fun renamedAiMediaGetsOriginalName() {
        val aiSprite = aiCopyOf(originalSprite) { xml ->
            xml.replace("name=\"heroLook\"", "name=\"renamedLook\"")
                .replace("name=\"heroSound\"", "name=\"renamedSound\"")
        }

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        assertEquals(listOf("heroLook"), aiSprite.lookList.map { it.name })
        assertEquals(listOf("heroSound"), aiSprite.soundList.map { it.name })
    }

    @Test
    fun setLookBrickPointsToLookInList() {
        originalSprite.scriptList[0].addBrick(SetLookBrick().apply { look = originalSprite.lookList[0] })
        val aiSprite = aiCopyOf(originalSprite)

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        val brick = aiSprite.scriptList[0].brickList[1] as SetLookBrick
        assertSame(aiSprite.lookList[0], brick.look)
    }

    @Test
    fun playSoundBrickPointsToSoundInList() {
        originalSprite.scriptList[0].addBrick(PlaySoundBrick().apply { sound = originalSprite.soundList[0] })
        val aiSprite = aiCopyOf(originalSprite)

        AiTutorSpriteMerger.preserveOriginalIdentityAndMedia(originalSprite, aiSprite)

        val brick = aiSprite.scriptList[0].brickList[1] as PlaySoundBrick
        assertSame(aiSprite.soundList[0], brick.sound)
    }

    private fun aiCopyOf(sprite: Sprite, editXml: (String) -> String = { it }): Sprite {
        val xml = XstreamSerializer.getInstance().getXmlAsStringFromSprite(sprite)
        return XstreamSerializer.getInstance().getSpriteFromXmlString(editXml(xml))
    }
}
