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
package org.catrobat.catroid.test.drone;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Array;
import com.parrot.freeflight.service.DroneControlService;

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
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.formulaeditor.Formula;
import org.mockito.Mockito;

public class DroneBricksActionTests extends InstrumentationTestCase {

	public DroneControlService droneControlService;
	public TemporalAction action;
	Sprite sprite;
	SequenceAction sequenceAction;
	private Formula powerInPercent;
	private Formula durationInSeconds;

	public DroneBricksActionTests() {
		powerInPercent = new Formula(0.2 * 100);
		durationInSeconds = new Formula(2);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		//Workaround for Android 4.4 Devices
		//https://code.google.com/p/dexmaker/issues/detail?id=2
		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

		droneControlService = Mockito.mock(DroneControlService.class);
		DroneServiceWrapper.getInstance().setDroneService(droneControlService);
		sprite = new Sprite(getName());
		sequenceAction = new SequenceAction();
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

	public void testFlip() {
		addActionToSequenceAndAct(new DroneFlipBrick());
		Mockito.verify(droneControlService, Mockito.atLeast(1)).doLeftFlip();
	}

	public void testPlayLedAnimation() {
		addActionToSequenceAndAct(new DronePlayLedAnimationBrick());
		Mockito.verify(droneControlService, Mockito.atLeast(1)).playLedAnimation(5.0f, 3, 3);
	}

	public void testTakeOff() {
		addActionToSequenceAndAct(new DroneTakeOffBrick());
		Mockito.verify(droneControlService, Mockito.atLeast(1)).triggerTakeOff();
	}

	public void testLand() {
		addActionToSequenceAndAct(new DroneLandBrick());
		Mockito.verify(droneControlService, Mockito.atLeast(1)).triggerTakeOff();
	}

	public void testMoveUp() {
		DroneMoveUpBrick moveUpBrick = new DroneMoveUpBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveUpBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveUp(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveUp(0);
	}

	public void testMoveDown() {
		DroneMoveDownBrick moveDownBrick = new DroneMoveDownBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveDownBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveDown(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveDown(0);
	}

	public void testMoveLeft() {
		DroneMoveLeftBrick moveLeftBrick = new DroneMoveLeftBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveLeftBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveLeft(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveLeft(0);
	}

	public void testMoveRight() {
		DroneMoveRightBrick moveRightBrick = new DroneMoveRightBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveRightBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveRight(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveRight(0);
	}

	public void testMoveForward() {
		DroneMoveForwardBrick moveForwardBrick = new DroneMoveForwardBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveForwardBrick, 2);

		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveForward(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveForward(0);
	}

	public void testMoveBackward() {
		DroneMoveBackwardBrick moveBackwardBrick = new DroneMoveBackwardBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(moveBackwardBrick, 2);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveBackward(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).moveBackward(0);
	}

	public void testTurnLeft() {
		DroneTurnLeftBrick turnLeftBrick = new DroneTurnLeftBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(turnLeftBrick, 2);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnLeft(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnLeft(0);
	}

	public void testTurnRight() {
		DroneTurnRightBrick turnRightBrick = new DroneTurnRightBrick(durationInSeconds, powerInPercent);

		addActionToSequenceAndAct(turnRightBrick, 2);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnRight(0.2f);
		Mockito.verify(droneControlService, Mockito.atLeast(1)).turnRight(0);
	}
}
