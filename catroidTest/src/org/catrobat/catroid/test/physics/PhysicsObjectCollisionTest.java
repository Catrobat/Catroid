/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class PhysicsObjectCollisionTest extends PhysicsCollisionBaseTest {


	private List<HashSet<Fixture>> contactFixturePairs = new ArrayList<HashSet<Fixture>>();
	private HashSet<Fixture> expectedcontactFixtures = new HashSet<Fixture>();


	public PhysicsObjectCollisionTest() {
		spritePosition = new Vector2(-125f, 0f);
		sprite2Position = new Vector2(125f, 0f);
		physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		physicsObject2Type = PhysicsObject.Type.DYNAMIC;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		physicsObject1.setGravityScale(0f);
		physicsObject2.setGravityScale(0f);
		physicsObject1.setVelocity(64f, 0f);
		physicsObject2.setVelocity(-64f, 0f);

		Body body1 = (Body) Reflection.getPrivateField(physicsObject1, "body");
		Fixture expectedContactFixture1 = ((ArrayList<Fixture>)(Reflection.getPrivateField(body1, "fixtures"))).get(0);
		expectedcontactFixtures.add(expectedContactFixture1);
		Body body2 = (Body) Reflection.getPrivateField(physicsObject2, "body");
		Fixture expectedContactFixture2 = ((ArrayList<Fixture>)(Reflection.getPrivateField(body2, "fixtures"))).get(0);
		expectedcontactFixtures.add(expectedContactFixture2);
	}

	@Override
	protected void tearDown() throws Exception {
		contactFixturePairs = null;
		expectedcontactFixtures = null;

		super.tearDown();
	}

	@Override
	public void beginContactCallback(Contact contact) {
		super.beginContactCallback(contact);
		HashSet<Fixture> contactFixtureSet = new HashSet<Fixture>();
		contactFixtureSet.add(contact.getFixtureA());
		contactFixtureSet.add(contact.getFixtureB());
		contactFixturePairs.add(contactFixtureSet);
	}

	public void testCollisionDynamicDynamic() {
		simulateFullCollision();
		assertTrue("Collision between wo dynamic physics objects did not occur", contactFixturePairs.contains(expectedcontactFixtures));
	}

	public void testCollisionDynamicFixed() {
		physicsObject2.setType(PhysicsObject.Type.FIXED);
		simulateFullCollision();
		assertTrue("Collision between dynamic and fixed physics objects did not occur", contactFixturePairs.contains(expectedcontactFixtures));
	}

	public void testCollisionDynamicNone() {
		physicsObject2.setType(PhysicsObject.Type.NONE);
		simulateFullCollision();
		assertFalse("Dynamic physics object should not collide with non-physics object", contactFixturePairs.contains(expectedcontactFixtures));
	}

	public void testCollisionFixedFixed() {
		physicsObject1.setType(PhysicsObject.Type.FIXED);
		physicsObject2.setType(PhysicsObject.Type.FIXED);
		simulateFullCollision();
		assertFalse("Two fixed physics objects should not collide with each other", contactFixturePairs.contains(expectedcontactFixtures));
	}

	public void testCollisionFixedNone() {
		physicsObject1.setType(PhysicsObject.Type.FIXED);
		physicsObject2.setType(PhysicsObject.Type.NONE);
		simulateFullCollision();
		assertFalse("Fixed and non-physics objects should not collide with each other", contactFixturePairs.contains(expectedcontactFixtures));
	}

	public void testCollisionNoneNone() {
		physicsObject1.setType(PhysicsObject.Type.NONE);
		physicsObject2.setType(PhysicsObject.Type.NONE);
		simulateFullCollision();
		assertFalse("Two non-physics objects should not collide with each other", contactFixturePairs.contains(expectedcontactFixtures));
	}

}
