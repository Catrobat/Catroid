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

import com.badlogic.gdx.graphics.Pixmap;
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
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
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
		PhysicsShapeBuilder physicsShapeBuilder = new PhysicsShapeBuilder();

		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		sprite.getLookDataList().add(lookData);
		Pixmap pixmap = null;
		pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		Shape[] shapes = physicsShapeBuilder.getShape(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertTrue("shapes are 0", shapes.length > 0);

	}

	public void testPositionAndAngle() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		float x = 1.2f;
		physicsLook.setX(x);
		assertEquals("Wrong x position", x, physicsObject.getX());

		float y = -3.4f;
		physicsLook.setY(y);
		assertEquals("Wrong y position", y, physicsObject.getY());

		x = 5.6f;
		y = 7.8f;
		physicsLook.setPosition(x, y);
		assertEquals("Wrong position", new Vector2(x, y), physicsObject.getPosition());

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
		Pixmap pixmap = null;
		pixmap = Utils.getPixmapFromFile(testImage);
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

		physicsLook.setScale(2.0f, 2.0f);
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
						assertEquals("vertex x-value is not the expected", vertexXQueue.poll() * 2.0f, vertex.x);
						assertEquals("vertex x-value is not the expected", vertexYQueue.poll() * 2.0f, vertex.y);
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
}