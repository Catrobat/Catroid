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
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SpriteListFragmentExplanationTextProjectWithObjectTest {
	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("SpriteListFragmentExplanationTextProjectWithObjectTest");
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void spriteListFragmentExplanationTextIsDisplayedAfterDeleteObjects() {
		onView(withId(R.id.empty_view))
				.check(matches(not(isDisplayed())));
		onView(withText(R.string.fragment_sprite_text_description))
				.check(matches(not(isDisplayed())));

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());

		onRecyclerView().atPosition(1)
				.performCheckItem();

		onView(withId(R.id.confirm))
				.perform(click());

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		onView(withId(R.id.empty_view))
				.check(matches(isDisplayed()));
		onView(withText(R.string.fragment_sprite_text_description))
				.check(matches(isDisplayed()));
	}
}
