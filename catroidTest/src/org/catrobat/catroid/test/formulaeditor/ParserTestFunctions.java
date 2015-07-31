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

public class ParserTestFunctions extends AndroidTestCase {

	private Sprite testSprite;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";

	@Override
	protected void setUp() {
		testSprite = new Sprite("sprite");
	}

	public void testSin() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.NUMBER, "90",
				Math.sin(Math.toRadians(90d)), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.STRING, "45.0",
				Math.sin(Math.toRadians(45d)), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SIN, firstParameterList, Double.NaN, testSprite);
	}

	public void testCos() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.COS, InternTokenType.NUMBER, "180",
				Math.cos(Math.toRadians(180d)), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.COS, InternTokenType.STRING, "45.0",
				Math.cos(Math.toRadians(45d)), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.COS, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.COS, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.COS, firstParameterList, Double.NaN, testSprite);
	}

	public void testTan() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.NUMBER, "180",
				Math.tan(Math.toRadians(180d)), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.STRING, "45.0",
				Math.tan(Math.toRadians(45d)), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.TAN, firstParameterList, Double.NaN, testSprite);
	}

	public void testLn() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LN, InternTokenType.NUMBER, "2.7182818",
				Math.log(2.7182818), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LN, InternTokenType.STRING, "45.0", Math.log(45d),
				testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LN, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LN, firstParameterList, Double.NaN, testSprite);
	}

	public void testLog() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.NUMBER, "10", Math.log10(10),
				testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.STRING, "45.0", Math.log10(45),
				testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LOG, firstParameterList, Double.NaN, testSprite);
	}

	public void testPi() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.PI.name()));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		assertNotNull("Formula is not parsed correctly: pi", parseTree);
		assertEquals("Formula interpretation is not as expected", Math.PI, parseTree.interpretRecursive(testSprite));
	}

	public void testSqrt() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.NUMBER, "100", Math.sqrt(100),
				testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.STRING, "45.0", Math.sqrt(45),
				testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.SQRT, firstParameterList, Double.NaN, testSprite);
	}

	public void testRandomNaturalNumbers() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER, ","));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		assertNotNull("Formula is not parsed correctly: random(0,1)", parseTree);
		Double result = (Double) parseTree.interpretRecursive(testSprite);
		assertTrue("Formula interpretation is not as expected", result == 0d || result == 1d);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "1",
				InternTokenType.STRING, "1", 1d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "",
				InternTokenType.STRING, "3", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "5",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "5",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.RAND, firstParameterList, secondParameterList,
				Double.NaN, testSprite);
	}

	public void testRound() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.NUMBER, "1.33333",
				((Long) Math.round(1.33333)).doubleValue(), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.STRING, "45.55555",
				((Long) Math.round(45.55555)).doubleValue(), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ROUND, firstParameterList, 0d, testSprite);
	}

	public void testFloor() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.FLOOR, InternTokenType.NUMBER, "1.33333",
				Math.floor(1.33333), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.FLOOR, InternTokenType.STRING, "45.55555",
				Math.floor(45.55555), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.FLOOR, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.FLOOR, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.FLOOR, firstParameterList, Double.NaN, testSprite);
	}

	public void testCeil() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.CEIL, InternTokenType.NUMBER, "1.33333",
				Math.ceil(1.33333), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.CEIL, InternTokenType.STRING, "45.55555",
				Math.ceil(45.55555), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.CEIL, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.CEIL, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.CEIL, firstParameterList, Double.NaN, testSprite);
	}

	public void testMod() {

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = 1 + offset;
			Integer divisor = 1 + offset;

			FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), 0d, testSprite);
		}

		for (int offset = 0; offset < 100; offset += 2) {
			Integer dividend = 3 + offset;
			Integer divisor = 2 + offset;

			FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), 1d, testSprite);
		}

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = 3 + offset;
			Integer divisor = 5 + offset;

			FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), dividend.doubleValue(), testSprite);
		}

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = -3 - offset;
			Integer divisor = 2 + offset;

			FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), 1d + offset, testSprite);
		}

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "5",
				InternTokenType.STRING, "3", 2d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "",
				InternTokenType.STRING, "3", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "5",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "5",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MOD, firstParameterList, secondParameterList,
				Double.NaN, testSprite);
	}

	public void testAbs() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.NUMBER, "-1.1", Math.abs(-1.1),
				testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.STRING, "45.666",
				Math.abs(45.666), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ABS, firstParameterList, Double.NaN, testSprite);
	}

	public void testInvalidFunction() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, "INVALID_FUNCTION"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNull("Formula parsed but should not: INVALID_FUNCTION(1)", parseTree);
		assertEquals("Formula error value is not as expected", 0, internParser.getErrorTokenIndex());
	}

	public void testTrue() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.TRUE.name()));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		assertNotNull("Formula is not parsed correctly: true", parseTree);
		assertEquals("Formula interpretation is not as expected", 1.0, parseTree.interpretRecursive(testSprite));
	}

	public void testFalse() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.FALSE.name()));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		assertNotNull("Formula is not parsed correctly: false", parseTree);
		assertEquals("Formula interpretation is not as expected", 0.0, parseTree.interpretRecursive(testSprite));
	}

	public void testArcsin() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.NUMBER, "1",
				Math.toDegrees(Math.asin(1)), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.STRING, "0.666",
				Math.toDegrees(Math.asin(0.666)), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCSIN, firstParameterList, Double.NaN, testSprite);
	}

	public void testArccos() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.NUMBER, "0",
				Math.toDegrees(Math.acos(0)), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.STRING, "0.666",
				Math.toDegrees(Math.acos(0.666)), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCCOS, firstParameterList, Double.NaN, testSprite);
	}

	public void testArctan() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.NUMBER, "1",
				Math.toDegrees(Math.atan(1)), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.STRING, "45.666",
				Math.toDegrees(Math.atan(45.666)), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.ARCTAN, firstParameterList, Double.NaN, testSprite);
	}

	public void testExp() {
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.NUMBER, "2", Math.exp(2),
				testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.STRING, "45.666",
				Math.exp(45.666), testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.EXP, firstParameterList, Double.NaN, testSprite);
	}

	public void testMax() {
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.NUMBER, "3",
				InternTokenType.STRING, "4", 4d, testSprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, "22.33", 33.22, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "",
				InternTokenType.STRING, "22.33", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MAX, firstParameterList, secondParameterList,
				Double.NaN, testSprite);
	}

	public void testMin() {
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.NUMBER, "3",
				InternTokenType.STRING, "4", 3d, testSprite);

		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, "22.33", 22.33, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "",
				InternTokenType.STRING, "22.33", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.MIN, firstParameterList, secondParameterList,
				Double.NaN, testSprite);
	}
}
