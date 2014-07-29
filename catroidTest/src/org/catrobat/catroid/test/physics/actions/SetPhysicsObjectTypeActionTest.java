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

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetPhysicsObjectTypeAction;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;

public class SetPhysicsObjectTypeActionTest extends PhysicsBaseTest {

	public void testPhysicsTypeNone() {
		PhysicsObject.Type type = PhysicsObject.Type.NONE;
		SetPhysicsObjectTypeAction setPhysicsObjectTypeAction = new SetPhysicsObjectTypeAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsObjectTypeAction.setPhysicsObject(physicsObject);
		setPhysicsObjectTypeAction.setType(type);

		assertEquals("Unexpected physics object type", PhysicsObject.Type.NONE, physicsObject.getType());

		setPhysicsObjectTypeAction.act(1.0f);

		assertEquals("Unexpected physics object type", type, physicsObject.getType());
	}

	public void testPhysicsTypeDynamic() {
		PhysicsObject.Type type = PhysicsObject.Type.DYNAMIC;
		SetPhysicsObjectTypeAction setPhysicsObjectTypeAction = new SetPhysicsObjectTypeAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsObjectTypeAction.setPhysicsObject(physicsObject);
		setPhysicsObjectTypeAction.setType(type);

		assertEquals("Unexpected physics object type", PhysicsObject.Type.NONE, physicsObject.getType());

		setPhysicsObjectTypeAction.act(1.0f);

		assertEquals("Unexpected physics object type", type, physicsObject.getType());
	}

	public void testPhysicsTypeFixed() {
		PhysicsObject.Type type = PhysicsObject.Type.FIXED;
		SetPhysicsObjectTypeAction setPhysicsObjectTypeAction = new SetPhysicsObjectTypeAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsObjectTypeAction.setPhysicsObject(physicsObject);
		setPhysicsObjectTypeAction.setType(type);

		assertEquals("Unexpected physics object type", PhysicsObject.Type.NONE, physicsObject.getType());

		setPhysicsObjectTypeAction.act(1.0f);

		assertEquals("Unexpected physics object type", type, physicsObject.getType());
	}

}
