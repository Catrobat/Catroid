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

package org.catrobat.catroid.test.robolectric.formulaeditor

import android.app.Activity
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListItems
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Integration test that verifies the completeness, order, naming, and headers
 * of all sensor items in the Formula Editor's DEVICE category.
 *
 * This replaces ~160 parameterized Espresso tests with a single fast
 * Robolectric test that calls [CategoryListItems.getSensorItems] directly.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FormulaEditorSensorListIntegrationTest {

    private lateinit var activity: Activity
    private var initialSettings = mutableMapOf<String, Boolean>()

    private val allShowBrickSettings: List<String> = listOf(
        SettingsFragment.SETTINGS_SHOW_ARDUINO_BRICKS,
        SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE,
        SettingsFragment.SETTINGS_SHOW_NFC_BRICKS,
        SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE,
        SettingsFragment.SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE,
        SettingsFragment.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS,
        SettingsFragment.SETTINGS_SHOW_RASPI_BRICKS,
        SettingsFragment.SETTINGS_SHOW_PLOT_BRICKS,
        SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED,
        SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
        SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
        SettingsFragment.SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
        SettingsFragment.SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
        SettingsFragment.SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS,
        SettingsFragment.SETTINGS_SHOW_AI_OBJECT_DETECTION_SENSORS
    )

    private fun str(resId: Int): String = activity.getString(resId)

    @Before
    fun setUp() {
        val application = RuntimeEnvironment.getApplication()

        // Settings MUST be set before activity setup
        saveInitialSettings()
        setAllShowBrickSettingsToTrue()

        // Mock SensorHandler to return true for all availability checks
        val mockSensorHandler = mock(SensorHandler::class.java)
        `when`(mockSensorHandler.accelerationAvailable()).thenReturn(true)
        `when`(mockSensorHandler.inclinationAvailable()).thenReturn(true)
        `when`(mockSensorHandler.compassAvailable()).thenReturn(true)

        // Inject into Koin (for components that use injection)
        loadKoinModules(module {
            single(override = true) { mockSensorHandler }
        })

        // Force the static instance (for components that use SensorHandler.getInstance())
        val instanceField = SensorHandler::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, mockSensorHandler)

        // setupActivity triggers Application.onCreate which starts Koin
        activity = Robolectric.setupActivity(AppCompatActivity::class.java)

        // Get the ProjectManager instance that Koin is using
        val projectManager = KoinJavaComponent.get(ProjectManager::class.java)

        val project = Project(application, "FormulaEditorSensorListIntegrationTest", false, true)
        val sprite = Sprite("testSprite")
        val script = StartScript()
        sprite.addScript(script)
        project.getDefaultScene().addSprite(sprite)

        projectManager.setCurrentProject(project)
        projectManager.setCurrentSprite(sprite)
        projectManager.setCurrentlyEditedScene(project.getDefaultScene())
    }

    @After
    fun tearDown() {
        restoreInitialSettings()

        // Reset SensorHandler static instance
        try {
            val instanceField = SensorHandler::class.java.getDeclaredField("instance")
            instanceField.isAccessible = true
            instanceField.set(null, null)
        } catch (ignored: Exception) {
            // Ignore clean up errors
        }

        ProjectManager.getInstance().resetProjectManager()
    }

    /**
     * Builds the expected list of (name, header?) pairs matching the production
     * [CategoryListItems.getSensorItems] output, then asserts every item matches.
     */
    @Test
    fun testAllSensorListItemsCompleteAndOrdered() {
        val expected = buildExpectedSensorList()
        val categoryListItems = CategoryListItems()
        val actual = categoryListItems.getSensorItems(activity)
        val actualPairs = actual.map { it.text to it.header }

        if (expected.size != actual.size) {
            println("--- SENSOR LIST MISMATCH DIFF ---")
            println("Expected size: ${expected.size}, Actual size: ${actual.size}")

            val expectedNames = expected.map { it.first }.toSet()
            val actualNames = actualPairs.map { it.first }.toSet()

            println("Missing from Actual: ${expectedNames - actualNames}")
            println("Extra in Actual: ${actualNames - expectedNames}")
            println("---------------------------------")
        }

        assertEquals(
            "Sensor list size mismatch. Missing: ${expected.size - actual.size} items.",
            expected, actualPairs
        )

        expected.forEachIndexed { index, (expectedName, expectedHeader) ->
            val item = actual[index]
            assertEquals(
                "Sensor name mismatch at index $index (expected: $expectedName)",
                expectedName, item.text
            )
            if (expectedHeader != null) {
                assertNotNull(
                    "Expected header '$expectedHeader' at index $index but was null",
                    item.header
                )
                assertEquals(
                    "Header mismatch at index $index",
                    expectedHeader, item.header
                )
            } else {
                assertNull(
                    "Expected no header at index $index but found '${item.header}'",
                    item.header
                )
            }
        }
    }

    @Test
    fun testSensorListIsNotEmpty() {
        val categoryListItems = CategoryListItems()
        val sensorItems = categoryListItems.getSensorItems(activity)
        assert(sensorItems.isNotEmpty()) { "Sensor list should not be empty" }
    }

    @Test
    fun testFirstItemInEachSectionHasHeader() {
        val categoryListItems = CategoryListItems()
        val sensorItems = categoryListItems.getSensorItems(activity)

        val expectedHeaders = listOf(
            str(R.string.formula_editor_device_lego_nxt),
            str(R.string.formula_editor_device_lego_ev3),
            str(R.string.formula_editor_device_phiro),
            str(R.string.formula_editor_device_arduino),
            str(R.string.formula_editor_device_drone),
            str(R.string.formula_editor_device_raspberry),
            str(R.string.formula_editor_device_nfc),
            str(R.string.formula_editor_device_cast),
            str(R.string.formula_editor_speech_recognition),
            str(R.string.formula_editor_device_face_detection),
            str(R.string.formula_editor_device_pose_detection),
            str(R.string.formula_editor_device_text_recognition),
            str(R.string.formula_editor_device_object_recognition),
            str(R.string.formula_editor_device_sensors),
            str(R.string.formula_editor_device_touch_detection),
            str(R.string.formula_editor_device_date_and_time)
        )

        val actualHeaders = sensorItems.mapNotNull { it.header }
        assertEquals(
            "Number of section headers mismatch",
            expectedHeaders.size, actualHeaders.size
        )
        expectedHeaders.forEachIndexed { i, expected ->
            assertEquals("Header $i mismatch", expected, actualHeaders[i])
        }
    }

    // Build expected sensor list - used for verification against CategoryListItems.getSensorItems
    private fun buildExpectedSensorList(): List<Pair<String, String?>> {
        val items = mutableListOf<Pair<String, String?>>()

        // NXT
        items.addAll(buildNxtExpected())
        // EV3
        items.addAll(buildEv3Expected())
        // Phiro
        items.addAll(buildPhiroExpected())
        // Arduino
        items.addAll(buildArduinoExpected())
        // Drone
        items.addAll(buildDroneExpected())
        // Raspberry
        items.addAll(buildRaspberryExpected())
        // NFC
        items.addAll(buildNfcExpected())
        // Cast
        items.addAll(buildCastExpected())
        // Speech Recognition
        items.addAll(buildSpeechExpected())
        // Face Detection
        items.addAll(buildFaceDetectionExpected())
        // Pose Detection
        items.addAll(buildPoseDetectionExpected())
        // Text Recognition
        items.addAll(buildTextRecognitionExpected())
        // Object Detection
        items.addAll(buildObjectDetectionExpected())
        // Device Sensors
        items.addAll(buildDeviceSensorsExpected())
        // Touch Detection
        items.addAll(buildTouchDetectionExpected())
        // Date/Time
        items.addAll(buildDateTimeExpected())

        return items
    }

    private fun buildNxtExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_lego_nxt)
        return listOf(
            str(R.string.formula_editor_sensor_lego_nxt_touch) to h,
            str(R.string.formula_editor_sensor_lego_nxt_sound) to null,
            str(R.string.formula_editor_sensor_lego_nxt_light) to null,
            str(R.string.formula_editor_sensor_lego_nxt_light_active) to null,
            str(R.string.formula_editor_sensor_lego_nxt_ultrasonic) to null
        )
    }

    private fun buildEv3Expected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_lego_ev3)
        return listOf(
            str(R.string.formula_editor_sensor_lego_ev3_sensor_touch) to h,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_infrared) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_color) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light_active) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_sound) to null,
            str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_ultrasonic) to null
        )
    }

    private fun buildPhiroExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_phiro)
        return listOf(
            str(R.string.formula_editor_phiro_sensor_front_left) to h,
            str(R.string.formula_editor_phiro_sensor_front_right) to null,
            str(R.string.formula_editor_phiro_sensor_side_left) to null,
            str(R.string.formula_editor_phiro_sensor_side_right) to null,
            str(R.string.formula_editor_phiro_sensor_bottom_left) to null,
            str(R.string.formula_editor_phiro_sensor_bottom_right) to null
        )
    }

    private fun buildArduinoExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_arduino)
        val p = str(R.string.formula_editor_function_pin_default_parameter)
        return listOf(
            (str(R.string.formula_editor_function_arduino_read_pin_value_analog) + p) to h,
            (str(R.string.formula_editor_function_arduino_read_pin_value_digital) + p) to null
        )
    }

    private fun buildDroneExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_drone)
        return listOf(
            str(R.string.formula_editor_sensor_drone_battery_status) to h,
            str(R.string.formula_editor_sensor_drone_emergency_state) to null,
            str(R.string.formula_editor_sensor_drone_flying) to null,
            str(R.string.formula_editor_sensor_drone_initialized) to null,
            str(R.string.formula_editor_sensor_drone_usb_active) to null,
            str(R.string.formula_editor_sensor_drone_usb_remaining_time) to null,
            str(R.string.formula_editor_sensor_drone_camera_ready) to null,
            str(R.string.formula_editor_sensor_drone_record_ready) to null,
            str(R.string.formula_editor_sensor_drone_recording) to null,
            str(R.string.formula_editor_sensor_drone_num_frames) to null
        )
    }

    private fun buildRaspberryExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_raspberry)
        val p = str(R.string.formula_editor_function_pin_default_parameter)
        return listOf(
            (str(R.string.formula_editor_function_raspi_read_pin_value_digital) + p) to h
        )
    }

    private fun buildNfcExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_nfc)
        return listOf(
            str(R.string.formula_editor_nfc_tag_id) to h,
            str(R.string.formula_editor_nfc_tag_message) to null
        )
    }

    private fun buildCastExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_cast)
        return listOf(
            str(R.string.formula_editor_sensor_gamepad_a_pressed) to h,
            str(R.string.formula_editor_sensor_gamepad_b_pressed) to null,
            str(R.string.formula_editor_sensor_gamepad_up_pressed) to null,
            str(R.string.formula_editor_sensor_gamepad_down_pressed) to null,
            str(R.string.formula_editor_sensor_gamepad_left_pressed) to null,
            str(R.string.formula_editor_sensor_gamepad_right_pressed) to null
        )
    }

    private fun buildSpeechExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_speech_recognition)
        return listOf(
            str(R.string.formula_editor_listening_language_sensor) to h
        )
    }

    private fun buildFaceDetectionExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_face_detection)
        val np = str(R.string.formula_editor_function_no_parameter)
        return listOf(
            (str(R.string.formula_editor_sensor_face_detected) + np) to h,
            (str(R.string.formula_editor_sensor_face_size) + np) to null,
            (str(R.string.formula_editor_sensor_face_x_position) + np) to null,
            (str(R.string.formula_editor_sensor_face_y_position) + np) to null,
            (str(R.string.formula_editor_sensor_second_face_detected) + np) to null,
            (str(R.string.formula_editor_sensor_second_face_size) + np) to null,
            (str(R.string.formula_editor_sensor_second_face_x_position) + np) to null,
            (str(R.string.formula_editor_sensor_second_face_y_position) + np) to null
        )
    }

    private fun buildPoseDetectionExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_pose_detection)
        val np = str(R.string.formula_editor_function_no_parameter)
        val sensorResIds = listOf(
            R.string.formula_editor_sensor_nose_x,
            R.string.formula_editor_sensor_nose_y,
            R.string.formula_editor_sensor_left_eye_inner_x,
            R.string.formula_editor_sensor_left_eye_inner_y,
            R.string.formula_editor_sensor_left_eye_center_x,
            R.string.formula_editor_sensor_left_eye_center_y,
            R.string.formula_editor_sensor_left_eye_outer_x,
            R.string.formula_editor_sensor_left_eye_outer_y,
            R.string.formula_editor_sensor_right_eye_inner_x,
            R.string.formula_editor_sensor_right_eye_inner_y,
            R.string.formula_editor_sensor_right_eye_center_x,
            R.string.formula_editor_sensor_right_eye_center_y,
            R.string.formula_editor_sensor_right_eye_outer_x,
            R.string.formula_editor_sensor_right_eye_outer_y,
            R.string.formula_editor_sensor_left_ear_x,
            R.string.formula_editor_sensor_left_ear_y,
            R.string.formula_editor_sensor_right_ear_x,
            R.string.formula_editor_sensor_right_ear_y,
            R.string.formula_editor_sensor_mouth_left_corner_x,
            R.string.formula_editor_sensor_mouth_left_corner_y,
            R.string.formula_editor_sensor_mouth_right_corner_x,
            R.string.formula_editor_sensor_mouth_right_corner_y,
            R.string.formula_editor_sensor_left_shoulder_x,
            R.string.formula_editor_sensor_left_shoulder_y,
            R.string.formula_editor_sensor_right_shoulder_x,
            R.string.formula_editor_sensor_right_shoulder_y,
            R.string.formula_editor_sensor_left_elbow_x,
            R.string.formula_editor_sensor_left_elbow_y,
            R.string.formula_editor_sensor_right_elbow_x,
            R.string.formula_editor_sensor_right_elbow_y,
            R.string.formula_editor_sensor_left_wrist_x,
            R.string.formula_editor_sensor_left_wrist_y,
            R.string.formula_editor_sensor_right_wrist_x,
            R.string.formula_editor_sensor_right_wrist_y,
            R.string.formula_editor_sensor_left_pinky_knuckle_x,
            R.string.formula_editor_sensor_left_pinky_knuckle_y,
            R.string.formula_editor_sensor_right_pinky_knuckle_x,
            R.string.formula_editor_sensor_right_pinky_knuckle_y,
            R.string.formula_editor_sensor_left_index_knuckle_x,
            R.string.formula_editor_sensor_left_index_knuckle_y,
            R.string.formula_editor_sensor_right_index_knuckle_x,
            R.string.formula_editor_sensor_right_index_knuckle_y,
            R.string.formula_editor_sensor_left_thumb_knuckle_x,
            R.string.formula_editor_sensor_left_thumb_knuckle_y,
            R.string.formula_editor_sensor_right_thumb_knuckle_x,
            R.string.formula_editor_sensor_right_thumb_knuckle_y,
            R.string.formula_editor_sensor_left_hip_x,
            R.string.formula_editor_sensor_left_hip_y,
            R.string.formula_editor_sensor_right_hip_x,
            R.string.formula_editor_sensor_right_hip_y,
            R.string.formula_editor_sensor_left_knee_x,
            R.string.formula_editor_sensor_left_knee_y,
            R.string.formula_editor_sensor_right_knee_x,
            R.string.formula_editor_sensor_right_knee_y,
            R.string.formula_editor_sensor_left_ankle_x,
            R.string.formula_editor_sensor_left_ankle_y,
            R.string.formula_editor_sensor_right_ankle_x,
            R.string.formula_editor_sensor_right_ankle_y,
            R.string.formula_editor_sensor_left_heel_x,
            R.string.formula_editor_sensor_left_heel_y,
            R.string.formula_editor_sensor_right_heel_x,
            R.string.formula_editor_sensor_right_heel_y,
            R.string.formula_editor_sensor_left_foot_index_x,
            R.string.formula_editor_sensor_left_foot_index_y,
            R.string.formula_editor_sensor_right_foot_index_x,
            R.string.formula_editor_sensor_right_foot_index_y
        )
        return sensorResIds.mapIndexed { i, resId ->
            (str(resId) + np) to (if (i == 0) h else null)
        }
    }

    private fun buildTextRecognitionExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_text_recognition)
        val np = str(R.string.formula_editor_function_no_parameter)
        val bp = str(R.string.formula_editor_function_text_block_parameter)
        return listOf(
            (str(R.string.formula_editor_sensor_text_from_camera) + np) to h,
            (str(R.string.formula_editor_sensor_text_blocks_number) + np) to null,
            (str(R.string.formula_editor_function_text_block_x) + bp) to null,
            (str(R.string.formula_editor_function_text_block_y) + bp) to null,
            (str(R.string.formula_editor_function_text_block_size) + bp) to null,
            (str(R.string.formula_editor_function_text_block_from_camera) + bp) to null,
            (str(R.string.formula_editor_function_text_block_language_from_camera) + bp) to null
        )
    }

    private fun buildObjectDetectionExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_object_recognition)
        return listOf(
            str(R.string.formula_editor_function_get_id_of_detected_object) to h,
            str(R.string.formula_editor_function_object_with_id_visible) to null
        )
    }

    private fun buildDeviceSensorsExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_sensors)
        val items = mutableListOf<Pair<String, String?>>()
        items.add(str(R.string.formula_editor_sensor_loudness) to h)
        items.add(str(R.string.formula_editor_function_touched) to null)
        items.add(str(R.string.formula_editor_sensor_stage_width) to null)
        items.add(str(R.string.formula_editor_sensor_stage_height) to null)
        items.add((str(R.string.formula_editor_sensor_color_at_x_y) +
            str(R.string.formula_editor_sensor_color_at_x_y_parameter)) to null)
        items.add((str(R.string.formula_editor_sensor_color_equals_color) +
            str(R.string.formula_editor_sensor_color_equals_color_parameter)) to null)
        items.add(str(R.string.formula_editor_sensor_x_acceleration) to null)
        items.add(str(R.string.formula_editor_sensor_y_acceleration) to null)
        items.add(str(R.string.formula_editor_sensor_z_acceleration) to null)
        items.add(str(R.string.formula_editor_sensor_x_inclination) to null)
        items.add(str(R.string.formula_editor_sensor_y_inclination) to null)
        items.add(str(R.string.formula_editor_sensor_compass_direction) to null)
        items.add(str(R.string.formula_editor_sensor_latitude) to null)
        items.add(str(R.string.formula_editor_sensor_longitude) to null)
        items.add(str(R.string.formula_editor_sensor_location_accuracy) to null)
        items.add(str(R.string.formula_editor_sensor_altitude) to null)
        items.add(str(R.string.formula_editor_sensor_user_language) to null)
        return items
    }

    private fun buildTouchDetectionExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_touch_detection)
        val np = str(R.string.formula_editor_function_no_parameter)
        val tp = str(R.string.formula_editor_function_touch_parameter)
        return listOf(
            (str(R.string.formula_editor_function_finger_x) + np) to h,
            (str(R.string.formula_editor_function_finger_y) + np) to null,
            (str(R.string.formula_editor_function_is_finger_touching) + np) to null,
            (str(R.string.formula_editor_function_multi_finger_x) + tp) to null,
            (str(R.string.formula_editor_function_multi_finger_y) + tp) to null,
            (str(R.string.formula_editor_function_is_multi_finger_touching) + tp) to null,
            (str(R.string.formula_editor_function_index_of_last_finger) + np) to null,
            (str(R.string.formula_editor_function_number_of_current_touches) + np) to null,
            (str(R.string.formula_editor_function_index_of_current_touch) + tp) to null
        )
    }

    private fun buildDateTimeExpected(): List<Pair<String, String?>> {
        val h = str(R.string.formula_editor_device_date_and_time)
        return listOf(
            str(R.string.formula_editor_sensor_timer) to h,
            str(R.string.formula_editor_sensor_date_year) to null,
            str(R.string.formula_editor_sensor_date_month) to null,
            str(R.string.formula_editor_sensor_date_day) to null,
            str(R.string.formula_editor_sensor_date_weekday) to null,
            str(R.string.formula_editor_sensor_time_hour) to null,
            str(R.string.formula_editor_sensor_time_minute) to null,
            str(R.string.formula_editor_sensor_time_second) to null
        )
    }

    // Settings helpers

    private fun saveInitialSettings() {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
        allShowBrickSettings.forEach { setting ->
            initialSettings[setting] = prefs.getBoolean(setting, false)
        }
    }

    private fun restoreInitialSettings() {
        val editor = PreferenceManager
            .getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit()
        allShowBrickSettings.forEach { setting ->
            editor.putBoolean(setting, initialSettings[setting] ?: false)
        }
        editor.commit()
    }

    private fun setAllShowBrickSettingsToTrue() {
        val editor = PreferenceManager
            .getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit()
        allShowBrickSettings.forEach { setting ->
            editor.putBoolean(setting, true)
        }
        editor.commit()
    }
}
