package org.catrobat.catroid.test.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.PhysicsWorld;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public class PhysicsTestUtils {

	public static PhysicsObject createPhysicObject(PhysicsWorld physicsWorld, PhysicsObject.Type type, float width,
			float height) {
		return createPhysicObject(physicsWorld, type, createRectanglePolygonShape(width, height));
	}

	public static PolygonShape createRectanglePolygonShape(float width, float height) {
		PolygonShape rectangle = new PolygonShape();
		rectangle.setAsBox(width / 2.0f, height / 2.0f);
		return rectangle;
	}

	public static PhysicsObject createPhysicObject(PhysicsWorld physicsWorld, PhysicsObject.Type type, Shape shape) {
		PhysicsObject physicsObject = physicsWorld.getPhysicObject(new Sprite("TestSprite"));

		if (type != null || type == PhysicsObject.Type.NONE) {
			physicsObject.setType(type);
		}

		if (shape != null) {
			physicsObject.setShape(new Shape[] { shape });
		}

		return physicsObject;
	}

	public static PhysicsObject createPhysicObject(PhysicsWorld physicsWorld, PhysicsObject.Type type) {
		return createPhysicObject(physicsWorld, type, null);
	}

	public static PhysicsObject createPhysicObject(PhysicsWorld physicsWorld) {
		return createPhysicObject(physicsWorld, null, null);
	}

	// Private member helper methods.
	public static Body getBody(PhysicsObject physicsObject) {
		return (Body) Reflection.getPrivateField(physicsObject, "body");
	}

	public static PhysicsObject.Type getType(PhysicsObject physicsObject) {
		return (PhysicsObject.Type) Reflection.getPrivateField(physicsObject, "type");
	}

	public static float getMass(PhysicsObject physicsObject) {
		return (Float) Reflection.getPrivateField(physicsObject, "mass");
	}

	public static Shape[] getShapes(PhysicsObject physicsObject) {
		return (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
	}

	public static FixtureDef getFixtureDef(PhysicsObject physicsObject) {
		return (FixtureDef) Reflection.getPrivateField(physicsObject, "fixtureDef");
	}

	public static String getInternalImageFilenameFromFilename(String filename) {
		return Utils.md5Checksum(filename) + "_" + filename;
	}

	public static LookData generateLookData() {
		return new LookData();
	}

	public static LookData generateLookData(File testImage) {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		Pixmap pixmap = null;
		pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
		return lookData;
	}

}
