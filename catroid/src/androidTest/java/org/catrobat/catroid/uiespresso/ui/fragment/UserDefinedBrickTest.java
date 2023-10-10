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
package org.catrobat.catroid.uiespresso.ui.fragment;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.WaitForConditionAction.waitFor;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class UserDefinedBrickTest {

	private final long waitThreshold = 5000;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(UserDefinedBrickTest.class.getSimpleName());
	}

	@Before
	public void setUp() throws IOException {
		UiTestUtils.createProjectAndGetStartScript(UserDefinedBrickTest.class.getSimpleName());
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void checkPlusButtonExists() {
		selectYourBricks();
		onView(withId(R.id.button_add_user_brick))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testAddUserDataToUserBrickFragmentShown() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.fragment_add_user_data_to_user_brick)).check(matches(isDisplayed()));
	}

	@Test
	public void testAddInputToUserBrickDefaultText() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.user_data_user_brick_edit_field)).check(matches(withText(baseActivityTestRule.getActivity().getString(R.string.brick_user_defined_default_input_name))));
	}

	@Test
	public void testAddLabelToUserBrickDefaultText() {
		clickOnAddLabelToUserBrick();
		onView(withId(R.id.user_data_user_brick_edit_field)).check(matches(withText(baseActivityTestRule.getActivity().getString(R.string.brick_user_defined_default_label))));
	}

	@Test
	public void testAddInputToUserBrickEmptyInput() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.user_data_user_brick_edit_field)).perform(replaceText(""));
		onView(withText(R.string.name_empty)).check(matches(isDisplayed()));
		onView(withId(R.id.next)).check(matches(not(isEnabled())));
	}

	@Test
	public void testAddInputToUserBrickOnlyWhitespaceInput() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.user_data_user_brick_edit_field)).perform(replaceText(" \n"));
		onView(withText(R.string.name_consists_of_spaces_only)).check(matches(isDisplayed()));
		onView(withId(R.id.next)).check(matches(not(isEnabled())));
	}

	@Test
	public void testAddLabelToUserBrickConsecutiveLabels() {
		clickOnAddLabelToUserBrick();
		onView(withId(R.id.user_data_user_brick_edit_field)).perform(replaceText("Test1"));
		onView(withId(R.id.next))
				.perform(click());
		onView(withId(R.id.button_add_label))
				.perform(click());
		onView(withId(R.id.user_data_user_brick_edit_field)).perform(replaceText("Test"));
		onView(withId(R.id.next))
				.perform(click());
		onView(withId(R.id.button_add_label))
				.perform(click());
		onView(withText("Test1")).check(doesNotExist());
	}

	@Test
	public void testAddInputToUserBrickSameInput() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.next))
				.perform(click());
		onView(withId(R.id.button_add_input))
				.perform(click());
		onView(withId(R.id.user_data_user_brick_edit_field)).perform(replaceText(baseActivityTestRule.getActivity().getString(R.string.brick_user_defined_default_input_name)));
		onView(withText(R.string.name_already_exists)).check(matches(isDisplayed()));
		onView(withId(R.id.next)).check(matches(not(isEnabled())));
	}

	@Test
	public void testAddUserDefinedBrickToScriptFragment() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.next))
				.perform(click());
		onView(withId(R.id.confirm))
				.perform(click());

		onView(withId(R.id.fragment_script)).perform(waitFor(isDisplayed(), waitThreshold));

		ScriptFragment scriptFragment = ((ScriptFragment) baseActivityTestRule.getActivity()
				.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG));
		assertTrue(scriptFragment.isCurrentlyMoving());
		onView(withId(R.id.fragment_script)).perform(click());

		selectYourBricks();
		onData(allOf(is(instanceOf(UserDefinedBrick.class))))
				.inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(0)
				.perform(click());

		onView(withId(R.id.fragment_script)).perform(waitFor(isDisplayed(), waitThreshold));

		assertTrue(scriptFragment.isCurrentlyMoving());
	}

	@Test
	public void testEditFormulaInUserDefinedBrickWithInput() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.next))
				.perform(click());
		onView(withId(R.id.confirm))
				.perform(click());

		onView(withId(R.id.fragment_script)).perform(waitFor(isDisplayed(), waitThreshold));

		onView(withId(R.id.fragment_script)).perform(click());

		selectYourBricks();
		onData(allOf(is(instanceOf(UserDefinedBrick.class))))
				.inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(0)
				.perform(click());

		onView(withId(R.id.fragment_script)).perform(waitFor(isDisplayed(), waitThreshold));

		onBrickAtPosition(0)
				.perform(click());
		onBrickAtPosition(1)
				.perform(click());

		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testEditFormulaInUserDefinedBrickWithoutInput() {
		clickOnAddLabelToUserBrick();
		onView(withId(R.id.next))
				.perform(click());
		onView(withId(R.id.confirm))
				.perform(click());

		onView(withId(R.id.fragment_script)).perform(waitFor(isDisplayed(), waitThreshold));

		onView(withId(R.id.fragment_script)).perform(click());

		selectYourBricks();
		onData(allOf(is(instanceOf(UserDefinedBrick.class))))
				.inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(0)
				.perform(click());

		onView(withId(R.id.fragment_script)).perform(waitFor(isDisplayed(), waitThreshold));

		onBrickAtPosition(0)
				.perform(click());
		onBrickAtPosition(1)
				.perform(click());

		onView(withText(R.string.brick_context_dialog_formula_edit_brick))
				.check(doesNotExist());
	}

	private void selectYourBricks() {
		onView(withId(R.id.button_add))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)),
				is(UiTestUtils.getResourcesString(R.string.category_user_bricks))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
	}

	private void clickOnAddInputToUserBrick() {
		selectYourBricks();
		onView(withId(R.id.button_add_user_brick))
				.perform(click());
		onView(withId(R.id.button_add_input))
				.perform(click());
	}

	private void clickOnAddLabelToUserBrick() {
		selectYourBricks();
		onView(withId(R.id.button_add_user_brick))
				.perform(click());
		onView(withId(R.id.button_add_label))
				.perform(click());
	}

	@Test
	public void clickOnWithoutScreen() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.next)).perform(click());
		onView(withId(R.id.confirm)).perform(click());

		onView(withId(R.id.fragment_script)).perform(waitFor(isDisplayed(), waitThreshold));
		onView(withId(R.id.fragment_script)).perform(click());

		onBrickAtPosition(0).checkShowsText(R.string.brick_user_defined_script_screen_refresh_as);
		onBrickAtPosition(0).onSpinner(R.id.brick_set_screen_refresh_spinner)
				.checkShowsText(R.string.brick_user_defined_with_screen_refreshing);
		onBrickAtPosition(1).checkShowsText(R.string.brick_when_started);
	}
}
