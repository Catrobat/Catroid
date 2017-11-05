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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.FlashBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript;

@RunWith(AndroidJUnit4.class)
public class FlashBrickStageTest {
	private int flashBrickPosition;
	private int waitingTime = 2500;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		flashBrickPosition = 1;
		createProjectAndGetStartScript("flashBrickTest").addBrick(new FlashBrick());
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testActualFlashOnBrick() {
		onBrickAtPosition(flashBrickPosition).onSpinner(R.id.brick_flash_spinner)
				.performSelect(R.string.brick_flash_on);
		onView(withId(R.id.button_play))
				.perform(click());

		SensorTestArduinoServerConnection.checkLightSensorValue(SensorTestArduinoServerConnection
				.SET_LED_ON_VALUE, waitingTime);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testActualFlashOffBrick() {
		onBrickAtPosition(flashBrickPosition).onSpinner(R.id.brick_flash_spinner)
				.performSelect(R.string.brick_flash_off);
		onView(withId(R.id.button_play))
				.perform(click());

		SensorTestArduinoServerConnection.checkLightSensorValue(SensorTestArduinoServerConnection
				.SET_LED_OFF_VALUE, waitingTime);
	}
}
