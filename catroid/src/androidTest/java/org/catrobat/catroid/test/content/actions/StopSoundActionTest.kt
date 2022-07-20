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
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.test.utils.TestUtils.createSoundFile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class StopSoundActionTest {
    private val soundManager = SoundManager.getInstance()
    lateinit var soundFile: File
    lateinit var project: Project
    lateinit var sprite: Sprite

    @Before
    fun setUp() {
        project = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
        soundFile = createSoundFile(project, R.raw.testsound, "soundTest.mp3")
        sprite = Sprite(TestUtils.DEFAULT_TEST_SPRITE_NAME)
    }

    @Test
    fun testStopOneSound() {
        val soundInfo = createSoundInfo(soundFile)
        sprite.soundList.add(soundInfo)

        assertTrue(sprite.actionFactory.createPlaySoundAction(sprite, soundInfo).act(1.0f))
        assertEquals(1, soundManager.mediaPlayers.size)
        assertTrue(soundManager.mediaPlayers[0].isPlaying)

        assertTrue(sprite.actionFactory.createStopSoundAction(sprite, soundInfo).act(1.0f))
        assertFalse(soundManager.mediaPlayers[0].isPlaying)
    }

    @Test
    fun testStopSimultaneousPlayingSounds() {
        val soundInfo1 = createSoundInfo(soundFile)
        val soundInfo2 = createSoundInfo(createSoundFile(project, R.raw.testsoundui, soundFile.name))
        sprite.soundList.add(soundInfo1)
        sprite.soundList.add(soundInfo2)

        assertTrue(sprite.actionFactory.createPlaySoundAction(sprite, soundInfo1).act(1.0f))
        assertTrue(sprite.actionFactory.createPlaySoundAction(sprite, soundInfo2).act(1.0f))
        assertEquals(2, soundManager.mediaPlayers.size)
        assertTrue(soundManager.mediaPlayers[0].isPlaying)
        assertTrue(soundManager.mediaPlayers[1].isPlaying)

        assertTrue(sprite.actionFactory.createStopSoundAction(sprite, soundInfo1).act(1.0f))
        assertFalse(soundManager.mediaPlayers[0].isPlaying)
        assertTrue(soundManager.mediaPlayers[1].isPlaying)
    }

    @After
    fun tearDown() {
        soundManager.clear()
    }

    private fun createSoundInfo(soundFile: File) = SoundInfo().also { it.file = soundFile }
}
