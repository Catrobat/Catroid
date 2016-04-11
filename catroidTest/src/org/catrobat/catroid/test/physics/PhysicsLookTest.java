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
import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeScaleUtils;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class PhysicsLookTest extends InstrumentationTestCase {

	private static final String TAG = PhysicsLookTest.class.getSimpleName();

	PhysicsWorld physicsWorld;
	private Project project;
	private File testImage;
	private String testImageFilename;
	private static final int IMAGE_FILE_ID = R.raw.multible_mixed_polygons;
	private Sprite sprite;
	static {
		GdxNativesLoader.load();
	}
	@Override
	protected void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
		File projectFile = new File(Constants.DEFAULT_ROOT + File.separator + TestUtils.DEFAULT_TEST_PROJECT_NAME);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		testImageFilename = PhysicsTestUtils.getInternalImageFilenameFromFilename("testImage.png");

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, testImageFilename, IMAGE_FILE_ID,
				getInstrumentation().getContext(), TestUtils.TYPE_IMAGE_FILE);

		sprite = new Sprite("TestSprite");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.clearProject(TestUtils.DEFAULT_TEST_PROJECT_NAME);
		physicsWorld = null;
		sprite = null;
		super.tearDown();
	}

	public void testShapeComputationOfLook() {
		PhysicsShapeBuilder physicsShapeBuilder = PhysicsShapeBuilder.getInstance();

		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		sprite.getLookDataList().add(lookData);
		Pixmap pixmap = null;
		pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		Shape[] shapes = physicsShapeBuilder.getScaledShapes(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertTrue("shapes are 0", shapes.length > 0);
		physicsShapeBuilder.reset();
	}

	public void testPositionAndAngle() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		float x = 1.2f;
		physicsLook.setX(x);
		assertEquals("Wrong x position in PhysicsObject", x, physicsObject.getX());
		assertEquals("Wrong x position in PhysicsLook", x, physicsLook.getX());

		float y = -3.4f;
		physicsLook.setY(y);
		assertEquals("Wrong y position in PhysicsObject", y, physicsObject.getY());
		assertEquals("Wrong y position in PhysicsLook", y, physicsLook.getY());

		x = 5.6f;
		y = 7.8f;
		physicsLook.setPosition(x, y);
		assertEquals("Wrong position", new Vector2(x, y), physicsObject.getPosition());
		assertEquals("Wrong x position in PhysicsLook (due to set/getPosition)", x, physicsLook.getX());
		assertEquals("Wrong y position in PhysicsLook (due to set/getPosition)", y, physicsLook.getY());

		float rotation = 9.0f;
		physicsLook.setRotation(rotation);
		assertEquals("Wrong physics object angle", rotation, physicsObject.getDirection());

		assertEquals("X position has changed", x, physicsLook.getX());
		assertEquals("Y position has changed", y, physicsLook.getY());
		assertEquals("Wrong rotation", rotation, physicsLook.getRotation());
	}

	public void testSetScale() {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImageFilename);
		sprite.getLookDataList().add(lookData);
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		Shape[] shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertEquals("Shapes are not null", null, shapes);

		physicsLook.setLookData(lookData);

		Queue<Float> vertexXQueue = new LinkedList<Float>();
		Queue<Float> vertexYQueue = new LinkedList<Float>();
		shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertNotNull("shapes is null", shapes);
		assertTrue("shapes length not > 0", shapes.length > 0);
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
		assertNotNull("shapes is null", shapes);
		assertTrue("shapes length not > 0", shapes.length > 0);
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

						Object[] objectsX = { vertexXQueue.poll(), testScaleFactor };
						Reflection.ParameterList parameterListX = new Reflection.ParameterList(objectsX);
						float scaledX = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
								parameterListX);

						Object[] objectsY = { vertexYQueue.poll(), testScaleFactor };
						Reflection.ParameterList parameterListY = new Reflection.ParameterList(objectsY);
						float scaledY = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
								parameterListY);

						assertEquals("vertex x-value is not the expected", scaledX, vertex.x);
						assertEquals("vertex x-value is not the expected", scaledY, vertex.y);
						Log.d(TAG, "x=" + vertex.x + ";y=" + vertex.y);
					}
					break;
			}
		}
	}

	public void testSetLookDataWithNullPixmap() {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());

		sprite.look = new PhysicsLook(sprite, physicsWorld);
		try {
			sprite.look.setLookData(lookData);
		} catch (Exception exception) {
			Log.e(TAG, "unexpected exception", exception);
			fail("unexpected exception");
		}
	}

	public void testDefaultValueEqualityOfPhysicsLookAndLook() {
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);
		Look look = new Look(sprite);

		assertEquals("physicsLook getAngularVelocityInUserInterfaceDimensionUnit()"
						+ physicsLook.getAngularVelocityInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getAngularVelocityInUserInterfaceDimensionUnit() + ".",
				physicsLook.getAngularVelocityInUserInterfaceDimensionUnit(), look.getAngularVelocityInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getXVelocityInUserInterfaceDimensionUnit()"
						+ physicsLook.getXVelocityInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getXVelocityInUserInterfaceDimensionUnit() + ".",
				physicsLook.getXVelocityInUserInterfaceDimensionUnit(), look.getXVelocityInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getYVelocityInUserInterfaceDimensionUnit()"
						+ physicsLook.getYVelocityInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getYVelocityInUserInterfaceDimensionUnit() + ".",
				physicsLook.getYVelocityInUserInterfaceDimensionUnit(), look.getYVelocityInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getX()"
						+ physicsLook.getX() + " differs from look value"
						+ look.getX() + ".",
				physicsLook.getX(), look.getX());

		assertEquals("physicsLook getY()"
						+ physicsLook.getY() + " differs from look value"
						+ look.getY() + ".",
				physicsLook.getY(), look.getY());

		assertEquals("physicsLook getRotation()"
						+ physicsLook.getRotation() + " differs from look value"
						+ look.getRotation() + ".",
				physicsLook.getRotation(), look.getRotation());

		assertEquals("physicsLook getLookData()"
						+ physicsLook.getLookData() + " differs from look value"
						+ look.getLookData() + ".",
				physicsLook.getLookData(), look.getLookData());

		assertEquals("physicsLook getAllActionsAreFinished()"
						+ physicsLook.getAllActionsAreFinished() + " differs from look value"
						+ look.getAllActionsAreFinished() + ".",
				physicsLook.getAllActionsAreFinished(), look.getAllActionsAreFinished());

		assertEquals("physicsLook getImagePath()"
						+ physicsLook.getImagePath() + " differs from look value"
						+ look.getImagePath() + ".",
				physicsLook.getImagePath(), look.getImagePath());

		assertEquals("physicsLook getXInUserInterfaceDimensionUnit()"
						+ physicsLook.getXInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getXInUserInterfaceDimensionUnit() + ".",
				physicsLook.getXInUserInterfaceDimensionUnit(), look.getXInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getYInUserInterfaceDimensionUnit()"
						+ physicsLook.getYInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getYInUserInterfaceDimensionUnit() + ".",
				physicsLook.getYInUserInterfaceDimensionUnit(), look.getYInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getAngularVelocityInUserInterfaceDimensionUnit()"
						+ physicsLook.getAngularVelocityInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getAngularVelocityInUserInterfaceDimensionUnit() + ".",
				physicsLook.getAngularVelocityInUserInterfaceDimensionUnit(), look.getAngularVelocityInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getXVelocityInUserInterfaceDimensionUnit()"
						+ physicsLook.getXVelocityInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getXVelocityInUserInterfaceDimensionUnit() + ".",
				physicsLook.getXVelocityInUserInterfaceDimensionUnit(), look.getXVelocityInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getYVelocityInUserInterfaceDimensionUnit()"
						+ physicsLook.getYVelocityInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getYVelocityInUserInterfaceDimensionUnit() + ".",
				physicsLook.getYVelocityInUserInterfaceDimensionUnit(), look.getYVelocityInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getWidthInUserInterfaceDimensionUnit()"
						+ physicsLook.getWidthInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getWidthInUserInterfaceDimensionUnit() + ".",
				physicsLook.getWidthInUserInterfaceDimensionUnit(), look.getWidthInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getHeightInUserInterfaceDimensionUnit()"
						+ physicsLook.getHeightInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getHeightInUserInterfaceDimensionUnit() + ".",
				physicsLook.getHeightInUserInterfaceDimensionUnit(), look.getHeightInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getDirectionInUserInterfaceDimensionUnit()"
						+ physicsLook.getDirectionInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getDirectionInUserInterfaceDimensionUnit() + ".",
				physicsLook.getDirectionInUserInterfaceDimensionUnit(), look.getDirectionInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getSizeInUserInterfaceDimensionUnit()"
						+ physicsLook.getSizeInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getSizeInUserInterfaceDimensionUnit() + ".",
				physicsLook.getSizeInUserInterfaceDimensionUnit(), look.getSizeInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getTransparencyInUserInterfaceDimensionUnit()"
						+ physicsLook.getTransparencyInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getTransparencyInUserInterfaceDimensionUnit() + ".",
				physicsLook.getTransparencyInUserInterfaceDimensionUnit(), look.getTransparencyInUserInterfaceDimensionUnit());

		assertEquals("physicsLook getBrightnessInUserInterfaceDimensionUnit()"
						+ physicsLook.getBrightnessInUserInterfaceDimensionUnit() + " differs from look value"
						+ look.getBrightnessInUserInterfaceDimensionUnit() + ".",
				physicsLook.getBrightnessInUserInterfaceDimensionUnit(), look.getBrightnessInUserInterfaceDimensionUnit());
	}
}
