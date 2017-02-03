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

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.graphics.Pixmap;

import junit.framework.Assert;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.Arrays;

public class CollisionDetectionPolygonCreationTest extends InstrumentationTestCase {
	protected Project project;
	protected Sprite sprite;

	public static LookData generateLookData(File testImage) {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
		return lookData;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}

	protected CollisionInformation generateCollisionInformation(int resID, String filename) {
		String rectangle125x125FileName = PhysicsTestUtils.getInternalImageFilenameFromFilename(filename);
		File file = null;
		try {
			file = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene()
							.getName(),
					rectangle125x125FileName, resID, getInstrumentation().getContext(),
					TestUtils.TYPE_IMAGE_FILE);
		} catch (Exception e) {
			Assert.fail("Couldn't load file, exception thrown!");
		}

		LookData lookData = generateLookData(file);
		sprite.getLookDataList().add(lookData);

		CollisionInformation collisionInformation = lookData.getCollisionInformation();
		collisionInformation.loadOrCreateCollisionPolygon();
		return collisionInformation;
	}

	public void testRectangle() {

		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw
				.rectangle_125x125, "rectangle_125x125.png");
		collisionInformation.printDebugCollisionPolygons();

		Assert.assertNotNull("CollsionPolygons is null", collisionInformation.collisionPolygons);
		Assert.assertEquals("Wrong amount of collisionPolygons", 1, collisionInformation.collisionPolygons.length);
		Assert.assertTrue("Wrong Collision Polygon",
				Arrays.equals(collisionInformation.collisionPolygons[0].getVertices(),
						new float[] { 0.0f, 0.0f, 0.0f, 125.0f, 125.0f, 125.0f, 125.0f, 0.0f }));
	}

	public void testSimpleConvexPolygon() {
		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw.complex_single_convex_polygon, "complex_single_convex_polygon.png");
		collisionInformation.printDebugCollisionPolygons();

		Assert.assertNotNull("CollsionPolygons is null", collisionInformation.collisionPolygons);
		Assert.assertEquals("Wrong amount of collisionPolygons", 1, collisionInformation.collisionPolygons.length);
		Assert.assertTrue("Wrong Collision Polygon",
				Arrays.equals(collisionInformation.collisionPolygons[0].getVertices(),
						new float[] { 0.0f, 47.0f, 17.0f, 98.0f, 52.0f, 98.0f, 68.0f, 44.0f, 52.0f, 0.0f, 17.0f, 0.0f }));
	}

	public void testMultipleConcavePolygons() {
		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw
				.multible_concave_polygons, "multible_concave_polygons.png");
		collisionInformation.printDebugCollisionPolygons();

		Assert.assertNotNull("CollsionPolygons is null", collisionInformation.collisionPolygons);
		Assert.assertEquals("Wrong amount of collisionPolygons", 2, collisionInformation.collisionPolygons.length);
		Assert.assertTrue("Wrong first Collision Polygon",
				Arrays.equals(collisionInformation.collisionPolygons[0].getVertices(),
						new float[] { 0.0f, 110.0f, 0.0f, 185.0f, 91.0f, 185.0f, 91.0f, 136.0f, 34.0f, 136.0f, 34.0f, 110.0f }));
		Assert.assertTrue("Wrong second Collision Polygon",
				Arrays.equals(collisionInformation.collisionPolygons[1].getVertices(),
						new float[] { 128.0f, 30.0f, 128.0f, 91.0f, 159.0f, 91.0f, 159.0f, 121.0f, 227.0f, 121.0f, 227.0f, 91.0f,
								257.0f, 91.0f, 257.0f, 30.0f, 227.0f, 30.0f, 227.0f, 0.0f, 159.0f, 0.0f, 159.0f, 30.0f }));
	}

	public void testDonutPolygons() {
		CollisionInformation collisionInformation = generateCollisionInformation(org.catrobat.catroid.test.R.raw.collision_donut,
				"collision_donut.png");
		collisionInformation.printDebugCollisionPolygons();

		Assert.assertNotNull("CollsionPolygons is null", collisionInformation.collisionPolygons);
		Assert.assertEquals("Wrong amount of collisionPolygons", 2, collisionInformation.collisionPolygons.length);
		Assert.assertTrue("Wrong first Collision Polygon",
				Arrays.equals(collisionInformation.collisionPolygons[0].getVertices(),
						new float[] { 0.0f, 228.0f, 9.0f, 321.0f, 57.0f, 411.0f, 136.0f, 474.0f, 228.0f, 500.0f, 305.0f,
								495.0f, 375.0f, 468.0f, 436.0f, 419.0f, 474.0f, 364.0f, 497.0f, 295.0f, 499.0f, 218.0f,
								481.0f, 151.0f, 443.0f, 89.0f, 385.0f, 38.0f, 321.0f, 9.0f, 179.0f, 9.0f, 115.0f, 38.0f,
								57.0f, 89.0f, 19.0f, 151.0f }));
		Assert.assertTrue("Wrong second Collision Polygon",
				Arrays.equals(collisionInformation.collisionPolygons[1].getVertices(),
						new float[] { 125.0f, 248.0f, 154.0f, 330.0f, 201.0f, 365.0f, 248.0f, 375.0f, 313.0f, 358.0f,
								365.0f, 299.0f, 374.0f, 234.0f, 346.0f, 170.0f, 285.0f, 130.0f, 206.0f, 133.0f, 150.0f,
								175.0f }));
	}
}
