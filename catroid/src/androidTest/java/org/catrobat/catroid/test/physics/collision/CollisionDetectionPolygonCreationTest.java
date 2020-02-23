/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.test.physics.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.test.physics.PhysicsTestUtils.generateLookData;
import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class CollisionDetectionPolygonCreationTest {
	protected Project project;
	protected Sprite sprite;
	private static final float DELTA = Float.MIN_VALUE;

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects();

		project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);
	}

	protected CollisionInformation generateCollisionInformation(int resourceId, String filename) throws IOException {
		String hashedFileName = PhysicsTestUtils.getInternalImageFilenameFromFilename(filename);

		File file = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				resourceId,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				hashedFileName,
				1);

		LookData lookData = generateLookData(file);
		sprite.getLookList().add(lookData);

		CollisionInformation collisionInformation = lookData.getCollisionInformation();
		collisionInformation.loadCollisionPolygon();
		return collisionInformation;
	}

	@Test
	public void testRectangle() throws IOException {
		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw
				.rectangle_125x125, "rectangle_125x125.png");
		collisionInformation.printDebugCollisionPolygons();

		assertNotNull(collisionInformation.collisionPolygons);
		assertEquals(1, collisionInformation.collisionPolygons.length);
		assertArrayEquals(new float[] {0.0f, 0.0f, 0.0f, 125.0f, 125.0f, 125.0f, 125.0f, 0.0f},
				collisionInformation.collisionPolygons[0].getVertices(), DELTA);
	}

	@Test
	public void testSimpleConvexPolygon() throws IOException {
		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw.complex_single_convex_polygon, "complex_single_convex_polygon.png");
		collisionInformation.printDebugCollisionPolygons();

		assertNotNull(collisionInformation.collisionPolygons);
		assertEquals(1, collisionInformation.collisionPolygons.length);
		assertArrayEquals(new float[] {0.0f, 47.0f, 17.0f, 98.0f, 52.0f, 98.0f, 68.0f, 44.0f, 52.0f, 0.0f, 17.0f, 0.0f},
				collisionInformation.collisionPolygons[0].getVertices(), DELTA);
	}

	@Test
	public void testMultipleConcavePolygons() throws IOException {
		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw
				.multible_concave_polygons, "multible_concave_polygons.png");
		collisionInformation.printDebugCollisionPolygons();

		assertNotNull(collisionInformation.collisionPolygons);
		assertEquals(2, collisionInformation.collisionPolygons.length);
		assertArrayEquals(new float[] {0.0f, 110.0f, 0.0f, 185.0f, 91.0f, 185.0f, 91.0f, 136.0f, 34.0f, 136.0f,
				34.0f, 110.0f},
				collisionInformation.collisionPolygons[0].getVertices(), DELTA);

		assertArrayEquals(new float[] {128.0f, 30.0f, 128.0f, 91.0f, 159.0f, 91.0f, 159.0f, 121.0f, 227.0f, 121.0f,
				227.0f, 91.0f, 257.0f, 91.0f, 257.0f, 30.0f, 227.0f, 30.0f, 227.0f, 0.0f, 159.0f, 0.0f, 159.0f, 30.0f},
				collisionInformation.collisionPolygons[1].getVertices(), DELTA);
	}

	@Test
	public void testDonutPolygons() throws IOException {
		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw.collision_donut,
				"collision_donut.png");
		collisionInformation.printDebugCollisionPolygons();

		assertNotNull(collisionInformation.collisionPolygons);
		assertEquals(2, collisionInformation.collisionPolygons.length);

		assertArrayEquals(new float[] {0.0f, 228.0f, 9.0f, 321.0f, 57.0f, 411.0f, 136.0f, 474.0f, 228.0f, 500.0f, 305.0f,
				495.0f, 375.0f, 468.0f, 436.0f, 419.0f, 474.0f, 364.0f, 497.0f, 295.0f, 499.0f, 218.0f, 481.0f,
				151.0f, 443.0f, 89.0f, 385.0f, 38.0f, 321.0f, 9.0f, 179.0f, 9.0f, 115.0f, 38.0f, 57.0f,
				89.0f, 19.0f, 151.0f},
				collisionInformation.collisionPolygons[0].getVertices(), DELTA);
		assertArrayEquals(new float[] {125.0f, 248.0f, 154.0f, 330.0f, 201.0f, 365.0f, 248.0f, 375.0f, 313.0f, 358.0f,
				365.0f, 299.0f, 374.0f, 234.0f, 346.0f, 170.0f, 285.0f, 130.0f, 206.0f, 133.0f, 150.0f, 175.0f},
				collisionInformation.collisionPolygons[1].getVertices(), DELTA);
	}
}
