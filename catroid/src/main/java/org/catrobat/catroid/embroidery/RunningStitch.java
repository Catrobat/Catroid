/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import org.catrobat.catroid.content.Sprite;

public class RunningStitch {
	private Sprite sprite;
	private boolean isRunning = false;
	private RunningStitchType type;

	public void activateStitching(Sprite sprite, RunningStitchType type) {
		if (sprite != null && type != null) {
			this.sprite = sprite;
			this.type = type;
			isRunning = true;
		}
	}

	public void update() {
		if (isRunning) {
			float currentX = sprite.look.getXInUserInterfaceDimensionUnit();
			float currentY = sprite.look.getYInUserInterfaceDimensionUnit();
			type.update(currentX, currentY);
		}
	}

	public void setStartCoordinates(float xStart, float yStart) {
		if (type != null) {
			type.setStartCoordinates(xStart, yStart);
		}
	}

	public void pause() {
		isRunning = false;
	}

	public void resume() {
		if (type != null) {
			isRunning = true;
		}
	}
}
