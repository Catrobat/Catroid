package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.content.actions.TurnRightSpeedAction;

public class TurnRightSpeedActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		float speed = 45.55f;
		Formula speedFormula = new Formula(speed);
		TurnRightSpeedAction turnRightSpeedAction = new TurnRightSpeedAction();
		turnRightSpeedAction.setSprite(sprite);
		turnRightSpeedAction.setPhysicsObject(physicsObject);
		turnRightSpeedAction.setSpeed(speedFormula);

		assertEquals("Unexpected rotation speed value", 0.0f, physicsObject.getRotationSpeed());

		turnRightSpeedAction.act(1.0f);

		assertEquals("Unexpected rotation speed value", -speed, physicsObject.getRotationSpeed());
	}

	public void testNegativeValue() {
		float speed = -45.55f;
		Formula speedFormula = new Formula(speed);
		TurnRightSpeedAction turnRightSpeedAction = new TurnRightSpeedAction();
		turnRightSpeedAction.setSprite(sprite);
		turnRightSpeedAction.setPhysicsObject(physicsObject);
		turnRightSpeedAction.setSpeed(speedFormula);

		assertEquals("Unexpected rotation speed value", 0.0f, physicsObject.getRotationSpeed());

		turnRightSpeedAction.act(1.0f);

		assertEquals("Unexpected rotation speed value", -speed, physicsObject.getRotationSpeed());
	}

	public void testZeroValue() {
		float speed = 0f;
		Formula speedFormula = new Formula(speed);
		TurnRightSpeedAction turnRightSpeedAction = new TurnRightSpeedAction();
		turnRightSpeedAction.setSprite(sprite);
		turnRightSpeedAction.setPhysicsObject(physicsObject);
		turnRightSpeedAction.setSpeed(speedFormula);

		assertEquals("Unexpected rotation speed value", 0.0f, physicsObject.getRotationSpeed());

		turnRightSpeedAction.act(1.0f);

		assertEquals("Unexpected rotation speed value", -speed, physicsObject.getRotationSpeed());
	}

}
