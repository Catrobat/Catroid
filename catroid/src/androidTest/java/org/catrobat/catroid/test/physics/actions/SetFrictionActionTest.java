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

public class SetFrictionActionTest extends PhysicsBaseTest {

	private static final float FRICTION = 100f;

	public void testNormalBehavior() {
		initFrictionValue(FRICTION);
		assertEquals("Unexpected friction value", FRICTION / 100.0f, sprite.getPhysicsProperties()
				.getFriction());
	}

	public void testNegativeValue() {
		float friction = -1f;
		initFrictionValue(friction);
		assertEquals("Unexpected friction value", PhysicsProperties.MIN_FRICTION, sprite.getPhysicsProperties()
				.getFriction());
	}

	public void testHighValue() {
		float friction = 101f;
		initFrictionValue(friction);
		assertEquals("Unexpected friction value", PhysicsProperties.MAX_FRICTION, sprite.getPhysicsProperties()
				.getFriction());
	}

	private void initFrictionValue(float frictionFactor) {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		Action action = sprite.getActionFactory().createSetFrictionAction(sprite, new Formula(frictionFactor));

		assertEquals("Unexpected friction value", PhysicsProperties.DEFAULT_FRICTION, physicsProperties.getFriction());

		action.act(1.0f);
		physicsWorld.step(1.0f);
	}

	public void testBrickWithStringFormula() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		sprite.getActionFactory().createSetFrictionAction(sprite, new Formula(String.valueOf(FRICTION))).act(1.0f);
		assertEquals("Unexpected friction value", FRICTION / 100.f,
				physicsProperties.getFriction());

		sprite.getActionFactory().createSetFrictionAction(sprite, new Formula(String.valueOf("not a numerical string")))
				.act(1.0f);
		assertEquals("Unexpected friction value", FRICTION / 100.f,
				physicsProperties.getFriction());
	}

	public void testNullFormula() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		sprite.getActionFactory().createSetFrictionAction(sprite, null).act(1.0f);
		assertEquals("Unexpected friction value", 0f, physicsProperties.getFriction());
	}

	public void testNotANumberFormula() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		sprite.getActionFactory().createSetFrictionAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Unexpected friction value", PhysicsProperties.DEFAULT_FRICTION, physicsProperties.getFriction());
	}
}
