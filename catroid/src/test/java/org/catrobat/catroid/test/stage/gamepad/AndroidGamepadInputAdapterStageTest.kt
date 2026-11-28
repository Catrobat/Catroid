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

package org.catrobat.catroid.test.stage.gamepad

import android.hardware.input.InputManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.InputDevice.SOURCE_KEYBOARD
import android.view.KeyEvent
import android.view.MotionEvent.AXIS_HAT_X
import android.view.MotionEvent.AXIS_RZ
import android.view.MotionEvent.AXIS_X
import android.view.MotionEvent.AXIS_Y
import android.view.MotionEvent.AXIS_Z
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.catrobat.catroid.gamepad.GamepadButton.LEFT
import org.catrobat.catroid.gamepad.GamepadButton.R2
import org.catrobat.catroid.gamepad.GamepadButton.RIGHT
import org.catrobat.catroid.gamepad.GamepadButton.X
import org.catrobat.catroid.gamepad.GamepadStick
import org.catrobat.catroid.gamepad.GamepadStickPosition
import org.catrobat.catroid.stage.gamepad.StageGamepadInputAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Tests the stage adapters of [StageGamepadInputAdapterFactory], the analog sticks and the controller listener. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
class AndroidGamepadInputAdapterStageTest : AndroidGamepadInputAdapterTestBase() {

    @Test
    fun `releaseAll releases every held button without firing`() {
        holdButtonsFromAllSources()

        adapter.releaseAll()
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `removing an input device releases every held button without firing`() {
        holdButtonsFromAllSources()

        adapter.onInputDeviceRemoved(GAMEPAD_DEVICE_ID)
        applyPendingInput()

        assertFired()
        assertPressedExactly()
    }

    @Test
    fun `adding or changing an input device keeps held buttons`() {
        holdButtonsFromAllSources()

        adapter.onInputDeviceAdded(KEYBOARD_DEVICE_ID)
        adapter.onInputDeviceChanged(GAMEPAD_DEVICE_ID)
        applyPendingInput()

        assertFired()
        assertPressedExactly(A, LEFT, R2)
    }

    @Test
    fun `register adds and unregister removes the input device listener`() {
        val inputManager = mock(InputManager::class.java)
        val handler = Handler(Looper.getMainLooper())

        adapter.register(inputManager, handler)
        verify(inputManager).registerInputDeviceListener(adapter, handler)

        adapter.unregister(inputManager)
        verify(inputManager).unregisterInputDeviceListener(adapter)
    }

    @Test
    fun `register without handler uses the calling thread`() {
        val inputManager = mock(InputManager::class.java)

        adapter.register(inputManager)

        verify(inputManager).registerInputDeviceListener(adapter, null)
    }

    @Test
    fun `register and unregister without input manager do nothing`() {
        adapter.register(null)
        adapter.unregister(null)
    }

    @Test
    fun `stage adapter consumes and forwards controller B in every project`() {
        useStageGamepadInput()
        val projects = listOf(
            null,
            Project(),
            projectWith(StartScript()),
            projectWith(WhenGamepadButtonPressedScript(A.name)),
            projectWith(WhenGamepadButtonPressedScript(B.name))
        )

        projects.forEachIndexed { index, project ->
            val stageAdapter = StageGamepadInputAdapterFactory.create(project)

            val downConsumed = stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B))
            assertTrue("B down consumed in project $index", downConsumed)
            applyPendingInput()
            assertPressedExactly(B)
            val upConsumed = stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B))
            assertTrue("B up consumed in project $index", upConsumed)
            applyPendingInput()
            assertPressedExactly()
        }
        assertFired(B, B, B, B, B)
    }

    @Test
    fun `stage adapter consumes other buttons only in a project with gamepad elements`() {
        useStageGamepadInput()
        val adapterWithoutGamepad = StageGamepadInputAdapterFactory.create(projectWith(StartScript()))
        val adapterWithGamepad =
            StageGamepadInputAdapterFactory.create(projectWith(WhenGamepadButtonPressedScript(X.name)))

        KEY_CODE_BUTTONS.filterValues { it != B }.keys.forEach { keyCode ->
            assertFalse(adapterWithoutGamepad.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, keyCode)))
            assertTrue(adapterWithGamepad.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, keyCode)))
        }
    }

    @Test
    fun `stage adapter does not consume B from a keyboard`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(Project())

        assertFalse(
            stageAdapter.dispatchKeyEvent(
                keyEvent(
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B,
                    source = SOURCE_KEYBOARD, deviceId = KEYBOARD_DEVICE_ID
                )
            )
        )
    }

    @Test
    fun `stage adapter does not consume B without a running stage`() {
        val stageAdapter = StageGamepadInputAdapterFactory.create(Project())

        assertFalse(stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
    }

    @Test
    fun `stage adapter with swapped face buttons takes a controller A as B`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(Project(), swapsFaceButtons = true)

        assertTrue(stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)))
        assertTrue(stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        input.applyPendingInput { button -> firedButtons.add(button) }

        assertPressedExactly(B)
        assertFired(B)
    }

    @Test
    fun `stage adapter forwards the left stick of a project that uses a left stick sensor`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_LEFT_STICK_X))

        assertTrue(moveAxes(AXIS_X to 0.5f, AXIS_Y to -0.25f, target = stageAdapter))

        assertEquals(GamepadStickPosition(50.0, 25.0), input.stickPosition(GamepadStick.LEFT))
        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.RIGHT))
    }

    @Test
    fun `stage adapter reads the right stick from the Z and RZ axes`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_RIGHT_STICK_Y))

        assertTrue(moveAxes(AXIS_Z to 1f, AXIS_RZ to 1f, AXIS_X to 1f, target = stageAdapter))

        assertEquals(GamepadStickPosition(100.0, -100.0), input.stickPosition(GamepadStick.RIGHT))
        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.LEFT))
    }

    @Test
    fun `stick deflection in the dead zone around the center reads as centered`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_LEFT_STICK_Y))

        assertTrue(moveAxes(AXIS_X to 0.05f, AXIS_Y to -0.09f, target = stageAdapter))

        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.LEFT))
    }

    @Test
    fun `D-pad presses its button in a project that also reads a stick`() {
        useStageGamepadInput()
        val project = projectUsingSensor(Sensors.GAMEPAD_LEFT_STICK_X).apply {
            defaultScene.spriteList.first().addScript(WhenGamepadButtonPressedScript(LEFT.name))
        }
        val stageAdapter = StageGamepadInputAdapterFactory.create(project)

        assertTrue(moveAxes(AXIS_HAT_X to -1f, AXIS_X to 0.6f, target = stageAdapter))
        input.applyPendingInput { button -> firedButtons.add(button) }
        assertPressedExactly(LEFT)

        assertTrue(moveAxes(AXIS_HAT_X to 0f, target = stageAdapter))
        assertTrue(stageAdapter.dispatchKeyEvent(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)))
        input.applyPendingInput { button -> firedButtons.add(button) }

        assertPressedExactly(LEFT)
        assertFired(LEFT, LEFT)
        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.LEFT))
    }

    @Test
    fun `stage adapter does not forward a stick that the project does not use`() {
        useStageGamepadInput()
        val stageAdapter = StageGamepadInputAdapterFactory.create(projectUsingSensor(Sensors.GAMEPAD_BUTTON_A_PRESSED))

        moveAxes(AXIS_X to 1f, AXIS_Z to 1f, target = stageAdapter)

        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.LEFT))
        assertEquals(GamepadStickPosition.CENTER, input.stickPosition(GamepadStick.RIGHT))
    }
}
