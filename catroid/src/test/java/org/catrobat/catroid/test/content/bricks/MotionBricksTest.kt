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
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.ComeToFrontBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.VibrationBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

@RunWith(JUnit4::class)
class MotionBricksTest {

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

    // --- MoveNStepsBrick ---

    @Test
    fun testMoveNStepsBrickConstructorsAndAction() {
        val brickDefault = MoveNStepsBrick()
        assertEquals(R.layout.brick_move_n_steps, brickDefault.viewResource)

        val brickDouble = MoveNStepsBrick(15.0)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.STEPS)
        assertEquals(15.0, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createMoveNStepsAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(25.0)
        val brickFormula = MoveNStepsBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.STEPS))
    }

    // --- TurnLeftBrick ---

    @Test
    fun testTurnLeftBrickConstructorsAndAction() {
        val brickDefault = TurnLeftBrick()
        assertEquals(R.layout.brick_turn_left, brickDefault.viewResource)

        val brickDouble = TurnLeftBrick(45.0)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.TURN_LEFT_DEGREES)
        assertEquals(45.0, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createTurnLeftAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(90.0)
        val brickFormula = TurnLeftBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.TURN_LEFT_DEGREES))
    }

    // --- TurnRightBrick ---

    @Test
    fun testTurnRightBrickConstructorsAndAction() {
        val brickDefault = TurnRightBrick()
        assertEquals(R.layout.brick_turn_right, brickDefault.viewResource)

        val brickDouble = TurnRightBrick(60.0)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.TURN_RIGHT_DEGREES)
        assertEquals(60.0, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createTurnRightAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(180.0)
        val brickFormula = TurnRightBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.TURN_RIGHT_DEGREES))
    }

    // --- PointInDirectionBrick ---

    @Test
    fun testPointInDirectionBrickConstructorsAndAction() {
        val brickDefault = PointInDirectionBrick()
        assertEquals(R.layout.brick_point_in_direction, brickDefault.viewResource)

        val brickDouble = PointInDirectionBrick(90.0)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.DEGREES)
        assertEquals(90.0, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPointInDirectionAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(270.0)
        val brickFormula = PointInDirectionBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.DEGREES))
    }

    // --- PointToBrick ---

    @Test
    fun testPointToBrickDefaultConstructorAndGetViewResource() {
        val brick = PointToBrick()
        assertEquals(R.layout.brick_point_to, brick.viewResource)
    }

    @Test
    fun testPointToBrickCreatesActionWithPointedSprite() {
        val targetSprite = Sprite("TargetSprite")
        val brick = PointToBrick(targetSprite)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPointToAction(eq(sprite), eq(targetSprite))
    }

    @Test
    fun testPointToBrickOnItemSelectedAndClone() {
        val targetSprite = Sprite("TargetSprite")
        val brick = PointToBrick()
        brick.onItemSelected(0, targetSprite)

        val clone = brick.clone() as PointToBrick
        assertNotNull(clone)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPointToAction(eq(sprite), eq(targetSprite))
    }

    // --- PlaceAtBrick ---

    @Test
    fun testPlaceAtBrickConstructorsAndAction() {
        val brickDefault = PlaceAtBrick()
        assertEquals(R.layout.brick_place_at, brickDefault.viewResource)
        assertEquals(Brick.BrickField.X_POSITION, brickDefault.defaultBrickField)
        assertEquals(Brick.BrickField.X_POSITION, brickDefault.xBrickField)
        assertEquals(Brick.BrickField.Y_POSITION, brickDefault.yBrickField)
        assertEquals(R.id.brick_place_at_edit_text_x, brickDefault.xEditTextId)
        assertEquals(R.id.brick_place_at_edit_text_y, brickDefault.yEditTextId)

        val brickInts = PlaceAtBrick(100, 200)
        val xFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.X_POSITION)
        val yFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.Y_POSITION)
        assertEquals(100, xFormula.interpretInteger(scope))
        assertEquals(200, yFormula.interpretInteger(scope))

        brickInts.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createPlaceAtAction(eq(sprite), eq(sequence), eq(xFormula), eq(yFormula))

        val customX = Formula(50)
        val customY = Formula(75)
        val brickFormulas = PlaceAtBrick(customX, customY)
        assertEquals(customX, brickFormulas.getFormulaWithBrickField(Brick.BrickField.X_POSITION))
        assertEquals(customY, brickFormulas.getFormulaWithBrickField(Brick.BrickField.Y_POSITION))
    }

    // --- SetXBrick ---

    @Test
    fun testSetXBrickConstructorsAndAction() {
        val brickDefault = SetXBrick()
        assertEquals(R.layout.brick_set_x, brickDefault.viewResource)

        val brickInt = SetXBrick(150)
        val formula = brickInt.getFormulaWithBrickField(Brick.BrickField.X_POSITION)
        assertEquals(150, formula.interpretInteger(scope))

        brickInt.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetXAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(300)
        val brickFormula = SetXBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.X_POSITION))
    }

    // --- SetYBrick ---

    @Test
    fun testSetYBrickConstructorsAndAction() {
        val brickDefault = SetYBrick()
        assertEquals(R.layout.brick_set_y, brickDefault.viewResource)

        val brickInt = SetYBrick(-250)
        val formula = brickInt.getFormulaWithBrickField(Brick.BrickField.Y_POSITION)
        assertEquals(-250, formula.interpretInteger(scope))

        brickInt.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetYAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(400)
        val brickFormula = SetYBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.Y_POSITION))
    }

    // --- ChangeXByNBrick ---

    @Test
    fun testChangeXByNBrickConstructorsAndAction() {
        val brickDefault = ChangeXByNBrick()
        assertEquals(R.layout.brick_change_x, brickDefault.viewResource)

        val brickInt = ChangeXByNBrick(10)
        val formula = brickInt.getFormulaWithBrickField(Brick.BrickField.X_POSITION_CHANGE)
        assertEquals(10, formula.interpretInteger(scope))

        brickInt.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createChangeXByNAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(-20)
        val brickFormula = ChangeXByNBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.X_POSITION_CHANGE))
    }

    // --- ChangeYByNBrick ---

    @Test
    fun testChangeYByNBrickConstructorsAndAction() {
        val brickDefault = ChangeYByNBrick()
        assertEquals(R.layout.brick_change_y, brickDefault.viewResource)

        val brickInt = ChangeYByNBrick(-15)
        val formula = brickInt.getFormulaWithBrickField(Brick.BrickField.Y_POSITION_CHANGE)
        assertEquals(-15, formula.interpretInteger(scope))

        brickInt.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createChangeYByNAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(35)
        val brickFormula = ChangeYByNBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.Y_POSITION_CHANGE))
    }

    // --- IfOnEdgeBounceBrick ---

    @Test
    fun testIfOnEdgeBounceBrickConstructorsAndAction() {
        val brick = IfOnEdgeBounceBrick()
        assertEquals(R.layout.brick_if_on_edge_bounce, brick.viewResource)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createIfOnEdgeBounceAction(eq(sprite))
    }

    // --- GlideToBrick ---

    @Test
    fun testGlideToBrickConstructorsAndAction() {
        val brickDefault = GlideToBrick()
        assertEquals(R.layout.brick_glide_to, brickDefault.viewResource)
        assertEquals(Brick.BrickField.DURATION_IN_SECONDS, brickDefault.defaultBrickField)
        assertEquals(Brick.BrickField.X_DESTINATION, brickDefault.xBrickField)
        assertEquals(Brick.BrickField.Y_DESTINATION, brickDefault.yBrickField)
        assertEquals(R.id.brick_glide_to_edit_text_x, brickDefault.xEditTextId)
        assertEquals(R.id.brick_glide_to_edit_text_y, brickDefault.yEditTextId)

        val brickInts = GlideToBrick(100, 200, 1500)
        val xFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.X_DESTINATION)
        val yFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.Y_DESTINATION)
        val durationFormula = brickInts.getFormulaWithBrickField(Brick.BrickField.DURATION_IN_SECONDS)
        assertEquals(100, xFormula.interpretInteger(scope))
        assertEquals(200, yFormula.interpretInteger(scope))
        assertEquals(1.5, durationFormula.interpretDouble(scope), 0.001)

        brickInts.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createGlideToAction(
            eq(sprite),
            eq(sequence),
            eq(xFormula),
            eq(yFormula),
            eq(durationFormula)
        )

        val customX = Formula(50)
        val customY = Formula(75)
        val customDuration = Formula(2.0)
        val brickFormulas = GlideToBrick(customX, customY, customDuration)
        assertEquals(customX, brickFormulas.getFormulaWithBrickField(Brick.BrickField.X_DESTINATION))
        assertEquals(customY, brickFormulas.getFormulaWithBrickField(Brick.BrickField.Y_DESTINATION))
        assertEquals(customDuration, brickFormulas.getFormulaWithBrickField(Brick.BrickField.DURATION_IN_SECONDS))
    }

    // --- GoToBrick ---

    @Test
    fun testGoToBrickConstructorsAndAction() {
        val brickDefault = GoToBrick()
        assertEquals(R.layout.brick_go_to, brickDefault.viewResource)

        val targetSprite = Sprite("TargetSprite")
        val brick = GoToBrick(targetSprite)
        brick.onItemSelected(0, targetSprite)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createGoToAction(
            eq(sprite),
            eq(targetSprite),
            eq(BrickValues.GO_TO_OTHER_SPRITE_POSITION)
        )
    }

    // --- GoNStepsBackBrick ---

    @Test
    fun testGoNStepsBackBrickConstructorsAndAction() {
        val brickDefault = GoNStepsBackBrick()
        assertEquals(R.layout.brick_go_back, brickDefault.viewResource)

        val brickInt = GoNStepsBackBrick(3)
        val formula = brickInt.getFormulaWithBrickField(Brick.BrickField.STEPS)
        assertEquals(3, formula.interpretInteger(scope))

        brickInt.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createGoNStepsBackAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(5)
        val brickFormula = GoNStepsBackBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.STEPS))
    }

    // --- ComeToFrontBrick ---

    @Test
    fun testComeToFrontBrickConstructorsAndAction() {
        val brick = ComeToFrontBrick()
        assertEquals(R.layout.brick_go_to_front, brick.viewResource)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createComeToFrontAction(eq(sprite))
    }

    // --- VibrationBrick ---

    @Test
    fun testVibrationBrickConstructorsAndAction() {
        val brickDefault = VibrationBrick()
        assertEquals(R.layout.brick_vibration, brickDefault.viewResource)

        val requiredResources = Brick.ResourcesSet()
        brickDefault.addRequiredResources(requiredResources)
        assertTrue(requiredResources.contains(Brick.VIBRATION))

        val brickDouble = VibrationBrick(0.5)
        val formula = brickDouble.getFormulaWithBrickField(Brick.BrickField.VIBRATE_DURATION_IN_SECONDS)
        assertEquals(0.5, formula.interpretDouble(scope), 0.001)

        brickDouble.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createVibrateAction(eq(sprite), eq(sequence), eq(formula))

        val customFormula = Formula(1.5)
        val brickFormula = VibrationBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.VIBRATE_DURATION_IN_SECONDS))
    }

    // --- SetRotationStyleBrick ---

    @Test
    fun testSetRotationStyleBrickConstructorsAndAction() {
        val brickDefault = SetRotationStyleBrick()
        assertEquals(R.layout.brick_set_rotation_style, brickDefault.viewResource)

        brickDefault.onItemSelected(0, null)
        brickDefault.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createSetRotationStyleAction(eq(sprite), eq(0))
    }
}
