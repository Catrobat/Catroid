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

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorUserListFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;
import java.util.Locale;

public class FormulaEditorUserListFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final double ADD_VALUE_TO_LIST = 10d;
	private static final String USER_LIST_NAME = "_userList1";
	private Project project;
	private Sprite firstSprite;
	private Sprite secondSprite;
	private Brick changeBrick;
	private Brick glideToBrick;

	private static final int CHANGE_SIZE_EDIT_TEXT_RID = R.id.brick_change_size_by_edit_text;
	private static final int ADD_ITEM_TO_USERLIST_EDIT_TEXT_RID = R.id.brick_add_item_to_userlist_edit_text;
	private static final int GLIDE_TO_EDIT_TEXT_RID = R.id.brick_glide_to_edit_text_x;
	private static final int ACTIONMODE_INDEX = 0;
	private static final String LIST_SYMBOL_LEFT = "[";
	private static final String LIST_SYMBOL_RIGHT = "]";

	public FormulaEditorUserListFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void createProjectAndAddAddItemToListBrick(String projectName) throws InterruptedException {

		project = new Project(null, projectName);

		firstSprite = new Sprite("firstSprite");
		project.addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		ProjectManager.getInstance().getCurrentProject().getUserLists()
				.addProjectUserList(USER_LIST_NAME);

		Double listItemToAdd = Double.valueOf(ADD_VALUE_TO_LIST);

		AddItemToUserListBrick addItemToUserListBrick = new AddItemToUserListBrick(firstSprite, new Formula(listItemToAdd),
				ProjectManager.getInstance().getCurrentProject().getUserLists()
						.getUserList(USER_LIST_NAME, firstSprite)
		);

		Script startScript1 = new StartScript(firstSprite);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(addItemToUserListBrick);
	}

	public void testAddUserListAfterStage() throws InterruptedException {
		String userListName = "userList1";

		solo.goBack();
		createProjectAndAddAddItemToListBrick("testProject");
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.clickOnView(solo.getView(ADD_ITEM_TO_USERLIST_EDIT_TEXT_RID));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		solo.goBack();
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		solo.clickOnView(solo.getView(R.id.button_play));
		solo.sleep(500);
		assertTrue("StageActivity not shown: ", solo.waitForActivity(StageActivity.class.getSimpleName()));

		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		assertTrue("ScriptActivity not shown: ", solo.waitForActivity(ScriptActivity.class.getSimpleName()));

		solo.sleep(500);
		solo.clickOnView(solo.getView(ADD_ITEM_TO_USERLIST_EDIT_TEXT_RID));
		assertTrue("FormulaEditorFragment not shown: ",
				solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("FormulaEditorUserListFragment not shown: ",
				solo.waitForFragmentByTag(FormulaEditorUserListFragment.USERLIST_TAG));

		createUserListFromUserListFragment(userListName, false);
		assertTrue("User List Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));

		ListView listView = getUserListListView();
		assertTrue("UserList not added!", listView.getCount() == 2);
	}

	public void testModifyUserListValuesInStage() throws InterruptedException {

		solo.goBack();
		createProjectAndAddAddItemToListBrick("testProject");
		solo.waitForView(solo.getView(R.id.program_menu_button_scripts));
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(ADD_ITEM_TO_USERLIST_EDIT_TEXT_RID));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		solo.waitForFragmentByTag(FormulaEditorUserListFragment.USERLIST_TAG);

		ListView listView = getUserListListView();

		UserList userList = (UserList) listView.getItemAtPosition(0);
		Double addValueToList = Double.valueOf(ADD_VALUE_TO_LIST);
		assertEquals("Wrong size of User List after stage!", userList.getList().size(), 1);
		assertTrue("Value of UserList not saved after stage1!",
				((Double) userList.getList().get(0)).compareTo(addValueToList) == 0);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(ADD_ITEM_TO_USERLIST_EDIT_TEXT_RID));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		solo.waitForFragmentByTag(FormulaEditorUserListFragment.USERLIST_TAG);

		listView = getUserListListView();

		userList = (UserList) listView.getItemAtPosition(0);
		addValueToList = Double.valueOf(ADD_VALUE_TO_LIST);
		assertTrue("Value of UserList not added after stage2!",
				((Double) userList.getList().get(0)).compareTo(addValueToList) == 0);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnView(solo.getView(ADD_ITEM_TO_USERLIST_EDIT_TEXT_RID));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		solo.waitForFragmentByTag(FormulaEditorUserListFragment.USERLIST_TAG);

		listView = getUserListListView();

		userList = (UserList) listView.getItemAtPosition(0);
		addValueToList = Double.valueOf(ADD_VALUE_TO_LIST);
		assertTrue("Value of UserList not saved after stage3!",
				((Double) userList.getList().get(0)).compareTo(addValueToList) == 0);
	}

	public void testCreateUserList() {

		String itemString = "zzz";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		createUserListFromUserListFragment(itemString, false);

		solo.waitForText(itemString);
		solo.clickOnText(itemString);
		itemString = LIST_SYMBOL_LEFT + itemString + LIST_SYMBOL_RIGHT;
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);
		String editTextString = text.getText().toString();
		assertEquals("Wrong text in EditText", itemString, editTextString.substring(0, itemString.length()));

		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName("zzz");
	}

	public void testDeleteUserListWithLongPress() {

		String itemString = "del";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		createUserListFromUserListFragment(itemString, true);

		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);
		itemString = LIST_SYMBOL_LEFT + itemString + LIST_SYMBOL_RIGHT;
		Log.i("info", "editText: " + text.getText().toString());
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_edit_field_clear));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		itemString = itemString.replace(LIST_SYMBOL_LEFT, "");
		itemString = itemString.replace(LIST_SYMBOL_RIGHT, "");
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		solo.clickLongOnText(itemString);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.goBack();
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		solo.clickLongOnText(itemString);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));

		ListView userListsListView = getUserListListView();

		assertEquals("Wrong number of UserLists deleted", 0, userListsListView.getAdapter().getCount());

		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName("del");
	}

	public void testDeleteUserListWithMultipleChoice() {

		String itemString = "1stdel";
		String itemString2nd = "myList";
		String itemString3rd = "2ndDel";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));

		createUserListFromUserListFragment(itemString, true);
		createUserListFromUserListFragment(itemString2nd, true);
		createUserListFromUserListFragment(itemString3rd, false);

		solo.clickOnView(solo.getView(R.id.formula_editor_userlist_delete));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		solo.clickOnImage(ACTIONMODE_INDEX);
		solo.sleep(250);

		ListView userListListView = getUserListListView();

		assertEquals("Wrong number of UserLists deleted", 1, userListListView.getAdapter().getCount());

		UserList userList = (UserList) userListListView.getAdapter().getItem(0);
		assertEquals(itemString2nd + " deleted, but should not!", userList.getName(), itemString2nd);
		assertFalse(itemString + "not deleted", solo.searchText(itemString, true));
		assertFalse(itemString3rd + "not deleted", solo.searchText(itemString3rd, true));

		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName(itemString);
		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName(itemString2nd);
		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName(itemString3rd);
	}

	public void testKeyCodeBackOnContextMode() {
		String itemString = "myvar1";
		String itemString2nd = "myvar2";
		String itemString3rd = "myvar3";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));

		createUserListFromUserListFragment(itemString, true);
		createUserListFromUserListFragment(itemString2nd, true);
		createUserListFromUserListFragment(itemString3rd, true);

		solo.clickOnView(solo.getView(R.id.formula_editor_userlist_delete));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		solo.goBack();
		assertTrue("KeyCode Back deleted checked item: " + itemString, solo.searchText(itemString, true));
		assertTrue("KeyCode Back deleted checked item: " + itemString2nd, solo.searchText(itemString2nd, true));
		assertTrue("KeyCode Back deleted checked item: " + itemString3rd, solo.searchText(itemString3rd, true));
	}

	public void testScopeOfUserList() {

		String itemString = "local";
		String itemString2nd = "global";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));

		createUserListFromUserListFragment(itemString, false);
		assertTrue(itemString + " not found:", solo.searchText(itemString, true));

		createUserListFromUserListFragment(itemString2nd, true);
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);

		solo.clickOnView(solo.getView(GLIDE_TO_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		assertFalse(itemString + "  should not be found:", solo.searchText(itemString, true));
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName("local");
		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName("global");
	}

	public void testCreateUserListDoubleName() {

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		String itemString = "var1";
		createUserListFromUserListFragment(itemString, true);

		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add UserList Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_userlist_dialog_title)));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_userlist_name_edit_text);
		solo.enterText(editText, itemString);

		assertTrue("Toast not shown when UserListName already exists",
				solo.waitForText(solo.getString(R.string.formula_editor_existing_userlist), 0, 5000));

		solo.waitForText(itemString);

		solo.clearEditText(editText);
		solo.enterText(editText, "var2");

		assertTrue("Inserted list not shown", solo.waitForText("var2"));

		solo.clickOnButton(solo.getString(R.string.ok));

		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName("var1");
		ProjectManager.getInstance().getCurrentProject().getUserLists().deleteUserListByName("var2");
	}

	public void testVisibilityOfMenuItems() {

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_variables)));
		ArrayList<View> currentViews = solo.getCurrentViews();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (View view : currentViews) {
			ids.add(view.getId());
		}

		assertTrue("MenuItem should have been found!", solo.getView(R.id.formula_editor_userlist_delete) != null);

		assertFalse("MenuItem should not be found!", ids.contains(R.id.copy));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.cut));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.insert_below));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.move));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.rename));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.show_details));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.settings));
	}

	public void testEmptyUserListCreation() {

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));
		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add UserList Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_userlist_dialog_title)));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_userlist_name_edit_text);

		solo.enterText(editText, "");
		Button ok = solo.getButton(solo.getString(R.string.ok));
		assertFalse("Ok-Button should not be enabled!", ok.isEnabled());

		solo.enterText(editText, "easy");
		ok = solo.getButton(solo.getString(R.string.ok));
		assertTrue("Ok-Button should be enabled!", ok.isEnabled());

		solo.enterText(editText, "");
		ok = solo.getButton(solo.getString(R.string.ok));
		assertFalse("Ok-Button should not be enabled!", ok.isEnabled());
	}

	public void testUserListListHeadlines() {
		String local = "local";
		String global = "global";
		String globalHeadline = solo.getString(R.string.formula_editor_dialog_for_all_sprites).toUpperCase(
				Locale.getDefault());
		String localHeadline = solo.getString(R.string.formula_editor_dialog_for_this_sprite_only).toUpperCase(
				Locale.getDefault());

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));

		createUserListFromUserListFragment(global, true);

		assertTrue("Global Headline not shown", solo.searchText(globalHeadline, true));

		createUserListFromUserListFragment(local, false);

		assertTrue("Local Headline not shown", solo.searchText(localHeadline, true));

		solo.clickLongOnText(global);
		solo.waitForText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.delete));

		assertFalse("Global Headline still shown", solo.searchText(globalHeadline, true));

		solo.clickLongOnText(local);
		solo.waitForText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.delete));

		assertFalse("Local Headline still shown", solo.searchText(localHeadline, true));

	}

	public void testSelectAllActionModeButton() {
		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_lists));
		assertTrue("UserList Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_lists)));

		createUserListFromUserListFragment("global", true);
		createUserListFromUserListFragment("local", false);

		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		solo.sleep(400);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.formula_editor_userlist_delete,
				getActivity());
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		solo.clickOnText(selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		solo.clickOnCheckBox(1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());
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

	private void createUserListFromUserListFragment(String userListName, boolean forAllSprites) {
		assertTrue("FormulaEditorUserListFragment not shown: ",
				solo.waitForFragmentByTag(FormulaEditorUserListFragment.USERLIST_TAG));

		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add UserList Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_userlist_dialog_title)));
		solo.waitForView(solo.getView(R.id.dialog_formula_editor_userlist_name_edit_text));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_userlist_name_edit_text);
		solo.enterText(editText, userListName);

		if (forAllSprites) {
			solo.waitForView(solo.getView(R.id.dialog_formula_editor_userlist_name_global_variable_radio_button));
			solo.clickOnView(solo.getView(R.id.dialog_formula_editor_userlist_name_global_variable_radio_button));
		} else {
			solo.waitForView(solo.getView(R.id.dialog_formula_editor_userlist_name_local_variable_radio_button));
			solo.clickOnView(solo.getView(R.id.dialog_formula_editor_userlist_name_local_variable_radio_button));
		}
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	private ListView getUserListListView() {
		return solo.getCurrentViews(ListView.class).get(1);
	}

}
