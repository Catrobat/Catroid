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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.actions.SetGravityAction;
import org.catrobat.catroid.test.utils.Reflection;

public class SetGravityActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		float gravityX = 10.0f;
		float gravityY = 10.0f;
		Formula gravityXFormula = new Formula(gravityX);
		Formula gravityYFormula = new Formula(gravityY);
		SetGravityAction setGravityAction = new SetGravityAction();
		setGravityAction.setSprite(sprite);
		setGravityAction.setPhysicsWorld(physicsWorld);
		setGravityAction.setGravity(gravityXFormula, gravityYFormula);

		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		setGravityAction.act(1.0f);

		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();
		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);

	}

	public void testNegativeValue() {
		float gravityX = 10.0f;
		float gravityY = -10.0f;
		Formula gravityXFormula = new Formula(gravityX);
		Formula gravityYFormula = new Formula(gravityY);
		SetGravityAction setGravityAction = new SetGravityAction();
		setGravityAction.setSprite(sprite);
		setGravityAction.setPhysicsWorld(physicsWorld);
		setGravityAction.setGravity(gravityXFormula, gravityYFormula);

		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		setGravityAction.act(1.0f);

		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();
		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);
	}

	public void testZeroValue() {
		float gravityX = 0.0f;
		float gravityY = 10.0f;
		Formula gravityXFormula = new Formula(gravityX);
		Formula gravityYFormula = new Formula(gravityY);
		SetGravityAction setGravityAction = new SetGravityAction();
		setGravityAction.setSprite(sprite);
		setGravityAction.setPhysicsWorld(physicsWorld);
		setGravityAction.setGravity(gravityXFormula, gravityYFormula);

		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		setGravityAction.act(1.0f);

		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();
		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);
	}

}
