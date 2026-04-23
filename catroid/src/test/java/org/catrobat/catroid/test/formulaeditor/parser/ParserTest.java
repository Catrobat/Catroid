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
package org.catrobat.catroid.test.formulaeditor.parser;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ParserTest {
	@Mock
	private Scope scope;

	@Test
	public void testNumbers() {
		FormulaEditorTestUtil.testSingleToken(InternTokenType.NUMBER, "1.0", "1.0", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.NUMBER, "1", "1", scope);

		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.NUMBER, "", 0, null);
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.NUMBER, ".", 0, null);
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.NUMBER, ".1", 0, null);
	}

	@Test
	public void testStrings() {
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, "1.0", "1.0", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, "1", "1", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, "", "", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, ".", ".", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, ".1", ".1", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, "1.1.1", "1.1.1", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, "\"o.o\"", "\"o.o\"", null);
		FormulaEditorTestUtil.testSingleToken(InternTokenType.STRING, "\'^_^\'", "\'^_^\'", null);
	}

	@Test
	public void testBracket() {
		List<InternToken> internTokenList = new LinkedList<>();

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
		FormulaElement parseTree = internParser.parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(9.0, parseTree.interpretRecursive(scope));

		internTokenList = new LinkedList<>();

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
		parseTree = internParser.parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(0.0, parseTree.interpretRecursive(scope));
	}

	@Test
	public void testEmptyInput() {
		List<InternToken> internTokenList = new LinkedList<>();

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);

		assertNull(parseTree);
		assertEquals(InternFormulaParser.PARSER_NO_INPUT, internParser.getErrorTokenIndex());
	}

	@Test
	public void testFuctionalAndSimpleBracketsCorrection() {
		List<InternToken> internTokenList = new LinkedList<>();

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
		FormulaElement parseTree = internParser.parseFormula(scope);

		assertNotNull(parseTree);
		assertEquals(10.0, parseTree.interpretRecursive(scope));
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
		parseTree = internParser.parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(9.0, parseTree.interpretRecursive(scope));
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
		parseTree = internParser.parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(0.0, parseTree.interpretRecursive(null));
	}
}
