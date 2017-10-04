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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class SetMassActionTest extends PhysicsBaseTest {

	private static final float MASS = 10f;

	public void testNormalBehavior() {
		initMassValue(MASS);
		assertEquals("Unexpected mass value", MASS, sprite.getPhysicsProperties().getMass());
	}

	public void testNegativeValue() {
		float mass = -10f;
		initMassValue(mass);
		assertEquals("Unexpected mass value", PhysicsProperties.MIN_MASS, sprite.getPhysicsProperties().getMass());
	}

	public void testZeroValue() {
		float mass = 0f;
		initMassValue(mass);
		assertEquals("Unexpected mass value", 0.0f, sprite.getPhysicsProperties().getMass());
	}

	private void initMassValue(float mass) {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		Action action = sprite.getActionFactory().createSetMassAction(sprite, new Formula(mass));

		assertEquals("Unexpected mass value", PhysicsProperties.DEFAULT_MASS, physicsProperties.getMass());

		action.act(1.0f);
		physicsWorld.step(1.0f);
	}

	public void testBrickWithStringFormula() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		sprite.getActionFactory().createSetMassAction(sprite, new Formula(String.valueOf(MASS))).act(1.0f);
		assertEquals("Unexpected mass value", MASS, physicsProperties.getMass());

		sprite.getActionFactory().createSetMassAction(sprite, new Formula(String.valueOf("not a numerical string")))
				.act(1.0f);
		assertEquals("Unexpected mass value", MASS, physicsProperties.getMass());
	}

	public void testNullFormula() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		sprite.getActionFactory().createSetMassAction(sprite, null).act(1.0f);
		assertEquals("Unexpected mass value", 0f, physicsProperties.getMass());
	}

	public void testNotANumberFormula() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		sprite.getActionFactory().createSetMassAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Unexpected mass value", PhysicsProperties.DEFAULT_MASS, physicsProperties.getMass());
	}

	public void testMassAcceleration() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		physicsProperties.setType(PhysicsProperties.Type.DYNAMIC);
		physicsProperties.setMass(5.0f);

		physicsWorld.step(0.10f);
		float lastVelocity = Math.abs(physicsProperties.getVelocity().y);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		float currentVelocity = Math.abs(physicsProperties.getVelocity().y);

		assertTrue("Object does not accelerate", (currentVelocity - lastVelocity) > 1.0f);
	}
}
