/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class BrickValueParameterTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private static final String KEY_SETTINGS_MINDSTORM_BRICKS = "setting_mindstorm_bricks";

	public BrickValueParameterTest() {
		super(MainMenuActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// disable mindstorm bricks, if enabled at start
		if (!sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, true).commit();
		}

		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false).commit();
		}
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testifEditMotionEqualBrickValue() {
		String categoryMotionText = solo.getString(R.string.category_motion);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryMotionText);

		TextView placeAtX = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_x);
		String xPositionPrototype = placeAtX.getText().toString();
		String xPositionValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick PlaceAtX are not correct", xPositionValue, xPositionPrototype);

		TextView placeAtY = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_y);
		String yPositionPrototype = placeAtY.getText().toString();
		String yPositionValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick PlaceAtY are not correct", yPositionValue, yPositionPrototype);

		TextView setXTo = (TextView) solo.getView(R.id.brick_set_x_prototype_text_view);
		String setXtoPrototype = setXTo.getText().toString();
		String xPositionValueSetX = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick SetXTo are not correct", xPositionValueSetX, setXtoPrototype);

		solo.searchText(solo.getString(R.string.brick_set_y));
		TextView setYTo = (TextView) solo.getView(R.id.brick_set_y_prototype_text_view);
		String setYtoPrototype = setYTo.getText().toString();
		String yPositionValueSetY = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick SetYTo are not correct", yPositionValueSetY, setYtoPrototype);

		solo.searchText(solo.getString(R.string.brick_change_x_by));
		TextView changeXBy = (TextView) solo.getView(R.id.brick_change_x_prototype_text_view);
		String changeXByPrototype = changeXBy.getText().toString();
		String rPositionValueChangeXBy = Integer.toString(BrickValues.CHANGE_X_BY);
		assertEquals("Value in Brick ChangeXBy are not correct", rPositionValueChangeXBy, changeXByPrototype);

		solo.searchText(solo.getString(R.string.brick_change_y_by));
		TextView changeYBy = (TextView) solo.getView(R.id.brick_change_y_prototype_text_view);
		String changeYByPrototype = changeYBy.getText().toString();
		String xPositionValueChangeYBy = Integer.toString(BrickValues.CHANGE_Y_BY);
		assertEquals("Value in Brick ChangeYBy are not correct", xPositionValueChangeYBy, changeYByPrototype);

		solo.searchText(solo.getString(R.string.brick_move));
		TextView moveNSteps = (TextView) solo.getView(R.id.brick_move_n_steps_prototype_text_view);
		String moveNStepsPrototype = moveNSteps.getText().toString();
		String stepsValue = Float.toString(BrickValues.Move_Steps_Value);
		assertEquals("Value in Brick MoveNSteps are not correct", stepsValue, moveNStepsPrototype);

		solo.searchText(solo.getString(R.string.brick_turn_left));
		TextView turnLeft = (TextView) solo.getView(R.id.brick_turn_left_prototype_text_view);
		String turnLeftPrototype = turnLeft.getText().toString();
		String turnLeftDegreesValue = Float.toString(BrickValues.TURN_RIGHT);
		assertEquals("Value in Brick TurnLeft are not correct", turnLeftDegreesValue, turnLeftPrototype);

		solo.searchText(solo.getString(R.string.brick_turn_right));
		TextView turnRight = (TextView) solo.getView(R.id.brick_turn_right_prototype_text_view);
		String turnRightPrototype = turnRight.getText().toString();
		String turnRightDegreesValue = Float.toString(BrickValues.TURN_RIGHT);
		assertEquals("Value in Brick TurnRight are not correct", turnRightDegreesValue, turnRightPrototype);

		solo.searchText(solo.getString(R.string.brick_point_in_direction));
		TextView pointInDirection = (TextView) solo.getView(R.id.brick_point_in_direction_prototype_text_view);
		String pointInDirectionPrototype = pointInDirection.getText().toString();
		String pointInDirectionValue = Float.toString(BrickValues.POINT_IN_DIRECTION);
		assertEquals("Value in Brick PointInDirection are not correct", pointInDirectionValue,
				pointInDirectionPrototype);

		solo.searchText(solo.getString(R.string.brick_glide));
		TextView glideSeconds = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_duration);
		String glideSecondsPrototype = glideSeconds.getText().toString();
		int seconds = BrickValues.GLIDE_SECONDS / 1000;
		String glideSecondsValue = Integer.toString(seconds);
		assertEquals("Value in Brick GlideSeconds are not correct", glideSecondsValue, glideSecondsPrototype);

		TextView glideX = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_x);
		String glideXPrototype = glideX.getText().toString();
		String glideXValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick GlideX are not correct", glideXValue, glideXPrototype);

		TextView glideY = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_y);
		String glideYPrototype = glideY.getText().toString();
		String glideYValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick GlideY are not correct", glideYValue, glideYPrototype);

		solo.searchText(solo.getString(R.string.brick_go_back));
		TextView goBack = (TextView) solo.getView(R.id.brick_go_back_prototype_text_view);
		String goBackPrototype = goBack.getText().toString();
		String goBackValue = Integer.toString(BrickValues.GO_BACK);
		assertEquals("Value in Brick GoBack are not correct", goBackValue, goBackPrototype);

		solo.clickOnText(solo.getString(R.string.brick_go_back));
		solo.clickOnScreen(200, 200);

		TextView goBackSelect = (TextView) solo.getView(R.id.brick_go_back_edit_text);
		String editText = goBackSelect.getText().toString();
		assertEquals("Value in Selected Brick GoBack are not correct", goBackValue, editText);

	}

	@Smoke
	public void testifEditTextLooksEqualBrickValue() {

		String categoryLooksText = solo.getString(R.string.category_looks);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooksText);

		TextView setSizeTo = (TextView) solo.getView(R.id.brick_set_size_to_prototype_text_view);
		String setSizeToPrototype = setSizeTo.getText().toString();
		String setSizeToValue = Float.toString(BrickValues.SET_SIZE_TO);
		assertEquals("Value in Brick SetSizeTo are not correct", setSizeToValue, setSizeToPrototype);

		TextView changeSizeBy = (TextView) solo.getView(R.id.brick_change_size_by_prototype_text_view);
		String changeSizeByPrototype = changeSizeBy.getText().toString();
		String changeSizeByValue = Float.toString(BrickValues.CHANGE_SIZE_BY);
		assertEquals("Value in Brick ChangeSizeBy are not correct", changeSizeByValue, changeSizeByPrototype);

		solo.searchText(solo.getString(R.string.brick_set_ghost_effect));
		TextView setGhostEffect = (TextView) solo.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		String setGhostEffectPrototype = setGhostEffect.getText().toString();
		String setGhostEffectValue = Float.toString(BrickValues.SET_GHOST_EFFECT);
		assertEquals("Value in Brick SetGhostEffect are not correct", setGhostEffectValue, setGhostEffectPrototype);

		solo.searchText(solo.getString(R.string.brick_change_ghost_effect));
		TextView changeGhostEffect = (TextView) solo.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		String changeGhostEffectPrototype = changeGhostEffect.getText().toString();
		String changeGhostEffectValue = Float.toString(BrickValues.SET_GHOST_EFFECT);
		assertEquals("Value in Brick SetGhostEffect are not correct", changeGhostEffectValue,
				changeGhostEffectPrototype);

		solo.searchText(solo.getString(R.string.brick_set_brightness));
		TextView setBrightness = (TextView) solo.getView(R.id.brick_set_brightness_prototype_text_view);
		String setBrightnessPrototype = setBrightness.getText().toString();
		String setBrightnessValue = Float.toString(BrickValues.SET_BRIGHTNESS_TO);
		assertEquals("Value in Brick SetBrightness are not correct", setBrightnessValue, setBrightnessPrototype);

		solo.searchText(solo.getString(R.string.brick_change_brightness));
		TextView changeBrightness = (TextView) solo.getView(R.id.brick_change_brightness_prototype_text_view);
		String changeBrightnessPrototype = changeBrightness.getText().toString();
		String changeBrightnessValue = Float.toString(BrickValues.CHANGE_BRITHNESS_BY);
		assertEquals("Value in Brick ChangeBrightness are not correct", changeBrightnessValue,
				changeBrightnessPrototype);

		solo.clickOnText(solo.getString(R.string.brick_change_brightness));
		solo.clickOnScreen(200, 200);

		TextView changeBrightnessSelect = (TextView) solo.getView(R.id.brick_change_brightness_edit_text);
		String editText = changeBrightnessSelect.getText().toString();
		assertEquals("Value in Selected Brick ChangeBrightness are not correct", changeBrightnessValue, editText);

	}

	@Smoke
	public void testifEditTextSoundEqualBrickValue() {

		String categorySoundText = solo.getString(R.string.category_sound);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categorySoundText);

		TextView setVolumenTo = (TextView) solo.getView(R.id.brick_set_volume_to_prototype_text_view);
		String setVolumenToPrototype = setVolumenTo.getText().toString();
		String setVolumenToValue = Float.toString(BrickValues.SET_VOLUMEN_TO);
		assertEquals("Value in Brick SetVolumenTo are not correct", setVolumenToValue, setVolumenToPrototype);

		TextView changeVolumenTo = (TextView) solo.getView(R.id.brick_change_volume_by_prototype_text_view);
		String changeVolumenToPrototype = changeVolumenTo.getText().toString();
		String changeVolumenToValue = Float.toString(BrickValues.CHANGE_VOLUMEN_BY);
		assertEquals("Value in Brick SetVolumenTo are not correct", changeVolumenToValue, changeVolumenToPrototype);

		solo.searchText(solo.getString(R.string.brick_speak));
		TextView speak = (TextView) solo.getView(R.id.brick_speak_prototype_text_view);
		String speakPrototype = speak.getText().toString();
		String speakValue = solo.getString(R.string.brick_speak_default_value);
		assertEquals("Value in Brick Speak are not correct", speakValue, speakPrototype);

		solo.clickOnText(solo.getString(R.string.brick_speak));
		solo.clickOnScreen(200, 200);

		TextView speakSelect = (TextView) solo.getView(R.id.brick_speak_edit_text);
		String editText = speakSelect.getText().toString();
		assertEquals("Value in Selected Brick Speak are not correct", speakValue, editText);

	}

	@Smoke
	public void testifEditTextControlEqualBrickValue() {

		String categoryControlText = solo.getString(R.string.category_control);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryControlText);

		TextView waitSeconds = (TextView) solo.getView(R.id.brick_wait_prototype_text_view);
		String waitPrototype = waitSeconds.getText().toString();
		String waitValue = Float.toString(BrickValues.WAIT / 1000);
		assertEquals("Value in Brick Wait are not correct", waitValue, waitPrototype);

		solo.searchText(solo.getString(R.string.brick_note));
		TextView note = (TextView) solo.getView(R.id.brick_note_prototype_text_view);
		String notePrototype = note.getText().toString();
		String noteValue = solo.getString(R.string.brick_note_default_value);
		assertEquals("Value in Note Speak are not correct", noteValue, notePrototype);

		solo.searchText(solo.getString(R.string.brick_repeat));
		TextView repeate = (TextView) solo.getView(R.id.brick_repeat_prototype_text_view);
		String repeatePrototype = repeate.getText().toString();
		String repeateValue = Integer.toString(BrickValues.REPEAT);
		assertEquals("Value in Repeate Wait are not correct", repeateValue, repeatePrototype);

		solo.clickOnText(solo.getString(R.string.brick_repeat));
		solo.clickOnScreen(200, 200);

		TextView repeateSelect = (TextView) solo.getView(R.id.brick_repeat_edit_text);
		String editText = repeateSelect.getText().toString();
		assertEquals("Value in Selected Brick Repeate are not correct", repeateValue, editText);

	}

	@Smoke
	public void testifEditTextLegoNXTEqualBrickValue() {

		String categoryLegoNXTText = solo.getString(R.string.category_lego_nxt);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLegoNXTText);

		TextView nXTturnMotor = (TextView) solo.getView(R.id.motor_turn_angle_text_view);
		String nXTturnMotorPrototype = nXTturnMotor.getText().toString();
		String nXTturnMotorValue = Integer.toString(BrickValues.ANGLE);
		assertEquals("Value in Brick NXTturnMotor are not correct", nXTturnMotorValue, nXTturnMotorPrototype);

		TextView nXTMoveMotor = (TextView) solo.getView(R.id.motor_action_speed_text_view);
		String nXTMoveMotorPrototype = nXTMoveMotor.getText().toString();
		String nXTMoveMotorValue = Integer.toString(BrickValues.SPEED);
		assertEquals("Value in Brick NXTMoveMotor are not correct", nXTMoveMotorValue, nXTMoveMotorPrototype);

		solo.searchText(solo.getString(R.string.nxt_play_tone));
		TextView nXTPlayToneSeconds = (TextView) solo.getView(R.id.nxt_tone_duration_text_view);
		String nXTPlayTonePrototype = nXTPlayToneSeconds.getText().toString();
		int seconds = BrickValues.SECONDS / 1000;
		String nXTPlayToneValue = Float.toString(seconds);
		assertEquals("Value in Brick NXTPlayTone are not correct", nXTPlayToneValue, nXTPlayTonePrototype);

		TextView nXTPlayToneFreq = (TextView) solo.getView(R.id.nxt_tone_freq_text_view);
		String nXTPlayToneFreqPrototype = nXTPlayToneFreq.getText().toString();
		int frequenz = BrickValues.FREQUENCY / 100;
		String nXTPlayToneFreqValue = Integer.toString(frequenz);
		assertEquals("Value in Brick NXTPlayTone are not correct", nXTPlayToneFreqValue, nXTPlayToneFreqPrototype);

		solo.clickOnText(solo.getString(R.string.nxt_play_tone));
		solo.clickOnScreen(200, 200);

		TextView nXTPlayToneSelect = (TextView) solo.getView(R.id.nxt_tone_freq_edit_text);
		String editText = nXTPlayToneSelect.getText().toString();
		assertEquals("Value in Selected Brick Repeate are not correct", nXTPlayToneFreqValue, editText);

	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.PROJECTNAME1);
		Sprite sprite = new Sprite("Dog");
		Sprite sprite1 = new Sprite("Cat");
		Script script = new StartScript(sprite);
		Script script1 = new StartScript(sprite1);
		sprite.addScript(script);
		sprite.addScript(script1);
		project.addSprite(sprite);
		project.addSprite(sprite1);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
