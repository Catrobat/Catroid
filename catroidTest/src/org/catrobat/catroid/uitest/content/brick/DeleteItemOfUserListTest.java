/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class DeleteItemOfUserListTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private DeleteItemOfUserListBrick deleteItemOfUserListBrick;

	public DeleteItemOfUserListTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testCreateNewUserListAndDeletion() {
		String userListName = "testList1";
		String secondUserListName = "testList2";

		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_delete_item_from_userlist)));

		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));
		assertTrue("NewUserListDialog not visible", solo.waitForFragmentByTag(NewDataDialog.DIALOG_FRAGMENT_TAG));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, userListName);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText(solo.getString(R.string.brick_delete_item_from_userlist)));
		assertTrue("Created List not set in spinner", solo.searchText(userListName));

		UserList userList = (UserList) Reflection.getPrivateField(UserListBrick.class, deleteItemOfUserListBrick, "userList");
		assertNotNull("UserList is null", userList);

		solo.clickOnView(solo.getView(R.id.delete_item_of_userlist_spinner));
		solo.waitForText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));
		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));

		assertTrue("NewUserListDialog not visible", solo.waitForFragmentByTag(NewDataDialog.DIALOG_FRAGMENT_TAG));

		editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, secondUserListName);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText(solo.getString(R.string.brick_delete_item_from_userlist)));
		assertTrue("Created UserList not set in spinner", solo.searchText(secondUserListName));

		userList = (UserList) Reflection.getPrivateField(UserListBrick.class, deleteItemOfUserListBrick, "userList");
		assertNotNull("UserList is null", userList);
		assertTrue("UserList Name not as expected", userList.getName().equals(secondUserListName));

		solo.clickOnView(solo.getView(R.id.brick_delete_item_of_userlist_edit_text));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		solo.clickLongOnText(secondUserListName);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		assertTrue("UserList not set in spinner after deletion", solo.searchText(userListName));
		Spinner userListSpinner = (Spinner) UiTestUtils.getViewContainerByIds(solo, R.id.delete_item_of_userlist_spinner,
				R.id.formula_editor_brick_space);
		assertEquals("UserList count not as expected in spinner", 2, userListSpinner.getAdapter().getCount());

		solo.goBack();
		assertTrue("ScriptFragment not visible", solo.waitForFragmentByTag(ScriptFragment.TAG));
		assertTrue("UserList not set in spinner after deletion", solo.searchText(userListName));
		userListSpinner = (Spinner) solo.getView(R.id.delete_item_of_userlist_spinner);
		assertEquals("UserList count not as expected in spinner", 2, userListSpinner.getAdapter().getCount());
		userList = (UserList) Reflection.getPrivateField(UserListBrick.class, deleteItemOfUserListBrick, "userList");
		assertNotNull("UserList is null", userList);
		assertTrue("UserList Name not as expected", userList.getName().equals(userListName));
	}

	public void testCreateUserListInFormulaEditor() {
		String userListName = "testList1";

		solo.clickOnView(solo.getView(R.id.brick_delete_item_of_userlist_edit_text));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		UiTestUtils.createUserListFromDataFragment(solo, userListName, true);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		assertTrue("UserList not set in spinner after creation", solo.searchText(userListName));
		Spinner userListSpinner = (Spinner) UiTestUtils.getViewContainerByIds(solo, R.id.delete_item_of_userlist_spinner,
				R.id.formula_editor_brick_space);
		assertEquals("UserList count not as expected in spinner", 2, userListSpinner.getAdapter().getCount());
		assertEquals("UserList not set in spinner after creation", 1, userListSpinner.getSelectedItemPosition());

		solo.goBack();
		assertTrue("ScriptFragment not visible", solo.waitForFragmentByTag(ScriptFragment.TAG));
		assertTrue("UserList not set in spinner after deletion", solo.searchText(userListName));
		userListSpinner = (Spinner) solo.getView(R.id.delete_item_of_userlist_spinner);
		assertEquals("UserList count not as expected in spinner", 2, userListSpinner.getAdapter().getCount());
		UserList userList = (UserList) Reflection.getPrivateField(UserListBrick.class, deleteItemOfUserListBrick, "userList");
		assertNotNull("UserList is null", userList);
		assertTrue("UserList Name not as expected", userList.getName().equals(userListName));
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		deleteItemOfUserListBrick = new DeleteItemOfUserListBrick(10);
		script.addBrick(deleteItemOfUserListBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
