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

public class ZigZagRunningStitch implements RunningStitchType {
	private float length;
	private float width;
	private Sprite sprite;
	private float firstX = 0;
	private float firstY = 0;
	private int direction = -1;
	private boolean first = true;

	public ZigZagRunningStitch(Sprite sprite, float length, float width) {
		this.sprite = sprite;
		this.length = length;
		this.width = width;
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
		if (sprite != null) {
			float distance = getDistanceToPoint(currentX, currentY);
			if (distance >= length) {
				float surplusPercentage = ((distance - (distance % length)) / distance);
				currentX = firstX + (surplusPercentage * (currentX - firstX));
				currentY = firstY + (surplusPercentage * (currentY - firstY));
				distance -= distance % length;

				int interpolationCount = (int) (Math.floor(distance / length));
				interpolateStitches(interpolationCount, currentX, currentY);
				setStartCoordinates(currentX, currentY);
			}
		}
	}

	private void interpolateStitches(int interpolationCount, float currentX, float currentY) {
		float degrees = sprite.look.getDirectionInUserInterfaceDimensionUnit();

		if (first) {
			first = false;
			addPointInDirection(firstX, firstY, degrees);
			addPointInDirection(firstX, firstY, degrees);
		}

		for (int count = 1; count < interpolationCount; count++) {
			float splitFactor = (float) count / interpolationCount;
			float x = interpolate(currentX, firstX, splitFactor);
			float y = interpolate(currentY, firstY, splitFactor);
			addPointInDirection(x, y, degrees);
			addPointInDirection(x, y, degrees);
		}

		addPointInDirection(currentX, currentY, degrees);
		addPointInDirection(currentX, currentY, degrees);
	}

	private void addPointInDirection(float x, float y, float degrees) {
		float xCoord = (float) (x + (width / 2) * Math.sin(Math.toRadians(degrees + 90)) * direction);
		float yCoord = (float) (y + (width / 2) * Math.cos(Math.toRadians(degrees + 90)) * direction);
		direction *= (-1);
		StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(xCoord, yCoord,
				sprite.look.getZIndex(), sprite));
	}

	private float interpolate(float endValue, float startValue, float percentage) {
		float value = Math.round(startValue + percentage * (endValue - startValue));
		return value;
	}

	private float getDistanceToPoint(float currentX, float currentY) {
		double xDistance = Math.pow(currentX - firstX, 2);
		double yDistance = Math.pow(currentY - firstY, 2);
		double difference = Math.sqrt(xDistance + yDistance);
		return (float) difference;
	}
}
