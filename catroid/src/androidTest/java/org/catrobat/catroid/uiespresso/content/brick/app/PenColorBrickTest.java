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
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class PenColorBrickTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenColorBrickRed() {
		int positionPenColorBrick = 1;
		int rgbValueRed = 0;

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.perform(click());
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(CustomSwipeAction.swipeLeftSlow());
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(pressBack());
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button1))
				.perform(click());
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.checkShowsNumber(rgbValueRed);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenColorBrickBlue() {
		int positionPenColorBrick = 1;
		int rgbValueBlue = 3;

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

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenColorBrickGreen() {
		int positionPenColorBrick = 1;
		int rgbValueGreen = 255;
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_green_edit_text)
				.perform(click());
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(CustomSwipeAction.swipeRightSlow());
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(pressBack());
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button1))
				.perform(click());

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_green_edit_text)
				.checkShowsNumber(rgbValueGreen);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenColorBrickUndoRedo() {
		int positionPenColorBrick = 1;
		float[] setRgbValuesRed = {36, 45, 98};
		float[] setRgbValuesGreen = {122, 24, 0};
		float[] setRgbValuesBlue = {44, 255};

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.perform(click());
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesRed[0] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesGreen[0] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_blue))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesBlue[0] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesGreen[1] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesGreen[2] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_blue))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesBlue[1] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesRed[1] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(CustomSwipeAction.swipeToPosition(setRgbValuesRed[2]));

		onView(withId(R.id.menu_undo)).perform(click());
		onView(withId(R.id.menu_undo)).perform(click());
		onView(withId(R.id.menu_undo)).perform(click());
		onView(withId(R.id.menu_undo)).perform(click());

		onView(withId(R.id.menu_redo)).perform(click());
		onView(withId(R.id.menu_redo)).perform(click());
		onView(withId(R.id.menu_redo)).perform(click());

		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(pressBack());
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button1))
				.perform(click());
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.checkShowsNumber((int) setRgbValuesRed[1]);
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_green_edit_text)
				.checkShowsNumber((int) setRgbValuesGreen[2]);
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_blue_edit_text)
				.checkShowsNumber((int) setRgbValuesBlue[1]);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenColorBrickDiscardChanges() {
		int positionPenColorBrick = 1;
		float[] newRgbValues = {55, 122, 99};

		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.perform(click());
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(CustomSwipeAction.swipeToPosition(newRgbValues[0] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_green))
				.perform(CustomSwipeAction.swipeToPosition(newRgbValues[1] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_blue))
				.perform(CustomSwipeAction.swipeToPosition(newRgbValues[2] / 255.0f));
		onView(withId(R.id.color_rgb_seekbar_red))
				.perform(pressBack());
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button2))
				.perform(click());
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_red_edit_text)
				.checkShowsNumber(255);
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_green_edit_text)
				.checkShowsNumber(0);
		onBrickAtPosition(positionPenColorBrick).onFormulaTextField(R.id.brick_set_pen_color_action_blue_edit_text)
				.checkShowsNumber(0);
	}

	private void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "penColorBrickTest");
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("penColorBrickTest");
		Sprite sprite = new Sprite("testSprite");
		sprite.addScript(startScript);
		startScript.addBrick(new SetPenColorBrick(255, 0, 0));
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
