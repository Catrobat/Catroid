/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import android.content.Context;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.Formula.StringProvider;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormulaTest {

	private List<InternToken> internTokenList;

	@Mock
	Scope scope;

	@Before
	public void setUp() {
		internTokenList = new LinkedList<>();
		if (ProjectManager.getInstance() == null) {
			new ProjectManager(mock(Context.class));
		}
	}

	@Test
	public void resourceWithSensorsVariationTest() {
		Formula formula0 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_DETECTED.name(), null));
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		formula0.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.FACE_DETECTION));

		Formula formula1 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_SIZE.name(), null));
		resourcesSet = new Brick.ResourcesSet();
		formula1.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.FACE_DETECTION));

		Formula formula2 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_X.name(), null));
		resourcesSet = new Brick.ResourcesSet();
		formula2.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.FACE_DETECTION));

		Formula formula3 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_Y.name(), null));
		resourcesSet = new Brick.ResourcesSet();
		formula3.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.FACE_DETECTION));
	}

	@Test
	public void resourceSetEmptyTest() {
		Formula simpleFormula = new Formula(42.0d);
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		simpleFormula.addRequiredResources(resourcesSet);
		assertThat(resourcesSet, is(empty()));
	}

	@Test
	public void resourceWithMultipleElementsAndSensorsTest() {
		Formula formulaWithResourceLeft = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null, new FormulaElement(ElementType.SENSOR, Sensors.FACE_Y.name(), null), new FormulaElement(ElementType.NUMBER, Double.toString(96d), null)));
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		formulaWithResourceLeft.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.FACE_DETECTION));

		Formula formulaWithResourceRight = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null, new FormulaElement(ElementType.NUMBER, Double.toString(96d), null), new FormulaElement(ElementType.SENSOR, Sensors.FACE_X.name(), null)));
		resourcesSet = new Brick.ResourcesSet();
		formulaWithResourceRight.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.FACE_DETECTION));

		Formula formulaSameResourceTwice = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null, new FormulaElement(ElementType.SENSOR, Sensors.FACE_DETECTED.name(), null), new FormulaElement(ElementType.SENSOR, Sensors.FACE_SIZE.name(), null)));
		resourcesSet = new Brick.ResourcesSet();
		formulaSameResourceTwice.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.FACE_DETECTION));
	}

	@Test
	public void singleNumberAsIntegerTest() {
		Formula formula = new Formula(1);
		assertTrue(formula.isNumber());
	}

	@Test
	public void singleNumberAsDoubleTest() {
		Formula formula = new Formula(1.0d);
		assertTrue(formula.isNumber());
	}

	@Test
	public void singleNumberAsFloatTest() {
		Formula formula = new Formula(1.0f);
		assertTrue(formula.isNumber());
	}

	@Test
	public void minusOneIntegerTest() {
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);

		assertNotNull(parseTree);
		assertEquals(-1d, parseTree.interpretRecursive(scope));
		internTokenList.clear();

		Formula formula = new Formula(parseTree);
		assertTrue(formula.isNumber());
	}

	@Test
	public void minusOneDoubleTest() {
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);

		assertNotNull(parseTree);
		assertEquals(-1d, parseTree.interpretRecursive(scope));
		internTokenList.clear();

		Formula formula = new Formula(parseTree);
		assertTrue(formula.isNumber());
	}

	@Test
	public void subtractionWithDoubleTest() {
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);

		assertNotNull(parseTree);
		assertEquals(-2d, parseTree.interpretRecursive(scope));
		internTokenList.clear();

		Formula formula = new Formula(parseTree);
		assertFalse(formula.isNumber());
	}

	@Test
	public void roundValue() {
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.1111"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(null);

		assertNotNull(parseTree);
		assertEquals(1d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		Formula formula = new Formula(parseTree);
		assertFalse(formula.isNumber());
	}

	@Test
	public void stringConcatenationTest() {
		FormulaElement helloStringFormulaElement = new FormulaElement(ElementType.STRING, "hello", null);
		FormulaElement worldStringFormulaElement = new FormulaElement(ElementType.STRING, "world", null);
		FormulaElement joinFunctionFormulaElement = new FormulaElement(ElementType.FUNCTION, Functions.JOIN.name(), null, helloStringFormulaElement, worldStringFormulaElement);
		Formula joinFormula = new Formula(joinFunctionFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = joinFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("helloworld", computeDialogResult);
	}

	@Test
	public void letterAtIndexTest() {
		FormulaElement helloStringFormulaElement = new FormulaElement(ElementType.STRING, "hello", null);
		FormulaElement indexFormulaElement = new FormulaElement(ElementType.NUMBER, "1", null);
		FormulaElement letterFunctionFormulaElement = new FormulaElement(ElementType.FUNCTION, Functions.LETTER.name(), null, indexFormulaElement, helloStringFormulaElement);
		Formula letterFormula = new Formula(letterFunctionFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = letterFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("h", computeDialogResult);
	}

	@Test
	public void regularExpressionTest() {
		FormulaElement regexStringFormulaElement = new FormulaElement(ElementType.STRING, " an? ([^ .]+)", null);
		FormulaElement iamanelephantStringFormulaElement = new FormulaElement(ElementType.STRING, "I am an elephant.", null);
		FormulaElement regexFunctionFormulaElement = new FormulaElement(ElementType.FUNCTION, Functions.REGEX.name(), null, regexStringFormulaElement, iamanelephantStringFormulaElement);
		Formula regexFormula = new Formula(regexFunctionFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = regexFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("elephant", computeDialogResult);
	}

	@Test
	public void bracketsWrappedFormulaTest() {
		FormulaElement bracketOpenFormulaElement = new FormulaElement(ElementType.BRACKET, null, null);
		FormulaElement numberFormulaElement = new FormulaElement(ElementType.NUMBER, "1", bracketOpenFormulaElement);
		bracketOpenFormulaElement.replaceElement(new FormulaElement(FormulaElement.ElementType.BRACKET, null, null, null, numberFormulaElement));
		Formula bracketWrappedFormula = new Formula(bracketOpenFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = bracketWrappedFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("1", computeDialogResult);
	}

	@Test
	public void singleNumberIntTest() {
		FormulaElement numberFormulaElement = new FormulaElement(ElementType.NUMBER, "5000", null);
		Formula numberFormula = new Formula(numberFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = numberFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("5000", computeDialogResult);
	}

	@Test
	public void singleNumberDoubleTest() {
		FormulaElement numberFormulaElement = new FormulaElement(ElementType.NUMBER, "5000.23", null);
		Formula numberFormula = new Formula(numberFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = numberFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("5000.23", computeDialogResult);
	}

	@Test
	public void singleNumberIntWithDecimalPointTest() {
		FormulaElement numberFormulaElement = new FormulaElement(ElementType.NUMBER, "5000.0", null);
		Formula numberFormula = new Formula(numberFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = numberFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("5000", computeDialogResult);
	}

	@Test
	public void singleStringNumberTest() {
		FormulaElement stringFormulaElement = new FormulaElement(ElementType.STRING, "1000", null);
		Formula stringFormula = new Formula(stringFormulaElement);
		StringProvider stringProvider = mock(StringProvider.class);
		String computeDialogResult = stringFormula.getUserFriendlyString(stringProvider, null);
		assertEquals("1000", computeDialogResult);
	}

	@Test
	public void arithmeticOperatorsTest() {
		StringProvider stringProvider = mock(StringProvider.class);

		assertEquals("234", createOperatorFormula(Operators.PLUS, "123", "111").getUserFriendlyString(stringProvider, null));
		assertEquals("-111", createOperatorFormula(Operators.MINUS, "123", "234").getUserFriendlyString(stringProvider, null));
		assertEquals("-1000000", createOperatorFormula(Operators.MINUS, "300", "1000300").getUserFriendlyString(stringProvider, null));
		assertEquals("-35", createOperatorFormula(Operators.MULT, "5", "-7").getUserFriendlyString(stringProvider, null));
		assertEquals("-5", createOperatorFormula(Operators.DIVIDE, "-35", "7").getUserFriendlyString(stringProvider, null));
		assertEquals("32", createOperatorFormula(Operators.POW, "2", "5").getUserFriendlyString(stringProvider, null));
	}

	@Test
	public void booleanOperatorsTest() {
		StringProvider stringProvider = mock(StringProvider.class);
		when(stringProvider.getTrueOrFalse(true)).thenReturn("true");
		when(stringProvider.getTrueOrFalse(false)).thenReturn("false");

		assertEquals("false", createOperatorFormula(Operators.LOGICAL_NOT, null, "1").getUserFriendlyString(stringProvider, null));
		assertEquals("false", createOperatorFormula(Operators.LOGICAL_AND, "1", "0").getUserFriendlyString(stringProvider, null));
		assertEquals("true", createOperatorFormula(Operators.LOGICAL_OR, "1", "0").getUserFriendlyString(stringProvider, null));
		assertEquals("true", createOperatorFormula(Operators.EQUAL, "1", "1").getUserFriendlyString(stringProvider, null));
		assertEquals("true", createOperatorFormula(Operators.NOT_EQUAL, "0", "1").getUserFriendlyString(stringProvider, null));
		assertEquals("false", createOperatorFormula(Operators.SMALLER_OR_EQUAL, "1", "0").getUserFriendlyString(stringProvider, null));
		assertEquals("true", createOperatorFormula(Operators.GREATER_OR_EQUAL, "1", "1").getUserFriendlyString(stringProvider, null));
		assertEquals("true", createOperatorFormula(Operators.SMALLER_THAN, "0", "1").getUserFriendlyString(stringProvider, null));
		assertEquals("false", createOperatorFormula(Operators.GREATER_THAN, "1", "1").getUserFriendlyString(stringProvider, null));
	}

	@Test
	public void functionBooleanValueTest() {
		StringProvider stringProvider = mock(StringProvider.class);
		when(stringProvider.getTrueOrFalse(true)).thenReturn("true");
		when(stringProvider.getTrueOrFalse(false)).thenReturn("false");

		FormulaElement trueFunctionFormulaElement = new FormulaElement(ElementType.FUNCTION, Functions.TRUE.name(), null);
		Formula trueFunctionFormula = new Formula(trueFunctionFormulaElement);
		assertEquals("true", trueFunctionFormula.getUserFriendlyString(stringProvider, null));

		FormulaElement falseFunctionFormulaElement = new FormulaElement(ElementType.FUNCTION, Functions.FALSE.name(), null);
		Formula falseFunctionFormula = new Formula(falseFunctionFormulaElement);
		assertEquals("false", falseFunctionFormula.getUserFriendlyString(stringProvider, null));
	}

	@Test
	public void userVariableBooleanValueTest() {
		StringProvider stringProvider = mock(StringProvider.class);
		when(stringProvider.getTrueOrFalse(true)).thenReturn("true");
		when(stringProvider.getTrueOrFalse(false)).thenReturn("false");

		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);
		Sprite testSprite = new Sprite("sprite");
		project.getDefaultScene().addSprite(testSprite);
		Scope testScope = new Scope(project, testSprite, new SequenceAction());
		project.addUserVariable(new UserVariable("trueVariable", true));
		project.addUserVariable(new UserVariable("falseVariable", false));

		FormulaElement userVariableFormulaElement = new FormulaElement(ElementType.USER_VARIABLE, "trueVariable", null);
		Formula userVariableFormula = new Formula(userVariableFormulaElement);
		assertEquals("true", userVariableFormula.getUserFriendlyString(stringProvider, testScope));

		userVariableFormulaElement = new FormulaElement(ElementType.USER_VARIABLE, "falseVariable", null);
		userVariableFormula = new Formula(userVariableFormulaElement);
		assertEquals("false", userVariableFormula.getUserFriendlyString(stringProvider, testScope));
	}

	@Test
	public void testCollision() {
		StringProvider stringProvider = mock(StringProvider.class);
		when(stringProvider.getTrueOrFalse(false)).thenReturn("false");

		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);
		Sprite testSprite = new Sprite("sprite");
		project.getDefaultScene().addSprite(testSprite);
		Scope testScope = new Scope(project, testSprite, new SequenceAction());

		FormulaElement collisionFormulaElement = new FormulaElement(ElementType.COLLISION_FORMULA, "sprite", null);
		Formula collisionFormula = new Formula(collisionFormulaElement);
		assertEquals("false", collisionFormula.getUserFriendlyString(stringProvider, testScope));
	}

	private Formula createOperatorFormula(Operators operator, String leftChildValue, String rightChildValue) {
		FormulaElement operatorElement = new FormulaElement(ElementType.OPERATOR, operator.name(), null);
		if (leftChildValue != null) {
			operatorElement.setLeftChild(new FormulaElement(ElementType.NUMBER, leftChildValue, operatorElement));
		}
		if (rightChildValue != null) {
			operatorElement.setRightChild(new FormulaElement(ElementType.NUMBER, rightChildValue, operatorElement));
		}
		return new Formula(operatorElement);
	}
}
