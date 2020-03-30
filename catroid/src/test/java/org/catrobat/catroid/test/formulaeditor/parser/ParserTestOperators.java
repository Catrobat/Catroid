/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor.parser;

import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class ParserTestOperators {

	private Sprite testSprite;
	private static final Double TRUE = 1d;
	private static final Double FALSE = 0d;

	@Before
	public void setUp() {
		testSprite = new SingleSprite("sprite");
	}

	@Test
	public void testOperatorChain() {
		List<InternToken> firstTerm = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "1", Operators.PLUS, InternTokenType.NUMBER, "2");
		firstTerm = FormulaEditorTestUtil.buildBinaryOperator(firstTerm, Operators.MULT, InternTokenType.NUMBER, "3");
		List<InternToken> secondTerm = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "2", Operators.PLUS, InternTokenType.NUMBER, "1");
		FormulaEditorTestUtil.testBinaryOperator(firstTerm, Operators.POW, secondTerm, 20d, testSprite);

		firstTerm = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "1", Operators.PLUS, InternTokenType.NUMBER, "2");
		secondTerm = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "3", Operators.MULT, InternTokenType.NUMBER, "2");
		FormulaEditorTestUtil.testBinaryOperator(firstTerm, Operators.POW, secondTerm, 17d, testSprite);
	}

	@Test
	public void testOperatorLeftBinding() {
		List<InternToken> firstTerm = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MINUS, InternTokenType.NUMBER, "4");
		FormulaEditorTestUtil.testBinaryOperator(firstTerm, Operators.MINUS, InternTokenType.NUMBER, "1", 0d, testSprite);

		firstTerm = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "100", Operators.DIVIDE, InternTokenType.NUMBER, "10");
		FormulaEditorTestUtil.testBinaryOperator(firstTerm, Operators.DIVIDE, InternTokenType.NUMBER, "10", 1d, testSprite);
	}

	@Test
	public void testOperatorPriority() {
		List<InternToken> firstTerm = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "1", Operators.MINUS, InternTokenType.NUMBER, "2");
		FormulaEditorTestUtil.testBinaryOperator(firstTerm, Operators.MULT, InternTokenType.NUMBER, "2", -3d, testSprite);
	}

	@Test
	public void testUnaryMinus() {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull(parseTree);
		assertEquals(-42.42, parseTree.interpretRecursive(testSprite));
	}

	@Test
	public void testGreaterThan() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "2", Operators.GREATER_THAN,
				InternTokenType.NUMBER, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.GREATER_THAN,
				InternTokenType.NUMBER, "1", FALSE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "2", Operators.GREATER_THAN,
				InternTokenType.STRING, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.GREATER_THAN,
				InternTokenType.STRING, "1", FALSE, testSprite);
	}

	@Test
	public void testGreaterOrEqualThan() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "2", Operators.GREATER_OR_EQUAL,
				InternTokenType.NUMBER, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.GREATER_OR_EQUAL,
				InternTokenType.NUMBER, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "0", Operators.GREATER_OR_EQUAL,
				InternTokenType.NUMBER, "1", FALSE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "2", Operators.GREATER_OR_EQUAL,
				InternTokenType.STRING, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.GREATER_OR_EQUAL,
				InternTokenType.STRING, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "0", Operators.GREATER_OR_EQUAL,
				InternTokenType.STRING, "1", FALSE, testSprite);
	}

	@Test
	public void testSmallerThan() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.SMALLER_THAN,
				InternTokenType.NUMBER, "1", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "0", Operators.SMALLER_THAN,
				InternTokenType.NUMBER, "1", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.SMALLER_THAN,
				InternTokenType.STRING, "1", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "0", Operators.SMALLER_THAN,
				InternTokenType.STRING, "1", TRUE, testSprite);
	}

	@Test
	public void testSmallerOrEqualThan() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "2", Operators.SMALLER_OR_EQUAL,
				InternTokenType.NUMBER, "1", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.SMALLER_OR_EQUAL,
				InternTokenType.NUMBER, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "0", Operators.SMALLER_OR_EQUAL,
				InternTokenType.NUMBER, "1", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "2", Operators.SMALLER_OR_EQUAL,
				InternTokenType.STRING, "1", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.SMALLER_OR_EQUAL,
				InternTokenType.STRING, "1", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "0", Operators.SMALLER_OR_EQUAL,
				InternTokenType.STRING, "1", TRUE, testSprite);
	}

	@Test
	public void testEqual() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.EQUAL, InternTokenType.NUMBER, "1",
				TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "5", Operators.EQUAL, InternTokenType.NUMBER, "1",
				FALSE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "equalString", Operators.EQUAL,
				InternTokenType.STRING, "equalString", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.EQUAL, InternTokenType.STRING,
				"1.0", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.EQUAL, InternTokenType.NUMBER,
				"1.0", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.EQUAL, InternTokenType.STRING,
				"1.0", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1.0", Operators.EQUAL, InternTokenType.NUMBER,
				"1.9", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "!`\"§$%&/()=?", Operators.EQUAL,
				InternTokenType.STRING, "!`\"§$%&/()=????", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "555.555", Operators.EQUAL,
				InternTokenType.STRING, "055.77.77", FALSE, testSprite);
	}

	@Test
	public void testNegativeZeroEqualsZero() {

		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.EQUAL.name(), null,
				new FormulaElement(ElementType.NUMBER, "-0", null),
				new FormulaElement(ElementType.NUMBER, "0", null));

		assertEquals(1d, formulaElement.interpretRecursive(null));

		formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.EQUAL.name(), null,
				new FormulaElement(ElementType.NUMBER, "0", null),
				new FormulaElement(ElementType.NUMBER, "-0", null));

		assertEquals(1d, formulaElement.interpretRecursive(null));
	}

	@Test
	public void testEqualsNaN() {
		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.EQUAL.name(), null,
				new FormulaElement(ElementType.NUMBER, String.valueOf(Double.NaN), null),
				new FormulaElement(ElementType.NUMBER, String.valueOf(Double.NaN), null));
		assertEquals(1d, formulaElement.interpretRecursive(null));
	}

	@Test
	public void testNotEqual() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.NOT_EQUAL, InternTokenType.NUMBER,
				"1", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "5", Operators.NOT_EQUAL, InternTokenType.NUMBER,
				"1", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.NOT_EQUAL, InternTokenType.STRING,
				"1", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "5", Operators.NOT_EQUAL, InternTokenType.STRING,
				"1", TRUE, testSprite);
	}

	@Test
	public void testNOT() {
		FormulaEditorTestUtil.testUnaryOperator(Operators.LOGICAL_NOT, InternTokenType.NUMBER, "1", FALSE, testSprite);
		FormulaEditorTestUtil.testUnaryOperator(Operators.LOGICAL_NOT, InternTokenType.NUMBER, "0", TRUE, testSprite);

		FormulaEditorTestUtil.testUnaryOperator(Operators.LOGICAL_NOT, InternTokenType.STRING, "1", FALSE, testSprite);
		FormulaEditorTestUtil.testUnaryOperator(Operators.LOGICAL_NOT, InternTokenType.STRING, "0", TRUE, testSprite);
	}

	@Test
	public void testAND() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "0", Operators.LOGICAL_AND,
				InternTokenType.NUMBER, "0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.LOGICAL_AND,
				InternTokenType.NUMBER, "0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.LOGICAL_AND,
				InternTokenType.NUMBER, "1", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "0", Operators.LOGICAL_AND,
				InternTokenType.STRING, "0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.LOGICAL_AND,
				InternTokenType.STRING, "0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.LOGICAL_AND,
				InternTokenType.STRING, "1", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "0", Operators.LOGICAL_AND,
				InternTokenType.STRING, "0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.LOGICAL_AND,
				InternTokenType.NUMBER, "0", FALSE, testSprite);
	}

	@Test
	public void testOR() {
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "0", Operators.LOGICAL_OR, InternTokenType.NUMBER,
				"0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.LOGICAL_OR, InternTokenType.NUMBER,
				"0", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.LOGICAL_OR, InternTokenType.NUMBER,
				"1", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "0", Operators.LOGICAL_OR, InternTokenType.STRING,
				"0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.LOGICAL_OR, InternTokenType.STRING,
				"0", TRUE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "1", Operators.LOGICAL_OR, InternTokenType.STRING,
				"1", TRUE, testSprite);

		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, "0", Operators.LOGICAL_OR, InternTokenType.NUMBER,
				"0", FALSE, testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, "1", Operators.LOGICAL_OR, InternTokenType.STRING,
				"0", TRUE, testSprite);
	}

	@Test
	public void testAddition() {
		String firstOperand = "1.3";
		String secondOperand = "3";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.PLUS,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) + Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.PLUS,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) + Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.PLUS,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) + Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.PLUS,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) + Double.valueOf(secondOperand),
				testSprite);

		firstOperand = "NotANumber";
		secondOperand = "3.14";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.PLUS,
				InternTokenType.NUMBER, secondOperand, Double.NaN, testSprite);

		List<InternToken> first = FormulaEditorTestUtil.buildDoubleParameterFunction(Functions.JOIN,
				InternTokenType.STRING, "FreeYour", InternTokenType.STRING, "Mind");
		List<InternToken> second = new LinkedList<>();
		second.add(new InternToken(InternTokenType.NUMBER, "1"));
		FormulaEditorTestUtil.testBinaryOperator(first, Operators.PLUS, second, Double.NaN, testSprite);
	}

	@Test
	public void testDivision() {
		String firstOperand = "9.0";
		String secondOperand = "2";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.DIVIDE,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) / Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.DIVIDE,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) / Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.DIVIDE,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) / Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.DIVIDE,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) / Double.valueOf(secondOperand),
				testSprite);

		firstOperand = "NotANumber";
		secondOperand = "3.14";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.DIVIDE,
				InternTokenType.NUMBER, secondOperand, Double.NaN, testSprite);

		List<InternToken> first = FormulaEditorTestUtil.buildDoubleParameterFunction(Functions.JOIN,
				InternTokenType.STRING, "FreeYour", InternTokenType.STRING, "Mind");
		List<InternToken> second = new LinkedList<>();
		second.add(new InternToken(InternTokenType.NUMBER, "1"));
		FormulaEditorTestUtil.testBinaryOperator(first, Operators.DIVIDE, second, Double.NaN, testSprite);
	}

	@Test
	public void testMultiplication() {
		String firstOperand = "9.0";
		String secondOperand = "2";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.MULT,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) * Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.MULT,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) * Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.MULT,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) * Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.MULT,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) * Double.valueOf(secondOperand),
				testSprite);

		firstOperand = "NotANumber";
		secondOperand = "3.14";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.MULT,
				InternTokenType.NUMBER, secondOperand, Double.NaN, testSprite);

		List<InternToken> first = FormulaEditorTestUtil.buildDoubleParameterFunction(Functions.JOIN,
				InternTokenType.STRING, "FreeYour", InternTokenType.STRING, "Mind");
		List<InternToken> second = new LinkedList<>();
		second.add(new InternToken(InternTokenType.NUMBER, "1"));
		FormulaEditorTestUtil.testBinaryOperator(first, Operators.MULT, second, Double.NaN, testSprite);
	}

	@Test
	public void testSubstraction() {
		String firstOperand = "9.0";
		String secondOperand = "2";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.MINUS,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) - Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.MINUS,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) - Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.NUMBER, firstOperand, Operators.MINUS,
				InternTokenType.STRING, secondOperand, Double.valueOf(firstOperand) - Double.valueOf(secondOperand),
				testSprite);
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.MINUS,
				InternTokenType.NUMBER, secondOperand, Double.valueOf(firstOperand) - Double.valueOf(secondOperand),
				testSprite);

		firstOperand = "NotANumber";
		secondOperand = "3.14";
		FormulaEditorTestUtil.testBinaryOperator(InternTokenType.STRING, firstOperand, Operators.MINUS,
				InternTokenType.NUMBER, secondOperand, Double.NaN, testSprite);

		List<InternToken> first = FormulaEditorTestUtil.buildDoubleParameterFunction(Functions.JOIN,
				InternTokenType.STRING, "FreeYour", InternTokenType.STRING, "Mind");
		List<InternToken> second = new LinkedList<>();
		second.add(new InternToken(InternTokenType.NUMBER, "1"));
		FormulaEditorTestUtil.testBinaryOperator(first, Operators.MINUS, second, Double.NaN, testSprite);
	}
}
