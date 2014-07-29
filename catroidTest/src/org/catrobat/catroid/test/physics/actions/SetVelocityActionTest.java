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

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetVelocityAction;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class SetVelocityActionTest extends PhysicsBaseTest {

	public void testNormalBehavior() {
		float velocityX = 10.0f;
		float velocityY = 10.0f;
		Formula velocityXFormula = new Formula(velocityX);
		Formula velocityYFormula = new Formula(velocityY);
		SetVelocityAction setVelocityAction = new SetVelocityAction();
		setVelocityAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setVelocityAction.setPhysicsObject(physicsObject);
		setVelocityAction.setVelocity(velocityXFormula, velocityYFormula);

		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0.0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0.0f, velocityVector.y);

		setVelocityAction.act(1.0f);

		velocityVector = physicsObject.getVelocity();
		assertEquals("Unexpected velocityX value", velocityX, velocityVector.x);
		assertEquals("Unexpected velocityY value", velocityY, velocityVector.y);
	}

	public void testNegativeValue() {
		float velocityX = 10.0f;
		float velocityY = -10.0f;
		Formula velocityXFormula = new Formula(velocityX);
		Formula velocityYFormula = new Formula(velocityY);
		SetVelocityAction setVelocityAction = new SetVelocityAction();
		setVelocityAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setVelocityAction.setPhysicsObject(physicsObject);
		setVelocityAction.setVelocity(velocityXFormula, velocityYFormula);

		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0.0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0.0f, velocityVector.y);

		setVelocityAction.act(1.0f);

		velocityVector = physicsObject.getVelocity();
		assertEquals("Unexpected velocityX value", velocityX, velocityVector.x);
		assertEquals("Unexpected velocityY value", velocityY, velocityVector.y);
	}

	public void testZeroValue() {
		float velocityX = 0.0f;
		float velocityY = 10.0f;
		Formula velocityXFormula = new Formula(velocityX);
		Formula velocityYFormula = new Formula(velocityY);
		SetVelocityAction setVelocityAction = new SetVelocityAction();
		setVelocityAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setVelocityAction.setPhysicsObject(physicsObject);
		setVelocityAction.setVelocity(velocityXFormula, velocityYFormula);

		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0.0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0.0f, velocityVector.y);

		setVelocityAction.act(1.0f);

		velocityVector = physicsObject.getVelocity();
		assertEquals("Unexpected velocityX value", velocityX, velocityVector.x);
		assertEquals("Unexpected velocityY value", velocityY, velocityVector.y);
	}

}
