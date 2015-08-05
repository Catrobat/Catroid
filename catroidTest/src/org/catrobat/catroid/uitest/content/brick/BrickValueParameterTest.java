/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.uitest.content.brick;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class BrickValueParameterTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public BrickValueParameterTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		// enable mindstorms bricks, if disabled at start
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (!sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true).commit();
		}
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);
	}

	@Override
	public void tearDown() throws Exception {
		// disable mindstorms bricks, if enabled
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false).commit();
		}
		super.tearDown();
	}

	public void testMotionBricksDefaultValues() {
		String categoryMotionText = solo.getString(R.string.category_motion);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryMotionText);
		solo.searchText(categoryMotionText);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);

		TextView placeAtXTextView = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_x);
		int xPositionPrototypeValue = Integer.parseInt(placeAtXTextView.getText().toString());
		assertEquals("Value in Brick PlaceAtX is not correct", BrickValues.X_POSITION, xPositionPrototypeValue);

		TextView placeAtYTextView = (TextView) solo.getView(R.id.brick_place_at_prototype_text_view_y);
		int yPositionPrototypeValue = Integer.parseInt(placeAtYTextView.getText().toString());
		assertEquals("Value in Brick PlaceAtY is not correct", BrickValues.Y_POSITION, yPositionPrototypeValue);

		TextView setXToTextView = (TextView) solo.getView(R.id.brick_set_x_prototype_text_view);
		int setXtoPrototypeValue = Integer.parseInt(setXToTextView.getText().toString());
		assertEquals("Value in Brick SetXTo is not correct", BrickValues.X_POSITION, setXtoPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_set_y))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView setYToTextView = (TextView) solo.getView(R.id.brick_set_y_prototype_text_view);
		int setYtoPrototypeValue = Integer.parseInt(setYToTextView.getText().toString());
		assertEquals("Value in Brick SetYTo is not correct", BrickValues.Y_POSITION, setYtoPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_change_x_by))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView changeXByTextView = (TextView) solo.getView(R.id.brick_change_x_prototype_text_view);
		int changeXByPrototypeValue = Integer.parseInt(changeXByTextView.getText().toString());
		assertEquals("Value in Brick ChangeXBy is not correct", BrickValues.CHANGE_X_BY, changeXByPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_change_y_by))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView changeYByTextView = (TextView) solo.getView(R.id.brick_change_y_prototype_text_view);
		int changeYByPrototypeValue = Integer.parseInt(changeYByTextView.getText().toString());
		assertEquals("Value in Brick ChangeYBy is not correct", BrickValues.CHANGE_Y_BY, changeYByPrototypeValue);

		solo.scrollDownList(fragmentListView);
		solo.searchText(solo.getString(R.string.brick_move));
		TextView moveNStepsTextView = (TextView) solo.getView(R.id.brick_move_n_steps_prototype_text_view);
		float moveNStepsPrototypeValue = Float.parseFloat(moveNStepsTextView.getText().toString());
		assertEquals("Value in Brick MoveNSteps is not correct", BrickValues.MOVE_STEPS, moveNStepsPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_turn_left))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView turnLeftTextView = (TextView) solo.getView(R.id.brick_turn_left_prototype_text_view);
		float turnLeftPrototypeValue = Float.parseFloat(turnLeftTextView.getText().toString());
		assertEquals("Value in Brick TurnLeft is not correct", (float) BrickValues.TURN_DEGREES, turnLeftPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_turn_right))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView turnRightTextView = (TextView) solo.getView(R.id.brick_turn_right_prototype_text_view);
		float turnRightPrototypeValue = Float.parseFloat(turnRightTextView.getText().toString());
		assertEquals("Value in Brick TurnRight is not correct", (float) BrickValues.TURN_DEGREES,
				turnRightPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_point_in_direction))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView pointInDirectionTextView = (TextView) solo.getView(R.id.brick_point_in_direction_prototype_text_view);
		float pointInDirectionPrototypeValue = Float.parseFloat(pointInDirectionTextView.getText().toString());
		assertEquals("Value in Brick PointInDirection is not correct", (float) BrickValues.POINT_IN_DIRECTION,
				pointInDirectionPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_point_to))) {
			solo.scrollDownList(fragmentListView);
		}
		Spinner pointToSpinner = (Spinner) solo.getView(R.id.brick_point_to_spinner);
		assertEquals("Value in PointToBrick is not correct", "Dog", pointToSpinner.getSelectedItem().toString());

		if (!solo.searchText(solo.getString(R.string.brick_glide))) {
			solo.scrollDownList(fragmentListView);
		}
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

		solo.scrollDownList(fragmentListView);
		solo.searchText(solo.getString(R.string.brick_go_back));
		TextView goBackTextView = (TextView) solo.getView(R.id.brick_go_back_prototype_text_view);
		int goBackPrototypeValue = Integer.parseInt(goBackTextView.getText().toString());
		assertEquals("Value in Brick GoBack is not correct", BrickValues.GO_BACK, goBackPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_go_back));
		solo.clickOnScreen(200, 200);

		TextView goBackEditText = (TextView) solo.getView(R.id.brick_go_back_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		String goBackEditTextString = goBackEditText.getText().toString();
		int goBackEditTextValue = Integer
				.parseInt(goBackEditTextString.substring(0, goBackEditTextString.length() - 1));
		assertEquals("Value in Selected Brick GoBack is not correct", BrickValues.GO_BACK, goBackEditTextValue);
	}

	public void testLookBricksDefaultValues() {
		String categoryLooksText = solo.getString(R.string.category_looks);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooksText);

		// Just to get focus
		solo.searchText(categoryLooksText);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);

		if (!solo.searchText(solo.getString(R.string.brick_set_look))) {
			solo.scrollDownList(fragmentListView);
		}
		Spinner setLookSpinner = (Spinner) solo.getView(R.id.brick_set_look_spinner);
		assertEquals("Value in SetLookBrick is not correct", solo.getString(R.string.new_broadcast_message),
				setLookSpinner.getSelectedItem().toString());

		TextView setSizeToTextView = (TextView) solo.getView(R.id.brick_set_size_to_prototype_text_view);
		float setSizeToPrototypeValue = Float.parseFloat(setSizeToTextView.getText().toString());
		assertEquals("Value in Brick SetSizeTo is not correct", (float) BrickValues.SET_SIZE_TO,
				setSizeToPrototypeValue);

		TextView changeSizeByTextView = (TextView) solo.getView(R.id.brick_change_size_by_prototype_text_view);
		float changeSizeByPrototypeValue = Float.parseFloat(changeSizeByTextView.getText().toString());
		assertEquals("Value in Brick ChangeSizeBy is not correct", (float) BrickValues.CHANGE_SIZE_BY,
				changeSizeByPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_set_transparency))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView setTransparencyTextView = (TextView) solo.getView(R.id.brick_set_transparency_to_prototype_text_view);
		float setTransparencyPrototypeValue = Float.parseFloat(setTransparencyTextView.getText().toString());
		assertEquals("Value in Brick SetTransparency is not correct", (float) BrickValues.SET_TRANSPARENCY,
				setTransparencyPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_change_ghost_effect))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView changeTransparencyTextView = (TextView) solo
				.getView(R.id.brick_set_transparency_to_prototype_text_view);
		float changeTransparencyPrototypeValue = Float.parseFloat(changeTransparencyTextView.getText().toString());
		assertEquals("Value in Brick SetTransparency is not correct", (float) BrickValues.SET_TRANSPARENCY,
				changeTransparencyPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_set_brightness))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView setBrightnessTextView = (TextView) solo.getView(R.id.brick_set_brightness_prototype_text_view);
		float setBrightnessPrototypeValue = Float.parseFloat(setBrightnessTextView.getText().toString());
		assertEquals("Value in Brick SetBrightness is not correct", (float) BrickValues.SET_BRIGHTNESS_TO,
				setBrightnessPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_change_brightness))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView changeBrightnessTextView = (TextView) solo.getView(R.id.brick_change_brightness_prototype_text_view);
		float changeBrightnessPrototypeValue = Float.parseFloat(changeBrightnessTextView.getText().toString());
		assertEquals("Value in Brick ChangeBrightness is not correct", (float) BrickValues.CHANGE_BRITHNESS_BY,
				changeBrightnessPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_change_brightness));
		solo.clickOnScreen(200, 200);

		TextView changeBrightnessEditText = (TextView) solo.getView(R.id.brick_change_brightness_edit_text);
		float changeBrightnessEditTextValue = Float.parseFloat(changeBrightnessEditText.getText().toString()
				.replace(',', '.'));

		assertEquals("Value in Selected Brick ChangeBrightness is not correct",
				(float) BrickValues.CHANGE_BRITHNESS_BY, changeBrightnessEditTextValue);
	}

	public void testSoundBricksDefaultValues() {
		String categorySoundText = solo.getString(R.string.category_sound);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categorySoundText);

		solo.searchText(solo.getString(R.string.brick_play_sound));
		Spinner playSoundSpinner = (Spinner) solo.getView(R.id.playsound_spinner);
		assertEquals("Value in PlaySoundBrick is not correct", solo.getString(R.string.new_broadcast_message),
				playSoundSpinner.getSelectedItem().toString());

		TextView setVolumeToTextView = (TextView) solo.getView(R.id.brick_set_volume_to_prototype_text_view);
		float setVolumeToPrototypeValue = Float.parseFloat(setVolumeToTextView.getText().toString());
		assertEquals("Value in Brick SetVolumeTo is not correct", (float) BrickValues.SET_VOLUME_TO,
				setVolumeToPrototypeValue);

		TextView changeVolumeByTextView = (TextView) solo.getView(R.id.brick_change_volume_by_prototype_text_view);
		float changeVolumeToPrototypeValue = Float.parseFloat(changeVolumeByTextView.getText().toString());
		assertEquals("Value in Brick ChangeVolumeBy is not correct", (float) BrickValues.CHANGE_VOLUME_BY,
				changeVolumeToPrototypeValue);

		solo.searchText(solo.getString(R.string.brick_speak));
		TextView speakTextView = (TextView) solo.getView(R.id.brick_speak_prototype_text_view);
		String speakPrototypeValue = speakTextView.getText().toString();
		String defaultSpeakValue = solo.getString(R.string.brick_speak_default_value);
		assertEquals("Value in Brick Speak is not correct", defaultSpeakValue, speakPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_speak));
		solo.sleep(300);

		UiTestUtils.dragFloatingBrickDownwards(solo);

		solo.sleep(300);

		String speakEditTextValue = ((TextView) solo.getView(R.id.brick_speak_edit_text)).getText().toString();
		assertEquals("Value in Selected Brick Speak is not correct", "\'" + defaultSpeakValue + "\'",
				speakEditTextValue.substring(0, speakEditTextValue.length() - 1));

		solo.sleep(500);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categorySoundText);
		solo.clickOnText(solo.getString(R.string.brick_change_volume_by));
		solo.sleep(300);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(300);

		TextView changeVolumeByEditText = (TextView) solo.getView(R.id.brick_change_volume_by_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		// in this case, between the minus operator and the value there is a blank also
		String changeVolumeByEditTextString = changeVolumeByEditText.getText().toString();
		float changeVolumeByEditTextValue = Float.parseFloat(changeVolumeByEditTextString.replaceAll(" ", "").replace(
				',', '.'));
		assertEquals("Value in Selected Brick ChangeVolumeBy is not correct", BrickValues.CHANGE_VOLUME_BY,
				changeVolumeByEditTextValue);
	}

	public void testControlBricksDefaultValues() {
		String categoryControlText = solo.getString(R.string.category_control);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryControlText);

		//Just to get focus
		solo.searchText(categoryControlText);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);

		TextView waitSecondsTextView = (TextView) solo.getView(R.id.brick_wait_prototype_text_view);
		float waitPrototypeValue = Float.parseFloat(waitSecondsTextView.getText().toString());
		assertEquals("Value in Brick Wait is not correct", (float) BrickValues.WAIT / 1000, waitPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_broadcast_receive))) {
			solo.scrollDownList(fragmentListView);
		}
		Spinner broadcastReceiveSpinner = (Spinner) solo.getView(R.id.brick_broadcast_receive_spinner);
		assertEquals("Value in BroadcastReceiverBrick is not correct",
				solo.getString(R.string.brick_broadcast_default_value), broadcastReceiveSpinner.getSelectedItem()
						.toString());

		if (!solo.searchText(solo.getString(R.string.brick_broadcast))) {
			solo.scrollDownList(fragmentListView);
		}
		Spinner broadcastSpinner = (Spinner) solo.getView(R.id.brick_broadcast_spinner);
		assertEquals("Value in BroadcastBrick is not correct", solo.getString(R.string.brick_broadcast_default_value),
				broadcastSpinner.getSelectedItem().toString());

		if (!solo.searchText(solo.getString(R.string.brick_broadcast_wait))) {
			solo.scrollDownList(fragmentListView);
		}
		Spinner broadcastWaitSpinner = (Spinner) solo.getView(R.id.brick_broadcast_wait_spinner);
		assertEquals("Value in BroadcastWaitBrick is not correct",
				solo.getString(R.string.brick_broadcast_default_value), broadcastWaitSpinner.getSelectedItem()
						.toString());

		if (!solo.searchText(solo.getString(R.string.brick_note))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView noteTextView = (TextView) solo.getView(R.id.brick_note_prototype_text_view);
		String notePrototypeValue = noteTextView.getText().toString();
		String defaultNoteValue = solo.getString(R.string.brick_note_default_value);
		assertEquals("Value in Note Speak is not correct", defaultNoteValue, notePrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_if_begin))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView ifLogicBeginView = (TextView) solo.getView(R.id.brick_if_begin_prototype_text_view);
		int ifLogicBeginPrototypeValue = Integer.parseInt(ifLogicBeginView.getText().toString());
		assertEquals("Value in If Begin is not correct", BrickValues.IF_CONDITION, ifLogicBeginPrototypeValue);

		if (!solo.searchText(solo.getString(R.string.brick_repeat))) {
			solo.scrollDownList(fragmentListView);
		}
		TextView repeatTextView = (TextView) solo.getView(R.id.brick_repeat_prototype_text_view);
		int repeatPrototypeValue = Integer.parseInt(repeatTextView.getText().toString());
		assertEquals("Value in Repeat Wait is not correct", BrickValues.REPEAT, repeatPrototypeValue);

		solo.clickOnText(solo.getString(R.string.brick_repeat));
		solo.clickOnScreen(200, 200);

		TextView repeatEditText = (TextView) solo.getView(R.id.brick_repeat_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		String repeatEditTextString = repeatEditText.getText().toString();
		int repeatEditTextValue = Integer
				.parseInt(repeatEditTextString.substring(0, repeatEditTextString.length() - 1));
		assertEquals("Value in Selected Brick Repeat is not correct", BrickValues.REPEAT, repeatEditTextValue);
	}

	public void testLegoBricksDefaultValues() {
		String categoryLegoNXTText = solo.getString(R.string.category_lego_nxt);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
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
		float nXTPlayTonePrototypeValue = Float.parseFloat(nxtPlayToneSecondsTextView.getText().toString());
		assertEquals("Value in Brick NXTPlayTone is not correct", BrickValues.LEGO_DURATION, nXTPlayTonePrototypeValue);

		TextView nxtPlayToneFreqTextView = (TextView) solo.getView(R.id.nxt_tone_freq_text_view);
		int nXTPlayToneFreqPrototypeValue = Integer.parseInt(nxtPlayToneFreqTextView.getText().toString());
		assertEquals("Value in Brick NXTPlayTone is not correct", BrickValues.LEGO_FREQUENCY,
				nXTPlayToneFreqPrototypeValue);

		Spinner legoNxtMotorStopSpinner = (Spinner) solo.getView(R.id.stop_motor_spinner);
		assertEquals("Spinner value in LegoNxtMotorStopBrick is not correct", BrickValues.LEGO_MOTOR,
				legoNxtMotorStopSpinner.getSelectedItem().toString());

		Spinner legoNxtMotorActionSpinner = (Spinner) solo.getView(R.id.lego_motor_action_spinner);
		assertEquals("Spinner value in LegoNxtMotorMoveBrick is not correct", BrickValues.LEGO_MOTOR,
				legoNxtMotorActionSpinner.getSelectedItem().toString());

		Spinner legoNxtMotorTurnAngleSpinner = (Spinner) solo.getView(R.id.lego_motor_turn_angle_spinner);
		assertEquals("Spinner value in LegoNxtMotorTurnAngleBrick is not correct", BrickValues.LEGO_MOTOR,
				legoNxtMotorTurnAngleSpinner.getSelectedItem().toString());

		solo.clickOnText(solo.getString(R.string.nxt_play_tone));
		solo.clickOnScreen(200, 200);

		TextView nxtPlayToneEditText = (TextView) solo.getView(R.id.nxt_tone_freq_edit_text);
		// Formula appends a blank after the value, so last character has to be deleted
		// before parsing an int from the string
		String nxtPlayToneEditTextString = nxtPlayToneEditText.getText().toString();
		int nxtPlayToneEditTextValue = Integer.parseInt(nxtPlayToneEditTextString.substring(0,
				nxtPlayToneEditTextString.length() - 1));
		assertEquals("Value in Selected Brick Repeat is not correct", BrickValues.LEGO_FREQUENCY,
				nxtPlayToneEditTextValue);
	}

	public void testDataBricksDefaultValues() {
		String categoryDataText = solo.getString(R.string.category_data);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryDataText);

		// Just to get focus
		solo.searchText(categoryDataText);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);

		solo.searchText(solo.getString(R.string.brick_set_variable));
		Spinner setVariableSpinner = (Spinner) solo.getView(R.id.set_variable_spinner);
		assertEquals("Value in SetVariableBrick is not correct", "BrickValueParameterTestUserVariable",
				((UserVariable) setVariableSpinner.getSelectedItem()).getName());

		TextView setVariableTextView = (TextView) solo.getView(R.id.brick_set_variable_prototype_view);
		assertEquals("Value in SetVariableBrick is not correct", "1.0", setVariableTextView.getText().toString());

		solo.searchText(solo.getString(R.string.brick_change_variable));
		Spinner changeVariableSpinner = (Spinner) solo.getView(R.id.change_variable_spinner);
		assertEquals("Value in ChangeVariableBrick is not correct", "BrickValueParameterTestUserVariable",
				((UserVariable) changeVariableSpinner.getSelectedItem()).getName());

		TextView changeVariableTextView = (TextView) solo.getView(R.id.brick_change_variable_prototype_view);
		assertEquals("Value in ChangeVariableBrick is not correct", "1.0", changeVariableTextView.getText().toString());

		solo.searchText(solo.getString(R.string.brick_add_item_to_userlist_add));
		Spinner addItemToUserListSpinner = (Spinner) solo.getView(R.id.add_item_to_userlist_spinner);
		assertEquals("Value in addItemToUserListSpinner is not correct", "BrickValueParameterTestUserList",
				((UserList) addItemToUserListSpinner.getSelectedItem()).getName());

		TextView addItemToUserListTextView = (TextView) solo.getView(R.id.brick_add_item_to_userlist_prototype_view);
		assertEquals("Value in Add Item To List is not correct", "1.0", addItemToUserListTextView.getText().toString());

		solo.searchText(solo.getString(R.string.brick_delete_item_from_userlist));
		Spinner deleteItemInListSpinner = (Spinner) solo.getView(R.id.delete_item_of_userlist_spinner);
		assertEquals("Value in deleteItemInListSpinner is not correct", "BrickValueParameterTestUserList",
				((UserList) deleteItemInListSpinner.getSelectedItem()).getName());

		TextView deleteItemInUserListIndexTextView = (TextView) solo.getView(R.id.brick_delete_item_of_userlist_prototype_view);
		assertEquals("Value in Delete Item Of List is not correct", "1", deleteItemInUserListIndexTextView.getText().toString());

		solo.scrollDownList(fragmentListView);

		solo.searchText(solo.getString(R.string.brick_insert_item_into_userlist_insert_into));
		Spinner insertItemInListSpinner = (Spinner) solo.getView(R.id.insert_item_into_userlist_spinner);
		assertEquals("Value in insertItemInListSpinner is not correct", "BrickValueParameterTestUserList",
				((UserList) insertItemInListSpinner.getSelectedItem()).getName());

		TextView insertItemInUserListIndexTextView = (TextView) solo.getView(R.id.brick_insert_item_into_userlist_at_index_prototype_view);
		assertEquals("Value in Insert Item In List is not correct", "1", insertItemInUserListIndexTextView.getText().toString());

		TextView insertItemInUserListIndexTextValue = (TextView) solo.getView(R.id.brick_insert_item_into_userlist_value_prototype_view);
		assertEquals("Value in insert item in userlist is not correct", "1.0", insertItemInUserListIndexTextValue.getText().toString());

		solo.searchText(solo.getString(R.string.brick_replace_item_in_userlist_replace_in_list));
		Spinner replaceItemInListSpinner = (Spinner) solo.getView(R.id.replace_item_in_userlist_spinner);
		assertEquals("Value in replaceItemInListSpinner is not correct", "BrickValueParameterTestUserList",
				((UserList) replaceItemInListSpinner.getSelectedItem()).getName());

		TextView replaceItemInUserListIndexTextView = (TextView) solo.getView(R.id.brick_replace_item_in_userlist_at_index_prototype_view);
		assertEquals("Value in replaceItemInList is not correct", "1", replaceItemInUserListIndexTextView.getText().toString());

		TextView replaceItemInUserListIndexTextValue = (TextView) solo.getView(R.id.brick_replace_item_in_userlist_value_prototype_view);
		assertEquals("Value in replaceItemInList is not correct", "1.0", replaceItemInUserListIndexTextValue.getText().toString());
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.PROJECTNAME1);
		Sprite sprite = new Sprite("Dog");
		Sprite sprite1 = new Sprite("Cat");
		Script script = new StartScript();
		Script script1 = new StartScript();
		sprite.addScript(script);
		sprite.addScript(script1);
		project.addSprite(sprite);
		project.addSprite(sprite1);
		project.getDataContainer().addProjectUserVariable("BrickValueParameterTestUserVariable");
		project.getDataContainer().addProjectUserList("BrickValueParameterTestUserList");

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
