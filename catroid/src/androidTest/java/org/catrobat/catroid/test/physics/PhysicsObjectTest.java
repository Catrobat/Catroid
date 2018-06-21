/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PhysicsObjectTest {

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
	public void testDefaultSettings() {
		assertEquals(1.0f, PhysicsObject.DEFAULT_DENSITY);
		assertEquals(0.0f, PhysicsObject.MIN_DENSITY);

		assertEquals(0.2f, PhysicsObject.DEFAULT_FRICTION);
		assertEquals(1.0f, PhysicsObject.MAX_FRICTION);
		assertEquals(0.0f, PhysicsObject.MIN_FRICTION);

		assertEquals(0.8f, PhysicsObject.DEFAULT_BOUNCE_FACTOR);
		assertEquals(0.0f, PhysicsObject.MIN_BOUNCE_FACTOR);

		assertEquals(1.0f, PhysicsObject.DEFAULT_MASS);
		assertEquals(0.000001f, PhysicsObject.MIN_MASS);
	}

	@Test
	public void testNullBody() {
		exception.expect(NullPointerException.class);
		new PhysicsObject(null, new SingleSprite("TestSprite"));
	}

	@Test
	public void testDefaultProperties() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);

		assertEquals(PhysicsObject.Type.NONE, PhysicsTestUtils.getType(physicsObject));
		assertEquals(PhysicsObject.DEFAULT_MASS, PhysicsTestUtils.getMass(physicsObject));

		Body body = PhysicsTestUtils.getBody(physicsObject);
		assertEquals(0, body.getFixtureList().size);

		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsObject);
		assertEquals(PhysicsObject.DEFAULT_DENSITY, fixtureDef.density);
		assertEquals(PhysicsObject.DEFAULT_FRICTION, fixtureDef.friction);
		assertEquals(PhysicsObject.DEFAULT_BOUNCE_FACTOR, fixtureDef.restitution);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NO_COLLISION);

		assertFalse((Boolean) Reflection.getPrivateField(physicsObject, "ifOnEdgeBounce"));
	}

	@Test
	public void testSetShape() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		PolygonShape[] rectangle = new PolygonShape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)};
		physicsObject.setShape(rectangle);

		checkIfShapesAreTheSameAsInPhysicsObject(rectangle, PhysicsTestUtils.getBody(physicsObject));
	}

	@Test
	public void testSetNewShape() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Shape[] shape = new PolygonShape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)};
		physicsObject.setShape(shape);

		Body body = PhysicsTestUtils.getBody(physicsObject);
		PolygonShape[] newShape = new PolygonShape[] {PhysicsTestUtils.createRectanglePolygonShape(2.0f, 3.0f)};
		physicsObject.setShape(newShape);

		assertNotSame("The new shape hasn't been set", shape, PhysicsTestUtils.getShapes(physicsObject));
		checkIfShapesAreTheSameAsInPhysicsObject(newShape, body);
	}

	@Test
	public void testSetSameShape() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		Shape[] rectangle = new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)};
		physicsObject.setShape(rectangle);
		assertThat(body.getFixtureList().size, is(not(equalTo(0))));

		Array<Fixture> fixturesBeforeReset = body.getFixtureList();
		physicsObject.setShape(rectangle);
		Array<Fixture> fixturesAfterReset = body.getFixtureList();

		assertEquals(fixturesBeforeReset, fixturesAfterReset);
	}

	@Test
	public void testSetNullShapeRemovesAllFixtures() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		physicsObject.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)});
		assertThat(body.getFixtureList().size, is(not(equalTo(0))));

		physicsObject.setShape(null);
		assertNull(PhysicsTestUtils.getShapes(physicsObject));
		assertEquals(0, body.getFixtureList().size);
	}

	@Test
	public void testSetShapeUpdatesDensityButNotMass() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		physicsObject.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)});
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float oldDensity = PhysicsTestUtils.getFixtureDef(physicsObject).density;
		float oldMass = body.getMass();

		physicsObject.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(111.0f, 111.0f)});

		assertNotSame("Density hasn't changed", oldDensity, PhysicsTestUtils.getFixtureDef(physicsObject).density);
		assertEquals(oldMass, body.getMass());
	}

	@Test
	public void testSetType() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		physicsObject.setType(PhysicsObject.Type.FIXED);
		assertEquals(PhysicsObject.Type.FIXED, PhysicsTestUtils.getType(physicsObject));
		assertEquals(BodyType.KinematicBody, body.getType());

		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		assertEquals(PhysicsObject.Type.DYNAMIC, PhysicsTestUtils.getType(physicsObject));
		assertEquals(BodyType.DynamicBody, body.getType());

		physicsObject.setType(PhysicsObject.Type.NONE);
		assertEquals(PhysicsObject.Type.NONE, PhysicsTestUtils.getType(physicsObject));
		assertEquals(BodyType.KinematicBody, body.getType());
	}

	@Test
	public void testSetCollisionBits() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.NONE,
				10.0f, 5.0f);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NO_COLLISION);

		physicsObject.setType(PhysicsObject.Type.FIXED);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);

		physicsObject.setType(PhysicsObject.Type.NONE);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NO_COLLISION);

		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);
	}

	@Test
	public void testSetTypeToDynamicUpdatesMass() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.NONE);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float rectangleSize = 10.0f;
		physicsObject
				.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(rectangleSize, rectangleSize)});

		float mass = 128.0f;
		physicsObject.setMass(mass);
		assertEquals(0.0f, body.getMass());

		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		assertEquals(mass, body.getMass());
	}

	@Test
	public void testAngle() throws Exception {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			assertEquals(0.0f, PhysicsTestUtils.getBody(physicsObject).getAngle());

			float[] angles = {45.0f, 1.0f, 131.4f, -10.0f, -180.0f};
			for (float angle : angles) {
				physicsObject.setDirection(angle);
				assertEquals(angle, physicsObject.getDirection(), TestUtils.DELTA);
			}
		}
	}

	@Test
	public void testPosition() throws Exception {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			assertEquals(new Vector2(), PhysicsTestUtils.getBody(physicsObject).getPosition());

			Vector2[] positions = {new Vector2(12.34f, 56.78f), new Vector2(-87.65f, -43.21f)};
			for (Vector2 position : positions) {
				physicsObject.setPosition(position.x, position.y);

				Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter
						.convertBox2dToNormalVector(PhysicsTestUtils.getBody(physicsObject).getPosition());
				assertEquals(position, physicsObjectCatroidPosition);
				assertEquals(position, physicsObject.getPosition());
			}

			for (Vector2 position : positions) {
				physicsObject.setPosition(position);

				Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter
						.convertBox2dToNormalVector(PhysicsTestUtils.getBody(physicsObject).getPosition());
				assertEquals(position, physicsObjectCatroidPosition);
				assertEquals(position, physicsObject.getPosition());
			}
		}
	}

	@Test
	public void testAngleAndPosition() throws Exception {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
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
	}

	@Test
	public void testSetDensity() throws Exception {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

			float[] densityValues = {0.123f, -0.765f, 24.32f};
			assertThat(PhysicsTestUtils.getBody(physicsObject).getFixtureList().size, is(not(equalTo(0))));

			for (float density : densityValues) {
				Object[] values = {density};
				String methodName = "setDensity";
				ParameterList paramList = new ParameterList(values);
				Reflection.invokeMethod(physicsObject, methodName, paramList);
				if (density > 0) {
					assertEquals(density, PhysicsTestUtils.getFixtureDef(physicsObject).density);
				} else {
					assertEquals(0.0f, PhysicsTestUtils.getFixtureDef(physicsObject).density);
				}
				for (Fixture fixture : PhysicsTestUtils.getBody(physicsObject).getFixtureList()) {

					if (density > 0) {
						assertEquals(density, fixture.getDensity());
					} else {
						assertEquals(0.0f, fixture.getDensity());
					}
				}
			}
		}
	}

	@Test
	public void testSetDensityUpdatesMassData() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC,
				5.0f, 5.0f);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float oldMass = body.getMass();
		float density = 12.0f;
		assertNotSame("Densities are the same", density, PhysicsTestUtils.getFixtureDef(physicsObject).density);

		Object[] values = {density};
		String methodName = "setDensity";
		ParameterList paramList = new ParameterList(values);
		Reflection.invokeMethod(physicsObject, methodName, paramList);
		assertNotSame("Masses are the same", oldMass, body.getMass());
	}

	@Test
	public void testSetDensityAtMassChange() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float rectangleSize = 24.0f;
		float[] masses = {PhysicsObject.MIN_MASS, 1.0f, 24.0f};

		physicsObject
				.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(rectangleSize, rectangleSize)});
		for (float mass : masses) {
			physicsObject.setMass(mass);
			float actualDensity = body.getMass() / (rectangleSize * rectangleSize);
			assertEquals(PhysicsTestUtils.getFixtureDef(physicsObject).density, actualDensity, TestUtils.DELTA);
		}
	}

	@Test
	public void testSetFriction() throws Exception {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});
			float[] frictionValues = {0.123f, -0.765f, 0.32f};

			assertThat(PhysicsTestUtils.getBody(physicsObject).getFixtureList().size, is(not(equalTo(0))));

			for (float friction : frictionValues) {
				physicsObject.setFriction(friction);
				if (friction > 0) {
					assertEquals(friction, PhysicsTestUtils.getFixtureDef(physicsObject).friction);
				} else {
					assertEquals(0.0f, PhysicsTestUtils.getFixtureDef(physicsObject).friction);
				}
				for (Fixture fixture : PhysicsTestUtils.getBody(physicsObject).getFixtureList()) {

					if (friction > 0) {
						assertEquals(friction, fixture.getFriction());
					} else {
						assertEquals(0.0f, fixture.getFriction());
					}
				}
			}
		}
	}

	@Test
	public void testSetBounceFactor() throws Exception {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			physicsObject.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});
			float[] bounceFactors = {0.123f, -0.765f, 0.32f};

			assertThat(PhysicsTestUtils.getBody(physicsObject).getFixtureList().size, is(not(equalTo(0))));

			for (float value : bounceFactors) {
				physicsObject.setBounceFactor(value);
				if (value > 0) {
					assertEquals(value, PhysicsTestUtils.getFixtureDef(physicsObject).restitution);
				} else {
					assertEquals(0.0f, PhysicsTestUtils.getFixtureDef(physicsObject).restitution);
				}
				for (Fixture fixture : PhysicsTestUtils.getBody(physicsObject).getFixtureList()) {

					if (value > 0) {
						assertEquals(value, fixture.getRestitution());
					} else {
						assertEquals(0.0f, fixture.getRestitution());
					}
				}
			}
		}
	}

	@Test
	public void testMass() throws Exception {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type, 5.0f, 5.0f);
			Body body = PhysicsTestUtils.getBody(physicsObject);

			checkBodyMassDependingOnType(type, body, PhysicsObject.DEFAULT_MASS);
			assertEquals(PhysicsObject.DEFAULT_MASS, PhysicsTestUtils.getMass(physicsObject));

			float[] masses = {PhysicsObject.MIN_MASS, 0.01f, 1.0f, 12345.0f};
			for (float mass : masses) {
				physicsObject.setMass(mass);
				checkBodyMassDependingOnType(type, body, mass);
				assertEquals(mass, PhysicsTestUtils.getMass(physicsObject));
			}

			physicsObject.setMass(PhysicsObject.MIN_MASS / 10.0f);
			checkBodyMassDependingOnType(type, body, PhysicsObject.MIN_MASS);
			assertEquals(PhysicsObject.MIN_MASS / 10.0f, PhysicsTestUtils.getMass(physicsObject));

			physicsObject.setMass(0.0f);
			checkBodyMassDependingOnType(type, body, PhysicsObject.MIN_MASS);
			assertEquals(0.0f, PhysicsTestUtils.getMass(physicsObject));

			physicsObject.setMass(-1.0f);
			checkBodyMassDependingOnType(type, body, PhysicsObject.MIN_MASS);
			assertEquals(PhysicsObject.MIN_MASS, PhysicsTestUtils.getMass(physicsObject));
		}
	}

	private void checkBodyMassDependingOnType(PhysicsObject.Type type, Body body, float expectedBodyMass) {
		if (type != PhysicsObject.Type.DYNAMIC) {
			expectedBodyMass = 0.0f;
		}
		assertEquals("Wrong mass for " + type.toString().toLowerCase(Locale.getDefault()), expectedBodyMass, body.getMass(), TestUtils.DELTA);
	}

	@Test
	public void testMassWithNoShapeArea() throws Exception {
		PhysicsObject[] physicsObjects = {
				PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC),
				PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC, 0.0f, 0.0f)};

		for (PhysicsObject physicsObject : physicsObjects) {
			Body body = PhysicsTestUtils.getBody(physicsObject);

			float oldMass = body.getMass();
			float mass = 1.2f;
			assertNotSame("Masses are the same", oldMass, mass);

			physicsObject.setMass(mass);
			assertEquals(oldMass, body.getMass());
			assertEquals(mass, PhysicsTestUtils.getMass(physicsObject));
		}
	}

	@Test
	public void testSetRotationSpeed() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		assertEquals(0.0f, body.getAngularVelocity());
		float rotationSpeed = 20.0f;
		physicsObject.setRotationSpeed(rotationSpeed);
		float physicsObjectCatroidRotationSpeed = (float) Math.toDegrees(body.getAngularVelocity());
		assertEquals(rotationSpeed, physicsObjectCatroidRotationSpeed);
	}

	@Test
	public void testSetVelocity() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		assertEquals(new Vector2(), body.getLinearVelocity());
		Vector2 velocity = new Vector2(12.3f, 45.6f);
		physicsObject.setVelocity(velocity.x, velocity.y);

		Vector2 physicsObjectCatVelocity = PhysicsWorldConverter.convertBox2dToNormalVector(body.getLinearVelocity());
		assertEquals(velocity, physicsObjectCatVelocity);
	}

	@Test
	public void testIfOnEndgeBounce() throws Exception {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC,
				1.0f, 1.0f);
		Sprite sprite = new SingleSprite("TestSprite");
		physicsObject.setIfOnEdgeBounce(true, sprite);

		assertTrue((Boolean) Reflection.getPrivateField(physicsObject, "ifOnEdgeBounce"));
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_TO_BOUNCE);

		physicsObject.setIfOnEdgeBounce(false, sprite);
		assertFalse((Boolean) Reflection.getPrivateField(physicsObject, "ifOnEdgeBounce"));
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);
	}

	/*
	 * Helper
	 */
	private void checkCollisionMask(PhysicsObject physicsObject, short categoryBits, short maskBits) throws Exception {
		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsObject);
		assertEquals(categoryBits, fixtureDef.filter.categoryBits);
		assertEquals(maskBits, fixtureDef.filter.maskBits);

		Body body = PhysicsTestUtils.getBody(physicsObject);
		for (Fixture fixture : body.getFixtureList()) {
			Filter filter = fixture.getFilterData();
			assertEquals(categoryBits, filter.categoryBits);
			assertEquals(maskBits, filter.maskBits);
		}
	}

	private void checkIfShapesAreTheSameAsInPhysicsObject(PolygonShape[] shapes, Body body) {
		Array<Fixture> fixtures = body.getFixtureList();
		assertEquals(shapes.length, fixtures.size);

		if (body.getFixtureList().size == 0) {
			return;
		}

		PolygonShape currentShape;
		PolygonShape currentPhysicsObjectShape;
		for (int shapeIndex = 0; shapeIndex < shapes.length; shapeIndex++) {
			currentShape = shapes[shapeIndex];
			currentPhysicsObjectShape = (PolygonShape) fixtures.get(shapeIndex).getShape();
			assertEquals(currentShape.getVertexCount(), currentPhysicsObjectShape.getVertexCount());

			Vector2 expectedVertex = new Vector2();
			Vector2 actualVertex = new Vector2();
			for (int vertexIndex = 0; vertexIndex < currentShape.getVertexCount(); vertexIndex++) {
				currentShape.getVertex(vertexIndex, expectedVertex);
				currentPhysicsObjectShape.getVertex(vertexIndex, actualVertex);
				assertEquals(expectedVertex, actualVertex);
			}
		}
	}

	@Test
	public void testCloneValues() {
		PhysicsObject origin = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		origin.setBounceFactor(1);
		origin.setFriction(2);
		origin.setMass(3);
		origin.setRotationSpeed(4);
		origin.setType(PhysicsObject.Type.FIXED);
		origin.setVelocity(5, 6);
		origin.setPosition(7, 8);
		origin.setDirection(9);

		PhysicsObject clone = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		origin.copyTo(clone);

		assertEquals(origin.getBounceFactor(), clone.getBounceFactor());
		assertEquals(origin.getFriction(), clone.getFriction());
		assertEquals(origin.getMass(), clone.getMass());
		assertEquals(origin.getRotationSpeed(), clone.getRotationSpeed());
		assertEquals(origin.getType(), clone.getType());
		assertEquals(origin.getVelocity(), clone.getVelocity());
		assertEquals(origin.getPosition(), clone.getPosition());
		assertEquals(origin.getDirection(), clone.getDirection());
	}
}
