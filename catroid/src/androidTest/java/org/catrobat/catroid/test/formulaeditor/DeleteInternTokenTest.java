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
import org.catrobat.catroid.formulaeditor.Functions;
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

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DeleteInternTokenTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Invalid CursorPosition", new InternToken[]{
						new InternToken(InternTokenType.NUMBER, "42.42"),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						-1, "42.42( 42.42 "},
				{"Begin CursorPosition", new InternToken[]{
						new InternToken(InternTokenType.NUMBER, "42.42"),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						0, "42.42( 42.42 "},
				{"CursorPosition 1", new InternToken[]{
						new InternToken(InternTokenType.NUMBER, "42.42"),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						1, "2.42( 42.42 "},
				{"CursorPosition 8", new InternToken[]{
						new InternToken(InternTokenType.NUMBER, "42.42"),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						8, "42.42( 2.42 "},
				{"Invalid Deletion of Brace", new InternToken[]{
						new InternToken(InternTokenType.NUMBER, "42.42"),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						6, "42.42( 42.42 "},
				{"Invalid Sin Deletion Begin", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name())},
						1, "sine( "},
				{"Invalid Sin Deletion End", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						ApplicationProvider.getApplicationContext().getResources().getString(R.string.formula_editor_function_sin)
								.length(), "sine( 42.42 "},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public InternToken[] initialTokens;

	@Parameterized.Parameter(2)
	public int externCursorPosition;

	@Parameterized.Parameter(3)
	public String expectedFormulaString;

	private InternFormula internFormula;

	@Before
	public void setUp() {
		ArrayList<InternToken> internTokens = new ArrayList<>(Arrays.asList(initialTokens));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext());
		internFormula.setCursorAndSelection(0, false);
	}

	@Test
	public void testExternFormulaString() {
		internFormula.externCursorPosition = externCursorPosition;
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_delete, ApplicationProvider.getApplicationContext(), null);
		internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext());
		assertEquals(expectedFormulaString, internFormula.getExternFormulaString());
	}
}
