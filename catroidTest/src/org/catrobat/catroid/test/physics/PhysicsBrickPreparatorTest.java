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

import android.test.AndroidTestCase;

import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicsBrickPreparatorTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	@Override
	public void setUp() {
	}

	@Override
	public void tearDown() {
	}

	// TODO[physics]
	// no PhysicsBrickPreparator in new physics version

	//	
	//	public void testNoPhysicsBricksToPrepare() {
	//		Project project = new Project();
	//		Sprite sprite = new Sprite("TestSprite");
	//
	//		Script startScript = new StartScript(sprite);
	//		startScript.addBrick(new SetXBrick(sprite, 0));
	//		startScript.addBrick(new ChangeGhostEffectByNBrick(sprite, 5.0f));
	//
	//		Script broadcastScript = new BroadcastScript(sprite);
	//		broadcastScript.addBrick(new HideBrick(sprite));
	//		broadcastScript.addBrick(new BroadcastBrick(sprite));
	//
	//		Script whenScript = new WhenScript(sprite);
	//		whenScript.addBrick(new TurnLeftBrick(sprite, 2.123));
	//
	//		sprite.addScript(startScript);
	//		sprite.addScript(broadcastScript);
	//		sprite.addScript(whenScript);
	//		project.addSprite(sprite);
	//
	//		PhysicsWorldMock physicsWorldMock = new PhysicsWorldMock();
	//		PhysicsBrickPreparator physicsBrickPreparator = new PhysicsBrickPreparator(physicsWorldMock);
	//		physicsBrickPreparator.prepare(project);
	//
	//		assertEquals("Physics objects have been created", 0, physicsWorldMock.getPhysicsObjectExecutedCount);
	//	}
	//
	//	public void testSimplePhysicsObjectBrick() {
	//		Project project = new Project();
	//		Sprite sprite = new Sprite("TestSprite");
	//		Script script = new StartScript(sprite);
	//		PhysicsObjectBrick physicsObjectBrick = new SetMassBrick(sprite, PhysicsObject.DEFAULT_MASS);
	//		script.addBrick(physicsObjectBrick);
	//		sprite.addScript(script);
	//		project.addSprite(sprite);
	//
	//		assertNull("Physics object already has been set",
	//				TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
	//
	//		PhysicsWorldMock physicsWorldMock = new PhysicsWorldMock();
	//		PhysicsBrickPreparator physicsBrickPreparator = new PhysicsBrickPreparator(physicsWorldMock);
	//		physicsBrickPreparator.prepare(project);
	//
	//		assertEquals("Wrong number of get physics object calls", 1, physicsWorldMock.getPhysicsObjectExecutedCount);
	//		assertEquals("Wrong number of physics objects stored", 1, physicsWorldMock.physicsObjects.size());
	//		assertTrue("Physics world hasn't stored the right physics object", physicsWorldMock.physicsObjects.contains(sprite));
	//
	//		assertNotNull("Brick has no physics object", TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
	//		assertEquals("Brick has wrong physics object", physicsWorldMock.getPhysicsObject(sprite),
	//				TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
	//	}
	//
	//	public void testMultiplePhysicsBricksInOneSprite() {
	//		Project project = new Project();
	//		Sprite sprite = new Sprite("TestSprite");
	//		Script script = new StartScript(sprite);
	//		PhysicsObjectBrick[] physicsObjectBricks = { new SetMassBrick(sprite, PhysicsObject.DEFAULT_MASS),
	//				new TurnRightSpeedBrick(sprite, 0.0f), new SetPhysicsObjectTypeBrick(sprite, Type.DYNAMIC) };
	//		for (PhysicsObjectBrick physicsObjectBrick : physicsObjectBricks) {
	//			script.addBrick(physicsObjectBrick);
	//		}
	//		sprite.addScript(script);
	//		project.addSprite(sprite);
	//
	//		for (PhysicsObjectBrick physicsObjectBrick : physicsObjectBricks) {
	//			assertNull("Physics object already has been set",
	//					TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
	//		}
	//
	//		PhysicsWorldMock physicsWorldMock = new PhysicsWorldMock();
	//		PhysicsBrickPreparator physicsBrickPreparator = new PhysicsBrickPreparator(physicsWorldMock);
	//		physicsBrickPreparator.prepare(project);
	//
	//		assertEquals("Wrong number of get physics object calls", 1, physicsWorldMock.getPhysicsObjectExecutedCount);
	//		assertEquals("Wrong number of physics objects stored", 1, physicsWorldMock.physicsObjects.size());
	//		assertTrue("Physics world hasn't stored the right physics object", physicsWorldMock.physicsObjects.contains(sprite));
	//
	//		PhysicsObject physicsObject = physicsWorldMock.getPhysicsObject(sprite);
	//		for (PhysicsObjectBrick physicsObjectBrick : physicsObjectBricks) {
	//			assertNotNull("Brick has no physics object",
	//					TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
	//			assertEquals("Brick has wrong physics object", physicsObject,
	//					TestUtils.getPrivateField("physicsObject", physicsObjectBrick, false));
	//		}
	//	}
	//
	//	public void testSimplePhysicsWorldBrick() {
	//		Project project = new Project();
	//		Sprite sprite = new Sprite("TestSprite");
	//		Script script = new StartScript(sprite);
	//		PhysicsWorldBrick physicsWorldBrick = new SetGravityBrick(sprite, PhysicsWorld.DEFAULT_GRAVITY);
	//		script.addBrick(physicsWorldBrick);
	//		sprite.addScript(script);
	//		project.addSprite(sprite);
	//
	//		assertNull("Physics world has already been set",
	//				TestUtils.getPrivateField("physicsWorld", physicsWorldBrick, false));
	//
	//		PhysicsWorldMock physicsWorldMock = new PhysicsWorldMock();
	//		PhysicsBrickPreparator physicsBrickPreparator = new PhysicsBrickPreparator(physicsWorldMock);
	//		physicsBrickPreparator.prepare(project);
	//
	//		assertNotNull("Physics world hasn't been set", TestUtils.getPrivateField("physicsWorld", physicsWorldBrick, false));
	//		assertEquals("Wrong physics world", physicsWorldMock,
	//				TestUtils.getPrivateField("physicsWorld", physicsWorldBrick, false));
	//	}
	//
	//	public void testMultipleScriptBricksIncludingMultiplePhysicsBricksInOneSprite() {
	//		Vector2 gravity = new Vector2(1.2f, -3.4f);
	//		Project project = new Project();
	//		Sprite sprite = new Sprite("TestSprite");
	//
	//		Script startScript = new StartScript(sprite);
	//		startScript.addBrick(new SetXBrick(sprite, 0));
	//		startScript.addBrick(new ChangeGhostEffectByNBrick(sprite, 5.0f));
	//		startScript.addBrick(new SetGravityBrick(sprite, PhysicsWorld.DEFAULT_GRAVITY));
	//
	//		Script broadcastScript = new BroadcastScript(sprite);
	//		broadcastScript.addBrick(new HideBrick(sprite));
	//		broadcastScript.addBrick(new BroadcastBrick(sprite));
	//		broadcastScript.addBrick(new SetGravityBrick(sprite, gravity));
	//
	//		Script whenScript = new WhenScript(sprite);
	//		whenScript.addBrick(new IfOnEdgeBounceBrick(sprite));
	//		whenScript.addBrick(new SetPhysicsObjectTypeBrick(sprite, Type.FIXED));
	//		whenScript.addBrick(new SetBounceFactorBrick(sprite, 1.0f));
	//		whenScript.addBrick(new SetFrictionBrick(sprite, 0.5f));
	//
	//		sprite.addScript(startScript);
	//		sprite.addScript(broadcastScript);
	//		sprite.addScript(whenScript);
	//		project.addSprite(sprite);
	//
	//		PhysicsWorldMock physicsWorldMock = new PhysicsWorldMock();
	//		PhysicsBrickPreparator physicsBrickPreparator = new PhysicsBrickPreparator(physicsWorldMock);
	//		physicsBrickPreparator.prepare(project);
	//
	//		assertEquals("Wrong number of get physics object calls", 1, physicsWorldMock.getPhysicsObjectExecutedCount);
	//		assertEquals("Wrong number of physics objects stored", 1, physicsWorldMock.physicsObjects.size());
	//		assertTrue("Physics world hasn't stored the right physics object", physicsWorldMock.physicsObjects.contains(sprite));
	//
	//		PhysicsObject physicsObject = physicsWorldMock.getPhysicsObject(sprite);
	//		for (int index = 0; index < sprite.getNumberOfScripts(); index++) {
	//			Script script = sprite.getScript(index);
	//
	//			for (Brick brick : script.getBrickList()) {
	//				if (brick instanceof PhysicsWorldBrick) {
	//					assertEquals("Wrong physics world", physicsWorldMock,
	//							TestUtils.getPrivateField("physicsWorld", brick, false));
	//				} else if (brick instanceof PhysicsObjectBrick) {
	//					assertEquals("Wrong physics object", physicsObject,
	//							TestUtils.getPrivateField("physicsObject", brick, false));
	//				}
	//			}
	//		}
	//	}
	//
	//	private class PhysicsWorldMock extends PhysicsWorld {
	//		private int getPhysicsObjectExecutedCount = 0;
	//		private Set<Sprite> physicsObjects = new HashSet<Sprite>();
	//
	//		@Override
	//		public PhysicsObject getPhysicsObject(Sprite sprite) {
	//			PhysicsObject physicsObject = super.getPhysicsObject(sprite);
	//
	//			getPhysicsObjectExecutedCount++;
	//			physicsObjects.add(sprite);
	//
	//			return physicsObject;
	//		}
	//	}
}
