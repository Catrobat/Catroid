/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

public class InternFormulaKeyboardAdapter {

	public static final int FORMULA_EDITOR_USER_LIST_RESOURCE_ID = 1;
	public static final int FORMULA_EDITOR_USER_VARIABLE_RESOURCE_ID = 0;

	public List<InternToken> createInternTokenListByResourceId(int resource, String name) {
		//USER VARIABLES
		if ((resource == FORMULA_EDITOR_USER_VARIABLE_RESOURCE_ID) && !name.isEmpty()) {
			return buildUserVariable(name);
		}

		//USER LISTS
		if ((resource == FORMULA_EDITOR_USER_LIST_RESOURCE_ID) && !name.isEmpty()) {
			return buildUserList(name);
		}

		//STRING
		if ((resource == R.id.formula_editor_keyboard_string)) {
			return buildString(name);
		}

		switch (resource) {
			// NUMBER:
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

			//FUNCTIONS:
			case R.string.formula_editor_function_sin:
				return buildSingleParameterFunction(Functions.SIN, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_cos:
				return buildSingleParameterFunction(Functions.COS, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_tan:
				return buildSingleParameterFunction(Functions.TAN, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_ln:
				return buildSingleParameterFunction(Functions.LN, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_log:
				return buildSingleParameterFunction(Functions.LOG, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_pi:
				return buildFunctionWithoutParametersAndBrackets(Functions.PI);
			case R.string.formula_editor_function_sqrt:
				return buildSingleParameterFunction(Functions.SQRT, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_rand:
				return buildDoubleParameterFunction(Functions.RAND, InternTokenType.NUMBER, "0",
						InternTokenType.NUMBER, "1");
			case R.string.formula_editor_function_abs:
				return buildSingleParameterFunction(Functions.ABS, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_round:
				return buildSingleParameterFunction(Functions.ROUND, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_mod:
				return buildDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, "1", InternTokenType.NUMBER,
						"1");
			case R.string.formula_editor_function_arcsin:
				return buildSingleParameterFunction(Functions.ARCSIN, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_arccos:
				return buildSingleParameterFunction(Functions.ARCCOS, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_arctan:
				return buildSingleParameterFunction(Functions.ARCTAN, InternTokenType.NUMBER, "0");
			case R.string.formula_editor_function_exp:
				return buildSingleParameterFunction(Functions.EXP, InternTokenType.NUMBER, "1");
			case R.string.formula_editor_function_max:
				return buildDoubleParameterFunction(Functions.MAX, InternTokenType.NUMBER, "0", InternTokenType.NUMBER,
						"1");
			case R.string.formula_editor_function_min:
				return buildDoubleParameterFunction(Functions.MIN, InternTokenType.NUMBER, "0", InternTokenType.NUMBER,
						"1");
			case R.string.formula_editor_function_true:
				return buildFunctionWithoutParametersAndBrackets(Functions.TRUE);
			case R.string.formula_editor_function_false:
				return buildFunctionWithoutParametersAndBrackets(Functions.FALSE);
			case R.string.formula_editor_function_letter:
				return buildDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, "1",
						InternTokenType.STRING, "hello world");
			case R.string.formula_editor_function_length:
				return buildSingleParameterFunction(Functions.LENGTH, InternTokenType.STRING, "hello world");
			case R.string.formula_editor_function_number_of_items:
				return buildSingleParameterFunction(Functions.NUMBER_OF_ITEMS, InternTokenType.USER_LIST, "list name");
			case R.string.formula_editor_function_join:
				return buildDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, "hello",
						InternTokenType.STRING, " world");
			case R.string.formula_editor_function_list_item:
				return buildDoubleParameterFunction(Functions.LIST_ITEM, InternTokenType.NUMBER, "1",
						InternTokenType.USER_LIST, "list name");
			case R.string.formula_editor_function_contains:
				return buildDoubleParameterFunction(Functions.CONTAINS, InternTokenType.USER_LIST, "list name", InternTokenType.NUMBER, "1");

			case R.string.formula_editor_function_arduino_read_pin_value_digital:
				return buildSingleParameterFunction(Functions.ARDUINODIGITAL, InternTokenType.STRING, "0");
			case R.string.formula_editor_function_arduino_read_pin_value_analog:
				return buildSingleParameterFunction(Functions.ARDUINOANALOG, InternTokenType.STRING, "0");

			//SENSOR

			case R.string.formula_editor_sensor_x_acceleration:
				return buildSensor(Sensors.X_ACCELERATION);
			case R.string.formula_editor_sensor_y_acceleration:
				return buildSensor(Sensors.Y_ACCELERATION);
			case R.string.formula_editor_sensor_z_acceleration:
				return buildSensor(Sensors.Z_ACCELERATION);
			case R.string.formula_editor_sensor_compass_direction:
				return buildSensor(Sensors.COMPASS_DIRECTION);
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
				return buildSensor(Sensors.FACE_X_POSITION);
			case R.string.formula_editor_sensor_face_y_position:
				return buildSensor(Sensors.FACE_Y_POSITION);

			//PERIOD
			case R.id.formula_editor_keyboard_decimal_mark:
				return buildPeriod();

			//OPERATOR

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
			case R.id.formula_editor_keyboard_equal:
				return buildOperator(Operators.EQUAL);
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

			//BRACKETS

			case R.id.formula_editor_keyboard_bracket_open:
				return buildBracketOpen();
			case R.id.formula_editor_keyboard_bracket_close:
				return buildBracketClose();

			//COSTUME

			case R.string.formula_editor_object_x:
				return buildObject(Sensors.OBJECT_X);
			case R.string.formula_editor_object_y:
				return buildObject(Sensors.OBJECT_Y);
			case R.string.formula_editor_object_transparency:
				return buildObject(Sensors.OBJECT_TRANSPARENCY);
			case R.string.formula_editor_object_brightness:
				return buildObject(Sensors.OBJECT_BRIGHTNESS);
			case R.string.formula_editor_object_size:
				return buildObject(Sensors.OBJECT_SIZE);
			case R.string.formula_editor_object_rotation:
				return buildObject(Sensors.OBJECT_ROTATION);
			case R.string.formula_editor_object_layer:
				return buildObject(Sensors.OBJECT_LAYER);

		}
		return null;
	}

	private List<InternToken> buildBracketOpen() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		return returnList;
	}

	private List<InternToken> buildBracketClose() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildUserList(String userListName) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.USER_LIST, userListName));
		return returnList;
	}

	private List<InternToken> buildUserVariable(String userVariableName) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.USER_VARIABLE, userVariableName));
		return returnList;
	}

	private List<InternToken> buildPeriod() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.PERIOD));
		return returnList;
	}

	private List<InternToken> buildNumber(String numberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.NUMBER, numberValue));
		return returnList;
	}

	private List<InternToken> buildObject(Sensors sensors) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.SENSOR, sensors.name()));
		return returnList;
	}

	private List<InternToken> buildOperator(Operators operator) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.OPERATOR, operator.name()));
		return returnList;
	}

	private List<InternToken> buildSensor(Sensors sensor) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		return returnList;
	}

	private List<InternToken> buildDoubleParameterFunction(Functions function, InternTokenType firstParameter,
			String firstParameterNumberValue, InternTokenType secondParameter, String secondParameterNumberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		returnList.add(new InternToken(firstParameter, firstParameterNumberValue));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		returnList.add(new InternToken(secondParameter, secondParameterNumberValue));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildSingleParameterFunction(Functions function, InternTokenType firstParameter,
			String parameterNumberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		returnList.add(new InternToken(firstParameter, parameterNumberValue));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildFunctionWithoutParametersAndBrackets(Functions function) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		return returnList;
	}

	private List<InternToken> buildString(String myString) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.STRING, myString));
		return returnList;
	}

}
