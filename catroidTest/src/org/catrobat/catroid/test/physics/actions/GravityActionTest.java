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

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

public class GravityActionTest extends PhysicsBaseTest {

	private static final String TAG = GravityActionTest.class.getSimpleName();

	private static final int TEST_STEP_COUNT = 5;
	private static final float TEST_STEP_DELTA_TIME = 1.0f / 60.0f;

	PhysicsObject physicsObject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
	}

	public void testDefaultGravity() {
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();
		assertEquals("Unexpected initial gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected initial gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);
		assertEquals("Unexpected initial velocity Y value", 0, physicsObject.getVelocity().y, TestUtils.DELTA);
		assertEquals("Unexpected initial y-coordinate", 0, physicsObject.getY(), TestUtils.DELTA);

		float preVelocityYValue = physicsObject.getVelocity().y;
		float preYCoordinate = 0.0f;
		for (int step = 0; step < TEST_STEP_COUNT; step++) {
			physicsWorld.step(TEST_STEP_DELTA_TIME);

			Log.d(TAG, "Coordinates (x;y): (" + physicsObject.getX() + ";" + physicsObject.getY() + ")");
			Log.d(TAG, "velocity: " + physicsObject.getVelocity());

			float postYCoordinate = physicsObject.getY();
			float postVelocityYValue = physicsObject.getVelocity().y;

			assertTrue("post y-coordinate (" + postYCoordinate + ") is higher than pre y-coordinate (" + preYCoordinate + "), should be lower!", postYCoordinate < preYCoordinate);
			assertTrue("post velocity.y (" + postVelocityYValue + ") is higher than pre velocity.y (" + preVelocityYValue + "), should be lower!", postVelocityYValue < preVelocityYValue);

			preYCoordinate = postYCoordinate;
			preVelocityYValue = postVelocityYValue;
		}
	}

	public void testPositiveYVelocity() {
		assertEquals("Unexpected initial y-coordinate", 0, physicsObject.getY(), TestUtils.DELTA);

		float startVelocityYValue = 10.0f;
		physicsObject.setVelocity(0.0f, startVelocityYValue);
		assertEquals("Unexpected initial velocity Y value", startVelocityYValue, physicsObject.getVelocity().y, TestUtils.DELTA);

		float startYCoordinate = 0.0f;
		float preVelocityYValue = physicsObject.getVelocity().y;

		physicsWorld.step(TEST_STEP_DELTA_TIME);

		Log.d(TAG, "Coordinates (x;y): (" + physicsObject.getX() + ";" + physicsObject.getY() + ")");
		Log.d(TAG, "velocity: " + physicsObject.getVelocity());

		float yCoordinateAfterFirstStep = physicsObject.getY();
		float postVelocityYValue = physicsObject.getVelocity().y;

		assertTrue("after first step y-coordinate (" + yCoordinateAfterFirstStep + ") is lower than start y-coordinate (" + startYCoordinate + "), should be higher!", yCoordinateAfterFirstStep > startYCoordinate);
		assertTrue("post velocity.y (" + postVelocityYValue + ") is higher than initial value (" + preVelocityYValue + "), should be lower!", postVelocityYValue < preVelocityYValue);


		for (int step = 1; step < TEST_STEP_COUNT; step++) {
			physicsWorld.step(TEST_STEP_DELTA_TIME);

			Log.d(TAG, "Coordinates (x;y): (" + physicsObject.getX() + ";" + physicsObject.getY() + ")");
			Log.d(TAG, "velocity: " + physicsObject.getVelocity());

			postVelocityYValue = physicsObject.getVelocity().y;

			assertTrue("post velocity.y (" + postVelocityYValue + ") is higher than previous value (" + preVelocityYValue + "), should be lower!", postVelocityYValue < preVelocityYValue);

			preVelocityYValue = postVelocityYValue;
		}

		float yCoordinateAfterLastStep = physicsObject.getY();

		assertTrue("after last step y-coordinate (" + yCoordinateAfterLastStep + ") is higher than start y-coordinate (" + startYCoordinate + "), should be lower!", yCoordinateAfterLastStep < startYCoordinate);

	}

}
