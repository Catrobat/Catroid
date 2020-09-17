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
package org.catrobat.catroid.test.physics;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PhysicsShapeBuilderTest {

	private PhysicsShapeBuilder physicsShapeBuilder;
	private PhysicsWorld physicsWorld;
	private PhysicsLook physicsLook;
	private Project project;
	private File projectDir;
	private File simpleSingleConvexPolygonFile;

	private File complexSingleConvexPolygonFile;

	private Sprite sprite;
	static {
		GdxNativesLoader.load();
	}
	@Before
	public void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
		projectDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, TestUtils.DEFAULT_TEST_PROJECT_NAME);

		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}

		physicsShapeBuilder = PhysicsShapeBuilder.getInstance();

		project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		String simpleSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("simple_single_convex_polygon.png");

		simpleSingleConvexPolygonFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				R.raw.rectangle_125x125,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				simpleSingleConvexPolygonFileName,
				1);

		String complexSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("complex_single_convex_polygon.png");

		complexSingleConvexPolygonFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				R.raw.complex_single_convex_polygon,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				complexSingleConvexPolygonFileName,
				1);

		sprite = new Sprite("TestSprite");

		physicsLook = new PhysicsLook(sprite, physicsWorld);
	}

	@After
	public void tearDown() throws Exception {
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
		physicsShapeBuilder.reset();
		projectDir = null;
	}

	@Test
	public void testSimpleSingleConvexPolygon() {
		LookData lookData = PhysicsTestUtils.generateLookData(simpleSingleConvexPolygonFile);
		physicsLook.setLookData(lookData);

		Shape[] shapes = physicsShapeBuilder.getScaledShapes(lookData,
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
		assertNotNull(shapes);
		assertEquals(1, shapes.length);

		Shape shape = shapes[0];
		assertEquals(Shape.Type.Polygon, shape.getType());

		int expectedVertices = 4;
		assertEquals(expectedVertices, ((PolygonShape) shape).getVertexCount());
	}

	@Test
	public void testDifferentAccuracySettings() throws Exception {
		LookData lookData = PhysicsTestUtils.generateLookData(complexSingleConvexPolygonFile);
		physicsLook.setLookData(lookData);

		float[] accuracyLevels = (float[]) Reflection.getPrivateField(PhysicsShapeBuilder.class, "ACCURACY_LEVELS");

		Shape[] lowerAccuracyShapes = physicsShapeBuilder.getScaledShapes(lookData, accuracyLevels[0]);
		Shape[] lowestAccuracyShapes = lowerAccuracyShapes;
		Shape[] highestAccuracyShapes = null;
		for (int accuracyIdx = 1; accuracyIdx < accuracyLevels.length; accuracyIdx++) {
			Shape[] higherAccuracyShapes = physicsShapeBuilder.getScaledShapes(lookData, accuracyLevels[accuracyIdx]);
			assertThat(lowerAccuracyShapes.length, is(lessThanOrEqualTo(higherAccuracyShapes.length)));
			lowerAccuracyShapes = higherAccuracyShapes;
			highestAccuracyShapes = higherAccuracyShapes;
		}
		assertThat(lowestAccuracyShapes.length, is(lessThan(highestAccuracyShapes.length)));
	}
}
