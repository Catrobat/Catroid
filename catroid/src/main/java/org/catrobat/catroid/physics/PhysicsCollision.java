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
package org.catrobat.catroid.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.eventids.CollisionEventId;

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

	private Map<CollisionEventId, PhysicsCollisionBroadcast> physicsCollisionBroadcasts = new HashMap<>();

	private static CollisionEventId generateKey(Sprite sprite1, Sprite sprite2) {
		return new CollisionEventId(sprite1, sprite2);
	}

	private void registerContact(Sprite sprite1, Sprite sprite2) {
		CollisionEventId identifier = generateKey(sprite1, sprite2);
		if (!physicsCollisionBroadcasts.containsKey(identifier)) {
			PhysicsCollisionBroadcast physicsCollisionBroadcast = new PhysicsCollisionBroadcast(sprite1, sprite2);
			physicsCollisionBroadcasts.put(identifier, physicsCollisionBroadcast);
		}
		physicsCollisionBroadcasts.get(identifier).increaseContactCounter();
	}

	private void unregisterContact(Sprite sprite1, Sprite sprite2) {
		CollisionEventId identifier = generateKey(sprite1, sprite2);
		if (physicsCollisionBroadcasts.containsKey(identifier)) {
			PhysicsCollisionBroadcast physicsCollisionBroadcast = physicsCollisionBroadcasts.get(identifier);
			physicsCollisionBroadcast.decreaseContactCounter();

			if (physicsCollisionBroadcast.getContactCounter() == 0) {
				physicsCollisionBroadcast.sendBroadcast();
				physicsCollisionBroadcasts.remove(identifier);
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
