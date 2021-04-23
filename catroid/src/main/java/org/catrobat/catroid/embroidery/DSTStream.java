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

package org.catrobat.catroid.embroidery;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class DSTStream implements EmbroideryStream {
	private EmbroideryHeader header;

	private boolean colorChangeBitSet = false;
	private boolean jumpBitSet = false;

	private ArrayList<StitchPoint> points = new ArrayList<>();

	public DSTStream(EmbroideryHeader header) {
		this.header = header;
	}

	@Override
	public void addColorChange() {
		header.addColorChange();
		colorChangeBitSet = true;
	}

	@Override
	public void addJump() {
		jumpBitSet = true;
	}

	@Override
	public void addStitchPoint(float x, float y, Color color) {
		DSTStitchPoint point = new DSTStitchPoint(x, y, color);
		point.setColorChange(colorChangeBitSet);
		point.setJump(jumpBitSet);
		colorChangeBitSet = false;
		jumpBitSet = false;
		if (points.isEmpty()) {
			header.initialize(x, y);
			point.setRelativeCoordinatesToPreviousPoint(x, y);
		} else {
			addInterpolatedPoints(x, y, color);
			header.update(x, y);
			StitchPoint previousPoint = points.get(points.size() - 1);
			point.setRelativeCoordinatesToPreviousPoint(previousPoint.getX(), previousPoint.getY());
		}
		points.add(point);
	}

	private void addInterpolatedPoints(float currentX, float currentY, Color color) {
		StitchPoint previousPoint = points.get(points.size() - 1);

		float distance = DSTFileConstants.getMaxDistanceBetweenPoints(currentX, currentY,
				previousPoint.getX(), previousPoint.getY());
		if (distance > DSTFileConstants.MAX_DISTANCE) {
			int splitCount = (int) Math.ceil(distance / DSTFileConstants.MAX_DISTANCE);
			addJump();
			addStitchPoint(previousPoint.getX(), previousPoint.getY(), previousPoint.getColor());

			for (int count = 1; count < splitCount; count++) {
				float splitFactor = (float) count / splitCount;
				float x = interpolate(currentX, previousPoint.getX(), splitFactor);
				float y = interpolate(currentY, previousPoint.getY(), splitFactor);
				addJump();
				addStitchPoint(x, y, previousPoint.getColor());
			}

			addJump();
			addStitchPoint(currentX, currentY, color);
		}
	}

	private float interpolate(float endValue, float startValue, float percentage) {
		return Math.round(startValue + percentage * (endValue - startValue));
	}

	@Override
	public void addAllStitchPoints(ArrayList<StitchPoint> stitchPoints) {
		if (!points.isEmpty() && !stitchPoints.isEmpty()) {
			addJump();
			addStitchPoint(points.get(points.size() - 1).getX(),
					points.get(points.size() - 1).getY(), points.get(points.size() - 1).getColor());
		}
		for (StitchPoint stitchPoint : stitchPoints) {
			if (stitchPoint.isColorChangePoint()) {
				addColorChange();
			} else if (stitchPoint.isJumpPoint()) {
				addJump();
			}
			addStitchPoint(stitchPoint.getX(), stitchPoint.getY(), stitchPoint.getColor());
		}
	}

	@Override
	public ArrayList<StitchPoint> getPointList() {
		return points;
	}

	@Override
	public EmbroideryHeader getHeader() {
		return header;
	}

	@Override
	public void reset() {
		colorChangeBitSet = false;
		points.clear();
		header.reset();
	}
}
