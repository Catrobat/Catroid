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

import com.thoughtworks.xstream.io.xml.DomDriver
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.InternFormulaKeyboardAdapter
import org.catrobat.catroid.formulaeditor.InternToExternGenerator
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.findGamepadButtonSensor
import org.catrobat.catroid.formulaeditor.sensor
import org.catrobat.catroid.formulaeditor.sensorNameResId
import org.catrobat.catroid.formulaeditor.toGamepadButton
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.io.XstreamSerializer
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.StringReader

@RunWith(JUnit4::class)
class GamepadButtonSensorsTest {

    @Test
    fun `every button has its own sensor in button order`() {
        assertEquals(EXPECTED_SENSORS, GamepadButton.entries.map { it.sensor })
    }

    @Test
    fun `sensor names are derived from the stable button ID`() {
        GamepadButton.entries.forEach { button ->
            assertEquals("GAMEPAD_BUTTON_${button.name}_PRESSED", button.sensor.name)
        }
    }

    @Test
    fun `every gamepad button sensor maps back to its button`() {
        GamepadButton.entries.forEach { button ->
            assertEquals(button, button.sensor.toGamepadButton())
        }
    }

    @Test
    fun `legacy Chromecast gamepad sensors are not physical gamepad button sensors`() {
        LEGACY_CAST_SENSORS.forEach { sensor -> assertNull(sensor.name, sensor.toGamepadButton()) }
    }

    @Test
    fun `no other sensor is a physical gamepad button sensor`() {
        Sensors.values()
            .filterNot { it in EXPECTED_SENSORS }
            .forEach { sensor -> assertNull(sensor.name, sensor.toGamepadButton()) }
    }

    @Test
    fun `gamepad button sensors are appended after all existing sensors`() {
        val sensors = Sensors.values().toList().dropLast(STICK_SENSORS.size + 1)

        assertEquals(STICK_SENSORS, Sensors.values().toList().takeLast(STICK_SENSORS.size))
        assertEquals(Sensors.GAMEPAD_BUTTON_ANY_PRESSED, Sensors.values().dropLast(STICK_SENSORS.size).last())
        assertEquals(EXPECTED_SENSORS, sensors.takeLast(EXPECTED_SENSORS.size))
        assertEquals(
            Sensors.STAGE_HEIGHT,
            sensors[sensors.size - EXPECTED_SENSORS.size - 1]
        )
    }

    @Test
    fun `gamepad button sensors are Boolean`() {
        EXPECTED_SENSORS.forEach { sensor -> assertTrue(sensor.name, Sensors.isBoolean(sensor)) }
    }

    @Test
    fun `legacy Chromecast gamepad sensors stay Boolean`() {
        LEGACY_CAST_SENSORS.forEach { sensor -> assertTrue(sensor.name, Sensors.isBoolean(sensor)) }
    }

    @Test
    fun `gamepad button sensors are no object sensors`() {
        EXPECTED_SENSORS.forEach { sensor -> assertFalse(sensor.name, sensor.isObjectSensor) }
    }

    @Test
    fun `gamepad button sensors are recognized by their name`() {
        EXPECTED_SENSORS.forEach { sensor ->
            assertTrue(sensor.name, Sensors.isSensor(sensor.name))
            assertEquals(sensor, Sensors.getSensorByValue(sensor.name))
        }
    }

    @Test
    fun `every button has its own sensor name resource in button order`() {
        assertEquals(EXPECTED_SENSOR_NAME_RES_IDS, GamepadButton.entries.map { it.sensorNameResId })
    }

    @Test
    fun `sensor name resources resolve to their gamepad button sensors`() {
        GamepadButton.entries.forEach { button ->
            assertEquals(button.sensor, findGamepadButtonSensor(button.sensorNameResId))
        }
    }

    @Test
    fun `other resources resolve to no gamepad button sensor`() {
        listOf(
            R.string.formula_editor_sensor_gamepad_a_pressed,
            R.string.formula_editor_sensor_gamepad_up_pressed,
            R.string.formula_editor_sensor_user_language,
            R.string.gamepad_button_a,
            0
        ).forEach { resId -> assertNull(findGamepadButtonSensor(resId)) }
    }

    @Test
    fun `extern representation uses the sensor name resources`() {
        GamepadButton.entries.forEach { button ->
            assertEquals(
                button.sensorNameResId,
                InternToExternGenerator.getMappedString(button.sensor.name)
            )
        }
    }

    @Test
    fun `legacy Chromecast gamepad sensors keep their extern representation`() {
        assertEquals(
            R.string.formula_editor_sensor_gamepad_a_pressed,
            InternToExternGenerator.getMappedString(Sensors.GAMEPAD_A_PRESSED.name)
        )
        assertEquals(
            R.string.formula_editor_sensor_gamepad_right_pressed,
            InternToExternGenerator.getMappedString(Sensors.GAMEPAD_RIGHT_PRESSED.name)
        )
    }

    @Test
    fun `keyboard adapter inserts the sensor of each sensor name resource`() {
        val adapter = InternFormulaKeyboardAdapter()

        EXPECTED_SENSOR_NAME_RES_IDS.zip(EXPECTED_SENSORS).forEach { (resId, sensor) ->
            val tokens = adapter.createInternTokenListByResourceId(resId, "")

            assertEquals(sensor.name, 1, tokens.size)
            assertEquals(InternTokenType.SENSOR, tokens.single().internTokenType)
            assertEquals(sensor.name, tokens.single().tokenStringValue)
        }
    }

