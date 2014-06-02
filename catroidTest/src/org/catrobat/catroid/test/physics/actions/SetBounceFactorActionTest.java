package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetBounceFactorAction;
import org.catrobat.catroid.test.utils.Reflection;

public class SetBounceFactorActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		float bounceFactor = 45.55f;
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Formula bounceFactorFormula = new Formula(bounceFactor);
		SetBounceFactorAction setBounceFactorAction = new SetBounceFactorAction();
		setBounceFactorAction.setSprite(sprite);
		setBounceFactorAction.setPhysicsObject(physicsObject);
		setBounceFactorAction.setBounceFactor(bounceFactorFormula);

		float fixtureBouceFactor = ((FixtureDef) Reflection.getPrivateField(PhysicsObject.class, physicsObject,
				"fixtureDef")).restitution;

		assertEquals("Unexpected bounceFactor value", PhysicsObject.DEFAULT_BOUNCE_FACTOR, fixtureBouceFactor);

		setBounceFactorAction.act(1.0f);

		fixtureBouceFactor = ((FixtureDef) Reflection.getPrivateField(PhysicsObject.class, physicsObject, "fixtureDef")).restitution;

		assertEquals("Unexpected bounceFactor value", bounceFactor / 100.0f, fixtureBouceFactor);
	}

	public void testNegativeValue() {
		float bounceFactor = -45.55f;
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Formula bounceFactorFormula = new Formula(bounceFactor);
		SetBounceFactorAction setBounceFactorAction = new SetBounceFactorAction();
		setBounceFactorAction.setSprite(sprite);
		setBounceFactorAction.setPhysicsObject(physicsObject);
		setBounceFactorAction.setBounceFactor(bounceFactorFormula);

		float fixtureBouceFactor = ((FixtureDef) Reflection.getPrivateField(PhysicsObject.class, physicsObject,
				"fixtureDef")).restitution;

		assertEquals("Unexpected bounceFactor value", PhysicsObject.DEFAULT_BOUNCE_FACTOR, fixtureBouceFactor);

		setBounceFactorAction.act(1.0f);

		fixtureBouceFactor = ((FixtureDef) Reflection.getPrivateField(PhysicsObject.class, physicsObject, "fixtureDef")).restitution;

		assertEquals("Unexpected bounceFactor value", bounceFactor / 100.0f, fixtureBouceFactor);
	}

	public void testZeroValue() {
		float bounceFactor = 0f;
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		Formula bounceFactorFormula = new Formula(bounceFactor);
		SetBounceFactorAction setBounceFactorAction = new SetBounceFactorAction();
		setBounceFactorAction.setSprite(sprite);
		setBounceFactorAction.setPhysicsObject(physicsObject);
		setBounceFactorAction.setBounceFactor(bounceFactorFormula);

		float fixtureBouceFactor = ((FixtureDef) Reflection.getPrivateField(PhysicsObject.class, physicsObject,
				"fixtureDef")).restitution;

		assertEquals("Unexpected bounceFactor value", PhysicsObject.DEFAULT_BOUNCE_FACTOR, fixtureBouceFactor);

		setBounceFactorAction.act(1.0f);

		fixtureBouceFactor = ((FixtureDef) Reflection.getPrivateField(PhysicsObject.class, physicsObject, "fixtureDef")).restitution;

		assertEquals("Unexpected bounceFactor value", bounceFactor / 100.0f, fixtureBouceFactor);
	}

}
