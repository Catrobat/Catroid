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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsCollisionTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SetBounceFactorActionTest {

	private static final float BOUNCE_FACTOR = 50f;

	@Rule
	public PhysicsCollisionTestRule rule = new PhysicsCollisionTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
		rule.spritePosition = new Vector2(0.0f, 100.0f);
		rule.sprite2Position = new Vector2(0.0f, -200.0f);
		rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		rule.physicsObject2Type = PhysicsObject.Type.FIXED;

		rule.initializeSpritesForCollision();
	}

	@Test
	public void testNormalBounceFactor() {
		initBounceFactorValue(BOUNCE_FACTOR);
		assertEquals(BOUNCE_FACTOR / 100.0f, physicsWorld.getPhysicsObject(sprite).getBounceFactor());
	}

	@Test
	public void testZeroValue() {
		float bounceFactor = 0.0f;
		initBounceFactorValue(bounceFactor);
		assertEquals(bounceFactor / 100.0f, physicsWorld.getPhysicsObject(sprite).getBounceFactor());
	}

	@Test
	public void testNegativeValue() {
		float bounceFactor = -50.0f;
		initBounceFactorValue(bounceFactor);
		assertEquals(PhysicsObject.MIN_BOUNCE_FACTOR, physicsWorld.getPhysicsObject(sprite).getBounceFactor());
	}

	@Test
	public void testHighValue() {
		float bounceFactor = 1000.0f;
		initBounceFactorValue(bounceFactor);
		assertEquals(bounceFactor / 100.0f, physicsWorld.getPhysicsObject(sprite).getBounceFactor());
	}

	private void initBounceFactorValue(float bounceFactor) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Action action = sprite.getActionFactory().createSetBounceFactorAction(sprite,
				new SequenceAction(), new Formula(bounceFactor));

		assertEquals(PhysicsObject.DEFAULT_BOUNCE_FACTOR, physicsObject.getBounceFactor());

		action.act(1.0f);
		physicsWorld.step(1.0f);
	}

	@Test
	public void testBrickWithStringFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetBounceFactorAction(sprite,
				new SequenceAction(), new Formula(String.valueOf(BOUNCE_FACTOR)))
				.act(1.0f);
		assertEquals(BOUNCE_FACTOR / 100.f, physicsObject.getBounceFactor());

		sprite.getActionFactory().createSetBounceFactorAction(sprite, new SequenceAction(),
				new Formula(String
				.valueOf("not a numerical string"))).act(1.0f);
		assertEquals(BOUNCE_FACTOR / 100.f, physicsObject.getBounceFactor());
	}

	@Test
	public void testNullFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetBounceFactorAction(sprite, new SequenceAction(), null).act(1.0f);
		assertEquals(0f, physicsObject.getBounceFactor());
	}

	@Test
	public void testNotANumberFormula() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		sprite.getActionFactory().createSetBounceFactorAction(sprite, new SequenceAction(),
				new Formula(Double.NaN)).act(1.0f);
		assertEquals(PhysicsObject.DEFAULT_BOUNCE_FACTOR, physicsObject.getBounceFactor());
	}

	@Test
	public void testBounceWithDifferentValues() {
		float bounce01Height = bounce(0.1f);
		float bounce06Height = bounce(0.6f);
		assertThat(bounce01Height, is(lessThan(bounce06Height)));
	}

	private float bounce(float bounceFactor) {
		rule.initializeSpritesForCollision();
		physicsWorld.getPhysicsObject(sprite).setVelocity(0, 0);
		physicsWorld.getPhysicsObject(sprite).setMass(20);
		physicsWorld.getPhysicsObject(sprite).setBounceFactor(bounceFactor);
		while (!rule.collisionDetected()) {
			physicsWorld.step(0.3f);
		}

		float y = physicsWorld.getPhysicsObject(sprite).getY() + (ScreenValues.SCREEN_HEIGHT / 2);
		physicsWorld.step(0.3f);

		while (y < (physicsWorld.getPhysicsObject(sprite).getY() + (ScreenValues.SCREEN_HEIGHT / 2))) {
			y = physicsWorld.getPhysicsObject(sprite).getY() + (ScreenValues.SCREEN_HEIGHT / 2);
			physicsWorld.step(0.3f);
		}

		return y;
	}
}
