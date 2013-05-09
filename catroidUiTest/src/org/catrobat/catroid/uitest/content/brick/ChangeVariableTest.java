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

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.NewVariableDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.jayway.android.robotium.solo.Solo;

public class ChangeVariableTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private static final int MAX_ITERATIONS = 10;
	private Solo solo;
	private Project project;
	private ChangeVariableBrick changeVariableBrick;

	public ChangeVariableTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testCreateNewUserVariableAndDeletion() {
		String userVariableName = "testVariable1";
		String secondUserVariableName = "testVariable2";

		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_change_variable)));

		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));
		assertTrue("NewVariableDialog not visible", solo.waitForFragmentByTag(NewVariableDialog.DIALOG_FRAGMENT_TAG));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, userVariableName);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText(solo.getString(R.string.brick_change_variable)));
		assertTrue("Created Variable not set in spinner", solo.searchText(userVariableName));

		UserVariable userVariable = (UserVariable) Reflection.getPrivateField(changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);

		solo.clickOnView(solo.getView(R.id.change_variable_spinner));
		solo.waitForText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));
		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));

		assertTrue("NewVariableDialog not visible", solo.waitForFragmentByTag(NewVariableDialog.DIALOG_FRAGMENT_TAG));

		editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);
		solo.enterText(editText, secondUserVariableName);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText(solo.getString(R.string.brick_change_variable)));
		assertTrue("Created Variable not set in spinner", solo.searchText(secondUserVariableName));

		userVariable = (UserVariable) Reflection.getPrivateField(changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);
		assertTrue("UserVariable Name not as expected", userVariable.getName().equals(secondUserVariableName));

		solo.clickOnEditText(0);
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_variables)));

		solo.clickLongOnText(secondUserVariableName);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("Variable Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_variables)));

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		assertTrue("Variable not set in spinner after deletion", solo.searchText(userVariableName));
		Spinner userVariableSpinner = (Spinner) UiTestUtils.getViewContainerByIds(solo, R.id.change_variable_spinner,
				R.id.formula_editor_brick_space);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());

		solo.goBack();
		assertTrue("ScriptFragment not visible", solo.waitForFragmentByTag(ScriptFragment.TAG));
		assertTrue("Variable not set in spinner after deletion", solo.searchText(userVariableName));
		userVariableSpinner = (Spinner) solo.getView(R.id.change_variable_spinner);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());
		userVariable = (UserVariable) Reflection.getPrivateField(changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);
		assertTrue("UserVariable Name not as expected", userVariable.getName().equals(userVariableName));
	}

	public void testCreateUserVariableInFormulaEditor() {
		String userVariableName = "testVariable1";

		solo.clickOnEditText(0);
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_variables));
		assertTrue("Variable Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_variables)));

		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add Variable Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_title)));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_variable_name_edit_text);

		solo.enterText(editText, userVariableName);
		finishUserVariableCreationSafeButSlow(userVariableName, true);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		assertTrue("Variable not set in spinner after creation", solo.searchText(userVariableName));
		Spinner userVariableSpinner = (Spinner) UiTestUtils.getViewContainerByIds(solo, R.id.change_variable_spinner,
				R.id.formula_editor_brick_space);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());
		assertEquals("Variable not set in spinner after creation", 1, userVariableSpinner.getSelectedItemPosition());

		solo.goBack();
		assertTrue("ScriptFragment not visible", solo.waitForFragmentByTag(ScriptFragment.TAG));
		assertTrue("Variable not set in spinner after deletion", solo.searchText(userVariableName));
		userVariableSpinner = (Spinner) solo.getView(R.id.change_variable_spinner);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());
		UserVariable userVariable = (UserVariable) Reflection.getPrivateField(changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);
		assertTrue("UserVariable Name not as expected", userVariable.getName().equals(userVariableName));
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		changeVariableBrick = new ChangeVariableBrick(sprite, 10);
		script.addBrick(changeVariableBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	private void finishUserVariableCreationSafeButSlow(String itemString, boolean forAllSprites) {
		int iteration = 0;

		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForText(solo.getString(R.string.formula_editor_variables), 0, 1000);

		while (!solo.searchText(solo.getString(R.string.formula_editor_variables), true)) {

			if (iteration++ < MAX_ITERATIONS && iteration > 1) {
				solo.goBack();
				assertTrue("Variable Fragment not shown",
						solo.waitForText(solo.getString(R.string.formula_editor_variables), 0, 4000));
				solo.clickOnView(solo.getView(R.id.button_add));
				assertTrue("Add Variable Dialog not shown",
						solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_title)));

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
			solo.waitForText(solo.getString(R.string.formula_editor_variables), 0, 1000);
		}
	}

}
