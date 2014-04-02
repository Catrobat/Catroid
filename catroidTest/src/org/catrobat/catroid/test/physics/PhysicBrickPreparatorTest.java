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
package org.catrobat.catroid.test.physics;

import java.util.HashSet;
import java.util.Set;

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectByNBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.physics.SetBounceFactorBrick;
import org.catrobat.catroid.content.bricks.physics.SetFrictionBrick;
import org.catrobat.catroid.content.bricks.physics.SetGravityBrick;
import org.catrobat.catroid.content.bricks.physics.SetMassBrick;
import org.catrobat.catroid.content.bricks.physics.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.content.bricks.physics.TurnRightSpeedBrick;
import org.catrobat.catroid.physics.PhysicBrickPreparator;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.utils.TestUtils;

import android.test.AndroidTestCase;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicBrickPreparatorTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	@Override
	public void setUp() {
	}

	@Override
	public void tearDown() {
	}

	public void testNoPhysicBricksToPrepare() {
		Project project = new Project();
		Sprite sprite = new Sprite("TestSprite");

		Script startScript = new StartScript(sprite);
		startScript.addBrick(new SetXBrick(sprite, 0));
		startScript.addBrick(new ChangeGhostEffectByNBrick(sprite, 5.0f));

		Script broadcastScript = new BroadcastScript(sprite);
		broadcastScript.addBrick(new HideBrick(sprite));
		broadcastScript.addBrick(new BroadcastBrick(sprite));

		Script whenScript = new WhenScript(sprite);
		whenScript.addBrick(new TurnLeftBrick(sprite, 2.123));

		sprite.addScript(startScript);
		sprite.addScript(broadcastScript);
		sprite.addScript(whenScript);
		project.addSprite(sprite);

		PhysicWorldMock physicsWorldMock = new PhysicWorldMock();
		PhysicBrickPreparator physicsBrickPreparator = new PhysicBrickPreparator(physicsWorldMock);
		physicsBrickPreparator.prepare(project);

		assertEquals("Physic objects have been created", 0, physicsWorldMock.getPhysicObjectExecutedCount);
	}

	public void testSimplePhysicObjectBrick() {
		Project project = new Project();
		Sprite sprite = new Sprite("TestSprite");
		Script script = new StartScript(sprite);
		PhysicObjectBrick physicsObjectBrick = new SetMassBrick(sprite, PhysicsObject.DEFAULT_MASS);
		script.addBrick(physicsObjectBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		assertNull("Physic object already has been set",
				TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));

		PhysicWorldMock physicsWorldMock = new PhysicWorldMock();
		PhysicBrickPreparator physicsBrickPreparator = new PhysicBrickPreparator(physicsWorldMock);
		physicsBrickPreparator.prepare(project);

		assertEquals("Wrong number of get physics object calls", 1, physicsWorldMock.getPhysicObjectExecutedCount);
		assertEquals("Wrong number of physics objects stored", 1, physicsWorldMock.physicsObjects.size());
		assertTrue("Physic world hasn't stored the right physics object", physicsWorldMock.physicsObjects.contains(sprite));

		assertNotNull("Brick has no physics object", TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
		assertEquals("Brick has wrong physics object", physicsWorldMock.getPhysicObject(sprite),
				TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
	}

	public void testMultiplePhysicBricksInOneSprite() {
		Project project = new Project();
		Sprite sprite = new Sprite("TestSprite");
		Script script = new StartScript(sprite);
		PhysicObjectBrick[] physicsObjectBricks = { new SetMassBrick(sprite, PhysicsObject.DEFAULT_MASS),
				new TurnRightSpeedBrick(sprite, 0.0f), new SetPhysicsObjectTypeBrick(sprite, Type.DYNAMIC) };
		for (PhysicObjectBrick physicsObjectBrick : physicsObjectBricks) {
			script.addBrick(physicsObjectBrick);
		}
		sprite.addScript(script);
		project.addSprite(sprite);

		for (PhysicObjectBrick physicsObjectBrick : physicsObjectBricks) {
			assertNull("Physic object already has been set",
					TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
		}

		PhysicWorldMock physicsWorldMock = new PhysicWorldMock();
		PhysicBrickPreparator physicsBrickPreparator = new PhysicBrickPreparator(physicsWorldMock);
		physicsBrickPreparator.prepare(project);

		assertEquals("Wrong number of get physics object calls", 1, physicsWorldMock.getPhysicObjectExecutedCount);
		assertEquals("Wrong number of physics objects stored", 1, physicsWorldMock.physicsObjects.size());
		assertTrue("Physic world hasn't stored the right physics object", physicsWorldMock.physicsObjects.contains(sprite));

		PhysicsObject physicsObject = physicsWorldMock.getPhysicObject(sprite);
		for (PhysicObjectBrick physicsObjectBrick : physicsObjectBricks) {
			assertNotNull("Brick has no physics object",
					TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
			assertEquals("Brick has wrong physics object", physicsObject,
					TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
		}
	}

	public void testSimplePhysicWorldBrick() {
		Project project = new Project();
		Sprite sprite = new Sprite("TestSprite");
		Script script = new StartScript(sprite);
		PhysicWorldBrick physicsWorldBrick = new SetGravityBrick(sprite, PhysicsWorld.DEFAULT_GRAVITY);
		script.addBrick(physicsWorldBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		assertNull("Physic world has already been set",
				TestUtils.getPrivateField("physicsWorld", physicsWorldBrick, false));

		PhysicWorldMock physicsWorldMock = new PhysicWorldMock();
		PhysicBrickPreparator physicsBrickPreparator = new PhysicBrickPreparator(physicsWorldMock);
		physicsBrickPreparator.prepare(project);

		assertNotNull("Physic world hasn't been set", TestUtils.getPrivateField("physicsWorld", physicsWorldBrick, false));
		assertEquals("Wrong physics world", physicsWorldMock,
				TestUtils.getPrivateField("physicsWorld", physicsWorldBrick, false));
	}

	public void testMultipleScriptBricksIncludingMultiplePhysicBricksInOneSprite() {
		Vector2 gravity = new Vector2(1.2f, -3.4f);
		Project project = new Project();
		Sprite sprite = new Sprite("TestSprite");

		Script startScript = new StartScript(sprite);
		startScript.addBrick(new SetXBrick(sprite, 0));
		startScript.addBrick(new ChangeGhostEffectByNBrick(sprite, 5.0f));
		startScript.addBrick(new SetGravityBrick(sprite, PhysicsWorld.DEFAULT_GRAVITY));

		Script broadcastScript = new BroadcastScript(sprite);
		broadcastScript.addBrick(new HideBrick(sprite));
		broadcastScript.addBrick(new BroadcastBrick(sprite));
		broadcastScript.addBrick(new SetGravityBrick(sprite, gravity));

		Script whenScript = new WhenScript(sprite);
		whenScript.addBrick(new IfOnEdgeBounceBrick(sprite));
		whenScript.addBrick(new SetPhysicsObjectTypeBrick(sprite, Type.FIXED));
		whenScript.addBrick(new SetBounceFactorBrick(sprite, 1.0f));
		whenScript.addBrick(new SetFrictionBrick(sprite, 0.5f));

		sprite.addScript(startScript);
		sprite.addScript(broadcastScript);
		sprite.addScript(whenScript);
		project.addSprite(sprite);

		PhysicWorldMock physicsWorldMock = new PhysicWorldMock();
		PhysicBrickPreparator physicsBrickPreparator = new PhysicBrickPreparator(physicsWorldMock);
		physicsBrickPreparator.prepare(project);

		assertEquals("Wrong number of get physics object calls", 1, physicsWorldMock.getPhysicObjectExecutedCount);
		assertEquals("Wrong number of physics objects stored", 1, physicsWorldMock.physicsObjects.size());
		assertTrue("Physic world hasn't stored the right physics object", physicsWorldMock.physicsObjects.contains(sprite));

		PhysicsObject physicsObject = physicsWorldMock.getPhysicObject(sprite);
		for (int index = 0; index < sprite.getNumberOfScripts(); index++) {
			Script script = sprite.getScript(index);

			for (Brick brick : script.getBrickList()) {
				if (brick instanceof PhysicWorldBrick) {
					assertEquals("Wrong physics world", physicsWorldMock,
							TestUtils.getPrivateField("physicsWorld", brick, false));
				} else if (brick instanceof PhysicObjectBrick) {
					assertEquals("Wrong physics object", physicsObject,
							TestUtils.getPrivateField("physicsObject", brick, false));
				}
			}
		}
	}

	private class PhysicWorldMock extends PhysicsWorld {
		private int getPhysicObjectExecutedCount = 0;
		private Set<Sprite> physicsObjects = new HashSet<Sprite>();

		@Override
		public PhysicsObject getPhysicObject(Sprite sprite) {
			PhysicsObject physicsObject = super.getPhysicObject(sprite);

			getPhysicObjectExecutedCount++;
			physicsObjects.add(sprite);

			return physicsObject;
		}
	}
}

