/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;

public class PhysicsLook extends Look {

	public static final float SCALE_FACTOR_ACCURACY = 10000.0f;
	private static final int FULL_CIRCLE_DEGREE = 360;
	private static final int HALF_CIRCLE_DEGREE = 180;

	private final PhysicsObject physicsObject;
	private final PhysicsObjectStateHandler physicsObjectStateHandler;

	private boolean isFlippedByAction = false;

	public PhysicsLook(Sprite sprite, PhysicsWorld physicsWorld) {
		super(sprite);
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObjectStateHandler = new PhysicsObjectStateHandler(this, physicsObject);
	}

	@Override
	public void copyTo(final Look destination) {
		super.copyTo(destination);
		if (destination instanceof PhysicsLook) {
			this.physicsObject.copyTo(((PhysicsLook) destination).physicsObject);
		}
	}

	@Override
	public void setTransparencyInUserInterfaceDimensionUnit(float percent) {
		super.setTransparencyInUserInterfaceDimensionUnit(percent);
		updatePhysicsObjectState(true);
	}

	@Override
	public void setLookData(LookData lookData) {
		super.setLookData(lookData);
		PhysicsWorld physicsWorld = ProjectManager.getInstance().getCurrentlyPlayingScene().getPhysicsWorld();
		physicsWorld.changeLook(physicsObject, this);
		updatePhysicsObjectState(true);
	}

	@Override
	public void setXInUserInterfaceDimensionUnit(float x) {
		setX(applyCenterOffset(x, true, false));
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		if (null != physicsObject) {
			physicsObject.setX(applyCenterOffset(x, true, true));
			physicsObject.setY(applyCenterOffset(y, false, true));
		}
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		if (null != physicsObject) {
			physicsObject.setX(applyCenterOffset(x, true, true));
		}
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		if (null != physicsObject) {
			physicsObject.setY(applyCenterOffset(y, false, true));
		}
	}

	@Override
	public float getAngularVelocityInUserInterfaceDimensionUnit() {
		return physicsObject.getRotationSpeed();
	}

	@Override
	public float getXVelocityInUserInterfaceDimensionUnit() {
		if (sprite.isGliding()) {
			return sprite.getGlidingVelocityX();
		}
		return physicsObject.getVelocity().x;
	}

	@Override
	public float getYVelocityInUserInterfaceDimensionUnit() {
		if (sprite.isGliding()) {
			return sprite.getGlidingVelocityY();
		}
		return physicsObject.getVelocity().y;
	}

	@Override
	public float getMotionDirectionInUserInterfaceDimensionUnit() {
		if (physicsObject == null || physicsObject.getVelocity() == null || !isLookMoving()) {
			return super.getMotionDirectionInUserInterfaceDimensionUnit();
		}
		float motionDirection = DEGREE_UI_OFFSET - (float) Math.toDegrees(
				Math.atan2(physicsObject.getVelocity().y, physicsObject.getVelocity().x));
		return breakDownCatroidAngle(motionDirection);
	}

	@Override
	public float getLookDirectionInUserInterfaceDimensionUnit() {
		float direction = 0f;
		switch (getRotationMode()) {
			case ROTATION_STYLE_NONE:
				direction = DEGREE_UI_OFFSET;
				break;
			case ROTATION_STYLE_ALL_AROUND:
				direction = convertStageAngleToCatroidAngle(getRotation());
				break;
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				direction = isFlipped() ? -DEGREE_UI_OFFSET : DEGREE_UI_OFFSET;
				break;
		}
		return direction;
	}

	@Override
	public float getX() {
		float x = applyCenterOffset(physicsObject.getX(), true, false);
		super.setX(x);
		return x;
	}

	@Override
	public float getY() {
		float y = applyCenterOffset(physicsObject.getY(), false, false);
		super.setY(y);
		return y;
	}

	@Override
	public float getRotation() {
		super.setRotation((physicsObject.getDirection() % FULL_CIRCLE_DEGREE));
		float rotation = super.getRotation();
		float realRotation = physicsObject.getDirection() % FULL_CIRCLE_DEGREE;
		if (realRotation < 0) {
			realRotation += FULL_CIRCLE_DEGREE;
		}
		switch (super.getRotationMode()) {
			case ROTATION_STYLE_NONE:
				super.setRotation(0f);
				break;
			case ROTATION_STYLE_ALL_AROUND:
				super.setRotation(rotation);
				break;
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				super.setRotation(0f);
				flipLookDataIfNeeded(realRotation);
				break;
		}
		return super.getRotation();
	}

