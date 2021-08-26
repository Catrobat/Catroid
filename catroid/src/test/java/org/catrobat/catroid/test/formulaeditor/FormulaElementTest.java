/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.assertEqualsTokenLists;

@RunWith(JUnit4.class)
public class FormulaElementTest {

	Scope scope;

	@Before
	public void setUp() throws Exception {
		Project project = new Project(MockUtil.mockContextForProject(), "Project");
		Sprite background = project.getDefaultScene().getBackgroundSprite();
		Sprite sprite = new Sprite("testSprite");

		scope = new Scope(project, background, new SequenceAction());

		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}

	@Test
	public void testGetInternTokenList() {

		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, Operators.MINUS.name()));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "1"));
		internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);

		assertNotNull(parseTree);
		assertEquals(-1d, parseTree.interpretRecursive(scope));

		List<InternToken> internTokenListAfterConversion = parseTree.getInternTokenList();
		assertEquals(internTokenListAfterConversion.size(), internTokenList.size());

		assertEqualsTokenLists(internTokenList, internTokenListAfterConversion);
	}

	@Test
	public void testInterpretNonExistingUserVariable() {
		FormulaElement formulaElement = new FormulaElement(ElementType.USER_VARIABLE, "notExistingUserVariable", null);
		assertEquals(0d, formulaElement.interpretRecursive(scope));
	}

	@Test
	public void testInterpretNonExistingUserList() {
		FormulaElement formulaElement = new FormulaElement(ElementType.USER_LIST, "notExistingUserList", null);
		assertEquals(0d, formulaElement.interpretRecursive(scope));
	}

	@Test
	public void testInterpretNotExisitingUnaryOperator() {
		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null, null,
				new FormulaElement(ElementType.NUMBER, "1.0", null));

		assertEquals(0d, formulaElement.interpretRecursive(scope));
	}

	@Test
	public void testCheckDegeneratedDoubleValues() {

		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.PLUS.name(), null,
				new FormulaElement(ElementType.NUMBER, Double.toString(Double.MAX_VALUE), null), new FormulaElement(
				ElementType.NUMBER, Double.toString(Double.MAX_VALUE), null));

		assertEquals(Double.MAX_VALUE, formulaElement.interpretRecursive(scope));

		formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null, new FormulaElement(
				ElementType.NUMBER, Double.toString(Double.MAX_VALUE * -1d), null), new FormulaElement(
				ElementType.NUMBER, Double.toString(Double.MAX_VALUE), null));

		assertEquals(Double.MAX_VALUE * -1d, formulaElement.interpretRecursive(scope));

		formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.DIVIDE.name(), null, new FormulaElement(
				ElementType.NUMBER, "0", null), new FormulaElement(ElementType.NUMBER, "0", null));

		assertEquals(Double.NaN, formulaElement.interpretRecursive(scope));
	}

	@Test
	public void testIsBoolean() {
		FormulaElement formulaElement = new FormulaElement(ElementType.USER_VARIABLE, "notExistingUserVariable", null);
		assertFalse(formulaElement.isBoolean(scope));
	}

	@Test
	public void testContainsElement() {
		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null,
				new FormulaElement(ElementType.NUMBER, "0.0", null), new FormulaElement(ElementType.USER_VARIABLE,
				"user-variable", null));
		assertTrue(formulaElement.containsElement(ElementType.USER_VARIABLE));

		formulaElement = new FormulaElement(ElementType.FUNCTION, Functions.SIN.name(), null, new FormulaElement(
				ElementType.OPERATOR, "+", null), null);

		assertFalse(formulaElement.containsElement(ElementType.NUMBER));
	}

	@Test
	public void testClone() {
		FormulaElement formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null,
				new FormulaElement(ElementType.NUMBER, "0.0", null), new FormulaElement(ElementType.USER_VARIABLE,
				"user-variable", null));

		List<InternToken> internTokenList = formulaElement.getInternTokenList();

		FormulaElement clonedFormulaElement = formulaElement.clone();
		List<InternToken> internTokenListAfterClone = clonedFormulaElement.getInternTokenList();

		assertEqualsTokenLists(internTokenList, internTokenListAfterClone);

		formulaElement = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(), null, null,
				new FormulaElement(ElementType.USER_VARIABLE, "user-variable", null));

		internTokenList = formulaElement.getInternTokenList();

		clonedFormulaElement = formulaElement.clone();
		internTokenListAfterClone = clonedFormulaElement.getInternTokenList();

		assertEqualsTokenLists(internTokenList, internTokenListAfterClone);
	}

	@Test
	public void testCorrectDecimals() {
		FormulaElement formulaElementAddition = new FormulaElement(ElementType.OPERATOR,
				Operators.PLUS.name(), null,
				new FormulaElement(ElementType.NUMBER, "1.1", null),
				new FormulaElement(ElementType.NUMBER, "0.1", null));

		assertEquals(1.2, formulaElementAddition.interpretRecursive(scope));

		FormulaElement formulaElementSubtraction = new FormulaElement(ElementType.OPERATOR,
				Operators.MINUS.name(), null,
				new FormulaElement(ElementType.NUMBER, "15.3", null),
				new FormulaElement(ElementType.NUMBER, "3.2", null));

		assertEquals(12.1, formulaElementSubtraction.interpretRecursive(scope));

		FormulaElement formulaElementMultiplication = new FormulaElement(ElementType.OPERATOR,
				Operators.MULT.name(), null,
				new FormulaElement(ElementType.NUMBER, "3.5", null),
				new FormulaElement(ElementType.NUMBER, "3.2", null));

		assertEquals(11.2, formulaElementMultiplication.interpretRecursive(scope));

		FormulaElement formulaElementDivision = new FormulaElement(ElementType.OPERATOR,
				Operators.DIVIDE.name(), null,
				new FormulaElement(ElementType.NUMBER, "1.1", null),
				new FormulaElement(ElementType.NUMBER, "5", null));

		assertEquals(0.22, formulaElementDivision.interpretRecursive(scope));

		FormulaElement formulaElementDivisionInfiniteDecimals = new FormulaElement(ElementType.OPERATOR,
				Operators.DIVIDE.name(), null,
				new FormulaElement(ElementType.NUMBER, "1", null),
				new FormulaElement(ElementType.NUMBER, "2.34", null));

		assertEquals(0.42735042735042733, formulaElementDivisionInfiniteDecimals.interpretRecursive(scope));
	}
}
