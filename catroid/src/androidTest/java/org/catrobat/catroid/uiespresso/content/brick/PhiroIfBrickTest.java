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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkBrickNotExists;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.clickSelectCheckSpinnerValueOnBrick;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.deleteBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class PhiroIfBrickTest {
	private int brickPosition;
	private PhiroIfLogicBeginBrick ifBrick;
	private Script script;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {

		brickPosition = 1;

		ifBrick = new PhiroIfLogicBeginBrick();
		IfLogicElseBrick ifLogicElseBrick = new IfLogicElseBrick(ifBrick);
		IfLogicEndBrick ifLogicEndBrick = new IfLogicEndBrick(ifLogicElseBrick, ifBrick);
		ifBrick.setIfElseBrick(ifLogicElseBrick);
		ifBrick.setIfEndBrick(ifLogicEndBrick);
		script = BrickTestUtils.createProjectAndGetStartScript("PhiroIfBrickTest");
		script.addBrick(ifBrick);
		script.addBrick(ifLogicElseBrick);
		script.addBrick(ifLogicEndBrick);
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testPhiroIfBrick() {

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_phiro_sensor_begin);

		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_sensor_action_spinner, brickPosition,
				R.string.phiro_sensor_front_left);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_sensor_action_spinner, brickPosition,
				R.string.phiro_sensor_front_right);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_sensor_action_spinner, brickPosition,
				R.string.phiro_sensor_side_left);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_sensor_action_spinner, brickPosition,
				R.string.phiro_sensor_side_right);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_sensor_action_spinner, brickPosition,
				R.string.phiro_sensor_bottom_left);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_sensor_action_spinner, brickPosition,
				R.string.phiro_sensor_bottom_right);

		checkIfBrickAtPositionShowsString(brickPosition + 1, R.string.brick_if_else);
		checkIfBrickAtPositionShowsString(brickPosition + 2, R.string.brick_if_end);
	}

	@Test
	public void testPhiroIfBrickDelete() {

		deleteBrickAtPosition(brickPosition, R.string.brick_phiro_sensor_begin);

		checkBrickNotExists(R.string.brick_phiro_sensor_begin);
		checkBrickNotExists(R.string.brick_if_else);
		checkBrickNotExists(R.string.brick_if_end);
	}
}
