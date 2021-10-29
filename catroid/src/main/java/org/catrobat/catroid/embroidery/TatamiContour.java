/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.embroidery;

import android.util.Pair;

import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.List;

public class TatamiContour {
	private Sprite sprite = null;
	List<Pair<Integer, Integer>> coordinates = new ArrayList<>();
	private boolean isRunning = false;

	public void update() {
		if (isRunning) {
			int currentX = (int) sprite.look.getXInUserInterfaceDimensionUnit();
			int currentY = (int) sprite.look.getYInUserInterfaceDimensionUnit();
			Pair<Integer, Integer> pair = coordinates.get(coordinates.size() - 1);
			if (pair.first != currentX || pair.second != currentY) {
				coordinates.add(new Pair<>(currentX, currentY));
			}
		}
	}

	public void setStartCoordinates(int xCoordinate, int yCoordinate) {
		if (coordinates.isEmpty()) {
			coordinates.add(new Pair<>(xCoordinate, yCoordinate));
		}
	}

	public void reset() {
		isRunning = false;
		coordinates.clear();
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public List<Pair<Integer, Integer>> getCoordinates() {
		return coordinates;
	}
}
