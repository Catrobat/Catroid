/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.achievement;



import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.achievements.AchievementActivity;

import org.hamcrest.Description;

import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AchievementActivityTest{

	private Intent intent;
	private final String title_string = "test_Title";
	private final String description_string = "test_description";

	@Before
	public void setUP()
	{
		intent = new Intent(ApplicationProvider.getApplicationContext(),
				AchievementActivity.class);
		intent.putExtra("Image", R.drawable.test_image);
		intent.putExtra("Title", title_string);
		intent.putExtra("Description", description_string);
	}


	@Test
	public void bigAchievementImageIsDisplayed()
	{
		intent.putExtra("Unlocked", false);
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)){
			scenario.moveToState(Lifecycle.State.RESUMED);
			onView(withId(R.id.BigAchievementImage)).check(matches(isDisplayed()));
		}
	}

	@Test
	public void bigAchievementTitleIsRight()
	{
		intent.putExtra("Unlocked", false);
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)){
			scenario.moveToState(Lifecycle.State.RESUMED);
			onView(withId(R.id.BigAchievementTitle)).check(matches(withText(title_string)));
		}


	}

	@Test
	public void bigAchievementDescriptionIsRight()
	{
		intent.putExtra("Unlocked", false);
		try (ActivityScenario<AchievementActivity> scenario =ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onView(withId(R.id.BigAchievementDescription)).check(matches(withText(description_string)));
		}
	}
	@Test
	public void bigAchievementTitleIsDisplayed()
	{
		intent.putExtra("Unlocked", false);
		try (ActivityScenario<AchievementActivity> scenario =ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onView(withId(R.id.BigAchievementTitle)).check(matches(isDisplayed()));
		}
	}

	@Test
	public void bigAchievementDescriptionIsDisplayed()
	{
		intent.putExtra("Unlocked", false);
		try (ActivityScenario<AchievementActivity> scenario =ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onView(withId(R.id.BigAchievementDescription)).check(matches(isDisplayed()));
		}
	}

	@Test
	public void bigAchievementImageHasColorFilter()
	{
		intent.putExtra("Unlocked", false);
		try (ActivityScenario<AchievementActivity> scenario =ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onView(withId(R.id.BigAchievementImage)).check(matches(isDisplayed()));

			onView(withId(R.id.BigAchievementImage)).check(matches(new TypeSafeMatcher<View>() {
				@Override
				protected boolean matchesSafely(View item) {
					if (!(item instanceof ImageView)) {
						return false;
					}

					return ((ImageView) item).getColorFilter() != null;
				}

				@Override
				public void describeTo(Description description) {

				}
			}));
		}
	}

	@Test
	public void bigAchievementImageIsInColor() {
		intent.putExtra("Unlocked", true);
		try (ActivityScenario<AchievementActivity> scenario =ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onView(withId(R.id.BigAchievementImage)).check(matches(isDisplayed()));

			onView(withId(R.id.BigAchievementImage)).check(matches(new TypeSafeMatcher<View>() {
				@Override
				protected boolean matchesSafely(View item) {
					if (!(item instanceof ImageView)) {
						return false;
					}

					return ((ImageView) item).getColorFilter() == null;
				}

				@Override
				public void describeTo(Description description) {

				}
			}));
		}
	}

}