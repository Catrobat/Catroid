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
package org.catrobat.catroid.test.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public final class PhysicsTestUtils {

	private PhysicsTestUtils() {
		throw new AssertionError();
	}

	public static PhysicsObject createPhysicsObject(PhysicsWorld physicsWorld, PhysicsObject.Type type, float width,
			float height) {
		return createPhysicsObject(physicsWorld, type, createRectanglePolygonShape(width, height));
	}

	public static PolygonShape createRectanglePolygonShape(float width, float height) {
		PolygonShape rectangle = new PolygonShape();
		rectangle.setAsBox(width / 2.0f, height / 2.0f);
		return rectangle;
	}

	public static PhysicsObject createPhysicsObject(PhysicsWorld physicsWorld, PhysicsObject.Type type, Shape shape) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(new SingleSprite("TestSprite"));

		if (type != null) {
			physicsObject.setType(type);
		}

		if (shape != null) {
			physicsObject.setShape(new Shape[] { shape });
		}
		return physicsObject;
	}

	public static PhysicsObject createPhysicsObject(PhysicsWorld physicsWorld, PhysicsObject.Type type) {
		return createPhysicsObject(physicsWorld, type, null);
	}

	public static PhysicsObject createPhysicsObject(PhysicsWorld physicsWorld) {
		return createPhysicsObject(physicsWorld, null, null);
	}

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

	public static LookData generateLookData(File testImage) {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
		return lookData;
	}
}
