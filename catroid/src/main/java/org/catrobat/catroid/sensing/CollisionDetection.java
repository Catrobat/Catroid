/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.sensing;

import android.util.Log;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.Look;

public final class CollisionDetection {

	private CollisionDetection() {
	}

	public static double checkCollisionBetweenLooks(Look firstLook, Look secondLook) {
		if (!firstLook.isVisible() || !secondLook.isVisible()) {
			return 0d;
		}

		if (!firstLook.getHitbox().overlaps(secondLook.getHitbox())) {
			return 0d;
		}

		boolean colliding = checkCollisionBetweenPolygons(firstLook.getCurrentCollisionPolygon(),
				secondLook.getCurrentCollisionPolygon());

		return colliding ? 1d : 0d;
	}

	public static boolean checkCollisionBetweenPolygons(Polygon[] first, Polygon[] second) {
		Rectangle[] firstBoxes = createBoundingBoxesOfCollisionPolygons(first);
		Rectangle[] secondBoxes = createBoundingBoxesOfCollisionPolygons(second);

		for (int firstIndex = 0; firstIndex < first.length; firstIndex++) {
			for (int secondIndex = 0; secondIndex < second.length; secondIndex++) {
				if (firstBoxes[firstIndex].overlaps(secondBoxes[secondIndex])
						&& intersectPolygons(first[firstIndex], second[secondIndex])) {
					return true;
				}
			}
		}
		return checkCollisionForPolygonsInPolygons(first, second);
	}

	private static Rectangle[] createBoundingBoxesOfCollisionPolygons(Polygon[] polygons) {
		Rectangle[] boundingBoxes = new Rectangle[polygons.length];
		for (int i = 0; i < polygons.length; i++) {
			boundingBoxes[i] = polygons[i].getBoundingRectangle();
		}
		return boundingBoxes;
	}

	public static boolean intersectPolygons(Polygon first, Polygon second) {
		float[] firstVertices = first.getTransformedVertices();
		int firstLength = firstVertices.length;
		Vector2 v1 = new Vector2();
		Vector2 v2 = new Vector2();

		for (int firstIndex = 0; firstIndex < firstLength; firstIndex += 2) {
			v1.x = firstVertices[firstIndex];
			v1.y = firstVertices[firstIndex + 1];
			v2.x = firstVertices[(firstIndex + 2) % firstLength];
			v2.y = firstVertices[(firstIndex + 3) % firstLength];

			if (Intersector.intersectSegmentPolygon(v1, v2, second)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkCollisionForPolygonsInPolygons(Polygon[] first, Polygon[] second) {
		for (Polygon firstPolygon : first) {
			int containedIn = 0;
			for (Polygon secondPolygon : second) {
				if (secondPolygon.contains(firstPolygon.getTransformedVertices()[0], firstPolygon.getTransformedVertices()[1])) {
					containedIn++;
				}
			}
			if (containedIn % 2 != 0) {
				return true;
			}
		}

		for (Polygon secondPolygon : second) {
			int containedIn = 0;
			for (Polygon firstPolygon : first) {
				if (firstPolygon.contains(secondPolygon.getTransformedVertices()[0], secondPolygon.getTransformedVertices()[1])) {
					containedIn++;
				}
			}
			if (containedIn % 2 != 0) {
				return true;
			}
		}
		return false;
	}
}
