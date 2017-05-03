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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.enterStringInFormulaTextFieldOnBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.onScriptList;
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
		int timesToRepeat = 3;
		int changeYMovement = -10;

		LoopEndBrick endBrick = new LoopEndBrick();
		RepeatBrick repeatBrick = new RepeatBrick(timesToRepeat);
		repeatBrick.setLoopEndBrick(endBrick);

		Script script = BrickTestUtils.createProjectAndGetStartScript("LoopBrickTest1");
		script.addBrick(repeatBrick);
		script.addBrick(new ChangeYByNBrick(changeYMovement));
		script.addBrick(endBrick);

		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void repeatBrickTest() throws InterpretationException {
		checkSetUpBrickArrangement();

		deleteBrickAtPosition(1);

		checkIfBrickAtPositionShowsString(1, R.string.brick_change_y_by);
		onView(withText(R.string.brick_loop_end))
				.check(doesNotExist());

		addBrickAtPosition(RepeatBrick.class, 1, R.string.category_control);

		dragBrickAtPositionToBottom(2);

		checkSetUpBrickArrangement();
	}

	@Test
	public void foreverBrickTest() throws InterpretationException {
		addBrickAtPosition(ForeverBrick.class, 2, R.string.category_control);

		checkIfBrickAtPositionShowsString(1, R.string.brick_repeat);
		checkIfBrickAtPositionShowsString(2, R.string.brick_forever);
		checkIfBrickAtPositionShowsString(3, R.string.brick_change_y_by);
		checkIfBrickAtPositionShowsString(4, R.string.brick_loop_end);
		checkIfBrickAtPositionShowsString(5, R.string.brick_loop_end);

		deleteBrickAtPosition(2);

		checkSetUpBrickArrangement();
	}

	@Test
	public void testNestedForeverBricks() throws InterpretationException {

		checkSetUpBrickArrangement();

		addBrickAtPosition(ForeverBrick.class, 2, R.string.category_control);
		addBrickAtPosition(ForeverBrick.class, 3, R.string.category_control);

		checkIfBrickAtPositionShowsString(2, R.string.brick_forever);
		checkIfBrickAtPositionShowsString(3, R.string.brick_forever);

		dragBrickAtPositionToBottom(3);

		checkIfBrickAtPositionShowsString(1, R.string.brick_repeat);
		checkIfBrickAtPositionShowsString(2, R.string.brick_forever);
		checkIfBrickAtPositionShowsString(3, R.string.brick_loop_end);
		checkIfBrickAtPositionShowsString(4, R.string.brick_loop_end);
		checkIfBrickAtPositionShowsString(5, R.string.brick_forever);
		checkIfBrickAtPositionShowsString(6, R.string.brick_change_y_by);
		checkIfBrickAtPositionShowsString(7, R.string.brick_loop_end);

		deleteBrickAtPosition(2);
		deleteBrickAtPosition(3);

		dragBrickAtPositionToBottom(2);

		checkSetUpBrickArrangement();
	}

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
		enterStringInFormulaTextFieldOnBrickAtPosition("3", R.id.brick_repeat_edit_text, 1);
		addBrickAtPosition(ChangeYByNBrick.class, 1, R.string.category_motion);

		dragBrickAtPositionToTop(2);
		dragBrickAtPositionToBottom(2);

		checkSetUpBrickArrangement();
	}

	@Test
	public void testCopyLoopBrickTest() throws InterpretationException {
		onScriptList().atPosition(1).perform(click());

		onView(withText(R.string.brick_context_dialog_copy_brick))
				.perform(click());

		onScriptList().atPosition(1).perform(click());

		checkIfBrickAtPositionShowsString(1, R.string.brick_repeat);
		checkIfBrickAtPositionShowsString(2, R.string.brick_loop_end);
		checkIfBrickAtPositionShowsString(3, R.string.brick_repeat);
		checkIfBrickAtPositionShowsString(4, R.string.brick_change_y_by);
		checkIfBrickAtPositionShowsString(5, R.string.brick_loop_end);

		deleteBrickAtPosition(1);

		checkSetUpBrickArrangement();
	}

	public void dragBrickAtPositionToEdge(int position, final boolean dragToTop) {
		onScriptList().atPosition(position)
				.perform(longClick()).perform(new GeneralSwipeAction(Swipe.FAST,
				GeneralLocation.TOP_CENTER,
				new CoordinatesProvider() {
					@Override
					public float[] calculateCoordinates(View view) {
						float[] coordinates = GeneralLocation.CENTER.calculateCoordinates(view);
						if (dragToTop) {
							coordinates[1] = 0;
						} else {
							coordinates[1] = view.getContext().getResources().getDisplayMetrics().heightPixels;
						}
						return coordinates;
					}
				},
				Press.FINGER));
	}

	public void dragBrickAtPositionToTop(int position) {
		dragBrickAtPositionToEdge(position, true);
	}

	public void dragBrickAtPositionToBottom(int position) {
		dragBrickAtPositionToEdge(position, false);
	}

	public void selectMultipleBricksAndDelete(List<Integer> brickCheckBoxIdList) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());

		onView(withText(R.string.delete))
				.perform(click());

		for (int checkBoxId:brickCheckBoxIdList) {
			onView(withId(checkBoxId))
					.perform(click());
		}
		onView(withContentDescription("Done"))
				.perform(click());

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());
	}

	public void deleteBrickAtPosition(int position) {
		onScriptList().atPosition(position)
				.perform(click());
		onView(withText(R.string.brick_context_dialog_delete_brick))
				.perform(click());
		onView(withText(R.string.yes))
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
		onScriptList().atPosition(insertPosition)
				.perform(click());
	}

	public void checkSetUpBrickArrangement() {
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(1, R.string.brick_repeat);
		checkIfBrickAtPositionShowsString(2, R.string.brick_change_y_by);
		checkIfBrickAtPositionShowsString(3, R.string.brick_loop_end);
	}
}
