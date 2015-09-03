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
package org.catrobat.catroid.uitest.ui.fragment;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class UserBrickDataEditorFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public UserBrickDataEditorFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.createTestProjectWithNestedUserBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testRenameVariableEditsFormulas() throws InterruptedException {
		UiTestUtils.showSourceAndEditBrick(UiTestUtils.TEST_USER_BRICK_NAME, solo);

		String textOnChangeXBrickTextField = "" + Math.round(BrickValues.CHANGE_X_BY);

		assertTrue("'" + textOnChangeXBrickTextField + "' should have appeared", solo.waitForText(textOnChangeXBrickTextField, 0, 2000));

		solo.clickOnText(textOnChangeXBrickTextField);
		boolean gotIntoFormulaEditor = solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

		assertTrue("FormulaEditor should have appeared", gotIntoFormulaEditor);

		String stringOnVariablesButton = solo.getCurrentActivity().getString(R.string.formula_editor_data);
		solo.clickOnText(stringOnVariablesButton);

		String stringOnUserBrickVar = UiTestUtils.TEST_USER_BRICK_VARIABLE;
		boolean hasBrickVariable = solo.waitForText(stringOnUserBrickVar, 0, 5000);

		assertTrue("'" + stringOnUserBrickVar + "' didn't appear", hasBrickVariable);
		solo.clickOnText(stringOnUserBrickVar);

		solo.goBack();
		solo.waitForDialogToOpen(2000);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.yes));
		solo.waitForDialogToClose(2000);

		String defineString = solo.getCurrentActivity().getString(R.string.define);

		assertTrue("'" + defineString + "' should have appeared", solo.waitForText(defineString, 0, 2000));
		solo.clickOnText(defineString);

		assertTrue("'" + stringOnUserBrickVar + "' should have appeared", solo.waitForText(stringOnUserBrickVar, 1, 2000));
		solo.clickOnText(stringOnUserBrickVar, 1);

		solo.waitForDialogToOpen(2000);

		String newVariableName = "newName";
		solo.clearEditText(0);
		solo.enterText(0, newVariableName);

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.ok));
	}
}
