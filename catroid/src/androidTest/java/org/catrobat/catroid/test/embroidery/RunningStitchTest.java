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

package org.catrobat.catroid.test.embroidery;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.RunningStitch;
import org.catrobat.catroid.embroidery.RunningStitchType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyFloat;

@RunWith(AndroidJUnit4.class)
public class RunningStitchTest {
	private Sprite sprite;
	private Look spriteLook;
	private RunningStitch runningStitch;
	private RunningStitchType runningStitchType;

	@Before
	public void setUp() {
		spriteLook = Mockito.mock(Look.class);
		sprite = Mockito.mock(Sprite.class);
		sprite.look = spriteLook;
		runningStitchType = Mockito.mock(RunningStitchType.class);
		runningStitch = new RunningStitch();
	}

	@Test
	public void testActivateRunningStitch() {
		runningStitch.activateStitching(sprite, runningStitchType);
		runningStitch.update();

		Mockito.verify(spriteLook, Mockito.times(1)).getXInUserInterfaceDimensionUnit();
		Mockito.verify(spriteLook, Mockito.times(1)).getYInUserInterfaceDimensionUnit();
		Mockito.verify(runningStitchType, Mockito.times(1)).update(anyFloat(), anyFloat());
	}

	@Test
	public void testInvalidRunningTypeActivateRunningStitch() {
		runningStitch.activateStitching(sprite, null);
		runningStitch.update();

		Mockito.verify(spriteLook, Mockito.times(0)).getXInUserInterfaceDimensionUnit();
		Mockito.verify(spriteLook, Mockito.times(0)).getYInUserInterfaceDimensionUnit();
	}

	@Test
	public void testInvalidSpriteActivateRunningStitch() {
		runningStitch.activateStitching(null, runningStitchType);
		runningStitch.update();

		Mockito.verify(runningStitchType, Mockito.times(0)).update(anyFloat(), anyFloat());
	}

	@Test
	public void testPauseRunningStitch() {
		runningStitch.activateStitching(sprite, runningStitchType);
		runningStitch.pause();
		runningStitch.update();

		Mockito.verify(spriteLook, Mockito.times(0)).getXInUserInterfaceDimensionUnit();
		Mockito.verify(spriteLook, Mockito.times(0)).getYInUserInterfaceDimensionUnit();
		Mockito.verify(runningStitchType, Mockito.times(0)).update(anyFloat(), anyFloat());
	}

	@Test
	public void testResumeRunningStitch() {
		runningStitch.activateStitching(sprite, runningStitchType);
		runningStitch.pause();
		runningStitch.resume();
		runningStitch.update();

		Mockito.verify(spriteLook, Mockito.times(1)).getXInUserInterfaceDimensionUnit();
		Mockito.verify(spriteLook, Mockito.times(1)).getYInUserInterfaceDimensionUnit();
		Mockito.verify(runningStitchType, Mockito.times(1)).update(anyFloat(), anyFloat());
	}

	@Test
	public void testInvalidResumeRunningStitch() {
		runningStitch.resume();
		runningStitch.update();

		Mockito.verify(spriteLook, Mockito.times(0)).getXInUserInterfaceDimensionUnit();
		Mockito.verify(spriteLook, Mockito.times(0)).getYInUserInterfaceDimensionUnit();
		Mockito.verify(runningStitchType, Mockito.times(0)).update(anyFloat(), anyFloat());
	}

	@Test
	public void testSetStartCoordinates() {
		final float xCoord = 1;
		final float yCoord = 2;
		runningStitch.activateStitching(sprite, runningStitchType);
		runningStitch.setStartCoordinates(xCoord, yCoord);

		Mockito.verify(runningStitchType, Mockito.times(1)).setStartCoordinates(xCoord, yCoord);
	}

	@Test
	public void testInvalidSetStartCoordinates() {
		final float xCoord = 1;
		final float yCoord = 2;
		runningStitch.setStartCoordinates(xCoord, yCoord);

		Mockito.verify(runningStitchType, Mockito.times(0)).setStartCoordinates(anyFloat(), anyFloat());
	}
}
