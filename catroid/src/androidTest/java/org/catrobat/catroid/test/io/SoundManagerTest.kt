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
package org.catrobat.catroid.test.io

import android.media.MediaPlayer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.MediaPlayerWithSoundDetails
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.File
import java.util.Collections

@RunWith(AndroidJUnit4::class)
class SoundManagerTest {
    @get:Rule
    val exception = ExpectedException.none()
    private val soundManager = SoundManager.getInstance()
    private var project: Project? = null
    private val soundFiles = arrayOfNulls<File>(NUMBER_OF_SOUNDFILES)
    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestUtils.deleteProjects()
        createProject()
        soundManager.clear()
        soundFiles[0] = TestUtils.createSoundFile(project, R.raw.testsound, "testsound.m4a")
        soundFiles[1] = TestUtils.createSoundFile(project, R.raw.testsoundui, "testsoundui.mp3")
        soundFiles[2] = TestUtils.createSoundFile(project, R.raw.longsound, "longsound.mp3")
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        soundManager.clear()
        TestUtils.deleteProjects()
    }

    @Test
    fun testPlaySound() {
        soundManager.playSoundFile(
            soundFiles[0]!!.absolutePath,
            project!!.defaultScene.backgroundSprite
        )
        val mediaPlayer: MediaPlayer = soundManager.mediaPlayers[0]
        Assert.assertTrue(mediaPlayer.isPlaying)
        Assert.assertEquals(144, mediaPlayer.duration)
    }

    @Test
    fun testClear() {
        soundManager.playSoundFile(
            soundFiles[1]!!.absolutePath,
            project!!.defaultScene.backgroundSprite
        )
        val mediaPlayer: MediaPlayer = soundManager.mediaPlayers[0]
        Assert.assertTrue(mediaPlayer.isPlaying)
        soundManager.clear()
        Assert.assertTrue(soundManager.mediaPlayers.isEmpty())
        exception.expect(IllegalStateException::class.java)
        mediaPlayer.isPlaying
    }

    @Test
    fun testPauseAndResume() {
        soundManager.playSoundFile(
            soundFiles[0]!!.absolutePath,
            project!!.defaultScene.backgroundSprite
        )
        val mediaPlayer: MediaPlayer = soundManager.mediaPlayers[0]
        Assert.assertTrue(mediaPlayer.isPlaying)
        soundManager.pause()
        Assert.assertFalse(mediaPlayer.isPlaying)
        soundManager.resume()
        Assert.assertTrue(mediaPlayer.isPlaying)
    }

    @Test
    fun testPauseAndResumeMultipleSounds() {
        val mediaPlayers = soundManager.mediaPlayers
        for (index in 0 until NUMBER_OF_SOUNDFILES) {
            soundManager.playSoundFile(
                soundFiles[index]!!.absolutePath,
                Mockito.mock(Sprite::class.java)
            )
        }
        for (index in 0 until NUMBER_OF_SOUNDFILES) {
            Assert.assertTrue(mediaPlayers[index].isPlaying)
        }
        soundManager.pause()
        for (index in 0 until NUMBER_OF_SOUNDFILES) {
            Assert.assertFalse(mediaPlayers[index].isPlaying)
        }
        soundManager.resume()
        for (index in 0 until NUMBER_OF_SOUNDFILES) {
            Assert.assertTrue(mediaPlayers[index].isPlaying)
        }
    }

    @Test
    fun testStopOfSoundWhenSameSoundIsStarted() {
        val mediaPlayers = soundManager.mediaPlayers
        for (index in 0 until NUMBER_OF_SOUNDFILES) {
            soundManager.playSoundFile(
                soundFiles[0]!!.absolutePath,
                project!!.defaultScene.backgroundSprite
            )
        }
        Assert.assertEquals(1, mediaPlayers.size)
        Assert.assertTrue(mediaPlayers[0].isPlaying)
    }

    @Test
    fun testPlaySameSoundDifferentSprite() {
        val mediaPlayers = soundManager.mediaPlayers
        soundManager.playSoundFile(
            soundFiles[2]!!.absolutePath,
            project!!.defaultScene.backgroundSprite
        )
        soundManager.playSoundFile(
            soundFiles[2]!!.absolutePath,
            project!!.defaultScene.spriteList[1]
        )
        Assert.assertTrue(mediaPlayers[0].isPlaying)
        Assert.assertTrue(mediaPlayers[1].isPlaying)
    }

    @Test
    fun testPlaySameSoundFirstStopped() {
        soundManager.playSoundFile(
            soundFiles[0]!!.absolutePath,
            project!!.defaultScene.backgroundSprite
        )
        soundManager.mediaPlayers[0].stop()
        Assert.assertFalse(soundManager.mediaPlayers[0].isPlaying)
        soundManager.playSoundFile(
            soundFiles[0]!!.absolutePath,
            project!!.defaultScene.backgroundSprite
        )
        Assert.assertTrue(soundManager.mediaPlayers[0].isPlaying)
    }

    @Test
    fun testMediaPlayerLimit() {
        Assert.assertEquals(7, SoundManager.MAX_MEDIA_PLAYERS)
        for (index in 0 until SoundManager.MAX_MEDIA_PLAYERS + 3) {
            soundManager.playSoundFile(
                soundFiles[0]!!.absolutePath, Mockito.mock(
                    Sprite::class.java
                )
            )
        }
        Assert.assertEquals(SoundManager.MAX_MEDIA_PLAYERS, soundManager.mediaPlayers.size)
    }

    @Test
    fun testIfAllMediaPlayersInTheListAreUnique() {
        for (index in 0 until SoundManager.MAX_MEDIA_PLAYERS) {
            SoundManager.getInstance().playSoundFile(
                soundFiles[0]!!.absolutePath,
                Mockito.mock(Sprite::class.java)
            )
        }
        val mediaPlayers = soundManager.mediaPlayers
        for (mediaPlayer in mediaPlayers) {
            Assert.assertEquals(1, Collections.frequency(mediaPlayers, mediaPlayer))
        }
    }

    @Test
    fun testInitialVolume() {
        val soundManager = SoundManager()
        Assert.assertEquals(70.0f, soundManager.volume)
    }

    @Test
    fun testSetVolume() {
        val mediaPlayerMock = Mockito.mock(
            MediaPlayerWithSoundDetails::class.java
        )
        soundManager.mediaPlayers.add(mediaPlayerMock)
        val newVolume = 80.9f
        soundManager.volume = newVolume
        Assert.assertEquals(newVolume, soundManager.volume)
        Mockito.verify(mediaPlayerMock).setVolume(newVolume / 100f, newVolume / 100f)
    }

    private fun createProject() {
        project = Project(ApplicationProvider.getApplicationContext(), "testProject")
        val sprite = Sprite("TestSprite")
        project!!.defaultScene.addSprite(sprite)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
    }

    companion object {
        private const val NUMBER_OF_SOUNDFILES = 3
    }
}
