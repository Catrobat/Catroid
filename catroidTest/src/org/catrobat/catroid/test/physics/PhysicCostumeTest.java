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

import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;

import android.test.AndroidTestCase;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicCostumeTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCheckImageChanged() {
		Sprite sprite = new Sprite("TestSprite");
		PhysicsShapeBuilder physicsShapeBuilder = new PhysicShapeBuilderMock();

		PhysicObjectMock physicsObjectMock = new PhysicObjectMock();
		PhysicCostumeMock physicsCostume = new PhysicCostumeMock(sprite, physicsShapeBuilder, physicsObjectMock);

		Shape[] shapes = physicsShapeBuilder.getShape(physicsCostume.getCostumeData(), physicsCostume.getSize());

		assertNotNull("No shapes created", shapes);

		physicsCostume.setImageChanged(false);
		assertFalse("Costume image has changed", physicsCostume.checkImageChanged());
		assertFalse("Set shape has been executed", physicsObjectMock.setShapeExecuted);
		assertNull("Shapes already have been set", physicsObjectMock.setShapeExecutedWithShapes);

		physicsCostume.setImageChanged(true);
		assertTrue("Costume image hasn't changed", physicsCostume.checkImageChanged());
		assertTrue("Set shape hasn't been executed", physicsObjectMock.setShapeExecuted);
		assertEquals("Set wrong shapes", shapes, physicsObjectMock.setShapeExecutedWithShapes);
	}

	public void testUpdatePositionAndRotation() {
		Sprite sprite = new Sprite("TestSprite");
		PhysicsWorld physicsWorld = new PhysicsWorld();
		PhysicsObject physicsObject = physicsWorld.getPhysicObject(sprite);
		PhysicCostumeUpdateMock physicsCostume = new PhysicCostumeUpdateMock(sprite, null, physicsObject);

		Vector2 position = new Vector2(1.2f, 3.4f);
		float rotation = 3.14f;

		physicsCostume.setPosition(position.x, position.y);
		physicsCostume.setRotation(rotation);

		assertNotSame("Wrong position", position, physicsCostume.getCostumePosition());
		assertNotSame("Wrong rotation", rotation, physicsCostume.getCostumeRotation());

		physicsCostume.updatePositionAndRotation();

		assertEquals("Position not updated", position, physicsCostume.getCostumePosition());
		assertEquals("Rotation not updated", rotation, physicsCostume.getCostumeRotation());
	}

	public void testPositionAndAngle() {
		PhysicsWorld physicsWorld = new PhysicsWorld();
		PhysicsObject physicsObject = physicsWorld.getPhysicObject(new Sprite("TestSprite"));
		PhysicLook physicsCostume = new PhysicLook(null, null, physicsObject);

		float x = 1.2f;
		physicsCostume.setX(x);
		assertEquals("Wrong x position", x, physicsObject.getX());

		float y = -3.4f;
		physicsCostume.setY(y);
		assertEquals("Wrong y position", y, physicsObject.getY());

		x = 5.6f;
		y = 7.8f;
		physicsCostume.setPosition(x, y);
		assertEquals("Wrong position", new Vector2(x, y), physicsObject.getPosition());

		float rotation = 9.0f;
		physicsCostume.setRotation(rotation);
		assertEquals("Wrong physics object angle", rotation, physicsObject.getDirection());

		assertEquals("X position has changed", x, physicsCostume.getX());
		assertEquals("Y position has changed", y, physicsCostume.getY());
		assertEquals("Wrong rotation", rotation, physicsCostume.getRotation());
	}

	// TODO: Check if this test is correct.
	public void testSize() {
		Sprite sprite = new Sprite("TestSprite");
		PhysicsShapeBuilder physicsShapeBuilder = new PhysicShapeBuilderMock();
		PhysicObjectMock physicsObjectMock = new PhysicObjectMock();
		PhysicLook physicsCostume = new PhysicLook(sprite, physicsShapeBuilder, physicsObjectMock);
		float size = 3.14f;

		assertFalse("Set shape has been executed", physicsObjectMock.setShapeExecuted);
		assertNull("Shapes already has been set", physicsObjectMock.setShapeExecutedWithShapes);

		physicsCostume.setSize(size);
		assertEquals("Wrong size", size, physicsCostume.getSize());

		Shape[] shapes = physicsShapeBuilder.getShape(physicsCostume.getCostumeData(), size);
		assertTrue("Set shape hasn't been executed", physicsObjectMock.setShapeExecuted);
		assertEquals("Wrong shapes", shapes, physicsObjectMock.setShapeExecutedWithShapes);
	}

	private class PhysicCostumeUpdateMock extends PhysicLook {

		public PhysicCostumeUpdateMock(Sprite sprite, PhysicsShapeBuilder physicsShapeBuilder, PhysicsObject physicsObject) {
			super(sprite, physicsShapeBuilder, physicsObject);
		}

		public Vector2 getCostumePosition() {
			float x = super.getX();
			float y = super.getY();

			return new Vector2(x, y);
		}

		public float getCostumeRotation() {
			return super.getRotation();
		}
	}

	private class PhysicCostumeMock extends PhysicLook {

		public PhysicCostumeMock(Sprite sprite, PhysicsShapeBuilder physicsShapeBuilder, PhysicsObject physicsObject) {
			super(sprite, physicsShapeBuilder, physicsObject);
		}

		@Override
		protected boolean checkImageChanged() {
			return super.checkImageChanged();
		}

		public void setImageChanged(boolean imageChanged) {
			this.imageChanged = imageChanged;
		}
	}

	private class PhysicObjectMock extends PhysicsObject {
		public boolean setShapeExecuted = false;
		public Shape[] setShapeExecutedWithShapes = null;

		public PhysicObjectMock() {
			super(null);
		}

		@Override
		public void setShape(Shape[] shapes) {
			setShapeExecuted = true;
			setShapeExecutedWithShapes = shapes;
		}

		@Override
		protected void setCollisionBits(short categoryBits, short maskBits) {
		}

		@Override
		public void setType(Type type) {
		}
	}

	private class PhysicShapeBuilderMock extends PhysicsShapeBuilder {
		private final Shape[] shapes = new Shape[4];

		@Override
		public Shape[] getShape(CostumeData costumeData, float scaleFactor) {
			return shapes;
		}
	}
}

