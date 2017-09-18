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
import android.util.Log;

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
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.Locale;

public class PhysicsObjectTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private static final String TAG = PhysicsObjectTest.class.getSimpleName();

	private PhysicsWorld physicsWorld;

	@Override
	protected void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
	}

	@Override
	protected void tearDown() throws Exception {
		physicsWorld = null;
	}

	public void testDefaultSettings() {
		assertEquals("Wrong configuration", 1.0f, PhysicsProperties.DEFAULT_DENSITY);
		assertEquals("Wrong configuration", 0.0f, PhysicsProperties.MIN_DENSITY);

		assertEquals("Wrong configuration", 0.2f, PhysicsProperties.DEFAULT_FRICTION);
		assertEquals("Wrong configuration", 1.0f, PhysicsProperties.MAX_FRICTION);
		assertEquals("Wrong configuration", 0.0f, PhysicsProperties.MIN_FRICTION);

		assertEquals("Wrong configuration", 0.8f, PhysicsProperties.DEFAULT_BOUNCE_FACTOR);
		assertEquals("Wrong configuration", 0.0f, PhysicsProperties.MIN_BOUNCE_FACTOR);

		assertEquals("Wrong configuration", 1.0f, PhysicsProperties.DEFAULT_MASS);
		assertEquals("Wrong configuration", 0.000001f, PhysicsProperties.MIN_MASS);
	}

	public void testNullBody() {
		try {
			new PhysicsProperties(null, new SingleSprite("TestSprite"));
			fail("Creating a physics object with no body doesn't cause a NullPointerException");
		} catch (NullPointerException exception) {
			Log.e(TAG, exception.toString());
		}
	}

	public void testDefaultProperties() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld);

		assertEquals("Wrong initialization", PhysicsProperties.Type.NONE, PhysicsTestUtils.getType(physicsProperties));
		assertEquals("Wrong initialization", PhysicsProperties.DEFAULT_MASS, PhysicsTestUtils.getMass(physicsProperties));

		Body body = PhysicsTestUtils.getBody(physicsProperties);
		assertTrue("Body already contains fixtures", body.getFixtureList().size == 0);

		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsProperties);
		assertEquals("Wrong initialization", PhysicsProperties.DEFAULT_DENSITY, fixtureDef.density);
		assertEquals("Wrong initialization", PhysicsProperties.DEFAULT_FRICTION, fixtureDef.friction);
		assertEquals("Wrong initialization", PhysicsProperties.DEFAULT_BOUNCE_FACTOR, fixtureDef.restitution);
		checkCollisionMask(physicsProperties, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NO_COLLISION);

		assertFalse("Wrong initialization", (Boolean) Reflection.getPrivateField(physicsProperties, "ifOnEdgeBounce"));
	}

	public void testSetShape() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		PolygonShape[] rectangle = new PolygonShape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)};
		physicsProperties.setShape(rectangle);

		checkIfShapesAreTheSameAsInPhysicsObject(rectangle, PhysicsTestUtils.getBody(physicsProperties));
	}

	public void testSetNewShape() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		Shape[] shape = new PolygonShape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)};
		physicsProperties.setShape(shape);

		Body body = PhysicsTestUtils.getBody(physicsProperties);
		PolygonShape[] newShape = new PolygonShape[] {PhysicsTestUtils.createRectanglePolygonShape(2.0f, 3.0f)};
		physicsProperties.setShape(newShape);

		assertNotSame("The new shape hasn't been set", shape, PhysicsTestUtils.getShapes(physicsProperties));
		checkIfShapesAreTheSameAsInPhysicsObject(newShape, body);
	}

	public void testSetSameShape() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		Shape[] rectangle = new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)};
		physicsProperties.setShape(rectangle);
		assertFalse("No shape has been set", body.getFixtureList().size == 0);

		Array<Fixture> fixturesBeforeReset = body.getFixtureList();
		physicsProperties.setShape(rectangle);
		Array<Fixture> fixturesAfterReset = body.getFixtureList();

		assertEquals("Fixture has changed after setiting the same shape again", fixturesBeforeReset, fixturesAfterReset);
	}

	public void testSetNullShapeRemovesAllFixtures() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		physicsProperties.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)});
		assertFalse("No shape has been set", body.getFixtureList().size == 0);

		physicsProperties.setShape(null);
		assertNull("Physics shape isn't null", PhysicsTestUtils.getShapes(physicsProperties));
		assertTrue("Fixture hasn't been removed", body.getFixtureList().size == 0);
	}

	public void testSetShapeUpdatesDensityButNotMass() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		physicsProperties.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f)});
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		float oldDensity = PhysicsTestUtils.getFixtureDef(physicsProperties).density;
		float oldMass = body.getMass();

		physicsProperties.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(111.0f, 111.0f)});

		assertNotSame("Density hasn't changed", oldDensity, PhysicsTestUtils.getFixtureDef(physicsProperties).density);
		assertEquals("Mass has changed", oldMass, body.getMass());
	}

	public void testSetType() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		physicsProperties.setType(PhysicsProperties.Type.FIXED);
		assertEquals("Wrong physics object type", PhysicsProperties.Type.FIXED, PhysicsTestUtils.getType(physicsProperties));
		assertEquals("Wrong body type", BodyType.KinematicBody, body.getType());

		physicsProperties.setType(PhysicsProperties.Type.DYNAMIC);
		assertEquals("Wrong physics object type", PhysicsProperties.Type.DYNAMIC, PhysicsTestUtils.getType(physicsProperties));
		assertEquals("Wrong body type", BodyType.DynamicBody, body.getType());

		physicsProperties.setType(PhysicsProperties.Type.NONE);
		assertEquals("Wrong physics object type", PhysicsProperties.Type.NONE, PhysicsTestUtils.getType(physicsProperties));
		assertEquals("Wrong body type", BodyType.KinematicBody, body.getType());
	}

	public void testSetCollisionBits() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.NONE,
				10.0f, 5.0f);
		checkCollisionMask(physicsProperties, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NO_COLLISION);

		physicsProperties.setType(PhysicsProperties.Type.FIXED);
		checkCollisionMask(physicsProperties, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);

		physicsProperties.setType(PhysicsProperties.Type.NONE);
		checkCollisionMask(physicsProperties, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NO_COLLISION);

		physicsProperties.setType(PhysicsProperties.Type.DYNAMIC);
		checkCollisionMask(physicsProperties, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);
	}

	public void testSetTypeToDynamicUpdatesMass() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.NONE);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		float rectangleSize = 10.0f;
		physicsProperties
				.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(rectangleSize, rectangleSize)});

		float mass = 128.0f;
		physicsProperties.setMass(mass);
		assertEquals("Wrong mass", 0.0f, body.getMass());

		physicsProperties.setType(PhysicsProperties.Type.DYNAMIC);
		assertEquals("Mass hasn't been updated", mass, body.getMass());
	}

	public void testAngle() {
		for (PhysicsProperties.Type type : PhysicsProperties.Type.values()) {
			PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, type);
			assertEquals("Wrong initialization", 0.0f, PhysicsTestUtils.getBody(physicsProperties).getAngle());

			float[] angles = {45.0f, 1.0f, 131.4f, -10.0f, -180.0f};
			for (float angle : angles) {
				physicsProperties.setDirection(angle);
				assertEquals("Wrong angle returned from physics object", angle, physicsProperties.getDirection(), TestUtils.DELTA);
			}
		}
	}

	public void testPosition() {
		for (PhysicsProperties.Type type : PhysicsProperties.Type.values()) {
			PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, type);
			assertEquals("Wrong initialization", new Vector2(), PhysicsTestUtils.getBody(physicsProperties).getPosition());

			Vector2[] positions = {new Vector2(12.34f, 56.78f), new Vector2(-87.65f, -43.21f)};
			for (Vector2 position : positions) {
				physicsProperties.setPosition(position.x, position.y);

				Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter
						.convertBox2dToNormalVector(PhysicsTestUtils.getBody(physicsProperties).getPosition());
				assertEquals("Wrong catroid position", position, physicsObjectCatroidPosition);
				assertEquals("Wrong box2d position", position, physicsProperties.getPosition());
			}

			for (Vector2 position : positions) {
				physicsProperties.setPosition(position);

				Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter
						.convertBox2dToNormalVector(PhysicsTestUtils.getBody(physicsProperties).getPosition());
				assertEquals("Wrong catroid position", position, physicsObjectCatroidPosition);
				assertEquals("Wrong box2d position", position, physicsProperties.getPosition());
			}
		}
	}

	public void testAngleAndPosition() {
		for (PhysicsProperties.Type type : PhysicsProperties.Type.values()) {
			PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, type);
			assertEquals("Wrong initialization", 0.0f, PhysicsTestUtils.getBody(physicsProperties).getAngle());
			assertEquals("initialization", new Vector2(), PhysicsTestUtils.getBody(physicsProperties).getPosition());

			float angle = 15.6f;
			float expectedAngle = 15.6f;
			Vector2 position = new Vector2(12.34f, 56.78f);
			physicsProperties.setDirection(angle);
			physicsProperties.setPosition(position.x, position.y);

			float physicsObjectCatroidAngle = PhysicsWorldConverter.convertBox2dToNormalAngle(PhysicsTestUtils.getBody(
					physicsProperties).getAngle());
			Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter.convertBox2dToNormalVector(PhysicsTestUtils
					.getBody(physicsProperties).getPosition());

			assertEquals("Wrong catroid angle", expectedAngle, physicsObjectCatroidAngle, TestUtils.DELTA);
			assertEquals("Wrong catroid position", position, physicsObjectCatroidPosition);
		}
	}

	public void testSetDensity() {
		for (PhysicsProperties.Type type : PhysicsProperties.Type.values()) {
			PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, type);
			physicsProperties.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});

			float[] densityValues = {0.123f, -0.765f, 24.32f};
			assertFalse("Without any fixtures the correctness won't be tested.", PhysicsTestUtils
					.getBody(physicsProperties).getFixtureList().size == 0);

			for (float density : densityValues) {
				Object[] values = {density};
				String methodName = "setDensity";
				ParameterList paramList = new ParameterList(values);
				Reflection.invokeMethod(physicsProperties, methodName, paramList);
				if (density > 0) {
					assertEquals("Wrong fixture def density in physics object", density, PhysicsTestUtils.getFixtureDef(physicsProperties).density);
				} else {
					assertEquals("Wrong fixture def density in physics object", 0.0f, PhysicsTestUtils.getFixtureDef(physicsProperties).density);
				}
				for (Fixture fixture : PhysicsTestUtils.getBody(physicsProperties).getFixtureList()) {

					if (density > 0) {
						assertEquals("Wrong fixture def density in bodies fixtures.", density, fixture.getDensity());
					} else {
						assertEquals("Wrong fixture def density in bodies fixtures.", 0.0f, fixture.getDensity());
					}
				}
			}
		}
	}

	public void testSetDensityUpdatesMassData() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.DYNAMIC,
				5.0f, 5.0f);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		float oldMass = body.getMass();
		float density = 12.0f;
		assertNotSame("Densities are the same", density, PhysicsTestUtils.getFixtureDef(physicsProperties).density);

		Object[] values = {density};
		String methodName = "setDensity";
		ParameterList paramList = new ParameterList(values);
		Reflection.invokeMethod(physicsProperties, methodName, paramList);
		assertNotSame("Masses are the same", oldMass, body.getMass());
	}

	public void testSetDensityAtMassChange() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		float rectangleSize = 24.0f;
		float[] masses = {PhysicsProperties.MIN_MASS, 1.0f, 24.0f};

		physicsProperties
				.setShape(new Shape[] {PhysicsTestUtils.createRectanglePolygonShape(rectangleSize, rectangleSize)});
		for (float mass : masses) {
			physicsProperties.setMass(mass);
			float actualDensity = body.getMass() / (rectangleSize * rectangleSize);
			assertEquals("Wrong density calculation when mass changes",
					PhysicsTestUtils.getFixtureDef(physicsProperties).density, actualDensity, TestUtils.DELTA);
		}
	}

	public void testSetFriction() {
		for (PhysicsProperties.Type type : PhysicsProperties.Type.values()) {
			PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, type);
			physicsProperties.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});
			float[] frictionValues = {0.123f, -0.765f, 0.32f};

			assertFalse("Without any fixtures the correctness won't be tested.", PhysicsTestUtils
					.getBody(physicsProperties).getFixtureList().size == 0);

			for (float friction : frictionValues) {
				physicsProperties.setFriction(friction);
				if (friction > 0) {
					assertEquals("Wrong fixture def friction in physics object", friction, PhysicsTestUtils.getFixtureDef(physicsProperties).friction);
				} else {
					assertEquals("Wrong fixture def friction in physics object", 0.0f, PhysicsTestUtils.getFixtureDef(physicsProperties).friction);
				}
				for (Fixture fixture : PhysicsTestUtils.getBody(physicsProperties).getFixtureList()) {

					if (friction > 0) {
						assertEquals("Wrong fixture def friction in bodies fixtures.", friction, fixture.getFriction());
					} else {
						assertEquals("Wrong fixture def friction in bodies fixtures.", 0.0f, fixture.getFriction());
					}
				}
			}
		}
	}

	public void testSetBounceFactor() {
		for (PhysicsProperties.Type type : PhysicsProperties.Type.values()) {
			PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, type);
			physicsProperties.setShape(new Shape[] {new PolygonShape(), new PolygonShape()});
			float[] bounceFactors = {0.123f, -0.765f, 0.32f};

			assertFalse("Without any fixtures the correctness won't be tested.", PhysicsTestUtils
					.getBody(physicsProperties).getFixtureList().size == 0);

			for (float value : bounceFactors) {
				physicsProperties.setBounceFactor(value);
				if (value > 0) {
					assertEquals("Wrong fixture def value in physics object", value, PhysicsTestUtils.getFixtureDef(physicsProperties).restitution);
				} else {
					assertEquals("Wrong fixture def value in physics object", 0.0f, PhysicsTestUtils.getFixtureDef(physicsProperties).restitution);
				}
				for (Fixture fixture : PhysicsTestUtils.getBody(physicsProperties).getFixtureList()) {

					if (value > 0) {
						assertEquals("Wrong fixture def value in bodies fixtures.", value, fixture.getRestitution());
					} else {
						assertEquals("Wrong fixture def value in bodies fixtures.", 0.0f, fixture.getRestitution());
					}
				}
			}
		}
	}

	public void testMass() {
		for (PhysicsProperties.Type type : PhysicsProperties.Type.values()) {
			PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, type, 5.0f, 5.0f);
			Body body = PhysicsTestUtils.getBody(physicsProperties);

			checkBodyMassDependingOnType(type, body, PhysicsProperties.DEFAULT_MASS);
			assertEquals("Wrong initialization", PhysicsProperties.DEFAULT_MASS, PhysicsTestUtils.getMass(physicsProperties));

			float[] masses = {PhysicsProperties.MIN_MASS, 0.01f, 1.0f, 12345.0f};
			for (float mass : masses) {
				physicsProperties.setMass(mass);
				checkBodyMassDependingOnType(type, body, mass);
				assertEquals("Wrong mass in physics object", mass, PhysicsTestUtils.getMass(physicsProperties));
			}

			physicsProperties.setMass(PhysicsProperties.MIN_MASS / 10.0f);
			checkBodyMassDependingOnType(type, body, PhysicsProperties.MIN_MASS);
			assertEquals("Body mass isn't set to PhysicsProperties.MIN_MASS / 10.0f", PhysicsProperties.MIN_MASS / 10.0f,
					PhysicsTestUtils.getMass(physicsProperties));

			physicsProperties.setMass(0.0f);
			checkBodyMassDependingOnType(type, body, PhysicsProperties.MIN_MASS);
			assertEquals("Body mass isn't set to 0", 0.0f, PhysicsTestUtils.getMass(physicsProperties));

			physicsProperties.setMass(-1.0f);
			checkBodyMassDependingOnType(type, body, PhysicsProperties.MIN_MASS);
			assertEquals("Body mass isn't set to MIN_MASS", PhysicsProperties.MIN_MASS,
					PhysicsTestUtils.getMass(physicsProperties));
		}
	}

	private void checkBodyMassDependingOnType(PhysicsProperties.Type type, Body body, float expectedBodyMass) {
		if (type != PhysicsProperties.Type.DYNAMIC) {
			expectedBodyMass = 0.0f;
		}
		assertEquals("Wrong mass for " + type.toString().toLowerCase(Locale.getDefault()), expectedBodyMass, body.getMass(), TestUtils
				.DELTA);
	}

	public void testMassWithNoShapeArea() {
		PhysicsProperties[] physicsPropertiesList = {
				PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.DYNAMIC),
				PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.DYNAMIC, 0.0f, 0.0f)};

		for (PhysicsProperties physicsProperties : physicsPropertiesList) {
			Body body = PhysicsTestUtils.getBody(physicsProperties);

			float oldMass = body.getMass();
			float mass = 1.2f;
			assertNotSame("Masses are the same", oldMass, mass);

			physicsProperties.setMass(mass);
			assertEquals("Mass changed", oldMass, body.getMass());
			assertEquals("Wrong mass stored", mass, PhysicsTestUtils.getMass(physicsProperties));
		}
	}

	public void testSetRotationSpeed() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		assertEquals("Wrong initialization", 0.0f, body.getAngularVelocity());
		float rotationSpeed = 20.0f;
		physicsProperties.setRotationSpeed(rotationSpeed);
		float physicsObjectCatroidRotationSpeed = (float) Math.toDegrees(body.getAngularVelocity());
		assertEquals("Set wrong rotation speed", rotationSpeed, physicsObjectCatroidRotationSpeed);
	}

	public void testSetVelocity() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsProperties);

		assertEquals("Wrong initialization", new Vector2(), body.getLinearVelocity());
		Vector2 velocity = new Vector2(12.3f, 45.6f);
		physicsProperties.setVelocity(velocity.x, velocity.y);

		Vector2 physicsObjectCatVelocity = PhysicsWorldConverter.convertBox2dToNormalVector(body.getLinearVelocity());
		assertEquals("Set wrong velocity", velocity, physicsObjectCatVelocity);
	}

	public void testIfOnEndgeBounce() {
		PhysicsProperties physicsProperties = PhysicsTestUtils.createPhysicsProperties(physicsWorld, PhysicsProperties.Type.DYNAMIC,
				1.0f, 1.0f);
		Sprite sprite = new SingleSprite("TestSprite");
		physicsProperties.setIfOnEdgeBounce(true, sprite);

		assertTrue("If on edge bounce hasn't been set correctly",
				(Boolean) Reflection.getPrivateField(physicsProperties, "ifOnEdgeBounce"));
		checkCollisionMask(physicsProperties, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_TO_BOUNCE);

		physicsProperties.setIfOnEdgeBounce(false, sprite);
		assertFalse("If on edge bounce hasn't been set correctly",
				(Boolean) Reflection.getPrivateField(physicsProperties, "ifOnEdgeBounce"));
		checkCollisionMask(physicsProperties, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);
	}

	/*
	 * Helper
	 */
	private void checkCollisionMask(PhysicsProperties physicsProperties, short categoryBits, short maskBits) {
		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsProperties);
		assertEquals("Different category bits", categoryBits, fixtureDef.filter.categoryBits);
		assertEquals("Different bit mask", maskBits, fixtureDef.filter.maskBits);

		Body body = PhysicsTestUtils.getBody(physicsProperties);
		for (Fixture fixture : body.getFixtureList()) {
			Filter filter = fixture.getFilterData();
			assertEquals("Different category bits", categoryBits, filter.categoryBits);
			assertEquals("Different bit mask", maskBits, filter.maskBits);
		}
	}

	private void checkIfShapesAreTheSameAsInPhysicsObject(PolygonShape[] shapes, Body body) {
		Array<Fixture> fixtures = body.getFixtureList();
		assertEquals("Number of shapes and fixtures are not the same", shapes.length, fixtures.size);

		if (body.getFixtureList().size == 0) {
			return;
		}

		PolygonShape currentShape;
		PolygonShape currentPhysicsObjectShape;
		for (int shapeIndex = 0; shapeIndex < shapes.length; shapeIndex++) {
			currentShape = shapes[shapeIndex];
			currentPhysicsObjectShape = (PolygonShape) fixtures.get(shapeIndex).getShape();
			assertEquals("Different vertex count", currentShape.getVertexCount(),
					currentPhysicsObjectShape.getVertexCount());

			Vector2 expectedVertex = new Vector2();
			Vector2 actualVertex = new Vector2();
			for (int vertexIndex = 0; vertexIndex < currentShape.getVertexCount(); vertexIndex++) {
				currentShape.getVertex(vertexIndex, expectedVertex);
				currentPhysicsObjectShape.getVertex(vertexIndex, actualVertex);
				assertEquals("Vertex are different", expectedVertex, actualVertex);
			}
		}
	}

	public void testCloneValues() {
		PhysicsProperties origin = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		origin.setBounceFactor(1);
		origin.setFriction(2);
		origin.setMass(3);
		origin.setRotationSpeed(4);
		origin.setType(PhysicsProperties.Type.FIXED);
		origin.setVelocity(5, 6);
		origin.setPosition(7, 8);
		origin.setDirection(9);

		PhysicsProperties clone = PhysicsTestUtils.createPhysicsProperties(physicsWorld);
		origin.copyTo(clone);

		assertEquals("Bounce factor differs", origin.getBounceFactor(), clone.getBounceFactor());
		assertEquals("Friction differs", origin.getFriction(), clone.getFriction());
		assertEquals("Mass differs", origin.getMass(), clone.getMass());
		assertEquals("Rotation speed differs", origin.getRotationSpeed(), clone.getRotationSpeed());
		assertEquals("Type differs", origin.getType(), clone.getType());
		assertEquals("Velocity differs", origin.getVelocity(), clone.getVelocity());
		assertEquals("Position differs", origin.getPosition(), clone.getPosition());
		assertEquals("Direction differs", origin.getDirection(), clone.getDirection());
	}
}
