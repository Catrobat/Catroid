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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.Reflection;

public class SetGravityActionTest extends PhysicsBaseTest {

	private static final float GRAVITY_X = 10.0f;
	private static final float GRAVITY_Y= 10.0f;

	public void testNormalBehavior() {
		float gravityX = GRAVITY_X;
		float gravityY = GRAVITY_Y;

		initGravityValues(gravityX, gravityY);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);
	}

	public void testNegativeValue() {
		float gravityX = 10.0f;
		float gravityY = -10.0f;

		initGravityValues(gravityX, gravityY);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);
	}

	public void testZeroValue() {
		float gravityX = 0.0f;
		float gravityY = 10.0f;

		initGravityValues(gravityX, gravityY);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);
	}

	private void initGravityValues(float gravityX, float gravityY) {
		Action action = sprite.getActionFactory().createSetGravityAction(sprite, new Formula(gravityX),
				new Formula(gravityY));
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		action.act(1.0f);
	}

	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createSetGravityAction(sprite, new Formula(String.valueOf(GRAVITY_X)),
				new Formula(String.valueOf(GRAVITY_Y))).act(1.0f);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", GRAVITY_X, gravityVector.x);
		assertEquals("Unexpected gravityY value", GRAVITY_Y, gravityVector.y);

		sprite.getActionFactory().createSetGravityAction(sprite, new Formula(String.valueOf("not a numerical string")),
				new Formula(String.valueOf("not a numerical string"))).act(1.0f);
		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();

		assertEquals("Unexpected gravityX value", GRAVITY_X, gravityVector.x);
		assertEquals("Unexpected gravityY value", GRAVITY_Y, gravityVector.y);
	}

	public void testNullFormula() {
		sprite.getActionFactory().createSetGravityAction(sprite, null, null).act(1.0f);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", 0f, gravityVector.x);
		assertEquals("Unexpected gravityY value", 0f, gravityVector.y);
	}

	public void testNotANumberFormula() {
		sprite.getActionFactory().createSetGravityAction(sprite, new Formula(Double.NaN), new Formula(Double.NaN))
				.act(1.0f);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);
	}

}
