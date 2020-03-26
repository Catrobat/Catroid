/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class AddUserDefinedBrickTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(AddUserDefinedBrickTest.class.getSimpleName());
	}

	@Before
	public void setUp() throws IOException {
		BrickTestUtils.createProjectAndGetStartScript(AddUserDefinedBrickTest.class.getSimpleName());
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void checkPlusButtonExists() {
		selectYourBricks();
		onView(withId(R.id.button_add_user_brick))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testAddInputToUserBrickFragmentShown() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.fragment_add_input_to_user_brick)).check(matches(isDisplayed()));
	}

	@Test
	public void testAddInputToUserBrickDefaultText() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.input_user_brick_edit_field)).check(matches(withText(R.string.brick_user_defined_default_input_name)));
	}

	@Test
	public void testAddInputToUserBrickEmptyInput() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.input_user_brick_edit_field)).perform(replaceText(""));
		onView(withText(R.string.name_empty)).check(matches(isDisplayed()));
		onView(withId(R.id.next)).check(matches(not(isEnabled())));
	}

	@Test
	public void testAddInputToUserBrickOnlyWhitespaceInput() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.input_user_brick_edit_field)).perform(replaceText(" \n"));
		onView(withText(R.string.name_consists_of_spaces_only)).check(matches(isDisplayed()));
		onView(withId(R.id.next)).check(matches(not(isEnabled())));
	}

	@Test
	public void testAddInputToUserBrickSameInput() {
		clickOnAddInputToUserBrick();
		onView(withId(R.id.next))
				.perform(click());
		onView(withId(R.id.button_add_label))
				.perform(click());
		onView(withId(R.id.button_add_input))
				.perform(click());
		onView(withId(R.id.input_user_brick_edit_field)).perform(replaceText(baseActivityTestRule.getActivity().getString(R.string.brick_user_defined_default_input_name)));
		onView(withText(R.string.name_already_exists)).check(matches(isDisplayed()));
		onView(withId(R.id.next)).check(matches(not(isEnabled())));
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
}
