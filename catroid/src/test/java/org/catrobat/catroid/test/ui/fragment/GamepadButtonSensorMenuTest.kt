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

package org.catrobat.catroid.test.ui.fragment

import android.os.Build
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.ui.fragment.GamepadButtonSensorMenu
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GamepadButtonSensorMenuTest {
    private lateinit var activity: AppCompatActivity
    private val replacements = mutableListOf<Int>()
    private val menu = GamepadButtonSensorMenu { sensorNameResId -> replacements.add(sensorNameResId) }

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(AppCompatActivity::class.java).setup().get()
    }

    @Test
    fun `menu offers any and the labels of all buttons in button order`() {
        assertEquals(EXPECTED_LABELS, menu.getLabels(activity))
    }

    @Test
    fun `choosing a button replaces the selection by its sensor`() {
        menu.choose(UP_POSITION)

        assertEquals(listOf(R.string.formula_editor_sensor_gamepad_button_up_pressed), replacements)
    }

    @Test
    fun `choosing each button replaces the selection by the sensor of that button`() {
        EXPECTED_LABELS.indices.forEach(menu::choose)

        assertEquals(EXPECTED_REPLACEMENTS, replacements)
    }

    @Test
    fun `choosing an unknown position keeps the selection`() {
        menu.choose(-1)
        menu.choose(EXPECTED_LABELS.size)

        assertTrue(replacements.isEmpty())
    }

    @Test
    fun `a single gamepad button sensor is a gamepad button sensor selection`() {
        GAMEPAD_BUTTON_SENSORS.forEach { sensor ->
            assertTrue(sensor.name, GamepadButtonSensorMenu.isGamepadButtonSensorSelection(listOf(sensorToken(sensor))))
        }
    }

    @Test
    fun `other selections are no gamepad button sensor selections`() {
        listOf(
            null,
            emptyList(),
            listOf(sensorToken(Sensors.GAMEPAD_A_PRESSED)),
            listOf(sensorToken(Sensors.USER_LANGUAGE)),
            listOf(InternToken(InternTokenType.NUMBER, "1")),
            listOf(InternToken(InternTokenType.USER_VARIABLE, Sensors.GAMEPAD_BUTTON_A_PRESSED.name)),
            listOf(InternToken(InternTokenType.STRING, Sensors.GAMEPAD_BUTTON_A_PRESSED.name)),
            listOf(
                sensorToken(Sensors.GAMEPAD_BUTTON_A_PRESSED),
                InternToken(InternTokenType.OPERATOR, "LOGICAL_AND"),
                sensorToken(Sensors.GAMEPAD_BUTTON_B_PRESSED)
            )
        ).forEach { selection ->
            assertFalse(selection.toString(), GamepadButtonSensorMenu.isGamepadButtonSensorSelection(selection))
        }
    }

    @Test
    fun `selecting a gamepad button sensor shows the menu with all buttons`() {
        val shown = menu.showIfGamepadButtonSensorSelected(activity, gamepadButtonSelection())
        idleMainLooper()

        assertTrue(shown)
        assertEquals(EXPECTED_LABELS, shownLabels())
    }

    @Test
    fun `choosing a button in the shown menu replaces the selection and closes the menu`() {
        menu.showIfGamepadButtonSensorSelected(activity, gamepadButtonSelection())
        idleMainLooper()

        clickMenuItem(UP_POSITION)

        assertEquals(listOf(R.string.formula_editor_sensor_gamepad_button_up_pressed), replacements)
        assertFalse(latestDialog().isShowing)
    }

    @Test
    fun `cancelling the menu keeps the selection`() {
        menu.showIfGamepadButtonSensorSelected(activity, gamepadButtonSelection())
        idleMainLooper()

        latestDialog().cancel()
        idleMainLooper()

        assertTrue(replacements.isEmpty())
    }

    @Test
    fun `menu is not shown for other selections`() {
        val shown = menu.showIfGamepadButtonSensorSelected(
            activity,
            listOf(sensorToken(Sensors.GAMEPAD_A_PRESSED))
        )
        idleMainLooper()

        assertFalse(shown)
        assertNull(ShadowDialog.getLatestDialog())
        assertTrue(replacements.isEmpty())
    }

    @Test
    fun `menu is not shown without a selection`() {
        val shown = menu.showIfGamepadButtonSensorSelected(activity, null)
        idleMainLooper()

        assertFalse(shown)
        assertNull(ShadowDialog.getLatestDialog())
    }

    private fun gamepadButtonSelection() = listOf(sensorToken(Sensors.GAMEPAD_BUTTON_A_PRESSED))

    private fun sensorToken(sensor: Sensors) = InternToken(InternTokenType.SENSOR, sensor.name)

    private fun latestDialog(): AlertDialog = ShadowDialog.getLatestDialog() as AlertDialog

    private fun shownLabels(): List<String> {
        val adapter = latestDialog().listView.adapter
        return (0 until adapter.count).map { position -> adapter.getItem(position).toString() }
    }

    private fun clickMenuItem(position: Int) {
        val listView = latestDialog().listView
        val itemView = listView.adapter.getView(position, null, listView)
        listView.performItemClick(itemView, position, listView.adapter.getItemId(position))
        idleMainLooper()
    }

    private fun idleMainLooper() = shadowOf(Looper.getMainLooper()).idle()

    private companion object {
        const val UP_POSITION = 5

        val EXPECTED_LABELS = listOf(
            "Any",
            "A", "B", "X", "Y",
            "Up", "Down", "Left", "Right",
            "Start", "Select",
            "L1", "L2", "R1", "R2"
        )

        val GAMEPAD_BUTTON_SENSORS = listOf(
            Sensors.GAMEPAD_BUTTON_ANY_PRESSED,
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

        val EXPECTED_REPLACEMENTS = listOf(
            R.string.formula_editor_sensor_gamepad_button_any_pressed,
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
    }
}
