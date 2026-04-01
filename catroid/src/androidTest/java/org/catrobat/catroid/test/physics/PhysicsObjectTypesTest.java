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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;

@RunWith(Parameterized.class)
public class PhysicsObjectTypesTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{PhysicsObject.Type.DYNAMIC.toString(), PhysicsObject.Type.DYNAMIC},
				{PhysicsObject.Type.FIXED.toString(), PhysicsObject.Type.FIXED},
				{PhysicsObject.Type.NONE.toString(), PhysicsObject.Type.NONE}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public PhysicsObject.Type type;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	static {
		GdxNativesLoader.load();
	}

	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
	}

	@After
	public void tearDown() throws Exception {
		physicsWorld = null;
	}

	@Test
	public void testAngle() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		assertEquals(0.0f, PhysicsTestUtils.getBody(physicsObject).getAngle());

		physicsObject.setDirection(45.0f);
		assertEquals(45.0f, physicsObject.getDirection(), TestUtils.DELTA);

		physicsObject.setDirection(-10.0f);
		assertEquals(-10.0f, physicsObject.getDirection(), TestUtils.DELTA);

		physicsObject.setDirection(-180.0f);
		assertEquals(-180.0f, physicsObject.getDirection(), TestUtils.DELTA);
	}

	@Test
	public void testPosition() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		assertEquals(new Vector2(), PhysicsTestUtils.getBody(physicsObject).getPosition());

		Vector2 testVector1 = new Vector2(12.34f, 56.78f);
		physicsObject.setPosition(testVector1.x, testVector1.y);
		assertPositionCorrectlyConverted(physicsObject, testVector1);

		physicsObject.setPosition(testVector1);
		assertPositionCorrectlyConverted(physicsObject, testVector1);

		Vector2 testVector2 = new Vector2(-87.65f, -43.21f);
		physicsObject.setPosition(testVector2.x, testVector2.y);
		assertPositionCorrectlyConverted(physicsObject, testVector2);

		physicsObject.setPosition(testVector2);
		assertPositionCorrectlyConverted(physicsObject, testVector2);
	}

	private void assertPositionCorrectlyConverted(PhysicsObject physicsObject, Vector2 position) throws Exception {
		Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter
				.convertBox2dToNormalVector(PhysicsTestUtils.getBody(physicsObject).getPosition());
		assertEquals(position, physicsObjectCatroidPosition);
		assertEquals(position, physicsObject.getPosition());
	}

	@Test
	public void testAngleAndPosition() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		assertEquals(0.0f, PhysicsTestUtils.getBody(physicsObject).getAngle());
		assertEquals(new Vector2(), PhysicsTestUtils.getBody(physicsObject).getPosition());

		float angle = 15.6f;
		float expectedAngle = 15.6f;
		Vector2 position = new Vector2(12.34f, 56.78f);
		physicsObject.setDirection(angle);
		physicsObject.setPosition(position.x, position.y);

		float physicsObjectCatroidAngle = PhysicsWorldConverter.convertBox2dToNormalAngle(PhysicsTestUtils.getBody(
				physicsObject).getAngle());
		Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter.convertBox2dToNormalVector(PhysicsTestUtils
				.getBody(physicsObject).getPosition());

		assertEquals(expectedAngle, physicsObjectCatroidAngle, TestUtils.DELTA);
		assertEquals(position, physicsObjectCatroidPosition);
	}

	@Test
	public void testSetDensity() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

		assertNotEquals(0, PhysicsTestUtils.getBody(physicsObject).getFixtureList().size);

		assertDensitySet(0.123f, 0.123f, physicsObject);
	}

	@Test
	public void testSetNegativeDensity() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

		assertNotEquals(0, PhysicsTestUtils.getBody(physicsObject).getFixtureList().size);

		assertDensitySet(-0.234f, 0.0f, physicsObject);
	}

	private void assertDensitySet(float density, float expected, PhysicsObject physicsObject) throws Exception {
		physicsObject.setDensity(density);
		assertEquals(expected, PhysicsTestUtils.getFixtureDef(physicsObject).density);
		for (Fixture fixture : PhysicsTestUtils.getBody(physicsObject).getFixtureList()) {
			assertEquals(expected, fixture.getDensity());
		}
	}

	@Test
	public void testSetFriction() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

		assertNotEquals(0, PhysicsTestUtils.getBody(physicsObject).getFixtureList().size);

		assertFrictionSet(0.123f, 0.123f, physicsObject);
	}

	@Test
	public void testSetNegativeFriction() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

		assertNotEquals(0, PhysicsTestUtils.getBody(physicsObject).getFixtureList().size);

		assertFrictionSet(-0.765f, 0.0f, physicsObject);
	}

	private void assertFrictionSet(float friction, float expected, PhysicsObject physicsObject) throws Exception {
		physicsObject.setFriction(friction);
		assertEquals(expected, PhysicsTestUtils.getFixtureDef(physicsObject).friction);
		for (Fixture fixture : PhysicsTestUtils.getBody(physicsObject).getFixtureList()) {
			assertEquals(expected, fixture.getFriction());
		}
	}

	@Test
	public void testSetBounceFactor() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

		assertNotEquals(0, PhysicsTestUtils.getBody(physicsObject).getFixtureList().size);

		assertBounceFactorSet(0.123f, 0.123f, physicsObject);
	}

	@Test
	public void testSetNegativeBounceFactor() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
		physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

		assertNotEquals(0, PhysicsTestUtils.getBody(physicsObject).getFixtureList().size);

		assertBounceFactorSet(-0.765f, 0.0f, physicsObject);
	}

	private void assertBounceFactorSet(float bounceFactor, float expected, PhysicsObject physicsObject) throws
			Exception {
		physicsObject.setBounceFactor(bounceFactor);
		assertEquals(expected, PhysicsTestUtils.getFixtureDef(physicsObject).restitution);
		for (Fixture fixture : PhysicsTestUtils.getBody(physicsObject).getFixtureList()) {
			assertEquals(expected, fixture.getRestitution());
		}
	}
}
