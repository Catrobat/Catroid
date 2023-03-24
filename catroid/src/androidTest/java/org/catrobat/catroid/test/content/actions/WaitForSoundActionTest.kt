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

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.catrobat.catroid.content.actions.WaitForSoundAction
import org.catrobat.catroid.content.SoundFilePathWithSprite
import org.junit.Before
import org.catrobat.catroid.test.content.actions.WaitForSoundActionTest
import junit.framework.TestCase
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.io.SoundManager
import org.catrobat.catroid.content.ActionFactory
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.HashSet

@RunWith(JUnit4::class)
class WaitForSoundActionTest {
    private var action: WaitForSoundAction? = null
    private var pathSet: MutableSet<SoundFilePathWithSprite>? = null
    @Before
    fun setUp() {
        createProject(this.javaClass.simpleName)
    }

    @Test
    fun testWaitDurationSameAsSoundDuration() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        action!!.act(0.1f)
        TestCase.assertEquals(SOUND_DURATION, action!!.duration)
    }

    @Test
    fun testStopWaitWhenSameSoundStartsPlaying() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        action!!.act(0.1f)
        TestCase.assertEquals(action!!.time, action!!.duration)
    }

    @Test
    fun testWaitWhenDifferentSoundsStartsPlaying() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE + "test")
        action!!.act(0.1f)
        Assert.assertNotEquals(action!!.time.toDouble(), action!!.duration.toDouble(), 0.0)
    }

    @Test
    fun testWaitWhenOtherSpriteStoppedSameSound() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        pathSet!!.clear()
        pathSet!!.add(
            SoundFilePathWithSprite(
                PATH_TO_SOUND_FILE, Mockito.mock(
                    Sprite::class.java
                )
            )
        )
        action!!.act(0.1f)
        Assert.assertNotEquals(action!!.time.toDouble(), action!!.duration.toDouble(), 0.0)
    }

    private fun createProject(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        projectManager?.currentProject = project
        projectManager?.currentSprite =
            project!!.defaultScene.backgroundSprite
    }

    private fun createActionWithStoppedSoundFilePath(soundPath: String) {
        val soundManager = Mockito.mock(SoundManager::class.java)
        pathSet = HashSet()
        pathSet?.add(SoundFilePathWithSprite(soundPath, project!!.defaultScene.backgroundSprite))
        Mockito.`when`(soundManager.recentlyStoppedSoundfilePaths).thenReturn(pathSet)
        Mockito.`when`(soundManager.getDurationOfSoundFile(ArgumentMatchers.anyString()))
            .thenReturn(
                SOUND_DURATION * 1000
            )
        action = ActionFactory().createWaitForSoundAction(
            project!!.defaultScene.backgroundSprite, SequenceAction(),
            Formula(SOUND_DURATION),
            PATH_TO_SOUND_FILE
        ) as WaitForSoundAction
        action!!.setSoundManager(soundManager)
    }

    companion object {
        private var projectManager: ProjectManager? = null
        private var project: Project? = null
        private const val SOUND_DURATION = 2.0f
        private const val PATH_TO_SOUND_FILE = "soundFilePath"
        @BeforeClass
        fun setUpProjectManager() {
            project = Project(ApplicationProvider.getApplicationContext(), "projectName")
            projectManager = ProjectManager.getInstance()
        }
    }
}