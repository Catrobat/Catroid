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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
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
public class SetFrictionActionTest {

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
	}

	private static final float FRICTION = 100f;

	@Test
	public void testNormalBehavior() {
		initFrictionValue(FRICTION);
		assertEquals(FRICTION / 100.0f, physicsWorld.getPhysicsObject(sprite).getFriction());
	}

	@Test
	public void testNegativeValue() {
		float friction = -1f;
		initFrictionValue(friction);
		assertEquals(PhysicsObject.MIN_FRICTION, physicsWorld.getPhysicsObject(sprite).getFriction());
	}

	@Test
	public void testHighValue() {
		float friction = 101f;
		initFrictionValue(friction);
		assertEquals(PhysicsObject.MAX_FRICTION, physicsWorld.getPhysicsObject(sprite).getFriction());
	}

	private void initFrictionValue(float frictionFactor) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Action action = sprite.getActionFactory().createSetFrictionAction(sprite,
				new SequenceAction(), new Formula(frictionFactor));

		assertEquals(PhysicsObject.DEFAULT_FRICTION, physicsObject.getFriction());

		action.act(1.0f);
		physicsWorld.step(1.0f);
	}

	@Test
	public void testBrickWithStringFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetFrictionAction(sprite,
			new SequenceAction(), new Formula(String.valueOf(FRICTION))).act(1.0f);
		assertEquals(FRICTION / 100.f, physicsObject.getFriction());

		sprite.getActionFactory().createSetFrictionAction(sprite, new SequenceAction(),
				new Formula("not a numerical string"))
				.act(1.0f);
		assertEquals(FRICTION / 100.f, physicsObject.getFriction());
	}

	@Test
	public void testNullFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetFrictionAction(sprite, new SequenceAction(), null).act(1.0f);
		assertEquals(0f, physicsObject.getFriction());
	}

	@Test
	public void testNotANumberFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetFrictionAction(sprite, new SequenceAction(),
				new Formula(Double.NaN)).act(1.0f);
		assertEquals(PhysicsObject.DEFAULT_FRICTION, physicsObject.getFriction());
	}
}
