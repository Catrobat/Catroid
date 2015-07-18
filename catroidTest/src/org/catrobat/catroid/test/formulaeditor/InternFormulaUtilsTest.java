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

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaUtils;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InternFormulaUtilsTest extends InstrumentationTestCase {

	public void testGetFunctionByFunctionBracketCloseOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull("End function-bracket index is 0", InternFormulaUtils.getFunctionByFunctionBracketClose(null, 0));
		assertNull("End function-bracket index is InternTokenListSize",
				InternFormulaUtils.getFunctionByFunctionBracketClose(internTokens, 2));
		assertNull("No function name before brackets",
				InternFormulaUtils.getFunctionByFunctionBracketClose(internTokens, 1));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull("No function name before brackets",
				InternFormulaUtils.getFunctionByFunctionBracketClose(internTokens, 2));
	}

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
		assertEquals("GetFunctionByParameter wrong function returned", functionTokens.size(), internTokens.size());

		for (int index = 0; index < functionTokens.size(); index++) {
			assertTrue(
					"GetFunctionByParameter wrong function returned",
					functionTokens.get(index).getInternTokenType() == internTokens.get(index).getInternTokenType()
							&& functionTokens.get(index).getTokenStringValue()
							.compareTo(internTokens.get(index).getTokenStringValue()) == 0);
		}
	}

	public void testgetFunctionByParameterDelimiterOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull("Function delimiter index is 0", InternFormulaUtils.getFunctionByParameterDelimiter(null, 0));
		assertNull("End delimiter index is InternTokenListSize",
				InternFormulaUtils.getFunctionByParameterDelimiter(internTokens, 2));
		assertNull("No function name before brackets",
				InternFormulaUtils.getFunctionByParameterDelimiter(internTokens, 1));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull("No function name before brackets",
				InternFormulaUtils.getFunctionByParameterDelimiter(internTokens, 2));
	}

	public void testgetFunctionByFunctionBracketOpenOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		assertNull("Function bracket index is 0", InternFormulaUtils.getFunctionByFunctionBracketOpen(null, 0));
		assertNull("End delimiter index is InternTokenListSize",
				InternFormulaUtils.getFunctionByFunctionBracketOpen(internTokens, 2));
		assertNull("No function name before brackets",
				InternFormulaUtils.getFunctionByFunctionBracketOpen(internTokens, 1));
	}

	public void testgenerateTokenListByBracketOpenOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		assertNull("Index is >= list.size", InternFormulaUtils.generateTokenListByBracketOpen(internTokens, 3));
		assertNull("Index Token is not bracket open",
				InternFormulaUtils.generateTokenListByBracketOpen(internTokens, 0));
	}

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
		assertEquals("GetFunctionByParameter wrong function returned", functionTokens.size(), internTokens.size());

		for (int index = 0; index < functionTokens.size(); index++) {
			assertTrue(
					"GetFunctionByParameter wrong function returned",
					functionTokens.get(index).getInternTokenType() == internTokens.get(index).getInternTokenType()
							&& functionTokens.get(index).getTokenStringValue()
							.compareTo(internTokens.get(index).getTokenStringValue()) == 0);
		}
	}

	public void testgenerateTokenListByBracketCloseOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		assertNull("Index is >= list.size", InternFormulaUtils.generateTokenListByBracketClose(internTokens, 3));
		assertNull("Index Token is not bracket close",
				InternFormulaUtils.generateTokenListByBracketClose(internTokens, 0));
	}

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
		assertEquals("GetFunctionByParameter wrong function returned", functionTokens.size(), internTokens.size());

		for (int index = 0; index < functionTokens.size(); index++) {
			assertTrue(
					"GetFunctionByParameter wrong function returned",
					functionTokens.get(index).getInternTokenType() == internTokens.get(index).getInternTokenType()
							&& functionTokens.get(index).getTokenStringValue()
							.compareTo(internTokens.get(index).getTokenStringValue()) == 0);
		}
	}

	public void testgetFunctionParameterInternTokensAsListsOnErrorInput() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull("InternToken list is null", InternFormulaUtils.getFunctionParameterInternTokensAsLists(null));
		assertNull("InternToken list is too small",
				InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));

		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull("First token is not a FunctionName Token",
				InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull("Second token is not a Bracket Token",
				InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertNull("Function has no close bracket in list",
				InternFormulaUtils.getFunctionParameterInternTokensAsLists(internTokens));
	}

	public void testIsFunction() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertFalse("List contains more elements than just ONE function", InternFormulaUtils.isFunction(internTokens));
	}

	public void testgetFirstInternTokenTypeOnErrorInput() throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();

		Method method = InternFormulaUtils.class.getDeclaredMethod("getFirstInternTokenType", List.class);
		method.setAccessible(true);

		Object[] arguments = new Object[1];
		arguments[0] = null;
		assertNull("Token list is null", method.invoke(null, arguments));
		arguments[0] = internTokens;
		assertNull("Token list is null", method.invoke(null, arguments));
	}

	public void testisPeriodTokenOnError() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertFalse("Shoult return false, when parameter is null", InternFormulaUtils.isPeriodToken(null));
		assertFalse("List size not equal to 1", InternFormulaUtils.isPeriodToken(internTokens));
	}

	public void testisFunctionTokenOnError() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		assertFalse("Should return false on null", InternFormulaUtils.isFunctionToken(null));
		assertFalse("Should return false when List size < 1", InternFormulaUtils.isFunctionToken(internTokens));
	}

	public void testIsNumberOnError() {

		assertFalse("Should return false if parameter is null", InternFormulaUtils.isNumberToken(null));
	}

	public void testreplaceFunctionButKeepParametersOnError() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));

		assertNull("Should return null if functionToReplace is null",
				InternFormulaUtils.replaceFunctionButKeepParameters(null, null));

		assertEquals("Function without params whould return null", internTokens,
				InternFormulaUtils.replaceFunctionButKeepParameters(internTokens, internTokens));
	}

	public void testgetFunctionParameterCountOnError() throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Method method = InternFormulaUtils.class.getDeclaredMethod("getFunctionParameterCount", List.class);
		method.setAccessible(true);

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));

		Object[] params = new Object[1];
		params[0] = null;
		assertEquals("Should return 0 if List is null", 0, method.invoke(null, params));
		params[0] = internTokens;
		assertEquals("Should return 0 if List size < 4", 0, method.invoke(null, params));
		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		params[0] = internTokens;
		assertEquals("Should return 0 if first Token is not a function name token", 0, method.invoke(null, params));
		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		params[0] = internTokens;
		assertEquals("Should return 0 if second Token is not a function bracket open token", 0,
				method.invoke(null, params));
		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		internTokens.add(new InternToken(InternTokenType.NUMBER));
		params[0] = internTokens;
		assertEquals("Should return 0 if function list does not contain a bracket close token", 0,
				method.invoke(null, params));
	}

	public void testDeleteNumberByOffset() {
		InternToken numberToken = new InternToken(InternTokenType.NUMBER, "1.1");

		assertTrue("Wrong charakter deletd", InternFormulaUtils.deleteNumberByOffset(numberToken, 0) == numberToken);
	}
}
