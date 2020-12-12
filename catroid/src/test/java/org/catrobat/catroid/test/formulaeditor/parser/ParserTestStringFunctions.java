/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class ParserTestStringFunctions {

	private Sprite testSprite;
	private static final double USER_VARIABLE_1_VALUE_TYPE_DOUBLE = 888.88;
	private static final String USER_VARIABLE_2_VALUE_TYPE_STRING = "another String";
	private static final String PROJECT_USER_VARIABLE_NAME = "projectUserVariable";
	private static final String PROJECT_USER_VARIABLE_NAME2 = "projectUserVariable2";

	@Before
	public void setUp() {
		testSprite = new Sprite("testsprite");
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(testSprite);

		project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE_NAME, USER_VARIABLE_1_VALUE_TYPE_DOUBLE));
		project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE_NAME2, USER_VARIABLE_2_VALUE_TYPE_STRING));
	}

	@Test
	public void testLength() {
		String firstParameter = "testString";
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.STRING, firstParameter,
				(double) firstParameter.length(), testSprite);

		String number = "1.1";
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, number,
				(double) number.length(), testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, (double) Double.toString(USER_VARIABLE_1_VALUE_TYPE_DOUBLE).length(),
				testSprite);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME2, (double) USER_VARIABLE_2_VALUE_TYPE_STRING.length(), testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, firstParameterList, 0d, testSprite);
	}

	@Test
	public void testLetter() {
		String letterString = "letterString";
		String index = "7";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, String.valueOf(letterString.charAt(Integer.valueOf(index) - 1)),
				testSprite);

		index = "0";
		String emptyString = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, emptyString, testSprite);

		index = "-5";
		emptyString = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, emptyString, testSprite);

		index = "0";
		emptyString = "";
		letterString = emptyString;
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, String.valueOf(index),
				InternTokenType.STRING, letterString, emptyString, testSprite);

		letterString = "letterString";
		index = "2";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.STRING,
				String.valueOf(letterString.charAt(Integer.valueOf(index) - 1)), InternTokenType.STRING, letterString,
				emptyString, testSprite);

		index = "4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME,
				String.valueOf(Double.toString(USER_VARIABLE_1_VALUE_TYPE_DOUBLE).charAt(Integer.valueOf(index) - 1)),
				testSprite);

		index = "3";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				String.valueOf(USER_VARIABLE_2_VALUE_TYPE_STRING.charAt(Integer.valueOf(index) - 1)), testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, firstParameterList, secondParameterList, "",
				testSprite);
	}

	@Test
	public void testJoin() {
		String firstParameter = "first";
		String secondParameter = "second";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "";
		secondParameter = "second";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "first";
		secondParameter = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "55";
		secondParameter = "66";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.NUMBER, firstParameter,
				InternTokenType.NUMBER, secondParameter, firstParameter + secondParameter, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.NUMBER, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.NUMBER, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%%/&%(())??";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2, firstParameter
						+ USER_VARIABLE_2_VALUE_TYPE_STRING, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				USER_VARIABLE_1_VALUE_TYPE_DOUBLE + USER_VARIABLE_2_VALUE_TYPE_STRING, testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.STRING, secondParameter, USER_VARIABLE_1_VALUE_TYPE_DOUBLE
						+ secondParameter, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, firstParameterList, secondParameterList, ""
				+ Double.NaN + Double.NaN, testSprite);
	}

	@Test
	public void testJoin3() {
		String firstParameter = "first";
		String secondParameter = "second";
		String thirdParameter = "third";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.STRING, secondParameter,
				InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testSprite);

		firstParameter = "";
		secondParameter = "second";
		thirdParameter = "third";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.STRING, secondParameter,
				InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testSprite);

		firstParameter = "first";
		secondParameter = "";
		thirdParameter = "third";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.STRING, secondParameter,
				InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testSprite);

		firstParameter = "first";
		secondParameter = "";
		thirdParameter = "";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.STRING, secondParameter,
				InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testSprite);

		firstParameter = "33";
		secondParameter = "44";
		thirdParameter = "55";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.NUMBER,
				firstParameter,
				InternTokenType.NUMBER, secondParameter,
				InternTokenType.NUMBER, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testSprite);

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%\"%/&%(())??";
		thirdParameter = "blubb";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testSprite);
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2, InternTokenType.STRING,
				thirdParameter,
				firstParameter + USER_VARIABLE_2_VALUE_TYPE_STRING + thirdParameter, testSprite);
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3,
				InternTokenType.STRING, firstParameter, InternTokenType.STRING, secondParameter, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME,
				firstParameter + secondParameter + USER_VARIABLE_1_VALUE_TYPE_DOUBLE, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList =
				FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "7",
						Operators.MULT,
						InternTokenType.STRING, "anotherString");
		List<InternToken> thirdParameterList =
				FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "9", Operators.MULT,
						InternTokenType.STRING, "thisString");
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, firstParameterList,
				secondParameterList, thirdParameterList, ""
						+ Double.NaN + Double.NaN + Double.NaN, testSprite);
	}

	@Test
	public void testRegex() {
		String firstParameter = " an? ([^ .]+)";
		String secondParameter = "I am a penguin.";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "penguin", testSprite);

		firstParameter = "";
		secondParameter = "second";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "", testSprite);

		firstParameter = "first";
		secondParameter = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "", testSprite);

		firstParameter = "345";
		secondParameter = "123456";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.NUMBER, firstParameter,
				InternTokenType.NUMBER, secondParameter, "345", testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.NUMBER, firstParameter,
				InternTokenType.STRING, secondParameter, "345", testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.NUMBER, secondParameter, "345", testSprite);

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%%/&%(())??";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "", testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2, "", testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				"", testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.STRING, secondParameter, "", testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME,
				"888.88", testSprite);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME2, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				USER_VARIABLE_2_VALUE_TYPE_STRING, testSprite);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, firstParameterList, secondParameterList,
				"" + Double.NaN, testSprite);
	}

	@Test
	public void testStringFunctionsNested() {
		String firstParameter = "hello";
		String secondParameter = " world";
		List<InternToken> joinTokenList = FormulaEditorTestUtil.buildDoubleParameterFunction(Functions.JOIN,
				InternTokenType.STRING, firstParameter, InternTokenType.STRING, secondParameter);
		List<InternToken> lengthTokenList = FormulaEditorTestUtil.buildSingleParameterFunction(Functions.LENGTH,
				joinTokenList);
		List<InternToken> secondInternTokenList = new LinkedList<InternToken>();
		secondInternTokenList.add(new InternToken(InternTokenType.STRING, firstParameter + secondParameter));
		List<InternToken> letterTokenList = FormulaEditorTestUtil.buildDoubleParameterFunction(Functions.LETTER,
				lengthTokenList, secondInternTokenList);
		FormulaElement parseTree = new InternFormulaParser(letterTokenList).parseFormula();

		assertNotNull(parseTree);
		assertEquals(String.valueOf((firstParameter + secondParameter)
						.charAt((firstParameter + secondParameter).length() - 1)),
				parseTree.interpretRecursive(testSprite));
	}
}
