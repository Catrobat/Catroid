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

package org.catrobat.catroid.test.content.bricks

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeTempoByNBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick
import org.catrobat.catroid.content.bricks.PlaySoundAtBrick
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.content.bricks.SetTempoBrick
import org.catrobat.catroid.content.bricks.SetVolumeToBrick
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick
import org.catrobat.catroid.content.bricks.SpeakBrick
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick
import org.catrobat.catroid.content.bricks.StopSoundBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import java.io.File

@RunWith(JUnit4::class)
class SoundBricksTest {

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var actionFactory: ActionFactory
    private lateinit var sequence: ScriptSequenceAction
    private lateinit var scope: Scope

    @Before
    fun setUp() {
        project = Project(MockUtil.mockContextForProject(), "Project")
        val scene = Scene("Currently playing scene", project)
        sprite = Sprite("Sprite")

        scene.addSprite(sprite)
        project.addScene(scene)

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = Scene()
        ProjectManager.getInstance().currentlyPlayingScene = scene

        actionFactory = Mockito.mock(ActionFactory::class.java)
        sprite.actionFactory = actionFactory
        sequence = Mockito.mock(ScriptSequenceAction::class.java)
        scope = Scope(project, sprite, sequence)
    }

    @Test
    fun testPlaySoundBrickCreatesActionWithCorrectSpriteAndSound() {
        val sound = SoundInfo()
        val brick = PlaySoundBrick()
        brick.sound = sound
        assertEquals(sound, brick.sound)
        assertEquals(R.layout.brick_play_sound, brick.viewResource)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPlaySoundAction(eq(sprite), eq(sound))
    }

    @Test
    fun testPlaySoundBrickOnItemSelected() {
        val sound = SoundInfo()
        val brick = PlaySoundBrick()
        brick.onItemSelected(0, sound)
        assertEquals(sound, brick.sound)
    }

    @Test
    fun testPlaySoundBrickClone() {
        val sound = SoundInfo()
        val brick = PlaySoundBrick()
        brick.sound = sound
        val clone = brick.clone() as PlaySoundBrick
        assertNotNull(clone)
        assertEquals(sound, clone.sound)
    }

    @Test
    fun testPlaySoundAndWaitBrickWithoutSoundDoesNotAddAction() {
        val brick = PlaySoundAndWaitBrick()
        brick.addActionToSequence(sprite, sequence)
        Mockito.verifyNoInteractions(actionFactory)
    }

    @Test
    fun testPlaySoundAndWaitBrickSoundNotInSpriteDoesNotAddAction() {
        val sound = SoundInfo()
        val file = Mockito.mock(File::class.java)
        sound.file = file
        val brick = PlaySoundAndWaitBrick()
        brick.sound = sound

        brick.addActionToSequence(sprite, sequence)
        Mockito.verifyNoInteractions(actionFactory)
    }

    @Test
    fun testPlaySoundAtBrickDefaultConstructorAndGetViewResource() {
        val brick = PlaySoundAtBrick()
        assertEquals(R.layout.brick_play_sound_at, brick.viewResource)
    }

    @Test
    fun testPlaySoundAtBrickConstructorWithValue() {
        val brick = PlaySoundAtBrick(2.5)
        val formula = brick.getFormulaWithBrickField(Brick.BrickField.PLAY_SOUND_AT)
        assertEquals(2.5, formula.interpretDouble(scope), 0.001)
    }

    @Test
    fun testPlaySoundAtBrickWithoutSoundDoesNotAddAction() {
        val brick = PlaySoundAtBrick(1.0)
        brick.addActionToSequence(sprite, sequence)
        Mockito.verifyNoInteractions(actionFactory)
    }

    @Test
    fun testPlaySoundAtBrickWithSoundNotInSpriteDoesNotAddAction() {
        val sound = SoundInfo()
        val file = Mockito.mock(File::class.java)
        sound.file = file
        val brick = PlaySoundAtBrick(1.0)
        brick.sound = sound

        brick.addActionToSequence(sprite, sequence)
        Mockito.verifyNoInteractions(actionFactory)
    }

    @Test
    fun testPlaySoundAtBrickWithValidSoundAddsAction() {
        val sound = SoundInfo()
        val file = Mockito.mock(File::class.java)
        sound.file = file
        sprite.soundList.add(sound)

        val brick = PlaySoundAtBrick(1.5)
        brick.sound = sound
        assertEquals(sound, brick.sound)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPlaySoundAtAction(
            eq(sprite),
            eq(sequence),
            any(Formula::class.java),
            eq(sound)
        )
    }

    @Test
    fun testPlaySoundAtBrickOnItemSelected() {
        val sound = SoundInfo()
        val brick = PlaySoundAtBrick()
        brick.onItemSelected(0, sound)
        assertEquals(sound, brick.sound)
    }

