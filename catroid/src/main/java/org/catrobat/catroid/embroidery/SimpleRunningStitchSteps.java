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

public class SimpleRunningStitchSteps implements RunningStitchType {
	private Sprite sprite;
	private int steps;
	private boolean first;
	private float counter;
	private float firstX ;
	private float firstY;

	public SimpleRunningStitchSteps(Sprite sprite, int steps) {
		this.sprite = sprite;
		this.steps = steps;
		first = true;
		counter = 0;
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
		float distance = getDistanceToPoint(currentX, currentY);
		counter += distance;
		if(counter >= steps) {
			float surplusPercentage = ((distance - (counter % steps)) / distance);
			currentX = firstX + (surplusPercentage * (currentX - firstX));
			currentY = firstY + (surplusPercentage * (currentY - firstY));

			int interpolationCount = (int) (Math.floor(counter / steps));
			interpolateStitches(interpolationCount, currentX, currentY);
			setStartCoordinates(currentX, currentY);
			counter = counter % steps;
		}

	}

	private void interpolateStitches(int interpolationCount, float currentX, float currentY) {
		if (first) {
			first = false;
			StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(firstX, firstY,
					sprite.look.getZIndex(), sprite));
		}

		for (int count = 1; count <= interpolationCount; count++) {
			float splitFactor = (float) count / interpolationCount;
			float x = interpolate(currentX, firstX, splitFactor);
			float y = interpolate(currentY, firstY, splitFactor);
			StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(x, y,
					sprite.look.getZIndex(), sprite));
		}
	}

	private float interpolate(float endValue, float startValue, float percentage) {
		return Math.round(startValue + percentage * (endValue - startValue));
	}

	private float getDistanceToPoint(float currentX, float currentY) {
		return (float) Math.sqrt(Math.pow(currentX - firstX, 2) + Math.pow(currentY - firstY, 2));
	}
}
