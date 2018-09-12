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
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class API19SpinnerAndFormulaFieldCrashesRegressionTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	@Category({Cat.AppUi.class, Cat.ApiLevel19Regression.class, Level.Smoke.class})
	@Test
	public void testFormulaFieldBrickRegressionTest() {
		BrickTestUtils.createProjectAndGetStartScript("ChangeSizeByNBrick")
				.addBrick(new ChangeSizeByNBrick());
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onFormulaTextField(R.id.brick_change_size_by_edit_text)
				.perform(click());
		onView(FormulaEditorWrapper.FORMULA_EDITOR_KEYBOARD_MATCHER)
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Cat.ApiLevel19Regression.class, Level.Smoke.class})
	@Test
	public void testPlaySoundBrickSpinnerRegressionTest() {
		BrickTestUtils.createProjectAndGetStartScript("PlaySoundBrick")
				.addBrick(new PlaySoundBrick());
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_play_sound_spinner)
				.performSelectNameable(R.string.new_option);
		onView(withText(R.string.new_sound_dialog_title))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Cat.ApiLevel19Regression.class, Level.Smoke.class})
	@Test
	public void testPointToBrickSpinnerRegressionTest() {
		BrickTestUtils.createProjectAndGetStartScript("PointToBrick")
				.addBrick(new PointToBrick());
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_point_to_spinner)
				.performSelectNameable(R.string.new_option);
		onView(withText(R.string.new_sprite_dialog_title))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Cat.ApiLevel19Regression.class, Level.Smoke.class})
	@Test
	public void testSceneStartBrickSpinnerRegressionTest() {
		BrickTestUtils.createProjectAndGetStartScript("SceneStartBrick")
				.addBrick(new SceneStartBrick("Scene 1"));
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_scene_start_spinner)
				.performSelectNameable(R.string.new_option);
		onView(withText(R.string.new_scene_dialog))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Cat.ApiLevel19Regression.class, Level.Smoke.class})
	@Test
	public void testSceneTransitionBrickSpinnerRegressionTest() {
		BrickTestUtils.createProjectAndGetStartScript("SceneTransitionBrick")
				.addBrick(new SceneTransitionBrick("Scene 1"));
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_scene_transition_spinner)
				.performSelectNameable(R.string.new_option);
		onView(withText(R.string.new_scene_dialog))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Cat.ApiLevel19Regression.class, Level.Smoke.class})
	@Test
	public void testSetLookBrickSpinnerRegressionTest() {
		BrickTestUtils.createProjectAndGetStartScript("SetLookBrick")
				.addBrick(new SetLookBrick());
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_set_look_spinner)
				.performSelectNameable(R.string.new_option);
		onView(withText(R.string.new_look_dialog_title))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Cat.ApiLevel19Regression.class, Level.Smoke.class})
	@Test
	public void testWhenBackgroundChangesBrickSpinnerRegressionTest() {
		BrickTestUtils.createProjectAndGetStartScript("WhenBackgroundChangesBrick")
				.addBrick(new WhenBackgroundChangesBrick());
		baseActivityTestRule.launchActivity();
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_when_background_spinner)
				.performSelectNameable(R.string.new_option);
		onView(withText(R.string.new_look_dialog_title))
				.check(matches(isDisplayed()));
	}
}
