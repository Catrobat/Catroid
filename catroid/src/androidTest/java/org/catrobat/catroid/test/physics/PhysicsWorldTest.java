/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Map;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PhysicsWorldTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	static {
		GdxNativesLoader.load();
	}

	private PhysicsWorld physicsWorld;
	private World world;
	private Map<Sprite, PhysicsObject> physicsObjects;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
		world = (World) Reflection.getPrivateField(physicsWorld, "world");
		physicsObjects = (Map<Sprite, PhysicsObject>) Reflection.getPrivateField(physicsWorld, "physicsObjects");
		PhysicsTestRule.stabilizePhysicsWorld(physicsWorld);
	}

	@After
	public void tearDown() {
		physicsWorld = null;
		world = null;
		physicsObjects = null;
	}

	@Test
	public void testDefaultSettings() throws Exception {
		assertEquals(10.0f, PhysicsWorld.RATIO);
		assertEquals(3, PhysicsWorld.VELOCITY_ITERATIONS);
		assertEquals(3, PhysicsWorld.POSITION_ITERATIONS);

		assertEquals(new Vector2(0, -10), PhysicsWorld.DEFAULT_GRAVITY);
		assertFalse(PhysicsWorld.IGNORE_SLEEPING_OBJECTS);

		assertEquals(6, Reflection.getPrivateField(physicsWorld, "STABILIZING_STEPS"));

		short expectedCategoryBoundaryBox = 0x0002;
		short expectedCategoryPhysicsObject = 0x0004;
		assertEquals(0x0000, PhysicsWorld.CATEGORY_NO_COLLISION);
		assertEquals(expectedCategoryBoundaryBox, PhysicsWorld.CATEGORY_BOUNDARYBOX);
		assertEquals(expectedCategoryPhysicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT);

		assertEquals(expectedCategoryPhysicsObject, PhysicsWorld.MASK_BOUNDARYBOX);
		assertEquals(~expectedCategoryBoundaryBox, PhysicsWorld.MASK_PHYSICSOBJECT);
		assertEquals(-1, PhysicsWorld.MASK_TO_BOUNCE);
		assertEquals(0, PhysicsWorld.MASK_NO_COLLISION);
	}

	@Test
	public void testWrapper() {
		assertNotNull(world);
	}

	@Test
	public void testGravity() {
		assertEquals(PhysicsWorld.DEFAULT_GRAVITY, world.getGravity());

		Vector2 newGravity = new Vector2(-1.2f, 3.4f);
		physicsWorld.setGravity(newGravity.x, newGravity.y);

		assertEquals(newGravity, world.getGravity());
	}

	@Test
	public void testGetNullPhysicsObject() {
		exception.expect(NullPointerException.class);
		physicsWorld.getPhysicsObject(null);
	}

	@Test
	public void testGetPhysicsObject() {
		Sprite sprite = new Sprite("TestSprite");
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);

		assertNotNull(physicsObject);
		assertEquals(1, physicsObjects.size());
		assertTrue(physicsObjects.containsKey(sprite));
		assertEquals(physicsObject, physicsObjects.get(sprite));
	}

	@Test
	public void testCreatePhysicsObject() throws Exception {
		Object[] values = {new Sprite("testsprite")};
		ParameterList paramList = new ParameterList(values);
		PhysicsObject physicsObject = (PhysicsObject) Reflection.invokeMethod(physicsWorld, "createPhysicsObject",
				paramList);

		assertEquals(Type.NONE, physicsObject.getType());
	}

	@Test
	public void testGetSamePhysicsObject() {
		Sprite sprite = new Sprite("TestSprite");
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsObject samePhysicsObject = physicsWorld.getPhysicsObject(sprite);

		assertEquals(1, physicsObjects.size());
		assertEquals(physicsObject, samePhysicsObject);
	}

	@Test
	public void testSteps() throws SecurityException, IllegalArgumentException {
		Sprite sprite = new Sprite("TestSprite");
		sprite.look = new PhysicsLook(sprite, physicsWorld);

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);

		Vector2 velocity = new Vector2(2.3f, 4.5f);
		float rotationSpeed = 45.0f;
		physicsWorld.setGravity(0.0f, 0.0f);

		assertEquals(new Vector2(), physicsObject.getPosition());

		physicsObject.setVelocity(velocity.x, velocity.y);
		physicsObject.setRotationSpeed(rotationSpeed);

		physicsWorld.step(1.0f);
		assertEquals(velocity.x, physicsObject.getX(), 1e-8);
		assertEquals(velocity.y, physicsObject.getY(), 1e-8);
		assertEquals(rotationSpeed, physicsObject.getDirection(), 1e-8);

		physicsWorld.step(1.0f);
		assertEquals(2 * velocity.x, physicsObject.getX(), 1e-8);
		assertEquals(2 * velocity.y, physicsObject.getY(), 1e-8);
		assertEquals(2 * rotationSpeed, physicsObject.getDirection(), 1e-8);
	}
}
