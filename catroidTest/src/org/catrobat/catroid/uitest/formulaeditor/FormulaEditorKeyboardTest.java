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
package org.catrobat.catroid.uitest.formulaeditor;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;
import java.util.List;

public class FormulaEditorKeyboardTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private Sprite firstSprite;
	private Sprite secondSprite;
	private Brick changeBrick;
	private Brick glideToBrick;

	private static final int CHANGE_SIZE_EDIT_TEXT_RID = R.id.brick_change_size_by_edit_text;

	public FormulaEditorKeyboardTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void createProject(String projectName) throws InterruptedException {
		project = new Project(null, projectName);
		firstSprite = new Sprite("firstSprite");
		secondSprite = new Sprite("secondSprite");
		Script startScript1 = new StartScript();
		Script startScript2 = new StartScript();
		changeBrick = new ChangeSizeByNBrick(0);
		glideToBrick = new GlideToBrick(0, 0, 0);

		firstSprite.addScript(startScript1);
		secondSprite.addScript(startScript2);
		startScript1.addBrick(changeBrick);
		startScript2.addBrick(glideToBrick);
		project.addSprite(firstSprite);
		project.addSprite(secondSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

	public void testNormalKeysKeyboard() {

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
		ArrayList<EditText> textList = solo.getCurrentViews(EditText.class);

		EditText text = textList.get(textList.size() - 1);

		assertEquals("Wrong button clicked", "9", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_8));
		assertEquals("Wrong button clicked", "8", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_7));
		assertEquals("Wrong button clicked", "7", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		assertEquals("Wrong button clicked", "6", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
		assertEquals("Wrong button clicked", "5", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));
		assertEquals("Wrong button clicked", "4", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
		assertEquals("Wrong button clicked", "3", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
		assertEquals("Wrong button clicked", "2", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		assertEquals("Wrong button clicked", "1", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		assertEquals("Wrong button clicked", "+", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		assertEquals("Wrong button clicked", "-", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_mult));
		assertEquals("Wrong button clicked", "×", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_divide));
		assertEquals("Wrong button clicked", "÷", text.getText().toString().substring(0, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_bracket_open));
		assertEquals("Wrong button clicked", getActivity().getString(R.string.formula_editor_bracket_open), text
				.getText().toString().substring(0, text.length() - 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_bracket_close));
		assertEquals("Wrong button clicked", getActivity().getString(R.string.formula_editor_bracket_close), text
				.getText().toString().substring(0, text.length() - 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		assertEquals(
				"Wrong button clicked",
				solo.getString(R.string.formula_editor_function_rand) + "( 0 , 1 )",
				text.getText().toString()
						.substring(0, (solo.getString(R.string.formula_editor_function_rand) + "( 0 , 1 )").length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	}

	public void testLayout() {
		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		List<List<View>> keyboard = new ArrayList<List<View>>();
		LinearLayout keyboardContainer = (LinearLayout) solo.getView(R.id.formula_editor_keyboardview);

		for (int rowIndex = 0; rowIndex < keyboardContainer.getChildCount(); rowIndex++) {
			View rowView = keyboardContainer.getChildAt(rowIndex);
			List<View> row = new ArrayList<View>();

			if (rowView.getClass() == LinearLayout.class) {
				for (int i = 0; i < ((LinearLayout) rowView).getChildCount(); i++) {
					row.add(((LinearLayout) rowView).getChildAt(i));
				}
			}
			keyboard.add(row);
		}

		for (int rowIndex = 0; rowIndex < keyboard.size(); rowIndex++) {
			List<View> row = keyboard.get(rowIndex);
			float currentRowWeightSum = 0;
			for (View key : row) {
				currentRowWeightSum += ((LinearLayout.LayoutParams) key.getLayoutParams()).weight;
			}
			assertEquals("Row " + (rowIndex + 1) + "'s weights don't add up.", 1.0f, currentRowWeightSum);
		}
	}

	public void testObjectFragment() {

		String itemString = "";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		ArrayList<EditText> textList = solo.getCurrentViews(EditText.class);
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_object_x);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_object_y);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_object_transparency);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_object_brightness);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_object_size);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_object_rotation);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_object));
		itemString = solo.getString(R.string.formula_editor_object_layer);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	}

	public void testMathFragment() {

		String itemString = "";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_sin);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_cos);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_tan);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_ln);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_log);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_pi);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_sqrt);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));

		itemString = solo.getString(R.string.formula_editor_function_rand);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_abs);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		itemString = solo.getString(R.string.formula_editor_function_round);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	}

	public void testLogicFragment() {

		String itemString = "";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_equal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_notequal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_lesserthan);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_leserequal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_greaterthan);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_greaterequal);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_and);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_or);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_logic));
		itemString = solo.getString(R.string.formula_editor_logic_not);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	}

	public void testSensorsFragment() {

		String itemString = "";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		ArrayList<EditText> textList = solo.getCurrentViews(EditText.class);
		EditText text = textList.get(textList.size() - 1);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_x_acceleration);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_y_acceleration);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_z_acceleration);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_compass_direction);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_x_inclination);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		itemString = solo.getString(R.string.formula_editor_sensor_y_inclination);
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
	}
}
