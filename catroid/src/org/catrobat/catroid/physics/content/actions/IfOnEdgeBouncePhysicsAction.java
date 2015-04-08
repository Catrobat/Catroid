/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.physics.content.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsBoundaryBox;
import org.catrobat.catroid.physics.PhysicsWorld;

public class IfOnEdgeBouncePhysicsAction extends TemporalAction {

	//private static final String TAG = IfOnEdgeBouncePhysicsAction.class.getSimpleName();

	public static final float THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE = 10.0f;
	private static final float COLLISION_OVERLAP_RANGE_FACTOR = 0.5f;

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	private void performVerticalRepositioning(float bbLookOffsetX, boolean velocityHighEnoughToCollideAfterRepositioning, boolean correctGravityPresent) {
		sprite.look.setXInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit() + bbLookOffsetX);
		checkBounceActivation(correctGravityPresent, velocityHighEnoughToCollideAfterRepositioning, sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_VERTICAL);
	}

	private void performHorizontalRepositioning(float bbLookOffsetY, boolean velocityHighEnoughToCollideAfterRepositioning, boolean correctGravityPresent) {
		sprite.look.setYInUserInterfaceDimensionUnit(sprite.look.getYInUserInterfaceDimensionUnit() + bbLookOffsetY);
		checkBounceActivation(correctGravityPresent, velocityHighEnoughToCollideAfterRepositioning, sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_HORIZONTAL);
	}

	private void checkBounceActivation(boolean correctGravityPresent, boolean velocityHighEnoughToCollideAfterRepositioning, Sprite sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier) {
		if ((velocityHighEnoughToCollideAfterRepositioning || correctGravityPresent)) {
			physicsWorld.setBounceOnce(sprite, boundaryBoxIdentifier);
		}
	}

	@Override
	protected void update(float percent) {
		// AABB ... AXIS-ALIGNED-BOUNDING-BOX
		Vector2 bbLowerEdge = new Vector2();
		Vector2 bbUpperEdge = new Vector2();
		physicsWorld.getPhysicsObject(sprite).getBoundaryBox(bbLowerEdge, bbUpperEdge);

		float bbWidth = bbUpperEdge.x - bbLowerEdge.x;
		float bbHeight = bbUpperEdge.y - bbLowerEdge.y;
		float bbCenterX = bbLowerEdge.x + bbWidth / 2;
		float bbCenterY = bbLowerEdge.y + bbHeight / 2;

		int vsWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		int vsHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;

		float leftCollisionAreaInnerBorder = (-vsWidth / 2.0f) + (bbWidth / 2.0f);
		float leftCollisionAreaOuterBorder = leftCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * (-bbWidth);
		boolean leftVelocityHighEnoughToCollideAfterRepositioning = physicsWorld.getPhysicsObject(sprite).getVelocity().x <= -THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean leftGravityPresent = physicsWorld.getGravity().x < 0;

		float rightCollisionAreaInnerBorder = (vsWidth / 2.0f) - (bbWidth / 2.0f);
		float rightCollisionAreaOuterBorder = rightCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * bbWidth;
		boolean rightVelocityHighEnoughToCollideAfterRepositioning = physicsWorld.getPhysicsObject(sprite).getVelocity().x >= THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean rightGravityPresent = physicsWorld.getGravity().x > 0;

		if (leftCollisionAreaOuterBorder < bbCenterX && bbCenterX < leftCollisionAreaInnerBorder) {
			float bbLookOffsetX = Math.abs(bbCenterX - (bbWidth / 2.0f) + (vsWidth / 2.0f));
			performVerticalRepositioning(bbLookOffsetX, leftVelocityHighEnoughToCollideAfterRepositioning, leftGravityPresent);
		} else if (rightCollisionAreaOuterBorder > bbCenterX && bbCenterX > rightCollisionAreaInnerBorder) {
			float bbLookOffsetX = Math.abs(bbCenterX + (bbWidth / 2.0f) - (vsWidth / 2.0f));
			performVerticalRepositioning(-bbLookOffsetX, rightVelocityHighEnoughToCollideAfterRepositioning, rightGravityPresent);
		}

		float bottomCollisionAreaInnerBorder = (-vsHeight / 2.0f) + (bbHeight / 2.0f);
		float bottomCollisionAreaOuterBorder = bottomCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * (-bbHeight);
		boolean bottomVelocityHighEnoughToCollideAfterRepositioning = physicsWorld.getPhysicsObject(sprite).getVelocity().y <= -THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean bottomGravityPresent = physicsWorld.getGravity().y < 0;

		float topCollisionAreaInnerBorder = (vsHeight / 2.0f) - (bbHeight / 2.0f);
		float topCollisionAreaOuterBorder = topCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * bbHeight;
		boolean topVelocityHighEnoughToCollideAfterRepositioning = physicsWorld.getPhysicsObject(sprite).getVelocity().y >= THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean topGravityPresent = physicsWorld.getGravity().y > 0;

		if (bottomCollisionAreaOuterBorder < bbCenterY && bbCenterY < bottomCollisionAreaInnerBorder) {
			float bbLookOffsetY = Math.abs(bbCenterY - (bbHeight / 2.0f) + (vsHeight / 2.0f));
			performHorizontalRepositioning(bbLookOffsetY, bottomVelocityHighEnoughToCollideAfterRepositioning, bottomGravityPresent);
		} else if (topCollisionAreaOuterBorder > bbCenterY && bbCenterY > topCollisionAreaInnerBorder) {
			float bbLookOffsetY = Math.abs(bbCenterY + (bbHeight / 2.0f) - (vsHeight / 2.0f));
			performHorizontalRepositioning(-bbLookOffsetY, topVelocityHighEnoughToCollideAfterRepositioning, topGravityPresent);
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPhysicsWorld(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}
}
