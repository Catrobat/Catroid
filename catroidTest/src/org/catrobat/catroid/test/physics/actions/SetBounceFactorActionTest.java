package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.ActionFactory;
import org.catrobat.catroid.physics.content.actions.SetBounceFactorAction;
import org.catrobat.catroid.test.utils.Reflection;

public class SetBounceFactorActionTest extends PhysicsActionTestCase {

	private boolean bounced;

	public void testDefaultBounceFactor() {
		physicsWorld.step(1.0f);
		assertEquals("Unexpected bounce factor", PhysicsObject.DEFAULT_BOUNCE_FACTOR,
				physicsWorld.getPhysicsObject(sprite).getBounceFactor());
	}

	public void testZeroValue() {
		float bounceFactor = 0.0f;
		initBounceFactor(bounceFactor);
		assertEquals("Unexpected bounce factor", bounceFactor / 100.0f, physicsWorld.getPhysicsObject(sprite)
				.getBounceFactor());
	}

	public void testHighValue() {
		float bounceFactor = 250.0f;
		initBounceFactor(bounceFactor);
		assertEquals("Unexpected bounce factor", bounceFactor / 100.0f, physicsWorld.getPhysicsObject(sprite)
				.getBounceFactor());
	}

	public void testNegativeValue() {
		float bounceFactor = -50.0f;
		initBounceFactor(bounceFactor);
		assertEquals("Unexpected bounce factor", bounceFactor / 100.0f, physicsWorld.getPhysicsObject(sprite)
				.getBounceFactor());
	}

	private void initBounceFactor(float bounceFactor) {
		Formula bounceFactorFormula = new Formula(bounceFactor);
		SetBounceFactorAction setBounceFactorAction = new SetBounceFactorAction();
		setBounceFactorAction.setSprite(sprite);
		setBounceFactorAction.setPhysicsObject(physicsWorld.getPhysicsObject(sprite));
		setBounceFactorAction.setBounceFactor(bounceFactorFormula);

		setBounceFactorAction.act(1.0f);
		physicsWorld.step(1.0f);
	}

	public void testBounceWithDifferentValues() {
		physicsWorld.getPhysicsObject(sprite).setType(Type.DYNAMIC);
		float bounce_01_hight = bounce(0.1f);
		physicsWorld.getPhysicsObject(sprite).setIfOnEdgeBounce(false, sprite);
		float bounce_06_hight = bounce(0.6f);
		assertTrue(
				"Unexpected value: bounce_06_hight->bounce(0.6) should be greater then bounce_01_hight->bounce(0.1)",
				bounce_01_hight < bounce_06_hight);
	}

	private float bounce(float bounceFactor) {
		bounced = false;
		setContectListener();
		// let gravity work to increase velocity
		physicsWorld.getPhysicsObject(sprite).setPosition(0, 0);
		physicsWorld.getPhysicsObject(sprite).setVelocity(0, 0);
		physicsWorld.getPhysicsObject(sprite).setMass(20);
		physicsWorld.getPhysicsObject(sprite).setBounceFactor(bounceFactor);
		physicsWorld.step(0.3f);
		while (physicsWorld.getPhysicsObject(sprite).getY() > -ScreenValues.SCREEN_HEIGHT / 2) {
			physicsWorld.step(0.3f);
		}
		ActionFactory factory = sprite.getActionFactory();
		Action ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);
		ifOnEdgeBouncePhysicsAction.act(0.1f);

		while (!bounced) {
			physicsWorld.step(0.3f);
		}

		float y = physicsWorld.getPhysicsObject(sprite).getY() + (ScreenValues.SCREEN_HEIGHT / 2);
		physicsWorld.step(0.3f);

		while (y < (physicsWorld.getPhysicsObject(sprite).getY() + (ScreenValues.SCREEN_HEIGHT / 2))) {
			y = physicsWorld.getPhysicsObject(sprite).getY() + (ScreenValues.SCREEN_HEIGHT / 2);
			physicsWorld.step(0.3f);
		}

		return y;
	}

	private void setContectListener() {
		((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.setContactListener(new ContactListener() {
					public void preSolve(Contact contact, Manifold oldManifold) {
						bounced = true;
					}

					public void postSolve(Contact contact, ContactImpulse impulse) {
						bounced = true;
					}

					public void endContact(Contact contact) {
						bounced = true;
					}

					public void beginContact(Contact contact) {
						bounced = true;
					}
				});
	}
}
