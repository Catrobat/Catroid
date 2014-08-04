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
package org.catrobat.catroid.test.physics.actions.conditional;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.ActionFactory;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.test.physics.actions.PhysicsActionTestCase;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;

public class HideActionAndCollisionTest extends PhysicsActionTestCase {

	private boolean bounced;
	private Sprite sprite2;

	protected void setUp() throws Exception {
		super.setUp();
		bounced = false;
		assertTrue("Unexpected default visibility", sprite.look.isVisible());
		sprite2 = new Sprite("TestSprite2");
		sprite2.look = new PhysicsLook(sprite2, physicsWorld);
		sprite2.setActionFactory(new ActionPhysicsFactory());
		LookData lookdata = PhysicsTestUtils.generateLookData(rectangle125x125File);
		sprite2.look.setLookData(lookdata);
		assertTrue("Unexpected default visibility", sprite2.look.isVisible());
	}

	@Override
	protected void contactBegin() {
		bounced = true;
	}

	public void testNoCollisionAfterHide() {
		PhysicsObject physicsObject2 = physicsWorld.getPhysicsObject(sprite2);
		physicsObject2.setType(PhysicsObject.Type.DYNAMIC);
		physicsObject2.setGravityScale(0.0f);
		sprite2.look.setVisible(false);

		PhysicsObject physicsObject1 = physicsWorld.getPhysicsObject(sprite);
		physicsObject1.setType(PhysicsObject.Type.DYNAMIC);
		sprite.look.setPosition(0f, lookHeigth);
		setContactListener();
		simulate(5);
		assertFalse("PhysicObjects shouldn't collide because sprite2 is invisible", bounced);
	}

	public void testCollisionAfterHide() {
		PhysicsObject physicsObject2 = physicsWorld.getPhysicsObject(sprite2);
		physicsObject2.setType(PhysicsObject.Type.DYNAMIC);
		physicsObject2.setGravityScale(0.0f);
		sprite2.look.setVisible(false);
		sprite2.look.setVisible(true);

		PhysicsObject physicsObject1 = physicsWorld.getPhysicsObject(sprite);
		physicsObject1.setType(PhysicsObject.Type.DYNAMIC);
		sprite.look.setPosition(0f, lookHeigth);
		setContactListener();
		simulate(5);
		assertTrue("PhysicObjects should collide because sprite2 is visible", bounced);
	}

	public void testHide() {
		Action action = sprite.getActionFactory().createHideAction(sprite);
		action.act(1.0f);
		assertFalse("Sprite is still visible after HideBrick executed", sprite.look.isVisible());
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
