/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.drone;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.formulaeditor.Formula;
import org.mockito.Mockito;

public class DroneTests extends InstrumentationTestCase {

	public DroneControlService dcs;
	public TemporalAction action;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		//Workaround for Android 4.4 Devices
		//https://code.google.com/p/dexmaker/issues/detail?id=2
		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

		dcs = Mockito.mock(DroneControlService.class);
		DroneServiceWrapper.getInstance().setDroneService(dcs);
	}

	public void testFlip() {
		action = ExtendedActions.droneFlip();
		action.act(0.0f);
		Mockito.verify(dcs, Mockito.atLeast(1)).doLeftFlip();
	}

	public void testPlayLedAnimation() {
		action = ExtendedActions.dronePlayLedAnimation();
		action.act(0.0f);
		Mockito.verify(dcs, Mockito.atLeast(1)).playLedAnimation(5.0f, 3, 3);
	}

	public void testTakeOff() {
		action = ExtendedActions.droneTakeOff();
		action.act(0.0f);
		Mockito.verify(dcs, Mockito.atLeast(1)).triggerTakeOff();
	}

	public void testLand() {
		action = ExtendedActions.droneLand();
		action.act(0.0f);
		Mockito.verify(dcs, Mockito.atLeast(1)).triggerTakeOff();
	}

	public void testMoveUp() {
		Formula formula = new Formula(0.2 * 100);
		Formula seconds = new Formula(2);
		Sprite sprite = new Sprite("TestSprite");

		action = ExtendedActions.droneMoveUp(sprite, seconds, formula);
		action.act(2);

		Mockito.verify(dcs, Mockito.atLeast(1)).moveUp(0.2f);
		Mockito.verify(dcs, Mockito.atLeast(1)).moveUp(0);
	}

	public void testMoveDown() {
		Formula formula = new Formula(0.2 * 100);
		Formula seconds = new Formula(2);
		Sprite sprite = new Sprite("TestSprite");

		action = ExtendedActions.droneMoveDown(sprite, seconds, formula);
		action.act(2);

		Mockito.verify(dcs, Mockito.atLeast(1)).moveDown(0.2f);
		Mockito.verify(dcs, Mockito.atLeast(1)).moveDown(0);
	}

	public void testMoveLeft() {
		Formula formula = new Formula(0.2 * 100);
		Formula seconds = new Formula(2);
		Sprite sprite = new Sprite("TestSprite");

		action = ExtendedActions.droneMoveLeft(sprite, seconds, formula);
		action.act(2);

		Mockito.verify(dcs, Mockito.atLeast(1)).moveLeft(0.2f);
		Mockito.verify(dcs, Mockito.atLeast(1)).moveLeft(0);
	}

	public void testMoveRight() {
		Formula formula = new Formula(0.2 * 100);
		Formula seconds = new Formula(2);
		Sprite sprite = new Sprite("TestSprite");

		action = ExtendedActions.droneMoveRight(sprite, seconds, formula);
		action.act(2);

		Mockito.verify(dcs, Mockito.atLeast(1)).moveRight(0.2f);
		Mockito.verify(dcs, Mockito.atLeast(1)).moveRight(0);
	}

	public void testMoveForward() {
		Formula formula = new Formula(0.2 * 100);
		Formula seconds = new Formula(2);
		Sprite sprite = new Sprite("TestSprite");

		action = ExtendedActions.droneMoveForward(sprite, seconds, formula);
		action.act(2);

		Mockito.verify(dcs, Mockito.atLeast(1)).moveForward(0.2f);
		Mockito.verify(dcs, Mockito.atLeast(1)).moveForward(0);
	}

	public void testMoveBackward() {
		Formula formula = new Formula(0.2 * 100);
		Formula seconds = new Formula(2);
		Sprite sprite = new Sprite("TestSprite");

		action = ExtendedActions.droneMoveBackward(sprite, seconds, formula);
		action.act(2);

		Mockito.verify(dcs, Mockito.atLeast(1)).moveBackward(0.2f);
		Mockito.verify(dcs, Mockito.atLeast(1)).moveBackward(0);
	}
}
