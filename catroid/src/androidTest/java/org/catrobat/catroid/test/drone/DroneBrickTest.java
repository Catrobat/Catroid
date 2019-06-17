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
package org.catrobat.catroid.test.drone;

import com.parrot.freeflight.drone.DroneProxy;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
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
				{"DRONE_TAKE_OFF_LAND_BRICK", new DroneTakeOffLandBrick()},
				{"DRONE_FLIP_BRICK", new DroneFlipBrick()},
				{"DRONE_PLAY_LED_ANIMATION_BRICK", new DronePlayLedAnimationBrick(DroneProxy.ARDRONE_LED_ANIMATION.ARDRONE_LED_ANIMATION_BLINK_GREEN_RED)},
				{"DRONE_GO_EMERGENCY", new DroneEmergencyBrick()},
				{"DRONE_MOVE_DOWN_BRICK", new DroneMoveDownBrick()},
				{"DRONE_MOVE_UP_BRICK", new DroneMoveUpBrick()},
				{"DRONE_MOVE_LEFT_BRICK", new DroneMoveLeftBrick()},
				{"DRONE_MOVE_RIGHT_BRICK", new DroneMoveRightBrick()},
				{"DRONE_MOVE_BACKWARD_BRICK", new DroneMoveBackwardBrick()},
				{"DRONE_MOVE_FORWARD_BRICK", new DroneMoveForwardBrick()},
				{"DRONE_TURN_RIGHT_BRICK", new DroneTurnRightBrick()},
				{"DRONE_TURN_LEFT_BRICK", new DroneTurnLeftBrick()},
				{"DRONE_SWITCH_CAMERA_BRICK", new DroneSwitchCameraBrick()}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Brick droneBrick;

	@Test
	public void testDroneResources() {
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		droneBrick.addRequiredResources(resourcesSet);
		assertTrue(resourcesSet.contains(Brick.ARDRONE_SUPPORT));
	}
}
