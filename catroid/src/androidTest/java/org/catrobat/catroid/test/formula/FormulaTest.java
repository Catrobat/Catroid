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

import junit.framework.Assert;

import org.catrobat.catroid.formula.Formula;
import org.catrobat.catroid.formula.FormulaInterpreter;
import org.catrobat.catroid.formula.Token;
import org.catrobat.catroid.formula.function.FunctionToken;
import org.catrobat.catroid.formula.function.BinaryFunctionToken.Max;
import org.catrobat.catroid.formula.function.BinaryFunctionToken.Min;
import org.catrobat.catroid.formula.function.BinaryFunctionToken.Pow;
import org.catrobat.catroid.formula.function.BinaryFunctionToken.Mod;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Abs;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Acos;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Asin;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Atan;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Ceil;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Cos;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Exp;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Floor;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Lg;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Ln;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Round;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Sin;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Sqrt;
import org.catrobat.catroid.formula.function.UnaryFunctionToken.Tan;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.AddOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.AndOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.DivOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.EqualsOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.GreaterEqualsOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.GreaterOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.MultOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.NotEqualsOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.OrOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.SmallerEqualsOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.SmallerOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.SubOperatorToken;
import org.catrobat.catroid.formula.operator.OperatorToken.BracketOperator;
import org.catrobat.catroid.formula.operator.UnaryOperatorToken.NotOperatorToken;
import org.catrobat.catroid.formula.value.ValueToken;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FormulaTest {

	private ValueToken varA = new ValueToken(100);
	private ValueToken varB = new ValueToken(5);
	private ValueToken varC = new ValueToken(0.1);
	private ValueToken varD = new ValueToken(-2);
	private ValueToken varE = new ValueToken(0.8);
	private ValueToken var0 = new ValueToken(0);

	private MultOperatorToken mult = new MultOperatorToken();
	private DivOperatorToken div = new DivOperatorToken();
	private AddOperatorToken add = new AddOperatorToken();
	private SubOperatorToken sub = new SubOperatorToken();

	private AndOperatorToken and = new AndOperatorToken();
	private OrOperatorToken or = new OrOperatorToken();
	private NotOperatorToken not = new NotOperatorToken();

	private GreaterOperatorToken greater = new GreaterOperatorToken();
	private GreaterEqualsOperatorToken greaterEquals = new GreaterEqualsOperatorToken();
	private SmallerOperatorToken smaller = new SmallerOperatorToken();
	private SmallerEqualsOperatorToken smallerEquals = new SmallerEqualsOperatorToken();
	private EqualsOperatorToken equals = new EqualsOperatorToken();
	private NotEqualsOperatorToken notEquals = new NotEqualsOperatorToken();

	private ValueToken valTrue = new ValueToken(1);
	private ValueToken valFalse = new ValueToken(0);

	private BracketOperator leftBracket = new BracketOperator(Token.Type.LEFT_BRACKET);
	private BracketOperator rightBracket = new BracketOperator(Token.Type.RIGHT_BRACKET);

	private void testFormula(Formula formula, double expectedResult, String expectedString) {
		FormulaInterpreter interpreter = new FormulaInterpreter();
		assertEquals(expectedString, formula.getDisplayText());
		assertEquals(expectedResult, interpreter.eval(formula.getTokens()).getValue());
	}

	@Test
	public void testAdd() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(add);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, varB.getValue() + varE.getValue(), "5.0 + 0.8");
	}

	@Test
	public void testSub() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(sub);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, varB.getValue() - varE.getValue(), "5.0 - 0.8");
	}

	@Test
	public void testMult() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(mult);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, varB.getValue() * varE.getValue(), "5.0 * 0.8");
	}

	@Test
	public void testDiv() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(div);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, varB.getValue() / varE.getValue(), "5.0 / 0.8");

		tokens.clear();
		tokens.add(varB);
		tokens.add(div);
		tokens.add(var0);

		formula = new Formula(tokens);

		try {
			testFormula(formula, varB.getValue() / var0.getValue(), "5.0 / 0.0");
			// Division by 0 should NEVER work!
			Assert.fail();
		} catch (Exception e) {
			assertEquals("DIVIDED BY 0", e.getMessage());
		}
	}

	@Test
	public void testMathOperatorPrecedence() {
		List<Token> tokens = new ArrayList<>();
		Formula formula;

		tokens.add(varA);
		tokens.add(add);
		tokens.add(varB);
		tokens.add(mult);
		tokens.add(varC);

		formula = new Formula(tokens);
		testFormula(formula, varA.getValue() + varB.getValue() * varC.getValue(), "100.0 + 5.0 * 0.1");
	}

	@Test
	public void testBracketsInMathFormula() {
		List<Token> tokens = new ArrayList<>();
		Formula formula;

		tokens.clear();
		tokens.add(leftBracket);
		tokens.add(varA);
		tokens.add(add);
		tokens.add(varB);
		tokens.add(rightBracket);
		tokens.add(mult);
		tokens.add(varC);

		formula = new Formula(tokens);
		testFormula(formula, (varA.getValue() + varB.getValue()) * varC.getValue(), "( 100.0 + 5.0 ) * 0.1");

		tokens.clear();
		tokens.add(varD);
		tokens.add(mult);
		tokens.add(leftBracket);
		tokens.add(varA);
		tokens.add(sub);
		tokens.add(varB);
		tokens.add(rightBracket);
		tokens.add(div);
		tokens.add(varC);

		formula = new Formula(tokens);
		testFormula(formula, varD.getValue() * (varA.getValue() - varB.getValue()) / varC.getValue(),
				"-2.0 * ( 100.0 - 5.0 ) / 0.1");
	}

	private void testFunction(FunctionToken functionToken, double expectedResult, String expectedString) {

		List<Token> tokens = new ArrayList<>();
		tokens.add(functionToken);
		Formula formula = new Formula(tokens);

		testFormula(formula, expectedResult, expectedString);
	}

	@Test
	public void testSin() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Sin func = new Sin(internalTokens);
		testFunction(func, Math.sin(varA.getValue() / varB.getValue()), "sin( 100.0 / 5.0 )");
	}

	@Test
	public void testCos() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Cos func = new Cos(internalTokens);
		testFunction(func, Math.cos(varA.getValue() / varB.getValue()), "cos( 100.0 / 5.0 )");
	}

	@Test
	public void testTan() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Tan func = new Tan(internalTokens);
		testFunction(func, Math.tan(varA.getValue() / varB.getValue()), "tan( 100.0 / 5.0 )");
	}

	@Test
	public void testLn() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Ln func = new Ln(internalTokens);
		testFunction(func, Math.log(varA.getValue() / varB.getValue()), "ln( 100.0 / 5.0 )");
	}

	@Test
	public void testLog() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Lg func = new Lg(internalTokens);
		testFunction(func, Math.log10(varA.getValue() / varB.getValue()), "log( 100.0 / 5.0 )");
	}

	@Test
	public void testSqrt() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Sqrt func = new Sqrt(internalTokens);
		testFunction(func, Math.sqrt(varA.getValue() / varB.getValue()), "sqrt( 100.0 / 5.0 )");
	}

	@Test
	public void testAbs() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varD);
		internalTokens.add(mult);
		internalTokens.add(varB);

		Abs func = new Abs(internalTokens);
		testFunction(func, Math.abs(varD.getValue() * varB.getValue()), "abs( -2.0 * 5.0 )");
	}

	@Test
	public void testAsin() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Asin func = new Asin(internalTokens);
		testFunction(func, Math.asin(varA.getValue() / varB.getValue()), "arcsin( 100.0 / 5.0 )");
	}

	@Test
	public void testAcos() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Acos func = new Acos(internalTokens);
		testFunction(func, Math.acos(varA.getValue() / varB.getValue()), "arccos( 100.0 / 5.0 )");
	}

	@Test
	public void testAtan() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Atan func = new Atan(internalTokens);
		testFunction(func, Math.atan(varA.getValue() / varB.getValue()), "arctan( 100.0 / 5.0 )");
	}

	@Test
	public void testExp() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varA);
		internalTokens.add(div);
		internalTokens.add(varB);

		Exp func = new Exp(internalTokens);
		testFunction(func, Math.exp(varA.getValue() / varB.getValue()), "exp( 100.0 / 5.0 )");
	}

	@Test
	public void testFloor() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varC);

		Floor func = new Floor(internalTokens);
		testFunction(func, Math.floor(varC.getValue()), "floor( 0.1 )");
	}

	@Test
	public void testCeil() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varC);

		Ceil func = new Ceil(internalTokens);
		testFunction(func, Math.ceil(varC.getValue()), "ceil( 0.1 )");
	}

	@Test
	public void testRoundUp() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varE);

		Round func = new Round(internalTokens);
		testFunction(func, Math.round(varE.getValue()), "round( 0.8 )");
	}

	@Test
	public void testRoundDown() {
		List<Token> internalTokens = new ArrayList<>();
		internalTokens.add(varC);

		Round func = new Round(internalTokens);
		testFunction(func, Math.round(varC.getValue()), "round( 0.1 )");
	}

	@Test
	public void testGreater() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(greater);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(varB.getValue() > varE.getValue()), "5.0 > 0.8");
	}

	@Test
	public void testGreaterEquals() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(greaterEquals);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(varB.getValue() >= varE.getValue()), "5.0 >= 0.8");
	}

	@Test
	public void testSmaller() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(smaller);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(varB.getValue() < varE.getValue()), "5.0 < 0.8");
	}

	@Test
	public void testSmallerEquals() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(smallerEquals);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(varB.getValue() <= varE.getValue()), "5.0 <= 0.8");
	}

	@Test
	public void testEquals() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(equals);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(varB.getValue() == varE.getValue()), "5.0 = 0.8");
	}

	@Test
	public void testNotEquals() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(varB);
		tokens.add(notEquals);
		tokens.add(varE);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(varB.getValue() != varE.getValue()), "5.0 != 0.8");
	}

	@Test
	public void testAnd() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(valTrue);
		tokens.add(and);
		tokens.add(valFalse);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(false), "1.0 AND 0.0");
	}

	@Test
	public void testOr() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(valTrue);
		tokens.add(or);
		tokens.add(valFalse);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(true), "1.0 OR 0.0");
	}

	@Test
	public void testNot() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(not);
		tokens.add(valFalse);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(true), "NOT 0.0");
	}

	@Test
	public void testBooleanOperatorPrecedence() {
		List<Token> tokens = new ArrayList<>();

		tokens.add(not);
		tokens.add(valFalse);
		tokens.add(and);
		tokens.add(valTrue);
		tokens.add(or);
		tokens.add(valFalse);

		Formula formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(true), "NOT 0.0 AND 1.0 OR 0.0");
	}

	@Test
	public void testBracketsInBooleanFormula() {
		List<Token> tokens = new ArrayList<>();
		Formula formula;

		tokens.add(not);
		tokens.add(leftBracket);
		tokens.add(valFalse);
		tokens.add(and);
		tokens.add(valTrue);
		tokens.add(rightBracket);

		formula = new Formula(tokens);
		testFormula(formula, 1, "NOT ( 0.0 AND 1.0 )");

		tokens.clear();
		tokens.add(leftBracket);
		tokens.add(valFalse);
		tokens.add(or);
		tokens.add(valTrue);
		tokens.add(rightBracket);
		tokens.add(and);
		tokens.add(valTrue);

		formula = new Formula(tokens);
		testFormula(formula, FormulaInterpreter.Companion.eval(true), "( 0.0 OR 1.0 ) AND 1.0");
	}

	@Test
	public void testMax() {
		List<Token> leftTokens = new ArrayList<>();
		leftTokens.add(varC);

		List<Token> rightTokens = new ArrayList<>();
		rightTokens.add(varA);

		Max func = new Max(leftTokens, rightTokens);
		testFunction(func, Math.max(varC.getValue(), varA.getValue()), "max( 0.1 , 100.0 )");
	}

	@Test
	public void testMin() {
		List<Token> leftTokens = new ArrayList<>();
		leftTokens.add(varC);

		List<Token> rightTokens = new ArrayList<>();
		rightTokens.add(varA);

		Min func = new Min(leftTokens, rightTokens);
		testFunction(func, Math.min(varC.getValue(), varA.getValue()), "min( 0.1 , 100.0 )");
	}

	@Test
	public void testPow() {
		List<Token> leftTokens = new ArrayList<>();
		leftTokens.add(varC);

		List<Token> rightTokens = new ArrayList<>();
		rightTokens.add(varA);

		Pow func = new Pow(leftTokens, rightTokens);
		testFunction(func, Math.pow(varC.getValue(), varA.getValue()), "power( 0.1 , 100.0 )");
	}

	@Test
	public void testMod() {
		List<Token> leftTokens = new ArrayList<>();
		leftTokens.add(varA);

		List<Token> rightTokens = new ArrayList<>();
		rightTokens.add(varC);

		Mod func = new Mod(leftTokens, rightTokens);
		testFunction(func, varA.getValue() % varC.getValue(), "mod( 100.0 , 0.1 )");
	}
}
