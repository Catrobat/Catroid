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
		FormulaEditorUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.NUMBER, "90",
				Math.sin(Math.toRadians(90d)), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.STRING, "45.0",
				Math.sin(Math.toRadians(45d)), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.SIN, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.SIN, firstParameterList, Double.NaN, testSprite);
	}

	public void testCos() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.COS, InternTokenType.NUMBER, "180",
				Math.cos(Math.toRadians(180d)), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.COS, InternTokenType.STRING, "45.0",
				Math.cos(Math.toRadians(45d)), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.COS, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.COS, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.COS, firstParameterList, Double.NaN, testSprite);
	}

	public void testTan() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.NUMBER, "180",
				Math.tan(Math.toRadians(180d)), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.STRING, "45.0",
				Math.tan(Math.toRadians(45d)), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.TAN, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.TAN, firstParameterList, Double.NaN, testSprite);
	}

	public void testLn() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.LN, InternTokenType.NUMBER, "2.7182818",
				Math.log(2.7182818), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.LN, InternTokenType.STRING, "45.0", Math.log(45d),
				testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.LN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.LN, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.LN, firstParameterList, Double.NaN, testSprite);
	}

	public void testLog() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.NUMBER, "10", Math.log10(10),
				testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.STRING, "45.0", Math.log10(45),
				testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.LOG, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.LOG, firstParameterList, Double.NaN, testSprite);
	}

	public void testPi() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.PI.name()));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula();

		assertNotNull("Formula is not parsed correctly: pi", parseTree);
		assertEquals("Formula interpretation is not as expected", Math.PI, parseTree.interpretRecursive(testSprite));
	}

	public void testSqrt() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.NUMBER, "100", Math.sqrt(100),
				testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.STRING, "45.0", Math.sqrt(45),
				testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.SQRT, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.SQRT, firstParameterList, Double.NaN, testSprite);
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

		FormulaEditorUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "1",
				InternTokenType.STRING, "1", 1d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "",
				InternTokenType.STRING, "3", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "5",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.RAND, InternTokenType.STRING, "5",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = new LinkedList<InternToken>();
		List<InternToken> secondParameterList = new LinkedList<InternToken>();
		firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		secondParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorUtil.testDoubleParameterFunction(Functions.RAND, firstParameterList, secondParameterList,
				Double.NaN, testSprite);
	}

	public void testRound() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.NUMBER, "1.33333",
				((Long) Math.round(1.33333)).doubleValue(), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.STRING, "45.55555",
				((Long) Math.round(45.55555)).doubleValue(), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ROUND, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ROUND, firstParameterList, 0d, testSprite);
	}

	public void testMod() {

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = Integer.valueOf(1 + offset);
			Integer divisor = Integer.valueOf(1 + offset);

			FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), 0d, testSprite);
		}

		for (int offset = 0; offset < 100; offset += 2) {
			Integer dividend = Integer.valueOf(3 + offset);
			Integer divisor = Integer.valueOf(2 + offset);

			FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), 1d, testSprite);
		}

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = Integer.valueOf(3 + offset);
			Integer divisor = Integer.valueOf(5 + offset);

			FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), dividend.doubleValue(), testSprite);
		}

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = Integer.valueOf(-3 - offset);
			Integer divisor = Integer.valueOf(2 + offset);

			FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.NUMBER, dividend.toString(),
					InternTokenType.NUMBER, divisor.toString(), 1d + offset, testSprite);
		}

		FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "5",
				InternTokenType.STRING, "3", 2d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "",
				InternTokenType.STRING, "3", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "5",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, InternTokenType.STRING, "5",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = new LinkedList<InternToken>();
		List<InternToken> secondParameterList = new LinkedList<InternToken>();
		firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		secondParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MOD, firstParameterList, secondParameterList,
				Double.NaN, testSprite);
	}

	public void testAbs() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.NUMBER, "-1.1", Math.abs(-1.1),
				testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.STRING, "45.666",
				Math.abs(45.666), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ABS, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ABS, firstParameterList, Double.NaN, testSprite);
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
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.NUMBER, "1",
				Math.toDegrees(Math.asin(1)), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.STRING, "0.666",
				Math.toDegrees(Math.asin(0.666)), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCSIN, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCSIN, firstParameterList, Double.NaN, testSprite);
	}

	public void testArccos() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.NUMBER, "0",
				Math.toDegrees(Math.acos(0)), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.STRING, "0.666",
				Math.toDegrees(Math.acos(0.666)), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCCOS, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCCOS, firstParameterList, Double.NaN, testSprite);
	}

	public void testArctan() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.NUMBER, "1",
				Math.toDegrees(Math.atan(1)), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.STRING, "45.666",
				Math.toDegrees(Math.atan(45.666)), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCTAN, InternTokenType.STRING, NOT_NUMERICAL_STRING,
				0d, testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.ARCTAN, firstParameterList, Double.NaN, testSprite);
	}

	public void testExp() {
		FormulaEditorUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.NUMBER, "2", Math.exp(2),
				testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.STRING, "45.666",
				Math.exp(45.666), testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testSingleParameterFunction(Functions.EXP, InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d,
				testSprite);

		List<InternToken> firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "15.0",
				Operators.PLUS, InternTokenType.STRING, NOT_NUMERICAL_STRING);
		FormulaEditorUtil.testSingleParameterFunction(Functions.EXP, firstParameterList, Double.NaN, testSprite);
	}

	public void testMax() {
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.NUMBER, "3",
				InternTokenType.STRING, "4", 4d, testSprite);

		FormulaEditorUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, "22.33", 33.22, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "",
				InternTokenType.STRING, "22.33", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MAX, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = new LinkedList<InternToken>();
		List<InternToken> secondParameterList = new LinkedList<InternToken>();
		firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		secondParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MAX, firstParameterList, secondParameterList,
				Double.NaN, testSprite);

	}

	public void testMin() {
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.NUMBER, "3",
				InternTokenType.STRING, "4", 3d, testSprite);

		FormulaEditorUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, "22.33", 22.33, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "",
				InternTokenType.STRING, "22.33", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "",
				InternTokenType.STRING, "", 0d, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MIN, InternTokenType.STRING, "33.22",
				InternTokenType.STRING, NOT_NUMERICAL_STRING, 0d, testSprite);

		List<InternToken> firstParameterList = new LinkedList<InternToken>();
		List<InternToken> secondParameterList = new LinkedList<InternToken>();
		firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		secondParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorUtil.testDoubleParameterFunction(Functions.MIN, firstParameterList, secondParameterList,
				Double.NaN, testSprite);
	}

}
