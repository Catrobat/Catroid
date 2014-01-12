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
package org.catrobat.catroid.physic;

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

import java.util.ArrayList;
import java.util.List;

public class PhysicsObject {
	public enum Type {
		DYNAMIC, FIXED, NONE;
	}

	public final static float DEFAULT_DENSITY = 1.0f;
	public final static float DEFAULT_FRICTION = 0.2f;
	public final static float DEFAULT_BOUNCE_FACTOR = 0.8f;
	public final static float DEFAULT_MASS = 1.0f;
	public final static float MIN_MASS = 0.000001f;
	public final static short COLLISION_MASK = 0x0004;

	private final Body body;
	private final FixtureDef fixtureDef = new FixtureDef();
	private Shape[] shapes;
	private Type type;
	private float mass;
	private boolean ifOnEdgeBounce = false;

	private static Vector2 bodyAABBlower;
	private static Vector2 bodyAABBupper;
	private static Vector2 fixtureAABBlower;
	private static Vector2 fixtureAABBupper;
	private static Vector2 tmpVertice;

	public PhysicsObject(Body body) {
		this.body = body;

		mass = PhysicsObject.DEFAULT_MASS;
		fixtureDef.density = PhysicsObject.DEFAULT_DENSITY;
		fixtureDef.friction = PhysicsObject.DEFAULT_FRICTION;
		fixtureDef.restitution = PhysicsObject.DEFAULT_BOUNCE_FACTOR;

		bodyAABBlower = new Vector2();
		bodyAABBupper = new Vector2();
		fixtureAABBlower = new Vector2();
		fixtureAABBupper = new Vector2();
		tmpVertice = new Vector2();

		short collisionBits = 0;
		setCollisionBits(collisionBits, collisionBits);
		setType(Type.NONE);
	}

	public void setShape(Shape[] shapes) {
		if (this.shapes == shapes) {
			return;
		}
		this.shapes = shapes;

		List<Fixture> fixturesOld = new ArrayList<Fixture>(body.getFixtureList());

		if (shapes != null) {
			for (Shape tempShape : shapes) {
				fixtureDef.shape = tempShape;
				body.createFixture(fixtureDef);
			}
		}

		for (Fixture fixture : fixturesOld) {
			body.destroyFixture(fixture);
		}

		setMass(mass);
	}

	public void setIfOnEdgeBounce(boolean bounce) {
		if (ifOnEdgeBounce == bounce) {
			return;
		}
		ifOnEdgeBounce = bounce;

		short maskBits;
		if (bounce) {
			maskBits = PhysicsObject.COLLISION_MASK | PhysicsBoundaryBox.COLLISION_MASK;
		} else {
			maskBits = PhysicsObject.COLLISION_MASK;
		}

		setCollisionBits(fixtureDef.filter.categoryBits, maskBits);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		if (this.type == type) {
			return;
		}
		this.type = type;

		short collisionMask = 0;
		switch (type) {
			case DYNAMIC:
				body.setType(BodyType.DynamicBody);
				body.setGravityScale(1.0f);
				setMass(mass);
				collisionMask = PhysicsObject.COLLISION_MASK;
				break;
			case FIXED:
				body.setType(BodyType.KinematicBody);
				collisionMask = PhysicsObject.COLLISION_MASK;
				break;
			case NONE:
				body.setType(BodyType.KinematicBody);
				break;
		}
		setCollisionBits(collisionMask, collisionMask);
	}

	/**
	 * if ((categoryBitsB & maskBitsA) != 0) {
	 * - A collides with B.
	 * }
	 */
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

	public float getDirection() {
		return PhysicsWorldConverter.toCatroidAngle(body.getAngle());
	}

	public void setDirection(float degrees) {
		body.setTransform(body.getPosition(), PhysicsWorldConverter.toBox2dAngle(degrees));
	}

	public float getX() {
		return PhysicsWorldConverter.toCatroidCoordinate(body.getPosition().x);
	}

	public float getY() {
		return PhysicsWorldConverter.toCatroidCoordinate(body.getPosition().y);
	}

	public Vector2 getPosition() {
		return PhysicsWorldConverter.toCatroidVector(body.getPosition());
	}

