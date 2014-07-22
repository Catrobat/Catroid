/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

/**
 * TODO: Write Test cases.
 */
public class PhysicsShapeBuilderTest extends InstrumentationTestCase {

	private static final String TAG = PhysicsShapeBuilderTest.class.getSimpleName();

	private PhysicsShapeBuilder physicsShapeBuilder;
	private PhysicsWorld physicsWorld;
	private PhysicsLook physicsLook;
	private Project project;
	private File projectFile;
	private String simpleSingleConvexPolygonFileName;
	private String complexSingleConvexPolygonFileName;
	private String multibleConvexPolygonsFileName;
	private String singleConcavePolygonFileName;
	private String multibleConcavePolygonsFileName;
	private String multibleMixedPolygonsFileName;
	private static final int SIMPLE_SINGLE_CONVEX_POLYGON_RES_ID = R.raw.rectangle_125x125;
	private static final int COMPLEX_SINGLE_CONVEX_POLYGON_RES_ID = R.raw.complex_single_convex_polygon;
	private static final int MULTIBLE_CONVEX_POLYGON_RES_ID = R.raw.multible_convex_polygons;
	private static final int SINGLE_CONCAVE_POLYGON_RES_ID = R.raw.single_concave_polygon;
	private static final int MULTIBLE_CONVEX_POLYGONS_RES_ID = R.raw.multible_concave_polygons;
	private static final int MULTIBLE_MIXED_POLYGONS_RES_ID = R.raw.multible_mixed_polygons;
	private File simpleSingleConvexPolygonFile;
	private File complexSingleConvexPolygonFile;
	private File multibleConvexPolygonsFile;
	private File singleConcavePolygonFile;
	private File multibleConcavePolygonsFile;
	private File multibleMixedPolygonsFile;
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

		physicsShapeBuilder = new PhysicsShapeBuilder();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		simpleSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("simple_single_convex_polygon.png");
		complexSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("complex_single_convex_polygon.png");
		multibleConvexPolygonsFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("multible_convex_polygons.png");
		singleConcavePolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("single_concave_polygon.png");
		multibleConcavePolygonsFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("multible_concave_polygons.png");
		multibleMixedPolygonsFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("multible_mixed_polygons.png");

		simpleSingleConvexPolygonFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				simpleSingleConvexPolygonFileName, SIMPLE_SINGLE_CONVEX_POLYGON_RES_ID, getInstrumentation()
						.getContext(), TestUtils.TYPE_IMAGE_FILE);
		complexSingleConvexPolygonFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				complexSingleConvexPolygonFileName, COMPLEX_SINGLE_CONVEX_POLYGON_RES_ID, getInstrumentation()
						.getContext(), TestUtils.TYPE_IMAGE_FILE);
		multibleConvexPolygonsFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				multibleConvexPolygonsFileName, MULTIBLE_CONVEX_POLYGON_RES_ID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);
		singleConcavePolygonFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				singleConcavePolygonFileName, SINGLE_CONCAVE_POLYGON_RES_ID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);
		multibleConcavePolygonsFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				multibleConcavePolygonsFileName, MULTIBLE_CONVEX_POLYGONS_RES_ID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);
		multibleMixedPolygonsFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				multibleMixedPolygonsFileName, MULTIBLE_MIXED_POLYGONS_RES_ID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);

		sprite = new Sprite("TestSprite");

		physicsShapeBuilder = new PhysicsShapeBuilder();
		physicsLook = new PhysicsLook(sprite, physicsWorld);
	}

	@Override
	protected void tearDown() throws Exception {
		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		projectFile = null;
		super.tearDown();
	}

	public void testShapingOfNoImage() {
		Shape[] shapes = physicsShapeBuilder.getShape(physicsLook.getLookData(),
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertNull("initial value is not null", shapes);

		LookData lookData = PhysicsTestUtils.generateLookData();
		sprite.getLookDataList().add(lookData);


		shapes = physicsShapeBuilder.getShape(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
		assertNull("value of no image shaping is not null",shapes);
	}

	public void testSimpleSingleConvexPolygon() {
		LookData lookData = PhysicsTestUtils.generateLookData(simpleSingleConvexPolygonFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		int expectedPolynoms = 1;
		int[] expectedVertices = { 4 };
		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	}

	//	public void testComplexSingleConvexPolygon() {
	//		// TODO[Physics] rework or delete (algorithm works different)
	//		LookData lookData = PhysicsTestUtils.generateLookData(complexSingleConvexPolygonFile);
	//		physicsLook.setLookData(lookData);
	//
	//		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
	//				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
	//
	//		int expectedPolynoms = 1;
	//		int[] expectedVertices = { 6 };
	//		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	//	}
	//
	//	public void testMultibleConvexPolygons() {
	//		// TODO[Physics] rework or delete (algorithm detects no multiple)
	//		LookData lookData = PhysicsTestUtils.generateLookData(multibleConvexPolygonsFile);
	//		physicsLook.setLookData(lookData);
	//
	//		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
	//				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
	//
	//		int expectedPolynoms = 2;
	//		int[] expectedVertices = { 4, 4 };
	//		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	//	}
	//
	//	public void testSingleConcavePolygon() {
	//		// TODO[Physics] rework or delete (algorithm detects no concave polygons)
	//		LookData lookData = PhysicsTestUtils.generateLookData(singleConcavePolygonFile);
	//		physicsLook.setLookData(lookData);
	//
	//		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
	//				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
	//
	//		int expectedPolynoms = 1;
	//		int[] expectedVertices = { 12 };
	//		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	//	}
	//
	//	public void testMultibleConcavePolygons() {
	//		// TODO[Physics] rework or delete (algorithm detects no multiple)
	//		LookData lookData = PhysicsTestUtils.generateLookData(multibleConvexPolygonsFile);
	//		physicsLook.setLookData(lookData);
	//
	//		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
	//				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
	//
	//		int expectedPolynoms = 2;
	//		int[] expectedVertices = { 6, 12 };
	//		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	//	}
	//
	//	public void testMultibleMixedPolygons() {
	//		// TODO[Physics] rework or delete (algorithm detects no multiple)
	//		LookData lookData = PhysicsTestUtils.generateLookData(multibleMixedPolygonsFile);
	//		physicsLook.setLookData(lookData);
	//
	//		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
	//				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
	//
	//		int expectedPolynoms = 2;
	//		int[] expectedVertices = { 4, 12 };
	//		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	//	}

	private void checkBuildedShapes(Shape[] shapes, int expectedPolynomCount, int[] expectedVertices) {
		boolean debug = false;

		assertNotNull("shapes should not be null", shapes);

		if (!debug) {
			assertEquals("polynomCount is not correct", expectedPolynomCount, shapes.length);
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
