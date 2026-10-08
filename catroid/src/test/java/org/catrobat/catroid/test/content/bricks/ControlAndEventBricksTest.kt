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
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.EndBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.PenDownBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.ResetTimerBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

class ControlAndEventBricksTest {

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var actionFactory: ActionFactory
    private lateinit var sequence: ScriptSequenceAction
    private lateinit var scope: Scope
    private lateinit var script: StartScript

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

        script = StartScript()
        sequence = Mockito.mock(ScriptSequenceAction::class.java)
        Mockito.`when`(sequence.script).thenReturn(script)

        scope = Scope(project, sprite, sequence)
    }

    // --- BroadcastBrick ---

    @Test
    fun testBroadcastBrickConstructorsAndProperties() {
        val brickDefault = BroadcastBrick()
        assertEquals(R.layout.brick_broadcast, brickDefault.viewResource)
        assertEquals("", brickDefault.broadcastMessage)

        val brickMessage = BroadcastBrick("TestMessage")
        assertEquals("TestMessage", brickMessage.broadcastMessage)

        brickDefault.broadcastMessage = "NewMessage"
        assertEquals("NewMessage", brickDefault.broadcastMessage)
    }

    @Test
    fun testBroadcastBrickCreatesAction() {
        val brick = BroadcastBrick("Alert")
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createBroadcastAction(eq("Alert"), eq(false))
    }

    // --- BroadcastWaitBrick ---

    @Test
    fun testBroadcastWaitBrickConstructorsAndProperties() {
        val brickDefault = BroadcastWaitBrick()
        assertEquals(R.layout.brick_broadcast_wait, brickDefault.viewResource)
        assertEquals("", brickDefault.broadcastMessage)

        val brickMessage = BroadcastWaitBrick("WaitAlert")
        assertEquals("WaitAlert", brickMessage.broadcastMessage)

        brickDefault.broadcastMessage = "UpdatedWaitAlert"
        assertEquals("UpdatedWaitAlert", brickDefault.broadcastMessage)
    }

    @Test
    fun testBroadcastWaitBrickCreatesAction() {
        val brick = BroadcastWaitBrick("SyncMessage")
        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createBroadcastAction(eq("SyncMessage"), eq(true))
    }

    // --- WaitBrick ---

    @Test
    fun testWaitBrickConstructorsAndAction() {
        val brickDefault = WaitBrick()
        assertEquals(R.layout.brick_wait, brickDefault.viewResource)

        val brickMs = WaitBrick(2500)
        val formulaMs = brickMs.getFormulaWithBrickField(Brick.BrickField.TIME_TO_WAIT_IN_SECONDS)
        assertEquals(2.5, formulaMs.interpretDouble(scope), 0.001)

        brickMs.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createWaitAction(eq(sprite), eq(sequence), eq(formulaMs))

        val customFormula = Formula(3.5)
        val brickFormula = WaitBrick(customFormula)
        assertEquals(customFormula, brickFormula.getFormulaWithBrickField(Brick.BrickField.TIME_TO_WAIT_IN_SECONDS))
    }

    // --- WaitUntilBrick ---

    @Test
    fun testWaitUntilBrickConstructorsAndAction() {
        val brickDefault = WaitUntilBrick()
        assertEquals(R.layout.brick_wait_until, brickDefault.viewResource)

        val condition = Formula(1)
        val brickFormula = WaitUntilBrick(condition)
        assertEquals(condition, brickFormula.getFormulaWithBrickField(Brick.BrickField.IF_CONDITION))

        brickFormula.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createWaitUntilAction(eq(sprite), eq(sequence), eq(condition))
    }

    // --- WaitTillIdleBrick ---

    @Test
    fun testWaitTillIdleBrickConstructorsAndAction() {
        val brick = WaitTillIdleBrick()
        assertEquals(R.layout.brick_wait_till_idle, brick.viewResource)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createWaitTillIdleAction()
    }

    // --- ResetTimerBrick ---

    @Test
    fun testResetTimerBrickConstructorsAndAction() {
        val brick = ResetTimerBrick()
        assertEquals(R.layout.brick_reset_timer, brick.viewResource)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createResetTimerAction()
    }

    // --- StopScriptBrick ---

    @Test
    fun testStopScriptBrickConstructorsAndAction() {
        val brickDefault = StopScriptBrick()
        assertEquals(R.layout.brick_stop_script, brickDefault.viewResource)

        brickDefault.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStopScriptAction(eq(0), eq(script), eq(sprite))

        val brickStopAll = StopScriptBrick(BrickValues.STOP_ALL_SCRIPTS)
        brickStopAll.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStopScriptAction(
            eq(BrickValues.STOP_ALL_SCRIPTS),
            eq(script),
            eq(sprite)
        )

        val brickStopOther = StopScriptBrick(BrickValues.STOP_OTHER_SCRIPTS)
        brickStopOther.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createStopScriptAction(
            eq(BrickValues.STOP_OTHER_SCRIPTS),
            eq(script),
            eq(sprite)
        )
    }

    // --- ForeverBrick ---

    @Test
    fun testForeverBrickPropertiesAndCompositeBehavior() {
        val brick = ForeverBrick()
        assertEquals(R.layout.brick_forever, brick.viewResource)
        assertFalse(brick.hasSecondaryList())
        assertNull(brick.secondaryNestedBricks)
        assertTrue(brick.consistsOfMultipleParts())

        val parts = brick.allParts
        assertEquals(2, parts.size)
        assertEquals(brick, parts[0])
        assertTrue(parts[1] is EndBrick)

        val childBrick1 = PenDownBrick()
        val childBrick2 = ResetTimerBrick()
        brick.addBrick(childBrick1)
        brick.addBrick(childBrick2)

        assertEquals(2, brick.nestedBricks.size)
        assertEquals(2, brick.dragAndDropTargetList.size)
        assertEquals(childBrick1, brick.nestedBricks[0])
        assertEquals(childBrick2, brick.nestedBricks[1])

        val flatList = ArrayList<Brick>()
        brick.addToFlatList(flatList)
        assertEquals(4, flatList.size)
        assertEquals(brick, flatList[0])
        assertEquals(childBrick1, flatList[1])
        assertEquals(childBrick2, flatList[2])
        assertTrue(flatList[3] is EndBrick)

        brick.isCommentedOut = true
        assertTrue(brick.isCommentedOut)
        assertTrue(childBrick1.isCommentedOut)
        assertTrue(childBrick2.isCommentedOut)

        assertTrue(brick.removeChild(childBrick1))
        assertEquals(1, brick.nestedBricks.size)
        assertFalse(brick.removeChild(childBrick1))
    }

    @Test
    fun testForeverBrickCloningAndAction() {
        val brick = ForeverBrick()
        val childBrick = ResetTimerBrick()
        brick.addBrick(childBrick)

        val clone = brick.clone() as ForeverBrick
        assertEquals(1, clone.nestedBricks.size)
        assertTrue(clone.nestedBricks[0] !== childBrick)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createForeverAction(
            eq(sprite),
            eq(sequence),
            any(ScriptSequenceAction::class.java),
            anyBoolean()
        )
    }

    // --- RepeatBrick ---

    @Test
    fun testRepeatBrickConstructorsAndCompositeBehavior() {
        val brickDefault = RepeatBrick()
        assertEquals(R.layout.brick_repeat, brickDefault.viewResource)
        assertFalse(brickDefault.hasSecondaryList())
        assertNull(brickDefault.secondaryNestedBricks)
        assertTrue(brickDefault.consistsOfMultipleParts())

        val formula = Formula(5)
        val brickFormula = RepeatBrick(formula)
        assertEquals(formula, brickFormula.getFormulaWithBrickField(Brick.BrickField.TIMES_TO_REPEAT))

        val childBrick = ResetTimerBrick()
        brickFormula.addBrick(childBrick)
        assertEquals(1, brickFormula.nestedBricks.size)
        assertEquals(childBrick, brickFormula.nestedBricks[0])

        brickFormula.isCommentedOut = true
        assertTrue(brickFormula.isCommentedOut)
        assertTrue(childBrick.isCommentedOut)

        assertTrue(brickFormula.removeChild(childBrick))
        assertEquals(0, brickFormula.nestedBricks.size)
    }

    @Test
    fun testRepeatBrickCloningAndAction() {
        val formula = Formula(10)
        val brick = RepeatBrick(formula)
        val childBrick = ResetTimerBrick()
        brick.addBrick(childBrick)

        val clone = brick.clone() as RepeatBrick
        assertEquals(1, clone.nestedBricks.size)
        assertTrue(clone.nestedBricks[0] !== childBrick)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createRepeatAction(
            eq(sprite),
            eq(sequence),
            eq(formula),
            any(ScriptSequenceAction::class.java),
            anyBoolean()
        )
    }

    // --- RepeatUntilBrick ---

    @Test
    fun testRepeatUntilBrickConstructorsAndCompositeBehavior() {
        val brickDefault = RepeatUntilBrick()
        assertEquals(R.layout.brick_repeat_until, brickDefault.viewResource)
        assertFalse(brickDefault.hasSecondaryList())
        assertNull(brickDefault.secondaryNestedBricks)
        assertTrue(brickDefault.consistsOfMultipleParts())

        val formula = Formula(0)
        val brickFormula = RepeatUntilBrick(formula)
        assertEquals(formula, brickFormula.getFormulaWithBrickField(Brick.BrickField.REPEAT_UNTIL_CONDITION))

        val childBrick = ResetTimerBrick()
        brickFormula.addBrick(childBrick)
        assertEquals(1, brickFormula.nestedBricks.size)
        assertEquals(childBrick, brickFormula.nestedBricks[0])

        brickFormula.isCommentedOut = true
        assertTrue(brickFormula.isCommentedOut)
        assertTrue(childBrick.isCommentedOut)

        assertTrue(brickFormula.removeChild(childBrick))
        assertEquals(0, brickFormula.nestedBricks.size)
    }

    @Test
    fun testRepeatUntilBrickCloningAndAction() {
        val formula = Formula(1)
        val brick = RepeatUntilBrick(formula)
        val childBrick = ResetTimerBrick()
        brick.addBrick(childBrick)

        val clone = brick.clone() as RepeatUntilBrick
        assertEquals(1, clone.nestedBricks.size)
        assertTrue(clone.nestedBricks[0] !== childBrick)

        brick.addActionToSequence(sprite, sequence)
        Mockito.verify(actionFactory).createRepeatUntilAction(
            eq(sprite),
            eq(sequence),
            eq(formula),
            any(ScriptSequenceAction::class.java),
            anyBoolean()
        )
    }
}
