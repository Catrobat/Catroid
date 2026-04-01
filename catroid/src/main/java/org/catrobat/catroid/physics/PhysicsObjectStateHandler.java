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

import java.util.LinkedList;

public class PhysicsObjectStateHandler {

	private PhysicsLook physicsLook;
	private PhysicsObject physicsObject;

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

	PhysicsObjectStateHandler(PhysicsLook physicsLook, PhysicsObject physicsObject) {
		this.physicsLook = physicsLook;
		this.physicsObject = physicsObject;

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
				return !physicsLook.isLookVisible();
			}
		};

		transparencyCondition = new PhysicsObjectStateCondition() {
			@Override
			public boolean isTrue() {
				return physicsLook.getAlpha() == 0.0;
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

	boolean checkHangup(boolean record) {
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
		updateRotation();
	}

	public void updateRotation() {
		// Needs to be set for all styles except ALL_AROUND, since the rotation would be
		// off otherwise
		boolean rotationCondition =
				!(physicsLook.getRotationMode() == PhysicsLook.ROTATION_STYLE_ALL_AROUND);
		physicsObject.setFixedRotation(rotationCondition);
	}

	public void activateGlideTo() {
		if (!glideToIsActive) {
			glideToIsActive = true;
			physicsLook.updatePhysicsObjectState(true);
		}
	}

	public void deactivateGlideTo() {
		glideToIsActive = false;
		physicsLook.updatePhysicsObjectState(true);
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

	private interface PhysicsObjectStateCondition {
		boolean isTrue();
	}
}
