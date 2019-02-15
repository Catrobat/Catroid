/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeScaleUtils;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PhysicsLookTest {

	private static final String TAG = PhysicsLookTest.class.getSimpleName();
	PhysicsWorld physicsWorld;
	private final String projectName = "testProject";
	private File projectDir;
	private Project project;
	private File testImage;
	private String testImageFilename;
	private Sprite sprite;
	static {
		GdxNativesLoader.load();
	}

	@Before
	public void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
		projectDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName);
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
		testImageFilename = PhysicsTestUtils.getInternalImageFilenameFromFilename("testImage.png");
		project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		testImage = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				R.raw.multible_mixed_polygons,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				testImageFilename,
				1);

		sprite = new SingleSprite("TestSprite");
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
		physicsWorld = null;
		sprite = null;
	}

	@Test
	public void testShapeComputationOfLook() {
		PhysicsShapeBuilder physicsShapeBuilder = PhysicsShapeBuilder.getInstance();

		LookData lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName(testImage.getName());
		sprite.getLookList().add(lookData);
		Pixmap pixmap = null;
		pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		Shape[] shapes = physicsShapeBuilder.getScaledShapes(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertThat(shapes.length, is(greaterThan(0)));
		physicsShapeBuilder.reset();
	}

	@Test
	public void testPositionAndAngle() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		float x = 1.2f;
		physicsLook.setX(x);
		assertEquals(x, physicsObject.getX());
		assertEquals(x, physicsLook.getX());

		float y = -3.4f;
		physicsLook.setY(y);
		assertEquals(y, physicsObject.getY());
		assertEquals(y, physicsLook.getY());

		x = 5.6f;
		y = 7.8f;
		physicsLook.setPosition(x, y);
		assertEquals(new Vector2(x, y), physicsObject.getPosition());
		assertEquals(x, physicsLook.getX());
		assertEquals(y, physicsLook.getY());

		float rotation = 9.0f;
		physicsLook.setRotation(rotation);
		assertEquals(rotation, physicsObject.getDirection());

		assertEquals(x, physicsLook.getX());
		assertEquals(y, physicsLook.getY());
		assertEquals(rotation, physicsLook.getRotation());
	}

	@Test
	public void testSetScale() throws Exception {
		LookData lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName(testImageFilename);
		sprite.getLookList().add(lookData);
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		Shape[] shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertEquals(null, shapes);

		physicsLook.setLookData(lookData);

		Queue<Float> vertexXQueue = new LinkedList<Float>();
		Queue<Float> vertexYQueue = new LinkedList<Float>();
		shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertNotNull(shapes);
		assertThat(shapes.length, is(greaterThan(0)));
		Log.d(TAG, "shapes.length: " + shapes.length);
		for (Shape shape : shapes) {
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
					for (int idx = 0; idx < vertexCount; idx++) {
						Vector2 vertex = new Vector2();
						((PolygonShape) shape).getVertex(idx, vertex);
						vertexXQueue.add(Float.valueOf(vertex.x));
						vertexYQueue.add(Float.valueOf(vertex.y));
						Log.d(TAG, "x=" + vertex.x + ";y=" + vertex.y);
					}
					break;
			}
		}

		float[] accuracyLevels = (float[]) Reflection.getPrivateField(PhysicsShapeBuilder.class, "ACCURACY_LEVELS");
		float testScaleFactor = 1.1f;
		if (accuracyLevels.length > 1) {
			for (int i = 0; i < accuracyLevels.length - 1; i++) {
				if (Math.abs(accuracyLevels[i] - 1.0f) < 0.05) {
					testScaleFactor = (accuracyLevels[i] + accuracyLevels[i + 1]);
					testScaleFactor /= 2.0f;
					testScaleFactor -= 0.025f;
				}
			}
		}

		physicsLook.setScale(testScaleFactor, testScaleFactor);
		shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertNotNull(shapes);
		assertThat(shapes.length, is(greaterThan(0)));
		for (Shape shape : shapes) {
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
					for (int idx = 0; idx < vertexCount; idx++) {
						Vector2 vertex = new Vector2();
						((PolygonShape) shape).getVertex(idx, vertex);

						Object[] objectsX = {vertexXQueue.poll(), testScaleFactor};
						Reflection.ParameterList parameterListX = new Reflection.ParameterList(objectsX);
						float scaledX = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
								parameterListX);

						Object[] objectsY = {vertexYQueue.poll(), testScaleFactor};
						Reflection.ParameterList parameterListY = new Reflection.ParameterList(objectsY);
						float scaledY = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
								parameterListY);

						assertEquals(scaledX, vertex.x);
						assertEquals(scaledY, vertex.y);
						Log.d(TAG, "x=" + vertex.x + ";y=" + vertex.y);
					}
					break;
			}
		}
	}

	@Test
	public void testSetLookDataWithNullPixmap() {
		LookData lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName(testImage.getName());

		sprite.look = new PhysicsLook(sprite, physicsWorld);
		sprite.look.setLookData(lookData);
	}

	@Test
	public void testDefaultValueEqualityOfPhysicsLookAndLook() {
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);
		Look look = new Look(sprite);

		assertEquals(physicsLook.getAngularVelocityInUserInterfaceDimensionUnit(),
				look.getAngularVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getXVelocityInUserInterfaceDimensionUnit(),
				look.getXVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getYVelocityInUserInterfaceDimensionUnit(),
				look.getYVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getX(), look.getX());

		assertEquals(physicsLook.getY(), look.getY());

		assertEquals(physicsLook.getRotation(), look.getRotation());

		assertEquals(physicsLook.getLookData(), look.getLookData());

		assertEquals(physicsLook.haveAllThreadsFinished(), look.haveAllThreadsFinished());

		assertEquals(physicsLook.getImagePath(), look.getImagePath());

		assertEquals(physicsLook.getXInUserInterfaceDimensionUnit(), look.getXInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getYInUserInterfaceDimensionUnit(), look.getYInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getAngularVelocityInUserInterfaceDimensionUnit(),
				look.getAngularVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getXVelocityInUserInterfaceDimensionUnit(),
				look.getXVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getYVelocityInUserInterfaceDimensionUnit(),
				look.getYVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getWidthInUserInterfaceDimensionUnit(),
				look.getWidthInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getHeightInUserInterfaceDimensionUnit(),
				look.getHeightInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getDirectionInUserInterfaceDimensionUnit(),
				look.getDirectionInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getSizeInUserInterfaceDimensionUnit(),
				look.getSizeInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getTransparencyInUserInterfaceDimensionUnit(),
				look.getTransparencyInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getBrightnessInUserInterfaceDimensionUnit(),
				look.getBrightnessInUserInterfaceDimensionUnit());
	}

	@Test
	public void testCloneValues() {
		PhysicsWorld world = new PhysicsWorld();

		Sprite originSprite = new Sprite("Origin");
		PhysicsLook originLook = new PhysicsLook(originSprite, world);
		PhysicsObject originPhysicsObject = world.getPhysicsObject(originSprite);

		Sprite cloneSprite = new Sprite("Clone");
		PhysicsLook cloneLook = new PhysicsLook(cloneSprite, world);
		PhysicsObject clonePhysicsObject = world.getPhysicsObject(cloneSprite);

		originLook.setXInUserInterfaceDimensionUnit(10);
		originLook.setBrightnessInUserInterfaceDimensionUnit(32);
		originPhysicsObject.setMass(10);

		originLook.copyTo(cloneLook);

		assertEquals(originLook.getXInUserInterfaceDimensionUnit(), cloneLook.getXInUserInterfaceDimensionUnit());
		assertEquals(originLook.getBrightnessInUserInterfaceDimensionUnit(), cloneLook.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(originPhysicsObject.getMass(), clonePhysicsObject.getMass());
	}
}
