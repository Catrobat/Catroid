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
package org.catrobat.catroid.formulaeditor;

import com.google.common.base.Objects;

import org.catrobat.catroid.content.bricks.Brick;

public class UndoState {

	public final InternFormulaState internFormulaState;
	public final Brick.BrickField brickField;

	public UndoState(InternFormulaState internFormulaState, Brick.BrickField brickField) {
		this.brickField = brickField;
		this.internFormulaState = internFormulaState;
	}

	@Override
	public boolean equals(Object objectCompareTo) {
		if (this == objectCompareTo) {
			return true;
		}
		if (!(objectCompareTo instanceof UndoState)) {
			return false;
		}
		UndoState stateCompareTo = (UndoState) objectCompareTo;
		return Objects.equal(this.internFormulaState, stateCompareTo.internFormulaState)
				&& this.brickField == stateCompareTo.brickField;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(internFormulaState, brickField);
	}
}
