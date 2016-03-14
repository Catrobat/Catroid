/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.physics;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

public class PhysicsShapeBuilderTest extends InstrumentationTestCase {

	private static final String TAG = PhysicsShapeBuilderTest.class.getSimpleName();

	private PhysicsShapeBuilder physicsShapeBuilder;
	private PhysicsWorld physicsWorld;
	private PhysicsLook physicsLook;
	private Project project;
	private File projectFile;
	private static final int SIMPLE_SINGLE_CONVEX_POLYGON_RES_ID = R.raw.rectangle_125x125;
	private File simpleSingleConvexPolygonFile;

	private static final int COMPLEX_SINGLE_CONVEX_POLYGON_RES_ID = R.raw.complex_single_convex_polygon;
	private File complexSingleConvexPolygonFile;

	private Sprite sprite;

	static {
		GdxNativesLoader.load();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		physicsWorld = new PhysicsWorld(1920, 1600);
		projectFile = new File(Constants.DEFAULT_ROOT + File.separator + TestUtils.DEFAULT_TEST_PROJECT_NAME);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		physicsShapeBuilder = PhysicsShapeBuilder.getInstance();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		String simpleSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("simple_single_convex_polygon.png");
		simpleSingleConvexPolygonFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				simpleSingleConvexPolygonFileName, SIMPLE_SINGLE_CONVEX_POLYGON_RES_ID, getInstrumentation()
						.getContext(), TestUtils.TYPE_IMAGE_FILE);

		String complexSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("complex_single_convex_polygon.png");
		complexSingleConvexPolygonFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				complexSingleConvexPolygonFileName, COMPLEX_SINGLE_CONVEX_POLYGON_RES_ID, getInstrumentation()
						.getContext(), TestUtils.TYPE_IMAGE_FILE);

		sprite = new Sprite("TestSprite");

		physicsLook = new PhysicsLook(sprite, physicsWorld);
	}

	@Override
	protected void tearDown() throws Exception {
		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		physicsShapeBuilder.reset();
		projectFile = null;
		super.tearDown();
	}

	public void testSimpleSingleConvexPolygon() {
		LookData lookData = PhysicsTestUtils.generateLookData(simpleSingleConvexPolygonFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getScaledShapes(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		int expectedPolynoms = 1;
		int[] expectedVertices = { 4 };
		checkBuiltShapes(shapes, expectedPolynoms, expectedVertices);
	}

	public void testDifferentAccuracySettings() {
		LookData lookData = PhysicsTestUtils.generateLookData(complexSingleConvexPolygonFile);
		physicsLook.setLookData(lookData);

		float[] accuracyLevels = (float[]) Reflection.getPrivateField(PhysicsShapeBuilder.class, "ACCURACY_LEVELS");

		Shape[] lowerAccuracyShapes = physicsShapeBuilder.getScaledShapes(lookData, accuracyLevels[0]);
		Shape[] lowestAccuracyShapes = lowerAccuracyShapes;
		Shape[] highestAccuracyShapes = null;
		for (int accuracyIdx = 1; accuracyIdx < accuracyLevels.length; accuracyIdx++) {
			Shape[] higherAccuracyShapes = physicsShapeBuilder.getScaledShapes(lookData, accuracyLevels[accuracyIdx]);
			assertTrue("lower accuracy must have less or equal shapes than higher accuracy", lowerAccuracyShapes
					.length <= higherAccuracyShapes.length);
			lowerAccuracyShapes = higherAccuracyShapes;
			highestAccuracyShapes = higherAccuracyShapes;
		}
		assertTrue("lower accuracy must have less shapes than higher accuracy", lowestAccuracyShapes.length <
				highestAccuracyShapes.length);
	}

	private void checkBuiltShapes(Shape[] shapes, int expectedPolynomCount, int[] expectedVertices) {
		boolean debug = false;

		assertNotNull("Shapes should not be null", shapes);

		if (!debug) {
			assertEquals("Polynom count is not correct", expectedPolynomCount, shapes.length);
		}
		if (!debug) {
			assertEquals("The array expectedVertices must have length of expectedPolynomCount", expectedPolynomCount,
					expectedVertices.length);
		}

		for (int idx = 0; idx < shapes.length; idx++) {
			Shape shape = shapes[idx];
			switch (shape.getType()) {
				case Chain:
					Log.d(TAG, "type = Chain: ");
					break;
				case Circle:
					Log.d(TAG, "type = Circle: ");
					break;
				case Edge:
					Log.d(TAG, "type = Edge: ");
					break;
				case Polygon:
					int vertexCount = ((PolygonShape) shape).getVertexCount();
					Log.d(TAG, "type = Polygon: " + vertexCount);
					for (int vertexIdx = 0; vertexIdx < vertexCount; vertexIdx++) {
						Vector2 vertex = new Vector2();
						((PolygonShape) shape).getVertex(vertexIdx, vertex);
						Log.d(TAG, "x=" + vertex.x + ";y=" + vertex.y);
					}
					if (!debug) {
						assertEquals("vertex count is not correct", expectedVertices[idx], vertexCount);
					}
					break;
			}
		}
	}
}
