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
package org.catrobat.catroid.uitest.content.interaction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class UserBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public UserBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.createTestProjectWithUserBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testCopyAndDeleteBricksInUserScriptInclDefineBrick() {
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, solo.getCurrentActivity());
		solo.clickOnCheckBox(0);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(300);
		assertEquals("The selected brick could not be copied!", 2, ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript().getBrickList().size());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, solo.getCurrentActivity());
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.select_all));
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(300);
		assertEquals("Any of the bricks could not be copied or the definition brick was copied by mistake!", 4, ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript().getBrickList().size());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, solo.getCurrentActivity());
		solo.clickOnCheckBox(0);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForDialogToOpen();
		solo.sleep(100);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		assertEquals("The selected brick could not be deleted!", 3, ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript().getBrickList().size());

		//copy via context menu
		solo.clickOnText(solo.getString(R.string.brick_change_x_by));
		String stringOnCopy = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_copy_brick);
		solo.waitForText(stringOnCopy);
		solo.clickOnText(stringOnCopy);
		solo.sleep(1000);
		UiTestUtils.dragFloatingBrick(solo, 1);
		solo.sleep(1000);
		assertEquals("The selected brick could not be copied!", 4, ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript().getBrickList().size());

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
		assertEquals("The selected brick could not be deleted!", 3, ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript().getBrickList().size());

		solo.clickOnView(solo.getView(R.id.delete));
		solo.sleep(100);
//		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, solo.getCurrentActivity());
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.select_all));
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForDialogToOpen();
		solo.sleep(100);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();
		assertEquals("Any of the bricks could not be deleted or the definition brick was deleted by mistake!", 0, ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScript().getBrickList().size());
	}

