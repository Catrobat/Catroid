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

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaEditorHistory;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class FormulaEditorFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private PlaceAtBrick placeAtBrick;
	private static final int INITIAL_X = 8;
	private static final int INITIAL_Y = 7;

	private static final int X_POS_EDIT_TEXT_RID = R.id.brick_place_at_edit_text_x;
	private static final int Y_POS_EDIT_TEXT_RID = R.id.brick_place_at_edit_text_y;
	private static final int FORMULA_EDITOR_EDIT_TEXT_ID = 0;
	private Sprite sprite;

	public FormulaEditorFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript();
		placeAtBrick = new PlaceAtBrick(INITIAL_X, INITIAL_Y);
		script.addBrick(placeAtBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public void testChangeFormula() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));
		solo.sleep(150);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.sleep(150);
		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID, 1));
		solo.sleep(50);
		assertTrue("Saved changes message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.goBack();
		solo.sleep(100);
		assertEquals("Value not saved!", "1 ", ((TextView) solo.getView(X_POS_EDIT_TEXT_RID)).getText().toString());

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID, 1));
		assertTrue("Fix error message not found!", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));
		solo.sleep(500);
		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID, 1));
		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID, 1));
		assertTrue("Changes saved message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_discarded)));

		solo.goBack();
		solo.goBack();
	}

	public void testOnTheFlyUpdateOfBrickEditText() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));

		assertEquals("Wrong text in FormulaEditor", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		solo.sleep(100);
		assertEquals("Wrong text in X EditText", "1 ", ((TextView) solo.getView(X_POS_EDIT_TEXT_RID, 1)).getText()
				.toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));

		assertEquals("Wrong text in FormulaEditor", "12 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in X EditText", "12 ", ((TextView) solo.getView(X_POS_EDIT_TEXT_RID, 1)).getText()
				.toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(200);
		assertEquals("Wrong text in X EditText", "12 ", ((TextView) solo.getView(X_POS_EDIT_TEXT_RID)).getText()
				.toString());
	}

	public void testUndo() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_mult));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_cos));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_sin));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_tan));

		assertEquals("Wrong text in field", "1 - 2 × cos( sin( tan( 0 ) ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 × cos( sin( 0 ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 × cos( 0 ) ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 × ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		solo.sleep(200);
		assertEquals("Undo did something wrong", "1 - 2 ", ((TextView) solo.getView(X_POS_EDIT_TEXT_RID)).getText()
				.toString());

		solo.goBack();
		solo.goBack();
	}

	public void testUndoRedo() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_8));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_mult));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_7));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));

		solo.sleep(200);

		assertEquals("Wrong text in field", "9 - 8 × 7 + 9 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		for (int i = 0; i < 7; i++) {
			solo.clickOnView(solo.getView(R.id.menu_undo));
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnView(solo.getView(R.id.menu_undo));

		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < 7; i++) {
			solo.clickOnView(solo.getView(R.id.menu_redo));
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", "9 - 8 × 7 + 9 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.waitForText(solo.getString(R.string.formula_editor_discard_changes_dialog_title));
		solo.clickOnButton(solo.getString(R.string.no));
	}

	public void testUndoLimit() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));
		int maxHistoryElements = (Integer) Reflection.getPrivateField(new FormulaEditorHistory(null),
				"MAXIMUM_HISTORY_LENGTH");
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));

		String searchString = "";
		for (int i = 0; i < maxHistoryElements; i++) {
			solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
			searchString += " +";
		}
		solo.sleep(50);

		assertEquals("Wrong text in field", "1" + searchString + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			solo.clickOnView(solo.getView(R.id.menu_undo));
		}

		assertEquals("Wrong text in field", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			solo.clickOnView(solo.getView(R.id.menu_redo));
		}

		assertEquals("Wrong text in field", "1" + searchString + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.waitForText(solo.getString(R.string.formula_editor_discard_changes_dialog_title));
		solo.clickOnButton(solo.getString(R.string.no));
	}

	public void testSimpleInterpretation() {
		String newXFormula = "10 + 12 - 2 × 3 - 4 ";
		int newXValue = 10 + 12 - 2 * 3 - 4;
		String newYFormula = getActivity().getString(R.string.formula_editor_function_rand) + "( "
				+ getActivity().getString(R.string.formula_editor_function_cos) + "( 90 - - 30 ) , 1 ) ";

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));

		solo.sleep(250);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_mult));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(250);
		assertTrue("Save failed toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_cos));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID, 1));
		assertTrue("Changes saved toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		assertEquals("Wrong text in FormulaEditor", newXFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID, 1));
		solo.sleep(250);
		assertEquals("Wrong text in FormulaEditor", newYFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(300);

		//Interpretation test

		Formula formula = placeAtBrick.getFormulaWithBrickField(Brick.BrickField.X_POSITION);
		try {
			assertEquals("Wrong text in field", newXValue, formula.interpretInteger(sprite).intValue());
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}

		formula = placeAtBrick.getFormulaWithBrickField(Brick.BrickField.Y_POSITION);
		try {
			float newYValue = formula.interpretFloat(sprite);
			assertTrue("Wrong text in field", newYValue >= -0.5f && newYValue <= 1f);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}
	}

	public void testRandomInterpretationWithFloatParameters() {

		String newXFormula = "random(9.9,1)";

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_decimal_mark));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(300);

		Formula formula = placeAtBrick.getFormulaWithBrickField(Brick.BrickField.X_POSITION);
		try {
			float value = formula.interpretFloat(sprite);
			assertTrue("random() interpretation of float parameter is wrong: " + newXFormula + " value=" + value,
					1 <= value && value <= 9.9f && (Math.abs(value) - (int) Math.abs(value)) > 0);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}

		String newYFormula = "random(7.0,1)";

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_7));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_decimal_mark));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(300);

		Formula anotherFormula = placeAtBrick.getFormulaWithBrickField(Brick.BrickField.Y_POSITION);

		try {
			float anotherValue = anotherFormula.interpretFloat(sprite);
			assertTrue("random() interpretation of float parameter is wrong: " + newYFormula + " anotherValue="
							+ anotherValue,
					1 <= anotherValue && anotherValue <= 7.0f
							&& (Math.abs(anotherValue) - (int) Math.abs(anotherValue)) > 0);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}
	}

	public void testRandomInterpretationWithIntegerParameters() {

		String newXFormula = "rand(rand(3),1)";

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(300);

		Formula formula = placeAtBrick.getFormulaWithBrickField(Brick.BrickField.X_POSITION);

		try {
			float value = formula.interpretFloat(sprite);
			assertTrue("random() interpretation of integer parameters is wrong: " + newXFormula + " anotherValue="
					+ value, (value == 1 || value == 2 || value == 3));
			assertEquals("random() interpretation of integer parameters is wrong: " + newXFormula + " anotherValue="
					+ value, 0, Math.abs(value) - (int) Math.abs(value), 0);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}

		String newYFormula = "rand(4,1)";

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.sleep(300);

		Formula anotherFormula = placeAtBrick.getFormulaWithBrickField(Brick.BrickField.Y_POSITION);
		try {
			float anotherValue = anotherFormula.interpretFloat(sprite);
			assertTrue("random() interpretation of integer parameters is wrong: " + newYFormula + " anotherValue="
					+ anotherValue, (anotherValue == 1 || anotherValue == 2 || anotherValue == 3 || anotherValue == 4));
			assertEquals("random() interpretation of integer parameters is wrong: " + newYFormula + " anotherValue="
					+ anotherValue, 0, Math.abs(anotherValue) - (int) Math.abs(anotherValue), 0);
		} catch (InterpretationException interpretationException) {
			fail("Wrong text in field.");
		}
	}

	public void testIfLandscapeOrientationIsDeactivated() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));

		int orientation = getActivity().getRequestedOrientation();

		assertTrue("Landscape Orientation isn't deactivated", orientation == Solo.PORTRAIT);
	}

	public void testGoBackAndEditTextSwitches() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID, 1));
		solo.goBack();
		solo.waitForText(solo.getString(R.string.formula_editor_discard_changes_dialog_title));
		solo.clickOnButton(solo.getString(R.string.yes));

		boolean isFound = solo.searchText("6") && solo.searchText("-");
		assertTrue("6 or - is/are not found!", isFound);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID, 1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID, 1));
		isFound = solo.searchText("6") && solo.searchText("+") && solo.searchText("3");
		assertTrue("6 + 3 not found!", isFound);

		solo.clickOnView(solo.getView(Y_POS_EDIT_TEXT_RID, 1));
		isFound = solo.searchText("5") && solo.searchText("-") && solo.searchText("4");
		assertTrue("5 - 4 not found!", isFound);
	}

	public void testRedoAndUndoButtonViewOfKeyboard() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));
		assertTrue("Formula Editor Fragment not shown!",
				solo.waitForText(solo.getString(R.string.formula_editor_title)));

		View undo = solo.getView(R.id.menu_undo);
		View redo = solo.getView(R.id.menu_redo);

		assertTrue("Undo Button not inactive!", !undo.isEnabled());
		assertTrue("Redo Button not inactive!", !redo.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));

		assertTrue("Undo Button not active!", undo.isEnabled());
		assertTrue("Redo Button not inactive!", !redo.isEnabled());

		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.sleep(100);

		assertTrue("Undo Button not active!", undo.isEnabled());
		assertTrue("Redo Button not active!", redo.isEnabled());

		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.sleep(100);

		assertTrue("Undo Button not inactive!", !undo.isEnabled());
		assertTrue("Redo Button not active!", redo.isEnabled());
	}

	public void testDeleteButtonViewOfKeyboard() {

		solo.clickOnView(solo.getView(X_POS_EDIT_TEXT_RID));
		assertTrue("Formula Editor Fragment not shown!",
				solo.waitForText(solo.getString(R.string.formula_editor_title)));

		ImageButton deleteEditField = (ImageButton) solo.getView(R.id.formula_editor_edit_field_clear);
		ImageButton deleteKeyboard = (ImageButton) solo.getView(R.id.formula_editor_keyboard_delete);
		assertTrue("Delete Button not active!", areDeleteButtonsEnabled(deleteEditField, deleteKeyboard));

		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));
		assertTrue("Delete Button not inactive!", !areDeleteButtonsEnabled(deleteEditField, deleteKeyboard));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_function));
		solo.clickOnText(solo.getString(R.string.formula_editor_function_rand));
		solo.waitForText(solo.getString(R.string.formula_editor_title));
		assertTrue("Delete Button not active!", areDeleteButtonsEnabled(deleteEditField, deleteKeyboard));

		setAbsoluteCursorPosition(0);
		assertTrue("Delete Button not inactive!", !areDeleteButtonsEnabled(deleteEditField, deleteKeyboard));

		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.clickOnView(solo.getView(R.id.menu_undo));
		solo.sleep(200);
		assertTrue("Delete Button not active!", areDeleteButtonsEnabled(deleteEditField, deleteKeyboard));

		solo.clickOnView(solo.getView(R.id.menu_redo));
		solo.clickOnView(solo.getView(R.id.menu_redo));
		assertTrue("Delete Button not inactive!", !areDeleteButtonsEnabled(deleteEditField, deleteKeyboard));
	}

	private void setAbsoluteCursorPosition(int position) {
		((FormulaEditorEditText) solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)).setDoNotMoveCursorOnTab(true);
		Reflection.setPrivateField(solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID), "absoluteCursorPosition", position);
		clickOnFormulaEditorEditText();
	}

	//click on edit text
	private void clickOnFormulaEditorEditText() {
		Rect globalVisibleRect = new Rect();
		solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getGlobalVisibleRect(globalVisibleRect);
		solo.clickOnScreen(30, globalVisibleRect.top + 10);
	}

	private boolean areDeleteButtonsEnabled(ImageButton deleteEditField, ImageButton deleteKeyboard) {
		return deleteEditField.isEnabled() && deleteKeyboard.isEnabled();
	}
}
