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

package org.catrobat.catroid.test.formulaeditor

import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.eventids.AnyGamepadButtonPressedEventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.findGamepadButtonSensor
import org.catrobat.catroid.formulaeditor.formulaEditorGamepadInput
import org.catrobat.catroid.formulaeditor.isGamepadButtonSensor
import org.catrobat.catroid.formulaeditor.toGamepadButton
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.stage.gamepad.GamepadUsage
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock

/** The choice "any button": its sensor value, its event id and how it is told apart from buttons. */
@RunWith(JUnit4::class)
class GamepadAnyButtonTest {

    private val stageInput = GamepadInput()
    private val stageListener = mock(StageListener::class.java)
    private var previousStageListener: StageListener? = null

    @Before
    fun setUp() {
        previousStageListener = StageActivity.stageListener
        doReturn(stageInput).`when`(stageListener).gamepadInput
        useProjectScripts(WhenGamepadButtonPressedScript(GamepadButton.ANY_ID))
        StageActivity.stageListener = stageListener
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = previousStageListener
        formulaEditorGamepadInput = null
    }

    @Test
    fun `sensor is false while no button is held`() {
        assertEquals(RELEASED, anySensorValue())
    }

    @Test
    fun `sensor is true while any button other than B is held`() {
        GamepadButton.entries.filterNot { it == GamepadButton.B }.forEach { button ->
            setPressed(stageInput, button, true)
            assertEquals(button.name, PRESSED, anySensorValue())

            setPressed(stageInput, button, false)
            assertEquals(button.name, RELEASED, anySensorValue())
        }
    }

    @Test
    fun `sensor stays true until the last held button is released`() {
        setPressed(stageInput, GamepadButton.L2, true)
        setPressed(stageInput, GamepadButton.A, true)
        setPressed(stageInput, GamepadButton.L2, false)
        assertEquals(PRESSED, anySensorValue())

        setPressed(stageInput, GamepadButton.A, false)
        assertEquals(RELEASED, anySensorValue())
    }

    @Test
    fun `sensor ignores an unused B`() {
        setPressed(stageInput, GamepadButton.B, true)

        assertEquals(RELEASED, anySensorValue())
    }

    @Test
    fun `sensor counts an explicitly used B`() {
        useProjectScripts(WhenGamepadButtonPressedScript(GamepadButton.B.name))
        setPressed(stageInput, GamepadButton.B, true)

        assertEquals(PRESSED, anySensorValue())
    }

    @Test
    fun `sensor is false without a stage`() {
        setPressed(stageInput, GamepadButton.A, true)
        StageActivity.stageListener = null

        assertEquals(RELEASED, anySensorValue())
    }

    @Test
    fun `sensor reads the input of the compute dialog while it is open`() {
        setPressed(stageInput, GamepadButton.A, true)
        formulaEditorGamepadInput = GamepadInput()
        assertEquals(RELEASED, anySensorValue())

        formulaEditorGamepadInput?.let { setPressed(it, GamepadButton.X, true) }
        assertEquals(PRESSED, anySensorValue())
    }

    @Test
    fun `sensor is a Boolean gamepad button sensor without a single button`() {
        assertTrue(Sensors.isBoolean(Sensors.GAMEPAD_BUTTON_ANY_PRESSED))
        assertTrue(Sensors.GAMEPAD_BUTTON_ANY_PRESSED.isGamepadButtonSensor)
        assertNull(Sensors.GAMEPAD_BUTTON_ANY_PRESSED.toGamepadButton())
    }

    @Test
    fun `sensor name resource resolves to the sensor`() {
        assertEquals(
            Sensors.GAMEPAD_BUTTON_ANY_PRESSED,
            findGamepadButtonSensor(R.string.formula_editor_sensor_gamepad_button_any_pressed)
        )
    }

    @Test
    fun `any is no physical button`() {
        assertNull(GamepadButton.fromId(GamepadButton.ANY_ID))
    }

    @Test
    fun `script of any button listens for the any button event only`() {
        val eventId = WhenGamepadButtonPressedScript(GamepadButton.ANY_ID).createEventId(Sprite("sprite"))

        assertEquals(AnyGamepadButtonPressedEventId, eventId)
        GamepadButton.entries.forEach { button ->
            assertNotEquals(GamepadButtonPressedEventId(button), eventId)
        }
    }

    private fun anySensorValue(): Any = SensorHandler.getSensorValue(Sensors.GAMEPAD_BUTTON_ANY_PRESSED)

    private fun setPressed(input: GamepadInput, button: GamepadButton, pressed: Boolean) {
        input.setButtonState(button, GamepadInputSource.KEY, pressed)
        input.applyPendingInput { }
    }

    private fun useProjectScripts(vararg scripts: Script) {
        val project = Project()
        val scene = Scene("Scene", project)
        scene.addSprite(Sprite("Sprite").apply { scripts.forEach { addScript(it) } })
        project.addScene(scene)
        doReturn(GamepadUsage.of(project)).`when`(stageListener).gamepadUsage
    }

    private companion object {
        const val PRESSED = 1.0
        const val RELEASED = 0.0
    }
}
