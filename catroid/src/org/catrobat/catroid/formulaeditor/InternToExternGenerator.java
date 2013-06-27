/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.R;

import android.content.Context;
import android.util.Log;

public class InternToExternGenerator {

	private String generatedExternFormulaString;
	private ExternInternRepresentationMapping generatedExternInternRepresentationMapping;
	private Context context;

	private static final HashMap<String, Integer> internExternLanguageConverterMap = new HashMap<String, Integer>();
	static {
		internExternLanguageConverterMap.put(Operators.DIVIDE.name(), R.string.formula_editor_operator_divide);
		internExternLanguageConverterMap.put(Operators.MULT.name(), R.string.formula_editor_operator_mult);
		internExternLanguageConverterMap.put(Operators.MINUS.name(), R.string.formula_editor_operator_minus);
		internExternLanguageConverterMap.put(Operators.PLUS.name(), R.string.formula_editor_operator_plus);

		internExternLanguageConverterMap.put(".", R.string.formula_editor_decimal_mark);
		internExternLanguageConverterMap.put(Functions.SIN.name(), R.string.formula_editor_function_sin);
		internExternLanguageConverterMap.put(Functions.COS.name(), R.string.formula_editor_function_cos);
		internExternLanguageConverterMap.put(Functions.TAN.name(), R.string.formula_editor_function_tan);
		internExternLanguageConverterMap.put(Functions.LN.name(), R.string.formula_editor_function_ln);
		internExternLanguageConverterMap.put(Functions.LOG.name(), R.string.formula_editor_function_log);
		internExternLanguageConverterMap.put(Functions.PI.name(), R.string.formula_editor_function_pi);
		internExternLanguageConverterMap.put(Functions.SQRT.name(), R.string.formula_editor_function_sqrt);
		internExternLanguageConverterMap.put(Functions.RAND.name(), R.string.formula_editor_function_rand);
		internExternLanguageConverterMap.put(Functions.ABS.name(), R.string.formula_editor_function_abs);
		internExternLanguageConverterMap.put(Functions.ROUND.name(), R.string.formula_editor_function_round);
		internExternLanguageConverterMap.put(Functions.MOD.name(), R.string.formula_editor_function_mod);
		internExternLanguageConverterMap.put(Functions.ARCSIN.name(), R.string.formula_editor_function_arcsin);
		internExternLanguageConverterMap.put(Functions.ARCCOS.name(), R.string.formula_editor_function_arccos);
		internExternLanguageConverterMap.put(Functions.ARCTAN.name(), R.string.formula_editor_function_arctan);
		internExternLanguageConverterMap.put(Functions.EXP.name(), R.string.formula_editor_function_exp);
		internExternLanguageConverterMap.put(Functions.MAX.name(), R.string.formula_editor_function_max);
		internExternLanguageConverterMap.put(Functions.MIN.name(), R.string.formula_editor_function_min);
		internExternLanguageConverterMap.put(Functions.TRUE.name(), R.string.formula_editor_function_true);
		internExternLanguageConverterMap.put(Functions.FALSE.name(), R.string.formula_editor_function_false);
		internExternLanguageConverterMap.put(Sensors.X_ACCELERATION.name(),
				R.string.formula_editor_sensor_x_acceleration);
		internExternLanguageConverterMap.put(Sensors.Y_ACCELERATION.name(),
				R.string.formula_editor_sensor_y_acceleration);
		internExternLanguageConverterMap.put(Sensors.Z_ACCELERATION.name(),
				R.string.formula_editor_sensor_z_acceleration);
		internExternLanguageConverterMap.put(Sensors.COMPASS_DIRECTION.name(),
				R.string.formula_editor_sensor_compass_direction);
		internExternLanguageConverterMap
				.put(Sensors.X_INCLINATION.name(), R.string.formula_editor_sensor_x_inclination);
		internExternLanguageConverterMap
				.put(Sensors.Y_INCLINATION.name(), R.string.formula_editor_sensor_y_inclination);
		internExternLanguageConverterMap.put(Sensors.OBJECT_X.name(), R.string.formula_editor_object_x);
		internExternLanguageConverterMap.put(Sensors.OBJECT_Y.name(), R.string.formula_editor_object_y);
		internExternLanguageConverterMap.put(Sensors.OBJECT_GHOSTEFFECT.name(), R.string.formula_editor_object_ghosteffect);
		internExternLanguageConverterMap.put(Sensors.OBJECT_BRIGHTNESS.name(), R.string.formula_editor_object_brightness);
		internExternLanguageConverterMap.put(Sensors.OBJECT_SIZE.name(), R.string.formula_editor_object_size);
		internExternLanguageConverterMap.put(Sensors.OBJECT_ROTATION.name(), R.string.formula_editor_object_rotation);
		internExternLanguageConverterMap.put(Sensors.OBJECT_LAYER.name(), R.string.formula_editor_object_layer);

		internExternLanguageConverterMap.put(Operators.LOGICAL_NOT.name(), R.string.formula_editor_logic_not);
		internExternLanguageConverterMap.put(Operators.NOT_EQUAL.name(), R.string.formula_editor_logic_notequal);
		internExternLanguageConverterMap.put(Operators.EQUAL.name(), R.string.formula_editor_logic_equal);
		internExternLanguageConverterMap.put(Operators.GREATER_OR_EQUAL.name(),
				R.string.formula_editor_logic_greaterequal);
		internExternLanguageConverterMap.put(Operators.GREATER_THAN.name(), R.string.formula_editor_logic_greaterthan);
		internExternLanguageConverterMap.put(Operators.LOGICAL_AND.name(), R.string.formula_editor_logic_and);
		internExternLanguageConverterMap.put(Operators.LOGICAL_OR.name(), R.string.formula_editor_logic_or);
		internExternLanguageConverterMap.put(Operators.SMALLER_OR_EQUAL.name(),
				R.string.formula_editor_logic_leserequal);
		internExternLanguageConverterMap.put(Operators.SMALLER_THAN.name(), R.string.formula_editor_logic_lesserthan);

	}

