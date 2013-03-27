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
import android.widget.EditText;
import android.widget.ListView;
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
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();

		// enable mindstorm bricks, if disabled at start
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (!sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, true).commit();
		}
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		// disable mindstorm bricks, if enabled
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
	public void testMotionBricksDefaultValues() {
		String categoryMotionText = solo.getString(R.string.category_motion);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryMotionText);

		TextView placeAtXTextView = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_x);
		int xPositionPrototypeValue = Integer.parseInt(placeAtXTextView.getText().toString());
		assertEquals("Value in Brick PlaceAtX is not correct", BrickValues.X_POSITION, xPositionPrototypeValue);

		TextView placeAtYTextView = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_y);
		int yPositionPrototypeValue = Integer.parseInt(placeAtYTextView.getText().toString());
		assertEquals("Value in Brick PlaceAtY is not correct", BrickValues.Y_POSITION, yPositionPrototypeValue);

		TextView setXToTextView = (TextView) solo.getView(R.id.brick_set_x_prototype_text_view);
		int setXtoPrototypeValue = Integer.parseInt(setXToTextView.getText().toString());
		assertEquals("Value in Brick SetXTo is not correct", BrickValues.X_POSITION, setXtoPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_set_y));
		TextView setYToTextView = (TextView) solo.getView(R.id.brick_set_y_prototype_text_view);
		int setYtoPrototypeValue = Integer.parseInt(setYToTextView.getText().toString());
		assertEquals("Value in Brick SetYTo is not correct", BrickValues.Y_POSITION, setYtoPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_change_x_by));
		TextView changeXByTextView = (TextView) solo.getView(R.id.brick_change_x_prototype_text_view);
		int changeXByPrototypeValue = Integer.parseInt(changeXByTextView.getText().toString());
		assertEquals("Value in Brick ChangeXBy is not correct", BrickValues.CHANGE_X_BY, changeXByPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_change_y_by));
		TextView changeYByTextView = (TextView) solo.getView(R.id.brick_change_y_prototype_text_view);
		int changeYByPrototypeValue = Integer.parseInt(changeYByTextView.getText().toString());
		assertEquals("Value in Brick ChangeYBy is not correct", BrickValues.CHANGE_Y_BY, changeYByPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_move));
		TextView moveNStepsTextView = (TextView) solo.getView(R.id.brick_move_n_steps_prototype_text_view);
		float moveNStepsPrototypeValue = Float.parseFloat(moveNStepsTextView.getText().toString());
		assertEquals("Value in Brick MoveNSteps is not correct", BrickValues.MOVE_STEPS, moveNStepsPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_turn_left));
		TextView turnLeftTextView = (TextView) solo.getView(R.id.brick_turn_left_prototype_text_view);
		float turnLeftPrototypeValue = Float.parseFloat(turnLeftTextView.getText().toString());
		assertEquals("Value in Brick TurnLeft is not correct", (float) BrickValues.TURN_DEGREES, turnLeftPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_turn_right));
		TextView turnRightTextView = (TextView) solo.getView(R.id.brick_turn_right_prototype_text_view);
		float turnRightPrototypeValue = Float.parseFloat(turnRightTextView.getText().toString());
		assertEquals("Value in Brick TurnRight is not correct", (float) BrickValues.TURN_DEGREES,
				turnRightPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_point_in_direction));
		TextView pointInDirectionTextView = (TextView) solo.getView(R.id.brick_point_in_direction_prototype_text_view);
		float pointInDirectionPrototypeValue = Float.parseFloat(pointInDirectionTextView.getText().toString());
		assertEquals("Value in Brick PointInDirection is not correct", (float) BrickValues.POINT_IN_DIRECTION,
				pointInDirectionPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_glide));
		TextView glideSecondsTextView = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_duration);
		float glideSecondsPrototypeValue = Float.parseFloat(glideSecondsTextView.getText().toString());
		assertEquals("Value in Brick GlideSeconds is not correct", (float) BrickValues.GLIDE_SECONDS / 1000,
				glideSecondsPrototypeValue);

		TextView glideXTextView = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_x);
		int glideXPrototypeValue = Integer.parseInt(glideXTextView.getText().toString());
		assertEquals("Value in Brick GlideX is not correct", BrickValues.X_POSITION, glideXPrototypeValue);

		TextView glideYTextView = (TextView) solo.getView(R.id.brick_glide_to_prototype_text_view_y);
		int glideYPrototypeValue = Integer.parseInt(glideYTextView.getText().toString());
		assertEquals("Value in Brick GlideY is not correct", BrickValues.Y_POSITION, glideYPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_go_back));
		TextView goBackTextView = (TextView) solo.getView(R.id.brick_go_back_prototype_text_view);
		int goBackPrototypeValue = Integer.parseInt(goBackTextView.getText().toString());
		assertEquals("Value in Brick GoBack is not correct", BrickValues.GO_BACK, goBackPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_go_back));
		solo.clickOnScreen(200, 200);

		EditText goBackEditText = (EditText) solo.getView(R.id.brick_go_back_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		String goBackEditTextString = goBackEditText.getText().toString();
		int goBackEditTextValue = Integer
				.parseInt(goBackEditTextString.substring(0, goBackEditTextString.length() - 1));
		assertEquals("Value in Selected Brick GoBack is not correct", BrickValues.GO_BACK, goBackEditTextValue);
	}

	@Smoke
	public void testLookBricksDefaultValues() {
		String categoryLooksText = solo.getString(R.string.category_looks);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooksText);

		TextView setSizeToTextView = (TextView) solo.getView(R.id.brick_set_size_to_prototype_text_view);
		float setSizeToPrototypeValue = Float.parseFloat(setSizeToTextView.getText().toString());
		assertEquals("Value in Brick SetSizeTo is not correct", (float) BrickValues.SET_SIZE_TO,
				setSizeToPrototypeValue);

		TextView changeSizeByTextView = (TextView) solo.getView(R.id.brick_change_size_by_prototype_text_view);
		float changeSizeByPrototypeValue = Float.parseFloat(changeSizeByTextView.getText().toString());
		assertEquals("Value in Brick ChangeSizeBy is not correct", (float) BrickValues.CHANGE_SIZE_BY,
				changeSizeByPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_set_ghost_effect));
		TextView setGhostEffectTextView = (TextView) solo.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		float setGhostEffectPrototypeValue = Float.parseFloat(setGhostEffectTextView.getText().toString());
		assertEquals("Value in Brick SetGhostEffect is not correct", (float) BrickValues.SET_GHOST_EFFECT,
				setGhostEffectPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_change_ghost_effect));
		TextView changeGhostEffectTextView = (TextView) solo
				.getView(R.id.brick_set_ghost_effect_to_prototype_text_view);
		float changeGhostEffectPrototypeValue = Float.parseFloat(changeGhostEffectTextView.getText().toString());
		assertEquals("Value in Brick SetGhostEffect is not correct", (float) BrickValues.SET_GHOST_EFFECT,
				changeGhostEffectPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_set_brightness));
		TextView setBrightnessTextView = (TextView) solo.getView(R.id.brick_set_brightness_prototype_text_view);
		float setBrightnessPrototypeValue = Float.parseFloat(setBrightnessTextView.getText().toString());
		assertEquals("Value in Brick SetBrightness is not correct", (float) BrickValues.SET_BRIGHTNESS_TO,
				setBrightnessPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_change_brightness));
		TextView changeBrightnessTextView = (TextView) solo.getView(R.id.brick_change_brightness_prototype_text_view);
		float changeBrightnessPrototypeValue = Float.parseFloat(changeBrightnessTextView.getText().toString());
		assertEquals("Value in Brick ChangeBrightness is not correct", (float) BrickValues.CHANGE_BRITHNESS_BY,
				changeBrightnessPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_change_brightness));
		solo.clickOnScreen(200, 200);

		EditText changeBrightnessEditText = (EditText) solo.getView(R.id.brick_change_brightness_edit_text);
		float changeBrightnessEditTextValue = Float.parseFloat(changeBrightnessEditText.getText().toString());
		assertEquals("Value in Selected Brick ChangeBrightness is not correct",
				(float) BrickValues.CHANGE_BRITHNESS_BY, changeBrightnessEditTextValue);
	}

	@Smoke
	public void testSoundBricksDefaultValues() {
		String categorySoundText = solo.getString(R.string.category_sound);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categorySoundText);

		TextView setVolumeToTextView = (TextView) solo.getView(R.id.brick_set_volume_to_prototype_text_view);
		float setVolumeToPrototypeValue = Float.parseFloat(setVolumeToTextView.getText().toString());
		assertEquals("Value in Brick SetVolumeTo is not correct", (float) BrickValues.SET_VOLUME_TO,
				setVolumeToPrototypeValue);

		TextView changeVolumeByTextView = (TextView) solo.getView(R.id.brick_change_volume_by_prototype_text_view);
		float changeVolumenToPrototypeValue = Float.parseFloat(changeVolumeByTextView.getText().toString());
		assertEquals("Value in Brick ChangeVolumeBy is not correct", (float) BrickValues.CHANGE_VOLUME_BY,
				changeVolumenToPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_speak));
		TextView speakTextView = (TextView) solo.getView(R.id.brick_speak_prototype_text_view);
		String speakPrototypeValue = speakTextView.getText().toString();
		String defaultSpeakValue = solo.getString(R.string.brick_speak_default_value);
		assertEquals("Value in Brick Speak is not correct", defaultSpeakValue, speakPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_speak));
		solo.clickOnScreen(200, 200);

		EditText speakEditText = (EditText) solo.getView(R.id.brick_speak_edit_text);
		String speakEditTextValue = speakEditText.getText().toString();
		assertEquals("Value in Selected Brick Speak is not correct", defaultSpeakValue, speakEditTextValue);

		solo.sleep(500);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categorySoundText);
		solo.clickOnText(solo.getString(R.string.brick_change_volume_by));
		solo.clickOnScreen(200, 200);

		EditText changeVolumeByEditText = (EditText) solo.getView(R.id.brick_change_volume_by_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		// in this case, between the minus operator and the value there is a blank also
		String changeVolumeByEditTextString = changeVolumeByEditText.getText().toString();
		float changeVolumeByEditTextValue = Float.parseFloat(changeVolumeByEditTextString.replaceAll(" ", ""));
		assertEquals("Value in Selected Brick ChangeVolumeBy is not correct", BrickValues.CHANGE_VOLUME_BY,
				changeVolumeByEditTextValue);
	}

	@Smoke
	public void testControlBricksDefaultValues() {
		String categoryControlText = solo.getString(R.string.category_control);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryControlText);

		TextView waitSecondsTextView = (TextView) solo.getView(R.id.brick_wait_prototype_text_view);
		float waitPrototypeValue = Float.parseFloat(waitSecondsTextView.getText().toString());
		assertEquals("Value in Brick Wait is not correct", (float) BrickValues.WAIT / 1000, waitPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_note));
		TextView noteTextView = (TextView) solo.getView(R.id.brick_note_prototype_text_view);
		String notePrototypeValue = noteTextView.getText().toString();
		String defaultNoteValue = solo.getString(R.string.brick_note_default_value);
		assertEquals("Value in Note Speak is not correct", defaultNoteValue, notePrototypeValue);

		solo.searchText(solo.getString(R.string.brick_repeat));
		TextView repeatTextView = (TextView) solo.getView(R.id.brick_repeat_prototype_text_view);
		int repeatPrototypeValue = Integer.parseInt(repeatTextView.getText().toString());
		assertEquals("Value in Repeat Wait is not correct", BrickValues.REPEAT, repeatPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_repeat));
		solo.clickOnScreen(200, 200);

		EditText repeatEditText = (EditText) solo.getView(R.id.brick_repeat_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		String repeatEditTextString = repeatEditText.getText().toString();
		int repeatEditTextValue = Integer
				.parseInt(repeatEditTextString.substring(0, repeatEditTextString.length() - 1));
		assertEquals("Value in Selected Brick Repeat is not correct", BrickValues.REPEAT, repeatEditTextValue);
	}

	@Smoke
	public void testLegoBricksDefaultValues() {
		String categoryLegoNXTText = solo.getString(R.string.category_lego_nxt);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		ListView fragmentListView = solo.getCurrentListViews().get(solo.getCurrentListViews().size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnText(categoryLegoNXTText);

		TextView nxtTurnMotorTextView = (TextView) solo.getView(R.id.motor_turn_angle_text_view);
		int nXTturnMotorPrototypeValue = Integer.parseInt(nxtTurnMotorTextView.getText().toString());
		assertEquals("Value in Brick NXTTurnMotor is not correct", BrickValues.LEGO_ANGLE, nXTturnMotorPrototypeValue);

		TextView nxtMoveMotorTextView = (TextView) solo.getView(R.id.motor_action_speed_text_view);
		int nXTMoveMotorPrototypeValue = Integer.parseInt(nxtMoveMotorTextView.getText().toString());
		assertEquals("Value in Brick NXTMoveMotor is not correct", BrickValues.LEGO_SPEED, nXTMoveMotorPrototypeValue);

		solo.searchText(solo.getString(R.string.nxt_play_tone));
		TextView nxtPlayToneSecondsTextView = (TextView) solo.getView(R.id.nxt_tone_duration_text_view);
		int nXTPlayTonePrototypeValue = Integer.parseInt(nxtPlayToneSecondsTextView.getText().toString());
		assertEquals("Value in Brick NXTPlayTone is not correct", BrickValues.LEGO_DURATION, nXTPlayTonePrototypeValue);

		TextView nxtPlayToneFreqTextView = (TextView) solo.getView(R.id.nxt_tone_freq_text_view);
		int nXTPlayToneFreqPrototypeValue = Integer.parseInt(nxtPlayToneFreqTextView.getText().toString());
		assertEquals("Value in Brick NXTPlayTone is not correct", BrickValues.LEGO_FREQUENCY,
				nXTPlayToneFreqPrototypeValue);

		solo.clickOnText(solo.getString(R.string.nxt_play_tone));
		solo.clickOnScreen(200, 200);

		EditText nxtPlayToneEditText = (EditText) solo.getView(R.id.nxt_tone_freq_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		String nxtPlayToneEditTextString = nxtPlayToneEditText.getText().toString();
		int nxtPlayToneEditTextValue = Integer.parseInt(nxtPlayToneEditTextString.substring(0,
				nxtPlayToneEditTextString.length() - 1));
		assertEquals("Value in Selected Brick Repeat is not correct", BrickValues.LEGO_FREQUENCY,
				nxtPlayToneEditTextValue);
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
