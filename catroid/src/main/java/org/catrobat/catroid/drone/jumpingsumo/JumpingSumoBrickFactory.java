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
package org.catrobat.catroid.drone.jumpingsumo;

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoShowBatteryStatusBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick;

public final class JumpingSumoBrickFactory {

	private JumpingSumoBrickFactory() {
	}

	public enum JumpingSumoBricks {
		JUMPING_SUMO_TURN, JUMPING_SUMO_FORWARD, JUMPING_SUMO_BACKWARD, JUMPING_SUMO_ROTATE_LEFT, JUMPING_SUMO_ROTATE_RIGHT,
		JUMPING_SUMO_JUMP_LONG, JUMPING_SUMO_JUMP_HIGH, JUMPING_SUMO_SHOW_BATTERY_STATUS, JUMPING_SUMO_ANIMATIONS, JUMPING_SUMO_SOUND,
		JUMPING_SUMO_RECORD, JUMPING_SUMO_PICTURE
	}

	public static BrickBaseType getInstanceOfJumpingSumoBrick(JumpingSumoBricks brick, int timeInMilliseconds,
														byte powerInPercent, byte volumeInPercent, float degree, int xPosition, int
			yPosition) {

		switch (brick) {
			case JUMPING_SUMO_TURN:
				return new JumpingSumoTurnBrick();
			case JUMPING_SUMO_FORWARD:
				return new JumpingSumoMoveForwardBrick(timeInMilliseconds, powerInPercent);
			case JUMPING_SUMO_BACKWARD:
				return new JumpingSumoMoveBackwardBrick(timeInMilliseconds, powerInPercent);
			case JUMPING_SUMO_ROTATE_LEFT:
				return new JumpingSumoRotateLeftBrick(degree);
			case JUMPING_SUMO_ROTATE_RIGHT:
				return new JumpingSumoRotateRightBrick(degree);
			case JUMPING_SUMO_JUMP_HIGH:
				return new JumpingSumoJumpLongBrick();
			case JUMPING_SUMO_JUMP_LONG:
				return new JumpingSumoJumpHighBrick();
			case JUMPING_SUMO_SHOW_BATTERY_STATUS:
				return new JumpingSumoShowBatteryStatusBrick(xPosition, yPosition);
			case JUMPING_SUMO_ANIMATIONS:
				return new JumpingSumoAnimationsBrick(JumpingSumoAnimationsBrick.Animation.SPIN);
			case JUMPING_SUMO_SOUND:
				return new JumpingSumoSoundBrick(JumpingSumoSoundBrick.Sounds.DEFAULT, volumeInPercent);
			case JUMPING_SUMO_RECORD:
				return new JumpingSumoAnimationsBrick(JumpingSumoAnimationsBrick.Animation.SPIN);
			case JUMPING_SUMO_PICTURE:
				return new JumpingSumoAnimationsBrick(JumpingSumoAnimationsBrick.Animation.SPIN);
			default:
				return null;
		}
	}
}
