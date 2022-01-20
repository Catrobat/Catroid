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

import org.catrobat.catroid.R;

import java.util.LinkedList;
import java.util.List;

import static org.catrobat.catroid.formulaeditor.InternTokenType.BRACKET_CLOSE;
import static org.catrobat.catroid.formulaeditor.InternTokenType.BRACKET_OPEN;
import static org.catrobat.catroid.formulaeditor.InternTokenType.COLLISION_FORMULA;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_NAME;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETER_DELIMITER;
import static org.catrobat.catroid.formulaeditor.InternTokenType.NUMBER;
import static org.catrobat.catroid.formulaeditor.InternTokenType.OPERATOR;
import static org.catrobat.catroid.formulaeditor.InternTokenType.PERIOD;
import static org.catrobat.catroid.formulaeditor.InternTokenType.SENSOR;
import static org.catrobat.catroid.formulaeditor.InternTokenType.STRING;
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_DEFINED_BRICK_INPUT;
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_LIST;
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_VARIABLE;

public class InternFormulaKeyboardAdapter {

	public static final int FORMULA_EDITOR_USER_VARIABLE_RESOURCE_ID = 0;
	public static final int FORMULA_EDITOR_USER_LIST_RESOURCE_ID = 1;
	public static final int FORMULA_EDITOR_USER_DEFINED_BRICK_INPUT_RESOURCE_ID = 2;
	public static final int FORMULA_EDITOR_COLLIDE_RESOURCE_ID = 3;

