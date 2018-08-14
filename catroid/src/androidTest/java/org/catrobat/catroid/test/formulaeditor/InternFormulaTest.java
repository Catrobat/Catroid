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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class InternFormulaTest {

	@Test
	public void testInsertRightToCurrentToken() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				InstrumentationRegistry.getTargetContext(), null);

		assertEquals("0.", internTokens.get(0).getTokenStringValue());

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				InstrumentationRegistry.getTargetContext(), null);

		assertEquals("0.", internTokens.get(1).getTokenStringValue());

		internTokens = new ArrayList<InternToken>();
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				InstrumentationRegistry.getTargetContext(), null);

		assertEquals("0.", internTokens.get(0).getTokenStringValue());
	}

	@Test
	public void testInsertLeftToCurrentToken() {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		String externFormulaStringBeforeInput = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				InstrumentationRegistry.getTargetContext(), null);

		assertEquals(externFormulaStringBeforeInput, internFormula.getExternFormulaString());

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(6, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_0, InstrumentationRegistry.getInstrumentation().getTargetContext(), null);
		assertEquals("42.420", internTokens.get(0).getTokenStringValue());

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(6, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				InstrumentationRegistry.getTargetContext(), null);
		assertEquals("42.42", internTokens.get(0).getTokenStringValue());

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "4242"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(5, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				InstrumentationRegistry.getTargetContext(), null);
		assertEquals("4242.", internTokens.get(0).getTokenStringValue());

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(0, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_decimal_mark,
				InstrumentationRegistry.getTargetContext(), null);
		assertEquals("0.", internTokens.get(0).getTokenStringValue());
	}

	@Test
	public void testInsertOperatorInNumberToken() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "1234"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(2, false);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_mult,
				InstrumentationRegistry.getTargetContext(), null);

		assertEquals("12", internTokens.get(0).getTokenStringValue());
		assertEquals("MULT", internTokens.get(1).getTokenStringValue());
		assertEquals("34", internTokens.get(2).getTokenStringValue());
	}

	@Test
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
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();
		int doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(9, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.id.formula_editor_keyboard_4, InstrumentationRegistry.getTargetContext(), null);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_2, InstrumentationRegistry.getTargetContext(), null);

		assertNull(internFormula.getSelection());

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(0, internFormula.getSelection().getEndIndex());
	}

	@Test
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
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();
		int doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(9, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.string.formula_editor_function_rand, InstrumentationRegistry.getTargetContext(),
				null);

		assertEquals(2, internFormula.getSelection().getStartIndex());
		assertEquals(8, internFormula.getSelection().getEndIndex());

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(11, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.string.formula_editor_function_sqrt, InstrumentationRegistry.getTargetContext(),
				null);

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		assertEquals(2, internFormula.getSelection().getStartIndex());
		assertEquals(8, internFormula.getSelection().getEndIndex());

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(9, internFormula.getSelection().getEndIndex());
	}

	@Test
	public void testSelectBrackets() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();

		int doubleClickIndex = externFormulaString.length();
		int offsetRight = 0;
		while (offsetRight < 3) {
			internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);

			assertEquals(0, internFormula.getSelection().getStartIndex());
			assertEquals(5, internFormula.getSelection().getEndIndex());
			offsetRight++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);
		assertEquals(1, internFormula.getSelection().getStartIndex());
		assertEquals(4, internFormula.getSelection().getEndIndex());

		doubleClickIndex = 0;
		int offsetLeft = 0;

		while (offsetLeft < 2) {
			internFormula.setCursorAndSelection(doubleClickIndex + offsetLeft, true);

			assertEquals(0, internFormula.getSelection().getStartIndex());
			assertEquals(5, internFormula.getSelection().getEndIndex());
			offsetLeft++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex + offsetLeft, true);
		assertEquals(1, internFormula.getSelection().getStartIndex());
		assertEquals(4, internFormula.getSelection().getEndIndex());
	}

	@Test
	public void testSelectFunctionAndSingleTab() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();

		internFormula.setCursorAndSelection(0, false);
		assertNull(internFormula.getSelection());

		int doubleClickIndex = externFormulaString.length();
		int offsetRight = 0;
		while (offsetRight < 3) {
			internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);

			assertEquals(0, internFormula.getSelection().getStartIndex());
			assertEquals(5, internFormula.getSelection().getEndIndex());
			offsetRight++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, true);
		assertEquals(4, internFormula.getSelection().getStartIndex());
		assertEquals(4, internFormula.getSelection().getEndIndex());

		doubleClickIndex = 0;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(5, internFormula.getSelection().getEndIndex());

		doubleClickIndex = InstrumentationRegistry.getTargetContext().getString(R.string.formula_editor_function_rand)
				.length();

		int singleClickIndex = doubleClickIndex;

		internFormula.setCursorAndSelection(singleClickIndex, false);
		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(5, internFormula.getSelection().getEndIndex());

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(5, internFormula.getSelection().getEndIndex());

		doubleClickIndex += " 42.42 ".length();

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, true);
		assertEquals(4, internFormula.getSelection().getStartIndex());
		assertEquals(4, internFormula.getSelection().getEndIndex());
	}

	@Test
	public void testReplaceSelection() throws Exception {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());

		internFormula.setCursorAndSelection(1, true);
		String externFormulaString = internFormula.getExternFormulaString();

		int tokenSelectionStartIndex = -1;
		int tokenSelectionEndIndex = 3;

		InternFormulaTokenSelection internFormulaTokenSelection = new InternFormulaTokenSelection(
				InternFormula.TokenSelectionType.USER_SELECTION, tokenSelectionStartIndex, tokenSelectionEndIndex);
		Reflection.setPrivateField(internFormula, "internFormulaTokenSelection", internFormulaTokenSelection);

		internFormula.handleKeyInput(R.id.formula_editor_keyboard_0, InstrumentationRegistry.getTargetContext(), null);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
	}

	@Test
	public void testHandleDeletion() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());

		internFormula.setCursorAndSelection(0, false);
		String externFormulaString = internFormula.getExternFormulaString();

		internFormula.handleKeyInput(R.id.formula_editor_keyboard_delete, InstrumentationRegistry.getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
	}

	@Test
	public void testDeleteInternTokenByIndex() throws Exception {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		String externFormulaString = internFormula.getExternFormulaString();
		Reflection.setPrivateField(internFormula, "externCursorPosition", -1);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_delete, InstrumentationRegistry.getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_delete, InstrumentationRegistry.getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(
				InstrumentationRegistry.getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 1, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_delete, InstrumentationRegistry.getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(
				InstrumentationRegistry.getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 2, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_delete, InstrumentationRegistry.getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
		internTokens.clear();

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(
				InstrumentationRegistry.getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 2, false);
		externFormulaString = internFormula.getExternFormulaString();
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_delete, InstrumentationRegistry.getTargetContext(),
				null);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		assertEquals(externFormulaString, internFormula.getExternFormulaString());
		internTokens.clear();
	}

	@Test
	public void testSetExternCursorPositionLeftTo() throws Exception {

		Method setExternCursorPositionLeftTo = InternFormula.class.getDeclaredMethod("setExternCursorPositionLeftTo",
				int.class);
		setExternCursorPositionLeftTo.setAccessible(true);

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		ExternInternRepresentationMapping externInternRepresentationMapping = new ExternInternRepresentationMapping();

		Reflection.setPrivateField(internFormula, "externInternRepresentationMapping",
				externInternRepresentationMapping);

		int externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		setExternCursorPositionLeftTo.invoke(internFormula, 1);

		assertEquals(externCursorPositionBeforeMethodCall, internFormula.getExternCursorPosition());
	}

	@Test
	public void testSetExternCursorPositionRightTo() throws Exception {

		Method setExternCursorPositionRightTo = InternFormula.class.getDeclaredMethod("setExternCursorPositionRightTo",
				int.class);
		setExternCursorPositionRightTo.setAccessible(true);

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		int externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		setExternCursorPositionRightTo.invoke(internFormula, 1);

		assertEquals(externCursorPositionBeforeMethodCall, internFormula.getExternCursorPosition());

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name()));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		setExternCursorPositionRightTo.invoke(internFormula, 3);

		assertEquals(13, internFormula.getExternCursorPosition());

		ExternInternRepresentationMapping externInternRepresentationMapping = new ExternInternRepresentationMapping();

		Reflection.setPrivateField(internFormula, "externInternRepresentationMapping",
				externInternRepresentationMapping);

		externCursorPositionBeforeMethodCall = internFormula.getExternCursorPosition();
		setExternCursorPositionRightTo.invoke(internFormula, 1);

		assertEquals(externCursorPositionBeforeMethodCall, internFormula.getExternCursorPosition());
	}

	@Test
	public void testSelectCursorPositionInternTokenOnError() throws Exception {

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

		assertNull(Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));
	}

	@Test
	public void testSelectCursorPositionInternToken() throws Exception {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(0, true);
		assertNull(Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(0, true);
		assertNull(Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(
				InstrumentationRegistry.getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 4, true);
		assertNull(Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(
				InstrumentationRegistry.getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length() + 2, true);
		assertNull(Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(
				InstrumentationRegistry.getTargetContext().getResources().getString(R.string.formula_editor_function_sin)
						.length(), true);
		assertNull(Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		assertNull(Reflection.getPrivateField(internFormula, "internFormulaTokenSelection"));
	}

	@Test
	public void testreplaceCursorPositionInternTokenByTokenList() throws Exception {

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
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, true);

		Reflection.setPrivateField(internFormula, "cursorPositionInternTokenIndex", -1);

		assertEquals(CursorTokenPropertiesAfterModification.DO_NOT_MODIFY, method.invoke(internFormula, arguments));

		tokensToReplaceWith = new ArrayList<InternToken>();
		tokensToReplaceWith.add(new InternToken(InternTokenType.PERIOD));
		arguments = new Object[1];
		arguments[0] = tokensToReplaceWith;

		assertEquals(CursorTokenPropertiesAfterModification.DO_NOT_MODIFY, method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.NUMBER, "4242"));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);
		Reflection.setPrivateField(internFormula, "cursorPositionInternTokenIndex", -1);

		assertEquals(CursorTokenPropertiesAfterModification.DO_NOT_MODIFY, method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		assertEquals(CursorTokenPropertiesAfterModification.DO_NOT_MODIFY, method.invoke(internFormula, arguments));

		internTokens = new ArrayList<>();
		internTokens.add(new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_COLOR.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		assertEquals(CursorTokenPropertiesAfterModification.RIGHT, method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_BRIGHTNESS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		assertEquals(CursorTokenPropertiesAfterModification.RIGHT, method.invoke(internFormula, arguments));

		internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_BRIGHTNESS.name()));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(InstrumentationRegistry.getTargetContext());
		internFormula.setCursorAndSelection(1, false);

		tokensToReplaceWith = new ArrayList<InternToken>();
		tokensToReplaceWith.add(new InternToken(InternTokenType.FUNCTION_NAME));
		arguments = new Object[1];
		arguments[0] = tokensToReplaceWith;

		assertEquals(CursorTokenPropertiesAfterModification.RIGHT, method.invoke(internFormula, arguments));
	}
}
