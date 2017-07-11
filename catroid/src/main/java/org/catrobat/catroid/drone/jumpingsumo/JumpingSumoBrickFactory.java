/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick;

public final class JumpingSumoBrickFactory {

	private JumpingSumoBrickFactory() {
	}

	public enum JumpingSumoBricks {
		JUMPING_SUMO_FORWARD, JUMPING_SUMO_BACKWARD, JUMPING_SUMO_ANIMATIONS, JUMPING_SUMO_SOUND, JUMPING_SUMO_NO_SOUND,
		JUMPING_SUMO_JUMP_LONG, JUMPING_SUMO_JUMP_HIGH
	}

	public static BrickBaseType getInstanceOfJumpingSumoBrick(JumpingSumoBricks brick, int timeInMilliseconds,
														byte powerInPercent, byte volumeInPercent) {
		switch (brick) {
			case JUMPING_SUMO_FORWARD:
				return new JumpingSumoMoveForwardBrick(timeInMilliseconds, powerInPercent);
			case JUMPING_SUMO_BACKWARD:
				return new JumpingSumoMoveBackwardBrick(timeInMilliseconds, powerInPercent);
			case JUMPING_SUMO_ANIMATIONS:
				return new JumpingSumoAnimationsBrick(JumpingSumoAnimationsBrick.Animation.SPIN);
			case JUMPING_SUMO_SOUND:
				return new JumpingSumoSoundBrick(JumpingSumoSoundBrick.Sounds.DEFAULT, volumeInPercent);
			case JUMPING_SUMO_NO_SOUND:
				return new JumpingSumoNoSoundBrick();
			case JUMPING_SUMO_JUMP_HIGH:
				return new JumpingSumoJumpLongBrick();
			case JUMPING_SUMO_JUMP_LONG:
				return new JumpingSumoJumpHighBrick();
			default:
				return null;
		}
	}
}
