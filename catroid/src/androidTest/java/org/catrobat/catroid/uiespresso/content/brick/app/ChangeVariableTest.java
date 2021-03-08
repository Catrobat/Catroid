/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ChangeVariableTest {

	private ChangeVariableBrick changeVariableBrick;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testCreateNewUserVariableAndDeletion() {
		String userVariableName = "testVariable1";
		String secondUserVariableName = "testVariable2";

		onBrickAtPosition(0)
				.checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1)
				.checkShowsText(R.string.brick_change_variable);
		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.performNewVariable(userVariableName);
		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.performNewVariable(secondUserVariableName);

		onBrickAtPosition(1).onFormulaTextField(R.id.brick_change_variable_edit_text)
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();
		onDataList()
				.onVariableAtPosition(0)
				.performDelete();

		onView(allOf(withText(userVariableName), hasSibling(withText("0.0"))))
				.check(doesNotExist());
		pressBack();
		onView(withText(userVariableName))
				.check(doesNotExist());
	}

	@Test
	public void testCreateUserVariableInFormulaEditor() {
		String userVariableName1 = "Variable1";
		performNewVariableFromFormulaEditor(1, userVariableName1);
		onBrickAtPosition(1)
				.checkShowsText(userVariableName1);
		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.checkShowsVariableNameInAdapter(userVariableName1);
	}

	@Test
	public void testViewInFormulaEditorAfterClone() {
		String userVariableName = "testvariable1";
		String userVariableName2 = "testvariable2";

		performNewVariableFromFormulaEditor(1, userVariableName);

		onBrickAtPosition(1)
				.performClick();
		onView(withText(R.string.brick_context_dialog_copy_brick))
				.perform(click());
		onBrickAtPosition(0).perform(click());

		performNewVariableFromFormulaEditor(1, userVariableName2);

		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.checkShowsVariableNamesInAdapter(Arrays.asList(userVariableName, userVariableName2));
	}

	private void performNewVariableFromFormulaEditor(int brickId, String variableName) {
		onBrickAtPosition(brickId).onFormulaTextField(R.id.brick_change_variable_edit_text)
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();
		onDataList()
				.performAdd(variableName)
				.performClose();
		onFormulaEditor()
				.performCloseAndSave();
	}

	public void createProject() {
		Script script = BrickTestUtils
				.createProjectAndGetStartScript("ChangeVariableTest");
		changeVariableBrick = new ChangeVariableBrick(10);
		script.addBrick(changeVariableBrick);
		script.getBrickList().size();
	}
}