	public void setX(float x) {
		body.setTransform(PhysicsWorldConverter.toBox2dCoordinate(x), body.getPosition().y, body.getAngle());
	}

	public void setY(float y) {
		body.setTransform(body.getPosition().x, PhysicsWorldConverter.toBox2dCoordinate(y), body.getAngle());
	}

	public void setPosition(float x, float y) {
		x = PhysicsWorldConverter.toBox2dCoordinate(x);
		y = PhysicsWorldConverter.toBox2dCoordinate(y);
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
		return PhysicsWorldConverter.toCatroidVector(body.getLinearVelocity());
	}

	public void setVelocity(float x, float y) {
		body.setLinearVelocity(PhysicsWorldConverter.toBox2dCoordinate(x), PhysicsWorldConverter.toBox2dCoordinate(y));
	}

	public float getMass() {
		return body.getMass();
	}

	public Body getBody() {
		return body;
	}

	public void setMass(float mass) {
		if (mass < PhysicsObject.MIN_MASS) {
			mass = PhysicsObject.MIN_MASS;
		}

		//		if (mass != Integer.MAX_VALUE) {
		this.mass = mass;
		//		}

		float bodyMass = body.getMass();
		if (bodyMass == 0.0f) {
			return;
		}

		float area = bodyMass / fixtureDef.density;
		float density = mass / area;
		setDensity(density);
	}

	private void setDensity(float density) {
		fixtureDef.density = density;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setDensity(density);
		}
		body.resetMassData();
	}

	public void setFriction(float friction) {
		fixtureDef.friction = friction;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setFriction(friction);
		}
	}

	public void setBounceFactor(float bounceFactor) {
		fixtureDef.restitution = bounceFactor;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setRestitution(bounceFactor);
		}
	}

	public void setVisible(boolean visible) {
		// TODO[PHYSIC]
	}

	public void getBoundaryBox(Vector2 lower, Vector2 upper) {
		calcAABB(body);
		lower = PhysicsWorldConverter.toCatroidVector(bodyAABBlower);
		upper = PhysicsWorldConverter.toCatroidVector(bodyAABBupper);
	}

	protected void calcAABB(Body body) {
		Transform transform = body.getTransform();
		int len = body.getFixtureList().size();
		List<Fixture> fixtures = body.getFixtureList();
		for (int i = 0; i < len; i++) {
			Fixture fixture = fixtures.get(i);
			calcAABB(fixture, transform);
		}
	}

	private void calcAABB(Fixture fixture, Transform transform) {
		if (fixture.getType() == Shape.Type.Circle) {
			CircleShape shape = (CircleShape) fixture.getShape();
			float radius = shape.getRadius();
			tmpVertice.set(shape.getPosition());
			tmpVertice.rotate(transform.getRotation()).add(transform.getPosition());
			fixtureAABBlower.set(tmpVertice.x - radius, tmpVertice.y - radius);
			fixtureAABBupper.set(tmpVertice.x + radius, tmpVertice.y + radius);

		} else if (fixture.getType() == Shape.Type.Polygon) {
			PolygonShape shape = (PolygonShape) fixture.getShape();
			int vertexCount = shape.getVertexCount();

			shape.getVertex(0, tmpVertice);
			fixtureAABBlower.set(transform.mul(tmpVertice));
			fixtureAABBupper.set(fixtureAABBlower);
			for (int i = 1; i < vertexCount; i++) {
				shape.getVertex(i, tmpVertice);
				transform.mul(tmpVertice);
				fixtureAABBlower.x = Math.min(fixtureAABBlower.x, tmpVertice.x);
				fixtureAABBlower.y = Math.min(fixtureAABBlower.y, tmpVertice.y);
				fixtureAABBupper.x = Math.max(fixtureAABBupper.x, tmpVertice.x);
				fixtureAABBupper.y = Math.max(fixtureAABBupper.y, tmpVertice.y);
			}
		}

		bodyAABBlower.x = Math.min(fixtureAABBlower.x, bodyAABBlower.x);
		bodyAABBlower.y = Math.min(fixtureAABBlower.y, bodyAABBlower.y);
		bodyAABBupper.x = Math.max(fixtureAABBupper.x, bodyAABBupper.x);
		bodyAABBupper.y = Math.max(fixtureAABBupper.y, bodyAABBupper.y);
	}
}