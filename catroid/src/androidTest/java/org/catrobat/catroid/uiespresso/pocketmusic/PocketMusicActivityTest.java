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

import android.content.Intent;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.pocketmusic.ui.TactScrollRecyclerView;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;
import org.catrobat.catroid.pocketmusic.ui.TrackView;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class PocketMusicActivityTest {

	private static final int TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON = 3;
	@Rule
	public BaseActivityInstrumentationRule<PocketMusicActivity> pocketMusicActivityRule =
			new BaseActivityInstrumentationRule<>(PocketMusicActivity.class, true, false);

	public static ViewAction toggleNoteViewAtPositionInTact(final int position) {

		final int row = position / TrackRowView.QUARTER_COUNT;
		final int col = position % TrackRowView.QUARTER_COUNT;

		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isDisplayed();
			}

			@Override
			public String getDescription() {
				String description;
				if (row < TrackView.ROW_COUNT && col < TrackRowView.QUARTER_COUNT) {
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
				if (row < TrackView.ROW_COUNT && col < TrackRowView.QUARTER_COUNT) {
					trackView.getTrackRowViews().get(row).getNoteViews().get(col).performClick();
				}
			}
		};
	}

	@Before
	public void startPocketMusicActivityWithEmptyProject() {
		createProject("pocketMusicInputTest");
		pocketMusicActivityRule.launchActivity(null);
	}

	public static RecyclerViewMatcher withTactScroller(final int recyclerViewId) {
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

	@Test
	public void toggleRandomNoteViewsAndAddTacts() {

		TactScrollRecyclerView recyclerView = (TactScrollRecyclerView) pocketMusicActivityRule
				.getActivity().findViewById(R.id.tact_scroller);

		int[] randomPosition = new int[TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON];

		Random random = new Random();
		for (int i = 0; i < TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON; i++) {
			randomPosition[i] = random.nextInt(TrackRowView.QUARTER_COUNT * TrackView.ROW_COUNT);

			if (i == recyclerView.getAdapter().getItemCount() - 1) {
				onView(withId(R.id.tact_scroller)).perform(RecyclerViewActions.actionOnItemAtPosition(i,
						click()));
			}

			onView(withId(R.id.tact_scroller))
					.perform(actionOnItemAtPosition(i, toggleNoteViewAtPositionInTact(randomPosition[i])));
			onView(withTactScroller(R.id.tact_scroller).atPosition(i))
					.check(matches(withToggledNoteView(randomPosition[i])));
		}

		relaunchActivityOpenJustSavedFile();

		for (int i = 0; i < TACT_COUNT_TO_TOGGLE_RANDOM_NOTE_ON; i++) {
			onView(withId(R.id.tact_scroller)).perform(scrollToPosition(i));
			onView(withTactScroller(R.id.tact_scroller).atPosition(i))
					.check(matches(withToggledNoteView(randomPosition[i])));
		}
	}

	private void relaunchActivityOpenJustSavedFile() {
		pocketMusicActivityRule.getActivity().finish();

		List<SoundInfo> soundInfo = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		assertNotNull("Soundinfo not found", soundInfo);
		assertFalse("Soundinfo not found", soundInfo.isEmpty());

		Intent pocketMusicDataIntent = new Intent();
		pocketMusicDataIntent.putExtra("FILENAME", soundInfo.get(0).getSoundFileName());
		pocketMusicDataIntent.putExtra("TITLE", soundInfo.get(0).getTitle());

		pocketMusicActivityRule.launchActivity(pocketMusicDataIntent);
		onView(withId(android.R.id.content)).check(matches(isDisplayed()));
	}

	@Test
	public void playButtonElementExists() {
		onView(withId(R.id.pocketmusic_play_button)).check(matches(isDisplayed()));
	}

	public static Project createProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		SetVariableBrick setVariableBrick = new SetVariableBrick();
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		UserVariable userVariable = dataContainer.addProjectUserVariable("Global1");
		setVariableBrick.setUserVariable(userVariable);

		script.addBrick(setVariableBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		return project;
	}
}
