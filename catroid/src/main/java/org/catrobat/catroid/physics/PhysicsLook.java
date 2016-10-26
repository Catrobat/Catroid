/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import java.util.LinkedList;

public class PhysicsLook extends Look {

	public static final float SCALE_FACTOR_ACCURACY = 10000.0f;

	private final PhysicsObject physicsObject;
	private final PhysicsObjectStateHandler physicsObjectStateHandler = new PhysicsObjectStateHandler();

	public PhysicsLook(Sprite sprite, PhysicsWorld physicsWorld) {
		super(sprite);
		physicsObject = physicsWorld.getPhysicsObject(sprite);
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
		PhysicsWorld physicsWorld = ProjectManager.getInstance().getSceneToPlay().getPhysicsWorld();
		physicsWorld.changeLook(physicsObject, this);
		updatePhysicsObjectState(true);
	}

	@Override
	public void setXInUserInterfaceDimensionUnit(float x) {
		setX(x - getWidth() / 2f);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		if (null != physicsObject) {
			physicsObject.setX(x + getWidth() / 2.0f);
			physicsObject.setY(y + getHeight() / 2.0f);
		}
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		if (null != physicsObject) {
			physicsObject.setX(x + getWidth() / 2.0f);
		}
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		if (null != physicsObject) {
			physicsObject.setY(y + getHeight() / 2.0f);
		}
	}

	@Override
	public float getAngularVelocityInUserInterfaceDimensionUnit() {
		return physicsObject.getRotationSpeed();
	}

	@Override
	public float getXVelocityInUserInterfaceDimensionUnit() {
		return physicsObject.getVelocity().x;
	}

	@Override
	public float getYVelocityInUserInterfaceDimensionUnit() {
		return physicsObject.getVelocity().y;
	}

	@Override
	public float getX() {
		float x = physicsObject.getX() - getWidth() / 2.0f;
		super.setX(x);
		return x;
	}

	@Override
	public float getY() {
		float y = physicsObject.getY() - getHeight() / 2.0f;
		super.setY(y);
		return y;
	}

	@Override
	public float getRotation() {
		super.setRotation((physicsObject.getDirection() % 360));

		float rotation = super.getRotation();
		float realRotation = physicsObject.getDirection() % 360;
		if (realRotation < 0) {
			realRotation += 360;
		}

		switch (super.getRotationMode()) {
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				super.setRotation(0f);
				boolean orientedRight = realRotation > 180 || realRotation == 0;
				boolean orientedLeft = realRotation <= 180 && realRotation != 0;
				if (((isFlipped() && orientedRight) || (!isFlipped() && orientedLeft)) && lookData != null) {
					lookData.getTextureRegion().flip(true, false);
				}
				break;
			case ROTATION_STYLE_ALL_AROUND:
				super.setRotation(rotation);
				break;
			case ROTATION_STYLE_NONE:
				super.setRotation(0f);
				break;
		}

		return super.getRotation();
	}

	@Override
	public void setRotation(float degrees) {
		super.setRotation(degrees);
		if (null != physicsObject) {
			physicsObject.setDirection(super.getRotation() % 360);
		}
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
		if (scaleXComp == Math.round(oldScales.x * SCALE_FACTOR_ACCURACY) && scaleYComp == Math.round(oldScales.y * SCALE_FACTOR_ACCURACY)) {
			return;
		}

		super.setScale(scaleX, scaleY);

		if (physicsObject != null) {
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getSceneToPlay().getPhysicsWorld();
			physicsWorld.changeLook(physicsObject, this);
			updatePhysicsObjectState(true);
		}
	}

	public void updatePhysicsObjectState(boolean record) {
		physicsObjectStateHandler.update(record);
	}

