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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper.onColorPickerPresetButton;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class PhiroColorBrickTest {

	private static final Integer INIT_COLOR = 0;

	private static Integer whenBrickPosition = 0;
	private static Integer phiroRGBLightBrickPosition = 1;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("PhiroColorBrickTest");
		script.addBrick(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, INIT_COLOR, INIT_COLOR, INIT_COLOR));

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPhiroLightRGBValuesWithFormulaEditor() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.checkShowsNumber(INIT_COLOR)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.perform(click());
		onFormulaEditor()
				.performEnterString("1+2 ")
				.performCloseAndSave();

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_green_edit_text)
				.checkShowsNumber(INIT_COLOR)
				.perform(click());
		onFormulaEditor()
				.check(matches(isDisplayed()));
		pressBack();

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_blue_edit_text)
				.checkShowsNumber(INIT_COLOR)
				.perform(click());
		onFormulaEditor()
				.check(matches(isDisplayed()));
		pressBack();

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.perform(click());
		onFormulaEditor()
				.check(matches(isDisplayed()));
		onFormulaEditor()
				.performEnterNumber(24)
				.performCloseAndSave();

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPhiroLightRGBValuesWithColorPicker() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.checkShowsNumber(INIT_COLOR)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		onView(withId(R.id.color_picker_button_ok))
				.perform(click());
		onView(withId(R.id.brick_phiro_rgb_led_action_red_edit_text))
				.check(matches(withText(containsString("0"))));
		onView(withId(R.id.brick_phiro_rgb_led_action_green_edit_text))
				.check(matches(withText(containsString("116"))));
		onView(withId(R.id.brick_phiro_rgb_led_action_blue_edit_text))
				.check(matches(withText(containsString("205"))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPhiroLightRGBPickColorCancel() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		onView(withId(R.id.color_picker_button_cancel))
				.perform(click());
		onView(withId(R.id.brick_phiro_rgb_led_action_red_edit_text))
				.check(matches(withText(containsString("0"))));
		onView(withId(R.id.brick_phiro_rgb_led_action_green_edit_text))
				.check(matches(withText(containsString("0"))));
		onView(withId(R.id.brick_phiro_rgb_led_action_blue_edit_text))
				.check(matches(withText(containsString("0"))));
	}
}
