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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SetMassActionTest {

	private static final float MASS = 10f;

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
	public void testNormalBehavior() {
		initMassValue(MASS);
		assertEquals(MASS, physicsWorld.getPhysicsObject(sprite).getMass());
	}

	@Test
	public void testNegativeValue() {
		float mass = -10f;
		initMassValue(mass);
		assertEquals(PhysicsObject.MIN_MASS, physicsWorld.getPhysicsObject(sprite).getMass());
	}

	@Test
	public void testZeroValue() {
		float mass = 0f;
		initMassValue(mass);
		assertEquals(0.0f, physicsWorld.getPhysicsObject(sprite).getMass());
	}

	private void initMassValue(float mass) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Action action = sprite.getActionFactory().createSetMassAction(sprite,
				new SequenceAction(), new Formula(mass));

		assertEquals(PhysicsObject.DEFAULT_MASS, physicsObject.getMass());

		action.act(1.0f);
		physicsWorld.step(1.0f);
	}

	@Test
	public void testBrickWithStringFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetMassAction(sprite,
				new SequenceAction(), new Formula(String.valueOf(MASS))).act(1.0f);
		assertEquals(MASS, physicsObject.getMass());

		sprite.getActionFactory().createSetMassAction(sprite, new SequenceAction(),
				new Formula("not a numerical string"))
				.act(1.0f);
		assertEquals(MASS, physicsObject.getMass());
	}

	@Test
	public void testNullFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetMassAction(sprite, new SequenceAction(), null).act(1.0f);
		assertEquals(0f, physicsObject.getMass());
	}

	@Test
	public void testNotANumberFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetMassAction(sprite, new SequenceAction(),
				new Formula(Double.NaN)).act(1.0f);
		assertEquals(PhysicsObject.DEFAULT_MASS, physicsObject.getMass());
	}

	@Test
	public void testMassAcceleration() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		physicsObject.setMass(5.0f);

		physicsWorld.step(0.10f);
		float lastVelocity = Math.abs(physicsObject.getVelocity().y);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		physicsWorld.step(0.25f);
		float currentVelocity = Math.abs(physicsObject.getVelocity().y);

		assertThat((currentVelocity - lastVelocity), is(greaterThan(1.0f)));
	}
}
