/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

	private static final double DELTA = 0.01;
	private static final float LOOK_ALPHA = 50f;
	private static final float LOOK_Y_POSITION = 23.4f;
	private static final float LOOK_X_POSITION = 5.6f;
	private static final float LOOK_BRIGHTNESS = 70f;
	private static final float LOOK_SCALE = 90.3f;
	private static final float LOOK_ROTATION = 30.7f;
	private static final int LOOK_ZPOSITION = 3;
	private Sprite testSprite;

	@Override
	protected void setUp() {
		testSprite = new Sprite("sprite");
		testSprite.look.setXInUserInterfaceDimensionUnit(LOOK_X_POSITION);
		testSprite.look.setYInUserInterfaceDimensionUnit(LOOK_Y_POSITION);
		testSprite.look.setTransparencyInUserInterfaceDimensionUnit(LOOK_ALPHA);
		testSprite.look.setBrightnessInUserInterfaceDimensionUnit(LOOK_BRIGHTNESS);
		testSprite.look.setSizeInUserInterfaceDimensionUnit(LOOK_SCALE);
		testSprite.look.setRotation(LOOK_ROTATION);
		testSprite.look.setZIndex(LOOK_ZPOSITION);
	}

	public void testSin() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "90"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: sin(90)", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
	}

	public void testCos() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "180"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: cos(180)", parseTree);
		assertEquals("Formula interpretation is not as expected", -1d, parseTree.interpretRecursive());
	}

	public void testTan() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.TAN.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "180"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: tan(180)", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(), DELTA);
	}

	public void testLn() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.LN.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2.7182818"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: ln(e)", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(), DELTA);
	}

	public void testLog() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.LOG.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "10"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: log(10)", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(), DELTA);
	}

	public void testPi() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.PI.name()));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: pi", parseTree);
		assertEquals("Formula interpretation is not as expected", Math.PI, parseTree.interpretRecursive(),
				DELTA);
	}

	public void testSqrt() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SQRT.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "100"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: sqrt(100)", parseTree);
		assertEquals("Formula interpretation is not as expected", 10d, parseTree.interpretRecursive(), DELTA);
	}

	public void testRandomNaturalNumbers() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER, ","));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: random(0,1)", parseTree);
		Double result = parseTree.interpretRecursive();
		assertTrue("Formula interpretation is not as expected", result == 0d || result == 1d);
	}

	public void testRound() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.33333"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: round(1.33333)", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(), DELTA);
	}

	public void testMod() {

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = Integer.valueOf(1 + offset);
			Integer divisor = Integer.valueOf(1 + offset);

			List<InternToken> internTokenList = new LinkedList<InternToken>();
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, dividend.toString()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, divisor.toString()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

			InternFormulaParser internParser = new InternFormulaParser(internTokenList);
			FormulaElement parseTree = internParser.parseFormula();

			assertNotNull("Formula is not parsed correctly: mod(" + dividend.toString() + ", " + divisor.toString()
					+ ")", parseTree);
			assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(),
					DELTA);
		}

		for (int offset = 0; offset < 100; offset += 2) {
			Integer dividend = Integer.valueOf(3 + offset);
			Integer divisor = Integer.valueOf(2 + offset);

			List<InternToken> internTokenList = new LinkedList<InternToken>();
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, dividend.toString()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, divisor.toString()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

			InternFormulaParser internParser = new InternFormulaParser(internTokenList);
			FormulaElement parseTree = internParser.parseFormula();

			assertNotNull("Formula is not parsed correctly: mod(" + dividend.toString() + ", " + divisor.toString()
					+ ")", parseTree);
			assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(),
					DELTA);
		}

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = Integer.valueOf(3 + offset);
			Integer divisor = Integer.valueOf(5 + offset);

			List<InternToken> internTokenList = new LinkedList<InternToken>();
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, dividend.toString()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, divisor.toString()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

			InternFormulaParser internParser = new InternFormulaParser(internTokenList);
			FormulaElement parseTree = internParser.parseFormula();

			assertNotNull("Formula is not parsed correctly: mod(" + dividend.toString() + ", " + divisor.toString()
					+ ")", parseTree);
			assertEquals("Formula interpretation is not as expected", dividend.doubleValue(),
					parseTree.interpretRecursive(), DELTA);
		}

		for (int offset = 0; offset < 10; offset += 1) {
			Integer dividend = Integer.valueOf(-3 - offset);
			Integer divisor = Integer.valueOf(2 + offset);

			List<InternToken> internTokenList = new LinkedList<InternToken>();
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
			internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.toString()));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, String.valueOf(Math.abs(dividend.intValue()))));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
			internTokenList.add(new InternToken(InternTokenType.NUMBER, divisor.toString()));
			internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

			InternFormulaParser internParser = new InternFormulaParser(internTokenList);
			FormulaElement parseTree = internParser.parseFormula();

			assertNotNull("Formula is not parsed correctly: mod(" + dividend.toString() + ", " + divisor.toString()
					+ ")", parseTree);
			assertEquals("Formula interpretation is not as expected", 1d + offset,
					parseTree.interpretRecursive(), DELTA);
		}
	}

	public void testAbs() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ABS.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: abs(-1)", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(), DELTA);
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

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: true", parseTree);
		assertEquals("Formula interpretation is not as expected", 1.0, parseTree.interpretRecursive());
	}

	public void testFalse() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.FALSE.name()));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: false", parseTree);
		assertEquals("Formula interpretation is not as expected", 0.0, parseTree.interpretRecursive());
	}

	public void testArcsin() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ARCSIN.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: arcsin(1)", parseTree);
		assertEquals("Formula interpretation is not as expected", 90d, parseTree.interpretRecursive());
	}

	public void testArccos() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ARCCOS.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: arccos(0)", parseTree);
		assertEquals("Formula interpretation is not as expected", 90d, parseTree.interpretRecursive());
	}

	public void testArctan() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ARCTAN.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: arctan(1)", parseTree);
		assertEquals("Formula interpretation is not as expected", 45d, parseTree.interpretRecursive());
	}

	public void testExp() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.EXP.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: exp(2)", parseTree);
		assertEquals("Formula interpretation is not as expected", Math.exp(2.0),
				parseTree.interpretRecursive());
	}

	public void testMax() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MAX.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "3"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER, ","));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "4"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: max(3,4)", parseTree);
		assertEquals("Formula interpretation is not as expected", 4d, parseTree.interpretRecursive());

	}

	public void testMin() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MIN.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "3"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER, ","));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "4"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: min(3,4)", parseTree);
		assertEquals("Formula interpretation is not as expected", 3d, parseTree.interpretRecursive());

	}

}
