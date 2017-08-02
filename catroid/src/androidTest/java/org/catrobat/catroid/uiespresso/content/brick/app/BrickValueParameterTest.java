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

package org.catrobat.catroid.uiespresso.content.brick.app;

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
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick;
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
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick;
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
import org.catrobat.catroid.content.bricks.SayBubbleBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
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
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick;
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
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
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
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

	private String nameSpriteOne = "testSpriteOne";

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private List<String> allPeripheralCategories = new ArrayList<>(Arrays.asList(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, SETTINGS_SHOW_PHIRO_BRICKS,
			SETTINGS_SHOW_ARDUINO_BRICKS, SETTINGS_SHOW_RASPI_BRICKS, SETTINGS_SHOW_NFC_BRICKS));
	private List<String> enabledByThisTestPeripheralCategories = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		createProject("brickDefaultValueParameterTest");
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

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void categoriesTest() {
		onView(withId(R.id.button_add))
				.perform(click());

		List<Integer> categoryResourceStrings = Arrays.asList(
				R.string.category_event,
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

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testEventBricksDefaultValues() {
		openCategory(R.string.category_event);

		// When program starts
		checkIfBrickShowsText(WhenStartedBrick.class, R.string.brick_when_started);

		//When tapped
		checkIfBrickShowsText(WhenBrick.class, R.string.brick_when);

		//When screen is touched
		checkIfBrickShowsText(WhenTouchDownBrick.class, R.string.brick_when_touched);

		//When you receive
		checkIfBrickShowsText(BroadcastReceiverBrick.class, R.string.brick_broadcast_receive);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(BroadcastReceiverBrick.class, R.id.brick_broadcast_spinner,
				R.string.brick_broadcast_default_value);

		//Broadcast
		checkIfBrickAtPositionShowsText(BroadcastBrick.class, 0, R.string.brick_broadcast);
		checkIfBrickAtPositionShowsSpinnerWithText(BroadcastBrick.class, 0,
				R.id.brick_broadcast_spinner,
				R.string.brick_broadcast_default_value);

		//Broadcast and wait
		checkIfBrickShowsText(BroadcastWaitBrick.class, R.string.brick_broadcast_wait);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(BroadcastReceiverBrick.class, R.id.brick_broadcast_wait_spinner, R.string
				.brick_broadcast_default_value);

		//When  becomes true
		checkIfBrickShowsText(WhenConditionBrick.class, R.string.brick_when_becomes_true);
		checkIfBrickShowsText(WhenConditionBrick.class, R.string.brick_when_condition_when);
		checkIfBrickShowsEditTextWithText(WhenConditionBrick.class, R.id.brick_when_condition_edit_text, "1 < 2");

		//When physical collision with
		checkIfBrickShowsText(CollisionReceiverBrick.class, R.string.brick_collision_receive);
		onData(instanceOf(CollisionReceiverBrick.class)).inAdapterView(BrickPrototypeListMatchers
				.isBrickPrototypeView())
				.onChildView(withId(R.id.brick_collision_receive_spinner))
				.onChildView(withText(CollisionReceiverBrick.ANYTHING_ESCAPE_CHAR + "anything" + CollisionReceiverBrick
						.ANYTHING_ESCAPE_CHAR))
				.check(matches(isDisplayed()));

		//When Background changes to
		checkIfBrickShowsText(WhenBackgroundChangesBrick.class, R.string.brick_when_background);
		checkIfBrickShowsSpinnerWithText(WhenBackgroundChangesBrick.class,
				R.id.brick_when_background_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		// /When I start as a clone
		checkIfBrickShowsText(WhenClonedBrick.class, R.string.brick_when_cloned);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testMotionBricksDefaultValues() {
		openCategory(R.string.category_motion);

		//Place at
		checkIfBrickShowsText(PlaceAtBrick.class, R.string.brick_place_at);
		checkIfBrickShowsText(PlaceAtBrick.class, R.string.x_label);
		checkIfBrickShowsText(PlaceAtBrick.class, R.string.y_label);
		checkIfBrickShowsEditTextWithText(PlaceAtBrick.class, R.id.brick_place_at_edit_text_x, "100");
		checkIfBrickShowsEditTextWithText(PlaceAtBrick.class, R.id.brick_place_at_edit_text_y, "200");

		//Set X to
		checkIfBrickShowsText(SetXBrick.class, R.string.brick_set_x);
		checkIfBrickShowsText(SetXBrick.class, "100");

		//Set Y to
		checkIfBrickShowsText(SetYBrick.class, R.string.brick_set_y);
		checkIfBrickShowsText(SetYBrick.class, "200");

		//Change X by
		checkIfBrickShowsText(ChangeXByNBrick.class, R.string.brick_change_x_by);
		checkIfBrickShowsText(ChangeXByNBrick.class, "10");

		//Change Y by
		checkIfBrickShowsText(ChangeYByNBrick.class, R.string.brick_change_y_by);
		checkIfBrickShowsText(ChangeYByNBrick.class, "10");

		//Go to
		checkIfBrickShowsText(GoToBrick.class, R.string.brick_go_to);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(GoToBrick.class, R.id.brick_go_to_spinner, R.string.brick_go_to_touch_position);

		//If on edge, bounce
		checkIfBrickShowsText(IfOnEdgeBounceBrick.class, R.string.brick_if_on_edge_bounce);

		//Move  steps
		checkIfBrickShowsText(MoveNStepsBrick.class, R.string.brick_move);
		checkIfBrickShowsText(MoveNStepsBrick.class, "10");

		//Turn left degrees
		checkIfBrickShowsText(TurnLeftBrick.class, R.string.brick_turn_left);
		checkIfBrickShowsText(TurnLeftBrick.class, R.string.degrees);
		checkIfBrickShowsText(TurnLeftBrick.class, "15");

		//Turn right degrees
		checkIfBrickShowsText(TurnRightBrick.class, R.string.brick_turn_right);
		checkIfBrickShowsText(TurnRightBrick.class, R.string.degrees);
		checkIfBrickShowsText(TurnRightBrick.class, "15");

		//Point in direction degrees
		checkIfBrickShowsText(PointInDirectionBrick.class, R.string.brick_point_in_direction);
		checkIfBrickShowsText(PointInDirectionBrick.class, R.string.degrees);
		checkIfBrickShowsText(PointInDirectionBrick.class, "90");

		//Point towards
		checkIfBrickShowsText(PointToBrick.class, R.string.brick_point_to);
		checkIfBrickShowsSpinnerWithText(PointToBrick.class,
				R.id.brick_point_to_spinner,
				nameSpriteOne);

		//Set rotation style
		checkIfBrickShowsText(SetRotationStyleBrick.class, R.string.brick_set_rotation_style);
		checkIfBrickShowsSpinnerWithText(SetRotationStyleBrick.class,
				R.id.brick_set_rotation_style_spinner,
				R.string.brick_set_rotation_style_lr);

		//Glide second
		checkIfBrickShowsText(GlideToBrick.class, R.string.brick_glide);
		checkIfBrickShowsText(GlideToBrick.class, R.string.brick_glide_to_x);
		checkIfBrickShowsText(GlideToBrick.class, R.string.y_label);
		checkIfBrickShowsText(GlideToBrick.class, R.string.number_1);
		checkIfBrickShowsText(GlideToBrick.class, "100");
		checkIfBrickShowsText(GlideToBrick.class, "200");

		//Go back
		checkIfBrickShowsText(GoNStepsBackBrick.class, R.string.brick_go_back);
		checkIfBrickShowsText(GoNStepsBackBrick.class, "1");

		//Go to front
		checkIfBrickShowsText(ComeToFrontBrick.class, R.string.brick_come_to_front);

		//Vibrate for second
		checkIfBrickShowsText(VibrationBrick.class, R.string.brick_vibration);
		checkIfBrickShowsText(VibrationBrick.class, R.string.number_1);

		//Set motion type to
		checkIfBrickShowsText(SetPhysicsObjectTypeBrick.class, R.string.brick_set_physics_object_type);
		checkIfBrickShowsSpinnerWithText(SetPhysicsObjectTypeBrick.class,
				R.id.brick_set_physics_object_type_spinner,
				R.string.brick_set_physics_object_type_dynamic);

		//Set velocity
		checkIfBrickShowsText(SetVelocityBrick.class, R.string.brick_set_velocity_to);
		checkIfBrickShowsText(SetVelocityBrick.class, R.string.x_label);
		checkIfBrickShowsText(SetVelocityBrick.class, R.string.y_label);
		checkIfBrickShowsText(SetVelocityBrick.class, R.string.brick_set_velocity_unit);

		//Rotate left
		checkIfBrickShowsText(TurnLeftBrick.class, R.string.brick_turn_left);
		checkIfBrickShowsText(TurnLeftBrick.class, R.string.degrees);
		checkIfBrickShowsText(TurnLeftBrick.class, "15");

		//Rotate right
		checkIfBrickShowsText(TurnRightBrick.class, R.string.brick_turn_right);
		checkIfBrickShowsText(TurnRightBrick.class, R.string.degrees);
		checkIfBrickShowsText(TurnRightBrick.class, "15");

		//Set gravity
		checkIfBrickShowsText(SetGravityBrick.class, R.string.brick_set_gravity_to);
		checkIfBrickShowsText(SetGravityBrick.class, R.string.x_label);
		checkIfBrickShowsText(SetGravityBrick.class, R.string.y_label);
		checkIfBrickShowsText(SetGravityBrick.class, R.string.brick_set_gravity_unit);
		checkIfBrickShowsText(SetGravityBrick.class, "0.0");
		checkIfBrickShowsText(SetGravityBrick.class, "-10.0");

		//Set mass
		checkIfBrickShowsText(SetMassBrick.class, R.string.brick_set_mass);
		checkIfBrickShowsText(SetMassBrick.class, "1.0");
		checkIfBrickShowsText(SetMassBrick.class, R.string.brick_set_mass_unit);

		//Set bounce
		checkIfBrickShowsText(SetBounceBrick.class, R.string.brick_set_bounce_factor);
		checkIfBrickShowsText(SetBounceBrick.class, "80.0");
		checkIfBrickShowsText(SetBounceBrick.class, R.string.percent_symbol);

		//Set friction
		checkIfBrickShowsText(SetFrictionBrick.class, R.string.brick_set_friction);
		checkIfBrickShowsText(SetFrictionBrick.class, "20.0");
		checkIfBrickShowsText(SetFrictionBrick.class, R.string.percent_symbol);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
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
		checkIfBrickShowsEditTextWithText(SpeakBrick.class, R.id.brick_speak_edit_text,
				R.string.brick_speak_default_value);

		//speak and wait  - edit text "hello"
		checkIfBrickShowsText(SpeakAndWaitBrick.class, R.string.brick_speak_and_wait);
		checkIfBrickShowsEditTextWithText(SpeakAndWaitBrick.class, R.id.brick_speak_and_wait_edit_text,
				R.string.brick_speak_default_value);

		//ask and store - edit text "whats your name" - spinner "new"
		checkIfBrickShowsText(AskSpeechBrick.class, R.string.brick_ask_speech_label);
		checkIfBrickShowsEditTextWithText(AskSpeechBrick.class, R.id.brick_ask_speech_question_edit_text,
				R.string.brick_ask_speech_default_question);
		checkIfBrickShowsText(AskSpeechBrick.class, R.string.brick_ask_speech_store);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(AskSpeechBrick.class, R.id.brick_ask_speech_spinner,
				R.string.new_broadcast_message);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testLooksBricksDefaultValues() {
		openCategory(R.string.category_looks);

		checkIfBrickAtPositionShowsText(SetLookBrick.class, 0, R.string.brick_set_look);
		checkIfBrickAtPositionShowsSpinnerWithText(SetLookBrick.class, 0,
				R.id.brick_set_look_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(NextLookBrick.class, R.string.brick_next_look);

		checkIfBrickShowsText(PreviousLookBrick.class, R.string.brick_previous_look);

		checkIfBrickShowsText(SetSizeToBrick.class, R.string.brick_set_size_to);
		checkIfBrickShowsText(SetSizeToBrick.class, R.string.percent_symbol);
		checkIfBrickShowsText(SetSizeToBrick.class, "60");

		checkIfBrickShowsText(ChangeSizeByNBrick.class, R.string.brick_change_size_by);
		checkIfBrickShowsText(ChangeSizeByNBrick.class, "10");

		checkIfBrickShowsText(HideBrick.class, R.string.brick_hide);

		checkIfBrickShowsText(ShowBrick.class, R.string.brick_show);

		checkIfBrickShowsText(AskBrick.class, R.string.brick_ask_label);
		checkIfBrickShowsText(AskBrick.class, R.string.brick_ask_default_question);
		checkIfBrickShowsText(AskBrick.class, R.string.brick_ask_store);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(AskBrick.class, R.id.brick_ask_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(SayBubbleBrick.class, R.string.brick_say_bubble);
		checkIfBrickShowsText(SayBubbleBrick.class, R.string.brick_say_bubble_default_value);

		checkIfBrickShowsText(SayForBubbleBrick.class, R.string.brick_say_bubble);
		checkIfBrickShowsText(SayForBubbleBrick.class, R.string.brick_say_bubble_default_value);
		checkIfBrickShowsText(SayForBubbleBrick.class, R.string.brick_think_say_for_text);
		checkIfBrickShowsText(SayForBubbleBrick.class, UiTestUtils.getResourcesString(R.string
				.formula_editor_sensor_time_second) + " ");

		checkIfBrickAtPositionShowsText(ThinkBubbleBrick.class, 1, R.string.brick_think_bubble);
		checkIfBrickAtPositionShowsText(ThinkBubbleBrick.class, 1, R.string.brick_think_bubble_default_value);

		checkIfBrickAtPositionShowsText(ThinkForBubbleBrick.class, 1, R.string.brick_think_bubble);
		checkIfBrickAtPositionShowsText(ThinkForBubbleBrick.class, 1, R.string.brick_think_bubble_default_value);
		checkIfBrickAtPositionShowsText(ThinkForBubbleBrick.class, 1, R.string.brick_think_say_for_text);
		onData(instanceOf(ThinkForBubbleBrick.class))
				.inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(1)
				.onChildView(withText(UiTestUtils.getResourcesString(R.string.formula_editor_sensor_time_second)
						+ " "))
				.check(matches(isDisplayed()));

		checkIfBrickShowsText(SetTransparencyBrick.class, R.string.brick_set_transparency);
		checkIfBrickShowsText(SetTransparencyBrick.class, R.string.percent_symbol);
		checkIfBrickShowsText(SetTransparencyBrick.class, "50");

		checkIfBrickShowsText(ChangeTransparencyByNBrick.class, R.string.brick_change_ghost_effect);
		checkIfBrickShowsText(ChangeTransparencyByNBrick.class, "25");

		checkIfBrickShowsText(SetBrightnessBrick.class, R.string.brick_set_brightness);
		checkIfBrickShowsText(SetBrightnessBrick.class, "50");
		checkIfBrickShowsText(SetBrightnessBrick.class, R.string.percent_symbol);

		checkIfBrickShowsText(ChangeBrightnessByNBrick.class, R.string.brick_change_brightness);
		checkIfBrickShowsText(ChangeBrightnessByNBrick.class, "25");

		checkIfBrickShowsText(SetColorBrick.class, R.string.brick_set_color);
		checkIfBrickShowsText(SetColorBrick.class, "0.0");

		checkIfBrickShowsText(ChangeColorByNBrick.class, R.string.brick_change_color);
		checkIfBrickShowsText(ChangeColorByNBrick.class, "25.0");

		checkIfBrickShowsText(ClearGraphicEffectBrick.class, R.string.brick_clear_graphic_effect);

		checkIfBrickShowsText(WhenBackgroundChangesBrick.class, R.string.brick_when_background);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(WhenBackgroundChangesBrick.class,
				R.id.brick_when_background_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickAtPositionShowsText(SetBackgroundBrick.class, 0, R.string.brick_set_look);
		checkIfBrickAtPositionShowsSpinnerWithText(SetBackgroundBrick.class, 0,
				R.id.brick_set_look_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickAtPositionShowsText(SetBackgroundAndWaitBrick.class, 0, R.string.brick_set_look);
		checkIfBrickAtPositionShowsText(SetBackgroundAndWaitBrick.class, 0, R.string.brick_and_wait);
		checkIfBrickAtPositionShowsSpinnerWithText(SetBackgroundAndWaitBrick.class, 0,
				R.id.brick_set_look_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(CameraBrick.class, R.string.brick_video);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(CameraBrick.class,
				R.id.brick_video_spinner,
				R.string.video_brick_camera_on);

		checkIfBrickShowsText(ChooseCameraBrick.class, R.string.brick_choose_camera);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(ChooseCameraBrick.class,
				R.id.brick_choose_camera_spinner,
				R.string.choose_camera_front);

		checkIfBrickShowsText(FlashBrick.class, R.string.brick_flash);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(FlashBrick.class,
				R.id.brick_flash_spinner,
				R.string.brick_flash_on);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testPenBricksDefaultValues() {
		openCategory(R.string.category_pen);

		checkIfBrickShowsText(PenDownBrick.class, R.string.brick_pen_down);

		checkIfBrickShowsText(PenUpBrick.class, R.string.brick_pen_up);

		checkIfBrickAtPositionShowsText(SetPenSizeBrick.class, 0, R.string.brick_pen_size);

		checkIfBrickShowsText(SetPenSizeBrick.class, "4");

		checkIfBrickShowsText(ClearBackgroundBrick.class, R.string.brick_clear_background);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testDataBricksDefaultValues() {
		openCategory(R.string.category_data);

		checkIfBrickShowsText(SetVariableBrick.class, R.string.brick_set_variable);
		checkIfBrickShowsText(SetVariableBrick.class, R.string.to_label);
		checkIfBrickShowsText(SetVariableBrick.class, "1.0");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(SetVariableBrick.class,
				R.id.set_variable_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(ChangeVariableBrick.class, R.string.brick_change_variable);
		checkIfBrickShowsText(ChangeVariableBrick.class, R.string.by_label);
		checkIfBrickShowsText(ChangeVariableBrick.class, "1.0");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(ChangeVariableBrick.class,
				R.id.change_variable_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(ShowTextBrick.class, R.string.brick_show_variable);
		checkIfBrickShowsText(ShowTextBrick.class, R.string.brick_show_variable_position);
		checkIfBrickShowsText(ShowTextBrick.class, R.string.x_label);
		checkIfBrickShowsText(ShowTextBrick.class, R.string.y_label);
		checkIfBrickShowsText(ShowTextBrick.class, "100");
		checkIfBrickShowsText(ShowTextBrick.class, "200");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(ShowTextBrick.class,
				R.id.show_variable_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(HideTextBrick.class, R.string.brick_hide_variable);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(HideTextBrick.class,
				R.id.hide_variable_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(AddItemToUserListBrick.class, R.string.brick_add_item_to_userlist_add);
		checkIfBrickShowsText(AddItemToUserListBrick.class, R.string.brick_add_item_to_userlist);
		checkIfBrickShowsText(AddItemToUserListBrick.class, "1.0");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(AddItemToUserListBrick.class,
				R.id.add_item_to_userlist_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(DeleteItemOfUserListBrick.class, R.string.brick_delete_item_from_userlist_delete);
		checkIfBrickShowsText(DeleteItemOfUserListBrick.class, R.string.brick_delete_item_from_userlist);
		checkIfBrickShowsText(DeleteItemOfUserListBrick.class, "1");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(DeleteItemOfUserListBrick.class,
				R.id.delete_item_of_userlist_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(InsertItemIntoUserListBrick.class, R.string.brick_insert_item_into_userlist_insert_into);
		checkIfBrickShowsText(InsertItemIntoUserListBrick.class, R.string.brick_insert_item_into_userlist_into_list);
		checkIfBrickShowsText(InsertItemIntoUserListBrick.class, R.string.brick_insert_item_into_userlist_at_position);
		checkIfBrickShowsText(InsertItemIntoUserListBrick.class, "1.0");
		checkIfBrickShowsText(InsertItemIntoUserListBrick.class, "1");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(InsertItemIntoUserListBrick.class,
				R.id.insert_item_into_userlist_spinner,
				R.string.brick_variable_spinner_create_new_variable);

		checkIfBrickShowsText(ReplaceItemInUserListBrick.class, R.string.brick_replace_item_in_userlist_replace_in_list);
		checkIfBrickShowsText(ReplaceItemInUserListBrick.class, R.string.brick_replace_item_in_userlist_item_at_index);
		checkIfBrickShowsText(ReplaceItemInUserListBrick.class, R.string.brick_replace_item_in_userlist_with_value);
		checkIfBrickShowsText(ReplaceItemInUserListBrick.class, "1.0");
		checkIfBrickShowsText(ReplaceItemInUserListBrick.class, "1");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(ReplaceItemInUserListBrick.class,
				R.id.replace_item_in_userlist_spinner,
				R.string.brick_variable_spinner_create_new_variable);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testLegoNXTBricksDefaultValues() {
		openCategory(R.string.category_lego_nxt);

		checkIfBrickShowsText(LegoNxtMotorTurnAngleBrick.class, R.string.nxt_brick_motor_turn_angle);
		checkIfBrickShowsText(LegoNxtMotorTurnAngleBrick.class, R.string.nxt_motor_move_by);
		checkIfBrickShowsText(LegoNxtMotorTurnAngleBrick.class, R.string.degree_symbol);
		checkIfBrickShowsText(LegoNxtMotorTurnAngleBrick.class, "180");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(LegoNxtMotorTurnAngleBrick.class,
				R.id.lego_motor_turn_angle_spinner,
				R.string.nxt_motor_a);

		checkIfBrickShowsText(LegoNxtMotorStopBrick.class, R.string.nxt_motor_stop);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(LegoNxtMotorStopBrick.class,
				R.id.stop_motor_spinner,
				R.string.nxt_motor_a);

		checkIfBrickShowsText(LegoNxtMotorMoveBrick.class, R.string.nxt_brick_motor_move);
		checkIfBrickShowsText(LegoNxtMotorMoveBrick.class, R.string.nxt_motor_speed_to);
		checkIfBrickShowsText(LegoNxtMotorMoveBrick.class, R.string.percent_symbol);
		checkIfBrickShowsText(LegoNxtMotorMoveBrick.class, R.string.nxt_motor_speed);
		checkIfBrickShowsText(LegoNxtMotorMoveBrick.class, "100");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(LegoNxtMotorMoveBrick.class,
				R.id.lego_motor_action_spinner,
				R.string.nxt_motor_a);

		checkIfBrickShowsText(LegoNxtPlayToneBrick.class, R.string.nxt_play_tone);
		checkIfBrickShowsText(LegoNxtPlayToneBrick.class, R.string.nxt_tone_duration);
		checkIfBrickShowsText(LegoNxtPlayToneBrick.class, R.string.nxt_seconds);
		checkIfBrickShowsText(LegoNxtPlayToneBrick.class, R.string.nxt_tone_frequency);
		checkIfBrickShowsText(LegoNxtPlayToneBrick.class, R.string.nxt_tone_hundred_hz);
		checkIfBrickShowsText(LegoNxtPlayToneBrick.class, "1.0");
		checkIfBrickShowsText(LegoNxtPlayToneBrick.class, "2");
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testLegoEV3BricksDefaultValues() {
		openCategory(R.string.category_lego_ev3);

		checkIfBrickShowsText(LegoEv3MotorTurnAngleBrick.class, R.string.ev3_brick_motor_turn_angle);
		checkIfBrickShowsText(LegoEv3MotorTurnAngleBrick.class, R.string.ev3_motor_move_by);
		checkIfBrickShowsText(LegoEv3MotorTurnAngleBrick.class, R.string.degree_symbol);
		checkIfBrickShowsText(LegoEv3MotorTurnAngleBrick.class, "180");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(LegoEv3MotorTurnAngleBrick.class,
				R.id.lego_ev3_motor_turn_angle_spinner,
				R.string.ev3_motor_a);

		checkIfBrickShowsText(LegoEv3MotorMoveBrick.class, R.string.ev3_motor_move);
		checkIfBrickShowsText(LegoEv3MotorMoveBrick.class, R.string.nxt_motor_speed_to);
		checkIfBrickShowsText(LegoEv3MotorMoveBrick.class, R.string.percent_symbol);
		checkIfBrickShowsText(LegoEv3MotorMoveBrick.class, R.string.nxt_motor_speed);
		checkIfBrickShowsText(LegoEv3MotorMoveBrick.class, "100");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(LegoEv3MotorMoveBrick.class,
				R.id.brick_ev3_motor_move_spinner,
				R.string.ev3_motor_a);

		checkIfBrickShowsText(LegoEv3MotorStopBrick.class, R.string.ev3_motor_stop);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(LegoEv3MotorStopBrick.class,
				R.id.ev3_stop_motor_spinner,
				R.string.ev3_motor_a);

		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, R.string.ev3_play_tone);
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, R.string.ev3_tone_duration_for);
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, R.string.nxt_seconds);
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, R.string.nxt_tone_frequency);
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, R.string.nxt_tone_hundred_hz);
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, R.string.ev3_tone_volume);
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, R.string.ev3_tone_percent);
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, "1.0");
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, "2");
		checkIfBrickShowsText(LegoEv3PlayToneBrick.class, "100");

		checkIfBrickShowsText(LegoEv3SetLedBrick.class, R.string.ev3_set_led_status);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(LegoEv3SetLedBrick.class,
				R.id.brick_ev3_set_led_spinner,
				R.string.ev3_led_status_green);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testDroneBricksDefaultValues() {
		openCategory(R.string.category_drone);

		checkIfBrickShowsText(DroneTakeOffLandBrick.class, R.string.brick_drone_takeoff_land);

		checkIfBrickShowsText(DroneEmergencyBrick.class, R.string.brick_drone_emergency);

		checkIfBrickShowsText(DroneMoveUpBrick.class, R.string.brick_drone_move_up);
		checkIfBrickShowsText(DroneMoveUpBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneMoveUpBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneMoveUpBrick.class, "1");
		checkIfBrickShowsText(DroneMoveUpBrick.class, "20.0");

		checkIfBrickShowsText(DroneMoveDownBrick.class, R.string.brick_drone_move_down);
		checkIfBrickShowsText(DroneMoveDownBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneMoveDownBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneMoveDownBrick.class, "1");
		checkIfBrickShowsText(DroneMoveDownBrick.class, "20.0");

		checkIfBrickShowsText(DroneMoveLeftBrick.class, R.string.brick_drone_move_left);
		checkIfBrickShowsText(DroneMoveLeftBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneMoveLeftBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneMoveLeftBrick.class, "1");
		checkIfBrickShowsText(DroneMoveLeftBrick.class, "20.0");

		checkIfBrickShowsText(DroneMoveRightBrick.class, R.string.brick_drone_move_right);
		checkIfBrickShowsText(DroneMoveRightBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneMoveRightBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneMoveRightBrick.class, "1");
		checkIfBrickShowsText(DroneMoveRightBrick.class, "20.0");

		checkIfBrickShowsText(DroneMoveForwardBrick.class, R.string.brick_drone_move_forward);
		checkIfBrickShowsText(DroneMoveForwardBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneMoveForwardBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneMoveForwardBrick.class, "1");
		checkIfBrickShowsText(DroneMoveForwardBrick.class, "20.0");

		checkIfBrickShowsText(DroneMoveBackwardBrick.class, R.string.brick_drone_move_backward);
		checkIfBrickShowsText(DroneMoveBackwardBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneMoveBackwardBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneMoveBackwardBrick.class, "1");
		checkIfBrickShowsText(DroneMoveBackwardBrick.class, "20.0");

		checkIfBrickShowsText(DroneTurnLeftBrick.class, R.string.brick_drone_turn_left);
		checkIfBrickShowsText(DroneTurnLeftBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneTurnLeftBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneTurnLeftBrick.class, "1");
		checkIfBrickShowsText(DroneTurnLeftBrick.class, "20.0");

		checkIfBrickShowsText(DroneTurnRightBrick.class, R.string.brick_drone_turn_right);
		checkIfBrickShowsText(DroneTurnRightBrick.class, R.string.brick_drone_with);
		checkIfBrickShowsText(DroneTurnRightBrick.class, UiTestUtils.getResourcesString(R.string.percent_symbol)
				+ " "
				+ UiTestUtils.getResourcesString(R.string.formula_editor_function_power));
		checkIfBrickShowsText(DroneTurnRightBrick.class, "1");
		checkIfBrickShowsText(DroneTurnRightBrick.class, "20.0");

		checkIfBrickShowsText(DroneSwitchCameraBrick.class, R.string.brick_drone_switch_camera);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testJumpingSumoBricksDefaultValues() {
		openCategory(R.string.category_jumping_sumo);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testPhiroBricksDefaultValues() {
		openCategory(R.string.category_phiro);

		checkIfBrickShowsText(PhiroMotorMoveForwardBrick.class, R.string.brick_phiro_motor_forward_action);
		checkIfBrickShowsText(PhiroMotorMoveForwardBrick.class, R.string.phiro_motor_speed);
		checkIfBrickShowsText(PhiroMotorMoveForwardBrick.class, R.string.percent_symbol);
		checkIfBrickShowsText(PhiroMotorMoveForwardBrick.class, "100");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(PhiroMotorMoveForwardBrick.class,
				R.id.brick_phiro_motor_forward_action_spinner,
				R.string.phiro_motor_left);

		checkIfBrickShowsText(PhiroMotorMoveBackwardBrick.class, R.string.brick_phiro_motor_backward_action);
		checkIfBrickShowsText(PhiroMotorMoveBackwardBrick.class, R.string.phiro_motor_speed);
		checkIfBrickShowsText(PhiroMotorMoveBackwardBrick.class, R.string.percent_symbol);
		checkIfBrickShowsText(PhiroMotorMoveBackwardBrick.class, "100");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(PhiroMotorMoveBackwardBrick.class,
				R.id.brick_phiro_motor_backward_action_spinner,
				R.string.phiro_motor_left);

		checkIfBrickShowsText(PhiroMotorStopBrick.class, R.string.phiro_motor_stop);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(PhiroMotorStopBrick.class,
				R.id.brick_phiro_stop_motor_spinner,
				R.string.phiro_motor_both);

		checkIfBrickShowsText(PhiroPlayToneBrick.class, R.string.phiro_play_tone);
		checkIfBrickShowsText(PhiroPlayToneBrick.class, R.string.phiro_tone_duration);
		checkIfBrickShowsText(PhiroPlayToneBrick.class, R.string.phiro_seconds);
		checkIfBrickShowsText(PhiroPlayToneBrick.class, "1");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(PhiroPlayToneBrick.class,
				R.id.brick_phiro_select_tone_spinner,
				R.string.phiro_tone_do);

		checkIfBrickShowsText(PhiroRGBLightBrick.class, R.string.brick_phiro_rgb_led_action);
		checkIfBrickShowsText(PhiroRGBLightBrick.class, R.string.phiro_rgb_led_red);
		checkIfBrickShowsText(PhiroRGBLightBrick.class, R.string.phiro_rgb_led_green);
		checkIfBrickShowsText(PhiroRGBLightBrick.class, R.string.phiro_rgb_led_blue);
		checkIfBrickShowsText(PhiroRGBLightBrick.class, "0");
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(PhiroRGBLightBrick.class,
				R.id.brick_phiro_rgb_light_spinner,
				R.string.phiro_motor_both);

		checkIfBrickShowsText(PhiroIfLogicBeginBrick.class, R.string.brick_phiro_sensor_begin);
		checkIfBrickShowsText(PhiroIfLogicBeginBrick.class, R.string.brick_phiro_sensor_second_part);
		checkIfBrickShowsSpinnerWithEditTextOverlayWithText(PhiroIfLogicBeginBrick.class,
				R.id.brick_phiro_sensor_action_spinner,
				R.string.phiro_sensor_front_left);

		//Set variable brick (Sensor: front-left)
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 0, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 0, R.string.to_label);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 0, R.string.formula_editor_phiro_sensor_front_left);
		checkIfBrickAtPositionShowsSpinnerWithText(SetVariableBrick.class, 0, R.id
				.set_variable_spinner, R.string.brick_variable_spinner_create_new_variable);

		//Set variable brick (Sensor: front-right)
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 1, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 1, R.string.to_label);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 1, R.string.formula_editor_phiro_sensor_front_right);
		checkIfBrickAtPositionShowsSpinnerWithText(SetVariableBrick.class, 1, R.id
				.set_variable_spinner, R.string.brick_variable_spinner_create_new_variable);

		//Set variable brick (Sensor: side-left)
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 2, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 2, R.string.to_label);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 2, R.string.formula_editor_phiro_sensor_side_left);
		checkIfBrickAtPositionShowsSpinnerWithText(SetVariableBrick.class, 2, R.id
				.set_variable_spinner, R.string.brick_variable_spinner_create_new_variable);

		//Set variable brick (Sensor: side-right)
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 3, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 3, R.string.to_label);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 3, R.string.formula_editor_phiro_sensor_side_right);
		checkIfBrickAtPositionShowsSpinnerWithText(SetVariableBrick.class, 3, R.id
				.set_variable_spinner, R.string.brick_variable_spinner_create_new_variable);

		//Set variable brick (Sensor: bottom-left)
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 4, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 4, R.string.to_label);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 4, R.string.formula_editor_phiro_sensor_bottom_left);
		checkIfBrickAtPositionShowsSpinnerWithText(SetVariableBrick.class, 4, R.id
				.set_variable_spinner, R.string.brick_variable_spinner_create_new_variable);

		//Set variable brick (Sensor: bottom-right)
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 5, R.string.brick_set_variable);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 5, R.string.to_label);
		checkIfBrickAtPositionShowsText(SetVariableBrick.class, 5, R.string.formula_editor_phiro_sensor_bottom_right);
		checkIfBrickAtPositionShowsSpinnerWithText(SetVariableBrick.class, 5, R.id
				.set_variable_spinner, R.string.brick_variable_spinner_create_new_variable);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testArduinoBricksDefaultValues() {
		openCategory(R.string.category_arduino);

		checkIfBrickShowsText(ArduinoSendDigitalValueBrick.class, R.string.brick_arduino_select_digital_value);
		checkIfBrickShowsText(ArduinoSendDigitalValueBrick.class, R.string.brick_arduino_set_pin_value_to);
		checkIfBrickShowsText(ArduinoSendDigitalValueBrick.class, "1");
		checkIfBrickShowsText(ArduinoSendDigitalValueBrick.class, "13");

		checkIfBrickShowsText(ArduinoSendPWMValueBrick.class, R.string.brick_arduino_select_analog_value);
		checkIfBrickShowsText(ArduinoSendPWMValueBrick.class, R.string.brick_arduino_set_pin_value_to);
		checkIfBrickShowsText(ArduinoSendPWMValueBrick.class, "3");
		checkIfBrickShowsText(ArduinoSendPWMValueBrick.class, "255");
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void testRaspiBricksDefaultValues() {
		openCategory(R.string.category_raspi);

		checkIfBrickShowsText(WhenRaspiPinChangedBrick.class, R.string.brick_raspi_when_begin);
		checkIfBrickShowsText(WhenRaspiPinChangedBrick.class, R.string.brick_raspi_when_equals);
		checkIfBrickShowsSpinnerWithText(WhenRaspiPinChangedBrick.class, R.id.brick_raspi_when_pinspinner,
				R.string.number_3);
		checkIfBrickShowsSpinnerWithText(WhenRaspiPinChangedBrick.class, R.id.brick_raspi_when_valuespinner,
				R.string.brick_raspi_pressed_text);

		checkIfBrickShowsText(RaspiIfLogicBeginBrick.class, R.string.brick_raspi_if_begin);
		checkIfBrickShowsText(RaspiIfLogicBeginBrick.class, R.string.brick_raspi_if_begin_second_part);
		checkIfBrickShowsText(RaspiIfLogicBeginBrick.class, "3");

		checkIfBrickShowsText(RaspiSendDigitalValueBrick.class, R.string.brick_raspi_select_digital_value);
		checkIfBrickShowsText(RaspiSendDigitalValueBrick.class, R.string.brick_raspi_set_pin_value_to);
		checkIfBrickShowsText(RaspiSendDigitalValueBrick.class, "3");
		checkIfBrickShowsText(RaspiSendDigitalValueBrick.class, "1");

		checkIfBrickShowsText(RaspiPwmBrick.class, R.string.brick_raspi_set_pwm);
		checkIfBrickShowsText(RaspiPwmBrick.class, R.string.brick_arduino_set_pin_value_to);
		checkIfBrickShowsText(RaspiPwmBrick.class, R.string.percent_symbol);
		checkIfBrickShowsText(RaspiPwmBrick.class, R.string.hertz_symbol);
		checkIfBrickShowsText(RaspiPwmBrick.class, "3");
		checkIfBrickShowsText(RaspiPwmBrick.class, "50.0");
		checkIfBrickShowsText(RaspiPwmBrick.class, "100.0");
	}

	//Educational Test on how to deal with old/hacked spinner default values
	//If test fails with a String is null exception when trying to get the current spinner text.
	//(some spinners dont have a default "new" value, but are derived from another spinner and have an editText as a
	// child that contains this "new" text)
	@Category({Cat.Educational.class})
	@Test
	public void testAskSpeechBrickInSoundSpinnerProblem() {
		openCategory(R.string.category_sound);
		onData(instanceOf(AskSpeechBrick.class)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withId(R.id.brick_ask_speech_spinner))
				.onChildView(withId(android.R.id.text1))
				.check(matches(withText(R.string.new_broadcast_message)));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
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
		checkIfBrickAtPositionShowsEditTextWithText(IfLogicBeginBrick.class, ifBrickPosition,
				R.id.brick_if_begin_edit_text, "1 < 2");
		checkIfBrickAtPositionShowsText(IfLogicBeginBrick.class, ifBrickPosition, R.string.brick_if_begin_second_part);
		checkIfBrickAtPositionShowsText(IfLogicBeginBrick.class, ifBrickPosition, R.string.brick_if_else);

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

	//Checks for labels
	private void checkIfBrickShowsText(Class brickClass, String text) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withText(text))
				.check(matches(isDisplayed()));
	}

	private void checkIfBrickShowsText(Class brickClass, int stringResourceId) {
		checkIfBrickShowsText(brickClass, UiTestUtils.getResourcesString(stringResourceId));
	}

	private void checkIfBrickAtPositionShowsText(Class brickClass, int position, int stringResourceId) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(position)
				.onChildView(withText(stringResourceId))
				.check(matches(isDisplayed()));
	}

	//Checks for Edit Text values
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

	//Checks for spinner values
	private void checkIfBrickShowsSpinnerWithText(Class brickClass, int spinnerResourceId, int stringResourceId) {
		checkIfBrickShowsSpinnerWithText(brickClass, spinnerResourceId,
				UiTestUtils.getResourcesString(stringResourceId));
	}

	private void checkIfBrickShowsSpinnerWithText(Class brickClass, int spinnerResourceId, String text) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withId(spinnerResourceId))
				.check(matches(withSpinnerText(text)));
	}

	//If above function fails with a String is null exception use this function below.
	//(some spinners dont have a default "new" value, but are derived from another spinner and have an editText as a
	// child that contains this "new" text)
	//see educational test testAskSpeechBrickInSoundSpinnerProblem()
	private void checkIfBrickShowsSpinnerWithEditTextOverlayWithText(Class brickClass, int spinnerResourceId,
			int stringResourceId) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.onChildView(withId(spinnerResourceId))
				.onChildView(withId(android.R.id.text1)) //could be omitted, but just to make clear whats going on
				.check(matches(withText(stringResourceId)));
	}

	private void checkIfBrickAtPositionShowsSpinnerWithText(Class brickClass, int position,
			int spinnerResourceId, int stringResourceId) {
		onData(instanceOf(brickClass)).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.atPosition(position)
				.onChildView(withId(spinnerResourceId))
				.onChildView(withId(android.R.id.text1)) //could be omitted, but just to make clear whats going on
				.check(matches(withText(stringResourceId)));
	}

	private void createProject(String projectName) {
		String nameSpriteTwo = "testSpriteTwo";

		Project project = new Project(null, projectName);
		Sprite spriteOne = new Sprite(nameSpriteOne);
		project.getDefaultScene().addSprite(spriteOne);

		Sprite spriteTwo = new Sprite(nameSpriteTwo);
		Script script = new StartScript();
		spriteTwo.addScript(script);

		project.getDefaultScene().addSprite(spriteTwo);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(spriteTwo);
	}
}
