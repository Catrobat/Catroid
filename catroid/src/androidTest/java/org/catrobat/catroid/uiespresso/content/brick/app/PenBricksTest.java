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

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class PenBricksTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	private static ViewAction swipeRightSlow() {
		return new CustomSwipeAction(Swipe.SLOW, CustomSwipeAction.SwipeAction.SWIPE_RIGHT, Press.FINGER);
	}

	private static ViewAction swipeLeftSlow() {
		return new CustomSwipeAction(Swipe.SLOW, CustomSwipeAction.SwipeAction.SWIPE_LEFT, Press.FINGER);
	}

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenColorBrick() {
		int positionPenColorBrick = 4;
		int rgbValueRed = 0;
		int rgbValueGreen = 255;
		int rgbValueBlue = 3;

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.perform(click());
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(swipeLeftSlow());
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(pressBack());
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.checkShowsNumber(rgbValueRed);

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_green_edit_text)
				.perform(click());
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(swipeRightSlow());
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(pressBack());
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_green_edit_text)
				.checkShowsNumber(rgbValueGreen);

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_blue_edit_text)
				.perform(click());
		onView(withId(R.id.color_rgb_preview))
				.check(matches(isDisplayed()));
		onView(withId(R.id.color_rgb_seekbar_blue))
				.check(matches(isDisplayed()));
		onView(withId(R.id.rgb_blue_value))
				.check(matches(isDisplayed()));
		onView(withId(R.id.color_rgb_textview_blue))
				.check(matches(isDisplayed()));

		onView(withId(R.id.rgb_blue_value))
				.perform(click());
		onFormulaEditor()
				.performEnterNumber(rgbValueBlue);
		onFormulaEditor()
				.performCloseAndSave();
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_blue_edit_text)
				.checkShowsNumber(rgbValueBlue);
	}

	private void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "penBricksTest");
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("penBricksTest");
		Sprite sprite1 = new Sprite("testSprite");
		sprite1.addScript(startScript);
		startScript.addBrick(new PenDownBrick());
		startScript.addBrick(new PenUpBrick());
		startScript.addBrick(new SetPenSizeBrick(5));
		startScript.addBrick(new SetPenColorBrick(255, 0, 0));
		project.getDefaultScene().addSprite(sprite1);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
	}
}
