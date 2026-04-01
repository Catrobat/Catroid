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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PhysicsObjectCollisionTest {

	private List<HashSet<Fixture>> contactFixturePairs = new ArrayList<HashSet<Fixture>>();
	private HashSet<Fixture> expectedcontactFixtures = new HashSet<Fixture>();

	@Rule
	public PhysicsCollisionTestRule rule = new PhysicsCollisionTestRule() {
		@Override
		public void beginContactCallback(Contact contact) {
			super.beginContactCallback(contact);
			HashSet<Fixture> contactFixtureSet = new HashSet<Fixture>();
			contactFixtureSet.add(contact.getFixtureA());
			contactFixtureSet.add(contact.getFixtureB());
			contactFixturePairs.add(contactFixtureSet);
		}
	};

	@Before
	public void setUp() throws Exception {
		rule.spritePosition = new Vector2(-125f, 0f);
		rule.sprite2Position = new Vector2(125f, 0f);
		rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		rule.physicsObject2Type = PhysicsObject.Type.DYNAMIC;

		rule.physicsObject1.setGravityScale(0f);
		rule.physicsObject2.setGravityScale(0f);
		rule.physicsObject1.setVelocity(64f, 0f);
		rule.physicsObject2.setVelocity(-64f, 0f);

		Body body1 = (Body) Reflection.getPrivateField(rule.physicsObject1, "body");

		Fixture expectedContactFixture1 = body1.getFixtureList().get(0);
		expectedcontactFixtures.add(expectedContactFixture1);
		Body body2 = (Body) Reflection.getPrivateField(rule.physicsObject2, "body");
		Fixture expectedContactFixture2 = body2.getFixtureList().get(0);
		expectedcontactFixtures.add(expectedContactFixture2);

		rule.initializeSpritesForCollision();
	}

	@After
	public void tearDown() throws Exception {
		contactFixturePairs = null;
		expectedcontactFixtures = null;
	}

	@Test
	public void testCollisionDynamicNone() {
		rule.physicsObject2.setType(PhysicsObject.Type.NONE);
		assertTrue(rule.simulateFullCollision());
		assertFalse(contactFixturePairs.contains(expectedcontactFixtures));
	}

	@Test
	public void testCollisionFixedFixed() {
		rule.physicsObject1.setType(PhysicsObject.Type.FIXED);
		rule.physicsObject2.setType(PhysicsObject.Type.FIXED);
		assertTrue(rule.simulateFullCollision());
		assertFalse(contactFixturePairs.contains(expectedcontactFixtures));
	}

	@Test
	public void testCollisionFixedNone() {
		rule.physicsObject1.setType(PhysicsObject.Type.FIXED);
		rule.physicsObject2.setType(PhysicsObject.Type.NONE);
		assertTrue(rule.simulateFullCollision());
		assertFalse(contactFixturePairs.contains(expectedcontactFixtures));
	}

	@Test
	public void testCollisionNoneNone() {
		rule.physicsObject1.setType(PhysicsObject.Type.NONE);
		rule.physicsObject2.setType(PhysicsObject.Type.NONE);
		assertTrue(rule.simulateFullCollision());
		assertFalse(contactFixturePairs.contains(expectedcontactFixtures));
	}
}
