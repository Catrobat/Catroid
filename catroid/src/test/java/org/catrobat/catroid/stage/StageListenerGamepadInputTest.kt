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

package org.catrobat.catroid.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catrobat.catroid.content.EventWrapper
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.WhenStageExitedScript
import org.catrobat.catroid.content.eventids.AnyGamepadButtonPressedEventId
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.content.eventids.StageExitedEventId
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.catrobat.catroid.gamepad.GamepadInputSource.KEY
import org.catrobat.catroid.gamepad.GamepadInputSource.SIMULATED
import org.catrobat.catroid.stage.StageExitController.State
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.lang.ref.WeakReference

@RunWith(JUnit4::class)
class StageListenerGamepadInputTest {

    private val project: Project = mock(Project::class.java)
    private val stageActivity: StageActivity = mock(StageActivity::class.java)
    private val stageListener = StageListener()

    private val previousGl20: GL20? = Gdx.gl20
    private var finishStageCount = 0
    private val previousGraphics: Graphics? = Gdx.graphics

    @Before
    fun setUp() {
        Gdx.gl20 = mock(GL20::class.java)
        Gdx.graphics = mock(Graphics::class.java)

        setField("project", project)
        setField("scene", mock(Scene::class.java))
        setField("stage", mock(Stage::class.java))
        setField("batch", mock(Batch::class.java))
        setField("camera", mock(OrthographicCamera::class.java))
        setField("sprites", mutableListOf<Sprite>())
        stageListener.shapeRenderer = mock(ShapeRenderer::class.java)
        stageListener.cameraPositioner = mock(CameraPositioner::class.java)
        stageListener.embroideryPatternManager = mock(EmbroideryPatternManager::class.java)
        useStageExitControllerFor(project)
        StageActivity.activeStageActivity = WeakReference(stageActivity)
    }

    @After
    fun tearDown() {
        Gdx.gl20 = previousGl20
        Gdx.graphics = previousGraphics
        StageActivity.activeStageActivity = null
    }

    @Test
    fun `rendering a paused stage keeps pending gamepad input for later`() {
        stageListener.setPaused(true)
        val sequence = stageListener.gamepadInput.setButtonState(B, KEY, true)

        stageListener.render()

        assertFalse(stageListener.gamepadInput.isApplied(sequence))
        assertFalse(stageListener.gamepadInput.isPressed(B))
        assertFiredEventIds()

        stageListener.setPaused(false)
        stageListener.render()

        assertTrue(stageListener.gamepadInput.isPressed(B))
        assertFiredEventIds(GamepadButtonPressedEventId(B))
    }

    @Test
    fun `restarting the stage releases all buttons and discards pending gamepad input`() {
        stageListener.gamepadInput.setButtonState(A, KEY, true)
        stageListener.applyPendingGamepadInput()
        val pendingSequence = stageListener.gamepadInput.setButtonState(B, KEY, true)
        setField("reloadProject", true)

        stageListener.render()

        assertFalse(stageListener.gamepadInput.isPressed(A))
        assertTrue(stageListener.gamepadInput.isApplied(pendingSequence))

        stageListener.setPaused(false)
        stageListener.render()

        assertFalse(stageListener.gamepadInput.isPressed(B))
        assertFiredEventIds(GamepadButtonPressedEventId(A))
    }

    @Test
    fun `stage owns one exit controller without a pending exit`() {
        val listener = StageListener()

        assertSame(listener.stageExitController, listener.stageExitController)
        assertEquals(State.NONE, listener.stageExitController.state)
    }

    @Test
    fun `pressing an unused B presses Android Back`() {
        pressAndRelease(B)

        assertBackPresses(1)
        assertFiredEventIds(GamepadButtonPressedEventId(B))
    }

    @Test
    fun `simulated press of an unused B presses Android Back`() {
        stageListener.gamepadInput.setButtonState(B, SIMULATED, true)
        stageListener.applyPendingGamepadInput()

        assertBackPresses(1)
    }

    @Test
    fun `holding or releasing an unused B presses Android Back only once`() {
        stageListener.gamepadInput.setButtonState(B, KEY, true)
        stageListener.applyPendingGamepadInput()
        stageListener.gamepadInput.setButtonState(B, SIMULATED, true)
        stageListener.applyPendingGamepadInput()
        stageListener.gamepadInput.setButtonState(B, KEY, false)
        stageListener.gamepadInput.setButtonState(B, SIMULATED, false)
        stageListener.applyPendingGamepadInput()

        assertBackPresses(1)
    }

    @Test
    fun `pressing other buttons presses no Android Back`() {
        GamepadButton.entries.filter { it != B }.forEach { pressAndRelease(it) }

        assertBackPresses(0)
    }

    @Test
    fun `pressing B in a project that uses B presses no Android Back`() {
        useStageExitControllerFor(projectWith(WhenGamepadButtonPressedScript(B.name)))

        pressAndRelease(B)

        assertBackPresses(0)
        assertFiredEventIds(GamepadButtonPressedEventId(B))
    }

    @Test
    fun `pressing B in a project that only uses B in a commented out script presses Android Back`() {
        val commentedOutScript = WhenGamepadButtonPressedScript(B.name).apply { isCommentedOut = true }
        useStageExitControllerFor(projectWith(WhenGamepadButtonPressedScript(A.name), commentedOutScript))

        pressAndRelease(B)

        assertBackPresses(1)
    }

    @Test
    fun `B pressed on a paused stage presses Android Back once the stage runs again`() {
        stageListener.setPaused(true)
        stageListener.gamepadInput.setButtonState(B, KEY, true)

        stageListener.render()
        assertBackPresses(0)

        stageListener.setPaused(false)
        stageListener.render()
        assertBackPresses(1)
    }

