/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SetPhysicsObjectTypeActionTest {

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
	}

	@Test
	public void testPhysicsTypeNone() {
		PhysicsObject.Type type = PhysicsObject.Type.NONE;
		initPhysicsTypeValue(type);
		assertEquals(type, physicsWorld.getPhysicsObject(sprite).getType());
	}

	@Test
	public void testPhysicsTypeDynamic() {
		PhysicsObject.Type type = PhysicsObject.Type.DYNAMIC;
		initPhysicsTypeValue(type);
		assertEquals(type, physicsWorld.getPhysicsObject(sprite).getType());
	}

	@Test
	public void testPhysicsTypeFixed() {
		PhysicsObject.Type type = PhysicsObject.Type.FIXED;
		initPhysicsTypeValue(type);
		assertEquals(type, physicsWorld.getPhysicsObject(sprite).getType());
	}

	private void initPhysicsTypeValue(PhysicsObject.Type type) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Action action = sprite.getActionFactory().createSetPhysicsObjectTypeAction(sprite, type);

		assertEquals(PhysicsObject.Type.NONE, physicsObject.getType());

		action.act(1.0f);
	}
}
