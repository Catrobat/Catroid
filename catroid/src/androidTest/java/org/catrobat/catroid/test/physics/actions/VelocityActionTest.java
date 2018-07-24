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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class VelocityActionTest {

	private static final float POSITIVE_X_TEST_VELOCITY = 10.0f;
	private static final float POSITIVE_Y_TEST_VELOCITY = 10.0f;
	private static final float NEGATIVE_X_TEST_VELOCITY = -10.0f;
	private static final float NEGATIVE_Y_TEST_VELOCITY = -10.0f;

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
	public void testPositiveYVelocityWithoutGravity() {
		assertEquals(0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals(0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(0.0f, POSITIVE_Y_TEST_VELOCITY);

		assertEquals(0, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals(POSITIVE_Y_TEST_VELOCITY, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = POSITIVE_Y_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals(expectedStepLength, postStepYCoordinate - preStepYCoordinate, TestUtils.DELTA);

			assertEquals(preStepXCoordinate, postStepXCoordinate);

			assertThat(postStepYCoordinate, is(greaterThan(preStepYCoordinate)));
		}
	}

	@Test
	public void testNegativeYVelocityWithoutGravity() {
		assertEquals(0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals(0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(0.0f, NEGATIVE_Y_TEST_VELOCITY);

		assertEquals(0, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals(NEGATIVE_Y_TEST_VELOCITY, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = NEGATIVE_Y_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals(expectedStepLength, postStepYCoordinate - preStepYCoordinate, TestUtils.DELTA);

			assertEquals(preStepXCoordinate, postStepXCoordinate);

			assertThat(postStepYCoordinate, is(lessThan(preStepYCoordinate)));
		}
	}

	@Test
	public void testPositiveXVelocityWithoutGravity() {
		assertEquals(0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals(0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(POSITIVE_X_TEST_VELOCITY, 0.0f);

		assertEquals(POSITIVE_X_TEST_VELOCITY, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals(0.0f, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = POSITIVE_X_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals(expectedStepLength, postStepXCoordinate - preStepXCoordinate, TestUtils.DELTA);

			assertThat(postStepXCoordinate, is(greaterThan(preStepXCoordinate)));

			assertEquals(preStepYCoordinate, postStepYCoordinate);
		}
	}

	@Test
	public void testNegativeXVelocityWithoutGravity() {
		assertEquals(0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals(0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(NEGATIVE_X_TEST_VELOCITY, 0.0f);

		assertEquals(NEGATIVE_X_TEST_VELOCITY, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals(0.0f, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = NEGATIVE_X_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals(expectedStepLength, postStepXCoordinate - preStepXCoordinate, TestUtils.DELTA);

			assertThat(postStepXCoordinate, is(lessThan(preStepXCoordinate)));

			assertEquals(preStepYCoordinate, postStepYCoordinate);
		}
	}

	private void skipWorldStabilizingSteps() {
		for (int i = 0; i < PhysicsWorld.STABILIZING_STEPS; i++) {
			physicsWorld.step(1.0f);
		}
	}
}
