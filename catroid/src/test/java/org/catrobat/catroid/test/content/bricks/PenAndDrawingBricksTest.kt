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
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ClearBackgroundBrick
import org.catrobat.catroid.content.bricks.PenDownBrick
import org.catrobat.catroid.content.bricks.PenUpBrick
import org.catrobat.catroid.content.bricks.RunningStitchBrick
import org.catrobat.catroid.content.bricks.SetPenColorBrick
import org.catrobat.catroid.content.bricks.SetPenSizeBrick
import org.catrobat.catroid.content.bricks.StampBrick
import org.catrobat.catroid.content.bricks.StartPlotBrick
import org.catrobat.catroid.content.bricks.StopPlotBrick
import org.catrobat.catroid.content.bricks.TripleStitchBrick
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

@RunWith(JUnit4::class)
class PenAndDrawingBricksTest {

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

    // --- PenDownBrick ---

    @Test
    fun testPenDownBrickDefaultConstructorAndGetViewResource() {
        val brick = PenDownBrick()
        assertEquals(R.layout.brick_pen_down, brick.viewResource)
    }

    @Test
    fun testPenDownBrickCreatesActionWithCorrectSprite() {
        val brick = PenDownBrick()
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPenDownAction(eq(sprite))
    }

    // --- PenUpBrick ---

    @Test
    fun testPenUpBrickDefaultConstructorAndGetViewResource() {
        val brick = PenUpBrick()
        assertEquals(R.layout.brick_pen_up, brick.viewResource)
    }

    @Test
    fun testPenUpBrickCreatesActionWithCorrectSprite() {
        val brick = PenUpBrick()
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPenUpAction(eq(sprite))
    }

    // --- SetPenSizeBrick ---

