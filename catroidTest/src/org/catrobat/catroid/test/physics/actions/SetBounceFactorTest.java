package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetBounceFactorAction;

public class SetBounceFactorTest extends PhysicsActionTestCase {

	public void testDefaultBounceFactor() {
		physicsWorld.step(1.0f);
		assertEquals("Unexpected bounce factor", PhysicsObject.DEFAULT_BOUNCE_FACTOR, physicsObject.getBounceFactor());
	}

	public void testZeroValue() {
		float bounceFactor = 0.0f;
		initBounceFactor(bounceFactor);
		assertEquals("Unexpected bounce factor", bounceFactor / 100.0f, physicsObject.getBounceFactor());
	}

	public void testHighValue() {
		float bounceFactor = 250.0f;
		initBounceFactor(bounceFactor);
		assertEquals("Unexpected bounce factor", bounceFactor / 100.0f, physicsObject.getBounceFactor());
	}

	public void testNegativeValue() {
		float bounceFactor = -50.0f;
		initBounceFactor(bounceFactor);
		assertEquals("Unexpected bounce factor", bounceFactor / 100.0f, physicsObject.getBounceFactor());
	}

	private void initBounceFactor(float bounceFactor) {
		Formula bounceFactorFormula = new Formula(bounceFactor);
		SetBounceFactorAction setBounceFactorAction = new SetBounceFactorAction();
		setBounceFactorAction.setSprite(sprite);
		setBounceFactorAction.setPhysicsObject(physicsObject);
		setBounceFactorAction.setBounceFactor(bounceFactorFormula);

		setBounceFactorAction.act(1.0f);
		physicsWorld.step(1.0f);
	}
}
