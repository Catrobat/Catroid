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
package org.catrobat.catroid.test.formulaeditor;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

public final class FormulaEditorTestUtil {

	private FormulaEditorTestUtil() {
		throw new AssertionError();
	}

	public static List<InternToken> buildSingleParameterFunction(Functions function, List<InternToken> internTokenList) {
		List<InternToken> tokenList = new LinkedList<>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.addAll(internTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testSingleParameterFunction(Functions function, List<InternToken> internTokenList,
			Object expected, Scope testScope) {

		List<InternToken> tokenList = FormulaEditorTestUtil.buildSingleParameterFunction(function, internTokenList);
		FormulaElement parseTree = new InternFormulaParser(tokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static List<InternToken> buildSingleParameterFunction(Functions function, InternTokenType firstParameter,
			String parameterNumberValue) throws NumberFormatException {
		List<InternToken> tokenList = new LinkedList<>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		if (isParsableDouble(parameterNumberValue) && Double.valueOf(parameterNumberValue) < 0) {
			tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
			parameterNumberValue = String.valueOf(Math.abs(Double.valueOf(parameterNumberValue)));
		}
		tokenList.add(new InternToken(firstParameter, parameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static List<InternToken> buildDoubleParameterFunction(Functions function,
			List<InternToken> firstInternTokenList, List<InternToken> secondInternTokenList) {
		List<InternToken> tokenList = new LinkedList<>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.addAll(firstInternTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		tokenList.addAll(secondInternTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testDoubleParameterFunction(Functions function, List<InternToken> firstInternTokenList,
			List<InternToken> secondInternTokenList, Object expected, Scope testScope) {

		List<InternToken> internTokenList = buildDoubleParameterFunction(function, firstInternTokenList,
				secondInternTokenList);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static List<InternToken> buildDoubleParameterFunction(Functions function, InternTokenType firstParameter,
			String firstParameterNumberValue, InternTokenType secondParameter, String secondParameterNumberValue)
			throws NumberFormatException {
		List<InternToken> tokenList = new LinkedList<>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		if (isParsableDouble(firstParameterNumberValue) && Double.valueOf(firstParameterNumberValue) < 0) {
			tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
			firstParameterNumberValue = String.valueOf(Math.abs(Double.valueOf(firstParameterNumberValue)));
		}

		tokenList.add(new InternToken(firstParameter, firstParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		if (isParsableDouble(secondParameterNumberValue) && Double.valueOf(secondParameterNumberValue) < 0) {
			tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
			secondParameterNumberValue = String.valueOf(Math.abs(Double.valueOf(secondParameterNumberValue)));
		}

		tokenList.add(new InternToken(secondParameter, secondParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testDoubleParameterFunction(Functions function, InternTokenType firstInternTokenType,
			String firstParameter, InternTokenType secondInternTokenType, String secondParameter, Object expected,
			Scope testScope) {

		List<InternToken> internTokenList = buildDoubleParameterFunction(function, firstInternTokenType,
				firstParameter, secondInternTokenType, secondParameter);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static void testDoubleParameterFunction(Functions function, InternTokenType firstInternTokenType,
			String firstParameter, InternTokenType secondInternTokenType, String secondParameter, Double median,
			Double error, Scope testScope) {

		List<InternToken> internTokenList = buildDoubleParameterFunction(function, firstInternTokenType,
				firstParameter, secondInternTokenType, secondParameter);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		Double result = (double) parseTree.interpretRecursive(testScope);
		assertThat(result, closeTo(median, error));
	}

	public static List<InternToken> buildTripleParameterFunction(Functions function,
			List<InternToken> firstInternTokenList, List<InternToken> secondInternTokenList,
			List<InternToken> thirdInternTokenList) {
		List<InternToken> tokenList = new LinkedList<>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.addAll(firstInternTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		tokenList.addAll(secondInternTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		tokenList.addAll(thirdInternTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testTripleParameterFunction(Functions function,
			List<InternToken> firstInternTokenList,
			List<InternToken> secondInternTokenList,
			List<InternToken> thirdInternTokenList, Object expected, Scope testScope) {

		List<InternToken> internTokenList = buildTripleParameterFunction(function,
				firstInternTokenList,
				secondInternTokenList, thirdInternTokenList);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static List<InternToken> buildTripleParameterFunction(Functions function,
			InternTokenType firstParameter,
			String firstParameterNumberValue, InternTokenType secondParameter,
			String secondParameterNumberValue, InternTokenType thirdParameter,
			String thirdParameterNumberValue)
			throws NumberFormatException {
		List<InternToken> tokenList = new LinkedList<>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		if (isParsableDouble(firstParameterNumberValue) && Double.valueOf(firstParameterNumberValue) < 0) {
			tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
			firstParameterNumberValue = String.valueOf(Math.abs(Double.valueOf(firstParameterNumberValue)));
		}

		tokenList.add(new InternToken(firstParameter, firstParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		if (isParsableDouble(secondParameterNumberValue) && Double.valueOf(secondParameterNumberValue) < 0) {
			tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
			secondParameterNumberValue = String.valueOf(Math.abs(Double.valueOf(secondParameterNumberValue)));
		}

		tokenList.add(new InternToken(secondParameter, secondParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		if (isParsableDouble(thirdParameterNumberValue) && Double.valueOf(thirdParameterNumberValue) < 0) {
			tokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
			thirdParameterNumberValue =
					String.valueOf(Math.abs(Double.valueOf(thirdParameterNumberValue)));
		}

		tokenList.add(new InternToken(thirdParameter, thirdParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	public static void testTripleParameterFunction(Functions function,
			InternTokenType firstInternTokenType,
			String firstParameter, InternTokenType secondInternTokenType, String secondParameter,
			InternTokenType thirdInternTokenType,
			String thirdParameter, Object expected,
			Scope testScope) {

		List<InternToken> internTokenList = buildTripleParameterFunction(function,
				firstInternTokenType,
				firstParameter, secondInternTokenType, secondParameter, thirdInternTokenType,
				thirdParameter);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static void testBinaryOperator(InternTokenType firstInternTokenType, String firstOperand,
			Operators operatorType, InternTokenType secondInternTokenType, String secondOperand, Object expected,
			Scope testScope) {
		List<InternToken> internTokenList = buildBinaryOperator(firstInternTokenType, firstOperand, operatorType,
				secondInternTokenType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static void testBinaryOperator(List<InternToken> firstOperand, Operators operatorType,
			InternTokenType secondInternTokenType, String secondOperand, Object expected, Scope testScope) {
		List<InternToken> internTokenList = buildBinaryOperator(firstOperand, operatorType, secondInternTokenType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static void testBinaryOperator(List<InternToken> firstOperand, Operators operatorType,
			List<InternToken> secondOperand, Object expected, Scope testScope) {
		List<InternToken> internTokenList = buildBinaryOperator(firstOperand, operatorType, secondOperand);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(expected, parseTree.interpretRecursive(testScope));
	}

	public static void testSingleParameterFunction(Functions function, InternTokenType firstInternTokenType,
			String firstParameter, Object expected, Scope testScope) {
		List<InternToken> internTokenList = FormulaEditorTestUtil.buildSingleParameterFunction(function,
				firstInternTokenType, firstParameter);
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);
		Formula formula = new Formula(parseTree);

		assertNotNull(parseTree);
		assertEquals(expected, formula.interpretObject(testScope));
	}

	public static void testSingleToken(InternTokenType firstInternTokenType, String firstParameter, Object expected,
			Scope testScope) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(firstInternTokenType, firstParameter));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);
		Formula formula = new Formula(parseTree);

		assertNotNull(parseTree);
		assertEquals(expected, formula.interpretObject(testScope));
	}

	public static void testSingleTokenError(InternTokenType firstInternTokenType,
			String firstParameter, int expectedErrorTokenIndex, Scope testScope) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(firstInternTokenType, firstParameter));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internFormulaParser.parseFormula(testScope);

		assertNull(parseTree);
		assertEquals(expectedErrorTokenIndex, internFormulaParser.getErrorTokenIndex());
	}

	public static void testUnaryOperator(Operators operatorType, InternTokenType firstInternTokenType,
			String firstParameter, Object expected, Scope testScope) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, operatorType.name()));
		internTokenList.add(new InternToken(firstInternTokenType, firstParameter));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(testScope);
		Formula formula = new Formula(parseTree);

		assertNotNull(parseTree);
		assertEquals(expected, formula.interpretObject(testScope));
	}

	public static List<InternToken> buildBinaryOperator(InternTokenType firstInternTokenType, String firstOperand,
			Operators operatorType, InternTokenType secondInternTokenType, String secondOperand) {
		List<InternToken> firstOperandList = new LinkedList<>();
		firstOperandList.add(new InternToken(firstInternTokenType, firstOperand));
		List<InternToken> secondOperandList = new LinkedList<>();
		secondOperandList.add(new InternToken(secondInternTokenType, secondOperand));
		return buildBinaryOperator(firstOperandList, operatorType, secondOperandList);
	}

	public static List<InternToken> buildBinaryOperator(List<InternToken> firstOperand, Operators operatorType,
			List<InternToken> secondOperand) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.addAll(firstOperand);
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, operatorType.name()));
		internTokenList.addAll(secondOperand);
		return internTokenList;
	}

	public static List<InternToken> buildBinaryOperator(List<InternToken> firstOperandList,
			Operators operatorType, InternTokenType secondInternTokenType, String secondOperand) {
		List<InternToken> secondOperandList = new LinkedList<>();
		secondOperandList.add(new InternToken(secondInternTokenType, secondOperand));
		return buildBinaryOperator(firstOperandList, operatorType, secondOperandList);
	}

	public static void assertEqualsTokenLists(List<InternToken> expectedTokenList, List<InternToken> actualTokenList) {
		assertEquals(expectedTokenList.size(), actualTokenList.size());
		for (int index = 0; index < expectedTokenList.size(); index++) {
			assertEquals(expectedTokenList.get(index).getInternTokenType(),
					actualTokenList.get(index).getInternTokenType());
			assertEquals(expectedTokenList.get(index).getTokenStringValue(),
					actualTokenList.get(index).getTokenStringValue());
		}
	}

	private static boolean isParsableDouble(String value) {
		try {
			Double.valueOf(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
