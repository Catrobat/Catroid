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

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaUtils;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;

import android.test.InstrumentationTestCase;

public class InternFormulaUtilsTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

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
							&& functionTokens.get(index).getTokenSringValue()
									.compareTo(internTokens.get(index).getTokenSringValue()) == 0);

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
							&& functionTokens.get(index).getTokenSringValue()
									.compareTo(internTokens.get(index).getTokenSringValue()) == 0);

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
							&& functionTokens.get(index).getTokenSringValue()
									.compareTo(internTokens.get(index).getTokenSringValue()) == 0);

		}

	}
}