    @Test
    fun `rendering a running stage fires the stage exited event of a pending exit`() {
        val catchProject = mock(Project::class.java)
        doReturn(projectWith(WhenStageExitedScript()).sceneList).`when`(catchProject).sceneList
        setField("project", catchProject)
        useStageExitControllerFor(catchProject)
        stageListener.stageExitController.requestFinishStage()
        stageListener.setPaused(true)

        stageListener.render()
        assertEquals(State.PENDING, stageListener.stageExitController.state)

        stageListener.setPaused(false)
        stageListener.render()

        val firedEvents = ArgumentCaptor.forClass(EventWrapper::class.java)
        verify(catchProject).fireToAllSprites(firedEvents.capture())
        assertEquals(listOf<EventId>(StageExitedEventId), firedEvents.allValues.map { it.eventId })
        assertEquals(State.EXITED, stageListener.stageExitController.state)
        assertEquals(1, finishStageCount)
    }

    @Test
    fun `press fires the any button event before the event of the button`() {
        useProjectScripts(WhenGamepadButtonPressedScript(GamepadButton.ANY_ID))

        pressAndRelease(A)

        assertFiredEventIds(AnyGamepadButtonPressedEventId, GamepadButtonPressedEventId(A))
    }

    @Test
    fun `unused B presses Android Back without firing the any button event`() {
        useProjectScripts(WhenGamepadButtonPressedScript(GamepadButton.ANY_ID))

        pressAndRelease(B)

        assertFiredEventIds(GamepadButtonPressedEventId(B))
        assertBackPresses(1)
    }

    @Test
    fun `explicitly used B fires the any button event and stays on the stage`() {
        useProjectScripts(
            WhenGamepadButtonPressedScript(GamepadButton.ANY_ID),
            WhenGamepadButtonPressedScript(B.name)
        )

        pressAndRelease(B)

        assertFiredEventIds(AnyGamepadButtonPressedEventId, GamepadButtonPressedEventId(B))
        assertBackPresses(0)
    }

    @Test
    fun `press fires no any button event without such a script`() {
        useProjectScripts(WhenGamepadButtonPressedScript(A.name))

        pressAndRelease(A)

        assertFiredEventIds(GamepadButtonPressedEventId(A))
    }

    @Test
    fun `second press of a button queued in the same frame fires its event in a later frame`() {
        useProjectScripts(WhenGamepadButtonPressedScript(A.name))
        val input = stageListener.gamepadInput
        input.setButtonState(A, KEY, true)
        input.setButtonState(A, KEY, false)
        val secondPress = input.setButtonState(A, KEY, true)

        stageListener.applyPendingGamepadInput()

        assertFiredEventIds(GamepadButtonPressedEventId(A))

        stageListener.applyPendingGamepadInput()
        stageListener.applyPendingGamepadInput()

        assertTrue(input.isApplied(secondPress))
        assertTrue(input.isPressed(A))
        assertFiredEventIds(GamepadButtonPressedEventId(A), GamepadButtonPressedEventId(A))
    }

    @Test
    fun `button pressed and released in the same frame stays pressed until the next frame`() {
        val input = stageListener.gamepadInput
        input.setButtonState(A, KEY, true)
        val release = input.setButtonState(A, KEY, false)

        stageListener.applyPendingGamepadInput()

        assertTrue(input.isPressed(A))
        assertFalse(input.isApplied(release))
        assertFiredEventIds(GamepadButtonPressedEventId(A))

        stageListener.applyPendingGamepadInput()

        assertFalse(input.isPressed(A))
        assertTrue(input.isApplied(release))
        assertFiredEventIds(GamepadButtonPressedEventId(A))
    }

    /** Asserts that the stage pressed Android Back [count] times and never finished silently. */
    private fun assertBackPresses(count: Int) {
        verify(stageActivity, times(count)).onBackPressed()
        assertEquals(0, finishStageCount)
    }

    private fun useProjectScripts(vararg scripts: Script) {
        doReturn(projectWith(*scripts).sceneList).`when`(project).sceneList
    }

    private fun pressAndRelease(button: GamepadButton) {
        stageListener.gamepadInput.setButtonState(button, KEY, true)
        stageListener.applyPendingGamepadInput()
        stageListener.gamepadInput.setButtonState(button, KEY, false)
        stageListener.applyPendingGamepadInput()
    }

    /**
     * Gives the stage an exit controller that looks at [controllerProject], records finishing the
     * stage and runs hand-offs to the UI thread at once.
     */
    private fun useStageExitControllerFor(controllerProject: Project) {
        setField(
            "stageExitController",
            StageExitController(
                project = { controllerProject },
                finishStage = { finishStageCount++ },
                runOnUiThread = { it.run() }
            )
        )
    }

    private fun projectWith(vararg scripts: Script): Project {
        val scriptProject = Project()
        val scene = Scene("Scene", scriptProject)
        scene.addSprite(Sprite("Sprite").apply { scripts.forEach { addScript(it) } })
        scriptProject.addScene(scene)
        return scriptProject
    }

    private fun assertFiredEventIds(vararg expectedEventIds: EventId) {
        val firedEvents = ArgumentCaptor.forClass(EventWrapper::class.java)
        verify(project, atLeast(0)).fireToAllSprites(firedEvents.capture())
        assertEquals(expectedEventIds.toList(), firedEvents.allValues.map { it.eventId })
    }

    private fun setField(name: String, value: Any?) {
        StageListener::class.java.getDeclaredField(name).apply {
            isAccessible = true
            set(stageListener, value)
        }
    }
}
