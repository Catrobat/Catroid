/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class ShowTextColorSizeAlignmentBrickTest {
	private int showBrickPosition;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		showBrickPosition = 1;
		BrickTestUtils.createProjectAndGetStartScript("TEST").addBrick(new ShowTextColorSizeAlignmentBrick());
		ProjectManager.getInstance().getCurrentProject().addUserVariable(new UserVariable("testVariable1"));
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testShowVariableColorSizeAlignmentBrick() {
		final int positionX = 30;
		final int positionY = 40;
		final int relativeTextSize = 40;
		final String color = "#FF00FF";

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(showBrickPosition).checkShowsText(R.string.brick_show_variable);

		onBrickAtPosition(showBrickPosition).onFormulaTextField(R.id.brick_show_variable_color_size_edit_text_x)
				.performEnterNumber(positionX)
				.checkShowsNumber(positionX);

		onBrickAtPosition(showBrickPosition).onFormulaTextField(R.id.brick_show_variable_color_size_edit_text_y)
				.performEnterNumber(positionY)
				.checkShowsNumber(positionY);

		onBrickAtPosition(showBrickPosition).onFormulaTextField(R.id.brick_show_variable_color_size_edit_color)
				.performEnterString(color)
				.checkShowsText(color);

		onBrickAtPosition(showBrickPosition)
				.onFormulaTextField(R.id.brick_show_variable_color_size_edit_relative_size)
				.performEnterNumber(relativeTextSize)
				.checkShowsNumber(relativeTextSize);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testShowVariableColorAndSizeBrickCheckAlignmentSpinner() {
		onBrickAtPosition(showBrickPosition).onSpinner(R.id.brick_show_variable_color_size_align_spinner)
				.checkShowsText(R.string.brick_show_variable_aligned_centered);

		Context context = InstrumentationRegistry.getTargetContext();
		List<String> spinnerValues = Arrays.asList(
				context.getString(R.string.brick_show_variable_aligned_left),
				context.getString(R.string.brick_show_variable_aligned_right));

		onBrickAtPosition(showBrickPosition).onSpinner(R.id.brick_show_variable_color_size_align_spinner)
				.checkNameableValuesAvailable(spinnerValues);
	}
}
