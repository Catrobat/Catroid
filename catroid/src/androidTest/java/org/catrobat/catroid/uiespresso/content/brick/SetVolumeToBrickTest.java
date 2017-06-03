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
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.content.brick.utils.FormulaTextFieldUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.utils.FormulaTextFieldUtils.enterStringInFormulaTextFieldOnBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.FormulaTextFieldUtils.enterValueInFormulaTextFieldOnBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class SetVolumeToBrickTest {
	private static int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("setVolumeToBrickTest1").addBrick(new SetVolumeToBrick());
		brickPosition = 1;
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	@Flaky
	public void testSetVolumeToBrickStringValue() {
		String volumeToChange = "10.2";

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_set_volume_to);
		enterStringInFormulaTextFieldOnBrickAtPosition(volumeToChange, R.id.brick_set_volume_to_edit_text,
				brickPosition);
	}

	@Test
	public void testSetVolumeToBrickIntValue() {
		int volumeToChange = 12;

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_set_volume_to);
		enterValueInFormulaTextFieldOnBrickAtPosition(volumeToChange, R.id.brick_set_volume_to_edit_text,
				brickPosition);
	}

	@Test
	public void testSetVolumeToBrickFloatValue() {
		float volumeToChange = 12.4f;

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_set_volume_to);
		FormulaTextFieldUtils.enterValueInFormulaTextFieldOnBrickAtPosition(volumeToChange, R.id.brick_set_volume_to_edit_text,
				brickPosition);
	}
}
