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
package org.catrobat.catroid.test.physics.look;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.physics.PhysicsTestUtils;
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
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PhysicsLookDataTest {

	private PhysicsWorld physicsWorld;
	private final String projectName = "testProject";
	private Sprite sprite;
	private LookData lookData;

	@Before
	public void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
		File projectDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName);
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
		String testImageFilename = PhysicsTestUtils.getInternalImageFilenameFromFilename("testImage.png");
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		File testImage = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				R.raw.multible_mixed_polygons,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				testImageFilename,
				1);

		sprite = new Sprite("TestSprite");
		lookData = PhysicsTestUtils.generateLookData(testImage);
		sprite.getLookList().add(lookData);
		Pixmap pixmap = PhysicsTestUtils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
	}

	@Test
	public void testShapeComputationOfLook() {
		PhysicsShapeBuilder physicsShapeBuilder = PhysicsShapeBuilder.getInstance();

		Shape[] shapes = physicsShapeBuilder.getScaledShapes(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertThat(shapes.length, is(greaterThan(0)));
		physicsShapeBuilder.reset();
	}

	@Test
	public void testSetScale() throws Exception {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);
		physicsLook.setLookData(lookData);

		float testScaleFactor = 1.1f;

		Vector2[] expectedVertices = new Vector2[] {
				new Vector2(10.84f, -7.31f),
				new Vector2(10.84f, -0.6f),
				new Vector2(9.63f, 10.62f),
				new Vector2(-10.84f, 10.62f),
				new Vector2(-10.84f, 6.44f),
				new Vector2(-3.35f, -7.31f),
				new Vector2(-0.06f, -10.61f),
				new Vector2(7.54f, -10.61f),
		};

		physicsLook.setScale(testScaleFactor, testScaleFactor);
		Shape[] scaledShapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertNotNull(scaledShapes);
		assertEquals(1, scaledShapes.length);

		Shape scaledShape = scaledShapes[0];
		assertEquals(Shape.Type.Polygon, scaledShape.getType());

		int scaledVertexCount = ((PolygonShape) scaledShape).getVertexCount();
		assertEquals(8, scaledVertexCount);
		Vector2[] scaledVertices = new Vector2[8];
		for (int idx = 0; idx < scaledVertexCount; idx++) {
			scaledVertices[idx] = new Vector2();
			((PolygonShape) scaledShape).getVertex(idx, scaledVertices[idx]);
		}
		assertArrayEquals(expectedVertices, scaledVertices);
	}
}
