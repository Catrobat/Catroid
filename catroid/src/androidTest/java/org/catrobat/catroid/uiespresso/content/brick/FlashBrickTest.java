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

package org.catrobat.catroid.uiespresso.content.brick;

import android.os.SystemClock;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.FlashBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uitest.util.SensorTestServerConnection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfSpinnerOnBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.clickSelectCheckSpinnerValueOnBrick;

public class FlashBrickTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		Script script = BrickTestUtils.createProjectAndGetStartScript("flashBrickTest");
		script.addBrick(new FlashBrick());
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testFlashBrick() {
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_flash);
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_flash_spinner, brickPosition, R.string.brick_flash_on);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_flash_spinner, brickPosition, R.string.brick_flash_off);
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_flash_spinner, brickPosition, R.string.brick_flash_off);
	}

	@Test
	public void testActualFlashOnBrick() {
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_flash_spinner, brickPosition, R.string.brick_flash_on);
		onView(withId(R.id.button_play)).perform(click());

		SystemClock.sleep(2500);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection
				.SET_LED_ON_VALUE);
	}

	@Test
	public void testActualFlashOffBrick() {
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_flash_spinner, brickPosition, R.string.brick_flash_off);
		onView(withId(R.id.button_play)).perform(click());

		SystemClock.sleep(2500);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection
				.SET_LED_OFF_VALUE);
	}
}
