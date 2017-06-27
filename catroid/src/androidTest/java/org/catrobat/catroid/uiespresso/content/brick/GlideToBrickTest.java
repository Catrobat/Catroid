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
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class GlideToBrickTest {
	private int brickPosition;
	private GlideToBrick glideToBrick;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		brickPosition = 1;
		glideToBrick = new GlideToBrick(0, 0, 0);
		BrickTestUtils.createProjectAndGetStartScript("glideToBrickTest1")
				.addBrick(glideToBrick);
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void glideToBrickTest() throws InterpretationException {
		int duration = 2;
		int xPosition = 123;
		int yPosition = 567;

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_glide_to_x);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_glide_to_edit_text_duration)
				.performEnterNumber(duration)
				.checkShowsNumber(duration);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_glide_to_edit_text_x)
				.performEnterNumber(xPosition)
				.checkShowsNumber(xPosition);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_glide_to_edit_text_y)
				.performEnterNumber(yPosition)
				.checkShowsNumber(yPosition);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		Formula formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.DURATION_IN_SECONDS);
		assertEquals(Math.round(duration * 1000), Math.round(formula.interpretFloat(sprite) * 1000));

		formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.X_DESTINATION);
		assertEquals(xPosition, (int) formula.interpretInteger(sprite));

		formula = glideToBrick.getFormulaWithBrickField(Brick.BrickField.Y_DESTINATION);
		assertEquals(yPosition, (int) formula.interpretInteger(sprite));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void glideToBrickTestPluralSeconds() {
		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_glide_to_edit_text_duration)
				.performEnterNumber(1).checkShowsNumber(1);

		onBrickAtPosition(brickPosition).checkShowsText(UiTestUtils.getResources().getQuantityString(R.plurals.second_plural, 1));

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_glide_to_edit_text_duration)
				.performEnterNumber(5)
				.checkShowsNumber(5);

		onBrickAtPosition(brickPosition).checkShowsText(UiTestUtils.getResources().getQuantityString(R.plurals.second_plural, 5));
	}
}
