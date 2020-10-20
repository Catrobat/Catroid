/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicalCollision;
import org.catrobat.catroid.physics.PhysicsBoundaryBox;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;

public class IfOnEdgeBouncePhysicsAction extends TemporalAction {

	enum Side{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT
	}

	public static final float THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE = 10.0f;
	private static final float COLLISION_OVERLAP_RANGE_FACTOR = 0.9f;
	private final int vsWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
	private final int vsHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	private float bbWidth = 0.0f;
	private float bbHeight = 0.0f;
	private float bbCenterX = 0.0f;
	private float bbCenterY = 0.0f;

	private void performRepositioning(float bbLookOffset, boolean velocityHighEnoughToCollideAfterRepositioning, boolean correctGravityPresent, Side side) {
		if (side == Side.LEFT || side == Side.RIGHT) {
			sprite.look.setXInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit() + bbLookOffset);
			changeDirectionOnStepsTaken(side);
			checkBounceActivation(correctGravityPresent, velocityHighEnoughToCollideAfterRepositioning, sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_VERTICAL);
		} else {
			sprite.look.setYInUserInterfaceDimensionUnit(sprite.look.getYInUserInterfaceDimensionUnit() + bbLookOffset);
			changeDirectionOnStepsTaken(side);
			checkBounceActivation(correctGravityPresent, velocityHighEnoughToCollideAfterRepositioning, sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_HORIZONTAL);
		}
	}

	private void checkBounceActivation(boolean correctGravityPresent, boolean velocityHighEnoughToCollideAfterRepositioning, Sprite sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier) {
		if (velocityHighEnoughToCollideAfterRepositioning || correctGravityPresent) {
			physicsWorld.setBounceOnce(sprite, boundaryBoxIdentifier);
		}
	}

	private void changeDirectionOnStepsTaken(Side side) {
		if (sprite.movedByStepsBrick) {
			PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
			float realRotation = sprite.look.getDirectionInUserInterfaceDimensionUnit();
			if (side == Side.LEFT || side == Side.RIGHT) {
				sprite.look.setDirectionInUserInterfaceDimensionUnit((-realRotation));
				calculateBBDimensions(physicsObject);
				((PhysicsLook) sprite.look).updateFlippedByAction();
			} else if (side == Side.TOP || side == Side.BOTTOM) {
				sprite.look.setDirectionInUserInterfaceDimensionUnit((180.0f - realRotation));
				calculateBBDimensions(physicsObject);
			}
			switch (side) {
				case LEFT:
					sprite.look.setXInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit() + calculateLeftCollisionOffset());
					break;
				case RIGHT:
					sprite.look.setXInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit() - calculateRightCollisionOffset());
					break;
				case TOP:
					sprite.look.setYInUserInterfaceDimensionUnit(sprite.look.getYInUserInterfaceDimensionUnit() - calculateTopCollisionOffset());
					break;
				case BOTTOM:
					sprite.look.setYInUserInterfaceDimensionUnit(sprite.look.getYInUserInterfaceDimensionUnit() + calculateBottomCollisionOffset());
					break;
				default:
					throw new IllegalArgumentException("invalid side");
			}
			PhysicalCollision.fireBounceOffEvent(sprite, null);
		}
	}

	private void calculateBBDimensions(PhysicsObject physicsObject) {
		Vector2 bbLowerEdge = new Vector2();
		Vector2 bbUpperEdge = new Vector2();
		physicsObject.getBoundaryBox(bbLowerEdge, bbUpperEdge);
		bbWidth = bbUpperEdge.x - bbLowerEdge.x;
		bbHeight = bbUpperEdge.y - bbLowerEdge.y;
		bbCenterX = bbLowerEdge.x + bbWidth / 2;
		bbCenterY = bbLowerEdge.y + bbHeight / 2;
	}

	@Override
	protected void update(float percent) {
		// AABB ... AXIS-ALIGNED-BOUNDING-BOX
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		calculateBBDimensions(physicsObject);

		float leftCollisionAreaInnerBorder = (-vsWidth / 2.0f) + (bbWidth / 2.0f);
		float leftCollisionAreaOuterBorder = leftCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * (-bbWidth);
		boolean leftVelocityHighEnoughToCollideAfterRepositioning = physicsObject.getVelocity().x <= -THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean leftGravityPresent = physicsWorld.getGravity().x < 0;

		float rightCollisionAreaInnerBorder = (vsWidth / 2.0f) - (bbWidth / 2.0f);
		float rightCollisionAreaOuterBorder = rightCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * bbWidth;
		boolean rightVelocityHighEnoughToCollideAfterRepositioning = physicsObject.getVelocity().x >= THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean rightGravityPresent = physicsWorld.getGravity().x > 0;

		if (leftCollisionAreaOuterBorder < bbCenterX && bbCenterX < leftCollisionAreaInnerBorder) {
			performRepositioning(calculateLeftCollisionOffset(), leftVelocityHighEnoughToCollideAfterRepositioning, leftGravityPresent, Side.LEFT);
		} else if (rightCollisionAreaOuterBorder > bbCenterX && bbCenterX > rightCollisionAreaInnerBorder) {
			performRepositioning(-calculateRightCollisionOffset(), rightVelocityHighEnoughToCollideAfterRepositioning, rightGravityPresent, Side.RIGHT);
		}

		float bottomCollisionAreaInnerBorder = (-vsHeight / 2.0f) + (bbHeight / 2.0f);
		float bottomCollisionAreaOuterBorder = bottomCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * (-bbHeight);
		boolean bottomVelocityHighEnoughToCollideAfterRepositioning = physicsObject.getVelocity().y <= -THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean bottomGravityPresent = physicsWorld.getGravity().y < 0;

		float topCollisionAreaInnerBorder = (vsHeight / 2.0f) - (bbHeight / 2.0f);
		float topCollisionAreaOuterBorder = topCollisionAreaInnerBorder + COLLISION_OVERLAP_RANGE_FACTOR * bbHeight;
		boolean topVelocityHighEnoughToCollideAfterRepositioning = physicsObject.getVelocity().y >= THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE;
		boolean topGravityPresent = physicsWorld.getGravity().y > 0;

		if (bottomCollisionAreaOuterBorder < bbCenterY && bbCenterY < bottomCollisionAreaInnerBorder) {
			performRepositioning(calculateBottomCollisionOffset(), bottomVelocityHighEnoughToCollideAfterRepositioning, bottomGravityPresent, Side.BOTTOM);
		} else if (topCollisionAreaOuterBorder > bbCenterY && bbCenterY > topCollisionAreaInnerBorder) {
			performRepositioning(-calculateTopCollisionOffset(), topVelocityHighEnoughToCollideAfterRepositioning, topGravityPresent, Side.TOP);
		}
		sprite.movedByStepsBrick = false;
	}

	private float calculateLeftCollisionOffset() {
		return Math.abs(bbCenterX - (bbWidth / 2.0f) + (vsWidth / 2.0f));
	}

	private float calculateRightCollisionOffset() {
		return Math.abs(bbCenterX + (bbWidth / 2.0f) - (vsWidth / 2.0f));
	}

	private float calculateBottomCollisionOffset() {
		return Math.abs(bbCenterY - (bbHeight / 2.0f) + (vsHeight / 2.0f));
	}

	private float calculateTopCollisionOffset() {
		return Math.abs(bbCenterY + (bbHeight / 2.0f) - (vsHeight / 2.0f));
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPhysicsWorld(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}
}
