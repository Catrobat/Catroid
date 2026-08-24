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
package org.catrobat.catroid.test.content.actions

import androidx.test.core.app.ApplicationProvider
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.SoundFilePathWithSprite
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.WaitForSoundAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.SoundManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class WaitForSoundActionTest {

    private lateinit var project: Project
    private lateinit var projectManager: ProjectManager
    private lateinit var action: WaitForSoundAction
    private lateinit var pathSet: MutableSet<SoundFilePathWithSprite>

    companion object {
        private const val SOUND_DURATION = 2.0f
        private const val PATH_TO_SOUND_FILE = "soundFilePath"
    }

    @Before
    fun setUp() {
        projectManager = ProjectManager.getInstance()
        createProject(this.javaClass.simpleName)
    }

    @Test
    fun testWaitDurationSameAsSoundDuration() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        action.act(0.1f)
        assertEquals(SOUND_DURATION, action.duration)
    }

    @Test
    fun testStopWaitWhenSameSoundStartsPlaying() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        action.act(0.1f)
        assertEquals(action.time, action.duration)
    }

    @Test
    fun testWaitWhenDifferentSoundsStartsPlaying() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE + "test")
        action.act(0.1f)
        assertNotEquals(action.time, action.duration, 0.0f)
    }

    @Test
    fun testWaitWhenOtherSpriteStoppedSameSound() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        pathSet.clear()
        pathSet.add(SoundFilePathWithSprite(PATH_TO_SOUND_FILE, mock(Sprite::class.java)))
        action.act(0.1f)
        assertNotEquals(action.time, action.duration, 0.0f)
    }

    private fun createProject(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        projectManager.currentProject = project
        projectManager.currentSprite = project.defaultScene.backgroundSprite
    }

    private fun createActionWithStoppedSoundFilePath(soundPath: String) {
        val soundManager = mock(SoundManager::class.java)
        pathSet = mutableSetOf(
            SoundFilePathWithSprite(soundPath, project.defaultScene.backgroundSprite)
        )
        `when`(soundManager.recentlyStoppedSoundfilePaths).thenReturn(pathSet)
        `when`(soundManager.getDurationOfSoundFile(anyString())).thenReturn(SOUND_DURATION * 1000)
        action = ActionFactory().createWaitForSoundAction(
            project.defaultScene.backgroundSprite, SequenceAction(),
            Formula(SOUND_DURATION),
            PATH_TO_SOUND_FILE
        ) as WaitForSoundAction
        action.setSoundManager(soundManager)
    }
}

