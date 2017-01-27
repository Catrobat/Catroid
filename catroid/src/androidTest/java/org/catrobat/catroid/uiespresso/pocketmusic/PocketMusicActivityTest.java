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
package org.catrobat.catroid.uiespresso.pocketmusic;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.ui.TactScrollRecyclerView;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;
import org.catrobat.catroid.pocketmusic.ui.TrackView;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.RecyclerViewMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class PocketMusicActivityTest {

	private IdlingResource idlingResource;

	public static ViewAction clickNoteViewAtPosition(final int position) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return null;
			}

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public void perform(UiController uiController, View view) {
				TrackView trackView = ((TrackView) view);
				int row = position / TrackRowView.QUARTER_COUNT;
				int col = position % TrackRowView.QUARTER_COUNT;
				if (row < TrackView.ROW_COUNT && col < TrackRowView.QUARTER_COUNT) {
					trackView.getTrackRowViews().get(row).getNoteViews().get(col).performClick();
				}
			}
		};
	}

	public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
		return new RecyclerViewMatcher(recyclerViewId);
	}

	private static Matcher<View> withToggledNoteView(final int position) {
		return new BoundedMatcher<View, TrackView>(TrackView.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("No Noteview Toggled");
			}

			@Override
			protected boolean matchesSafely(TrackView item) {
				int row = position / TrackRowView.QUARTER_COUNT;
				int col = position % TrackRowView.QUARTER_COUNT;
				return item.getTrackRowViews().get(row).getNoteViews().get(col).isToggled();
			}
		};
	}

	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule =
			new BaseActivityInstrumentationRule<>(MainMenuActivity.class);

	@Before
	public void registerIdlingResource() {
		//class under test, in this case the mainMenuActivity has to implement getIdlingResource that returns
		//the idlingResource instance it has. (in this case its a countingIdlingResource)
		idlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);
	}

	@Before
	public void setUp() throws Exception {
		UiTestUtils.createProject("pocketMusicActivityTest");
	}

	private static void navigateToPocketMusicActivity() {
		onView(withId(R.id.main_menu_button_continue)).perform(click());
		onView(withText(R.string.background)).perform(click());
		onView(withText(R.string.sounds)).perform(click());
		onView(withId(R.id.button_add)).perform(click());
		onView(withId(R.id.dialog_new_sound_pocketmusic)).perform(click());
	}

	private Activity getCurrentActivity() {
		Activity currentActivity = UiTestUtils.getCurrentActivity();
		if (currentActivity == null) {
			fail("No current Activity!");
		}
		return currentActivity;
	}

	@Test
	public void toggleRandomNoteViews() {

		navigateToPocketMusicActivity();

		TactScrollRecyclerView recyclerView = (TactScrollRecyclerView) getCurrentActivity()
				.findViewById(R.id.tact_scroller);

		int recyclerViewItemCount = recyclerView.getAdapter().getItemCount();

		int[] randomPosition = new int[recyclerViewItemCount];

		Random random = new Random();
		for (int i = 0; i < recyclerViewItemCount; i++) {
			randomPosition[i] = random.nextInt(TrackRowView.QUARTER_COUNT * TrackView.ROW_COUNT);
			onView(withId(R.id.tact_scroller))
					.perform(actionOnItemAtPosition(i, clickNoteViewAtPosition(randomPosition[i])));
			onView(withRecyclerView(R.id.tact_scroller).atPosition(i))
					.check(matches(withToggledNoteView(randomPosition[i])));
		}

		pressBack();
		onView(withText(R.string.pocketmusic_recorded_filename)).perform(click());

		for (int i = 0; i < recyclerViewItemCount; i++) {
			onView(withId(R.id.tact_scroller)).perform(scrollToPosition(i));
			onView(withRecyclerView(R.id.tact_scroller).atPosition(i))
					.check(matches(withToggledNoteView(randomPosition[i])));
		}
	}

	@Test
	public void playButtonElementExists() {
		navigateToPocketMusicActivity();
		onView(withId(R.id.pocketmusic_play_button)).check(matches(isDisplayed()));
	}

	@Test
	public void testOrientationChange() {
		navigateToPocketMusicActivity();
		Activity currentActivity = getCurrentActivity();
		currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@After
	public void tearDown() throws Exception {
	}

	@After
	public void unregisterResource() {
		Espresso.unregisterIdlingResources(idlingResource);
	}
}
