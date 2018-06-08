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

package org.catrobat.catroid.test.formulaeditor;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.LinkedList;
import java.util.List;

public class FormulaTest extends InstrumentationTestCase {

	public void testRequiredRessources() {
		Formula formula0 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_DETECTED.name(), null));
		assertEquals(formula0.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formula1 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_SIZE.name(), null));
		assertEquals(formula1.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formula2 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_X_POSITION.name(), null));
		assertEquals(formula2.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formula3 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_Y_POSITION.name(), null));
		assertEquals(formula3.getRequiredResources(), Brick.FACE_DETECTION);

		Formula simpleFormula = new Formula(42.0d);
		assertEquals(simpleFormula.getRequiredResources(), Brick.NO_RESOURCES);

		Formula formulaWithRessourceLeft = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(),
				null, new FormulaElement(ElementType.SENSOR, Sensors.FACE_Y_POSITION.name(), null), new FormulaElement(
				ElementType.NUMBER, Double.toString(96d), null)));
		assertEquals(formulaWithRessourceLeft.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formulaWithRessourceRight = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(),
				null, new FormulaElement(ElementType.NUMBER, Double.toString(96d), null), new FormulaElement(
				ElementType.SENSOR, Sensors.FACE_X_POSITION.name(), null)));
		assertEquals(formulaWithRessourceRight.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formulaSameRessourceTwice = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(),
				null, new FormulaElement(ElementType.SENSOR, Sensors.FACE_DETECTED.name(), null), new FormulaElement(
				ElementType.SENSOR, Sensors.FACE_SIZE.name(), null)));
		assertEquals(formulaSameRessourceTwice.getRequiredResources(), Brick.FACE_DETECTION);
	}

	public void testIsSingleNumberFormula() {

		Formula formula = new Formula(1);
		assertTrue(formula.isSingleNumberFormula());

		formula = new Formula(1.0d);
		assertTrue(formula.isSingleNumberFormula());

		formula = new Formula(1.0f);
		assertTrue(formula.isSingleNumberFormula());

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull(parseTree);
		assertEquals(-1d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertTrue(formula.isSingleNumberFormula());

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull(parseTree);
		assertEquals(-1d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertTrue(formula.isSingleNumberFormula());

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull(parseTree);
		assertEquals(-2d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertFalse(formula.isSingleNumberFormula());

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.1111"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull(parseTree);
		assertEquals(1d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertFalse(formula.isSingleNumberFormula());
	}

	public void testComputeDialogResult() {
		FormulaElement helloStringFormulaElement = new FormulaElement(ElementType.STRING, "hello", null);
		FormulaElement worldStringFormulaElement = new FormulaElement(ElementType.STRING, "world", null);
		FormulaElement joinFunctionFormulaElement = new FormulaElement(ElementType.FUNCTION,
				Functions.JOIN.name(), null, helloStringFormulaElement, worldStringFormulaElement);

		Formula joinFormular = new Formula(joinFunctionFormulaElement);
		String computeDialogResult = joinFormular.getResultForComputeDialog(null);
		assertEquals("helloworld", computeDialogResult);

		FormulaElement indexFormulaElement = new FormulaElement(ElementType.NUMBER, "1", null);
		FormulaElement letterFunctionFormulaElement = new FormulaElement(ElementType.FUNCTION,
				Functions.LETTER.name(), null, indexFormulaElement, helloStringFormulaElement);

		Formula letterFormular = new Formula(letterFunctionFormulaElement);
		computeDialogResult = letterFormular.getResultForComputeDialog(null);
		assertEquals("h", computeDialogResult);
	}
}
