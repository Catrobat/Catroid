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
		assertEquals("Required ressources for is_face_detected are not set to FACE_DETECTION",
				formula0.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formula1 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_SIZE.name(), null));
		assertEquals("Required ressources for face_size are not set to FACE_DETECTION",
				formula1.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formula2 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_X_POSITION.name(), null));
		assertEquals("Required ressources for face_x_position are not set to FACE_DETECTION",
				formula2.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formula3 = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.FACE_Y_POSITION.name(), null));
		assertEquals("Required ressources for face_y_position are not set to FACE_DETECTION",
				formula3.getRequiredResources(), Brick.FACE_DETECTION);

		Formula simpleFormula = new Formula(42.0d);
		assertEquals("Simple formula requests resources", simpleFormula.getRequiredResources(), Brick.NO_RESOURCES);

		Formula formulaWithRessourceLeft = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(),
				null, new FormulaElement(ElementType.SENSOR, Sensors.FACE_Y_POSITION.name(), null), new FormulaElement(
				ElementType.NUMBER, Double.toString(96d), null)));
		assertEquals("Required ressources of left child are not calculated propperly",
				formulaWithRessourceLeft.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formulaWithRessourceRight = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(),
				null, new FormulaElement(ElementType.NUMBER, Double.toString(96d), null), new FormulaElement(
				ElementType.SENSOR, Sensors.FACE_X_POSITION.name(), null)));
		assertEquals("Required ressources of right child are not calculated propperly",
				formulaWithRessourceRight.getRequiredResources(), Brick.FACE_DETECTION);

		Formula formulaSameRessourceTwice = new Formula(new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(),
				null, new FormulaElement(ElementType.SENSOR, Sensors.FACE_DETECTED.name(), null), new FormulaElement(
				ElementType.SENSOR, Sensors.FACE_SIZE.name(), null)));
		assertEquals("Required ressources of formula with several ressources are not calculated propperly",
				formulaSameRessourceTwice.getRequiredResources(), Brick.FACE_DETECTION);
	}

	public void testIsSingleNumberFormula() {

		Formula formula = new Formula(1);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		formula = new Formula(1.0d);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		formula = new Formula(1.0f);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: - 1", parseTree);
		assertEquals("Formula interpretation is not as expected", -1d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: - 1", parseTree);
		assertEquals("Formula interpretation is not as expected", -1d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertTrue("Formula should be single number formula", formula.isSingleNumberFormula());

		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.0"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: - 1 - 1", parseTree);
		assertEquals("Formula interpretation is not as expected", -2d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertFalse("Should NOT be a single number formula", formula.isSingleNumberFormula());

		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name()));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1.1111"));
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"));

		internParser = new InternFormulaParser(internTokenList);
		parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: round(1.1111)", parseTree);
		assertEquals("Formula interpretation is not as expected", 1d, parseTree.interpretRecursive(null));
		internTokenList.clear();

		formula = new Formula(parseTree);
		assertFalse("Should NOT be a single number formula", formula.isSingleNumberFormula());
	}
}
