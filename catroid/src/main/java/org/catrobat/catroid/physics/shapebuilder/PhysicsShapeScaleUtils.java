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

package org.catrobat.catroid.physics.shapebuilder;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import java.util.LinkedList;
import java.util.List;

public final class PhysicsShapeScaleUtils {

	public static final float COORDINATE_SCALING_DECIMAL_ACCURACY = 100.0f;

	private PhysicsShapeScaleUtils() {
	}

	public static Shape[] scaleShapes(Shape[] shapes, float targetScale) {
		return scaleShapes(shapes, targetScale, 1.0f);
	}

	public static Shape[] scaleShapes(Shape[] shapes, float targetScale, float originScale) {
		if (shapes == null || shapes.length == 0 || targetScale == 0.0f || originScale == 0.0f) {
			return null;
		}
		if (targetScale == originScale) {
			return shapes;
		}
		float scale = targetScale / originScale;
		List<Shape> scaledShapes = new LinkedList<>();
		if (shapes != null) {
			for (Shape shape : shapes) {
				List<Vector2> vertices = new LinkedList<>();
				PolygonShape polygon = (PolygonShape) shape;
				for (int index = 0; index < polygon.getVertexCount(); index++) {
					Vector2 vertex = new Vector2();
					polygon.getVertex(index, vertex);
					vertex = scaleCoordinate(vertex, scale);
					vertices.add(vertex);
				}
				PolygonShape polygonShape = new PolygonShape();
				polygonShape.set(vertices.toArray(new Vector2[vertices.size()]));
				scaledShapes.add(polygonShape);
			}
		}
		return scaledShapes.toArray(new Shape[scaledShapes.size()]);
	}

	private static Vector2 scaleCoordinate(Vector2 vertex, float scaleFactor) {
		Vector2 v = new Vector2(vertex);
		v.x = scaleCoordinate(v.x, scaleFactor);
		v.y = scaleCoordinate(v.y, scaleFactor);
		return v;
	}

	private static float scaleCoordinate(float coordinates, float scaleFactor) {
		return Math.round(coordinates * scaleFactor * COORDINATE_SCALING_DECIMAL_ACCURACY) / COORDINATE_SCALING_DECIMAL_ACCURACY;
	}
}
