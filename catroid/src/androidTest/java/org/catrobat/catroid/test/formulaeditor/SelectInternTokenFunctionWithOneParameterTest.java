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

package org.catrobat.catroid.test.formulaeditor;

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternToExternGenerator;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_NAME;

@RunWith(Parameterized.class)
public class SelectInternTokenFunctionWithOneParameterTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{Functions.SIN.name(), new InternToken(FUNCTION_NAME, Functions.SIN.name())},
				{Functions.COS.name(), new InternToken(FUNCTION_NAME, Functions.COS.name())},
				{Functions.TAN.name(), new InternToken(FUNCTION_NAME, Functions.TAN.name())},
				{Functions.LOG.name(), new InternToken(FUNCTION_NAME, Functions.LOG.name())},
				{Functions.SQRT.name(), new InternToken(FUNCTION_NAME, Functions.SQRT.name())},
				{Functions.ABS.name(), new InternToken(FUNCTION_NAME, Functions.ABS.name())},
				{Functions.ROUND.name(), new InternToken(FUNCTION_NAME, Functions.ROUND.name())},
				{Functions.ARCSIN.name(), new InternToken(FUNCTION_NAME, Functions.ARCSIN.name())},
				{Functions.ARCCOS.name(), new InternToken(FUNCTION_NAME, Functions.ARCCOS.name())},
				{Functions.ARCTAN.name(), new InternToken(FUNCTION_NAME, Functions.ARCTAN.name())},
				{Functions.LENGTH.name(), new InternToken(FUNCTION_NAME, Functions.LENGTH.name())},
				{Functions.LETTER.name(), new InternToken(FUNCTION_NAME, Functions.LETTER.name())},
				{Functions.NUMBER_OF_ITEMS.name(), new InternToken(FUNCTION_NAME, Functions.NUMBER_OF_ITEMS.name())},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public InternToken functionToken;

	private InternFormula internFormula;
	private String functionName;

	@Before
	public void setUp() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(functionToken);
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext());

		functionName = ApplicationProvider.getApplicationContext().getResources()
				.getString(InternToExternGenerator.getMappedString(functionToken.getTokenStringValue()));
	}

	@Test
	public void testSelectFunctionNameBegin() {
		internFormula.setCursorAndSelection(0, true);
		assertInternFormulaSelectionIndices(0, 3, internFormula);
	}

	@Test
	public void testSelectFunctionNameMiddle() {
		internFormula.setCursorAndSelection(functionName.length() / 2, true);
		assertInternFormulaSelectionIndices(0, 3, internFormula);
	}

	@Test
	public void testSelectFunctionNameEnd() {
		internFormula.setCursorAndSelection(functionName.length(), true);
		assertInternFormulaSelectionIndices(0, 3, internFormula);
	}

	@Test
	public void testSelectBracketOpen() {
		internFormula.setCursorAndSelection(functionName.length() + 1, true);
		assertInternFormulaSelectionIndices(0, 3, internFormula);
	}

	@Test
	public void testSelectNumberParameter() {
		internFormula.setCursorAndSelection(functionName.length() + 2, true);
		assertInternFormulaSelectionIndices(2, 2, internFormula);
		internFormula.setCursorAndSelection(functionName.length() + 3, true);
		assertInternFormulaSelectionIndices(2, 2, internFormula);
	}

	@Test
	public void testSelectStringParameterEnd() {
		internFormula.setCursorAndSelection(functionName.length() + 4, true);
		assertInternFormulaSelectionIndices(0, 3, internFormula);
	}

	private void assertInternFormulaSelectionIndices(int expectedStartIndex, int expectedEndIndex, InternFormula internFormula) {
		assertEquals(expectedStartIndex, internFormula.getSelection().getStartIndex());
		assertEquals(expectedEndIndex, internFormula.getSelection().getEndIndex());
	}
}

