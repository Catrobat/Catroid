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

import android.test.AndroidTestCase;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.physics.PhysicsProperties.Type;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.utils.Reflection;

public class PhysicsWorldTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private PhysicsWorld physicsWorld;
	private World world;

	@SuppressWarnings("unchecked")
	@Override
	public void setUp() {
		physicsWorld = new PhysicsWorld(1920, 1600);
		world = (World) Reflection.getPrivateField(physicsWorld, "world");
		PhysicsBaseTest.stabilizePhysicsWorld(physicsWorld);
	}

	@Override
	public void tearDown() {
		physicsWorld = null;
		world = null;
	}

	public void testDefaultSettings() {
		assertEquals("Wrong configuration", 10.0f, PhysicsWorld.RATIO);
		assertEquals("Wrong configuration", 3, PhysicsWorld.VELOCITY_ITERATIONS);
		assertEquals("Wrong configuration", 3, PhysicsWorld.POSITION_ITERATIONS);

		assertEquals("Wrong configuration", new Vector2(0, -10), PhysicsWorld.DEFAULT_GRAVITY);
		assertFalse("Wrong configuration", PhysicsWorld.IGNORE_SLEEPING_OBJECTS);

		assertEquals("Wrong configuration", 6, Reflection.getPrivateField(physicsWorld, "STABILIZING_STEPS"));

		short expectedCategoryBoundaryBox = 0x0002;
		short expectedCategoryPhysicsObject = 0x0004;
		assertEquals("Wrong configuration", 0x0000, PhysicsWorld.CATEGORY_NO_COLLISION);
		assertEquals("Wrong configuration", expectedCategoryBoundaryBox, PhysicsWorld.CATEGORY_BOUNDARYBOX);
		assertEquals("Wrong configuration", expectedCategoryPhysicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT);

		assertEquals("Wrong configuration", expectedCategoryPhysicsObject, PhysicsWorld.MASK_BOUNDARYBOX);
		assertEquals("Wrong configuration", ~expectedCategoryBoundaryBox, PhysicsWorld.MASK_PHYSICSOBJECT);
		assertEquals("Wrong configuration", -1, PhysicsWorld.MASK_TO_BOUNCE);
		assertEquals("Wrong configuration", 0, PhysicsWorld.MASK_NO_COLLISION);
	}

	public void testWrapper() {
		assertNotNull("Didn't load box2d wrapper", world);
	}

	public void testGravity() {
		assertEquals("Wrong initialization", PhysicsWorld.DEFAULT_GRAVITY, world.getGravity());

		Vector2 newGravity = new Vector2(-1.2f, 3.4f);
		physicsWorld.setGravity(newGravity.x, newGravity.y);

		assertEquals("Did not update gravity", newGravity, world.getGravity());
	}

	public void testSteps() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Sprite sprite = new Sprite("TestSprite");
		sprite.look = new Look(sprite);
		sprite.setPhysicsProperties(new PhysicsProperties(physicsWorld.createBody(), sprite));

		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		physicsProperties.setType(Type.NONE);

		Vector2 velocity = new Vector2(2.3f, 4.5f);
		float rotationSpeed = 45.0f;
		physicsWorld.setGravity(0.0f, 0.0f);

		assertEquals("Physics object has a wrong start position", new Vector2(), physicsProperties.getPosition());

		physicsProperties.setVelocity(velocity.x, velocity.y);
		physicsProperties.setRotationSpeed(rotationSpeed);

		physicsWorld.step(1.0f);
		assertEquals("Wrong x position", velocity.x, physicsProperties.getX(), 1e-8);
		assertEquals("Wrong y position", velocity.y, physicsProperties.getY(), 1e-8);
		assertEquals("Wrong angle", rotationSpeed, physicsProperties.getDirection(), 1e-8);

		// TODO[Physics] angle problem
		physicsWorld.step(1.0f);
		assertEquals("Wrong x position", 2 * velocity.x, physicsProperties.getX(), 1e-8);
		assertEquals("Wrong y position", 2 * velocity.y, physicsProperties.getY(), 1e-8);
		assertEquals("Wrong angle", 2 * rotationSpeed, physicsProperties.getDirection(), 1e-8);
	}
}
