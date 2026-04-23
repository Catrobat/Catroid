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
import org.catrobat.catroid.test.utils.Reflection;

import static junit.framework.Assert.assertNotNull;

public class PhysicsCollisionTestRule extends PhysicsTestRule implements PhysicsCollisionTestReceiver {

	private static final String TAG = PhysicsCollisionTestRule.class.getSimpleName();

	public Sprite sprite2;
	public PhysicsCollisionTestListener physicsCollisionTestListener;

	public PhysicsObject physicsObject1;
	public PhysicsObject physicsObject2;

	public Vector2 spritePosition;
	public Vector2 sprite2Position;
	public PhysicsObject.Type physicsObject1Type = PhysicsObject.Type.NONE;
	public PhysicsObject.Type physicsObject2Type = PhysicsObject.Type.NONE;

	public int beginContactCounter = 0;
	public int endContactCounter = 0;

	public static final float DELTA_TIME = 0.1f;
	public static final int MAX_STEPS = 25;

	@Override
	protected void before() throws Throwable {
		super.before();
		sprite2 = new Sprite("TestSprite2");
		project.getDefaultScene().addSprite(sprite2);
		sprite2.look = new PhysicsLook(sprite2, physicsWorld);
		sprite2.setActionFactory(new ActionPhysicsFactory());

		LookData lookdata = PhysicsTestUtils.generateLookData(rectangle125x125File);
		sprite2.look.setLookData(lookdata);
		assertNotNull(sprite2.look.getLookData());

		physicsObject1 = physicsWorld.getPhysicsObject(sprite);
		physicsObject2 = physicsWorld.getPhysicsObject(sprite2);

		World world = (World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world");
		physicsCollisionTestListener = new PhysicsCollisionTestListener(this, physicsWorld);
		world.setContactListener(physicsCollisionTestListener);
	}

	@Override
	protected void after() {
		sprite2 = null;
		physicsCollisionTestListener = null;
		super.after();
	}

	public void initializeSpritesForCollision() {
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

	public boolean collisionDetected() {
		return (beginContactCounter > 0);
	}

	public boolean isContactRateOk() {
		Log.d(TAG, "getContactDifference(): " + getContactDifference() + " == 0");
		return (getContactDifference()) == 0;
	}

	public int getContactDifference() {
		return beginContactCounter - endContactCounter;
	}

	public boolean simulateFullCollision() {
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
