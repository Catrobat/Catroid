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
package org.catrobat.catroid.test.physics;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilderStrategy;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilderStrategyFastHull;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeScaleUtils;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

public class PhysicsShapeScaleUtilsTest extends InstrumentationTestCase {

	private static final float DELTA = 0.001f;

	private static final int COMPLEX_SINGLE_CONVEX_POLYGON_RES_ID = R.raw.complex_single_convex_polygon;

	private PhysicsWorld physicsWorld;
	private File projectFile;
	private Project project;
	private PhysicsShapeBuilderStrategy strategy = new PhysicsShapeBuilderStrategyFastHull();
	private Shape[] complexSingleConvexPolygonShapes;
	static {
		GdxNativesLoader.load();
	}
	@Override
	public void setUp() throws Exception {
		super.setUp();

		physicsWorld = new PhysicsWorld(1920, 1600);
		physicsWorld.step(0.1f);
		projectFile = new File(Constants.DEFAULT_ROOT + File.separator + TestUtils.DEFAULT_TEST_PROJECT_NAME);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		String complexSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("complex_single_convex_polygon.png");
		File complexSingleConvexPolygonFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
				complexSingleConvexPolygonFileName, COMPLEX_SINGLE_CONVEX_POLYGON_RES_ID, getInstrumentation()
						.getContext(), TestUtils.TYPE_IMAGE_FILE);

		LookData complexSingleConvexPolygonLookData = PhysicsTestUtils.generateLookData(complexSingleConvexPolygonFile);
		Pixmap pixmap = complexSingleConvexPolygonLookData.getPixmap();
		complexSingleConvexPolygonShapes = strategy.build(pixmap, 1.0f);
	}

	@Override
	protected void tearDown() throws Exception {
		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		projectFile = null;
		physicsWorld = null;
		super.tearDown();
	}

	public void testShapeScaling() {
		Shape[] ninetyPercent = PhysicsShapeScaleUtils.scaleShapes(complexSingleConvexPolygonShapes, 0.9f);
		Shape[] oneHundredAndTenPercent = PhysicsShapeScaleUtils.scaleShapes(complexSingleConvexPolygonShapes, 1.1f);
		Shape[] oneHundredAndFortyPercent = PhysicsShapeScaleUtils.scaleShapes(ninetyPercent, 1.4f, 0.9f);
		Shape[] eightyPercent = PhysicsShapeScaleUtils.scaleShapes(oneHundredAndTenPercent, 0.8f, 1.1f);
		compareShapeSize(complexSingleConvexPolygonShapes, ninetyPercent, 0.9f);
		compareShapeSize(complexSingleConvexPolygonShapes, oneHundredAndTenPercent, 1.1f);
		compareShapeSize(complexSingleConvexPolygonShapes, oneHundredAndFortyPercent, 1.4f);
		compareShapeSize(complexSingleConvexPolygonShapes, eightyPercent, 0.8f);
	}

	public void testScaleCoordinate() {
		float coordinate = 100f;
		float expectedCoordinate = 50f;
		float actualCoordinate;
		float scaleFactor = 0.5f;
		Reflection.ParameterList parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals("Scaled coordinates not as expected.", expectedCoordinate, actualCoordinate, DELTA);

		coordinate = 500f;
		expectedCoordinate = 100f;
		scaleFactor = 0.2f;
		parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals("Scaled coordinates not as expected.", expectedCoordinate, actualCoordinate, DELTA);

		coordinate = 100;
		expectedCoordinate = 150f;
		scaleFactor = 1.5f;
		parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals("Scaled coordinates not as expected.", expectedCoordinate, actualCoordinate, DELTA);

		coordinate = 500;
		expectedCoordinate = 600f;
		scaleFactor = 1.2f;
		parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals("Scaled coordinates not as expected.", expectedCoordinate, actualCoordinate, DELTA);

		Vector2 coordinateVector = new Vector2(200, 400);
		Vector2 expectedCoordinateVector = new Vector2(50, 100);
		Vector2 actualCoordinateVector;
		scaleFactor = 0.25f;
		parameterList = new Reflection.ParameterList(coordinateVector, scaleFactor);
		actualCoordinateVector = (Vector2) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals("Scaled x coordinates not as expected.", expectedCoordinateVector.x, actualCoordinateVector.x,
				DELTA);
		assertEquals("Scaled y coordinates not as expected.", expectedCoordinateVector.y, actualCoordinateVector.y,
				DELTA);
	}

	private void compareShapeSize(Shape[] firstShapes, Shape[] secondShapes, float scaleFactor) {
		assertNotNull("Shapes (one) should not be null", firstShapes);
		assertNotNull("Shapes (two) should not be null", secondShapes);

		for (int idx = 0; idx < firstShapes.length; idx++) {
			Shape firstShape = firstShapes[idx];
			Shape secondShape = secondShapes[idx];
			if (firstShape.getType() == Shape.Type.Polygon) {
				int vertexCount = ((PolygonShape) firstShape).getVertexCount();
				for (int vertexIdx = 0; vertexIdx < vertexCount - 1; vertexIdx++) {
					Vector2 vertexOne = new Vector2();
					((PolygonShape) firstShape).getVertex(vertexIdx, vertexOne);
					Vector2 vertexTwo = new Vector2();
					((PolygonShape) firstShape).getVertex(vertexIdx + 1, vertexTwo);
					float firstShapeVertexDistance = vertexOne.dst(vertexTwo);

					vertexOne = new Vector2();
					((PolygonShape) secondShape).getVertex(vertexIdx, vertexOne);
					vertexTwo = new Vector2();
					((PolygonShape) secondShape).getVertex(vertexIdx + 1, vertexTwo);
					float secondShapeVertexDistance = vertexOne.dst(vertexTwo);

					assertEquals("distance between vertices of shapes have not the correct relation",
							firstShapeVertexDistance, secondShapeVertexDistance * (1 / scaleFactor), DELTA);
				}
			} else {
				assertTrue("There should be no other type than Polygon", false);
			}
		}
	}
}
