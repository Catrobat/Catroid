/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.content.Sprite;

import java.util.Arrays;

public class PhysicsObject {
	private static final String TAG = PhysicsObject.class.getSimpleName();

	public enum Type {
		DYNAMIC, FIXED, NONE;
	}

	public static final float DEFAULT_DENSITY = 1.0f;
	public static final float DEFAULT_FRICTION = 0.2f;
	public static final float MAX_FRICTION = 1.0f;
	public static final float MIN_FRICTION = 0.0f;
	public static final float MIN_DENSITY = 0.0f;
	public static final float MIN_BOUNCE_FACTOR = 0.0f;
	public static final float DEFAULT_BOUNCE_FACTOR = 0.8f;
	public static final float DEFAULT_MASS = 1.0f;
	public static final float MIN_MASS = 0.000001f;

	private short collisionMaskRecord = 0;
	private short categoryMaskRecord = PhysicsWorld.CATEGORY_PHYSICSOBJECT;


	private final Body body;
	private final FixtureDef fixtureDef = new FixtureDef();
	private Shape[] shapes;
	private Type type;
	private float mass;
	private float circumference;
	private boolean ifOnEdgeBounce = false;

	private Vector2 bodyAABBLowerLeft;
	private Vector2 bodyAABBUpperRight;
	private Vector2 fixtureAABBLowerLeft;
	private Vector2 fixtureAABBUpperRight;
	private Vector2 tmpVertice;

	private Vector2 velocity = new Vector2();
	private float rotationSpeed = 0;
	private float gravityScale = 0;
	private Type savedType = Type.NONE;

	public PhysicsObject(Body b, Sprite sprite) {
		body = b;
		body.setUserData(sprite);
		mass = PhysicsObject.DEFAULT_MASS;
		fixtureDef.density = PhysicsObject.DEFAULT_DENSITY;
		fixtureDef.friction = PhysicsObject.DEFAULT_FRICTION;
		fixtureDef.restitution = PhysicsObject.DEFAULT_BOUNCE_FACTOR;
		setType(Type.NONE);
		// --
		tmpVertice = new Vector2();
	}

	public void setShape(Shape[] shapes) {
		if (Arrays.equals(this.shapes, shapes)) {
			return;
		}

		if (shapes != null) {
			this.shapes = Arrays.copyOf(shapes, shapes.length);
		} else {
			this.shapes = null;
		}

		Array<Fixture> fixturesOld = body.getFixtureList();
		for (Fixture fixture : fixturesOld) {
			body.destroyFixture(fixture);
		}

		if (shapes != null) {
			for (Shape tempShape : shapes) {
				fixtureDef.shape = tempShape;
				body.createFixture(fixtureDef);
			}
		}

		setMass(mass);
		calculateCircumference();
	}

	private void calculateCircumference() {
		if (body.getFixtureList().size == 0) {
			//Log.d(TAG, "No fixtures, so reset circumference to zero");
			circumference = 0;
			return;
		}

		calculateAABB();
		float max1 = Math.max(getMassCenter().dst(fixtureAABBUpperRight), getMassCenter().dst(fixtureAABBLowerLeft));
		Vector2 aabbLowerRight = new Vector2(fixtureAABBUpperRight.x, fixtureAABBLowerLeft.y);
		Vector2 aaabbUpperLeft = new Vector2(fixtureAABBLowerLeft.x, fixtureAABBUpperRight.y);
		float max2 = Math.max(getMassCenter().dst(aabbLowerRight), getMassCenter().dst(aaabbUpperLeft));
		circumference = Math.max(max1, max2);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		if (this.type == type) {
			return;
		}
		this.type = type;

		switch (type) {
			case DYNAMIC:
				body.setType(BodyType.DynamicBody);
				body.setGravityScale(1.0f);
				body.setBullet(true);
				setMass(mass);
				collisionMaskRecord = PhysicsWorld.MASK_PHYSICSOBJECT;
				break;
			case FIXED:
				body.setType(BodyType.KinematicBody);
				collisionMaskRecord = PhysicsWorld.MASK_PHYSICSOBJECT;
				break;
			case NONE:
				body.setType(BodyType.KinematicBody);
				collisionMaskRecord = PhysicsWorld.NOCOLLISION_MASK;
				break;
		}
		setCollisionBits(categoryMaskRecord, collisionMaskRecord);
		calculateCircumference();
	}

	public float getDirection() {
		return PhysicsWorldConverter.convertBox2dToNormalAngle(body.getAngle());
	}

