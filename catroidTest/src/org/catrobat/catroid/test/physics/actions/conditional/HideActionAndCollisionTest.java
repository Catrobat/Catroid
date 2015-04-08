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
package org.catrobat.catroid.test.physics.actions.conditional;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.ActionFactory;
import org.catrobat.catroid.test.physics.PhysicsCollisionBaseTest;

public class HideActionAndCollisionTest extends PhysicsCollisionBaseTest {
	public HideActionAndCollisionTest() {
		spritePosition = new Vector2(0.0f, 100.0f);
		sprite2Position = new Vector2(0.0f, -200.0f);
		physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		physicsObject2Type = PhysicsObject.Type.FIXED;
	}

	public void testNoCollisionAfterHide() {
		sprite.look.setVisible(false);
		simulateFullCollision();
		assertFalse("PhysicObjects shouldn't collide because sprite2 is invisible", collisionDetected());
	}

	public void testCollisionAfterHide() {
		sprite.look.setVisible(false);
		sprite.look.setVisible(true);
		simulateFullCollision();
		assertTrue("PhysicObjects should collide because sprite2 is visible", collisionDetected());
	}

	public void testHide() {
		Action action = sprite.getActionFactory().createHideAction(sprite);
		action.act(1.0f);
		assertFalse("Sprite is still visible after HideBrick executed", sprite.look.visible);
	}

	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createHideAction(null);
		try {
			action.act(1.0f);
			fail("Execution of HideBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown successful", true);
		}
	}
}
