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

import android.graphics.Bitmap;
import android.test.InstrumentationTestCase;

import com.badlogic.gdx.math.Polygon;

import junit.framework.Assert;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.sensing.CollisionPolygonVertex;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class CollisionInformationTest extends InstrumentationTestCase {
	public void testCheckMetaString() {
		String isNull = null;
		Assert.assertFalse("Null string returned true", CollisionInformation.checkMetaDataString(isNull));
		String empty = "";
		Assert.assertFalse("Empty string returned true", CollisionInformation.checkMetaDataString(empty));
		String faulty1 = "1.0;1.0;1.0";
		Assert.assertFalse("Faulty string returned true", CollisionInformation.checkMetaDataString(faulty1));
		String faulty2 = "1.0;1.0;1.0;1.0;1.0;1.0|";
		Assert.assertFalse("Faulty string returned true", CollisionInformation.checkMetaDataString(faulty2));
		String faulty3 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0";
		Assert.assertFalse("Faulty string returned true", CollisionInformation.checkMetaDataString(faulty3));
		String faulty4 = "|1.0;1.0;1.0;1.0;1.0;1.0";
		Assert.assertFalse("Faulty string returned true", CollisionInformation.checkMetaDataString(faulty4));
		String faulty5 = "1.0;1.0;1.0;1.0;1.0,1.0";
		Assert.assertFalse("Faulty string returned true", CollisionInformation.checkMetaDataString(faulty5));
		String faulty6 = "1.0;1.0;1.0;1.0;1.0;1.0||1.0;1.0;1.0;1.0;1.0;1.0";
		Assert.assertFalse("Faulty string returned true", CollisionInformation.checkMetaDataString(faulty6));
		String faulty7 = "1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0";
		Assert.assertFalse("Faulty string returned true", CollisionInformation.checkMetaDataString(faulty7));

		String correct1 = "1.0;1.0;1.0;1.0;1.0;1.0";
		Assert.assertTrue("Correct string returned false", CollisionInformation.checkMetaDataString(correct1));
		String correct2 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0";
		Assert.assertTrue("Correct string returned false", CollisionInformation.checkMetaDataString(correct2));
		String correct3 = "1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0";
		Assert.assertTrue("Correct string returned false", CollisionInformation.checkMetaDataString(correct3));
		String correct4 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0";
		Assert.assertTrue("Correct string returned false", CollisionInformation.checkMetaDataString(correct4));
	}

	public void testCreateCollisionPolygonByHitbox() {
		Bitmap bitmap = Bitmap.createBitmap(200, 100, Bitmap.Config.ALPHA_8);
		Polygon[] polygons = CollisionInformation.createCollisionPolygonByHitbox(bitmap);
		Assert.assertTrue("Wrong vertices calculated", Arrays.equals(polygons[0].getVertices(),
				new float[] {0.0f, 0.0f, 200.0f, 0.0f, 200.0f, 100.0f, 0.0f, 100.0f}));
	}

	public void testGetCollisionPolygonFromPNGMeta() {
		final int resourceId = org.catrobat.catroid.test.R.raw.polygon_in_file;
		TestUtils.deleteTestProjects();

		Project project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		String filename = PhysicsTestUtils.getInternalImageFilenameFromFilename("polygon_in_file.png");
		File file = null;
		try {
			file = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
					filename, resourceId, getInstrumentation().getContext(),
					TestUtils.TYPE_IMAGE_FILE);
		} catch (Exception e) {
			Assert.fail("Couldn't load file, exception thrown!");
		}

		Polygon[] collisionPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.getAbsolutePath());

		Assert.assertNotNull("CollsionPolygons is null", collisionPolygons);
		Assert.assertTrue("CollisionPolygons length is 0", collisionPolygons.length != 0);
		Assert.assertEquals("Wrong amount of collisionPolygons", 1, collisionPolygons.length);
		Assert.assertTrue("Wrong Collision Polygon",
				Arrays.equals(collisionPolygons[0].getVertices(),
						new float[] {0.0f, 47.0f, 17.0f, 98.0f, 52.0f, 98.0f, 68.0f, 44.0f, 52.0f, 0.0f, 17.0f, 0.0f}));
	}

	public void testWriteReadCollisionVerticesToPNGMeta() {
		TestUtils.deleteTestProjects();

		Project project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		String filename = "collision_donut.png";
		int resourceId = org.catrobat.catroid.test.R.raw.collision_donut;
		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;
		File file = null;
		try {
			file = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
					hashedFileName, resourceId, getInstrumentation().getContext(),
					TestUtils.TYPE_IMAGE_FILE);
		} catch (Exception e) {
			Assert.fail("Couldn't load file, exception thrown!");
		}

		float[] firstVertices = new float[] {0.0f, 0.0f, 111.0f, 0.0f, 111.0f, 222.0f};
		float[] secondVertices = new float[] {10.0f, 10.0f, 20.0f, 10.0f, 20.0f, 20.0f, 10.0f, 20.0f};
		Polygon[] polygons = {new Polygon(firstVertices), new Polygon(secondVertices)};
		CollisionInformation.writeCollisionVerticesToPNGMeta(polygons, file.getAbsolutePath());
		Polygon[] testPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.getAbsolutePath());

		boolean sameVertices = Arrays.equals(testPolygons[0].getVertices(), firstVertices)
				&& Arrays.equals(testPolygons[1].getVertices(), secondVertices);
		Assert.assertTrue("Not the same vertices have been read from the file! ", sameVertices);
	}

	public void testWriteReadEmptyCollisionVerticesToPNGMeta() {
		TestUtils.deleteTestProjects();

		Project project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		String filename = "collision_donut.png";
		int resourceId = org.catrobat.catroid.test.R.raw.collision_donut;
		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;
		File file = null;
		try {
			file = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
					hashedFileName, resourceId, getInstrumentation().getContext(),
					TestUtils.TYPE_IMAGE_FILE);
		} catch (Exception e) {
			Assert.fail("Couldn't load file, exception thrown!");
		}

		Polygon[] polygons = new Polygon[0];
		CollisionInformation.writeCollisionVerticesToPNGMeta(polygons, file.getAbsolutePath());
		Polygon[] testPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.getAbsolutePath());

		Assert.assertTrue("Test polygon size is not 0! Reading and/or writing failed", testPolygons.length == 0);
	}

	private float[] getFloatArrayFromCollisionPolygonVertexArrayList(ArrayList<CollisionPolygonVertex> arrayList) {
		float[] array = new float[arrayList.size() * 4];
		for (int i = 0; i < arrayList.size(); i++) {
			array[i * 4] = arrayList.get(i).startX;
			array[i * 4 + 1] = arrayList.get(i).startY;
			array[i * 4 + 2] = arrayList.get(i).endX;
			array[i * 4 + 3] = arrayList.get(i).endY;
		}
		return array;
	}

	public void testCreateHorizontalAndVerticalVertices() {
		boolean[][] grid = new boolean[][] {{false, false, true, true, true, false, false},
				{false, false, true, false, true, false, false},
				{true, true, true, true, true, true, true},
				{true, false, true, false, true, false, true},
				{true, true, true, true, true, true, true},
				{false, false, true, false, true, false, false},
				{false, false, true, true, true, false, false}};
		int width = grid.length;
		int height = grid[0].length;
		float[] horizontalCorrect = new float[] {2.0f, 0.0f, 5.0f, 0.0f, 3.0f, 1.0f, 4.0f, 1.0f, 0.0f, 2.0f, 2.0f, 2.0f,
				1.0f, 3.0f, 2.0f, 3.0f, 3.0f, 2.0f, 4.0f, 2.0f, 3.0f, 3.0f, 4.0f, 3.0f, 5.0f, 2.0f, 7.0f, 2.0f, 5.0f,
				3.0f, 6.0f, 3.0f, 0.0f, 5.0f, 2.0f, 5.0f, 1.0f, 4.0f, 2.0f, 4.0f, 3.0f, 4.0f, 4.0f, 4.0f, 3.0f, 5.0f,
				4.0f, 5.0f, 5.0f, 4.0f, 6.0f, 4.0f, 5.0f, 5.0f, 7.0f, 5.0f, 2.0f, 7.0f, 5.0f, 7.0f, 3.0f, 6.0f, 4.0f,
				6.0f};
		float[] verticalCorrect = new float[] {0.0f, 2.0f, 0.0f, 5.0f, 1.0f, 3.0f, 1.0f, 4.0f, 2.0f, 0.0f, 2.0f, 2.0f,
				3.0f, 1.0f, 3.0f, 2.0f, 2.0f, 3.0f, 2.0f, 4.0f, 3.0f, 3.0f, 3.0f, 4.0f, 2.0f, 5.0f, 2.0f, 7.0f, 3.0f,
				5.0f, 3.0f, 6.0f, 5.0f, 0.0f, 5.0f, 2.0f, 4.0f, 1.0f, 4.0f, 2.0f, 4.0f, 3.0f, 4.0f, 4.0f, 5.0f, 3.0f,
				5.0f, 4.0f, 4.0f, 5.0f, 4.0f, 6.0f, 5.0f, 5.0f, 5.0f, 7.0f, 7.0f, 2.0f, 7.0f, 5.0f, 6.0f, 3.0f, 6.0f,
				4.0f};

		ArrayList<CollisionPolygonVertex> horizontal = CollisionInformation.createHorizontalVertices(grid, width,
				height);
		ArrayList<CollisionPolygonVertex> vertical = CollisionInformation.createVerticalVertices(grid, width, height);
		float[] horizontalTest = getFloatArrayFromCollisionPolygonVertexArrayList(horizontal);
		float[] verticalTest = getFloatArrayFromCollisionPolygonVertexArrayList(vertical);

		Assert.assertEquals("Horizontal size not matching", horizontalCorrect.length, horizontalTest.length);
		Assert.assertEquals("Vertical size not matching", verticalCorrect.length, verticalTest.length);
		Assert.assertTrue("Horizontal vertices wrongly calculated", Arrays.equals(horizontalTest, horizontalCorrect));
		Assert.assertTrue("Vertical vertices wrongly calculated", Arrays.equals(verticalTest, verticalCorrect));
	}
}
