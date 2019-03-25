/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
package org.catrobat.catroid.test.drone;

import android.support.annotation.IdRes;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.drone.ardrone.DroneBrickFactory;
import org.catrobat.catroid.drone.ardrone.DroneBrickFactory.DroneBricks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DroneBrickTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"DRONE_TAKE_OFF_LAND_BRICK", DroneBricks.DRONE_TAKE_OFF_LAND_BRICK},
			{"DRONE_FLIP_BRICK", DroneBricks.DRONE_FLIP_BRICK},
			{"DRONE_PLAY_LED_ANIMATION_BRICK", DroneBricks.DRONE_PLAY_LED_ANIMATION_BRICK},
			{"DRONE_GO_EMERGENCY", DroneBricks.DRONE_GO_EMERGENCY},
			{"DRONE_MOVE_DOWN_BRICK", DroneBricks.DRONE_MOVE_DOWN_BRICK},
			{"DRONE_MOVE_UP_BRICK", DroneBricks.DRONE_MOVE_UP_BRICK},
			{"DRONE_MOVE_LEFT_BRICK", DroneBricks.DRONE_MOVE_LEFT_BRICK},
			{"DRONE_MOVE_RIGHT_BRICK", DroneBricks.DRONE_MOVE_RIGHT_BRICK},
			{"DRONE_MOVE_BACKWARD_BRICK", DroneBricks.DRONE_MOVE_BACKWARD_BRICK},
			{"DRONE_MOVE_FORWARD_BRICK", DroneBricks.DRONE_MOVE_FORWARD_BRICK},
			{"DRONE_TURN_RIGHT_BRICK", DroneBricks.DRONE_TURN_RIGHT_BRICK},
			{"DRONE_TURN_LEFT_BRICK", DroneBricks.DRONE_TURN_LEFT_BRICK},
			{"DRONE_SWITCH_CAMERA_BRICK", DroneBricks.DRONE_SWITCH_CAMERA_BRICK}
		});
	}

	@Parameterized.Parameter
	public @IdRes String name;

	@Parameterized.Parameter(1)
	public @IdRes DroneBricks droneBrickIdentifier;

	@Test
	public void testDroneResources() {
		BrickBaseType brickFromFactory = DroneBrickFactory.getInstanceOfDroneBrick(droneBrickIdentifier, 0, 0);
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		brickFromFactory.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.ARDRONE_SUPPORT));
	}
}
