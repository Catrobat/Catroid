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
import org.catrobat.catroid.stage.StageActivity;

public class TripleRunningStitch extends RunningStitchType {
	private Sprite sprite;
	private int steps;
	private boolean first;
	private float firstX = 0;
	private float firstY = 0;

	public TripleRunningStitch(Sprite sprite, int steps) {
		this.sprite = sprite;
		this.steps = steps;
		first = true;
		setStartCoordinates(sprite.look.getXInUserInterfaceDimensionUnit(),
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Override
	public void setStartCoordinates(float x, float y) {
		this.firstX = x;
		this.firstY = y;
	}

	@Override
	public void update(float currentX, float currentY) {
		float distance = getDistanceToPoint(currentX, firstX, currentY, firstY);
		if (distance >= steps) {
			float surplusPercentage = ((distance - (distance % steps)) / distance);
			currentX = firstX + (surplusPercentage * (currentX - firstX));
			currentY = firstY + (surplusPercentage * (currentY - firstY));
			distance -= distance % steps;

			int interpolationCount = (int) (Math.floor(distance / steps));
			interpolateStitches(interpolationCount, currentX, currentY);
			setStartCoordinates(currentX, currentY);
		}
	}

	private void interpolateStitches(int interpolationCount, float currentX, float currentY) {
		if (first) {
			first = false;
			StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(firstX, firstY,
					sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
		}
		float previousX = firstX;
		float previousY = firstY;

		for (int count = 1; count <= interpolationCount; count++) {
			float splitFactor = (float) count / interpolationCount;
			float x = interpolate(currentX, firstX, splitFactor);
			float y = interpolate(currentY, firstY, splitFactor);
			StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(x, y,
					sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
			StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(previousX, previousY,
					sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
			StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(x, y,
					sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
			previousX = x;
			previousY = y;
		}
	}
}
