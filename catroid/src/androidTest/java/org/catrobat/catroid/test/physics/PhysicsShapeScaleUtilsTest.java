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
package org.catrobat.catroid.test.physics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilderStrategy;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilderStrategyFastHull;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeScaleUtils;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(AndroidJUnit4.class)
public class PhysicsShapeScaleUtilsTest {

	private static final float DELTA = 0.001f;

	private PhysicsWorld physicsWorld;
	private File projectDir;
	private Project project;
	private PhysicsShapeBuilderStrategy strategy = new PhysicsShapeBuilderStrategyFastHull();
	private Shape[] complexSingleConvexPolygonShapes;
	static {
		GdxNativesLoader.load();
	}
	@Before
	public void setUp() throws Exception {

		physicsWorld = new PhysicsWorld(1920, 1600);
		physicsWorld.step(0.1f);
		projectDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, TestUtils.DEFAULT_TEST_PROJECT_NAME);

		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}

		project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		XstreamSerializer.getInstance().saveProject(project);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);

		String complexSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("complex_single_convex_polygon.png");

		File complexSingleConvexPolygonFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				R.raw.complex_single_convex_polygon,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				complexSingleConvexPolygonFileName,
				1);

		LookData complexSingleConvexPolygonLookData = PhysicsTestUtils.generateLookData(complexSingleConvexPolygonFile);
		Pixmap pixmap = complexSingleConvexPolygonLookData.getPixmap();
		complexSingleConvexPolygonShapes = strategy.build(pixmap, 1.0f);
	}

	@After
	public void tearDown() throws Exception {
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
		projectDir = null;
		physicsWorld = null;
	}

	@Test
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

	@Test
	public void testScaleCoordinate() throws Exception {
		float coordinate = 100f;
		float expectedCoordinate = 50f;
		float actualCoordinate;
		float scaleFactor = 0.5f;
		Reflection.ParameterList parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals(expectedCoordinate, actualCoordinate, DELTA);

		coordinate = 500f;
		expectedCoordinate = 100f;
		scaleFactor = 0.2f;
		parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals(expectedCoordinate, actualCoordinate, DELTA);

		coordinate = 100;
		expectedCoordinate = 150f;
		scaleFactor = 1.5f;
		parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals(expectedCoordinate, actualCoordinate, DELTA);

		coordinate = 500;
		expectedCoordinate = 600f;
		scaleFactor = 1.2f;
		parameterList = new Reflection.ParameterList(coordinate, scaleFactor);
		actualCoordinate = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals(expectedCoordinate, actualCoordinate, DELTA);

		Vector2 coordinateVector = new Vector2(200, 400);
		Vector2 expectedCoordinateVector = new Vector2(50, 100);
		Vector2 actualCoordinateVector;
		scaleFactor = 0.25f;
		parameterList = new Reflection.ParameterList(coordinateVector, scaleFactor);
		actualCoordinateVector = (Vector2) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
				parameterList);
		assertEquals(expectedCoordinateVector.x, actualCoordinateVector.x, DELTA);
		assertEquals(expectedCoordinateVector.y, actualCoordinateVector.y, DELTA);
	}

	private void compareShapeSize(Shape[] firstShapes, Shape[] secondShapes, float scaleFactor) {
		assertNotNull(firstShapes);
		assertNotNull(secondShapes);

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

					assertEquals(firstShapeVertexDistance, secondShapeVertexDistance * (1 / scaleFactor), DELTA);
				}
			}
		}
	}
}
