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
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

//TODO incomplete Test! ks

@RunWith(AndroidJUnit4.class)
public class PhiroMoveMotorForwardBrickTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() {
		int initialSpeed = -70;

		PhiroMotorMoveForwardBrick phiroMotorMoveForwardBrick =
				new PhiroMotorMoveForwardBrick(PhiroMotorMoveForwardBrick.Motor.MOTOR_RIGHT, initialSpeed);
		BrickTestUtils.createProjectAndGetStartScript("PhiroMoveMotorForwardBrickTest")
				.addBrick(phiroMotorMoveForwardBrick);
		brickPosition = 1;
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testPhiroMoveMotorForwardBrick() {
		int setSpeed = 30;

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_phiro_motor_forward_action);

		List<Integer> spinnerValuesResourceIds = Arrays.asList(
				R.string.phiro_motor_right,
				R.string.phiro_motor_left,
				R.string.phiro_motor_both);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_phiro_motor_forward_action_spinner)
				.checkValuesAvailable(spinnerValuesResourceIds);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_phiro_motor_forward_action_speed_edit_text)
				.performEnterNumber(setSpeed)
				.checkShowsNumber(setSpeed);
	}
}
