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

import android.graphics.drawable.ColorDrawable;
import android.test.suitebuilder.annotation.Smoke;
import android.view.KeyEvent;
import android.view.View;
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
	private static final int SCROLL_DOWN_INDEX = 2;

	private static final String QUOTE = "\"";

	public FormulaEditorKeyboardTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {

		createProject("testProjectFomulaEditorKeyboard");
		this.solo = new Solo(getInstrumentation(), getActivity());
		solo.clickOnButton("Current Project");// TODO insert R.id from mastebranch
		solo.clickOnText("Background");
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.sleep(1000);
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		this.project = null;
		super.tearDown();
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

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
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
		solo.sleep(100);// without sleep it crashes x.x
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_y);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_ghosteffect);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_brightness);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_size);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_rotation);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_look_layer);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
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
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_sin);
		solo.clickOnText(itemString);
		solo.sleep(100);// without sleep it crashes x.x
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_cos);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_tan);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_ln);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		itemString = solo.getString(R.string.formula_editor_function_log);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.scrollDownList(SCROLL_DOWN_INDEX);
		itemString = solo.getString(R.string.formula_editor_function_pi);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.scrollDownList(SCROLL_DOWN_INDEX);
		itemString = solo.getString(R.string.formula_editor_function_sqrt);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		//		itemString = solo.getString(R.string.formula_editor_function_e);
		//		solo.clickOnText(itemString);
		//		solo.sleep(100);
		//		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.scrollDownList(SCROLL_DOWN_INDEX);
		itemString = solo.getString(R.string.formula_editor_function_rand);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.scrollDownList(SCROLL_DOWN_INDEX);
		itemString = solo.getString(R.string.formula_editor_function_abs);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.scrollDownList(SCROLL_DOWN_INDEX);
		itemString = solo.getString(R.string.formula_editor_function_round);
		View view = UiTestUtils.getViewContainerByString(solo, solo.getString(R.string.formula_editor_function_round),
				R.layout.fragment_formula_editor_variablelist);
		solo.clickOnView(view);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
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
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_equal);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_notequal);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_lesserthan);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_leserequal);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_greaterthan);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_greaterequal);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_and);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_or);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_not);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
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
		solo.sleep(100);// without sleep it crashes x.x
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_y_acceleration);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_z_acceleration);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_z_orientation);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_x_orientation);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_y_orientation);
		solo.clickOnText(itemString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
	}

	public void testCreateUserVariable() {

		String itemString = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_Z);
		solo.sendKey(KeyEvent.KEYCODE_Z);
		solo.sendKey(KeyEvent.KEYCODE_Z);
		itemString = "zzz";
		solo.sendKey(KeyEvent.KEYCODE_BACK);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.clickOnText(itemString);
		solo.sleep(100);
		itemString = QUOTE + itemString + QUOTE;
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("zzz");

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

	}

	public void testDeleteUserVariableWithLongPress() {

		String itemString = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_D);
		solo.sendKey(KeyEvent.KEYCODE_E);
		solo.sendKey(KeyEvent.KEYCODE_L);
		itemString = "del";
		solo.sendKey(KeyEvent.KEYCODE_BACK);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.clickOnText(itemString);
		solo.sleep(100);
		itemString = QUOTE + itemString + QUOTE;
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		itemString = "del";
		solo.clickLongOnText(itemString);
		solo.clickOnText(solo.getString(R.string.delete));
		assertFalse(solo.searchText(itemString, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("del");

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
	}

	public void testDeleteUserVariableWithMultipleChoice() {

		String itemString = "";
		String itemString2nd = "";
		String itemString3rd = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_D);
		solo.sendKey(KeyEvent.KEYCODE_E);
		solo.sendKey(KeyEvent.KEYCODE_L);
		itemString = "del";
		solo.sendKey(KeyEvent.KEYCODE_BACK);
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_V);
		solo.sendKey(KeyEvent.KEYCODE_A);
		solo.sendKey(KeyEvent.KEYCODE_R);
		itemString2nd = "var";
		solo.sendKey(KeyEvent.KEYCODE_BACK);
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_D);
		solo.sendKey(KeyEvent.KEYCODE_E);
		solo.sendKey(KeyEvent.KEYCODE_L);
		solo.sendKey(KeyEvent.KEYCODE_2);
		itemString3rd = "del2";
		solo.sendKey(KeyEvent.KEYCODE_BACK);
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.clickOnMenuItem(solo.getString(R.string.delete), true);
		solo.clickOnText(itemString);
		solo.clickOnText(itemString3rd);
		solo.clickOnImage(0);

		assertFalse(solo.searchButton(itemString, true));
		assertTrue(solo.searchButton(itemString2nd, true));
		assertFalse(solo.searchButton(itemString, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("del");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("del2");

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

	}

	public void testScopeOfUserVariable() {

		String itemString = "";
		String itemString2nd = "";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_L);
		solo.sendKey(KeyEvent.KEYCODE_O);
		solo.sendKey(KeyEvent.KEYCODE_C);
		solo.sendKey(KeyEvent.KEYCODE_A);
		solo.sendKey(KeyEvent.KEYCODE_L);
		itemString = "local";
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue(solo.searchText(itemString, true));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_G);
		solo.sendKey(KeyEvent.KEYCODE_L);
		solo.sendKey(KeyEvent.KEYCODE_O);
		solo.sendKey(KeyEvent.KEYCODE_B);
		solo.sendKey(KeyEvent.KEYCODE_A);
		solo.sendKey(KeyEvent.KEYCODE_L);
		itemString2nd = "global";
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue(solo.searchText(itemString2nd, true));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.clickOnText("secondSprite");
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertFalse(solo.searchText(itemString, true));
		assertTrue(solo.searchText(itemString2nd, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("local");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("global");

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

	}

	public void testCreateUserVariableDoubleName() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_V);
		solo.sendKey(KeyEvent.KEYCODE_A);
		solo.sendKey(KeyEvent.KEYCODE_R);
		solo.sendKey(KeyEvent.KEYCODE_1);
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_variable_dialog_hint));
		solo.sendKey(KeyEvent.KEYCODE_V);
		solo.sendKey(KeyEvent.KEYCODE_A);
		solo.sendKey(KeyEvent.KEYCODE_R);
		solo.sendKey(KeyEvent.KEYCODE_1);

		EditText userVariableNameEditText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		ColorDrawable cd = (ColorDrawable) userVariableNameEditText.getBackground();
		int colorBackground = (Integer) Reflection.getPrivateField(Reflection.getPrivateField(cd, "mState"),
				"mBaseColor");
		int colorMustBe = solo.getCurrentActivity().getResources().getColor(R.color.solid_red);
		assertEquals("Wrong BackgroundColor when UserVariableName already exists", colorMustBe, colorBackground);
		assertTrue("Toast not shown when UserVariableName already exists",
				solo.searchText(solo.getString(R.string.formula_editor_existing_user_variable), true));

		solo.sendKey(KeyEvent.KEYCODE_DEL);

		userVariableNameEditText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		cd = (ColorDrawable) userVariableNameEditText.getBackground();
		colorBackground = (Integer) Reflection.getPrivateField(Reflection.getPrivateField(cd, "mState"), "mBaseColor");
		colorMustBe = solo.getCurrentActivity().getResources().getColor(R.color.transparent);
		assertEquals("Wrong BackgroundColor", colorMustBe, colorBackground);

		solo.sendKey(KeyEvent.KEYCODE_2);
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var1");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var2");

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

	}

}
