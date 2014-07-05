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
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.utils.TestUtils;

public class VelocityActionTest extends PhysicsActionTestCase {

	private static final float POSITIVE_X_TEST_VELOCITY = 10.0f;
	private static final float POSITIVE_Y_TEST_VELOCITY = 10.0f;
	private static final float NEGATIVE_X_TEST_VELOCITY = -10.0f;
	private static final float NEGATIVE_Y_TEST_VELOCITY = -10.0f;

	private static final int TEST_STEPS = 5;
	private static final float TEST_STEP_DELTA_TIME = 1.0f / 60.0f;

	private PhysicsObject physicsObject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);

	}

	public void testPositiveYVelocityWithoutGravity() {
		assertEquals("Unexpected initial x-coordinate", 0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals("Unexpected initial y-coordinate", 0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(0.0f, POSITIVE_Y_TEST_VELOCITY);

		assertEquals("Unexpected initial x-velocity", 0, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals("Unexpected initial y-velocity", POSITIVE_Y_TEST_VELOCITY, physicsObject.getVelocity().y,
				TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = POSITIVE_Y_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals("Unexpected step length: ", expectedStepLength, postStepYCoordinate - preStepYCoordinate,
					TestUtils.DELTA);

			assertTrue("Post-step y-coordinate (" + postStepYCoordinate + ") is lower than pre-step y-coordinate ("
					+ preStepYCoordinate + "), should be higher!", postStepYCoordinate > preStepYCoordinate);

			assertTrue("Post-step x-coordinate (" + postStepXCoordinate + ") differs from pre-step x-coordinate ("
					+ preStepXCoordinate + "), should be equal!", postStepXCoordinate == preStepXCoordinate);
		}

	}

	public void testNegativeYVelocityWithoutGravity() {
		assertEquals("Unexpected initial x-coordinate", 0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals("Unexpected initial y-coordinate", 0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(0.0f, NEGATIVE_Y_TEST_VELOCITY);

		assertEquals("Unexpected initial x-velocity", 0, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals("Unexpected initial y-velocity", NEGATIVE_Y_TEST_VELOCITY, physicsObject.getVelocity().y,
				TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = NEGATIVE_Y_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals("Unexpected step length: ", expectedStepLength, postStepYCoordinate - preStepYCoordinate,
					TestUtils.DELTA);

			assertTrue("Post-step y-coordinate (" + postStepYCoordinate + ") is higher than pre-step y-coordinate ("
					+ preStepYCoordinate + "), should be lower!", postStepYCoordinate < preStepYCoordinate);

			assertTrue("Post-step x-coordinate (" + postStepXCoordinate + ") differs from pre-step x-coordinate ("
					+ preStepXCoordinate + "), should be equal!", postStepXCoordinate == preStepXCoordinate);
		}

	}

	public void testPositiveXVelocityWithoutGravity() {
		assertEquals("Unexpected initial x-coordinate", 0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals("Unexpected initial y-coordinate", 0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(POSITIVE_X_TEST_VELOCITY, 0.0f);

		assertEquals("Unexpected initial x-velocity", POSITIVE_X_TEST_VELOCITY, physicsObject.getVelocity().x,
				TestUtils.DELTA);
		assertEquals("Unexpected initial y-velocity", 0.0f, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = POSITIVE_X_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals("Unexpected step length: ", expectedStepLength, postStepXCoordinate - preStepXCoordinate,
					TestUtils.DELTA);

			assertTrue("Post-step x-coordinate (" + postStepXCoordinate + ") is lower than pre-step x-coordinate ("
					+ preStepXCoordinate + "), should be higher!", postStepXCoordinate > preStepXCoordinate);

			assertTrue("Post-step Y-coordinate (" + postStepYCoordinate + ") differs from pre-step x-coordinate ("
					+ preStepYCoordinate + "), should be equal!", postStepYCoordinate == preStepYCoordinate);
		}

	}

	public void testNegativeXVelocityWithoutGravity() {
		assertEquals("Unexpected initial x-coordinate", 0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals("Unexpected initial y-coordinate", 0, physicsObject.getY(), TestUtils.DELTA);

		physicsObject.setVelocity(NEGATIVE_X_TEST_VELOCITY, 0.0f);

		assertEquals("Unexpected initial x-velocity", NEGATIVE_X_TEST_VELOCITY, physicsObject.getVelocity().x,
				TestUtils.DELTA);
		assertEquals("Unexpected initial y-velocity", 0.0f, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedStepLength = NEGATIVE_X_TEST_VELOCITY * TEST_STEP_DELTA_TIME;

		for (int i = 0; i < TEST_STEPS; i++) {

			float preStepXCoordinate = physicsObject.getX();
			float preStepYCoordinate = physicsObject.getY();

			physicsWorld.step(TEST_STEP_DELTA_TIME);

			float postStepXCoordinate = physicsObject.getX();
			float postStepYCoordinate = physicsObject.getY();

			assertEquals("Unexpected step length: ", expectedStepLength, postStepXCoordinate - preStepXCoordinate,
					TestUtils.DELTA);

			assertTrue("Post-step x-coordinate (" + postStepXCoordinate + ") is higher than pre-step x-coordinate ("
					+ preStepXCoordinate + "), should be lower!", postStepXCoordinate < preStepXCoordinate);

			assertTrue("Post-step Y-coordinate (" + postStepYCoordinate + ") differs from pre-step x-coordinate ("
					+ preStepYCoordinate + "), should be equal!", postStepYCoordinate == preStepYCoordinate);
		}

	}

	private void skipWorldStabilizingSteps() {
		for (int i = 0; i < PhysicsWorld.STABILIZING_STEPS; i++) {
			physicsWorld.step(1.0f);
		}
	}

}
