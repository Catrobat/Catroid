/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class GravityActionTest {

	private static final int TEST_STEP_COUNT = 10;
	private static final float TEST_STEP_DELTA_TIME = 0.1f;

	PhysicsObject physicsObject;

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() throws Exception {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
	}

	@Test
	public void testDefaultGravity() throws Exception {
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();
		assertEquals(PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals(PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);
		assertEquals(0, physicsObject.getVelocity().y, TestUtils.DELTA);
		assertEquals(0, physicsObject.getY(), TestUtils.DELTA);
		simulate();
	}

	@Test
	public void testVaryingGravity() {
		assertEquals(0, physicsObject.getY(), TestUtils.DELTA);
		simulate();
		float velocityByDefaultGravity = Math.abs(physicsObject.getVelocity().y);
		resetPhysicObject();
		physicsWorld.setGravity(0.0f, PhysicsWorld.DEFAULT_GRAVITY.y * 2);
		simulate();
		float velocityByDuplexGravity = Math.abs(physicsObject.getVelocity().y);
		assertThat(velocityByDuplexGravity, is(greaterThan(velocityByDefaultGravity)));
	}

	private void simulate() {
		float preVelocityYValue = Math.abs(physicsObject.getVelocity().y);
		float postVelocityYValue = 0;
		for (int step = 1; step < TEST_STEP_COUNT; step++) {
			physicsWorld.step(TEST_STEP_DELTA_TIME);
			postVelocityYValue = Math.abs(physicsObject.getVelocity().y);
			assertThat(postVelocityYValue, is(greaterThan(preVelocityYValue)));
			preVelocityYValue = postVelocityYValue;
		}
	}

	private void resetPhysicObject() {
		physicsObject.setVelocity(0.0f, 0.0f);
		physicsObject.setPosition(0.0f, 0.0f);
	}
}
