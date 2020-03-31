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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class ProjectSaveAfterUploadTest {
	private static final String PROJECT_NAME = "SaveAfterUploadTestPrj1";

	@Rule
	public ActivityTestRule<ProjectListActivity> activityTestRule =
			new ActivityTestRule<ProjectListActivity>(ProjectListActivity.class, true, false);

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup && parentMatcher.matches(parent)
						&& view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}

	@Before
	public void setUp() throws Exception {
		Project project = new Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("firstSprite");
		project.getDefaultScene().addSprite(firstSprite);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		XstreamSerializer.getInstance().saveProject(project);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void projectSaveAfterUploadTest() {
		String newName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
		activityTestRule.launchActivity(null);
		onView(withText(PROJECT_NAME)).perform(click());
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getContext());
		onView(withText("Upload")).perform(click());
		pressBack();
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getContext());
		onView(withText("Rename")).perform(click());

		ViewInteraction appCompatCheckBox = onView(
				allOf(withId(R.id.checkbox),
						childAtPosition(
								childAtPosition(
										withId(R.id.recycler_view),
										1),
								0),
						isDisplayed()));
		appCompatCheckBox.perform(click());

		ViewInteraction actionMenuItemView = onView(
				allOf(withId(R.id.confirm), withContentDescription("Confirm"),
						childAtPosition(
								childAtPosition(
										withId(R.id.action_mode_bar),
										2),
								0),
						isDisplayed()));
		actionMenuItemView.perform(click());

		ViewInteraction textInputEditText2 = onView(
				allOf(withId(R.id.input_edit_text),
						childAtPosition(
								childAtPosition(
										withId(R.id.input),
										0),
								0),
						isDisplayed()));
		textInputEditText2.perform(replaceText(newName));

		onView(withText("OK")).perform(click());

		pressBack();
		//open again
		onView(withText(PROJECT_NAME)).perform(click());

		ViewInteraction textView = onView(
				allOf(withId(R.id.title_view), withText(newName),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.instanceOf(android.widget.LinearLayout.class),
										1),
								0),
						isDisplayed()));
		textView.check(matches(withText(newName)));

		pressBack();
		onView(withText(PROJECT_NAME)).perform(longClick());
		onView(withText("Delete")).perform(click());
		onView(withText("YES")).perform(click());
	}
}
