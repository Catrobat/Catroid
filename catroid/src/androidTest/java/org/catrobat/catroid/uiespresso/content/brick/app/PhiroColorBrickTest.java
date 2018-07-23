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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
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
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class PhiroColorBrickTest {

	private static final Integer INIT_COLOR = 0;
	private static final Integer SET_COLOR = 100;

	private static Integer whenBrickPosition = 0;
	private static Integer phiroRGBLightBrickPosition = 1;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Parameters(name = "{2}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.id.brick_phiro_rgb_led_action_red_edit_text,
						R.id.rgb_red_value,
						"ColorRedTest"},
				{R.id.brick_phiro_rgb_led_action_green_edit_text,
						R.id.rgb_green_value,
						"ColorGreenTest"},
				{R.id.brick_phiro_rgb_led_action_blue_edit_text,
						R.id.rgb_blue_value,
						"ColorBlueTest"},
		});
	}

	@Parameter
	public @IdRes int brickActionEditTextId;

	@Parameter(1)
	public @IdRes int rgbValueId;

	@Parameter(2)
	public String testName;

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("PhiroColorBrickTest");
		script.addBrick(new PhiroRGBLightBrick(PhiroRGBLightBrick.Eye.BOTH, INIT_COLOR, INIT_COLOR, INIT_COLOR));

		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testPhiroLightRGBValues() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(phiroRGBLightBrickPosition).checkShowsText(R.string.brick_phiro_rgb_led_action);
		onBrickAtPosition(phiroRGBLightBrickPosition).checkShowsText(R.string.phiro_rgb_led_red);
		onBrickAtPosition(phiroRGBLightBrickPosition).checkShowsText(R.string.phiro_rgb_led_green);
		onBrickAtPosition(phiroRGBLightBrickPosition).checkShowsText(R.string.phiro_rgb_led_blue);

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(brickActionEditTextId)
				.checkShowsNumber(INIT_COLOR)
				.perform(click());
		onView(withId(rgbValueId))
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(SET_COLOR)
				.performCloseAndSave();

		onBrickAtPosition(phiroRGBLightBrickPosition).onFormulaTextField(brickActionEditTextId)
				.checkShowsNumber(SET_COLOR);
	}
}