    @Test
    fun testSetPenSizeBrickConstructorsAndAction() {
        val brickDefault = SetPenSizeBrick()
        assertEquals(R.layout.brick_set_pen_size, brickDefault.viewResource)

        val brickDouble = SetPenSizeBrick(5.5)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.PEN_SIZE)
        assertEquals(5.5, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetPenSizeAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(12.0)
        val brickFormula = SetPenSizeBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.PEN_SIZE))
    }

    // --- SetPenColorBrick ---

    @Test
    fun testSetPenColorBrickConstructorsAndAction() {
        val brickDefault = SetPenColorBrick()
        assertEquals(R.layout.brick_set_pen_color, brickDefault.viewResource)
        assertEquals(Brick.BrickField.PEN_COLOR_RED, brickDefault.defaultBrickField)

        val brickInts = SetPenColorBrick(255, 128, 64)
        val redFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.PEN_COLOR_RED)
        val greenFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.PEN_COLOR_GREEN)
        val blueFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.PEN_COLOR_BLUE)
        assertEquals(255, redFormula.interpretInteger(scope))
        assertEquals(128, greenFormula.interpretInteger(scope))
        assertEquals(64, blueFormula.interpretInteger(scope))

        brickInts.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetPenColorAction(
            eq(sprite),
            eq(sequence),
            eq(redFormula),
            eq(greenFormula),
            eq(blueFormula)
        )

        val customRed = Formula(100)
        val customGreen = Formula(150)
        val customBlue = Formula(200)
        val brickFormulas = SetPenColorBrick(customRed, customGreen, customBlue)
        assertEquals(customRed, brickFormulas.getFormulaWithBrickField(Brick.BrickField.PEN_COLOR_RED))
        assertEquals(customGreen, brickFormulas.getFormulaWithBrickField(Brick.BrickField.PEN_COLOR_GREEN))
        assertEquals(customBlue, brickFormulas.getFormulaWithBrickField(Brick.BrickField.PEN_COLOR_BLUE))
    }

    // --- StampBrick ---

    @Test
    fun testStampBrickDefaultConstructorAndGetViewResource() {
        val brick = StampBrick()
        assertEquals(R.layout.brick_stamp, brick.viewResource)
    }

    @Test
    fun testStampBrickCreatesActionWithCorrectSprite() {
        val brick = StampBrick()
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStampAction(eq(sprite))
    }

    // --- ClearBackgroundBrick ---

    @Test
    fun testClearBackgroundBrickDefaultConstructorAndGetViewResource() {
        val brick = ClearBackgroundBrick()
        assertEquals(R.layout.brick_clear_background, brick.viewResource)
    }

    @Test
    fun testClearBackgroundBrickCreatesAction() {
        val brick = ClearBackgroundBrick()
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createClearBackgroundAction()
    }

    // --- StartPlotBrick ---

    @Test
    fun testStartPlotBrickDefaultConstructorAndGetViewResource() {
        val brick = StartPlotBrick()
        assertEquals(R.layout.brick_start_plot, brick.viewResource)
    }

    @Test
    fun testStartPlotBrickCreatesActionWithCorrectSprite() {
        val brick = StartPlotBrick()
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStartPlotAction(eq(sprite))
    }

    // --- StopPlotBrick ---

    @Test
    fun testStopPlotBrickDefaultConstructorAndGetViewResource() {
        val brick = StopPlotBrick()
        assertEquals(R.layout.brick_stop_plot, brick.viewResource)
    }

    @Test
    fun testStopPlotBrickCreatesActionWithCorrectSprite() {
        val brick = StopPlotBrick()
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStopPlotAction(eq(sprite))
    }

    // --- TripleStitchBrick ---

    @Test
    fun testTripleStitchBrickConstructorsAndAction() {
        val brickDefault = TripleStitchBrick()
        assertEquals(R.layout.brick_triple_stitch, brickDefault.viewResource)
        assertEquals(Brick.BrickField.EMBROIDERY_LENGTH, brickDefault.defaultBrickField)

        val customLength = Formula(8.0)
        val brickFormula = TripleStitchBrick(customLength)
        assertEquals(customLength, brickFormula.getFormulaWithBrickField(Brick.BrickField.EMBROIDERY_LENGTH))

        brickFormula.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createTripleStitchAction(eq(sprite), eq(sequence), eq(customLength))
    }

    // --- ZigZagStitchBrick ---

    @Test
    fun testZigZagStitchBrickConstructorsAndAction() {
        val brickDefault = ZigZagStitchBrick()
        assertEquals(R.layout.brick_zigzag_stitch, brickDefault.viewResource)
        assertEquals(Brick.BrickField.ZIGZAG_EMBROIDERY_LENGTH, brickDefault.defaultBrickField)

        val lengthFormula = Formula(10.0)
        val widthFormula = Formula(4.0)
        val brickFormulas = ZigZagStitchBrick(lengthFormula, widthFormula)
        assertEquals(lengthFormula, brickFormulas.getFormulaWithBrickField(Brick.BrickField.ZIGZAG_EMBROIDERY_LENGTH))
        assertEquals(widthFormula, brickFormulas.getFormulaWithBrickField(Brick.BrickField.ZIGZAG_EMBROIDERY_WIDTH))

        brickFormulas.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createZigZagStitchAction(
            eq(sprite),
            eq(sequence),
            eq(lengthFormula),
            eq(widthFormula)
        )
    }

    // --- RunningStitchBrick ---

    @Test
    fun testRunningStitchBrickConstructorsAndAction() {
        val brickDefault = RunningStitchBrick()
        assertEquals(R.layout.brick_running_stitch_with_length, brickDefault.viewResource)
        assertEquals(Brick.BrickField.EMBROIDERY_LENGTH, brickDefault.defaultBrickField)

        val lengthFormula = Formula(6.0)
        val brickFormula = RunningStitchBrick(lengthFormula)
        assertEquals(lengthFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.EMBROIDERY_LENGTH))

        brickFormula.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createRunningStitchAction(eq(sprite), eq(sequence), eq(lengthFormula))
    }
}
