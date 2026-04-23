/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.physics.collision;

import com.badlogic.gdx.math.Polygon;

import org.catrobat.catroid.sensing.CollisionDetection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CollisionDetectionBasicTest {

	@Test
	public void testIntersectPolygons() {
		Polygon[] p1 = {new Polygon(new float[] {0, 2, 2, 2, 1, 0})};
		Polygon[] p2 = {new Polygon(new float[] {0, 0, 1, 2, 2, 0})};
		assertTrue(CollisionDetection.checkCollisionBetweenPolygons(p1, p2));

		Polygon[] p3 = {new Polygon(new float[] {2, 0, 3, 2, 4, 0})};
		assertFalse(CollisionDetection.checkCollisionBetweenPolygons(p1, p3));

		Polygon[] p4 = {new Polygon(new float[] {-5, -1, -5, 1, 5, 1, 5, -1})};
		Polygon[] p5 = {new Polygon(new float[] {-1, 5, 1, 5, 1, -5, -1, -5})};
		assertTrue(CollisionDetection.checkCollisionBetweenPolygons(p4, p5));

		Polygon[] p6 = {new Polygon(new float[] {0, 2, 2, 2, 1, 0})};
		Polygon[] p7 = {new Polygon(new float[] {0, 2, 2, 2, 1, 0})};
		assertTrue(CollisionDetection.checkCollisionBetweenPolygons(p6, p7));
	}

	@Test
	public void testCollisionForContainedPolygon() {
		Polygon[] p1 = {new Polygon(new float[] {0, 0, 0, 4, 4, 4, 4, 0})};
		Polygon[] p2 = {new Polygon(new float[] {2, 2, 3, 2, 2, 3})};
		assertTrue(CollisionDetection.checkCollisionBetweenPolygons(p1, p2));
	}

	@Test
	public void testObjectInDonut() {
		Polygon[] donut = {new Polygon(new float[] {0, 0, 0, 5, 5, 5, 5, 0}),
				new Polygon(new float[] {2, 2, 2, 3, 3, 3, 3, 2})};
		Polygon[] p2 = {new Polygon(new float[] {0.5f, 0.5f, 0.5f, 1.5f, 1.5f, 1.5f, 1.5f, 0.5f})};
		assertTrue(CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}

	@Test
	public void testDonutInObject() {
		Polygon[] donut = {new Polygon(new float[] {1, 1, 1, 5, 5, 5, 5, 1}),
				new Polygon(new float[] {2, 2, 2, 3, 3, 3, 3, 2})};
		Polygon[] p2 = {new Polygon(new float[] {0, 0, 0, 10, 10, 10, 10, 0})};
		assertTrue(CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}

	@Test
	public void testObjectInDonutHole() {
		Polygon[] donut = {new Polygon(new float[] {0, 0, 0, 5, 5, 5, 5, 0}),
				new Polygon(new float[] {1, 1, 1, 4, 4, 4, 4, 1})};
		Polygon[] p2 = {new Polygon(new float[] {2, 2, 2, 3, 3, 3, 3, 2})};
		assertFalse(CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}

	@Test
	public void testObjectCollidingWithDonut() {
		Polygon[] donut = {new Polygon(new float[] {0, 0, 0, 5, 5, 5, 5, 0}),
				new Polygon(new float[] {1, 1, 1, 4, 4, 4, 4, 1})};
		Polygon[] p2 = {new Polygon(new float[] {2, 2, 2, 4.5f, 3, 3, 3, 2})};
		assertTrue(CollisionDetection.checkCollisionBetweenPolygons(donut, p2));
	}
}
