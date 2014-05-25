package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.actions.SetGravityAction;
import org.catrobat.catroid.test.utils.Reflection;

public class SetGravityActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		float gravityX = 10.0f;
		float gravityY = 10.0f;
		Formula gravityXFormula = new Formula(gravityX);
		Formula gravityYFormula = new Formula(gravityY);
		SetGravityAction setGravityAction = new SetGravityAction();
		setGravityAction.setSprite(sprite);
		setGravityAction.setPhysicsWorld(physicsWorld);
		setGravityAction.setGravity(gravityXFormula, gravityYFormula);

		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		setGravityAction.act(1.0f);

		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();
		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);

	}

	public void testNegativeValue() {
		float gravityX = 10.0f;
		float gravityY = -10.0f;
		Formula gravityXFormula = new Formula(gravityX);
		Formula gravityYFormula = new Formula(gravityY);
		SetGravityAction setGravityAction = new SetGravityAction();
		setGravityAction.setSprite(sprite);
		setGravityAction.setPhysicsWorld(physicsWorld);
		setGravityAction.setGravity(gravityXFormula, gravityYFormula);

		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		setGravityAction.act(1.0f);

		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();
		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);
	}

	public void testZeroValue() {
		float gravityX = 0.0f;
		float gravityY = 10.0f;
		Formula gravityXFormula = new Formula(gravityX);
		Formula gravityYFormula = new Formula(gravityY);
		SetGravityAction setGravityAction = new SetGravityAction();
		setGravityAction.setSprite(sprite);
		setGravityAction.setPhysicsWorld(physicsWorld);
		setGravityAction.setGravity(gravityXFormula, gravityYFormula);

		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals("Unexpected gravityX value", PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals("Unexpected gravityY value", PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		setGravityAction.act(1.0f);

		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();
		assertEquals("Unexpected gravityX value", gravityX, gravityVector.x);
		assertEquals("Unexpected gravityY value", gravityY, gravityVector.y);
	}

}
