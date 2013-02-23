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

import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;

import android.test.AndroidTestCase;

public class ParserTest extends AndroidTestCase {

	public void testLogicalOperators() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, ">"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 > 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, ">"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 > 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.GREATER_OR_EQUAL.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 >= 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.GREATER_OR_EQUAL.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 >= 2", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "<"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 < 2", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "<"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 < 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "<="));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 <= 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "<="));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 <= 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "="));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 = 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "="));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 = 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "!="));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 2 != 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "!="));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: 1 != 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_AND.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 0 AND 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_OR.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 1 OR 0", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_OR.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 0 OR 0", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive());
		internTokenList.clear();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_NOT.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.LOGICAL_AND.operatorName));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: NOT 0 AND 0", parseTree);
		assertEquals("Formula interpretation is not as expected", 0d, parseTree.interpretRecursive());
		internTokenList.clear();
	}

	public void testUnaryMinus() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: - 42.42", parseTree);
		assertEquals("Formula interpretation is not as expected", -42.42, parseTree.interpretRecursive());
	}

	public void testOperatorPriority() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "×"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  1 - 2 x 2", parseTree);
		assertEquals("Formula interpretation is not as expected", -3.0, parseTree.interpretRecursive());

	}

	public void testOperatorLeftBinding() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "5"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "4"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  5 - 4 - 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 0.0, parseTree.interpretRecursive());

		internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "100"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "÷"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "10"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "÷"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "10"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  100 ÷ 10 ÷ 10", parseTree);
		assertEquals("Formula interpretation is not as expected", 1.0, parseTree.interpretRecursive());

	}

	public void testOperatorChain() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "×"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "3"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "^"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  1 + 2 × 3 ^ 2 + 1", parseTree);
		assertEquals("Formula interpretation is not as expected", 20.0, parseTree.interpretRecursive());

	}

	public void testBracket() {

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE, ")"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "×"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  (1+2) x (1+2)", parseTree);
		assertEquals("Formula interpretation is not as expected", 9.0, parseTree.interpretRecursive());

		internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "^"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE, ")"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "-"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "2"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE, ")"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly:  -(1^2)--(-1--2)", parseTree);
		assertEquals("Formula interpretation is not as expected", 0.0, parseTree.interpretRecursive());

	}

}
