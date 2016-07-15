/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import junit.framework.AssertionFailedError;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
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
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.PointToBrick.SpinnerAdapterWrapper;
import org.catrobat.catroid.content.bricks.RepeatBrick;
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
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.UserBrickScriptActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog.ActionAfterFinished;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorDataFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.utils.NotificationData;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT;
import static org.catrobat.catroid.utils.Utils.buildPath;

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
	static {
		FRAGMENT_INDEX_LIST.add(R.id.fragment_script);
		FRAGMENT_INDEX_LIST.add(R.id.fragment_look);
		FRAGMENT_INDEX_LIST.add(R.id.fragment_sprites_list);
		FRAGMENT_INDEX_LIST.add(R.id.fragment_nfctags);
	}
	public static SetVariableBrick createSendBroadcastAfterBroadcastAndWaitProject(String message) {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("sprite1");
		Script scriptOfSprite1 = new StartScript();

		firstSprite.addScript(scriptOfSprite1);
		project.getDefaultScene().addSprite(firstSprite);

		Script startScript = firstSprite.getScript(0);
		SetVariableBrick setVariableBrick = new SetVariableBrick(1.0f);
		startScript.addBrick(setVariableBrick);
		startScript.addBrick(new BroadcastWaitBrick(message));
		startScript.addBrick(new ChangeVariableBrick(10.0f));

		Sprite secondSprite = new SingleSprite("sprite2");
		Script scriptOfSprite2 = new StartScript();
		secondSprite.addScript(scriptOfSprite2);
		scriptOfSprite2.addBrick(new WaitBrick(300));
		scriptOfSprite2.addBrick(new ChangeVariableBrick(100.0f));
		scriptOfSprite2.addBrick(new BroadcastBrick(message));
		project.getDefaultScene().addSprite(secondSprite);

		Sprite thirdSprite = new SingleSprite("sprite3");
		Script whenIReceive = new BroadcastScript(message);
		thirdSprite.addScript(whenIReceive);
		whenIReceive.addBrick(new ChangeVariableBrick(1000.0f));
		project.getDefaultScene().addSprite(thirdSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(scriptOfSprite2);

		return setVariableBrick;
	}

	public static int createSendBroadcastInBroadcastAndWaitProject(String message1, String message2, double degreesToTurn, Sprite secondSprite, Sprite thirdSprite) {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("sprite1");
		Script testScript = new StartScript();

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		int initialRotation = (int) firstSprite.look.getRotation();
		Script startScript = firstSprite.getScript(0);
		startScript.addBrick(new BroadcastBrick(message1));

		Script whenIReceiveSecondSprite = new BroadcastScript(message1);
		secondSprite.addScript(whenIReceiveSecondSprite);
		whenIReceiveSecondSprite.addBrick(new TurnRightBrick(degreesToTurn));
		whenIReceiveSecondSprite.addBrick(new WaitBrick(1000));
		whenIReceiveSecondSprite.addBrick(new BroadcastWaitBrick(message2));
		project.getDefaultScene().addSprite(secondSprite);

		Script whenIReceiveThirdSprite = new BroadcastScript(message1);
		thirdSprite.addScript(whenIReceiveThirdSprite);
		whenIReceiveThirdSprite.addBrick(new TurnLeftBrick(degreesToTurn));
		whenIReceiveThirdSprite.addBrick(new WaitBrick(1000));
		whenIReceiveThirdSprite.addBrick(new BroadcastBrick(message1));
		project.getDefaultScene().addSprite(thirdSprite);

		return initialRotation;
	}

	public static SetVariableBrick createSameActionsBroadcastProject(String message) {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("sprite1");
		Script firstScript = new StartScript();

		firstSprite.addScript(firstScript);
		project.getDefaultScene().addSprite(firstSprite);

		Script startScript = firstSprite.getScript(0);
		SetVariableBrick setVariableBrick = new SetVariableBrick(0.0f);
		startScript.addBrick(setVariableBrick);
		RepeatBrick repeatBrick = new RepeatBrick(10);
		LoopEndBrick endBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(endBrick);
		startScript.addBrick(repeatBrick);
		startScript.addBrick(new BroadcastWaitBrick(message));
		startScript.addBrick(endBrick);

		Sprite secondSprite = new SingleSprite("sprite2");
		Script secondScript = new BroadcastScript(message);
		secondSprite.addScript(secondScript);
		IfLogicBeginBrick ifBeginBrickSecondScript = new IfLogicBeginBrick(1);
		IfLogicElseBrick ifElseBrickSecondScript = new IfLogicElseBrick(ifBeginBrickSecondScript);
		IfLogicEndBrick ifEndBrickSecondScript = new IfLogicEndBrick(ifElseBrickSecondScript, ifBeginBrickSecondScript);
		secondScript.addBrick(ifBeginBrickSecondScript);
		secondScript.addBrick(new ChangeVariableBrick(1.0f));
		secondScript.addBrick(ifElseBrickSecondScript);
		secondScript.addBrick(ifEndBrickSecondScript);
		project.getDefaultScene().addSprite(secondSprite);

		Sprite thirdSprite = new SingleSprite("sprite3");
		Script thirdScript = new BroadcastScript(message);
		thirdSprite.addScript(thirdScript);
		IfLogicBeginBrick ifBeginBrickThirdScript = new IfLogicBeginBrick(1);
		IfLogicElseBrick ifElseBrickThirdScript = new IfLogicElseBrick(ifBeginBrickThirdScript);
		IfLogicEndBrick ifEndBrickThirdScript = new IfLogicEndBrick(ifElseBrickThirdScript, ifBeginBrickThirdScript);
		thirdScript.addBrick(ifBeginBrickThirdScript);
		thirdScript.addBrick(new ChangeVariableBrick(1.0f));
		thirdScript.addBrick(ifElseBrickThirdScript);
		thirdScript.addBrick(ifEndBrickThirdScript);
		project.getDefaultScene().addSprite(thirdSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(thirdScript);

		return setVariableBrick;
	}

	public static enum FileTypes {
		IMAGE, SOUND, ROOT, SCREENSHOT
	}

	// Suppress default constructor for noninstantiability
	private UiTestUtils() {
		throw new AssertionError();
	}

	public static ProjectManager getProjectManager() {
		return projectManager;
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
	 * Clicks on the EditText given by editTextId, inserts the integer value and closes the Dialog
	 *
	 * @param value The value you want to put into the EditText
	 */
	public static void insertIntegerIntoEditText(Solo solo, int value) {
		insertValue(solo, value + "");
	}

	/**
	 * Clicks on the EditText given by editTextId, inserts the double value and closes the Dialog
	 *
	 * @param value The value you want to put into the EditText
	 */
	public static void insertDoubleIntoEditText(Solo solo, double value) {
		insertValue(solo, new BigDecimal(value).toPlainString());
	}

	public static void insertStringIntoEditText(Solo solo, String newValue) {
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_string));
		UiTestUtils.clickEnterClose(solo, 0, newValue);
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

	public static void createUserVariableFromDataFragment(Solo solo, String variableName, boolean forAllSprites) {
		assertTrue("FormulaEditorDataFragment not shown: ",
				solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG));

		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add Data Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_data_dialog_title)));
		solo.waitForView(solo.getView(R.id.dialog_formula_editor_data_name_edit_text));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, variableName);

		if (forAllSprites) {
			solo.waitForView(solo.getView(R.id.dialog_formula_editor_data_name_global_variable_radio_button));
			solo.clickOnView(solo.getView(R.id.dialog_formula_editor_data_name_global_variable_radio_button));
		} else {
			solo.waitForView(solo.getView(R.id.dialog_formula_editor_data_name_local_variable_radio_button));
			solo.clickOnView(solo.getView(R.id.dialog_formula_editor_data_name_local_variable_radio_button));
		}
		solo.clickOnButton(solo.getString(R.string.ok));
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

	public static void testBrickWithFormulaEditor(Sprite sprite, Solo solo, int editTextId, String newValue, Brick.BrickField brickField, FormulaBrick theBrick) {

		solo.clickOnView(solo.getView(editTextId));
		solo.sleep(200);
		insertStringIntoEditText(solo, newValue);

		solo.sleep(200);
		String formulaEditorString = ((EditText) solo.getView(R.id.formula_editor_edit_field)).getText().toString();

		assertEquals("Text not updated within FormulaEditor", "\'" + newValue + "\'",
				formulaEditorString.substring(0, formulaEditorString.length() - 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);

		Formula formula = (Formula) theBrick.getFormulaWithBrickField(brickField);
		formulaEditorString = ((TextView) solo.getView(editTextId)).getText().toString();
		try {
			assertEquals("Wrong text in field", newValue, formula.interpretString(sprite));
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}
		assertEquals("Text not updated in the brick list", "\'" + newValue + "\'",
				formulaEditorString.substring(0, formulaEditorString.length() - 1));
	}

	public static void insertValueViaFormulaEditor(Solo solo, int editTextId, double value) {

		solo.clickOnView(solo.getView(editTextId));
		UiTestUtils.insertDoubleIntoEditText(solo, value);

		assertEquals(
				"Text not updated within FormulaEditor",
				value,
				Double.parseDouble(((EditText) solo.getView(R.id.formula_editor_edit_field)).getText().toString()
						.replace(',', '.')));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
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

	public static void clickEnterClose(Solo solo, EditText editText, String value, int buttonIndex) {
		Log.v(TAG, "Solo.Enter clickEnterClose");
		solo.enterText(editText, value);
		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnButton(buttonIndex);
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
		brickCategoryMap.put(R.string.brick_drone_flip, R.string.category_drone);
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

		boolean activityShowedUp = solo.waitForActivity(UserBrickScriptActivity.class, 3000);
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

	public static int[] tapFloatingBrick(Solo solo) {
		return dragFloatingBrick(solo, 0);
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

	public static int[] dragFloatingBrickDownwards(Solo solo, int bricks) {
		return dragFloatingBrick(solo, bricks);
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

	public static void addSpriteToProject(Project project, String name) {
		Sprite sprite = new SingleSprite(name);
		project.getDefaultScene().addSprite(sprite);
	}

	public static List<Brick> createTestProjectWithTwoSprites(String projectName) {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(null, projectName);
		Sprite firstSprite = new SingleSprite("cat");
		Sprite secondSprite = new SingleSprite("second_sprite");

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
		project.getDefaultScene().addSprite(secondSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		StorageHandler.getInstance().saveProject(project);

		// the application version is needed when the project will be uploaded
		// 0.7.3beta is the lowest possible version currently accepted by the web
		Reflection.setPrivateField(project.getXmlHeader(), "applicationVersion", "0.7.3beta");

		return brickList;
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

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
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

	public static List<Brick> createTestProjectNestedLoops() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<>();

		ForeverBrick firstForeverBrick = new ForeverBrick();
		ForeverBrick secondForeverBrick = new ForeverBrick();

		brickList.add(firstForeverBrick);
		brickList.add(new ShowBrick());
		brickList.add(secondForeverBrick);
		brickList.add(new SetMassBrick());
		brickList.add(new LoopEndBrick(secondForeverBrick));
		brickList.add(new LoopEndBrick(firstForeverBrick));

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static List<Brick> createOldTestProjectWithSprites(String oldSpriteProject) {
		Project project = new Project(null, oldSpriteProject);
		project.setCatrobatLanguageVersion(0.99f);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("testSprite1");
		Sprite thirdSprite = new Sprite("third_sprite");

		Script testScript = new StartScript();
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		ArrayList<Brick> brickList = new ArrayList<>();
		Brick brick = new HideBrick();
		brickList.add(brick);
		testScript.addBrick(brick);

		firstSprite.addScript(testScript);

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		StorageHandler.getInstance().saveProject(project);

		return brickList;
	}

	public static List<Brick> createTestProjectWithGroups() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");
		Sprite secondSprite = new GroupSprite("second_sprite");
		Sprite thirdSprite = new GroupItemSprite("third_sprite");
		Sprite fourthSprite = new GroupSprite("fourth_sprite");
		Sprite fifthSprite = new GroupItemSprite("fifth_sprite");
		Sprite sixthSprite = new GroupItemSprite("sixth_sprite");
		Sprite seventhSprite = new GroupItemSprite("seventh_sprite");
		Sprite eightSprite = new SingleSprite("eight_sprite");

		Script testScript = new StartScript();

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		ArrayList<Brick> brickList = new ArrayList<>();
		brickList.add(new HideBrick());
		brickList.add(new ShowBrick());
		brickList.add(new GoNStepsBackBrick(1));
		brickList.add(new ComeToFrontBrick());

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);
		project.addSprite(fifthSprite);
		project.addSprite(sixthSprite);
		project.addSprite(seventhSprite);
		project.addSprite(eightSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		StorageHandler.getInstance().saveProject(project);

		return brickList;
	}

	public static List<Brick> createTestProjectWithUserBrick() {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");
		Sprite secondSprite = new SingleSprite("second_sprite");

		Script testScript = new StartScript();

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		ArrayList<Brick> brickList = new ArrayList<>();
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

		UserBrick firstUserBrick = new UserBrick();
		firstSprite.addUserBrick(firstUserBrick);
		firstUserBrick.getDefinitionBrick().addUIText(TEST_USER_BRICK_NAME);
		firstUserBrick.getDefinitionBrick().addUILocalizedVariable(TEST_USER_BRICK_VARIABLE);
		firstUserBrick.appendBrickToScript(new ChangeXByNBrick(2));

		testScript.addBrick(firstUserBrick);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);
		firstUserBrick.updateUserBrickParametersAndVariables();

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		StorageHandler.getInstance().saveProject(project);

		return brickList;
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
		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		StorageHandler.getInstance().saveProject(project);
	}

	public static List<Brick> createTestProjectIfBricks() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<>();

		IfLogicBeginBrick ifBeginBrick = new IfLogicBeginBrick(0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBeginBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrick, ifBeginBrick);

		brickList.add(ifBeginBrick);
		brickList.add(new ShowBrick());
		brickList.add(ifElseBrick);
		brickList.add(new ComeToFrontBrick());
		brickList.add(ifEndBrick);

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static List<Brick> createTestProjectNestedBricks() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();
		ArrayList<Brick> brickList = new ArrayList<>();

		IfLogicBeginBrick ifBeginBrick = new IfLogicBeginBrick(0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBeginBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrick, ifBeginBrick);
		RepeatBrick repeatBrick = new RepeatBrick(10);
		LoopEndBrick loopEndBrick = new LoopEndBrick();
		repeatBrick.setLoopEndBrick(loopEndBrick);

		brickList.add(ifBeginBrick);
		brickList.add(new ShowBrick());
		brickList.add(ifElseBrick);
		brickList.add(new ComeToFrontBrick());
		brickList.add(ifEndBrick);
		brickList.add(repeatBrick);
		brickList.add(new ShowBrick());
		brickList.add(loopEndBrick);

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static List<Brick> createTestProjectWithUserVariables() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");

		String globalVariableName = "global_var";
		String spriteVariableName = "sprite_var";
		String userBrickVariableName = "userbrick_var";
		FormulaElement variableElementGlobal = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, globalVariableName, null);
		FormulaElement variableElementSprite = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, spriteVariableName, null);
		FormulaElement variableElementUserBrick = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, userBrickVariableName, null);

		UserScriptDefinitionBrick definitionBrick = new UserScriptDefinitionBrick();
		definitionBrick.addUIText("test");
		definitionBrick.addUILocalizedVariable(userBrickVariableName);
		ChangeXByNBrick xBrick = new ChangeXByNBrick(new Formula(variableElementUserBrick));
		definitionBrick.getUserScript().addBrick(xBrick);
		UserBrick userBrick = new UserBrick(definitionBrick);
		ProjectManager.getInstance().setCurrentUserBrick(userBrick);

		DataContainer variableContainer = project.getDefaultScene().getDataContainer();
		UserVariable globalVariable = variableContainer.addProjectUserVariable(globalVariableName);
		UserVariable spriteVariable = variableContainer.addSpriteUserVariableToSprite(firstSprite, spriteVariableName);

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<>();
		brickList.add(new ChangeVariableBrick(new Formula(variableElementGlobal), globalVariable));
		brickList.add(new SetVariableBrick(new Formula(variableElementSprite), spriteVariable));
		brickList.add(userBrick);

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		userBrick.updateUserBrickParametersAndVariables();

		return brickList;
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

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static List<Brick> createTestProjectWithSpecialBricksForBackPack(String projectName) {
		Project project = new Project(null, projectName);
		Sprite firstSprite = new SingleSprite("cat");
		Sprite secondSprite = new SingleSprite("dog");

		Script testScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<>();

		brickList.add(new PlaySoundBrick());
		brickList.add(new PointToBrick(secondSprite));
		brickList.add(new SetLookBrick());
		brickList.add(new AddItemToUserListBrick(0));
		brickList.add(new AddItemToUserListBrick(0));
		brickList.add(new SetVariableBrick(0));
		brickList.add(new ChangeVariableBrick(0));
		brickList.add(new ShowBrick());

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		Script testScriptSecondSprite = new StartScript();
		ArrayList<Brick> brickListSecondSprite = new ArrayList<>();
		brickListSecondSprite.add(new SetXBrick(0));
		brickListSecondSprite.add(new SetYBrick(0));
		for (Brick brick : brickListSecondSprite) {
			testScriptSecondSprite.addBrick(brick);
		}

		firstSprite.addScript(testScript);
		secondSprite.addScript(testScriptSecondSprite);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static void createTestProjectWithTwoScripts() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");

		Script firstScript = new StartScript();

		ArrayList<Brick> firstBrickList = new ArrayList<>();
		firstBrickList.add(new HideBrick());
		firstBrickList.add(new ShowBrick());

		for (Brick brick : firstBrickList) {
			firstScript.addBrick(brick);
		}

		Script secondScript = new WhenScript();
		ArrayList<Brick> secondBrickList = new ArrayList<>();
		secondBrickList.add(new HideBrick());
		secondBrickList.add(new ShowBrick());

		for (Brick brick : secondBrickList) {
			secondScript.addBrick(brick);
		}

		firstSprite.addScript(firstScript);
		firstSprite.addScript(secondScript);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(firstScript);
	}

	public static void createEmptyProject() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
	}

	public static void createEmptyProjectWithoutScript() {
		Project project = new Project(null, PROJECTNAME3);
		Sprite firstSprite = new SingleSprite("cat");

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		StorageHandler.getInstance().saveProject(project);
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

	public static boolean clearProject(String projectname) {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectname);
		if (directory.exists()) {
			return UtilFile.deleteDirectory(directory);
		}
		return false;
	}

	public static Project createProject(String projectName, ArrayList<Sprite> spriteList, Context context) {
		Project project = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		for (Sprite sprite : spriteList) {
			ProjectManager.getInstance().addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static void createProjectForCopySprite(String projectName, Context context) {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(context, projectName);
		Sprite firstSprite = new SingleSprite(context.getString(R.string.default_drone_project_name));
		Sprite secondSprite = new SingleSprite("second_sprite");

		Script firstSpriteScript = new StartScript();

		ArrayList<Brick> brickList = new ArrayList<>();
		brickList.add(new PlaceAtBrick(11, 12));
		brickList.add(new SetXBrick(13));
		brickList.add(new SetYBrick(14));
		brickList.add(new ChangeXByNBrick(15));
		brickList.add(new ChangeXByNBrick(16));
		brickList.add(new IfOnEdgeBounceBrick());
		brickList.add(new MoveNStepsBrick(17));
		brickList.add(new TurnLeftBrick(18));
		brickList.add(new TurnRightBrick(19));
		brickList.add(new PointToBrick(secondSprite));
		brickList.add(new GlideToBrick(21, 22, 23));
		brickList.add(new GoNStepsBackBrick(24));
		brickList.add(new ComeToFrontBrick());

		brickList.add(new SetLookBrick());
		brickList.add(new SetSizeToBrick(11));
		brickList.add(new ChangeSizeByNBrick(12));
		brickList.add(new HideBrick());
		brickList.add(new ShowBrick());
		brickList.add(new SetTransparencyBrick(13));
		brickList.add(new ChangeTransparencyByNBrick(14));
		brickList.add(new SetBrightnessBrick(15));
		brickList.add(new ChangeTransparencyByNBrick(16));
		brickList.add(new ClearGraphicEffectBrick());
		brickList.add(new NextLookBrick());

		brickList.add(new PlaySoundBrick());
		brickList.add(new StopAllSoundsBrick());
		brickList.add(new SetVolumeToBrick(17));
		brickList.add(new ChangeVolumeByNBrick(18));
		brickList.add(new SpeakBrick("Hallo"));

		brickList.add(new WaitBrick(19));
		brickList.add(new BroadcastWaitBrick("firstMessage"));
		brickList.add(new NoteBrick());
		ForeverBrick foreverBrick = new ForeverBrick();
		LoopEndBrick endBrick = new LoopEndBrick(foreverBrick);
		brickList.add(foreverBrick);
		brickList.add(endBrick);

		RepeatBrick repeatBrick = new RepeatBrick(20);
		endBrick = new LoopEndBrick(repeatBrick);
		brickList.add(repeatBrick);
		brickList.add(endBrick);
		brickList.add(new WaitBrick(1));

		// create formula to test copying
		// ( 1 + global ) * local - COMPASS_DIRECTION

		FormulaElement numberElement = new FormulaElement(FormulaElement.ElementType.NUMBER, "1", null);
		FormulaElement bracesElement = new FormulaElement(FormulaElement.ElementType.BRACKET, null, null, null,
				numberElement);

		FormulaElement operatorElementPlus = new FormulaElement(FormulaElement.ElementType.OPERATOR, "PLUS", null);
		FormulaElement operatorElementMult = new FormulaElement(FormulaElement.ElementType.OPERATOR, "MULT", null);
		FormulaElement operatorElementMinus = new FormulaElement(FormulaElement.ElementType.OPERATOR, "MINUS", null);

		DataContainer variableContainer = project.getDefaultScene().getDataContainer();
		variableContainer.addProjectUserVariable("global");
		FormulaElement variableElementGlobal = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, "global",
				null);
		variableContainer.addSpriteUserVariableToSprite(firstSprite, "local");
		FormulaElement variableElemetLocal = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, "local", null);

		FormulaElement sensorElemet = new FormulaElement(FormulaElement.ElementType.SENSOR, "COMPASS_DIRECTION", null);

		operatorElementPlus.setLeftChild(numberElement);
		operatorElementPlus.setRightChild(variableElementGlobal);
		bracesElement.setRightChild(operatorElementPlus);
		operatorElementMult.setLeftChild(bracesElement);
		operatorElementMult.setRightChild(variableElemetLocal);
		operatorElementMinus.setLeftChild(operatorElementMult);
		operatorElementMinus.setRightChild(sensorElemet);

		if (internTokenList.isEmpty()) {
			internTokenList.addAll(operatorElementMinus.getInternTokenList());
		}

		Formula formula = new Formula(operatorElementMinus);

		IfLogicBeginBrick ifBeginBrick = new IfLogicBeginBrick(formula);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBeginBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrick, ifBeginBrick);
		brickList.add(ifBeginBrick);
		brickList.add(new SpeakBrick("Hello, I'm true!"));
		brickList.add(ifElseBrick);
		brickList.add(new SpeakBrick("Hallo, I'm false!"));
		brickList.add(ifEndBrick);

		for (Brick brick : brickList) {
			firstSpriteScript.addBrick(brick);
		}
		firstSprite.addScript(firstSpriteScript);

		BroadcastScript broadcastScript = new BroadcastScript("Hallo");
		BroadcastReceiverBrick brickBroad = new BroadcastReceiverBrick(broadcastScript);
		firstSprite.addScript(broadcastScript);
		brickList.add(brickBroad);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(firstSpriteScript);

		storageHandler.saveProject(project);
	}

	public static List<InternToken> getInternTokenList() {
		return internTokenList;
	}

	public static void clearAllUtilTestProjects() {
		Log.v(TAG, "clearAllUtilTestProjects");
		projectManager.setFileChecksumContainer(new FileChecksumContainer());
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

			if (overflowMenuItemName.equals(solo.getString(R.string.unpack))
					|| overflowMenuItemName.equals(solo.getString(R.string.unpack_keep))) {
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

	private static void openOrCloseMenu(Solo solo) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			solo.clickOnView(solo.getCurrentViews(ActionMenuView.class).get(0));
		}
	}

	public static boolean isActionModeItemPresent(Solo solo, String overflowMenuItemName) {
		openOrCloseMenu(solo);
		boolean found = solo.searchText(overflowMenuItemName, 0, false, true);
		openOrCloseMenu(solo);
		return found;
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

	public static File createTestMediaFile(String filePath, int fileID, Context context) throws IOException {

		File testImage = new File(filePath);

		if (!testImage.exists()) {
			testImage.createNewFile();
		}

		InputStream in = context.getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Constants.BUFFER_8K);

		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
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

	public static boolean comparePixelRgbaArrays(byte[] firstArray, byte[] secondArray) {
		if (firstArray == null || secondArray == null || firstArray.length != secondArray.length) {
			return false;
		}
		for (int i = 0; i < 4; i++) {
			if (Math.abs((firstArray[0] & 0xFF) - (secondArray[0] & 0xFF)) > 10) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the absolute pixel y coordinates of elements from a listview
	 */
	public static ArrayList<Integer> getListItemYPositions(final Solo solo) {
		return getListItemYPositions(solo, 0);
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

	public static void longClickAndDrag(final Solo solo, final float xFrom, final float yFrom, final float xTo,
			final float yTo, final int steps) {
		final Activity activity = solo.getCurrentActivity();
		Handler handler = new Handler(activity.getMainLooper());

		handler.post(new Runnable() {

			public void run() {
				MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_DOWN, xFrom, yFrom, 0);
				activity.dispatchTouchEvent(downEvent);
				downEvent.recycle();
			}
		});

		solo.sleep(ViewConfiguration.getLongPressTimeout() + 200);

		handler.post(new Runnable() {
			public void run() {
				double offsetX = xTo - xFrom;
				offsetX /= steps;
				double offsetY = yTo - yFrom;
				offsetY /= steps;
				for (int i = 0; i <= steps; i++) {
					float x = xFrom + (float) (offsetX * i);
					float y = yFrom + (float) (offsetY * i);
					MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_MOVE, x, y, 0);
					activity.dispatchTouchEvent(moveEvent);

					solo.sleep(20);
					moveEvent.recycle();
				}
			}
		});

		solo.sleep(steps * 20 + 200);

		handler.post(new Runnable() {

			public void run() {
				MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_UP, xTo, yTo, 0);
				activity.dispatchTouchEvent(upEvent);
				upEvent.recycle();
				Log.d(TAG, "longClickAndDrag finished: " + (int) yTo);
			}
		});

		solo.sleep(1000);
	}

	private static class ProjectWithCatrobatLanguageVersion extends Project {
		static final long serialVersionUID = 1L;
		private final float catrobatLanguageVersion;

		public ProjectWithCatrobatLanguageVersion(String name, float catrobatLanguageVersion) {
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

		ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());
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

	public static void getIntoProgramMenuFromMainMenu(Solo solo, int spriteIndex) {
		getIntoProgramMenuFromMainMenu(solo, null, spriteIndex);
	}

	public static void getIntoProgramMenuFromMainMenu(Solo solo, String sceneName, int spriteIndex) {
		getIntoSpritesFromMainMenu(solo, sceneName);
		solo.sleep(200);

		solo.clickInList(spriteIndex);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
	}

	public static void getIntoSoundsFromMainMenu(Solo solo) {
		getIntoSoundsFromMainMenu(solo, null, 0);
	}

	public static void getIntoSoundsFromMainMenu(Solo solo, String sceneName, int spriteIndex) {
		getIntoProgramMenuFromMainMenu(solo, sceneName, spriteIndex);

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
	}

	public static void getIntoLooksFromMainMenu(Solo solo) {
		getIntoLooksFromMainMenu(solo, null, 0, false);
	}

	public static void getIntoLooksFromMainMenu(Solo solo, boolean isBackground) {
		getIntoLooksFromMainMenu(solo, null, 0, isBackground);
	}

	public static void getIntoLooksFromMainMenu(Solo solo, String sceneName, int spriteIndex, boolean isBackground) {
		getIntoProgramMenuFromMainMenu(solo, sceneName, spriteIndex);

		String textToClickOn = "";

		if (isBackground) {
			textToClickOn = solo.getString(R.string.backgrounds);
		} else {
			textToClickOn = solo.getString(R.string.looks);
		}
		solo.clickOnText(textToClickOn);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
	}

	public static void getIntoNfcTagsFromMainMenu(Solo solo) {
		getIntoNfcTagsFromMainMenu(solo, null, 0, false);
	}

	public static void getIntoNfcTagsFromMainMenu(Solo solo, boolean isBackground) {
		getIntoNfcTagsFromMainMenu(solo, null, 0, isBackground);
	}

	public static void getIntoNfcTagsFromMainMenu(Solo solo, String sceneName, int spriteIndex, boolean isBackground) {
		getIntoProgramMenuFromMainMenu(solo, sceneName, spriteIndex);

		String textToClickOn = "";

		textToClickOn = solo.getString(R.string.nfctags);

		solo.clickOnText(textToClickOn);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
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
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
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

	public static boolean longClickOnTextInList(Solo solo, String text) {
		solo.waitForView(solo.getView(android.R.id.content));
		ArrayList<TextView> textViews = solo.getCurrentViews(TextView.class);
		for (int position = 0; position < textViews.size(); position++) {
			TextView view = textViews.get(position);
			if (view.getText().toString().equalsIgnoreCase(text)) {
				solo.clickLongOnView(view);
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
		return getListView(solo, 0);
	}

	public static ListView getListView(Solo solo, int index) {
		ArrayList<ListView> listOfListViews = solo.getCurrentViews(ListView.class);
		assertTrue("no ListView found!", listOfListViews.size() > 0);
		return listOfListViews.get(index);
	}

	public static void waitForFragment(Solo solo, int fragmentRootLayoutId) {
		waitForFragment(solo, fragmentRootLayoutId, 5000);
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

	public static View getViewContainerByString(Solo solo, String text, int containerId) {
		View parent = solo.getView(containerId);
		List<TextView> views = solo.getCurrentViews(TextView.class, parent);
		for (TextView view : views) {

			if (view.getText().equals(text)) {
				return view;
			}
		}
		return null;
	}

	public static View getViewContainerByString(String text, List<TextView> views) {
		for (TextView view : views) {
			if (view.getText().equals(text)) {
				return view;
			}
		}
		return null;
	}

	public static List<TextView> getViewsByParentId(Solo solo, int parentId) {
		View parent = solo.getView(parentId);
		return solo.getCurrentViews(TextView.class, parent);
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
		assertEquals("Text not found!", true, solo.waitForText(text, 0, 3000));
	}

	public static void switchToFragmentInScriptActivity(Solo solo, int fragmentIndex) {
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnButton(fragmentIndex);
		solo.waitForActivity(ScriptActivity.class);
		int id = FRAGMENT_INDEX_LIST.get(fragmentIndex);
		solo.waitForFragmentById(id);
	}

	public static void cancelAllNotifications(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		@SuppressWarnings("unchecked")
		SparseArray<NotificationData> notificationMap = (SparseArray<NotificationData>) Reflection.getPrivateField(
				StatusBarNotificationManager.class, StatusBarNotificationManager.getInstance(), "notificationDataMap");
		notificationMap.clear();
	}

	public static boolean getContextMenuAndGoBackToCheckIfSelected(Solo solo, Activity activity, int buttonId, String buttonText, String listElementName) {
		longClickOnTextInList(solo, listElementName);
		solo.waitForText(buttonText);
		solo.goBack();

		openActionMode(solo, buttonText, buttonId);
		ArrayList<CheckBox> checkBoxList = solo.getCurrentViews(CheckBox.class);
		for (CheckBox checkBox : checkBoxList) {
			if (checkBox.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public static boolean menuButtonVisible(Solo solo, int menuItemId) {
		ArrayList<View> views = solo.getCurrentViews();

		for (View view : views) {
			if (view.getId() == menuItemId) {
				return true;
			}
		}
		return false;
	}

	public static File setUpLookFile(Solo solo, Context instrumentationContext) throws IOException {
		File lookFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/testFile.png",
				R.drawable.default_project_bird_wing_up, solo.getCurrentActivity());

		return lookFile;
	}

	public static void showAndFilloutNewSpriteDialogWithoutClickingOk(Solo solo, String spriteName, Uri uri,
			ActionAfterFinished actionToPerform, SpinnerAdapterWrapper spinner) {
		showAndFilloutNewSpriteDialogWithoutClickingOk(solo, spriteName, uri, actionToPerform, spinner, false);
	}

	public static void showAndFilloutNewSpriteDialogWithoutClickingOk(Solo solo, String spriteName, Uri uri,
			ActionAfterFinished actionToPerform, SpinnerAdapterWrapper spinner, boolean isDroneVideo) {
		if (actionToPerform == null) {
			actionToPerform = ActionAfterFinished.ACTION_FORWARD_TO_NEW_OBJECT;
		}

		FragmentManager fragmentManager = solo.getCurrentActivity().getFragmentManager();

		NewSpriteDialog dialog;

		// create dialog and skip step 1 (choosing an image)
		try {
			Constructor<NewSpriteDialog> constructor = NewSpriteDialog.class.getDeclaredConstructor(
					NewSpriteDialog.DialogWizardStep.class, Uri.class, String.class, ActionAfterFinished.class,
					SpinnerAdapterWrapper.class, boolean.class);
			constructor.setAccessible(true);
			dialog = constructor.newInstance(NewSpriteDialog.DialogWizardStep.STEP_2, uri, spriteName,
					actionToPerform, spinner, isDroneVideo);
		} catch (Exception e) {
			fail("Reflection failure. For more information please use Log.e output");
			Log.e(TAG, "Reflection failure.", e);
			return;
		}

		dialog.show(fragmentManager, NewSpriteDialog.DIALOG_FRAGMENT_TAG);

		EditText addNewSpriteEditText = solo.getEditText(0);

		//check if hint is set
		String hintString = addNewSpriteEditText.getHint().toString();
		assertEquals("Not the proper hint set", true, hintString.startsWith(spriteName));
		assertEquals("There should no text be set", "", addNewSpriteEditText.getText().toString());
		solo.enterText(0, spriteName);
	}

	public static void addNewSprite(Solo solo, String spriteName, File file, ActionAfterFinished actionToPerform) {
		addNewSprite(solo, spriteName, file, actionToPerform, false);
	}

	public static void addNewSprite(Solo solo, String spriteName, File file, ActionAfterFinished actionToPerform,
			boolean isDroneVideo) {
		if (actionToPerform == null) {
			actionToPerform = ActionAfterFinished.ACTION_FORWARD_TO_NEW_OBJECT;
		}

		Uri uri = Uri.fromFile(file);
		showAndFilloutNewSpriteDialogWithoutClickingOk(solo, spriteName, uri, actionToPerform, null, isDroneVideo);

		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForDialogToClose();
		assertEquals("Not in expected object", true, solo.waitForText(spriteName, 0, 500));
		assertEquals("Not in expected fragment", true, solo.waitForText(solo.getString(R.string.scripts), 0, 500));
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class);
		assertEquals("Not in expected fragment", true, solo.waitForText(solo.getString(R.string.scripts), 0, 500));
		assertEquals("Not in expected fragment", true, solo.waitForText(solo.getString(R.string.looks), 0, 500));
		assertEquals("Not in expected fragment", true, solo.waitForText(solo.getString(R.string.sounds), 0, 500));
		solo.goBack();
		hidePocketPaintDialog(solo);
		solo.waitForFragmentById(R.id.fragment_container);
		assertEquals("Not in expected fragment", true,
				solo.waitForText(ProjectManager.getInstance().getCurrentProject().getName(), 0, 500));
	}

	public static void hidePocketPaintDialog(Solo solo) {
		while (solo.searchText(solo.getString(R.string.pocket_paint_not_installed_title))) {
			solo.clickOnButton(solo.getString(R.string.no));
		}
	}

	public static void clickOnExactText(Solo solo, String text) {
		String regularExpressionForExactClick = "^" + java.util.regex.Pattern.quote(text) + "$";
		solo.clickOnText(regularExpressionForExactClick);
	}

	public static boolean searchExactText(Solo solo, String text) {
		return searchExactText(solo, text, false);
	}

	public static boolean searchExactText(Solo solo, String text, boolean onlyVisible) {
		String regularExpressionForExactClick = "^" + java.util.regex.Pattern.quote(text) + "$";
		return solo.searchText(regularExpressionForExactClick, onlyVisible);
	}

	public static void fakeNfcTag(Solo solo, String uid, NdefMessage ndefMessage, Tag tag) {
		Class activityCls = solo.getCurrentActivity().getClass();
		Context packageContext = solo.getCurrentActivity();

		PendingIntent pendingIntent = PendingIntent.getActivity(packageContext, 0,
				new Intent(packageContext, activityCls).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		String intentAction = NfcAdapter.ACTION_TAG_DISCOVERED;
		byte[] tagId = uid.getBytes();

		Intent intent = new Intent();
		intent.setAction(intentAction);
		if (tag != null) {
			intent.putExtra(NfcAdapter.EXTRA_TAG, tag);
		}
		intent.putExtra(NfcAdapter.EXTRA_ID, tagId);
		if (ndefMessage != null) {
			intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, new NdefMessage[] { ndefMessage });
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentAction)) {
				Uri uri = ndefMessage.getRecords()[0].toUri();
				String mime = ndefMessage.getRecords()[0].toMimeType();
				if (uri != null) {
					intent.setData(uri);
				} else {
					intent.setType(mime);
				}
			}
		}

		try {
			pendingIntent.send(packageContext, Activity.RESULT_OK, intent);
		} catch (PendingIntent.CanceledException e) {
			Log.d("fakeNfcTag", e.getMessage());
		}
	}

	public static void enableNfcBricks(Context context) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(SettingsActivity.SETTINGS_SHOW_NFC_BRICKS, true).commit();
	}

	public static void clickOnCheckBox(Solo solo, int checkBoxIndex) {
		solo.clickOnCheckBox(checkBoxIndex);
		solo.sleep(100);
	}

	public static void clickOnListItem(Solo solo, int listIndex) {
		solo.clickInList(listIndex + 1);
		solo.sleep(100);
	}

	public static void clickOnText(Solo solo, String text) {
		solo.waitForText(text);
		solo.clickOnText(text);
		solo.sleep(100);
	}

	// waits for a view to be in a specific state until a timeout occurs
	public static boolean waitForShownState(Solo solo, final View view, final boolean wantedState) {
		boolean result = solo.waitForCondition(
				new Condition() {
					public boolean isSatisfied() {
						return view.isShown() == wantedState;
					}
				}, 800);

		if (!result) {
			fail("Condition is not satisfied before timeout. wantedState: " + Boolean.valueOf(wantedState));
		}
		return wantedState;
	}

	public static boolean checkTempFileFromMediaLibrary(String lookOrSound, String fileName) {
		File folder = new File(lookOrSound);
		File[] filesInFolder = folder.listFiles();

		for (File file : filesInFolder) {
			if (file.isFile()) {
				String filename = file.getName();
				return filename.contains(fileName);
			}
		}
		return false;
	}

	public static Map<String, String> readConfigFile(Context context) {
		try {

			InputStream stream = context.getAssets().open("oauth_config.xml");
			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			String text = new String(buffer);
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

	public static void switchToProgrammeBackground(Solo solo, String programName, String spriteName) {
		clickOnHomeActionBarButton(solo);
		solo.clickOnText(solo.getString(R.string.programs));
		solo.sleep(500);
		UiTestUtils.clickOnTextInList(solo, programName);
		solo.sleep(500);
		UiTestUtils.clickOnTextInList(solo, spriteName);
		solo.sleep(500);
		solo.clickOnText(solo.getString(R.string.background));
		solo.sleep(500);
	}

	public static void selectAllItems(Solo solo) {
		solo.waitForActivity("ScriptActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
	}

	public static void checkAllItemsForBackpack(Solo solo, Activity activity) {
		openBackPackActionModeWhenEmpty(solo);
		selectAllItems(solo);
	}

	public static void backpackAllCheckedItems(Solo solo, String firstTestItemNamePacked, String
			secondTestItemNamePacked) {
		acceptAndCloseActionMode(solo);
		assertTrue("Backpack didn't appear", solo.waitForText(solo.getString(R.string.backpack_title)));
		if (firstTestItemNamePacked != null) {
			assertTrue("Item wasn't backpacked!", solo.waitForText(firstTestItemNamePacked, 0, 300));
		}
		if (secondTestItemNamePacked != null) {
			assertTrue("Item wasn't backpacked!", solo.waitForText(secondTestItemNamePacked, 0, 300));
		}
	}

	public static void backPackAllItems(Solo solo, Activity activity, String firstTestItemNamePacked, String
			secondTestItemNamePacked) {
		checkAllItemsForBackpack(solo, activity);
		backpackAllCheckedItems(solo, firstTestItemNamePacked, secondTestItemNamePacked);
	}

	public static void openBackPackActionMode(Solo solo) {
		openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack);
		solo.waitForDialogToOpen();
		solo.sleep(50);
		solo.waitForText(solo.getString(R.string.packing));
		solo.clickOnText(solo.getString(R.string.packing));
		solo.sleep(500);
	}

	public static void openBackPack(Solo solo) {
		openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack);
		solo.waitForDialogToOpen();
		solo.sleep(100);
		solo.waitForText(solo.getString(R.string.unpack));
		solo.clickOnText(solo.getString(R.string.unpack));
		solo.waitForDialogToClose();
		solo.sleep(500);
	}

	public static void openBackPackFromEmptyAdapter(Solo solo) {
		openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack);
		solo.waitForDialogToOpen();
		solo.sleep(300);
	}

	public static void openBackPackActionModeWhenEmpty(Solo solo) {
		openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack);
		solo.sleep(500);
	}

	public static boolean fileExists(String path) {
		File fileToCheck = new File(path);
		return fileToCheck.exists();
	}

	public static void deleteAllItems(Solo solo) {
		openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		solo.waitForText(selectAll);
		clickOnText(solo, selectAll);
		acceptAndCloseActionMode(solo);
		solo.waitForDialogToOpen();
		if (solo.waitForText(solo.getString(R.string.yes), 1, 800)) {
			solo.clickOnButton(solo.getString(R.string.yes));
			solo.waitForDialogToClose();
		}
		solo.sleep(300);
	}

	public static void clearBackPack(boolean deleteBackPackDirectories) {
		BackPackListManager.getInstance().clearBackPackLooks();
		BackPackListManager.getInstance().clearBackPackSounds();
		BackPackListManager.getInstance().clearBackPackScripts();
		BackPackListManager.getInstance().clearBackPackSprites();
		BackPackListManager.getInstance().clearBackPackScenes();
		BackPackListManager.getInstance().clearBackPackUserBricks();
		if (deleteBackPackDirectories) {
			clearBackPackJson();
			StorageHandler.getInstance().clearBackPackLookDirectory();
			StorageHandler.getInstance().clearBackPackSoundDirectory();
		}
	}

	public static void clearBackPackJson() {
		File backPackFile = new File(buildPath(DEFAULT_ROOT, BACKPACK_DIRECTORY, StorageHandler.BACKPACK_FILENAME));
		backPackFile.delete();
	}

	public static void prepareForSpecialBricksTest(Context instrumentationContext, int imageResource,
			int soundResource, String testLookName, String testSoundName) {
		ProjectManager projectManager = ProjectManager.getInstance();

		List<LookData> lookDataList = projectManager.getCurrentSprite().getLookDataList();
		File imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, projectManager.getCurrentScene().getName(), "catroid_sunglasses.png",
				imageResource, instrumentationContext, UiTestUtils.FileTypes.IMAGE);
		LookData lookData = new LookData();
		lookData.setLookFilename(imageFile.getName());
		lookData.setLookName(testLookName);
		lookDataList.add(lookData);
		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());

		List<SoundInfo> soundInfoList = projectManager.getCurrentSprite().getSoundList();
		File soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, projectManager.getCurrentScene().getName(), "longsound.mp3",
				soundResource, instrumentationContext, UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(testSoundName);
		soundInfoList.add(soundInfo);
		projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());

		DataContainer dataContainer = projectManager.getCurrentProject().getDefaultScene().getDataContainer();
		dataContainer.addProjectUserVariable("global_var");
		dataContainer.addSpriteUserVariable("sprite_var");
		dataContainer.addProjectUserList("global_list");
		dataContainer.addSpriteUserList("sprite_list");
		UserList projectUserList = projectManager.getCurrentProject().getDefaultScene().getDataContainer()
				.getUserList("global_list", null);
		projectUserList.setList(INITIALIZED_LIST_VALUES);
		UserList spriteUserList = projectManager.getCurrentProject().getDefaultScene().getDataContainer()
				.getSpriteListOfLists(projectManager.getCurrentSprite()).get(0);
		spriteUserList.setList(INITIALIZED_LIST_VALUES);
		UserVariable spriteUserVariable = dataContainer.getUserVariable("sprite_var", projectManager.getCurrentSprite());
		UserVariable projectUserVariable = dataContainer.getProjectVariables().get(0);

		List<Brick> bricks = projectManager.getCurrentSprite().getListWithAllBricks();

		((PlaySoundBrick) bricks.get(1)).setSoundInfo(soundInfo);
		((SetLookBrick) bricks.get(3)).setLook(lookData);
		((AddItemToUserListBrick) bricks.get(4)).setUserList(spriteUserList);
		((AddItemToUserListBrick) bricks.get(5)).setUserList(projectUserList);
		((SetVariableBrick) bricks.get(6)).setUserVariable(spriteUserVariable);
		((ChangeVariableBrick) bricks.get(7)).setUserVariable(projectUserVariable);
	}

	public static void getIntoUserBrickOverView(Solo solo) {
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
		solo.waitForActivity(UserBrickScriptActivity.class);
		solo.waitForFragmentByTag(ScriptFragment.TAG);
	}
}
