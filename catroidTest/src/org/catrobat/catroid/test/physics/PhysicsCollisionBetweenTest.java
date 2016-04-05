/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import com.badlogic.gdx.physics.box2d.Contact;

import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.physics.PhysicsCollisionBroadcast;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.utils.Reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicsCollisionBetweenTest extends PhysicsCollisionBaseTest {

	private static final String TAG = PhysicsCollisionBetweenTest.class.getSimpleName();

	public PhysicsCollisionBetweenTest() {
		spritePosition = new Vector2(0.0f, 100.0f);
		sprite2Position = new Vector2(0.0f, -200.0f);
		physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		physicsObject2Type = PhysicsObject.Type.FIXED;
	}

	@Override
	public void beginContactCallback(Contact contact) {
		try {
			super.beginContactCallback(contact);
			Map<Integer, PhysicsCollisionBroadcast> physicsCollisionBroadcasts = (Map<Integer,
					PhysicsCollisionBroadcast>) Reflection.getPrivateField(PhysicsCollision.class,
					physicsCollisionTestListener, "physicsCollisionBroadcasts");
			assertTrue("Map must contain one element", physicsCollisionBroadcasts.size() == 2);
			Object[] parameters = { sprite, sprite2 };
			Reflection.ParameterList paramList = new Reflection.ParameterList(parameters);
			String key = (String) Reflection.invokeMethod(PhysicsCollision.class, physicsCollisionTestListener,
					"generateKey", paramList);
			PhysicsCollisionBroadcast collisionBroadcast = physicsCollisionBroadcasts.get(key);
			assertEquals("collision broadcast counter must be equal to beginCounter - endCounter", collisionBroadcast
					.getContactCounter(), getContactDifference());
		} catch (Exception exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
			fail("An unexpected exception was captured. See Logcat for details");
		}
	}

	@Override
	public void endContactCallback(Contact contact) {
		try {
			super.endContactCallback(contact);
			Map<Integer, PhysicsCollisionBroadcast> physicsCollisionBroadcasts = (Map<Integer,
					PhysicsCollisionBroadcast>) Reflection.getPrivateField(PhysicsCollision.class,
					physicsCollisionTestListener, "physicsCollisionBroadcasts");
			if (getContactDifference() == 0) {
				assertTrue("Map must contain zero elements", physicsCollisionBroadcasts.size() == 0);
			} else {
				assertTrue("Map must contain one element", physicsCollisionBroadcasts.size() == 2);
			}
		} catch (Exception exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
			fail("An unexpected exception was captured. See Logcat for details");
		}
	}

	public void testIfBroadcastsAreCorrectPreparedAndFired() {
		assertTrue("collision rate is not zero before step", isContactRateOk());
		assertTrue("no collision detected", simulateFullCollision());
		assertTrue("collision rate is not zero after step", isContactRateOk());
	}

	public void testCollisionBroadcastOfTwoSprites() {
		assertTrue("getLookData of sprite is null", sprite.look.getLookData() != null);
		assertTrue("getLookData of sprite2 is null", sprite2.look.getLookData() != null);

		CollisionScript secondSpriteCollisionScript = new CollisionScript("");
		secondSpriteCollisionScript.setAndReturnBroadcastMessage(sprite2.getName(), sprite.getName());
		secondSpriteCollisionScript.getScriptBrick();
		int testXValue = 444;
		int testYValue = 555;
		PlaceAtBrick testBrick = new PlaceAtBrick(testXValue, testYValue);
		secondSpriteCollisionScript.addBrick(testBrick);
		sprite2.addScript(secondSpriteCollisionScript);

		sprite2.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		simulateFullCollision();

		while (!allActionsOfAllSpritesAreFinished()) {
			for (Sprite spriteOfList : project.getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertEquals("X Value of Sprite was not set", (float) testXValue, sprite2.look.getXInUserInterfaceDimensionUnit
				());
		assertEquals("Y Value of Sprite was not set", (float) testYValue, sprite2.look.getYInUserInterfaceDimensionUnit
				());
	}

	public boolean allActionsOfAllSpritesAreFinished() {
		for (Sprite spriteOfList : project.getSpriteList()) {
			if (!spriteOfList.look.getAllActionsAreFinished()) {
				return false;
			}
		}
		return true;
	}
}
