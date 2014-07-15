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

import android.test.AndroidTestCase;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

public class PhysicsObjectTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private static final String TAG = PhysicsObjectTest.class.getSimpleName();

	private PhysicsWorld physicsWorld;

	@Override
	protected void setUp() throws Exception {
		// TODO[physics] maybe initialize with values from a real project
		physicsWorld = new PhysicsWorld(1920, 1600);
	}

	@Override
	protected void tearDown() throws Exception {
		physicsWorld = null;
	}

	public void testDefaultSettings() {
		assertEquals("Wrong configuration", 1.0f, PhysicsObject.DEFAULT_DENSITY);
		assertEquals("Wrong configuration", 0.2f, PhysicsObject.DEFAULT_FRICTION);
		assertEquals("Wrong configuration", 0.8f, PhysicsObject.DEFAULT_BOUNCE_FACTOR);

		assertEquals("Wrong configuration", 1.0f, PhysicsObject.DEFAULT_MASS);
		assertEquals("Wrong configuration", 0.000001f, PhysicsObject.MIN_MASS);

		// TODO[physics] no PhysicsObject.COLLISION_MASK in new physics version
		//assertEquals("Wrong configuration", 0x0004, PhysicsObject.COLLISION_MASK);
	}

	public void testNullBody() {
		try {
			new PhysicsObject(null, new Sprite("TestSprite"));
			fail("Creating a physics object with no body doesn't cause a NullPointerException");
		} catch (NullPointerException exception) {
			// Expected behavior.
		}
	}

	public void testDefaultProperties() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		assertEquals("Wrong initialization", PhysicsObject.Type.NONE, PhysicsTestUtils.getType(physicsObject));
		assertEquals("Wrong initialization", PhysicsObject.DEFAULT_MASS, PhysicsTestUtils.getMass(physicsObject));

		Body body = PhysicsTestUtils.getBody(physicsObject);
		assertTrue("Body already contains fixtures", body.getFixtureList().isEmpty());

		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsObject);
		assertEquals("Wrong initialization", PhysicsObject.DEFAULT_DENSITY, fixtureDef.density);
		assertEquals("Wrong initialization", PhysicsObject.DEFAULT_FRICTION, fixtureDef.friction);
		assertEquals("Wrong initialization", PhysicsObject.DEFAULT_BOUNCE_FACTOR, fixtureDef.restitution);

		short categoryBits = PhysicsWorld.CATEGORY_PHYSICSOBJECT;
		short collisionBits = 0;
		checkCollisionMask(physicsObject, categoryBits, collisionBits);

		assertFalse("Wrong initialization", (Boolean) Reflection.getPrivateField(physicsObject, "ifOnEdgeBounce"));
	}

	public void testSetShape() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		PolygonShape[] rectangle = new PolygonShape[] { PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f) };
		physicsObject.setShape(rectangle);

		checkIfShapesAreTheSameAsInPhysicsObject(rectangle, PhysicsTestUtils.getBody(physicsObject));
	}

	public void testSetNewShape() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Shape[] shape = new PolygonShape[] { PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f) };
		physicsObject.setShape(shape);

		Body body = PhysicsTestUtils.getBody(physicsObject);
		PolygonShape[] newShape = new PolygonShape[] { PhysicsTestUtils.createRectanglePolygonShape(1.0f, 2.0f) };
		physicsObject.setShape(newShape);

		assertSame("The new shape hasn't been set", newShape, PhysicsTestUtils.getShapes(physicsObject));
		checkIfShapesAreTheSameAsInPhysicsObject(newShape, body);
	}

	public void testSetSameShape() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		Shape[] rectangle = new Shape[] { PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f) };
		physicsObject.setShape(rectangle);
		assertFalse("No shape has been set", body.getFixtureList().isEmpty());

		List<Fixture> fixturesBeforeReset = new ArrayList<Fixture>(body.getFixtureList());
		physicsObject.setShape(rectangle);
		List<Fixture> fixturesAfterReset = new ArrayList<Fixture>(body.getFixtureList());

		assertEquals("Fixture has changed after setiting the same shape again", fixturesBeforeReset, fixturesAfterReset);
	}

	public void testSetNullShapeRemovesAllFixtures() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		physicsObject.setShape(new Shape[] { PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f) });
		assertFalse("No shape has been set", body.getFixtureList().isEmpty());

		physicsObject.setShape(null);
		assertNull("Physics shape isn't null", PhysicsTestUtils.getShapes(physicsObject));
		assertTrue("Fixture hasn't been removed", body.getFixtureList().isEmpty());
	}

	public void testSetShapeUpdatesDensityButNotMass() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		physicsObject.setShape(new Shape[] { PhysicsTestUtils.createRectanglePolygonShape(5.0f, 5.0f) });
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float oldDensity = PhysicsTestUtils.getFixtureDef(physicsObject).density;
		float oldMass = body.getMass();

		physicsObject.setShape(new Shape[] { PhysicsTestUtils.createRectanglePolygonShape(111.0f, 111.0f) });

		assertNotSame("Density hasn't changed", oldDensity, PhysicsTestUtils.getFixtureDef(physicsObject).density);
		assertEquals("Mass has changed", oldMass, body.getMass());
	}

	public void testSetType() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		physicsObject.setType(PhysicsObject.Type.FIXED);
		assertEquals("Wrong physics object type", PhysicsObject.Type.FIXED, PhysicsTestUtils.getType(physicsObject));
		assertEquals("Wrong body type", BodyType.KinematicBody, body.getType());

		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		assertEquals("Wrong physics object type", PhysicsObject.Type.DYNAMIC, PhysicsTestUtils.getType(physicsObject));
		assertEquals("Wrong body type", BodyType.DynamicBody, body.getType());

		physicsObject.setType(PhysicsObject.Type.NONE);
		assertEquals("Wrong physics object type", PhysicsObject.Type.NONE, PhysicsTestUtils.getType(physicsObject));
		assertEquals("Wrong body type", BodyType.KinematicBody, body.getType());
	}

	public void testSetCollisionBits() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.NONE,
				10.0f, 5.0f);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NOCOLLISION);

		physicsObject.setType(PhysicsObject.Type.FIXED);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);

		physicsObject.setType(PhysicsObject.Type.NONE);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_NOCOLLISION);

		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);
	}

	public void testSetTypeToDynamicUpdatesMass() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.NONE);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float rectangeSize = 10.0f;
		physicsObject
				.setShape(new Shape[] { PhysicsTestUtils.createRectanglePolygonShape(rectangeSize, rectangeSize) });

		float mass = 128.0f;
		physicsObject.setMass(mass);
		assertEquals("Wrong mass", 0.0f, body.getMass());

		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		assertEquals("Mass hasn't been updated", mass, body.getMass());
	}

	public void testAngle() {
		// TODO[Physics] angle problem
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			assertEquals("Wrong initialization", 0.0f, PhysicsTestUtils.getBody(physicsObject).getAngle());

			float[] degrees = { 45.0f, 1.0f, 131.4f, -10.0f, -180.0f };
			float[] expectedDegrees = { 45.0f, 1.0f, 131.4f, -10.0f, -180.0f };

			int idx = 0;
			for (float angle : degrees) {

				//angle = computeScratchCompatibleAngleForDirectSetting(angle);

				physicsObject.setDirection(angle);
				float physicsObjectCatroidAngle = PhysicsWorldConverter.convertBox2dToNormalAngle(PhysicsTestUtils
						.getBody(physicsObject).getAngle());
				physicsObjectCatroidAngle = physicsObject.getDirection();

				assertEquals("Wrong catroid angle", expectedDegrees[idx], physicsObjectCatroidAngle, TestUtils.DELTA);
				assertEquals("Wrong box2d angle", expectedDegrees[idx], physicsObject.getDirection(), TestUtils.DELTA);
				//assertEquals("Wrong catroid angle", angle, physicsObjectCatroidAngle);
				//assertEquals("Wrong box2d angle", angle, physicsObject.getDirection());
				idx++;
			}
		}
	}

	public void testPosition() {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			assertEquals("Wrong initialization", new Vector2(), PhysicsTestUtils.getBody(physicsObject).getPosition());

			Vector2[] positions = { new Vector2(12.34f, 56.78f), new Vector2(-87.65f, -43.21f) };
			for (Vector2 position : positions) {
				physicsObject.setPosition(position.x, position.y);

				Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter
						.convertBox2dToNormalVector(PhysicsTestUtils.getBody(physicsObject).getPosition());
				assertEquals("Wrong catroid position", position, physicsObjectCatroidPosition);
				assertEquals("Wrong box2d position", position, physicsObject.getPosition());
			}

			for (Vector2 position : positions) {
				physicsObject.setPosition(position);

				Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter
						.convertBox2dToNormalVector(PhysicsTestUtils.getBody(physicsObject).getPosition());
				assertEquals("Wrong catroid position", position, physicsObjectCatroidPosition);
				assertEquals("Wrong box2d position", position, physicsObject.getPosition());
			}
		}
	}

	public void testAngleAndPosition() {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			assertEquals("Wrong initialization", 0.0f, PhysicsTestUtils.getBody(physicsObject).getAngle());
			assertEquals("initialization", new Vector2(), PhysicsTestUtils.getBody(physicsObject).getPosition());

			float angle = 15.6f;
			float expectedAngle = 15.6f;
			Vector2 position = new Vector2(12.34f, 56.78f);
			physicsObject.setDirection(angle);
			physicsObject.setPosition(position.x, position.y);

			float physicsObjectCatroidAngle = PhysicsWorldConverter.convertBox2dToNormalAngle(PhysicsTestUtils.getBody(
					physicsObject).getAngle());
			Vector2 physicsObjectCatroidPosition = PhysicsWorldConverter.convertBox2dToNormalVector(PhysicsTestUtils
					.getBody(physicsObject).getPosition());

			assertEquals("Wrong catroid angle", expectedAngle, physicsObjectCatroidAngle, TestUtils.DELTA);
			assertEquals("Wrong catroid position", position, physicsObjectCatroidPosition);
		}
	}

	public void testSetDensity() {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			physicsObject.setShape(new Shape[] { new PolygonShape(), new PolygonShape() });
			float[] densityValues = { 0.123f, -0.765f, 24.32f };

			FixtureProptertyTestTemplate densityTemplate = new FixtureProptertyTestTemplate(physicsObject,
					densityValues) {
				@Override
				protected void setValue(float value) {
					Object[] values = { value };
					String methodName = "setDensity";
					ParameterList paramList = new ParameterList(values);
					Reflection.invokeMethod(physicsObject, methodName, paramList);
				}

				@Override
				protected float getFixtureValue(Fixture fixture) {
					return fixture.getDensity();
				}

				@Override
				protected float getFixtureDefValue() {
					return PhysicsTestUtils.getFixtureDef(physicsObject).density;
				}
			};
			densityTemplate.test();
		}
	}

	public void testSetDensityUpdatesMassData() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC,
				5.0f, 5.0f);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float oldMass = body.getMass();
		float density = 12.0f;
		assertNotSame("Densities are the same", density, PhysicsTestUtils.getFixtureDef(physicsObject).density);

		Object[] values = { density };
		String methodName = "setDensity";
		ParameterList paramList = new ParameterList(values);
		Reflection.invokeMethod(physicsObject, methodName, paramList);
		assertNotSame("Masses are the same", oldMass, body.getMass());
	}

	public void testSetDensityAtMassChange() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		float rectangleSize = 24.0f;
		float[] masses = { PhysicsObject.MIN_MASS, 1.0f, 24.0f };

		physicsObject
				.setShape(new Shape[] { PhysicsTestUtils.createRectanglePolygonShape(rectangleSize, rectangleSize) });
		for (float mass : masses) {
			physicsObject.setMass(mass);
			float actualDensity = body.getMass() / (rectangleSize * rectangleSize);
			assertEquals("Wrong density calculation when mass changes",
					PhysicsTestUtils.getFixtureDef(physicsObject).density, actualDensity);
		}
	}

	public void testSetFriction() {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			physicsObject.setShape(new Shape[] { new PolygonShape(), new PolygonShape() });

			float[] frictionValues = { 0.123f, -0.765f, 0.32f };
			FixtureProptertyTestTemplate frictionTemplate = new FixtureProptertyTestTemplate(physicsObject,
					frictionValues) {
				@Override
				protected void setValue(float value) {
					physicsObject.setFriction(value);
				}

				@Override
				protected float getFixtureValue(Fixture fixture) {
					return fixture.getFriction();
				}

				@Override
				protected float getFixtureDefValue() {
					return PhysicsTestUtils.getFixtureDef(physicsObject).friction;
				}
			};
			frictionTemplate.test();
		}
	}

	public void testSetBounceFactor() {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type);
			physicsObject.setShape(new Shape[] { new PolygonShape(), new PolygonShape() });
			float[] bounceFactors = { 0.123f, -0.765f, 0.32f };

			FixtureProptertyTestTemplate restitutionTemplate = new FixtureProptertyTestTemplate(physicsObject,
					bounceFactors) {
				@Override
				protected void setValue(float value) {
					physicsObject.setBounceFactor(value);
				}

				@Override
				protected float getFixtureValue(Fixture fixture) {
					return fixture.getRestitution();
				}

				@Override
				protected float getFixtureDefValue() {
					return PhysicsTestUtils.getFixtureDef(physicsObject).restitution;
				}
			};
			restitutionTemplate.test();
		}
	}

	public void testMass() {
		for (PhysicsObject.Type type : PhysicsObject.Type.values()) {
			PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, type, 5.0f, 5.0f);
			Body body = PhysicsTestUtils.getBody(physicsObject);

			checkBodyMassDependingOnType(type, body, PhysicsObject.DEFAULT_MASS);
			assertEquals("Wrong initialization", PhysicsObject.DEFAULT_MASS, PhysicsTestUtils.getMass(physicsObject));

			float[] masses = { PhysicsObject.MIN_MASS, 0.01f, 1.0f, 12345.0f };
			for (float mass : masses) {
				physicsObject.setMass(mass);
				checkBodyMassDependingOnType(type, body, mass);
				assertEquals("Wrong mass in physics object", mass, PhysicsTestUtils.getMass(physicsObject));
			}

			physicsObject.setMass(PhysicsObject.MIN_MASS / 10.0f);
			checkBodyMassDependingOnType(type, body, PhysicsObject.MIN_MASS);
			assertEquals("Body mass isn't set to PhysicsObject.MIN_MASS / 10.0f", PhysicsObject.MIN_MASS / 10.0f,
					PhysicsTestUtils.getMass(physicsObject));

			physicsObject.setMass(0.0f);
			checkBodyMassDependingOnType(type, body, PhysicsObject.MIN_MASS);
			assertEquals("Body mass isn't set to 0", 0.0f, PhysicsTestUtils.getMass(physicsObject));

			physicsObject.setMass(-1.0f);
			checkBodyMassDependingOnType(type, body, PhysicsObject.MIN_MASS);
			assertEquals("Body mass isn't set to MIN_MASS", PhysicsObject.MIN_MASS,
					PhysicsTestUtils.getMass(physicsObject));

		}
	}

	private void checkBodyMassDependingOnType(PhysicsObject.Type type, Body body, float expectedBodyMass) {
		if (type != PhysicsObject.Type.DYNAMIC) {
			expectedBodyMass = 0.0f;
		}
		assertEquals("Wrong mass for " + type.toString().toLowerCase(), expectedBodyMass, body.getMass());
	}

	public void testMassWithNoShapeArea() {
		PhysicsObject[] physicsObjects = {
				PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC),
				PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC, 0.0f, 0.0f) };

		for (PhysicsObject physicsObject : physicsObjects) {
			Body body = PhysicsTestUtils.getBody(physicsObject);

			float oldMass = body.getMass();
			float mass = 1.2f;
			assertNotSame("Masses are the same", oldMass, mass);

			physicsObject.setMass(mass);
			assertEquals("Mass changed", oldMass, body.getMass());
			assertEquals("Wrong mass stored", mass, PhysicsTestUtils.getMass(physicsObject));
		}
	}

	public void testSetRotationSpeed() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		assertEquals("Wrong initialization", 0.0f, body.getAngularVelocity());
		float rotationSpeed = 20.0f;
		physicsObject.setRotationSpeed(rotationSpeed);
		float physicsObjectCatroidRotationSpeed = (float) Math.toDegrees(body.getAngularVelocity());
		assertEquals("Set wrong rotation speed", rotationSpeed, physicsObjectCatroidRotationSpeed);
	}

	public void testSetVelocity() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC);
		Body body = PhysicsTestUtils.getBody(physicsObject);

		assertEquals("Wrong initialization", new Vector2(), body.getLinearVelocity());
		Vector2 velocity = new Vector2(12.3f, 45.6f);
		physicsObject.setVelocity(velocity.x, velocity.y);

		Vector2 physicsObjectCatVelocity = PhysicsWorldConverter.convertBox2dToNormalVector(body.getLinearVelocity());
		assertEquals("Set wrong velocity", velocity, physicsObjectCatVelocity);
	}

	public void testIfOnEndgeBounce() {
		PhysicsObject physicsObject = PhysicsTestUtils.createPhysicsObject(physicsWorld, PhysicsObject.Type.DYNAMIC,
				1.0f, 1.0f);
		Sprite sprite = new Sprite("TestSprite");
		physicsObject.setIfOnEdgeBounce(true, sprite);

		assertTrue("If on edge bounce hasn't been set correctly",
				(Boolean) Reflection.getPrivateField(physicsObject, "ifOnEdgeBounce"));
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_TOBOUNCE);

		physicsObject.setIfOnEdgeBounce(false, sprite);
		assertFalse("If on edge bounce hasn't been set correctly",
				(Boolean) Reflection.getPrivateField(physicsObject, "ifOnEdgeBounce"));
		checkCollisionMask(physicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT, PhysicsWorld.MASK_PHYSICSOBJECT);
	}

	// SANTA'S LITTLE HELPERS :)
	// (TODO: Also need to be tested.)
	// Fixture property helper.
	private abstract class FixtureProptertyTestTemplate {
		protected final PhysicsObject physicsObject;
		protected final float[] values;

		public FixtureProptertyTestTemplate(PhysicsObject physicsObject, float[] values) {
			this.physicsObject = physicsObject;
			this.values = values;
		}

		public void test() {
			assertTrue("Without any values the correctness won't be tested.", values.length > 0);
			assertFalse("Without any fixtures the correctness won't be tested.", PhysicsTestUtils
					.getBody(physicsObject).getFixtureList().isEmpty());

			for (float value : values) {
				setValue(value);
				if (value > 0) {
					assertEquals("Wrong fixture def value in physics object", value, getFixtureDefValue());
				}
				else{
					assertEquals("Wrong fixture def value in physics object", 0.0f, getFixtureDefValue());
				}
				for (Fixture fixture : PhysicsTestUtils.getBody(physicsObject).getFixtureList()) {

					if (value > 0) {
						assertEquals("Wrong fixture def value in bodies fixtures.", value, getFixtureValue(fixture));
					}
					else{
						assertEquals("Wrong fixture def value in bodies fixtures.", 0.0f, getFixtureValue(fixture));
					}
				}
			}
		}

		protected abstract float getFixtureValue(Fixture fixture);

		protected abstract float getFixtureDefValue();

		protected abstract void setValue(float value);
	}

	/*
	 * Helper
	 */
	private void checkCollisionMask(PhysicsObject physicsObject, short categoryBits, short maskBits) {
		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsObject);
		assertEquals("Different category bits", categoryBits, fixtureDef.filter.categoryBits);
		assertEquals("Different bit mask", maskBits, fixtureDef.filter.maskBits);

		Body body = PhysicsTestUtils.getBody(physicsObject);
		for (Fixture fixture : body.getFixtureList()) {
			Filter filter = fixture.getFilterData();
			assertEquals("Different category bits", categoryBits, filter.categoryBits);
			assertEquals("Different bit mask", maskBits, filter.maskBits);
		}
	}

	private void checkIfShapesAreTheSameAsInPhysicsObject(PolygonShape[] shapes, Body body) {
		List<Fixture> fixtures = body.getFixtureList();
		assertEquals("Number of shapes and fixtures are not the same", shapes.length, fixtures.size());

		if (body.getFixtureList().isEmpty()) {
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

	public static float computeScratchCompatibleAngleForDirectSetting(float direction) {
		direction = direction % 360;
		if (direction < 0) {
			direction += 360f;
		}
		direction = 180f - direction;
		return direction;
	}
}