	@Override
	public void setLookVisible(boolean visible) {
		super.setLookVisible(visible);
		physicsObjectStateHandler.update(true);
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

	private interface PhysicsObjectStateCondition {
		boolean isTrue();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		physicsObjectStateHandler.checkHangup(true);
		super.draw(batch, parentAlpha);
	}

	private class PhysicsObjectStateHandler {

		private LinkedList<PhysicsObjectStateCondition> hangupConditions = new LinkedList<>();
		private LinkedList<PhysicsObjectStateCondition> nonCollidingConditions = new LinkedList<>();
		private LinkedList<PhysicsObjectStateCondition> fixConditions = new LinkedList<>();

		private PhysicsObjectStateCondition positionCondition;
		private PhysicsObjectStateCondition visibleCondition;
		private PhysicsObjectStateCondition transparencyCondition;
		private PhysicsObjectStateCondition glideToCondition;

		private boolean glideToIsActive = false;
		private boolean hangedUp = false;
		private boolean fixed = false;
		private boolean nonColliding = false;

		public PhysicsObjectStateHandler() {

			positionCondition = new PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return isOutsideActiveArea();
				}

				private boolean isOutsideActiveArea() {
					return isXOutsideActiveArea() || isYOutsideActiveArea();
				}

				private boolean isXOutsideActiveArea() {
					return Math.abs(PhysicsWorldConverter.convertBox2dToNormalCoordinate(physicsObject.getMassCenter().x))
							- physicsObject.getCircumference() > PhysicsWorld.activeArea.x / 2.0f;
				}

				private boolean isYOutsideActiveArea() {
					return Math.abs(PhysicsWorldConverter.convertBox2dToNormalCoordinate(physicsObject.getMassCenter().y))
							- physicsObject.getCircumference() > PhysicsWorld.activeArea.y / 2.0f;
				}
			};

			visibleCondition = new PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return !isLookVisible();
				}
			};

			transparencyCondition = new PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return alpha == 0.0;
				}
			};

			glideToCondition = new PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return glideToIsActive;
				}
			};

			hangupConditions.add(transparencyCondition);
			hangupConditions.add(positionCondition);
			hangupConditions.add(visibleCondition);
			hangupConditions.add(glideToCondition);

			nonCollidingConditions.add(transparencyCondition);
			nonCollidingConditions.add(positionCondition);
			nonCollidingConditions.add(visibleCondition);

			fixConditions.add(glideToCondition);
		}

		private boolean checkHangup(boolean record) {
			boolean shouldBeHangedUp = false;
			for (PhysicsObjectStateCondition hangupCondition : hangupConditions) {
				if (hangupCondition.isTrue()) {
					shouldBeHangedUp = true;
					break;
				}
			}
			boolean deactivateHangup = hangedUp && !shouldBeHangedUp;
			boolean activateHangup = !hangedUp && shouldBeHangedUp;
			if (deactivateHangup) {
				physicsObject.deactivateHangup(record);
			} else if (activateHangup) {
				physicsObject.activateHangup();
			}
			hangedUp = shouldBeHangedUp;
			return hangedUp;
		}

		private boolean checkNonColliding(boolean record) {
			boolean shouldBeNonColliding = false;
			for (PhysicsObjectStateCondition nonCollideCondition : nonCollidingConditions) {
				if (nonCollideCondition.isTrue()) {
					shouldBeNonColliding = true;
					break;
				}
			}
			boolean deactivateNonColliding = nonColliding && !shouldBeNonColliding;
			boolean activateNonColliding = !nonColliding && shouldBeNonColliding;
			if (deactivateNonColliding) {
				physicsObject.deactivateNonColliding(record, false);
			} else if (activateNonColliding) {
				physicsObject.activateNonColliding(false);
			}
			nonColliding = shouldBeNonColliding;
			return nonColliding;
		}

		private boolean checkFixed(boolean record) {
			boolean shouldBeFixed = false;
			for (PhysicsObjectStateCondition fixedCondition : fixConditions) {
				if (fixedCondition.isTrue()) {
					shouldBeFixed = true;
					break;
				}
			}
			boolean deactivateFix = fixed && !shouldBeFixed;
			boolean activateFix = !fixed && shouldBeFixed;
			if (deactivateFix) {
				physicsObject.deactivateFixed(record);
			} else if (activateFix) {
				physicsObject.activateFixed();
			}
			fixed = shouldBeFixed;
			return fixed;
		}

		public void update(boolean record) {
			checkHangup(record);
			checkNonColliding(record);
			checkFixed(record);
		}

		public void activateGlideTo() {
			if (!glideToIsActive) {
				glideToIsActive = true;
				updatePhysicsObjectState(true);
			}
		}

		public void deactivateGlideTo() {
			glideToIsActive = false;
			updatePhysicsObjectState(true);
		}

		public boolean isHangedUp() {
			return hangedUp;
		}

		public void setNonColliding(boolean nonColliding) {
			if (this.nonColliding != nonColliding) {
				this.nonColliding = nonColliding;
				update(true);
			}
		}
	}
}
