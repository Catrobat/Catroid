/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.uitest.content.interaction;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BackPackUserBrickAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.BackPackUserBrickFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final int TIME_TO_WAIT_BACKPACK = 800;

	private static final String DEFAULT_USERBRICK_GROUP_NAME = "Cat";
	private static final String SECOND_USERBRICK_GROUP_NAME = "Dog";
	private static final java.lang.String SECOND_SPRITE_NAME = "second_sprite";
	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private String unpack;
	private String backpack;
	private String backpackAdd;
	private String backpackTitle;

	private String delete;
	private String deleteDialogTitle;

	public UserBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		unpack = solo.getString(R.string.unpack);
		backpack = solo.getString(R.string.backpack);
		backpackAdd = solo.getString(R.string.backpack_add);
		backpackTitle = solo.getString(R.string.backpack_title);

		delete = solo.getString(R.string.delete);
		deleteDialogTitle = solo.getString(R.string.dialog_confirm_delete_backpack_group_title);

		UiTestUtils.clearBackPack(true);

		UiTestUtils.prepareStageForTest();
		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProjectWithUserBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testCopyAndDeleteBricksInUserScriptIncludingDefinitionBrick() {
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.clickOnCheckBox(0);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(300);
		List<Brick> brickList = ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript().getBrickList();
		assertEquals("The selected brick could not be copied!", 2, brickList.size());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.select_all));
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(300);
		assertEquals("Any of the bricks could not be copied or the definition brick was copied by mistake!", 4, brickList.size());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.clickOnCheckBox(0);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForDialogToOpen();
		solo.sleep(100);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		assertEquals("The selected brick could not be deleted!", 3, brickList.size());

		//copy via context menu
		solo.clickOnText(solo.getString(R.string.brick_change_x_by));
		String stringOnCopy = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_copy_brick);
		solo.waitForText(stringOnCopy);
		solo.clickOnText(stringOnCopy);
		solo.sleep(1000);
		UiTestUtils.dragFloatingBrick(solo, 1);
		solo.sleep(1000);
		assertEquals("The selected brick could not be copied!", 4, brickList.size());

		//delete via context menu
		solo.clickOnText(solo.getString(R.string.brick_change_x_by));
		String stringOnDelete = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_delete_brick);
		solo.waitForText(stringOnDelete);
		solo.clickOnText(stringOnDelete);
		solo.waitForDialogToOpen();
		solo.sleep(100);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		assertEquals("The selected brick could not be deleted!", 3, brickList.size());

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.sleep(100);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.select_all));
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForDialogToOpen();
		solo.sleep(100);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		assertEquals("Any of the bricks could not be deleted or the definition brick was deleted by mistake!", 0, brickList.size());
	}

	public void testEditUserBricksInEditorAndScript() {
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.define));
		solo.clickOnText(solo.getString(R.string.add_variable));
		solo.waitForDialogToOpen(2000);
		String proposedVariableName = solo.getString(R.string.new_user_brick_variable) + " 1";
		assertTrue("Variable name was not proposed to user!", solo.searchText(proposedVariableName, 0, true));
		String newVariableName = "testVariable";
		solo.clearEditText(0);
		solo.enterText(0, solo.getString(R.string.new_user_brick_variable) + " 0");
		solo.sleep(200);
		String alreadyUsed = solo.getString(R.string.formula_editor_existing_variable);
		assertTrue("There was no warning that the variable name is already used", solo.searchText(alreadyUsed, 0, true));
		assertFalse("ok button was not disabled", solo.getButton(solo.getString(R.string.ok), true).isEnabled());

		solo.clearEditText(0);
		solo.enterText(0, newVariableName);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
		solo.waitForDialogToClose(2000);
		UserBrick userBrick = ProjectManager.getInstance().getCurrentUserBrick();
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		List<UserVariable> userBrickVariables = dataContainer.getOrCreateVariableListForUserBrick(userBrick);
		assertTrue("User variable not in data container!", userBrickVariables.get(0).getName().equals(
				solo.getString(R.string.new_user_brick_variable) + " 0"));
		assertTrue("User variable not in data container!", userBrickVariables.get(1).getName().equals(newVariableName));
		solo.goBack();
		solo.goBack();

		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.yes));
		solo.waitForDialogToClose();

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
		solo.sleep(200);
		solo.waitForDialogToOpen();
		String proposedUserBrickName = solo.getString(R.string.new_user_brick) + " 1";
		assertTrue("UserBrick name was not proposed to user!", solo.searchText(proposedUserBrickName, 0, true));
		solo.clearEditText(2);
		solo.enterText(2, UiTestUtils.SECOND_TEST_USER_BRICK_NAME);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(500);
		solo.waitForDialogToClose();
		assertTrue("UserBrick name was not proposed to user!", solo.searchText(proposedUserBrickName, 0, true));

		solo.sleep(5000);
		solo.clickOnText(UiTestUtils.SECOND_TEST_USER_BRICK_NAME);
		String stringOnShowAddButton = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_add_to_script);
		solo.waitForText(stringOnShowAddButton);
		solo.clickOnText(stringOnShowAddButton);
		UiTestUtils.dragFloatingBrick(solo, -1);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
		solo.clickInList(2);
		solo.waitForText(stringOnShowAddButton);
		solo.clickOnText(stringOnShowAddButton);
		UiTestUtils.dragFloatingBrick(solo, -1);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
		solo.waitForText(UiTestUtils.TEST_USER_BRICK_NAME);
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
		solo.waitForText(stringOnShowAddButton);
		solo.clickOnText(stringOnShowAddButton);
		UiTestUtils.dragFloatingBrick(solo, -1);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
		solo.clickInList(2);
		solo.waitForText(stringOnShowAddButton);
		solo.clickOnText(stringOnShowAddButton);
		UiTestUtils.dragFloatingBrick(solo, -1);
		solo.sleep(200);
	}

	public void testUseUserBrickVariableInFormulaAndDeleteVariableInFormulaEditor() {
		//use userBrickVariable in formula
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
		UiTestUtils.addNewBrick(solo, R.string.category_data, R.string.brick_set_variable, 0);
		UiTestUtils.dragFloatingBrick(solo, 1);
		solo.clickOnText("1");
		solo.clickOnText(solo.getString(R.string.formula_editor_operator_minus));
		solo.clickOnText("1");
		solo.clickOnText(solo.getString(R.string.ok));
		solo.sleep(600);

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		SetVariableBrick brick = (SetVariableBrick) ProjectManager.getInstance().getCurrentUserBrick()
				.getDefinitionBrick().getUserScript().getBrick(1);
		try {
			double value = brick.getFormulas().get(0).interpretDouble(currentSprite);
			assertTrue("UserVariable of brick doesn't have correct value -1, but:" + value, value == -1);
		} catch (InterpretationException e) {
			fail("Could not interpret formula" + e.getMessage());
		}

		//delete userBrickVariable in VariablesView of formulaeditor
		solo.clickOnText("- 1");
		solo.clickOnText(solo.getString(R.string.formula_editor_data));
		assertFalse("Delete option visible, despite it shouldn't be", solo.waitForText(solo.getString(R.string.delete), 0, 2000, false));
		solo.goBack();
		solo.goBack();

		String defineString = solo.getCurrentActivity().getString(R.string.define);

		assertTrue("'" + defineString + "' should have appeared", solo.waitForText(defineString, 0, 1000));
		solo.clickOnText(defineString);

		int brickElementList = ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScriptDefinitionBrickElements().size();

		assertEquals("There should be a variable and a text element", 2, brickElementList);
	}

	public void testChangeDeleteUserBrickElements() {
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
		String defineString = solo.getCurrentActivity().getString(R.string.define);

		assertTrue("'" + defineString + "' should have appeared", solo.waitForText(defineString, 0, 2000));
		solo.clickOnText(defineString);

		String stringOnUserBrickVar = UiTestUtils.TEST_USER_BRICK_VARIABLE;

		assertTrue("'" + stringOnUserBrickVar + "' should have appeared", solo.waitForText(stringOnUserBrickVar, 1, 2000));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_line_break));
		solo.sleep(200);

		String stringOnUserBrickText = UiTestUtils.TEST_USER_BRICK_NAME;
		solo.clickOnText(stringOnUserBrickText, 1);
		solo.waitForDialogToOpen(2000);
		String newTextName = "newName";
		solo.clearEditText(0);
		solo.enterText(0, newTextName);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
		solo.waitForDialogToClose(2000);

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button));
		solo.sleep(300);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button));
		solo.sleep(300);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button));
		solo.sleep(300);

		assertFalse("the whole data (Variable, Text and LineBreak) should have disappeared",
				solo.waitForText(newTextName, 0, 1000, false, true)
						|| solo.waitForText(UiTestUtils.TEST_USER_BRICK_NAME, 0, 1000, false, true)
						|| solo.waitForText(UiTestUtils.TEST_USER_BRICK_VARIABLE, 0, 1000, false, true));
	}

	public void testEditFormulaWithUserBrickElementsAndChangeValuesViaFormulaEditor() {
		//add 4 userbrick variables, userbrick text and a userbrick linebreak
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);

		String defineString = solo.getCurrentActivity().getString(R.string.define);

		assertTrue("'" + defineString + "' should have appeared", solo.waitForText(defineString, 0, 2000));
		solo.clickOnText(defineString);

		String stringOnUserBrickVar = UiTestUtils.TEST_USER_BRICK_VARIABLE;
		assertTrue("'" + stringOnUserBrickVar + "' should have appeared", solo.waitForText(stringOnUserBrickVar, 1, 2000));

		String newTextName = "newName";
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_text));
		solo.clearEditText(0);
		solo.enterText(0, newTextName);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
		solo.waitForDialogToClose(2000);
		assertTrue("'" + newTextName + "' should have appeared", solo.waitForText(newTextName, 0, 2000));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_line_break));
		solo.sleep(200);

		String newVariableName = "newVariable";
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_variable));
		solo.clearEditText(0);
		solo.enterText(0, newVariableName);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
		solo.waitForDialogToClose(2000);
		assertTrue("'" + newVariableName + "' should have appeared", solo.waitForText(newVariableName, 0, 2000));

		String newVariableName2 = "newVariable2";
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_variable));
		solo.clearEditText(0);
		solo.enterText(0, newVariableName2);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
		solo.waitForDialogToClose(2000);
		assertTrue("'" + newVariableName2 + "' should have appeared", solo.waitForText(newVariableName2, 0, 2000));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.close));
		solo.goBack();

		//click on EditFormula and change all values
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);

		String stringOnEditFormula = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_formula_edit_brick);
		solo.waitForText(stringOnEditFormula);
		solo.clickOnText(stringOnEditFormula);

		solo.clickOnButton("5");
		solo.clickOnEditText(1);
		solo.clickOnButton("3");
		solo.clickOnEditText(2);
		solo.clickOnButton("6");

		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.yes));

		assertTrue("Variable values: '5', '3' and '6' should have appeared", solo.waitForText("5", 0, 1000) || solo.waitForText("3", 0, 1000) || solo.waitForText("6", 0, 1000));
	}

	public void testCopyAndDeleteUserBrickFromScriptWithBothVariants() {
		//copy via action mode
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.scrollDown();
		solo.clickOnCheckBox(7);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.scrollDown();
		solo.sleep(300);
		assertTrue("2 userBricks should exist in the script after copying via action mode, but they don't! brickList"
				+ ".size(): " + ProjectManager.getInstance().getCurrentScript().getBrickList().size(), ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 8);

		//delete via action mode
		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.scrollDown();
		solo.clickOnCheckBox(7);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.scrollDown();
		solo.sleep(500);
		assertTrue("only 1 userbrick should exist in the script after copying via action mode, but that's not the case!", ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 7);

		//copy via context menu
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
		String stringOnCopy = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_copy_brick);
		solo.waitForText(stringOnCopy);
		solo.clickOnText(stringOnCopy);
		solo.sleep(1000);
		UiTestUtils.dragFloatingBrick(solo, -1);
		solo.sleep(2000);
		solo.scrollDown();
		solo.sleep(500);
		assertTrue("2 userbricks should exist in the script after copying via context menu, but they don't!", ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 8);
		solo.sleep(300);

		//delete via context menu
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
		String stringOnDelete = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_delete_brick);
		solo.waitForText(stringOnDelete);
		solo.clickOnText(stringOnDelete);
		solo.waitForDialogToOpen();
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		solo.scrollDown();
		solo.sleep(500);
		assertTrue("only 1 userbrick should exist in the script after copying via context menu, but that's not the case!", ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 7);
		solo.sleep(300);
	}

	public void testMoveUserBrickUpAndDown() throws InterruptedException {
		solo.scrollDown();
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);

		String stringOnMove = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_move_brick);
		solo.waitForText(stringOnMove);
		solo.clickOnText(stringOnMove);

		int[] location = UiTestUtils.dragFloatingBrick(solo, -1);
		assertTrue("was not able to move the brick up", location != null);

		solo.scrollUp();
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
		solo.waitForText(stringOnMove);
		solo.clickOnText(stringOnMove);
		location = UiTestUtils.dragFloatingBrick(solo, 3);
		assertTrue("was not able to move the brick down", location != null);
		solo.sleep(300);
	}

	//	delete a userbrick, go back to scripts and check if the deletion was updated
	public void testDeleteUserBrickAndCheckIfScriptActivityUpdates() throws InterruptedException {
		UiTestUtils.getIntoUserBrickOverView(solo);

		UiTestUtils.deleteFirstUserBrick(solo, UiTestUtils.TEST_USER_BRICK_NAME);
		solo.sleep(500);
		solo.goBack();
		solo.goBack();
		solo.sleep(200);
		Script currentScript = UiTestUtils.getProjectManager().getCurrentScript();
		int indexOfUserBrickInScript = currentScript.containsBrickOfTypeReturnsFirstIndex(UserBrick.class);
		assertTrue("current script should not contain a User Brick after we tried to delete one.",
				indexOfUserBrickInScript == -1);
	}

	public void testUserBrickEditInstanceScriptChangesOtherInstanceScript() throws InterruptedException {
		UiTestUtils.addNewBrick(solo, R.string.category_user_bricks, UiTestUtils.TEST_USER_BRICK_NAME, 0);

		solo.sleep(1000);
		// click on position x brick-heights above/below the place where the brick currently is
		int[] location = UiTestUtils.dragFloatingBrick(solo, -1);
		assertTrue("was not able to find the brick we just added: first user brick", location != null);
		solo.sleep(4000);

		Script currentScript = UiTestUtils.getProjectManager().getCurrentScript();
		int indexOfUserBrickInScript = currentScript.containsBrickOfTypeReturnsFirstIndex(UserBrick.class);
		assertTrue("current script should contain a User Brick after we tried to add one.",
				indexOfUserBrickInScript != -1);

		UserBrick userBrick = (UserBrick) currentScript.getBrick(indexOfUserBrickInScript);
		assertTrue("we should be able to cast the brick we found to a User Brick.", userBrick != null);

		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);

		// add a new brick to the internal script of the user brick
		UiTestUtils.addNewBrick(solo, R.string.brick_change_y_by);

		// place it
		location = UiTestUtils.dragFloatingBrick(solo, 1);
		assertTrue("was not able to find the brick we just added: brick inside user brick", location != null);
		solo.sleep(1000);

		// go back to normal script activity
		solo.goBack();
		solo.sleep(2000);

		UiTestUtils.addNewBrick(solo, R.string.category_user_bricks, UiTestUtils.TEST_USER_BRICK_NAME, 0);

		location = UiTestUtils.dragFloatingBrick(solo, 1);
		assertTrue("was not able to find the brick we just added: second user brick", location != null);

		solo.sleep(2000);

		// click on the location the brick was just dragged to.
		solo.clickLongOnScreen(location[0], location[1], 10);

		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, false, solo);

		String brickAddedToUserBrickScriptName = solo.getCurrentActivity().getString(R.string.brick_change_y_by);
		assertTrue("was not able to find the script we added to the other instance",
				solo.searchText(brickAddedToUserBrickScriptName));
	}

	public void testBackPackUserBricksContextMenu() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackUserBricksDoubleContextMenu() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		solo.goBack();
		backPackFirstUserBrickWithContextMenu(SECOND_USERBRICK_GROUP_NAME);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(SECOND_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackUserBricksSimpleUnpackingContextMenu() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		int numberOfUserBricksInBrickList = getCurrentUserBrickCount();

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));

		unpackUserBrickGroup(DEFAULT_USERBRICK_GROUP_NAME, unpack);
		assertEquals("Brick count in current sprite not correct", numberOfUserBricksInBrickList + 1,
				getCurrentUserBrickCount());

		UiTestUtils.openBackPack(solo);
		assertTrue("Userbrick wasn't kept in backpack!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0,
				TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackAndUnPackFromDifferentProgrammes() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));

		UiTestUtils.switchToProgrammeBackground(solo, UiTestUtils.PROJECTNAME1, "cat");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.getIntoUserBrickOverView(solo);

		int numberOfBricksInBrickList = getCurrentUserBrickCount();

		UiTestUtils.openBackPackFromEmptyAdapter(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(DEFAULT_USERBRICK_GROUP_NAME, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertEquals("UserBrick count in current sprite not correct", numberOfBricksInBrickList + 1,
				getCurrentUserBrickCount());
	}

	public void testBackPackAndUnPackFromDifferentSprites() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.clickOnText(SECOND_SPRITE_NAME);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.getIntoUserBrickOverView(solo);

		int numberOfBricksInBrickList = getCurrentUserBrickCount();

		UiTestUtils.openBackPackFromEmptyAdapter(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(DEFAULT_USERBRICK_GROUP_NAME, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 1,
				getCurrentUserBrickCount());
	}

	public void testBackPackActionModeCheckingAndTitle() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		addOneUserBrickInPrototypeView(UiTestUtils.SECOND_TEST_USER_BRICK_NAME);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String expectedUserBricksFirstUserBrick = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_backpack,
				1, 1);

		String expectedBricksSecondUserBrick = getActivity().getResources().getQuantityString(R.plurals
						.number_of_bricks_to_backpack,
				2, 2);

		assertFalse("Bricks should not be displayed in title", solo.waitForText(expectedUserBricksFirstUserBrick, 3, 300, false, true));

		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);

		String expectedTitle = expectedUserBricksFirstUserBrick;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(true, false);
		assertTrue("Title not as expected:" + expectedTitle, solo.waitForText(expectedTitle, 0,
				timeToWaitForTitle,
				false, true));

		expectedTitle = expectedBricksSecondUserBrick;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(true, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = expectedUserBricksFirstUserBrick;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = backpack;

		solo.clickOnCheckBox(2);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackActionModeIfNothingSelected() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		int expectedNumberOfBricks = getCurrentUserBrickCount();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(300);
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT_BACKPACK, false, true));
		checkIfNumberOfBricksIsEqual(expectedNumberOfBricks);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT_BACKPACK, false, true));
		checkIfNumberOfBricksIsEqual(expectedNumberOfBricks);
	}

	public void testBackPackActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		addOneUserBrickInPrototypeView(UiTestUtils.SECOND_TEST_USER_BRICK_NAME);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnCheckBox(2);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT_BACKPACK, false, true));
		assertFalse("Backpack was opened, but shouldn't be!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK, false, true));
	}

	public void testBackPackSelectAll() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		addOneUserBrickInPrototypeView(UiTestUtils.SECOND_TEST_USER_BRICK_NAME);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForActivity(ScriptActivity.class);

		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		for (Brick brick : ProjectManager.getInstance().getCurrentSprite().getUserBrickList()) {
			assertTrue("CheckBox is not checked!", brick.getCheckBox().isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		fillNewUserBrickGroupDialog(DEFAULT_USERBRICK_GROUP_NAME);
		assertTrue("BackPack title didn't show up", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK, false, true));
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK, false, true));
	}

	public void testBackPackUserBricksDeleteContextMenu() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);

		BackPackUserBrickAdapter adapter = getBackPackUserBrickAdapter();
		int oldCount = adapter.getCount();

		clickOnContextMenuItem(DEFAULT_USERBRICK_GROUP_NAME, delete);
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		int newCount = adapter.getCount();
		solo.sleep(500);

		assertEquals("Not all userbricks were backpacked", 1, oldCount);
		assertEquals("Userbrick group wasn't deleted in backpack", 0, newCount);
		assertEquals("Count of the backpack userbrickGroupList is not correct", newCount, BackPackListManager
				.getInstance().getBackPackedUserBrickGroups().size());
	}

	public void testBackPackUserBricksDeleteActionMode() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);

		BackPackUserBrickAdapter adapter = getBackPackUserBrickAdapter();
		int oldCount = adapter.getCount();

		UiTestUtils.deleteAllItems(solo);

		int newCount = adapter.getCount();
		solo.sleep(500);
		assertTrue("No backpack is emtpy text appeared", solo.searchText(backpack));
		assertTrue("No backpack is emtpy text appeared", solo.searchText(solo.getString(R.string.is_empty)));

		assertEquals("Not all userbricks were backpacked", 1, oldCount);
		assertEquals("Userbrick Groups were not deleted in backpack", 0, newCount);
		assertEquals("Count of the backpack userbrickGroupList is not correct", newCount, BackPackListManager
				.getInstance().getBackPackedUserBrickGroups().size());
	}

	public void testBackPackUserBricksActionModeDifferentProgrammes() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		addOneUserBrickInPrototypeView(UiTestUtils.SECOND_TEST_USER_BRICK_NAME);

		backPackAllUserBricks(DEFAULT_USERBRICK_GROUP_NAME);
		UiTestUtils.switchToProgrammeBackground(solo, UiTestUtils.PROJECTNAME1, "cat");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.getIntoUserBrickOverView(solo);
		int numberOfBricksInBrickList = getCurrentUserBrickCount();

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForActivity(ScriptActivity.class);
		solo.sleep(1000);
		assertEquals("Userbrick count in current sprite not correct", numberOfBricksInBrickList + 2, getCurrentUserBrickCount());
		UiTestUtils.deleteAllItems(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertEquals("Brick count in current sprite not correct", 0, getCurrentUserBrickCount());

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Backpack items were cleared!", solo.waitForText(backpackTitle, 1, 1000));
	}

	public void testBackPackDeleteActionModeCheckingAndTitle() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		solo.goBack();
		backPackFirstUserBrickWithContextMenu(SECOND_USERBRICK_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String expectedTitleOneUserBrickGroup = delete + " 1 " + solo.getString(R.string.userbrick_group);
		String expectedTitleTwoUserBrickGroups = delete + " 2 " + solo.getString(R.string.userbrick_groups);

		assertFalse("Userbrick Group should not be displayed in title", solo.waitForText(solo.getString(R.string.userbrick_group), 3, 300, false,
				true));

		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);

		String expectedTitle = expectedTitleOneUserBrickGroup;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(true, false);
		assertTrue("Title not as expected:" + expectedTitle, solo.waitForText(expectedTitle, 0,
				timeToWaitForTitle,
				false, true));

		expectedTitle = expectedTitleTwoUserBrickGroups;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = expectedTitleOneUserBrickGroup;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackDeleteActionModeIfNothingSelected() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		solo.goBack();
		backPackFirstUserBrickWithContextMenu(SECOND_USERBRICK_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		int expectedNumberOfUserBrickGroups = BackPackListManager.getInstance().getBackPackedUserBrickGroups().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT_BACKPACK, false, true));
		checkIfNumberOfBricksIsEqualInBackPack(expectedNumberOfUserBrickGroups);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT_BACKPACK, false, true));
		checkIfNumberOfBricksIsEqualInBackPack(expectedNumberOfUserBrickGroups);
	}

	public void testBackPackDeleteActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		solo.goBack();
		backPackFirstUserBrickWithContextMenu(SECOND_USERBRICK_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		int expectedNumberOfUserBrickGroups = BackPackListManager.getInstance().getBackPackedUserBrickGroups().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnCheckBox(1);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT_BACKPACK, false, true));
		checkIfNumberOfBricksIsEqualInBackPack(expectedNumberOfUserBrickGroups);
	}

	public void testBackPackDeleteSelectAll() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		solo.goBack();
		backPackFirstUserBrickWithContextMenu(SECOND_USERBRICK_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		CheckBox firstCheckBox = getUserBrickCheckBox(1, R.id.fragment_group_backpack_item_checkbox);
		CheckBox secondCheckBox = getUserBrickCheckBox(2, R.id.fragment_group_backpack_item_checkbox);
		assertNotNull("Checkbox is null!", firstCheckBox);
		assertNotNull("Checkbox is null!", secondCheckBox);
		assertTrue("CheckBox is not Checked!", firstCheckBox.isChecked());
		assertTrue("CheckBox is not Checked!", secondCheckBox.isChecked());

		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertFalse("Userbrick group wasn't deleted!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0,
				TIME_TO_WAIT_BACKPACK, false, true));
		assertFalse("Userbrick group wasn't deleted!", solo.waitForText(SECOND_USERBRICK_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK, false, true));
		assertTrue("No empty bg found!", solo.waitForText(solo.getString(R.string.is_empty), 0, TIME_TO_WAIT_BACKPACK, false, true));
	}

	public void testBackPackShowAndHideDetails() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		int timeToWait = 300;

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		hideDetails();

		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.openBackPack(solo);
		solo.waitForActivity(BackPackActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testBackPackUserBrickGroupWithSameName() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		assertTrue("Userbrick wasn't backpacked!", solo.waitForText(DEFAULT_USERBRICK_GROUP_NAME, 0,
				TIME_TO_WAIT_BACKPACK, false, true));
		solo.goBack();

		solo.waitForActivity(ScriptActivity.class);
		solo.waitForFragmentByTag(ScriptFragment.TAG);
		backPackFirstUserBrickWithContextMenu(DEFAULT_USERBRICK_GROUP_NAME);
		assertTrue("No userbrick group already existing warning appeared!",
				solo.waitForText(solo.getString(R.string.script_group_name_given), 0, TIME_TO_WAIT_BACKPACK, false, true));
	}

	public void testEmptyActionModeDialogs() {
		UiTestUtils.getIntoUserBrickOverView(solo);
		UiTestUtils.deleteAllItems(solo);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to backpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_backpack_and_unpack)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
	}

	public void testEmptyActionModeDialogsInBackPack() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackAllUserBricks(DEFAULT_USERBRICK_GROUP_NAME);
		UiTestUtils.deleteAllItems(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to unpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_unpack)));
	}

	public void testOpenBackPackWhenUserBrickListEmptyButSomethingInBackPack() {
		UiTestUtils.getIntoUserBrickOverView(solo);

		backPackAllUserBricks(DEFAULT_USERBRICK_GROUP_NAME);

		solo.goBack();
		UiTestUtils.deleteAllItems(solo);

		UiTestUtils.openActionMode(solo, backpack, R.id.backpack);
		solo.waitForActivity(BackPackActivity.class);
		assertTrue("Backpack wasn't opened", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK, false, true));
	}

	private BackPackUserBrickFragment getBackPackUserBrickFragment() {
		BackPackActivity activity = (BackPackActivity) solo.getCurrentActivity();
		return (BackPackUserBrickFragment) activity.getFragment(BackPackActivity.FRAGMENT_BACKPACK_USERBRICKS);
	}

	private BackPackUserBrickAdapter getBackPackUserBrickAdapter() {
		return (BackPackUserBrickAdapter) getBackPackUserBrickFragment().getListAdapter();
	}

	private void backPackFirstUserBrickWithContextMenu(String userBrickGroupName) {
		solo.sleep(200);
		solo.clickInList(0);
		solo.waitForDialogToOpen();
		solo.waitForText(backpackAdd);
		solo.clickOnText(backpackAdd);

		fillNewUserBrickGroupDialog(userBrickGroupName);
	}

	private void backPackAllUserBricks(String defaultUserBrickGroupName) {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForActivity(ScriptActivity.class);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		UiTestUtils.acceptAndCloseActionMode(solo);
		fillNewUserBrickGroupDialog(defaultUserBrickGroupName);
	}

	private void fillNewUserBrickGroupDialog(String userBrickGroupName) {
		solo.waitForDialogToOpen();
		EditText userBrickGroupEditText = (EditText) solo.getView(R.id.new_group_dialog_group_name);
		solo.clearEditText(userBrickGroupEditText);
		solo.enterText(userBrickGroupEditText, userBrickGroupName);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.ok));

		solo.waitForDialogToClose();
	}

	private void clickOnContextMenuItem(String userBrickGroupName, String menuItemName) {
		solo.clickLongOnText(userBrickGroupName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	private void unpackUserBrickGroup(String userBrickGroupName, String menuItemName) {
		clickOnContextMenuItem(userBrickGroupName, menuItemName);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.waitForActivity(ScriptActivity.class);
		solo.waitForFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
		solo.sleep(400);
	}

	private void checkVisibilityOfViews(int imageVisibility, int userBrickGroupNameVisibility, int userBrickGroupDetailsVisibility,
			int checkBoxVisibility) {
		solo.sleep(200);
		assertTrue("Userbrick group image " + getAssertMessageAffix(imageVisibility),
				solo.getView(R.id.fragment_group_backpack_item_image_view).getVisibility() == imageVisibility);
		assertTrue("Userbrick group name " + getAssertMessageAffix(userBrickGroupNameVisibility),
				solo.getView(R.id.fragment_group_backpack_item_name_text_view).getVisibility() == userBrickGroupNameVisibility);
		assertTrue("Userbrick group details " + getAssertMessageAffix(userBrickGroupDetailsVisibility),
				solo.getView(R.id.fragment_group_backpack_item_detail_linear_layout).getVisibility() == userBrickGroupDetailsVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility),
				solo.getView(R.id.fragment_group_backpack_item_checkbox).getVisibility() == checkBoxVisibility);
	}

	private String getAssertMessageAffix(int visibility) {
		String assertMessageAffix = "";
		switch (visibility) {
			case View.VISIBLE:
				assertMessageAffix = "not visible";
				break;
			case View.GONE:
				assertMessageAffix = "not gone";
				break;
			default:
				break;
		}
		return assertMessageAffix;
	}

	private void checkIfCheckboxesAreCorrectlyCheckedAndVisible(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(500);

		List<UserBrick> userBricks = ProjectManager.getInstance().getCurrentSprite().getUserBrickList();
		CheckBox firstCheckBox = userBricks.get(0).getCheckBox();
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertTrue("Userbrick checkbox is not visible", firstCheckBox.getVisibility() == VISIBLE);

		if (userBricks.size() > 1) {
			CheckBox secondCheckBox = userBricks.get(1).getCheckBox();
			assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
			assertTrue("Userbrick checkbox is not visible", secondCheckBox.getVisibility() == VISIBLE);
		}
	}

	private void checkIfCheckboxesAreCorrectlyCheckedInBackPack(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(500);

		CheckBox firstCheckBox = getUserBrickCheckBox(1, R.id.fragment_group_backpack_item_checkbox);
		CheckBox secondCheckBox = getUserBrickCheckBox(2, R.id.fragment_group_backpack_item_checkbox);

		assertNotNull("Checkbox is null!", firstCheckBox);
		assertNotNull("Checkbox is null!", secondCheckBox);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private CheckBox getUserBrickCheckBox(int number, int checkboxId) {
		ArrayList<CheckBox> checkBoxViews = solo.getCurrentViews(CheckBox.class);
		int found = 0;
		for (int index = 0; index < checkBoxViews.size(); index++) {
			if (checkBoxViews.get(index).getId() == checkboxId) { // R.id.brick_user_checkbox / fragment_group_backpack_item_checkbox) {
				found++;
				if (found == number) {
					return checkBoxViews.get(index);
				}
			}
		}
		return null;
	}

	private void checkIfNumberOfBricksIsEqual(int expectedNumber) {
		assertEquals("Number of bricks is not as expected", expectedNumber, getCurrentUserBrickCount());
	}

	private void checkIfNumberOfBricksIsEqualInBackPack(int expectedNumber) {
		int currentNumberOfUserBrickGroups = BackPackListManager.getInstance().getBackPackedUserBrickGroups().size();
		assertEquals("Number of userbrick groups is not as expected", expectedNumber, currentNumberOfUserBrickGroups);
	}

	private void hideDetails() {
		if (getBackPackUserBrickAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(200);
		}
	}

	private int getCurrentUserBrickCount() {
		return ProjectManager.getInstance().getCurrentSprite().getUserBrickList().size();
	}

	private void addOneUserBrickInPrototypeView(String secondTestUserBrickName) {
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForDialogToOpen();
		solo.clearEditText(2);
		solo.enterText(2, secondTestUserBrickName);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForDialogToClose();
		solo.sleep(500);
	}
}