//	public void testSetParameterOfUserbricksInScript() {
//
//		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
//		solo.clickOnText(solo.getCurrentActivity().getString(R.string.define));
//		solo.clickOnText(solo.getString(R.string.add_variable));
//		solo.waitForDialogToOpen(2000);
//		String newVariableName = "testVariable";
//		solo.clearEditText(0);
//		solo.enterText(0, newVariableName);
//		solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
//		solo.waitForDialogToClose(2000);
//		solo.goBack();
//		solo.goBack();
//		solo.goBack();
//
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
//		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
//		solo.clickOnText(solo.getString(R.string.yes));
//
//		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
//		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
//		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
//		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
//		solo.clickInList(1);
//
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//		String stringOnShowAddButton = solo.getCurrentActivity()
//				.getString(R.string.brick_context_dialog_add_to_script);
//		solo.waitForText(stringOnShowAddButton);
//		solo.clickOnText(stringOnShowAddButton);
//		UiTestUtils.dragFloatingBrick(solo, -1);
//		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
//		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
//		solo.clickInList(2);
//		solo.waitForText(stringOnShowAddButton);
//		solo.clickOnText(stringOnShowAddButton);
//		UiTestUtils.dragFloatingBrick(solo, -1);
//		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
//		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
//		solo.waitForText("New Brick 2");
//		solo.clickOnText("New Brick 2");
//		solo.waitForText(stringOnShowAddButton);
//		solo.clickOnText(stringOnShowAddButton);
//		UiTestUtils.dragFloatingBrick(solo, -1);
//		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_add));
//		UiTestUtils.clickOnBrickCategory(solo, solo.getCurrentActivity().getString(R.string.category_user_bricks));
//		solo.clickInList(2);
//		solo.waitForText(stringOnShowAddButton);
//		solo.clickOnText(stringOnShowAddButton);
//		UiTestUtils.dragFloatingBrick(solo, -1);
//		solo.sleep(200);
//
//		ArrayList<Pair<Integer, Integer>> listUserBrickNewBrick1OneParameter = new ArrayList<Pair<Integer, Integer>>();
//		ArrayList<Pair<Integer, Integer>> listUserBrickNewBrick1TwoParameter = new ArrayList<Pair<Integer, Integer>>();
//		ArrayList<Pair<Integer, Integer>> listUserBrickNewBrick2OneParameter = new ArrayList<Pair<Integer, Integer>>();
//		Pair<Integer, Integer> userbrickNewBrick1WithOneParameterFirstParameterPos1 = new Pair<Integer, Integer>(2, 0);
//		Pair<Integer, Integer> userbrickNewBrick1WithOneParameterFirstParameterPos2 = new Pair<Integer, Integer>(1, 0);
//		Pair<Integer, Integer> userbrickNewBrick1WithTwoParametersFirstParameterPos1 = new Pair<Integer, Integer>(1, 0);
//		Pair<Integer, Integer> userbrickNewBrick1WithTwoParametersSecondParameterPos1 = new Pair<Integer, Integer>(1, 1);
//		Pair<Integer, Integer> userbrickNewBrick2WithOneParameterFirstParameter = new Pair<Integer, Integer>(1, 0);
//		listUserBrickNewBrick1OneParameter.add(userbrickNewBrick1WithOneParameterFirstParameterPos1);
//		listUserBrickNewBrick1OneParameter.add(userbrickNewBrick1WithOneParameterFirstParameterPos2);
//		listUserBrickNewBrick1TwoParameter.add(userbrickNewBrick1WithTwoParametersFirstParameterPos1);
//		listUserBrickNewBrick1TwoParameter.add(userbrickNewBrick1WithTwoParametersSecondParameterPos1);
//		listUserBrickNewBrick2OneParameter.add(userbrickNewBrick2WithOneParameterFirstParameter);
//
//		UserBrick userBrickNewBrick1OneParameter = (UserBrick) ProjectManager.getInstance().getCurrentScript().getBrickList().get(0);
//		UserBrick userBrickNewBrick1TwoParameters = (UserBrick) ProjectManager.getInstance().getCurrentScript().getBrickList().get(3);
//		UserBrick userBrickNewBrick2OneParameter = (UserBrick) ProjectManager.getInstance().getCurrentScript().getBrickList().get(1);
//
//		assertTrue("Parameterlist for Userbrick \"New Brick 1\" with one parameter not correct: " +
//				userBrickNewBrick1OneParameter.getUserBrickPositionToParameter().get(0).first + userBrickNewBrick1OneParameter.getUserBrickPositionToParameter().get(0).second +
//				userBrickNewBrick1OneParameter.getUserBrickPositionToParameter().get(1).first + userBrickNewBrick1OneParameter.getUserBrickPositionToParameter().get(1).second, userBrickNewBrick1OneParameter.getUserBrickPositionToParameter().equals(listUserBrickNewBrick1OneParameter));
//		assertTrue("Parameterlist for Userbrick \"New Brick 1\" with two parameters not correct: " +
//				userBrickNewBrick1TwoParameters.getUserBrickPositionToParameter().get(0).first + userBrickNewBrick1TwoParameters.getUserBrickPositionToParameter().get(0).second +
//				userBrickNewBrick1TwoParameters.getUserBrickPositionToParameter().get(1).first + userBrickNewBrick1TwoParameters.getUserBrickPositionToParameter().get(1).second, userBrickNewBrick1TwoParameters.getUserBrickPositionToParameter().equals(listUserBrickNewBrick1TwoParameter));
//		assertTrue("Parameterlist for Userbrick \"New Brick 2\" with one parameter not correct: " +
//				userBrickNewBrick2OneParameter.getUserBrickPositionToParameter().get(0).first + userBrickNewBrick1TwoParameters.getUserBrickPositionToParameter().get(0).second, userBrickNewBrick2OneParameter.getUserBrickPositionToParameter().equals(listUserBrickNewBrick2OneParameter));
//	}

