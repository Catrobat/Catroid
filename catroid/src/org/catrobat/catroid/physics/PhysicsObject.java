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
package org.catrobat.catroid.physics;

import android.util.Log;

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

import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;

public class PhysicsObject {
	public enum Type {
		DYNAMIC, FIXED, NONE;
	}

	public final static float DEFAULT_DENSITY = 1.0f;
	public final static float DEFAULT_FRICTION = 0.2f;
	public final static float DEFAULT_BOUNCE_FACTOR = 0.8f;
	public final static float DEFAULT_MASS = 1.0f;
	public final static float MIN_MASS = 0.000001f;

	private short collisionMaskRecord = 0;
	private short categoryMaskRecord = PhysicsWorld.CATEGORY_PHYSICSOBJECT;
	private Vector2 velocity;
	private float rotationSpeed;
	private float gravityScale;

	private final Body body;
	private Sprite sprite;
	private final FixtureDef fixtureDef = new FixtureDef();
	private Shape[] shapes;
	private Type type;
	private float mass;
	private boolean ifOnEdgeBounce = false;
	private boolean hangup = false;
	private boolean isVisible = true;
	//	private boolean isTransparent = false;

	private Vector2 bodyAABBlower;
	private Vector2 bodyAABBupper;
	private Vector2 fixtureAABBlower;
	private Vector2 fixtureAABBupper;
	private Vector2 tmpVertice;

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
		if (this.shapes == shapes) {
			return;
		}
		this.shapes = shapes;

		ArrayList<Fixture> fixturesOld = new ArrayList<Fixture>(body.getFixtureList());

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

	public void setVisible(boolean visible) {
		if (visible == isVisible) {
			return;
		}

		isVisible = visible;

		if (isVisible) {
			resume(true);
		} else if (!isVisible) {
			hangup();
		}
	}

	//	public void setTransparent(boolean transparend) {
	//		if (!transparend && isVisible && isTransparent) {
	//			resume(true);
	//			isTransparent = transparend;
	//		} else if (transparend && !isVisible && !isTransparent) {
	//			hangup();
	//			isTransparent = transparend;
	//		}
	//	}

	public void hangup() {
		if (!hangup) {
			Log.d("PhysicsObject", "- - hangup - -");
			recordData();
			setGravityScale(0);
			setVelocity(0, 0);
			setRotationSpeed(0);
			setCollisionBits(PhysicsWorld.NOCOLLISION_MASK, PhysicsWorld.NOCOLLISION_MASK);
			hangup = true;
		}
	}

	public void resume(boolean fromLastRecord) {
		if (hangup) {
			Log.d("PhysicsObject", "- - resume - -");
			if (fromLastRecord) {
				setGravityScale(gravityScale);
				setVelocity(velocity.x, velocity.y);
				setRotationSpeed(rotationSpeed);
			} else {
				setGravityScale(1);
			}
			setCollisionBits(categoryMaskRecord, collisionMaskRecord);
			hangup = false;
		}
	}

	private void recordData() {
		velocity = new Vector2(getVelocity());
		rotationSpeed = getRotationSpeed();
		gravityScale = getGravityScale();
	}

	public void getBoundaryBox(Vector2 lower, Vector2 upper) {
		calcAABB();
		lower.x = PhysicsWorldConverter.toCatroidVector(bodyAABBlower).x;
		lower.y = PhysicsWorldConverter.toCatroidVector(bodyAABBlower).y;
		upper.x = PhysicsWorldConverter.toCatroidVector(bodyAABBupper).x;
		upper.y = PhysicsWorldConverter.toCatroidVector(bodyAABBupper).y;
	}

	private void calcAABB() {
		bodyAABBlower = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
		bodyAABBupper = new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE);
		Transform transform = body.getTransform();
		int len = body.getFixtureList().size();
		ArrayList<Fixture> fixtures = body.getFixtureList();
		for (int i = 0; i < len; i++) {
			Fixture fixture = fixtures.get(i);
			calcAABB(fixture, transform);
		}
	}

	private void calcAABB(Fixture fixture, Transform transform) {
		fixtureAABBlower = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
		fixtureAABBupper = new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE);
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