    @Test
    fun testPlaySoundAtBrickClone() {
        val sound = SoundInfo()
        val brick = PlaySoundAtBrick(3.0)
        brick.sound = sound
        val clone = brick.clone() as PlaySoundAtBrick
        assertNotNull(clone)
        assertEquals(sound, clone.sound)
    }

    @Test
    fun testStopAllSoundsBrickCreatesAction() {
        val brick = StopAllSoundsBrick()
        assertEquals(R.layout.brick_stop_all_sounds, brick.viewResource)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStopAllSoundsAction()
    }

    @Test
    fun testStopSoundBrickCreatesActionWithCorrectSpriteAndSound() {
        val sound = SoundInfo()
        val brick = StopSoundBrick()
        brick.sound = sound
        assertEquals(sound, brick.sound)
        assertEquals(R.layout.brick_stop_sound, brick.viewResource)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStopSoundAction(eq(sprite), eq(sound))
    }

    @Test
    fun testStopSoundBrickOnItemSelected() {
        val sound = SoundInfo()
        val brick = StopSoundBrick()
        brick.onItemSelected(0, sound)
        assertEquals(sound, brick.sound)
    }

    @Test
    fun testSetVolumeToBrickConstructorsAndAction() {
        val brickDefault = SetVolumeToBrick()
        assertEquals(R.layout.brick_set_volume_to, brickDefault.viewResource)

        val brickDouble = SetVolumeToBrick(50.0)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.VOLUME)
        assertEquals(50.0, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetVolumeToAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(75.0)
        val brickFormula = SetVolumeToBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.VOLUME))
    }

    @Test
    fun testChangeVolumeByNBrickConstructorsAndAction() {
        val brickDefault = ChangeVolumeByNBrick()
        assertEquals(R.layout.brick_change_volume_by, brickDefault.viewResource)

        val brickDouble = ChangeVolumeByNBrick(10.0)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.VOLUME_CHANGE)
        assertEquals(10.0, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createChangeVolumeByNAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(-15.0)
        val brickFormula = ChangeVolumeByNBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.VOLUME_CHANGE))
    }

    @Test
    fun testSpeakBrickConstructorsAndAction() {
        val brickDefault = SpeakBrick()
        assertEquals(R.layout.brick_speak, brickDefault.viewResource)

        val requiredResources = Brick.ResourcesSet()
        brickDefault.addRequiredResources(requiredResources)
        assertTrue(requiredResources.contains(Brick.TEXT_TO_SPEECH))

        val brickString = SpeakBrick("Hello")
        val formula = brickString.getFormulaWithBrickField(Brick.BrickField.SPEAK)
        assertEquals("Hello", formula.interpretString(scope))

        brickString.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSpeakAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula("World")
        val brickFormula = SpeakBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.SPEAK))
    }

    @Test
    fun testSpeakAndWaitBrickConstructorsAndAction() {
        val brickDefault = SpeakAndWaitBrick()
        assertEquals(R.layout.brick_speak_and_wait, brickDefault.viewResource)

        val requiredResources = Brick.ResourcesSet()
        brickDefault.addRequiredResources(requiredResources)
        assertTrue(requiredResources.contains(Brick.TEXT_TO_SPEECH))

        val brickString = SpeakAndWaitBrick("Wait Hello")
        val formula = brickString.getFormulaWithBrickField(Brick.BrickField.SPEAK)
        assertEquals("Wait Hello", formula.interpretString(scope))

        brickString.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSpeakAndWaitAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula("Wait World")
        val brickFormula = SpeakAndWaitBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.SPEAK))
    }

    @Test
    fun testSetTempoBrickConstructorsAndAction() {
        val brickDefault = SetTempoBrick()
        assertEquals(R.layout.brick_set_tempo, brickDefault.viewResource)

        val brickInt = SetTempoBrick(120)
        val formula = brickInt.getFormulaWithBrickField(Brick.BrickField.TEMPO)
        assertEquals(120, formula.interpretInteger(scope))

        brickInt.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetTempoAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(60)
        val brickFormula = SetTempoBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.TEMPO))
    }

    @Test
    fun testChangeTempoByNBrickConstructorsAndAction() {
        val brickDefault = ChangeTempoByNBrick()
        assertEquals(R.layout.brick_change_tempo, brickDefault.viewResource)

        val brickInt = ChangeTempoByNBrick(20)
        val formula = brickInt.getFormulaWithBrickField(Brick.BrickField.TEMPO_CHANGE)
        assertEquals(20, formula.interpretInteger(scope))

        brickInt.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createChangeTempoAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(-10)
        val brickFormula = ChangeTempoByNBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.TEMPO_CHANGE))
    }
}
