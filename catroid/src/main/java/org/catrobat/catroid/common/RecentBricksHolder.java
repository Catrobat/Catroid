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

package org.catrobat.catroid.common;

import org.catrobat.catroid.content.bricks.Brick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecentBricksHolder implements Serializable {
	private List<Brick> recentBricks;

	public RecentBricksHolder() {
		recentBricks = new ArrayList<>();
	}

	public List<Brick> getRecentBricks() {
		return recentBricks;
	}

	public int find(Brick brick) {
		for (int i = 0; i < recentBricks.size(); i++) {
			Brick b = recentBricks.get(i);
			if (b.getClass().equals(brick.getClass())) {
				return i;
			}
		}
		return -1;
	}

	public int size() {
		return recentBricks.size();
	}

	public void remove() {
		recentBricks.remove(size() - 1);
	}

	public void remove(int index) {
		recentBricks.remove(index);
	}

	public void insert(Brick brick) {
		recentBricks.add(0, brick);
	}
}
