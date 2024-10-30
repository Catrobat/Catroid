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

import android.content.Context;

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class InternFormulaTokenSelectionTest {

	private InternFormula internFormula;

	@Before
	public void setUp() throws Exception {

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));
		int doubleClickIndex = internFormula.getExternFormulaString().length();
		internFormula.setCursorAndSelection(doubleClickIndex, true);
	}

	@Test
	public void testReplaceFunctionByToken() throws Exception {
		assertEquals(0, internFormula.getSelection().getStartIndex());
		assertEquals(3, internFormula.getSelection().getEndIndex());

		InternFormulaTokenSelection tokenSelection = internFormula.getSelection();
		InternFormulaTokenSelection tokenSelectionDeepCopy = tokenSelection.deepCopy();

		assertEquals(tokenSelection, tokenSelectionDeepCopy);

		Reflection.setPrivateField(tokenSelectionDeepCopy, "tokenSelectionType",
				TokenSelectionType.PARSER_ERROR_SELECTION);
		assertNotEquals(tokenSelection, tokenSelectionDeepCopy);

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionStart", -1);
		assertNotEquals(tokenSelection, tokenSelectionDeepCopy);

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionEnd", -1);
		assertNotEquals(tokenSelection, tokenSelectionDeepCopy);

		assertNotEquals(1, tokenSelectionDeepCopy);
	}

	@Test
	public void testHashCodeFunction() throws Exception {
		InternFormulaTokenSelection tokenSelection = internFormula.getSelection();
		InternFormulaTokenSelection tokenSelectionDeepCopy = tokenSelection.deepCopy();

		assertEquals(tokenSelection.hashCode(), tokenSelectionDeepCopy.hashCode());

		Reflection.setPrivateField(tokenSelectionDeepCopy, "tokenSelectionType",
				TokenSelectionType.PARSER_ERROR_SELECTION);

		assertNotEquals(tokenSelection.hashCode(), tokenSelectionDeepCopy.hashCode());

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionStart", -1);
		assertNotEquals(tokenSelection.hashCode(), tokenSelectionDeepCopy.hashCode());

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionEnd", -1);
		assertNotEquals(tokenSelection.hashCode(), tokenSelectionDeepCopy.hashCode());

		assertNotEquals(1, tokenSelectionDeepCopy.hashCode());
	}
}
