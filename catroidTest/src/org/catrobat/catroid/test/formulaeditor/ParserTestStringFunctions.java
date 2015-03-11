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

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.LinkedList;
import java.util.List;

public class ParserTestStringFunctions extends AndroidTestCase {

	private Sprite testSprite;
	private Project project;
	private static final double USER_VARIABLE_1_VALUE_TYPE_DOUBLE = 888.88;
	private static final String USER_VARIABLE_2_VALUE_TYPE_STRING = "another String";
	private static final String PROJECT_USER_VARIABLE_NAME = "projectUserVariable";
	private static final String PROJECT_USER_VARIABLE_NAME2 = "projectUserVariable2";

	@Override
	protected void setUp() {
		testSprite = new Sprite("testsprite");
		project = new Project(null, UiTestUtils.PROJECTNAME1);
		project.addSprite(testSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		UserVariablesContainer userVariableContainer = ProjectManager.getInstance().getCurrentProject()
				.getUserVariables();
		userVariableContainer.addProjectUserVariable(PROJECT_USER_VARIABLE_NAME).setValue(
				USER_VARIABLE_1_VALUE_TYPE_DOUBLE);
		userVariableContainer.addProjectUserVariable(PROJECT_USER_VARIABLE_NAME2).setValue(
				USER_VARIABLE_2_VALUE_TYPE_STRING);
	}

	public void testLength() {
		String firstParameter = "testString";
		FormulaEditorUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.STRING, firstParameter,
				(double) firstParameter.length(), testSprite);

		String number = "1.1";
		FormulaEditorUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.NUMBER, number,
				(double) number.length(), testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, (double) Double.toString(USER_VARIABLE_1_VALUE_TYPE_DOUBLE).length(),
				testSprite);

		FormulaEditorUtil.testSingleParameterFunction(Functions.LENGTH, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME2, (double) USER_VARIABLE_2_VALUE_TYPE_STRING.length(), testSprite);

		List<InternToken> firstParameterList = new LinkedList<InternToken>();
		firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		FormulaEditorUtil.testSingleParameterFunction(Functions.LENGTH, firstParameterList, 0d, testSprite);

	}

	public void testLetter() {
		String letterString = "letterString";
		String index = "7";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, String.valueOf(letterString.charAt(Integer.valueOf(index) - 1)),
				testSprite);

		index = "0";
		String emptyString = "";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, emptyString, testSprite);

		index = "-5";
		emptyString = "";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.STRING, letterString, emptyString, testSprite);

		index = "0";
		emptyString = "";
		letterString = emptyString;
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, String.valueOf(index),
				InternTokenType.STRING, letterString, emptyString, testSprite);

		letterString = "letterString";
		index = "2";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.STRING,
				String.valueOf(letterString.charAt(Integer.valueOf(index) - 1)), InternTokenType.STRING, letterString,
				emptyString, testSprite);

		index = "4";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME,
				String.valueOf(Double.toString(USER_VARIABLE_1_VALUE_TYPE_DOUBLE).charAt(Integer.valueOf(index) - 1)),
				testSprite);

		index = "3";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, InternTokenType.NUMBER, index,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				String.valueOf(USER_VARIABLE_2_VALUE_TYPE_STRING.charAt(Integer.valueOf(index) - 1)), testSprite);

		List<InternToken> firstParameterList = new LinkedList<InternToken>();
		List<InternToken> secondParameterList = new LinkedList<InternToken>();
		firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		secondParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorUtil.testDoubleParameterFunction(Functions.LETTER, firstParameterList, secondParameterList, "",
				testSprite);
	}

	public void testJoin() {
		String firstParameter = "first";
		String secondParameter = "second";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "";
		secondParameter = "second";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "first";
		secondParameter = "";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "55";
		secondParameter = "66";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.NUMBER, firstParameter,
				InternTokenType.NUMBER, secondParameter, firstParameter + secondParameter, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.NUMBER, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.NUMBER, secondParameter, firstParameter + secondParameter, testSprite);

		firstParameter = "5*3-6+(8*random(1,2))";
		secondParameter = "string'**##!§\"$\'§%%/&%(())??";
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.STRING, secondParameter, firstParameter + secondParameter, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.STRING, firstParameter,
				InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2, firstParameter
						+ USER_VARIABLE_2_VALUE_TYPE_STRING, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.USER_VARIABLE, PROJECT_USER_VARIABLE_NAME2,
				USER_VARIABLE_1_VALUE_TYPE_DOUBLE + USER_VARIABLE_2_VALUE_TYPE_STRING, testSprite);
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, InternTokenType.USER_VARIABLE,
				PROJECT_USER_VARIABLE_NAME, InternTokenType.STRING, secondParameter, USER_VARIABLE_1_VALUE_TYPE_DOUBLE
						+ secondParameter, testSprite);

		List<InternToken> firstParameterList = new LinkedList<InternToken>();
		List<InternToken> secondParameterList = new LinkedList<InternToken>();
		firstParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.PLUS,
				InternTokenType.STRING, "datString");
		secondParameterList = FormulaEditorUtil.buildBinaryOperator(InternTokenType.NUMBER, "5", Operators.MULT,
				InternTokenType.STRING, "anotherString");
		FormulaEditorUtil.testDoubleParameterFunction(Functions.JOIN, firstParameterList, secondParameterList, ""
				+ Double.NaN + Double.NaN, testSprite);
	}

	public void testStringFunctionsNested() {
		String firstParameter = "hello";
		String secondParameter = " world";
		List<InternToken> joinTokenList = FormulaEditorUtil.buildDoubleParameterFunction(Functions.JOIN,
				InternTokenType.STRING, firstParameter, InternTokenType.STRING, secondParameter);
		List<InternToken> lengthTokenList = FormulaEditorUtil.buildSingleParameterFunction(Functions.LENGTH,
				joinTokenList);
		List<InternToken> secondInternTokenList = new LinkedList<InternToken>();
		secondInternTokenList.add(new InternToken(InternTokenType.STRING, firstParameter + secondParameter));
		List<InternToken> letterTokenList = FormulaEditorUtil.buildDoubleParameterFunction(Functions.LETTER,
				lengthTokenList, secondInternTokenList);
		FormulaElement parseTree = new InternFormulaParser(letterTokenList).parseFormula();

		assertNotNull("Formula is not parsed correctly: " + Functions.LETTER.name() + "(" + Functions.LENGTH.name()
				+ "(" + Functions.JOIN.name() + "(" + firstParameter + "," + secondParameter + ")" + ")" + ","
				+ firstParameter + secondParameter + ")", parseTree);
		assertEquals(
				"Formula interpretation is not as expected: " + Functions.LETTER.name() + "(" + Functions.LENGTH.name()
						+ "(" + Functions.JOIN.name() + "(" + firstParameter + "," + secondParameter + ")" + ")" + ","
						+ firstParameter + secondParameter + ")", String.valueOf((firstParameter + secondParameter)
						.charAt((firstParameter + secondParameter).length() - 1)),
				parseTree.interpretRecursive(testSprite));
	}
}
