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
package org.catrobat.catroid.test.formulaeditor;

import android.test.InstrumentationTestCase;
import android.util.Log;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;

import java.util.LinkedList;
import java.util.List;

public final class FormulaEditorTestUtil {

	private static final String TAG = FormulaEditorTestUtil.class.getSimpleName();

	private FormulaEditorTestUtil() {
		throw new AssertionError();
	}

	public static List<InternToken> buildSingleParameterFunction(Functions function, List<InternToken> internTokenList) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.addAll(internTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testSingleParameterFunction(Functions function, List<InternToken> internTokenList,
			Object expected, Sprite testSprite) {

		List<InternToken> tokenList = FormulaEditorTestUtil.buildSingleParameterFunction(function, internTokenList);
		FormulaElement parseTree = new InternFormulaParser(tokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + function + "(" + tokenList + ")",
				parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + function + "(" + tokenList
				+ ")", expected, parseTree.interpretRecursive(testSprite));
	}

	public static List<InternToken> buildSingleParameterFunction(Functions function, InternTokenType firstParameter,
			String parameterNumberValue) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		try {
			if (Double.valueOf(parameterNumberValue) < 0) {
				tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
				parameterNumberValue = String.valueOf(Math.abs(Double.valueOf(parameterNumberValue)));
			}
		} catch (NumberFormatException numberFormatException) {
			Log.e(TAG, Log.getStackTraceString(numberFormatException));
		}
		tokenList.add(new InternToken(firstParameter, parameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static List<InternToken> buildDoubleParameterFunction(Functions function,
			List<InternToken> firstInternTokenList, List<InternToken> secondInternTokenList) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.addAll(firstInternTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		tokenList.addAll(secondInternTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testDoubleParameterFunction(Functions function, List<InternToken> firstInternTokenList,
			List<InternToken> secondInternTokenList, Object expected, Sprite testSprite) {

		List<InternToken> internTokenList = buildDoubleParameterFunction(function, firstInternTokenList,
				secondInternTokenList);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + function.name() + "("
				+ firstInternTokenList + "," + secondInternTokenList + ")", parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + function.name() + "("
						+ firstInternTokenList + "," + secondInternTokenList + ")", expected,
				parseTree.interpretRecursive(testSprite));
	}

	public static List<InternToken> buildDoubleParameterFunction(Functions function, InternTokenType firstParameter,
			String firstParameterNumberValue, InternTokenType secondParameter, String secondParameterNumberValue) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		try {
			if (Double.valueOf(firstParameterNumberValue) < 0) {
				tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
				firstParameterNumberValue = String.valueOf(Math.abs(Double.valueOf(firstParameterNumberValue)));
			}
		} catch (NumberFormatException numberFormatException) {
			Log.e(TAG, Log.getStackTraceString(numberFormatException));
		}
		tokenList.add(new InternToken(firstParameter, firstParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		try {
			if (Double.valueOf(secondParameterNumberValue) < 0) {
				tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
				secondParameterNumberValue = String.valueOf(Math.abs(Double.valueOf(secondParameterNumberValue)));
			}
		} catch (NumberFormatException numberFormatException) {
			Log.e(TAG, Log.getStackTraceString(numberFormatException));
		}
		tokenList.add(new InternToken(secondParameter, secondParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testDoubleParameterFunction(Functions function, InternTokenType firstInternTokenType,
			String firstParameter, InternTokenType secondInternTokenType, String secondParameter, Object expected,
			Sprite testSprite) {

		List<InternToken> internTokenList = buildDoubleParameterFunction(function, firstInternTokenType,
				firstParameter, secondInternTokenType, secondParameter);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + function.name() + "("
				+ firstParameter + "," + secondParameter + ")", parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + function.name() + "("
				+ firstParameter + "," + secondParameter + ")", expected, parseTree.interpretRecursive(testSprite));
	}

	public static void testBinaryOperator(InternTokenType firstInternTokenType, String firstOperand,
			Operators operatorType, InternTokenType secondInternTokenType, String secondOperand, Object expected,
			Sprite testSprite) {
		List<InternToken> internTokenList = buildBinaryOperator(firstInternTokenType, firstOperand, operatorType,
				secondInternTokenType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + firstOperand + operatorType
				+ secondOperand, parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + firstOperand
				+ operatorType + secondOperand, expected, parseTree.interpretRecursive(testSprite));
	}

	public static void testBinaryOperator(InternTokenType firstInternTokenType, String firstOperand, Operators operatorType,
			List<InternToken> secondOperand, Object expected, Sprite testSprite) {
		List<InternToken> internTokenList = buildBinaryOperator(firstInternTokenType, firstOperand, operatorType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + firstOperand + operatorType
				+ secondOperand, parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + firstOperand
				+ operatorType + secondOperand, expected, parseTree.interpretRecursive(testSprite));
	}

	public static void testBinaryOperator(List<InternToken> firstOperand, Operators operatorType,
			InternTokenType secondInternTokenType, String secondOperand, Object expected, Sprite testSprite) {
		List<InternToken> internTokenList = buildBinaryOperator(firstOperand, operatorType, secondInternTokenType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + firstOperand + operatorType
				+ secondOperand, parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + firstOperand
				+ operatorType + secondOperand, expected, parseTree.interpretRecursive(testSprite));
	}

	public static void testBinaryOperator(List<InternToken> firstOperand, Operators operatorType,
			List<InternToken> secondOperand, Object expected, Sprite testSprite) {
		List<InternToken> internTokenList = buildBinaryOperator(firstOperand, operatorType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + firstOperand + operatorType
				+ secondOperand, parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + firstOperand
				+ operatorType + secondOperand, expected, parseTree.interpretRecursive(testSprite));
	}

	public static void testSingleParameterFunction(Functions function, InternTokenType firstInternTokenType,
			String firstParameter, Object expected, Sprite testSprite) {
		List<InternToken> internTokenList = FormulaEditorTestUtil.buildSingleParameterFunction(function,
				firstInternTokenType, firstParameter);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();
		Formula formula = new Formula(parseTree);

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + function + "(" + firstParameter
				+ ")", parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! " + function + "("
				+ firstParameter + ")", expected, formula.interpretObject(testSprite));
	}

	public static void testSingleToken(InternTokenType firstInternTokenType, String firstParameter, Object expected,
			Sprite testSprite) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(firstInternTokenType, firstParameter));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();
		Formula formula = new Formula(parseTree);

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + firstParameter, parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! ", expected,
				formula.interpretObject(testSprite));
	}

	public static void testSingleTokenError(InternTokenType firstInternTokenType, String firstParameter, int expectedErrorTokenIndex) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(firstInternTokenType, firstParameter));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internFormulaParser.parseFormula();

		InstrumentationTestCase.assertNull("Formula should not have been parsed: " + firstParameter, parseTree);
		InstrumentationTestCase.assertEquals("Error Token Index is not as expected! ", expectedErrorTokenIndex,
				internFormulaParser.getErrorTokenIndex());
	}

	public static void testUnaryOperator(Operators operatorType, InternTokenType firstInternTokenType,
			String firstParameter, Object expected, Sprite testSprite) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, operatorType.name()));
		internTokenList.add(new InternToken(firstInternTokenType, firstParameter));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();
		Formula formula = new Formula(parseTree);

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + operatorType + firstParameter,
				parseTree);
		InstrumentationTestCase.assertEquals("Formula interpretation is not as expected! ", expected,
				formula.interpretObject(testSprite));
	}

	public static void testNotANumberWithBinaryOperator(InternTokenType firstInternTokenType, String firstOperand,
			Operators operatorType, InternTokenType secondInternTokenType, String secondOperand, Sprite testSprite) {
		List<InternToken> internTokenList = buildBinaryOperator(firstInternTokenType, firstOperand, operatorType,
				secondInternTokenType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		InstrumentationTestCase.assertNotNull("Formula is not parsed correctly: " + firstOperand + operatorType
				+ secondOperand, parseTree);
		try {
			parseTree.interpretRecursive(testSprite);
			InstrumentationTestCase.fail("Formula interpretation is not as expected: " + firstOperand + operatorType
					+ secondOperand);
		} catch (Exception exception) {
			InstrumentationTestCase.assertEquals("Wrong exception message", exception.getMessage(),
					String.valueOf(Double.NaN));
		}
	}

	public static List<InternToken> buildBinaryOperator(InternTokenType firstInternTokenType, String firstOperand,
			Operators operatorType, InternTokenType secondInternTokenType, String secondOperand) {
		List<InternToken> firstOperandList = new LinkedList<InternToken>();
		firstOperandList.add(new InternToken(firstInternTokenType, firstOperand));
		List<InternToken> secondOperandList = new LinkedList<InternToken>();
		secondOperandList.add(new InternToken(secondInternTokenType, secondOperand));
		return buildBinaryOperator(firstOperandList, operatorType, secondOperandList);
	}

	public static List<InternToken> buildBinaryOperator(List<InternToken> firstOperand, Operators operatorType,
			List<InternToken> secondOperand) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.addAll(firstOperand);
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, operatorType.name()));
		internTokenList.addAll(secondOperand);
		return internTokenList;
	}

	public static List<InternToken> buildBinaryOperator(InternTokenType firstInternTokenType, String firstOperand,
			Operators operatorType, List<InternToken> secondOperandList) {
		List<InternToken> firstOperandList = new LinkedList<InternToken>();
		firstOperandList.add(new InternToken(firstInternTokenType, firstOperand));
		return buildBinaryOperator(firstOperandList, operatorType, secondOperandList);
	}

	public static List<InternToken> buildBinaryOperator(List<InternToken> firstOperandList,
			Operators operatorType, InternTokenType secondInternTokenType, String secondOperand) {
		List<InternToken> secondOperandList = new LinkedList<InternToken>();
		secondOperandList.add(new InternToken(secondInternTokenType, secondOperand));
		return buildBinaryOperator(firstOperandList, operatorType, secondOperandList);
	}
}
