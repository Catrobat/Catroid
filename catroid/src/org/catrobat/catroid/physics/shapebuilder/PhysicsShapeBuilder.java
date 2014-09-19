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

import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.physics.PhysicsWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicsShapeBuilder {

	private static final String TAG = PhysicsShapeBuilder.class.getSimpleName();
	private static final float OFFSET_VALUE = 1 / (2 * PhysicsWorld.RATIO);
	private final Map<String, Shape[]> shapeMap = new HashMap<String, Shape[]>();
	private PhysicsShapeBuilderStrategy strategy = new PhysicsShapeBuilderStrategyFastHull();

	public PhysicsShapeBuilder() {
	}

	public Shape[] getShape(LookData lookData, float scaleFactor) {

		Shape[] shapes = null;
		float scaleLevel = getScaleLevel(scaleFactor);
		String key = getKey(lookData, scaleLevel);

		if (shapeMap.containsKey(key)) {
			Log.d(TAG, "reuse");
			shapes = shapeMap.get(key);
		} else {
			Pixmap pixmap = lookData.getPixmap();
			if (pixmap == null) {
				return null;
			}
			Pixmap thumb = new Pixmap((int) (pixmap.getWidth() * scaleLevel), (int) (pixmap.getHeight() * scaleLevel), pixmap.getFormat());
			Pixmap.setFilter(Pixmap.Filter.NearestNeighbour);
			thumb.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, thumb.getWidth(), thumb.getHeight());
			shapes = strategy.build(pixmap, scaleFactor);
			thumb.dispose();
			//shapes = normalize(shapes, scaleLevel);
			shapeMap.put(key, shapes);
		}
		return scaleShapes(shapes, scaleFactor);
	}

	//private Shape[] normalize(Shape[] shapes, float scaleLevel) {
	//	return scaleShapes(shapes, 1 / scaleLevel);
	//}

	private float getScaleLevel(float scaleFactor) {
		if (scaleFactor >= 0.25f) {
			return 0.25f;
		}
		return 0.05f;
	}

	private String getKey(LookData lookData, float scaleFactor) {
		return lookData.getChecksum() + (int) (scaleFactor * 10);
	}

	private Shape[] scaleShapes(Shape[] shapes, float scaleFactor) {
		List<Shape> scaledShapes = new ArrayList<Shape>();

		for (Shape shape : shapes) {
			List<Vector2> vertices = new ArrayList<Vector2>();

			PolygonShape polygon = (PolygonShape) shape;
			for (int index = 0; index < polygon.getVertexCount(); index++) {
				Vector2 vertex = new Vector2();
				polygon.getVertex(index, vertex);

				vertex = scaleCoordinate(vertex, scaleFactor);
				vertices.add(vertex);
			}

			PolygonShape polygonShape = new PolygonShape();
			polygonShape.set(vertices.toArray(new Vector2[vertices.size()]));
			scaledShapes.add(polygonShape);
		}

		return scaledShapes.toArray(new Shape[scaledShapes.size()]);
	}

	public static Vector2 scaleCoordinate(Vector2 vertex, float scaleFactor) {
		if (Math.abs(scaleFactor) < 0.001f) {
			vertex = vertex.mul(scaleFactor);
		} else if (Math.abs(scaleFactor - 1.0f) > 0.001f) {
			Vector2 offset = new Vector2();
			offset.x = vertex.x < -0.001f ? -OFFSET_VALUE : vertex.x > 0.001f ? OFFSET_VALUE : 0.0f;
			offset.y = vertex.y < -0.001f ? -OFFSET_VALUE : vertex.y > 0.001f ? OFFSET_VALUE : 0.0f;
			vertex = vertex.add(offset).mul(scaleFactor).sub(offset);
		}
		return vertex;
	}

	public static float scaleCoordinate (float coord, float factor) {
		if (Math.abs(factor) < 0.001f) {
			coord = coord*factor;
		} else if (Math.abs(factor - 1.0f) > 0.001f) {
			float offset;
			offset = coord < -0.001f ? -OFFSET_VALUE : coord > 0.001f ? OFFSET_VALUE : 0.0f;
			coord = (((coord + offset) * factor) - offset);
		}
		return coord;
	}
}
