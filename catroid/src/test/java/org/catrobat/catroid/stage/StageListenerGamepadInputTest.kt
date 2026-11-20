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
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadInputSource.KEY
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class StageListenerGamepadInputTest {

    private val project: Project = mock(Project::class.java)
    private val stageListener = StageListener()

    private val previousGl20: GL20? = Gdx.gl20
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
    }

    @After
    fun tearDown() {
        Gdx.gl20 = previousGl20
        Gdx.graphics = previousGraphics
    }

    @Test
    fun `all gamepad buttons are released initially`() {
        GamepadButton.entries.forEach { button ->
            assertFalse(stageListener.gamepadInput.isPressed(button))
        }
    }

    @Test
    fun `applying pending gamepad input fires the pressed button to all sprites`() {
        stageListener.gamepadInput.setButtonState(X, KEY, true)

        stageListener.applyPendingGamepadInput()

        assertTrue(stageListener.gamepadInput.isPressed(X))
        assertFiredEventIds(GamepadButtonPressedEventId(X))
    }

    @Test
    fun `applying pending gamepad input fires nothing for a release`() {
        stageListener.gamepadInput.setButtonState(A, KEY, true)
        stageListener.applyPendingGamepadInput()
        stageListener.gamepadInput.setButtonState(A, KEY, false)

        stageListener.applyPendingGamepadInput()

        assertFalse(stageListener.gamepadInput.isPressed(A))
        assertFiredEventIds(GamepadButtonPressedEventId(A))
    }

    @Test
    fun `rendering a running stage applies pending gamepad input`() {
        stageListener.setPaused(false)
        val sequence = stageListener.gamepadInput.setButtonState(B, KEY, true)

        stageListener.render()

        assertTrue(stageListener.gamepadInput.isApplied(sequence))
        assertTrue(stageListener.gamepadInput.isPressed(B))
        assertFiredEventIds(GamepadButtonPressedEventId(B))
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
