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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class SetVelocityActionTest extends PhysicsBaseTest {

	private static final float VELOCITY_X = 10.0f;
	private static final float VELOCITY_Y = 11.0f;

	public void testNormalBehavior() {
		initVelocityValue(VELOCITY_X, VELOCITY_Y);
		assertEquals("Unexpected velocityX value", VELOCITY_X, physicsWorld.getPhysicsObject(sprite).getVelocity().x);
		assertEquals("Unexpected velocityY value", VELOCITY_Y, physicsWorld.getPhysicsObject(sprite).getVelocity().y);
	}

	public void testNegativeValue() {
		float velocityX = 10.0f;
		float velocityY = -10.0f;
		initVelocityValue(velocityX, velocityY);
		assertEquals("Unexpected velocityX value", velocityX, physicsWorld.getPhysicsObject(sprite).getVelocity().x);
		assertEquals("Unexpected velocityY value", velocityY, physicsWorld.getPhysicsObject(sprite).getVelocity().y);
	}

	public void testZeroValue() {
		float velocityX = 0.0f;
		float velocityY = 10.0f;
		initVelocityValue(velocityX, velocityY);
		assertEquals("Unexpected velocityX value", velocityX, physicsWorld.getPhysicsObject(sprite).getVelocity().x);
		assertEquals("Unexpected velocityY value", velocityY, physicsWorld.getPhysicsObject(sprite).getVelocity().y);
	}

	private void initVelocityValue(float velocityX, float velocityY) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Action action = sprite.getActionFactory().createSetVelocityAction(sprite, new Formula(velocityX),
				new Formula(velocityY));
		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0.0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0.0f, velocityVector.y);

		action.act(1.0f);
	}

	public void testBrickWithStringFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetVelocityAction(sprite, new Formula(String.valueOf(VELOCITY_X)),
				new Formula(String.valueOf(VELOCITY_Y))).act(1.0f);
		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", VELOCITY_X, velocityVector.x);
		assertEquals("Unexpected velocityY value", VELOCITY_Y, velocityVector.y);

		sprite.getActionFactory().createSetVelocityAction(sprite, new Formula(String.valueOf("not a numerical string")),
				new Formula(String.valueOf("not a numerical string"))).act(1.0f);
		velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", VELOCITY_X, velocityVector.x);
		assertEquals("Unexpected velocityY value", VELOCITY_Y, velocityVector.y);
	}

	public void testNullFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetVelocityAction(sprite, null, null).act(1.0f);
		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0f, velocityVector.y);
	}

	public void testNotANumberFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetVelocityAction(sprite, new Formula(Double.NaN), new Formula(Double.NaN))
				.act(1.0f);
		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0f, velocityVector.y);
	}
}
