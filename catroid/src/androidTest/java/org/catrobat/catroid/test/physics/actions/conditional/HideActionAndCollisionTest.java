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
package org.catrobat.catroid.test.physics.actions.conditional;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.PhysicsCollisionTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class HideActionAndCollisionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Rule
	public PhysicsCollisionTestRule rule = new PhysicsCollisionTestRule();

	private Sprite sprite;

	@Before
	public void setUp() {
		sprite = rule.sprite;

		rule.spritePosition = new Vector2(0.0f, 100.0f);
		rule.sprite2Position = new Vector2(0.0f, -200.0f);
		rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		rule.physicsObject2Type = PhysicsObject.Type.FIXED;

		rule.initializeSpritesForCollision();
	}

	@Test
	public void testNoCollisionAfterHide() {
		Action action = sprite.getActionFactory().createHideAction(sprite);
		action.act(1.0f);
		rule.simulateFullCollision();
		assertFalse(rule.collisionDetected());
	}

	@Test
	public void testCollisionAfterHide() {
		Action action = sprite.getActionFactory().createHideAction(sprite);
		action.act(1.0f);
		action = sprite.getActionFactory().createShowAction(sprite);
		action.act(1.0f);
		rule.simulateFullCollision();
		assertTrue(rule.collisionDetected());
	}

	@Test
	public void testHide() {
		Action action = sprite.getActionFactory().createHideAction(sprite);
		action.act(1.0f);
		assertFalse(sprite.look.isLookVisible());
	}

	@Test
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createHideAction(null);
		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}
}
