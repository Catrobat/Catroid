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
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterStringInFormulaTextFieldOnBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class LegoEv3PlayToneBrickTest {
	private static int brickPosition = 1;

	public static int startFrequency = 1;
	public static float startDuration = 1;
	public static int startVolume = 100;
	public static int testFrequency = 2;
	public static float testDuration = 2;
	public static int testVolume = 50;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("LegoEv3PlayToneBrickTest")
				.addBrick(new LegoEv3PlayToneBrick(startFrequency, startDuration, startVolume));
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void legoEv3PlayToneBrickTest() {
		checkIfBrickAtPositionShowsString(0, "When program starts");
		checkIfBrickAtPositionShowsString(brickPosition, "Play EV3 tone");

		enterStringInFormulaTextFieldOnBrickAtPosition(Float.toString(testDuration),
				R.id.brick_ev3_tone_duration_edit_text, brickPosition);

		enterStringInFormulaTextFieldOnBrickAtPosition(Integer.toString(testFrequency),
				R.id.brick_ev3_tone_freq_edit_text, brickPosition);

		enterStringInFormulaTextFieldOnBrickAtPosition(Integer.toString(testVolume), 
				R.id.brick_ev3_tone_volume_edit_text, brickPosition);
	}
}
