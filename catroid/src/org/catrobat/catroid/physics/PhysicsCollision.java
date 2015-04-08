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

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.catrobat.catroid.content.Sprite;

import java.util.HashMap;
import java.util.Map;

public class PhysicsCollision implements ContactListener {
	//private static final String TAG = PhysicsCollision.class.getSimpleName();
	private PhysicsWorld physicsWorld;

	public PhysicsCollision(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}

	Map<String, PhysicsCollisionBroadcast> physicsCollisionBroadcasts = new HashMap<String, PhysicsCollisionBroadcast>();

	private static String generateKey(Sprite sprite1, Sprite sprite2) {
		String key = sprite1.getName().concat(sprite2.getName());
		return key;
	}

	private void registerContact(Sprite sprite1, Sprite sprite2) {
		String key1 = generateKey(sprite1, sprite2);
		String key2 = generateKey(sprite2, sprite1);
		if (!physicsCollisionBroadcasts.containsKey(key1)) {
			PhysicsCollisionBroadcast physicsCollisionBroadcast = new PhysicsCollisionBroadcast(sprite1.getName(), sprite2.getName());
			physicsCollisionBroadcasts.put(key1, physicsCollisionBroadcast);
			physicsCollisionBroadcasts.put(key2, physicsCollisionBroadcast);
		}
		physicsCollisionBroadcasts.get(key1).increaseContactCounter();
		//Log.d(TAG, "registerContact:" + physicsCollisionBroadcasts.toString());
	}

	private void unregisterContact(Sprite sprite1, Sprite sprite2) {
		String key1 = generateKey(sprite1, sprite2);
		String key2 = generateKey(sprite2, sprite1);
		if (physicsCollisionBroadcasts.containsKey(key1)) {
			PhysicsCollisionBroadcast physicsCollisionBroadcast = physicsCollisionBroadcasts.get(key1);
			physicsCollisionBroadcast.decreaseContactCounter();

			if (physicsCollisionBroadcast.getContactCounter() == 0) {
				physicsCollisionBroadcast.sendBroadcast();
				//Log.d(TAG, "remove contact:" + physicsCollisionBroadcasts.toString());
				physicsCollisionBroadcasts.remove(key1);
				physicsCollisionBroadcasts.remove(key2);
			}
		}
	}

	@Override
	public void beginContact(Contact contact) {
		//Log.d(TAG, "beginContact");

		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();


		if (a.getUserData() instanceof Sprite && b.getUserData() instanceof PhysicsBoundaryBox.BoundaryBoxIdentifier) {
			physicsWorld.bouncedOnEdge((Sprite) a.getUserData(), (PhysicsBoundaryBox.BoundaryBoxIdentifier) b.getUserData());
			//Log.d(TAG, "bouncedOnEdge SPRITE A");
		} else if (a.getUserData() instanceof PhysicsBoundaryBox.BoundaryBoxIdentifier && (b.getUserData() instanceof Sprite)) {
			physicsWorld.bouncedOnEdge((Sprite) b.getUserData(), (PhysicsBoundaryBox.BoundaryBoxIdentifier) a.getUserData());
			//Log.d(TAG, "bouncedOnEdge SPRITE B");
		} else if (a.getUserData() instanceof Sprite && b.getUserData() instanceof Sprite) {
			Sprite sprite1 = (Sprite) a.getUserData();
			Sprite sprite2 = (Sprite) b.getUserData();
			registerContact(sprite1, sprite2);
		}
	}

	@Override
	public void endContact(Contact contact) {
		//Log.d(TAG, "endContact");

		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();

		if (a.getUserData() instanceof Sprite && b.getUserData() instanceof Sprite) {
			Sprite sprite1 = (Sprite) a.getUserData();
			Sprite sprite2 = (Sprite) b.getUserData();
			unregisterContact(sprite1, sprite2);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		//Log.d(TAG, "preSolve");
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		//Log.d(TAG, "postSolve");
	}
}
