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

import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.formulaeditor.InternFormulaState;
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;

import java.util.ArrayList;
import java.util.List;

public class InternFormulaStateTest extends InstrumentationTestCase {

	private InternFormulaState internState;
	private InternFormulaState internStateToCompare;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		List<InternToken> internTokenList = new ArrayList<InternToken>();
		InternFormulaTokenSelection internTokenSelection = new InternFormulaTokenSelection(
				TokenSelectionType.USER_SELECTION, 0, 1);
		internState = new InternFormulaState(internTokenList, null, 0);
		internStateToCompare = new InternFormulaState(internTokenList, internTokenSelection, 0);

		assertFalse("TokenSelection is different", internState.equals(internStateToCompare));

		internTokenList = new ArrayList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.NUMBER));
		internState = new InternFormulaState(internTokenList, null, 0);
		internTokenList = new ArrayList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internStateToCompare = new InternFormulaState(internTokenList, null, 0);
	}

	public void testEquals() {
		assertFalse("Token List ist different", internState.equals(internStateToCompare));
		assertFalse("Object to compare is not instance of InternFormulaState", internState.equals(Integer.valueOf(1)));
	}

	public void testHashCode() {
		int hashCodeInternalState = internState.hashCode();
		int hashCodeInternStateToCompare = internStateToCompare.hashCode();
		assertFalse("HashCodes are the same", hashCodeInternalState == hashCodeInternStateToCompare);
		InternFormulaState internalState2 = internState;
		assertTrue("HashCodes are different", internalState2.hashCode() == internState.hashCode());
		assertFalse("HashCode function fail", internState.hashCode() == 1);
	}
}
