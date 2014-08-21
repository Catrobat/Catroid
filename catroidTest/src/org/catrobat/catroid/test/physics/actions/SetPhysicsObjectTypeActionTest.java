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

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class SetPhysicsObjectTypeActionTest extends PhysicsBaseTest {

	public void testPhysicsTypeNone() {
		PhysicsObject.Type type = PhysicsObject.Type.NONE;
		initPhysicsTypeValue(type);
		assertEquals("Unexpected physics object type", type, physicsWorld.getPhysicsObject(sprite).getType());
	}

	public void testPhysicsTypeDynamic() {
		PhysicsObject.Type type = PhysicsObject.Type.DYNAMIC;
		initPhysicsTypeValue(type);
		assertEquals("Unexpected physics object type", type, physicsWorld.getPhysicsObject(sprite).getType());
	}

	public void testPhysicsTypeFixed() {
		PhysicsObject.Type type = PhysicsObject.Type.FIXED;
		initPhysicsTypeValue(type);
		assertEquals("Unexpected physics object type", type, physicsWorld.getPhysicsObject(sprite).getType());
	}

	private void initPhysicsTypeValue(PhysicsObject.Type type) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Action action = sprite.getActionFactory().createSetPhysicsObjectTypeAction(sprite,type);

		assertEquals("Unexpected physics object type", PhysicsObject.Type.NONE, physicsObject.getType());

		action.act(1.0f);
	}

}
