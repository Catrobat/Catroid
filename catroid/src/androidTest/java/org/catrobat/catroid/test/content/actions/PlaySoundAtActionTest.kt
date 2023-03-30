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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.TestCase
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.PlaySoundAtAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException

@RunWith(JUnit4::class)
class PlaySoundAtActionTest {
    private var action: PlaySoundAtAction? = null
    @Before
    @Throws(IOException::class)
    fun setUp() {
        TestUtils.deleteProjects()
        soundManager.clear()
        project = Project(ApplicationProvider.getApplicationContext(), "projectName")
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        soundFile = TestUtils.createSoundFile(project, R.raw.testsoundui, "soundTest.mp3")
    }

    @After
    @Throws(IOException::class)
    fun cleanup() {
        TestUtils.deleteProjects()
        soundManager.clear()
    }

    @Test
    fun testPlaySound() {
        soundManager.clear()
        val offset = 0.0f
        val testSprite = Sprite("testSprite")
        val soundinfo = createSoundInfo(soundFile)
        val factory = testSprite.actionFactory
        testSprite.soundList.add(soundinfo)
        action = factory.createPlaySoundAtAction(
            testSprite,
            SequenceAction(),
            Formula(offset), soundinfo
        ) as PlaySoundAtAction
        action!!.act(1.0f)
        val mediaPlayers = soundManager.mediaPlayers
        TestCase.assertEquals(1, mediaPlayers.size)
        Assert.assertTrue(mediaPlayers[0].isPlaying)
    }

    @Test
    fun testPlaySoundNoOffset() {
        soundManager.clear()
        val offset = 0.0f
        val soundDuration = soundManager.getDurationOfSoundFile(
            soundFile?.absolutePath ?: error("Sound file is null")
        )
        val testSprite = Sprite("testSprite")
        val soundinfo = createSoundInfo(soundFile)
        val factory = testSprite.actionFactory
        testSprite.soundList.add(soundinfo)
        action = factory.createPlaySoundAtAction(
            testSprite,
            SequenceAction(),
            Formula(offset), soundinfo
        ) as PlaySoundAtAction
        val playedDuration = action?.runWithMockedSoundManager(soundManager) ?: error("Action is null")
        action?.act(1.0f) ?: error("Failed to execute action")
        TestCase.assertEquals(playedDuration, soundDuration)
    }

    @Test
    fun testPlaySoundAtOffset() {
        soundManager.clear()
        val offset = 1.5f
        val soundDuration = soundManager.getDurationOfSoundFile(
            soundFile?.absolutePath ?: error("Sound file is null")
        ) - offset * 1000.0f
        val testSprite = Sprite("testSprite")
        val soundinfo = createSoundInfo(soundFile)
        val factory = testSprite.actionFactory
        testSprite.soundList.add(soundinfo)
        action = factory.createPlaySoundAtAction(
            testSprite,
            SequenceAction(),
            Formula(offset), soundinfo
        ) as PlaySoundAtAction
        val playedDuration = action?.runWithMockedSoundManager(soundManager) ?: error("Action is null")
        action?.act(1.0f) ?: error("Failed to execute action")
        TestCase.assertEquals(playedDuration, soundDuration)
    }

    @Test
    fun testPlaySoundWrongParameter() {
        soundManager.clear()
        val testSprite = Sprite("testSprite")
        val soundinfo = createSoundInfo(soundFile)
        val factory = testSprite.actionFactory
        testSprite.soundList.add(soundinfo)
        action = factory.createPlaySoundAtAction(
            testSprite,
            SequenceAction(),
            Formula("WrongParameter"), soundinfo
        ) as PlaySoundAtAction
        action!!.act(1.0f)
        val mediaPlayers = soundManager.mediaPlayers
        TestCase.assertEquals(0, mediaPlayers.size)
    }

    private fun createSoundInfo(soundFile: File?): SoundInfo {
        val soundInfo = SoundInfo()
        soundInfo.file = soundFile
        return soundInfo
    }

    companion object {
        private val soundManager = SoundManager.getInstance()
        private var project: Project? = null
        private var soundFile: File? = null
    }
}