	public List<InternToken> createInternTokenListByResourceId(int resource, String name) {

		if ((resource == FORMULA_EDITOR_USER_VARIABLE_RESOURCE_ID) && !name.isEmpty()) {
			return buildUserVariable(name);
		}

		if ((resource == FORMULA_EDITOR_USER_LIST_RESOURCE_ID) && !name.isEmpty()) {
			return buildUserList(name);
		}

		if ((resource == FORMULA_EDITOR_USER_DEFINED_BRICK_INPUT_RESOURCE_ID) && !name.isEmpty()) {
			return buildUserDefinedBrickInput(name);
		}

		if ((resource == FORMULA_EDITOR_COLLIDE_RESOURCE_ID) && !name.isEmpty()) {
			return buildCollideWithFormula(name);
		}

		if ((resource == R.id.formula_editor_keyboard_string)) {
			return buildString(name);
		}

		switch (resource) {
			case R.id.formula_editor_keyboard_0:
				return buildNumber("0");
			case R.id.formula_editor_keyboard_1:
				return buildNumber("1");
			case R.id.formula_editor_keyboard_2:
				return buildNumber("2");
			case R.id.formula_editor_keyboard_3:
				return buildNumber("3");
			case R.id.formula_editor_keyboard_4:
				return buildNumber("4");
			case R.id.formula_editor_keyboard_5:
				return buildNumber("5");
			case R.id.formula_editor_keyboard_6:
				return buildNumber("6");
			case R.id.formula_editor_keyboard_7:
				return buildNumber("7");
			case R.id.formula_editor_keyboard_8:
				return buildNumber("8");
			case R.id.formula_editor_keyboard_9:
				return buildNumber("9");

			case R.string.formula_editor_function_sin:
				return buildSingleParameterFunction(Functions.SIN, NUMBER, "90");
			case R.string.formula_editor_function_cos:
				return buildSingleParameterFunction(Functions.COS, NUMBER, "360");
			case R.string.formula_editor_function_tan:
				return buildSingleParameterFunction(Functions.TAN, NUMBER, "45");
			case R.string.formula_editor_function_ln:
				return buildSingleParameterFunction(Functions.LN, NUMBER, "2.718281828459");
			case R.string.formula_editor_function_log:
				return buildSingleParameterFunction(Functions.LOG, NUMBER, "10");
			case R.string.formula_editor_function_pi:
				return buildFunctionWithoutParametersAndBrackets(Functions.PI);
			case R.string.formula_editor_function_sqrt:
				return buildSingleParameterFunction(Functions.SQRT, NUMBER, "4");
			case R.string.formula_editor_function_rand:
				return buildDoubleParameterFunction(Functions.RAND,
						NUMBER, "1",
						NUMBER, "6");
			case R.string.formula_editor_function_abs:
				return buildSingleParameterFunction(Functions.ABS, NUMBER, "1");
			case R.string.formula_editor_function_round:
				return buildSingleParameterFunction(Functions.ROUND, NUMBER, "1.6");
			case R.string.formula_editor_function_mod:
				return buildDoubleParameterFunction(Functions.MOD,
						NUMBER, "3",
						NUMBER, "2");
			case R.string.formula_editor_function_arcsin:
				return buildSingleParameterFunction(Functions.ARCSIN, NUMBER, "0.5");
			case R.string.formula_editor_function_arccos:
				return buildSingleParameterFunction(Functions.ARCCOS, NUMBER, "0");
			case R.string.formula_editor_function_arctan:
				return buildSingleParameterFunction(Functions.ARCTAN, NUMBER, "1");
			case R.string.formula_editor_function_arctan2:
				return buildDoubleParameterFunctionWithNegativeValues(Functions.ARCTAN2,
						true, NUMBER, "1",
						false, NUMBER, "0");
			case R.string.formula_editor_function_exp:
				return buildSingleParameterFunction(Functions.EXP, NUMBER, "1");
			case R.string.formula_editor_function_power:
				return buildDoubleParameterFunction(Functions.POWER,
						NUMBER, "2",
						NUMBER, "3");
			case R.string.formula_editor_function_floor:
				return buildSingleParameterFunction(Functions.FLOOR, NUMBER, "0.7");
			case R.string.formula_editor_function_ceil:
				return buildSingleParameterFunction(Functions.CEIL, NUMBER, "0.3");
			case R.string.formula_editor_function_max:
				return buildDoubleParameterFunction(Functions.MAX,
						NUMBER, "5",
						NUMBER, "4");
			case R.string.formula_editor_function_min:
				return buildDoubleParameterFunction(Functions.MIN,
						NUMBER, "7",
						NUMBER, "2");
			case R.string.formula_editor_function_if_then_else:
				return buildTripleParameterFunction(Functions.IF_THEN_ELSE,
						FUNCTION_NAME, Functions.getFunctionByValue("FALSE").toString(),
						NUMBER, "2",
						NUMBER, "3");
			case R.string.formula_editor_function_true:
				return buildFunctionWithoutParametersAndBrackets(Functions.TRUE);
			case R.string.formula_editor_function_false:
				return buildFunctionWithoutParametersAndBrackets(Functions.FALSE);
			case R.string.formula_editor_function_letter:
				return buildDoubleParameterFunction(Functions.LETTER, NUMBER, "1",
						STRING, "hello world");
			case R.string.formula_editor_function_length:
				return buildSingleParameterFunction(Functions.LENGTH, STRING, "hello world");
			case R.string.formula_editor_function_number_of_items:
				return buildSingleParameterFunction(Functions.NUMBER_OF_ITEMS, USER_LIST, "list name");
			case R.string.formula_editor_function_join:
				return buildDoubleParameterFunction(Functions.JOIN, STRING, "hello",
						STRING, " world");
			case R.string.formula_editor_function_join3:
				return buildTripleParameterFunction(Functions.JOIN3, STRING, "hello",
						STRING, " world", STRING, "!");
			case R.string.formula_editor_function_regex:
				return buildDoubleParameterFunction(Functions.REGEX, STRING, " an? ([^ .]+)",
						STRING, "I am a panda.");
			case R.string.formula_editor_function_list_item:
				return buildDoubleParameterFunction(Functions.LIST_ITEM, NUMBER, "1",
						USER_LIST, "list name");
			case R.string.formula_editor_function_contains:
				return buildDoubleParameterFunction(Functions.CONTAINS,
						USER_LIST, "list name",
						NUMBER, "1");
			case R.string.formula_editor_function_index_of_item:
				return buildDoubleParameterFunction(Functions.INDEX_OF_ITEM,
						NUMBER, "1",
						USER_LIST, "list_name");
			case R.string.formula_editor_function_flatten:
				return buildSingleParameterFunction(Functions.FLATTEN, STRING, "list_name");
			case R.string.formula_editor_function_arduino_read_pin_value_digital:
				return buildSingleParameterFunction(Functions.ARDUINODIGITAL, NUMBER, "0");
			case R.string.formula_editor_function_arduino_read_pin_value_analog:
				return buildSingleParameterFunction(Functions.ARDUINOANALOG, NUMBER, "0");

			case R.string.formula_editor_function_raspi_read_pin_value_digital:
				return buildSingleParameterFunction(Functions.RASPIDIGITAL, NUMBER, "0");

			case R.string.formula_editor_function_finger_x:
				return buildSensor(Sensors.FINGER_X);
			case R.string.formula_editor_function_finger_y:
				return buildSensor(Sensors.FINGER_Y);
			case R.string.formula_editor_function_is_finger_touching:
				return buildSensor(Sensors.FINGER_TOUCHED);
			case R.string.formula_editor_function_multi_finger_x:
				return buildSingleParameterFunction(Functions.MULTI_FINGER_X, NUMBER, "1");
			case R.string.formula_editor_function_multi_finger_y:
				return buildSingleParameterFunction(Functions.MULTI_FINGER_Y, NUMBER, "1");
			case R.string.formula_editor_function_is_multi_finger_touching:
				return buildSingleParameterFunction(Functions.MULTI_FINGER_TOUCHED, NUMBER, "1");
			case R.string.formula_editor_function_index_of_last_finger:
				return buildSensor(Sensors.LAST_FINGER_INDEX);
			case R.string.formula_editor_function_number_of_current_touches:
				return buildSensor(Sensors.NUMBER_CURRENT_TOUCHES);
			case R.string.formula_editor_function_index_of_current_touch:
				return buildSingleParameterFunction(Functions.INDEX_CURRENT_TOUCH, NUMBER, "1");
			case R.string.formula_editor_sensor_color_at_x_y:
				return buildDoubleParameterFunction(Functions.COLOR_AT_XY, NUMBER, "100", NUMBER, "200");
			case R.string.formula_editor_sensor_color_equals_color:
				return buildTripleParameterFunction(Functions.COLOR_EQUALS_COLOR, STRING, "#ff0000",
						STRING, "#fe0000", NUMBER, "1");
			case R.string.formula_editor_sensor_x_acceleration:
				return buildSensor(Sensors.X_ACCELERATION);
			case R.string.formula_editor_sensor_y_acceleration:
				return buildSensor(Sensors.Y_ACCELERATION);
			case R.string.formula_editor_sensor_z_acceleration:
				return buildSensor(Sensors.Z_ACCELERATION);
			case R.string.formula_editor_sensor_compass_direction:
				return buildSensor(Sensors.COMPASS_DIRECTION);
			case R.string.formula_editor_sensor_latitude:
				return buildSensor(Sensors.LATITUDE);
			case R.string.formula_editor_sensor_longitude:
				return buildSensor(Sensors.LONGITUDE);
			case R.string.formula_editor_sensor_location_accuracy:
				return buildSensor(Sensors.LOCATION_ACCURACY);
			case R.string.formula_editor_sensor_altitude:
				return buildSensor(Sensors.ALTITUDE);
			case R.string.formula_editor_sensor_user_language:
				return buildSensor(Sensors.USER_LANGUAGE);
			case R.string.formula_editor_sensor_x_inclination:
				return buildSensor(Sensors.X_INCLINATION);
			case R.string.formula_editor_sensor_y_inclination:
				return buildSensor(Sensors.Y_INCLINATION);
			case R.string.formula_editor_sensor_loudness:
				return buildSensor(Sensors.LOUDNESS);
			case R.string.formula_editor_sensor_face_detected:
				return buildSensor(Sensors.FACE_DETECTED);
			case R.string.formula_editor_sensor_face_size:
				return buildSensor(Sensors.FACE_SIZE);
			case R.string.formula_editor_sensor_face_x_position:
				return buildSensor(Sensors.FACE_X);
			case R.string.formula_editor_sensor_face_y_position:
				return buildSensor(Sensors.FACE_Y);
			case R.string.formula_editor_sensor_second_face_detected:
				return buildSensor(Sensors.SECOND_FACE_DETECTED);
			case R.string.formula_editor_sensor_second_face_size:
				return buildSensor(Sensors.SECOND_FACE_SIZE);
			case R.string.formula_editor_sensor_second_face_x_position:
				return buildSensor(Sensors.SECOND_FACE_X);
			case R.string.formula_editor_sensor_second_face_y_position:
				return buildSensor(Sensors.SECOND_FACE_Y);
			case R.string.formula_editor_sensor_head_top_x:
				return buildSensor(Sensors.HEAD_TOP_X);
			case R.string.formula_editor_sensor_head_top_y:
				return buildSensor(Sensors.HEAD_TOP_Y);
			case R.string.formula_editor_sensor_neck_x:
				return buildSensor(Sensors.NECK_X);
			case R.string.formula_editor_sensor_neck_y:
				return buildSensor(Sensors.NECK_Y);
			case R.string.formula_editor_sensor_nose_x:
				return buildSensor(Sensors.NOSE_X);
			case R.string.formula_editor_sensor_nose_y:
				return buildSensor(Sensors.NOSE_Y);
			case R.string.formula_editor_sensor_left_eye_inner_x:
				return buildSensor(Sensors.LEFT_EYE_INNER_X);
			case R.string.formula_editor_sensor_left_eye_inner_y:
				return buildSensor(Sensors.LEFT_EYE_INNER_Y);
			case R.string.formula_editor_sensor_left_eye_center_x:
				return buildSensor(Sensors.LEFT_EYE_CENTER_X);
			case R.string.formula_editor_sensor_left_eye_center_y:
				return buildSensor(Sensors.LEFT_EYE_CENTER_Y);
			case R.string.formula_editor_sensor_left_eye_outer_x:
				return buildSensor(Sensors.LEFT_EYE_OUTER_X);
			case R.string.formula_editor_sensor_left_eye_outer_y:
				return buildSensor(Sensors.LEFT_EYE_OUTER_Y);
			case R.string.formula_editor_sensor_right_eye_inner_x:
				return buildSensor(Sensors.RIGHT_EYE_INNER_X);
			case R.string.formula_editor_sensor_right_eye_inner_y:
				return buildSensor(Sensors.RIGHT_EYE_INNER_Y);
			case R.string.formula_editor_sensor_right_eye_center_x:
				return buildSensor(Sensors.RIGHT_EYE_CENTER_X);
			case R.string.formula_editor_sensor_right_eye_center_y:
				return buildSensor(Sensors.RIGHT_EYE_CENTER_Y);
			case R.string.formula_editor_sensor_right_eye_outer_x:
				return buildSensor(Sensors.RIGHT_EYE_OUTER_X);
			case R.string.formula_editor_sensor_right_eye_outer_y:
				return buildSensor(Sensors.RIGHT_EYE_OUTER_Y);
			case R.string.formula_editor_sensor_left_ear_x:
				return buildSensor(Sensors.LEFT_EAR_X);
			case R.string.formula_editor_sensor_left_ear_y:
				return buildSensor(Sensors.LEFT_EAR_Y);
			case R.string.formula_editor_sensor_right_ear_x:
				return buildSensor(Sensors.RIGHT_EAR_X);
			case R.string.formula_editor_sensor_right_ear_y:
				return buildSensor(Sensors.RIGHT_EAR_Y);
			case R.string.formula_editor_sensor_mouth_left_corner_x:
				return buildSensor(Sensors.MOUTH_LEFT_CORNER_X);
			case R.string.formula_editor_sensor_mouth_left_corner_y:
				return buildSensor(Sensors.MOUTH_LEFT_CORNER_Y);
			case R.string.formula_editor_sensor_mouth_right_corner_x:
				return buildSensor(Sensors.MOUTH_RIGHT_CORNER_X);
			case R.string.formula_editor_sensor_mouth_right_corner_y:
				return buildSensor(Sensors.MOUTH_RIGHT_CORNER_Y);
			case R.string.formula_editor_sensor_left_shoulder_x:
				return buildSensor(Sensors.LEFT_SHOULDER_X);
			case R.string.formula_editor_sensor_left_shoulder_y:
				return buildSensor(Sensors.LEFT_SHOULDER_Y);
			case R.string.formula_editor_sensor_right_shoulder_x:
				return buildSensor(Sensors.RIGHT_SHOULDER_X);
			case R.string.formula_editor_sensor_right_shoulder_y:
				return buildSensor(Sensors.RIGHT_SHOULDER_Y);
			case R.string.formula_editor_sensor_left_elbow_x:
				return buildSensor(Sensors.LEFT_ELBOW_X);
			case R.string.formula_editor_sensor_left_elbow_y:
				return buildSensor(Sensors.LEFT_ELBOW_Y);
			case R.string.formula_editor_sensor_right_elbow_x:
				return buildSensor(Sensors.RIGHT_ELBOW_X);
			case R.string.formula_editor_sensor_right_elbow_y:
				return buildSensor(Sensors.RIGHT_ELBOW_Y);
			case R.string.formula_editor_sensor_left_wrist_x:
				return buildSensor(Sensors.LEFT_WRIST_X);
			case R.string.formula_editor_sensor_left_wrist_y:
				return buildSensor(Sensors.LEFT_WRIST_Y);
			case R.string.formula_editor_sensor_right_wrist_x:
				return buildSensor(Sensors.RIGHT_WRIST_X);
			case R.string.formula_editor_sensor_right_wrist_y:
				return buildSensor(Sensors.RIGHT_WRIST_Y);
			case R.string.formula_editor_sensor_left_pinky_knuckle_x:
				return buildSensor(Sensors.LEFT_PINKY_X);
			case R.string.formula_editor_sensor_left_pinky_knuckle_y:
				return buildSensor(Sensors.LEFT_PINKY_Y);
			case R.string.formula_editor_sensor_right_pinky_knuckle_x:
				return buildSensor(Sensors.RIGHT_PINKY_X);
			case R.string.formula_editor_sensor_right_pinky_knuckle_y:
				return buildSensor(Sensors.RIGHT_PINKY_Y);
			case R.string.formula_editor_sensor_left_index_knuckle_x:
				return buildSensor(Sensors.LEFT_INDEX_X);
			case R.string.formula_editor_sensor_left_index_knuckle_y:
				return buildSensor(Sensors.LEFT_INDEX_Y);
			case R.string.formula_editor_sensor_right_index_knuckle_x:
				return buildSensor(Sensors.RIGHT_INDEX_X);
			case R.string.formula_editor_sensor_right_index_knuckle_y:
				return buildSensor(Sensors.RIGHT_INDEX_Y);
			case R.string.formula_editor_sensor_left_thumb_knuckle_x:
				return buildSensor(Sensors.LEFT_THUMB_X);
			case R.string.formula_editor_sensor_left_thumb_knuckle_y:
				return buildSensor(Sensors.LEFT_THUMB_Y);
			case R.string.formula_editor_sensor_right_thumb_knuckle_x:
				return buildSensor(Sensors.RIGHT_THUMB_X);
			case R.string.formula_editor_sensor_right_thumb_knuckle_y:
				return buildSensor(Sensors.RIGHT_THUMB_Y);
			case R.string.formula_editor_sensor_left_hip_x:
				return buildSensor(Sensors.LEFT_HIP_X);
			case R.string.formula_editor_sensor_left_hip_y:
				return buildSensor(Sensors.LEFT_HIP_Y);
			case R.string.formula_editor_sensor_right_hip_x:
				return buildSensor(Sensors.RIGHT_HIP_X);
			case R.string.formula_editor_sensor_right_hip_y:
				return buildSensor(Sensors.RIGHT_HIP_Y);
			case R.string.formula_editor_sensor_left_knee_x:
				return buildSensor(Sensors.LEFT_KNEE_X);
			case R.string.formula_editor_sensor_left_knee_y:
				return buildSensor(Sensors.LEFT_KNEE_Y);
			case R.string.formula_editor_sensor_right_knee_x:
				return buildSensor(Sensors.RIGHT_KNEE_X);
			case R.string.formula_editor_sensor_right_knee_y:
				return buildSensor(Sensors.RIGHT_KNEE_Y);
			case R.string.formula_editor_sensor_left_ankle_x:
				return buildSensor(Sensors.LEFT_ANKLE_X);
			case R.string.formula_editor_sensor_left_ankle_y:
				return buildSensor(Sensors.LEFT_ANKLE_Y);
			case R.string.formula_editor_sensor_right_ankle_x:
				return buildSensor(Sensors.RIGHT_ANKLE_X);
			case R.string.formula_editor_sensor_right_ankle_y:
				return buildSensor(Sensors.RIGHT_ANKLE_Y);
			case R.string.formula_editor_sensor_left_heel_x:
				return buildSensor(Sensors.LEFT_HEEL_X);
			case R.string.formula_editor_sensor_left_heel_y:
				return buildSensor(Sensors.LEFT_HEEL_Y);
			case R.string.formula_editor_sensor_right_heel_x:
				return buildSensor(Sensors.RIGHT_HEEL_X);
			case R.string.formula_editor_sensor_right_heel_y:
				return buildSensor(Sensors.RIGHT_HEEL_Y);
			case R.string.formula_editor_sensor_left_foot_index_x:
				return buildSensor(Sensors.LEFT_FOOT_INDEX_X);
			case R.string.formula_editor_sensor_left_foot_index_y:
				return buildSensor(Sensors.LEFT_FOOT_INDEX_Y);
			case R.string.formula_editor_sensor_right_foot_index_x:
				return buildSensor(Sensors.RIGHT_FOOT_INDEX_X);
			case R.string.formula_editor_sensor_right_foot_index_y:
				return buildSensor(Sensors.RIGHT_FOOT_INDEX_Y);
			case R.string.formula_editor_phiro_sensor_front_left:
				return buildSensor(Sensors.PHIRO_FRONT_LEFT);
			case R.string.formula_editor_phiro_sensor_front_right:
				return buildSensor(Sensors.PHIRO_FRONT_RIGHT);
			case R.string.formula_editor_phiro_sensor_side_left:
				return buildSensor(Sensors.PHIRO_SIDE_LEFT);
			case R.string.formula_editor_phiro_sensor_side_right:
				return buildSensor(Sensors.PHIRO_SIDE_RIGHT);
			case R.string.formula_editor_phiro_sensor_bottom_left:
				return buildSensor(Sensors.PHIRO_BOTTOM_LEFT);
			case R.string.formula_editor_phiro_sensor_bottom_right:
				return buildSensor(Sensors.PHIRO_BOTTOM_RIGHT);
			case R.string.formula_editor_sensor_timer:
				return buildSensor(Sensors.TIMER);
			case R.string.formula_editor_sensor_date_year:
				return buildSensor(Sensors.DATE_YEAR);
			case R.string.formula_editor_sensor_date_month:
				return buildSensor(Sensors.DATE_MONTH);
			case R.string.formula_editor_sensor_date_day:
				return buildSensor(Sensors.DATE_DAY);
			case R.string.formula_editor_sensor_date_weekday:
				return buildSensor(Sensors.DATE_WEEKDAY);
			case R.string.formula_editor_sensor_time_hour:
				return buildSensor(Sensors.TIME_HOUR);
			case R.string.formula_editor_sensor_time_minute:
				return buildSensor(Sensors.TIME_MINUTE);
			case R.string.formula_editor_sensor_time_second:
				return buildSensor(Sensors.TIME_SECOND);
			case R.string.formula_editor_nfc_tag_id:
				return buildSensor(Sensors.NFC_TAG_ID);
			case R.string.formula_editor_nfc_tag_message:
				return buildSensor(Sensors.NFC_TAG_MESSAGE);

			case R.string.formula_editor_sensor_lego_nxt_1:
				return buildSensor(Sensors.NXT_SENSOR_1);
			case R.string.formula_editor_sensor_lego_nxt_2:
				return buildSensor(Sensors.NXT_SENSOR_2);
			case R.string.formula_editor_sensor_lego_nxt_3:
				return buildSensor(Sensors.NXT_SENSOR_3);
			case R.string.formula_editor_sensor_lego_nxt_4:
				return buildSensor(Sensors.NXT_SENSOR_4);

			case R.string.formula_editor_sensor_lego_ev3_1:
				return buildSensor(Sensors.EV3_SENSOR_1);
			case R.string.formula_editor_sensor_lego_ev3_2:
				return buildSensor(Sensors.EV3_SENSOR_2);
			case R.string.formula_editor_sensor_lego_ev3_3:
				return buildSensor(Sensors.EV3_SENSOR_3);
			case R.string.formula_editor_sensor_lego_ev3_4:
				return buildSensor(Sensors.EV3_SENSOR_4);

			case R.string.formula_editor_sensor_gamepad_a_pressed:
				return buildSensor(Sensors.GAMEPAD_A_PRESSED);
			case R.string.formula_editor_sensor_gamepad_b_pressed:
				return buildSensor(Sensors.GAMEPAD_B_PRESSED);
			case R.string.formula_editor_sensor_gamepad_up_pressed:
				return buildSensor(Sensors.GAMEPAD_UP_PRESSED);
			case R.string.formula_editor_sensor_gamepad_down_pressed:
				return buildSensor(Sensors.GAMEPAD_DOWN_PRESSED);
			case R.string.formula_editor_sensor_gamepad_left_pressed:
				return buildSensor(Sensors.GAMEPAD_LEFT_PRESSED);
			case R.string.formula_editor_sensor_gamepad_right_pressed:
				return buildSensor(Sensors.GAMEPAD_RIGHT_PRESSED);

			case R.string.formula_editor_sensor_drone_battery_status:
				return buildSensor(Sensors.DRONE_BATTERY_STATUS);
			case R.string.formula_editor_sensor_drone_emergency_state:
				return buildSensor(Sensors.DRONE_EMERGENCY_STATE);
			case R.string.formula_editor_sensor_drone_flying:
				return buildSensor(Sensors.DRONE_FLYING);
			case R.string.formula_editor_sensor_drone_initialized:
				return buildSensor(Sensors.DRONE_INITIALIZED);
			case R.string.formula_editor_sensor_drone_usb_active:
				return buildSensor(Sensors.DRONE_USB_ACTIVE);
			case R.string.formula_editor_sensor_drone_usb_remaining_time:
				return buildSensor(Sensors.DRONE_USB_REMAINING_TIME);
			case R.string.formula_editor_sensor_drone_camera_ready:
				return buildSensor(Sensors.DRONE_CAMERA_READY);
			case R.string.formula_editor_sensor_drone_record_ready:
				return buildSensor(Sensors.DRONE_RECORD_READY);
			case R.string.formula_editor_sensor_drone_recording:
				return buildSensor(Sensors.DRONE_RECORDING);
			case R.string.formula_editor_sensor_drone_num_frames:
				return buildSensor(Sensors.DRONE_NUM_FRAMES);

			case R.id.formula_editor_keyboard_decimal_mark:
				return buildPeriod();

			case R.id.formula_editor_keyboard_plus:
				return buildOperator(Operators.PLUS);
			case R.id.formula_editor_keyboard_minus:
				return buildOperator(Operators.MINUS);
			case R.id.formula_editor_keyboard_mult:
				return buildOperator(Operators.MULT);
			case R.id.formula_editor_keyboard_divide:
				return buildOperator(Operators.DIVIDE);
			case R.string.formula_editor_operator_power:
				return buildOperator(Operators.POW);
			case R.string.formula_editor_logic_equal:
				return buildOperator(Operators.EQUAL);
			case R.string.formula_editor_logic_notequal:
				return buildOperator(Operators.NOT_EQUAL);
			case R.string.formula_editor_logic_lesserthan:
				return buildOperator(Operators.SMALLER_THAN);
			case R.string.formula_editor_logic_leserequal:
				return buildOperator(Operators.SMALLER_OR_EQUAL);
			case R.string.formula_editor_logic_greaterthan:
				return buildOperator(Operators.GREATER_THAN);
			case R.string.formula_editor_logic_greaterequal:
				return buildOperator(Operators.GREATER_OR_EQUAL);
			case R.string.formula_editor_logic_and:
				return buildOperator(Operators.LOGICAL_AND);
			case R.string.formula_editor_logic_or:
				return buildOperator(Operators.LOGICAL_OR);
			case R.string.formula_editor_logic_not:
				return buildOperator(Operators.LOGICAL_NOT);

			case R.id.formula_editor_keyboard_bracket_open:
				return buildBracketOpen();
			case R.id.formula_editor_keyboard_bracket_close:
				return buildBracketClose();

			case R.string.formula_editor_object_x:
				return buildObject(Sensors.OBJECT_X);
			case R.string.formula_editor_object_y:
				return buildObject(Sensors.OBJECT_Y);
			case R.string.formula_editor_object_transparency:
				return buildObject(Sensors.OBJECT_TRANSPARENCY);
			case R.string.formula_editor_object_brightness:
				return buildObject(Sensors.OBJECT_BRIGHTNESS);
			case R.string.formula_editor_object_color:
				return buildObject(Sensors.OBJECT_COLOR);
			case R.string.formula_editor_object_size:
				return buildObject(Sensors.OBJECT_SIZE);
			case R.string.formula_editor_object_rotation:
				return buildObject(Sensors.MOTION_DIRECTION);
			case R.string.formula_editor_object_rotation_look:
				return buildObject(Sensors.LOOK_DIRECTION);
			case R.string.formula_editor_object_layer:
				return buildObject(Sensors.OBJECT_LAYER);
			case R.string.formula_editor_object_x_velocity:
				return buildObject(Sensors.OBJECT_X_VELOCITY);
			case R.string.formula_editor_object_y_velocity:
				return buildObject(Sensors.OBJECT_Y_VELOCITY);
			case R.string.formula_editor_object_angular_velocity:
				return buildObject(Sensors.OBJECT_ANGULAR_VELOCITY);
			case R.string.formula_editor_object_look_number:
				return buildObject(Sensors.OBJECT_LOOK_NUMBER);
			case R.string.formula_editor_object_look_name:
				return buildObject(Sensors.OBJECT_LOOK_NAME);
			case R.string.formula_editor_object_number_of_looks:
			case R.string.formula_editor_object_number_of_backgrounds:
				return buildObject(Sensors.OBJECT_NUMBER_OF_LOOKS);
			case R.string.formula_editor_object_background_number:
				return buildObject(Sensors.OBJECT_BACKGROUND_NUMBER);
			case R.string.formula_editor_object_background_name:
				return buildObject(Sensors.OBJECT_BACKGROUND_NAME);
			case R.string.formula_editor_object_distance_to:
				return buildObject(Sensors.OBJECT_DISTANCE_TO);
			case R.string.formula_editor_function_collides_with_edge:
				return buildObject(Sensors.COLLIDES_WITH_EDGE);
			case R.string.formula_editor_function_touched:
				return buildObject(Sensors.COLLIDES_WITH_FINGER);

			case R.string.formula_editor_sensor_text_from_camera:
				return buildObject(Sensors.TEXT_FROM_CAMERA);
			case R.string.formula_editor_sensor_text_blocks_number:
				return buildObject(Sensors.TEXT_BLOCKS_NUMBER);
			case R.string.formula_editor_function_text_block_x:
				return buildSingleParameterFunction(Functions.TEXT_BLOCK_X,
						InternTokenType.NUMBER, "1");
			case R.string.formula_editor_function_text_block_y:
				return buildSingleParameterFunction(Functions.TEXT_BLOCK_Y,
						InternTokenType.NUMBER, "1");
			case R.string.formula_editor_function_text_block_size:
				return buildSingleParameterFunction(Functions.TEXT_BLOCK_SIZE,
						InternTokenType.NUMBER, "1");
			case R.string.formula_editor_function_text_block_from_camera:
				return buildSingleParameterFunction(Functions.TEXT_BLOCK_FROM_CAMERA,
						InternTokenType.NUMBER, "1");
			case R.string.formula_editor_function_text_block_language_from_camera:
				return buildSingleParameterFunction(Functions.TEXT_BLOCK_LANGUAGE_FROM_CAMERA,
						InternTokenType.NUMBER, "1");
			case R.string.formula_editor_function_collides_with_color:
				return buildSingleParameterFunction(Functions.COLLIDES_WITH_COLOR, STRING, "#ff0000");
			case R.string.formula_editor_function_color_touches_color:
				return buildDoubleParameterFunction(Functions.COLOR_TOUCHES_COLOR, STRING, "#ff0000", STRING, "#ff0000");
			case R.string.formula_editor_listening_language_sensor:
				return buildSensor(Sensors.SPEECH_RECOGNITION_LANGUAGE);
			case R.string.formula_editor_sensor_stage_width:
				return buildSensor(Sensors.STAGE_WIDTH);
			case R.string.formula_editor_sensor_stage_height:
				return buildSensor(Sensors.STAGE_HEIGHT);

			case R.string.formula_editor_function_get_id_of_detected_object:
				return buildSingleParameterFunction(Functions.ID_OF_DETECTED_OBJECT, NUMBER, "1");
			case R.string.formula_editor_function_object_with_id_visible:
				return buildSingleParameterFunction(Functions.OBJECT_WITH_ID_VISIBLE, NUMBER, "1");
		}
		return null;
	}

