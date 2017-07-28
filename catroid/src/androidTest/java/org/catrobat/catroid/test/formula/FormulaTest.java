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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.formula.Formula;
import org.catrobat.catroid.formula.FormulaInterpreter;
import org.catrobat.catroid.formula.FunctionToken.Abs;
import org.catrobat.catroid.formula.FunctionToken.Acos;
import org.catrobat.catroid.formula.FunctionToken.Asin;
import org.catrobat.catroid.formula.FunctionToken.Atan;
import org.catrobat.catroid.formula.FunctionToken.Ceil;
import org.catrobat.catroid.formula.FunctionToken.Cos;
import org.catrobat.catroid.formula.FunctionToken.Exp;
import org.catrobat.catroid.formula.FunctionToken.Floor;
import org.catrobat.catroid.formula.FunctionToken.Lg;
import org.catrobat.catroid.formula.FunctionToken.Ln;
import org.catrobat.catroid.formula.FunctionToken.Round;
import org.catrobat.catroid.formula.FunctionToken.Sin;
import org.catrobat.catroid.formula.FunctionToken.Sqrt;
import org.catrobat.catroid.formula.FunctionToken.Tan;
import org.catrobat.catroid.formula.MathOperatorToken.AddOperatorToken;
import org.catrobat.catroid.formula.MathOperatorToken.DivOperatorToken;
import org.catrobat.catroid.formula.MathOperatorToken.MultOperatorToken;
import org.catrobat.catroid.formula.MathOperatorToken.SubOperatorToken;
import org.catrobat.catroid.formula.NumericValueToken;
import org.catrobat.catroid.formula.OperatorToken.BracketOperator;
import org.catrobat.catroid.formula.Token;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FormulaTest {

	private NumericValueToken a = new NumericValueToken(100);
	private NumericValueToken b = new NumericValueToken(5);
	private NumericValueToken c = new NumericValueToken(0.1);
	private NumericValueToken d = new NumericValueToken(-2);
	private NumericValueToken e = new NumericValueToken(0.8);

	private MultOperatorToken mult = new MultOperatorToken();
	private DivOperatorToken div = new DivOperatorToken();
	private AddOperatorToken add = new AddOperatorToken();
	private SubOperatorToken sub = new SubOperatorToken();

	private BracketOperator leftBracket = new BracketOperator(Token.Type.LEFT_BRACKET);
	private BracketOperator rightBracket = new BracketOperator(Token.Type.RIGHT_BRACKET);

	private List<Token> tokens = new ArrayList<>();
	private Formula formula;
	private double expectedResult;
	private String expectedString;

	private FormulaInterpreter interpreter = new FormulaInterpreter();

	@Test
	public void testBasicMathFunctions() {

		tokens.clear();
		tokens.add(a);
		tokens.add(add);
		tokens.add(b);
		tokens.add(mult);
		tokens.add(c);

		formula = new Formula(tokens);
		expectedResult = 100 + 5 * 0.1;
		expectedString = "100.0 + 5.0 * 0.1";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();
		tokens.add(leftBracket);
		tokens.add(a);
		tokens.add(add);
		tokens.add(b);
		tokens.add(rightBracket);
		tokens.add(mult);
		tokens.add(c);

		formula = new Formula(tokens);
		expectedResult = (100 + 5) * 0.1;
		expectedString = "( 100.0 + 5.0 ) * 0.1";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();
		tokens.add(d);
		tokens.add(mult);
		tokens.add(leftBracket);
		tokens.add(a);
		tokens.add(sub);
		tokens.add(b);
		tokens.add(rightBracket);
		tokens.add(div);
		tokens.add(c);

		formula = new Formula(tokens);
		expectedResult = -2 * (100 - 5) / 0.1;
		expectedString = "-2.0 * ( 100.0 - 5.0 ) / 0.1";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());
	}

	@Test
	public void testUnaryFunctions() {

		List<Token> internTokens = new ArrayList<>();

		tokens.clear();
		internTokens.add(a);
		internTokens.add(add);
		internTokens.add(b);
		internTokens.add(mult);
		internTokens.add(c);

		Sin sin = new Sin(internTokens);

		tokens.add(sin);
		formula = new Formula(tokens);
		expectedResult = Math.sin(100 + 5 * 0.1);
		expectedString = "sin( 100.0 + 5.0 * 0.1 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();
		internTokens.clear();
		tokens.add(b);
		tokens.add(add);
		internTokens.add(a);
		internTokens.add(div);
		internTokens.add(b);

		Cos cos = new Cos(internTokens);

		tokens.add(cos);
		formula = new Formula(tokens);
		expectedResult = 5 + Math.cos(100 / 5);
		expectedString = "5.0 + cos( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Tan tan = new Tan(internTokens);

		tokens.add(tan);
		formula = new Formula(tokens);
		expectedResult = Math.tan(100 / 5);
		expectedString = "tan( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Ln ln = new Ln(internTokens);

		tokens.add(ln);
		formula = new Formula(tokens);
		expectedResult = Math.log(100 / 5);
		expectedString = "ln( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Lg lg = new Lg(internTokens);

		tokens.add(lg);
		formula = new Formula(tokens);
		expectedResult = Math.log10(100 / 5);
		expectedString = "log( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Sqrt sqrt = new Sqrt(internTokens);

		tokens.add(sqrt);
		formula = new Formula(tokens);
		expectedResult = Math.sqrt(100 / 5);
		expectedString = "sqrt( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();
		internTokens.clear();
		internTokens.add(d);

		Abs abs = new Abs(internTokens);

		tokens.add(abs);
		formula = new Formula(tokens);
		expectedResult = Math.abs(-2);
		expectedString = "abs( -2.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();
		internTokens.clear();
		tokens.add(b);
		tokens.add(add);
		internTokens.add(a);
		internTokens.add(div);
		internTokens.add(b);

		Exp exp = new Exp(internTokens);

		tokens.add(exp);
		formula = new Formula(tokens);
		expectedResult = 5 + Math.exp(100 / 5);
		expectedString = "5.0 + exp( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Asin asin = new Asin(internTokens);

		tokens.add(asin);
		formula = new Formula(tokens);
		expectedResult = Math.asin(100 / 5);
		expectedString = "arcsin( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Acos acos = new Acos(internTokens);

		tokens.add(acos);
		formula = new Formula(tokens);
		expectedResult = Math.acos(100 / 5);
		expectedString = "arccos( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Atan atan = new Atan(internTokens);

		tokens.add(atan);
		formula = new Formula(tokens);
		expectedResult = Math.atan(100 / 5);
		expectedString = "arctan( 100.0 / 5.0 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();
		internTokens.clear();
		internTokens.add(c);

		Floor floor = new Floor(internTokens);

		tokens.add(floor);
		formula = new Formula(tokens);
		expectedResult = 0;
		expectedString = "floor( 0.1 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Ceil ceil = new Ceil(internTokens);

		tokens.add(ceil);
		formula = new Formula(tokens);
		expectedResult = 1;
		expectedString = "ceil( 0.1 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();

		Round roundDown = new Round(internTokens);

		tokens.add(roundDown);
		formula = new Formula(tokens);
		expectedResult = 0;
		expectedString = "round( 0.1 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());

		tokens.clear();
		internTokens.clear();
		internTokens.add(e);

		Round roundUp = new Round(internTokens);

		tokens.add(roundUp);
		formula = new Formula(tokens);
		expectedResult = 1;
		expectedString = "round( 0.8 )";
		assertEquals(expectedResult, interpreter.eval(tokens));
		assertEquals(expectedString, formula.getDisplayText());
	}
}
