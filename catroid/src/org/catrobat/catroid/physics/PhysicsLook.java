/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;

import java.util.LinkedList;

public class PhysicsLook extends Look {

	private final transient PhysicsObject physicsObject;
	private boolean isVisible;

	private PhysicsObjectHangupState hangupState = new PhysicsObjectHangupState();

	public PhysicsLook(Sprite sprite, PhysicsWorld physicsWorld) {
		super(sprite);
		isVisible = true;
		physicsObject = physicsWorld.getPhysicsObject(sprite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		isVisible = visible;
		updateHangupState(true);
	}

	@Override
	public void setLookData(LookData lookData) {
		super.setLookData(lookData);
		PhysicsWorld physicsWorld = ProjectManager.getInstance().getCurrentProject().getPhysicsWorld();
		physicsWorld.changeLook(physicsObject, this);
	}

	@Override
	public void setXInUserInterfaceDimensionUnit(float x) {
		setX(x - getWidth() / 2f);
	}

	@Override
	public void setX(float x) {
		if (null != physicsObject) {
			physicsObject.setX(x + getWidth() / 2.0f);
		}
	}

	@Override
	public void setY(float y) {
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
		updateHangupState(false);
		return x;
	}

	@Override
	public float getY() {
		float y = physicsObject.getY() - getHeight() / 2.0f;
		updateHangupState(false);
		return y;
	}

	@Override
	public float getRotation() {
		super.setRotation((physicsObject.getDirection() % 360));
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
		super.setScale(scaleX, scaleY);
		if (null != physicsObject) {
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getCurrentProject().getPhysicsWorld();
			physicsWorld.changeLook(physicsObject, this);
		}
	}

	public void updateHangupState(boolean record) {
		hangupState.update(record);
	}

	public void startGlide() {
		hangupState.activateGlideToHangup();
	}

	public void stopGlide() {
		hangupState.deactivateGlideToHangup();
	}

	private interface HangupCondition {
		boolean isTrue();
	}

	public boolean isActive() {
		updateHangupState(false);
		return !hangupState.isHangedUp();
	}

	private class PhysicsObjectHangupState {
		//private final String TAG = PhysicsObjectHangupState.class.getSimpleName();

		private LinkedList<HangupCondition> hangupConditions = new LinkedList<HangupCondition>();

		private HangupCondition positionCondition;
		private HangupCondition visibleCondition;
		private HangupCondition transparancyCondition;
		private HangupCondition glideToCondition;

		private boolean hangedUp = false;
		private boolean glideToIsActive = false;

		public PhysicsObjectHangupState() {
			positionCondition = new HangupCondition() {
				@Override
				public boolean isTrue() {
					Vector2 bbLowerEdge = new Vector2();
					Vector2 bbUpperEdge = new Vector2();
					physicsObject.getBoundaryBox(bbLowerEdge, bbUpperEdge);
					float bbWidth = bbUpperEdge.x - bbLowerEdge.x;
					float bbHeight = bbUpperEdge.y - bbLowerEdge.y;
					float bbCenterX = bbLowerEdge.x + bbWidth / 2;
					float bbCenterY = bbLowerEdge.y + bbHeight / 2;
					//Log.d(TAG, "bbCenterX: " + bbCenterX);
					//Log.d(TAG, "bbCenterY: " + bbCenterY);
					//Log.d(TAG, "PhysicsWorld.activeArea.x: " + PhysicsWorld.activeArea.x);
					//Log.d(TAG, "PhysicsWorld.activeArea.y: " + PhysicsWorld.activeArea.y);
					boolean isXinActiveArea = !(Math.abs(bbCenterX) > PhysicsWorld.activeArea.x);
					boolean isYinActiveArea = !(Math.abs(bbCenterY) > PhysicsWorld.activeArea.y);
					if (isXinActiveArea && isYinActiveArea) {
						//Log.d(TAG, "positionCondition: FALSE");
						return false;
					}
					//Log.d(TAG, "positionCondition: TRUE");
					return true;
				}
			};

			visibleCondition = new HangupCondition() {
				@Override
				public boolean isTrue() {
					//Log.d(TAG, "visibleCondition:"+!isVisible);
					return !isVisible;
				}
			};

			transparancyCondition = new HangupCondition() {
				@Override
				public boolean isTrue() {
					//Log.d(TAG, "transparancyCondition:"+(alpha==0.0));
					return alpha == 0.0;
				}
			};

			glideToCondition = new HangupCondition() {
				@Override
				public boolean isTrue() {
					//Log.d(TAG, "glideToCondition:"+glideToIsActive);
					return glideToIsActive;
				}
			};

			hangupConditions.add(transparancyCondition);
			hangupConditions.add(positionCondition);
			hangupConditions.add(visibleCondition);
			hangupConditions.add(glideToCondition);
		}


		public void update(boolean record) {
			boolean shouldBeHangedUp = false;
			for (HangupCondition hangupCondition : hangupConditions) {
				if (hangupCondition.isTrue()) {
					shouldBeHangedUp = true;
					break;
				}
			}
			boolean resume = hangedUp && !shouldBeHangedUp;
			boolean hangup = !hangedUp && shouldBeHangedUp;
			if (resume) {
				//Log.d(TAG, "HangupState.hangedUp = false; recoveryModeActive = " + String.valueOf(record));
				physicsObject.resume(record);
			} else if (hangup) {
				//Log.d(TAG, "HangupState.hangedUp = true");
				physicsObject.hangup();
			}
			hangedUp = shouldBeHangedUp;
		}

		public void activateGlideToHangup() {
			if (!glideToIsActive) {
				glideToIsActive = true;
				updateHangupState(true);
			}
		}

		public void deactivateGlideToHangup() {
			glideToIsActive = false;
			updateHangupState(true);
		}

		public boolean isHangedUp() {
			return hangedUp;
		}
	}
}
