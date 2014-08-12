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

package org.catrobat.catroid.test.physics.actions.conditional;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.test.physics.PhysicsCollisionBaseTest;

public class GlideToPhysicsActionAndCollisionTest extends PhysicsCollisionBaseTest {

	private static final String TAG = GlideToPhysicsActionAndCollisionTest.class.getSimpleName();

	private ActionPhysicsFactory actionFactory = new ActionPhysicsFactory();
	private Action glideToPhysicsAction;

	public GlideToPhysicsActionAndCollisionTest() {
		spritePosition = new Vector2(0.0f, -200.0f);
		sprite2Position = new Vector2(0.0f, 0.0f);
		physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		physicsObject2Type = PhysicsObject.Type.FIXED;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		glideToPhysicsAction = actionFactory.createGlideToAction(sprite, new Formula(0f), new Formula(0f), new Formula(1f));
	}

	public void testCollisionsDependingOnGlide() {
		physicsWorld.step(DELTA_TIME);
		assertTrue("Velocity does not grow. Gravity does not work correctly", physicsObject1.getVelocity().y < 0);
		glideToPhysicsAction.act(DELTA_TIME);
		assertTrue("Glide in progress, physics-object should be hanged up", physicsObject1.getVelocity().y == 0);
		boolean gliding;
		do {
			Log.d(TAG, "y-position: " + sprite.look.getYInUserInterfaceDimensionUnit());
			assertFalse("No collision while gliding allowed", collisionDetected() && physicsObject1.getVelocity().y == 0);
			physicsWorld.step(DELTA_TIME);
			gliding = physicsObject1.getVelocity().y == 0;
		} while(gliding);

		assertTrue("No collision with the second physics object after glide to.", simulateFullCollision());


	}

	public void testHangupAfterGlideStart() {

	}

	public void testResumeAfterGlideFinish() {

	}

	public void testNullSprite() {
		Action action = actionFactory.createGlideToAction(null, null, null, null);
		try {
			action.act(1.0f);
			fail("Execution of GlideToBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown successful", true);
		}
	}
}
