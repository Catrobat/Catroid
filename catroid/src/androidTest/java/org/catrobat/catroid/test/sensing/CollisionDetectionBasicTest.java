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

package org.catrobat.catroid.test.sensing;

import android.test.AndroidTestCase;

import com.badlogic.gdx.math.Polygon;

import org.catrobat.catroid.sensing.CollisionDetection;

public class CollisionDetectionBasicTest extends AndroidTestCase {

	public void testIntersectPolygons() {
		Polygon[] p1 = {new Polygon(new float[] {0, 2, 2, 2, 1, 0})};
		Polygon[] p2 = {new Polygon(new float[] {0, 0, 1, 2, 2, 0})};
		assertTrue("Collision of intersecting polygons not detected!",
				CollisionDetection.checkCollisionBetweenPolygons(p1, p2));

		Polygon[] p3 = {new Polygon(new float[] {2, 0, 3, 2, 4, 0})};
		assertFalse("Not colliding polygons falsely detected as intersecting!",
				CollisionDetection.checkCollisionBetweenPolygons(p1, p3));

		Polygon[] p4 = {new Polygon(new float[] {-5, -1, -5, 1, 5, 1, 5, -1})};
		Polygon[] p5 = {new Polygon(new float[] {-1, 5, 1, 5, 1, -5, -1, -5})};
		assertTrue("Cross test not detected as intersecting!",
				CollisionDetection.checkCollisionBetweenPolygons(p4, p5));

		Polygon[] p6 = {new Polygon(new float[] {0, 2, 2, 2, 1, 0})};
		Polygon[] p7 = {new Polygon(new float[] {0, 2, 2, 2, 1, 0})};
		assertTrue("Same Polygons not detected as collision",
				CollisionDetection.checkCollisionBetweenPolygons(p6, p7));
	}

	public void testCollisionForContainedPolygon() {
		Polygon[] p1 = {new Polygon(new float[] {0, 0, 0, 4, 4, 4, 4, 0})};
		Polygon[] p2 = {new Polygon(new float[] {2, 2, 3, 2, 2, 3})};
		assertTrue("Polygon in Polygon not detected as intersecting!",
				CollisionDetection.checkCollisionBetweenPolygons(p1, p2));
	}

	public void testObjectInDonut() {
		Polygon[] donut = {new Polygon(new float[] {0, 0, 0, 5, 5, 5, 5, 0}),
				new Polygon(new float[] {2, 2, 2, 3, 3, 3, 3, 2})};
		Polygon[] p2 = {new Polygon(new float[] {0.5f, 0.5f, 0.5f, 1.5f, 1.5f, 1.5f, 1.5f, 0.5f})};
		assertTrue("Polygon in Donut falsely detected as colliding!",
				CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}

	public void testDonutInObject() {
		Polygon[] donut = {new Polygon(new float[] {1, 1, 1, 5, 5, 5, 5, 1}),
				new Polygon(new float[] {2, 2, 2, 3, 3, 3, 3, 2})};
		Polygon[] p2 = {new Polygon(new float[] {0, 0, 0, 10, 10, 10, 10, 0})};
		assertTrue("Polygon in Donut falsely detected as colliding!",
				CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}

	public void testObjectInDonutHole() {
		Polygon[] donut = {new Polygon(new float[] {0, 0, 0, 5, 5, 5, 5, 0}),
				new Polygon(new float[] {1, 1, 1, 4, 4, 4, 4, 1})};
		Polygon[] p2 = {new Polygon(new float[] {2, 2, 2, 3, 3, 3, 3, 2})};
		assertFalse("Polygon in Donut Hole falsely detected as colliding!",
				CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}

	public void testObjectCollidingWithDonut() {
		Polygon[] donut = {new Polygon(new float[] {0, 0, 0, 5, 5, 5, 5, 0}),
				new Polygon(new float[] {1, 1, 1, 4, 4, 4, 4, 1})};
		Polygon[] p2 = {new Polygon(new float[] {2, 2, 2, 4.5f, 3, 3, 3, 2})};
		assertTrue("Polygon colliding with Donut not detected!",
				CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}
}
