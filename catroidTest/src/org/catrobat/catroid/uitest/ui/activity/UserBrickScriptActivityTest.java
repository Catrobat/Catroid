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
package org.catrobat.catroid.uitest.ui.activity;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.UserBrickDataEditorFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class UserBrickScriptActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public UserBrickScriptActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.createTestProjectWithNestedUserBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testNestedUserBrickScriptActivities() throws InterruptedException {
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);

		String textOnChangeYByNBrick = solo.getCurrentActivity().getString(R.string.brick_change_y_by);
		boolean foundText = solo.waitForText(textOnChangeYByNBrick, 0, 2000);
		assertTrue("Did not find '" + textOnChangeYByNBrick + "'", foundText);

		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME + "2", solo);

		String textOnChangeXByNBrick = solo.getCurrentActivity().getString(R.string.brick_change_x_by);
		foundText = solo.waitForText(textOnChangeXByNBrick, 0, 300);
		assertTrue("Did not find '" + textOnChangeXByNBrick + "'", foundText);

		solo.goBack();
		solo.goBack();

		foundText = solo.waitForText(textOnChangeYByNBrick, 0, 300);
		assertTrue("Did not find '" + textOnChangeYByNBrick + "'", foundText);

		solo.goBack();
		solo.goBack();

		foundText = solo.waitForText(UiTestUtils.TEST_USER_BRICK_NAME, 0, 300);
		assertTrue("Did not find '" + UiTestUtils.TEST_USER_BRICK_NAME + "'", foundText);
	}

	public void testUserBrickVariableScope() throws InterruptedException {
		ProjectManager.getInstance().getCurrentProject().getDataContainer().addProjectUserVariable("projectVar");
		ProjectManager.getInstance().getCurrentProject().getDataContainer().addSpriteUserVariable("spriteVar");

		String textOnSetSizeToBrickTextField = "" + Math.round(BrickValues.SET_SIZE_TO);
		checkVariableScope(textOnSetSizeToBrickTextField, 0, false);

		solo.waitForText(UiTestUtils.TEST_USER_BRICK_NAME, 1, 5000);
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);

		String textOnChangeXBrickTextField = "" + Math.round(BrickValues.CHANGE_X_BY);
		checkVariableScope(textOnChangeXBrickTextField, 1, true);

		solo.goBack();
		solo.goBack();

		checkVariableScope(textOnSetSizeToBrickTextField, 0, false);
	}

	public void testCantEditBrickDataWhileAddingNewBrick() throws InterruptedException {
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);

		// add a new brick to the internal script of the user brick
		UiTestUtils.addNewBrick(solo, R.string.brick_change_y_by);

		// place it (this should click on the define brick)
		UiTestUtils.dragFloatingBrick(solo, -1);

		boolean wentToDataEditor = solo.waitForFragmentByTag(
				UserBrickDataEditorFragment.BRICK_DATA_EDITOR_FRAGMENT_TAG, 5000);

		assertTrue("the userBrickDataEditor should not be open!!", !wentToDataEditor);
	}

	private void checkVariableScope(String valueOnBrick, int depth, boolean expectedBrickVariable) {
		if (!solo.waitForText(valueOnBrick, 0, 5000)) {
			fail("'" + valueOnBrick + "' should have appeared");
		}
		solo.clickOnText(valueOnBrick);
		boolean gotIntoFormulaEditor = solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		if (!gotIntoFormulaEditor) {
			fail("FormulaEditor should have appeared");
		}

		String stringOnVariablesButton = solo.getCurrentActivity().getString(R.string.formula_editor_data);
		solo.clickOnText(stringOnVariablesButton, depth);

		solo.sleep(100);

		String stringOnGlobalTag = solo.getString(R.string.formula_editor_dialog_for_all_sprites).toUpperCase();
		boolean gotIntoVariableList = solo.waitForText(stringOnGlobalTag, 1, 5000);
		if (!gotIntoVariableList) {
			fail("'" + stringOnGlobalTag + "' should have appeared");
		}

		String stringOnUserBrickVar = UiTestUtils.TEST_USER_BRICK_VARIABLE;
		boolean hasBrickVariable = solo.waitForText(stringOnUserBrickVar, 1, 5000);
		if (hasBrickVariable != expectedBrickVariable) {
			fail("'" + stringOnUserBrickVar + "' appeared: " + (hasBrickVariable ? "true" : "false"));
		}

		solo.goBack();
		solo.goBack();
	}
}
