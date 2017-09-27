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
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetNfcTagBrick;
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
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
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
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
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_RASPI_BRICKS;
import static org.catrobat.catroid.uiespresso.util.matchers.rtl.RtlViewDirection.isViewRtl;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class RtlBrickTest {
	private Locale arLocale = new Locale("ar");
	private Locale defaultLocale = Locale.getDefault();

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	private List<String> allPeripheralCategories = new ArrayList<>(Arrays.asList(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, SETTINGS_SHOW_PHIRO_BRICKS,
			SETTINGS_SHOW_ARDUINO_BRICKS, SETTINGS_SHOW_RASPI_BRICKS, SETTINGS_SHOW_NFC_BRICKS));
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
