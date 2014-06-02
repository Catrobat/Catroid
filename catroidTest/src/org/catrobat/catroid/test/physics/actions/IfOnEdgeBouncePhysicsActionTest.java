package org.catrobat.catroid.test.physics.actions;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.content.ActionFactory;

public class IfOnEdgeBouncePhysicsActionTest extends PhysicsActionTestCase {

	PhysicsObject physicsObject;

	public void testNormalBehavior() {
		bounce(new Vector2(0, -ScreenValues.SCREEN_HEIGHT / 2), new Vector2(0,-100));
	}

	public void testRightBounce() {
		bounce(new Vector2(ScreenValues.SCREEN_WIDTH / 2, 0), new Vector2(100, 0));
	}

	public void testLeftBounce() {
		bounce(new Vector2(-ScreenValues.SCREEN_WIDTH / 2, 0), new Vector2(-100, 0));
	}

	public void testUpBounce() {
		bounce(new Vector2(0, ScreenValues.SCREEN_HEIGHT / 2), new Vector2(0,100));
	}

	public void bounce(Vector2 position, Vector2 velocity) {
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(Type.DYNAMIC);
		physicsObject.setMass(20);
		physicsObject.setPosition(position);
		physicsObject.setBounceFactor(3);
		// let the sprite fall
		for (int i = 0; i < 20; i++) {
			physicsObject.setVelocity(velocity.x,velocity.y);
			physicsWorld.step(1.3f);
			float x = physicsObject.getX();
			float y = physicsObject.getY();
			Log.d("IfOnEdgeBouncePhysicsActionTest", "physicsObject.getX():" + x);
			Log.d("IfOnEdgeBouncePhysicsActionTest", "physicsObject.getY():" + y);
			if ((Math.abs(x) > ScreenValues.SCREEN_WIDTH / 2)||(Math.abs(y) > ScreenValues.SCREEN_HEIGHT / 2)) {
				break;
			}
		}
		// to be on the safe site ;)
		physicsWorld.step(0.1f);

		ActionFactory factory = sprite.getActionFactory();
		Action ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);
		ifOnEdgeBouncePhysicsAction.act(0.2f);
		float x = physicsObject.getX();
		float y = physicsObject.getY();
		assertTrue("Unexpected X value: object is not totaly on screen", (Math.abs(x) < ScreenValues.SCREEN_WIDTH / 2));
		assertTrue("Unexpected Y value: object is not totaly on screen", (Math.abs(y) < ScreenValues.SCREEN_HEIGHT / 2));
		physicsWorld.step(0.1f);
		assertTrue("Unexpected behavior: object not bounced yet", bounced);
	}

}
