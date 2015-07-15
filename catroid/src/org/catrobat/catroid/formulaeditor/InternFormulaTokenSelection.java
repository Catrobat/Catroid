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
package org.catrobat.catroid.formulaeditor;

import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;

public class InternFormulaTokenSelection {

	private int internTokenSelectionStart;
	private int internTokenSelectionEnd;
	private TokenSelectionType tokenSelectionType;

	public InternFormulaTokenSelection(TokenSelectionType tokenSelectionType, int internTokenSelectionStart,
			int internTokenSelectionEnd) {
		this.tokenSelectionType = tokenSelectionType;
		this.internTokenSelectionStart = internTokenSelectionStart;
		this.internTokenSelectionEnd = internTokenSelectionEnd;
	}

	public int getStartIndex() {
		return internTokenSelectionStart;
	}

	public int getEndIndex() {
		return internTokenSelectionEnd;
	}

	public TokenSelectionType getTokenSelectionType() {
		return tokenSelectionType;
	}

	@Override
	public boolean equals(Object objectToCompare) {

		if (objectToCompare instanceof InternFormulaTokenSelection) {
			InternFormulaTokenSelection selectionToCompare = (InternFormulaTokenSelection) objectToCompare;
			if (internTokenSelectionStart != selectionToCompare.internTokenSelectionStart
					|| internTokenSelectionEnd != selectionToCompare.internTokenSelectionEnd
					|| tokenSelectionType != selectionToCompare.tokenSelectionType) {
				return false;
			}
			return true;
		}
		return super.equals(objectToCompare);
	}

	@Override
	public int hashCode() {
		int result = 31;
		int prime = 41;

		result = prime * result + internTokenSelectionStart;
		result = prime * result + internTokenSelectionEnd;
		if (tokenSelectionType != null) {
			result = prime * result + tokenSelectionType.hashCode();
		}

		return result;
	}

	public InternFormulaTokenSelection deepCopy() {

		return new InternFormulaTokenSelection(tokenSelectionType, internTokenSelectionStart, internTokenSelectionEnd);
	}
}
