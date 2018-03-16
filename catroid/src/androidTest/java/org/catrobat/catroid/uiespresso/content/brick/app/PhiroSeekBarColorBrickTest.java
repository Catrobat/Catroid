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

import android.support.annotation.IdRes;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.content.brick.utils.CustomSwipeAction;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class PhiroSeekBarColorBrickTest {

	private static final Integer MIN_COLOR_VALUE = 0;
	private static final Integer MAX_COLOR_VALUE = 255;
	private static final Integer INIT_COLOR_VALUE = 128;

	private static Integer whenBrickPosition = 0;
	private static Integer phiroRGBLightBrickPosition = 1;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Parameters(name = "{3}-Test")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.id.color_rgb_seekbar_red,
						R.id.rgb_red_value,
						R.id.brick_phiro_rgb_led_action_red_edit_text,
						"SeekbarRed"},
				{R.id.color_rgb_seekbar_green,
						R.id.rgb_green_value,
						R.id.brick_phiro_rgb_led_action_green_edit_text,
						"SeekbarGreen"},
				{R.id.color_rgb_seekbar_blue,
						R.id.rgb_blue_value,
						R.id.brick_phiro_rgb_led_action_blue_edit_text,
						"SeekbarBlue"},
		});
	}

	@Parameter
	public @IdRes int colorRgbSeekbarId;

	@Parameter(1)
	public @IdRes int rgbValueId;

	@Parameter(2)
	public @IdRes int brickActionEditTextId;

	@Parameter(3)
	public String testName;

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("PhiroSeekBarColorBrickTest");
		script.addBrick(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, INIT_COLOR_VALUE, INIT_COLOR_VALUE, INIT_COLOR_VALUE));

		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testSeekBar() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(phiroRGBLightBrickPosition).checkShowsText(R.string.brick_phiro_rgb_led_action);

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(brickActionEditTextId)
				.checkShowsNumber(INIT_COLOR_VALUE)
				.perform(click());

		onView(withId(colorRgbSeekbarId))
				.perform(swipeLeftSlow());
		onView(withId(rgbValueId))
				.check(matches(withText(MIN_COLOR_VALUE.toString() + " ")));
		onView(withId(colorRgbSeekbarId))
				.perform(swipeRightSlow());
		onView(withId(rgbValueId))
				.check(matches(withText(MAX_COLOR_VALUE.toString() + " ")));
		onView(withId(rgbValueId))
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_ok))
				.perform(click());
		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(brickActionEditTextId)
				.checkShowsNumber(MAX_COLOR_VALUE);
	}

	private static ViewAction swipeRightSlow() {
		return new CustomSwipeAction(Swipe.SLOW, CustomSwipeAction.SwipeAction.SWIPE_RIGHT, Press.FINGER);
	}

	private static ViewAction swipeLeftSlow() {
		return new CustomSwipeAction(Swipe.SLOW, CustomSwipeAction.SwipeAction.SWIPE_LEFT, Press.FINGER);
	}
}
