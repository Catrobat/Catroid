/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;

import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.eventids.CollisionEventId;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.physics.PhysicsCollisionBroadcast;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PhysicsCollisionBetweenTest {

	@Rule
	public PhysicsCollisionTestRule rule = new PhysicsCollisionTestRule();

	private Sprite sprite;
	private Sprite sprite2;
	private Project project;

	@Before
	public void setUp() {
		sprite = rule.sprite;
		sprite2 = rule.sprite2;
		project = rule.project;

		rule.spritePosition = new Vector2(0.0f, 100.0f);
		rule.sprite2Position = new Vector2(0.0f, -200.0f);
		rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC;
		rule.physicsObject2Type = PhysicsObject.Type.FIXED;
		rule.initializeSpritesForCollision();
	}

	public void beginContactCallback(Contact contact) throws Exception {
		rule.beginContactCallback(contact);
		Map<Integer, PhysicsCollisionBroadcast> physicsCollisionBroadcasts =
				(Map<Integer, PhysicsCollisionBroadcast>) Reflection.getPrivateField(PhysicsCollision.class,
						rule.physicsCollisionTestListener, "physicsCollisionBroadcasts");
		assertEquals(1, physicsCollisionBroadcasts.size());
		Object[] parameters = {sprite, sprite2};
		Reflection.ParameterList paramList = new Reflection.ParameterList(parameters);
		CollisionEventId key = (CollisionEventId) Reflection.invokeMethod(PhysicsCollision.class,
				rule.physicsCollisionTestListener,
				"generateKey", paramList);
		PhysicsCollisionBroadcast collisionBroadcast = physicsCollisionBroadcasts.get(key);
		assertEquals(collisionBroadcast.getContactCounter(), rule.getContactDifference());
	}

	public void endContactCallback(Contact contact) throws Exception {
		rule.endContactCallback(contact);
		Map<Integer, PhysicsCollisionBroadcast> physicsCollisionBroadcasts =
				(Map<Integer, PhysicsCollisionBroadcast>) Reflection.getPrivateField(PhysicsCollision.class,
						rule.physicsCollisionTestListener, "physicsCollisionBroadcasts");
		if (rule.getContactDifference() == 0) {
			assertEquals(0, physicsCollisionBroadcasts.size());
		} else {
			assertEquals(2, physicsCollisionBroadcasts.size());
		}
	}

	@Test
	public void testIfBroadcastsAreCorrectPreparedAndFired() {
		assertTrue(rule.isContactRateOk());
		assertTrue(rule.simulateFullCollision());
		assertTrue(rule.isContactRateOk());
	}

	@Test
	public void testCollisionBroadcastOfTwoSprites() {
		assertNotNull(sprite.look.getLookData());
		assertNotNull(sprite2.look.getLookData());

		CollisionScript secondSpriteCollisionScript = new CollisionScript(null);
		secondSpriteCollisionScript.setSpriteToCollideWithName(sprite.getName());
		secondSpriteCollisionScript.getScriptBrick();
		int testXValue = 444;
		int testYValue = 555;
		PlaceAtBrick testBrick = new PlaceAtBrick(testXValue, testYValue);
		secondSpriteCollisionScript.addBrick(testBrick);
		sprite2.addScript(secondSpriteCollisionScript);

		sprite2.initializeEventThreads(EventId.START);

		rule.simulateFullCollision();

		while (!allActionsOfAllSpritesAreFinished()) {
			for (Sprite spriteOfList : project.getDefaultScene().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertEquals((float) testXValue, sprite2.look.getXInUserInterfaceDimensionUnit());
		assertEquals((float) testYValue, sprite2.look.getYInUserInterfaceDimensionUnit());
	}

	public boolean allActionsOfAllSpritesAreFinished() {
		for (Sprite spriteOfList : project.getDefaultScene().getSpriteList()) {
			if (!spriteOfList.look.haveAllThreadsFinished()) {
				return false;
			}
		}
		return true;
	}
}
