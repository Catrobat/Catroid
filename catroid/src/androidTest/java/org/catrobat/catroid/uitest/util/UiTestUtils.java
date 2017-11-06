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
package org.catrobat.catroid.uitest.util;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.text.InputType;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.io.CharStreams;
import com.robotium.solo.Solo;

import junit.framework.AssertionFailedError;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.SpriteAttributesActivity;
import org.catrobat.catroid.ui.UserBrickSpriteActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorDataFragment;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public final class UiTestUtils {
	private static ProjectManager projectManager = ProjectManager.getInstance();
	private static SparseIntArray brickCategoryMap;
	private static List<InternToken> internTokenList = new ArrayList<InternToken>();
	private static final String TAG = UiTestUtils.class.getName();

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
	public static final String PROJECTNAME1 = "testingproject1";
	public static final String PROJECTNAME2 = "testingproject2";
	public static final String PROJECTNAME3 = "testingproject3";
	public static final String PROJECTNAMEOFFENSIVELANGUAGE = "fuck i have to use fuck penis";
	public static final String PROJECTDESCRIPTION1 = "testdescription1";
	public static final String PROJECTDESCRIPTION2 = "testdescription2";
	public static final String DEFAULT_TEST_PROJECT_NAME_MIXED_CASE = "TeStPROjeCt";
	public static final String COPIED_PROJECT_NAME = "copiedProject";
	public static final String JAPANESE_PROJECT_NAME = "これは例の説明です。";
	public static final String TEST_USER_BRICK_NAME = "New Brick 0";
	public static final String SECOND_TEST_USER_BRICK_NAME = "New Brick 1";
	public static final String TEST_USER_BRICK_VARIABLE = "Variable 0";
	public static final String NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME = "[Hey+, =lo_ok. I'm; -special! ?äöüß<>]";
	public static final String NORMAL_AND_SPECIAL_CHAR_PROJECT_NAME2 = "../*T?E\"S/T:%22T<E>S?T\\T\\E|S%äö|üß";
	public static final String JUST_SPECIAL_CHAR_PROJECT_NAME = "*\"/:<>?\\|";
	public static final String JUST_SPECIAL_CHAR_PROJECT_NAME2 = "*\"/:<>?\\|%";
	public static final String JUST_ONE_DOT_PROJECT_NAME = ".";
	public static final String JUST_TWO_DOTS_PROJECT_NAME = "..";
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<>();
	static {
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
	}

	public static final String CONFIG_FACEBOOK_NAME = "facebook_testuser_name";
	public static final String CONFIG_FACEBOOK_MAIL = "facebook_testuser_mail";
	public static final String CONFIG_FACEBOOK_PASSWORD = "facebook_testuser_password";
	public static final String CONFIG_FACEBOOK_ID = "facebook_testuser_id";
	public static final String CONFIG_FACEBOOK_APP_TOKEN = "facebook_app_token";
	public static final String CONFIG_GPLUS_NAME = "gplus_testuser_name";
	public static final String CONFIG_GPLUS_MAIL = "gplus_testuser_mail";
	public static final String CONFIG_GPLUS_PASSWORD = "gplus_testuser_password";
	public static final String CONFIG_GPLUS_ID = "gplus_testuser_id";

	public static final int DRAG_FRAMES = 35;
	public static final int SCRIPTS_INDEX = 0;
	public static final int LOOKS_INDEX = 1;
	public static final int SOUNDS_INDEX = 2;
	public static final int NFCTAGS_INDEX = 3;

	private static final List<Integer> FRAGMENT_INDEX_LIST = new ArrayList<>();

	public static String facebookTestUserName;
	public static String gplusTestUserName;
	public static String facebookTestuserMail;
	public static String facebookTestuserId;
	public static String gplusTestuserMail;
	public static String facebookTestuserPassword;
	public static String facebookAppToken;
	public static String gplusTestuserPassword;
	public static String gplusTestuserId;

	public enum FileTypes {
		IMAGE, SOUND, ROOT, SCREENSHOT
	}

	// Suppress default constructor for noninstantiability
	private UiTestUtils() {
		throw new AssertionError();
	}

	public static void enterText(Solo solo, int editTextIndex, String text) {

		solo.sleep(50);
		final EditText editText = solo.getEditText(editTextIndex);
		solo.getCurrentActivity().runOnUiThread(new Runnable() {
			public void run() {
				editText.setInputType(InputType.TYPE_NULL);
			}
		});
		solo.clearEditText(editTextIndex);
		solo.enterText(editTextIndex, text);
		solo.sleep(50);
	}

	/**
	 * Clicks on the EditText given by editTextId, inserts the double value and closes the Dialog
	 *
	 * @param value The value you want to put into the EditText
	 */
	public static void insertDoubleIntoEditText(Solo solo, double value) {
		insertValue(solo, new BigDecimal(value).toPlainString());	//NOPMD
	}

	private static void insertValue(Solo solo, String value) {

		for (char item : value.toCharArray()) {
			switch (item) {
				case '-':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
					break;
				case '0':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
					break;
				case '1':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
					break;
				case '2':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
					break;
				case '3':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
					break;
				case '4':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));
					break;
				case '5':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
					break;
				case '6':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
					break;
				case '7':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_7));
					break;
				case '8':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_8));
					break;
				case '9':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
					break;
				case '.':
				case ',':
					solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_decimal_mark));
			}
		}
	}

	public static void createUserListFromDataFragment(Solo solo, String userListName, boolean forAllSprites) {
		assertTrue("FormulaEditorDataFragment not shown: ",
				solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG));

		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add Data Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_data_dialog_title)));
		solo.waitForView(solo.getView(R.id.dialog_formula_editor_data_name_edit_text));
		solo.clickOnView(solo.getView(R.id.dialog_formula_editor_data_is_list_checkbox));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, userListName);

		if (forAllSprites) {
			solo.waitForView(solo.getView(R.id.dialog_formula_editor_data_name_global_variable_radio_button));
			solo.clickOnView(solo.getView(R.id.dialog_formula_editor_data_name_global_variable_radio_button));
		} else {
			solo.waitForView(solo.getView(R.id.dialog_formula_editor_data_name_local_variable_radio_button));
			solo.clickOnView(solo.getView(R.id.dialog_formula_editor_data_name_local_variable_radio_button));
		}
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	/**
	 * For bricks using the FormulaEditor. Tests starting the FE, entering a new number/formula and
	 * ensures its set correctly to the brick´s edit text field
	 */
	public static void testBrickWithFormulaEditor(Solo solo, Sprite sprite, int editTextId, double newValue, Brick.BrickField brickField, FormulaBrick theBrick) {

		solo.clickOnView(solo.getView(editTextId));

		insertDoubleIntoEditText(solo, newValue);

		assertEquals(
				"Text not updated within FormulaEditor",
				newValue,
				Double.parseDouble(((EditText) solo.getView(R.id.formula_editor_edit_field)).getText().toString()
						.replace(',', '.').replace(" ", "")));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);

		Formula formula = theBrick.getFormulaWithBrickField(brickField);
		try {
			assertEquals("Wrong text in field", newValue, formula.interpretDouble(sprite), 0.01f);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}

		assertEquals("Text not updated in the brick list", newValue,
				Double.parseDouble(((TextView) solo.getView(editTextId)).getText().toString().replace(',', '.')
						.replace(" ", "")), 0.01f);
	}

	public static void clickEnterClose(Solo solo, int editTextNumber, String value) {
		solo.clickOnEditText(editTextNumber);
		solo.clearEditText(0);
		solo.enterText(0, value);
		String buttonPositiveText = solo.getString(R.string.ok);
		// if click is not successful, try workaround
		try {
			solo.clickOnText(buttonPositiveText);
		} catch (AssertionFailedError e) {
			solo.sendKey(Solo.ENTER);
		}
		solo.sleep(50);
	}

	private static void initBrickCategoryMap() {
		brickCategoryMap = new SparseIntArray();

		//brickCategoryMap.put(R.string.brick_place_at_x, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_x, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_y, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_change_x_by, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_change_y_by, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_go_back, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_come_to_front, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_if_on_edge_bounce, R.string.category_motion);
		//brickCategoryMap.put(R.string.brick_move_n_steps, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_turn_left, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_turn_right, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_point_in_direction, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_point_to, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_glide, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_rotation_style_normal, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_rotation_style_lr, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_rotation_style_no, R.string.category_motion);

		brickCategoryMap.put(R.string.brick_set_look, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_size_to, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_change_size_by, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_hide, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_show, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_transparency, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_brightness, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_change_brightness, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_clear_graphic_effect, R.string.category_looks);
		//brickCategoryMap.put(R.string.brick_say, R.string.category_looks);
		//brickCategoryMap.put(R.string.brick_think, R.string.category_looks);

		brickCategoryMap.put(R.string.brick_play_sound, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_stop_all_sounds, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_set_volume_to, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_change_volume_by, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_speak, R.string.category_sound);

		brickCategoryMap.put(R.string.brick_when_started, R.string.category_control);
		brickCategoryMap.put(R.string.brick_when, R.string.category_control);
		brickCategoryMap.put(R.string.brick_wait, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast_receive, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast_wait, R.string.category_control);
		brickCategoryMap.put(R.string.brick_note, R.string.category_control);
		brickCategoryMap.put(R.string.brick_forever, R.string.category_control);
		brickCategoryMap.put(R.string.brick_repeat, R.string.category_control);
		brickCategoryMap.put(R.string.brick_if_begin, R.string.category_control);
		brickCategoryMap.put(R.string.brick_change_variable, R.string.category_data);
		brickCategoryMap.put(R.string.brick_set_variable, R.string.category_data);

//		brickCategoryMap.put(R.string.brick_motor_action, R.string.category_lego_nxt);

		brickCategoryMap.put(R.string.brick_drone_angle, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_move_backward, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_move_down, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_move_forward, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_move_left, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_move_right, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_move_up, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_play_led_animation, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_with, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_percent_power, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_set_config, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_switch_camera, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_takeoff_land, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_turn_left, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_turn_left_magneto, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_turn_right, R.string.category_drone);
		brickCategoryMap.put(R.string.brick_drone_turn_right_magneto, R.string.category_drone);

		brickCategoryMap.put(R.string.nxt_brick_motor_move, R.string.category_lego_nxt);
		brickCategoryMap.put(R.string.ev3_motor_move, R.string.category_lego_ev3);
		brickCategoryMap.put(R.string.brick_when_nfc, R.string.category_control);
	}

	public static int getBrickCategory(Solo solo, int brickStringId) {
		if (brickCategoryMap == null) {
			initBrickCategoryMap();
		}

		Integer brickCategoryid = brickCategoryMap.get(brickStringId);
		if (brickCategoryid == null) {
			String brickString = solo.getCurrentActivity().getString(brickStringId);
			throw new RuntimeException("No category was found for brick string \"" + brickString + "\".\n"
					+ "Please check brick string or add brick string to category map");
		}

		return brickCategoryMap.get(brickStringId);
	}

	public static void addNewBrick(Solo solo, int brickStringId) {
		int categoryStringId = getBrickCategory(solo, brickStringId);
		addNewBrick(solo, categoryStringId, brickStringId);
	}

	public static void addNewBrick(Solo solo, int categoryStringId, int brickStringId) {
		addNewBrick(solo, categoryStringId, brickStringId, 0);
	}

	public static void addNewBrick(Solo solo, int categoryStringId, int brickStringId, int nThElement) {
		String brickName = solo.getCurrentActivity().getString(brickStringId);
		addNewBrick(solo, categoryStringId, brickName, nThElement);
	}

	public static void addNewBrick(Solo solo, int categoryStringId, String brickName, int nThElement) {
		clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(1000);
		clickOnBrickCategory(solo, solo.getCurrentActivity().getString(categoryStringId));
		boolean fragmentAppeared = solo.waitForFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG, 5000);
		if (!fragmentAppeared) {
			fail("add brick fragment should appear");
		}

		if (categoryStringId == R.string.category_user_bricks) {
			solo.sleep(300);
			clickOnBottomBar(solo, R.id.button_add);
			solo.waitForDialogToOpen();
			solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
			solo.waitForDialogToClose();
		}
		solo.sleep(600);
		boolean succeeded = clickOnBrickInAddBrickFragment(solo, brickName, false);
		if (!succeeded) {
			fail(brickName + " should appear. Failed to scroll to find it.");
		}

		if (categoryStringId == R.string.category_user_bricks) {
			String stringOnAddToScriptButton = solo.getCurrentActivity().getString(
					R.string.brick_context_dialog_add_to_script);
			if (!solo.waitForText(stringOnAddToScriptButton, 0, 2000)) {
				fail("Text '" + stringOnAddToScriptButton + "' not shown in 5 secs!");
			}
			solo.clickOnText(stringOnAddToScriptButton);
		}
		solo.sleep(600);
	}

	public static void deleteFirstUserBrick(Solo solo, String brickName) {
		boolean fragmentAppeared = solo.waitForFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG, 5000);
		if (!fragmentAppeared) {
			fail("add brick fragment should appear");
		}

		solo.sleep(600);
		openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

		solo.clickOnCheckBox(1);

		acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
	}

	public static void clickOnBrickCategory(Solo solo, String category) {
		if (!solo.waitForText(category, 0, 300)) {
			solo.drag(40, 40, 300, 40, DRAG_FRAMES);
		}
		solo.clickOnText(category);
	}

	public static void showSourceAndEditBrick(String brickName, Solo solo) {
		showSourceAndEditBrick(brickName, true, solo);
	}

	public static void showSourceAndEditBrick(String brickName, boolean click, Solo solo) {
		if (click) {
			solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
		}

		String stringOnShowSourceButton = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_show_source);
		solo.waitForText(stringOnShowSourceButton);
		solo.clickOnText(stringOnShowSourceButton);

		boolean activityShowedUp = solo.waitForActivity(UserBrickSpriteActivity.class, 3000);
		if (!activityShowedUp) {
			fail("UserBrickScriptActivity should have showed up");
		}

		solo.sleep(50);
	}

	public static boolean clickOnBrickInAddBrickFragment(Solo solo, String brickName, boolean addToScript) {
		boolean success = false;
		int lowestIdTimeBeforeLast = -2;
		int lowestIdLastTime = -1;

		while (!success && lowestIdLastTime != lowestIdTimeBeforeLast) {

			lowestIdTimeBeforeLast = lowestIdLastTime;
			int farthestDownThisTime = -999999;
			int highestUpThisTime = 999999;

			ArrayList<TextView> array = solo.getCurrentViews(TextView.class);
			for (TextView candidate : array) {
				View greatGreatGrandParent = greatGreatGrandParent(candidate);
				if (greatGreatGrandParent != null && greatGreatGrandParent.getId() == R.id.add_brick_fragment_list) {
					int bottom = getBottomOfBrickGivenViewInsideThatBrick(candidate);
					if (farthestDownThisTime < bottom) {
						farthestDownThisTime = bottom;
						lowestIdLastTime = candidate.getId();
					}
					if (highestUpThisTime > bottom) {
						highestUpThisTime = bottom;
					}
					if (candidate.getText().toString().equals(brickName)) {
						solo.sleep(500);
						solo.clickOnView(candidate);
						success = true;
						break;
					}
				}
			}

			if (!success) {
				int difference = farthestDownThisTime - highestUpThisTime;
				solo.drag(40, 40, difference * 0.75f, 40, DRAG_FRAMES);
			}
		}

		return success;
	}

	private static int getBottomOfBrickGivenViewInsideThatBrick(View view) {
		return ((View) (view.getParent().getParent())).getBottom();
	}

	private static View greatGreatGrandParent(View view) {
		ViewParent parent = view.getParent();
		int i = 0;
		while (i < 3 && parent != null) {
			parent = parent.getParent();
			i++;
		}

		return (parent != null && parent instanceof View ? ((View) parent) : null);
	}

	public static int[] dragFloatingBrickUpwards(Solo solo) {
		return dragFloatingBrick(solo, -1);
	}

	public static int[] dragFloatingBrickUpwards(Solo solo, int bricks) {
		return dragFloatingBrick(solo, -bricks);
	}

	public static int[] dragFloatingBrickDownwards(Solo solo) {
		return dragFloatingBrick(solo, 1);
	}

	public static int[] dragFloatingBrick(Solo solo, float offsetY) {
		int[] location = null;
		int width = 0;
		int height = 0;

		ArrayList<View> views = solo.getCurrentViews();
		for (View view : views) {
			if (view.getId() == R.id.drag_and_drop_list_view_image_view) {
				location = new int[2];
				view.getLocationOnScreen(location);
				width = view.getWidth();
				height = view.getHeight();
			}
		}

		if (location == null) {
			return null;
		}

		int originX = location[0] + Math.round(width * 0.2f);
		int originY = location[1] + Math.round(height * 0.5f);
		int destinationX = originX;
		int destinationY = Math.round(originY + height * offsetY);

		solo.drag(originX, destinationX, originY, destinationY, DRAG_FRAMES);
		solo.sleep(1000);

		location[0] = destinationX;
		location[1] = destinationY;

		return location;
	}

	public static List<Brick> createTestProject(String projectName) {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(null, projectName);
		Sprite firstSprite = new SingleSprite("cat");

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<Brick>();
		brickList.add(new HideBrick());
		brickList.add(new ShowBrick());
		brickList.add(new SetSizeToBrick(size));
		brickList.add(new GoNStepsBackBrick(1));
		brickList.add(new ComeToFrontBrick());
		brickList.add(new PlaceAtBrick(xPosition, yPosition));

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		projectManager.setCurrentScene(project.getDefaultScene());
		StorageHandler.getInstance().saveProject(project);

		// the application version is needed when the project will be uploaded
		// 0.7.3beta is the lowest possible version currently accepted by the web
		Reflection.setPrivateField(project.getXmlHeader(), "applicationVersion", "0.7.3beta");

		return brickList;
	}

	public static List<Brick> createTestProject() {
		return createTestProject(DEFAULT_TEST_PROJECT_NAME);
	}

	public static void createTestProjectWithNestedUserBrick() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");

		Script testScript = new StartScript();

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		UserBrick firstUserBrick = new UserBrick(new UserScriptDefinitionBrick());
		firstUserBrick.getDefinitionBrick().addUIText(TEST_USER_BRICK_NAME + "2");
		firstUserBrick.getDefinitionBrick().addUILocalizedVariable(TEST_USER_BRICK_VARIABLE + "2");
		firstUserBrick.appendBrickToScript(new ChangeXByNBrick(BrickValues.CHANGE_X_BY));
		firstSprite.addUserBrick(firstUserBrick);

		UserBrick secondUserBrick = new UserBrick(new UserScriptDefinitionBrick());
		secondUserBrick.getDefinitionBrick().addUIText(TEST_USER_BRICK_NAME);
		secondUserBrick.getDefinitionBrick().addUILocalizedVariable(TEST_USER_BRICK_VARIABLE);
		secondUserBrick.appendBrickToScript(firstUserBrick);
		secondUserBrick.appendBrickToScript(new ChangeYByNBrick(BrickValues.CHANGE_Y_BY));

		testScript.addBrick(secondUserBrick);
		testScript.addBrick(new SetSizeToBrick(BrickValues.SET_SIZE_TO));
		testScript.addBrick(new SetVariableBrick(BrickValues.SET_BRIGHTNESS_TO));
		firstSprite.addUserBrick(secondUserBrick);

		firstSprite.addScript(testScript);

		project.getDefaultScene().addSprite(firstSprite);
		StorageHandler.getInstance().saveProject(project);
	}

	public static List<Brick> createTestProjectWithEveryBrick() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<>();

		brickList.add(new BroadcastBrick("broadcastMessage1"));
		brickList.add(new BroadcastWaitBrick("broadcastMessage2"));
		brickList.add(new ChangeBrightnessByNBrick(0));
		brickList.add(new ChangeTransparencyByNBrick(0));
		brickList.add(new ChangeSizeByNBrick(0));
		brickList.add(new ChangeVolumeByNBrick(0));
		brickList.add(new ChangeVariableBrick(0));
		brickList.add(new ChangeXByNBrick(0));
		brickList.add(new ChangeYByNBrick(0));
		brickList.add(new ClearGraphicEffectBrick());
		brickList.add(new ComeToFrontBrick());
		brickList.add(new GlideToBrick(0, 0, 0));
		brickList.add(new GoNStepsBackBrick(0));
		brickList.add(new HideBrick());
		brickList.add(new IfOnEdgeBounceBrick());
		brickList.add(new MoveNStepsBrick(0));
		brickList.add(new NextLookBrick());
		brickList.add(new NoteBrick(""));
		brickList.add(new PlaceAtBrick(0, 0));
		brickList.add(new PlaySoundBrick());
		brickList.add(new PointInDirectionBrick(Direction.DOWN));
		brickList.add(new PointToBrick(firstSprite));
		brickList.add(new SetBrightnessBrick(0));
		brickList.add(new SetTransparencyBrick(0));
		brickList.add(new SetLookBrick());
		brickList.add(new SetSizeToBrick(0));
		brickList.add(new SetVariableBrick(0));
		brickList.add(new SetVolumeToBrick(0));
		brickList.add(new SetXBrick(0));
		brickList.add(new SetYBrick(0));
		brickList.add(new ShowBrick());
		brickList.add(new SpeakBrick(""));
		brickList.add(new StopAllSoundsBrick());
		brickList.add(new TurnLeftBrick(0));
		brickList.add(new TurnRightBrick(0));
		brickList.add(new WaitBrick(0));
		brickList.add(new SetColorBrick(0f));
		brickList.add(new ChangeColorByNBrick(25f));

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static void createEmptyProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
	}

	/**
	 * saves a file into the project folder
	 * if project == null or "" file will be saved into Catroid folder
	 *
	 * @param project Folder where the file will be saved, this folder should exist
	 * @param name    Name of the file
	 * @param fileID  the id of the file --> needs the right context
	 * @param context
	 * @param type    type of the file: 0 = imagefile, 1 = soundfile
	 * @return the file
	 * @throws IOException
	 */
	public static File saveFileToProject(String project, String sceneName, String name, int fileID, Context context, FileTypes type) {

		boolean withChecksum = true;
		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Constants.DEFAULT_ROOT + "/";
		} else {
			switch (type) {
				case IMAGE:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + sceneName + "/" + Constants.IMAGE_DIRECTORY + "/";
					break;
				case SOUND:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + sceneName + "/" + Constants.SOUND_DIRECTORY + "/";
					break;
				case SCREENSHOT:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + sceneName + "/";
					withChecksum = false;
					break;
				case ROOT:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/";
					withChecksum = false;
					break;
				default:
					filePath = Constants.DEFAULT_ROOT + "/";
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID),
				Constants.BUFFER_8K);

		try {
			File file = new File(filePath + name);
			file.getParentFile().mkdirs();
			file.createNewFile();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Constants.BUFFER_8K);
			byte[] buffer = new byte[Constants.BUFFER_8K];
			int length = 0;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			out.close();

			String checksum;
			if (withChecksum) {
				checksum = Utils.md5Checksum(file) + "_";
			} else {
				checksum = "";
			}

			File tempFile = new File(filePath + checksum + name);
			file.renameTo(tempFile);

			return tempFile;
		} catch (IOException e) {
			Log.e(TAG, "File handling error", e);
			return null;
		}
	}

	public static Project createProject(String projectName, ArrayList<Sprite> spriteList, Context context) {
		Project project = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		for (Sprite sprite : spriteList) {
			ProjectManager.getInstance().getCurrentScene().addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static List<InternToken> getInternTokenList() {
		return internTokenList;
	}

	public static void clearAllUtilTestProjects() {
		Log.v(TAG, "clearAllUtilTestProjects");
		File directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME1);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME2);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME3);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "defaultProject");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "standardProjekt");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "My first project");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "Mein erstes Projekt");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "My first program");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "Mein erstes Programm");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "Project");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME_MIXED_CASE);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + COPIED_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + JAPANESE_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
	}

	public static void clickOnActionBar(Solo solo, int imageButtonId) {
		solo.clickOnActionBarItem(imageButtonId);
	}

	/**
	 * This method can be used in 2 ways. Either to click on an action item
	 * (icon), or to click on an item in the overflow menu. So either pass a
	 * String + ID --OR-- a String + 0.
	 *
	 * @param solo                 Use Robotium functionality
	 * @param overflowMenuItemName Name of the overflow menu item
	 * @param menuItemId           ID of an action item (icon)
	 */
	public static void openActionMode(Solo solo, String overflowMenuItemName, int menuItemId) {

		solo.sleep(1000);
		ArrayList<View> views = solo.getCurrentViews();
		ArrayList<Integer> ids = new ArrayList<>();
		for (View view : views) {
			ids.add(view.getId());
		}

		if (ids.contains(menuItemId)) {
			solo.waitForView(menuItemId, 0, 20000, false);
			UiTestUtils.clickOnActionBar(solo, menuItemId);
		} else if (overflowMenuItemName != null) {
			solo.waitForText(overflowMenuItemName, 0, 20000, false);

			if (overflowMenuItemName.equals(solo.getString(R.string.unpack))) {
				solo.clickOnActionBarItem(menuItemId);
			} else {
				solo.clickOnMenuItem(overflowMenuItemName, true);
			}
		} else {
			fail("Cannot click on element with menuItemid " + menuItemId + " or overflowMenuItemName "
					+ overflowMenuItemName);
		}

		solo.sleep(400);
	}

	public static void acceptAndCloseActionMode(Solo solo) {
		View doneButton = solo.getView(Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android"));

		solo.clickOnView(doneButton);
		solo.waitForView(doneButton.getId());
	}

	/**
	 * Due to maintainability reasons you should use this method to open an options menu.The way to open an options menu
	 * might differ in future.
	 */
	public static void openOptionsMenu(Solo solo) {
		solo.sendKey(Solo.MENU);
		solo.sleep(200);
	}

	public static void clickOnBottomBar(Solo solo, int buttonId) {
		solo.waitForView(ImageButton.class);
		ImageButton imageButton = (ImageButton) solo.getView(buttonId);
		solo.clickOnView(imageButton);
	}

	public static void clickOnPlayButton(Solo solo) {
		clickOnBottomBar(solo, R.id.button_play);
	}

	public static void createValidUserWithCredentials(Context context, String testUser, String testPassword, String
			testEmail) {
		try {
			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, context);

			assertTrue("User has not been registered", userRegistered);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Error creating test user.", e);
			fail("Error creating test user.");
		}
	}

	public static void createValidUser(Context context) {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = Constants.NO_TOKEN;
			boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
					"de", "at", token, context);

			assertTrue("User has not been registered", userRegistered);
		} catch (WebconnectionException e) {
			Log.e(TAG, "Error creating test user.", e);
			fail("Error creating test user.");
		}
	}

	// Stage methods
	public static void compareByteArrays(byte[] firstArray, byte[] secondArray) {
		assertEquals("Length of byte arrays not equal", firstArray.length, secondArray.length);

		assertEquals("Arrays don't have same content.", firstArray[0] & 0xFF, secondArray[0] & 0xFF, 10);
		assertEquals("Arrays don't have same content.", firstArray[1] & 0xFF, secondArray[1] & 0xFF, 10);
		assertEquals("Arrays don't have same content.", firstArray[2] & 0xFF, secondArray[2] & 0xFF, 10);
		assertEquals("Arrays don't have same content.", firstArray[3] & 0xFF, secondArray[3] & 0xFF, 10);
	}

	/**
	 * Returns the absolute pixel y coordinates of elements from a listview
	 * with a given index
	 */
	public static ArrayList<Integer> getListItemYPositions(final Solo solo, int listViewIndex) {
		ArrayList<Integer> yPositionList = new ArrayList<>();
		if (!solo.waitForView(ListView.class, 0, 10000, false)) {
			fail("ListView not shown in 10 secs!");
		}

		ArrayList<ListView> listViews = solo.getCurrentViews(ListView.class);
		if (listViews.size() <= listViewIndex) {
			fail("Listview Index wrong");
		}
		ListView listView = listViews.get(listViewIndex);

		for (int i = 0; i < listView.getChildCount(); ++i) {
			View currentViewInList = listView.getChildAt(i);

			Rect globalVisibleRectangle = new Rect();
			currentViewInList.getGlobalVisibleRect(globalVisibleRectangle);
			int middleYPosition = globalVisibleRectangle.top + globalVisibleRectangle.height() / 2;
			yPositionList.add(middleYPosition);
		}

		return yPositionList;
	}

	public static int getAddedListItemYPosition(Solo solo) {
		ArrayList<Integer> yPositionsList = getListItemYPositions(solo, 0);
		int middleYPositionIndex = yPositionsList.size() / 2;

		return yPositionsList.get(middleYPositionIndex);
	}

	private static class ProjectWithCatrobatLanguageVersion extends Project {
		static final long serialVersionUID = 1L;
		private final float catrobatLanguageVersion;

		ProjectWithCatrobatLanguageVersion(String name, float catrobatLanguageVersion) {
			super(null, name);
			this.catrobatLanguageVersion = catrobatLanguageVersion;
		}

		@Override
		public float getCatrobatLanguageVersion() {
			return catrobatLanguageVersion;
		}
	}

	public static boolean createTestProjectOnLocalStorageWithCatrobatLanguageVersion(float catrobatLanguageVersion) {
		Project project = new ProjectWithCatrobatLanguageVersion(DEFAULT_TEST_PROJECT_NAME, catrobatLanguageVersion);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		return StorageHandler.getInstance().saveProject(project);
	}

	public static void goToHomeActivity(Activity activity) {
		Intent intent = new Intent(activity, MainMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}

	public static void clickOnHomeActionBarButton(Solo solo) {
		solo.clickOnActionBarHomeButton();
	}

	public static void getIntoSpritesFromMainMenu(Solo solo) {
		getIntoSpritesFromMainMenu(solo, null);
	}

	public static void getIntoScenesFromMainMenu(Solo solo) {
		Log.d(TAG, "waitForMainMenuActivity: " + solo.waitForActivity(MainMenuActivity.class.getSimpleName()));
		solo.sleep(300);

		String continueString = solo.getString(R.string.main_menu_continue);
		solo.waitForText(continueString);

		solo.clickOnText(continueString);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
	}

	public static void getIntoSpritesFromMainMenu(Solo solo, String sceneName) {
		Log.d(TAG, "waitForMainMenuActivity: " + solo.waitForActivity(MainMenuActivity.class.getSimpleName()));
		solo.sleep(300);

		String continueString = solo.getString(R.string.main_menu_continue);
		solo.waitForText(continueString);

		solo.clickOnText(continueString);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		if (sceneName != null) {
			solo.clickOnText(sceneName);
		}
		solo.waitForView(ListView.class);
	}

	public static void getIntoProgramMenuFromMainMenu(Solo solo, String sceneName, int spriteIndex) {
		getIntoSpritesFromMainMenu(solo, sceneName);
		solo.sleep(200);

		solo.clickInList(spriteIndex);
		solo.waitForActivity(SpriteAttributesActivity.class.getSimpleName());
	}

	public static void getIntoScriptActivityFromMainMenu(Solo solo) {
		getIntoScriptActivityFromMainMenu(solo, null, 0);
	}

	public static void getIntoScriptActivityFromMainMenu(Solo solo, String sceneName) {
		getIntoScriptActivityFromMainMenu(solo, sceneName, 0);
	}

	public static void getIntoScriptActivityFromMainMenu(Solo solo, int spriteIndex) {
		getIntoScriptActivityFromMainMenu(solo, null, spriteIndex);
	}

	public static void getIntoScriptActivityFromMainMenu(Solo solo, String sceneName, int spriteIndex) {
		getIntoProgramMenuFromMainMenu(solo, sceneName, spriteIndex);

		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(SpriteActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
	}

	public static boolean clickOnTextInList(Solo solo, String text) {
		solo.sleep(300);
		ArrayList<TextView> textViews = solo.getCurrentViews(TextView.class, solo.getView(android.R.id.content));
		for (int textView = 0; textView < textViews.size(); textView++) {
			TextView view = textViews.get(textView);
			if (view.getText().toString().equalsIgnoreCase(text)) {
				solo.clickOnView(view);
				return true;
			}
		}
		return false;
	}

	public static void clickOnStageCoordinates(Solo solo, int x, int y, int screenWidth, int screenHeight) {
		solo.clickOnScreen(screenWidth / 2 + x, screenHeight / 2 - y);
	}

	/**
	 * Returns to the main screen.
	 * This method should be called in tearDown() in tests which use Robotium.
	 * See explanation here:
	 * http://stackoverflow.com/questions/7851351/robotium-in-the-suite-of-tests-each-next-test-is-
	 * affected-by-the-previous-test
	 */
	public static void goBackToHome(Instrumentation instrumentation) {
		boolean more = true;
		while (more) {
			try {
				instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				instrumentation.waitForIdleSync();
			} catch (SecurityException e) { // Done, at Home.
				more = false;
			}
		}
	}

	public static void cropImage(String pathToImageFile, int sampleSize) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		Bitmap imageBitmap = BitmapFactory.decodeFile(pathToImageFile, options);
		File imageFile = new File(pathToImageFile);
		StorageHandler.saveBitmapToImageFile(imageFile, imageBitmap);
	}

	public static ListView getScriptListView(Solo solo) {
		for (ListView listView : solo.getCurrentViews(ListView.class)) {
			if (listView instanceof BrickListView && listView.getAdapter() instanceof BrickAdapter) {
				return listView;
			}
		}
		fail("Could not find a Script ListView");
		return null;
	}

	public static void waitForFragment(Solo solo, int fragmentRootLayoutId, int timeout) {
		boolean fragmentFoundInTime = solo.waitForView(solo.getView(fragmentRootLayoutId), timeout, true);
		if (!fragmentFoundInTime) {
			fail("Fragment was not loaded");
		}
	}

	public static View getViewContainerByIds(Solo solo, int id, int containerId) {
		View parent = solo.getView(containerId);
		List<View> views = solo.getViews(parent);
		for (View view : views) {
			if (view.getId() == id) {
				return view;
			}
		}
		return null;
	}

	public static void prepareStageForTest() {
		Reflection.setPrivateField(StageListener.class, "DYNAMIC_SAMPLING_RATE_FOR_ACTIONS", false);
	}

	/*
	 * This is a workaround from this robotium issue
	 * http://code.google.com/p/robotium/issues/detail?id=296
	 *
	 * This method should be removed, when the issue is fixed in robotium!
	 */
	public static void clickOnButton(Solo solo, ActivityInstrumentationTestCase2<?> testCase, String buttonText) {
		final Button buttonWithinTheDialog = solo.getButton(buttonText);
		try {
			testCase.runTestOnUiThread(new Runnable() {
				public void run() {
					buttonWithinTheDialog.performClick();
				}
			});
		} catch (Throwable throwable) {
			Log.e(TAG, throwable.getMessage());
		}
		solo.sleep(500);
	}

	public static void waitForText(Solo solo, String text) {
		assertTrue("Text not found!", solo.waitForText(text, 0, 3000));
	}

	public static void switchToFragmentInScriptActivity(Solo solo, int fragmentIndex) {
		solo.goBack();
		solo.waitForActivity(SpriteAttributesActivity.class);
		solo.clickOnButton(fragmentIndex);
		solo.waitForActivity(SpriteActivity.class);
		int id = FRAGMENT_INDEX_LIST.get(fragmentIndex);
		solo.waitForFragmentById(id);
	}

	public static void clickOnExactText(Solo solo, String text) {
		String regularExpressionForExactClick = "^" + java.util.regex.Pattern.quote(text) + "$";
		solo.clickOnText(regularExpressionForExactClick);
	}

	public static void clickOnText(Solo solo, String text) {
		solo.waitForText(text);
		solo.clickOnText(text);
		solo.sleep(100);
	}

	public static Map<String, String> readConfigFile(Context context) {
		try {
			InputStreamReader reader = new InputStreamReader(context.getAssets().open("oauth_config.xml"));
			String text = CharStreams.toString(reader);
			reader.close();
			facebookTestUserName = text.substring(text.indexOf(CONFIG_FACEBOOK_NAME) + CONFIG_FACEBOOK_NAME.length() + 1,
					text.indexOf("/" + CONFIG_FACEBOOK_NAME) - 1);
			facebookTestuserMail = text.substring(text.indexOf(CONFIG_FACEBOOK_MAIL) + CONFIG_FACEBOOK_MAIL.length() + 1,
					text.indexOf("/" + CONFIG_FACEBOOK_MAIL) - 1);
			facebookTestuserPassword = text.substring(text.indexOf(CONFIG_FACEBOOK_PASSWORD) + CONFIG_FACEBOOK_PASSWORD.length() + 1,
					text.indexOf("/" + CONFIG_FACEBOOK_PASSWORD) - 1);
			facebookTestuserId = text.substring(text.indexOf(CONFIG_FACEBOOK_ID) + CONFIG_FACEBOOK_ID.length() + 1,
					text.indexOf("/" + CONFIG_FACEBOOK_ID) - 1);
			facebookAppToken = text.substring(text.indexOf(CONFIG_FACEBOOK_APP_TOKEN) + CONFIG_FACEBOOK_APP_TOKEN.length() + 1,
					text.indexOf("/" + CONFIG_FACEBOOK_APP_TOKEN) - 1);
			gplusTestUserName = text.substring(text.indexOf(CONFIG_GPLUS_NAME) + CONFIG_GPLUS_NAME.length() + 1,
					text.indexOf("/" + CONFIG_GPLUS_NAME) - 1);
			gplusTestuserMail = text.substring(text.indexOf(CONFIG_GPLUS_MAIL) + CONFIG_GPLUS_MAIL.length() + 1,
					text.indexOf("/" + CONFIG_GPLUS_MAIL) - 1);
			gplusTestuserPassword = text.substring(text.indexOf(CONFIG_GPLUS_PASSWORD) + CONFIG_GPLUS_PASSWORD.length() + 1,
					text.indexOf("/" + CONFIG_GPLUS_PASSWORD) - 1);
			gplusTestuserId = text.substring(text.indexOf(CONFIG_GPLUS_ID) + CONFIG_GPLUS_ID.length() + 1,
					text.indexOf("/" + CONFIG_GPLUS_ID) - 1);

			Map<String, String> configMap = new HashMap<>();
			configMap.put(CONFIG_FACEBOOK_NAME, facebookTestUserName);
			configMap.put(CONFIG_FACEBOOK_MAIL, facebookTestuserMail);
			configMap.put(CONFIG_FACEBOOK_PASSWORD, facebookTestuserPassword);
			configMap.put(CONFIG_FACEBOOK_ID, facebookTestuserId);
			configMap.put(CONFIG_FACEBOOK_APP_TOKEN, facebookAppToken);
			configMap.put(CONFIG_GPLUS_NAME, gplusTestUserName);
			configMap.put(CONFIG_GPLUS_MAIL, gplusTestuserMail);
			configMap.put(CONFIG_GPLUS_PASSWORD, gplusTestuserPassword);
			configMap.put(CONFIG_GPLUS_ID, gplusTestuserId);
			return configMap;
		} catch (IOException e) {
			Log.e(TAG, "IOException occurred", e);
		}
		return null;
	}
}
