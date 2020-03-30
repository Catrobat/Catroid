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

import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.formulaeditor.InternFormulaState;
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class InternFormulaStateTest {

	private InternFormulaState internState;
	private InternFormulaState internStateToCompare;

	@Before
	public void setUp() throws Exception {

		List<InternToken> internTokenList = new ArrayList<>();
		InternFormulaTokenSelection internTokenSelection = new InternFormulaTokenSelection(
				TokenSelectionType.USER_SELECTION, 0, 1);
		internState = new InternFormulaState(internTokenList, null, 0);
		internStateToCompare = new InternFormulaState(internTokenList, internTokenSelection, 0);

		assertFalse(internState.equals(internStateToCompare));

		internTokenList = new ArrayList<>();
		internTokenList.add(new InternToken(InternTokenType.NUMBER));
		internState = new InternFormulaState(internTokenList, null, 0);
		internTokenList = new ArrayList<>();
		internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME));
		internStateToCompare = new InternFormulaState(internTokenList, null, 0);
	}

	@Test
	public void testEquals() {
		assertThat(internState, not(equalTo(internStateToCompare)));
		assertFalse(internState.equals(Integer.valueOf(1)));
	}

	@Test
	public void testHashCode() {
		int hashCodeInternalState = internState.hashCode();
		int hashCodeInternStateToCompare = internStateToCompare.hashCode();
		assertThat(hashCodeInternalState, not(equalTo(hashCodeInternStateToCompare)));

		InternFormulaState internalState2 = internState;
		assertEquals(internState.hashCode(), internalState2.hashCode());
		assertThat(internState.hashCode(), not(equalTo(1)));
	}
}
