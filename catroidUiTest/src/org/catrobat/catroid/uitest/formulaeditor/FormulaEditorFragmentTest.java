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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaEditorHistory;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ImageButton;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;
	private static final int INITIAL_X = 8;
	private static final int INITIAL_Y = 7;

	private static final int X_POS_EDIT_TEXT_ID = 0;
	private static final int Y_POS_EDIT_TEXT_ID = 1;
	private static final int FORMULA_EDITOR_EDIT_TEXT_ID = 2;
	private Sprite sprite;

	public FormulaEditorFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		solo = null;
		super.tearDown();
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		placeAtBrick = new PlaceAtBrick(sprite, INITIAL_X, INITIAL_Y);
		script.addBrick(placeAtBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public void testChangeFormula() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertTrue("Saved changes message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.goBack();
		solo.sleep(100);
		assertEquals("Value not saved!", "1 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Fix error message not found!", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));
		solo.sleep(500);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Changes saved message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_discarded)));

		solo.goBack();
		solo.goBack();

	}

	public void testOnTheFlyUpdateOfBrickEditText() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));

		assertEquals("Wrong text in FormulaEditor", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		solo.sleep(100);
		assertEquals("Wrong text in X EditText", "1 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));

		assertEquals("Wrong text in FormulaEditor", "12 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in X EditText", "12 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.sleep(50);
		assertEquals("Wrong text in X EditText", "12 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());
	}

	public void testUndo() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_1));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_2));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_mult));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_cos));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_sin));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_tan));

		assertEquals("Wrong text in field", "1 - 2 × cos( sin( tan( 0 ) ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 × cos( sin( 0 ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 × cos( 0 ) ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 × ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.goBack();

		assertEquals("Undo did something wrong", "1 - 2 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testUndoRedo() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

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
			solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));

		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < 7; i++) {
			solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", "9 - 8 × 7 + 9 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testUndoLimit() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		int maxHistoryElements = (Integer) Reflection.getPrivateField(new FormulaEditorHistory(null),
				"MAXIMUM_HISTORY_LENGTH");
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
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
			solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		}

		assertEquals("Wrong text in field", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
		}

		assertEquals("Wrong text in field", "1" + searchString + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testSimpleInterpretation() {
		String newXFormula = "10 + 12 - 2 × 3 - 4 ";
		int newXValue = 10 + 12 - 2 * 3 - 4;
		String newYFormula = getActivity().getString(R.string.formula_editor_function_rand) + "( "
				+ getActivity().getString(R.string.formula_editor_function_cos) + "( 90 - - 30 ) , 1 ) ";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

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
		solo.goBack();
		solo.sleep(250);
		assertTrue("Save failed toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_math));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_function_cos));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Changes saved toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		assertEquals("Wrong text in FormulaEditor", newXFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(250);
		assertEquals("Wrong text in FormulaEditor", newYFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.sleep(300);

		//Interpretation test
		Formula formula = (Formula) Reflection.getPrivateField(placeAtBrick, "xPosition");
		assertEquals("Wrong text in field", newXValue, formula.interpretInteger(sprite));

		formula = (Formula) Reflection.getPrivateField(placeAtBrick, "yPosition");

		float newYValue = formula.interpretFloat(sprite);
		assertTrue("Wrong text in field", newYValue >= -0.5f && newYValue <= 1f);

	}

	public void testRandomInterpretationWithFloatParameters() {

		String newXFormula = "random(9.9,1)";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_decimal_mark));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_9));

		solo.goBack();
		solo.sleep(300);

		Formula formula = (Formula) Reflection.getPrivateField(placeAtBrick, "xPosition");
		float value = formula.interpretFloat(sprite);

		Log.i("info", "value: " + value);

		assertTrue("random() interpretation of float parameter is wrong: " + newXFormula + " value=" + value,
				1 <= value && value <= 9.9f && (Math.abs(value) - (int) Math.abs(value)) > 0);

		String newYFormula = "random(7.0,1)";

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_7));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_decimal_mark));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));

		solo.goBack();
		solo.sleep(300);

		Formula anotherFormula = (Formula) Reflection.getPrivateField(placeAtBrick, "yPosition");
		float anotherValue = anotherFormula.interpretFloat(sprite);

		Log.i("info", "value: " + value);

		assertTrue("random() interpretation of float parameter is wrong: " + newYFormula + " anotherValue="
				+ anotherValue,
				1 <= anotherValue && anotherValue <= 7.0f
						&& (Math.abs(anotherValue) - (int) Math.abs(anotherValue)) > 0);

	}

	public void testRandomInterpretationWithIntegerParameters() {

		String newXFormula = "rand(rand(3),1)";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));

		solo.goBack();
		solo.sleep(300);

		Formula formula = (Formula) Reflection.getPrivateField(placeAtBrick, "xPosition");
		float value = formula.interpretFloat(sprite);

		Log.i("info", "value: " + value);

		assertTrue("random() interpretation of integer parameters is wrong: " + newXFormula + " anotherValue=" + value,
				(value == 1 || value == 2 || value == 3));
		assertEquals("random() interpretation of integer parameters is wrong: " + newXFormula + " anotherValue="
				+ value, 0, Math.abs(value) - (int) Math.abs(value), 0);

		String newYFormula = "rand(4,1)";

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));

		solo.goBack();
		solo.sleep(300);

		Formula anotherFormula = (Formula) Reflection.getPrivateField(placeAtBrick, "yPosition");
		float anotherValue = anotherFormula.interpretFloat(sprite);

		Log.i("info", "anotherValue: " + anotherValue);

		assertTrue("random() interpretation of integer parameters is wrong: " + newYFormula + " anotherValue="
				+ anotherValue, (anotherValue == 1 || anotherValue == 2 || anotherValue == 3 || anotherValue == 4));
		assertEquals("random() interpretation of integer parameters is wrong: " + newYFormula + " anotherValue="
				+ anotherValue, 0, Math.abs(anotherValue) - (int) Math.abs(anotherValue), 0);

	}

	public void testIfLandscapeOrientationIsDeactivated() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		int orientation = getActivity().getRequestedOrientation();

		assertTrue("Landscape Orientation isn't deactivated", orientation == Solo.PORTRAIT);

	}

	public void testGoBackAndEditTextSwitches() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.goBack();

		boolean isFound = solo.searchText("6") && solo.searchText("-");
		assertTrue("6 or - is/are not found!", isFound);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_3));

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_plus));
		solo.goBack();

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_minus));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_4));

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		isFound = solo.searchText("6") && solo.searchText("+") && solo.searchText("3");
		assertTrue("6 + 3 not found!", isFound);

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		isFound = solo.searchText("5") && solo.searchText("-") && solo.searchText("4");
		assertTrue("5 - 4 not found!", isFound);

	}

	public void testRedoAndUndoButtonViewOfKeyboard() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Formula Editor Fragment not shown!",
				solo.waitForText(solo.getString(R.string.formula_editor_title)));

		ImageButton undo = (ImageButton) solo.getView(R.id.formula_editor_keyboard_undo);
		ImageButton redo = (ImageButton) solo.getView(R.id.formula_editor_keyboard_redo);

		assertTrue("Undo Button not inactive!", !undo.isEnabled());
		assertTrue("Redo Button not inactive!", !redo.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));

		assertTrue("Undo Button not active!", undo.isEnabled());
		assertTrue("Redo Button not inactive!", !redo.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));

		assertTrue("Undo Button not active!", undo.isEnabled());
		assertTrue("Redo Button not active!", redo.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));

		assertTrue("Undo Button not inactive!", !undo.isEnabled());
		assertTrue("Redo Button not active!", redo.isEnabled());

	}

	public void testDeleteButtonViewOfKeyboard() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Formula Editor Fragment not shown!",
				solo.waitForText(solo.getString(R.string.formula_editor_title)));

		ImageButton delete = (ImageButton) solo.getView(R.id.formula_editor_keyboard_delete);
		assertTrue("Delete Button not active!", delete.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));
		assertTrue("Delete Button not inactive!", !delete.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_random));
		assertTrue("Delete Button not active!", delete.isEnabled());

		setAbsoluteCursorPosition(0);
		assertTrue("Delete Button not inactive!", !delete.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_undo));
		assertTrue("Delete Button not active!", delete.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_redo));
		assertTrue("Delete Button not inactive!", !delete.isEnabled());
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
}
