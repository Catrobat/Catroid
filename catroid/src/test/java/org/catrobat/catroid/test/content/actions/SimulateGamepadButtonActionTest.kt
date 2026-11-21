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

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.actions.SimulateGamepadButtonAction
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource.SIMULATED
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class SimulateGamepadButtonActionTest {

    private val gamepadInput: GamepadInput = spy(GamepadInput())
    private val firedButtons = mutableListOf<GamepadButton>()
    private var previousStageListener: StageListener? = null

    @Before
    fun setUp() {
        previousStageListener = StageActivity.stageListener
        val stageListener = mock(StageListener::class.java)
        `when`(stageListener.gamepadInput).thenReturn(gamepadInput)
        StageActivity.stageListener = stageListener
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = previousStageListener
    }

    @Test
    fun `action is created with the given button and state`() {
        val action = createAction("R2", false) as SimulateGamepadButtonAction

        assertEquals("R2", action.button)
        assertFalse(action.pressed)
    }

    @Test
    fun `first act queues the press and waits until the next frame applies it`() {
        val action = createAction("A", true)

        assertFalse(action.act(DELTA))
        assertFalse(gamepadInput.isPressed(A))
        assertFired()

        applyFrame()

        assertTrue(gamepadInput.isPressed(A))
        assertFired(A)
        assertTrue(action.act(DELTA))
    }

    @Test
    fun `waiting action queues its change only once`() {
        val action = createAction("A", true)

        action.act(DELTA)
        action.act(DELTA)
        action.act(DELTA)
        applyFrame()
        action.act(DELTA)

        verify(gamepadInput, times(1)).setButtonState(A, SIMULATED, true)
    }

    @Test
    fun `restarted action queues its change again`() {
        val press = createAction("X", true)
        val release = createAction("X", false)
        runUntilFinished(press)
        runUntilFinished(release)

        press.restart()
        release.restart()
        runUntilFinished(press)
        runUntilFinished(release)

        assertFired(X, X)
        verify(gamepadInput, times(2)).setButtonState(X, SIMULATED, true)
        verify(gamepadInput, times(2)).setButtonState(X, SIMULATED, false)
    }

    @Test
    fun `looped actions press the button in every iteration`() {
        val loop = Actions.repeat(
            LOOP_COUNT,
            Actions.sequence(createAction("X", true), createAction("X", false))
        )
        loop.actor = Actor()

        runUntilFinished(loop)

        assertFired(X, X, X)
        assertFalse(gamepadInput.isPressed(X))
    }

    @Test
    fun `action finishes without effect when there is no stage`() {
        StageActivity.stageListener = null
        val action = createAction("A", true)

        assertTrue(action.act(DELTA))
    }

    @Test
    fun `action with an unknown button finishes without changing any button`() {
        val action = createAction("HOME", true)

        assertTrue(action.act(DELTA))
        applyFrame()

        assertFired()
        GamepadButton.entries.forEach { button -> assertFalse(gamepadInput.isPressed(button)) }
    }

    private fun createAction(button: String, pressed: Boolean): Action =
        ActionFactory().createSimulateGamepadButtonAction(button, pressed)

    private fun applyFrame() {
        gamepadInput.applyPendingInput { button -> firedButtons.add(button) }
    }

    private fun runUntilFinished(action: Action) {
        repeat(MAX_FRAMES) {
            if (action.act(DELTA)) {
                return
            }
            applyFrame()
        }
        throw AssertionError("Action did not finish within $MAX_FRAMES frames")
    }

    private fun assertFired(vararg buttons: GamepadButton) {
        assertEquals(buttons.toList(), firedButtons)
    }

    private companion object {
        const val DELTA = 0.016f
        const val LOOP_COUNT = 3
        const val MAX_FRAMES = 100
    }
}