	private List<InternToken> buildBracketOpen() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(BRACKET_OPEN));
		return returnList;
	}

	private List<InternToken> buildBracketClose() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildUserList(String userListName) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(USER_LIST, userListName));
		return returnList;
	}

	private List<InternToken> buildUserVariable(String userVariableName) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(USER_VARIABLE, userVariableName));
		return returnList;
	}

	private List<InternToken> buildUserDefinedBrickInput(String userDefinedBrickInput) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(USER_DEFINED_BRICK_INPUT,
				userDefinedBrickInput));
		return returnList;
	}

	private List<InternToken> buildCollideWithFormula(String formula) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(COLLISION_FORMULA, formula));
		return returnList;
	}

	private List<InternToken> buildPeriod() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(PERIOD));
		return returnList;
	}

	private List<InternToken> buildNumber(String numberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(NUMBER, numberValue));
		return returnList;
	}

	private List<InternToken> buildObject(Sensors sensors) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(SENSOR, sensors.name()));
		return returnList;
	}

	private List<InternToken> buildOperator(Operators operator) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(OPERATOR, operator.name()));
		return returnList;
	}

	private List<InternToken> buildSensor(Sensors sensor) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(SENSOR, sensor.name()));
		return returnList;
	}

	private List<InternToken> buildTripleParameterFunction(Functions function, InternTokenType firstParameter,
			String firstParameterNumberValue, InternTokenType secondParameter, String secondParameterNumberValue,
			InternTokenType thirdParameter, String thirdParameterNumberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(FUNCTION_NAME, function.name()));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_OPEN));
		returnList.add(new InternToken(firstParameter, firstParameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETER_DELIMITER));
		returnList.add(new InternToken(secondParameter, secondParameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETER_DELIMITER));
		returnList.add(new InternToken(thirdParameter, thirdParameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildDoubleParameterFunction(Functions function, InternTokenType firstParameter,
			String firstParameterNumberValue, InternTokenType secondParameter, String secondParameterNumberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(FUNCTION_NAME, function.name()));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_OPEN));
		returnList.add(new InternToken(firstParameter, firstParameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETER_DELIMITER));
		returnList.add(new InternToken(secondParameter, secondParameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildSingleParameterFunction(Functions function, InternTokenType firstParameter,
			String parameterNumberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(FUNCTION_NAME, function.name()));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_OPEN));
		returnList.add(new InternToken(firstParameter, parameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildFunctionWithoutParametersAndBrackets(Functions function) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(FUNCTION_NAME, function.name()));
		return returnList;
	}

	private List<InternToken> buildString(String myString) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(STRING, myString));
		return returnList;
	}

	private List<InternToken> buildDoubleParameterFunctionWithNegativeValues(Functions function,
			boolean isFirstParameterNegative, InternTokenType firstParameter, String firstParameterNumberValue,
			boolean isSecondParameterNegative, InternTokenType secondParameter, String secondParameterNumberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(FUNCTION_NAME, function.name()));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_OPEN));
		if (isFirstParameterNegative) {
			returnList.add(new InternToken(OPERATOR, Operators.MINUS.name()));
		}
		returnList.add(new InternToken(firstParameter, firstParameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETER_DELIMITER));
		if (isSecondParameterNegative) {
			returnList.add(new InternToken(OPERATOR, Operators.MINUS.name()));
		}
		returnList.add(new InternToken(secondParameter, secondParameterNumberValue));
		returnList.add(new InternToken(FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}
}
