/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

	public static final String COLLISION_MESSAGE_ESCAPE_CHAR = "\t";
	public static final String COLLISION_MESSAGE_CONNECTOR = "<" + COLLISION_MESSAGE_ESCAPE_CHAR
			+ "-" + COLLISION_MESSAGE_ESCAPE_CHAR + ">";
	public static final String COLLISION_WITH_ANYTHING_IDENTIFIER = COLLISION_MESSAGE_ESCAPE_CHAR
			+ "ANYTHING" + COLLISION_MESSAGE_ESCAPE_CHAR;

	private PhysicsWorld physicsWorld;

	public PhysicsCollision(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}

	private Map<String, PhysicsCollisionBroadcast> physicsCollisionBroadcasts = new HashMap<>();

	public static String generateBroadcastMessage(String collisionObjectOneIdentifier, String
			collisionObjectTwoIdentifier) {
		return collisionObjectOneIdentifier + COLLISION_MESSAGE_CONNECTOR + collisionObjectTwoIdentifier;
	}

	public static boolean isCollisionBroadcastMessage(String message) {
		if (message == null) {
			return false;
		}
		return message.contains(PhysicsCollision.COLLISION_MESSAGE_CONNECTOR);
	}

	private static String generateKey(Sprite sprite1, Sprite sprite2) {
		return sprite1.getName() + COLLISION_MESSAGE_CONNECTOR + sprite2.getName();
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
	}

	private void unregisterContact(Sprite sprite1, Sprite sprite2) {
		String key1 = generateKey(sprite1, sprite2);
		String key2 = generateKey(sprite2, sprite1);
		if (physicsCollisionBroadcasts.containsKey(key1)) {
			PhysicsCollisionBroadcast physicsCollisionBroadcast = physicsCollisionBroadcasts.get(key1);
			physicsCollisionBroadcast.decreaseContactCounter();

			if (physicsCollisionBroadcast.getContactCounter() == 0) {
				physicsCollisionBroadcast.sendBroadcast();
				physicsCollisionBroadcasts.remove(key1);
				physicsCollisionBroadcasts.remove(key2);
			}
		}
	}

	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();

		if (a.getUserData() instanceof Sprite && b.getUserData() instanceof PhysicsBoundaryBox.BoundaryBoxIdentifier) {
			physicsWorld.bouncedOnEdge((Sprite) a.getUserData(), (PhysicsBoundaryBox.BoundaryBoxIdentifier) b.getUserData());
		} else if (a.getUserData() instanceof PhysicsBoundaryBox.BoundaryBoxIdentifier && (b.getUserData() instanceof Sprite)) {
			physicsWorld.bouncedOnEdge((Sprite) b.getUserData(), (PhysicsBoundaryBox.BoundaryBoxIdentifier) a.getUserData());
		} else if (a.getUserData() instanceof Sprite && b.getUserData() instanceof Sprite) {
			Sprite sprite1 = (Sprite) a.getUserData();
			Sprite sprite2 = (Sprite) b.getUserData();
			registerContact(sprite1, sprite2);
		}
	}

	@Override
	public void endContact(Contact contact) {
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
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
