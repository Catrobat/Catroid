/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class PlayMidiBricksTest {

	@Rule
	public BaseActivityTestRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(SpriteActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("playMidiBricksTest")
				.addBrick(new PlayNoteForBeatsBrick(28));
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPlayNoteForDurationUpdate() {
		int brickPosition = 1;

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_play_note_label_text);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.perform(click());
		onView(ViewMatchers.withId(R.id.piano_note_selector_view))
				.perform(ViewActions.click());

		onView(ViewMatchers.withId(R.id.piano_note_selector_view))
				.perform(pressBack());

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.checkShowsNumber(30);

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_duration)
				.performEnterNumber(1)
				.checkShowsNumber(1);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPlayNoteForDurationPianoViewLowerScrollingLimit() {

		int brickPosition = 1;
		int firstNoteMidiOfFirstOctave = 24;

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.perform(click());

		onView(ViewMatchers.withId(R.id.note_value)).perform(click());
		onView(ViewMatchers.withId(R.id.formula_editor_keyboard_2)).perform(ViewActions.click());
		onView(ViewMatchers.withId(R.id.formula_editor_keyboard_4)).perform(ViewActions.click());
		onView(ViewMatchers.withId(R.id.formula_editor_keyboard_ok)).perform(ViewActions.click());

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.perform(click());

		NoteName note = new NoteName(firstNoteMidiOfFirstOctave);
		onView(ViewMatchers.withId(R.id.piano_note_selector_view)).check(
				ViewAssertions.matches(ViewMatchers.withChild(
						ViewMatchers.withText(note.getName()))));

		onView(ViewMatchers.withId(R.id.piano_note_selector_previous_octave_button)).perform(ViewActions.click());

		onView(ViewMatchers.withId(R.id.piano_note_selector_view)).check(
				ViewAssertions.matches(ViewMatchers.withChild(
						ViewMatchers.withText(note.toString()))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPlayNoteForDurationPianoViewUpperScrollingLimit() {

		int brickPosition = 1;
		int firstNoteMidiOfLastOctave = 96;

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.perform(click());

		onView(ViewMatchers.withId(R.id.note_value)).perform(click());
		onView(ViewMatchers.withId(R.id.formula_editor_keyboard_9)).perform(ViewActions.click());
		onView(ViewMatchers.withId(R.id.formula_editor_keyboard_6)).perform(ViewActions.click());
		onView(ViewMatchers.withId(R.id.formula_editor_keyboard_ok)).perform(ViewActions.click());

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.perform(click());

		NoteName note = new NoteName(firstNoteMidiOfLastOctave);
		onView(ViewMatchers.withId(R.id.piano_note_selector_view)).check(
				ViewAssertions.matches(ViewMatchers.withChild(
						ViewMatchers.withText(note.getName()))));

		onView(ViewMatchers.withId(R.id.piano_note_selector_next_octave_button)).perform(ViewActions.click());

		onView(ViewMatchers.withId(R.id.piano_note_selector_view)).check(
				ViewAssertions.matches(ViewMatchers.withChild(
						ViewMatchers.withText(note.getName()))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPlayNoteForDurationPianoViewScrolling() {

		int brickPosition = 1;

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.perform(click());

		onView(ViewMatchers.withId(R.id.piano_note_selector_next_octave_button)).perform(ViewActions.click());

		onView(ViewMatchers.withId(R.id.piano_note_selector_view))
				.perform(ViewActions.click());

		onView(ViewMatchers.withId(R.id.piano_note_selector_view))
				.perform(pressBack());

		onBrickAtPosition(brickPosition).onFormulaTextField(R.id.brick_play_note_edit_note)
				.checkShowsNumber(42);
	}
}
