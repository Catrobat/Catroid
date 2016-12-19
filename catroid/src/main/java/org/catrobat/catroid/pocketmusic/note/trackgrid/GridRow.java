/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.pocketmusic.note.trackgrid;

import android.util.SparseArray;

import org.catrobat.catroid.pocketmusic.note.NoteName;

import java.util.List;

public class GridRow {

	private final NoteName noteName;
	private final SparseArray<List<GridRowPosition>> gridRowPositions;

	public GridRow(NoteName noteName, SparseArray<List<GridRowPosition>> gridRowPositions) {
		this.noteName = noteName;
		this.gridRowPositions = gridRowPositions;
	}

	public NoteName getNoteName() {
		return noteName;
	}

	public SparseArray<List<GridRowPosition>> getGridRowPositions() {
		return gridRowPositions;
	}

	@Override
	public int hashCode() {
		int hashCode = 21;
		int primeWithGoodCollisionPrevention = 31;
		hashCode = primeWithGoodCollisionPrevention * hashCode + noteName.hashCode();

		for (int i = 0; i < getGridRowPositions().size(); i++) {
			hashCode = hashCode + primeWithGoodCollisionPrevention * getGridRowPositions().valueAt(i).hashCode();
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GridRow)) {
			return false;
		}
		GridRow reference = (GridRow) o;

		if (!reference.noteName.equals(noteName)) {
			return false;
		}

		if (reference.getGridRowPositions().size() != getGridRowPositions().size()) {
			return false;
		}
		for (int i = 0; i < reference.getGridRowPositions().size(); i++) {
			if (reference.getGridRowPositions().keyAt(i) != getGridRowPositions().keyAt(i)) {
				return false;
			}
			if (reference.getGridRowPositions().valueAt(i).size() != getGridRowPositions().valueAt(i).size()) {
				return false;
			}
			if (!reference.getGridRowPositions().valueAt(i).containsAll(getGridRowPositions().valueAt(i))) {
				return false;
			}
			if (!getGridRowPositions().valueAt(i).containsAll(reference.getGridRowPositions().valueAt(i))) {
				return false;
			}
		}
		return true;
	}
}
