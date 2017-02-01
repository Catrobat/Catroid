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

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.Reflection;

public abstract class PhysicsCollisionBaseTest extends PhysicsBaseTest implements PhysicsCollisionTestReceiver {

	private static final String TAG = PhysicsCollisionBaseTest.class.getSimpleName();

	protected Sprite sprite2;
	protected PhysicsCollisionTestListener physicsCollisionTestListener;

	protected PhysicsObject physicsObject1;
	protected PhysicsObject physicsObject2;

	protected Vector2 spritePosition;
	protected Vector2 sprite2Position;
	protected PhysicsObject.Type physicsObject1Type = PhysicsObject.Type.NONE;
	protected PhysicsObject.Type physicsObject2Type = PhysicsObject.Type.NONE;

	private int beginContactCounter = 0;
	private int endContactCounter = 0;

	protected static final float DELTA_TIME = 0.1f;
	private static final int MAX_STEPS = 25;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		sprite2 = new Sprite("TestSprite2");
		sprite2.look = new PhysicsLook(sprite2, physicsWorld);
		sprite2.setActionFactory(new ActionPhysicsFactory());

		LookData lookdata = PhysicsTestUtils.generateLookData(rectangle125x125File);
		sprite2.look.setLookData(lookdata);
		assertTrue("getLookData is null", sprite2.look.getLookData() != null);

		physicsObject1 = physicsWorld.getPhysicsObject(sprite);
		physicsObject2 = physicsWorld.getPhysicsObject(sprite2);

		World world = (World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world");
		physicsCollisionTestListener = new PhysicsCollisionTestListener(this, physicsWorld);
		world.setContactListener(physicsCollisionTestListener);

		initializeSpritesForCollision();
	}

	@Override
	protected void tearDown() throws Exception {
		sprite2 = null;
		physicsCollisionTestListener = null;

		super.tearDown();
	}

	protected void initializeSpritesForCollision() {
		if (spritePosition == null || sprite2Position == null) {
			throw new RuntimeException("You must initialize the sprite position for your test physicsObject1Type in your constructor.");
		}

		if (physicsObject1Type == PhysicsObject.Type.NONE || physicsObject2Type == PhysicsObject.Type.NONE) {
			throw new RuntimeException("You must specify a type that can collide for both physics objects in your constructor");
		}

		sprite.look.setPositionInUserInterfaceDimensionUnit(spritePosition.x, spritePosition.y);
		sprite2.look.setPositionInUserInterfaceDimensionUnit(sprite2Position.x, sprite2Position.y);

		physicsObject1.setType(physicsObject1Type);
		physicsObject2.setType(physicsObject2Type);

		physicsObject1.setVelocity(0.0f, 0.0f);
		physicsObject2.setVelocity(0.0f, 0.0f);

		physicsObject1.setRotationSpeed(0.0f);
		physicsObject2.setRotationSpeed(0.0f);
	}

	protected boolean collisionDetected() {
		return (beginContactCounter > 0);
	}

	protected boolean isContactRateOk() {
		Log.d(TAG, "getContactDifference(): " + getContactDifference() + " == 0");
		return (getContactDifference()) == 0;
	}

	protected int getContactDifference() {
		return beginContactCounter - endContactCounter;
	}

	protected boolean simulateFullCollision() {
		int stepCount = 0;
		for (; stepCount < MAX_STEPS; stepCount++) {
			physicsWorld.step(DELTA_TIME);
		}
		if (beginContactCounter - endContactCounter == 0) {
			return true;
		} else {
			Log.e(TAG, "Attention, no full collision occurred.");
			return false;
		}
	}

	@Override
	public void beginContactCallback(Contact contact) {
		beginContactCounter++;
		Log.d(TAG, "beginContactCallback " + beginContactCounter);
	}

	@Override
	public void endContactCallback(Contact contact) {
		endContactCounter++;
		Log.d(TAG, "endContactCallback " + endContactCounter);
	}

	@Override
	public void preSolveCallback(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolveCallback(Contact contact, ContactImpulse impulse) {
	}
}
