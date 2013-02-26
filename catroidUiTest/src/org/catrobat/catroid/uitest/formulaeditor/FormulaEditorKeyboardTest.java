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
package org.catrobat.catroid.uitest.formulaeditor;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorKeyboardTest extends android.test.ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Project project;
	private Solo solo;
	private Sprite firstSprite;
	private Sprite secondSprite;
	private Brick changeBrick;
	private Brick glideToBrick;

	private static final int X_POS_EDIT_TEXT_ID = 0;
	private static final int Y_POS_EDIT_TEXT_ID = 1;

	private static final int ACTIONMODE_INDEX = 0;

	private static final String QUOTE = "\"";

	public FormulaEditorKeyboardTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME + this.getClass());
		this.solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	private void createProject(String projectName) throws InterruptedException {

		project = new Project(null, projectName);

		firstSprite = new Sprite("firstSprite");
		secondSprite = new Sprite("secondSprite");
		Script startScript1 = new StartScript(firstSprite);
		Script startScript2 = new StartScript(secondSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 0);
		glideToBrick = new GlideToBrick(secondSprite, 0, 0, 0);

		firstSprite.addScript(startScript1);
		secondSprite.addScript(startScript2);
		startScript1.addBrick(changeBrick);
		startScript2.addBrick(glideToBrick);
		project.addSprite(firstSprite);
		project.addSprite(secondSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

	@Smoke
	public void testNormalKeysKeyboard() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		//		Log.i("info", "text.size()" + textList.size());
		EditText text = textList.get(textList.size() - 1);
		//		Log.i("info", "textstring" + text.getText().toString());
		assertEquals("Wrong button clicked", "9", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_8));
		assertEquals("Wrong button clicked", "8", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_7));
		assertEquals("Wrong button clicked", "7", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		assertEquals("Wrong button clicked", "6", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
		assertEquals("Wrong button clicked", "5", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));
		assertEquals("Wrong button clicked", "4", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
		assertEquals("Wrong button clicked", "3", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
		assertEquals("Wrong button clicked", "2", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		assertEquals("Wrong button clicked", "1", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		assertEquals("Wrong button clicked", "+", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		assertEquals("Wrong button clicked", "-", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_mult));
		assertEquals("Wrong button clicked", "×", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_divide));
		assertEquals("Wrong button clicked", "÷", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_bracket_open));
		assertEquals("Wrong button clicked", getActivity().getString(R.string.formula_editor_bracket_open), text
				.getText().toString().substring(0, text.getText().toString().length() - 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_bracket_close));
		assertEquals("Wrong button clicked", getActivity().getString(R.string.formula_editor_bracket_close), text
				.getText().toString().substring(0, text.getText().toString().length() - 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		assertEquals(
				"Wrong button clicked",
				solo.getString(R.string.formula_editor_function_rand) + "( 0 , 1 )",
				text.getText().toString()
						.substring(0, (solo.getString(R.string.formula_editor_function_rand) + "( 0 , 1 )").length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
	}

	public void testObjectFragment() {

		String itemString = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_x);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_y);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_ghosteffect);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_brightness);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_size);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_rotation);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_layer);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
	}

	public void testMathFragment() {

		String itemString = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_sin);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_cos);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_tan);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_ln);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_log);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_pi);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_sqrt);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));

		itemString = solo.getString(R.string.formula_editor_function_rand);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_abs);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_round);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
	}

	public void testLogicFragment() {

		String itemString = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_equal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_notequal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_lesserthan);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_leserequal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_greaterthan);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_greaterequal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_and);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_or);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_not);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
	}

	public void testSensorsFragment() {

		String itemString = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_x_acceleration);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_y_acceleration);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_z_acceleration);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_z_orientation);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_x_orientation);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_y_orientation);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
	}

	private void clickOnDialogOk(String itemString) {
		clickOnDialogOk(itemString, true);

	}

	public void clickOnDialogOk(String itemString, boolean isGlobalVariable) {
		int iteration = 0;
		while (!solo.searchText(solo.getString(R.string.formula_editor_new_variable), true)) {

			//				fail("ROBOTIUM Error!");

			if (iteration++ > 0) {
				solo.goBack();
				assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable), 0, 4000));
				solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
				assertTrue(solo.waitForText("Variable name ?"));

				EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
				solo.enterText(editText, itemString);

				if (!isGlobalVariable) {
					assertTrue(solo.waitForText(solo
							.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only)));
					solo.clickOnText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only));
				}
			}
			Log.i("info", "(" + iteration + ")OkButton-found: " + solo.searchButton(solo.getString(R.string.ok)));

			solo.clickOnButton(solo.getString(R.string.ok));
			solo.waitForText(solo.getString(R.string.formula_editor_new_variable), 0, 4000);

		}
	}

	public void testCreateUserVariable() {

		String itemString = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));

		assertTrue(solo.waitForText(solo.getString(R.string.ok)));

		itemString = "zzz";
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		//		UiTestUtils.clickEnterClose(solo, editText, itemString, 2);
		solo.enterText(editText, itemString);
		//		solo.clickOnButton(2);
		clickOnDialogOk(itemString);

		solo.clickOnText(itemString);
		itemString = QUOTE + itemString + QUOTE;
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);
		String editTextString = text.getText().toString();
		assertEquals("Wrong text in EditText", itemString, editTextString.substring(0, itemString.length()));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("zzz");
	}

	public void testDeleteUserVariableWithLongPress() {

		String itemString = "del";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);

		solo.enterText(editText, itemString);
		clickOnDialogOk(itemString);

		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);
		itemString = QUOTE + itemString + QUOTE;
		Log.i("info", "editText: " + text.getText().toString());
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		itemString = itemString.replace(QUOTE, "");
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		solo.clickLongOnText(itemString);
		assertTrue(solo.waitForText(solo.getString(R.string.delete)));
		solo.clickOnText(solo.getString(R.string.delete));
		assertFalse(itemString + " not found!", solo.searchText(itemString, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("del");
	}

	public void testDeleteUserVariableWithMultipleChoice() {

		String itemString = "del";
		String itemString2nd = "var";
		String itemString3rd = "2ndDel";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);
		clickOnDialogOk(itemString);

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));
		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString2nd);
		clickOnDialogOk(itemString2nd);

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));
		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString3rd);
		clickOnDialogOk(itemString3rd);

		solo.clickOnMenuItem(solo.getString(R.string.delete), true);
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		solo.clickOnText(itemString);
		solo.clickOnText(itemString3rd);
		solo.clickOnImage(ACTIONMODE_INDEX);

		assertFalse(itemString + " should not be found!", solo.searchButton(itemString, true));
		assertTrue(itemString2nd + " not found!", solo.searchButton(itemString2nd, true));
		assertFalse(itemString3rd + " should not be found!", solo.searchButton(itemString3rd, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(itemString);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(itemString2nd);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(itemString3rd);

	}

	public void testScopeOfUserVariable() {

		String itemString = "local";
		String itemString2nd = "global";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));
		solo.goBack();
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only)));
		solo.clickOnText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);
		clickOnDialogOk(itemString, false);

		assertTrue(itemString + " not found:", solo.searchText(itemString, true));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only)));
		solo.goBack();

		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);

		solo.enterText(editText, itemString2nd);
		clickOnDialogOk(itemString2nd);
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		assertFalse(itemString + "  should not be found:", solo.searchText(itemString, true));
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("local");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("global");

	}

	public void testCreateUserVariableDoubleName() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));
		String itemString = "var1";
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);

		solo.enterText(editText, itemString);
		clickOnDialogOk(itemString);

		assertTrue(solo.waitForText(solo.getString(R.string.formula_editor_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue(solo.waitForText("Variable name ?"));

		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);

		solo.sleep(2000);

		//TODO test Toast !!
		//		assertTrue("Toast not shown when UserVariableName already exists",
		//				solo.searchText(solo.getString(R.string.formula_editor_existing_user_variable)));

		Button positiveButton = solo.getButton(solo.getString(R.string.ok));
		ColorStateList actualPositiveButtonTextColor = positiveButton.getTextColors();
		ColorStateList expectedPositiveButtonTextColor = solo.getCurrentActivity().getResources()
				.getColorStateList(R.color.gray);

		assertEquals("Wrong PositiveButton TextColor", expectedPositiveButtonTextColor, actualPositiveButtonTextColor);

		EditText userVariableNameEditText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		ColorDrawable cd = (ColorDrawable) userVariableNameEditText.getBackground();
		int colorBackground = (Integer) Reflection.getPrivateField(Reflection.getPrivateField(cd, "mState"),
				"mBaseColor");
		int colorMustBe = solo.getCurrentActivity().getResources().getColor(R.color.solid_red);

		assertEquals("Wrong BackgroundColor when UserVariableName already exists", colorMustBe, colorBackground);

		solo.clearEditText(editText);
		solo.enterText(editText, "var2");

		assertTrue(solo.waitForText("var2"));

		userVariableNameEditText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		cd = (ColorDrawable) userVariableNameEditText.getBackground();
		colorBackground = (Integer) Reflection.getPrivateField(Reflection.getPrivateField(cd, "mState"), "mBaseColor");
		colorMustBe = solo.getCurrentActivity().getResources().getColor(R.color.transparent);
		assertEquals("Wrong BackgroundColor", colorMustBe, colorBackground);

		clickOnDialogOk("var2");

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var1");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var2");

	}

}