	public void setDirection(float degrees) {
		body.setTransform(body.getPosition(), PhysicsWorldConverter.convertNormalToBox2dAngle(degrees));
	}

	public float getX() {
		return PhysicsWorldConverter.convertBox2dToNormalCoordinate(body.getPosition().x);
	}

	public float getY() {
		return PhysicsWorldConverter.convertBox2dToNormalCoordinate(body.getPosition().y);
	}

	public Vector2 getMassCenter() {
		return body.getWorldCenter();
	}

	public float getCircumference() {
		return circumference;
	}

	public Vector2 getPosition() {
		return PhysicsWorldConverter.convertBox2dToNormalVector(body.getPosition());
	}

	public void setX(float x) {
		body.setTransform(PhysicsWorldConverter.convertNormalToBox2dCoordinate(x), body.getPosition().y,
				body.getAngle());
	}

	public void setY(float y) {
		body.setTransform(body.getPosition().x, PhysicsWorldConverter.convertNormalToBox2dCoordinate(y),
				body.getAngle());
	}

	public void setPosition(float x, float y) {
		x = PhysicsWorldConverter.convertNormalToBox2dCoordinate(x);
		y = PhysicsWorldConverter.convertNormalToBox2dCoordinate(y);
		body.setTransform(x, y, body.getAngle());
	}

	public void setPosition(Vector2 position) {
		setPosition(position.x, position.y);
	}

	public float getRotationSpeed() {
		return (float) Math.toDegrees(body.getAngularVelocity());
	}

	public void setRotationSpeed(float degreesPerSecond) {
		body.setAngularVelocity((float) Math.toRadians(degreesPerSecond));
	}

	public Vector2 getVelocity() {
		return PhysicsWorldConverter.convertBox2dToNormalVector(body.getLinearVelocity());
	}

	public void setVelocity(float x, float y) {
		body.setLinearVelocity(PhysicsWorldConverter.convertNormalToBox2dCoordinate(x),
				PhysicsWorldConverter.convertNormalToBox2dCoordinate(y));
	}

	public float getMass() {
		return this.mass;
	}

	public float getBounceFactor() {
		return this.fixtureDef.restitution;
	}

	public void setMass(float mass) {
		this.mass = mass;

		if (mass < 0) {
			this.mass = PhysicsObject.MIN_MASS;
		}
		if (mass < PhysicsObject.MIN_MASS) {
			mass = PhysicsObject.MIN_MASS;
		}
		if (isStaticObject()) {
			return;
		}
		float area = body.getMass() / fixtureDef.density;
		float density = mass / area;
		setDensity(density);
	}

	private boolean isStaticObject() {
		return body.getMass() == 0.0f;
	}

