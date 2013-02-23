/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.util.LinkedList;
import java.util.List;

public class InternFormulaState {

	private List<InternToken> internTokenFormulaList;
	private InternFormulaTokenSelection tokenSelection;
	private int externCursorPosition;

	public InternFormulaState(List<InternToken> internTokenFormulaList, InternFormulaTokenSelection tokenSelection,
			int externCursorPosition) {
		this.internTokenFormulaList = internTokenFormulaList;
		this.tokenSelection = tokenSelection;
		this.externCursorPosition = externCursorPosition;
	}

	@Override
	public boolean equals(Object objectToCompare) {

		if (objectToCompare instanceof InternFormulaState) {
			InternFormulaState stateToCompare = (InternFormulaState) objectToCompare;
			if (externCursorPosition != stateToCompare.externCursorPosition) {
				return false;
			}

			if (tokenSelection == null && stateToCompare.tokenSelection != null) {
				return false;
			}

			if (tokenSelection != null && !tokenSelection.equals(stateToCompare.tokenSelection)) {
				return false;
			}

			if (internTokenFormulaList.size() != stateToCompare.internTokenFormulaList.size()) {
				return false;
			}

			for (int index = 0; index < internTokenFormulaList.size(); index++) {
				InternToken original = internTokenFormulaList.get(index);
				InternToken internTokenToCompare = stateToCompare.internTokenFormulaList.get(index);

				if (original.getInternTokenType() != internTokenToCompare.getInternTokenType()) {
					return false;
				}
				if (!original.getTokenSringValue().equals(internTokenToCompare.getTokenSringValue())) {
					return false;
				}

			}

			return true;
		}

		return super.equals(objectToCompare);
	}

	public void setSelection(InternFormulaTokenSelection internFormulaTokenSelection) {
		this.tokenSelection = internFormulaTokenSelection;

	}

	public void setExternCursorPosition(int externCursorPosition) {
		this.externCursorPosition = externCursorPosition;
	}

	public InternFormula createInternFormulaFromState() {
		List<InternToken> deepCopyOfInternTokenFormula = new LinkedList<InternToken>();
		InternFormulaTokenSelection deepCopyOfInternFormulaTokenSelection = null;

		for (InternToken tokenToCopy : internTokenFormulaList) {
			deepCopyOfInternTokenFormula.add(tokenToCopy.deepCopy());
		}

		if (tokenSelection != null) {
			deepCopyOfInternFormulaTokenSelection = tokenSelection.deepCopy();
		}

		return new InternFormula(deepCopyOfInternTokenFormula, deepCopyOfInternFormulaTokenSelection,
				externCursorPosition);
	}

}
