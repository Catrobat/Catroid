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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterValueInFormulaTextFieldOnBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class GlideToBrickTest {
	private int brickPosition;
	private GlideToBrick glideToBrick;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		glideToBrick = new GlideToBrick(0, 0, 0);
		BrickTestUtils.createProjectAndGetStartScript("glideToBrickTest1").addBrick(glideToBrick);
		brickPosition = 1;
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void glideToBrickTest() throws InterpretationException {
		int duration = 2;
		int xPosition = 123;
		int yPosition = 567;

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_glide_to_x);

		enterValueInFormulaTextFieldOnBrickAtPosition(duration, R.id.brick_glide_to_edit_text_duration, brickPosition);
		enterValueInFormulaTextFieldOnBrickAtPosition(xPosition, R.id.brick_glide_to_edit_text_x, brickPosition);
		enterValueInFormulaTextFieldOnBrickAtPosition(yPosition, R.id.brick_glide_to_edit_text_y, brickPosition);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		Formula formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.DURATION_IN_SECONDS);
		assertEquals(Math.round(duration * 1000), Math.round(formula.interpretFloat(sprite) * 1000));

		formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.X_DESTINATION);
		assertEquals(xPosition, (int) formula.interpretInteger(sprite));

		formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.Y_DESTINATION);
		assertEquals(yPosition, (int) formula.interpretInteger(sprite));
	}

	@Test
	public void glideToBrickTestPluralSeconds() {
		enterValueInFormulaTextFieldOnBrickAtPosition(1, R.id.brick_glide_to_edit_text_duration, brickPosition);
		checkIfBrickAtPositionShowsString(brickPosition,
				UiTestUtils.getResources().getQuantityString(R.plurals.second_plural, 1));

		enterValueInFormulaTextFieldOnBrickAtPosition(5, R.id.brick_glide_to_edit_text_duration, brickPosition);
		checkIfBrickAtPositionShowsString(brickPosition,
				UiTestUtils.getResources().getQuantityString(R.plurals.second_plural, 5));
	}
}
