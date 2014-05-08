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
import org.catrobat.catroid.physic.PhysicsLook;
import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.PhysicsWorld;
import org.catrobat.catroid.physic.shapebuilder.PhysicsShapeBuilder;
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

		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		Shape[] shapes = physicsShapeBuilder.getShape(physicsLook.getLookData(),
				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertNull("Shapes are created out of no image", shapes);

		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		sprite.getLookDataList().add(lookData);
		Pixmap pixmap = null;
		pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		shapes = physicsShapeBuilder.getShape(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertTrue("shapes are 0", shapes.length > 0);

	}

	public void testPositionAndAngle() {
		PhysicsObject physicsObject = physicsWorld.getPhysicObject(sprite);
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

		PhysicsObject physicsObject = physicsWorld.getPhysicObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		Shape[] shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertEquals("Shapes are not null", null, shapes);

		physicsLook.setLookData(lookData);

		Queue<Float> vertexXQueue = new LinkedList<Float>();
		Queue<Float> vertexYQueue = new LinkedList<Float>();
		shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertNotNull("shapes is null", shapes);
		assertTrue("shapes length not > 0", shapes.length > 0);
		Log.d("phill_test", "shapes.length: " + shapes.length);
		for (Shape shape : shapes) {
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
					for (int idx = 0; idx < vertexCount; idx++) {
						Vector2 vertex = new Vector2();
						((PolygonShape) shape).getVertex(idx, vertex);
						vertexXQueue.add(Float.valueOf(vertex.x));
						vertexYQueue.add(Float.valueOf(vertex.y));
						Log.d("phill_test", "x=" + vertex.x + ";y=" + vertex.y);
					}
					break;
			}
		}

		physicsLook.setScale(2.0f, 2.0f);
		shapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertNotNull("shapes is null", shapes);
		assertTrue("shapes length not > 0", shapes.length > 0);
		Log.d("phill_test", "shapes.length: " + shapes.length);
		for (Shape shape : shapes) {
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
					for (int idx = 0; idx < vertexCount; idx++) {
						Vector2 vertex = new Vector2();
						((PolygonShape) shape).getVertex(idx, vertex);
						assertEquals("vertex x-value is not the expected", vertexXQueue.poll() * 2.0f, vertex.x);
						assertEquals("vertex x-value is not the expected", vertexYQueue.poll() * 2.0f, vertex.y);
						Log.d("phill_test", "x=" + vertex.x + ";y=" + vertex.y);
					}
					break;
			}
		}
	}
}
//	
//	public void testCheckImageChanged() {
//		Sprite sprite = new Sprite("TestSprite");
//		PhysicsShapeBuilder physicsShapeBuilder = new PhysicsShapeBuilder();
//
//		PhysicsObject physicsObject = new PhysicsObject(null, sprite);
//
//		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);
//
//		Shape[] shapes = physicsShapeBuilder.getShape(physicsLook.getLookData(),
//				sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);
//
//		assertNotNull("No shapes created", shapes);
//
//		
//		physicsLook.setImageChanged(false);
//		assertFalse("Costume image has changed", physicsLook.checkImageChanged());
//		 assertFalse("Set shape has been executed", physicsObject.setShapeExecuted);
//		 assertNull("Shapes already have been set", physicsObject.setShapeExecutedWithShapes);
//		 
//		 physicsLook.setImageChanged(true);
//		 assertTrue("Costume image hasn't changed", physicsLook.checkImageChanged());
//		 assertTrue("Set shape hasn't been executed", physicsObject.setShapeExecuted);
//		 assertEquals("Set wrong shapes", shapes, physicsObject.setShapeExecutedWithShapes);
//		
//	}
//
////	public void testUpdatePositionAndRotation() {
//		Sprite sprite = new Sprite("TestSprite");
//		PhysicsWorld physicsWorld = new PhysicsWorld();
//		PhysicsObject physicsObject = physicsWorld.getPhysicObject(sprite);
//		PhysicCostumeUpdateMock physicsCostume = new PhysicCostumeUpdateMock(sprite, null, physicsObject);
//
//		Vector2 position = new Vector2(1.2f, 3.4f);
//		float rotation = 3.14f;
//
//		physicsCostume.setPosition(position.x, position.y);
//		physicsCostume.setRotation(rotation);
//
//		assertNotSame("Wrong position", position, physicsCostume.getCostumePosition());
//		assertNotSame("Wrong rotation", rotation, physicsCostume.getCostumeRotation());
//
//		physicsCostume.updatePositionAndRotation();
//
//		assertEquals("Position not updated", position, physicsCostume.getCostumePosition());
//		assertEquals("Rotation not updated", rotation, physicsCostume.getCostumeRotation());
//	}//
//	
//
//	// TODO: Check if this test is correct.
//	public void testSize() {
//		Sprite sprite = new Sprite("TestSprite");
//		PhysicsShapeBuilder physicsShapeBuilder = new PhysicShapeBuilderMock();
//		PhysicObjectMock physicsObjectMock = new PhysicObjectMock();
//		PhysicLook physicsCostume = new PhysicLook(sprite, physicsShapeBuilder, physicsObjectMock);
//		float size = 3.14f;
//
//		assertFalse("Set shape has been executed", physicsObjectMock.setShapeExecuted);
//		assertNull("Shapes already has been set", physicsObjectMock.setShapeExecutedWithShapes);
//
//		physicsCostume.setSize(size);
//		assertEquals("Wrong size", size, physicsCostume.getSize());
//
//		Shape[] shapes = physicsShapeBuilder.getShape(physicsCostume.getCostumeData(), size);
//		assertTrue("Set shape hasn't been executed", physicsObjectMock.setShapeExecuted);
//		assertEquals("Wrong shapes", shapes, physicsObjectMock.setShapeExecutedWithShapes);
//	}
//
//	