	@Override
	public void setRotation(float degrees) {
		super.setRotation(degrees);
		if (null != physicsObject) {
			physicsObject.setDirection(super.getRotation() % FULL_CIRCLE_DEGREE);
		}
	}

	@Override
	public void setRotationMode(int mode) {
		super.setRotationMode(mode);
		updatePhysicsObjectState(true);
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		Vector2 oldScales = new Vector2(getScaleX(), getScaleY());
		if (scaleX < 0.0f || scaleY < 0.0f) {
			scaleX = 0.0f;
			scaleY = 0.0f;
		}

		int scaleXComp = Math.round(scaleX * SCALE_FACTOR_ACCURACY);
		int scaleYComp = Math.round(scaleY * SCALE_FACTOR_ACCURACY);

		int oldScaleXComp = Math.round(oldScales.x * SCALE_FACTOR_ACCURACY);
		int oldScaleYComp = Math.round(oldScales.y * SCALE_FACTOR_ACCURACY);
		if (scaleXComp == oldScaleXComp && scaleYComp == oldScaleYComp) {
			return;
		}

		super.setScale(scaleX, scaleY);

		if (physicsObject != null) {
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getCurrentlyPlayingScene().getPhysicsWorld();
			physicsWorld.changeLook(physicsObject, this);
			updatePhysicsObjectState(true);
		}
	}

	@Override
	public void setLookVisible(boolean visible) {
		super.setLookVisible(visible);
		physicsObjectStateHandler.update(true);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		physicsObjectStateHandler.checkHangup(true);
		super.draw(batch, parentAlpha);
	}

	public void updatePhysicsObjectState(boolean record) {
		physicsObjectStateHandler.update(record);
	}

	public boolean isHangedUp() {
		return physicsObjectStateHandler.isHangedUp();
	}

	public void setNonColliding(boolean nonColliding) {
		physicsObjectStateHandler.setNonColliding(nonColliding);
	}

	public void startGlide() {
		physicsObjectStateHandler.activateGlideTo();
	}

	public void stopGlide() {
		physicsObjectStateHandler.deactivateGlideTo();
	}

	public void updateFlippedByAction() {
		isFlippedByAction = !isFlippedByAction;
	}

	public void setFlippedByDegree(float degree) {
		float direction = getMotionDirectionInUserInterfaceDimensionUnit();
		float newDirection = (degree + direction) % FULL_CIRCLE_DEGREE;
		setFlippedByDirection(newDirection);
	}

	public void setFlippedByDirection(float newDirection) {
		newDirection %= FULL_CIRCLE_DEGREE;
		if (newDirection < 0) {
			newDirection += FULL_CIRCLE_DEGREE;
		}
		float direction = getMotionDirectionInUserInterfaceDimensionUnit() - Look.DEGREE_UI_OFFSET;
		if ((direction >= 0 && direction <= HALF_CIRCLE_DEGREE) != (newDirection >= 0 && newDirection <= HALF_CIRCLE_DEGREE)) {
			updateFlippedByAction();
		}
	}

	private boolean isLookMoving() {
		return physicsObject.getVelocity().y != 0.0 || physicsObject.getVelocity().x != 0.0;
	}

	private void flipLookDataIfNeeded(float realRotation) {
		boolean orientedRight = realRotation > HALF_CIRCLE_DEGREE || realRotation == 0;
		boolean orientedLeft = realRotation <= HALF_CIRCLE_DEGREE && realRotation != 0;
		boolean isLookDataFlipped = isFlipped();
		if (isFlippedByAction) {
			isLookDataFlipped = !isLookDataFlipped;
		}
		if (lookData != null && ((isLookDataFlipped && orientedRight) || (!isLookDataFlipped && orientedLeft))) {
			lookData.getTextureRegion().flip(true, false);
		}
	}

	private float applyCenterOffset(float coordinate, boolean isXCoordinate, boolean add) {
		float screenParam = isXCoordinate ? getWidth() : getHeight();
		if (add) {
			return coordinate + screenParam / 2.0f;
		} else {
			return coordinate - screenParam / 2.0f;
		}
	}
}
