/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.gui.adapter;

import java.util.SortedSet;
import java.util.TreeSet;

public class MultiSelectionManager {

	private SortedSet<Integer> selectedPositions = new TreeSet<>();

	public void toggleSelection(int position) {
		if (isPositionSelected(position)) {
			removeSelection(position);
		} else {
			setSelection(position);
		}
	}

	public boolean isPositionSelected(int position) {
		return selectedPositions.contains(position);
	}

	public void setSelection(int position) {
		selectedPositions.add(position);
	}

	public void removeSelection(int position) {
		selectedPositions.remove(position);
	}

	public SortedSet<Integer> getSelectedPositions() {
		return selectedPositions;
	}

	public void updateSelection(int fromPosition, int toPosition) {

		if (selectedPositions.contains(fromPosition) && !selectedPositions.contains(toPosition)) {
			selectedPositions.remove(fromPosition);
			selectedPositions.add(toPosition);
			return;
		}

		if (selectedPositions.contains(toPosition) && !selectedPositions.contains(fromPosition)) {
			selectedPositions.remove(toPosition);
			selectedPositions.add(fromPosition);
		}
	}

	public boolean isSelectionActive() {
		return !selectedPositions.isEmpty();
	}

	public void clearSelection() {
		selectedPositions.clear();
	}
}
