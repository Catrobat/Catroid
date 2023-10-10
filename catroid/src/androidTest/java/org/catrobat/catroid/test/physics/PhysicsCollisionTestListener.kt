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
package org.catrobat.catroid.test.physics

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Manifold
import org.catrobat.catroid.physics.PhysicsCollisionListener
import org.catrobat.catroid.physics.PhysicsWorld

class PhysicsCollisionTestListener(
    private val receiver: PhysicsCollisionTestReceiver?,
    physicsWorld: PhysicsWorld?
) : PhysicsCollisionListener(physicsWorld) {
    override fun beginContact(contact: Contact) {
        super.beginContact(contact)
        receiver?.beginContactCallback(contact)
    }

    override fun endContact(contact: Contact) {
        super.endContact(contact)
        receiver?.endContactCallback(contact)
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        super.preSolve(contact, oldManifold)
        receiver?.preSolveCallback(contact, oldManifold)
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        super.postSolve(contact, impulse)
        receiver?.postSolveCallback(contact, impulse)
    }
}
