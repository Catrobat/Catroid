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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;

import java.util.LinkedList;
import java.util.List;

public class FormulaElementTest extends InstrumentationTestCase {

	public void testGetInternTokenList() {

		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();

		assertNotNull("Formula is not parsed correctly: ( - 1 )", parseTree);
		assertEquals("Formula interpretation is not as expected", -1d, parseTree.interpretRecursive(null));

		List<InternToken> internTokenListAfterConversion = parseTree.getInternTokenList();
		assertEquals("Generate InternTokenList from Tree error", internTokenListAfterConversion.size(),
				internTokenList.size());

		for (int index = 0; index < internTokenListAfterConversion.size(); index++) {
			assertTrue(
					"Generate InternTokenList from Tree error",
					internTokenListAfterConversion.get(index).getInternTokenType() == internTokenList.get(index)
							.getInternTokenType()
							&& internTokenListAfterConversion.get(index).getTokenStringValue()
							.compareTo(internTokenList.get(index).getTokenStringValue()) == 0);
		}
	}

	public void testInterpretNonExistingUserVariable() {
		Project project = new Project(getInstrumentation().getTargetContext(), "testProject");
		ProjectManager.getInstance().setProject(project);
		FormulaElement formulaElement = new FormulaElement(ElementType.USER_VARIABLE, "notExistingUserVariable", null);
		assertEquals("Not existing UserVariable misinterpretation",
				FormulaElement.NOT_EXISTING_USER_VARIABLE_INTERPRETATION_VALUE, formulaElement.interpretRecursive(null));
	}

	public void testInterpretNonExistingUserList() {
		Project project = new Project(getInstrumentation().getTargetContext(), "testProject");
		ProjectManager.getInstance().setProject(project);
		FormulaElement formulaElement = new FormulaElement(ElementType.USER_LIST, "notExistingUserList", null);
		assertEquals("Not existing UserList misinterpretation",
				FormulaElement.NOT_EXISTING_USER_LIST_INTERPRETATION_VALUE, formulaElement.interpretRecursive(null));
	}

	public void testInterpretNotExisitingUnaryOperator() {
		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null, null,
				new FormulaElement(ElementType.NUMBER, "1.0", null));

		assertEquals("Not existing unary operator misinterpretation", 0d, formulaElement.interpretRecursive(null));
	}

	public void testCheckDegeneratedDoubleValues() {

		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null,
				new FormulaElement(ElementType.NUMBER, Double.toString(Double.MAX_VALUE), null), new FormulaElement(
				ElementType.NUMBER, Double.toString(Double.MAX_VALUE), null));

		assertEquals("Degenerated double values error", Double.MAX_VALUE, formulaElement.interpretRecursive(null));

		formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null, new FormulaElement(
				ElementType.NUMBER, Double.toString(Double.MAX_VALUE * -1d), null), new FormulaElement(
				ElementType.NUMBER, Double.toString(Double.MAX_VALUE), null));

		assertEquals("Degenerated double values error", Double.MAX_VALUE * -1d, formulaElement.interpretRecursive(null));

		formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.DIVIDE.name(), null, new FormulaElement(
				ElementType.NUMBER, "0", null), new FormulaElement(ElementType.NUMBER, "0", null));

		assertEquals("Degenerated double values error", Double.NaN, formulaElement.interpretRecursive(null));
	}

	public void testIsLogicalOperator() {
		FormulaElement formulaElement = new FormulaElement(ElementType.USER_VARIABLE, "notExistingUserVariable", null);
		assertFalse("isLogicalOperator found logical operator but was userVariable", formulaElement.isLogicalOperator());
	}

	public void testContainsElement() {
		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null,
				new FormulaElement(ElementType.NUMBER, "0.0", null), new FormulaElement(ElementType.USER_VARIABLE,
				"user-variable", null));
		assertTrue("ContainsElement: uservariable not found", formulaElement.containsElement(ElementType.USER_VARIABLE));

		formulaElement = new FormulaElement(ElementType.FUNCTION, Functions.SIN.name(), null, new FormulaElement(
				ElementType.OPERATOR, "+", null), null);

		assertTrue("ContainsElement: Operator \" + \" should not have been found!",
				!formulaElement.containsElement(ElementType.NUMBER));
	}

	public void testClone() {
		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null,
				new FormulaElement(ElementType.NUMBER, "0.0", null), new FormulaElement(ElementType.USER_VARIABLE,
				"user-variable", null));

		List<InternToken> internTokenList = formulaElement.getInternTokenList();

		FormulaElement clonedFormulaElement = formulaElement.clone();
		List<InternToken> internTokenListAfterClone = clonedFormulaElement.getInternTokenList();

		for (int index = 0; index < internTokenListAfterClone.size(); index++) {
			assertTrue(
					"Clone error",
					internTokenListAfterClone.get(index).getInternTokenType() == internTokenList.get(index)
							.getInternTokenType()
							&& internTokenListAfterClone.get(index).getTokenStringValue()
							.compareTo(internTokenList.get(index).getTokenStringValue()) == 0);
		}

		formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null, null,
				new FormulaElement(ElementType.USER_VARIABLE, "user-variable", null));

		internTokenList = formulaElement.getInternTokenList();

		clonedFormulaElement = formulaElement.clone();
		internTokenListAfterClone = clonedFormulaElement.getInternTokenList();

		for (int index = 0; index < internTokenListAfterClone.size(); index++) {
			assertTrue(
					"Clone error",
					internTokenListAfterClone.get(index).getInternTokenType() == internTokenList.get(index)
							.getInternTokenType()
							&& internTokenListAfterClone.get(index).getTokenStringValue()
							.compareTo(internTokenList.get(index).getTokenStringValue()) == 0);
		}
	}
}
