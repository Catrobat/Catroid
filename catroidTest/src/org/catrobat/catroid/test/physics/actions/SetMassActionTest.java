package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetMassAction;

public class SetMassActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		Formula mass = new Formula(10);
		SetMassAction setMassAction = new SetMassAction();
		setMassAction.setSprite(sprite);
		setMassAction.setPhysicsObject(physicsObject);
		setMassAction.setMass(mass);

		setMassAction.act(1.0f);
		physicsWorld.step(1.0f);

		assertEquals("Unexpected mass value", 10f, physicsObject.getMass());
	}

	public void testNegativeValue() {
		Formula mass = new Formula(-10);
		SetMassAction setMassAction = new SetMassAction();
		setMassAction.setSprite(sprite);
		setMassAction.setPhysicsObject(physicsObject);
		setMassAction.setMass(mass);

		setMassAction.act(1.0f);
		physicsWorld.step(1.0f);

		assertEquals("Unexpected mass value", PhysicsObject.MIN_MASS, physicsObject.getMass());
	}

	public void testZeroValue() {
		Formula mass = new Formula(0);
		SetMassAction setMassAction = new SetMassAction();
		setMassAction.setSprite(sprite);
		setMassAction.setPhysicsObject(physicsObject);
		setMassAction.setMass(mass);

		setMassAction.act(1.0f);
		physicsWorld.step(1.0f);

		assertEquals("Unexpected mass value", 0.0f, physicsObject.getMass());
	}

}
