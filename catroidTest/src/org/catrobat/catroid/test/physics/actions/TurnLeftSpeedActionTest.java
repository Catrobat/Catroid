/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class TurnLeftSpeedActionTest extends PhysicsBaseTest {

	private static final float SPEED = 45.55f;

	public void testNormalBehavior() {
		initLeftSpeedValue(SPEED);
		assertEquals("Unexpected rotation speed value", SPEED, physicsWorld.getPhysicsObject(sprite)
				.getRotationSpeed());
	}

	public void testNegativeValue() {
		float speed = -45.55f;
		initLeftSpeedValue(speed);
		assertEquals("Unexpected rotation speed value", speed, physicsWorld.getPhysicsObject(sprite)
				.getRotationSpeed());
	}

	public void testZeroValue() {
		float speed = 0f;
		initLeftSpeedValue(speed);
		assertEquals("Unexpected rotation speed value", speed, physicsWorld.getPhysicsObject(sprite)
				.getRotationSpeed());
	}

	private void initLeftSpeedValue(float speed) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Action action = sprite.getActionFactory().createTurnLeftSpeedAction(sprite, new Formula(speed));

		assertEquals("Unexpected rotation speed value", 0.0f, physicsObject.getRotationSpeed());

		action.act(1.0f);

	}

	public void testBrickWithStringFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createTurnLeftSpeedAction(sprite, new Formula(String.valueOf(SPEED))).act(1.0f);
		assertEquals("Unexpected rotation speed value", SPEED, physicsObject.getRotationSpeed());

		sprite.getActionFactory().createTurnLeftSpeedAction(sprite, new Formula(
				String.valueOf("not a numerical string"))).act(1.0f);
		assertEquals("Unexpected rotation speed value", SPEED, physicsObject.getRotationSpeed());
	}

	public void testNullFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createTurnLeftSpeedAction(sprite, null).act(1.0f);
		assertEquals("Unexpected rotation speed value", 0f, physicsObject.getRotationSpeed());
	}

	public void testNotANumberFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createTurnLeftSpeedAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Unexpected rotation speed value", 0f, physicsObject.getRotationSpeed());
	}

}
