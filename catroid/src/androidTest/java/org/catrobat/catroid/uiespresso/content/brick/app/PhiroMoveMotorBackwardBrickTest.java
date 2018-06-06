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
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
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

import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class PhiroMoveMotorBackwardBrickTest {
	private int brickPosition;
	private int initialSpeed = 70;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {

		PhiroMotorMoveBackwardBrick phiroMotorMoveBackwardBrick =
				new PhiroMotorMoveBackwardBrick(PhiroMotorMoveBackwardBrick.Motor.MOTOR_RIGHT, initialSpeed);

		BrickTestUtils.createProjectAndGetStartScript("PhiroMoveMotorBackwardBrickTest")
				.addBrick(phiroMotorMoveBackwardBrick);

		brickPosition = 1;
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testPhiroMoveMotorBackwardBrick() {
		int setSpeed = 30;

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_phiro_motor_backward_action);

		List<Integer> spinnerValuesResourceIds = Arrays.asList(
				R.string.phiro_motor_right,
				R.string.phiro_motor_left,
				R.string.phiro_motor_both);
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_phiro_motor_backward_action_spinner)
				.checkValuesAvailable(spinnerValuesResourceIds);

		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_phiro_motor_backward_action_speed_edit_text))
				.perform(click());
		onView(withId(R.id.single_seekbar_value))
				.perform(click());

		onFormulaEditor()
				.checkValue(((Integer) initialSpeed).toString())
				.performEnterNumber(setSpeed)
				.checkValue(((Integer) setSpeed).toString())
				.performCloseAndSave();

		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_phiro_motor_backward_action_speed_edit_text))
				.perform(click());
		onView(allOf(withId(R.id.single_seekbar_title), withText(R.string.phiro_motor_speed)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.single_seekbar_value), withText(Integer.toString(setSpeed) + " ")))
				.check(matches(isDisplayed()))
				.perform(click());

		onFormulaEditor()
				.performEnterNumber(5)
				.performCloseAndSave();

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_phiro_motor_backward_action_speed_edit_text)
				.checkShowsNumber(5);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testPhiroMoveMotorBackwardBrickUndo() {
		int[] setSpeeds = {1, 2, 3};
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_phiro_motor_backward_action);

		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_phiro_motor_backward_action_speed_edit_text))
				.perform(click());

		onView(withId(R.id.single_seekbar_seekbar))
				.perform(CustomSwipeAction.swipeToPosition(setSpeeds[0] / 100.0f));
		onView(withId(R.id.single_seekbar_seekbar))
				.perform(CustomSwipeAction.swipeToPosition(setSpeeds[1] / 100.0f));
		onView(withId(R.id.single_seekbar_seekbar))
				.perform(CustomSwipeAction.swipeToPosition(setSpeeds[2] / 100.0f));

		onView(withId(R.id.menu_undo)).perform(click());
		onView(withId(R.id.menu_undo)).perform(click());
		onView(withId(R.id.menu_undo)).perform(click());
		onView(withId(R.id.single_seekbar_seekbar))
				.perform(pressBack());
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button1))
				.perform(click());
		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_phiro_motor_backward_action_speed_edit_text)
				.checkShowsNumber(initialSpeed);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testPhiroMoveMotorBackwardBrickUndoRedo() {
		int[] setSpeeds = {45, 10};

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_phiro_motor_backward_action);

		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_phiro_motor_backward_action_speed_edit_text))
				.perform(click());

		onView(withId(R.id.single_seekbar_seekbar))
				.perform(CustomSwipeAction.swipeToPosition(setSpeeds[0] / 100.0f));
		onView(withId(R.id.single_seekbar_seekbar))
				.perform(CustomSwipeAction.swipeToPosition(setSpeeds[1] / 100.0f));

		onView(withId(R.id.menu_undo)).perform(click());
		onView(withId(R.id.single_seekbar_seekbar))
				.perform(pressBack());
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button1))
				.perform(click());
		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_phiro_motor_backward_action_speed_edit_text)
				.checkShowsNumber(setSpeeds[0]);
	}
}
