package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.SetFrictionAction;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;

public class SetFrictionActionTest extends PhysicsActionTestCase {

	public void testDefaultValue() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		FixtureDef fixtureDef = PhysicsTestUtils.getFixtureDef(physicsObject);
		assertEquals("Unexpected default friction value", PhysicsObject.DEFAULT_FRICTION, fixtureDef.friction);
	}

	public void testNormalBehavior() {

		for (int i = 0; i <= 100; i += 10) {
			initFrictionFactor(i);
			assertEquals("Unexpected friction value", i / 100.0f, physicsWorld.getPhysicsObject(sprite).getFriction());
		}
	}

	public void testNegativeValues() {
		for (int i = -1; i >= -101; i -= 10) {
			initFrictionFactor(i);
			assertEquals("Unexpected friction value", PhysicsObject.MIN_FRICTION, physicsWorld.getPhysicsObject(sprite)
					.getFriction());
		}
	}

	public void testTooLargeValues() {
		for (int i = 101; i <= 201; i += 10) {
			initFrictionFactor(i);
			assertEquals("Unexpected friction value", PhysicsObject.MAX_FRICTION, physicsWorld.getPhysicsObject(sprite)
					.getFriction());
		}
	}

	private void initFrictionFactor(float frictionFactor) {
		Formula friction = new Formula(frictionFactor);
		SetFrictionAction setFrictionAction = new SetFrictionAction();
		setFrictionAction.setSprite(sprite);
		setFrictionAction.setPhysicsObject(physicsWorld.getPhysicsObject(sprite));
		setFrictionAction.setFriction(friction);

		setFrictionAction.act(1.0f);
		physicsWorld.step(1.0f);
	}
}