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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper.onColorPickerPresetButton;
import static org.hamcrest.Matchers.containsString;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(Parameterized.class)
public class PhiroColorBrickNumberTest {

	private static Integer whenBrickPosition = 0;
	private static Integer phiroRGBLightBrickPosition = 1;

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"negativeParametersTest", -23, -1, -1},
				{"allZeroParametersTest", 0, 0, 0},
				{"positiveParametersTest", 2, 3, 4}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Object red;

	@Parameterized.Parameter(2)
	public Object green;

	@Parameterized.Parameter(3)
	public Object blue;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Script script = UiTestUtils.createProjectAndGetStartScript("PhiroColorBrickTest");

		Object[] parameters = {red, green, blue};
		ArrayList<Formula> formula = new ArrayList<>();

		for (Object color : parameters) {
			formula.add(new Formula((Integer) color));
		}

		script.addBrick(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, formula.get(0), formula.get(1),
				formula.get(2)));

		formula.clear();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testPhiroLightRGBShowDialog() {
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.checkShowsNumber((Integer) red);

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_green_edit_text)
				.checkShowsNumber((Integer) green);

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_blue_edit_text)
				.checkShowsNumber((Integer) blue)
				.perform(click());

		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testPhiroLightRGBValuesWithColorPicker() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		onView(withText(R.string.color_picker_apply))
				.perform(click());
		onView(withId(R.id.brick_phiro_rgb_led_action_red_edit_text))
				.check(matches(withText(containsString("0"))));
		onView(withId(R.id.brick_phiro_rgb_led_action_green_edit_text))
				.check(matches(withText(containsString("116"))));
		onView(withId(R.id.brick_phiro_rgb_led_action_blue_edit_text))
				.check(matches(withText(containsString("205"))));
	}

	@Test
	public void testPhiroLightRGBPickColorCancel() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_pick_color))
				.perform(click());
		onColorPickerPresetButton(0, 0)
				.perform(click());
		onView(withText(R.string.color_picker_cancel))
				.perform(click());

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_red_edit_text)
				.checkShowsNumber((Integer) red);
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_green_edit_text)
				.checkShowsNumber((Integer) green);
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(R.id.brick_phiro_rgb_led_action_blue_edit_text)
				.checkShowsNumber((Integer) blue);
	}
}