//	public void testUseUserBrickVariableInFormulaAndDeleteVariableInFormulaEditor()
//	{
//		//use userbrickvariable in formula
//		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);
//		UiTestUtils.addNewBrick(solo, R.string.category_variables, R.string.brick_set_variable, 0);
//		UiTestUtils.dragFloatingBrick(solo, 1);
//		solo.clickOnText("0.0");
//		solo.clickOnText(solo.getString(R.string.formula_editor_variables));
//		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.fragment_formula_editor_variablelist_item_value_text_view));
//		solo.clickOnText(solo.getString(R.string.formula_editor_operator_minus));
//		solo.clickOnText("0");
//		solo.clickOnText(solo.getString(R.string.ok));
//
//		assertTrue("'" + "\"Variable 1\" - 0" + "' should have appeared", solo.waitForText("\"Variable 1\" - 0", 1, 1000));
//
//		//delete userbrickvariable in variablesview of formulaeditor
//		solo.clickOnText("\"Variable 1\" - 0");
//		solo.clickOnText(solo.getString(R.string.formula_editor_variables));
//		solo.clickLongOnView(solo.getCurrentActivity().findViewById(R.id.fragment_formula_editor_variablelist_item_value_text_view));
//		solo.waitForText(solo.getString(R.string.delete));
//		solo.clickOnText(solo.getString(R.string.delete));
//		solo.goBack();
//		solo.goBack();
//
//		assertFalse("'" + "Variable 1:" + "' should have disappeared in dropdown menu of set variable", solo.waitForText("Variable 1:", 1, 1000));
//
//		String defineString = solo.getCurrentActivity().getString(R.string.define);
//
//		assertTrue("'" + defineString + "' should have appeared", solo.waitForText(defineString, 0, 1000));
//		solo.clickOnText(defineString);
//
//		int brickElementList = ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick().getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().size();
//
//		assertEquals("The variable \"Variable 1\" should have disappeared", 1, brickElementList);
//	}

	public void testChangeDeleteUserBrickData() {
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

		assertFalse("the whole data (Variable, Text and LineBreak) should have disappeared", solo.waitForText(newTextName, 0, 1000) || solo.waitForText(UiTestUtils.TEST_USER_BRICK_NAME, 0, 1000) || solo.waitForText(UiTestUtils.TEST_USER_BRICK_VARIABLE, 0, 1000));
	}

	public void testEditFormulaWithUserBrickDataAndChangeValuesViaFormulaEditor() {
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
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, solo.getCurrentActivity());
		solo.scrollDown();
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.scrollDown();
		solo.sleep(300);
		assertTrue("2 userbricks should exist in the script after copying via action mode, but they don't! brickList.size(): " + ProjectManager.getInstance().getCurrentScript().getBrickList().size(), ProjectManager.getInstance().getCurrentScript().getBrickList().size() == 8);

		//delete via action mode
		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, solo.getCurrentActivity());
		solo.scrollDown();
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
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

//	public void testMoveUserBrickUpAndDown() throws InterruptedException {
//		solo.scrollDown();
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//
//		String stringOnMove = solo.getCurrentActivity()
//				.getString(R.string.brick_context_dialog_move_brick);
//		solo.waitForText(stringOnMove);
//		solo.clickOnText(stringOnMove);
//
//		int[] location = UiTestUtils.dragFloatingBrick(solo, -1);
//		assertTrue("was not able to move the brick up", location != null);
//
//		solo.scrollUp();
//		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);
//		solo.waitForText(stringOnMove);
//		solo.clickOnText(stringOnMove);
//		location = UiTestUtils.dragFloatingBrick(solo, 3);
//		assertTrue("was not able to move the brick down", location != null);
//		solo.sleep(300);
//	}

	//	delete a userbrick, go back to scripts and check if the deletion was updated
	public void testDeleteUserBrickAndCheckIfScriptActivityUpdates() throws InterruptedException {
		solo.clickOnText(UiTestUtils.TEST_USER_BRICK_NAME);

		String stringOnShowSourceButton = solo.getCurrentActivity()
				.getString(R.string.brick_context_dialog_show_source);
		solo.waitForText(stringOnShowSourceButton);
		solo.clickOnText(stringOnShowSourceButton);

		boolean addBrickShowedUp = solo.waitForFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG, 2000);
		assertTrue("addBrickFragment should have showed up", addBrickShowedUp);

		UiTestUtils.deleteFirstUserBrick(solo, UiTestUtils.TEST_USER_BRICK_NAME);
		solo.sleep(500);
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
}
