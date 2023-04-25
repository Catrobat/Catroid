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
import org.catrobat.catroid.formulaeditor.InternFormula.CursorTokenPropertiesAfterModification;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ReplaceCursorPositionInternTokenByTokenListTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Replace number with the same", new InternToken[]{new InternToken(InternTokenType.NUMBER, "42.42")},
						new InternToken[]{new InternToken(InternTokenType.NUMBER, "42.42")},
						CursorTokenPropertiesAfterModification.DO_NOT_MODIFY},
				{"Replace Float number with period", new InternToken[]{new InternToken(InternTokenType.NUMBER, "42.42")},
						new InternToken[]{new InternToken(InternTokenType.PERIOD)},
						CursorTokenPropertiesAfterModification.DO_NOT_MODIFY},
				{"Replace Integer number with period", new InternToken[]{new InternToken(InternTokenType.NUMBER, "4242")},
						new InternToken[]{new InternToken(InternTokenType.PERIOD)},
						CursorTokenPropertiesAfterModification.DO_NOT_MODIFY},
				{"Replace function SIN with period",
						new InternToken[]{new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()),
								new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN)},
						new InternToken[]{new InternToken(InternTokenType.PERIOD)},
						CursorTokenPropertiesAfterModification.DO_NOT_MODIFY},
				{"Replace sensor OBJECT_COLOR with period",
						new InternToken[]{new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_COLOR.name())},
						new InternToken[]{new InternToken(InternTokenType.PERIOD)},
						CursorTokenPropertiesAfterModification.RIGHT},
				{"Replace sensor OBJECT_BRIGHTNESS with period",
						new InternToken[]{new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_BRIGHTNESS.name())},
						new InternToken[]{new InternToken(InternTokenType.PERIOD)},
						CursorTokenPropertiesAfterModification.RIGHT},
				{"Replace sensor OBJECT_BRIGHTNESS with function",
						new InternToken[]{new InternToken(InternTokenType.SENSOR, Sensors.OBJECT_BRIGHTNESS.name())},
						new InternToken[]{new InternToken(InternTokenType.FUNCTION_NAME)},
						CursorTokenPropertiesAfterModification.RIGHT},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public InternToken[] initialTokens;

	@Parameterized.Parameter(2)
	public InternToken[] tokensToReplaceWith;

	@Parameterized.Parameter(3)
	public CursorTokenPropertiesAfterModification expectedModification;

	private InternFormula internFormula;

	@Before
	public void setUp() {
		ArrayList<InternToken> internTokens = new ArrayList<>(Arrays.asList(initialTokens));
		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(ApplicationProvider.getApplicationContext());
		internFormula.setCursorAndSelection(1, true);
	}

	@Test
	public void testReplaceInternTokenModification() {
		assertEquals(expectedModification, internFormula.replaceCursorPositionInternTokenByTokenList(Arrays.asList(tokensToReplaceWith)));
	}
}
