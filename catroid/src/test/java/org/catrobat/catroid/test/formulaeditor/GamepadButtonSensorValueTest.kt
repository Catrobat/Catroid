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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.sensor
import org.catrobat.catroid.formulaeditor.sensorValue
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class GamepadButtonSensorValueTest {

    private val gamepadInput = GamepadInput()
    private var previousStageListener: StageListener? = null
    private lateinit var scope: Scope

    @Before
    fun setUp() {
        previousStageListener = StageActivity.stageListener
        val stageListener = mock(StageListener::class.java)
        `when`(stageListener.gamepadInput).thenReturn(gamepadInput)
        StageActivity.stageListener = stageListener

        val project = Project(MockUtil.mockContextForProject(), "Project")
        val sprite = Sprite("sprite")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
        scope = Scope(project, sprite, SequenceAction())
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = previousStageListener
    }

    @Test
    fun `sensor of a pressed button is 1`() {
        press(GamepadButton.A)

        assertEquals(PRESSED, SensorHandler.getSensorValue(Sensors.GAMEPAD_BUTTON_A_PRESSED))
    }

    @Test
    fun `sensors of the other buttons stay 0 while one button is pressed`() {
        press(GamepadButton.UP)

        GamepadButton.entries.filterNot { it == GamepadButton.UP }.forEach { button ->
            assertEquals(button.name, RELEASED, SensorHandler.getSensorValue(button.sensor))
        }
    }

    @Test
    fun `sensor of every pressed button is 1`() {
        GamepadButton.entries.forEach(::press)

        GamepadButton.entries.forEach { button ->
            assertEquals(button.name, PRESSED, SensorHandler.getSensorValue(button.sensor))
        }
    }

    @Test
    fun `sensor of a released button is 0`() {
        press(GamepadButton.R2)
        release(GamepadButton.R2)

        assertEquals(RELEASED, SensorHandler.getSensorValue(Sensors.GAMEPAD_BUTTON_R2_PRESSED))
    }

    @Test
    fun `sensor of a button that was never pressed is 0`() {
        GamepadButton.entries.forEach { button ->
            assertEquals(button.name, RELEASED, SensorHandler.getSensorValue(button.sensor))
        }
    }

    @Test
    fun `sensor changes only after the next frame applied the press`() {
        gamepadInput.setButtonState(GamepadButton.START, GamepadInputSource.KEY, true)

        assertEquals(RELEASED, SensorHandler.getSensorValue(Sensors.GAMEPAD_BUTTON_START_PRESSED))

        gamepadInput.applyPendingInput { }

        assertEquals(PRESSED, SensorHandler.getSensorValue(Sensors.GAMEPAD_BUTTON_START_PRESSED))
    }

    @Test
    fun `sensors are 0 without a stage`() {
        press(GamepadButton.A)
        StageActivity.stageListener = null

        GamepadButton.entries.forEach { button ->
            assertEquals(button.name, RELEASED, SensorHandler.getSensorValue(button.sensor))
        }
    }

    @Test
    fun `sensor value of a button follows its pressed state`() {
        assertEquals(RELEASED, GamepadButton.SELECT.sensorValue(), DELTA)

        press(GamepadButton.SELECT)

        assertEquals(PRESSED, GamepadButton.SELECT.sensorValue(), DELTA)
    }

    @Test
    fun `sensor value of a button is 0 without a stage`() {
        StageActivity.stageListener = null

        assertEquals(RELEASED, GamepadButton.L1.sensorValue(), DELTA)
    }

    @Test
    fun `formula with the sensor of a pressed button is true`() {
        press(GamepadButton.L2)

        val formula = sensorFormula(Sensors.GAMEPAD_BUTTON_L2_PRESSED)

        assertTrue(formula.interpretBoolean(scope))
        assertEquals(PRESSED, formula.interpretDouble(scope), DELTA)
    }

    @Test
    fun `formula with the sensor of a released button is false`() {
        press(GamepadButton.L2)

        val formula = sensorFormula(Sensors.GAMEPAD_BUTTON_R1_PRESSED)

        assertFalse(formula.interpretBoolean(scope))
        assertEquals(RELEASED, formula.interpretDouble(scope), DELTA)
    }

    @Test
    fun `formula with a gamepad button sensor is false without a stage`() {
        StageActivity.stageListener = null

        assertFalse(sensorFormula(Sensors.GAMEPAD_BUTTON_A_PRESSED).interpretBoolean(scope))
    }

    private fun press(button: GamepadButton) {
        gamepadInput.setButtonState(button, GamepadInputSource.KEY, true)
        gamepadInput.applyPendingInput { }
    }

    private fun release(button: GamepadButton) {
        gamepadInput.setButtonState(button, GamepadInputSource.KEY, false)
        gamepadInput.applyPendingInput { }
    }

    private fun sensorFormula(sensor: Sensors) =
        Formula(FormulaElement(FormulaElement.ElementType.SENSOR, sensor.name, null))

    private companion object {
        const val PRESSED = 1.0
        const val RELEASED = 0.0
        const val DELTA = 0.0
    }
}
