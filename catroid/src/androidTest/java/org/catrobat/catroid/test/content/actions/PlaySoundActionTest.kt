/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PlaySoundActionTest {
    private val soundManager = SoundManager.getInstance()
    lateinit var soundFile: File
    lateinit var project: Project

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestUtils.deleteProjects()
        soundManager.clear()
        createTestProject()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects()
        soundManager.clear()
    }

    @Test
    fun testPlaySound() {
        val testSprite = Sprite("testSprite")
        val soundInfo = createSoundInfo(soundFile)
        testSprite.soundList.add(soundInfo)
        val factory = testSprite.actionFactory
        val action = factory.createPlaySoundAction(testSprite, soundInfo)
        action.act(1.0f)
        val mediaPlayers = soundManager.mediaPlayers
        Assert.assertEquals(1, mediaPlayers.size)
        Assert.assertTrue(mediaPlayers[0].isPlaying)
    }

    @Test
    @Throws(IOException::class)
    fun testPlaySimultaneousSounds() {
        val soundFile2 = TestUtils.createSoundFile(project, R.raw.testsoundui, "soundTest.mp3")
        val testSprite = Sprite("testSprite")
        val soundInfo = createSoundInfo(soundFile)
        val soundInfo2 = createSoundInfo(soundFile2)
        testSprite.soundList.add(soundInfo)
        testSprite.soundList.add(soundInfo2)
        val factory = testSprite.actionFactory
        val playSoundAction1 = factory.createPlaySoundAction(testSprite, soundInfo)
        val playSoundAction2 = factory.createPlaySoundAction(testSprite, soundInfo2)
        playSoundAction1.act(1.0f)
        playSoundAction2.act(1.0f)
        val mediaPlayers = soundManager.mediaPlayers
        Assert.assertEquals(2, mediaPlayers.size)
        Assert.assertTrue(mediaPlayers[0].isPlaying)
        Assert.assertTrue(mediaPlayers[1].isPlaying)
    }

    @Throws(IOException::class)
    private fun createTestProject() {
        val projectName = "testProject"
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        soundFile = TestUtils.createSoundFile(project, R.raw.testsound, "soundTest.mp3")
    }

    private fun createSoundInfo(soundFile: File?): SoundInfo {
        val soundInfo = SoundInfo()
        soundInfo.file = soundFile
        return soundInfo
    }
}
