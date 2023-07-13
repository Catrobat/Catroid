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

import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class NoSelectionInternTokenTest {
	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Select Bracket OPEN", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						0},
				{"Select Bracket CLOSE", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						0},
				{"Select Bracket CLOSE SIN", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						ApplicationProvider.getApplicationContext().getResources().getString(R.string.formula_editor_function_sin)
								.length() + 4},
				{"Select SIN Bracket OPEN", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
						new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						ApplicationProvider.getApplicationContext().getResources().getString(R.string.formula_editor_function_sin)
								.length() + 2},
				{"Select SIN name end", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						ApplicationProvider.getApplicationContext().getResources().getString(R.string.formula_editor_function_sin)
								.length()},
				{"Select SIN name begin", new InternToken[]{
						new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()),
						new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, Operators.PLUS.name()),
						new InternToken(InternTokenType.NUMBER, "42.42")},
						1},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public InternToken[] initialTokens;

	@Parameterized.Parameter(2)
	public int externCursorPosition;

	private InternFormula internFormula;

	@Before
	public void setUp() {
		ArrayList<InternToken> internTokens = new ArrayList<>(Arrays.asList(initialTokens));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext());
		internFormula.setCursorAndSelection(externCursorPosition, true);
	}

	@Test
	public void testExternFormulaString() {
		assertNull(internFormula.internFormulaTokenSelection);
	}
}

