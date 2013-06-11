/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.pocketcode.test.formulaeditor;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.pocketcode.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.pocketcode.formulaeditor.InternFormulaState;
import org.catrobat.catroid.pocketcode.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.pocketcode.formulaeditor.InternToken;
import org.catrobat.catroid.pocketcode.formulaeditor.InternTokenType;

import android.test.InstrumentationTestCase;

public class InternFormulaStateTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEquals() {

		List<InternToken> internTokenList = new ArrayList<InternToken>();
		InternFormulaTokenSelection internTokenSelection = new InternFormulaTokenSelection(
				TokenSelectionType.USER_SELECTION, 0, 1);

		InternFormulaState internState = new InternFormulaState(internTokenList, null, 0);
		InternFormulaState internStateToCompare = new InternFormulaState(internTokenList, internTokenSelection, 0);

		assertFalse("TokenSelection is different", internState.equals(internStateToCompare));

		internTokenList = new ArrayList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.NUMBER));
		internState = new InternFormulaState(internTokenList, null, 0);
		internTokenList = new ArrayList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internStateToCompare = new InternFormulaState(internTokenList, null, 0);

		assertFalse("Token List ist different", internState.equals(internStateToCompare));
		assertFalse("Object to compare is not instance of InternFormulaState", internState.equals(Integer.valueOf(1)));
	}
}