	public InternToExternGenerator(Context context) {
		this.context = context;
		generatedExternFormulaString = "";
		generatedExternInternRepresentationMapping = new ExternInternRepresentationMapping();
	}

	public void generateExternStringAndMapping(List<InternToken> internTokenFormula) {
		Log.i("info", "generateExternStringAndMapping:enter");

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		for (InternToken internToken : internTokenFormula) {
			internTokenList.add(internToken);
		}

		generatedExternInternRepresentationMapping = new ExternInternRepresentationMapping();

		generatedExternFormulaString = "";
		InternToken currentToken = null;
		InternToken nextToken = null;
		String externTokenString;
		int externStringStartIndex;
		int externStringEndIndex;

		int internTokenListIndex = 0;

		while (internTokenList.isEmpty() == false) {
			if (appendWhiteSpace(currentToken, nextToken)) {
				generatedExternFormulaString += " ";
			}
			externStringStartIndex = generatedExternFormulaString.length();
			currentToken = internTokenList.get(0);

			if (internTokenList.size() < 2) {
				nextToken = null;
			} else {
				nextToken = internTokenList.get(1);
			}

			externTokenString = generateExternStringFromToken(currentToken);
			generatedExternFormulaString += externTokenString;
			externStringEndIndex = generatedExternFormulaString.length();

			generatedExternInternRepresentationMapping.putMapping(externStringStartIndex, externStringEndIndex,
					internTokenListIndex);

			internTokenList.remove(0);
			internTokenListIndex++;

		}

		generatedExternFormulaString += " ";

	}

	private String generateExternStringFromToken(InternToken internToken) {
		switch (internToken.getInternTokenType()) {
			case NUMBER:
				String number = internToken.getTokenStringValue();

				if (!number.contains(".")) {
					return number;
				}

				String left = number.substring(0, number.indexOf("."));
				String right = number.substring(number.indexOf(".") + 1);

				return left + getExternStringForInternTokenValue(".", context) + right;

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

			default:
				return getExternStringForInternTokenValue(internToken.getTokenStringValue(), context);

		}
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
		Integer stringResourceID = internExternLanguageConverterMap.get(internTokenValue);
		if (stringResourceID == null) {
			return null;
		}
		return context.getString(stringResourceID);
	}
}
