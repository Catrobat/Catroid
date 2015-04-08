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

package org.catrobat.catroid.test.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.physics.PhysicsWorld;

public class PhysicsCollisionTestListener extends PhysicsCollision {

	private PhysicsCollisionTestReceiver receiver;

	public PhysicsCollisionTestListener(PhysicsCollisionTestReceiver receiver, PhysicsWorld physicsWorld) {
		super(physicsWorld);
		this.receiver = receiver;
	}

	@Override
	public void beginContact(Contact contact) {
		super.beginContact(contact);
		if (receiver != null) {
			receiver.beginContactCallback(contact);
		}
	}

	@Override
	public void endContact(Contact contact) {
		super.endContact(contact);
		if (receiver != null) {
			receiver.endContactCallback(contact);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		super.preSolve(contact, oldManifold);
		if (receiver != null) {
			receiver.preSolveCallback(contact, oldManifold);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		super.postSolve(contact, impulse);
		if (receiver != null) {
			receiver.postSolveCallback(contact, impulse);
		}
	}
}
