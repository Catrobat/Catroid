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

import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetFrictionAction;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;

public class SetFrictionActionTest extends PhysicsBaseTest {

	public void testDefaultValue() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsObject);
		assertEquals("Unexpected default friction value", PhysicsObject.DEFAULT_FRICTION, fixtureDef.friction);
	}

	public void testNormalBehavior() {

		for (int i = 0; i <= 100; i += 10) {
			initFrictionFactor(i);
			assertEquals("Unexpected friction value", i / 100.0f, physicsWorld.getPhysicsObject(sprite).getFriction());
		}
	}

	public void testNegativeValues() {
		for (int i = -1; i >= -101; i -= 10) {
			initFrictionFactor(i);
			assertEquals("Unexpected friction value", PhysicsObject.MIN_FRICTION, physicsWorld.getPhysicsObject(sprite)
					.getFriction());
		}
	}

	public void testTooLargeValues() {
		for (int i = 101; i <= 201; i += 10) {
			initFrictionFactor(i);
			assertEquals("Unexpected friction value", PhysicsObject.MAX_FRICTION, physicsWorld.getPhysicsObject(sprite)
					.getFriction());
		}
	}

	private void initFrictionFactor(float frictionFactor) {
		Formula friction = new Formula(frictionFactor);
		SetFrictionAction setFrictionAction = new SetFrictionAction();
		setFrictionAction.setSprite(sprite);
		setFrictionAction.setPhysicsObject(physicsWorld.getPhysicsObject(sprite));
		setFrictionAction.setFriction(friction);

		setFrictionAction.act(1.0f);
		physicsWorld.step(1.0f);
	}
}