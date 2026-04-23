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

package org.catrobat.catroid.ui.recyclerview.adapter.draganddrop;

import java.util.SortedSet;
import java.util.TreeSet;

public class ViewStateManager {

	private SortedSet<Integer> invisiblePositions = new TreeSet<>();
	private SortedSet<Integer> disabledPositions = new TreeSet<>();

	public boolean isVisible(int position) {
		return !invisiblePositions.contains(position);
	}

	public void setVisible(int position, boolean visible) {
		if (visible) {
			invisiblePositions.remove(position);
		} else {
			invisiblePositions.add(position);
		}
	}

	public void setAllPositionsVisible() {
		invisiblePositions.clear();
	}

	public boolean isEnabled(int position) {
		return !disabledPositions.contains(position);
	}

	public void setEnabled(boolean enabled, int position) {
		if (enabled) {
			disabledPositions.remove(position);
		} else {
			disabledPositions.add(position);
		}
	}

	public void clearDisabledPositions() {
		disabledPositions.clear();
	}
}
