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

import android.bluetooth.BluetoothAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class ArduinoReadPinFunctionTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private Project project;
	private ChangeSizeByNBrick changeSizeByNBrick;
	private static final int CHANGE_SIZE_BY_EDIT_TEXT_RID = R.id.brick_change_size_by_edit_text;

	public ArduinoReadPinFunctionTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	public void testIfProjectWasCreatedCorrectly() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_change_size_by)));

	}

	public void testEmptyFunctionInput() {
		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_arduino_read_pin_value_digital));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(100);
		assertEquals("Error in the formula input", true, solo.waitForView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
	}

	public void testWrongFunctionDigitalPinInput() {
		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_arduino_read_pin_value_digital));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(100);
		assertEquals("Error in the formula input", true, solo.waitForView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
		//assertTrue("Error in the formula input", solo.waitForView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID) , 100, false));
	}

	public void testCorrectFunctionDigitalPinInput() {
		//Pin 01
		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_arduino_read_pin_value_digital));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(100);
		assertEquals("Error in the formula input", true, solo.waitForView(CHANGE_SIZE_BY_EDIT_TEXT_RID));

		//ToDo: for all other possible inputs
	}

	public void testWrongFunctionAnalogPinInput() {
		solo.clickOnView(solo.getView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_arduino_read_pin_value_analog));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(100);
		assertEquals("Error in the formula input", true, solo.waitForView(CHANGE_SIZE_BY_EDIT_TEXT_RID));
	}

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("Change Size By N Brick");
		Script script = new StartScript();
		changeSizeByNBrick = new ChangeSizeByNBrick();

		script.addBrick(changeSizeByNBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
