/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
package org.catrobat.catroid.physics.shapebuilder;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.physics.PhysicsWorldConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class PhysicsShapeBuilderStrategyFastHull implements PhysicsShapeBuilderStrategy {

	private final Stack<Vector2> convexHull = new Stack<Vector2>();

	@Override
	public Shape[] build(LookData lookData) {
		Pixmap pixmap = lookData.getPixmap();


		//float scale = 0.25f; //25% of original size
		//Pixmap thumb = new Pixmap((int)(pixmap.getWidth() * scale), (int)(pixmap.getHeight() * scale), pixmap.getFormat());
		//Pixmap.setFilter(Pixmap.Filter.BiLinear);
		//thumb.draw(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, thumb.getWidth(), thumb.getHeight());


		if (pixmap == null) {
			return null;
		}

		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		convexHull.clear();

		Vector2 point = new Vector2(width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < point.x; x++) {
				if ((pixmap.getPixel(x, y) & 0xff) > 0) {
					point = new Vector2(x, y);
					addPoint(convexHull, point);
					//Log.d("PhysicsShapeBuilderStrategyFastHull", "X:" + point.x + " Y:" + point.y);
					break;
				}
			}
		}

		if (convexHull.isEmpty()) {
			return null;
		}

		for (int x = (int) point.x; x < width; x++) {
			for (int y = height - 1; y > point.y; y--) {
				if ((pixmap.getPixel(x, y) & 0xff) > 0) {
					point = new Vector2(x, y);
					addPoint(convexHull, point);
					break;
				}
			}
		}

		Vector2 firstPoint = convexHull.firstElement();
		for (int y = (int) point.y; y >= firstPoint.y; y--) {
			for (int x = width - 1; x > point.x; x--) {
				if ((pixmap.getPixel(x, y) & 0xff) > 0) {
					point = new Vector2(x, y);
					addPoint(convexHull, point);
					break;
				}
			}
		}

		for (int x = (int) point.x; x > firstPoint.x; x--) {
			for (int y = (int) firstPoint.y; y < point.y; y++) {
				if ((pixmap.getPixel(x, y) & 0xff) > 0) {
					point = new Vector2(x, y);
					addPoint(convexHull, point);
					break;
				}
			}
		}

		if (convexHull.size() > 2) {
			removeNonConvexPoints(convexHull, firstPoint);
		}

		return devideShape(convexHull.toArray(new Vector2[convexHull.size()]), width, height);
	}

	private void addPoint(Stack<Vector2> convexHull, Vector2 point) {
		removeNonConvexPoints(convexHull, point);
		convexHull.add(point);
	}

	private void removeNonConvexPoints(Stack<Vector2> convexHull, Vector2 newTop) {
		while (convexHull.size() > 1) {
			Vector2 top = convexHull.peek();
			Vector2 secondTop = convexHull.get(convexHull.size() - 2);

			if (leftTurn(secondTop, top, newTop)) {
				break;
			}

			if (top.y > newTop.y && top.y > secondTop.y) {
				break;
			}

			convexHull.pop();
		}
	}

	private boolean leftTurn(Vector2 a, Vector2 b, Vector2 c) {
		return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x) < 0;
	}

	private Shape[] devideShape(Vector2[] convexpoints, int width, int height) {
		for (int index = 0; index < convexpoints.length; index++) {
			Vector2 point = convexpoints[index];
			point.x -= width / 2.0f;
			point.y = height / 2.0f - point.y;
			convexpoints[index] = PhysicsWorldConverter.convertCatroidToBox2dVector(point);
		}

		if (convexpoints.length < 9) {
			PolygonShape polygon = new PolygonShape();
			polygon.set(convexpoints);
			return new Shape[]{polygon};
		}

		List<Shape> shapes = new ArrayList<Shape>(convexpoints.length / 6 + 1);
		List<Vector2> pointsPerShape = new ArrayList<Vector2>(8);

		Vector2 rome = convexpoints[0];
		int index = 1;
		while (index < convexpoints.length - 1) {
			int k = index + 7;

			int remainingPointsCount = convexpoints.length - index;
			if (remainingPointsCount > 7 && remainingPointsCount < 9) {
				k -= 3;
			}

			pointsPerShape.add(rome);
			for (; index < k && index < convexpoints.length; index++) {
				pointsPerShape.add(convexpoints[index]);
			}

			PolygonShape polygon = new PolygonShape();
			polygon.set(pointsPerShape.toArray(new Vector2[pointsPerShape.size()]));
			shapes.add(polygon);

			pointsPerShape.clear();
			index--;
		}

		return shapes.toArray(new Shape[shapes.size()]);
	}
}
