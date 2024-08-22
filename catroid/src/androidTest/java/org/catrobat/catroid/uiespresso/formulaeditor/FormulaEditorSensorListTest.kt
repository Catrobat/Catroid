/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(Parameterized::class)
class FormulaEditorSensorListTest(
    index: String,
    name: String,
    param: String,
    private var sensorHeader: String
) {
    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule<SpriteActivity>(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    private val itemIndex: Int = index.toInt()
    private var sensorName: String = name + param
    private var isHeader = sensorHeader.isNotBlank()

    private var sensorNumber: String? = null

    private val projectName = "FormulaEditorSensorListTest"
    private lateinit var recyclerViewItemMatcher: RecyclerViewItemMatcher
    private var initialSettings = mutableMapOf<String, Boolean>()

    init {
        if (sensorName.contains("^(NXT|EV3)\\s?.*".toRegex())) {
            sensorName = name
            sensorNumber = param
        }
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

    @Test
    fun testSensorListElements() {
        onBrickAtPosition(1).onChildView(withId(R.id.brick_change_size_by_edit_text))
            .perform(click())

        onFormulaEditor()
            .performOpenCategory(FormulaEditorWrapper.Category.DEVICE)

        onView(withId(R.id.recycler_view))
            .perform(scrollToPosition<ViewHolder>(itemIndex))

        if (isHeader) {
            onView(withText(sensorHeader))
                .check(
                    matches(
                        recyclerViewItemMatcher
                            .withIdInsidePosition(R.id.headline, itemIndex)
                    )
                )
        }

        onView(withText(sensorName))
            .check(
                matches(
                    recyclerViewItemMatcher
                        .withIdInsidePosition(R.id.title_view, itemIndex)
                )
            )

        onCategoryList().performSelect(sensorName)
        sensorNumber?.let { sensor ->
            val portNumber = when (sensor.trim()[sensor.length - 1]) {
                '1' -> str(R.string.lego_port_1)
                '2' -> str(R.string.lego_port_2)
                '3' -> str(R.string.lego_port_3)
                '4' -> str(R.string.lego_port_4)
                else -> "Error in port number string"
            }
            onView(withText(StringContains(portNumber))).perform(click())
            onView(withText(R.string.ok)).perform(click())

            onFormulaEditor().checkShows(getSelectedSensorString(sensor))
            true
        } ?: onFormulaEditor().checkShows(getSelectedSensorString(sensorName))
    }

    private fun getSelectedSensorString(functionString: String): String {
        return functionString
            .replace("^(.+?)\\(".toRegex(), "$1( ")
            .replace(",", " , ")
            .replace("-", "- ")
            .replace("\\)$".toRegex(), " )") + " "
    }

    @Suppress("LargeClass")
    companion object {
        private fun str(paramId: Int): String = UiTestUtils.getResourcesString(paramId) ?: ""

        @JvmStatic
        @Parameterized.Parameters(name = "{3}\n{index}. {1}")
        fun params() = arrayListOf<Array<String>>().run {
            val paramsData = mutableListOf<List<String>>()
            paramsData.addAll(listOfNXT)
            paramsData.addAll(listOfEV3)
            paramsData.addAll(listOfPhiro)
            paramsData.addAll(listOfArduino)
            paramsData.addAll(listOfDrone)
            paramsData.addAll(listOfRaspberry)
            paramsData.addAll(listOfNFC)
            paramsData.addAll(listOfCast)
            paramsData.addAll(listOfSpeech)
            paramsData.addAll(listOfFaceDetection)
            paramsData.addAll(listOfPoseDetection)
            paramsData.addAll(listOfTextRecognition)
            paramsData.addAll(listOfDevice)
            paramsData.addAll(listOfTouch)
            paramsData.addAll(listOfDateTime)

            paramsData.forEachIndexed { index, sensor ->
                this.add(arrayListOf(index.toString()).run {
                    this.addAll(sensor)
                    this.toTypedArray()
                })
            }

            this
        }

        private val listOfNXT = listOf(
            listOf(
                str(R.string.formula_editor_sensor_lego_nxt_touch),
                str(R.string.formula_editor_sensor_lego_nxt_4),
                str(R.string.formula_editor_device_lego_nxt)
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_nxt_sound),
                str(R.string.formula_editor_sensor_lego_nxt_1), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_nxt_light),
                str(R.string.formula_editor_sensor_lego_nxt_2), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_nxt_light_active),
                str(R.string.formula_editor_sensor_lego_nxt_3), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_nxt_ultrasonic),
                str(R.string.formula_editor_sensor_lego_nxt_3), ""
            )
        )

        private val listOfEV3 = listOf(
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_touch),
                str(R.string.formula_editor_sensor_lego_ev3_1),
                str(R.string.formula_editor_device_lego_ev3)
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_infrared),
                str(R.string.formula_editor_sensor_lego_ev3_2), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_color),
                str(R.string.formula_editor_sensor_lego_ev3_3), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient),
                str(R.string.formula_editor_sensor_lego_ev3_4), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected),
                str(R.string.formula_editor_sensor_lego_ev3_1), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color),
                str(R.string.formula_editor_sensor_lego_ev3_2), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c),
                str(R.string.formula_editor_sensor_lego_ev3_3), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f),
                str(R.string.formula_editor_sensor_lego_ev3_4), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light),
                str(R.string.formula_editor_sensor_lego_ev3_1), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light_active),
                str(R.string.formula_editor_sensor_lego_ev3_2), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_sound),
                str(R.string.formula_editor_sensor_lego_ev3_3), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_lego_ev3_sensor_nxt_ultrasonic),
                str(R.string.formula_editor_sensor_lego_ev3_4), ""
            )
        )

        private val listOfPhiro = listOf(
            listOf(
                str(R.string.formula_editor_phiro_sensor_front_left),
                "", str(R.string.formula_editor_device_phiro)
            ),
            listOf(str(R.string.formula_editor_phiro_sensor_front_right), "", ""),
            listOf(str(R.string.formula_editor_phiro_sensor_side_left), "", ""),
            listOf(str(R.string.formula_editor_phiro_sensor_side_right), "", ""),
            listOf(str(R.string.formula_editor_phiro_sensor_bottom_left), "", ""),
            listOf(str(R.string.formula_editor_phiro_sensor_bottom_right), "", "")
        )

        private val listOfArduino = listOf(
            listOf(
                str(R.string.formula_editor_function_arduino_read_pin_value_analog),
                str(R.string.formula_editor_function_pin_default_parameter),
                str(R.string.formula_editor_device_arduino)
            ),
            listOf(
                str(R.string.formula_editor_function_arduino_read_pin_value_digital),
                str(R.string.formula_editor_function_pin_default_parameter), ""
            )
        )

        private val listOfDrone = listOf(
            listOf(
                str(R.string.formula_editor_sensor_drone_battery_status),
                "", str(R.string.formula_editor_device_drone)
            ),
            listOf(str(R.string.formula_editor_sensor_drone_emergency_state), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_flying), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_initialized), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_usb_active), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_usb_remaining_time), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_camera_ready), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_record_ready), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_recording), "", ""),
            listOf(str(R.string.formula_editor_sensor_drone_num_frames), "", "")
        )

        private val listOfRaspberry = listOf(
            listOf(
                str(R.string.formula_editor_function_raspi_read_pin_value_digital),
                str(R.string.formula_editor_function_pin_default_parameter),
                str(R.string.formula_editor_device_raspberry)
            )
        )

        private val listOfNFC = listOf(
            listOf(
                str(R.string.formula_editor_nfc_tag_id),
                "", str(R.string.formula_editor_device_nfc)
            ),
            listOf(str(R.string.formula_editor_nfc_tag_message), "", "")
        )

        private val listOfCast = listOf(
            listOf(
                str(R.string.formula_editor_sensor_gamepad_a_pressed),
                "", str(R.string.formula_editor_device_cast)
            ),
            listOf(str(R.string.formula_editor_sensor_gamepad_b_pressed), "", ""),
            listOf(str(R.string.formula_editor_sensor_gamepad_up_pressed), "", ""),
            listOf(str(R.string.formula_editor_sensor_gamepad_down_pressed), "", ""),
            listOf(str(R.string.formula_editor_sensor_gamepad_left_pressed), "", ""),
            listOf(str(R.string.formula_editor_sensor_gamepad_right_pressed), "", "")
        )

        private val listOfSpeech = listOf(
            listOf(
                str(R.string.formula_editor_listening_language_sensor),
                "", str(R.string.formula_editor_speech_recognition)
            )
        )

        private val listOfFaceDetection = listOf(
            listOf(
                str(R.string.formula_editor_sensor_face_detected),
                str(R.string.formula_editor_function_no_parameter),
                str(R.string.formula_editor_device_face_detection)
            ),
            listOf(
                str(R.string.formula_editor_sensor_face_size),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_face_x_position),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_face_y_position),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_second_face_detected),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_second_face_size),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_second_face_x_position),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_second_face_y_position),
                str(R.string.formula_editor_function_no_parameter), ""
            )
        )

        private val listOfPoseDetection = listOf(
            listOf(
                str(R.string.formula_editor_sensor_nose_x),
                str(R.string.formula_editor_function_no_parameter),
                str(R.string.formula_editor_device_pose_detection)
            ),
            listOf(
                str(R.string.formula_editor_sensor_nose_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_eye_inner_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_eye_inner_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_eye_center_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_eye_center_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_eye_outer_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_eye_outer_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_eye_inner_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_eye_inner_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_eye_center_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_eye_center_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_eye_outer_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_eye_outer_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_ear_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_ear_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_ear_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_ear_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_mouth_left_corner_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_mouth_left_corner_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_mouth_right_corner_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_mouth_right_corner_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_shoulder_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_shoulder_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_shoulder_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_shoulder_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_elbow_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_elbow_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_elbow_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_elbow_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_wrist_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_wrist_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_wrist_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_wrist_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_pinky_knuckle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_pinky_knuckle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_pinky_knuckle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_pinky_knuckle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_index_knuckle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_index_knuckle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_index_knuckle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_index_knuckle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_thumb_knuckle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_thumb_knuckle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_thumb_knuckle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_thumb_knuckle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_hip_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_hip_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_hip_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_hip_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_knee_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_knee_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_knee_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_knee_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_ankle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_ankle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_ankle_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_ankle_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_heel_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_heel_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_heel_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_heel_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_foot_index_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_left_foot_index_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_foot_index_x),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_right_foot_index_y),
                str(R.string.formula_editor_function_no_parameter), ""
            )
        )

        private val listOfTextRecognition = listOf(
            listOf(
                str(R.string.formula_editor_sensor_text_from_camera),
                str(R.string.formula_editor_function_no_parameter),
                str(R.string.formula_editor_device_text_recognition)
            ),
            listOf(
                str(R.string.formula_editor_sensor_text_blocks_number),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_text_block_x),
                str(R.string.formula_editor_function_text_block_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_text_block_y),
                str(R.string.formula_editor_function_text_block_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_text_block_size),
                str(R.string.formula_editor_function_text_block_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_text_block_from_camera),
                str(R.string.formula_editor_function_text_block_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_text_block_language_from_camera),
                str(R.string.formula_editor_function_text_block_parameter), ""
            )
        )

        private val listOfDevice = listOf(
            listOf(
                str(R.string.formula_editor_sensor_loudness),
                "", str(R.string.formula_editor_device_sensors)
            ),
            listOf(str(R.string.formula_editor_function_touched), "", ""),
            listOf(str(R.string.formula_editor_sensor_stage_width), "", ""),
            listOf(str(R.string.formula_editor_sensor_stage_height), "", ""),
            listOf(
                str(R.string.formula_editor_sensor_color_at_x_y),
                str(R.string.formula_editor_sensor_color_at_x_y_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_sensor_color_equals_color),
                str(R.string.formula_editor_sensor_color_equals_color_parameter), ""
            ),
            listOf(str(R.string.formula_editor_sensor_x_acceleration), "", ""),
            listOf(str(R.string.formula_editor_sensor_y_acceleration), "", ""),
            listOf(str(R.string.formula_editor_sensor_z_acceleration), "", ""),
            listOf(str(R.string.formula_editor_sensor_x_inclination), "", ""),
            listOf(str(R.string.formula_editor_sensor_y_inclination), "", ""),
            listOf(str(R.string.formula_editor_sensor_compass_direction), "", ""),
            listOf(str(R.string.formula_editor_sensor_latitude), "", ""),
            listOf(str(R.string.formula_editor_sensor_longitude), "", ""),
            listOf(str(R.string.formula_editor_sensor_location_accuracy), "", ""),
            listOf(str(R.string.formula_editor_sensor_altitude), "", ""),
            listOf(str(R.string.formula_editor_sensor_user_language), "", "")
        )

        private val listOfTouch = listOf(
            listOf(
                str(R.string.formula_editor_function_finger_x),
                str(R.string.formula_editor_function_no_parameter),
                str(R.string.formula_editor_device_touch_detection)
            ),
            listOf(
                str(R.string.formula_editor_function_finger_y),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_is_finger_touching),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_multi_finger_x),
                str(R.string.formula_editor_function_touch_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_multi_finger_y),
                str(R.string.formula_editor_function_touch_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_is_multi_finger_touching),
                str(R.string.formula_editor_function_touch_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_index_of_last_finger),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_number_of_current_touches),
                str(R.string.formula_editor_function_no_parameter), ""
            ),
            listOf(
                str(R.string.formula_editor_function_index_of_current_touch),
                str(R.string.formula_editor_function_touch_parameter), ""
            )
        )

        private val listOfDateTime = listOf(
            listOf(
                str(R.string.formula_editor_sensor_timer),
                "", str(R.string.formula_editor_device_date_and_time)
            ),
            listOf(str(R.string.formula_editor_sensor_date_year), "", ""),
            listOf(str(R.string.formula_editor_sensor_date_month), "", ""),
            listOf(str(R.string.formula_editor_sensor_date_day), "", ""),
            listOf(str(R.string.formula_editor_sensor_date_weekday), "", ""),
            listOf(str(R.string.formula_editor_sensor_time_hour), "", ""),
            listOf(str(R.string.formula_editor_sensor_time_minute), "", ""),
            listOf(str(R.string.formula_editor_sensor_time_second), "", "")
        )

        private val allShowBrickSettings: List<String> = listOf(
            SettingsFragment.SETTINGS_SHOW_ARDUINO_BRICKS,
            SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS,
            SettingsFragment.SETTINGS_SHOW_NFC_BRICKS,
            SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
            SettingsFragment.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED,
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
