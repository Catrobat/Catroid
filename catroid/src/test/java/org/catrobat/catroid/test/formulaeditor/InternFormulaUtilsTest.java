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

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaUtils;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;

import static org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.assertEqualsTokenLists;

@RunWith(JUnit4.class)
public class InternFormulaUtilsTest {

	@Test
	public void testGetFunctionByFunctionBracketCloseOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull(InternFormulaUtils.getFunctionByFunctionBracketClose(null, 0));
		assertNull(InternFormulaUtils.getFunctionByFunctionBracketClose(internTokens, 2));
		assertNull(InternFormulaUtils.getFunctionByFunctionBracketClose(internTokens, 1));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull(InternFormulaUtils.getFunctionByFunctionBracketClose(internTokens, 2));
	}

	@Test
	public void testgetFunctionByParameterDelimiter() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		List<InternToken> functionTokens = InternFormulaUtils.getFunctionByParameterDelimiter(internTokens, 8);
		assertEqualsTokenLists(functionTokens, internTokens);
	}

	@Test
	public void testgetFunctionByParameterDelimiterOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull(InternFormulaUtils.getFunctionByParameterDelimiter(null, 0));
		assertNull(InternFormulaUtils.getFunctionByParameterDelimiter(internTokens, 2));
		assertNull(InternFormulaUtils.getFunctionByParameterDelimiter(internTokens, 1));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull(InternFormulaUtils.getFunctionByParameterDelimiter(internTokens, 2));
	}

	@Test
	public void testgetFunctionByFunctionBracketOpenOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull(InternFormulaUtils.getFunctionByFunctionBracketOpen(null, 0));
		assertNull(InternFormulaUtils.getFunctionByFunctionBracketOpen(internTokens, 2));
		assertNull(InternFormulaUtils.getFunctionByFunctionBracketOpen(internTokens, 1));
	}

	@Test
	public void testgenerateTokenListByBracketOpenOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		assertNull(InternFormulaUtils.generateTokenListByBracketOpen(internTokens, 3));
		assertNull(InternFormulaUtils.generateTokenListByBracketOpen(internTokens, 0));
	}

	@Test
	public void testgenerateTokenListByBracketOpen() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		List<InternToken> functionTokens = InternFormulaUtils.generateTokenListByBracketOpen(internTokens, 0);
		assertEqualsTokenLists(functionTokens, internTokens);
	}

	@Test
	public void testgenerateTokenListByBracketCloseOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		assertNull(InternFormulaUtils.generateTokenListByBracketClose(internTokens, 3));
		assertNull(InternFormulaUtils.generateTokenListByBracketClose(internTokens, 0));
	}

	@Test
	public void testgenerateTokenListByBracketClose() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		List<InternToken> functionTokens = InternFormulaUtils.generateTokenListByBracketClose(internTokens, 8);
		assertEqualsTokenLists(functionTokens, internTokens);
	}

	@Test
	public void testgetFunctionParameterInternTokensAsListsOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull(InternFormulaUtils.getFunctionParameterInternTokensAsLists(null));
		assertNull(InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));

		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull(InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull(InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull(InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));
	}

	@Test
	public void testIsFunction() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertFalse(InternFormulaUtils.isFunction(internTokens));
	}

	@Test
	public void testgetFirstInternTokenTypeOnErrorInput() throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();

		Method method = InternFormulaUtils.class.getDeclaredMethod("getFirstInternTokenType", List.class);
		method.setAccessible(true);

		Object[] arguments = new Object[1];
		arguments[0] = null;
		assertNull(method.invoke(null, arguments));
		arguments[0] = internTokens;
		assertNull(method.invoke(null, arguments));
	}

	@Test
	public void testisPeriodTokenOnError() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertFalse(InternFormulaUtils.isPeriodToken(null));
		assertFalse(InternFormulaUtils.isPeriodToken(internTokens));
	}

	@Test
	public void testisFunctionTokenOnError() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertFalse(InternFormulaUtils.isFunctionToken(null));
		assertFalse(InternFormulaUtils.isFunctionToken(internTokens));
	}

	@Test
	public void testIsNumberOnError() {
		assertFalse(InternFormulaUtils.isNumberToken(null));
	}

	@Test
	public void testreplaceFunctionButKeepParametersOnError() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));

		assertNull(InternFormulaUtils.replaceFunctionButKeepParameters(null, null));

		assertEquals(internTokens, InternFormulaUtils.replaceFunctionButKeepParameters(internTokens, internTokens));
	}

	@Test
	public void testgetFunctionParameterCountOnError() throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Method method = InternFormulaUtils.class.getDeclaredMethod("getFunctionParameterCount", List.class);
		method.setAccessible(true);

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		Object[] params = new Object[1];
		params[0] = null;
		assertEquals(0, method.invoke(null, params));
		params[0] = internTokens;
		assertEquals(0, method.invoke(null, params));
		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		params[0] = internTokens;
		assertEquals(0, method.invoke(null, params));
		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		params[0] = internTokens;
		assertEquals(0, method.invoke(null, params));
		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		params[0] = internTokens;
		assertEquals(0, method.invoke(null, params));
	}

	@Test
	public void testDeleteNumberByOffset() {
		InternToken numberToken = new InternToken(InternTokenType.NUMBER, "1.1");
		assertEquals(numberToken, InternFormulaUtils.deleteNumberByOffset(numberToken, 0));
	}
}
