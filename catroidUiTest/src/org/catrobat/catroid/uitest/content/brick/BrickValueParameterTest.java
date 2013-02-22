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
	private Project project;
	private static final String KEY_SETTINGS_MINDSTORM_BRICKS = "setting_mindstorm_bricks";

	//private static final TextView TextView = null;

	public BrickValueParameterTest() {
		super(MainMenuActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// disable mindstorm bricks, if enabled at start
		if (sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false).commit();
		}

		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		getIntoActivity();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
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

		TextView PlaceAtX = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_x);
		String XPositionPrototype = PlaceAtX.getText().toString();
		String XPositionValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick PlaceAtX are not correct", XPositionValue, XPositionPrototype);

		TextView PlaceAtY = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_y);
		String YPositionPrototype = PlaceAtY.getText().toString();
		String YPositionValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick PlaceAtY are not correct", YPositionValue, YPositionPrototype);

		TextView SetXTo = (TextView) solo.getView(R.id.brick_set_x_prototype_text_view);
		String SetXtoPrototype = SetXTo.getText().toString();
		String XPositionValueSetX = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick SetXTo are not correct", XPositionValueSetX, SetXtoPrototype);

		solo.searchText(solo.getString(R.string.brick_set_y));
		TextView SetYTo = (TextView) solo.getView(R.id.brick_set_y_prototype_text_view);
		String SetYtoPrototype = SetYTo.getText().toString();
		String YPositionValueSetY = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick SetYTo are not correct", YPositionValueSetY, SetYtoPrototype);

		solo.searchText(solo.getString(R.string.brick_change_x_by));
		TextView ChangeXBy = (TextView) solo.getView(R.id.brick_change_x_prototype_text_view);
		String ChangeXByPrototype = ChangeXBy.getText().toString();
		String XPositionValueChangeXBy = Integer.toString(BrickValues.CHANGE_X_BY);
		assertEquals("Value in Brick ChangeXBy are not correct", XPositionValueChangeXBy, ChangeXByPrototype);

		solo.searchText(solo.getString(R.string.brick_change_y_by));
		TextView ChangeYBy = (TextView) solo.getView(R.id.brick_change_y_prototype_text_view);
		String ChangeYByPrototype = ChangeYBy.getText().toString();
		String XPositionValueChangeYBy = Integer.toString(BrickValues.CHANGE_Y_BY);
		assertEquals("Value in Brick ChangeYBy are not correct", XPositionValueChangeYBy, ChangeYByPrototype);

		solo.searchText(solo.getString(R.string.brick_move_n_steps));
		TextView MoveNSteps = (TextView) solo.getView(R.id.brick_move_n_steps_prototype_text_view);
		String MoveNStepsPrototype = MoveNSteps.getText().toString();
		String StepsValue = Float.toString(BrickValues.Move_Steps_Value);
		assertEquals("Value in Brick MoveNSteps are not correct", StepsValue, MoveNStepsPrototype);

		solo.searchText(solo.getString(R.string.brick_turn_left));
		TextView TurnLeft = (TextView) solo.getView(R.id.brick_turn_left_prototype_text_view);
		String TurnLeftPrototype = TurnLeft.getText().toString();
		String TurnLeftDegreesValue = Float.toString(BrickValues.TURN_REIGTH);
		assertEquals("Value in Brick TurnLeft are not correct", TurnLeftDegreesValue, TurnLeftPrototype);

		solo.searchText(solo.getString(R.string.brick_turn_right));
		TextView TurnRight = (TextView) solo.getView(R.id.brick_turn_right_prototype_text_view);
		String TurnRightPrototype = TurnRight.getText().toString();
		String TurnRightDegreesValue = Float.toString(BrickValues.TURN_REIGTH);
		assertEquals("Value in Brick TurnRight are not correct", TurnRightDegreesValue, TurnRightPrototype);

		solo.searchText(solo.getString(R.string.brick_point_in_direction));
		TextView PointInDirection = (TextView) solo.getView(R.id.brick_point_in_direction_prototype_text_view);
		String PointInDirectionPrototype = PointInDirection.getText().toString();
		String PointInDirectionValue = Float.toString(BrickValues.POINT_IN_DIRECTION);
		assertEquals("Value in Brick PointInDirection are not correct", PointInDirectionValue,
				PointInDirectionPrototype);

		solo.searchText(solo.getString(R.string.brick_glide));
		TextView GlideSeconds = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_duration);
		String GlideSecondsPrototype = GlideSeconds.getText().toString();
		int seconds = BrickValues.GLIDE_SECONDS / 1000;
		String GlideSecondsValue = Integer.toString(seconds);
		assertEquals("Value in Brick GlideSeconds are not correct", GlideSecondsValue, GlideSecondsPrototype);

		TextView GlideX = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_x);
		String GlideXPrototype = GlideX.getText().toString();
		String GlideXValue = Integer.toString(BrickValues.X_POSITION);
		assertEquals("Value in Brick GlideX are not correct", GlideXValue, GlideXPrototype);

		TextView GlideY = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_y);
		String GlideYPrototype = GlideY.getText().toString();
		String GlideYValue = Integer.toString(BrickValues.Y_POSITION);
		assertEquals("Value in Brick GlideY are not correct", GlideYValue, GlideYPrototype);

		//TextView GoBack = (TextView) solo.getView(R.id.brick_go_back_prototype_text_view);
		//String GoBackPrototype = GoBack.getText().toString();
		//String GoBackValue = Integer.toString(BrickValues.GO_BACK);
		//assertEquals("Value in Brick GoBack are not correct", GoBackValue, GoBackPrototype);

		solo.clickOnText(solo.getString(R.string.brick_point_in_direction));
		solo.clickOnScreen(200, 200);

		TextView PointinDirectionSelect = (TextView) solo.getView(R.id.brick_point_in_direction_edit_text);
		String EditText = PointinDirectionSelect.getText().toString();
		assertEquals("Value in Selected Brick PointInDirection are not correct", PointInDirectionValue, EditText);

		solo.goBack();
	}

	@Smoke
	public void testifEditTextLooksEqualBrickValue() {

		String categoryLooksText = solo.getString(R.string.category_looks);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooksText);

		TextView SetSizeTo = (TextView) solo.getView(R.id.brick_set_size_to_prototype_text_view);
		String SetSizeToPrototype = SetSizeTo.getText().toString();
		String SetSizeToValue = Float.toString(BrickValues.SET_SIZE_TO);
		assertEquals("Value in Brick SetSizeTo are not correct", SetSizeToValue, SetSizeToPrototype);

		TextView ChangeSizeBy = (TextView) solo.getView(R.id.brick_change_size_by_prototype_text_view);
		String ChangeSizeByPrototype = ChangeSizeBy.getText().toString();
		String ChangeSizeByValue = Float.toString(BrickValues.CHANGE_SIZE_BY);
		assertEquals("Value in Brick ChangeSizeBy are not correct", ChangeSizeByValue, ChangeSizeByPrototype);

		solo.searchText(solo.getString(R.string.brick_set_ghost_effect));
		TextView SetGhostEffect = (TextView) solo.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		String SetGhostEffectPrototype = SetGhostEffect.getText().toString();
		String SetGhostEffectValue = Float.toString(BrickValues.SET_GHOST_EFFECT);
		assertEquals("Value in Brick SetGhostEffect are not correct", SetGhostEffectValue, SetGhostEffectPrototype);

		solo.searchText(solo.getString(R.string.brick_change_ghost_effect));
		TextView ChangeGhostEffect = (TextView) solo.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		String ChangeGhostEffectPrototype = ChangeGhostEffect.getText().toString();
		String ChngeGhostEffectValue = Float.toString(BrickValues.SET_GHOST_EFFECT);
		assertEquals("Value in Brick SetGhostEffect are not correct", ChngeGhostEffectValue, ChangeGhostEffectPrototype);

		solo.searchText(solo.getString(R.string.brick_set_brightness));
		TextView SetBrightness = (TextView) solo.getView(R.id.brick_set_brightness_prototype_text_view);
		String SetBrightnessPrototype = SetBrightness.getText().toString();
		String SetBrightnessValue = Float.toString(BrickValues.SET_BRIGHTNESS_TO);
		assertEquals("Value in Brick SetBrightness are not correct", SetBrightnessValue, SetBrightnessPrototype);

		solo.searchText(solo.getString(R.string.brick_change_brightness));
		TextView ChangeBrightness = (TextView) solo.getView(R.id.brick_change_brightness_prototype_text_view);
		String ChangeBrightnessPrototype = ChangeBrightness.getText().toString();
		String ChangeBrightnessValue = Float.toString(BrickValues.CHANGE_BRITHNESS_BY);
		assertEquals("Value in Brick ChangeBrightness are not correct", ChangeBrightnessValue,
				ChangeBrightnessPrototype);

		solo.clickOnText(solo.getString(R.string.brick_change_brightness));
		solo.clickOnScreen(200, 200);

		TextView ChangeBrightnessSelect = (TextView) solo.getView(R.id.brick_change_brightness_edit_text);
		String EditText = ChangeBrightnessSelect.getText().toString();
		assertEquals("Value in Selected Brick ChangeBrightness are not correct", ChangeBrightnessValue, EditText);

		solo.goBack();

	}

	@Smoke
	public void testifEditTextSoundEqualBrickValue() {

		String categorySoundText = solo.getString(R.string.category_sound);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categorySoundText);

		TextView SetVolumenTo = (TextView) solo.getView(R.id.brick_set_volume_to_prototype_text_view);
		String SetVolumenToPrototype = SetVolumenTo.getText().toString();
		String SetVolumenToValue = Float.toString(BrickValues.SET_VOLUMEN_TO);
		assertEquals("Value in Brick SetVolumenTo are not correct", SetVolumenToValue, SetVolumenToPrototype);

		TextView ChangeVolumenTo = (TextView) solo.getView(R.id.brick_change_volume_by_prototype_text_view);
		String ChangeVolumenToPrototype = ChangeVolumenTo.getText().toString();
		String ChangeVolumenToValue = Float.toString(BrickValues.CHANGE_VOLUMEN_BY);
		assertEquals("Value in Brick SetVolumenTo are not correct", ChangeVolumenToValue, ChangeVolumenToPrototype);

		solo.searchText(solo.getString(R.string.brick_speak));
		TextView Speak = (TextView) solo.getView(R.id.brick_speak_prototype_text_view);
		String SpeakPrototype = Speak.getText().toString();
		String SpeakValue = BrickValues.SPEAK;
		assertEquals("Value in Brick Speak are not correct", SpeakValue, SpeakPrototype);

		solo.clickOnText(solo.getString(R.string.brick_speak));
		solo.clickOnScreen(200, 200);

		TextView SpeakSelect = (TextView) solo.getView(R.id.brick_speak_edit_text);
		String EditText = SpeakSelect.getText().toString();
		assertEquals("Value in Selected Brick Speak are not correct", SpeakValue, EditText);

		solo.goBack();

	}

	@Smoke
	public void testifEditTextControlEqualBrickValue() {

		String categoryControlText = solo.getString(R.string.category_control);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryControlText);

		TextView Wait = (TextView) solo.getView(R.id.brick_wait_prototype_text_view);
		String WaitPrototype = Wait.getText().toString();
		String WaitValue = Integer.toString(BrickValues.WAIT);
		assertEquals("Value in Brick Wait are not correct", WaitValue, WaitPrototype);

		solo.searchText(solo.getString(R.string.brick_note));
		TextView Note = (TextView) solo.getView(R.id.brick_note_prototype_text_view);
		String NotePrototype = Note.getText().toString();
		String NoteValue = BrickValues.NOTE;
		assertEquals("Value in Note Speak are not correct", NoteValue, NotePrototype);

		solo.searchText(solo.getString(R.string.brick_repeat));
		TextView Repeate = (TextView) solo.getView(R.id.brick_repeat_prototype_text_view);
		String RepeatePrototype = Repeate.getText().toString();
		String RepeateValue = Integer.toString(BrickValues.REPEAT);
		assertEquals("Value in Repeate Wait are not correct", RepeateValue, RepeatePrototype);

		solo.clickOnText(solo.getString(R.string.brick_repeat));
		solo.clickOnScreen(200, 200);

		TextView RepeateSelect = (TextView) solo.getView(R.id.brick_repeat_edit_text);
		String EditText = RepeateSelect.getText().toString();
		assertEquals("Value in Selected Brick Repeate are not correct", RepeateValue, EditText);

		solo.goBack();

	}

	@Smoke
	public void testifEditTextLegoNXTEqualBrickValue() {

		String categoryLegoNXTText = solo.getString(R.string.category_lego_nxt);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLegoNXTText);

		TextView NXTturnMotor = (TextView) solo.getView(R.id.motor_turn_angle_text_view);
		String NXTturnMotorPrototype = NXTturnMotor.getText().toString();
		String NXTturnMotorValue = Integer.toString(BrickValues.ANGLE);
		assertEquals("Value in Brick NXTturnMotor are not correct", NXTturnMotorValue, NXTturnMotorPrototype);

		TextView NXTMoveMotor = (TextView) solo.getView(R.id.motor_action_speed_text_view);
		String NXTMoveMotorPrototype = NXTMoveMotor.getText().toString();
		String NXTMoveMotorValue = Integer.toString(BrickValues.SPEED);
		assertEquals("Value in Brick NXTMoveMotor are not correct", NXTMoveMotorValue, NXTMoveMotorPrototype);

		solo.searchText(solo.getString(R.string.nxt_play_tone));
		TextView NXTPlayToneSeconds = (TextView) solo.getView(R.id.nxt_tone_duration_text_view);
		String NXTPlayTonePrototype = NXTPlayToneSeconds.getText().toString();
		int seconds = BrickValues.SECONDS / 1000;
		String NXTPlayToneValue = Float.toString(seconds);
		assertEquals("Value in Brick NXTPlayTone are not correct", NXTPlayToneValue, NXTPlayTonePrototype);

		TextView NXTPlayToneFreq = (TextView) solo.getView(R.id.nxt_tone_freq_text_view);
		String NXTPlayToneFreqPrototype = NXTPlayToneFreq.getText().toString();
		int frequenz = BrickValues.FREQUENCY / 100;
		String NXTPlayToneFreqValue = Integer.toString(frequenz);
		assertEquals("Value in Brick NXTPlayTone are not correct", NXTPlayToneFreqValue, NXTPlayToneFreqPrototype);

		solo.clickOnText(solo.getString(R.string.nxt_play_tone));
		solo.clickOnScreen(200, 200);

		TextView NXTPlayToneSelect = (TextView) solo.getView(R.id.nxt_tone_freq_edit_text);
		String EditText = NXTPlayToneSelect.getText().toString();
		assertEquals("Value in Selected Brick Repeate are not correct", NXTPlayToneFreqValue, EditText);

	}

	private void getIntoActivity() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createEmptyProject();

		if (!sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, true).commit();
		}

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("Dog");
		Script script = new StartScript(sprite);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
