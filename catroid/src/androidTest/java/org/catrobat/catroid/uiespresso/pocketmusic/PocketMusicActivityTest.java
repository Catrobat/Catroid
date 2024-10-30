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
package org.catrobat.catroid.uiespresso.pocketmusic;

import android.content.Intent;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.pocketmusic.ui.TactScrollRecyclerView;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;
import org.catrobat.catroid.pocketmusic.ui.TrackView;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.util.SystemAnimations;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PocketMusicActivityTest {

	private static final int TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON = 3;
	@Rule
	public BaseActivityTestRule<PocketMusicActivity> pocketMusicActivityRule =
			new BaseActivityTestRule<>(PocketMusicActivity.class, true, false);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	// For testing all Animations are disabled, this causes troubles, because the PocketMusic
	// functionality is highly coupled with the Animation-Framework. To avoid rewrites of
	// PocketMusic simply activate the animations for that single Test-Class.
	// See setUp()- and tearDown()-methods.
	private static SystemAnimations systemAnimations = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		systemAnimations = new SystemAnimations(InstrumentationRegistry.getInstrumentation());
		systemAnimations.storeCurrentSettings();
		systemAnimations.enableAll();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		systemAnimations.resetToStoredSettings();
		systemAnimations = null;
	}

	public static ViewAction toggleNoteViewAtPositionInTact(final int position) {

		final int row = position / TrackRowView.INITIAL_QUARTER_COUNT;
		final int col = position % TrackRowView.INITIAL_QUARTER_COUNT;

		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isDisplayed();
			}

			@Override
			public String getDescription() {
				String description;
				if (row < TrackView.ROW_COUNT && col < TrackRowView.INITIAL_QUARTER_COUNT) {
					description = String.format(Locale.getDefault(), "Toggle note at row %d, and columnt %d", row, col);
				} else {
					description = "Do not toggle note because the index is out of bounds";
				}
				return description;
			}

			@Override
			public void perform(UiController uiController, View view) {
				uiController.loopMainThreadUntilIdle();
				TrackView trackView = ((TrackView) view);
				if (row < TrackView.ROW_COUNT && col < TrackRowView.INITIAL_QUARTER_COUNT) {
					trackView.getTrackRowViews().get(row).getNoteViews().get(col).performClick();
				}
			}
		};
	}

	@Before
	public void startPocketMusicActivityWithEmptyProject() {
		UiTestUtils.createDefaultTestProject("pocketMusicInputTest");
		pocketMusicActivityRule.launchActivity(null);
	}

	public static RecyclerViewMatcher withTactScroller(final int recyclerViewId) {
		return new RecyclerViewMatcher(recyclerViewId);
	}

	private static Matcher<View> isNoteViewToggled(final int position) {
		return new BoundedMatcher<View, TrackView>(TrackView.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("No Noteview Toggled");
			}

			@Override
			protected boolean matchesSafely(TrackView item) {
				int row = position / TrackRowView.INITIAL_QUARTER_COUNT;
				int col = position % TrackRowView.INITIAL_QUARTER_COUNT;
				return item.getTrackRowViews().get(row).getNoteViews().get(col).isToggled();
			}
		};
	}

	private static Matcher<View> isRecyclerViewSizeZero() {
		return new BoundedMatcher<View, TactScrollRecyclerView>(TactScrollRecyclerView.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("The calculated size of the RecyclerView is 0.");
			}

			@Override
			protected boolean matchesSafely(TactScrollRecyclerView item) {
				return item.getMeasuredHeight() > 0 && item.getMeasuredWidth() > 0;
			}
		};
	}

	private static Matcher<View> isRecyclerViewPlaying() {
		return new BoundedMatcher<View, TactScrollRecyclerView>(TactScrollRecyclerView.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("The music doesn't play long enough.");
			}

			@Override
			protected boolean matchesSafely(TactScrollRecyclerView item) {
				return item.isPlaying();
			}
		};
	}

	@Test
	@Category({Level.Functional.class, Cat.PocketMusicUiTests.class})
	public void toggleRandomNoteViewsAndAddTacts() {
		onView(withId(R.id.tact_scroller)).check(matches(isRecyclerViewSizeZero()));

		TactScrollRecyclerView recyclerView = pocketMusicActivityRule
				.getActivity().findViewById(R.id.tact_scroller);

		int[] randomPosition = new int[TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON];

		Random random = new Random();
		for (int i = 0; i < TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON; i++) {
			randomPosition[i] = random.nextInt(TrackRowView.INITIAL_QUARTER_COUNT * TrackView.ROW_COUNT);

			if (i == recyclerView.getAdapter().getItemCount() - 1) {
				onView(withId(R.id.tact_scroller))
						.perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
			}

			onView(withId(R.id.tact_scroller))
					.perform(actionOnItemAtPosition(i, toggleNoteViewAtPositionInTact(randomPosition[i])));
			onView(withTactScroller(R.id.tact_scroller).atPosition(i))
					.check(matches(isNoteViewToggled(randomPosition[i])));
		}

		relaunchActivityOpenJustSavedFile();

		for (int i = 0; i < TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON; i++) {
			onView(withId(R.id.tact_scroller)).perform(scrollToPosition(i));
			onView(withTactScroller(R.id.tact_scroller).atPosition(i))
					.check(matches(isNoteViewToggled(randomPosition[i])));
		}
	}

	private void relaunchActivityOpenJustSavedFile() {
		pocketMusicActivityRule.getActivity().finish();

		List<SoundInfo> sounds = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		assertNotNull(sounds);
		assertFalse(sounds.isEmpty());

		Intent pocketMusicDataIntent = new Intent();
		pocketMusicDataIntent.putExtra(PocketMusicActivity.TITLE, sounds.get(0).getName());
		pocketMusicDataIntent.putExtra(PocketMusicActivity.ABSOLUTE_FILE_PATH,
				sounds.get(0).getFile().getAbsolutePath());

		pocketMusicActivityRule.launchActivity(pocketMusicDataIntent);
		onView(withId(android.R.id.content)).check(matches(isDisplayed()));
	}

	@Test
	@Category({Level.Functional.class, Cat.PocketMusicUiTests.class})
	@Flaky
	public void playButtonDoesPlay() {
		onView(withId(R.id.pocketmusic_play_button)).check(matches(isDisplayed()));

		onView(withId(R.id.tact_scroller)).perform(swipeLeft());

		onView(withTactScroller(R.id.tact_scroller).atPosition(2)).perform(click());

		onView(withId(R.id.tact_scroller)).perform(RecyclerViewActions.actionOnItemAtPosition(2,
				toggleNoteViewAtPositionInTact(1)));

		onView(withId(R.id.pocketmusic_play_button)).perform(click());

		onView(withId(R.id.tact_scroller)).check(matches(isRecyclerViewPlaying()));
	}
}
