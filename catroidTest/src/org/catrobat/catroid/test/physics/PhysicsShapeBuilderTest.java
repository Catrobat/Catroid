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
import org.catrobat.catroid.physic.PhysicsLook;
import org.catrobat.catroid.physic.PhysicsWorld;
import org.catrobat.catroid.physic.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

/**
 * TODO: Write Test cases.
 */
public class PhysicsShapeBuilderTest extends InstrumentationTestCase {

	private PhysicsShapeBuilder physicsShapeBuilder;
	private PhysicsWorld physicsWorld;
	private PhysicsLook physicsLook;
	private Project project;
	private String simpleSingleConvexPolygonFileName;
	private String complexSingleConvexPolygonFileName;
	private String multibleConvexPolygonsFileName;
	private String singleConcavePolygonFileName;
	private String multibleConcavePolygonsFileName;
	private String multibleMixedPolygonsFileName;
	private static final int SIMPLE_SINGLE_CONVEX_POLYGON_RES_ID = R.raw.simple_single_convex_polygon;
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
		physicsWorld = new PhysicsWorld(1920, 1600);
		File projectFile = new File(Constants.DEFAULT_ROOT + File.separator + TestUtils.DEFAULT_TEST_PROJECT_NAME);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		physicsShapeBuilder = new PhysicsShapeBuilder();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		simpleSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("single_convex_polygon.png");
		complexSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("complex_convex_polygon.png");
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

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShapingOfNoImage() {
		Shape[] shapes = physicsShapeBuilder.getShape(physicsLook.getLookData(),
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertNull("initial value is not null", shapes);

		LookData lookData = PhysicsTestUtils.generateLookData();
		sprite.getLookDataList().add(lookData);

		try {
			shapes = physicsShapeBuilder.getShape(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
			fail("does not exit with an null pointer exception");
		} catch (NullPointerException e) {
			// expected behavior
		}

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

	public void testComplexSingleConvexPolygon() {
		LookData lookData = PhysicsTestUtils.generateLookData(complexSingleConvexPolygonFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		int expectedPolynoms = 1;
		int[] expectedVertices = { 6 };
		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	}

	public void testMultibleConvexPolygons() {
		LookData lookData = PhysicsTestUtils.generateLookData(multibleConvexPolygonsFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		int expectedPolynoms = 2;
		int[] expectedVertices = { 4, 4 };
		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	}

	public void testSingleConcavePolygon() {
		LookData lookData = PhysicsTestUtils.generateLookData(singleConcavePolygonFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		int expectedPolynoms = 1;
		int[] expectedVertices = { 12 };
		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	}

	public void testMultibleConcavePolygons() {
		LookData lookData = PhysicsTestUtils.generateLookData(multibleConvexPolygonsFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		int expectedPolynoms = 2;
		int[] expectedVertices = { 6, 12 };
		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	}

	public void testMultibleMixedPolygons() {
		LookData lookData = PhysicsTestUtils.generateLookData(multibleMixedPolygonsFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getShape(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		int expectedPolynoms = 2;
		int[] expectedVertices = { 4, 12 };
		checkBuildedShapes(shapes, expectedPolynoms, expectedVertices);
	}

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
					Log.d("phill_test", "type = Chain: ");
					break;
				case Circle:
					Log.d("phill_test", "type = Circle: ");
					break;
				case Edge:
					Log.d("phill_test", "type = Edge: ");
					break;
				case Polygon:
					int vertexCount = ((PolygonShape) shape).getVertexCount();
					Log.d("phill_test", "type = Polygon: " + vertexCount);
					for (int vertexIdx = 0; vertexIdx < vertexCount; vertexIdx++) {
						Vector2 vertex = new Vector2();
						((PolygonShape) shape).getVertex(vertexIdx, vertex);
						Log.d("phill_test", "x=" + vertex.x + ";y=" + vertex.y);
					}
					if (!debug) {
						assertEquals("vertex count is not correct", expectedVertices[idx], vertexCount);
					}
					break;
			}
		}
	}
}
