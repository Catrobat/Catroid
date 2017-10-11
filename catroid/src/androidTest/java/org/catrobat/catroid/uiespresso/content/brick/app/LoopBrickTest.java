/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickCoordinatesProvider;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class LoopBrickTest {

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void repeatBrickTest() {
		checkSetUpBrickArrangement();

		onBrickAtPosition(1).performDeleteBrick();

		onBrickAtPosition(1).checkShowsText(R.string.brick_change_y_by);
		onView(withText(R.string.brick_loop_end))
				.check(doesNotExist());

		addBrickAtPosition(RepeatBrick.class, 1, R.string.category_control);

		onBrickAtPosition(2).performDragNDrop(BrickCoordinatesProvider.DOWN_ONE_POSITION);

		checkSetUpBrickArrangement();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void foreverBrickTest() {
		addBrickAtPosition(ForeverBrick.class, 2, R.string.category_control);

		onBrickAtPosition(1).checkShowsText(R.string.brick_repeat);
		onBrickAtPosition(2).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(3).checkShowsText(R.string.brick_change_y_by);
		onBrickAtPosition(4).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(5).checkShowsText(R.string.brick_loop_end);

		onBrickAtPosition(2).performDeleteBrick();

		checkSetUpBrickArrangement();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testNestedForeverBricks() {

		checkSetUpBrickArrangement();

		addBrickAtPosition(ForeverBrick.class, 2, R.string.category_control);
		addBrickAtPosition(ForeverBrick.class, 3, R.string.category_control);

		onBrickAtPosition(2).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(3).checkShowsText(R.string.brick_forever);

		onBrickAtPosition(3).performDragNDrop(BrickCoordinatesProvider.DOWN_TO_BOTTOM);

		onBrickAtPosition(1).checkShowsText(R.string.brick_repeat);
		onBrickAtPosition(2).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(4).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(5).checkShowsText(R.string.brick_forever);
		onBrickAtPosition(6).checkShowsText(R.string.brick_change_y_by);
		onBrickAtPosition(7).checkShowsText(R.string.brick_loop_end);

		onBrickAtPosition(2).performDeleteBrick();
		onBrickAtPosition(3).performDeleteBrick();

		onBrickAtPosition(2).performDragNDrop(BrickCoordinatesProvider.DOWN_TO_BOTTOM);

		checkSetUpBrickArrangement();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testSelectLoopBrickAndDelete() throws InterpretationException {
		checkSetUpBrickArrangement();

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.id.brick_repeat_checkbox);
		selectMultipleBricksAndDelete(idList);

		onView(withText(R.string.brick_repeat))
				.check(doesNotExist());
		onView(withText(R.string.brick_loop_end))
				.check(doesNotExist());
		onView(withText(R.string.brick_change_y_by))
				.check(doesNotExist());

		addBrickAtPosition(RepeatBrick.class, 1, R.string.category_control);

		onBrickAtPosition(1).onFormulaTextField(R.id.brick_repeat_edit_text)
				.performEnterString("3")
				.checkShowsText("3");

		addBrickAtPosition(ChangeYByNBrick.class, 1, R.string.category_motion);

		onBrickAtPosition(1).performDragNDrop(BrickCoordinatesProvider.DOWN_ONE_POSITION);

		checkSetUpBrickArrangement();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCopyLoopBrickTest() throws InterpretationException {
		onBrickAtPosition(1).perform(click());

		onView(withText(R.string.brick_context_dialog_copy_brick))
				.perform(click());

		onBrickAtPosition(1).perform(click());

		onBrickAtPosition(1).checkShowsText(R.string.brick_repeat);
		onBrickAtPosition(2).checkShowsText(R.string.brick_loop_end);
		onBrickAtPosition(3).checkShowsText(R.string.brick_repeat);
		onBrickAtPosition(4).checkShowsText(R.string.brick_change_y_by);
		onBrickAtPosition(5).checkShowsText(R.string.brick_loop_end);

		onBrickAtPosition(1).performDeleteBrick();

		checkSetUpBrickArrangement();
	}

	public void selectMultipleBricksAndDelete(List<Integer> brickCheckBoxIdList) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());

		onView(withText(R.string.delete))
				.perform(click());

		for (int checkBoxId : brickCheckBoxIdList) {
			onView(withId(checkBoxId))
					.perform(click());
		}
		onView(withContentDescription("Done"))
				.perform(click());

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());
	}

	public void addBrickAtPosition(Class<?> brickHeaderClass, int insertPosition, int brickCategoryId) {
		onView(withId(R.id.button_add))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(brickCategoryId))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
		onData(is(instanceOf(brickHeaderClass))).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.perform(click());
		onBrickAtPosition(insertPosition)
				.perform(click());
	}

	public void checkSetUpBrickArrangement() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1).checkShowsText(R.string.brick_repeat);
		onBrickAtPosition(2).checkShowsText(R.string.brick_change_y_by);
		onBrickAtPosition(3).checkShowsText(R.string.brick_loop_end);
	}

	public void createProject() {
		int timesToRepeat = 3;
		int changeYMovement = -10;

		LoopEndBrick endBrick = new LoopEndBrick();
		RepeatBrick repeatBrick = new RepeatBrick(timesToRepeat);
		repeatBrick.setLoopEndBrick(endBrick);

		Script script = BrickTestUtils.createProjectAndGetStartScript("LoopBrickTest1");
		script.addBrick(repeatBrick);
		script.addBrick(new ChangeYByNBrick(changeYMovement));
		script.addBrick(endBrick);
	}
}
