/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper.onColorPickerPresetButton;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class ShowTextColorSizeAlignmentBrickTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {
		brickPosition = 1;
		createProjectAndGetStartScript("ShowTextColorSizeAlignmentBrickTest")
				.addBrick(new ShowTextColorSizeAlignmentBrick());
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPickColor() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_show_variable);
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		onView(withId(R.id.color_picker_button_ok))
				.perform(click());
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.check(matches(withText(containsString("'#0074CD'"))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPickColorCancel() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_show_variable);
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		onView(withId(R.id.color_picker_button_cancel))
				.perform(click());
		onView(withId(R.id.brick_show_variable_color_size_edit_color))
				.check(matches(withText(containsString("0"))));
	}
}
