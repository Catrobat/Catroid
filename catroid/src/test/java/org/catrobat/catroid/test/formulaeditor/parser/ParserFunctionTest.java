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

import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class ParserFunctionTest {

	@Before
	public void setUp() {
	}

	@Test
	public void testPi() {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.PI.name()));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(Math.PI, parseTree.interpretRecursive(null));
	}

	@Test
	public void testRandomNaturalNumbers() {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER, ","));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(null);

		assertNotNull(parseTree);
		Double result = (Double) parseTree.interpretRecursive(null);
		assertThat(result, is(anyOf(equalTo(0d), equalTo(1d))));
	}

	@Test
	public void testInvalidFunction() {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, "INVALID_FUNCTION"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(null);

		assertNull(parseTree);
		assertEquals(0, internParser.getErrorTokenIndex());
	}

	@Test
	public void testTrue() {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.TRUE.name()));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(1.0, parseTree.interpretRecursive(null));
	}

	@Test
	public void testFalse() {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.FALSE.name()));
		FormulaElement parseTree = new InternFormulaParser(internTokenList).parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(0.0, parseTree.interpretRecursive(null));
	}

	@Test
	public void testArctan2ZeroParameter() {
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.ARCTAN2, InternTokenType.NUMBER, "0",
				InternTokenType.NUMBER, "0", 0.0, 180.0, null);
	}
}
