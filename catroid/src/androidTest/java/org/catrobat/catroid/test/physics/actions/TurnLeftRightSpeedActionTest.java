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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TurnLeftRightSpeedActionTest {

	private static final float TURN_TEST_SPEED = 10.0f;

	private static final int TEST_STEPS = 5;
	private static final float TEST_STEP_DELTA_TIME = 1.0f / 60.0f;

	private PhysicsObject physicsObject;

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
	public void testLeftSpeedRotation() {
		physicsObject.setDirection(0);
		physicsObject.setRotationSpeed(TURN_TEST_SPEED);

		assertEquals(TURN_TEST_SPEED, physicsObject.getRotationSpeed(), TestUtils.DELTA);
		skipWorldStabilizingSteps();
		float expectedDegrees = TURN_TEST_SPEED * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {
			float preStepDirection = physicsObject.getDirection();
			physicsWorld.step(TEST_STEP_DELTA_TIME);
			float postStepDirection = physicsObject.getDirection();
			assertEquals(expectedDegrees, postStepDirection - preStepDirection, TestUtils.DELTA);
			assertThat(postStepDirection, is(greaterThan(preStepDirection)));
		}
	}

	@Test
	public void testRightSpeedRotation() {
		physicsObject.setDirection(0);
		physicsObject.setRotationSpeed(-TURN_TEST_SPEED);

		assertEquals(-TURN_TEST_SPEED, physicsObject.getRotationSpeed(), TestUtils.DELTA);
		skipWorldStabilizingSteps();
		float expectedDegrees = -TURN_TEST_SPEED * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {
			float preStepDirection = physicsObject.getDirection();
			physicsWorld.step(TEST_STEP_DELTA_TIME);
			float postStepDirection = physicsObject.getDirection();
			assertEquals(expectedDegrees, postStepDirection - preStepDirection, TestUtils.DELTA);
			assertThat(postStepDirection, is(lessThan(preStepDirection)));
		}
	}

	private void skipWorldStabilizingSteps() {
		for (int i = 0; i < PhysicsWorld.STABILIZING_STEPS; i++) {
			physicsWorld.step(1.0f);
		}
	}
}
