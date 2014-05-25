package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.actions.IfOnEdgeBouncePhysicsAction;

public class IfOnEdgeBouncePhysicsActionTest extends PhysicsActionTestCase {

	public void testNormalBehavior() {
		PhysicsObject.Type type = PhysicsObject.Type.NONE;
		IfOnEdgeBouncePhysicsAction ifOnEdgeBouncePhysicsAction = new IfOnEdgeBouncePhysicsAction();
		ifOnEdgeBouncePhysicsAction.setSprite(sprite);

	}
}
