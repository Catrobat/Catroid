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
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorVariableListFragment;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorUserVariableTest extends android.test.ActivityInstrumentationTestCase2<MainMenuActivity> {

	private static final double SET_USERVARIABLE_TO_BRICK_VALUE = 10d;
	private static final String USER_VARIABLE_NAME_UNDERLINE_PREFIX = "_userVar1";
	private Project project;
	private Solo solo;
	private Sprite firstSprite;
	private Sprite secondSprite;
	private Brick changeBrick;
	private Brick glideToBrick;

	private static final int X_POS_EDIT_TEXT_ID = 0;
	private static final int ACTIONMODE_INDEX = 0;
	private static final String QUOTE = "\"";
	private static final int MAX_ITERATIONS = 10;

	public FormulaEditorUserVariableTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		this.solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		solo = null;
		super.tearDown();
	}

	private void createProject(String projectName) throws InterruptedException {

		project = new Project(null, projectName);

		firstSprite = new Sprite("firstSprite");
		secondSprite = new Sprite("secondSprite");

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		Script startScript1 = new StartScript(firstSprite);
		Script startScript2 = new StartScript(secondSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 0);
		glideToBrick = new GlideToBrick(secondSprite, 0, 0, 0);

		firstSprite.addScript(startScript1);
		secondSprite.addScript(startScript2);
		startScript1.addBrick(changeBrick);
		startScript2.addBrick(glideToBrick);
	}

	private void createProjectSetVariableToBrick(String projectName) throws InterruptedException {

		project = new Project(null, projectName);

		firstSprite = new Sprite("firstSprite");
		project.addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.addProjectUserVariable(USER_VARIABLE_NAME_UNDERLINE_PREFIX, 0d);

		Double setVariableToValue = Double.valueOf(SET_USERVARIABLE_TO_BRICK_VALUE);

		SetVariableBrick setVariableBrick = new SetVariableBrick(firstSprite, new Formula(setVariableToValue),
				ProjectManager.getInstance().getCurrentProject().getUserVariables()
						.getUserVariable(USER_VARIABLE_NAME_UNDERLINE_PREFIX, firstSprite.getName()));

		Script startScript1 = new StartScript(firstSprite);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(setVariableBrick);

	}

	private void finishUserVariableCreationSafeButSlow(String itemString, boolean forAllSprites) {
		int iteration = 0;

		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable), 0, 1000);

		while (!solo.searchText(solo.getString(R.string.formula_editor_make_new_variable), true)) {

			if (iteration++ < MAX_ITERATIONS && iteration > 1) {
				solo.goBack();
				assertTrue("Variable Fragment not shown",
						solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable), 0, 4000));
				solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
				assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));

				EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
				solo.enterText(editText, itemString);

				if (forAllSprites) {
					assertTrue("Variable Dialog not shown",
							solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_for_all_sprites)));
					solo.clickOnText(solo.getString(R.string.formula_editor_variable_dialog_for_all_sprites));
				} else {
					assertTrue("Variable Dialog not shown", solo.waitForText(solo
							.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only)));
					solo.clickOnText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only));
				}
			}
			Log.i("info", "(" + iteration + ")OkButton-found: " + solo.searchButton(solo.getString(R.string.ok)));

			solo.clickOnButton(solo.getString(R.string.ok));
			solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable), 0, 1000);

		}
	}

	public void testAddUserVarialbeAfterStage() throws InterruptedException {
		String userVariableString = "userVar1";

		solo.goBack();
		createProjectSetVariableToBrick("testProject");
		solo.waitForView(solo.getView(R.id.program_menu_button_scripts));
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnEditText(0);
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.waitForFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);
		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(250);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.clickOnEditText(0);
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.waitForFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		assertTrue("Variable Dialog not shown", solo.waitForText(solo.getString(R.string.ok)));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, userVariableString);
		finishUserVariableCreationSafeButSlow(userVariableString, false);
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));

		ListView listView = getVariableListView();
		assertTrue("UserVariable not added!", listView.getCount() == 2);
	}

	public void testModifyUserVariableValuesInStage() throws InterruptedException {

		solo.goBack();
		createProjectSetVariableToBrick("testProject");
		solo.waitForView(solo.getView(R.id.program_menu_button_scripts));
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(250);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnEditText(0);
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.waitForFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		ListView listView = getVariableListView();

		UserVariable userVariable = (UserVariable) listView.getItemAtPosition(0);

		Double setVariableToValue = Double.valueOf(SET_USERVARIABLE_TO_BRICK_VALUE);

		assertTrue("Value of UserVariable not saved after stage!", userVariable.getValue()
				.compareTo(setVariableToValue) == 0);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(250);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnEditText(0);
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.waitForFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		listView = getVariableListView();

		userVariable = (UserVariable) listView.getItemAtPosition(0);

		assertTrue("Value of UserVariable not saved after stage!",
				userVariable.getValue().compareTo(SET_USERVARIABLE_TO_BRICK_VALUE) == 0);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(250);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnEditText(0);
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		solo.waitForFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		listView = getVariableListView();

		userVariable = (UserVariable) listView.getItemAtPosition(0);

		assertTrue("Value of UserVariable not saved after stage!",
				userVariable.getValue().compareTo(SET_USERVARIABLE_TO_BRICK_VALUE) == 0);

	}

	public void testCreateUserVariable() {

		String itemString = "zzz";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));

		assertTrue("Variable Dialog not shown", solo.waitForText(solo.getString(R.string.ok)));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);
		finishUserVariableCreationSafeButSlow(itemString, false);

		solo.waitForText(itemString);
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
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);

		solo.enterText(editText, itemString);
		finishUserVariableCreationSafeButSlow(itemString, true);

		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);
		itemString = QUOTE + itemString + QUOTE;
		Log.i("info", "editText: " + text.getText().toString());
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		itemString = itemString.replace(QUOTE, "");
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickLongOnText(itemString);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.goBack();
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickLongOnText(itemString);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));

		ListView userVariableListView = getVariableListView();

		assertEquals("Wrong number of UserVariables deleted", 0, userVariableListView.getAdapter().getCount());

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("del");
	}

	public void testDeleteUserVariableWithMultipleChoice() {

		String itemString = "1stdel";
		String itemString2nd = "myvar";
		String itemString3rd = "2ndDel";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);
		finishUserVariableCreationSafeButSlow(itemString, true);

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString2nd);
		finishUserVariableCreationSafeButSlow(itemString2nd, true);

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString3rd);
		solo.clickOnView(solo.getView(R.id.dialog_formula_editor_variable_name_radio_button_right));
		finishUserVariableCreationSafeButSlow(itemString3rd, true);

		solo.clickOnMenuItem(solo.getString(R.string.delete), true);
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		solo.clickOnImage(ACTIONMODE_INDEX);
		solo.sleep(250);

		ListView userVariableListView = getVariableListView();

		assertEquals("Wrong number of UserVariables deleted", 1, userVariableListView.getAdapter().getCount());

		UserVariable myVar = (UserVariable) userVariableListView.getAdapter().getItem(0);
		assertEquals(itemString2nd + " deleted, but should not!", myVar.getName(), itemString2nd);
		assertFalse(itemString + "not deleted", solo.searchText(itemString, true));
		assertFalse(itemString3rd + "not deleted", solo.searchText(itemString3rd, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(itemString);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(itemString2nd);
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName(itemString3rd);

	}

	public void testKeyCodeBackOnContextMode() {
		String itemString = "myvar1";
		String itemString2nd = "myvar2";
		String itemString3rd = "myvar3";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);
		finishUserVariableCreationSafeButSlow(itemString, true);

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString2nd);
		finishUserVariableCreationSafeButSlow(itemString2nd, true);

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString3rd);
		solo.clickOnView(solo.getView(R.id.dialog_formula_editor_variable_name_radio_button_right));
		finishUserVariableCreationSafeButSlow(itemString3rd, true);

		solo.clickOnMenuItem(solo.getString(R.string.delete), true);
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		solo.goBack();
		assertTrue("KeyCode Back deleted checked item: " + itemString, solo.searchText(itemString, true));
		assertTrue("KeyCode Back deleted checked item: " + itemString2nd, solo.searchText(itemString2nd, true));
		assertTrue("KeyCode Back deleted checked item: " + itemString3rd, solo.searchText(itemString3rd, true));
	}

	public void testScopeOfUserVariable() {

		String itemString = "local";
		String itemString2nd = "global";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		solo.goBack();
		assertTrue("Add Variable Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only)));
		solo.clickOnText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);
		finishUserVariableCreationSafeButSlow(itemString, false);

		assertTrue(itemString + " not found:", solo.searchText(itemString, true));

		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		assertTrue("Add Variable Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_for_this_sprite_only)));
		solo.goBack();

		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString2nd);
		finishUserVariableCreationSafeButSlow(itemString2nd, true);
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		assertFalse(itemString + "  should not be found:", solo.searchText(itemString, true));
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("local");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("global");

	}

	public void testCreateUserVariableDoubleName() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));
		String itemString = "var1";
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);

		solo.enterText(editText, itemString);
		finishUserVariableCreationSafeButSlow(itemString, true);

		assertTrue("Variable Fragment not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_make_new_variable)));
		solo.clickOnView(solo.getView(R.id.formula_editor_variable_list_bottom_bar));
		assertTrue("Add Variable Dialog not shown", solo.waitForText("Variable name ?"));

		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, itemString);

		assertTrue("Toast not shown when UserVariableName already exists",
				solo.waitForText(solo.getString(R.string.formula_editor_existing_user_variable), 0, 5000));

		solo.waitForText(itemString);

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

		assertTrue("Inserted variable not shown", solo.waitForText("var2"));

		userVariableNameEditText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		cd = (ColorDrawable) userVariableNameEditText.getBackground();
		colorBackground = (Integer) Reflection.getPrivateField(Reflection.getPrivateField(cd, "mState"), "mBaseColor");
		colorMustBe = solo.getCurrentActivity().getResources().getColor(R.color.transparent);
		assertEquals("Wrong BackgroundColor", colorMustBe, colorBackground);

		finishUserVariableCreationSafeButSlow("var2", true);

		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var1");
		ProjectManager.getInstance().getCurrentProject().getUserVariables().deleteUserVariableByName("var2");

	}

	private ListView getVariableListView() {
		return solo.getCurrentListViews().get(2);
	}

}
