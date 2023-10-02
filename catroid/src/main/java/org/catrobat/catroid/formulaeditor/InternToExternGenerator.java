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
package org.catrobat.catroid.formulaeditor;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.FormatNumberUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InternToExternGenerator {
	private static final String TAG = InternToExternGenerator.class.getSimpleName();

	private String generatedExternFormulaString;
	private ExternInternRepresentationMapping generatedExternInternRepresentationMapping;
	private Context context;

	private static final HashMap<String, Integer> INTERN_EXTERN_LANGUAGE_CONVERTER_MAP = new HashMap<String, Integer>();
	static {
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.DIVIDE.name(), R.string.formula_editor_operator_divide);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.MULT.name(), R.string.formula_editor_operator_mult);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.MINUS.name(), R.string.formula_editor_operator_minus);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.PLUS.name(), R.string.formula_editor_operator_plus);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(".", R.string.formula_editor_decimal_mark);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.SIN.name(), R.string.formula_editor_function_sin);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.COS.name(), R.string.formula_editor_function_cos);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.TAN.name(), R.string.formula_editor_function_tan);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.LN.name(), R.string.formula_editor_function_ln);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.LOG.name(), R.string.formula_editor_function_log);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.PI.name(), R.string.formula_editor_function_pi);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.SQRT.name(), R.string.formula_editor_function_sqrt);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.RAND.name(), R.string.formula_editor_function_rand);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ABS.name(), R.string.formula_editor_function_abs);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ROUND.name(), R.string.formula_editor_function_round);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.MOD.name(), R.string.formula_editor_function_mod);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ARCSIN.name(), R.string.formula_editor_function_arcsin);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ARCCOS.name(), R.string.formula_editor_function_arccos);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ARCTAN.name(), R.string.formula_editor_function_arctan);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ARCTAN2.name(), R.string.formula_editor_function_arctan2);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.EXP.name(), R.string.formula_editor_function_exp);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.POWER.name(), R.string.formula_editor_function_power);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.FLOOR.name(), R.string.formula_editor_function_floor);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.CEIL.name(), R.string.formula_editor_function_ceil);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.MAX.name(), R.string.formula_editor_function_max);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.MIN.name(), R.string.formula_editor_function_min);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.IF_THEN_ELSE.name(), R.string.formula_editor_function_if_then_else);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.TRUE.name(), R.string.formula_editor_function_true);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.FALSE.name(), R.string.formula_editor_function_false);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.LENGTH.name(), R.string.formula_editor_function_length);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.NUMBER_OF_ITEMS.name(), R.string.formula_editor_function_number_of_items);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.LETTER.name(), R.string.formula_editor_function_letter);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.SUBTEXT.name(), R.string.formula_editor_function_subtext);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.JOIN.name(), R.string.formula_editor_function_join);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.JOIN3.name(), R.string.formula_editor_function_join3);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.REGEX.name(), R.string.formula_editor_function_regex);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.FLATTEN.name(), R.string.formula_editor_function_flatten);

		//DRONE SENSORS
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_BATTERY_STATUS.name(), R.string.formula_editor_sensor_drone_battery_status);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_EMERGENCY_STATE.name(), R.string.formula_editor_sensor_drone_emergency_state);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_FLYING.name(), R.string.formula_editor_sensor_drone_flying);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_INITIALIZED.name(), R.string.formula_editor_sensor_drone_initialized);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_USB_ACTIVE.name(), R.string.formula_editor_sensor_drone_usb_active);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_USB_REMAINING_TIME.name(), R.string.formula_editor_sensor_drone_usb_remaining_time);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_CAMERA_READY.name(), R.string.formula_editor_sensor_drone_camera_ready);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_RECORD_READY.name(), R.string.formula_editor_sensor_drone_record_ready);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_RECORDING.name(), R.string.formula_editor_sensor_drone_recording);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DRONE_NUM_FRAMES.name(), R.string.formula_editor_sensor_drone_num_frames);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ARDUINODIGITAL.name(), R.string
				.formula_editor_function_arduino_read_pin_value_digital);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ARDUINOANALOG.name(), R.string.formula_editor_function_arduino_read_pin_value_analog);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.RASPIDIGITAL.name(), R.string
				.formula_editor_function_raspi_read_pin_value_digital);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.FINGER_X.name(), R.string.formula_editor_function_finger_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.FINGER_Y.name(), R.string.formula_editor_function_finger_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.FINGER_TOUCHED.name(), R.string.formula_editor_function_is_finger_touching);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.MULTI_FINGER_X.name(), R.string.formula_editor_function_multi_finger_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.MULTI_FINGER_Y.name(), R.string.formula_editor_function_multi_finger_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.MULTI_FINGER_TOUCHED.name(), R.string.formula_editor_function_is_multi_finger_touching);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.INDEX_CURRENT_TOUCH.name(), R.string.formula_editor_function_index_of_current_touch);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LAST_FINGER_INDEX.name(), R.string.formula_editor_function_index_of_last_finger);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NUMBER_CURRENT_TOUCHES.name(), R.string.formula_editor_function_number_of_current_touches);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.LIST_ITEM.name(), R.string.formula_editor_function_list_item);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.CONTAINS.name(), R.string.formula_editor_function_contains);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.INDEX_OF_ITEM.name(), R.string.formula_editor_function_index_of_item);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.COLOR_AT_XY.name(), R.string.formula_editor_sensor_color_at_x_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.COLOR_EQUALS_COLOR.name(),
				R.string.formula_editor_sensor_color_equals_color);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.X_ACCELERATION.name(), R.string.formula_editor_sensor_x_acceleration);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.Y_ACCELERATION.name(), R.string.formula_editor_sensor_y_acceleration);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.Z_ACCELERATION.name(), R.string.formula_editor_sensor_z_acceleration);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.COMPASS_DIRECTION.name(), R.string.formula_editor_sensor_compass_direction);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LATITUDE.name(), R.string.formula_editor_sensor_latitude);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LONGITUDE.name(), R.string.formula_editor_sensor_longitude);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LOCATION_ACCURACY.name(), R.string.formula_editor_sensor_location_accuracy);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.ALTITUDE.name(), R.string.formula_editor_sensor_altitude);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.USER_LANGUAGE.name(),
				R.string.formula_editor_sensor_user_language);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.X_INCLINATION.name(),
				R.string.formula_editor_sensor_x_inclination);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.Y_INCLINATION.name(),
				R.string.formula_editor_sensor_y_inclination);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.FACE_DETECTED.name(),
				R.string.formula_editor_sensor_face_detected);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.FACE_SIZE.name(), R.string.formula_editor_sensor_face_size);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.FACE_X.name(),
				R.string.formula_editor_sensor_face_x_position);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.FACE_Y.name(),
				R.string.formula_editor_sensor_face_y_position);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.SECOND_FACE_DETECTED.name(),
				R.string.formula_editor_sensor_second_face_detected);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.SECOND_FACE_SIZE.name(),
				R.string.formula_editor_sensor_second_face_size);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.SECOND_FACE_X.name(),
				R.string.formula_editor_sensor_second_face_x_position);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.SECOND_FACE_Y.name(),
				R.string.formula_editor_sensor_second_face_y_position);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.HEAD_TOP_X.name(), R.string.formula_editor_sensor_head_top_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.HEAD_TOP_Y.name(), R.string.formula_editor_sensor_head_top_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NECK_X.name(), R.string.formula_editor_sensor_neck_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NECK_Y.name(), R.string.formula_editor_sensor_neck_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NOSE_X.name(), R.string.formula_editor_sensor_nose_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NOSE_Y.name(), R.string.formula_editor_sensor_nose_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EYE_INNER_X.name(), R.string.formula_editor_sensor_left_eye_inner_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EYE_INNER_Y.name(), R.string.formula_editor_sensor_left_eye_inner_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EYE_CENTER_X.name(), R.string.formula_editor_sensor_left_eye_center_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EYE_CENTER_Y.name(), R.string.formula_editor_sensor_left_eye_center_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EYE_OUTER_X.name(), R.string.formula_editor_sensor_left_eye_outer_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EYE_OUTER_Y.name(), R.string.formula_editor_sensor_left_eye_outer_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EYE_INNER_X.name(), R.string.formula_editor_sensor_right_eye_inner_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EYE_INNER_Y.name(), R.string.formula_editor_sensor_right_eye_inner_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EYE_CENTER_X.name(), R.string.formula_editor_sensor_right_eye_center_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EYE_CENTER_Y.name(), R.string.formula_editor_sensor_right_eye_center_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EYE_OUTER_X.name(), R.string.formula_editor_sensor_right_eye_outer_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EYE_OUTER_Y.name(), R.string.formula_editor_sensor_right_eye_outer_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EAR_X.name(), R.string.formula_editor_sensor_left_ear_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_EAR_Y.name(), R.string.formula_editor_sensor_left_ear_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EAR_X.name(), R.string.formula_editor_sensor_right_ear_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_EAR_Y.name(), R.string.formula_editor_sensor_right_ear_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.MOUTH_LEFT_CORNER_X.name(), R.string.formula_editor_sensor_mouth_left_corner_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.MOUTH_LEFT_CORNER_Y.name(), R.string.formula_editor_sensor_mouth_left_corner_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.MOUTH_RIGHT_CORNER_X.name(), R.string.formula_editor_sensor_mouth_right_corner_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.MOUTH_RIGHT_CORNER_Y.name(), R.string.formula_editor_sensor_mouth_right_corner_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_SHOULDER_X.name(), R.string.formula_editor_sensor_left_shoulder_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_SHOULDER_Y.name(), R.string.formula_editor_sensor_left_shoulder_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_SHOULDER_X.name(), R.string.formula_editor_sensor_right_shoulder_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_SHOULDER_Y.name(), R.string.formula_editor_sensor_right_shoulder_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_ELBOW_X.name(), R.string.formula_editor_sensor_left_elbow_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_ELBOW_Y.name(), R.string.formula_editor_sensor_left_elbow_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_ELBOW_X.name(), R.string.formula_editor_sensor_right_elbow_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_ELBOW_Y.name(), R.string.formula_editor_sensor_right_elbow_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_WRIST_X.name(), R.string.formula_editor_sensor_left_wrist_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_WRIST_Y.name(), R.string.formula_editor_sensor_left_wrist_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_WRIST_X.name(), R.string.formula_editor_sensor_right_wrist_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_WRIST_Y.name(), R.string.formula_editor_sensor_right_wrist_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_PINKY_X.name(), R.string.formula_editor_sensor_left_pinky_knuckle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_PINKY_Y.name(), R.string.formula_editor_sensor_left_pinky_knuckle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_PINKY_X.name(), R.string.formula_editor_sensor_right_pinky_knuckle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_PINKY_Y.name(), R.string.formula_editor_sensor_right_pinky_knuckle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_INDEX_X.name(), R.string.formula_editor_sensor_left_index_knuckle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_INDEX_Y.name(), R.string.formula_editor_sensor_left_index_knuckle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_INDEX_X.name(), R.string.formula_editor_sensor_right_index_knuckle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_INDEX_Y.name(), R.string.formula_editor_sensor_right_index_knuckle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_THUMB_X.name(), R.string.formula_editor_sensor_left_thumb_knuckle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_THUMB_Y.name(), R.string.formula_editor_sensor_left_thumb_knuckle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_THUMB_X.name(), R.string.formula_editor_sensor_right_thumb_knuckle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_THUMB_Y.name(), R.string.formula_editor_sensor_right_thumb_knuckle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_HIP_X.name(), R.string.formula_editor_sensor_left_hip_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_HIP_Y.name(), R.string.formula_editor_sensor_left_hip_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_HIP_X.name(), R.string.formula_editor_sensor_right_hip_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_HIP_Y.name(), R.string.formula_editor_sensor_right_hip_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_KNEE_X.name(), R.string.formula_editor_sensor_left_knee_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_KNEE_Y.name(), R.string.formula_editor_sensor_left_knee_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_KNEE_X.name(), R.string.formula_editor_sensor_right_knee_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_KNEE_Y.name(), R.string.formula_editor_sensor_right_knee_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_ANKLE_X.name(), R.string.formula_editor_sensor_left_ankle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_ANKLE_Y.name(), R.string.formula_editor_sensor_left_ankle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_ANKLE_X.name(), R.string.formula_editor_sensor_right_ankle_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_ANKLE_Y.name(), R.string.formula_editor_sensor_right_ankle_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_HEEL_X.name(), R.string.formula_editor_sensor_left_heel_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_HEEL_Y.name(), R.string.formula_editor_sensor_left_heel_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_HEEL_X.name(), R.string.formula_editor_sensor_right_heel_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_HEEL_Y.name(), R.string.formula_editor_sensor_right_heel_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_FOOT_INDEX_X.name(), R.string.formula_editor_sensor_left_foot_index_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LEFT_FOOT_INDEX_Y.name(), R.string.formula_editor_sensor_left_foot_index_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_FOOT_INDEX_X.name(), R.string.formula_editor_sensor_right_foot_index_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.RIGHT_FOOT_INDEX_Y.name(), R.string.formula_editor_sensor_right_foot_index_y);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TEXT_FROM_CAMERA.name(),
				R.string.formula_editor_sensor_text_from_camera);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TEXT_BLOCKS_NUMBER.name(),
				R.string.formula_editor_sensor_text_blocks_number);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TEXT_BLOCK_X.name(),
				R.string.formula_editor_function_text_block_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TEXT_BLOCK_Y.name(),
				R.string.formula_editor_function_text_block_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TEXT_BLOCK_SIZE.name(),
				R.string.formula_editor_function_text_block_size);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TEXT_BLOCK_FROM_CAMERA.name(),
				R.string.formula_editor_function_text_block_from_camera);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TEXT_BLOCK_LANGUAGE_FROM_CAMERA.name(),
				R.string.formula_editor_function_text_block_language_from_camera);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.GAMEPAD_A_PRESSED.name(),
				R.string.formula_editor_sensor_gamepad_a_pressed);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.GAMEPAD_B_PRESSED.name(),
				R.string.formula_editor_sensor_gamepad_b_pressed);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.GAMEPAD_UP_PRESSED.name(),
				R.string.formula_editor_sensor_gamepad_up_pressed);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.GAMEPAD_DOWN_PRESSED.name(),
				R.string.formula_editor_sensor_gamepad_down_pressed);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.GAMEPAD_LEFT_PRESSED.name(),
				R.string.formula_editor_sensor_gamepad_left_pressed);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.GAMEPAD_RIGHT_PRESSED.name(),
				R.string.formula_editor_sensor_gamepad_right_pressed);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NXT_SENSOR_1.name(), R.string.formula_editor_sensor_lego_nxt_1);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NXT_SENSOR_2.name(), R.string.formula_editor_sensor_lego_nxt_2);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NXT_SENSOR_3.name(), R.string.formula_editor_sensor_lego_nxt_3);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NXT_SENSOR_4.name(), R.string.formula_editor_sensor_lego_nxt_4);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.EV3_SENSOR_1.name(), R.string.formula_editor_sensor_lego_ev3_1);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.EV3_SENSOR_2.name(), R.string.formula_editor_sensor_lego_ev3_2);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.EV3_SENSOR_3.name(), R.string.formula_editor_sensor_lego_ev3_3);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.EV3_SENSOR_4.name(), R.string.formula_editor_sensor_lego_ev3_4);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.PHIRO_FRONT_LEFT.name(), R.string.formula_editor_phiro_sensor_front_left);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.PHIRO_FRONT_RIGHT.name(), R.string.formula_editor_phiro_sensor_front_right);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.PHIRO_SIDE_LEFT.name(), R.string.formula_editor_phiro_sensor_side_left);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.PHIRO_SIDE_RIGHT.name(), R.string.formula_editor_phiro_sensor_side_right);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.PHIRO_BOTTOM_LEFT.name(), R.string.formula_editor_phiro_sensor_bottom_left);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.PHIRO_BOTTOM_RIGHT.name(), R.string.formula_editor_phiro_sensor_bottom_right);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NFC_TAG_ID.name(), R.string.formula_editor_nfc_tag_id);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.NFC_TAG_MESSAGE.name(), R.string.formula_editor_nfc_tag_message);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LOUDNESS.name(), R.string.formula_editor_sensor_loudness);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.STAGE_WIDTH.name(),
				R.string.formula_editor_sensor_stage_width);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.STAGE_HEIGHT.name(),
				R.string.formula_editor_sensor_stage_height);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TIMER.name(), R.string.formula_editor_sensor_timer);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DATE_YEAR.name(), R.string.formula_editor_sensor_date_year);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DATE_MONTH.name(), R.string.formula_editor_sensor_date_month);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DATE_DAY.name(), R.string.formula_editor_sensor_date_day);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.DATE_WEEKDAY.name(), R.string.formula_editor_sensor_date_weekday);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TIME_HOUR.name(), R.string.formula_editor_sensor_time_hour);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TIME_MINUTE.name(), R.string.formula_editor_sensor_time_minute);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.TIME_SECOND.name(), R.string.formula_editor_sensor_time_second);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_X.name(), R.string.formula_editor_object_x);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_Y.name(), R.string.formula_editor_object_y);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_TRANSPARENCY.name(), R.string.formula_editor_object_transparency);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_BRIGHTNESS.name(), R.string.formula_editor_object_brightness);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_COLOR.name(), R.string.formula_editor_object_color);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_SIZE.name(), R.string.formula_editor_object_size);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.MOTION_DIRECTION.name(), R.string.formula_editor_object_rotation);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.LOOK_DIRECTION.name(), R.string.formula_editor_object_rotation_look);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_LAYER.name(), R.string.formula_editor_object_layer);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.COLLIDES_WITH_EDGE.name(), R.string
				.formula_editor_function_collides_with_edge);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.COLLIDES_WITH_FINGER.name(), R.string
				.formula_editor_function_touched);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.COLLIDES_WITH_COLOR.name(), R.string
				.formula_editor_function_collides_with_color);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.COLOR_TOUCHES_COLOR.name(), R.string.formula_editor_function_color_touches_color);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_X_VELOCITY.name(), R.string.formula_editor_object_x_velocity);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_Y_VELOCITY.name(), R.string.formula_editor_object_y_velocity);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_ANGULAR_VELOCITY.name(), R.string.formula_editor_object_angular_velocity);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_LOOK_NUMBER.name(), R.string
				.formula_editor_object_look_number);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_LOOK_NAME.name(), R.string
				.formula_editor_object_look_name);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_NUMBER_OF_LOOKS.name(), R.string.formula_editor_object_number_of_looks);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_BACKGROUND_NUMBER.name(), R.string
				.formula_editor_object_background_number);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_BACKGROUND_NAME.name(), R.string
				.formula_editor_object_background_name);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.OBJECT_DISTANCE_TO.name(), R.string.formula_editor_object_distance_to);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.LOGICAL_NOT.name(), R.string.formula_editor_logic_not);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.NOT_EQUAL.name(), R.string.formula_editor_logic_notequal);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.EQUAL.name(), R.string.formula_editor_logic_equal);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.GREATER_OR_EQUAL.name(), R.string.formula_editor_logic_greaterequal);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.GREATER_THAN.name(), R.string.formula_editor_logic_greaterthan);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.LOGICAL_AND.name(), R.string.formula_editor_logic_and);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.LOGICAL_OR.name(), R.string.formula_editor_logic_or);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.SMALLER_OR_EQUAL.name(),
				R.string.formula_editor_logic_leserequal);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Operators.SMALLER_THAN.name(),
				R.string.formula_editor_logic_lesserthan);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Sensors.SPEECH_RECOGNITION_LANGUAGE.name(),
				R.string.formula_editor_listening_language_sensor);

		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.ID_OF_DETECTED_OBJECT.name(),
				R.string.formula_editor_function_get_id_of_detected_object);
		INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.OBJECT_WITH_ID_VISIBLE.name(),
				R.string.formula_editor_function_object_with_id_visible);
	}
	public InternToExternGenerator(Context context) {
		this.context = context;
		generatedExternFormulaString = "";
		generatedExternInternRepresentationMapping = new ExternInternRepresentationMapping();
	}

	public void generateExternStringAndMapping(List<InternToken> internTokenFormula) {
		generateStringAndMappingInternal(internTokenFormula, false);
	}

	public void trimExternString(List<InternToken> internTokenFormula) {
		generateStringAndMappingInternal(internTokenFormula, true);
	}

	private void generateStringAndMappingInternal(List<InternToken> internTokenFormula, boolean trimNumbers) {
		Log.i(TAG, "generateExternStringAndMapping:enter");

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		for (InternToken internToken : internTokenFormula) {
			internTokenList.add(internToken);
		}

		generatedExternInternRepresentationMapping = new ExternInternRepresentationMapping();

		StringBuilder externalFormulaString = new StringBuilder();
		InternToken currentToken = null;
		InternToken nextToken = null;
		String externTokenString;
		int externStringStartIndex;
		int externStringEndIndex;

		int internTokenListIndex = 0;

		while (!internTokenList.isEmpty()) {
			if (appendWhiteSpace(currentToken, nextToken)) {
				externalFormulaString.append(' ');
			}
			externStringStartIndex = externalFormulaString.length();
			currentToken = internTokenList.get(0);

			if (internTokenList.size() < 2) {
				nextToken = null;
			} else {
				nextToken = internTokenList.get(1);
			}

			externTokenString = generateExternStringFromToken(currentToken, trimNumbers);
			externalFormulaString.append(externTokenString);
			externStringEndIndex = externalFormulaString.length();

			generatedExternInternRepresentationMapping.putMapping(externStringStartIndex, externStringEndIndex,
					internTokenListIndex);

			internTokenList.remove(0);
			internTokenListIndex++;
		}

		externalFormulaString.append(' ');
		generatedExternFormulaString = externalFormulaString.toString();
	}

	private String generateExternStringFromToken(InternToken internToken, boolean trimNumbers) {
		switch (internToken.getInternTokenType()) {
			case NUMBER:
				String number = internToken.getTokenStringValue();
				return getExternStringForNumber(number, trimNumbers);

			case OPERATOR:

				String returnvalue = internToken.getTokenStringValue();
				String mappingValue = getExternStringForInternTokenValue(internToken.getTokenStringValue(), context);

				return mappingValue == null ? returnvalue : mappingValue;

			case BRACKET_OPEN:
			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				return "(";
			case BRACKET_CLOSE:
			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				return ")";
			case FUNCTION_PARAMETER_DELIMITER:
				return ",";
			case USER_VARIABLE:
				return "\"" + internToken.getTokenStringValue() + "\"";
			case USER_LIST:
				return "*" + internToken.getTokenStringValue() + "*";
			case USER_DEFINED_BRICK_INPUT:
				return "[" + internToken.getTokenStringValue() + "]";
			case STRING:
				return "\'" + internToken.getTokenStringValue() + "\'";
			case COLLISION_FORMULA:
				String collisionTag = CatroidApplication.getAppContext().getString(R.string
						.formula_editor_function_collision);
				return collisionTag + "(" + internToken.getTokenStringValue() + ")";

			default:
				return getExternStringForInternTokenValue(internToken.getTokenStringValue(), context);
		}
	}

	private String getExternStringForNumber(String number, boolean trimNumbers) {

		if (trimNumbers) {
			number = getNumberExponentRepresentation(number);
		}

		if (!number.contains(".")) {
			return number;
		}

		String left = number.substring(0, number.indexOf('.'));
		String right = number.substring(number.indexOf('.') + 1);

		return left + getExternStringForInternTokenValue(".", context) + right;
	}

	private String getNumberExponentRepresentation(String number) {

		Double value = Double.parseDouble(number);
		String numberToCheck = String.valueOf(value);

		if (value < 1 && numberToCheck.contains("E")) {
			number = numberToCheck;
		} else {
			number = FormatNumberUtil.cutTrailingZeros(number);
		}

		return number;
	}

	private boolean appendWhiteSpace(InternToken currentToken, InternToken nextToken) {
		if (currentToken == null) {
			return false;
		}
		if (nextToken == null) {
			return true;
		}

		switch (nextToken.getInternTokenType()) {
			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				return false;
		}
		return true;
	}

	public String getGeneratedExternFormulaString() {
		return generatedExternFormulaString;
	}

	public ExternInternRepresentationMapping getGeneratedExternInternRepresentationMapping() {
		return generatedExternInternRepresentationMapping;
	}

	private String getExternStringForInternTokenValue(String internTokenValue, Context context) {
		Integer stringResourceID = INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.get(internTokenValue);
		if (stringResourceID == null) {
			return null;
		}
		return context.getString(stringResourceID);
	}

	public static int getMappedString(String token) {
		return INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.get(token);
	}

	public static void setInternExternLanguageConverterMap(Sensors sensor, Integer output) {
		InternToExternGenerator.INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(sensor.name(), output);
	}
}