	private void setDensity(float density) {
		if (density < MIN_DENSITY) {
			density = PhysicsObject.MIN_DENSITY;
		}
		fixtureDef.density = density;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setDensity(density);
		}
		body.resetMassData();
	}

	public float getFriction() {
		return fixtureDef.friction;
	}

	public void setFriction(float friction) {

		if (friction < MIN_FRICTION) {
			friction = MIN_FRICTION;
		}
		if (friction > MAX_FRICTION) {
			friction = MAX_FRICTION;
		}

		fixtureDef.friction = friction;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setFriction(friction);
		}
	}

	public void setBounceFactor(float bounceFactor) {

		if (bounceFactor < MIN_BOUNCE_FACTOR) {
			bounceFactor = MIN_BOUNCE_FACTOR;
		}
		fixtureDef.restitution = bounceFactor;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setRestitution(bounceFactor);
		}
	}

	public void setGravityScale(float scale) {
		body.setGravityScale(scale);
	}

	public float getGravityScale() {
		return body.getGravityScale();
	}

	public void setIfOnEdgeBounce(boolean bounce, Sprite sprite) {
		if (ifOnEdgeBounce == bounce) {
			return;
		}
		ifOnEdgeBounce = bounce;

		short maskBits;
		if (bounce) {
			maskBits = PhysicsWorld.MASK_TOBOUNCE;
			body.setUserData(sprite);
		} else {
			maskBits = PhysicsWorld.MASK_PHYSICSOBJECT;
		}

		setCollisionBits(categoryMaskRecord, maskBits);
	}

	protected void setCollisionBits(short categoryBits, short maskBits) {
		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;

		for (Fixture fixture : body.getFixtureList()) {
			Filter filter = fixture.getFilterData();
			filter.categoryBits = categoryBits;
			filter.maskBits = maskBits;
			fixture.setFilterData(filter);
		}
	}

	public void getBoundaryBox(Vector2 lowerLeft, Vector2 upperRight) {
		calculateAABB();
		lowerLeft.x = PhysicsWorldConverter.convertBox2dToNormalVector(bodyAABBLowerLeft).x;
		lowerLeft.y = PhysicsWorldConverter.convertBox2dToNormalVector(bodyAABBLowerLeft).y;
		upperRight.x = PhysicsWorldConverter.convertBox2dToNormalVector(bodyAABBUpperRight).x;
		upperRight.y = PhysicsWorldConverter.convertBox2dToNormalVector(bodyAABBUpperRight).y;
	}

	public void activateHangup() {
		velocity = new Vector2(getVelocity());
		rotationSpeed = getRotationSpeed();
		gravityScale = getGravityScale();

		setGravityScale(0);
		setVelocity(0, 0);
		setRotationSpeed(0);
	}

	public void deactivateHangup(boolean record) {
		if (record) {
			setGravityScale(gravityScale);
			setVelocity(velocity.x, velocity.y);
			setRotationSpeed(rotationSpeed);
		} else {
			setGravityScale(1);
		}
	}

	public void activateNonColliding() {
		setCollisionBits(categoryMaskRecord, PhysicsWorld.NOCOLLISION_MASK);
	}

	public void deactivateNonColliding(boolean record) {
		if (record) {
			setCollisionBits(categoryMaskRecord, collisionMaskRecord);
		}
	}

	public void activateFixed() {
		savedType = getType();
		setType(Type.FIXED);
	}

	public void deactivateFixed(boolean record) {
		if (record) {
			setType(savedType);
		}
	}

	private void calculateAABB() {
		bodyAABBLowerLeft = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
		bodyAABBUpperRight = new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE);
		Transform transform = body.getTransform();
		int len = body.getFixtureList().size;
		Array<Fixture> fixtures = body.getFixtureList();
		if (fixtures.size == 0) {
			bodyAABBLowerLeft.x = 0;
			bodyAABBLowerLeft.y = 0;
			bodyAABBUpperRight.x = 0;
			bodyAABBUpperRight.y = 0;
		}
		for (int i = 0; i < len; i++) {
			Fixture fixture = fixtures.get(i);
			calculateAABB(fixture, transform);
		}
	}

	private void calculateAABB(Fixture fixture, Transform transform) {
		fixtureAABBLowerLeft = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
		fixtureAABBUpperRight = new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE);
		if (fixture.getType() == Shape.Type.Circle) {
			CircleShape shape = (CircleShape) fixture.getShape();
			float radius = shape.getRadius();
			tmpVertice.set(shape.getPosition());
			tmpVertice.rotate(transform.getRotation()).add(transform.getPosition());
			fixtureAABBLowerLeft.set(tmpVertice.x - radius, tmpVertice.y - radius);
			fixtureAABBUpperRight.set(tmpVertice.x + radius, tmpVertice.y + radius);

		} else if (fixture.getType() == Shape.Type.Polygon) {
			PolygonShape shape = (PolygonShape) fixture.getShape();
			int vertexCount = shape.getVertexCount();

			shape.getVertex(0, tmpVertice);
			fixtureAABBLowerLeft.set(transform.mul(tmpVertice));
			fixtureAABBUpperRight.set(fixtureAABBLowerLeft);
			for (int i = 1; i < vertexCount; i++) {
				shape.getVertex(i, tmpVertice);
				transform.mul(tmpVertice);
				fixtureAABBLowerLeft.x = Math.min(fixtureAABBLowerLeft.x, tmpVertice.x);
				fixtureAABBLowerLeft.y = Math.min(fixtureAABBLowerLeft.y, tmpVertice.y);
				fixtureAABBUpperRight.x = Math.max(fixtureAABBUpperRight.x, tmpVertice.x);
				fixtureAABBUpperRight.y = Math.max(fixtureAABBUpperRight.y, tmpVertice.y);
			}
		}

		bodyAABBLowerLeft.x = Math.min(fixtureAABBLowerLeft.x, bodyAABBLowerLeft.x);
		bodyAABBLowerLeft.y = Math.min(fixtureAABBLowerLeft.y, bodyAABBLowerLeft.y);
		bodyAABBUpperRight.x = Math.max(fixtureAABBUpperRight.x, bodyAABBUpperRight.x);
		bodyAABBUpperRight.y = Math.max(fixtureAABBUpperRight.y, bodyAABBUpperRight.y);
	}

}