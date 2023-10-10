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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
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

	private Scope testScope;
	private static final double USER_VARIABLE_1_VALUE_TYPE_DOUBLE = 888.88;
	private static final String USER_VARIABLE_2_VALUE_TYPE_STRING = "another String";
	private static final String PROJECT_USER_VARIABLE_NAME = "projectUserVariable";
	private static final String PROJECT_USER_VARIABLE_NAME2 = "projectUserVariable2";

	@Before
	public void setUp() {
		Sprite testSprite = new Sprite("testsprite");
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(testSprite);

		project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE_NAME, USER_VARIABLE_1_VALUE_TYPE_DOUBLE));
		project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE_NAME2, USER_VARIABLE_2_VALUE_TYPE_STRING));

		testScope = new Scope(project, testSprite, new SequenceAction());
	}

	@Test
	public void testLength() {
		String firstParameter = "testString";
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.STRING, firstParameter,
				(double) firstParameter.length(), testScope);

		String number = "1.1";
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, number,
				(double) number.length(), testScope);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, (double) Double.toString(USER_VARIABLE_1_VALUE_TYPE_DOUBLE).length(),
				testScope);

		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME2, (double) USER_VARIABLE_2_VALUE_TYPE_STRING.length(), testScope);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		FormulaEditorTestUtil.testSingleParameterFunction(Functions.LENGTH, firstParameterList, 0d, testScope);
	}

	@Test
	public void testLetter() {
		String letterString = "letterString";
		String index = "7";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, String.valueOf(letterString.charAt(Integer.valueOf(index) - 1)),
				testScope);

		index = "0";
		String emptyString = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, emptyString, testScope);

		index = "-5";
		emptyString = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, emptyString, testScope);

		index = "0";
		emptyString = "";
		letterString = emptyString;
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, String.valueOf(index),
				InternTokenType.STRING, letterString, emptyString, testScope);

		letterString = "letterString";
		index = "2";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.STRING,
				String.valueOf(letterString.charAt(Integer.valueOf(index) - 1)), InternTokenType.STRING, letterString,
				emptyString, testScope);

		index = "4";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME,
				String.valueOf(Double.toString(USER_VARIABLE_1_VALUE_TYPE_DOUBLE).charAt(Integer.valueOf(index) - 1)),
				testScope);

		index = "3";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				String.valueOf(USER_VARIABLE_2_VALUE_TYPE_STRING.charAt(Integer.valueOf(index) - 1)), testScope);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.LETTER, firstParameterList, secondParameterList, "",
				testScope);
	}

	@Test
	public void testSubtext() {
		String start = "3";
		String end = "5";
		String letterString = "hello world";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT,
				InternTokenType.NUMBER, start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				letterString.substring(3 - 1, 5), testScope);

		letterString = "Should work";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT, InternTokenType.NUMBER,
				start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				letterString.substring(3 - 1, 5), testScope);

		start = "3";
		end = "2";
		letterString = "Should not work";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT,
				InternTokenType.NUMBER, start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				"", testScope);

		start = "3";
		end = "1000";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT,
				InternTokenType.NUMBER, start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				"", testScope);

		start = "1";
		end = "16";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT,
				InternTokenType.NUMBER, start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				"", testScope);

		start = "1";
		end = "15";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT,
				InternTokenType.NUMBER, start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				letterString, testScope);

		start = "1";
		letterString = "AVeryLongWordWithNoSpaces";
		end = "25";

		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT,
				InternTokenType.NUMBER, start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				letterString, testScope);

		start = "1";
		letterString = "A very long sentence with multiple spaces";
		end = "41";

		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT,
				InternTokenType.NUMBER, start, InternTokenType.NUMBER, end,
				InternTokenType.STRING, letterString,
				letterString, testScope);

		List<InternToken> firstParameterList =
				FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "3",
						Operators.PLUS, InternTokenType.STRING, "This");
		List<InternToken> secondParameterList =
				FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5",
						Operators.PLUS, InternTokenType.STRING, "Shouldnt");
		List<InternToken> thirdParameterList =
				FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "0",
						Operators.PLUS, InternTokenType.STRING, "Work");
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.SUBTEXT, firstParameterList,
				secondParameterList, thirdParameterList,
				"", testScope);
	}

	@Test
	public void testJoin() {
		String firstParameter = "first";
		String secondParameter = "second";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testScope);

		firstParameter = "";
		secondParameter = "second";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testScope);

		firstParameter = "first";
		secondParameter = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testScope);

		firstParameter = "55";
		secondParameter = "66";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.NUMBER, firstParameter,
				InternTokenType.NUMBER, secondParameter, firstParameter + secondParameter, testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.NUMBER, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.NUMBER, secondParameter, firstParameter + secondParameter, testScope);

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%%/&%(())??";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2, firstParameter
						+ USER_VARIABLE_2_VALUE_TYPE_STRING, testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				USER_VARIABLE_1_VALUE_TYPE_DOUBLE + USER_VARIABLE_2_VALUE_TYPE_STRING, testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.STRING, secondParameter, USER_VARIABLE_1_VALUE_TYPE_DOUBLE
						+ secondParameter, testScope);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.JOIN, firstParameterList, secondParameterList, ""
				+ Double.NaN + Double.NaN, testScope);
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
				testScope);

		firstParameter = "";
		secondParameter = "second";
		thirdParameter = "third";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.STRING, secondParameter,
				InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testScope);

		firstParameter = "first";
		secondParameter = "";
		thirdParameter = "third";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.STRING, secondParameter,
				InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testScope);

		firstParameter = "first";
		secondParameter = "";
		thirdParameter = "";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.STRING, secondParameter,
				InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testScope);

		firstParameter = "33";
		secondParameter = "44";
		thirdParameter = "55";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.NUMBER,
				firstParameter,
				InternTokenType.NUMBER, secondParameter,
				InternTokenType.NUMBER, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testScope);

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%\"%/&%(())??";
		thirdParameter = "blubb";
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, InternTokenType.STRING, thirdParameter,
				firstParameter + secondParameter + thirdParameter,
				testScope);
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3, InternTokenType.STRING,
				firstParameter,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2, InternTokenType.STRING,
				thirdParameter,
				firstParameter + USER_VARIABLE_2_VALUE_TYPE_STRING + thirdParameter, testScope);
		FormulaEditorTestUtil.testTripleParameterFunction(Functions.JOIN3,
				InternTokenType.STRING, firstParameter, InternTokenType.STRING, secondParameter, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME,
				firstParameter + secondParameter + USER_VARIABLE_1_VALUE_TYPE_DOUBLE, testScope);

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
						+ Double.NaN + Double.NaN + Double.NaN, testScope);
	}

	@Test
	public void testRegex() {
		String firstParameter = " an? ([^ .]+)";
		String secondParameter = "I am a penguin.";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "penguin", testScope);

		firstParameter = "";
		secondParameter = "second";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "", testScope);

		firstParameter = "first";
		secondParameter = "";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "", testScope);

		firstParameter = "345";
		secondParameter = "123456";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.NUMBER, firstParameter,
				InternTokenType.NUMBER, secondParameter, "345", testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.NUMBER, firstParameter,
				InternTokenType.STRING, secondParameter, "345", testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.NUMBER, secondParameter, "345", testScope);

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%%/&%(())??";
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, "", testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.STRING, firstParameter,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2, "", testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				"", testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.STRING, secondParameter, "", testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME,
				"888.88", testScope);
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME2, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				USER_VARIABLE_2_VALUE_TYPE_STRING, testScope);

		List<InternToken> firstParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		List<InternToken> secondParameterList = FormulaEditorTestUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorTestUtil.testDoubleParameterFunction(Functions.REGEX, firstParameterList, secondParameterList,
				"" + Double.NaN, testScope);
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
		FormulaElement parseTree = new InternFormulaParser(letterTokenList).parseFormula(testScope);

		assertNotNull(parseTree);
		assertEquals(String.valueOf((firstParameter + secondParameter)
						.charAt((firstParameter + secondParameter).length() - 1)),
				parseTree.interpretRecursive(testScope));
	}
}
