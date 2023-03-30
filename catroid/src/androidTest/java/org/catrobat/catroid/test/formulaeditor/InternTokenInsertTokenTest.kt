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
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.IdRes;
import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class InternTokenInsertTokenTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Formula: .+", new InternToken[]{new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name())},
						0, R.id.formula_editor_keyboard_decimal_mark, 0, "0.", "0. + "},
				{"Formula: +.", new InternToken[]{new InternToken(InternTokenType.OPERATOR, Operators.PLUS.name())},
						1, R.id.formula_editor_keyboard_decimal_mark, 1, "0.", "+ 0. "},
				{"Formula: .", new InternToken[]{},
						0, R.id.formula_editor_keyboard_decimal_mark, 0, "0.", "0. "},
				{"Formula: 42.42", new InternToken[]{new InternToken(InternTokenType.NUMBER, "42.42")},
						0, R.id.formula_editor_keyboard_decimal_mark, 0, "42.42", "42.42 "},
				{"Formula: 42.42 0", new InternToken[]{new InternToken(InternTokenType.NUMBER, "42.42")},
						6, R.id.formula_editor_keyboard_0, 0, "42.420", "42.420 "},
				{"Formula: 42.42 .", new InternToken[]{new InternToken(InternTokenType.NUMBER, "42.42")},
						6, R.id.formula_editor_keyboard_decimal_mark, 0, "42.42", "42.42 "},
				{"Formula: 4242 .", new InternToken[]{new InternToken(InternTokenType.NUMBER, "4242")},
						5, R.id.formula_editor_keyboard_decimal_mark, 0, "4242.", "4242. "},
				{"Formula Token(0): 12 MULT 34", new InternToken[]{new InternToken(InternTokenType.NUMBER, "1234")},
						2, R.id.formula_editor_keyboard_mult, 0, "12", "12 × 34 "},
				{"Formula Token(1): 34 MULT 56", new InternToken[]{new InternToken(InternTokenType.NUMBER, "3456")},
						2, R.id.formula_editor_keyboard_mult, 1, "MULT", "34 × 56 "},
				{"Formula Token(2): 78 MULT 90 .", new InternToken[]{new InternToken(InternTokenType.NUMBER, "7890")},
						2, R.id.formula_editor_keyboard_mult, 2, "90", "78 × 90 "},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public InternToken[] initialTokens;

	@Parameterized.Parameter(2)
	public int externCursorPosition;

	@Parameterized.Parameter(3)
	@IdRes
	public int keyInputId;

	@Parameterized.Parameter(4)
	public int tokenPosition;

	@Parameterized.Parameter(5)
	public String expectedStringTokenValue;

	@Parameterized.Parameter(6)
	public String expectedFormulaString;

	private InternFormula internFormula;
	private ArrayList<InternToken> internTokens;

	@Before
	public void setUp() {
		internTokens = new ArrayList<>(Arrays.asList(initialTokens));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext());
		internFormula.setCursorAndSelection(externCursorPosition, false);
		internFormula.handleKeyInput(keyInputId, ApplicationProvider.getApplicationContext(), null);
	}

	@Test
	public void testTokenStringValueFormulaString() {
		assertEquals(expectedStringTokenValue, internTokens.get(tokenPosition).getTokenStringValue());
		assertEquals(expectedFormulaString, internFormula.getExternFormulaString());
	}
}
