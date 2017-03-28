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

package org.catrobat.catroid.uiespresso.content.brick;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_ARDUINO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_NFC_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_RASPI_BRICKS;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class BrickValueParameterTest {

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private List<String> allPeripheralCategories = new ArrayList<>(Arrays.asList(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED,	SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, SETTINGS_SHOW_PHIRO_BRICKS,
			SETTINGS_SHOW_ARDUINO_BRICKS, SETTINGS_SHOW_RASPI_BRICKS, SETTINGS_SHOW_NFC_BRICKS));
	private List<String> enabledByThisTestPeripheralCategories = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("goToBrickTest1").addBrick(new GoToBrick());
		baseActivityTestRule.launchActivity(null);

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		for (String category : allPeripheralCategories) {
			boolean categoryEnabled = sharedPreferences.getBoolean(category, false);
			if (!categoryEnabled) {
				sharedPreferences.edit().putBoolean(category, true).commit();
				enabledByThisTestPeripheralCategories.add(category);
			}
		}
	}

	@After
	public void tearDown() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
		for (String category : enabledByThisTestPeripheralCategories) {
			sharedPreferences.edit().putBoolean(category, false).commit();
		}
		enabledByThisTestPeripheralCategories.clear();
	}

	@Test
	public void categoriesTest() {
		onView(withId(R.id.button_add))
				.perform(click());
		onView(isRoot()).perform(CustomActions.wait(2000));

		List<Integer> categoryResourceStrings = Arrays.asList(R.string.category_event,
				R.string.category_control,
				R.string.category_motion,
				R.string.category_sound,
				R.string.category_looks,
				R.string.category_pen,
				R.string.category_data,
				R.string.category_lego_nxt,
				R.string.category_lego_ev3,
				R.string.category_user_bricks,
				R.string.category_arduino,
				R.string.category_drone,
				R.string.category_phiro,
				R.string.category_raspi);

		for (Integer categoryStringResource : categoryResourceStrings) {
			onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(categoryStringResource))))
					.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
					.check(matches(isDisplayed()));
		}
	}

	@Test
	public void testSoundBricksDefaultValues() {
		openCategory(R.string.category_sound);

		//start sound - spinner "New..:"
		checkIfBrickShowsText(PlaySoundBrick.class, R.string.brick_play_sound);
		checkIfBrickShowsSpinnerWithText(PlaySoundBrick.class, R.id.playsound_spinner, R.string.new_broadcast_message);

		//start sound and wait  - spinner "new..:"
		checkIfBrickShowsText(PlaySoundAndWaitBrick.class, R.string.brick_play_sound_and_wait);
		checkIfBrickShowsSpinnerWithText(PlaySoundAndWaitBrick.class, R.id.playsound_spinner, R.string.new_broadcast_message);

		//stop all sounds
		checkIfBrickShowsText(StopAllSoundsBrick.class, R.string.brick_stop_all_sounds);

		//set volume to - edit text "60%"
		checkIfBrickShowsText(SetVolumeToBrick.class, R.string.brick_set_volume_to);
		checkIfBrickShowsText(SetVolumeToBrick.class, R.string.percent_symbol);
		checkIfBrickShowsEditTextWithText(SetVolumeToBrick.class, R.id.brick_set_volume_to_edit_text, "60.0");

		//change volume by - edit text "-10.0"
		checkIfBrickShowsText(ChangeVolumeByNBrick.class, R.string.brick_change_volume_by);
		checkIfBrickShowsEditTextWithText(ChangeVolumeByNBrick.class, R.id.brick_change_volume_by_edit_text, "-10.0");

		//speak - edit text "hello"
		checkIfBrickShowsText(SpeakBrick.class, R.string.brick_speak);
		checkIfBrickShowsEditTextWithText(SpeakBrick.class, R.id.brick_speak_edit_text,	R.string.brick_speak_default_value);

		//speak and wait  - edit text "hello"
		checkIfBrickShowsText(SpeakAndWaitBrick.class, R.string.brick_speak_and_wait);
		checkIfBrickShowsEditTextWithText(SpeakAndWaitBrick.class, R.id.brick_speak_and_wait_edit_text,	R.string.brick_speak_default_value);

		//ask and store - edit text "whats your name" - spinner "new"
		checkIfBrickShowsText(AskSpeechBrick.class, R.string.brick_ask_speech_label);
		checkIfBrickShowsEditTextWithText(AskSpeechBrick.class, R.id.brick_ask_speech_question_edit_text,
				R.string.brick_ask_speech_default_question);
		checkIfBrickShowsText(AskSpeechBrick.class, R.string.brick_ask_speech_store);
		//TODO see testAskSpeechBrickInSoundDefaultValues below
		onData(instanceOf(AskSpeechBrick.class)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				//.onChildView(withSpinnerText(R.string.new_broadcast_message))  //this breaks because of weird spinner
				.onChildView(withId(R.id.brick_ask_speech_spinner))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testAskSpeechBrickInSoundDefaultValues() {
		//TODO Fix whatever breaks the spinner text test on this brick
		//TODO and then move it back up into testSoundBricksDefaultValues
		openCategory(R.string.category_sound);
		checkIfBrickShowsText(AskSpeechBrick.class, R.string.brick_ask_speech_label);
		checkIfBrickShowsEditTextWithText(AskSpeechBrick.class, R.id.brick_ask_speech_question_edit_text,
				R.string.brick_ask_speech_default_question);
		checkIfBrickShowsText(AskSpeechBrick.class, R.string.brick_ask_speech_store);
		checkIfBrickShowsSpinnerWithText(AskSpeechBrick.class, R.id.brick_ask_speech_spinner, R.string.new_broadcast_message);
	}

	@Test
	public void testControlBricksDefaultValues() {
		openCategory(R.string.category_control);

		//wait - edit text "1" - second
		checkIfBrickShowsText(WaitBrick.class, R.string.brick_wait);
		checkIfBrickShowsEditTextWithText(WaitBrick.class, R.id.brick_wait_edit_text, "1");
		checkIfBrickShowsText(WaitBrick.class, UiTestUtils.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(1)));

		//note - edit text "add comment here..."
		checkIfBrickShowsText(NoteBrick.class, R.string.brick_note);
		checkIfBrickShowsEditTextWithText(NoteBrick.class, R.id.brick_note_edit_text, R.string.brick_note_default_value);

		//forever
		checkIfBrickShowsText(ForeverBrick.class, R.string.brick_forever);

		//if - edit text "1 st 2" - is true then ... else ...
		int ifBrickPosition = 0;
		checkIfBrickAtPositionShowsText(IfLogicBeginBrick.class, ifBrickPosition, R.string.brick_if_begin);
		checkIfBrickAtPositionShowsEditTextWithText(IfLogicBeginBrick.class, ifBrickPosition, R.id.brick_if_begin_edit_text, "1 < 2");
		checkIfBrickAtPositionShowsText(IfLogicBeginBrick.class, ifBrickPosition,  R.string.brick_if_begin_second_part);
		checkIfBrickAtPositionShowsText(IfLogicBeginBrick.class, ifBrickPosition,  R.string.brick_if_else);

		// if - edit text "1 st 2" - is true then
		checkIfBrickShowsText(IfThenLogicBeginBrick.class, R.string.brick_if_begin);
		checkIfBrickShowsEditTextWithText(IfThenLogicBeginBrick.class, R.id.brick_if_begin_edit_text, "1 < 2");
		checkIfBrickShowsText(IfThenLogicBeginBrick.class, R.string.brick_if_begin_second_part);

		//wait until  - edit text "1 st 2" - is true
		checkIfBrickShowsText(WaitUntilBrick.class, R.string.brick_wait_until);
		checkIfBrickShowsEditTextWithText(WaitUntilBrick.class, R.id.brick_wait_until_edit_text, "1 < 2");
		checkIfBrickShowsText(WaitUntilBrick.class, R.string.brick_wait_until_second_part);

		//repeat  - edit text "10" - times
		checkIfBrickShowsText(RepeatBrick.class, R.string.brick_repeat);
		checkIfBrickShowsEditTextWithText(RepeatBrick.class, R.id.brick_repeat_edit_text, "10");
		checkIfBrickShowsText(RepeatBrick.class, UiTestUtils.getResources().getQuantityString(R.plurals.time_plural,
				Utils.convertDoubleToPluralInteger(10)));

		//repeat until - edit text "1 st 2" - is true
		checkIfBrickShowsText(RepeatUntilBrick.class, R.string.brick_repeat_until);
		checkIfBrickShowsEditTextWithText(RepeatUntilBrick.class, R.id.brick_repeat_until_edit_text, "1 < 2");
		checkIfBrickShowsText(RepeatUntilBrick.class, R.string.brick_wait_until_second_part);

		//continue scene  - spinner "new..."
		checkIfBrickShowsText(SceneTransitionBrick.class, R.string.brick_scene_transition);
		checkIfBrickShowsSpinnerWithText(SceneTransitionBrick.class, R.id.brick_scene_transition_spinner, R.string.new_broadcast_message);

		//start scene - spinner "new..."
		checkIfBrickShowsText(SceneStartBrick.class, R.string.brick_scene_start);
		checkIfBrickShowsSpinnerWithText(SceneStartBrick.class, R.id.brick_scene_start_spinner, R.string
				.new_broadcast_message);

		//stop scripts - spinner "stop the script"
		checkIfBrickShowsText(StopScriptBrick.class, R.string.brick_stop_script);
		checkIfBrickShowsSpinnerWithText(StopScriptBrick.class, R.id.brick_stop_script_spinner, R.string.brick_stop_this_script);

		//create clone of  - spinner "myself"
		checkIfBrickShowsText(CloneBrick.class, R.string.brick_clone);
		checkIfBrickShowsSpinnerWithText(CloneBrick.class, R.id.brick_clone_spinner, R.string.brick_clone_this);

		//delete this clone
		checkIfBrickShowsText(DeleteThisCloneBrick.class, R.string.brick_delete_this_clone);

		//when start as a clone
		checkIfBrickShowsText(WhenClonedBrick.class, R.string.brick_when_cloned);
	}

	private void openCategory(int categoryNameStringResourceId) {
		onView(withId(R.id.button_add))
				.perform(click());

		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(categoryNameStringResourceId))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
	}

	private void checkIfBrickShowsText(Class brickClass, String text) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withText(text))
				.check(matches(isDisplayed()));
	}

	private void checkIfBrickShowsText(Class brickClass, int stringResourceId) {
		checkIfBrickShowsText(brickClass, UiTestUtils.getResourcesString(stringResourceId));
	}

	private void checkIfBrickShowsSpinnerWithText(Class brickClass, int spinnerResourceId, int stringResourceId) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withId(spinnerResourceId))
				.check(matches(withSpinnerText(stringResourceId)));
	}

	private void checkIfBrickShowsEditTextWithText(Class brickClass, int editTextResourceId, String text) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withId(editTextResourceId))
				.check(matches(withText(text)));
	}

	private void checkIfBrickShowsEditTextWithText(Class brickClass, int editTextResourceId, int stringResourceId) {
		checkIfBrickShowsEditTextWithText(brickClass, editTextResourceId, UiTestUtils.getResourcesString(stringResourceId));
	}

	private void checkIfBrickAtPositionShowsEditTextWithText(Class brickClass, int position, int editTextResourceId,
			String text) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(position)
				.onChildView(withId(editTextResourceId))
				.check(matches(withText(text)));
	}

	private void checkIfBrickAtPositionShowsText(Class brickClass, int position,  int stringResourceId) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(position)
				.onChildView(withText(stringResourceId))
				.check(matches(isDisplayed()));
	}
}
