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

package org.catrobat.catroid.test.stage

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenStageExitedScript
import org.catrobat.catroid.content.actions.AssertEqualsAction
import org.catrobat.catroid.content.actions.FinishStageAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ExitStageBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageExitController
import org.catrobat.catroid.stage.StageExitController.State
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.stage.TestResult
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import java.lang.ref.WeakReference

/**
 * Checks how [StageExitController] lets the "When stage is exited" catch scripts run before a real
 * exit of the stage, with real sprites and scripts. The stage listener is a mock that only hands
 * out the controller and the sprites, and every hand-off to the UI thread is queued, so the tests
 * decide when the UI thread runs.
 */
@RunWith(JUnit4::class)
class StageExitControllerTest {

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var controller: StageExitController
    private val stageActivity: StageActivity = mock(StageActivity::class.java)
    private val uiThreadQueue = mutableListOf<Runnable>()
    private var finishStageCount = 0
    private var exitCount = 0
    private var catchScriptStops = 0
    private val exitAction = Runnable { exitCount++ }
    private val startedSprites = mutableSetOf<Sprite>()

    @Before
    fun setUp() {
        project = Project(MockUtil.mockContextForProject(), "StageExitControllerTest")
        ProjectManager.getInstance().currentProject = project
        sprite = Sprite("sprite")
        project.defaultScene.addSprite(sprite)

        controller = StageExitController(
            project = { project },
            finishStage = { finishStageCount++ },
            runOnUiThread = { uiThreadQueue += it }
        )

        val stageListener = mock(StageListener::class.java)
        doReturn(controller).`when`(stageListener).stageExitController
        doReturn(project.defaultScene.spriteList).`when`(stageListener).spritesFromStage
        StageActivity.stageListener = stageListener
        StageActivity.activeStageActivity = WeakReference(stageActivity)
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
        StageActivity.activeStageActivity = null
    }

    @Test
    fun `new controller has no exit`() {
        assertEquals(State.NONE, controller.state)
    }

    @Test
    fun `without catch scripts an exit runs at once on the calling thread`() {
        controller.requestExit(exitAction)

        assertEquals(1, exitCount)
        assertTrue(uiThreadQueue.isEmpty())
        assertEquals(State.NONE, controller.state)
    }

    @Test
    fun `without catch scripts every exit runs at once as before`() {
        controller.requestExit(exitAction)
        controller.requestExit(exitAction)
        controller.requestFinishStage()

        assertEquals(2, exitCount)
        assertEquals(1, finishStageCount)
        assertEquals(State.NONE, controller.state)
    }

    @Test
    fun `commented out catch script is ignored`() {
        addCatchScript(ChangeXByNBrick(X_DELTA)).isCommentedOut = true

        controller.requestExit(exitAction)
        runFrames()

        assertEquals(1, exitCount)
        assertEquals(0, xPosition())
    }

    @Test
    fun `catch script whose script brick is commented out is ignored`() {
        addCatchScript(ChangeXByNBrick(X_DELTA)).scriptBrick.isCommentedOut = true

        controller.requestExit(exitAction)

        assertEquals(1, exitCount)
    }

    @Test
    fun `with a catch script an exit is pending until the next frame fires the catch`() {
        addCatchScript(ChangeXByNBrick(X_DELTA), WaitBrick(LONG_WAIT_MILLIS))

        controller.requestExit(exitAction)

        assertEquals(State.PENDING, controller.state)
        assertEquals(0, exitCount)
        assertTrue(uiThreadQueue.isEmpty())
        assertEquals(0, xPosition())

        runFrames(FEW_FRAMES)

        assertEquals(X_DELTA, xPosition())
        assertEquals(State.PENDING, controller.state)
        assertTrue(uiThreadQueue.isEmpty())
    }

    @Test
    fun `stage exited event fires once`() {
        addCatchScript(ChangeXByNBrick(X_DELTA))
        addCatchScript(ChangeXByNBrick(X_DELTA), WaitBrick(LONG_WAIT_MILLIS))

        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)
        controller.requestFinishStage()
        runFrames(FEW_FRAMES)

