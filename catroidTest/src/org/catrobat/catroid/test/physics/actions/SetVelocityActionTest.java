package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetVelocityAction;

public class SetVelocityActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		float velocityX = 10.0f;
		float velocityY = 10.0f;
		Formula velocityXFormula = new Formula(velocityX);
		Formula velocityYFormula = new Formula(velocityY);
		SetVelocityAction setVelocityAction = new SetVelocityAction();
		setVelocityAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setVelocityAction.setPhysicsObject(physicsObject);
		setVelocityAction.setVelocity(velocityXFormula, velocityYFormula);

		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0.0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0.0f, velocityVector.y);

		setVelocityAction.act(1.0f);

		velocityVector = physicsObject.getVelocity();
		assertEquals("Unexpected velocityX value", velocityX, velocityVector.x);
		assertEquals("Unexpected velocityY value", velocityY, velocityVector.y);
	}

	public void testNegativeValue() {
		float velocityX = 10.0f;
		float velocityY = -10.0f;
		Formula velocityXFormula = new Formula(velocityX);
		Formula velocityYFormula = new Formula(velocityY);
		SetVelocityAction setVelocityAction = new SetVelocityAction();
		setVelocityAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setVelocityAction.setPhysicsObject(physicsObject);
		setVelocityAction.setVelocity(velocityXFormula, velocityYFormula);

		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0.0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0.0f, velocityVector.y);

		setVelocityAction.act(1.0f);

		velocityVector = physicsObject.getVelocity();
		assertEquals("Unexpected velocityX value", velocityX, velocityVector.x);
		assertEquals("Unexpected velocityY value", velocityY, velocityVector.y);
	}

	public void testZeroValue() {
		float velocityX = 0.0f;
		float velocityY = 10.0f;
		Formula velocityXFormula = new Formula(velocityX);
		Formula velocityYFormula = new Formula(velocityY);
		SetVelocityAction setVelocityAction = new SetVelocityAction();
		setVelocityAction.setSprite(sprite);
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		setVelocityAction.setPhysicsObject(physicsObject);
		setVelocityAction.setVelocity(velocityXFormula, velocityYFormula);

		Vector2 velocityVector = physicsObject.getVelocity();

		assertEquals("Unexpected velocityX value", 0.0f, velocityVector.x);
		assertEquals("Unexpected velocityY value", 0.0f, velocityVector.y);

		setVelocityAction.act(1.0f);

		velocityVector = physicsObject.getVelocity();
		assertEquals("Unexpected velocityX value", velocityX, velocityVector.x);
		assertEquals("Unexpected velocityY value", velocityY, velocityVector.y);
	}

}
