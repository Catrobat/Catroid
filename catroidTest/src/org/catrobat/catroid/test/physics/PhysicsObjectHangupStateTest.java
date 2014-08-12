/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

package org.catrobat.catroid.test.physics;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.utils.Reflection;


public class PhysicsObjectHangupStateTest extends PhysicsBaseTest {
	private static final String TAG = PhysicsObjectHangupStateTest.class.getSimpleName();

	private static final float DEFAULT_VELOCITY_X = 50.0f;
	private static final float DEFAULT_VELOCITY_Y = 60.0f;
	private static final float DEFAULT_GRAVITY_SCALE = 0.5f;
	private static final float DEFAULT_ROTATION_SPEED = 70.0f;

	private PhysicsObject physicsObject;
	private Object hangupState;
	private Object positionCondition;
	private Object visibleCondition;
	private Object glideToCondition;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		hangupState = Reflection.getPrivateField(PhysicsObject.class, physicsObject, "hangupState");
		positionCondition = Reflection.getPrivateField(hangupState, "positionCondition");
		visibleCondition = Reflection.getPrivateField(hangupState, "visibleCondition");
		glideToCondition = Reflection.getPrivateField(hangupState, "glideToCondition");
		initializeDefaultTestValues();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	private void initializeDefaultTestValues() {
		physicsObject.setVelocity(DEFAULT_VELOCITY_X, DEFAULT_VELOCITY_Y);
		physicsObject.setGravityScale(DEFAULT_GRAVITY_SCALE);
		physicsObject.setRotationSpeed(DEFAULT_ROTATION_SPEED);
	}

	private void checkHangupCondition(Object hangupCondition, boolean expectedValue) {
		boolean computedValue = (Boolean) Reflection.invokeMethod(hangupCondition, "shouldHangupBeActive");
		assertEquals("Condition should return " + String.valueOf(expectedValue), expectedValue, computedValue);
	}

	private void checkHangoutStateValues(Vector2 expectedVelocity, float expectedRotationSpeed, float expectedGravityScale, boolean expectedHangedUp) {
		Log.d(TAG, "expectedVelocity.x: " + expectedVelocity.x);
		Log.d(TAG, "expectedVelocity.y: " + expectedVelocity.y);
		Log.d(TAG, "expectedRotationSpeed: " + expectedRotationSpeed);
		Log.d(TAG, "expectedGravityScale: " + expectedGravityScale);
		Log.d(TAG, "expectedHangedUp: " + expectedHangedUp);
		Vector2 velocity = (Vector2) Reflection.getPrivateField(hangupState, "velocity");
		float rotationSpeed = (Float) Reflection.getPrivateField(hangupState, "rotationSpeed");
		float gravityScale = (Float) Reflection.getPrivateField(hangupState, "gravityScale");
		boolean hangedUp = (Boolean) Reflection.getPrivateField(hangupState, "hangedUp");
		Log.d(TAG, "velocity.x: " + velocity.x);
		Log.d(TAG, "velocity.y: " + velocity.y);
		Log.d(TAG, "rotationSpeed: " + rotationSpeed);
		Log.d(TAG, "gravityScale: " + gravityScale);
		Log.d(TAG, "hangedUp: " + hangedUp);
		assertEquals("expectedVelocity.x " + expectedVelocity.x + " of hangupState must be equal to actual value " + velocity.x, expectedVelocity.x, velocity.x);
		assertEquals("expectedVelocity.y " + expectedVelocity.y + " of hangupState must be equal to actual value " + velocity.y, expectedVelocity.y, velocity.y);
		assertEquals("expectedRotationSpeed " + expectedRotationSpeed + " of hangupState must be equal to actual value " + rotationSpeed, expectedRotationSpeed, rotationSpeed);
		assertEquals("expectedGravityScale " + expectedGravityScale + " of hangupState must be equal to actual value " + gravityScale, expectedGravityScale, gravityScale);
		assertEquals("expectedHangedUp " + expectedHangedUp + " of hangupState must be equal to actual value " + hangedUp, expectedHangedUp, hangedUp);
	}

	public void testUpdateHangupWithNoHangupActivation() {
		physicsObject.updateHangupState(true);
		checkHangupCondition(positionCondition, false);
		checkHangupCondition(visibleCondition, false);
		checkHangupCondition(glideToCondition, false);
		Vector2 velocity = (Vector2) Reflection.getPrivateField(hangupState, "velocity");
		float rotationSpeed = (Float) Reflection.getPrivateField(hangupState, "rotationSpeed");
		float gravityScale = (Float) Reflection.getPrivateField(hangupState, "gravityScale");
		boolean hangedUp = (Boolean) Reflection.getPrivateField(hangupState, "hangedUp");
		Log.d(TAG, "velocity.x: " + velocity.x);
		Log.d(TAG, "velocity.y: " + velocity.y);
		Log.d(TAG, "rotationSpeed: " + rotationSpeed);
		Log.d(TAG, "gravityScale: " + gravityScale);
		Log.d(TAG, "hangedUp: " + hangedUp);
		assertNotSame("default velocity.x of hangupState must be different to value from Object", DEFAULT_VELOCITY_X, velocity.x);
		assertNotSame("default velocity.y of hangupState must be different to value from Object", DEFAULT_VELOCITY_Y, velocity.y);
		assertNotSame("default rotationSpeed of hangupState must be different to value from Object", DEFAULT_ROTATION_SPEED, rotationSpeed);
		assertNotSame("default gravityScale of hangupState must be different to value from Object", DEFAULT_GRAVITY_SCALE, gravityScale);
		assertFalse("physics object must not be hanged up", hangedUp);
	}

	public void testOutSideOfActiveAreaHangUp() {
		physicsObject.setPosition(5000, 5000);
		physicsObject.updateHangupState(true);
		checkHangupCondition(positionCondition, true);
		checkHangupCondition(visibleCondition, false);
		checkHangupCondition(glideToCondition, false);
		checkHangoutStateValues(new Vector2(DEFAULT_VELOCITY_X, DEFAULT_VELOCITY_Y), DEFAULT_ROTATION_SPEED, DEFAULT_GRAVITY_SCALE, true);
	}

	public void testNotVisibleHangup() {
		physicsObject.setVisible(false);
		physicsObject.updateHangupState(true);
		checkHangupCondition(positionCondition, false);
		checkHangupCondition(visibleCondition, true);
		checkHangupCondition(glideToCondition, false);
		checkHangoutStateValues(new Vector2(DEFAULT_VELOCITY_X, DEFAULT_VELOCITY_Y), DEFAULT_ROTATION_SPEED, DEFAULT_GRAVITY_SCALE, true);
	}

	public void testGlideToHangup() {
		Action glideToPhysicsAction = sprite.getActionFactory().createGlideToAction(sprite, new Formula(0), new Formula(0), new Formula(1000));
		// TODO check why i can not hook in
		glideToPhysicsAction.act(0.1f);
		physicsObject.updateHangupState(true);
		checkHangupCondition(positionCondition, false);
		checkHangupCondition(visibleCondition, false);
		checkHangupCondition(glideToCondition, true);
		checkHangoutStateValues(new Vector2(DEFAULT_VELOCITY_X, DEFAULT_VELOCITY_Y), DEFAULT_ROTATION_SPEED, DEFAULT_GRAVITY_SCALE, true);
	}

	public void testHangupContitionCombinations() {

	}

}
