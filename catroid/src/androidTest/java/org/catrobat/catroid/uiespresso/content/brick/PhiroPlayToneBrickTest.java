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
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.clickSelectCheckSpinnerValueOnBrick;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterValueInFormulaTextFieldOnBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class PhiroPlayToneBrickTest {
	private int brickPosition;
	private static final int TONE_DURATION = 30;
	private static final int TONE_DURATION_INITIALLY = -70;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {

		BrickTestUtils.createProjectAndGetStartScript("PhiroPlayToneBrickTest")
				.addBrick(new PhiroPlayToneBrick(PhiroPlayToneBrick.Tone.DO, TONE_DURATION_INITIALLY));
		brickPosition = 1;
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testPhiroPlayToneBrick() {

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.phiro_play_tone);

		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_select_tone_spinner, brickPosition,
				R.string.phiro_tone_do);

		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_select_tone_spinner, brickPosition,
				R.string.phiro_tone_re);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_select_tone_spinner, brickPosition,
				R.string.phiro_tone_mi);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_select_tone_spinner, brickPosition,
				R.string.phiro_tone_fa);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_select_tone_spinner, brickPosition,
				R.string.phiro_tone_so);
		clickSelectCheckSpinnerValueOnBrick(R.id.brick_phiro_select_tone_spinner, brickPosition,
				R.string.phiro_tone_la);
		enterValueInFormulaTextFieldOnBrickAtPosition(TONE_DURATION, R.id.brick_phiro_play_tone_duration_edit_text,
				brickPosition);
	}
}
