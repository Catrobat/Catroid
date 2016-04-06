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

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.test.utils.Reflection;

import java.util.HashMap;

public class PhysicsObjectTypeDefaultValueTest extends PhysicsBaseTest {

	public void testDefaultValueWithSetBounceBrick() {
		populateSprite(new StartScript(), new SetBounceBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetBounceBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetBounceBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetBounceBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new StartScript(), new SetBounceBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetBounceBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetBounceBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetBounceBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
	}

	public void testDefaultValueWithSetFrictionBrick() {
		populateSprite(new StartScript(), new SetFrictionBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetFrictionBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetFrictionBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetFrictionBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new StartScript(), new SetFrictionBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetFrictionBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetFrictionBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetFrictionBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
	}

	public void testDefaultValueWithSetGravityBrick() {
		populateSprite(new StartScript(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.NONE);
		populateSprite(new WhenScript(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.NONE);
		populateSprite(new BroadcastScript(""), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.NONE);
		populateSprite(new CollisionScript(""), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.NONE);
	}

	public void testDefaultValueWithSetMassBrick() {
		populateSprite(new StartScript(), new SetMassBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetMassBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetMassBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetMassBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new StartScript(), new SetMassBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetMassBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetMassBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetMassBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
	}

	public void testDefaultValueWithSetPhysicsObjectTypeBrick() {
		populateSprite(new StartScript(), new SetPhysicsObjectTypeBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetPhysicsObjectTypeBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetPhysicsObjectTypeBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetPhysicsObjectTypeBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new StartScript(), new SetPhysicsObjectTypeBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetPhysicsObjectTypeBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetPhysicsObjectTypeBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetPhysicsObjectTypeBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
	}

	public void testDefaultValueWithSetVelocityBrick() {
		populateSprite(new StartScript(), new SetVelocityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetVelocityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetVelocityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetVelocityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new StartScript(), new SetVelocityBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new SetVelocityBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new SetVelocityBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new SetVelocityBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
	}

	public void testDefaultValueWithTurnLeftSpeedBrick() {
		populateSprite(new StartScript(), new TurnLeftSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new TurnLeftSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new TurnLeftSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new TurnLeftSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new StartScript(), new TurnLeftSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new TurnLeftSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new TurnLeftSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new TurnLeftSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
	}

	public void testDefaultValueWithTurnRightSpeedBrick() {
		populateSprite(new StartScript(), new TurnRightSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new TurnRightSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new TurnRightSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new TurnRightSpeedBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new StartScript(), new TurnRightSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new WhenScript(), new TurnRightSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new BroadcastScript(""), new TurnRightSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
		populateSprite(new CollisionScript(""), new TurnRightSpeedBrick(), new SetGravityBrick());
		checkPhysicsObjectType(PhysicsObject.Type.DYNAMIC);
	}

	private void checkPhysicsObjectType(PhysicsObject.Type expectedType) {
		Reflection.setPrivateField(physicsWorld, "physicsObjects", new HashMap<Sprite, PhysicsObject>());
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		assertEquals("Physics Object has not the expected type.", expectedType, physicsObject.getType());
	}

	private void populateSprite(Script script, Brick... bricks) {
		sprite.removeAllScripts();
		for (Brick brick : bricks) {
			script.addBrick(brick);
		}
		sprite.addScript(script);
	}
}


