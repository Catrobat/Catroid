package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.TurnLeftSpeedAction;

public class TurnLeftSpeedActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		float speed = 45.55f;
		Formula speedFormula = new Formula(speed);
		TurnLeftSpeedAction turnLeftSpeedAction = new TurnLeftSpeedAction();
		turnLeftSpeedAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		turnLeftSpeedAction.setPhysicsObject(physicsObject);
		turnLeftSpeedAction.setSpeed(speedFormula);

		assertEquals("Unexpected rotation speed value", 0.0f, physicsObject.getRotationSpeed());

		turnLeftSpeedAction.act(1.0f);

		assertEquals("Unexpected rotation speed value", speed, physicsObject.getRotationSpeed());
	}

	public void testNegativeValue() {
		float speed = -45.55f;
		Formula speedFormula = new Formula(speed);
		TurnLeftSpeedAction turnLeftSpeedAction = new TurnLeftSpeedAction();
		turnLeftSpeedAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		turnLeftSpeedAction.setPhysicsObject(physicsObject);
		turnLeftSpeedAction.setSpeed(speedFormula);

		assertEquals("Unexpected rotation speed value", 0.0f, physicsObject.getRotationSpeed());

		turnLeftSpeedAction.act(1.0f);

		assertEquals("Unexpected rotation speed value", speed, physicsObject.getRotationSpeed());
	}

	public void testZeroValue() {
		float speed = 0f;
		Formula speedFormula = new Formula(speed);
		TurnLeftSpeedAction turnLeftSpeedAction = new TurnLeftSpeedAction();
		turnLeftSpeedAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		turnLeftSpeedAction.setPhysicsObject(physicsObject);
		turnLeftSpeedAction.setSpeed(speedFormula);

		assertEquals("Unexpected rotation speed value", 0.0f, physicsObject.getRotationSpeed());

		turnLeftSpeedAction.act(1.0f);

		assertEquals("Unexpected rotation speed value", speed, physicsObject.getRotationSpeed());
	}

}
