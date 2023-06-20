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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.achievements.Achievement;
import org.catrobat.catroid.achievements.AchievementActivity;
import org.catrobat.catroid.achievements.AchievementSystem;
import org.catrobat.catroid.achievements.AchievementsListActivity;

import static org.hamcrest.Matchers.anything;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;



import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class AchievementsListActivityTest {

	private final AchievementSystem achievementSystem = AchievementSystem.getInstance();
	private final Context context = ApplicationProvider.getApplicationContext();
	private Intent intent;

	@Before
	public void setUP()
	{
		intent = new Intent(context,
				AchievementsListActivity.class);
		achievementSystem.reset();
		achievementSystem.setPreferences(ApplicationProvider.getApplicationContext());
		SharedPreferences.Editor editor = achievementSystem.getEditor();
		String testKey1 = "test_key_1";
		editor.putBoolean(testKey1, false);
		String testKey2 = "test_key_2";
		editor.putBoolean(testKey2, true);
		editor.commit();
		String testTitle1 = "test_title_1";
		Achievement testAchievement1 = new Achievement(testTitle1, testKey1,R.drawable.test_image);
		String testTitle2 = "test_title_2";
		Achievement testAchievement2 = new Achievement(testTitle2, testKey2,R.drawable.test_image);
		achievementSystem.addAchievement(testAchievement1);
		achievementSystem.addAchievement(testAchievement2);

	}

	@Test
	public void achievementsListActivityIsDisplayed()
	{
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)){
			scenario.moveToState(Lifecycle.State.RESUMED);

			onView(withId(R.id.achievementsListView)).check(matches(isDisplayed()));
		}
	}
	@Test
	public void achievementListViewItemTitle(){
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onData(anything()).inAdapterView(withId(R.id.achievementsListView))
					.atPosition(0).onChildView(withId(R.id.achievementTitle)).check(matches(isDisplayed()));
		}
	}

	@Test
	public void openAchievement() {
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);

			onData(anything()).inAdapterView(withId(R.id.achievementsListView))
					.atPosition(0).perform(click());

			onView(withId(R.id.BigAchievementImage)).check(matches(isDisplayed()));

		}
	}

	@Test
	public void backToAchievementsListActivity()
	{
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);

			onData(anything()).inAdapterView(withId(R.id.achievementsListView))
					.atPosition(0).perform(click());

			onView(withId(R.id.BigAchievementImage)).check(matches(isDisplayed()));
			pressBack();

			onView(withId(R.id.achievementsListView)).check(matches(isDisplayed()));
		}
	}

	@Test
	public void listItemImageIsGrey()
	{
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onData(anything()).inAdapterView(withId(R.id.achievementsListView))
					.atPosition(0).onChildView(withId(R.id.achievementImage)).check(matches(new TypeSafeMatcher<View>() {
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
	public void listItemImageIsInColor()
	{
		try (ActivityScenario<AchievementActivity> scenario = ActivityScenario.launch(intent)) {
			scenario.moveToState(Lifecycle.State.RESUMED);
			onData(anything()).inAdapterView(withId(R.id.achievementsListView))
					.atPosition(1).onChildView(withId(R.id.achievementImage)).check(matches(new TypeSafeMatcher<View>() {
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