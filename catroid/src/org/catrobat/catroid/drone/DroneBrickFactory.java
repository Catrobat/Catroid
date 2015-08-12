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
package org.catrobat.catroid.drone;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneLandBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;

public final class DroneBrickFactory {

	private DroneBrickFactory() {
	}

	public static enum DroneBricks {
		DRONE_TAKE_OFF_BRICK, DRONE_PLAY_LED_ANIMATION_BRICK, DRONE_LAND_BRICK, DRONE_FLIP_BRICK, DRONE_MOVE_DOWN_BRICK, DRONE_MOVE_UP_BRICK, DRONE_MOVE_LEFT_BRICK, DRONE_MOVE_RIGHT_BRICK, DRONE_MOVE_BACKWARD_BRICK, DRONE_MOVE_FORWARD_BRICK, DRONE_TURN_RIGHT_BRICK, DRONE_TURN_LEFT_BRICK,
	}

	public static BrickBaseType getInstanceOfDroneBrick(DroneBricks brick, Sprite sprite, int timeInMilliseconds,
			int powerInPercent) {

		switch (brick) {
			case DRONE_TAKE_OFF_BRICK:
				return new DroneTakeOffBrick();

			case DRONE_FLIP_BRICK:
				return new DroneFlipBrick();

			case DRONE_PLAY_LED_ANIMATION_BRICK:
				return new DronePlayLedAnimationBrick();

			case DRONE_LAND_BRICK:
				return new DroneLandBrick();

			case DRONE_MOVE_DOWN_BRICK:
				return new DroneMoveDownBrick(timeInMilliseconds, powerInPercent);

			case DRONE_MOVE_UP_BRICK:
				return new DroneMoveUpBrick(timeInMilliseconds, powerInPercent);

			case DRONE_MOVE_FORWARD_BRICK:
				return new DroneMoveForwardBrick(timeInMilliseconds, powerInPercent);

			case DRONE_MOVE_BACKWARD_BRICK:
				return new DroneMoveBackwardBrick(timeInMilliseconds, powerInPercent);

			case DRONE_MOVE_LEFT_BRICK:
				return new DroneMoveLeftBrick(timeInMilliseconds, powerInPercent);

			case DRONE_MOVE_RIGHT_BRICK:
				return new DroneMoveRightBrick(timeInMilliseconds, powerInPercent);

			case DRONE_TURN_LEFT_BRICK:
				return new DroneTurnLeftBrick(timeInMilliseconds, powerInPercent);

			case DRONE_TURN_RIGHT_BRICK:
				return new DroneTurnRightBrick(timeInMilliseconds, powerInPercent);

			default:
				return null;
		}
	}
}
