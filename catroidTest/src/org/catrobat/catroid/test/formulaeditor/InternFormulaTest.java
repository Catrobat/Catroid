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

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.ExternInternRepresentationMapping;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternFormula.CursorTokenPropertiesAfterModification;
import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InternFormulaTest extends InstrumentationTestCase {

	public void testInsertRightToCurrentToken() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				getInstrumentation().getTargetContext(), null);

		assertTrue("Enter decimal mark error", internTokens.get(0).getTokenStringValue().compareTo("0.") == 0);

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				getInstrumentation().getTargetContext(), null);

		assertTrue("Enter decimal mark error", internTokens.get(1).getTokenStringValue().compareTo("0.") == 0);

		internTokens = new ArrayList<InternToken>();
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				getInstrumentation().getTargetContext(), null);

		assertTrue("Enter decimal mark error", internTokens.get(0).getTokenStringValue().compareTo("0.") == 0);
	}

	public void testInsertLeftToCurrentToken() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		String externFormulaStringBeforeInput = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				getInstrumentation().getTargetContext(), null);

		assertTrue("Number changed!",
				externFormulaStringBeforeInput.compareTo(internFormula.getExternFormulaString()) == 0);

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(6, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_0, getInstrumentation().getTargetContext(), null);
		assertTrue("Append number error", internTokens.get(0).getTokenStringValue().compareTo("42.420") == 0);

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(6, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				getInstrumentation().getTargetContext(), null);
		assertTrue("Append number error", internTokens.get(0).getTokenStringValue().compareTo("42.42") == 0);

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "4242"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(5, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				getInstrumentation().getTargetContext(), null);
		assertTrue("Append decimal mark error", internTokens.get(0).getTokenStringValue().compareTo("4242.") == 0);

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				getInstrumentation().getTargetContext(), null);
		assertTrue("Prepend decimal mark error", internTokens.get(0).getTokenStringValue().compareTo("0.") == 0);
	}

	public void testInsertOperatorInNumberToken() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1234"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(2, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_mult,
				getInstrumentation().getTargetContext(), null);

		assertTrue("Insert operator in number token error", internTokens.get(0).getTokenStringValue().compareTo("12") == 0);
		assertTrue("Insert operator in number token error", internTokens.get(1).getTokenStringValue().compareTo("MULT") == 0);
		assertTrue("Insert operator in number token error", internTokens.get(2).getTokenStringValue().compareTo("34") == 0);
	}

	public void testReplaceFunctionByToken() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
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
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();
		int doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 9, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.id.formula_editor_keyboard_4, getInstrumentation().getTargetContext(), null);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_2, getInstrumentation().getTargetContext(), null);

		assertNull("Selection found but should not", internFormula.getSelection());

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 0, internFormula.getSelection().getEndIndex());
	}

	public void testReplaceFunctionButKeepParameters() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
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
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();
		int doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 9, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.string.formula_editor_function_rand, getInstrumentation().getTargetContext(),
				null);

		assertEquals("Selection start index not as expected", 2, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 8, internFormula.getSelection().getEndIndex());

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 11, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.string.formula_editor_function_sqrt, getInstrumentation().getTargetContext(),
				null);

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		assertEquals("Selection start index not as expected", 2, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 8, internFormula.getSelection().getEndIndex());

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 9, internFormula.getSelection().getEndIndex());
	}

	public void testSelectBrackets() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();

		int doubleClickIndex = externFormulaString.length();
		int offsetRight = 0;
		while (offsetRight < 3) {
			internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);

			assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
			assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());
			offsetRight++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);
		assertEquals("Selection start index not as expected", 1, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());

		doubleClickIndex = 0;
		int offsetLeft = 0;

		while (offsetLeft < 2) {
			internFormula.setCursorAndSelection(doubleClickIndex + offsetLeft, true);

			assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
			assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());
			offsetLeft++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex + offsetLeft, true);
		assertEquals("Selection start index not as expected", 1, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());
	}

	public void testSelectFunctionAndSingleTab() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();

		internFormula.setCursorAndSelection(0, false);
		assertNull("Single Tab before Funtion fail", internFormula.getSelection());

		int doubleClickIndex = externFormulaString.length();
		int offsetRight = 0;
		while (offsetRight < 3) {
			internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);

			assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
			assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());
			offsetRight++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);
		assertEquals("Selection start index not as expected", 4, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());

		doubleClickIndex = 0;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex = getInstrumentation().getTargetContext().getString(R.string.formula_editor_function_rand)
				.length();

		int singleClickIndex = doubleClickIndex;

		internFormula.setCursorAndSelection(singleClickIndex, false);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex += " 42.42 ".length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals("Selection start index not as expected", 4, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());
	}

	public void testReplaceSelection() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());

		internFormula.setCursorAndSelection(1, true);
		String externFormulaString = internFormula.getExternFormulaString();

		int tokenSelectionStartIndex = -1;
		int tokenSelectionEndIndex = 3;

		InternFormulaTokenSelection internFormulaTokenSelection = new InternFormulaTokenSelection(
				InternFormula.TokenSelectionType.USER_SELECTION, tokenSelectionStartIndex, tokenSelectionEndIndex);
		Reflection.setPrivateField(internFormula, "internFormulaTokenSelection", internFormulaTokenSelection);

		internFormula.handleKeyInput(R.id.formula_editor_keyboard_0, getInstrumentation().getTargetContext(), null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		assertTrue("ExternFormulaString changed on buggy input!",
				internFormula.getExternFormulaString().compareTo(externFormulaString) == 0);
	}

	public void testHandleDeletion() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());

		internFormula.setCursorAndSelection(0, false);
		String externFormulaString = internFormula.getExternFormulaString();

		internFormula.handleKeyInput(R.id.formula_editor_edit_field_clear, getInstrumentation().getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		assertTrue("ExternFormulaString changed on buggy input!",
				internFormula.getExternFormulaString().compareTo(externFormulaString) == 0);
	}

	public void testDeleteInternTokenByIndex() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		String externFormulaString = internFormula.getExternFormulaString();
		Reflection.setPrivateField(internFormula, "externCursorPosition", -1);
		internFormula.handleKeyInput(R.id.formula_editor_edit_field_clear, getInstrumentation().getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		assertTrue("ExternFormulaString changed on buggy input!",
				internFormula.getExternFormulaString().compareTo(externFormulaString) == 0);
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_edit_field_clear, getInstrumentation().getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		assertTrue("ExternFormulaString changed on buggy input!",
				internFormula.getExternFormulaString().compareTo(externFormulaString) == 0);
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(
				getInstrumentation().getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 1, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_edit_field_clear, getInstrumentation().getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		assertTrue("ExternFormulaString changed on buggy input!",
				internFormula.getExternFormulaString().compareTo(externFormulaString) == 0);
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(
				getInstrumentation().getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 2, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_edit_field_clear, getInstrumentation().getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		assertTrue("ExternFormulaString changed on buggy input!",
				internFormula.getExternFormulaString().compareTo(externFormulaString) == 0);
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(
				getInstrumentation().getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 2, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_edit_field_clear, getInstrumentation().getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		assertTrue("ExternFormulaString changed on buggy input!",
				internFormula.getExternFormulaString().compareTo(externFormulaString) == 0);
		internTokens.clear();
	}

	public void testSetExternCursorPositionLeftTo() throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Method setExternCursorPositionLeftTo = InternFormula.class.getDeclaredMethod("setExternCursorPositionLeftTo",
				int.class);
		setExternCursorPositionLeftTo.setAccessible(true);

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		ExternInternRepresentationMapping externInternRepresentationMapping = new ExternInternRepresentationMapping();

		Reflection.setPrivateField(internFormula, "externInternRepresentationMapping",
				externInternRepresentationMapping);

		int externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		setExternCursorPositionLeftTo.invoke(internFormula, 1);

		assertEquals("Extern cursor position changed!", externCursorPositionBeforeMethodCall,
				internFormula.getExternCursorPosition());
	}

	public void testSetExternCursorPositionRightTo() throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Method setExternCursorPositionRightTo = InternFormula.class.getDeclaredMethod("setExternCursorPositionRightTo",
				int.class);
		setExternCursorPositionRightTo.setAccessible(true);

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		int externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		setExternCursorPositionRightTo.invoke(internFormula, 1);

		assertEquals("Extern cursor position changed!", externCursorPositionBeforeMethodCall,
				internFormula.getExternCursorPosition());

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		setExternCursorPositionRightTo.invoke(internFormula, 3);

		assertEquals("Extern cursor position changed!", 13, internFormula.getExternCursorPosition());

		ExternInternRepresentationMapping externInternRepresentationMapping = new ExternInternRepresentationMapping();

		Reflection.setPrivateField(internFormula, "externInternRepresentationMapping",
				externInternRepresentationMapping);

		externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		setExternCursorPositionRightTo.invoke(internFormula, 1);

		assertEquals("Extern cursor position changed!", externCursorPositionBeforeMethodCall,
				internFormula.getExternCursorPosition());
	}

	public void testSelectCursorPositionInternTokenOnError() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);

		Method method = InternFormula.class.getDeclaredMethod("selectCursorPositionInternToken",
				TokenSelectionType.class);
		method.setAccessible(true);

		Object[] arguments = new Object[1];
		arguments[0] = InternFormula.TokenSelectionType.USER_SELECTION;

		Reflection.setPrivateField(internFormula, "cursorPositionInternToken", null);
		method.invoke(internFormula, arguments);

		assertNull("Selection changed!", Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));
	}

	public void testSelectCursorPositionInternToken() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(0, true);
		assertNull("Selection changed!", Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(0, true);
		assertNull("Selection changed!", Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(
				getInstrumentation().getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 4, true);
		assertNull("Selection changed!", Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(
				getInstrumentation().getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 2, true);
		assertNull("Selection changed!", Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(
				getInstrumentation().getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length(), true);
		assertNull("Selection changed!", Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		assertNull("Selection changed!", Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));
	}

	public void testreplaceCursorPositionInternTokenByTokenList() throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Method method = InternFormula.class
				.getDeclaredMethod("replaceCursorPositionInternTokenByTokenList", List.class);
		method.setAccessible(true);

		List<InternToken> tokensToReplaceWith = new ArrayList<InternToken>();
		tokensToReplaceWith.add(new InternToken(InternTokenType.NUMBER, "1"));

		Object[] arguments = new Object[1];
		arguments[0] = tokensToReplaceWith;

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, true);

		Reflection.setPrivateField(internFormula, "cursorPositionInternTokenIndex", -1);

		assertEquals("Do not modify on error", CursorTokenPropertiesAfterModification.DO_NOT_MODIFY,
				method.invoke(internFormula, arguments));

		tokensToReplaceWith = new ArrayList<InternToken>();
		tokensToReplaceWith.add(new InternToken(InternTokenType.PERIOD));
		arguments = new Object[1];
		arguments[0] = tokensToReplaceWith;

		assertEquals("Do not modify on when second period token is inserted",
				CursorTokenPropertiesAfterModification.DO_NOT_MODIFY, method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "4242"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		Reflection.setPrivateField(internFormula, "cursorPositionInternTokenIndex", -1);

		assertEquals("Do not modify on error", CursorTokenPropertiesAfterModification.DO_NOT_MODIFY,
				method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		assertEquals("Do not modify on error", CursorTokenPropertiesAfterModification.DO_NOT_MODIFY,
				method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_BRIGHTNESS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		assertEquals("Do not modify on error", CursorTokenPropertiesAfterModification.RIGHT,
				method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_BRIGHTNESS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		tokensToReplaceWith = new ArrayList<InternToken>();
		tokensToReplaceWith.add(new InternToken(InternTokenType.FUNCTION_NAME));
		arguments = new Object[1];
		arguments[0] = tokensToReplaceWith;

		assertEquals("Do not modify on error", CursorTokenPropertiesAfterModification.RIGHT,
				method.invoke(internFormula, arguments));
	}
}
