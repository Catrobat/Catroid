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

import android.graphics.Bitmap;

import com.badlogic.gdx.math.Polygon;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.sensing.CollisionPolygonVertex;
import org.catrobat.catroid.test.physics.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class CollisionInformationTest {
	private static final float DELTA = Float.MIN_VALUE;

	@Test
	public void testCheckMetaString() {
		String isNull = null;
		assertFalse(CollisionInformation.checkMetaDataString(isNull));
		String empty = "";
		assertFalse(CollisionInformation.checkMetaDataString(empty));
		String faulty1 = "1.0;1.0;1.0";
		assertFalse(CollisionInformation.checkMetaDataString(faulty1));
		String faulty2 = "1.0;1.0;1.0;1.0;1.0;1.0|";
		assertFalse(CollisionInformation.checkMetaDataString(faulty2));
		String faulty3 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0";
		assertFalse(CollisionInformation.checkMetaDataString(faulty3));
		String faulty4 = "|1.0;1.0;1.0;1.0;1.0;1.0";
		assertFalse(CollisionInformation.checkMetaDataString(faulty4));
		String faulty5 = "1.0;1.0;1.0;1.0;1.0,1.0";
		assertFalse(CollisionInformation.checkMetaDataString(faulty5));
		String faulty6 = "1.0;1.0;1.0;1.0;1.0;1.0||1.0;1.0;1.0;1.0;1.0;1.0";
		assertFalse(CollisionInformation.checkMetaDataString(faulty6));
		String faulty7 = "1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0";
		assertFalse(CollisionInformation.checkMetaDataString(faulty7));

		String correct1 = "1.0;1.0;1.0;1.0;1.0;1.0";
		assertTrue(CollisionInformation.checkMetaDataString(correct1));
		String correct2 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0";
		assertTrue(CollisionInformation.checkMetaDataString(correct2));
		String correct3 = "1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0";
		assertTrue(CollisionInformation.checkMetaDataString(correct3));
		String correct4 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0";
		assertTrue(CollisionInformation.checkMetaDataString(correct4));
	}

	@Test
	public void testCreateCollisionPolygonByHitbox() {
		Bitmap bitmap = Bitmap.createBitmap(200, 100, Bitmap.Config.ALPHA_8);
		Polygon[] polygons = CollisionInformation.createCollisionPolygonByHitbox(bitmap);
		assertArrayEquals(new float[] {0.0f, 0.0f, 200.0f, 0.0f, 200.0f, 100.0f, 0.0f, 100.0f},
				polygons[0].getVertices(), DELTA);
	}

	@Test
	public void testGetCollisionPolygonFromPNGMeta() throws IOException {
		TestUtils.deleteProjects();

		Project project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		String filename = PhysicsTestUtils.getInternalImageFilenameFromFilename("polygon_in_file.png");

		File file = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.polygon_in_file,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				filename,
				1);

		Polygon[] collisionPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.getAbsolutePath());

		assertNotNull(collisionPolygons);
		assertThat(collisionPolygons.length, is(greaterThan(0)));
		assertEquals(1, collisionPolygons.length);
		assertArrayEquals(new float[] {0.0f, 47.0f, 17.0f, 98.0f, 52.0f, 98.0f, 68.0f, 44.0f, 52.0f, 0.0f, 17.0f, 0.0f},
				collisionPolygons[0].getVertices(), DELTA);
	}

	@Test
	public void testWriteReadCollisionVerticesToPNGMeta() throws IOException {
		TestUtils.deleteProjects();

		Project project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		String filename = "collision_donut.png";
		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;

		File file = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.collision_donut,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				hashedFileName,
				1);

		float[] firstVertices = new float[] {0.0f, 0.0f, 111.0f, 0.0f, 111.0f, 222.0f};
		float[] secondVertices = new float[] {10.0f, 10.0f, 20.0f, 10.0f, 20.0f, 20.0f, 10.0f, 20.0f};
		Polygon[] polygons = {new Polygon(firstVertices), new Polygon(secondVertices)};
		CollisionInformation.writeCollisionVerticesToPNGMeta(polygons, file.getAbsolutePath());
		Polygon[] testPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.getAbsolutePath());

		boolean sameVertices = Arrays.equals(testPolygons[0].getVertices(), firstVertices)
				&& Arrays.equals(testPolygons[1].getVertices(), secondVertices);
		assertTrue(sameVertices);
	}

	@Test
	public void testWriteReadEmptyCollisionVerticesToPNGMeta() throws IOException {
		TestUtils.deleteProjects();

		Project project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		String filename = "collision_donut.png";
		String hashedFileName = Utils.md5Checksum(filename) + "_" + filename;

		File file = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.collision_donut,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				hashedFileName,
				1);

		Polygon[] polygons = new Polygon[0];
		CollisionInformation.writeCollisionVerticesToPNGMeta(polygons, file.getAbsolutePath());
		Polygon[] testPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.getAbsolutePath());

		assertEquals(0, testPolygons.length);
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

	@Test
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

		assertEquals(horizontalCorrect.length, horizontalTest.length);
		assertEquals(verticalCorrect.length, verticalTest.length);
		assertArrayEquals(horizontalCorrect, horizontalTest, DELTA);
		assertArrayEquals(verticalCorrect, verticalTest, DELTA);
	}
}
