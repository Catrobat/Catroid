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
package org.catrobat.catroid.test.formulaeditor;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;

import java.util.LinkedList;
import java.util.List;

public class ParserTestStrings extends AndroidTestCase {

	private static final double DELTA = 0.01;
	private Sprite testSprite;
	public static final String STRING_BEGIN_END = "\'";
	public static final String USERVARIABLE_BEGIN_END = "\"";

	@Override
	protected void setUp() {
		testSprite = new Sprite("testsprite");
	}

	public void testStringInterpretation() {
		String testString = "testString";
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.STRING, testString));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly:" + testString, parseTree);
		assertEquals("Formula interpretation is not as expected:" + testString, testString,
				parseTree.interpretRecursive(testSprite));
	}

	//TODO: Decision: string with number -> compute (f.e '1.1' + compute should eval to string or number ?
	//TODO: testLengthWithUserVariableAsParameterInterpretation
	//TODO: testLetterWithUserVariableParameterS
	//TODO: testJoinWithUserVariableParameterS

	public void testStringToNumberInterpretation() {
		String stringWithNumber = "9.9";
		String stringWithNumber2 = "3.4";
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.STRING, stringWithNumber));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokenList.add(new InternToken(InternTokenType.STRING, stringWithNumber2));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + stringWithNumber + "+" + stringWithNumber2, parseTree);
		assertEquals("Formula interpretation is not as expected: " + stringWithNumber + "+" + stringWithNumber2, 13.3,
				parseTree.interpretRecursive(testSprite));
	}

	public void testLength() {
		String testString = "testString";
		List<InternToken> internTokenList = buildSingleParameterFunction(Functions.LENGTH, InternTokenType.STRING,
				testString);
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LENGTH.name() + "(" + testString + ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LENGTH.name() + "(" + testString + ")",
				(double) testString.length(), parseTree.interpretRecursive(testSprite));

		String number = "1.1";
		internTokenList = buildSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, number);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LENGTH.name() + "(" + number + ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LENGTH.name() + "(" + number + ")",
				(double) number.length(), parseTree.interpretRecursive(testSprite));
	}

	public void testLetter() {
		String letterString = "letterString";
		String index = "7";
		int normalizedIndex = Integer.valueOf(index) - 1;
		List<InternToken> internTokenList = buildDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER,
				index, InternTokenType.STRING, letterString);
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + index + "," + letterString
				+ ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + index + ","
				+ letterString + ")", String.valueOf(letterString.charAt(normalizedIndex)),
				parseTree.interpretRecursive(testSprite));

		index = "0";
		String emptyString = "";
		internTokenList = buildDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + index + "," + letterString
				+ ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + index + ","
				+ letterString + ")", emptyString, parseTree.interpretRecursive(testSprite));

		index = "-5";
		emptyString = "";
		internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.LETTER.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "5"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokenList.add(new InternToken(InternTokenType.STRING, letterString));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + index + "," + letterString
				+ ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + index + ","
				+ letterString + ")", emptyString, parseTree.interpretRecursive(testSprite));

		index = String.valueOf(letterString.length() + 1);
		internTokenList = buildDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + index + "," + letterString
				+ ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + index + ","
				+ letterString + ")", emptyString, parseTree.interpretRecursive(testSprite));

		index = "0";
		emptyString = "";
		letterString = emptyString;
		internTokenList = buildDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + index + "," + letterString
				+ ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + index + ","
				+ letterString + ")", emptyString, parseTree.interpretRecursive(testSprite));

		letterString = "letterString";
		index = "2";
		normalizedIndex = Integer.valueOf(index) - 1;
		internTokenList = buildDoubleParameterFunction(Functions.LETTER, InternTokenType.STRING, index,
				InternTokenType.STRING, letterString);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + index + "," + letterString
				+ ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + index + ","
				+ letterString + ")", String.valueOf(letterString.charAt(normalizedIndex)),
				parseTree.interpretRecursive(testSprite));

		String firstParameter = "hello";
		String secondParameter = " world";
		normalizedIndex = Integer.valueOf(index) - 1;
		internTokenList = buildDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter);
		internTokenList = buildSingleParameterFunction(Functions.LENGTH, internTokenList);
		internTokenList = buildDoubleParameterFunction(Functions.LETTER, internTokenList, InternTokenType.STRING,
				firstParameter + secondParameter);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + Functions.LENGTH.name()
				+ "(" + Functions.JOIN.name() + "(" + firstParameter + "," + secondParameter + ")" + ")" + ","
				+ firstParameter + secondParameter + ")", parseTree);
		assertEquals(
				"Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + Functions.LENGTH.name()
						+ "(" + Functions.JOIN.name() + "(" + firstParameter + "," + secondParameter + ")" + ")" + ","
						+ firstParameter + secondParameter + ")", String.valueOf((firstParameter + secondParameter)
						.charAt((firstParameter + secondParameter).length() - 1)),
				parseTree.interpretRecursive(testSprite));

	}

	public void testJoin() {
		String firstParameter = "first";
		String secondParameter = "second";
		List<InternToken> internTokenList = buildDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING,
				firstParameter, InternTokenType.STRING, secondParameter);
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", firstParameter + secondParameter, parseTree.interpretRecursive(testSprite));

		firstParameter = "";
		secondParameter = "second";
		internTokenList = buildDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", firstParameter + secondParameter, parseTree.interpretRecursive(testSprite));

		firstParameter = "first";
		secondParameter = "";
		internTokenList = buildDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", firstParameter + secondParameter, parseTree.interpretRecursive(testSprite));

		firstParameter = "5";
		secondParameter = "string";
		internTokenList = buildDoubleParameterFunction(Functions.JOIN, InternTokenType.NUMBER, firstParameter,
				InternTokenType.STRING, secondParameter);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", firstParameter + secondParameter, parseTree.interpretRecursive(testSprite));

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%%/&%(())??";
		internTokenList = buildDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter);
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", parseTree);
		assertEquals("Formula interpretation is not as expected: " + Functions.JOIN.name() + "(" + firstParameter + ","
				+ secondParameter + ")", firstParameter + secondParameter, parseTree.interpretRecursive(testSprite));
	}

	public void testEqual() {
		String firstOperand = "equalString";
		String secondOperand = "equalString";
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.STRING, firstOperand));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.STRING, secondOperand));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + firstOperand + Operators.EQUAL + secondOperand, parseTree);
		assertEquals("Formula interpretation is not as expected: " + firstOperand + Operators.EQUAL.name()
				+ secondOperand, firstOperand.compareTo(secondOperand) == 0,
				(Double) parseTree.interpretRecursive(testSprite) == 1d);

		firstOperand = "1";
		secondOperand = "1.0";
		internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.NUMBER, firstOperand));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.STRING, secondOperand));
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + firstOperand + Operators.EQUAL + secondOperand, parseTree);
		assertEquals("Formula interpretation is not as expected: " + firstOperand + Operators.EQUAL.name()
				+ secondOperand, Double.valueOf(firstOperand).compareTo(Double.valueOf(secondOperand)) == 0,
				(Double) parseTree.interpretRecursive(testSprite) == 1d);

		firstOperand = "1";
		secondOperand = "1.0";
		internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.STRING, firstOperand));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, secondOperand));
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + firstOperand + Operators.EQUAL + secondOperand, parseTree);
		assertEquals("Formula interpretation is not as expected: " + firstOperand + Operators.EQUAL.name()
				+ secondOperand, Double.valueOf(firstOperand).compareTo(Double.valueOf(secondOperand)) == 0,
				(Double) parseTree.interpretRecursive(testSprite) == 1d);

		firstOperand = "1.0";
		secondOperand = "1.9";
		internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.STRING, firstOperand));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, secondOperand));
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + firstOperand + Operators.EQUAL + secondOperand, parseTree);
		assertEquals("Formula interpretation is not as expected: " + firstOperand + Operators.EQUAL.name()
				+ secondOperand, Double.valueOf(firstOperand).compareTo(Double.valueOf(secondOperand)) == 1,
				(Double) parseTree.interpretRecursive(testSprite) == 1d);

		firstOperand = "!`\"§$%&/()=?";
		secondOperand = "!`\"§$%&/()=????";
		internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.STRING, firstOperand));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.STRING, secondOperand));
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + firstOperand + Operators.EQUAL + secondOperand, parseTree);
		assertEquals("Formula interpretation is not as expected: " + firstOperand + Operators.EQUAL.name()
				+ secondOperand, firstOperand.compareTo(secondOperand) == 0,
				(Double) parseTree.interpretRecursive(testSprite) == 1d);

		firstOperand = "555.555";
		secondOperand = "055.77.77";
		internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.NUMBER, firstOperand));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.STRING, secondOperand));
		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();
		assertNotNull("Formula is not parsed correctly: " + firstOperand + Operators.EQUAL + secondOperand, parseTree);
		assertEquals("Formula interpretation is not as expected: " + firstOperand + Operators.EQUAL.name()
				+ secondOperand, false, (Double) parseTree.interpretRecursive(testSprite) == 1d);
	}

	private List<InternToken> buildDoubleParameterFunction(Functions function, InternTokenType firstParameter,
			String firstParameterNumberValue, InternTokenType secondParameter, String secondParameterNumberValue) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.add(new InternToken(firstParameter, firstParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		tokenList.add(new InternToken(secondParameter, secondParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	private List<InternToken> buildDoubleParameterFunction(Functions function, List<InternToken> internTokenList,
			InternTokenType secondParameter, String secondParameterNumberValue) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.addAll(internTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		tokenList.add(new InternToken(secondParameter, secondParameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	private List<InternToken> buildSingleParameterFunction(Functions function, InternTokenType firstParameter,
			String parameterNumberValue) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.add(new InternToken(firstParameter, parameterNumberValue));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

	private List<InternToken> buildSingleParameterFunction(Functions function, List<InternToken> internTokenList) {
		List<InternToken> tokenList = new LinkedList<InternToken>();
		tokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.name()));
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		tokenList.addAll(internTokenList);
		tokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return tokenList;
	}

}
