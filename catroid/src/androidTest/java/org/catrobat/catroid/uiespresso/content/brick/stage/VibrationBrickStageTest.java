/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class VibrationBrickStageTest {
	private int vibrationBrickPosition;
	private int timeoutMS = 2500;
	private int longVibration = 10;
	private int shortVibration = 2;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
	@Before
	public void setUp() throws Exception {
		vibrationBrickPosition = 1;
		Script script = UiTestUtils.createProjectAndGetStartScript("vibrationBrickTest");
		script.addBrick(new VibrationBrick());
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testVibrationHardwareOn() {
		SensorTestArduinoServerConnection.calibrateVibrationSensor();

		onBrickAtPosition(vibrationBrickPosition).onFormulaTextField(R.id.brick_vibration_edit_text)
				.performEnterNumber(longVibration);

		onView(withId(R.id.button_play)).perform(click());

		SensorTestArduinoServerConnection.checkVibrationSensorValue(SensorTestArduinoServerConnection
				.SET_VIBRATION_ON_VALUE, timeoutMS);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testVibrationHardwareOff() {
		SensorTestArduinoServerConnection.calibrateVibrationSensor();

		onBrickAtPosition(vibrationBrickPosition).onFormulaTextField(R.id.brick_vibration_edit_text)
				.performEnterNumber(shortVibration);

		onView(withId(R.id.button_play)).perform(click());

		SensorTestArduinoServerConnection.checkVibrationSensorValue(SensorTestArduinoServerConnection
				.SET_VIBRATION_OFF_VALUE, timeoutMS);
	}
}
