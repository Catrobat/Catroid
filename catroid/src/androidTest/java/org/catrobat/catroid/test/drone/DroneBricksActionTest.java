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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Array;
import com.parrot.freeflight.drone.DroneConfig;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class DroneBricksActionTest {

	private DroneControlService droneControlService;
	private DroneConfig droneConfig;

	public TemporalAction action;
	private Sprite sprite;
	private ScriptSequenceAction sequenceAction;
	private int powerInPercent;
	private int durationInSeconds;

	public DroneBricksActionTest() {
		powerInPercent = 20;
		durationInSeconds = 2;
	}

	@Before
	public void setUp() throws Exception {
		droneControlService = Mockito.mock(DroneControlService.class);
		droneConfig = Mockito.mock(DroneConfig.class);
		when(droneControlService.getDroneConfig()).thenReturn(droneConfig);

		DroneServiceWrapper.setDroneService(droneControlService);
		sprite = new Sprite("droneTestSprite");
		sequenceAction = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(new StartScript());
	}

	private void addActionToSequenceAndAct(BrickBaseType brick) {
		addActionToSequenceAndAct(brick, 0.0f);
	}

	private void addActionToSequenceAndAct(BrickBaseType brick, float actDuration) {
		brick.addActionToSequence(sprite, sequenceAction);
		Array<Action> actionArray = sequenceAction.getActions();
		action = (TemporalAction) actionArray.get(0);
		action.act(actDuration);
	}

	@Test
	public void testTakeOff() {
		addActionToSequenceAndAct(new DroneTakeOffLandBrick());
		Mockito.verify(droneControlService, Mockito.atLeast(1)).triggerTakeOff();
	}

	@Test
	public void testLand() {
		addActionToSequenceAndAct(new DroneTakeOffLandBrick());
		Mockito.verify(droneControlService, Mockito.atLeast(1)).triggerTakeOff();
	}

	@Test
	public void testMoveUp() {
		DroneMoveUpBrick moveUpBrick = new DroneMoveUpBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveUpBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveUp(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveUp(0);
	}

	@Test
	public void testMoveDown() {
		DroneMoveDownBrick moveDownBrick = new DroneMoveDownBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveDownBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveDown(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveDown(0);
	}

	@Test
	public void testMoveLeft() {
		DroneMoveLeftBrick moveLeftBrick = new DroneMoveLeftBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveLeftBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveLeft(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveLeft(0);
	}

	@Test
	public void testMoveRight() {
		DroneMoveRightBrick moveRightBrick = new DroneMoveRightBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveRightBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveRight(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveRight(0);
	}

	@Test
	public void testMoveForward() {
		DroneMoveForwardBrick moveForwardBrick = new DroneMoveForwardBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveForwardBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveForward(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveForward(0);
	}

	@Test
	public void testMoveBackward() {
		DroneMoveBackwardBrick moveBackwardBrick = new DroneMoveBackwardBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveBackwardBrick, 2);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveBackward(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveBackward(0);
	}

	@Test
	public void testTurnLeft() {
		DroneTurnLeftBrick turnLeftBrick = new DroneTurnLeftBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(turnLeftBrick, 2);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnLeft(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnLeft(0);
	}

	@Test
	public void testTurnRight() {
		DroneTurnRightBrick turnRightBrick = new DroneTurnRightBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(turnRightBrick, 2);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnRight(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnRight(0);
	}

	@Test
	public void testSwitch() {
		DroneSwitchCameraBrick switchBrick = new DroneSwitchCameraBrick();
		addActionToSequenceAndAct(switchBrick);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).switchCamera();
	}
}
