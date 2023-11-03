/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.app.Activity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter
import org.catrobat.catroid.ui.settingsfragments.RaspberryPiSettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.koin.java.KoinJavaComponent

class CategoryListItems {

    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)

    fun getObjectItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        val result: MutableList<CategoryListRVAdapter.CategoryListItem> = ArrayList()
        result.addAll(getObjectGeneralPropertiesItems(activity))
        result.addAll(getObjectPhysicalPropertiesItems(activity))
        return result
    }

    fun getFunctionItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        val result: MutableList<CategoryListRVAdapter.CategoryListItem> = ArrayList()
        result.addAll(
            addHeader(
                toCategoryListItems(activity, MATH_FUNCTIONS, MATH_PARAMS),
                activity.getString(R.string.formula_editor_functions_maths)
            )
        )
        result.addAll(
            addHeader(
                toCategoryListItems(activity, STRING_FUNCTIONS, STRING_PARAMS),
                activity.getString(R.string.formula_editor_functions_strings)
            )
        )
        result.addAll(
            addHeader(
                toCategoryListItems(activity, LIST_FUNCTIONS, LIST_PARAMS),
                activity.getString(R.string.formula_editor_functions_lists)
            )
        )
        return result
    }

    fun getLogicItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        val result: MutableList<CategoryListRVAdapter.CategoryListItem> = ArrayList()
        result.addAll(
            addHeader(
                toCategoryListItems(activity, LOGIC_BOOL),
                activity.getString(R.string.formula_editor_logic_boolean)
            )
        )
        result.addAll(
            addHeader(
                toCategoryListItems(activity, LOGIC_COMPARISON),
                activity.getString(R.string.formula_editor_logic_comparison)
            )
        )
        return result
    }

    fun getSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        val result: MutableList<CategoryListRVAdapter.CategoryListItem> = ArrayList()
        result.addAll(getNxtSensorItems(activity))
        result.addAll(getEv3SensorItems(activity))
        result.addAll(getPhiroSensorItems(activity))
        result.addAll(getArduinoSensorItems(activity))
        result.addAll(getDroneSensorItems(activity))
        result.addAll(getRaspberrySensorItems(activity))
        result.addAll(getNfcItems(activity))
        result.addAll(getCastGamepadSensorItems(activity))
        result.addAll(getSpeechRecognitionItems(activity))
        result.addAll(getFaceSensorItems(activity))
        result.addAll(getPoseSensorItems(activity))
        result.addAll(getTextSensorItems(activity))
        result.addAll(getObjectDetectionSensorItems(activity))
        result.addAll(getDeviceSensorItems(activity))
        result.addAll(getTouchDetectionSensorItems(activity))
        result.addAll(getDateTimeSensorItems(activity))
        return result
    }

    private fun getObjectGeneralPropertiesItems(activity: Activity): List<CategoryListRVAdapter
    .CategoryListItem> {
        val resIds: MutableList<Int> = ArrayList(OBJECT_GENERAL_PROPERTIES)
        val currentScene = projectManager.currentlyEditedScene
        if (projectManager.currentSprite == currentScene.backgroundSprite) {
            resIds.addAll(OBJECT_BACKGROUND)
        } else {
            resIds.addAll(OBJECT_LOOK)
        }
        val result = toCategoryListItems(activity, resIds)
        result.addAll(
            toCategoryListItems(
                activity,
                OBJECT_COLOR_COLLISION.subList(1, 2),
                OBJECT_COLOR_PARAMS.subList(1, 2)
            )
        )
        return addHeader(result, activity.getString(R.string.formula_editor_object_look))
    }

    private fun getObjectPhysicalPropertiesItems(activity: Activity):
        List<CategoryListRVAdapter.CategoryListItem> {
        val result = toCategoryListItems(activity, OBJECT_PHYSICAL_1)
        result.addAll(
            toCategoryListItems(
                activity,
                OBJECT_PHYSICAL_COLLISION,
                null,
                CategoryListRVAdapter.COLLISION
            )
        )
        result.addAll(toCategoryListItems(activity, OBJECT_PHYSICAL_2))
        result.addAll(toCategoryListItems(activity, OBJECT_COLOR_COLLISION, OBJECT_COLOR_PARAMS))
        return addHeader(result, activity.getString(R.string.formula_editor_object_movement))
    }

    private fun getNxtSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isMindstormsNXTSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_NXT, null, CategoryListRVAdapter.NXT),
                activity.getString(R.string.formula_editor_device_lego_nxt)
            )
        } else emptyList()
    }

    private fun getEv3SensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isMindstormsEV3SharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_EV3, null, CategoryListRVAdapter.EV3),
                activity.getString(R.string.formula_editor_device_lego_ev3)
            )
        } else emptyList()
    }

    private fun getPhiroSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isPhiroSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_PHIRO),
                activity.getString(R.string.formula_editor_device_phiro)
            )
        } else emptyList()
    }

    private fun getArduinoSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isArduinoSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_ARDUINO, SENSORS_ARDUINO_PARAMS),
                activity.getString(R.string.formula_editor_device_arduino)
            )
        } else emptyList()
    }

    private fun getDroneSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isDroneSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_DRONE),
                activity.getString(R.string.formula_editor_device_drone)
            )
        } else emptyList()
    }

    private fun getRaspberrySensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (RaspberryPiSettingsFragment.isRaspiSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_RASPBERRY, SENSORS_RASPBERRY_PARAMS),
                activity.getString(R.string.formula_editor_device_raspberry)
            )
        } else emptyList()
    }

    private fun getNfcItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isNfcSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_NFC),
                activity.getString(R.string.formula_editor_device_nfc)
            )
        } else emptyList()
    }

    private fun getCastGamepadSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (projectManager.currentProject.isCastProject) {
            addHeader(
                toCategoryListItems(activity, SENSORS_CAST_GAMEPAD),
                activity.getString(R.string.formula_editor_device_cast)
            )
        } else emptyList()
    }

    private fun getDeviceSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        val deviceSensorItems: MutableList<CategoryListRVAdapter.CategoryListItem> = ArrayList(
            toCategoryListItems(activity, SENSORS_DEFAULT)
        )
        val sensorHandler = SensorHandler.getInstance(activity)
        deviceSensorItems.addAll(
            toCategoryListItems(
                activity,
                SENSORS_COLOR_AT_XY,
                SENSORS_COLOR_AT_XY_PARAMS
            )
        )
        deviceSensorItems.addAll(
            toCategoryListItems(
                activity,
                SENSORS_COLOR_EQUALS_COLOR,
                SENSORS_COLOR_EQUALS_COLOR_PARAMS
            )
        )
        deviceSensorItems.addAll(
            if (sensorHandler.accelerationAvailable()) toCategoryListItems(
                activity,
                SENSORS_ACCELERATION
            ) else emptyList()
        )
        deviceSensorItems.addAll(
            if (sensorHandler.inclinationAvailable()) toCategoryListItems(
                activity,
                SENSORS_INCLINATION
            ) else emptyList()
        )
        deviceSensorItems.addAll(
            if (sensorHandler.compassAvailable()) toCategoryListItems(
                activity,
                SENSORS_COMPASS
            ) else emptyList()
        )
        deviceSensorItems.addAll(toCategoryListItems(activity, SENSORS_GPS))
        deviceSensorItems.addAll(toCategoryListItems(activity, SENSOR_USER_LANGUAGE))
        return addHeader(
            deviceSensorItems,
            activity.getString(R.string.formula_editor_device_sensors)
        )
    }

    private fun getTouchDetectionSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return addHeader(
            toCategoryListItems(activity, SENSORS_TOUCH, SENSORS_TOUCH_PARAMS),
            activity.getString(R.string.formula_editor_device_touch_detection)
        )
    }

    private fun getDateTimeSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return addHeader(
            toCategoryListItems(activity, SENSORS_DATE_TIME),
            activity.getString(R.string.formula_editor_device_date_and_time)
        )
    }

    private fun getSpeechRecognitionItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_SPEECH_RECOGNITION),
                activity.getString(R.string.formula_editor_speech_recognition)
            )
        } else emptyList()
    }

    private fun getFaceSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isAIFaceDetectionSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(
                    activity,
                    SENSORS_FACE_DETECTION,
                    SENSORS_FACE_DETECTION_PARAMS
                ),
                activity.getString(R.string.formula_editor_device_face_detection)
            )
        } else emptyList()
    }

    private fun getPoseSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        val mobileServiceAvailability = KoinJavaComponent.get(MobileServiceAvailability::class.java)
        val isHMSAvailable = mobileServiceAvailability.isHmsAvailable(activity.applicationContext)
        val isPoseDetectionEnabled = SettingsFragment.isAIPoseDetectionSharedPreferenceEnabled(
            activity.applicationContext
        )
        return if (isPoseDetectionEnabled && isHMSAvailable) {
            addHeader(
                toCategoryListItems(
                    activity,
                    SENSORS_POSE_DETECTION_HUAWEI,
                    SENSORS_POSE_DETECTION_PARAMS_HUAWEI
                ),
                activity.getString(R.string.formula_editor_device_pose_detection)
            )
        } else if (isPoseDetectionEnabled) {
            addHeader(
                toCategoryListItems(
                    activity,
                    SENSORS_POSE_DETECTION,
                    SENSORS_POSE_DETECTION_PARAMS
                ),
                activity.getString(R.string.formula_editor_device_pose_detection)
            )
        } else {
            emptyList()
        }
    }

    private fun getTextSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isAITextRecognitionSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(
                    activity,
                    SENSORS_TEXT_RECOGNITION,
                    SENSORS_TEXT_RECOGNITION_PARAMS
                ),
                activity.getString(R.string.formula_editor_device_text_recognition)
            )
        } else emptyList()
    }

    private fun getObjectDetectionSensorItems(activity: Activity): List<CategoryListRVAdapter.CategoryListItem> {
        return if (SettingsFragment.isAIObjectDetectionSharedPreferenceEnabled(
                activity.applicationContext
            )
        ) {
            addHeader(
                toCategoryListItems(activity, SENSORS_OBJECT_DETECTION),
                activity.getString(R.string.formula_editor_device_object_recognition)
            )
        } else emptyList()
    }

    fun getListFunctions() = LIST_FUNCTIONS

    private fun addHeader(
        subCategory: List<CategoryListRVAdapter.CategoryListItem>,
        header: String
    ): List<CategoryListRVAdapter.CategoryListItem> {
        subCategory[0].header = header
        return subCategory
    }

    private fun toCategoryListItems(
        activity: Activity,
        nameResIds: List<Int>,
        paramResIds: List<Int>? = null,
        @CategoryListRVAdapter.CategoryListItemType type: Int = CategoryListRVAdapter.DEFAULT
    ): MutableList<CategoryListRVAdapter.CategoryListItem> {
        require(!(paramResIds != null && paramResIds.size != nameResIds.size)) {
            "Sizes of paramResIds and nameResIds parameters do not fit"
        }
        val result: MutableList<CategoryListRVAdapter.CategoryListItem> = ArrayList()
        for (i in nameResIds.indices) {
            var param = ""
            if (paramResIds != null) {
                param = activity.getString(paramResIds[i])
            }
            result.add(
                CategoryListRVAdapter.CategoryListItem(
                    nameResIds[i],
                    activity.getString(nameResIds[i]) + param,
                    type
                )
            )
        }
        return result
    }

    companion object {
        private val OBJECT_GENERAL_PROPERTIES = listOf(
            R.string.formula_editor_object_rotation_look,
            R.string.formula_editor_object_transparency,
            R.string.formula_editor_object_brightness,
            R.string.formula_editor_object_color
        )
        private val OBJECT_LOOK = listOf(
            R.string.formula_editor_object_look_number,
            R.string.formula_editor_object_look_name, R.string.formula_editor_object_number_of_looks
        )
        private val OBJECT_BACKGROUND = listOf(
            R.string.formula_editor_object_background_number,
            R.string.formula_editor_object_background_name,
            R.string.formula_editor_object_number_of_backgrounds
        )
        private val OBJECT_PHYSICAL_1 = listOf(
            R.string.formula_editor_object_x,
            R.string.formula_editor_object_y, R.string.formula_editor_object_size,
            R.string.formula_editor_object_rotation, R.string.formula_editor_object_rotation_look,
            R.string.formula_editor_object_layer
        )
        private val OBJECT_PHYSICAL_COLLISION = listOf(R.string.formula_editor_function_collision)
        private val OBJECT_PHYSICAL_2 = listOf(
            R.string.formula_editor_function_collides_with_edge,
            R.string.formula_editor_function_touched,
            R.string.formula_editor_object_x_velocity, R.string.formula_editor_object_y_velocity,
            R.string.formula_editor_object_angular_velocity
        )
        private val MATH_FUNCTIONS = listOf(
            R.string.formula_editor_function_sin,
            R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
            R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
            R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
            R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
            R.string.formula_editor_function_round, R.string.formula_editor_function_mod,
            R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arccos,
            R.string.formula_editor_function_arctan, R.string.formula_editor_function_arctan2,
            R.string.formula_editor_function_exp, R.string.formula_editor_function_power,
            R.string.formula_editor_function_floor, R.string.formula_editor_function_ceil,
            R.string.formula_editor_function_max, R.string.formula_editor_function_min,
            R.string.formula_editor_function_if_then_else
        )
        private val MATH_PARAMS = listOf(
            R.string.formula_editor_function_sin_parameter,
            R.string.formula_editor_function_cos_parameter,
            R.string.formula_editor_function_tan_parameter,
            R.string.formula_editor_function_ln_parameter,
            R.string.formula_editor_function_log_parameter,
            R.string.formula_editor_function_pi_parameter,
            R.string.formula_editor_function_sqrt_parameter,
            R.string.formula_editor_function_rand_parameter,
            R.string.formula_editor_function_abs_parameter,
            R.string.formula_editor_function_round_parameter,
            R.string.formula_editor_function_mod_parameter,
            R.string.formula_editor_function_arcsin_parameter,
            R.string.formula_editor_function_arccos_parameter,
            R.string.formula_editor_function_arctan_parameter,
            R.string.formula_editor_function_arctan2_parameter,
            R.string.formula_editor_function_exp_parameter,
            R.string.formula_editor_function_power_parameter,
            R.string.formula_editor_function_floor_parameter,
            R.string.formula_editor_function_ceil_parameter,
            R.string.formula_editor_function_max_parameter,
            R.string.formula_editor_function_min_parameter,
            R.string.formula_editor_function_if_then_else_parameter
        )
        private val STRING_FUNCTIONS = listOf(
            R.string.formula_editor_function_length,
            R.string.formula_editor_function_letter,
            R.string.formula_editor_function_subtext, R.string.formula_editor_function_join,
            R.string.formula_editor_function_join3, R.string.formula_editor_function_regex,
            R.string.formula_editor_function_regex_assistant,
            R.string.formula_editor_function_flatten
        )
        private val STRING_PARAMS = listOf(
            R.string.formula_editor_function_length_parameter,
            R.string.formula_editor_function_letter_parameter,
            R.string.formula_editor_function_subtext_parameter,
            R.string.formula_editor_function_join_parameter,
            R.string.formula_editor_function_join3_parameter,
            R.string.formula_editor_function_regex_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_flatten_parameter
        )
        private val LIST_FUNCTIONS = listOf(
            R.string.formula_editor_function_number_of_items,
            R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains,
            R.string.formula_editor_function_index_of_item, R.string.formula_editor_function_flatten
        )
        private val LIST_PARAMS = listOf(
            R.string.formula_editor_function_number_of_items_parameter,
            R.string.formula_editor_function_list_item_parameter,
            R.string.formula_editor_function_contains_parameter,
            R.string.formula_editor_function_index_of_item_parameter,
            R.string.formula_editor_function_flatten_parameter
        )
        private val LOGIC_BOOL = listOf(
            R.string.formula_editor_logic_and,
            R.string.formula_editor_logic_or, R.string.formula_editor_logic_not,
            R.string.formula_editor_function_true, R.string.formula_editor_function_false
        )
        private val LOGIC_COMPARISON = listOf(
            R.string.formula_editor_logic_equal,
            R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
            R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
            R.string.formula_editor_logic_greaterequal
        )
        private val SENSORS_DEFAULT = listOf(
            R.string.formula_editor_sensor_loudness,
            R.string.formula_editor_function_touched, R.string.formula_editor_sensor_stage_width,
            R.string.formula_editor_sensor_stage_height
        )
        private val OBJECT_COLOR_COLLISION = listOf(
            R.string.formula_editor_function_collides_with_color,
            R.string.formula_editor_function_color_touches_color
        )
        private val OBJECT_COLOR_PARAMS = listOf(
            R.string.formula_editor_function_collides_with_color_parameter,
            R.string.formula_editor_function_color_touches_color_parameter
        )

        private val SENSORS_COLOR_AT_XY = listOf(R.string.formula_editor_sensor_color_at_x_y)
        private val SENSORS_COLOR_AT_XY_PARAMS =
            listOf(R.string.formula_editor_sensor_color_at_x_y_parameter)

        private val SENSORS_COLOR_EQUALS_COLOR =
            listOf(R.string.formula_editor_sensor_color_equals_color)
        private val SENSORS_COLOR_EQUALS_COLOR_PARAMS =
            listOf(R.string.formula_editor_sensor_color_equals_color_parameter)

        private val SENSORS_ACCELERATION = listOf(
            R.string.formula_editor_sensor_x_acceleration,
            R.string.formula_editor_sensor_y_acceleration,
            R.string.formula_editor_sensor_z_acceleration
        )
        private val SENSORS_INCLINATION = listOf(
            R.string.formula_editor_sensor_x_inclination,
            R.string.formula_editor_sensor_y_inclination
        )
        private val SENSORS_COMPASS = listOf(R.string.formula_editor_sensor_compass_direction)
        private val SENSORS_GPS = listOf(
            R.string.formula_editor_sensor_latitude,
            R.string.formula_editor_sensor_longitude,
            R.string.formula_editor_sensor_location_accuracy,
            R.string.formula_editor_sensor_altitude
        )
        private val SENSOR_USER_LANGUAGE = listOf(R.string.formula_editor_sensor_user_language)
        private val SENSORS_TOUCH = listOf(
            R.string.formula_editor_function_finger_x,
            R.string.formula_editor_function_finger_y,
            R.string.formula_editor_function_is_finger_touching,
            R.string.formula_editor_function_multi_finger_x,
            R.string.formula_editor_function_multi_finger_y,
            R.string.formula_editor_function_is_multi_finger_touching,
            R.string.formula_editor_function_index_of_last_finger,
            R.string.formula_editor_function_number_of_current_touches,
            R.string.formula_editor_function_index_of_current_touch
        )
        private val SENSORS_TOUCH_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_touch_parameter,
            R.string.formula_editor_function_touch_parameter,
            R.string.formula_editor_function_touch_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_touch_parameter
        )
        private val SENSORS_FACE_DETECTION = listOf(
            R.string.formula_editor_sensor_face_detected,
            R.string.formula_editor_sensor_face_size,
            R.string.formula_editor_sensor_face_x_position,
            R.string.formula_editor_sensor_face_y_position,
            R.string.formula_editor_sensor_second_face_detected,
            R.string.formula_editor_sensor_second_face_size,
            R.string.formula_editor_sensor_second_face_x_position,
            R.string.formula_editor_sensor_second_face_y_position
        )
        private val SENSORS_FACE_DETECTION_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter
        )
        private val SENSORS_POSE_DETECTION = listOf(
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
        private val SENSORS_POSE_DETECTION_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter
        )
        private val SENSORS_POSE_DETECTION_HUAWEI = listOf(
            R.string.formula_editor_sensor_head_top_x,
            R.string.formula_editor_sensor_head_top_y,
            R.string.formula_editor_sensor_neck_x,
            R.string.formula_editor_sensor_neck_y,
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
            R.string.formula_editor_sensor_right_ankle_y
        )
        private val SENSORS_POSE_DETECTION_PARAMS_HUAWEI = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter
        )
        private val SENSORS_TEXT_RECOGNITION = listOf(
            R.string.formula_editor_sensor_text_from_camera,
            R.string.formula_editor_sensor_text_blocks_number,
            R.string.formula_editor_function_text_block_x,
            R.string.formula_editor_function_text_block_y,
            R.string.formula_editor_function_text_block_size,
            R.string.formula_editor_function_text_block_from_camera,
            R.string.formula_editor_function_text_block_language_from_camera
        )
        private val SENSORS_TEXT_RECOGNITION_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter
        )
        private val SENSORS_OBJECT_DETECTION = listOf(
            R.string.formula_editor_function_get_id_of_detected_object,
            R.string.formula_editor_function_object_with_id_visible
        )
        private val SENSORS_DATE_TIME = listOf(
            R.string.formula_editor_sensor_timer,
            R.string.formula_editor_sensor_date_year, R.string.formula_editor_sensor_date_month,
            R.string.formula_editor_sensor_date_day, R.string.formula_editor_sensor_date_weekday,
            R.string.formula_editor_sensor_time_hour, R.string.formula_editor_sensor_time_minute,
            R.string.formula_editor_sensor_time_second
        )
        private val SENSORS_NXT = listOf(
            R.string.formula_editor_sensor_lego_nxt_touch,
            R.string.formula_editor_sensor_lego_nxt_sound,
            R.string.formula_editor_sensor_lego_nxt_light,
            R.string.formula_editor_sensor_lego_nxt_light_active,
            R.string.formula_editor_sensor_lego_nxt_ultrasonic
        )
        private val SENSORS_EV3 = listOf(
            R.string.formula_editor_sensor_lego_ev3_sensor_touch,
            R.string.formula_editor_sensor_lego_ev3_sensor_infrared,
            R.string.formula_editor_sensor_lego_ev3_sensor_color,
            R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient,
            R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected,
            R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light_active,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_sound,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_ultrasonic
        )
        private val SENSORS_PHIRO = listOf(
            R.string.formula_editor_phiro_sensor_front_left,
            R.string.formula_editor_phiro_sensor_front_right,
            R.string.formula_editor_phiro_sensor_side_left,
            R.string.formula_editor_phiro_sensor_side_right,
            R.string.formula_editor_phiro_sensor_bottom_left,
            R.string.formula_editor_phiro_sensor_bottom_right
        )
        private val SENSORS_ARDUINO = listOf(
            R.string.formula_editor_function_arduino_read_pin_value_analog,
            R.string.formula_editor_function_arduino_read_pin_value_digital
        )
        private val SENSORS_ARDUINO_PARAMS = listOf(
            R.string.formula_editor_function_pin_default_parameter,
            R.string.formula_editor_function_pin_default_parameter
        )
        private val SENSORS_DRONE = listOf(
            R.string.formula_editor_sensor_drone_battery_status,
            R.string.formula_editor_sensor_drone_emergency_state,
            R.string.formula_editor_sensor_drone_flying,
            R.string.formula_editor_sensor_drone_initialized,
            R.string.formula_editor_sensor_drone_usb_active,
            R.string.formula_editor_sensor_drone_usb_remaining_time,
            R.string.formula_editor_sensor_drone_camera_ready,
            R.string.formula_editor_sensor_drone_record_ready,
            R.string.formula_editor_sensor_drone_recording,
            R.string.formula_editor_sensor_drone_num_frames
        )
        private val SENSORS_RASPBERRY =
            listOf(R.string.formula_editor_function_raspi_read_pin_value_digital)
        private val SENSORS_RASPBERRY_PARAMS =
            listOf(R.string.formula_editor_function_pin_default_parameter)
        private val SENSORS_NFC = listOf(
            R.string.formula_editor_nfc_tag_id,
            R.string.formula_editor_nfc_tag_message
        )
        private val SENSORS_CAST_GAMEPAD = listOf(
            R.string.formula_editor_sensor_gamepad_a_pressed,
            R.string.formula_editor_sensor_gamepad_b_pressed,
            R.string.formula_editor_sensor_gamepad_up_pressed,
            R.string.formula_editor_sensor_gamepad_down_pressed,
            R.string.formula_editor_sensor_gamepad_left_pressed,
            R.string.formula_editor_sensor_gamepad_right_pressed
        )

        private val SENSORS_SPEECH_RECOGNITION =
            listOf(R.string.formula_editor_listening_language_sensor)
    }
}
