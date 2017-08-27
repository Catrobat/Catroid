/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.test.formula;

import org.catrobat.catroid.formula.Formula;
import org.catrobat.catroid.formula.FormulaInterpreter;
import org.catrobat.catroid.formula.Token;
import org.catrobat.catroid.formula.dataprovider.DataProvider;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.AddOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.MultOperatorToken;
import org.catrobat.catroid.formula.value.ValueToken;
import org.catrobat.catroid.formula.value.ValueToken.VariableToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class UserVariableTest {

	private FormulaInterpreter interpreter = new FormulaInterpreter();

	@Test
	public void testSimpleVariable() {
		List<Token> internalTokens = new ArrayList<>();

		internalTokens.add(new ValueToken(2));
		internalTokens.add(new MultOperatorToken());
		internalTokens.add(new ValueToken(5));

		Formula internalFormula = new Formula(internalTokens);

		VariableToken variable = new VariableToken("A", 0.0);

		DataProvider dataProvider = new DataProvider();
		dataProvider.add(variable, internalFormula);

		dataProvider.updateValues();

		assertEquals(2 * 5.0, interpreter.eval(new ArrayList<Token>(Arrays.asList(variable))).getValue());
		assertEquals("A", variable.getString());
	}

	@Test
	public void testUpdateVariable() {
		List<Token> internalTokens = new ArrayList<>();

		internalTokens.add(new ValueToken(2));
		internalTokens.add(new MultOperatorToken());
		internalTokens.add(new ValueToken(5));

		Formula internalFormula = new Formula(internalTokens);

		VariableToken variable = new VariableToken("A", 0.0);

		DataProvider dataProvider = new DataProvider();
		dataProvider.add(variable, internalFormula);

		dataProvider.updateValues();
		assertEquals(2 * 5.0, interpreter.eval(new ArrayList<Token>(Arrays.asList(variable))).getValue());
		assertEquals("A", variable.getString());

		internalFormula.getTokens().add(new AddOperatorToken());
		internalFormula.getTokens().add(new ValueToken(3));

		dataProvider.updateValues();
		assertEquals(2 * 5 + 3.0, interpreter.eval(new ArrayList<Token>(Arrays.asList(variable))).getValue());
		assertEquals("A", variable.getString());

		variable.setName("B");

		dataProvider.updateValues();
		assertEquals("B", variable.getString());
	}

	@Test
	public void testInvalidReference() {
		List<Token> internalTokens = new ArrayList<>();

		internalTokens.add(new ValueToken(2));
		internalTokens.add(new MultOperatorToken());
		internalTokens.add(new ValueToken(5));

		Formula internalFormula = new Formula(internalTokens);

		VariableToken variable = new VariableToken("A", 0.0);

		DataProvider dataProvider = new DataProvider();
		dataProvider.add(variable, internalFormula);

		dataProvider.updateValues();
		assertEquals(2 * 5.0, interpreter.eval(new ArrayList<Token>(Arrays.asList(variable))).getValue());
		assertEquals("A", variable.getString());

		dataProvider.remove(variable);

		assertEquals("INVALID REFERENCE: A", variable.getString());
	}
}
