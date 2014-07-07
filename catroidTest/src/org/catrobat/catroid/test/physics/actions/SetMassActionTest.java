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
package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.content.actions.SetMassAction;

public class SetMassActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		Formula mass = new Formula(10);
		SetMassAction setMassAction = new SetMassAction();
		setMassAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setMassAction.setPhysicsObject(physicsObject);
		setMassAction.setMass(mass);

		setMassAction.act(1.0f);
		physicsWorld.step(1.0f);

		assertEquals("Unexpected mass value", 10f, physicsObject.getMass());
	}

	public void testNegativeValue() {
		Formula mass = new Formula(-10);
		SetMassAction setMassAction = new SetMassAction();
		setMassAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setMassAction.setPhysicsObject(physicsObject);
		setMassAction.setMass(mass);

		setMassAction.act(1.0f);
		physicsWorld.step(1.0f);

		assertEquals("Unexpected mass value", PhysicsObject.MIN_MASS, physicsObject.getMass());
	}

	public void testZeroValue() {
		Formula mass = new Formula(0);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		SetMassAction setMassAction = new SetMassAction();
		setMassAction.setSprite(sprite);
		setMassAction.setPhysicsObject(physicsObject);
		setMassAction.setMass(mass);

		setMassAction.act(1.0f);
		physicsWorld.step(1.0f);

		assertEquals("Unexpected mass value", 0.0f, physicsObject.getMass());
	}

	public void testMassAcceleration() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(Type.DYNAMIC);
		physicsObject.setMass(5.0f);

		physicsWorld.step(0.10f);
		float lastVelocity = Math.abs(physicsObject.getVelocity().y);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		float currentVelocity = Math.abs(physicsObject.getVelocity().y);

		assertTrue("Object does not accelerate", (currentVelocity - lastVelocity) > 1.0f);
	}
}
