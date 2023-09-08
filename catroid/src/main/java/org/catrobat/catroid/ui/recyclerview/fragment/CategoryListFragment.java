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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Constants.LegoSensorType;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.dialogs.LegoSensorPortConfigDialog;
import org.catrobat.catroid.ui.dialogs.regexassistant.RegularExpressionAssistantDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItem;
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItemType;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.settingsfragments.RaspberryPiSettingsFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.AddUserListDialog;
import org.catrobat.catroid.utils.MobileServiceAvailability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static org.catrobat.catroid.CatroidApplication.defaultSystemLanguage;
import static org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAGS;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY;
import static org.catrobat.catroid.ui.fragment.FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG;
import static org.koin.java.KoinJavaComponent.get;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class CategoryListFragment extends Fragment implements CategoryListRVAdapter.OnItemClickListener {

	public static final String OBJECT_TAG = "objectFragment";
	public static final String FUNCTION_TAG = "functionFragment";
	public static final String LOGIC_TAG = "logicFragment";
	public static final String SENSOR_TAG = "sensorFragment";
	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";
	public static final String TAG = CategoryListFragment.class.getSimpleName();

	private static final List<Integer> OBJECT_GENERAL_PROPERTIES = asList(
			R.string.formula_editor_object_rotation_look,
			R.string.formula_editor_object_transparency,
			R.string.formula_editor_object_brightness,
			R.string.formula_editor_object_color);
	private static final List<Integer> OBJECT_LOOK = asList(R.string.formula_editor_object_look_number,
			R.string.formula_editor_object_look_name, R.string.formula_editor_object_number_of_looks);
	private static final List<Integer> OBJECT_BACKGROUND = asList(R.string.formula_editor_object_background_number,
			R.string.formula_editor_object_background_name, R.string.formula_editor_object_number_of_backgrounds);
	private static final List<Integer> OBJECT_PHYSICAL_1 = asList(R.string.formula_editor_object_x,
			R.string.formula_editor_object_y, R.string.formula_editor_object_size,
			R.string.formula_editor_object_rotation, R.string.formula_editor_object_rotation_look,
			R.string.formula_editor_object_layer);
	private static final List<Integer> OBJECT_PHYSICAL_COLLISION = singletonList(R.string.formula_editor_function_collision);
	private static final List<Integer> OBJECT_PHYSICAL_2 = asList(R.string.formula_editor_function_collides_with_edge,
			R.string.formula_editor_function_touched,
			R.string.formula_editor_object_x_velocity, R.string.formula_editor_object_y_velocity,
			R.string.formula_editor_object_angular_velocity);
	private static final List<Integer> MATH_FUNCTIONS = asList(R.string.formula_editor_function_sin,
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
			R.string.formula_editor_function_if_then_else);
	private static final List<Integer> MATH_PARAMS = asList(R.string.formula_editor_function_sin_parameter,
			R.string.formula_editor_function_cos_parameter, R.string.formula_editor_function_tan_parameter,
			R.string.formula_editor_function_ln_parameter, R.string.formula_editor_function_log_parameter,
			R.string.formula_editor_function_pi_parameter, R.string.formula_editor_function_sqrt_parameter,
			R.string.formula_editor_function_rand_parameter, R.string.formula_editor_function_abs_parameter,
			R.string.formula_editor_function_round_parameter, R.string.formula_editor_function_mod_parameter,
			R.string.formula_editor_function_arcsin_parameter, R.string.formula_editor_function_arccos_parameter,
			R.string.formula_editor_function_arctan_parameter, R.string.formula_editor_function_arctan2_parameter,
			R.string.formula_editor_function_exp_parameter, R.string.formula_editor_function_power_parameter,
			R.string.formula_editor_function_floor_parameter, R.string.formula_editor_function_ceil_parameter,
			R.string.formula_editor_function_max_parameter, R.string.formula_editor_function_min_parameter,
			R.string.formula_editor_function_if_then_else_parameter);
	private static final List<Integer> STRING_FUNCTIONS = asList(R.string.formula_editor_function_length,
			R.string.formula_editor_function_letter, R.string.formula_editor_function_join,
			R.string.formula_editor_function_join3, R.string.formula_editor_function_regex,
			R.string.formula_editor_function_regex_assistant,
			R.string.formula_editor_function_flatten);
	private static final List<Integer> STRING_PARAMS = asList(R.string.formula_editor_function_length_parameter,
			R.string.formula_editor_function_letter_parameter,
			R.string.formula_editor_function_join_parameter,
			R.string.formula_editor_function_join3_parameter,
			R.string.formula_editor_function_regex_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_flatten_parameter);
	private static final List<Integer> LIST_FUNCTIONS = asList(R.string.formula_editor_function_number_of_items,
			R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains,
			R.string.formula_editor_function_index_of_item, R.string.formula_editor_function_flatten);
	private static final List<Integer> LIST_PARAMS = asList(R.string.formula_editor_function_number_of_items_parameter,
			R.string.formula_editor_function_list_item_parameter,
			R.string.formula_editor_function_contains_parameter,
			R.string.formula_editor_function_index_of_item_parameter,
			R.string.formula_editor_function_flatten_parameter);
	private static final List<Integer> LOGIC_BOOL = asList(R.string.formula_editor_logic_and,
			R.string.formula_editor_logic_or, R.string.formula_editor_logic_not,
			R.string.formula_editor_function_true, R.string.formula_editor_function_false);
	private static final List<Integer> LOGIC_COMPARISION = asList(R.string.formula_editor_logic_equal,
			R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
			R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
			R.string.formula_editor_logic_greaterequal);
	private static final List<Integer> SENSORS_DEFAULT = asList(R.string.formula_editor_sensor_loudness,
			R.string.formula_editor_function_touched, R.string.formula_editor_sensor_stage_width,
			R.string.formula_editor_sensor_stage_height);
	private static final List<Integer> OBJECT_COLOR_COLLISION =
			asList(R.string.formula_editor_function_collides_with_color, R.string.formula_editor_function_color_touches_color);
	private static final List<Integer> OBJECT_COLOR_PARAMS =
			asList(R.string.formula_editor_function_collides_with_color_parameter, R.string.formula_editor_function_color_touches_color_parameter);

	private static final List<Integer> SENSORS_COLOR_AT_XY = asList(R.string.formula_editor_sensor_color_at_x_y);
	private static final List<Integer> SENSORS_COLOR_AT_XY_PARAMS = asList(R.string.formula_editor_sensor_color_at_x_y_parameter);

	private static final List<Integer> SENSORS_COLOR_EQUALS_COLOR =
			asList(R.string.formula_editor_sensor_color_equals_color);
	private static final List<Integer> SENSORS_COLOR_EQUALS_COLOR_PARAMS =
			asList(R.string.formula_editor_sensor_color_equals_color_parameter);

	private static final List<Integer> SENSORS_ACCELERATION = asList(R.string.formula_editor_sensor_x_acceleration,
			R.string.formula_editor_sensor_y_acceleration, R.string.formula_editor_sensor_z_acceleration);
	private static final List<Integer> SENSORS_INCLINATION = asList(R.string.formula_editor_sensor_x_inclination,
			R.string.formula_editor_sensor_y_inclination);
	private static final List<Integer> SENSORS_COMPASS = singletonList(R.string.formula_editor_sensor_compass_direction);
	private static final List<Integer> SENSORS_GPS = asList(R.string.formula_editor_sensor_latitude,
			R.string.formula_editor_sensor_longitude, R.string.formula_editor_sensor_location_accuracy,
			R.string.formula_editor_sensor_altitude);
	private static final List<Integer> SENSOR_USER_LANGUAGE =
			Collections.singletonList(R.string.formula_editor_sensor_user_language);
	private static final List<Integer> SENSORS_TOUCH = asList(R.string.formula_editor_function_finger_x,
			R.string.formula_editor_function_finger_y, R.string.formula_editor_function_is_finger_touching,
			R.string.formula_editor_function_multi_finger_x, R.string.formula_editor_function_multi_finger_y,
			R.string.formula_editor_function_is_multi_finger_touching,
			R.string.formula_editor_function_index_of_last_finger,
			R.string.formula_editor_function_number_of_current_touches,
			R.string.formula_editor_function_index_of_current_touch);
	private static final List<Integer> SENSORS_TOUCH_PARAMS = asList(R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter, R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_touch_parameter, R.string.formula_editor_function_touch_parameter,
			R.string.formula_editor_function_touch_parameter, R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter, R.string.formula_editor_function_touch_parameter);
	private static final List<Integer> SENSORS_FACE_DETECTION = asList(R.string.formula_editor_sensor_face_detected,
			R.string.formula_editor_sensor_face_size, R.string.formula_editor_sensor_face_x_position,
			R.string.formula_editor_sensor_face_y_position,
			R.string.formula_editor_sensor_second_face_detected,
			R.string.formula_editor_sensor_second_face_size,
			R.string.formula_editor_sensor_second_face_x_position,
			R.string.formula_editor_sensor_second_face_y_position);
	private static final List<Integer> SENSORS_FACE_DETECTION_PARAMS = asList(R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter);
	private static final List<Integer> SENSORS_POSE_DETECTION =
			asList(R.string.formula_editor_sensor_nose_x,
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
					R.string.formula_editor_sensor_right_foot_index_y);
	private static final List<Integer> SENSORS_POSE_DETECTION_PARAMS =
			asList(R.string.formula_editor_function_no_parameter,
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
					R.string.formula_editor_function_no_parameter);
	private static final List<Integer> SENSORS_POSE_DETECTION_HUAWEI =
			asList(R.string.formula_editor_sensor_head_top_x,
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
					R.string.formula_editor_sensor_right_ankle_y);
	private static final List<Integer> SENSORS_POSE_DETECTION_PARAMS_HUAWEI =
			asList(R.string.formula_editor_function_no_parameter,
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
					R.string.formula_editor_function_no_parameter);
	private static final List<Integer> SENSORS_TEXT_RECOGNITION = asList(R.string.formula_editor_sensor_text_from_camera,
			R.string.formula_editor_sensor_text_blocks_number,
			R.string.formula_editor_function_text_block_x,
			R.string.formula_editor_function_text_block_y,
			R.string.formula_editor_function_text_block_size,
			R.string.formula_editor_function_text_block_from_camera,
			R.string.formula_editor_function_text_block_language_from_camera);
	private static final List<Integer> SENSORS_TEXT_RECOGNITION_PARAMS = asList(R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_no_parameter,
			R.string.formula_editor_function_text_block_parameter,
			R.string.formula_editor_function_text_block_parameter,
			R.string.formula_editor_function_text_block_parameter,
			R.string.formula_editor_function_text_block_parameter,
			R.string.formula_editor_function_text_block_parameter);
	private static final List<Integer> SENSORS_OBJECT_DETECTION = asList(
			R.string.formula_editor_function_get_id_of_detected_object,
			R.string.formula_editor_function_object_with_id_visible
	);
	private static final List<Integer> SENSORS_DATE_TIME = asList(R.string.formula_editor_sensor_timer,
			R.string.formula_editor_sensor_date_year, R.string.formula_editor_sensor_date_month,
			R.string.formula_editor_sensor_date_day, R.string.formula_editor_sensor_date_weekday,
			R.string.formula_editor_sensor_time_hour, R.string.formula_editor_sensor_time_minute,
			R.string.formula_editor_sensor_time_second);
	private static final List<Integer> SENSORS_NXT = asList(R.string.formula_editor_sensor_lego_nxt_touch,
			R.string.formula_editor_sensor_lego_nxt_sound, R.string.formula_editor_sensor_lego_nxt_light,
			R.string.formula_editor_sensor_lego_nxt_light_active,
			R.string.formula_editor_sensor_lego_nxt_ultrasonic);
	private static final List<Integer> SENSORS_EV3 = asList(R.string.formula_editor_sensor_lego_ev3_sensor_touch,
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
			R.string.formula_editor_sensor_lego_ev3_sensor_nxt_ultrasonic);
	private static final List<Integer> SENSORS_PHIRO = asList(R.string.formula_editor_phiro_sensor_front_left,
			R.string.formula_editor_phiro_sensor_front_right,
			R.string.formula_editor_phiro_sensor_side_left,
			R.string.formula_editor_phiro_sensor_side_right,
			R.string.formula_editor_phiro_sensor_bottom_left,
			R.string.formula_editor_phiro_sensor_bottom_right);
	private static final List<Integer> SENSORS_ARDUINO = asList(R.string.formula_editor_function_arduino_read_pin_value_analog,
			R.string.formula_editor_function_arduino_read_pin_value_digital);
	private static final List<Integer> SENSORS_ARDUINO_PARAMS = asList(R.string.formula_editor_function_pin_default_parameter,
			R.string.formula_editor_function_pin_default_parameter);
	private static final List<Integer> SENSORS_DRONE = asList(R.string.formula_editor_sensor_drone_battery_status,
			R.string.formula_editor_sensor_drone_emergency_state, R.string.formula_editor_sensor_drone_flying,
			R.string.formula_editor_sensor_drone_initialized, R.string.formula_editor_sensor_drone_usb_active,
			R.string.formula_editor_sensor_drone_usb_remaining_time, R.string.formula_editor_sensor_drone_camera_ready,
			R.string.formula_editor_sensor_drone_record_ready, R.string.formula_editor_sensor_drone_recording,
			R.string.formula_editor_sensor_drone_num_frames);
	private static final List<Integer> SENSORS_RASPBERRY = singletonList(R.string.formula_editor_function_raspi_read_pin_value_digital);
	private static final List<Integer> SENSORS_RASPBERRY_PARAMS = singletonList(R.string.formula_editor_function_pin_default_parameter);
	private static final List<Integer> SENSORS_NFC = asList(R.string.formula_editor_nfc_tag_id,
			R.string.formula_editor_nfc_tag_message);
	private static final List<Integer> SENSORS_CAST_GAMEPAD = asList(R.string.formula_editor_sensor_gamepad_a_pressed,
			R.string.formula_editor_sensor_gamepad_b_pressed,
			R.string.formula_editor_sensor_gamepad_up_pressed,
			R.string.formula_editor_sensor_gamepad_down_pressed,
			R.string.formula_editor_sensor_gamepad_left_pressed,
			R.string.formula_editor_sensor_gamepad_right_pressed);

	private static final List<Integer> SENSORS_SPEECH_RECOGNITION = Collections.singletonList(R.string.formula_editor_listening_language_sensor);

	private RecyclerView recyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parent.findViewById(R.id.recycler_view);
		setHasOptionsMenu(true);
		return parent;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initializeAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		Bundle arguments = getArguments();
		if (arguments == null) {
			return;
		}
		AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
		if (appCompatActivity == null) {
			return;
		}
		ActionBar supportActionBar = appCompatActivity.getSupportActionBar();
		if (supportActionBar != null) {
			String title = arguments.getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT);
			supportActionBar.setTitle(title);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
		if (appCompatActivity == null) {
			return;
		}
		appCompatActivity.getMenuInflater().inflate(R.menu.menu_formulareditor_category, menu);

		ActionBar supportActionBar = appCompatActivity.getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	public CategoryListItem chosenItem = null;

	@Override
	public void onItemClick(CategoryListItem item) {
		switch (item.type) {
			case CategoryListRVAdapter.NXT:
				showLegoSensorPortConfigDialog(item.nameResId, Constants.NXT);
				break;
			case CategoryListRVAdapter.EV3:
				showLegoSensorPortConfigDialog(item.nameResId, Constants.EV3);
				break;
			case CategoryListRVAdapter.COLLISION:
				showSelectSpriteDialog();
				break;
			case CategoryListRVAdapter.DEFAULT:
				if (LIST_FUNCTIONS.contains(item.nameResId)) {
					onUserListFunctionSelected(item);
				} else if (R.string.formula_editor_function_regex_assistant == item.nameResId) {
					regularExpressionAssistantActivityOnButtonClick();
				} else {
					FormulaEditorFragment formulaEditorFragment =
							((FormulaEditorFragment) getFragmentManager().findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG));
					if (formulaEditorFragment != null) {
						formulaEditorFragment.setChosenCategoryItem(item);
					}
					getActivity().onBackPressed();
				}
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.wiki_help) {
			onOptionsMenuClick(getTag());
		}
		return true;
	}

	public void onOptionsMenuClick(String tag) {
		String language = getLanguage(getActivity());
		switch (tag) {
			case FUNCTION_TAG:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constants.CATROBAT_FUNCTIONS_WIKI_URL + language)));
				break;
			case LOGIC_TAG:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constants.CATROBAT_LOGIC_WIKI_URL + language)));
				break;
			case OBJECT_TAG:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constants.CATROBAT_OBJECT_WIKI_URL + language)));
				break;
			case SENSOR_TAG:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constants.CATROBAT_SENSORS_WIKI_URL + language)));
				break;
		}
	}

	public String getHelpUrl(String tag, SpriteActivity activity) {
		String language = getLanguage(activity);
		switch (tag) {
			case FUNCTION_TAG:
				return Constants.CATROBAT_FUNCTIONS_WIKI_URL + language;
			case LOGIC_TAG:
				return Constants.CATROBAT_LOGIC_WIKI_URL + language;
			case OBJECT_TAG:
				return Constants.CATROBAT_OBJECT_WIKI_URL + language;
			case SENSOR_TAG:
				return Constants.CATROBAT_SENSORS_WIKI_URL + language;
		}
		return null;
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public String getLanguage(Activity activity) {
		String language = "?language=";
		SharedPreferences sharedPreferences = getSharedPreferences(activity.getApplicationContext());
		String languageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, "");
		Locale mLocale;
		if (languageTag.equals(DEVICE_LANGUAGE)) {
			mLocale = Locale.forLanguageTag(defaultSystemLanguage);
		} else {
			mLocale = Arrays.asList(LANGUAGE_TAGS).contains(languageTag)
					? Locale.forLanguageTag(languageTag)
					: Locale.forLanguageTag(defaultSystemLanguage);
		}
		language = language + mLocale.getLanguage();
		return language;
	}

	private FormulaEditorFragment addResourceToActiveFormulaInFormulaEditor(CategoryListItem categoryListItem) {
		FormulaEditorFragment formulaEditorFragment = null;
		if (getFragmentManager() != null) {
			formulaEditorFragment = ((FormulaEditorFragment) getFragmentManager()
					.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG));
			if (formulaEditorFragment != null) {
				formulaEditorFragment.addResourceToActiveFormula(categoryListItem.nameResId);
			}
		}
		return formulaEditorFragment;
	}

	private void addResourceToActiveFormulaInFormulaEditor(CategoryListItem categoryListItem, UserList lastUserList) {
		addResourceToActiveFormulaInFormulaEditor(categoryListItem).addUserListToActiveFormula(lastUserList.getName());
		getActivity().onBackPressed();
	}

	private void onUserListFunctionSelected(CategoryListItem item) {
		FragmentActivity activity = getActivity();
		TextInputDialog.Builder builder = new TextInputDialog.Builder(activity);
		final List<UserList> projectUserList =
				ProjectManager.getInstance().getCurrentProject().getUserLists();
		final List<UserList> spriteUserList =
				ProjectManager.getInstance().getCurrentSprite().getUserLists();
		insertLastUserListToActiveFormula(item, projectUserList, spriteUserList,
				activity, builder);
	}

	@VisibleForTesting
	public void insertLastUserListToActiveFormula(CategoryListItem categoryListItem,
			List<UserList> projectUserList, List<UserList> spriteUserList,
			FragmentActivity activity,
			TextInputDialog.Builder builder) {

		if (spriteUserList.isEmpty() && projectUserList.isEmpty()) {
			showNewUserListDialog(categoryListItem, projectUserList, spriteUserList,
					activity, builder);
			return;
		}

		if (!spriteUserList.isEmpty()) {
			addResourceToActiveFormulaInFormulaEditor(categoryListItem, spriteUserList.get(spriteUserList.size() - 1));
			return;
		}
		addResourceToActiveFormulaInFormulaEditor(categoryListItem, projectUserList.get(projectUserList.size() - 1));
	}

	private CategoryListItem getRegularExpressionItem() {
		CategoryListItem regexItem = null;

		List<CategoryListItem> itemList = getFunctionItems();
		for (CategoryListItem item : itemList) {
			if (item.nameResId == R.string.formula_editor_function_regex) {
				regexItem = item;
			}
		}
		return regexItem;
	}

	private void regularExpressionAssistantActivityOnButtonClick() {
		int indexOfCorrespondingRegularExpression = 0;
		FormulaEditorFragment formulaEditorFragment = getFormulaEditorFragment();

		if (formulaEditorFragment != null) {
			indexOfCorrespondingRegularExpression =
					formulaEditorFragment.getIndexOfCorrespondingRegularExpression();

			if (indexOfCorrespondingRegularExpression >= 0) {
				formulaEditorFragment.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(indexOfCorrespondingRegularExpression);
			} else {
				addResourceToActiveFormulaInFormulaEditor(getRegularExpressionItem());
			}

			getActivity().onBackPressed();
			openRegularExpressionAssistant();
		}
	}

	private FormulaEditorFragment getFormulaEditorFragment() {
		if (getFragmentManager() != null) {
			return ((FormulaEditorFragment) getFragmentManager()
					.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG));
		}
		return null;
	}

	private void openRegularExpressionAssistant() {
		new RegularExpressionAssistantDialog(getContext(), getFragmentManager()).createAssistant();
	}

	private void showNewUserListDialog(CategoryListItem categoryListItem, List<UserList> projectUserList, List<UserList> spriteUserList,
			FragmentActivity activity, TextInputDialog.Builder builder) {

		AddUserListDialog userListDialog = new AddUserListDialog(builder);
		userListDialog.show(activity.getString(R.string.data_label), activity.getString(R.string.ok), new AddUserListDialog.Callback() {
			@Override
			public void onPositiveButton(DialogInterface dialog, String textInput) {
				UserList userList = new UserList(textInput);
				userListDialog.addUserList(dialog, userList, projectUserList, spriteUserList);
				addResourceToActiveFormulaInFormulaEditor(categoryListItem, userList);
			}

			@Override
			public void onNegativeButton() {
				activity.onBackPressed();
			}
		});
	}

	private void showLegoSensorPortConfigDialog(int itemNameResId, @LegoSensorType final int type) {

		new LegoSensorPortConfigDialog.Builder(getContext(), type, itemNameResId)
				.setPositiveButton(getString(R.string.ok), (dialog, selectedPort, selectedSensor) -> {
					if (type == Constants.NXT) {
						SettingsFragment.setLegoMindstormsNXTSensorMapping(getActivity(),
								(NXTSensor.Sensor) selectedSensor, SettingsFragment.NXT_SENSORS[selectedPort]);
					} else if (type == Constants.EV3) {
						SettingsFragment.setLegoMindstormsEV3SensorMapping(getActivity(),
								(EV3Sensor.Sensor) selectedSensor, SettingsFragment.EV3_SENSORS[selectedPort]);
					}

					FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getFragmentManager()
							.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

					int sensorPortsId = type == Constants.NXT
							? R.array.formula_editor_nxt_ports
							: R.array.formula_editor_ev3_ports;
					TypedArray sensorPorts = getResources().obtainTypedArray(sensorPortsId);
					try {
						int resourceId = sensorPorts.getResourceId(selectedPort, 0);
						if (resourceId != 0) {
							formulaEditor.addResourceToActiveFormula(resourceId);
							formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
						}
					} finally {
						sensorPorts.recycle();
					}
					getActivity().onBackPressed();
				})
				.show();
	}

	private void showSelectSpriteDialog() {
		final Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		List<Sprite> sprites = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();
		final List<Sprite> selectableSprites = new ArrayList<>();

		for (Sprite sprite : sprites) {
			selectableSprites.add(sprite);
		}

		String[] selectableSpriteNames = new String[selectableSprites.size()];
		for (int i = 0; i < selectableSprites.size(); i++) {
			selectableSpriteNames[i] = selectableSprites.get(i).getName();
		}

		new AlertDialog.Builder(getContext())
				.setTitle(R.string.formula_editor_function_collision)
				.setItems(selectableSpriteNames, (dialog, which) -> {
					Sprite selectedSprite = selectableSprites.get(which);

					currentSprite.createCollisionPolygons();
					selectedSprite.createCollisionPolygons();

					((FormulaEditorFragment) getFragmentManager()
							.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG))
							.addCollideFormulaToActiveFormula(selectedSprite.getName());
					getActivity().onBackPressed();
				})
				.show();
	}

	private void initializeAdapter() {
		String argument = getArguments().getString(FRAGMENT_TAG_BUNDLE_ARGUMENT);

		List<CategoryListItem> items;
		if (OBJECT_TAG.equals(argument)) {
			items = getObjectItems();
		} else if (FUNCTION_TAG.equals(argument)) {
			items = getFunctionItems();
		} else if (LOGIC_TAG.equals(argument)) {
			items = getLogicItems();
		} else if (SENSOR_TAG.equals(argument)) {
			items = getSensorItems();
		} else {
			throw new IllegalArgumentException("Argument for CategoryListFragent null or unknown: " + argument);
		}

		CategoryListRVAdapter adapter = new CategoryListRVAdapter(items);
		adapter.setOnItemClickListener(this);
		recyclerView.setAdapter(adapter);
	}

	private List<CategoryListItem> addHeader(List<CategoryListItem> subCategory, String header) {
		subCategory.get(0).header = header;
		return subCategory;
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds) {
		return toCategoryListItems(nameResIds, null, CategoryListRVAdapter.DEFAULT);
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds, List<Integer> paramResIds) {
		return toCategoryListItems(nameResIds, paramResIds, CategoryListRVAdapter.DEFAULT);
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds, @CategoryListItemType int type) {
		return toCategoryListItems(nameResIds, null, type);
	}

	private List<CategoryListItem> toCategoryListItems(List<Integer> nameResIds, @Nullable List<Integer> paramResIds,
			@CategoryListItemType int type) {
		if (paramResIds != null && paramResIds.size() != nameResIds.size()) {
			throw new IllegalArgumentException("Sizes of paramResIds and nameResIds parameters do not fit");
		}

		List<CategoryListItem> result = new ArrayList<>();
		for (int i = 0; i < nameResIds.size(); i++) {
			String param = "";
			if (paramResIds != null) {
				param = getString(paramResIds.get(i));
			}
			result.add(new CategoryListItem(nameResIds.get(i), getString(nameResIds.get(i)) + param, type));
		}

		return result;
	}

	private List<CategoryListItem> getObjectItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(getObjectGeneralPropertiesItems());
		result.addAll(getObjectPhysicalPropertiesItems());

		return result;
	}

	private List<CategoryListItem> getFunctionItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(addHeader(toCategoryListItems(MATH_FUNCTIONS, MATH_PARAMS),
				getString(R.string.formula_editor_functions_maths)));
		result.addAll(addHeader(toCategoryListItems(STRING_FUNCTIONS, STRING_PARAMS),
				getString(R.string.formula_editor_functions_strings)));
		result.addAll(addHeader(toCategoryListItems(LIST_FUNCTIONS, LIST_PARAMS),
				getString(R.string.formula_editor_functions_lists)));

		return result;
	}

	private List<CategoryListItem> getLogicItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(addHeader(toCategoryListItems(LOGIC_BOOL), getString(R.string.formula_editor_logic_boolean)));
		result.addAll(addHeader(toCategoryListItems(LOGIC_COMPARISION),
				getString(R.string.formula_editor_logic_comparison)));
		return result;
	}

	private List<CategoryListItem> getSensorItems() {
		List<CategoryListItem> result = new ArrayList<>();
		result.addAll(getNxtSensorItems());
		result.addAll(getEv3SensorItems());
		result.addAll(getPhiroSensorItems());
		result.addAll(getArduinoSensorItems());
		result.addAll(getDroneSensorItems());
		result.addAll(getRaspberrySensorItems());
		result.addAll(getNfcItems());
		result.addAll(getCastGamepadSensorItems());
		result.addAll(getSpeechRecognitionItems());
		result.addAll(getFaceSensorItems());
		result.addAll(getPoseSensorItems());
		result.addAll(getTextSensorItems());
		result.addAll(getObjectDetectionSensorItems());
		result.addAll(getDeviceSensorItems());
		result.addAll(getTouchDetectionSensorItems());
		result.addAll(getDateTimeSensorItems());
		return result;
	}

	private List<CategoryListItem> getObjectGeneralPropertiesItems() {
		List<Integer> resIds = new ArrayList<>(OBJECT_GENERAL_PROPERTIES);

		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (currentSprite.equals(currentScene.getBackgroundSprite())) {
			resIds.addAll(OBJECT_BACKGROUND);
		} else {
			resIds.addAll(OBJECT_LOOK);
		}
		List<CategoryListItem> result = toCategoryListItems(resIds);
		result.addAll(toCategoryListItems(OBJECT_COLOR_COLLISION.subList(1, 2), OBJECT_COLOR_PARAMS.subList(1, 2)));
		return addHeader(result, getString(R.string.formula_editor_object_look));
	}

	private List<CategoryListItem> getObjectPhysicalPropertiesItems() {
		List<CategoryListItem> result = toCategoryListItems(OBJECT_PHYSICAL_1);
		result.addAll(toCategoryListItems(OBJECT_PHYSICAL_COLLISION, CategoryListRVAdapter.COLLISION));
		result.addAll(toCategoryListItems(OBJECT_PHYSICAL_2));
		result.addAll(toCategoryListItems(OBJECT_COLOR_COLLISION, OBJECT_COLOR_PARAMS));
		return addHeader(result, getString(R.string.formula_editor_object_movement));
	}

	private List<CategoryListItem> getNxtSensorItems() {
		return SettingsFragment.isMindstormsNXTSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_NXT, CategoryListRVAdapter.NXT), getString(R.string.formula_editor_device_lego_nxt))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getEv3SensorItems() {
		return SettingsFragment.isMindstormsEV3SharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_EV3, CategoryListRVAdapter.EV3), getString(R.string.formula_editor_device_lego_ev3))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getPhiroSensorItems() {
		return SettingsFragment.isPhiroSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_PHIRO), getString(R.string.formula_editor_device_phiro))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getArduinoSensorItems() {
		return SettingsFragment.isArduinoSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_ARDUINO, SENSORS_ARDUINO_PARAMS), getString(R.string.formula_editor_device_arduino))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getDroneSensorItems() {
		return SettingsFragment.isDroneSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_DRONE), getString(R.string.formula_editor_device_drone))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getRaspberrySensorItems() {
		return RaspberryPiSettingsFragment.isRaspiSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_RASPBERRY, SENSORS_RASPBERRY_PARAMS), getString(R.string.formula_editor_device_raspberry))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getNfcItems() {
		return SettingsFragment.isNfcSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_NFC), getString(R.string.formula_editor_device_nfc))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getCastGamepadSensorItems() {
		return ProjectManager.getInstance().getCurrentProject().isCastProject()
				? addHeader(toCategoryListItems(SENSORS_CAST_GAMEPAD), getString(R.string.formula_editor_device_cast))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getDeviceSensorItems() {
		List<CategoryListItem> deviceSensorItems = new ArrayList<>(toCategoryListItems(SENSORS_DEFAULT));
		SensorHandler sensorHandler = SensorHandler.getInstance(getActivity());
		deviceSensorItems.addAll(toCategoryListItems(SENSORS_COLOR_AT_XY, SENSORS_COLOR_AT_XY_PARAMS));
		deviceSensorItems.addAll(toCategoryListItems(SENSORS_COLOR_EQUALS_COLOR,
				SENSORS_COLOR_EQUALS_COLOR_PARAMS));
		deviceSensorItems.addAll(sensorHandler.accelerationAvailable() ? toCategoryListItems(SENSORS_ACCELERATION)
				: Collections.emptyList());
		deviceSensorItems.addAll(sensorHandler.inclinationAvailable() ? toCategoryListItems(SENSORS_INCLINATION)
				: Collections.emptyList());
		deviceSensorItems.addAll(sensorHandler.compassAvailable() ? toCategoryListItems(SENSORS_COMPASS)
				: Collections.emptyList());
		deviceSensorItems.addAll(toCategoryListItems(SENSORS_GPS));
		deviceSensorItems.addAll(toCategoryListItems(SENSOR_USER_LANGUAGE));

		return addHeader(deviceSensorItems, getString(R.string.formula_editor_device_sensors));
	}

	private List<CategoryListItem> getTouchDetectionSensorItems() {
		return addHeader(toCategoryListItems(SENSORS_TOUCH, SENSORS_TOUCH_PARAMS), getString(R.string.formula_editor_device_touch_detection));
	}

	private List<CategoryListItem> getDateTimeSensorItems() {
		return addHeader(toCategoryListItems(SENSORS_DATE_TIME), getString(R.string.formula_editor_device_date_and_time));
	}

	private List<CategoryListItem> getSpeechRecognitionItems() {
		return SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_SPEECH_RECOGNITION), getString(R.string.formula_editor_speech_recognition))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getFaceSensorItems() {
		return SettingsFragment.isAIFaceDetectionSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_FACE_DETECTION, SENSORS_FACE_DETECTION_PARAMS),
				getString(R.string.formula_editor_device_face_detection))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getPoseSensorItems() {
		MobileServiceAvailability mobileServiceAvailability = get(MobileServiceAvailability.class);
		boolean isHMSAvailable =
				mobileServiceAvailability.isHmsAvailable(getActivity().getApplicationContext());
		boolean isPoseDetectionEnabled =
				SettingsFragment.isAIPoseDetectionSharedPreferenceEnabled(getActivity().getApplicationContext());
		if (isPoseDetectionEnabled && isHMSAvailable) {
			return addHeader(toCategoryListItems(SENSORS_POSE_DETECTION_HUAWEI,
					SENSORS_POSE_DETECTION_PARAMS_HUAWEI),
					getString(R.string.formula_editor_device_pose_detection));
		} else if (isPoseDetectionEnabled) {
			return addHeader(toCategoryListItems(SENSORS_POSE_DETECTION,
					SENSORS_POSE_DETECTION_PARAMS),
					getString(R.string.formula_editor_device_pose_detection));
		} else {
			return Collections.emptyList();
		}
	}

	private List<CategoryListItem> getTextSensorItems() {
		return SettingsFragment.isAITextRecognitionSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_TEXT_RECOGNITION, SENSORS_TEXT_RECOGNITION_PARAMS),
				getString(R.string.formula_editor_device_text_recognition))
				: Collections.emptyList();
	}

	private List<CategoryListItem> getObjectDetectionSensorItems() {
		return SettingsFragment.isAIObjectDetectionSharedPreferenceEnabled(getActivity().getApplicationContext())
				? addHeader(toCategoryListItems(SENSORS_OBJECT_DETECTION),
				getString(R.string.formula_editor_device_object_recognition))
				: Collections.emptyList();
	}
}
