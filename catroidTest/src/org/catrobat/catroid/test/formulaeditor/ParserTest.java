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

public class ParserTest extends AndroidTestCase {

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
		testSprite.look.setScaleX(LOOK_SCALE);
		testSprite.look.setScaleY(LOOK_SCALE);
		testSprite.look.setRotation(LOOK_ROTATION);
		testSprite.look.setZIndex(LOOK_ZPOSITION);
	}

	public void testNumbers() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1.0", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, ""));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNull("Formula is not parsed correctly: <empty number> {}", parseTree);
		assertEquals("Parser error value not as expected", 0, internParser.getErrorTokenIndex());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "."));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNull("Formula is not parsed correctly: .", parseTree);
		assertEquals("Parser error value not as expected", 0, internParser.getErrorTokenIndex());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, ".1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNull("Formula is not parsed correctly: .1", parseTree);
		assertEquals("Parser error value not as expected", 0, internParser.getErrorTokenIndex());
		internTokenList.clear();
	}

	public void testLogicalOperators() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.GREATER_THAN.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 > 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.GREATER_THAN.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 > 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.GREATER_OR_EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 >= 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.GREATER_OR_EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 >= 2", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.SMALLER_THAN.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 < 2", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.SMALLER_THAN.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 < 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.SMALLER_OR_EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 <= 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.SMALLER_OR_EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 <= 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 = 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 = 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.NOT_EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 != 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.NOT_EQUAL.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 != 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_AND.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 0 AND 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_OR.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 1 OR 0", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_OR.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 0 OR 0", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_AND.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 0 AND 0", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();
	}

	public void testUnaryMinus() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: - 42.42", parseTree);
		assertEquals("Formula interpretation is not as expected", -42.42, parseTree.interpretRecursive(testSprite));
	}

	public void testOperatorPriority() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MULT.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  1 - 2 x 2", parseTree);
		assertEquals("Formula interpretation is not as expected", -3.0, parseTree.interpretRecursive(testSprite));

	}

	public void testOperatorLeftBinding() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "5"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "4"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  5 - 4 - 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0.0, parseTree.interpretRecursive(testSprite));

		internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "100"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.DIVIDE.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "10"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.DIVIDE.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "10"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  100 ÷ 10 ÷ 10", parseTree);
		assertEquals("Formula interpretation is not as expected", 1.0, parseTree.interpretRecursive(testSprite));

	}

	public void testOperatorChain() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MULT.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "3"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.POW.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  1 + 2 × 3 ^ 2 + 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 20.0, parseTree.interpretRecursive(testSprite));

		internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.POW.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "3"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MULT.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  1 + 2 ^ 3 * 2 ", parseTree);
		assertEquals("Formula interpretation is not as expected", 17.0, parseTree.interpretRecursive(testSprite));

	}

	public void testBracket() {

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MULT.name()));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  (1+2) x (1+2)", parseTree);
		assertEquals("Formula interpretation is not as expected", 9.0, parseTree.interpretRecursive(testSprite));

		internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.POW.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  -(1^2)--(-1--2)", parseTree);
		assertEquals("Formula interpretation is not as expected", 0.0, parseTree.interpretRecursive(testSprite));

	}

	public void testEmptyInput() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNull("Formula is not parsed correctly: EMPTY FORMULA {}", parseTree);
		assertEquals("Formula error value not as expected", InternFormulaParser.PARSER_NO_INPUT,
				internParser.getErrorTokenIndex());
	}

	public void testFuctionalAndSimpleBracketsCorrection() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ABS.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MULT.name()));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "5"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "10"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: abs(2 * (5 - 10))", parseTree);
		assertEquals("Formula interpretation is not as expected", 10.0, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "3"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MULT.name()));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 3 * (2 + cos(0)) ", parseTree);
		assertEquals("Formula interpretation is not as expected", 9.0, parseTree.interpretRecursive(testSprite));
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.MOD.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: mod( 1 , mod( 1 , mod( 1 , ( 1 )))) ", parseTree);
		assertEquals("Formula interpretation is not as expected", 0.0, parseTree.interpretRecursive(testSprite));
	}
}
