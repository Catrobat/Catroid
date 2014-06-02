package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetPhysicsObjectTypeAction;

public class SetPhysicsObjectTypeActionTest extends PhysicsActionTestCase {

	public void testPhysicsTypeNone() {
		PhysicsObject.Type type = PhysicsObject.Type.NONE;
		SetPhysicsObjectTypeAction setPhysicsObjectTypeAction = new SetPhysicsObjectTypeAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsObjectTypeAction.setPhysicsObject(physicsObject);
		setPhysicsObjectTypeAction.setType(type);

		assertEquals("Unexpected physics object type", PhysicsObject.Type.NONE, physicsObject.getType());

		setPhysicsObjectTypeAction.act(1.0f);

		assertEquals("Unexpected physics object type", type, physicsObject.getType());
	}

	public void testPhysicsTypeDynamic() {
		PhysicsObject.Type type = PhysicsObject.Type.DYNAMIC;
		SetPhysicsObjectTypeAction setPhysicsObjectTypeAction = new SetPhysicsObjectTypeAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsObjectTypeAction.setPhysicsObject(physicsObject);
		setPhysicsObjectTypeAction.setType(type);

		assertEquals("Unexpected physics object type", PhysicsObject.Type.NONE, physicsObject.getType());

		setPhysicsObjectTypeAction.act(1.0f);

		assertEquals("Unexpected physics object type", type, physicsObject.getType());
	}

	public void testPhysicsTypeFixed() {
		PhysicsObject.Type type = PhysicsObject.Type.FIXED;
		SetPhysicsObjectTypeAction setPhysicsObjectTypeAction = new SetPhysicsObjectTypeAction();
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setPhysicsObjectTypeAction.setPhysicsObject(physicsObject);
		setPhysicsObjectTypeAction.setType(type);

		assertEquals("Unexpected physics object type", PhysicsObject.Type.NONE, physicsObject.getType());

		setPhysicsObjectTypeAction.act(1.0f);

		assertEquals("Unexpected physics object type", type, physicsObject.getType());
	}

}
