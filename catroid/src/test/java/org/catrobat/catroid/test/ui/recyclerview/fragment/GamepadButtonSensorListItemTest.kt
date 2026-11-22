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

package org.catrobat.catroid.test.ui.recyclerview.fragment

import android.app.Activity
import android.os.Build
import android.preference.PreferenceManager
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItem
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListItems
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GamepadButtonSensorListItemTest {
    private lateinit var activity: Activity

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val project = Project(activity, javaClass.simpleName)
        val projectManager = KoinJavaComponent.get(ProjectManager::class.java)
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
    }

    @Test
    fun `device sensors end with the gamepad button sensor when the setting is on`() {
        setPhysicalGamepadSetting(true)

        val gamepadItem = deviceSensorItems().last()

        assertEquals(R.string.formula_editor_sensor_gamepad_button_a_pressed, gamepadItem.nameResId)
        assertEquals("gamepad button A▾pressed", gamepadItem.text)
        assertEquals(CategoryListRVAdapter.DEFAULT, gamepadItem.type)
        assertNull(gamepadItem.header)
    }

    @Test
    fun `gamepad button sensor follows user language when the setting is on`() {
        setPhysicalGamepadSetting(true)

        val resIds = deviceSensorItems().map { it.nameResId }

        assertEquals(
            listOf(
                R.string.formula_editor_sensor_user_language,
                R.string.formula_editor_sensor_gamepad_button_a_pressed
            ),
            resIds.takeLast(2)
        )
    }

    @Test
    fun `sensor list offers exactly one gamepad button sensor when the setting is on`() {
        setPhysicalGamepadSetting(true)

        val gamepadItems = sensorItems().filter { it.nameResId in GAMEPAD_BUTTON_SENSOR_NAME_RES_IDS }

        assertEquals(
            listOf(R.string.formula_editor_sensor_gamepad_button_a_pressed),
            gamepadItems.map { it.nameResId }
        )
    }

    @Test
    fun `sensor list offers no gamepad button sensor when the setting is off`() {
        setPhysicalGamepadSetting(false)

        assertTrue(sensorItems().none { it.nameResId in GAMEPAD_BUTTON_SENSOR_NAME_RES_IDS })
        assertEquals(R.string.formula_editor_sensor_user_language, deviceSensorItems().last().nameResId)
    }

    @Test
    fun `sensor list offers no gamepad button sensor by default`() {
        assertTrue(sensorItems().none { it.nameResId in GAMEPAD_BUTTON_SENSOR_NAME_RES_IDS })
    }

    private fun setPhysicalGamepadSetting(enabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(activity.applicationContext).edit()
            .putBoolean(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, enabled)
            .commit()
    }

    private fun sensorItems(): List<CategoryListItem> = CategoryListItems().getSensorItems(activity)

    /** Returns the items of the "Device sensors" section, from its header up to the next header. */
    private fun deviceSensorItems(): List<CategoryListItem> {
        val items = sensorItems()
        val deviceHeader = activity.getString(R.string.formula_editor_device_sensors)
        val start = items.indexOfFirst { it.header == deviceHeader }
        val afterStart = items.drop(start + 1)
        val length = afterStart.indexOfFirst { it.header != null }.takeIf { it >= 0 } ?: afterStart.size
        return items.subList(start, start + 1 + length)
    }

    private companion object {
        val GAMEPAD_BUTTON_SENSOR_NAME_RES_IDS = setOf(
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
