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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class VelocityActionTest {
	private static final float POSITIVE_X_TEST_VELOCITY = 10.0f;
	private static final float POSITIVE_Y_TEST_VELOCITY = 10.0f;
	private static final float NEGATIVE_X_TEST_VELOCITY = -10.0f;
	private static final float NEGATIVE_Y_TEST_VELOCITY = -10.0f;

	private static final float TEST_STEP_DELTA_TIME = 1.0f / 60.0f;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"NoVelocity", 0.0f, 0.0f},
				{"Negative YVelocity no XVelocity", 0.0f, NEGATIVE_Y_TEST_VELOCITY},
				{"Positive YVelocity no XVelocity", 0.0f, POSITIVE_Y_TEST_VELOCITY},
				{"Negative XVelocity no YVelocity", NEGATIVE_X_TEST_VELOCITY, 0.0f},
				{"Positive XVelocity no YVelocity", POSITIVE_X_TEST_VELOCITY, 0.0f},
				{"Negative XVelocity Negative YVelocity", NEGATIVE_X_TEST_VELOCITY, NEGATIVE_Y_TEST_VELOCITY},
				{"Negative XVelocity Positive YVelocity", NEGATIVE_X_TEST_VELOCITY, POSITIVE_Y_TEST_VELOCITY},
				{"Positive XVelocity Negative YVelocity", POSITIVE_X_TEST_VELOCITY, NEGATIVE_Y_TEST_VELOCITY},
				{"Positive XVelocity Positive YVelocity", POSITIVE_X_TEST_VELOCITY, POSITIVE_Y_TEST_VELOCITY},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public float xVelocity;

	@Parameterized.Parameter(2)
	public float yVelocity;

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private PhysicsObject physicsObject;
	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() throws Exception {
		Sprite sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
	}

	@Test
	public void testVelocityInit() {
		assertEquals(0, physicsObject.getX(), TestUtils.DELTA);
		assertEquals(0, physicsObject.getY(), TestUtils.DELTA);
		assertEquals(0, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals(0, physicsObject.getVelocity().y, TestUtils.DELTA);
		physicsObject.setVelocity(xVelocity, yVelocity);
		assertEquals(xVelocity, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals(yVelocity, physicsObject.getVelocity().y, TestUtils.DELTA);
	}

	@Test
	public void testObjectXStepWithoutGravity() {
		physicsObject.setVelocity(xVelocity, yVelocity);
		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();

		float expectedXStepLength = xVelocity * TEST_STEP_DELTA_TIME;
		float preStepXCoordinate = physicsObject.getX();
		physicsWorld.step(TEST_STEP_DELTA_TIME);
		float postStepXCoordinate = physicsObject.getX();
		assertEquals(expectedXStepLength, postStepXCoordinate - preStepXCoordinate, TestUtils.DELTA);
	}

	@Test
	public void testObjectYStepWithoutGravity() {
		physicsObject.setVelocity(xVelocity, yVelocity);
		physicsWorld.setGravity(0.0f, 0.0f);

		skipWorldStabilizingSteps();
		float expectedYStepLength = yVelocity * TEST_STEP_DELTA_TIME;

		float preStepYCoordinate = physicsObject.getY();
		physicsWorld.step(TEST_STEP_DELTA_TIME);
		float postStepYCoordinate = physicsObject.getY();

		assertEquals(expectedYStepLength, postStepYCoordinate - preStepYCoordinate, TestUtils.DELTA);
	}

	@Test
	public void testObjectVelocityAfterStep() {
		physicsObject.setVelocity(xVelocity, yVelocity);
		physicsWorld.setGravity(0.0f, 0.0f);
		skipWorldStabilizingSteps();

		physicsWorld.step(TEST_STEP_DELTA_TIME);
		assertEquals(xVelocity, physicsObject.getVelocity().x);
		assertEquals(yVelocity, physicsObject.getVelocity().y);
	}

	private void skipWorldStabilizingSteps() {
		for (int i = 0; i < PhysicsWorld.STABILIZING_STEPS; i++) {
			physicsWorld.step(1.0f);
		}
	}
}