    @Test
    fun `keyboard adapter keeps inserting legacy Chromecast gamepad sensors`() {
        val tokens = InternFormulaKeyboardAdapter()
            .createInternTokenListByResourceId(R.string.formula_editor_sensor_gamepad_a_pressed, "")

        assertEquals(listOf(Sensors.GAMEPAD_A_PRESSED.name), tokens.map(InternToken::getTokenStringValue))
    }

    @Test
    fun `gamepad button sensors require no brick resources`() {
        EXPECTED_SENSORS.forEach { sensor ->
            val resources = mutableSetOf<Int>()

            sensorElement(sensor).addRequiredResources(resources)

            assertTrue(sensor.name, resources.isEmpty())
        }
    }

    @Test
    fun `legacy Chromecast gamepad sensors still require a cast project`() {
        LEGACY_CAST_SENSORS.forEach { sensor ->
            val resources = mutableSetOf<Int>()

            sensorElement(sensor).addRequiredResources(resources)

            assertEquals(sensor.name, setOf(Brick.CAST_REQUIRED), resources)
        }
    }

    @Test
    fun `formula with a gamepad button sensor is serialized with the sensor name`() {
        val xml = xstream.toXML(WaitUntilBrick(Formula(sensorElement(Sensors.GAMEPAD_BUTTON_UP_PRESSED))))

        assertThat(xml, containsString("<type>SENSOR</type>"))
        assertThat(xml, containsString("<value>GAMEPAD_BUTTON_UP_PRESSED</value>"))
        assertTrue(
            xml,
            Regex("<type>SENSOR</type>\\s*<value>GAMEPAD_BUTTON_UP_PRESSED</value>").containsMatchIn(xml)
        )
    }

    @Test
    fun `formula with a gamepad button sensor is read back`() {
        val xml = xstream.toXML(WaitUntilBrick(Formula(sensorElement(Sensors.GAMEPAD_BUTTON_UP_PRESSED))))

        val brick = xstream.unmarshal(DomDriver().createReader(StringReader(xml))) as WaitUntilBrick
        val root = brick.getFormulaWithBrickField(Brick.BrickField.IF_CONDITION).root

        assertEquals(FormulaElement.ElementType.SENSOR, root.elementType)
        assertEquals(Sensors.GAMEPAD_BUTTON_UP_PRESSED, Sensors.getSensorByValue(root.value))
        assertEquals(GamepadButton.UP, Sensors.getSensorByValue(root.value)?.toGamepadButton())
    }

    private fun sensorElement(sensor: Sensors) =
        FormulaElement(FormulaElement.ElementType.SENSOR, sensor.name, null)

    private companion object {
        val STICK_SENSORS = listOf(
            Sensors.GAMEPAD_LEFT_STICK_X,
            Sensors.GAMEPAD_LEFT_STICK_Y,
            Sensors.GAMEPAD_RIGHT_STICK_X,
            Sensors.GAMEPAD_RIGHT_STICK_Y
        )

        val xstream = XstreamSerializer.getInstance().xstream

        val EXPECTED_SENSORS = listOf(
            Sensors.GAMEPAD_BUTTON_A_PRESSED,
            Sensors.GAMEPAD_BUTTON_B_PRESSED,
            Sensors.GAMEPAD_BUTTON_X_PRESSED,
            Sensors.GAMEPAD_BUTTON_Y_PRESSED,
            Sensors.GAMEPAD_BUTTON_UP_PRESSED,
            Sensors.GAMEPAD_BUTTON_DOWN_PRESSED,
            Sensors.GAMEPAD_BUTTON_LEFT_PRESSED,
            Sensors.GAMEPAD_BUTTON_RIGHT_PRESSED,
            Sensors.GAMEPAD_BUTTON_START_PRESSED,
            Sensors.GAMEPAD_BUTTON_SELECT_PRESSED,
            Sensors.GAMEPAD_BUTTON_L1_PRESSED,
            Sensors.GAMEPAD_BUTTON_L2_PRESSED,
            Sensors.GAMEPAD_BUTTON_R1_PRESSED,
            Sensors.GAMEPAD_BUTTON_R2_PRESSED
        )

        val EXPECTED_SENSOR_NAME_RES_IDS = listOf(
            R.string.formula_editor_sensor_gamepad_button_a_pressed,
            R.string.formula_editor_sensor_gamepad_button_b_pressed,
            R.string.formula_editor_sensor_gamepad_button_x_pressed,
            R.string.formula_editor_sensor_gamepad_button_y_pressed,
            R.string.formula_editor_sensor_gamepad_button_up_pressed,
            R.string.formula_editor_sensor_gamepad_button_down_pressed,
            R.string.formula_editor_sensor_gamepad_button_left_pressed,
            R.string.formula_editor_sensor_gamepad_button_right_pressed,
            R.string.formula_editor_sensor_gamepad_button_start_pressed,
            R.string.formula_editor_sensor_gamepad_button_select_pressed,
            R.string.formula_editor_sensor_gamepad_button_l1_pressed,
            R.string.formula_editor_sensor_gamepad_button_l2_pressed,
            R.string.formula_editor_sensor_gamepad_button_r1_pressed,
            R.string.formula_editor_sensor_gamepad_button_r2_pressed
        )

        val LEGACY_CAST_SENSORS = listOf(
            Sensors.GAMEPAD_A_PRESSED,
            Sensors.GAMEPAD_B_PRESSED,
            Sensors.GAMEPAD_UP_PRESSED,
            Sensors.GAMEPAD_DOWN_PRESSED,
            Sensors.GAMEPAD_LEFT_PRESSED,
            Sensors.GAMEPAD_RIGHT_PRESSED
        )
    }
}
