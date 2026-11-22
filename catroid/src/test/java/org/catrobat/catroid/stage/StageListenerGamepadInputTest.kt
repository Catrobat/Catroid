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
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class StageListenerGamepadInputTest {

    private val project: Project = mock(Project::class.java)
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
    }

    @After
    fun tearDown() {
        Gdx.gl20 = previousGl20
        Gdx.graphics = previousGraphics
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
    fun `pressing an unused B requests a stage exit like finish stage`() {
        pressAndRelease(B)

        assertEquals(1, finishStageCount)
        assertFiredEventIds(GamepadButtonPressedEventId(B))
    }

    @Test
    fun `simulated press of an unused B requests a stage exit`() {
        stageListener.gamepadInput.setButtonState(B, SIMULATED, true)
        stageListener.applyPendingGamepadInput()

        assertEquals(1, finishStageCount)
    }

    @Test
    fun `holding or releasing an unused B requests no further stage exit`() {
        stageListener.gamepadInput.setButtonState(B, KEY, true)
        stageListener.applyPendingGamepadInput()
        stageListener.gamepadInput.setButtonState(B, SIMULATED, true)
        stageListener.applyPendingGamepadInput()
        stageListener.gamepadInput.setButtonState(B, KEY, false)
        stageListener.gamepadInput.setButtonState(B, SIMULATED, false)
        stageListener.applyPendingGamepadInput()

        assertEquals(1, finishStageCount)
    }

    @Test
    fun `pressing other buttons requests no stage exit`() {
        GamepadButton.entries.filter { it != B }.forEach { pressAndRelease(it) }

        assertEquals(0, finishStageCount)
    }

    @Test
    fun `pressing B in a project that uses B requests no stage exit`() {
        useStageExitControllerFor(projectWith(WhenGamepadButtonPressedScript(B.name)))

        pressAndRelease(B)

        assertEquals(0, finishStageCount)
        assertFiredEventIds(GamepadButtonPressedEventId(B))
    }

    @Test
    fun `pressing B in a project that only uses B in a commented out script requests a stage exit`() {
        val commentedOutScript = WhenGamepadButtonPressedScript(B.name).apply { isCommentedOut = true }
        useStageExitControllerFor(projectWith(WhenGamepadButtonPressedScript(A.name), commentedOutScript))

        pressAndRelease(B)

        assertEquals(1, finishStageCount)
    }

    @Test
    fun `B pressed on a paused stage requests the stage exit once the stage runs again`() {
        stageListener.setPaused(true)
        stageListener.gamepadInput.setButtonState(B, KEY, true)

        stageListener.render()
        assertEquals(0, finishStageCount)

        stageListener.setPaused(false)
        stageListener.render()
        assertEquals(1, finishStageCount)
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
    fun `press fires the any button event for a project with such a script`() {
        useProjectScripts(WhenGamepadButtonPressedScript(GamepadButton.ANY_ID))

        pressAndRelease(A)

        assertFiredEventIds(GamepadButtonPressedEventId(A), AnyGamepadButtonPressedEventId)
    }

    @Test
    fun `unused B exits the stage without firing the any button event`() {
        useProjectScripts(WhenGamepadButtonPressedScript(GamepadButton.ANY_ID))

        pressAndRelease(B)

        assertFiredEventIds(GamepadButtonPressedEventId(B))
        assertEquals(1, finishStageCount)
    }

    @Test
    fun `explicitly used B fires the any button event and stays on the stage`() {
        useProjectScripts(
            WhenGamepadButtonPressedScript(GamepadButton.ANY_ID),
            WhenGamepadButtonPressedScript(B.name)
        )

        pressAndRelease(B)

        assertFiredEventIds(GamepadButtonPressedEventId(B), AnyGamepadButtonPressedEventId)
        assertEquals(0, finishStageCount)
    }

    @Test
    fun `press fires no any button event without such a script`() {
        useProjectScripts(WhenGamepadButtonPressedScript(A.name))

        pressAndRelease(A)

        assertFiredEventIds(GamepadButtonPressedEventId(A))
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
