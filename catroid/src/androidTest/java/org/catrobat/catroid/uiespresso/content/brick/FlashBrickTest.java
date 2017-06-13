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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.FlashBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uitest.util.SensorTestServerConnection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript;

@RunWith(AndroidJUnit4.class)
public class FlashBrickTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		createProjectAndGetStartScript("flashBrickTest").addBrick(new FlashBrick());
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testFlashBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_flash);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_flash_spinner)
				.checkShowsText(R.string.brick_flash_on);

		List<Integer> spinnerValues = new ArrayList<>();
		spinnerValues.add(R.string.brick_flash_on);
		spinnerValues.add(R.string.brick_flash_off);
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_flash_spinner)
				.checkValuesAvailable(spinnerValues);
	}

	@Test
	public void testActualFlashOnBrick() {
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_flash_spinner)
				.performSelect(R.string.brick_flash_on);
		onView(withId(R.id.button_play))
				.perform(click());

		onView(isRoot()).perform(CustomActions.wait(2500));

		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection
				.SET_LED_ON_VALUE);
	}

	@Test
	public void testActualFlashOffBrick() {
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_flash_spinner)
				.performSelect(R.string.brick_flash_off);
		onView(withId(R.id.button_play))
				.perform(click());

		onView(isRoot()).perform(CustomActions.wait(2500));

		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection
				.SET_LED_OFF_VALUE);
	}
}
