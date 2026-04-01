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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper.onColorPickerPresetButton;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.containsString;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(AndroidJUnit4.class)
public class ShowTextColorSizeAlignmentBrickTest {
	private static final String TAG = ShowTextColorSizeAlignmentBrickTest.class.getSimpleName();
	private int brickPosition;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() {
		try {
			TestUtils.deleteProjects(ShowTextColorSizeAlignmentBrickTest.class.getSimpleName());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Before
	public void setUp() {
		brickPosition = 1;
		UiTestUtils
				.createProjectAndGetStartScript(ShowTextColorSizeAlignmentBrickTest.class.getSimpleName())
				.addBrick(new ShowTextColorSizeAlignmentBrick(0, 0, 100, "#000000"));
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testPickColor() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_show_variable);
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		closeSoftKeyboard();
		onView(withText(R.string.color_picker_apply))
				.perform(click());
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.check(matches(withText(containsString("'#0074CD'"))));
	}

	@Test
	public void testPickColorCancel() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_show_variable);
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		closeSoftKeyboard();
		onView(withText(R.string.color_picker_cancel))
				.perform(click());
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.check(matches(withText(containsString("0"))));
	}

	@Test
	public void testPickColorInFormulaFragment() {
		onView(withId(R.id.brick_show_variable_color_size_edit_relative_size))
				.perform(click());
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testPickColorNotShownForComplexFormula() {
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterFormula("1+2");
		pressBack();
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.perform(click());
		onFormulaEditor()
				.check(matches(isDisplayed()));
	}
}
