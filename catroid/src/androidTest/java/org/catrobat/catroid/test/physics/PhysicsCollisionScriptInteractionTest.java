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

import android.test.InstrumentationTestCase;
import android.util.Log;

import junit.framework.Assert;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

public class PhysicsCollisionScriptInteractionTest extends InstrumentationTestCase {

	private static final String TAG = PhysicsSpriteCloneTest.class.getSimpleName();

	private static final String FIRST_SPRITE_NAME = "firstSprite";
	private static final String FIRST_SPRITE_NAME_NEW = "firstSpriteNEW";
	private static final String SECOND_SPRITE_NAME = "secondSprite";
	private static final String SECOND_SPRITE_NAME_NEW = "secondSpriteNEW";

	private static final String SPRITE_RENAME_METHOD_NAME = "rename";

	private static final String COLLISION_BROADCAST_MESSAGE = FIRST_SPRITE_NAME
			+ PhysicsCollision.COLLISION_MESSAGE_CONNECTOR + SECOND_SPRITE_NAME;

	private Project project;

	private Sprite firstSprite;
	private CollisionScript firstSpriteCollisionScript;

	private Sprite secondSprite;
	private CollisionScript secondSpriteCollisionScript;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		firstSprite = new Sprite(FIRST_SPRITE_NAME);
		Script firstSpriteStartScript = new StartScript();
		firstSpriteStartScript.addBrick(new SetPhysicsObjectTypeBrick());
		firstSpriteCollisionScript = new CollisionScript("");
		firstSpriteCollisionScript.setAndReturnBroadcastMessage(FIRST_SPRITE_NAME, SECOND_SPRITE_NAME);
		firstSpriteCollisionScript.getScriptBrick();
		firstSprite.addScript(firstSpriteStartScript);
		firstSprite.addScript(firstSpriteCollisionScript);
		project.getDefaultScene().addSprite(firstSprite);

		secondSprite = new Sprite(SECOND_SPRITE_NAME);
		Script secondSpriteStartScript = new StartScript();
		secondSpriteStartScript.addBrick(new SetPhysicsObjectTypeBrick());
		secondSpriteCollisionScript = new CollisionScript("");
		secondSpriteCollisionScript.setAndReturnBroadcastMessage(SECOND_SPRITE_NAME, FIRST_SPRITE_NAME);
		secondSpriteCollisionScript.getScriptBrick();
		secondSprite.addScript(secondSpriteStartScript);
		secondSprite.addScript(secondSpriteCollisionScript);
		project.getDefaultScene().addSprite(secondSprite);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}

	@Override
	protected void tearDown() throws Exception {
		project = null;

		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testCollisionBroadcastMessageGeneration() {
		Assert.assertEquals(String.format("The generated collision broadcast message is incorrect (%s != %s)",
						COLLISION_BROADCAST_MESSAGE,
						PhysicsCollision.generateBroadcastMessage(FIRST_SPRITE_NAME, SECOND_SPRITE_NAME)),
				COLLISION_BROADCAST_MESSAGE,
				PhysicsCollision.generateBroadcastMessage(FIRST_SPRITE_NAME, SECOND_SPRITE_NAME));
	}

	public void testRenamingOfSpriteWithCollisionScript() {

		String colBroadcastMsgFirstSecond = PhysicsCollision.generateBroadcastMessage(FIRST_SPRITE_NAME,
				SECOND_SPRITE_NAME);
		String colBroadcastMsgSecondFirst = PhysicsCollision.generateBroadcastMessage(SECOND_SPRITE_NAME,
				FIRST_SPRITE_NAME);
		String colBroadcastMsgFirstNewSecond = PhysicsCollision.generateBroadcastMessage(FIRST_SPRITE_NAME_NEW,
				SECOND_SPRITE_NAME);
		String colBroadcastMsgSecondFirstNew = PhysicsCollision.generateBroadcastMessage(SECOND_SPRITE_NAME,
				FIRST_SPRITE_NAME_NEW);
		String colBroadcastMsgFirstNewSecondNew = PhysicsCollision.generateBroadcastMessage(FIRST_SPRITE_NAME_NEW,
				SECOND_SPRITE_NAME_NEW);
		String colBroadcastMsgSecondNewFirstNew = PhysicsCollision.generateBroadcastMessage(SECOND_SPRITE_NAME_NEW,
				FIRST_SPRITE_NAME_NEW);

		Assert.assertEquals(String.format("collision broadcast message of first collision script is wrong before "
								+ "renaming (%s != %s)", colBroadcastMsgFirstSecond,
						firstSpriteCollisionScript.getBroadcastMessage()), colBroadcastMsgFirstSecond,
				firstSpriteCollisionScript.getBroadcastMessage());
		Assert.assertEquals(String.format("collision broadcast message of second collision script is wrong before "
								+ "renaming (%s != %s)", colBroadcastMsgSecondFirst,
						secondSpriteCollisionScript.getBroadcastMessage()), colBroadcastMsgSecondFirst,
				secondSpriteCollisionScript.getBroadcastMessage());

		try {
			Object[] values = { FIRST_SPRITE_NAME_NEW };
			Reflection.ParameterList paramList = new Reflection.ParameterList(values);
			Reflection.invokeMethod(Sprite.class, firstSprite, SPRITE_RENAME_METHOD_NAME, paramList);

			Assert.assertEquals(String.format("collision broadcast message of first collision script is wrong after "
									+ "renaming first sprite (%s != %s)", colBroadcastMsgFirstNewSecond,
							firstSpriteCollisionScript.getBroadcastMessage()), colBroadcastMsgFirstNewSecond,
					firstSpriteCollisionScript.getBroadcastMessage());
			Assert.assertEquals(String.format("collision broadcast message of second collision script is wrong after"
									+ "renaming first sprite (%s != %s)", colBroadcastMsgSecondFirstNew,
							secondSpriteCollisionScript.getBroadcastMessage()), colBroadcastMsgSecondFirstNew,
					secondSpriteCollisionScript.getBroadcastMessage());

			values = new Object[] { SECOND_SPRITE_NAME_NEW };
			paramList = new Reflection.ParameterList(values);
			Reflection.invokeMethod(Sprite.class, secondSprite, SPRITE_RENAME_METHOD_NAME, paramList);

			Assert.assertEquals(String.format("collision broadcast message of first collision script is wrong after "
									+ "renaming second sprite (%s != %s)", colBroadcastMsgFirstNewSecondNew,
							firstSpriteCollisionScript.getBroadcastMessage()), colBroadcastMsgFirstNewSecondNew,
					firstSpriteCollisionScript.getBroadcastMessage());
			Assert.assertEquals(String.format("collision broadcast message of second collision script is wrong after "
									+ "renaming second sprite (%s != %s)", colBroadcastMsgSecondNewFirstNew,
							secondSpriteCollisionScript.getBroadcastMessage()), colBroadcastMsgSecondNewFirstNew,
					secondSpriteCollisionScript.getBroadcastMessage());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			Assert.fail("Unexpected Exception in Reflection");
		}
	}
}
