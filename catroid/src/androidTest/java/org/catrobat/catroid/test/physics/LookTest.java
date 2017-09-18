/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsProperties;
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

public class LookTest extends InstrumentationTestCase {

	private static final String TAG = LookTest.class.getSimpleName();

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

		testImage = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(), testImageFilename, IMAGE_FILE_ID,
				getInstrumentation().getContext(), TestUtils.TYPE_IMAGE_FILE);

		sprite = new SingleSprite("TestSprite");
		sprite.setPhysicsProperties(new PhysicsProperties(physicsWorld.createBody(), sprite));
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
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		Look look = new Look(sprite);

		float x = 1.2f;
		look.setX(x);
		assertEquals("Wrong x position in PhysicsProperties", x, physicsProperties.getX());
		assertEquals("Wrong x position in Look", x, look.getX());

		float y = -3.4f;
		look.setY(y);
		assertEquals("Wrong y position in PhysicsProperties", y, physicsProperties.getY());
		assertEquals("Wrong y position in Look", y, look.getY());

		x = 5.6f;
		y = 7.8f;
		look.setPosition(x, y);
		assertEquals("Wrong position", new Vector2(x, y), physicsProperties.getPosition());
		assertEquals("Wrong x position in Look (due to set/getPosition)", x, look.getX());
		assertEquals("Wrong y position in Look (due to set/getPosition)", y, look.getY());

		float rotation = 9.0f;
		look.setRotation(rotation);
		assertEquals("Wrong physics object angle", rotation, physicsProperties.getDirection());

		assertEquals("X position has changed", x, look.getX());
		assertEquals("Y position has changed", y, look.getY());
		assertEquals("Wrong rotation", rotation, look.getRotation());
	}

	public void testSetScale() {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImageFilename);
		sprite.getLookDataList().add(lookData);
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		Look look = new Look(sprite);

		Shape[] shapes = (Shape[]) Reflection.getPrivateField(physicsProperties, "shapes");
		assertEquals("Shapes are not null", null, shapes);

		look.setLookData(lookData);

		Queue<Float> vertexXQueue = new LinkedList<Float>();
		Queue<Float> vertexYQueue = new LinkedList<Float>();
		shapes = (Shape[]) Reflection.getPrivateField(physicsProperties, "shapes");
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

		look.setScale(testScaleFactor, testScaleFactor);
		shapes = (Shape[]) Reflection.getPrivateField(physicsProperties, "shapes");
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

						Object[] objectsX = {vertexXQueue.poll(), testScaleFactor};
						Reflection.ParameterList parameterListX = new Reflection.ParameterList(objectsX);
						float scaledX = (float) Reflection.invokeMethod(PhysicsShapeScaleUtils.class, "scaleCoordinate",
								parameterListX);

						Object[] objectsY = {vertexYQueue.poll(), testScaleFactor};
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

		sprite.look = new Look(sprite);
		try {
			sprite.look.setLookData(lookData);
		} catch (Exception exception) {
			Log.e(TAG, "unexpected exception", exception);
			fail("unexpected exception");
		}
	}
}
