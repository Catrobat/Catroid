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
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfValuesAvailableInSpinnerOnBrick;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.clickSelectCheckSpinnerValueOnBrick;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterValueInFormulaTextFieldOnBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class PhiroMoveMotorForwardBrickTest {
	private int brickPosition;
	private static final int SET_SPEED = 30;
	private static final int SET_SPEED_INITIALLY = -70;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {

		BrickTestUtils.createProjectAndGetStartScript("PhiroMoveMotorForwardBrickTest")
				.addBrick(new PhiroMotorMoveForwardBrick(
						PhiroMotorMoveForwardBrick.Motor.MOTOR_RIGHT, SET_SPEED_INITIALLY));
		brickPosition = 1;
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testPhiroMoveMotorForwardBrick() {
		List<Integer> spinnerValuesResourceIds = Arrays.asList(
				R.string.phiro_motor_right,
				R.string.phiro_motor_left,
				R.string.phiro_motor_both);
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_phiro_motor_forward_action);
		checkIfValuesAvailableInSpinnerOnBrick(spinnerValuesResourceIds, R.id.brick_phiro_motor_forward_action_spinner, brickPosition);
		enterValueInFormulaTextFieldOnBrickAtPosition(SET_SPEED,
				R.id.brick_phiro_motor_forward_action_speed_edit_text, brickPosition);
	}
}
