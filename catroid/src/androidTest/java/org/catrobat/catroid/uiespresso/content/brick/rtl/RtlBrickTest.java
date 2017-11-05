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
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.CameraBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ChooseCameraBrick;
import org.catrobat.catroid.content.bricks.ClearBackgroundBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.content.bricks.FlashBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoAnimationsBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpHighBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoJumpLongBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoMoveForwardBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateLeftBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoRotateRightBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoSoundBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTakingPictureBrick;
import org.catrobat.catroid.content.bricks.JumpingSumoTurnBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick;
import org.catrobat.catroid.content.bricks.LegoEv3SetLedBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick;
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.PreviousLookBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiPwmBrick;
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetNfcTagBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StampBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.ui.activity.rtl.RtlUiTestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.LANGUAGE_TAG_KEY;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_ARDUINO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_NFC_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PARROT_JUMPING_SUMO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_RASPI_BRICKS;
import static org.catrobat.catroid.uiespresso.util.matchers.rtl.RtlViewDirection.isViewRtl;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class RtlBrickTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);
	private Locale arLocale = new Locale("ar");
	private Locale defaultLocale = Locale.getDefault();
	private List<String> allPeripheralCategories = new ArrayList<>(Arrays.asList(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, SETTINGS_SHOW_PHIRO_BRICKS,
			SETTINGS_SHOW_ARDUINO_BRICKS, SETTINGS_SHOW_RASPI_BRICKS, SETTINGS_SHOW_NFC_BRICKS, SETTINGS_SHOW_PARROT_JUMPING_SUMO_BRICKS));
	private List<String> enabledByThisTestPeripheralCategories = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		createProject("RtlBricksTest");
		SettingsActivity.updateLocale(getTargetContext(), "ar", "");
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
		resetToDefaultLanguage();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void eventBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_event);

		// When program starts
		checkIfBrickISRtl(WhenStartedBrick.class, R.id.brick_when_started_layout);

		// When tapped
		checkIfBrickISRtl(WhenBrick.class, R.id.brick_when_layout);

		// When screen is touched
		checkIfBrickISRtl(WhenTouchDownBrick.class, R.id.brick_when_screen_touched_layout);

		// When you receive
		checkIfBrickISRtl(BroadcastReceiverBrick.class, R.id.brick_broadcast_receive_layout);

		// Broadcast
		checkIfBrickAtPositionIsRtl(BroadcastBrick.class, 0, R.id.brick_broadcast_layout);

		// Broadcast and wait
		checkIfBrickISRtl(BroadcastWaitBrick.class, R.id.brick_broadcast_wait_layout);

		// When  becomes true
		checkIfBrickISRtl(WhenConditionBrick.class, R.id.brick_when_condition_layout);

		// When physical collision with
		checkIfBrickISRtl(CollisionReceiverBrick.class, R.id.brick_collision_receive_layout);

		// When Background changes to
		checkIfBrickISRtl(WhenBackgroundChangesBrick.class, R.id.brick_when_background_layout);

		// When I start as a clone
		checkIfBrickISRtl(WhenClonedBrick.class, R.id.brick_when_cloned_layout);

		// when NFC
		checkIfBrickISRtl(WhenNfcBrick.class, R.id.brick_when_nfc_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void controlBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_control);

		// wait
		checkIfBrickISRtl(WaitBrick.class, R.id.brick_wait_layout);

		// Note
		checkIfBrickISRtl(NoteBrick.class, R.id.brick_note_layout);

		// Forever
		checkIfBrickISRtl(ForeverBrick.class, R.id.brick_forever_layout);

		// if is true then ... else ...
		checkIfBrickAtPositionIsRtl(IfLogicBeginBrick.class, 0, R.id.brick_if_begin_layout);

		// if is true then
		checkIfBrickISRtl(IfThenLogicBeginBrick.class, R.id.brick_if_begin_layout);

		// wait until  is true
		checkIfBrickISRtl(WaitUntilBrick.class, R.id.brick_wait_until_layout);

		// repeat times
		checkIfBrickISRtl(RepeatBrick.class, R.id.brick_repeat_layout);

		// repeat until is true
		checkIfBrickISRtl(RepeatUntilBrick.class, R.id.brick_repeat_until_layout);

		// continue scene
		checkIfBrickISRtl(SceneTransitionBrick.class, R.id.brick_scene_transition_layout);

		// start scene
		checkIfBrickISRtl(SceneStartBrick.class, R.id.brick_scene_start_layout);

		// stop scripts
		checkIfBrickISRtl(StopScriptBrick.class, R.id.brick_stop_script_layout);

		// create clone of
		checkIfBrickISRtl(CloneBrick.class, R.id.brick_clone_layout);

		// delete this clone
		checkIfBrickISRtl(DeleteThisCloneBrick.class, R.id.brick_delete_clone_layout);

		// when start as a clone
		checkIfBrickISRtl(WhenClonedBrick.class, R.id.brick_when_cloned_layout);

		// set next NFC tag to
		checkIfBrickISRtl(SetNfcTagBrick.class, R.id.brick_set_nfc_tag_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void motionBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_motion);

		//place at
		checkIfBrickISRtl(PlaceAtBrick.class, R.id.brick_place_at_layout);

		//set x to
		checkIfBrickISRtl(SetXBrick.class, R.id.brick_set_x_layout);

		//set y to
		checkIfBrickISRtl(SetYBrick.class, R.id.brick_set_y_layout);

		// change x by
		checkIfBrickISRtl(ChangeXByNBrick.class, R.id.brick_change_x_layout);

		//change y by
		checkIfBrickISRtl(ChangeYByNBrick.class, R.id.brick_change_y_layout);

		// go to
		checkIfBrickISRtl(GoToBrick.class, R.id.brick_go_to_layout);

		//move steps
		checkIfBrickISRtl(MoveNStepsBrick.class, R.id.brick_move_n_steps_layout);

		//turn left 15 degrees
		checkIfBrickISRtl(TurnLeftBrick.class, R.id.brick_turn_left_layout);

		//turn right 15 degrees
		checkIfBrickISRtl(TurnRightBrick.class, R.id.brick_turn_right_layout);

		//point in direction
		checkIfBrickISRtl(PointInDirectionBrick.class, R.id.brick_point_in_direction_layout);

		// point in towards
		checkIfBrickISRtl(PointToBrick.class, R.id.brick_point_to_layout);

		//set rotation style
		checkIfBrickISRtl(SetRotationStyleBrick.class, R.id.brick_set_rotation_style_normal_layout);

		//Glide second
		checkIfBrickISRtl(GlideToBrick.class, R.id.brick_glide_to_layout);

		//vibrate for second
		checkIfBrickISRtl(VibrationBrick.class, R.id.brick_vibration_layout);

		//set motion type
		checkIfBrickISRtl(SetPhysicsObjectTypeBrick.class, R.id.brick_set_physics_object_layout);

		//set velocity
		checkIfBrickISRtl(SetVelocityBrick.class, R.id.brick_set_velocity_layout);

		//rotate left degrees/second
		checkIfBrickISRtl(TurnLeftSpeedBrick.class, R.id.brick_turn_left_speed_layout);

		//rotate right degrees/second
		checkIfBrickISRtl(TurnRightSpeedBrick.class, R.id.brick_turn_right_speed_layout);

		//set gravity for all objects to
		checkIfBrickISRtl(SetGravityBrick.class, R.id.brick_set_gravity_layout);

		//set mass to kilogram
		checkIfBrickISRtl(SetMassBrick.class, R.id.brick_set_mass_layout);

		//set bounce factor to
		checkIfBrickISRtl(SetBounceBrick.class, R.id.brick_set_bounce_factor_layout);

		//set friction to
		checkIfBrickISRtl(SetFrictionBrick.class, R.id.brick_set_friction_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void soundBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_sound);

		// Start sound
		checkIfBrickISRtl(PlaySoundBrick.class, R.id.brick_play_sound_layout);

		// Start sound and wait
		checkIfBrickISRtl(PlaySoundAndWaitBrick.class, R.id.brick_play_sound_and_wait_layout);

		// Stop all sounds
		checkIfBrickISRtl(StopAllSoundsBrick.class, R.id.brick_stop_all_sounds_layout);

		// Set volume to
		checkIfBrickISRtl(SetVolumeToBrick.class, R.id.brick_set_volume_to_layout);

		// Change volume by
		checkIfBrickISRtl(ChangeVolumeByNBrick.class, R.id.brick_change_volume_by_layout);

		// Speak
		checkIfBrickISRtl(SpeakBrick.class, R.id.brick_speak_layout);

		// Speak and wait
		checkIfBrickISRtl(SpeakAndWaitBrick.class, R.id.brick_speak_and_wait_layout);

		// Play Phiro music tone
		checkIfBrickISRtl(PhiroPlayToneBrick.class, R.id.brick_phiro_play_tone_layout);

		// Ask and store spoken answer
		checkIfBrickISRtl(AskSpeechBrick.class, R.id.brick_set_variable_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void looksBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_looks);

		// Next background
		checkIfBrickISRtl(NextLookBrick.class, R.id.brick_next_look_layout);

		// Previous background
		checkIfBrickISRtl(PreviousLookBrick.class, R.id.brick_previous_look_layout);

		// Set size to
		checkIfBrickISRtl(SetSizeToBrick.class, R.id.brick_set_size_to_layout);

		// Change size by
		checkIfBrickISRtl(ChangeSizeByNBrick.class, R.id.brick_change_size_by_layout);

		// Hide
		checkIfBrickISRtl(HideBrick.class, R.id.brick_hide_layout);

		// Show
		checkIfBrickISRtl(ShowBrick.class, R.id.brick_show_layout);

		// Ask and store written answer in
		checkIfBrickISRtl(AskBrick.class, R.id.brick_set_variable_layout);

		// Set transparency to
		checkIfBrickISRtl(SetTransparencyBrick.class, R.id.brick_set_transparency_layout);

		// Change transparency by
		checkIfBrickISRtl(ChangeTransparencyByNBrick.class, R.id.brick_change_transparency_layout);

		// Set brightness to
		checkIfBrickISRtl(SetBrightnessBrick.class, R.id.brick_set_brightness_layout);

		// Change brightness by
		checkIfBrickISRtl(ChangeBrightnessByNBrick.class, R.id.brick_change_brightness_layout);

		// Set color to
		checkIfBrickISRtl(SetColorBrick.class, R.id.brick_set_color_layout);

		// Change color to
		checkIfBrickISRtl(ChangeColorByNBrick.class, R.id.brick_change_color_by_layout);

		// Clear graphic effects
		checkIfBrickISRtl(ClearGraphicEffectBrick.class, R.id.brick_clear_graphic_effect_layout);

		// When background changes to
		checkIfBrickISRtl(WhenBackgroundChangesBrick.class, R.id.brick_when_background_layout);

		// Set background
		checkIfBrickAtPositionIsRtl(SetBackgroundBrick.class, 0, R.id.brick_set_look_layout);

		// Set background and wait
		checkIfBrickAtPositionIsRtl(SetBackgroundAndWaitBrick.class, 0, R.id.brick_set_look_layout);

		// Set background to number
		checkIfBrickAtPositionIsRtl(SetBackgroundByIndexBrick.class, 1, R.id.brick_set_look_by_index_layout);

		// Set background to number and wait
		checkIfBrickISRtl(SetBackgroundByIndexAndWaitBrick.class, R.id.brick_set_look_by_index_layout);

		// Turn camera
		checkIfBrickISRtl(CameraBrick.class, R.id.brick_video_layout);

		// Use camera
		checkIfBrickISRtl(ChooseCameraBrick.class, R.id.brick_choose_camera_layout);

		// Turn flashlight
		checkIfBrickISRtl(FlashBrick.class, R.id.brick_flash_layout);

		// Set Phiro light
		checkIfBrickISRtl(PhiroRGBLightBrick.class, R.id.brick_phiro_rgb_led_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void penBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_pen);

		// Pen down
		checkIfBrickISRtl(PenDownBrick.class, R.id.brick_pen_down_layout);

		// Pen up
		checkIfBrickISRtl(PenUpBrick.class, R.id.brick_pen_up_layout);

		// Set Pen size to
		checkIfBrickISRtl(SetPenSizeBrick.class, R.id.brick_set_pen_size_layout);

		// Set Pen color to RGB
		checkIfBrickISRtl(SetPenColorBrick.class, R.id.brick_set_pen_color_layout);

		// Stamp
		checkIfBrickISRtl(StampBrick.class, R.id.brick_stamp_layout);

		// clear
		checkIfBrickISRtl(ClearBackgroundBrick.class, R.id.brick_clear_background_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void dataBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_data);

		// Set variable
		checkIfBrickISRtl(SetVariableBrick.class, R.id.brick_set_variable_layout);

		// Change Variable by
		checkIfBrickISRtl(ChangeVariableBrick.class, R.id.brick_change_variable_layout);

		// Show Variable
		checkIfBrickISRtl(ShowTextBrick.class, R.id.brick_show_variable_layout);

		// Hide Variable
		checkIfBrickISRtl(HideTextBrick.class, R.id.brick_hide_variable_layout);

		// Add to List
		checkIfBrickISRtl(AddItemToUserListBrick.class, R.id.brick_add_item_to_userlist_layout);

		// Delete item from List at position
		checkIfBrickISRtl(DeleteItemOfUserListBrick.class, R.id.brick_delete_item_of_userlist_layout);

		// Insert into list at position
		checkIfBrickISRtl(InsertItemIntoUserListBrick.class, R.id.brick_insert_item_into_userlist_layout);

		// Replace item in List at Position with
		checkIfBrickISRtl(ReplaceItemInUserListBrick.class, R.id.brick_replace_item_in_userlist_layout);

		// Ask  and store written answer in
		checkIfBrickISRtl(AskBrick.class, R.id.brick_set_variable_layout);

		// Ask and store spoken answer in
		checkIfBrickISRtl(AskSpeechBrick.class, R.id.brick_set_variable_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void legoNxtBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_lego_nxt);

		// Turn NXT motor by
		checkIfBrickISRtl(LegoNxtMotorTurnAngleBrick.class, R.id.brick_nxt_motor_turn_layout);

		// Stop NXT motor
		checkIfBrickISRtl(LegoNxtMotorStopBrick.class, R.id.brick_nxt_motor_stop_layout);

		// Set NXT motor to speed
		checkIfBrickISRtl(LegoNxtMotorMoveBrick.class, R.id.brick_nxt_motor_action_layout);

		// Play NXT tone
		checkIfBrickISRtl(LegoNxtPlayToneBrick.class, R.id.brick_nxt_play_tone_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void legoEv3Bricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_lego_ev3);

		// Turn Ev3 motor by
		checkIfBrickISRtl(LegoEv3MotorTurnAngleBrick.class, R.id.brick_ev3_motor_turn_layout);

		// Set Ev3 motor to speed
		checkIfBrickISRtl(LegoEv3MotorMoveBrick.class, R.id.brick_ev3_motor_move_layout);

		// Stop Ev3 motor
		checkIfBrickISRtl(LegoEv3MotorStopBrick.class, R.id.brick_ev3_motor_stop_layout);

		// Play Ev3 tone
		checkIfBrickISRtl(LegoEv3PlayToneBrick.class, R.id.brick_ev3_play_tone_layout);

		// Set Ev3 Led status
		checkIfBrickISRtl(LegoEv3SetLedBrick.class, R.id.brick_ev3_set_led_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void arDroneBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_drone);

		// Take off
		checkIfBrickISRtl(DroneTakeOffLandBrick.class, R.id.brick_drone_basic_layout);

		//Flip
		checkIfBrickISRtl(DroneFlipBrick.class, R.id.brick_drone_flip_layout);

		// Emergency
		checkIfBrickISRtl(DroneEmergencyBrick.class, R.id.brick_drone_basic_layout);

		// Move up
		checkIfBrickISRtl(DroneMoveUpBrick.class, R.id.brick_drone_move_layout);

		// Move down
		checkIfBrickISRtl(DroneMoveDownBrick.class, R.id.brick_drone_move_layout);

		// Move Left
		checkIfBrickISRtl(DroneMoveLeftBrick.class, R.id.brick_drone_move_layout);

		// Move Right
		checkIfBrickISRtl(DroneMoveRightBrick.class, R.id.brick_drone_move_layout);

		// Move forward
		checkIfBrickISRtl(DroneMoveForwardBrick.class, R.id.brick_drone_move_layout);

		// Move Backward
		checkIfBrickISRtl(DroneMoveBackwardBrick.class, R.id.brick_drone_move_layout);

		// Turn Left
		checkIfBrickISRtl(DroneTurnLeftBrick.class, R.id.brick_drone_move_layout);

		// Turn Right
		checkIfBrickISRtl(DroneTurnRightBrick.class, R.id.brick_drone_move_layout);

		// Switch Drone camera
		checkIfBrickISRtl(DroneSwitchCameraBrick.class, R.id.brick_drone_basic_look_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void jumpingSumoBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_jumping_sumo);

		// Move Jumping Sumo forward
		checkIfBrickISRtl(JumpingSumoMoveForwardBrick.class, R.id.brick_jumping_sumo_move_forward_layout);

		// Move Jumping Sumo backward
		checkIfBrickISRtl(JumpingSumoMoveBackwardBrick.class, R.id.brick_jumping_sumo_move_backward_layout);

		// Animations Jumping sumo
		checkIfBrickISRtl(JumpingSumoAnimationsBrick.class, R.id.brick_jumping_sumo_animation_layout);

		// Sound
		checkIfBrickISRtl(JumpingSumoSoundBrick.class, R.id.brick_jumping_sumo_sound_layout);

		// No Jumping Sumo sound
		checkIfBrickISRtl(JumpingSumoNoSoundBrick.class, R.id.brick_jumping_sumo_nosound_layout);

		// Jump Jumping Sumo Long
		checkIfBrickISRtl(JumpingSumoJumpLongBrick.class, R.id.brick_jumping_sumo_jump_long_layout);

		// Jump Jumping Sumo High
		checkIfBrickISRtl(JumpingSumoJumpHighBrick.class, R.id.brick_jumping_sumo_jump_high_layout);

		// Rotate Jumping Sumo Left
		checkIfBrickISRtl(JumpingSumoRotateLeftBrick.class, R.id.brick_jumping_sumo_rotate_left_layout);

		// Rotate Jumping Sumo Right
		checkIfBrickISRtl(JumpingSumoRotateRightBrick.class, R.id.brick_jumping_sumo_rotate_right_layout);

		// Turn Jumping sumo
		checkIfBrickISRtl(JumpingSumoTurnBrick.class, R.id.brick_jumping_sumo_turn_layout);

		// Taking Pic
		checkIfBrickISRtl(JumpingSumoTakingPictureBrick.class, R.id.brick_jumping_sumo_taking_picture_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void phiroBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_phiro);

		// Move forward
		checkIfBrickISRtl(PhiroMotorMoveForwardBrick.class, R.id.brick_phiro_motor_forward_action_layout);

		// Move Backward
		checkIfBrickISRtl(PhiroMotorMoveBackwardBrick.class, R.id.brick_phiro_motor_backward_action_layout);

		// Stop Phiro Motor
		checkIfBrickISRtl(PhiroMotorStopBrick.class, R.id.brick_phiro_motor_stop_layout);

		// Play Phiro music tone
		checkIfBrickISRtl(PhiroPlayToneBrick.class, R.id.brick_phiro_play_tone_layout);

		// Set Phiro Light
		checkIfBrickISRtl(PhiroRGBLightBrick.class, R.id.brick_phiro_rgb_led_layout);

		// If Phiro
		checkIfBrickISRtl(PhiroIfLogicBeginBrick.class, R.id.brick_phiro_sensor_layout);

		// Set variable
		checkIfBrickAtPositionIsRtl(SetVariableBrick.class, 0, R.id.brick_set_variable_layout);
		checkIfBrickAtPositionIsRtl(SetVariableBrick.class, 1, R.id.brick_set_variable_layout);
		checkIfBrickAtPositionIsRtl(SetVariableBrick.class, 2, R.id.brick_set_variable_layout);
		checkIfBrickAtPositionIsRtl(SetVariableBrick.class, 3, R.id.brick_set_variable_layout);
		checkIfBrickAtPositionIsRtl(SetVariableBrick.class, 4, R.id.brick_set_variable_layout);
		checkIfBrickAtPositionIsRtl(SetVariableBrick.class, 5, R.id.brick_set_variable_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void arduinoBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_arduino);

		// Set digital pin
		checkIfBrickISRtl(ArduinoSendDigitalValueBrick.class, R.id.brick_arduino_send_digital_layout);

		// set PWM pin
		checkIfBrickISRtl(ArduinoSendPWMValueBrick.class, R.id.brick_arduino_send_analog_layout);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void raspPiBricks() throws Exception {
		assertEquals(Locale.getDefault().getDisplayLanguage(), arLocale.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		openCategory(R.string.category_raspi);

		// When RaspPi pin
		checkIfBrickISRtl(WhenRaspiPinChangedBrick.class, R.id.brick_raspi_when_layout);

		// If RaspPi pin is tru then
		checkIfBrickISRtl(RaspiIfLogicBeginBrick.class, R.id.brick_raspi_if_begin_layout);

		// Set raspPi Pin to
		checkIfBrickISRtl(RaspiSendDigitalValueBrick.class, R.id.brick_raspi_send_digital_layout);

		// Set raspPi PWM pin to
		checkIfBrickISRtl(RaspiPwmBrick.class, R.id.brick_raspi_pwm_layout);
	}

	private void checkIfBrickISRtl(Class brickClass, int bricksId) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withId(bricksId))
				.check(matches(isViewRtl()));
	}

	private void checkIfBrickAtPositionIsRtl(Class brickClass, int position, int brickId) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(position)
				.onChildView(withId(brickId))
				.check(matches(isViewRtl()));
	}

	private void createProject(String projectName) {
		String nameSpriteTwo = "testSpriteTwo";

		Project project = new Project(null, projectName);
		Sprite spriteOne = new Sprite("testSpriteOne");
		project.getDefaultScene().addSprite(spriteOne);

		Sprite spriteTwo = new Sprite(nameSpriteTwo);
		Script script = new StartScript();
		spriteTwo.addScript(script);

		project.getDefaultScene().addSprite(spriteTwo);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(spriteTwo);
	}

	private void resetToDefaultLanguage() {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit();
		editor.putString(LANGUAGE_TAG_KEY, defaultLocale.getLanguage());
		editor.commit();
		SettingsActivity.updateLocale(InstrumentationRegistry.getTargetContext(), defaultLocale.getLanguage(),
				defaultLocale.getCountry());
	}

	private void openCategory(int categoryNameStringResourceId) {
		onView(withId(R.id.button_add))
				.perform(click());

		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(categoryNameStringResourceId))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
	}
}
