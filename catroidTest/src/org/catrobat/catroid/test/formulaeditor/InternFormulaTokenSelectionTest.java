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

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.test.utils.Reflection;

import java.util.ArrayList;

public class InternFormulaTokenSelectionTest extends InstrumentationTestCase {

	private InternFormula internFormula;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		int doubleClickIndex = internFormula.getExternFormulaString().length();
		internFormula.setCursorAndSelection(doubleClickIndex, true);
	}

	public void testReplaceFunctionByToken() {
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 3, internFormula.getSelection().getEndIndex());

		InternFormulaTokenSelection tokenSelection = internFormula.getSelection();
		InternFormulaTokenSelection tokenSelectionDeepCopy = tokenSelection.deepCopy();

		assertTrue("Deep copy of InternFormulaTokenSelection failed", tokenSelection.equals(tokenSelectionDeepCopy));

		Reflection.setPrivateField(tokenSelectionDeepCopy, "tokenSelectionType",
				TokenSelectionType.PARSER_ERROR_SELECTION);
		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(tokenSelection));

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionStart", -1);
		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(tokenSelection));

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionEnd", -1);
		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(tokenSelection));

		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(1));
	}

	public void testHashCodeFunction() {
		InternFormulaTokenSelection tokenSelection = internFormula.getSelection();
		InternFormulaTokenSelection tokenSelectionDeepCopy = tokenSelection.deepCopy();

		assertTrue("HashCode function not correct implemented",
				tokenSelection.hashCode() == tokenSelectionDeepCopy.hashCode());

		Reflection.setPrivateField(tokenSelectionDeepCopy, "tokenSelectionType",
				TokenSelectionType.PARSER_ERROR_SELECTION);
		assertFalse("HashCode function not correct implemented",
				tokenSelectionDeepCopy.hashCode() == tokenSelection.hashCode());

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionStart", -1);
		assertFalse("HashCode function not correct implemented",
				tokenSelectionDeepCopy.hashCode() == tokenSelection.hashCode());

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionEnd", -1);
		assertFalse("HashCode function not correct implemented",
				tokenSelectionDeepCopy.hashCode() == tokenSelection.hashCode());

		assertFalse("HashCode function not correct implemented", tokenSelectionDeepCopy.hashCode() == 1);
	}
}
