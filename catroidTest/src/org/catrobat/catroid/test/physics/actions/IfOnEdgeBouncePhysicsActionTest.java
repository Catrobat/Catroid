package org.catrobat.catroid.test.physics.actions;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.ActionFactory;
import org.catrobat.catroid.test.utils.Reflection;

public class IfOnEdgeBouncePhysicsActionTest extends PhysicsActionTestCase {

	private boolean bounced = false;

	public void testBottomBounce() {
		setContectListener();
		// let gravity work to increase velocity
		simulate(); // some steps for simulation
		bounce(new Vector2(0, -ScreenValues.SCREEN_HEIGHT / 2)); // So the half of the rectangle should be outside of the screen
		assertTrue("IfOnEdgeBouncePhysicsActionTest - object not bounced", bounced);
	}

	public void testCeilingBounce() {
		setContectListener();
		physicsWorld.setGravity(0, 10);
		// let gravity work to increase velocity
		simulate(); // some steps for simulation
		bounce(new Vector2(0, ScreenValues.SCREEN_HEIGHT / 2)); // So the half of the rectangle should be outside of the screen
		assertTrue("IfOnEdgeBouncePhysicsActionTest - object not bounced", bounced);
	}

	public void testRightBounce() {
		setContectListener();
		physicsWorld.setGravity(10, 0);
		// let gravity work to increase velocity
		simulate(); // some steps for simulation
		bounce(new Vector2(ScreenValues.SCREEN_WIDTH / 2, 0)); // So the half of the rectangle should be outside of the screen
		assertTrue("IfOnEdgeBouncePhysicsActionTest - object not bounced", bounced);
	}

	public void testLeftBounce() {
		setContectListener();
		physicsWorld.setGravity(-10, 0);
		// let gravity work to increase velocity
		simulate(); // some steps for simulation
		bounce(new Vector2(-ScreenValues.SCREEN_WIDTH / 2, 0)); // So the half of the rectangle should be outside of the screen
		assertTrue("IfOnEdgeBouncePhysicsActionTest - object not bounced", bounced);
	}

	private void bounce(Vector2 pos) {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(Type.DYNAMIC);
		physicsObject.setPosition(pos);
		assertTrue("Unexpected value", physicsObject.getX() == pos.x);
		assertTrue("Unexpected value", physicsObject.getY() == pos.y);
		ActionFactory factory = sprite.getActionFactory();
		Action ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);
		logPosition(physicsObject);
		ifOnEdgeBouncePhysicsAction.act(0.1f);
		simulate(); // some steps for simulation
		logPosition(physicsObject);
	}

	private void logPosition(PhysicsObject physicsObject) {
		Log.d("IfOnEdgeBouncePhysicsActionTest", "ifOnEdgeBouncePhysicsAction ....getY():" + physicsObject.getX());
	}

	private void simulate() {
		physicsWorld.step(0.3f); // some steps for simulation
		physicsWorld.step(0.3f);
		physicsWorld.step(0.3f);
		physicsWorld.step(0.3f);
		physicsWorld.step(0.3f);
		physicsWorld.step(0.3f);
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
