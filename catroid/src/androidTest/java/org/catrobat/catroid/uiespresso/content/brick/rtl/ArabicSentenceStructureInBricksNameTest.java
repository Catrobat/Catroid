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

package org.catrobat.catroid.uiespresso.content.brick.rtl;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.SetNfcTagBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.ui.activity.rtl.RtlUiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.ScriptListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.PositionAssertions.isBelow;
import static android.support.test.espresso.assertion.PositionAssertions.isLeftOf;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_ARDUINO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_NFC_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PARROT_JUMPING_SUMO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_RASPI_BRICKS;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.catrobat.catroid.uiespresso.util.matchers.rtl.RtlViewDirection.isViewRtl;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class ArabicSentenceStructureInBricksNameTest {
	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
	private Locale arLocale = new Locale("ar");
	private List<String> allPeripheralCategories = new ArrayList<>(Arrays.asList(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, SETTINGS_SHOW_PHIRO_BRICKS,
			SETTINGS_SHOW_ARDUINO_BRICKS, SETTINGS_SHOW_RASPI_BRICKS, SETTINGS_SHOW_NFC_BRICKS, SETTINGS_SHOW_PARROT_JUMPING_SUMO_BRICKS));
	private List<String> enabledByThisTestPeripheralCategories = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		createProject("ArabicSentenceInBricksName");
		SettingsActivity.setLanguageSharedPreference(getTargetContext(), "ar");
		baseActivityTestRule.launchActivity();

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

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testElementsOrderInAdaptedBricksForArabicLanguage() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirectionIsRtl(Locale.getDefault().getDisplayName()));

		// if is true then ... else ...
		checkIfBrickISRtl(IfLogicBeginBrick.class, R.id.brick_if_begin_layout);
		onView(withId(R.id.if_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.if_label_second_part))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.if_label)));

		onView(withId(R.id.brick_if_begin_edit_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.if_label_second_part)));

		// repeat until is true
		checkIfBrickISRtl(RepeatUntilBrick.class, R.id.brick_repeat_until_layout);
		onView(withId(R.id.brick_repeat_until_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.wait_until_label_second_part))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id
						.brick_repeat_until_label)));

		onView(withId(R.id.brick_repeat_until_edit_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.wait_until_label_second_part)));

		// Set Ev3 motor to speed
		checkIfBrickISRtl(LegoEv3MotorMoveBrick.class, R.id.brick_ev3_motor_move_layout);
		onView(withId(R.id.brick_ev3_motor_move_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_ev3_motor_move_spinner))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_ev3_motor_move_label)));

		onView(withId(R.id.ev3_motor_move_speed_edit_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_ev3_motor_move_speed_text)));

		onView(withId(R.id.brick_ev3_motor_move_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.ev3_motor_move_speed_edit_text)));

		// Set NXT motor to speed
		checkIfBrickISRtl(LegoNxtMotorMoveBrick.class, R.id.brick_nxt_motor_action_layout);
		onView(withId(R.id.lego_motor_action_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.lego_motor_action_spinner))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.lego_motor_action_label)));

		onView(withId(R.id.motor_action_speed_edit_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.lego_motor_action_speed_text)));

		onView(withId(R.id.lego_motor_action_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.motor_action_speed_edit_text)));

		// Move Jumping Sumo backward
		checkIfBrickISRtl(JumpingSumoMoveBackwardBrick.class, R.id.brick_jumping_sumo_move_backward_layout);
		onView(withId(R.id.brick_jumping_sumo_move_backward_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_jumping_sumo_move_backward_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_jumping_sumo_move_backward_label)));

		onView(withId(R.id.brick_jumping_sumo_move_forward_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_backward_edit_text_second)));

		onView(withId(R.id.brick_jumping_sumo_move_backward_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_forward_text_second)));

		onView(withId(R.id.brick_jumping_sumo_move_backward_set_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_backward_text_with)));

		onView(withId(R.id.brick_jumping_sumo_move_backward_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_backward_set_power)));

		onView(withId(R.id.brick_jumping_sumo_move_backward_set_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_backward_edit_text_power)));

		// Move Jumping Sumo forward
		checkIfBrickISRtl(JumpingSumoMoveForwardBrick.class, R.id.brick_jumping_sumo_move_forward_layout);
		onView(withId(R.id.brick_jumping_sumo_move_forward_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_jumping_sumo_move_forward_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_jumping_sumo_move_forward_label)));

		onView(withId(R.id.brick_jumping_sumo_move_forward_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_forward_edit_text_second)));

		onView(withId(R.id.brick_jumping_sumo_move_forward_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_forward_text_second)));

		onView(withId(R.id.brick_jumping_sumo_move_forward_set_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_forward_text_with)));

		onView(withId(R.id.brick_jumping_sumo_move_forward_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_forward_set_power)));

		onView(withId(R.id.brick_jumping_sumo_move_forward_set_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_move_forward_edit_text_power)));

		// JumpingSumoRotateLeftBrick
		checkIfBrickISRtl(JumpingSumoRotateLeftBrick.class, R.id.brick_jumping_sumo_rotate_left_layout);
		onView(withId(R.id.brick_jumping_sumo_rotate_left_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_jumping_sumo_change_left_variable_edit_text))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_jumping_sumo_rotate_left_label)));

		onView(withId(R.id.brick_jumping_sumo_rotate_left_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_change_left_variable_edit_text)));

		// JumpingSumoRotateRightBrick
		checkIfBrickISRtl(JumpingSumoRotateRightBrick.class, R.id.brick_jumping_sumo_rotate_right_layout);
		onView(withId(R.id.brick_jumping_sumo_rotate_right_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_jumping_sumo_change_right_variable_edit_text))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_jumping_sumo_rotate_right_label)));

		onView(withId(R.id.brick_jumping_sumo_rotate_right_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_jumping_sumo_change_right_variable_edit_text)));

		// set next NFC tag to
		checkIfBrickISRtl(SetNfcTagBrick.class, R.id.brick_set_nfc_tag_layout);
		onView(withId(R.id.brick_set_nfc_tag_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_set_nfc_tag_edit_text))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_set_nfc_tag_label)));

		onView(withId(R.id.brick_set_nfc_tag_ndef_record_textview))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_set_nfc_tag_edit_text)));

		onView(withId(R.id.brick_set_nfc_tag_ndef_record_spinner))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_set_nfc_tag_ndef_record_textview)));

		// wait until  is true
		checkIfBrickISRtl(WaitUntilBrick.class, R.id.brick_wait_until_layout);
		onView(withId(R.id.wait_until_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.wait_until_label_second_part))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.wait_until_label)));

		onView(withId(R.id.brick_wait_until_edit_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.wait_until_label_second_part)));

		// When  becomes true
		checkIfBrickISRtl(WhenConditionBrick.class, R.id.brick_when_condition_layout);
		onView(withId(R.id.when_conditon_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.when_condition_label_second_part))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.when_conditon_label)));

		onView(withId(R.id.brick_when_condition_edit_text))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.when_condition_label_second_part)));

		// DroneMoveDownBrick
		checkIfBrickISRtl(DroneMoveDownBrick.class, R.id.brick_drone_move_down_layout);
		onView(withId(R.id.brick_drone_move_down_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_move_down_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_move_down_label)));

		onView(withId(R.id.brick_drone_move_down_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_down_edit_text_second)));

		onView(withId(R.id.brick_drone_move_down_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_down_text_second)));

		onView(withId(R.id.brick_drone_move_down_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_down_text_with)));

		onView(withId(R.id.brick_drone_move_down_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_down_power)));

		onView(withId(R.id.brick_drone_move_down_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_down_edit_text_power)));

		// DroneMoveUpBrick
		checkIfBrickISRtl(DroneMoveUpBrick.class, R.id.brick_drone_move_up_layout);
		onView(withId(R.id.brick_drone_move_up_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_move_up_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_move_up_label)));

		onView(withId(R.id.brick_drone_move_up_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_up_edit_text_second)));

		onView(withId(R.id.brick_drone_move_up_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_up_text_second)));

		onView(withId(R.id.brick_drone_move_up_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_up_text_with)));

		onView(withId(R.id.brick_drone_move_up_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_up_power)));

		onView(withId(R.id.brick_drone_move_up_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_up_edit_text_power)));

		//DroneMoveLeftBrick
		checkIfBrickISRtl(DroneMoveLeftBrick.class, R.id.brick_drone_move_left_layout);
		onView(withId(R.id.brick_drone_move_left_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_move_left_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_move_left_label)));

		onView(withId(R.id.brick_drone_move_left_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_left_edit_text_second)));

		onView(withId(R.id.brick_drone_move_left_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_left_text_second)));

		onView(withId(R.id.brick_drone_move_left_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_left_text_with)));

		onView(withId(R.id.brick_drone_move_left_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_left_power)));

		onView(withId(R.id.brick_drone_move_left_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_left_edit_text_power)));

		//DroneMoveRightBrick
		checkIfBrickISRtl(DroneMoveRightBrick.class, R.id.brick_drone_move_right_layout);
		onView(withId(R.id.brick_drone_move_right_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_move_right_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_move_right_label)));

		onView(withId(R.id.brick_drone_move_right_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_right_edit_text_second)));

		onView(withId(R.id.brick_drone_move_right_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_right_text_second)));

		onView(withId(R.id.brick_drone_move_right_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_right_text_with)));

		onView(withId(R.id.brick_drone_move_right_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_right_power)));

		onView(withId(R.id.brick_drone_move_right_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_right_edit_text_power)));

		//DroneMoveBackwardBrick
		checkIfBrickISRtl(DroneMoveBackwardBrick.class, R.id.brick_drone_move_backward_layout);
		onView(withId(R.id.brick_drone_move_backward_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_move_backward_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_move_backward_label)));

		onView(withId(R.id.brick_drone_move_backward_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_backward_edit_text_second)));

		onView(withId(R.id.brick_drone_move_backward_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_backward_text_second)));

		onView(withId(R.id.brick_drone_move_backward_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_backward_text_with)));

		onView(withId(R.id.brick_drone_move_backward_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_backward_power)));

		onView(withId(R.id.brick_drone_move_backward_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_backward_edit_text_power)));

		//DroneMoveForwardBrick
		checkIfBrickISRtl(DroneMoveForwardBrick.class, R.id.brick_drone_move_forward_layout);
		onView(withId(R.id.brick_drone_move_forward_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_move_forward_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_move_forward_label)));

		onView(withId(R.id.brick_drone_move_forward_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_forward_edit_text_second)));

		onView(withId(R.id.brick_drone_move_forward_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_forward_text_second)));

		onView(withId(R.id.brick_drone_move_forward_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_forward_text_with)));

		onView(withId(R.id.brick_drone_move_forward_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_forward_power)));

		onView(withId(R.id.brick_drone_move_forward_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_move_forward_edit_text_power)));

		//DroneTurnLeftBrick
		checkIfBrickISRtl(DroneTurnLeftBrick.class, R.id.brick_drone_turn_left_layout);
		onView(withId(R.id.brick_drone_turn_left_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_turn_left_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_turn_left_label)));

		onView(withId(R.id.brick_drone_turn_left_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_left_edit_text_second)));

		onView(withId(R.id.brick_drone_turn_left_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_left_text_second)));

		onView(withId(R.id.brick_drone_turn_left_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_left_text_with)));

		onView(withId(R.id.brick_drone_turn_left_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_left_power)));

		onView(withId(R.id.brick_drone_turn_left_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_left_edit_text_power)));

		//DroneTurnRightBrick
		checkIfBrickISRtl(DroneTurnRightBrick.class, R.id.brick_drone_turn_right_layout);
		onView(withId(R.id.brick_drone_turn_right_label))
				.check(matches(isDisplayed()));

		onView(withId(R.id.brick_drone_turn_right_edit_text_second))
				.check(matches(isDisplayed()))
				.check(isBelow(withId(R.id.brick_drone_turn_right_label)));

		onView(withId(R.id.brick_drone_turn_right_text_second))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_right_edit_text_second)));

		onView(withId(R.id.brick_drone_turn_right_text_with))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_right_text_second)));

		onView(withId(R.id.brick_drone_turn_right_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_right_text_with)));

		onView(withId(R.id.brick_drone_turn_right_edit_text_power))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_right_power)));

		onView(withId(R.id.brick_drone_turn_right_percent))
				.check(matches(isDisplayed()))
				.check(isLeftOf(withId(R.id.brick_drone_turn_right_edit_text_power)));
	}

	@After
	public void tearDown() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
		for (String category : enabledByThisTestPeripheralCategories) {
			sharedPreferences.edit().putBoolean(category, false).commit();
		}
		enabledByThisTestPeripheralCategories.clear();
		resetToDefaultLanguage();
	}

	public static void resetToDefaultLanguage() {
		SettingsActivity.removeLanguageSharedPreference(getTargetContext());
	}

	private void createProject(String projectName) {
		String nameSpriteTwo = "testSpriteTwo";

		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite spriteOne = new Sprite("testSpriteOne");
		project.getDefaultScene().addSprite(spriteOne);

		Sprite spriteTwo = new Sprite(nameSpriteTwo);
		Script script = new StartScript();

		script.addBrick(new IfLogicBeginBrick());
		script.addBrick(new RepeatUntilBrick(5));

		script.addBrick(new LegoEv3MotorMoveBrick(LegoEv3MotorMoveBrick.Motor.MOTOR_B, 5));
		script.addBrick(new LegoNxtMotorMoveBrick(LegoNxtMotorMoveBrick.Motor.MOTOR_B_C, 10));

		script.addBrick(new JumpingSumoMoveBackwardBrick(2000, 20));
		script.addBrick(new JumpingSumoMoveForwardBrick(2000, 20));

		script.addBrick(new JumpingSumoRotateLeftBrick(90));
		script.addBrick(new JumpingSumoRotateRightBrick(90));

		script.addBrick(new SetNfcTagBrick(getResourcesString(R.string.brick_set_nfc_tag_default_value)));
		script.addBrick(new WaitUntilBrick(new Formula(1)));
		Formula condition = new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR,
				Sensors.COLLIDES_WITH_FINGER.name(), null));

		script.addBrick(new WhenConditionBrick(condition));

		script.addBrick(new DroneMoveDownBrick(2000, 20));
		script.addBrick(new DroneMoveUpBrick(2000, 20));
		script.addBrick(new DroneMoveLeftBrick(2000, 20));
		script.addBrick(new DroneMoveRightBrick(2000, 20));
		script.addBrick(new DroneMoveBackwardBrick(2000, 20));
		script.addBrick(new DroneMoveForwardBrick(2000, 20));
		script.addBrick(new DroneTurnLeftBrick(2000, 20));
		script.addBrick(new DroneTurnRightBrick(2000, 20));

		spriteTwo.addScript(script);
		project.getDefaultScene().addSprite(spriteTwo);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(spriteTwo);
	}

	private void checkIfBrickISRtl(Class brickClass, int bricksId) {
		onData(instanceOf(brickClass)).inAdapterView(ScriptListMatchers.isScriptListView())
				.onChildView(withId(bricksId))
				.check(matches(isDisplayed()))
				.check(matches(isViewRtl()));
	}
}