        assertEquals(2 * X_DELTA, xPosition())
    }

    @Test
    fun `pending exit runs on the UI thread once all catch scripts have finished`() {
        addCatchScript(ChangeXByNBrick(X_DELTA), WaitBrick(SHORT_WAIT_MILLIS))
        addCatchScript(WaitBrick(SHORT_WAIT_MILLIS), WaitBrick(SHORT_WAIT_MILLIS))

        controller.requestExit(exitAction)
        runFrames(1)
        assertTrue(uiThreadQueue.isEmpty())

        runFrames()

        assertEquals(1, uiThreadQueue.size)
        assertEquals(0, exitCount)
        assertEquals(State.EXITED, controller.state)

        runUiThread()

        assertEquals(1, exitCount)
        assertEquals(X_DELTA, xPosition())
    }

    @Test
    fun `catch scripts of all sprites run before the exit`() {
        val otherSprite = Sprite("other sprite")
        project.defaultScene.addSprite(otherSprite)
        addCatchScript(ChangeXByNBrick(X_DELTA))
        otherSprite.addScript(catchScript(ChangeXByNBrick(X_DELTA), WaitBrick(SHORT_WAIT_MILLIS)))

        val sprites = listOf(sprite, otherSprite)

        controller.requestExit(exitAction)
        runFrames(1, sprites)
        assertTrue(uiThreadQueue.isEmpty())

        runFrames(sprites = sprites)
        runUiThread()

        assertEquals(1, exitCount)
        assertEquals(X_DELTA, otherSprite.look.xInUserInterfaceDimensionUnit.toInt())
    }

    @Test
    fun `exit follows at once when no sprite on the stage has a catch script`() {
        val otherScene = Scene("other scene", project)
        otherScene.addSprite(Sprite("sprite of other scene").apply { addScript(catchScript()) })
        project.addScene(otherScene)

        controller.requestExit(exitAction)
        assertEquals(State.PENDING, controller.state)
        controller.onFrame()
        runUiThread()

        assertEquals(1, exitCount)
        assertEquals(State.EXITED, controller.state)
    }

    @Test
    fun `leaving the scene stops the started catch scripts and runs the pending exit`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)
        assertEquals(State.PENDING, controller.state)

        controller.onSceneLeaving { catchScriptStops++ }

        assertEquals(State.EXITED, controller.state)
        assertEquals(1, catchScriptStops)
        runUiThread()
        assertEquals(1, exitCount)
    }

    @Test
    fun `leaving the scene before the catch scripts started keeps the exit pending`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        controller.requestExit(exitAction)

        controller.onSceneLeaving { catchScriptStops++ }

        assertEquals(State.PENDING, controller.state)
        assertEquals(0, catchScriptStops)
        assertTrue(uiThreadQueue.isEmpty())
    }

    @Test
    fun `leaving the scene without a pending exit does nothing`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))

        controller.onSceneLeaving { catchScriptStops++ }

        assertEquals(State.NONE, controller.state)
        assertEquals(0, catchScriptStops)
        assertTrue(uiThreadQueue.isEmpty())
        assertEquals(0, exitCount)
    }

    @Test
    fun `second request forces the pending exit`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        var secondExitCount = 0

        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)
        controller.requestExit { secondExitCount++ }

        assertEquals(State.FORCED, controller.state)
        runUiThread()
        assertEquals(1, exitCount)
        assertEquals(0, secondExitCount)
    }

    @Test
    fun `further requests run a pending exit only once`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        controller.requestExit(exitAction)

        controller.requestExit(exitAction)
        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)
        runUiThread()

        assertEquals(State.FORCED, controller.state)
        assertEquals(1, exitCount)
    }

    @Test
    fun `forcing before the first frame skips the catch`() {
        addCatchScript(ChangeXByNBrick(X_DELTA))
        controller.requestExit(exitAction)

        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)
        runUiThread()

        assertEquals(0, xPosition())
        assertEquals(1, exitCount)
    }

    @Test
    fun `request after a finished exit is ignored`() {
        addCatchScript()
        controller.requestExit(exitAction)
        runFrames()

        controller.requestExit(exitAction)
        runUiThread()

        assertEquals(State.EXITED, controller.state)
        assertEquals(1, exitCount)
    }

    @Test
    fun `reset lets the next exit run the catch scripts again`() {
        addCatchScript(ChangeXByNBrick(X_DELTA))
        controller.requestExit(exitAction)
        runFrames()
        runUiThread()

        controller.reset()
        assertEquals(State.NONE, controller.state)
        controller.requestExit(exitAction)
        assertEquals(State.PENDING, controller.state)
        runFrames()
        runUiThread()

        assertEquals(2, exitCount)
        assertEquals(2 * X_DELTA, xPosition())
    }

    @Test
    fun `reset after a forced exit lets the next exit wait for the catch scripts again`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)
        controller.requestExit(exitAction)
        runUiThread()
        assertEquals(State.FORCED, controller.state)

        controller.reset()
        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)
        runUiThread()

        assertEquals(State.PENDING, controller.state)
        assertEquals(1, exitCount)
    }

    @Test
    fun `reset drops a pending exit`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        controller.requestExit(exitAction)

        controller.reset()
        runFrames(FEW_FRAMES)
        runUiThread()

        assertEquals(State.NONE, controller.state)
        assertEquals(0, exitCount)
    }

    @Test
    fun `unused gamepad B presses Android Back on the UI thread`() {
        controller.onGamepadButtonPressed(GamepadButton.B)

        verify(stageActivity, never()).onBackPressed()
        runUiThread()
        verify(stageActivity).onBackPressed()
        assertEquals(0, finishStageCount)
        assertEquals(State.NONE, controller.state)
    }

    @Test
    fun `finish stage brick runs the catch before it finishes the stage`() {
        addCatchScript(ChangeXByNBrick(X_DELTA), WaitBrick(SHORT_WAIT_MILLIS))

        silentFinishStageAction().act(1f)
        assertEquals(State.PENDING, controller.state)
        assertEquals(0, finishStageCount)

        runFrames()
        runUiThread()

        assertEquals(X_DELTA, xPosition())
        assertEquals(1, finishStageCount)
    }

    @Test
    fun `finish stage brick inside a catch script exits at once`() {
        addCatchScript(ChangeXByNBrick(X_DELTA), ExitStageBrick(), WaitBrick(LONG_WAIT_MILLIS))

        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)

        assertEquals(State.FORCED, controller.state)
        runUiThread()
        assertEquals(1, exitCount)
        assertEquals(0, finishStageCount)
        assertEquals(X_DELTA, xPosition())
    }

    @Test
    fun `finish tests reports success at once while a catch is running`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)

        FinishStageAction().apply { silent = false }.act(1f)

        verify(stageActivity).setResult(eq(TestResult.STAGE_ACTIVITY_TEST_SUCCESS), any())
        verify(stageActivity).finish()
        assertEquals(State.PENDING, controller.state)
        assertTrue(uiThreadQueue.isEmpty())
    }

    @Test
    fun `failing assert reports the failure at once while a catch is running`() {
        addCatchScript(WaitBrick(LONG_WAIT_MILLIS))
        controller.requestExit(exitAction)
        runFrames(FEW_FRAMES)

        AssertEqualsAction().act(1f)

        verify(stageActivity).setResult(eq(TestResult.STAGE_ACTIVITY_TEST_FAIL), any())
        verify(stageActivity).finish()
        assertEquals(State.PENDING, controller.state)
        assertTrue(uiThreadQueue.isEmpty())
    }

    @Test
    fun `finish tests without catch scripts does not request a stage exit`() {
        addCatchScript().isCommentedOut = true

        FinishStageAction().apply { silent = false }.act(1f)

        verify(stageActivity).setResult(eq(TestResult.STAGE_ACTIVITY_TEST_SUCCESS), any())
        assertEquals(0, finishStageCount)
        assertEquals(State.NONE, controller.state)
    }

    @Test
    fun `finish stage without a running stage finishes the stage activity`() {
        StageActivity.stageListener = null

        silentFinishStageAction().act(1f)

        verify(stageActivity).finish()
        verify(stageActivity, never()).setResult(anyInt(), any())
    }

    private fun silentFinishStageAction() = FinishStageAction().apply { silent = true }

    private fun addCatchScript(vararg bricks: Brick): WhenStageExitedScript =
        catchScript(*bricks).also { sprite.addScript(it) }

    private fun catchScript(vararg bricks: Brick): WhenStageExitedScript =
        WhenStageExitedScript().apply { bricks.forEach { addBrick(it) } }

    /**
     * Runs stage frames like StageListener does: the exit controller first, then the threads of
     * the sprites, until no thread is left or [frames] have passed.
     */
    private fun runFrames(frames: Int = MAX_FRAMES, sprites: List<Sprite> = listOf(sprite)) {
        startScriptsIfNeeded(sprites)
        repeat(frames) {
            controller.onFrame()
            sprites.forEach { it.look.act(FRAME_SECONDS) }
        }
        controller.onFrame()
    }

    /** Creates the event threads of each sprite once, like the stage does when it starts. */
    private fun startScriptsIfNeeded(sprites: List<Sprite>) {
        sprites.filter { startedSprites.add(it) }
            .forEach { it.initializeEventThreads(EventId.START) }
    }

    private fun runUiThread() {
        val queued = uiThreadQueue.toList()
        uiThreadQueue.clear()
        queued.forEach { it.run() }
    }

    private fun xPosition(): Int = sprite.look.xInUserInterfaceDimensionUnit.toInt()

    private companion object {
        const val X_DELTA = 15
        const val FRAME_SECONDS = 0.1f
        const val FEW_FRAMES = 5
        const val MAX_FRAMES = 100
        const val SHORT_WAIT_MILLIS = 300
        const val LONG_WAIT_MILLIS = 1_000_000
    }
}
