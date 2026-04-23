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

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.ExternInternRepresentationMapping;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

@RunWith(AndroidJUnit4.class)
public class InternFormulaTest {
	@Test
	public void testReplaceFunctionByToken() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());

		internFormula.setCursorAndSelection(internFormula.getExternFormulaString().length(), true);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_4, getApplicationContext(), null);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_2, getApplicationContext(), null);
		assertNull(internFormula.getSelection());

		internFormula.setCursorAndSelection(internFormula.getExternFormulaString().length(), true);
		assertInternFormulaSelectionIndices(0, 0, internFormula);
	}

	@Test
	public void testReplaceFunctionButKeepParameters() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());

		internFormula.setCursorAndSelection(internFormula.getExternFormulaString().length(), true);
		assertInternFormulaSelectionIndices(0, 9, internFormula);

		internFormula.handleKeyInput(R.string.formula_editor_function_rand, getApplicationContext(), null);
		assertInternFormulaSelectionIndices(2, 8, internFormula);

		internFormula.setCursorAndSelection(internFormula.getExternFormulaString().length(), true);
		assertInternFormulaSelectionIndices(0, 11, internFormula);

		internFormula.handleKeyInput(R.string.formula_editor_function_sqrt, getApplicationContext(), null);
		assertInternFormulaSelectionIndices(2, 8, internFormula);

		internFormula.setCursorAndSelection(internFormula.getExternFormulaString().length(), true);
		assertInternFormulaSelectionIndices(0, 9, internFormula);
	}

	@Test
	public void testSelectBrackets() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "0"));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());
		String externFormulaString = internFormula.getExternFormulaString();

		internFormula.setCursorAndSelection(externFormulaString.length(), true);
		assertInternFormulaSelectionIndices(0, 2, internFormula);

		internFormula.setCursorAndSelection(externFormulaString.length(), true);
		assertInternFormulaSelectionIndices(0, 2, internFormula);
	}

	@Test
	public void testReplaceSelection() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());

		internFormula.setCursorAndSelection(1, true);
		String externFormulaString = internFormula.getExternFormulaString();

		int tokenSelectionStartIndex = -1;
		int tokenSelectionEndIndex = 3;

		internFormula.internFormulaTokenSelection = new InternFormulaTokenSelection(
				TokenSelectionType.USER_SELECTION, tokenSelectionStartIndex, tokenSelectionEndIndex);

		internFormula.handleKeyInput(R.id.formula_editor_keyboard_0, getApplicationContext(), null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
	}

	@Test
	public void testSetExternCursorPositionLeftTo() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());
		internFormula.setCursorAndSelection(1, false);

		ExternInternRepresentationMapping externInternRepresentationMapping = new ExternInternRepresentationMapping();

		internFormula.externInternRepresentationMapping = externInternRepresentationMapping;

		int externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		internFormula.setExternCursorPositionLeftTo(1);
		assertEquals(externCursorPositionBeforeMethodCall, internFormula.getExternCursorPosition());
	}

	@Test
	public void testSetExternCursorPositionRightToEmptyFormula() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());
		internFormula.setCursorAndSelection(1, false);

		int externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		internFormula.setExternCursorPositionRightTo(1);
		assertEquals(externCursorPositionBeforeMethodCall, internFormula.getExternCursorPosition());
	}

	@Test
	public void testSetExternCursorPositionRightTo() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getApplicationContext());
		internFormula.setCursorAndSelection(1, false);

		internFormula.setExternCursorPositionRightTo(3);
		assertEquals(13, internFormula.getExternCursorPosition());

		internFormula.externInternRepresentationMapping = new ExternInternRepresentationMapping();
		int externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		internFormula.setExternCursorPositionRightTo(1);
		assertEquals(externCursorPositionBeforeMethodCall, internFormula.getExternCursorPosition());
	}

	@Test
	public void testSelectCursorPositionInternTokenOnError() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);

		internFormula.cursorPositionInternToken = null;
		internFormula.selectCursorPositionInternToken(InternFormula.TokenSelectionType.USER_SELECTION);
		assertNull(internFormula.internFormulaTokenSelection);
	}

	@Test
	public void testTokenTrailingWhiteSpace() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "0 "));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.trimExternFormulaString(getApplicationContext());
	}

	private void assertInternFormulaSelectionIndices(int expectedStartIndex, int expectedEndIndex, InternFormula internFormula) {
		assertEquals(expectedStartIndex, internFormula.getSelection().getStartIndex());
		assertEquals(expectedEndIndex, internFormula.getSelection().getEndIndex());
	}
}
