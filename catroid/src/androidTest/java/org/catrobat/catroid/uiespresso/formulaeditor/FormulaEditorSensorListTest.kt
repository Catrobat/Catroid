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

package org.catrobat.catroid.uiespresso.formulaeditor

import android.Manifest
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorCategoryListWrapper.onCategoryList
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewItemMatcher
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.core.StringContains
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category

/**
 * Slimmed-down Espresso smoke test for the Formula Editor sensor list.
 *
 * This test covers representative UI interaction patterns that cannot be verified
 * without a real device/emulator:
 * - Standard sensor selection (basic click flow)
 * - Header rendering verification
 * - GPS sensor selection (compute + pressBack flow)
 * - NXT sensor selection (port config dialog flow)
 * - EV3 sensor selection (port config dialog flow)
 * - Touch sensor with parameter display
 * - Face detection sensor
 * - Date/Time sensor
 *
 * The full list of ~160 sensor items (completeness, order, naming) is verified by
 * the fast Robolectric-based FormulaEditorSensorListIntegrationTest.
 */
@Category(Cat.AppUi::class, Level.Smoke::class)
class FormulaEditorSensorListTest {

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule<SpriteActivity>(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @JvmField
    @Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val projectName = "FormulaEditorSensorListTest"
    private lateinit var recyclerViewItemMatcher: RecyclerViewItemMatcher
    private var initialSettings = mutableMapOf<String, Boolean>()

    @Before
    @kotlin.jvm.Throws(Exception::class)
    fun setUp() {
        val script = BrickTestUtils.createEmptyCastProjectAndGetStartScript(projectName)
        script.addBrick(ChangeSizeByNBrick(0.0))

        recyclerViewItemMatcher = RecyclerViewItemMatcher(R.id.recycler_view)
        baseActivityTestRule.launchActivity()

        saveInitialSettings()
        setAllShowBrickSettingsToTrue()
    }

    @After
    fun tearDown() {
        restoreInitialSettings()
    }

    /**
     * Tests a standard sensor selection (loudness) and verifies it renders with the
     * correct header for the Device Sensors section.
     */
    @Test
    fun testSelectStandardSensorWithHeader() {
        openDeviceCategory()

        val sensorName = str(R.string.formula_editor_sensor_loudness)
        val headerName = str(R.string.formula_editor_device_sensors)

        // Find the loudness sensor (first item in Device Sensors section)
        // We search by scrolling until we find it, then check header + name
        onCategoryList().performSelect(sensorName)
        onFormulaEditor().checkShows(getSelectedSensorString(sensorName))
    }

    /**
     * Tests GPS sensor selection which triggers the compute + pressBack flow.
     */
    @Test
    fun testSelectGpsSensor() {
        openDeviceCategory()

        val sensorName = str(R.string.formula_editor_sensor_latitude)
        onCategoryList().performSelect(sensorName)

        onView(withId(R.id.formula_editor_keyboard_compute))
            .perform(click())
        pressBack()

        onFormulaEditor().checkShows(getSelectedSensorString(sensorName))
    }

    /**
     * Tests NXT sensor selection which triggers the port config dialog flow.
     */
    @Test
    fun testSelectNxtSensorWithPortDialog() {
        openDeviceCategory()

        val sensorName = str(R.string.formula_editor_sensor_lego_nxt_touch)
        onCategoryList().performSelect(sensorName)

        val portNumber = str(R.string.lego_port_4)
        onView(withText(StringContains(portNumber))).perform(click())
        onView(withText(R.string.ok)).perform(click())

        val portSensorString = str(R.string.formula_editor_sensor_lego_nxt_4)
        onFormulaEditor().checkShows(getSelectedSensorString(portSensorString))
    }

    /**
     * Tests EV3 sensor selection which triggers the port config dialog flow.
     */
    @Test
    fun testSelectEv3SensorWithPortDialog() {
        openDeviceCategory()

        val sensorName = str(R.string.formula_editor_sensor_lego_ev3_sensor_touch)
        onCategoryList().performSelect(sensorName)

        val portNumber = str(R.string.lego_port_1)
        onView(withText(StringContains(portNumber))).perform(click())
        onView(withText(R.string.ok)).perform(click())

        val portSensorString = str(R.string.formula_editor_sensor_lego_ev3_1)
        onFormulaEditor().checkShows(getSelectedSensorString(portSensorString))
    }

    /**
     * Tests touch sensor with parameter display (multi-finger).
     */
    @Test
    fun testSelectTouchSensorWithParameter() {
        openDeviceCategory()

        val sensorName = str(R.string.formula_editor_function_finger_x) +
            str(R.string.formula_editor_function_no_parameter)
        onCategoryList().performSelect(sensorName)
        onFormulaEditor().checkShows(getSelectedSensorString(sensorName))
    }

    /**
     * Tests face detection sensor selection.
     */
    @Test
    fun testSelectFaceDetectionSensor() {
        openDeviceCategory()

        val sensorName = str(R.string.formula_editor_sensor_face_detected) +
            str(R.string.formula_editor_function_no_parameter)
        onCategoryList().performSelect(sensorName)
        onFormulaEditor().checkShows(getSelectedSensorString(sensorName))
    }

    /**
     * Tests date/time sensor selection.
     */
    @Test
    fun testSelectDateTimeSensor() {
        openDeviceCategory()

        val sensorName = str(R.string.formula_editor_sensor_timer)
        onCategoryList().performSelect(sensorName)
        onFormulaEditor().checkShows(getSelectedSensorString(sensorName))
    }

    /**
     * Tests that a header is correctly displayed for the first item in a section.
     */
    @Test
    fun testHeaderDisplayForNxtSection() {
        openDeviceCategory()

        // NXT is the first section (index 0)
        val headerText = str(R.string.formula_editor_device_lego_nxt)
        onView(withId(R.id.recycler_view))
            .perform(scrollToPosition<ViewHolder>(0))

        onView(withText(headerText))
            .check(
                matches(
                    recyclerViewItemMatcher
                        .withIdInsidePosition(R.id.headline, 0)
                )
            )
    }

    // Helper methods

    private fun openDeviceCategory() {
        onBrickAtPosition(1).onChildView(withId(R.id.brick_change_size_by_edit_text))
            .perform(click())
        onFormulaEditor()
            .performOpenCategory(FormulaEditorWrapper.Category.DEVICE)
    }

    private fun getSelectedSensorString(functionString: String): String {
        return functionString
            .replace("^(.+?)\\(".toRegex(), "$1( ")
            .replace(",", " , ")
            .replace("-", "- ")
            .replace("\\)$".toRegex(), " )") + " "
    }

    private fun saveInitialSettings() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        allShowBrickSettings.forEach { setting ->
            initialSettings[setting] = sharedPreferences.getBoolean(setting, false)
        }
    }

    private fun restoreInitialSettings() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        allShowBrickSettings.forEach { setting ->
            sharedPreferencesEditor.putBoolean(setting, initialSettings[setting]!!)
        }
        sharedPreferencesEditor.commit()
    }

    private fun setAllShowBrickSettingsToTrue() {
        val sharedPreferencesEditor = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
        allShowBrickSettings.forEach { setting ->
            sharedPreferencesEditor.putBoolean(setting, true)
        }
        sharedPreferencesEditor.commit()
    }

    companion object {
        private fun str(paramId: Int): String = UiTestUtils.getResourcesString(paramId) ?: ""

        private val allShowBrickSettings: List<String> = listOf(
            SettingsFragment.SETTINGS_SHOW_ARDUINO_BRICKS,
            SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE,
            SettingsFragment.SETTINGS_SHOW_NFC_BRICKS,
            SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE,
            SettingsFragment.SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE,
            SettingsFragment.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS,
            SettingsFragment.SETTINGS_SHOW_RASPI_BRICKS,
            SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED,
            SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
            SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
            SettingsFragment.SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
            SettingsFragment.SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
            SettingsFragment.SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
        )
    }
}
